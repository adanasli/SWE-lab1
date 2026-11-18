package swe_lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

//loads the users file once and checks login details
//after loading, the list is only read 
public class UserChecker {

    private final ArrayList<User> users;

    private UserChecker(ArrayList<User> users) {
        this.users = users;
    }

    //reads the users file 
    public static UserChecker loadFromFile(String fileName) {
        ArrayList<User> list = new ArrayList<>();
        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] data = line.trim().split("\\s+", 2);
                
                try {
                    list.add(new User(data[0], data.length == 2 ? data[1] : ""));
                }
                
                catch (InvalidUserNameException | InvalidPasswordException e) {
                	System.out.println("Skipped: " + line + " - " + e.getMessage());
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " not found!");
        }
        return new UserChecker(list);
    }

    //does this email exist
    public boolean exists(String email) {
        for (User u : users) {
            if (u.getUserName().equals(email)) {
                return true;
            }
        }
        return false;
    }

    //checks email + password
    public boolean authenticate(String email, String password)
            throws InvalidUserNameException, InvalidPasswordException {
    	
        //the constructor throws if the email or password are illegal
        new User(email, password);
        for (User u : users) {
            if (u.getUserName().equals(email) && u.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}