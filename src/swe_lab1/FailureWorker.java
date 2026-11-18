package swe_lab1;

import java.util.concurrent.BlockingQueue;

//takes FailedAttempt events from the shared queue, updates the counter and sends the answer back
public class FailureWorker extends Thread {

    private final BlockingQueue<FailedAttempt> queue;
    private final AttemptTracker tracker;

    public FailureWorker(String name, BlockingQueue<FailedAttempt> queue, AttemptTracker tracker) {
        super(name);
        this.queue = queue;
        this.tracker = tracker;
        
        //doesn't keep the program alive when the window is closed
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            //works until someone calls interrupt()
            while (!isInterrupted()) {
            	
                //waits until there's an event in the queue 
                FailedAttempt attempt = queue.take();
                
                //updates the counter (synchronized inside UserState)
                LoginResult result = tracker.recordFailure(attempt.getEmail());
                
                //sends the answer back, wakes up the LoginTask that is waiting
                attempt.getReply().put(result);
            }
        } catch (InterruptedException e) {
            //interrupt() was called when the program closes 
        	System.out.println(getName() + " stopped");
        }
    }
}