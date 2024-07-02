/**
 *
 */
package org.sat4j.pb.lcds;

import org.sat4j.minisat.core.ConflictTimer;
import org.sat4j.minisat.core.DataStructureFactory;
import org.sat4j.minisat.core.Glucose2LCDS;
import org.sat4j.minisat.core.LearnedConstraintsDeletionStrategy;
import org.sat4j.minisat.core.Solver;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.pb.core.PBSolverStats;
import org.sat4j.specs.Constr;

/**
 * @author wallon
 *
 */
public class PBGlucoseLCDS<D extends DataStructureFactory>
        extends Glucose2LCDS<D> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final transient ILBDComputerStrategy lbdStrategy;

    private PBGlucoseLCDS(Solver<D> solver, ConflictTimer timer,
            ILBDComputerStrategy lbdStrategy) {
        super(solver, timer);
        this.lbdStrategy = lbdStrategy;
    }

    public static <D extends DataStructureFactory> LearnedConstraintsDeletionStrategy newIgnoreUnassigned(
            Solver<D> solver, ConflictTimer timer) {
        return new PBGlucoseLCDS<D>(solver, timer,
                new IgnoreUnassignedLiteralsLBDComputerStrategy());
    }

    public static <D extends DataStructureFactory> LearnedConstraintsDeletionStrategy newFalsifiedOnly(
            Solver<D> solver, ConflictTimer timer) {
        return new PBGlucoseLCDS<D>(solver, timer,
                new FalsifiedLiteralsOnlyLBDComputerStrategy());
    }

    public static <D extends DataStructureFactory> LearnedConstraintsDeletionStrategy newEffectiveOnly(
            Solver<D> solver, ConflictTimer timer) {
        return new PBGlucoseLCDS<D>(solver, timer,
                new EffectiveLiteralsOnlyLBDComputerStrategy());
    }

    public static <D extends DataStructureFactory> LearnedConstraintsDeletionStrategy newUnassignedDifferent(
            Solver<D> solver, ConflictTimer timer) {
        return new PBGlucoseLCDS<D>(solver, timer,
                new UnassignedLiteralsHaveDifferentLevelLBDComputerStrategy());
    }

    public static <D extends DataStructureFactory> LearnedConstraintsDeletionStrategy newUnassignedSame(
            Solver<D> solver, ConflictTimer timer) {
        return new PBGlucoseLCDS<D>(solver, timer,
                new UnassignedLiteralsHaveSameLevelLBDComputerStrategy());
    }

    public static <D extends DataStructureFactory> LearnedConstraintsDeletionStrategy newDegree(
            Solver<D> solver, ConflictTimer timer) {
        return new PBGlucoseLCDS<D>(solver, timer,
                new DegreeLBDComputerStrategy());
    }

    public static <D extends DataStructureFactory> LearnedConstraintsDeletionStrategy newDegreeSize(
            Solver<D> solver, ConflictTimer timer) {
        return new PBGlucoseLCDS<D>(solver, timer,
                new DegreeSizeLBDComputerStrategy());
    }

    public static <D extends DataStructureFactory> LearnedConstraintsDeletionStrategy newRatio(
            Solver<D> solver, ConflictTimer timer) {
        return new PBGlucoseLCDS<D>(solver, timer,
                new RatioCoefficientsDegreeLBDComputerStrategy());
    }

    public static <D extends DataStructureFactory> LearnedConstraintsDeletionStrategy newSlack(
            Solver<D> solver, ConflictTimer timer) {
        return new PBGlucoseLCDS<D>(solver, timer,
                new SlackLBDComputerStrategy());
    }

    @Override
    public void init() {
        super.init();
        lbdStrategy.init(getSolver().nVars());
    }

    @Override
    protected int computeLBD(Constr constr, int propagated) {
        if (constr instanceof PBConstr) {
            PBConstr pbconstr = (PBConstr) constr;
            return lbdStrategy.computeLBD(getSolver().getVocabulary(), pbconstr,
                    propagated);
        }
        return super.computeLBD(constr, propagated);
    }

    @Override
    public String toString() {
        return "Glucose LCDS adapted to PB, with LBD computation strategy: "
                + lbdStrategy;
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
