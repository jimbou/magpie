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
public class UnassignedLiteralsHaveDifferentLevelLBDComputerStrategy
        extends AbstractLBDComputerStrategy {

    @Override
    protected int unassignedLiteral(ILits voc, PBConstr constr, int i) {
        return 1;
    }

    @Override
    protected int assignedLiteral(ILits voc, PBConstr constr, int i) {
        return 1;
    }
}
