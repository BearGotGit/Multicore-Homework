/**
 * Tournament-style N-consensus implementation using pairwise binary consensus.
 * 
 * Each thread competes in a tournament where:
 * - N threads participate (N must be a power of 2)
 * - N(N-1)/2 BinConsensus objects are used for pairwise matches
 * - Each thread has its own memory location to store its value
 * - When a thread loses a binary consensus, it adopts the winner's value
 * - Tournament proceeds in rounds until consensus is reached
 */
public class TournamentConsensus implements Consensus {
    private final int numThreads;
    private final BinConsensus[][] matches; // Pairwise consensus objects
    private final Object[] threadValues;     // Each thread's current value
    
    /**
     * Constructor for TournamentConsensus
     * @param n Number of threads (must be a power of 2)
     */
    public TournamentConsensus(int n) {
        // Verify n is a power of 2
        if (n <= 0 || (n & (n - 1)) != 0) {
            throw new IllegalArgumentException("Number of threads must be a power of 2");
        }
        
        this.numThreads = n;
        this.threadValues = new Object[n];
        
        // Initialize N(N-1)/2 BinConsensus objects for all pairwise matches
        // Using upper triangular matrix: matches[i][j] where i < j
        this.matches = new BinConsensus[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                matches[i][j] = new BinConsensus();
            }
        }
    }
    
    /**
     * Decide on a consensus value through tournament-style pairwise competition.
     * 
     * Algorithm:
     * 1. Each thread stores its initial value
     * 2. Threads compete with ALL other threads in pairwise matches
     * 3. When thread i competes with thread j:
     *    - Thread writes its value BEFORE competing
     *    - If i < j: thread i proposes 0, thread j proposes 1
     *    - The BinConsensus decides which thread "wins"
     *    - Winner of BinConsensus determines whose value is adopted
     * 4. After competing with all threads, consensus is reached
     */
    @Override
    public Object decide(Object value) {
        // Get current thread ID (set by BinConsensus.test())
        int myId = BinConsensus.threadId.get();
        
        // Store my initial value FIRST - this must be visible to all
        threadValues[myId] = value;
        Object myValue = value;
        
        // Compete with all other threads in order
        // This ensures a consistent tournament structure
        for (int otherId = 0; otherId < numThreads; otherId++) {
            if (otherId == myId) continue;
            
            // Determine the BinConsensus object for this pair
            int lower = Math.min(myId, otherId);
            int higher = Math.max(myId, otherId);
            
            // My proposal: if I'm the lower ID, I propose 0; if higher, I propose 1
            int myProposal = (myId == lower) ? 0 : 1;
            
            // Compete using BinConsensus
            // This is wait-free and determines which thread "wins" this match
            int decision = matches[lower][higher].decide(myProposal);
            
            // Interpret the decision:
            // - If decision == 0, the lower thread won
            // - If decision == 1, the higher thread won
            int winnerId = (decision == 0) ? lower : higher;
            
            // If I didn't win, adopt the winner's value
            if (winnerId != myId) {
                myValue = threadValues[winnerId];
                // Update my value so future competitors see it
                threadValues[myId] = myValue;
            }
        }
        
        return myValue;
    }
    
    /**
     * Alternative simpler approach: All-pairs tournament
     * Each thread competes with all others, adopting values when it loses
     */
    public Object decideAllPairs(Object value) {
        int myId = BinConsensus.threadId.get();
        threadValues[myId] = value;
        Object myValue = value;
        
        // Compete with all other threads
        for (int otherId = 0; otherId < numThreads; otherId++) {
            if (otherId == myId) continue;
            
            int lower = Math.min(myId, otherId);
            int higher = Math.max(myId, otherId);
            
            // Binary consensus: lower proposes 0, higher proposes 1
            int myProposal = (myId == lower) ? 0 : 1;
            int decision = matches[lower][higher].decide(myProposal);
            
            // If I lost, adopt the other thread's value
            if (decision != myProposal) {
                myValue = threadValues[otherId];
                threadValues[myId] = myValue;
            }
        }
        
        return myValue;
    }
}
