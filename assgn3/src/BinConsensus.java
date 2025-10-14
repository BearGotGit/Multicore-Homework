/**
 * A bunch of imports
 * 
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BinConsensus class defines the file. Public. Available for others
 * It is not clear what this class does on first glance
 * 
 *  Defines a static class called "Dead" inside it which simulates what happens when threads die in your code
 *  I assume it must be used inside this class. 
 * 
 * some vars: 
 * 
 * NUM_THREADS
 * numDead
 * used
 * iAmDead
 * 
 * lock - a reentrant lock
 * 
 * some methods: 
 * 
 * void maybeDie - simulates thread dying
 * 
 *  
 */
public class BinConsensus {
    public static class Dead extends RuntimeException {
    }

    // NUM_THREADS == 2 is important for some reason
    final static int NUM_THREADS = 1<<2;

    // numDead is 'volatile', changes between threads (definitely true, there's locking code around this member below)
    // In hindsight, idk why use volatile keyword. Seems to be associated with this because it's about exceptions and shit. value, an Integer defined later, which is also a shared resource seems not to have this issue. 
    static volatile int numDead = 0;

    // ThreadLocal defines a variable that cannot be shared between threads. It provides an initialValue, hence the ugly code. 
    // Eg. threadLocal used = false;
    final ThreadLocal<Boolean> used = new ThreadLocal<>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    // ""
    // Eg. threadLocal iAmDead = false;
    final static ThreadLocal<Boolean> iAmDead = new ThreadLocal<>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    // Some value. Apparently null is valid assignment for classes in Java. 
    Integer value = null;

    // Lock. This means we will access shared resources. We know it's the numDead variable. 
    private final static ReentrantLock lock = new ReentrantLock();


    /** Setup seems mostly over. Into the actual functions */

    /** This function is responsible for simulating Thread death. 
     * 
     * Basically, yield (in case other threads wanna go they can go), this thread will die, so don't matter... I guess. 
     * 
     * Lock around numDead
     * 
     * For some reason, numDead cannot be equal to number of threads. I guess we always want at least one thread making it through. If good continue, else just finish and unlock. 
     * 
     * Good to continue assumed now. 
     * 
     * Pick a thread with probability 1/NUM_THREADS to eliminate. 
     * numDead++, iAmDead var for thread set true (threadLocal, so needs special `.set(true)` on it)
     * 
     * For exome reason, doesn't just do lock.unlock and throw Dead() immediately after. Does another if statement, which makes 0 sense. Like I could copu. I guess when you do threadLocal things have to make sure they ran, but that's just a guess and if right so lame and if wrong so confusing. 
     * 
     * Anyways, just simulate thread death with a RunTime exception (Dead extends RuntimeException)
     */
    public static void maybeDie() {
        Thread.yield();
        lock.lock();
        if (numDead + 1 < NUM_THREADS) {

            // If ok to murk a thread, let's do it. 
            if (ThreadLocalRandom.current().nextInt(NUM_THREADS) == 0) {
                numDead++;
                iAmDead.set(true);
                System.out.println("I am dead: " + threadId.get());
            }

            // Why are you another if statement? You make 0 sense. Surely anything important would've happened by the end of `iAmDead.set(true)? Right?
            // No that makes no sense either, otherwise, wouldn't we need an else statement to undo? If a thing's delayed, why wouldn't we wait???
            if (iAmDead.get()) {
                lock.unlock();
                // Anyways, just simulate thread death with a RunTime exception
                throw new Dead();
            }
        }
        lock.unlock();
    }
    /**
     * "Pretend that this method is wait-free. Each is usable only once."
     * - Prof Brandt.
     * 
     * *-* verify assumptions *-*
     * 
     * Ok. Makes assertion on b. It can only be Boolean is what we're supposed to get, but I guess instead of using Java's boolean thing, we're gonna use ints. This might make it more extendable and also be a hint. 
     * 
     * Also, it seems important to make sure function not used before. 
     * 
     * *-* maybe dies before decides anything *-*
     * 
     * >>> I don't know why we need Thread.yield() again. Kinda weird...
     * 
     * Locks around shared resource. Apparently, `value` which we know to be an Integer (defined earlier). 
     */
    public int decide(int b) {

        // *-* invariant check *-*
        assert b == 0 || b == 1;
        assert !used.get();

        // no double-use 
        used.set(true);

        maybeDie();

        Thread.yield();

        // At end of this, value will be assigned b for sure
        lock.lock();
        if (value == null) {
            value = b;
        }
        lock.unlock();

        // Value can only be the first value assigned...
        // Only one thread wins here.

        System.out.println("Thread: " + threadId.get() + " decided -> " + value);

        // Since that's what value decided, now we return. 
        // I guess this means each thread must have a different `b` value; otherwise, cannot be consensus on who was "most recent". 

        // TODO: Ok. So seems we have a shared memory for two threads here. 
        // ... We know that the number of atomic memory locations we need to implement N-Consensus is N + N(N+1)/2 (Memory location for each thread and each pair of threads). 
        // We can implement a scheme that's wait-free to determine order of who won beat who. 

        // Question: I was confused why Dr. Brandt said to pretend "this method is wait-free"... but I get it now. I know this is what must be done. 

        return value;
    }

