package pkg.DiningPhilosophers;

class Philosopher implements Runnable {
    private Chopstick left, right;
    private int hunger;
    private long finishTime;

    private int sleep = 10;
    private boolean verbose = false;

    public Philosopher(Chopstick left, Chopstick right, int hunger) {
        this.left = left;
        this.right = right;
        this.hunger = hunger;
    }

    public Philosopher(Chopstick left, Chopstick right, int hunger, int sleep, boolean verbose) {
        this(left, right, hunger);
        this.sleep = sleep;
        this.verbose = verbose;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        while (hunger > 0) {

            // Avoid deadlock / circular dependency by picking chopstick w/ lowest index
            // ... Since Philosophers in circle, not everybody can pick up left. At one end
            // ... one philosopher must wait until both available. This always prevents cycle
            if (left.index() < right.index()) {
                left.take(this);
                right.take(this);
            } else {
                right.take(this);
                left.take(this);
            }

            if (verbose) System.out.println(this + " eats. Hunger: " + this.hunger);

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {}
            --hunger;

            left.drop();
            right.drop();
        }

        finishTime = System.currentTimeMillis() - startTime;

        if (verbose) System.out.printf("%s took %d ms to eat\n", this, finishTime);
    }

    public long finishTime() {
        return this.finishTime;
    }
}