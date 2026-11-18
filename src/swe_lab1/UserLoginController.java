package swe_lab1;

import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

//reads the fields, starts a LoginTask thread and shows the result
public class UserLoginController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label errortext;//label for the error
    @FXML
    private Button loginButton;
    @FXML
    private Button testButton;

    //given by Main 
    private UserChecker userChecker;
    private AttemptTracker tracker;
    private BlockingQueue<FailedAttempt> failureQueue;

    //we keep the threads so we can stop them when the window closes
    private Thread loginThread;
    private Thread countdownThread;
    private Thread testThread;

    @FXML
    void LoginAttempt(ActionEvent event) {//login function - connected with the login btn
        //disable the fields and the button so the user can't click twice
        setInputDisabled(true);
        errortext.setText("Checking...");
        
        // the txt in username and password goes to a LoginTask
        LoginTask task = new LoginTask(username.getText(), password.getText(),
                userChecker, tracker, failureQueue, this::showResult);
                
        //the login runs on a new thread 
        loginThread = new Thread(task, "LoginTask");
        loginThread.setDaemon(true);
        loginThread.start();
    }

    //called by LoginTask 
    private void showResult(LoginResult result) {
        switch (result.getType()) {
            case SUCCESS:
                errortext.setText("");
                System.out.println("Login Successful!");
                openWelcome();
                break;
            case FAILURE:
                errortext.setText("Wrong details, " + result.getAttemptsLeft() + " attempts left");
                setInputDisabled(false);
                break;
            case WRONG_DETAILS:
                errortext.setText("Wrong details");
                setInputDisabled(false);
                break;
            case BLOCKED:
                startCountdown(result.getRemainingMillis());
                break;
        }
    }

    //starts countdown
    private void startCountdown(long remainingMillis) {
        CountdownTask task = new CountdownTask(remainingMillis,
                //every second
                seconds -> errortext.setText("Account blocked, " + seconds + " seconds left"),
                //when the block is over
                () -> {
                    errortext.setText("");
                    setInputDisabled(false);
                });
        countdownThread = new Thread(task, "CountdownTask");
        countdownThread.setDaemon(true);
        countdownThread.start();
    }

    private void setInputDisabled(boolean disabled) {
        username.setDisable(disabled);
        password.setDisable(disabled);
        loginButton.setDisable(disabled);
    }

    //opens the welcome fxml 
    private void openWelcome() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Welcome.fxml"));
            javafx.scene.Parent root = loader.load();
         // get the current stage using the username field
            javafx.stage.Stage stage = (javafx.stage.Stage) username.getScene().getWindow();
         // replace the current Scene Welcome xml
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Welcome System");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void RunTest(ActionEvent event) {
        testButton.setDisable(true);
        testThread = new Thread(() -> {
            try {
                String report = ConcurrencyTest.run();
                System.out.println(report);
                //show the report 
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Concurrency test");
                    alert.setHeaderText("20 threads x 499 failures, n = 10,000");
                    alert.setContentText(report);
                    alert.show();
                    testButton.setDisable(false);
                });
            } catch (InterruptedException e) {
                //the window was closed during the test
            }
        }, "ConcurrencyTest");
        testThread.setDaemon(true);
        testThread.start();
    }
 
    public void setServices(UserChecker userChecker, AttemptTracker tracker,
                            BlockingQueue<FailedAttempt> failureQueue) {
        this.userChecker = userChecker;
        this.tracker = tracker;
        this.failureQueue = failureQueue;
    }

    //called from Main when the window closes
    public void shutdown() {
        if (countdownThread != null) countdownThread.interrupt();
        if (loginThread != null) loginThread.interrupt();
        if (testThread != null) testThread.interrupt();
    }

    //exit function for the X btn
    @FXML
    void Exit(ActionEvent event) {
        Platform.exit();
    }
}