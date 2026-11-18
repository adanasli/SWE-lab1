package swe_lab1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main extends Application {

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

        ArrayList<User> validUsers = new ArrayList<>();
        //reading our users file
        try {
            File file = new File("users.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                //taking the valid users
                if (line.trim().isEmpty()) continue;
                String[] data = line.trim().split("\\s+",2);
                try {
                    User user = new User(data[0], data.length == 2 ? data[1] : "");
                    validUsers.add(user);
                } catch (Exception e) {
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
        	//the file is not there
            System.out.println("users.txt not found!");
        }
        try {
        	//loading the login fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserLogin.fxml"));
            Parent root = loader.load();
            //transfer the valid users to the controller to deal with em
            UserLoginController controller = loader.getController();
            controller.setValidUsers(validUsers);
            Scene scene = new Scene(root);
            primaryStage.setTitle("User Login");
            primaryStage.setScene(scene);
            primaryStage.show();
            //exiting
            primaryStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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