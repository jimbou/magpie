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
public class LBDComputerStrategyDecorator implements ILBDComputerStrategy {

    private final ILBDComputerStrategy decorated;

    public LBDComputerStrategyDecorator(ILBDComputerStrategy decorated) {
        this.decorated = decorated;
    }

    @Override
    public void init(int howmany) {
        decorated.init(howmany);
    }

    @Override
    public int computeLBD(ILits voc, PBConstr constr, int propagated) {
        return decorated.computeLBD(voc, constr, propagated);
    }

}
