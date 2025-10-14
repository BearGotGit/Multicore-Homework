/**
 * 
 * Simple code ot make consensus object and test it using Dr. Brandt's code
 */

public class TestConsensus {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Testing Tournament Consensus with " + BinConsensus.NUM_THREADS + " threads");
        

        TournamentConsensus consensus = new TournamentConsensus(BinConsensus.NUM_THREADS);
        BinConsensus.test(consensus);


        System.out.println("Test completed successfully!");
    }
}
