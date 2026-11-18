package swe_lab1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main extends Application {

    //things the whole program shares
    private UserChecker userChecker;
    private AttemptTracker tracker;
    //the shared queue - LoginTasks put failed attempts, FailureWorkers take them
    private final BlockingQueue<FailedAttempt> failureQueue = new LinkedBlockingQueue<>();
    private final FailureWorker[] workers = new FailureWorker[2];
    private UserLoginController controller;

    //init() runs BEFORE start(), on a different thread 
    //so reading the file here doesn't freeze the screen
    @Override
    public void init() {
        userChecker = UserChecker.loadFromFile("users.txt");
    }

    @Override
    public void start(Stage primaryStage) {

        //reading n and t from the program arguments
        List<String> args = getParameters().getRaw();
        Integer n = null;
        Integer t = null;

        //we need at least 2 arguments, otherwise get(1) will crash
        if (args.size() >= 2) {
            n = parsePositive(args.get(0));
            t = parsePositive(args.get(1));
        }

        //if n is missing or not valid -> ask the user
        if (n == null) {
            n = askNumber("Please enter the max number of wrong logins: ");
            //if the user pressed Cancel -> close the program
            if (n == null) {
                Platform.exit();
                return;
            }
        }
        //if t is missing or not valid -> ask the user
        if (t == null) {
            t = askNumber("Please enter a block of time in seconds: ");
            if (t == null) {
                Platform.exit();
                return;
            }
        }
        System.out.println("n=" + n + ", t=" + t);

        //shared state + starting the two FailureWorker threads
        tracker = new AttemptTracker(n, t);
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new FailureWorker("FailureWorker-" + (i + 1), failureQueue, tracker);
            workers[i].start();
        }

        try {
        	//loading the login fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserLogin.fxml"));
            Parent root = loader.load();
            //transfer what the controller needs
            controller = loader.getController();
            controller.setServices(userChecker, tracker, failureQueue);
            Scene scene = new Scene(root);
            primaryStage.setTitle("User Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //called automatically when the window is closed or Platform.exit() -> stop all our threads
    @Override
    public void stop() {
        if (controller != null) {
            controller.shutdown();
        }
        for (FailureWorker w : workers) {
            if (w != null) {
                w.interrupt();
            }
        }
        //wait a little for the workers to finish (max 1 second each)
        for (FailureWorker w : workers) {
            try {
                if (w != null) {
                    w.join(1000);
                }
            } catch (InterruptedException e) {
                //closing
            }
        }
        System.out.println("All threads stopped - bye");
    }

    //function that checks if the string is a valid number
    //returns the number if valid, otherwise returns null
    private static Integer parsePositive(String s) {
        try {
            int num = Integer.parseInt(s.trim()); //remove spaces

            //n and t must be at least 1
            if (num < 1) {
                return null;
            }
            return num;
        }

        //not a whole number
        catch (NumberFormatException e) {
            return null;
        }
    }

    //asks the user for a number until it's valid
    //returns null if the user pressed Cancel
    private Integer askNumber(String message) {
        while (true) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Login settings");
            dialog.setHeaderText(message);
            
            //waiting for the user to press OK or Cancel
            Optional<String> answer = dialog.showAndWait();
            
            //the user pressed Cancel 
            if (answer.isEmpty()) {
                return null;
            }
            Integer num = parsePositive(answer.get());
            
            //valid number
            if (num != null) {
                return num;
            }
            //not valid
            message = "Invalid value! Please enter a valid number";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}