package swe_lab1;

import java.util.concurrent.ConcurrentHashMap;

//keeps one UserState for every email
public class AttemptTracker {

    private final int maxFailures;   //n
    private final int blockSeconds;  //t
    private final ConcurrentHashMap<String, UserState> states = new ConcurrentHashMap<>();

    public AttemptTracker(int maxFailures, int blockSeconds) {
    	
        //n and t must be at least 1
        if (maxFailures < 1 || blockSeconds < 1) {
            throw new IllegalArgumentException("n and t must be at least 1");
        }
        this.maxFailures = maxFailures;
        this.blockSeconds = blockSeconds;
    }

    //returns the state of the email, and creates a new one if it doesn't exist yet
    public UserState getState(String email) {
        return states.computeIfAbsent(email, e -> new UserState(maxFailures, blockSeconds));
    }

    public boolean isBlocked(String email) {
        UserState s = states.get(email);
        return s != null && s.isBlocked();
    }

    public long remainingBlockMillis(String email) {
        UserState s = states.get(email);
        if (s == null) {
            return 0;
        }
        return s.remainingBlockMillis();
    }

    public LoginResult recordFailure(String email) {
        return getState(email).recordFailure();
    }

    public boolean recordSuccess(String email) {
        return getState(email).recordSuccess();
    }

    public int getMaxFailures() {
        return maxFailures;
    }

    public int getBlockSeconds() {
        return blockSeconds;
    }
}