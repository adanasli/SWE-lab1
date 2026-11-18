package swe_lab1;

//the result of one login attempt
public class LoginResult {

    //the 4 possible results
    public enum Type {
        SUCCESS,        //correct email and password
        FAILURE,        //existing email, wrong password
        WRONG_DETAILS,  //email doesn't exist 
        BLOCKED         //the email is blocked right now
    }

    private final Type type;
    private final int attemptsLeft;      //only for FAILURE
    private final long remainingMillis;  //only for BLOCKED

    //private const
    private LoginResult(Type type, int attemptsLeft, long remainingMillis) {
        this.type = type;
        this.attemptsLeft = attemptsLeft;
        this.remainingMillis = remainingMillis;
    }

    //functions that create each type of results
    
    public static LoginResult success() {
        return new LoginResult(Type.SUCCESS, 0, 0);
    }

    public static LoginResult failure(int attemptsLeft) {
        return new LoginResult(Type.FAILURE, attemptsLeft, 0);
    }

    public static LoginResult wrongDetails() {
        return new LoginResult(Type.WRONG_DETAILS, 0, 0);
    }

    public static LoginResult blocked(long remainingMillis) {
        return new LoginResult(Type.BLOCKED, 0, remainingMillis);
    }

    //getters
    //no setters cause the fields are final
    public Type getType() {
        return type;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public long getRemainingMillis() {
        return remainingMillis;
    }

    //to string
    @Override
    public String toString() {
        return type + " (attemptsLeft=" + attemptsLeft + ", remainingMillis=" + remainingMillis + ")";
    }
}