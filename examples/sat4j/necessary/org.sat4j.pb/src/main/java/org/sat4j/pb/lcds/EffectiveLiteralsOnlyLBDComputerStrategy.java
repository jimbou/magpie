/**
 * 
 */
package org.sat4j.pb.lcds;

import java.math.BigInteger;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.core.ILits;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

/**
 * @author wallon
 *
 */
public class EffectiveLiteralsOnlyLBDComputerStrategy
        extends AbstractLBDComputerStrategy {

    private int currentPropagated;

    private BigInteger degree;

    private final IVecInt lbdCandidates = new VecInt();

    @Override
    protected void startComputeLBD(PBConstr constr, int propagated) {
        this.degree = constr.getDegree();
        this.currentPropagated = propagated;
        lbdCandidates.clear();
    }

    @Override
    protected int unassignedLiteral(ILits voc, PBConstr constr, int i) {
        degree = degree.subtract(constr.getCoef(i));
        return 0;
    }

    @Override
    protected int assignedLiteral(ILits voc, PBConstr constr, int i) {
        if (constr.get(i) == currentPropagated) {
            return 0;
        }

        if (voc.isSatisfied(constr.get(i))) {
            degree = degree.subtract(constr.getCoef(i));
            return 0;
        }

        lbdCandidates.push(i);
        return 1;
    }

    @Override
    protected int fixLbd(PBConstr constr, int lbd) {
        // For each level, the candidate literal is the one with the highest
        // coefficient.
        // As such, if it is effective, the level indeed appears in the LBD.
        // Otherwise, all other literals at this level will have a smaller
        // coefficient, and will not be effective either.
        int newLbd = 0;
        for (IteratorInt it = lbdCandidates.iterator(); it.hasNext();) {
            int v = it.next();
            if (constr.getCoef(v).compareTo(degree) >= 0) {
                newLbd++;
            } else {
                break;
            }
        }
        return newLbd;
    }
}
