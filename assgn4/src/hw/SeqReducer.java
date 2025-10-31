package hw;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

class SeqReducer<T> {

    private final BinaryOperator<T> op;
    private final T identity;

    public SeqReducer(BinaryOperator<T> op, T identity) {
        this.op = op;
        this.identity = identity;
    }

    public T seqReduce(ArrayList<T> list) {
        T r = identity;

        for (T a : list) {
            r = op.apply(r, a);
        }

        return r;
    }

}