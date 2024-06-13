/**
 * 
 */
package org.sat4j.pb.lcds;

import java.math.BigInteger;

import org.sat4j.minisat.core.ILits;
import org.sat4j.pb.constraints.pb.PBConstr;

/**
 * @author wallon
 *
 */
public class WeightedLBDComputerStrategy extends LBDComputerStrategyDecorator {

    public WeightedLBDComputerStrategy(ILBDComputerStrategy decorated) {
        super(decorated);
    }

    @Override
    public int computeLBD(ILits voc, PBConstr constr, int propagated) {
        BigInteger lbd = BigInteger
                .valueOf(super.computeLBD(voc, constr, propagated));
        BigInteger weighted = lbd.multiply(constr.getDegree().multiply(lbd));
        if (weighted.bitLength() < Integer.SIZE) {
            return weighted.intValue();
        }
        return Integer.MAX_VALUE;
    }

}
