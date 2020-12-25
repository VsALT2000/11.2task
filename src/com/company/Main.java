package com.company;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor (
                3, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<> (3));

        RejectedExecutionHandler handler = (r, executor1) -> {
            if ( !executor1.isShutdown() ) {
                try {
                    executor1.getQueue().put(r);
                } catch (InterruptedException ignored) {
                }
            }
        };
        executor.setRejectedExecutionHandler(handler);

        AtomicInteger count = new AtomicInteger (0);
        AtomicInteger inProgress = new AtomicInteger (0);

        for (int i = 0; i < 30; i++) {
            final int number = i;
            Thread.sleep (10);

            System.out.println ("creating #" + number);
            executor.submit (() -> {
                int working = inProgress.incrementAndGet ();
                System.out.println ("start #" + number + ", in progress: " + working);
                try {
                    Thread.sleep (Math.round (1000 + Math.random () * 2000));
                } catch (InterruptedException e) {
                }
                working = inProgress.decrementAndGet ();
                System.out.println ("end #" + number + ", in progress: "
                        + working + ", done tasks: " + count.incrementAndGet ());
                return null;
            });
        }
    }
}
