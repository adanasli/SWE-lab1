package swe_lab1;


public class UserState {

    private final int maxFailures;   //n
    private final long blockMillis;  //t in milliseconds

    private int failures = 0;        //failed attempts since the last reset
    private long blockedUntil = 0;   //the time the block ends 

    public UserState(int maxFailures, int blockSeconds) {
        this.maxFailures = maxFailures;
        this.blockMillis = blockSeconds * 1000L;
    }

    //if the block time is over -> reset the counter 
    private void releaseIfExpired(long now) {
        if (blockedUntil != 0 && now >= blockedUntil) {
            blockedUntil = 0;
            failures = 0;
        }
    }

    //is blocked
    public synchronized boolean isBlocked() {
        long now = System.currentTimeMillis();
        releaseIfExpired(now);
        return blockedUntil != 0;
    }

    //how many milliseconds are left until the block ends 
    public synchronized long remainingBlockMillis() {
        long now = System.currentTimeMillis();
        releaseIfExpired(now);
        
        if (blockedUntil == 0) {
            return 0;
        }
        return blockedUntil - now;
    }

    //counts failure
    public synchronized LoginResult recordFailure() {
        long now = System.currentTimeMillis();
        releaseIfExpired(now);
        
        if (blockedUntil != 0) {
            return LoginResult.blocked(blockedUntil - now);
        }
        failures++;
        
        //start a block of t seconds
        if (failures >= maxFailures) {
            blockedUntil = now + blockMillis;
            return LoginResult.blocked(blockMillis);
        }
        return LoginResult.failure(maxFailures - failures);
    }

    public synchronized boolean recordSuccess() {
        releaseIfExpired(System.currentTimeMillis());
        if (blockedUntil != 0) {
            return false;
        }
        
        failures = 0;
        return true;
    }

    public synchronized int getFailures() {
        return failures;
    }
}