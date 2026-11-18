package swe_lab1;

//LoginTask starts it, waits for it with join(), and then reads the answer
public class BlockCheckThread extends Thread {
	private final AttemptTracker tracker;
    private final String email;

    private boolean blocked;
    private long remainingMillis;

    public BlockCheckThread(AttemptTracker tracker, String email) {
        super("BlockCheckThread"); //the thread's name (helps when debugging)
        this.tracker = tracker;
        this.email = email;
    }

    @Override
    public void run() {
        remainingMillis = tracker.remainingBlockMillis(email);
        blocked = remainingMillis > 0;
    }

    //call only after join()!
    public boolean isBlocked() {
        return blocked;
    }

    //call only after join()!
    public long getRemainingMillis() {
        return remainingMillis;
    }
}
