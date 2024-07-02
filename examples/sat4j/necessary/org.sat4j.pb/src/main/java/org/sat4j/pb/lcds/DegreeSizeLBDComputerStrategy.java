/**
 * 
 */
package org.sat4j.pb.lcds;

import org.sat4j.minisat.core.ILits;
import org.sat4j.pb.constraints.pb.PBConstr;

/**
 * @author wallon
 *
 */
public class DegreeSizeLBDComputerStrategy implements ILBDComputerStrategy {

    @Override
    public void init(int howmany) {
        // TODO Auto-generated method stub

    }

    @Override
    public int computeLBD(ILits voc, PBConstr constr, int propagated) {
        return constr.getDegree().bitLength();
    }

}
