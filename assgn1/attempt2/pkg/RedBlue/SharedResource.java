package pkg.RedBlue;

import java.util.ArrayList;

public class SharedResource {
    private final RedBlueLock lock = new RedBlueLock();
    
    private int[] array;
    private ArrayList<Color>[] usage;

    private boolean verbose = false;

    public SharedResource (int n) {
        this.array = new int[n];
        this.usage = new ArrayList[n];
        for (int k = 0; k < n; k++) this.usage[k] = new ArrayList<>();
    }

    public SharedResource (int n, boolean verbose) {
        this(n);
        this.verbose = verbose;
    }

    public void increment(int i) {
        try {
            lock.lockRed();
            if (verbose) System.out.println("Locked red");
            array[i]++;
            // Track usage, purely stats
            usage[i].add(Color.RED);
        } finally {
            lock.unlock();
            if (verbose) System.out.println("Unlocked red");
        }
    }

    public void decrement(int i) {
        try {
            lock.lockBlue();
            if (verbose) System.out.println("Locked blue");
            array[i]--;
            usage[i].add(Color.BLUE);
        } finally {
            lock.unlock();
            if (verbose) System.out.println("Unlocked blue");
        }
    }

    public int[] array() {
        return array;
    }

    public ArrayList<Color>[] usage() {
        return usage;
    }
}