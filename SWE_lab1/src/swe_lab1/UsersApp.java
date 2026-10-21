package swe_lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class UsersApp {
	public static void main(String[] args) {
		
		//creating an ArrayList to store valid users
        ArrayList<User> users = new ArrayList<User>();
        
        try {
        	//reading the users from the file users.txt
        	
        	File file = new File("users.txt");
            Scanner reader = new Scanner(file);
            
            while (reader.hasNextLine()) {

                String line = reader.nextLine();

                //we split the line into username and password
                String[] data = line.trim().split("\\s+");
                
                //try to add the username and password if valid
                try {
                    User user = new User(data[0], data[1]);
                    users.add(user);
                }
                
                //print the invalid line, and the exception message
                catch (InvalidUserNameException e) {
                    System.out.println(line + " " + e.getMessage());
                }
                catch (InvalidPasswordException e) {
                    System.out.println(line + " " + e.getMessage());
                }
            }
            
            reader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        
        //sort the users by username
        Collections.sort(users, (u1, u2) -> u1.getUserName().compareTo(u2.getUserName()));
        
        //printing the sorted users
        for (User user : users) {
            System.out.println(user);
        }
	}
}
