import java.util.ArrayList;

class Chopstick {
    private boolean taken = false;

    public synchronized void grab() throws InterruptedException {
        while (taken) {
            wait();
        }

        taken = true;
    }

    public synchronized void release() throws InterruptedException {
        // // FIXME: Not sure bout this part
        // while (!taken) {
        //     wait();
        // }
        // // END FIXME

        taken = false;
        notify();
    }
}

class ChopsticksPlural {
    private Chopstick left; 
    private Chopstick right;

    public ChopsticksPlural (Chopstick left, Chopstick right) {
        this.left = left; this.right = right;
    }

    public synchronized void grabChopsticks () throws InterruptedException {
        left.grab();
        right.grab();
    }

    public synchronized void releaseChopsticks () throws InterruptedException {
        left.release();
        right.release();
    }
}

class AntiStarvinator {
    // Map index to time-stamp that philosopher ate at (or -1 if never eaten)
    private final int[] lastEaten;
    private final boolean[] doneEating;
    private int counter = 0;

    public AntiStarvinator(int n) {
        this.lastEaten = new int[n];
        this.doneEating = new boolean[n];
        for (int i = 0; i < n; i++) {
            lastEaten[i] = -1;
            doneEating[i] = false;
        }
    }

    // Update time stamp when philosopher done eating
    public synchronized void signalEaten(int p) {
        lastEaten[p] = ++counter;
        notifyAll(); 
    }

    // Philosopher p is permanently done (no more eating)
    // ... Needed b/c some philosophers would be too polite otherwise and not finish their meals
    // ... (see isBeingGluttonous) 
    public synchronized void signalDoneEating(int p) {
        doneEating[p] = true;
        notifyAll();
    }

    // Wait here until philosopher p is allowed to eat
    public synchronized void waitIfGluttonous(int p, int leftOf, int rightOf) throws InterruptedException {
        while (isBeingGluttonous(p, leftOf, rightOf)) {
            wait();
        }
    }

    // See above ^^ : if eaten already, not allowed to eat when neither neighbor has eaten
    private boolean isBeingGluttonous(int p, int leftOf, int rightOf) {
        // Case when neighbors are done
        if (doneEating[leftOf] && doneEating[rightOf]) return false;
        
        // Case at least one neighbor isn't done
        int l = lastEaten[leftOf];
        int m = lastEaten[p];
        int r = lastEaten[rightOf];

        // If anyone hasn't eaten yet, let p eat
        if (l == -1 || m == -1 || r == -1) return false;

        // p is gluttonous if p ate more recently than both neighbors and wants to eat again
        return m > l && m > r;
    }
}

class Philosopher {
    // I mean why would you give a dude one chopstick? 🤯 🤣 👉 🥢
    private final ChopsticksPlural both;
    private int hunger;
    private int leftIndex;
    private int thisIndex;
    private int rightIndex;

    private static AntiStarvinator gustatorialEmpathy;

    Philosopher(ChopsticksPlural both, int hunger, 
    // 🫣 Look away...
    AntiStarvinator antiStarver, int leftIndex, int thisIndex, int rightIndex) {
        this.both = both; this.hunger = hunger; 
        // Ughhhh
        this.gustatorialEmpathy = antiStarver;
        this.leftIndex = leftIndex; this.thisIndex = thisIndex; this.rightIndex = rightIndex;
    }

    public void thinkAndEat () {
        // can only eat if hungry
        while (hunger > 0) {

            try {
                // This prevents friends (philosophers directly left and right) from starving
                gustatorialEmpathy.waitIfGluttonous(thisIndex, leftIndex, rightIndex);
                
                // waits() until available
                both.grabChopsticks();

                // Eat
                // Thread.sleep(00);
                System.out.println(this + " says, 'Yum yum yum...' | Hunger goes from " + hunger + " to " + (hunger-1));
                hunger -= 1;

                // Put chopsticks down & notify() waiting threads
                both.releaseChopsticks();
                //  ... this part updates the common pager (to make sure this philo actually does his/her job)
                gustatorialEmpathy.signalEaten(thisIndex);
            } catch (InterruptedException denied) {}

            // This philosopher ate his last bite, mark done so neighbours don't wait forever
            if (hunger == 0) {
                gustatorialEmpathy.signalDoneEating(thisIndex);
            }
        }
    }
}

class FairnessRecorder {
    
}

class ThoughtfulDinner {
    public static void main (String[] args) {

        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<ChopsticksPlural> chopstickSets = new ArrayList<>();

        // Num philosophers and chopsticks
        final int n = 500;

        // Make chopsticks
        ArrayList<Chopstick> choppedSticks = new ArrayList<>(); 
        for (int i = 0; i < n; i++) {
            choppedSticks.add(new Chopstick());
        }

        // Aggr into sets of chopsticks
        for (int i = 0; i < n; i++) {
            Chopstick left = choppedSticks.get(i);
            Chopstick right = choppedSticks.get((i + 1) % n);
            chopstickSets.add(new ChopsticksPlural(left, right));
        }

        // Make a watchful antistarver aka. boss
        // ... We'll make visible to every instance, so they can all increment count properly
        AntiStarvinator bossPhilosopherAntiStarver = new AntiStarvinator(n); 

        // Making threads
        final int eat_times = 1000;
        for (int i = 0; i < n; i++){
            
            final int k = i;
            threads.add(new Thread(() -> {
                ChopsticksPlural sticks = chopstickSets.get(k);
                Philosopher p = new Philosopher(sticks, eat_times, bossPhilosopherAntiStarver, (k - 1 + n)%n, k, (k + 1)%n);
                
                p.thinkAndEat();
            }));
        }

        // Start dining!
        threads.forEach(t -> t.start());

        // Finish up / done food!
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException yawn) {}
        });        
        
    }
}