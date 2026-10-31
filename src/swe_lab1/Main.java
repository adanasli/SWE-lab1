package swe_lab1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
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
    public static void main(String[] args) {
        launch(args);
    }
}