/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004, 2012 Artois University and CNRS
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU Lesser General Public License Version 2.1 or later (the
 * "LGPL"), in which case the provisions of the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL, and not to allow others to use your version of
 * this file under the terms of the EPL, indicate your decision by deleting
 * the provisions above and replace them with the notice and other provisions
 * required by the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of the EPL or the LGPL.
 *
 * Based on the original MiniSat specification from:
 *
 * An extensible SAT solver. Niklas Een and Niklas Sorensson. Proceedings of the
 * Sixth International Conference on Theory and Applications of Satisfiability
 * Testing, LNCS 2919, pp 502-518, 2003.
 *
 * See www.minisat.se for the original solver in C++.
 *
 * Contributors:
 *   CRIL - initial API and implementation
 *******************************************************************************/
package org.sat4j.pb.core;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.core.Vec;
import org.sat4j.minisat.core.IOrder;
import org.sat4j.minisat.core.LearnedConstraintsEvaluationType;
import org.sat4j.minisat.core.LearningStrategy;
import org.sat4j.minisat.core.Pair;
import org.sat4j.minisat.core.RestartStrategy;
import org.sat4j.minisat.core.SearchParams;
import org.sat4j.minisat.restarts.MiniSATRestarts;
import org.sat4j.pb.IPBSolverService;
import org.sat4j.pb.constraints.pb.AutoDivisionStrategy;
import org.sat4j.pb.constraints.pb.ConflictMap;
import org.sat4j.pb.constraints.pb.IConflict;
import org.sat4j.pb.constraints.pb.IConflictFactory;
import org.sat4j.pb.constraints.pb.IPostProcess;
import org.sat4j.pb.constraints.pb.IPreProcess;
import org.sat4j.pb.constraints.pb.IWeakeningStrategy;
import org.sat4j.pb.constraints.pb.NoPostProcess;
import org.sat4j.pb.constraints.pb.NoPreProcess;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.pb.constraints.pb.SkipStrategy;
import org.sat4j.pb.orders.BumpStrategy;
import org.sat4j.pb.orders.Bumper;
import org.sat4j.pb.orders.IBumper;
import org.sat4j.pb.tools.PBSearchListener;
import org.sat4j.pb.tools.PBSearchListenerDecorator;
import org.sat4j.pb.tools.VoidPBTracing;
import org.sat4j.specs.Constr;
import org.sat4j.specs.ISolverService;
import org.sat4j.specs.IVec;
import org.sat4j.specs.SearchListener;
import org.sat4j.specs.TimeoutException;

