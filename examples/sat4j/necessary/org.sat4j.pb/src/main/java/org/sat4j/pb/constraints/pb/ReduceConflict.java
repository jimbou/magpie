/**
 * 
 */
package org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.core.VecInt;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

/**
 * @author romainwallon
 *
 */
public class ReduceConflict implements IReduceConflictStrategy {

    private static final IReduceConflictStrategy INSTANCE = new ReduceConflict();

    private ReduceConflict() {
        // TODO Auto-generated constructor stub
    }

    public static final IReduceConflictStrategy instance() {
        return INSTANCE;
    }

    @Override
    public void reduceConflict(ConflictMapDivideByPivot conflict, int literal) {
        BigInteger coef = conflict.weightedLits.get(literal);
        int size = conflict.weightedLits.size();
        IVecInt toRemove = new VecInt();
        for (int i = 0; i < size; i++) {
            int l = conflict.weightedLits.getLit(i);
            BigInteger coefLit = conflict.weightedLits.getCoef(i);
            if (conflict.voc.isFalsified(conflict.weightedLits.getLit(i))) {
                // The coefficient is not rounded up after division.
                conflict.setCoef(l, ConflictMapDivideByPivot
                        .ceildiv(conflict.weightedLits.getCoef(i), coef));

            } else {
                // Weakening the coefficient to allow its division.
                // No rounding up here: coefficients are rounded down, and the
                // remainder is subtracted from the degree.
                BigInteger[] tmp = conflict.divisionStrategy.divide(coefLit,
                        coef);
                if (tmp[0].signum() == 0) {
                    toRemove.push(l);
                    conflict.listener
                            .weakenOnConflict(LiteralsUtils.toDimacs(l));
                } else {
                    conflict.setCoef(l, tmp[0]);
                    conflict.listener.weakenOnConflict(tmp[1],
                            LiteralsUtils.toDimacs(l));
                }
                conflict.degree = conflict.degree.subtract(tmp[1]);
            }
        }

        for (IteratorInt it = toRemove.iterator(); it.hasNext();) {
            conflict.removeCoef(it.next());
        }
        conflict.listener.divideConflict(coef);
        conflict.degree = ConflictMapDivideByPivot.ceildiv(conflict.degree,
                coef);
        conflict.saturation();
        conflict.coefMultCons = BigInteger.ONE;
        conflict.stats.incNumberOfRoundingOperations();
    }

}
