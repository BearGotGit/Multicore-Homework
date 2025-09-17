package pkg.DiningPhilosophers;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

class Chopstick {
    ReentrantLock lock = new ReentrantLock(true);
    private int index;
    private ArrayList<Philosopher> usage = new ArrayList<>();

    public Chopstick(int index) {
        this.index = index;
    }

    public void take(Philosopher p) {
        try {
            // One thread gets the lock
            lock.lock();

            // Track usage (eg. one philosopher monopolizes before another or roughly equal balance?)
            usage.add(p);
        } finally {
            lock.unlock();
        }
    }

    public synchronized void drop() {
        notifyAll();
    }

    public int index() {
        return this.index;
    }

    public ArrayList<Philosopher> usage(){
        return usage;
    }
}