package hw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

class Main {

    public static void main(String args[]) {

        /**
         * Code organized like...
         *  1) arrays
         *  2) ops (associative and equality for testing)
         *  3) reduction tests for each case
         *      (each case conducted hudreds of times to ensure reliability of multithreaded implementation)
         */

        /** Some arrays to try reduction ops on */
        ArrayList<Integer> intsArray = new ArrayList<>();
        for (int i = 1; i <= 300; i++) {
            intsArray.add(i);
        }

        ArrayList<Boolean> boolsWithTrue = new ArrayList<>(Arrays.asList(false, false, true, false, false));
        ArrayList<Boolean> allFalseBools = new ArrayList<>(Arrays.asList(false, false, false, false, false));
        
        ArrayList<int[][]> matricesArray = new ArrayList<>(Arrays.asList(
            new int[][]{{1, 2}, {3, 4}},
            new int[][]{{5, 6}, {7, 8}},
            new int[][]{{9, 10}, {11, 12}},
            new int[][]{{1, 2}, {3, 4}},
            new int[][]{{2, 0}, {1, 2}},
            new int[][]{{1, 0}, {0, 1}},
            new int[][]{{5, 6}, {7, 8}},
            new int[][]{{2, 3}, {1, 4}},
            new int[][]{{1, 1}, {2, 2}},
            new int[][]{{3, 2}, {1, 5}},
            new int[][]{{4, 1}, {2, 3}},
            new int[][]{{0, 1}, {1, 0}},
            new int[][]{{7, 8}, {9, 10}},
            new int[][]{{2, 2}, {2, 2}},
            new int[][]{{3, 3}, {3, 3}},
            new int[][]{{1, 2}, {2, 1}},
            new int[][]{{3, 4}, {4, 3}},
            new int[][]{{5, 1}, {2, 3}},
            new int[][]{{1, 2}, {3, 4}},
            new int[][]{{6, 7}, {8, 9}},
            new int[][]{{1, 0}, {0, 1}},
            new int[][]{{4, 5}, {6, 7}},
            new int[][]{{2, 1}, {1, 2}}
        ));
        int[][] identity2matrix = new int[][]{{1,0},{0,1}};
// Additional 2x2 matrix multiplication tests

        /** Some binary ops */
        BinaryOperator<Integer> intMultiply = (a, b) -> a * b;
        BinaryOperator<Boolean> logicalOr = (a, b) -> a || b;

        BinaryOperator<int[][]> matrixMultiply = (a, b) -> {
            int rowsA = a.length;
            int colsA = a[0].length;
            int colsB = b[0].length;
            int[][] result = new int[rowsA][colsB];
            
            for (int i = 0; i < rowsA; i++) {
                for (int j = 0; j < colsB; j++) {
                    for (int k = 0; k < colsA; k++) {
                        result[i][j] += a[i][k] * b[k][j];
                    }
                }
            }
            return result;
        };

        /** Equality operators for testing */
        
        BiPredicate<int[][], int[][]> matrixEquals = (a, b) -> {
            if (a.length != b.length || a[0].length != b[0].length) return false;
            for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (a[i][j] != b[i][j]) return false;
            }
            }
            return true;
        };

        /** Tests to show reductions always return same result as sequential implementations */

        //
        ReductionTester<Integer> intTester = new ReductionTester<>();
        ReductionTester<Boolean> boolTester = new ReductionTester<>();
        ReductionTester<int[][]> shape2x2matrixTester = new ReductionTester<>();

        // 
        ArrayList<Boolean> results = new ArrayList<>();
        int repetitions = 1000;

        Boolean r = intTester.testReduction(intsArray, intMultiply, 0, (a, b) -> a.equals(b), repetitions);
        results.add(r);

        r = boolTester.testReduction(allFalseBools, logicalOr, false, (a, b) -> a.equals(b), repetitions);
        results.add(r);

        r = boolTester.testReduction(boolsWithTrue, logicalOr, false, (a, b) -> a.equals(b), repetitions);
        results.add(r);

        r = shape2x2matrixTester.testReduction(matricesArray, matrixMultiply, identity2matrix, matrixEquals, repetitions);
        results.add(r);

        r = intTester.testReduction(new ArrayList<>(), intMultiply, 0, (a, b) -> a.equals(b), repetitions);
        results.add(r);

        r = boolTester.testReduction(new ArrayList<>(), logicalOr, false, (a, b) -> a.equals(b), repetitions);
        results.add(r);

        r = boolTester.testReduction(new ArrayList<>(), logicalOr, false, (a, b) -> a.equals(b), repetitions);
        results.add(r);

        r = shape2x2matrixTester.testReduction(new ArrayList<>(), matrixMultiply, identity2matrix, matrixEquals, repetitions);
        results.add(r);

        // 
        // Verify results as expected

        BinaryOperator<Boolean> logicalAnd = (a,b) -> a && b;
        Boolean allTrue = new SeqReducer<>(logicalAnd, true).seqReduce(results);
        
        System.out.println("Test results: " + (allTrue ? "All same" : "Not all same"));

    }
}