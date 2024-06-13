/**
 * 
 */
package org.sat4j.pb.lcds;

import org.sat4j.minisat.core.ActivityLCDS;
import org.sat4j.minisat.core.ConflictTimer;
import org.sat4j.minisat.core.DataStructureFactory;
import org.sat4j.minisat.core.Solver;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.pb.core.PBSolverStats;
import org.sat4j.specs.Constr;

/**
 * @author romain
 *
 */
public class PBActivityLCDS extends ActivityLCDS {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PBActivityLCDS(Solver<? extends DataStructureFactory> solver,
            ConflictTimer timer) {
        super(solver, timer);
    }

    @Override
    protected void onRemove(Constr c) {
        PBConstr constr = (PBConstr) c;
        PBSolverStats stats = (PBSolverStats) solver.getStats();
        stats.incNbRemoved();
        stats.setMinRemoved(constr.getDegree());
        stats.setMaxRemoved(constr.getDegree());
    }

}