/**
 * @author parrain To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PBSolverCP extends PBSolver {

    private static final long serialVersionUID = 1L;

    /**
     * removing or not satisfied literals at a higher level before cutting
     * planes.
     */
    private boolean noRemove = true;

    /**
     * skipping as much as possible avoidable cutting planes during analysis
     * conflict
     */
    private SkipStrategy skipAllow = SkipStrategy.SKIP;

    private IConflictFactory conflictFactory = ConflictMap.factory();

    private IPreProcess preprocess = NoPreProcess.instance();
    private IPostProcess postprocess = NoPostProcess.instance();

    private IWeakeningStrategy weakeningStrategy = IWeakeningStrategy.UNASSIGNED_FIRST;

    private AutoDivisionStrategy autoDivisionStrategy = AutoDivisionStrategy.DISABLED;

    private BumpStrategy bumpStrategy = BumpStrategy.ALWAYS_ONE;

    private IBumper bumper = Bumper.ANY;

    {
        setSearchListener(new VoidPBTracing());
    }

    /**
     * @param acg
     * @param learner
     * @param dsf
     */
    public PBSolverCP(LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, IOrder order) {
        super(learner, dsf, new SearchParams(1.5, 100), order,
                new MiniSATRestarts());
        setLearnedConstraintsDeletionStrategy(
                LearnedConstraintsEvaluationType.ACTIVITY);
    }

    public PBSolverCP(LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            RestartStrategy restarter) {
        super(learner, dsf, params, order, restarter);
        setLearnedConstraintsDeletionStrategy(
                LearnedConstraintsEvaluationType.ACTIVITY);
    }

    public PBSolverCP(LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order) {
        super(learner, dsf, params, order, new MiniSATRestarts());
        setLearnedConstraintsDeletionStrategy(
                LearnedConstraintsEvaluationType.ACTIVITY);
    }

    public PBSolverCP(LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, IOrder order, boolean noRemove,
            SkipStrategy skipAllow) {
        this(learner, dsf, order);
        this.noRemove = noRemove;
        this.skipAllow = skipAllow;
    }

    public PBSolverCP(LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            RestartStrategy restarter, boolean noRemove,
            SkipStrategy skipAllow) {
        this(learner, dsf, params, order, restarter);
        this.noRemove = noRemove;
        this.skipAllow = skipAllow;
    }

    public PBSolverCP(LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            boolean noRemove, SkipStrategy skipAllow) {
        this(learner, dsf, params, order);
        this.noRemove = noRemove;
        this.skipAllow = skipAllow;
    }

    @SuppressWarnings("unchecked")
    protected PBSearchListener<IPBSolverService> listener() {
        return (PBSearchListener<IPBSolverService>) slistener;
    }

    @Override
    public void analyze(Constr myconfl, Pair results) throws TimeoutException {
        if (someCriteria()) {
            analyzeCP(myconfl, results);
        } else {
            super.analyze(myconfl, results);
        }
    }

    public void analyzeCP(Constr myconfl, Pair results)
            throws TimeoutException {
        listener().onConflict((PBConstr) myconfl);
        int litImplied = this.trail.last();
        int currentLevel = this.voc.getLevel(litImplied);
        IConflict confl = chooseConflict((PBConstr) myconfl, currentLevel);
        confl.setListener(listener());
        confl.setDecisionLevel(currentLevel);
        assert confl.slackConflict().signum() < 0;
        while (!confl.isUnsat() && !confl.isAssertive(currentLevel)) {
            if (!this.undertimeout) {
                throw new TimeoutException();
            }
            PBConstr constraint = (PBConstr) this.voc.getReason(litImplied);
            // result of the resolution is in the conflict (confl)
            confl.resolve(constraint, litImplied, this);
            updateNumberOfReductions(confl);
            assert confl.slackConflict().signum() < 0;
            // implication trail is reduced
            if (this.trail.size() == 1) {
                break;
            }
            undoOne();
            if (decisionLevel() == 0) {
                break;
            }
            litImplied = this.trail.last();
            if (this.voc.getLevel(litImplied) != currentLevel) {
                this.trailLim.pop();
                slistener.backtracking(LiteralsUtils.toDimacs(litImplied));
            }
            assert this.voc.getLevel(litImplied) <= currentLevel;
            currentLevel = this.voc.getLevel(litImplied);
            confl.setDecisionLevel(currentLevel);
            assert confl.slackIsCorrect(currentLevel);
            assert currentLevel == decisionLevel();
            assert litImplied > 1;
        }
        assert confl.isAssertive(currentLevel) || this.trail.size() == 1
                || decisionLevel() == 0 || confl.isUnsat();

        assert currentLevel == decisionLevel();
        confl.setDecisionLevel(currentLevel);
        confl.undoOne(this.trail.last());
        undoOne();
        this.qhead = this.trail.size();
        updateNumberOfReducedLearnedConstraints(confl);
        // necessary informations to build a PB-constraint
        // are kept from the conflict
        if (confl.isUnsat() || confl.size() == 0 || decisionLevel() == 0
                || (this.trail.size() == 0
                        && confl.slackConflict().signum() < 0)) {
            listener().learn(null);
            results.setReason(null);
            results.setBacktrackLevel(-1);
            return;
        }

        // assertive PB-constraint is build and referenced
        confl.postProcess(currentLevel);
        PBConstr resConstr = (PBConstr) this.dsfactory
                .createUnregisteredPseudoBooleanConstraint(confl);
        getLearnedConstraintsDeletionStrategy().onClauseLearning(resConstr);
        results.setReason(resConstr);

        // the conflict give the highest decision level for the backtrack
        // (which is less than current level)
        int backtrackLevel = confl.getBacktrackLevel(currentLevel);
        results.setBacktrackLevel(backtrackLevel);
        // }
    }

    protected IConflict chooseConflict(PBConstr myconfl, int level) {
        IConflict conflict = conflictFactory.createConflict(myconfl, level,
                noRemove, skipAllow, preprocess, postprocess, weakeningStrategy,
                autoDivisionStrategy, pbStats);
        return conflict;
    }

    @Override
    public String toString(String prefix) {
        return prefix + "Cutting planes based inference ("
                + this.getClass().getName() + ")\n"
                + (this.noRemove ? ""
                        : prefix + " - Removing satisfied literals at a higher level before CP \n")
                + prefix + " - " + skipAllow.getDescription() + "\n" + prefix
                + " - " + autoDivisionStrategy + "\n" + prefix + " - "
                + postprocess + "\n" + prefix + " - " + preprocess + "\n"
                + prefix + " - " + conflictFactory + "\n" + prefix + " - "
                + weakeningStrategy + "\n" + prefix + " - Bump strategy: "
                + bumpStrategy + "\n" + prefix + " - Bumper: " + bumper + "\n"
                + super.toString(prefix);
    }

    private final IVec<String> conflictVariables = new Vec<String>();
    private final IVec<String> conflictConstraints = new Vec<String>();

    void initExplanation() {
        this.conflictVariables.clear();
        this.conflictConstraints.clear();
    }

    boolean someCriteria() {
        return true;
    }

    protected void updateNumberOfReductions(IConflict confl) {
    }

    protected void updateNumberOfReducedLearnedConstraints(IConflict confl) {
    }

    public void setBumpStrategy(BumpStrategy bumpStrategy) {
        this.bumpStrategy = bumpStrategy;
    }

    public void setBumper(IBumper bumperEffective) {
        this.bumper = bumperEffective;
    }

    public SkipStrategy isSkipAllow() {
        return skipAllow;
    }

    public void setSkipAllow(SkipStrategy skipAllow) {
        this.skipAllow = skipAllow;
    }

    public boolean isNoRemove() {
        return noRemove;
    }

    public void setNoRemove(boolean noRemove) {
        this.noRemove = noRemove;
    }

    public IConflictFactory getConflictFactory() {
        return conflictFactory;
    }

    public void setConflictFactory(IConflictFactory conflictFactory) {
        this.conflictFactory = conflictFactory;
    }

    public void setPreprocess(IPreProcess preprocess) {
        this.preprocess = preprocess;
    }

    public IPostProcess getPostprocess() {
        return postprocess;
    }

    public void setPostprocess(IPostProcess postprocess) {
        this.postprocess = postprocess;
    }

    public IWeakeningStrategy getWeakeningStrategy() {
        return weakeningStrategy;
    }

    public void setWeakeningStrategy(IWeakeningStrategy weakeningStrategy) {
        this.weakeningStrategy = weakeningStrategy;
    }

    public void setAutoDivisionStrategy(
            AutoDivisionStrategy autoDivisionStrategy) {
        this.autoDivisionStrategy = autoDivisionStrategy;
    }

    @Override
    public void varBumpActivity(Constr constr, int i, int p,
            boolean conflicting) {
        bumper.varBumpActivity(voc, bumpStrategy, getOrder(), (PBConstr) constr,
                i, p, conflicting);
    }

    @Override
    public void postBumpActivity(Constr constr) {
        bumper.postBumpActivity(getOrder(), (PBConstr) constr);
    }

    @Override
    public <S extends ISolverService> void setSearchListener(
            SearchListener<S> sl) {
        if (sl instanceof PBSearchListener) {
            super.setSearchListener(sl);
        } else {
            super.setSearchListener(new PBSearchListenerDecorator<S>(sl));
        }
    }
}
