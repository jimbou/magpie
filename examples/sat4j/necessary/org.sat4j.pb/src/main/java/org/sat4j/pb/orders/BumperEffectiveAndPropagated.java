/**
 * 
 */
package org.sat4j.pb.orders;

import java.math.BigInteger;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.core.ILits;
import org.sat4j.minisat.core.IOrder;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

/**
 * 
 */
public class BumperEffectiveAndPropagated implements IBumper {

    private int propagatedLit;

    private BigInteger constrDegree;

    private final IVecInt bumpableCandidates = new VecInt();

    private BumpStrategy bumpStrategy;

    @Override
    public void varBumpActivity(ILits voc, BumpStrategy bStrategy, IOrder order,
            PBConstr constr, int lit, int propagated, boolean conflicting) {
        if (lit == 0) {
            // A new constraint is being bumped.
            constrDegree = constr.getDegree();
            bumpableCandidates.clear();
            bumpStrategy = bStrategy;
            propagatedLit = constr.get(propagated);
        }

        int currentLit = constr.get(lit);
        if ((lit == propagated) || (voc.isSatisfied(currentLit)
                && voc.getLevel(currentLit) == voc.getLevel(propagated))) {
            // The implied literals are always bumped.
            bStrategy.varBumpActivity(order, constr, lit);

        } else if (!voc.isFalsified(currentLit)) {
            // Weakening on this literal preserves the propagation.
            constrDegree = constrDegree.subtract(constr.getCoef(lit));

        } else {
            // The literal may be effective.
            bumpableCandidates.push(lit);
        }
    }

    @Override
    public void postBumpActivity(IOrder order, PBConstr constr) {
        for (IteratorInt it = bumpableCandidates.iterator(); it.hasNext();) {
            int v = it.next();
            if (constr.getCoef(v).compareTo(constrDegree) >= 0) {
                bumpStrategy.varBumpActivity(order, constr, v);
            } else {
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "EFFECTIVE AND PROPAGATED";
    }
}
