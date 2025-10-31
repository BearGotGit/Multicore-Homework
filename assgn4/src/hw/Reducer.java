package hw;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BinaryOperator;

class Reducer<T> {
    /**
     * Reducer class responsible for supplying multithreaded general reduce operation
     * Supply associative operation on type T
     * 
     * 
     */
    private final BinaryOperator<T> op;
    private final T identity;
    private final ExecutorService executor;
    
    public Reducer(BinaryOperator<T> op, T identity) {
        /**
         * Constructor takes function w/ associative property 
         * 
         * Initialize w/ CompletableFutures executor service
         * Initialize identity of type T (eg. let identity<T> be I. I \operatio X == X)
         * 
         * So then usage like Reducer(Integer, +).reduce({1,2,3,4}) should return 10
         */
        this.op = op;
        this.identity = identity;
        this.executor = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    }

    public CompletableFuture<T> reduceAsync(ArrayList<T> list) {
        /**
         * Perform multithreaded reduction on list of objects of type T
         * 
         * Will recursively assign tasks to be completed
         */

        // Base cases
        if (list.isEmpty()) {
            return CompletableFuture.completedFuture(identity);
        }
        if (list.size() == 1) {
            return CompletableFuture.completedFuture(list.get(0));
        }

        // Split list into two halves
        int mid = list.size() / 2;
        ArrayList<T> left = new ArrayList<>(list.subList(0, mid));
        ArrayList<T> right = new ArrayList<>(list.subList(mid, list.size()));

        // Create async tasks for both halves
        CompletableFuture<T> leftFuture = CompletableFuture.supplyAsync(
            () -> reduceAsync(left).join(), 
            executor
        );
        CompletableFuture<T> rightFuture = CompletableFuture.supplyAsync(
            () -> reduceAsync(right).join(),
            executor
        );

        // Combine results without blocking
        return leftFuture.thenCombine(rightFuture, op);
    }
}