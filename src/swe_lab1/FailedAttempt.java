package swe_lab1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

//LoginTask puts it in the shared queue and waits for the answer on the small reply queue
public class FailedAttempt {

    private final String email;
    
    //reply queue of size 1 - the FailureWorker puts the answer here
    private final BlockingQueue<LoginResult> reply = new ArrayBlockingQueue<>(1);

    public FailedAttempt(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public BlockingQueue<LoginResult> getReply() {
        return reply;
    }
}