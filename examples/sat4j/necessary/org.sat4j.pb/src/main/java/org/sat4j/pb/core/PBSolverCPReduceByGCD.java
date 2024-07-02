package org.sat4j.pb.core;

import org.sat4j.minisat.core.IOrder;
import org.sat4j.minisat.core.LearningStrategy;
import org.sat4j.minisat.core.RestartStrategy;
import org.sat4j.minisat.core.SearchParams;
import org.sat4j.pb.constraints.pb.AutoDivisionStrategy;
import org.sat4j.pb.constraints.pb.ConflictMapReduceByGCD;
import org.sat4j.pb.constraints.pb.IConflict;
import org.sat4j.pb.constraints.pb.IWeakeningStrategy;
import org.sat4j.pb.constraints.pb.NoPostProcess;
import org.sat4j.pb.constraints.pb.NoPreProcess;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.pb.constraints.pb.SkipStrategy;

public class PBSolverCPReduceByGCD extends PBSolverCP {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PBSolverCPReduceByGCD(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, IOrder order) {
        super(learner, dsf, order);
        // TODO Auto-generated constructor stub
    }

    public PBSolverCPReduceByGCD(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            RestartStrategy restarter) {
        super(learner, dsf, params, order, restarter);
        // TODO Auto-generated constructor stub
    }

    public PBSolverCPReduceByGCD(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order) {
        super(learner, dsf, params, order);
        // TODO Auto-generated constructor stub
    }

    public PBSolverCPReduceByGCD(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, IOrder order, boolean noRemove,
            SkipStrategy skipAllow) {
        super(learner, dsf, order, noRemove, skipAllow);
        // TODO Auto-generated constructor stub
    }

    public PBSolverCPReduceByGCD(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            RestartStrategy restarter, boolean noRemove,
            SkipStrategy skipAllow) {
        super(learner, dsf, params, order, restarter, noRemove, skipAllow);
        // TODO Auto-generated constructor stub
    }

    public PBSolverCPReduceByGCD(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            boolean noRemove, SkipStrategy skipAllow) {
        super(learner, dsf, params, order, noRemove, skipAllow);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected IConflict chooseConflict(PBConstr myconfl, int level) {
        return ConflictMapReduceByGCD.createConflict(myconfl, level,
                isNoRemove(), isSkipAllow(), NoPreProcess.instance(),
                NoPostProcess.instance(), IWeakeningStrategy.UNASSIGNED_FIRST,
                AutoDivisionStrategy.ENABLED, pbStats);
    }

    @Override
    public String toString(String prefix) {
        return super.toString(prefix) + "\n" + prefix
                + "Reduce coefs as much as possible by GCD over coefficients at each step during conflict analysis (ConflictMapReduceByGCD)";
    }

}
