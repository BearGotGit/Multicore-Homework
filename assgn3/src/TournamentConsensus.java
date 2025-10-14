
// Atomic Reference Array is used to enable common memory for threads. Usefu because will store thread values
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Tournament-style N-consensus implementation using pairwise binary consensus.
 */
public class TournamentConsensus implements Consensus {

    /**
     * 
     * Pairwise consensus objects stored in matches -- these will make up bulk of the logic behind our code
     * 
     * Atomic Ref array stores winning values for threads, useful to propagate answers
     */
    private final int numThreads;
    private final BinConsensus[][] matches;             
    private final AtomicReferenceArray<Object> threadValues;
    
    /**
     * Constructor for TournamentConsensus
     * 
     * Arg: n as in "n-consensus". 
     */
    public TournamentConsensus(int n) {
        // Verify n is a power of 2
        if (n <= 0 || (n & (n - 1)) != 0) {
            throw new IllegalArgumentException("Number of threads must be a power of 2");
        }
        
        this.numThreads = n;
        this.threadValues = new AtomicReferenceArray<>(n);
        
        // Uses N(N-1)/2 BinConsensus objects for deciding pairwise matches; therefore, just upper triangular part (i < j)
        this.matches = new BinConsensus[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                matches[i][j] = new BinConsensus();
            }
        }
    }
    
    /**
     * Decide on a consensus value through all-pairs competition.
     * 
     * Algorithm:
     * 1. Each thread stores its initial value
     * 2. Each thread competes with ALL other threads in sequence
     * 3. For each match between thread i and thread j:
     *    - They use the BinConsensus object matches[min(i,j)][max(i,j)]
     *    - Lower ID proposes 0, higher ID proposes 1
     *    - If a thread loses, it adopts the winner's value
     * 4. After competing with all threads, consensus is reached
     */
    @Override
    public Object decide(Object value) {
        // Get current thread ID (set by BinConsensus.test())
        int myId = BinConsensus.threadId.get();
        
        System.out.println(ConsoleColors.ANSI_YELLOW + "[Thread " + myId + "] Starting consensus with value: " + value + ConsoleColors.ANSI_RESET);
        
        // Store my initial value FIRST - this must be visible to all
        threadValues.set(myId, value);
        Object myValue = value;
        
        // Compete with all other threads in order
        for (int otherId = 0; otherId < numThreads; otherId++) {
            if (otherId == myId) continue;
            
            // CRITICAL: Update my current value BEFORE each match
            // This ensures opponents see my latest value if they lose to me
            threadValues.set(myId, myValue);
            
            // System.out.println("[Thread " + myId + "] Competing with thread " + otherId + " (my current value: " + myValue + ")");
            
            // Determine the BinConsensus object for this pair
            int lower = Math.min(myId, otherId);
            int higher = Math.max(myId, otherId);
            
            // This threads proposal: if I'm the lower ID, I propose 0; if higher, I propose 1
            int myProposal = (myId == lower) ? 0 : 1;
            
            // System.out.println("[Thread " + myId + "] My proposal: " + myProposal + " (match[" + lower + "][" + higher + "])");
            
            // Compete using BinConsensus for this pair
            int decision = matches[lower][higher].decide(myProposal);
            
            // System.out.println("[Thread " + myId + "] Decision for match[" + lower + "][" + higher + "]: " + decision);
            
            // Because only one thread can win, no need to worry about who arrived first like proof of 2-consensus with 3 memories. No backwards like 0 is 1 and 1 is 0 implications...
            int winnerId = (decision == 0) ? lower : higher;
            
            // If I didn't win, adopt the winner's value
            if (winnerId != myId) {
                Object opponentValue = threadValues.get(winnerId);
                if (opponentValue != null) {
                    // System.out.println("[Thread " + myId + "] LOST to thread " + winnerId + ", adopting value: " + opponentValue + " (was: " + myValue + ")");
                    myValue = opponentValue;
                    // Note: We'll update threadValues at the start of next iteration
                } else {
                    // System.out.println("[Thread " + myId + "] Thread " + winnerId + " has null value (may be dead), keeping my value: " + myValue);
                }
            } else {
                // System.out.println("[Thread " + myId + "] WON against thread " + otherId + ", keeping value: " + myValue);
            }
        }
        
        System.out.println(ConsoleColors.ANSI_BLUE + "[Thread " + myId + "] Final consensus value: " + myValue + ConsoleColors.ANSI_RESET);
        return myValue;
    }
}
