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
public class DegreeLBDComputerStrategy implements ILBDComputerStrategy {

    @Override
    public void init(int howmany) {
        // TODO Auto-generated method stub

    }

    @Override
    public int computeLBD(ILits voc, PBConstr constr, int propagated) {
        BigInteger degree = constr.getDegree();
        if (degree.bitLength() < Integer.SIZE) {
            return degree.intValue();
        }
        return Integer.MAX_VALUE;
    }

}
