/**
 * 
 */
package org.sat4j.pb.restarts;

import java.math.BigInteger;

import org.sat4j.minisat.core.CircularBuffer;
import org.sat4j.minisat.core.RestartStrategy;
import org.sat4j.minisat.core.SearchParams;
import org.sat4j.minisat.core.SolverStats;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.specs.Constr;

/**
 * @author wallon
 *
 */
public class GrowingCoefficientRestarts implements RestartStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final BigIntegerCircularBuffer bufferDegrees = new BigIntegerCircularBuffer(
            50);

    private final CircularBuffer bufferTrail = new CircularBuffer(5000);

    private BigInteger sumOfAllDegrees = BigInteger.ZERO;

    private SolverStats stats;

    @Override
    public void reset() {
        sumOfAllDegrees = BigInteger.ZERO;
        bufferDegrees.clear();
        bufferTrail.clear();
    }

    @Override
    public void newConflict() {
    }

    @Override
    public void init(SearchParams params, SolverStats stats) {
        this.stats = stats;
        reset();
    }

    @Override
    public boolean shouldRestart() {
        return bufferDegrees.isFull() && bufferDegrees.average()
                .multiply(BigInteger.valueOf(stats.getConflicts()).shiftLeft(2))
                .compareTo(sumOfAllDegrees.multiply(BigInteger.valueOf(5))) > 0;
    }

    @Override
    public void onRestart() {
        bufferDegrees.clear();
    }

    @Override
    public void newLearnedClause(Constr learned, int trailLevel) {
        // on conflict
        BigInteger degree;
        if (learned instanceof PBConstr) {
            degree = ((PBConstr) learned).getDegree();
        } else {
            degree = BigInteger.ONE;
        }
        bufferDegrees.push(degree);
        sumOfAllDegrees = sumOfAllDegrees.add(degree);
        bufferTrail.push(trailLevel);
        if (stats.getConflicts() > 10000 && bufferTrail.isFull()
                && trailLevel * 5L > 7L * bufferTrail.average()) {
            bufferDegrees.clear();
        }
    }

    @Override
    public String toString() {
        return "Dynamic restart strategy considering the size of the coefficients";
    }

}
