package swe_lab1;

//proves that UserState doesn't lose updates when many threads write at the same time
public class ConcurrencyTest {

    private static final int N = 10000;
    private static final int THREADS = 20;
    private static final int FAILURES_PER_THREAD = 499;
    private static final String EMAIL = "test@test.com";

    //runs the test and returns a report
    public static String run() throws InterruptedException {
        AttemptTracker tracker = new AttemptTracker(N, 60);
        String report = "";

        //step 1 - 20 threads against the same email
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < FAILURES_PER_THREAD; j++) {
                    tracker.recordFailure(EMAIL);
                }
            });
        }
        
        //start all of them, and only then wait for all of them
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        int expected = THREADS * FAILURES_PER_THREAD; //9980
        int actual = tracker.getState(EMAIL).getFailures();
        report += "Expected: " + expected + ", actual: " + actual;
        if (actual == expected) {
            report += " -> OK, no lost updates\n";
        } else {
            report += " -> LOST " + (expected - actual) + " UPDATES!\n";
        }

        //step 2 - 19 more failures must not block
        boolean blockedEarly = false;
        for (int i = 0; i < 19; i++) {
            if (tracker.recordFailure(EMAIL).getType() == LoginResult.Type.BLOCKED) {
                blockedEarly = true;
            }
        }
        report += "19 more failures: counter = " + tracker.getState(EMAIL).getFailures();
        report += blockedEarly ? " -> blocked too early, FAIL\n" : " -> not blocked, OK\n";

        //step 3 - the 20th failure must block
        boolean blockedNow = tracker.recordFailure(EMAIL).getType() == LoginResult.Type.BLOCKED;
        report += "20th failure: " + (blockedNow ? "blocked -> OK\n" : "NOT blocked -> FAIL\n");

        if (actual == expected && !blockedEarly && blockedNow) {
            report += "TEST PASSED";
        } else {
            report += "TEST FAILED";
        }
        return report;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(run());
    }
}