    // Interestingly this was a member defined after its use above. 
    // We have to make sure to initialize this when we make a class?
    // Oh... does this mean each thread is allowed to initialize it only once?
    final static ThreadLocal<Integer> threadId = new ThreadLocal<Integer>();

    // TODO: Ah... this is where we need to implement our Consensus object and pass it to this function. 
    // Note that this method is static. That must mean we can call the function without defining the class. That also means it changes no class members, though that seems sus as hell because it looks like it does. 
    
    //  ~~Step through code to read what happnes because I'm confused how this is possible~~
    // ok. 


    // It IS possible. Because only static members are being modified right now (numDead, which belongs to all the threads on this consensus object)
    
    public static void test(Consensus consensus) throws InterruptedException {
        // Init
        numDead = 0;
        List<Thread> threads = new ArrayList<>();


        // I'm confused kinda here. I know I need some number of BinConsensus objects, but I need to implement that in different file. 

        // Initialize some number of binary consensus objects
        // not necessarily this number.
        // TODO:  (MOSTLY DONE)  Implement the Consensus interface. 


        // ~~what is Object doing here?~~
        // Is ok. Is just interface for Consensus.decide()
        final Object[] results = new Object[NUM_THREADS];

        // For each thread, ez
        for (int i = 0; i < NUM_THREADS; i++) {

            // tno = 'this thread number'
            final int tno = i;
            
            /**
             * define thread and what it gonna do
             * 
             * Thread will simply pass a value. Internally Consensus object will use BinConsensus to decide whether or not the Thread got it. 
             * 
             * Consensus.decide(...) returns some result which will allow us to solve N-Consensus. It will try to assign some value to shared memory of BinConsensus. Will return value that's there. If value is different it beat the thread which had the value that it returned. 
             * 
             * If there's no value there other than its own though, will know that it won anyways. 
             * 
             * So we need BinConsensus object (with its shared `value` field, but also more fields that describe whether or not other threads even participated...)
             */
            Thread t = new Thread(() -> {
                
                // init the ThreadLocal stuff 
                threadId.set(tno); // this one can't be changed anymore, used in BinConsensus.decide
                iAmDead.set(false);

                // Need to decide some value. 
                try {
                    // Sim death, not super related really
                    maybeDie();
                    
                    // TODO: Decide the actual value. May not want to pass a STRING. Probably want to pass a number. That seems easy
                    // We still need to define the interface. He just uses an `Object` as a placeholder since all things inherit from it in Java...
                    Object r = consensus.decide("Value(" + tno + ")");
                    
                    // Decision & record result for thread
                    System.out.println(ConsoleColors.ANSI_BLUE + "Thread " + tno + " -> " + r + ConsoleColors.ANSI_RESET);
                    results[tno] = r;
                } 

                // dead thread thrown either top-level or during
                catch (Dead e) {

                } 
                // This is for your debugging benefit. Something ACTUALLY went wrong.
                catch (AssertionError ae) {
                    ae.printStackTrace();
                    System.exit(0);
                }

                // Thread completed work
                System.out.println("Thread " + tno + " done.");
            });

            // After defined, place it in pool of threads to execute
            threads.add(t);
        }

        // Boilerplate
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        // Assertions about running code. Make sure at least one thread survived
        assert numDead != NUM_THREADS : String.format("numDead=%d, NUM_THREADS=%d", numDead, NUM_THREADS);
        System.out.println();

        /**
         * For loop for...?
         * 
         */

        // Default init
        Object found = null;
        for (int i = 0; i < NUM_THREADS; i++) {
            
            // Okay. This says a lot. 
            // What Dr. Brandt wants here is for successful 
            // TODO: Note. I wanted more memory earlier to tell who won. But is that what results is for?

            if (results[i] != null) {
                if (found == null) {
                    found = results[i];
                } else {
                    // Kinda hard to decipher. Would mean we have ...
                    assert found.equals(results[i]);
                }
            }
        }

        // Means at least one decision (guaranteed because of the numDead < NUM_THREADS assertion earlier...)
        assert found != null;

        // Ok. Again to recap... Some results can be null (maybe because the thread died), but if there's a non-null result, all the threads better agree it's the same one! 
        // Okay that's it. For sure. 

        /**
         * 
         * ALL THREAD DECIDED `found`, the first and only non-null result. 
         * 
         */
        System.out.println(ConsoleColors.ANSI_GREEN + "All threads decided -> " + found + ConsoleColors.ANSI_RESET);
        System.out.println();
    }
}


/**
 * LOGISTICS
 * 
 * How do I use BinConsensus in another class? It's public, non-static, but it has some weird fields in it. 
 * 
 * It enables us to test a consensus object, but the consensus object obviously borrows from BinConsensus. I guess because that method is static it's ok!?
 * 
 * 
 */