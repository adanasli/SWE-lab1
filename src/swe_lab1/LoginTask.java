package swe_lab1;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import javafx.application.Platform;

//manages the whole login process and sends the result to the screen 
public class LoginTask implements Runnable {

    private final String email;
    private final String password;
    private final UserChecker userChecker;
    private final AttemptTracker tracker;
    private final BlockingQueue<FailedAttempt> failureQueue;
    
    //the function that shows the result 
    private final Consumer<LoginResult> onResult;

    public LoginTask(String email, String password, UserChecker userChecker, AttemptTracker tracker,
                     BlockingQueue<FailedAttempt> failureQueue, Consumer<LoginResult> onResult) {
        this.email = email;
        this.password = password;
        this.userChecker = userChecker;
        this.tracker = tracker;
        this.failureQueue = failureQueue;
        this.onResult = onResult;
    }

    @Override
    public void run() {
        try {
            LoginResult result = login();
            //screen controls can only be changed by the thread
            Platform.runLater(() -> onResult.accept(result));
        } catch (InterruptedException e) {
        	Thread.currentThread().interrupt();
        }
    }

    //the login process 
    private LoginResult login() throws InterruptedException {
        //is the email blocked?
        BlockCheckThread check = new BlockCheckThread(tracker, email);
        check.start();
        check.join();
        if (check.isBlocked()) {
            //blocked -> don't even check the password
            return LoginResult.blocked(check.getRemainingMillis());
        }

        //check the details 
        boolean ok;
        try {
            ok = userChecker.authenticate(email, password);
        } catch (InvalidUserNameException e) {
            //illegal email -> can't be in the file -> wrong details, no tracking
            return LoginResult.wrongDetails();
            
        } catch (InvalidPasswordException e) {
            //illegal password = wrong password 
            ok = false;
        }

        if (ok) {
            //success 
            if (tracker.recordSuccess(email)) {
                return LoginResult.success();
            }
            //got blocked by another login in the meantime
            return LoginResult.blocked(tracker.remainingBlockMillis(email));
        }

        //email doesn't exist -> wrong details, no tracking
        if (!userChecker.exists(email)) {
            return LoginResult.wrongDetails();
        }

        //existing email, wrong password -> a FailureWorker updates the counter
        FailedAttempt attempt = new FailedAttempt(email);
        failureQueue.put(attempt);          //put the event in the shared queue
        return attempt.getReply().take();   //wait for the answer
    }
}