package swe_lab1;

import java.util.function.IntConsumer;

import javafx.application.Platform;

//sleeps on its own thread and updates the screen 
public class CountdownTask implements Runnable {

    private final long endTime;         //the time the block ends
    private final IntConsumer onTick;   //gets the seconds left 
    private final Runnable onFinished;  //runs on the JavaFX thread when the block is over

    public CountdownTask(long remainingMillis, IntConsumer onTick, Runnable onFinished) {
        this.endTime = System.currentTimeMillis() + remainingMillis;
        this.onTick = onTick;
        this.onFinished = onFinished;
    }

    @Override
    public void run() {
        try {
            long left = endTime - System.currentTimeMillis();
            while (left > 0) {
            	
                //round up
                int seconds = (int) Math.ceil(left / 1000.0);
                Platform.runLater(() -> onTick.accept(seconds));
                
                //sleep until the next whole second 
                long sleep = left % 1000;
                if (sleep == 0) {
                    sleep = 1000;
                }
                Thread.sleep(sleep);
                left = endTime - System.currentTimeMillis();
            }
            
            //the block is over
            Platform.runLater(onFinished);
            
        } catch (InterruptedException e) {
            //the window was closed in the middle of the countdown 
        }
    }
}