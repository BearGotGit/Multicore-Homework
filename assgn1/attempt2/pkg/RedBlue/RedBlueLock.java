package pkg.RedBlue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

enum Color { NONE, RED, BLUE }

public class RedBlueLock {

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition queueNotEmpty = lock.newCondition();

    private Color colorActive = Color.NONE; 
    private int numQueued = 0; 
    private int numActive = 0;

    // Using blocking 
    public void lockRed () {
        lock.lock();
        try {
            numQueued++;
            while( !((colorActive == Color.RED && numQueued == 1) || (colorActive == Color.NONE)) ) {
                queueNotEmpty.await();
            }
            numQueued--;
            colorActive = Color.RED;
            numActive++;
        } catch (InterruptedException e) {}
        finally {
            lock.unlock();
        }
    }

    public void lockBlue () {
        lock.lock();
        try {
            numQueued++;
            while( !((colorActive == Color.BLUE && numQueued == 1) || (colorActive == Color.NONE)) ) {
                queueNotEmpty.await();
            }
            numQueued--;
            colorActive = Color.BLUE;
            numActive++;
        } catch (InterruptedException e) {}
        finally {
            lock.unlock();
        }
    }

    // Using blocking
    public void unlock () {
        try {
            lock.lock();
            if (--numActive == 0) {
                colorActive = Color.NONE;
                queueNotEmpty.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}