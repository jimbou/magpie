package org.sat4j.pb.core;

import org.sat4j.minisat.core.IOrder;
import org.sat4j.minisat.core.LearningStrategy;
import org.sat4j.minisat.core.RestartStrategy;
import org.sat4j.minisat.core.SearchParams;
import org.sat4j.pb.constraints.pb.AutoDivisionStrategy;
import org.sat4j.pb.constraints.pb.ConflictMapReduceByPowersOf2;
import org.sat4j.pb.constraints.pb.IConflict;
import org.sat4j.pb.constraints.pb.IWeakeningStrategy;
import org.sat4j.pb.constraints.pb.NoPostProcess;
import org.sat4j.pb.constraints.pb.NoPreProcess;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.pb.constraints.pb.SkipStrategy;

public class PBSolverCPReduceByPowersOf2 extends PBSolverCP {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PBSolverCPReduceByPowersOf2(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, IOrder order) {
        super(learner, dsf, order);
        // TODO Auto-generated constructor stub
    }

    public PBSolverCPReduceByPowersOf2(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            RestartStrategy restarter) {
        super(learner, dsf, params, order, restarter);
        // TODO Auto-generated constructor stub
    }

    public PBSolverCPReduceByPowersOf2(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order) {
        super(learner, dsf, params, order);
        // TODO Auto-generated constructor stub
    }

    public PBSolverCPReduceByPowersOf2(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, IOrder order, boolean noRemove,
            SkipStrategy skipAllow) {
        super(learner, dsf, order, noRemove, skipAllow);
        // TODO Auto-generated constructor stub
    }

    public PBSolverCPReduceByPowersOf2(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            RestartStrategy restarter, boolean noRemove,
            SkipStrategy skipAllow) {
        super(learner, dsf, params, order, restarter, noRemove, skipAllow);
        // TODO Auto-generated constructor stub
    }

    public PBSolverCPReduceByPowersOf2(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            boolean noRemove, SkipStrategy skipAllow) {
        super(learner, dsf, params, order, noRemove, skipAllow);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected IConflict chooseConflict(PBConstr myconfl, int level) {
        return ConflictMapReduceByPowersOf2.createConflict(myconfl, level,
                isNoRemove(), isSkipAllow(), NoPreProcess.instance(),
                NoPostProcess.instance(), IWeakeningStrategy.UNASSIGNED_FIRST,
                AutoDivisionStrategy.ENABLED, pbStats);
    }

    @Override
    public String toString(String prefix) {
        return super.toString(prefix) + "\n" + prefix
                + "Reduce coefs as much as possible by powers of 2 at each step during conflict analysis (ConflictMapReduceByPowersOf2)";
    }

}
