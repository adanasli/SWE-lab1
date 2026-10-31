package swe_lab1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.ArrayList;

public class UserLoginController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label errortext;//label for the error
    private ArrayList<User> validUsers = new ArrayList<>();// arraylist for the legal users
    @FXML
    void LoginAttempt(ActionEvent event) {//login function - connected with the login btn
        // the txt in username and password 
        String enteredUser = username.getText();
        String enteredPass = password.getText();
        boolean matchFound = false;
        for (User u:validUsers) {  //loop for for all the valid users in the arraylist
        	//checking if the pass and username are matching the ones in the system
        	if (u.getUserName().equals(enteredUser) && u.getPassword().equals(enteredPass)) {
                matchFound = true;
                break;
            }
        }
        if (matchFound) { // founded
            errortext.setText("");             
            System.out.println("Login Successful!");
            try {
            	//if they match then open the welcome fxml
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Welcome.fxml"));
                javafx.scene.Parent root = loader.load();
             // get the current Stage/window using the username field
                javafx.stage.Stage stage = (javafx.stage.Stage) username.getScene().getWindow();
             // replace the current Scene Welcome xml
                stage.setScene(new javafx.scene.Scene(root));
                stage.setTitle("Welcome System");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            errortext.setText("user or password do not match");
        }
    }

 //func called from main to pass the valid users lab1 to the controller
    public void setValidUsers(ArrayList<User> users) {
        this.validUsers = users;
    }
    //exit function for the X btn
    @FXML
    void Exit(ActionEvent event) {
        javafx.application.Platform.exit();
        System.exit(0);
    }
}