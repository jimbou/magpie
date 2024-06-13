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
public class FalsifiedLiteralsOnlyLBDComputerStrategy
        extends AbstractLBDComputerStrategy {

    @Override
    protected int unassignedLiteral(ILits voc, PBConstr constr, int i) {
        return 0;
    }

    @Override
    protected int assignedLiteral(ILits voc, PBConstr constr, int i) {
        if (voc.isSatisfied(constr.get(i))) {
            return 0;
        }
        return 1;
    }
}
