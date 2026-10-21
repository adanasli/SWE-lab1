package swe_lab1;

public class User {
    private String userName;
    private String password;

    public User(String userName, String password) throws InvalidUserNameException, InvalidPasswordException {

        //checking validation for both args
        isValidUser(userName);
        isValidPassword(password);

        this.userName=userName;
        this.password= password;
    }
    
    //no getPassword 
    public String getUserName() {
        return userName;
    }
    
    //setters must check validity
    public void setUserName(String userName) throws InvalidUserNameException {
        isValidUser(userName);
        this.userName = userName;
    }

    public void setPassword(String password) throws InvalidPasswordException {
        isValidPassword(password);
        this.password = password;
    }

    //function that checks the validity of the username
    public boolean isValidUser(String userName) throws InvalidUserNameException {

        if (userName.length() > 50) {
            throw new InvalidUserNameException("Username is too long, try something shorter");
        }

        // We divided the username into 3 parts, each part should match a certain pattern.
        // Combined, the email should have 3 parts that match the description.

        String part1 = "[a-zA-Z0-9+._%-]+";
        String part2 = "[a-zA-Z0-9][a-zA-Z0-9.-]*";
        String part3 = "[a-zA-Z]{2,}"; //there has to be at least 2 characters

        String combine = "^" + part1 + "@" + part2 + "\\." + part3 + "$";

        if (!userName.matches(combine))
            throw new InvalidUserNameException("Please enter a valid Email as username");

        return true;
    }

    //function that checks the validity of the password
    public boolean isValidPassword(String password) throws InvalidPasswordException {

        if (password.length() < 8) {
            throw new InvalidPasswordException("Your password is too short, add more characters");
        }

        if (password.length() > 12) {
            throw new InvalidPasswordException("Your password is too long, try a shorter one");
        }

        //a password must contain at least 1 letter, 1 number, and 1 special character
        //so we'll use flags to detect that

        boolean hasLetter = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        String special = "!@#$%^&*()"; //allowed characters

        for (int i = 0; i < password.length(); i++) {

            char c = password.charAt(i);

            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasLetter = true;
            }
            else if (c >= '0' && c <= '9') {
                hasNumber = true;
            }
            else if (special.indexOf(c) != -1) {
                hasSpecial = true;
            }
            else { //not an allowed character
                throw new InvalidPasswordException("Please enter a valid password");
            }
        }

        //all 3 conditions must happen
        if (!hasLetter || !hasNumber || !hasSpecial) {
            throw new InvalidPasswordException("Please enter a valid password");
        }

        return true;
    }

    @Override
    public String toString() {
        return userName;
    }
}