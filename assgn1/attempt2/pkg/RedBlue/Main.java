package pkg.RedBlue;

import java.util.ArrayList;

public class Main {
    private static int longestMonopoly(ArrayList<Color> usage) {
        int streak = 0, maxStreak = 0;
        Color prev = usage.get(0);
        for (int i = 1; i < usage.size(); i++) {
            if (prev == usage.get(i)) {
                if (++streak > maxStreak) maxStreak = streak;
            }
            else streak = 0;
        }
        return maxStreak;
    }
    // Gonna test red/blue w/ some even/odd
    public static void main (String[] args) {
        // Parse args
        int NUM = 0;
        int TURNS = 0;
        try {
            NUM = Integer.parseInt(args[0]);
            TURNS = Integer.parseInt(args[1]);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.out.println(e.getMessage());
            System.out.println("\nUsage: `make run rb n=<length of array to test with (int)> <t=<number of turns each thread gets to increment/decrement array (int)>`");
            return;
        }

        final int NUM_T = NUM * 2;

        // Shared resource & threads
        SharedResource shared = new SharedResource(NUM);

        // 
        Thread[] threads = new Thread[NUM_T];
        // Even threads increment shared resource at unique index for red, 
        // Odd threads decrement shared resource at unique index for blue, 
        // ... but guaranteed overlap with red.
        for (int i = 0; i < NUM_T; i++) {
            int k = i;
            int t = TURNS;
            if (i % 2 == 0) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < t; j++) shared.increment(k/2);
                });
            }
            else {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < t; j++) shared.decrement((k - 1)/2);
                });
            }
        }

        // Run it
        for (Thread t : threads) t.start();
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {}
        }

        // We expect shared array to just be a bunch of 0's...
        // ... Should not be case where one incremented without a decrement.
        // We also track the usage. We're interested in the longest streak without change.
        boolean isAllZero = true;
        int[] arr = shared.array();
        ArrayList<Color>[] usage = shared.usage();
        
        Color prev = Color.NONE;
        int maxStreak = 0;
        for (int i = 0; i < NUM; i++) {
            if (arr[i] != 0) {
                isAllZero = false;
            }
            int streak = longestMonopoly(usage[i]);
            if (streak > maxStreak) maxStreak = streak;
        }

        System.out.println("Should be all 0's: " + isAllZero);

        System.out.println("Max streak over shared resource: " + maxStreak);

    }
}