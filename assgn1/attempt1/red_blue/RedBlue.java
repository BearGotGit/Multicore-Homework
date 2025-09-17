// NOTE: Implementation Incomplete

import java.util.ArrayList;

class RedBlueLock {

    // Which is active?
    private boolean redActive = true;
    
    // num active
    private int numBlue = 0;
    private int numRed = 0;

    // num waiting
    private int numBlueWaiting = 0;
    private int numRedWaiting = 0;
    
    public RedBlueLock() {

    }

    // Lock
    public synchronized void redLock() {
        // Does blue wait? yeah, don't add more; else, proceed
        // FIXME: Might want to go through all blue (literally) before even begins a single red b/c of q strat. 
        while (numBlueWaiting > 0) {
            try {
                numRedWaiting += 1;

                wait();
            } catch (InterruptedException yawn) {}
        }

        redActive = true;
        numRed += 1;
    }

    public synchronized void blueLock() {
        while (numRedWaiting > 0) {
            try {
                numBlueWaiting += 1;

                wait();
            } catch (InterruptedException yawn) {}
        }

        redActive = false;
        numBlue += 1;
    }

    // Unlock
    public synchronized void redUnlock() {
        numRed -= 1;
        notifyAll();
    }

    public synchronized void blueUnlock() {
        numBlue -= 1;
    }
}

class RedBlueThread extends Thread {
    private boolean isRed = false;
    private Runnable runnable;

    public RedBlueThread(boolean isRed, Runnable runnable) {
        this.isRed = isRed;
        this.runnable = runnable;
    }
    
    // @Override
    // public void run() {
    //     super.run();
    // }
}

class SharedResource {
    private static int counter = 0;
    private RedBlueLock lock = new RedBlueLock();

    public synchronized void increment(boolean isRed) {
        try {
            if (isRed) {
                lock.redLock();
            } else {
                lock.blueLock();
            }

            // Danger zone
            counter += 1;

        } finally {
            if (isRed) {
                lock.redUnlock();
            } else {
                lock.blueLock();
            }
        }
    }

    public synchronized int getCount() {
        return counter;
    }

}

class RedBlue {

    public static void main(String[] args) {

        ArrayList<Thread> threads = new ArrayList<>();

        SharedResource counter = new SharedResource();

        // Make
        final int n = 10;
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                threads.add(new RedBlueThread(true, () -> {
                    counter.increment(true);
                }));
            } else {
                threads.add(new RedBlueThread(false, () -> {
                    counter.increment(false);
                }));
            }
        }

        // Start
        threads.forEach(t -> {
            t.start();
        });

        // Finish
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {}
        });

        System.out.println(counter.getCount());
    }
}