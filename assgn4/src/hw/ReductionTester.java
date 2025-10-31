package hw;

import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

class ReductionTester<T> {
    public Boolean testReduction (ArrayList<T> list, BinaryOperator<T> op, T identity, BiPredicate<T, T> equalityOp, Integer times) {
        /**
         * list - the list to perform reduction operator (op) over
         * op - 
         * identity - element s.t. any element op on that is element
         * 
         * times - number of times to execute test
         */

        SeqReducer<T> sr = new SeqReducer<>(op, identity);
        Reducer<T> pr = new Reducer<>(op, identity);

        ArrayList<Boolean> results = new ArrayList<>();

        try {

            for (int i = times; i > 0; i--) {
                T sres = sr.seqReduce(list);
                T pres = pr.reduceAsync(list).get();
                if (!equalityOp.test(sres, pres)) {
                    return false;
                }
            }

        } catch (Exception e) {
        }

        return true;
        
    }

}