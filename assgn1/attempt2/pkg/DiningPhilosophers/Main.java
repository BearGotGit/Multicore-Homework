package pkg.DiningPhilosophers;

import java.util.ArrayList;

public class Main {
    // If longestMonopoly is inordinately long given a single chopstick, we say it's not fair
    // ... Since at most 2 philosophers / chopstick, we should expect longestMonopoly to be short
    private static int longestMonopoly(ArrayList<Philosopher> usage) {
        int streak = 0, maxStreak = 0;
        Philosopher prev = usage.get(0);
        for (int i = 1; i < usage.size(); i++) {
            if (prev == usage.get(i)) {
                if (++streak > maxStreak) maxStreak = streak;
            }
            else streak = 0;
        }
        return maxStreak;
    }

    // Main function to run Dining Philosophers simulation
    public static void main(String[] args) {
        // Parse args
        int n = 0, h = 0;
        try {
            n = Integer.parseInt(args[0]);
            h = Integer.parseInt(args[1]);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.out.println(e.getMessage());
            System.out.println("\nUsage: `make run dp n=<number of philosophers (int)> h=<starting hunger (int)>`");
        }

        // Chopsticks and philosophers
        Chopstick[] chopsticks = new Chopstick[n];
        for (int i = 0; i < n; i++) chopsticks[i] = new Chopstick(i);

        Philosopher[] philosophers = new Philosopher[n];
        for (int i = 0; i < n; i++) {
            Chopstick left = chopsticks[i], right = chopsticks[(i + 1) % n];
            philosophers[i] = new Philosopher(left, right, h);
        }

        // Make threads
        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; i++) threads[i] = new Thread(philosophers[i]);

        // Start them
        for (int i = 0; i < n; i++) threads[i].start();

        // Join them
        for (int i = 0; i < n; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {}
        }

        System.out.println("\nCompleted simulation");

        // Usage and statistics
        int maxStreak = 0;
        for (int i = 0; i < n; i++) {
            Chopstick c = chopsticks[i];
            int streak = longestMonopoly(c.usage());
            // System.out.printf("Longest streak for chopstick #%d: %d\n", i, streak);

            if (streak > maxStreak) maxStreak = streak;
        }
        System.out.printf("Most times a single chopstick held by same philosopher: %d\n", maxStreak);
    }
}