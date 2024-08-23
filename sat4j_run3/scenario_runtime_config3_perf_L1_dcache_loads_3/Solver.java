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
package org.sat4j.minisat.core;

import static org.sat4j.Messages.NOT_IMPLEMENTED_YET;
import static org.sat4j.core.LiteralsUtils.toDimacs;
import static org.sat4j.core.LiteralsUtils.toInternal;
import static org.sat4j.core.LiteralsUtils.var;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sat4j.annotations.Feature;
import org.sat4j.core.ConstrGroup;
import org.sat4j.core.LiteralsUtils;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.constraints.xor.Xor;
import org.sat4j.specs.AssignmentOrigin;
import org.sat4j.specs.Constr;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ILogAble;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.ISolverService;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;
import org.sat4j.specs.Lbool;
import org.sat4j.specs.Propagatable;
import org.sat4j.specs.SearchListener;
import org.sat4j.specs.TimeoutException;
import org.sat4j.specs.UnitClauseConsumer;
import org.sat4j.specs.UnitClauseProvider;

/**
 * The backbone of the library providing the modular implementation of a MiniSAT
 * (Chaff) like solver.
 * 
 * @author leberre
 */
public class Solver<D extends DataStructureFactory>
        implements ISolverService, ICDCL<D> {

    private static final String CALL_THE_SOLVE_METHOD_FIRST = "Call the solve method first!!!";

    private static final long serialVersionUID = 1L;

    private static final double CLAUSE_RESCALE_FACTOR = 1e-20;

    private static final double CLAUSE_RESCALE_BOUND = 1
            / CLAUSE_RESCALE_FACTOR;

    protected ILogAble out;

    /**
     * Set of original constraints.
     */
    protected final IVec<Constr> constrs = new Vec<>();

    /**
     * Set of learned constraints.
     */
    protected final IVec<Constr> learnts = new Vec<>();

    /**
     * Increment for clause activity.
     */
    private double claInc = 1.0;

    /**
     * decay factor pour l'activit? des clauses.
     */
    private double claDecay = 1.0;

    /**
     * propagation queue
     */
    // head of the queue in trail ... (taken from MiniSAT 1.14)
    protected int qhead = 0;

    /**
     * variable assignments (literals) in chronological order.
     */
    protected final IVecInt trail = new VecInt();

    /**
     * position of the decision levels on the trail.
     */
    protected final IVecInt trailLim = new VecInt();

    /**
     * position of assumptions before starting the search.
     */
    protected int rootLevel;

    private int[] model = null;

    protected ILits voc;

    private IOrder order;

    private final ActivityComparator comparator = new ActivityComparator();

    SolverStats stats = new SolverStats();

    private LearningStrategy<D> learner;

    protected volatile boolean undertimeout;

    private long timeout = Integer.MAX_VALUE;

    private boolean timeBasedTimeout = true;

    protected D dsfactory;

    private SearchParams params;

    private final IVecInt internalDimacsReusableVector = new VecInt();

    protected SearchListener slistener;

    private RestartStrategy restarter;

    private final Map<String, Counter> constrTypes = new HashMap<>();

    private boolean isDBSimplificationAllowed = false;

    final IVecInt learnedLiterals = new VecInt();

    boolean verbose = false;

    private boolean keepHot = false;

    private String prefix = "c ";
    private int declaredMaxVarId = 0;

    private UnitClauseProvider unitClauseProvider = UnitClauseProvider.VOID;

    private UnitClauseConsumer unitClauseConsumer = UnitClauseConsumer.VOID;

    private final boolean classifyLiterals = System
            .getProperty("color") != null;

    /**
     * Translates an IvecInt containing Dimacs formatted variables into and
     * IVecInt containing internal formatted variables.
     * 
     * Note that for sake of efficiency, the IVecInt returned by this method is
     * always the same. DO NOT STORE IT N A CONSTRAINT.
     * 
     * @param in
     *            a vector of Dimacs formatted variables (e.g. 1,-2)
     * @return a vector of variables using internal representation (e.g 2,5)
     * @see LiteralsUtils
     * @since 2.3.6
     */
    public IVecInt dimacs2internal(IVecInt in) {
        this.internalDimacsReusableVector.clear();
        this.internalDimacsReusableVector.ensure(in.size());
        int p;
        for (var i = 0; i < in.size(); i++) {
            p = in.get(i);
            if (p == 0) {
                throw new IllegalArgumentException(
                        "0 is not a valid variable identifier");
            }
            this.internalDimacsReusableVector
                    .unsafePush(this.voc.getFromPool(p));
        }
        return this.internalDimacsReusableVector;
    }

    /*
     * @since 2.3.1
     */
    public void registerLiteral(int p) {
        this.voc.getFromPool(p);
    }

    /**
     * creates a Solver without LearningListener. A learningListener must be
     * added to the solver, else it won't backtrack!!! A data structure factory
     * must be provided, else it won't work either.
     */

    public Solver(LearningStrategy<D> learner, D dsf, IOrder order,
            RestartStrategy restarter) {
        this(learner, dsf, new SearchParams(), order, restarter);
    }

    public Solver(LearningStrategy<D> learner, D dsf, SearchParams params,
            IOrder order, RestartStrategy restarter) {
        this(learner, dsf, params, order, restarter, ILogAble.CONSOLE);
    }

    public Solver(LearningStrategy<D> learner, D dsf, SearchParams params,
            IOrder order, RestartStrategy restarter, ILogAble logger) {
        this.order = order;
        this.params = params;
        this.restarter = restarter;
        this.out = logger;
        setDataStructureFactory(dsf);
        // should be called after dsf has been set up
        setLearningStrategy(learner);
        setSearchListener(SearchListener.voidSearchListener());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#setDataStructureFactory(D)
     */
    public final void setDataStructureFactory(D dsf) {
        this.dsfactory = dsf;
        this.dsfactory.setUnitPropagationListener(this);
        this.dsfactory.setLearner(this);
        this.voc = dsf.getVocabulary();
        this.order.setLits(this.voc);
    }

    /**
     * @since 2.2
     */
    public boolean isVerbose() {
        return this.verbose;
    }

    /**
     * @param value
     * @since 2.2
     */
    public void setVerbose(boolean value) {
        this.verbose = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#setSearchListener(org.sat4j.specs.
     * SearchListener )
     */
    public <S extends ISolverService> void setSearchListener(
            SearchListener<S> sl) {
        this.slistener = sl;
        this.slistener.init(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#getSearchListener()
     */
    public SearchListener<? extends ISolverService> getSearchListener() {
        return this.slistener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.core.ICDCL#setLearningStrategy(org.sat4j.minisat.core.
     * LearningStrategy)
     */
    public void setLearningStrategy(LearningStrategy<D> strategy) {
        if (this.learner != null) {
            this.learner.setSolver(null);
        }
        this.learner = strategy;
        strategy.setSolver(this);
    }

    public void setTimeout(int t) {
        this.timeout = t * 1000L;
        this.timeBasedTimeout = true;
        this.undertimeout = true;
    }

    public void setTimeoutMs(long t) {
        this.timeout = t;
        this.timeBasedTimeout = true;
        this.undertimeout = true;
    }

    public void setTimeoutOnConflicts(int count) {
        this.timeout = count;
        this.timeBasedTimeout = false;
        this.undertimeout = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#setSearchParams(org.sat4j.minisat.core.
     * SearchParams)
     */
    public void setSearchParams(SearchParams sp) {
        this.params = sp;
    }

    public SearchParams getSearchParams() {
        return this.params;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.core.ICDCL#setRestartStrategy(org.sat4j.minisat.core
     * .RestartStrategy)
     */
    public void setRestartStrategy(RestartStrategy restarter) {
        this.restarter = restarter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#getRestartStrategy()
     */
    public RestartStrategy getRestartStrategy() {
        return this.restarter;
    }

    public void expireTimeout() {
        this.undertimeout = false;
        if (this.timeBasedTimeout) {
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
        } else {
            if (this.conflictCount != null) {
                this.conflictCount = null;
            }
        }
    }

    protected int nAssigns() {
        return this.trail.size();
    }

    public int nConstraints() {
        return this.constrs.size();
    }

    public void learn(Constr c) {
        this.slistener.learn(c);
        this.learnts.push(c);
        c.setLearnt();
        c.register();
        this.stats.incLearnedclauses();
        switch (c.size()) {
        case 2:
            this.stats.incLearnedbinaryclauses();
            break;
        case 3:
            this.stats.incLearnedternaryclauses();
            break;
        default:
            // do nothing
        }
    }

    public final int decisionLevel() {
        return this.trailLim.size();
    }

    public int newVar(int howmany) {
        if (this.declaredMaxVarId > 0 && howmany > this.declaredMaxVarId
                && this.voc.nVars() > this.declaredMaxVarId) {
            throw new IllegalStateException(
                    "Caution, you are making solver's internal var id public with uncontrolled consequences with features requiring internal/hidden variables.");
        }
        this.voc.ensurePool(howmany);
        this.declaredMaxVarId = howmany;
        return howmany;
    }

    public IConstr addClause(IVecInt literals) throws ContradictionException {
        IVecInt vlits = dimacs2internal(literals);
        IConstr constr = addConstr(this.dsfactory.createClause(vlits));
        slistener.addClause(literals);
        return constr;
    }

    public boolean removeConstr(IConstr co) {
        if (co == null) {
            throw new IllegalArgumentException(
                    "Reference to the constraint to remove needed!"); //$NON-NLS-1$
        }
        Constr c = (Constr) co;
        c.remove(this);
        this.constrs.removeFromLast(c);
        clearLearntClauses();
        String type = c.getClass().getName();
        this.constrTypes.get(type).dec();
        return true;
    }

    /**
     * @since 2.1
     */
    public boolean removeSubsumedConstr(IConstr co) {
        if (co == null) {
            throw new IllegalArgumentException(
                    "Reference to the constraint to remove needed!"); //$NON-NLS-1$
        }
        if (this.constrs.last() != co) {
            throw new IllegalArgumentException(
                    "Can only remove latest added constraint!!!"); //$NON-NLS-1$
        }
        Constr c = (Constr) co;
        c.remove(this);
        this.constrs.pop();
        String type = c.getClass().getName();
        this.constrTypes.get(type).dec();
        return true;
    }

    public void addAllClauses(IVec<IVecInt> clauses)
            throws ContradictionException {
        for (Iterator<IVecInt> iterator = clauses.iterator(); iterator
                .hasNext();) {
            addClause(iterator.next());
        }
    }

    public IConstr addAtMost(IVecInt literals, int degree)
            throws ContradictionException {
        int n = literals.size();
        IVecInt opliterals = new VecInt(n);
        for (IteratorInt iterator = literals.iterator(); iterator.hasNext();) {
            opliterals.push(-iterator.next());
        }
        return addAtLeast(opliterals, n - degree);
    }

    public IConstr addAtLeast(IVecInt literals, int degree)
            throws ContradictionException {
        IVecInt vlits = dimacs2internal(literals);
        return addConstr(
                this.dsfactory.createCardinalityConstraint(vlits, degree));
    }

    public IConstr addExactly(IVecInt literals, int n)
            throws ContradictionException {
        var group = new ConstrGroup(false);
        group.add(addAtLeast(literals, n));
        group.add(addAtMost(literals, n));
        return group;
    }

    public IConstr addParity(IVecInt literals, boolean even) {
        IVecInt vlits = dimacs2internal(literals);
        return addConstr(Xor.createParityConstraint(vlits, even, voc));
    }

    @SuppressWarnings("unchecked")
    public boolean simplifyDB() {
        // Simplifie la base de clauses apres la premiere propagation des
        // clauses unitaires
        IVec<Constr>[] cs = new IVec[] { this.constrs, this.learnts };
        for (var type = 0; type < 2; type++) {
            var j = 0;
            for (var i = 0; i < cs[type].size(); i++) {
                if (cs[type].get(i).simplify()) {
                    // enleve les contraintes satisfaites de la base
                    cs[type].get(i).remove(this);
                } else {
                    cs[type].moveTo(j++, i);
                }
            }
            cs[type].shrinkTo(j);
        }
        return true;
    }

    /**
     * Si un mod?le est trouv?, ce vecteur contient le mod?le.
     * 
     * @return un mod?le de la formule.
     */
    public int[] model() {
        if (this.model == null) {
            throw new UnsupportedOperationException(
                    CALL_THE_SOLVE_METHOD_FIRST);
        }
        var nmodel = new int[this.model.length];
        System.arraycopy(this.model, 0, nmodel, 0, this.model.length);
        return nmodel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#enqueue(int)
     */
    public boolean enqueue(int p) {
        return enqueue(p, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#enqueue(int,
     * org.sat4j.minisat.core.Constr)
     */
    public boolean enqueue(int p, Constr from) {
        assert p > 1;
        if (this.voc.isSatisfied(p)) {
            // literal is already satisfied. Skipping.
            return true;
        }
        if (this.voc.isFalsified(p)) {
            // conflicting enqueued assignment
            return false;
        }
        this.slistener.enqueueing(toDimacs(p), from);
        // new fact, store it
        this.voc.satisfies(p);
        this.voc.setLevel(p, decisionLevel());
        this.voc.setTrailPosition(p, this.trail.size());
        this.voc.setReason(p, from);
        this.trail.push(p);
        if (from != null && from.learnt()) {
            this.learnedConstraintsDeletionStrategy.onPropagation(from, p);
        }
        return true;
    }

    private boolean[] mseen = new boolean[0];

    private final IVecInt mpreason = new VecInt();

    private final IVecInt moutLearnt = new VecInt();

    /**
     * @throws TimeoutException
     *             if the timeout is reached during conflict analysis.
     */
    public void analyze(Constr confl, Pair results) throws TimeoutException {
        assert confl != null;

        final boolean[] seen = this.mseen;
        final IVecInt outLearnt = this.moutLearnt;
        final IVecInt preason = this.mpreason;

        outLearnt.clear();
        assert outLearnt.size() == 0;
        for (var i = 0; i < seen.length; i++) {
            seen[i] = false;
        }

        var counter = 0;
        int p = ILits.UNDEFINED;
        // placeholder for the asserting literal
        outLearnt.push(ILits.UNDEFINED);
        var outBtlevel = 0;
        IConstr prevConfl = null;

        do {
            preason.clear();
            assert confl != null;
            if (prevConfl != confl || confl.canBePropagatedMultipleTimes()) {
                confl.calcReason(p, preason);
                this.learnedConstraintsDeletionStrategy
                        .onConflictAnalysis(confl);
                // Trace reason for p
                for (var j = 0; j < preason.size(); j++) {
                    int q = preason.get(j);
                    this.order.updateVar(q);
                    if (!seen[q >> 1]) {
                        seen[q >> 1] = true;
                        if (this.voc.getLevel(q) == decisionLevel()) {
                            counter++;
                            this.order.updateVarAtDecisionLevel(q);
                        } else if (this.voc.getLevel(q) > 0) {
                            // only literals assigned after decision level 0
                            // part of
                            // the explanation
                            outLearnt.push(q ^ 1);
                            outBtlevel = Math.max(outBtlevel,
                                    this.voc.getLevel(q));
                        }
                    }
                }
            }
            prevConfl = confl;
            // select next reason to look at
            do {
                p = this.trail.last();
                confl = this.voc.getReason(p);
                undoOne();
            } while (!seen[p >> 1]);
            // seen[p.var] indique que p se trouve dans outLearnt ou dans
            // le dernier niveau de d?cision
        } while (--counter > 0);

        outLearnt.set(0, p ^ 1);
        this.simplifier.simplify(outLearnt);

        Constr c = this.dsfactory.createUnregisteredClause(outLearnt);
        this.learnedConstraintsDeletionStrategy.onClauseLearning(c);
        results.setReason(c);

        assert outBtlevel > -1;
        results.setBacktrackLevel(outBtlevel);
    }

    /**
     * Derive a subset of the assumptions causing the inconsistency.
     * 
     * @param confl
     *            the last conflict of the search, occuring at root level.
     * @param assumps
     *            the set of assumption literals
     * @param conflictingLiteral
     *            the literal detected conflicting while propagating
     *            assumptions.
     * @return a subset of assumps causing the inconsistency.
     * @since 2.2
     */
    public IVecInt analyzeFinalConflictInTermsOfAssumptions(Constr confl,
            IVecInt assumps, int conflictingLiteral) {
        if (assumps.size() == 0) {
            return null;
        }
        while (!this.trailLim.isEmpty()
                && this.trailLim.last() == this.trail.size()) {
            // conflict detected when assuming a value
            this.trailLim.pop();
        }
        final boolean[] seen = this.mseen;
        final IVecInt outLearnt = this.moutLearnt;
        final IVecInt preason = this.mpreason;

        outLearnt.clear();
        if (this.trailLim.size() == 0) {
            // conflict detected on unit clauses
            return outLearnt;
        }

        assert outLearnt.size() == 0;
        for (var i = 0; i < seen.length; i++) {
            seen[i] = false;
        }

        if (confl == null) {
            seen[conflictingLiteral >> 1] = true;
        }

        // if confl == null and conflictingLiteral exists, a propagation or
        // previous
        // propagation assigned the literal in the opposite direction
        // trying to find a propagation
        int p = ILits.UNDEFINED;
        if (conflictingLiteral != ILits.UNDEFINED) {
            confl = this.voc.getReason(conflictingLiteral ^ 1);
            if (confl == null) {
                if (assumps.contains(toDimacs(conflictingLiteral ^ 1))) {
                    outLearnt.push(toDimacs(conflictingLiteral ^ 1));
                } // else the literal is resulting from propagation at decision
                  // level 0, so is not part of the explanation
                return outLearnt;
            }
            p = conflictingLiteral ^ 1;
        }
        while (confl == null && this.trail.size() > 0
                && this.trailLim.size() > 0) {
            p = this.trail.last();
            confl = this.voc.getReason(p);
            undoOne();
            if (confl == null && p == (conflictingLiteral ^ 1)) {
                outLearnt.push(toDimacs(p));
            }
            if (this.trail.size() <= this.trailLim.last()) {
                
            }
        }
        if (confl == null) {
            return outLearnt;
        }
        do {
            preason.clear();
            confl.calcReason(p, preason);
            // Trace reason for p
            for (var j = 0; j < preason.size(); j++) {
                int q = preason.get(j);
                if (!seen[q >> 1]) {
                    seen[q >> 1] = true;
                    if (this.voc.getReason(q) == null
                            && this.voc.getLevel(q) > 0) {
                        assert assumps.contains(toDimacs(q));
                        outLearnt.push(toDimacs(q));
                    }
                }
            }

            // select next reason to look at
            do {
                p = this.trail.last();
                confl = this.voc.getReason(p);
                undoOne();
                if (decisionLevel() > 0
                        && this.trail.size() <= this.trailLim.last()) {
                    this.trailLim.pop();
                }
            } while (this.trail.size() > 0 && decisionLevel() > 0
                    && (!seen[p >> 1] || confl == null));
        } while (decisionLevel() > 0);
        return outLearnt;
    }

    @Feature(value = "simplifications", parent = "expert")
    public static final ISimplifier NO_SIMPLIFICATION = new ISimplifier() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void simplify(IVecInt outLearnt) {
        }

        @Override
        public String toString() {
            return "No reason simplification"; //$NON-NLS-1$
        }
    };

    @Feature(value = "simplifications", parent = "expert")
    public final ISimplifier simpleSimplification = new ISimplifier() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void simplify(IVecInt conflictToReduce) {
            // MiniSat -- Copyright (c) 2003-2005, Niklas Een, Niklas Sorensson
            //
            // Permission is hereby granted, free of charge, to any person
            // obtaining a
            // copy of this software and associated documentation files (the
            // "Software"), to deal in the Software without restriction,
            // including
            // without limitation the rights to use, copy, modify, merge,
            // publish,
            // distribute, sublicense, and/or sell copies of the Software, and
            // to
            // permit persons to whom the Software is furnished to do so,
            // subject to
            // the following conditions:
            //
            // The above copyright notice and this permission notice shall be
            // included
            // in all copies or substantial portions of the Software.
            //
            // THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
            // EXPRESS
            // OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
            // MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
            // NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
            // HOLDERS BE
            // LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
            // ACTION
            // OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
            // CONNECTION
            // WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

            // Taken from MiniSAT 1.14: Simplify conflict clause (a little):

            int i, j, p;
            final boolean[] seen = mseen;
            IConstr r;
            for (i = j = 1; i < conflictToReduce.size(); i++) {
                r = voc.getReason(conflictToReduce.get(i));
                if (r == null || r.canBePropagatedMultipleTimes()) {
                    conflictToReduce.moveTo(j++, i);
                } else {
                    for (var k = 0; k < r.size(); k++) {
                        p = r.get(k);
                        if (!seen[p >> 1] && voc.isFalsified(p)
                                && voc.getLevel(p) != 0) {
                            conflictToReduce.moveTo(j++, i);
                            break;
                        }
                    }
                }
            }
            conflictToReduce.shrink(i - j);
            stats.incReducedliterals(i - j);
        }

        @Override
        public String toString() {
            return "Simple reason simplification"; //$NON-NLS-1$
        }
    };

    @Feature(value = "simplifications", parent = "expert")
    public final ISimplifier expensiveSimplification = new ISimplifier() {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void simplify(IVecInt conflictToReduce) {
            // Taken from MiniSAT 1.14

            // Simplify conflict clause (a lot):
            //
            int i, j;
            // (maintain an abstraction of levels involved in conflict)
            analyzetoclear.clear();
            conflictToReduce.copyTo(analyzetoclear);
            for (i = 1, j = 1; i < conflictToReduce.size(); i++) {
                if (voc.getReason(conflictToReduce.get(i)) == null
                        || !analyzeRemovable(conflictToReduce.get(i))) {
                    conflictToReduce.moveTo(j++, i);
                }
            }
            conflictToReduce.shrink(i - j);
            stats.incReducedliterals(i - j);
        }

        // Check if 'p' can be removed.' min_level' is used to abort early if
        // visiting literals at a level that cannot be removed.
        //
        private boolean analyzeRemovable(int p) {
            assert voc.getReason(p) != null;
            ILits lvoc = voc;
            IVecInt lanalyzestack = analyzestack;
            IVecInt lanalyzetoclear = analyzetoclear;
            lanalyzestack.clear();
            lanalyzestack.push(p);
            final boolean[] seen = mseen;
            int top = lanalyzetoclear.size();
            while (lanalyzestack.size() > 0) {
                int q = lanalyzestack.last();
                assert lvoc.getReason(q) != null;
                Constr c = lvoc.getReason(q);
                lanalyzestack.pop();
                if (c.canBePropagatedMultipleTimes()) {
                    for (int j = top; j < lanalyzetoclear.size(); j++) {
                        seen[lanalyzetoclear.get(j) >> 1] = false;
                    }
                    lanalyzetoclear.shrink(lanalyzetoclear.size() - top);
                    return false;
                }
                for (var i = 0; i < c.size(); i++) {
                    int l = c.get(i);
                    if (!seen[var(l)] && lvoc.isFalsified(l)
                            && lvoc.getLevel(l) != 0) {
                        if (lvoc.getReason(l) == null) {
                            for (int j = top; j < lanalyzetoclear.size(); j++) {
                                seen[lanalyzetoclear.get(j) >> 1] = false;
                            }
                            lanalyzetoclear
                                    .shrink(lanalyzetoclear.size() - top);
                            return false;
                        }
                        seen[l >> 1] = true;
                        lanalyzestack.push(l);
                        lanalyzetoclear.push(l);
                    }
                }

            }

            return true;
        }

        @Override
        public String toString() {
            return "Expensive reason simplification"; //$NON-NLS-1$
        }
    };

    @Feature(value = "simplifications", parent = "expert")
    public final ISimplifier expensiveSimplificationWLOnly = new ISimplifier() {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void simplify(IVecInt conflictToReduce) {
            // Taken from MiniSAT 1.14

            // Simplify conflict clause (a lot):
            //
            int i, j;
            // (maintain an abstraction of levels involved in conflict)
            analyzetoclear.clear();
            conflictToReduce.copyTo(analyzetoclear);
            for (i = 1, j = 1; i < conflictToReduce.size(); i++) {
                if (voc.getReason(conflictToReduce.get(i)) == null
                        || !analyzeRemovableWLOnly(conflictToReduce.get(i))) {
                    conflictToReduce.moveTo(j++, i);
                }
            }
            conflictToReduce.shrink(i - j);
            stats.incReducedliterals(i - j);
        }

        // Check if 'p' can be removed.' min_level' is used to abort early if
        // visiting literals at a level that cannot be removed.
        //
        private boolean analyzeRemovableWLOnly(int p) {
            assert voc.getReason(p) != null;
            analyzestack.clear();
            analyzestack.push(p);
            final boolean[] seen = mseen;
            int top = analyzetoclear.size();
            while (analyzestack.size() > 0) {
                int q = analyzestack.last();
                assert voc.getReason(q) != null;
                Constr c = voc.getReason(q);
                analyzestack.pop();
                for (var i = 1; i < c.size(); i++) {
                    int l = c.get(i);
                    if (!seen[var(l)] && voc.getLevel(l) != 0) {
                        if (voc.getReason(l) == null) {
                            for (int j = top; j < analyzetoclear.size(); j++) {
                                seen[analyzetoclear.get(j) >> 1] = false;
                            }
                            analyzetoclear.shrink(analyzetoclear.size() - top);
                            return false;
                        }
                        seen[l >> 1] = true;
                        analyzestack.push(l);
                        analyzetoclear.push(l);
                    }
                }
            }

            return true;
        }

        // END Minisat 1.14 cut and paste

        @Override
        public String toString() {
            return "Expensive reason simplification specific for WL data structure"; //$NON-NLS-1$
        }
    };

    private ISimplifier simplifier = NO_SIMPLIFICATION;

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#setSimplifier(java.lang.String)
     */
    public void setSimplifier(SimplificationType simp) {
        Field f;
        try {
            f = Solver.class.getDeclaredField(simp.toString());
            this.simplifier = (ISimplifier) f.get(this);
        } catch (Exception e) {
            Logger.getLogger("org.sat4j.core").log(Level.INFO,
                    "Issue when assigning simplifier: disabling simplification",
                    e);
            this.simplifier = NO_SIMPLIFICATION;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.core.ICDCL#setSimplifier(org.sat4j.minisat.core.Solver
     * .ISimplifier)
     */
    public void setSimplifier(ISimplifier simp) {
        this.simplifier = simp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#getSimplifier()
     */
    public ISimplifier getSimplifier() {
        return this.simplifier;
    }

    private final IVecInt analyzetoclear = new VecInt();

    private final IVecInt analyzestack = new VecInt();

    /**
     * 
     */
    protected void undoOne() {
        // gather last assigned literal
        int p = this.trail.last();
        assert p > 1;
        assert this.voc.getLevel(p) >= 0;
        int x = p >> 1;
        // unassign variable
        this.voc.unassign(p);
        this.voc.setReason(p, null);
        this.voc.setLevel(p, -1);
        this.voc.setTrailPosition(p, -1);
        // update heuristics value
        this.order.undo(x);
        // remove literal from the trail
        this.trail.pop();
        // update constraints on backtrack.
        // not used if the solver uses watched literals.
        IVec<Undoable> undos = this.voc.undos(p);
        assert undos != null;
        for (int size = undos.size(); size > 0; size--) {
            undos.last().undo(p);
            undos.pop();
        }
    }

    /**
     * Propagate activity to a constraint
     * 
     * @param confl
     *            a constraint
     */
    public void claBumpActivity(Constr confl) {
        confl.incActivity(this.claInc);
        if (confl.getActivity() > CLAUSE_RESCALE_BOUND) {
            claRescalActivity();
        }
    }

    public void varBumpActivity(int p) {
        this.order.updateVar(p);
    }

    public void varBumpActivity(Constr constr, int i, int p,
            boolean conflicting) {
        this.order.updateVar(constr.get(i));
    }

    private void claRescalActivity() {
        for (var i = 0; i < this.learnts.size(); i++) {
            this.learnts.get(i).rescaleBy(CLAUSE_RESCALE_FACTOR);
        }
        this.claInc *= CLAUSE_RESCALE_FACTOR;
    }

    final IVec<Propagatable> watched = new Vec<>();

    /**
     * @return null if not conflict is found, else a conflicting constraint.
     */
    public final Constr propagate() {
        IVecInt ltrail = this.trail;
        SolverStats lstats = this.stats;
        IOrder lorder = this.order;
        SearchListener lslistener = this.slistener;
        // ltrail.size() changes due to propagation
        // cannot cache that value.
        while (this.qhead < ltrail.size()) {
            lstats.incPropagations();
            int p = ltrail.get(this.qhead++);
            lslistener.propagating(toDimacs(p));
            lorder.assignLiteral(p);
            Constr confl = reduceClausesContainingTheNegationOf(p);
            if (confl != null) {
                return confl;
            }
        }
        return null;
    }

    private Constr reduceClausesContainingTheNegationOf(int p) {
        // p is the literal to propagate
        // Moved original MiniSAT code to dsfactory to avoid
        // watches manipulation in counter Based clauses for instance.
        assert p > 1;
        IVec<Propagatable> lwatched = this.watched;
        lwatched.clear();
        this.voc.watches(p).moveTo(lwatched);
        final int size = lwatched.size();
        for (var i = 0; i < size; i++) {
            this.stats.incInspects();
            if (!lwatched.get(i).propagate(this, p)) {
                // Constraint is conflicting: copy remaining watches to
                // watches[p]
                // and return constraint
                final int sizew = lwatched.size();
                for (var j = i + 1; j < sizew; j++) {
                    this.voc.watch(p, lwatched.get(j));
                }
                this.qhead = this.trail.size();
                return lwatched.get(i).toConstraint();
            }
        }
        return null;
    }

    void recordConstraint(Constr constr) {
        constr.assertConstraint(this);
        int p = toDimacs(constr.get(0));
        this.slistener.adding(p);
        if (constr.size() == 1) {
            this.stats.incLearnedliterals();
            this.slistener.learnUnit(p);
            this.unitClauseConsumer.learnUnit(p);
        } else {
            this.learner.learns(constr);
        }
    }

    /**
     * @return false ssi conflit imm?diat.
     */
    public boolean assume(int p) {
        // Precondition: assume propagation queue is empty
        // assert this.trail.size() == this.qhead; no longer true with computing
        // PI
        assert !this.trailLim.contains(this.trail.size());
        this.trailLim.push(this.trail.size());
        return enqueue(p);
    }

    /**
     * Revert to the state before the last assume()
     */
    void cancel() {
        int decisionvar = this.trail.unsafeGet(this.trailLim.last());
        this.slistener.backtracking(toDimacs(decisionvar));
        for (int c = this.trail.size() - this.trailLim.last(); c > 0; c--) {
            undoOne();
        }
        this.trailLim.pop();
        this.qhead = this.trail.size();
    }

    /**
     * Restore literals
     */
    private void cancelLearntLiterals(int learnedLiteralsLimit) {
        this.learnedLiterals.clear();
        while (this.trail.size() > learnedLiteralsLimit) {
            this.learnedLiterals.push(this.trail.last());
            undoOne();
        }
    }

    /**
     * Cancel several levels of assumptions
     * 
     * @param level
     */
    protected void cancelUntil(int level) {
        while (decisionLevel() > level) {
            cancel();
        }
    }

    protected void cancelUntilTrailLevel(int level) {
        while (!trail.isEmpty() && trail.size() > level) {
            undoOne();
            if (!trailLim.isEmpty() && trailLim.last() == trail.size()) {
                trailLim.pop();
                decisions.pop();
            }
        }
    }

    private final Pair analysisResult = new Pair();

    private boolean[] userbooleanmodel;

    private IVecInt unsatExplanationInTermsOfAssumptions;

    private Lbool search(IVecInt assumps) {
        assert this.rootLevel == decisionLevel();
        this.stats.incStarts();
        int backjumpLevel;

        this.order.setVarDecay(1 / this.params.getVarDecay());
        this.claDecay = 1 / this.params.getClaDecay();

        do {
            this.slistener.beginLoop();
            // propagate unit clauses and other constraints
            Constr confl = propagate();
            assert this.trail.size() == this.qhead;

            if (confl == null) {
                // No conflict found
                if (decisionLevel() == 0 && this.isDBSimplificationAllowed) {
                    this.stats.incRootSimplifications();
                    boolean ret = simplifyDB();
                    assert ret;
                }
                assert nAssigns() <= this.voc.realnVars();
                if (nAssigns() == this.voc.realnVars()) {
                    modelFound();
                    this.slistener.solutionFound(
                            (this.fullmodel != null) ? this.fullmodel
                                    : this.model,
                            this);
                    if (this.sharedConflict == null) {
                        cancelUntil(this.rootLevel);
                        return Lbool.TRUE;
                    } else {
                        if (decisionLevel() == rootLevel) {
                            confl = this.sharedConflict;
                            this.sharedConflict = null;
                        } else {
                            int level = this.sharedConflict.getAssertionLevel(
                                    trail, trailLim, decisionLevel(), this.voc);
                            cancelUntilTrailLevel(level);
                            this.qhead = this.trail.size();
                            this.sharedConflict.assertConstraint(this);
                            this.sharedConflict = null;

                            continue;
                        }
                    }
                } else {
                    if (this.restarter.shouldRestart()) {
                        cancelUntil(this.rootLevel);
                        return Lbool.UNDEFINED;
                    }
                    if (this.needToReduceDB) {
                        reduceDB();
                        this.needToReduceDB = false;
                    }
                    if (this.sharedConflict == null) {
                        // New variable decision
                        this.stats.incDecisions();
                        if (this.stats.getConflicts() > 0L) {
                            this.stats.setNoDecisionAfterFirstConflict(false);
                        }
                        int p = this.order.select();
                        if (p == ILits.UNDEFINED) {
                            // check (expensive) if all the constraints are not
                            // satisfied
                            var allsat = true;
                            for (var i = 0; i < this.constrs.size(); i++) {
                                if (!this.constrs.get(i).isSatisfied()) {
                                    allsat = false;
                                    break;
                                }
                            }
                            if (allsat) {
                                modelFound();
                                this.slistener
                                        .solutionFound((this.fullmodel != null)
                                                ? this.fullmodel
                                                : this.model, this);
                                return Lbool.TRUE;
                            } else {
                                confl = preventTheSameDecisionsToBeMade();
                                this.lastConflictMeansUnsat = false;
                            }
                        } else {
                            assert p > 1;
                            this.slistener.assuming(toDimacs(p));
                            boolean ret = assume(p);
                            assert ret;
                        }
                    } else {
                        confl = this.sharedConflict;
                        this.sharedConflict = null;
                    }
                }
            }
            if (confl != null) {
                // conflict found
                this.stats.incConflicts();
                this.slistener.conflictFound(confl, decisionLevel(),
                        this.trail.size());
                this.conflictCount.newConflict();

                if (decisionLevel() == this.rootLevel) {
                    if (this.lastConflictMeansUnsat) {
                        // conflict at root level, the formula is inconsistent
                        this.unsatExplanationInTermsOfAssumptions = analyzeFinalConflictInTermsOfAssumptions(
                                confl, assumps, ILits.UNDEFINED);
                        return Lbool.FALSE;
                    }
                    return Lbool.UNDEFINED;
                }
                int conflictTrailLevel = this.trail.size();
                // analyze conflict
                try {
                    analyze(confl, this.analysisResult);
                } catch (TimeoutException e) {
                    return Lbool.UNDEFINED;
                }
                assert this.analysisResult
                        .getBacktrackLevel() < decisionLevel();
                backjumpLevel = Math.max(
                        this.analysisResult.getBacktrackLevel(),
                        this.rootLevel);
                this.slistener.backjump(backjumpLevel);
                cancelUntil(backjumpLevel);
                if (backjumpLevel == this.rootLevel) {
                    this.restarter.onBackjumpToRootLevel();
                }
                assert decisionLevel() >= this.rootLevel
                        && decisionLevel() >= this.analysisResult
                                .getBacktrackLevel();
                if (this.analysisResult.getReason() == null) {
                    return Lbool.FALSE;
                }
                recordConstraint(this.analysisResult.getReason());
                this.restarter.newLearnedClause(this.analysisResult.getReason(),
                        conflictTrailLevel);
                this.analysisResult.setReason(null);
                decayActivities();
            }
        } while (this.undertimeout);
        return Lbool.UNDEFINED; // timeout occured
    }

    private Constr preventTheSameDecisionsToBeMade() {
        IVecInt clause = new VecInt(nVars());
        int p;
        for (int i = this.trail.size() - 1; i >= 0; i--) {
            p = this.trail.get(i);
            if (this.voc.getReason(p) == null) {
                clause.push(p ^ 1);
            }
        }
        return this.dsfactory.createUnregisteredClause(clause);
    }

    protected void analyzeAtRootLevel(Constr conflict) {
    }

    protected final IVecInt implied = new VecInt();
    protected final IVecInt decisions = new VecInt();

    private AssignmentOrigin[] assignmentOrigins;
    int[] fullmodel;

    @Override
    public AssignmentOrigin getOriginInModel(int p) {
        return assignmentOrigins[Math.abs(p) - 1];
    }

    /**
     * 
     */
    void modelFound() {
        decisions.clear();
        IVecInt tempmodel = new VecInt(nVars());
        assignmentOrigins = new AssignmentOrigin[realNumberOfVariables()];
        this.userbooleanmodel = new boolean[realNumberOfVariables()];
        this.fullmodel = null;
        AssignmentOrigin origin;

        Constr reason;
        if (classifyLiterals) {
            var stb = new StringBuilder(getLogPrefix());
            int q;
            String str;
            for (var i = 0; i < trailLim.size(); i++) {
                q = trail.get(trailLim.get(i));
                stb.append(LiteralsUtils.toDimacs(q));
                this.voc.unassign(q);
                this.voc.satisfies(q ^ 1);
                // can change invariants in constraints data
                // structures
                // should only be used with care
                if ((reason = reduceClausesContainingTheNegationOf(
                        q ^ 1)) != null) {
                    if (reason.learnt()) {
                        origin = AssignmentOrigin.DECIDED_PROPAGATED_LEARNED;
                    } else {
                        origin = AssignmentOrigin.DECIDED_PROPAGATED;
                    }
                    int r;
                    TreeSet<Integer> levels = new TreeSet<>();
                    for (var j = 0; j < reason.size(); j++) {
                        r = reason.get(j);
                        if (r != q) {
                            levels.add(this.voc.getLevel(r));
                        }
                    }

                    stb.append(":");
                    str = levels.toString().replace(" ", "");
                    stb.append(str.substring(1, str.length() - 1));
                    if (voc.getLevel(q) == levels.last()) {
                        origin = AssignmentOrigin.DECIDED_CYCLE;
                    }
                } else {
                    origin = AssignmentOrigin.DECIDED;
                }

                this.voc.unassign(q);
                this.voc.satisfies(q);
                stb.append(" ");
                this.assignmentOrigins[(q >> 1) - 1] = origin;
            }
            System.out.println(stb);
        }
        for (var i = 1; i <= nVars(); i++) {
            if (this.voc.belongsToPool(i)) {
                int p = this.voc.getFromPool(i);
                if (!this.voc.isUnassigned(p)) {
                    tempmodel.push(this.voc.isSatisfied(p) ? i : -i);
                    this.userbooleanmodel[i - 1] = this.voc.isSatisfied(p);
                    reason = this.voc.getReason(p);
                    if (reason == null && this.voc.getLevel(p) > 0
                            // we consider literals propagated by learned
                            // clauses
                            // as decisions to allow blocking models by
                            // decisions.
                            || reason != null && reason.learnt()) {
                        this.decisions.push(tempmodel.last());
                        if (reason != null) {
                            this.assignmentOrigins[i
                                    - 1] = AssignmentOrigin.PROPAGATED_LEARNED;
                        }
                    } else {
                        this.implied.push(tempmodel.last());
                        this.assignmentOrigins[i
                                - 1] = AssignmentOrigin.PROPAGATED_ORIGINAL;
                    }
                }
            }
        }
        this.model = new int[tempmodel.size()];
        tempmodel.copyTo(this.model);
        if (realNumberOfVariables() > nVars()) {
            for (int i = nVars() + 1; i <= realNumberOfVariables(); i++) {
                if (this.voc.belongsToPool(i)) {
                    int p = this.voc.getFromPool(i);
                    if (!this.voc.isUnassigned(p)) {
                        tempmodel.push(this.voc.isSatisfied(p) ? i : -i);
                        this.userbooleanmodel[i - 1] = this.voc.isSatisfied(p);
                        if (this.voc.getReason(p) == null) {
                            this.decisions.push(tempmodel.last());
                        } else {
                            this.implied.push(tempmodel.last());
                            if (this.voc.getReason(p).learnt()) {
                                this.assignmentOrigins[i
                                        - 1] = AssignmentOrigin.PROPAGATED_LEARNED;
                            } else {
                                this.assignmentOrigins[i
                                        - 1] = AssignmentOrigin.PROPAGATED_ORIGINAL;
                            }
                        }
                    }
                } else {
                    this.assignmentOrigins[i - 1] = AssignmentOrigin.UNASSIGNED;
                }
            }
            this.fullmodel = new int[tempmodel.size()];
            tempmodel.moveTo(this.fullmodel);
        } else {
            this.fullmodel = this.model;
        }
    }

    /**
     * Forget a variable in the formula by falsifying both its positive and
     * negative literals.
     * 
     * @param variable
     *            a variable
     * @return a conflicting constraint resulting from the removal of those
     *         literals.
     */
    Constr forget(int variable) {
        boolean satisfied = this.voc.isSatisfied(toInternal(variable));
        this.voc.forgets(variable);
        Constr confl;
        if (satisfied) {
            confl = reduceClausesContainingTheNegationOf(
                    LiteralsUtils.toInternal(-variable));
        } else {
            confl = reduceClausesContainingTheNegationOf(
                    LiteralsUtils.toInternal(variable));
        }
        return confl;
    }

    protected int[] prime;

    public int[] primeImplicant() {
        String primeApproach = System.getProperty("prime");
        PrimeImplicantStrategy strategy;
        if ("OLD".equals(primeApproach)) {
            strategy = new QuadraticPrimeImplicantStrategy();
        } else if ("ALGO2".equals(primeApproach)) {
            strategy = new CounterBasedPrimeImplicantStrategy();
        } else {
            strategy = new WatcherBasedPrimeImplicantStrategy();
        }
        int[] implicant = strategy.compute(this);
        this.prime = strategy.getPrimeImplicantAsArrayWithHoles();
        return implicant;
    }

    public boolean primeImplicant(int p) {
        if (p == 0 || Math.abs(p) > realNumberOfVariables()) {
            throw new IllegalArgumentException(
                    "Use a valid Dimacs var id as argument!"); //$NON-NLS-1$
        }
        if (this.prime == null) {
            throw new UnsupportedOperationException(
                    "Call the primeImplicant method first!!!"); //$NON-NLS-1$
        }
        return this.prime[Math.abs(p)] == p;
    }

    public boolean model(int variable) {
        if (variable <= 0 || variable > realNumberOfVariables()) {
            throw new IllegalArgumentException(
                    "Use a valid Dimacs var id as argument!"); //$NON-NLS-1$
        }
        if (this.userbooleanmodel == null) {
            throw new UnsupportedOperationException(
                    CALL_THE_SOLVE_METHOD_FIRST);
        }
        return this.userbooleanmodel[variable - 1];
    }

    public void clearLearntClauses() {
        for (Iterator<Constr> iterator = this.learnts.iterator(); iterator
                .hasNext();) {
            iterator.next().remove(this);
        }
        this.learnts.clear();
        this.learnedLiterals.clear();
    }

    protected final void reduceDB() {
        this.stats.incReduceddb();
        this.slistener.cleaning();
        this.learnedConstraintsDeletionStrategy.reduce(this.learnts);
    }

    protected ActivityComparator getActivityComparator() {
        return this.comparator;
    }

    /**
     * 
     */
    protected void decayActivities() {
        this.order.varDecayActivity();
        claDecayActivity();
    }

    /**
     * 
     */
    private void claDecayActivity() {
        this.claInc *= this.claDecay;
    }

    /**
     * @return true iff the set of constraints is satisfiable, else false.
     */
    public boolean isSatisfiable() throws TimeoutException {
        return isSatisfiable(VecInt.EMPTY);
    }

    /**
     * @return true iff the set of constraints is satisfiable, else false.
     */
    public boolean isSatisfiable(boolean global) throws TimeoutException {
        return isSatisfiable(VecInt.EMPTY, global);
    }

    private double timebegin = 0;

    private boolean needToReduceDB;

    private ConflictTimerContainer conflictCount;

    private transient Timer timer;

    public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
        return isSatisfiable(assumps, false);
    }

    @Feature(value = "deletion", parent = "expert")
    public final LearnedConstraintsDeletionStrategy fixedSize(
            final int maxsize) {
        return new LearnedConstraintsDeletionStrategy() {

            private static final long serialVersionUID = 1L;
            private final ConflictTimer aTimer = new ConflictTimerAdapter(
                    Solver.this, maxsize) {

                private static final long serialVersionUID = 1L;

                @Override
                public void run() {
                    getSolver().setNeedToReduceDB(true);
                }
            };

            public void reduce(IVec<Constr> learnedConstrs) {
                int i, j, k;
                for (i = j = k = 0; i < Solver.this.learnts.size()
                        && Solver.this.learnts.size() - k > maxsize; i++) {
                    Constr c = Solver.this.learnts.get(i);
                    if (c.locked() || c.size() == 2) {
                        Solver.this.learnts.set(j++,
                                Solver.this.learnts.get(i));
                    } else {
                        c.remove(Solver.this);
                        k++;
                    }
                }
                for (; i < Solver.this.learnts.size(); i++) {
                    Solver.this.learnts.set(j++, Solver.this.learnts.get(i));
                }
                if (Solver.this.verbose) {
                    Solver.this.out.log(getLogPrefix() + "cleaning " //$NON-NLS-1$
                            + (Solver.this.learnts.size() - j)
                            + " clauses out of " + Solver.this.learnts.size()); //$NON-NLS-1$
                }
                Solver.this.learnts.shrinkTo(j);
            }

            @Override
            public String toString() {
                return "Fixed size (" + maxsize
                        + ") learned constraints deletion strategy";
            }

            public ConflictTimer getTimer() {
                return this.aTimer;
            }
        };
    }

    private final ConflictTimer memoryTimer = new MemoryBasedConflictTimer(this,
            500);

    /**
     * @since 2.1
     */
    public final LearnedConstraintsDeletionStrategy activityBasedLowMemory = new ActivityLCDS(
            this, this.memoryTimer);

    private final ConflictTimer glucoseTimer = new GlucoseConflictTimer(this,
            1000);

    /**
     * @since 2.1
     */
    public final LearnedConstraintsDeletionStrategy lbdBased = new Glucose2LCDS<D>(
            this, this.glucoseTimer);

    /**
     * @since 2.3.6
     */
    public final LearnedConstraintsDeletionStrategy ageBased = new AgeLCDS(this,
            this.glucoseTimer);

    /**
     * @since 2.3.6
     */
    public final LearnedConstraintsDeletionStrategy activityBased = new ActivityLCDS(
            this, this.glucoseTimer);

    /**
     * @since 2.3.6
     */
    public final LearnedConstraintsDeletionStrategy sizeBased = new SizeLCDS(
            this, this.glucoseTimer);

    private LearnedConstraintsDeletionStrategy learnedConstraintsDeletionStrategy = this.lbdBased;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.core.ICDCL#setLearnedConstraintsDeletionStrategy(org
     * .sat4j.minisat.core.Solver.LearnedConstraintsDeletionStrategy)
     */
    public void setLearnedConstraintsDeletionStrategy(
            LearnedConstraintsDeletionStrategy lcds) {
        if (this.conflictCount != null) {
            this.conflictCount.add(lcds.getTimer());
            assert this.learnedConstraintsDeletionStrategy != null;
            this.conflictCount
                    .remove(this.learnedConstraintsDeletionStrategy.getTimer());
        }
        this.learnedConstraintsDeletionStrategy = lcds;
    }

    private boolean lastConflictMeansUnsat;

    public boolean isSatisfiable(IVecInt assumps, boolean global)
            throws TimeoutException {
        this.slistener.checkSatisfiability(assumps, global);
        Lbool status = Lbool.UNDEFINED;
        boolean alreadylaunched = this.conflictCount != null;
        final int howmany = this.voc.nVars();
        if (this.mseen.length <= howmany) {
            this.mseen = new boolean[howmany + 1];
        }
        this.trail.ensure(howmany);
        this.trailLim.ensure(howmany);
        this.learnedLiterals.ensure(howmany);
        
        this.implied.clear();
        this.slistener.start();
        this.model = null; // forget about previous model
        this.fullmodel = null;
        this.userbooleanmodel = null;
        this.prime = null;
        this.unsatExplanationInTermsOfAssumptions = null;
        // To make sure that new literals in the assumptions appear in the
        // solver data structures
        IVecInt localAssumps = new VecInt(assumps.size());
        for (IteratorInt iterator = assumps.iterator(); iterator.hasNext();) {
            int assump = iterator.next();
            localAssumps.push(this.voc.getFromPool(assump));
        }
        if (!alreadylaunched || !this.keepHot) {
            this.order.init();
        }
        this.learnedConstraintsDeletionStrategy.init();
        int learnedLiteralsLimit = this.trail.size();

        // Fix for Bug SAT37
        this.qhead = 0;
        // Apply undos on unit literals because they are getting propagated
        // again now that qhead is 0.
        for (int i = learnedLiteralsLimit - 1; i >= 0; i--) {
            int p = this.trail.get(i);
            IVec<Undoable> undos = this.voc.undos(p);
            assert undos != null;
            for (int size = undos.size(); size > 0; size--) {
                undos.last().undo(p);
                undos.pop();
            }
        }
        // push previously learned literals
        for (IteratorInt iterator = this.learnedLiterals.iterator(); iterator
                .hasNext();) {
            enqueue(iterator.next());
        }

        // propagate constraints
        Constr confl = propagate();
        if (confl != null) {
            analyzeAtRootLevel(confl);
            this.slistener.conflictFound(confl, 0, 0);
            this.slistener.end(Lbool.FALSE);
            cancelUntil(0);
            cancelLearntLiterals(learnedLiteralsLimit);
            return false;
        }

        // push incremental assumptions
        for (IteratorInt iterator = localAssumps.iterator(); iterator
                .hasNext();) {
            int p = iterator.next();
            if (!this.voc.isSatisfied(p) && !assume(p)
                    || (confl = propagate()) != null) {
                if (confl == null) {
                    this.slistener.conflictFound(p);
                    this.unsatExplanationInTermsOfAssumptions = analyzeFinalConflictInTermsOfAssumptions(
                            null, assumps, p);
                    this.unsatExplanationInTermsOfAssumptions.push(toDimacs(p));
                } else {
                    this.slistener.conflictFound(confl, decisionLevel(),
                            this.trail.size());
                    this.unsatExplanationInTermsOfAssumptions = analyzeFinalConflictInTermsOfAssumptions(
                            confl, assumps, ILits.UNDEFINED);
                }

                this.slistener.end(Lbool.FALSE);
                cancelUntil(0);
                cancelLearntLiterals(learnedLiteralsLimit);
                return false;
            }
        }
        this.rootLevel = decisionLevel();
        // moved initialization here if new literals are added in the
        // assumptions.
        this.learner.init();

        if (!alreadylaunched) {
            this.conflictCount = new ConflictTimerContainer();
            this.conflictCount.add(this.restarter);
            this.conflictCount
                    .add(this.learnedConstraintsDeletionStrategy.getTimer());
        }
        var firstTimeGlobal = false;
        if (this.timeBasedTimeout) {
            if (!global || this.timer == null) {
                firstTimeGlobal = true;
                this.undertimeout = true;
                TimerTask stopMe = new TimerTask() {
                    @Override
                    public void run() {
                        Solver.this.undertimeout = false;
                        synchronized (Solver.this) {
                            if (Solver.this.timer != null) {
                                Solver.this.timer.cancel();
                                Solver.this.timer = null;
                            }
                        }
                    }
                };
                this.timer = new Timer(true);
                this.timer.schedule(stopMe, this.timeout);

            }
        } else {
            if (!global || !alreadylaunched) {
                firstTimeGlobal = true;
                this.undertimeout = true;
                ConflictTimer conflictTimeout = new ConflictTimerAdapter(this,
                        (int) this.timeout) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void run() {
                        getSolver().expireTimeout();
                    }
                };
                this.conflictCount.add(conflictTimeout);
            }
        }
        if (!global || firstTimeGlobal) {
            this.restarter.init(this.params, this.stats);
            this.timebegin = System.currentTimeMillis();
        }
        this.needToReduceDB = false;
        // this is used to allow the solver to be incomplete,
        // when using a heuristics limited to a subset of variables
        this.lastConflictMeansUnsat = true;
        // Solve
        while (status == Lbool.UNDEFINED && this.undertimeout
                && this.lastConflictMeansUnsat) {

            int before = this.trail.size();
            unitClauseProvider.provideUnitClauses(this);
            this.stats.incImportedUnits(this.trail.size() - before);
            status = search(assumps);
            if (status == Lbool.UNDEFINED) {
                this.restarter.onRestart();
                this.slistener.restarting();
            }
        }

        cancelUntil(0);
        cancelLearntLiterals(learnedLiteralsLimit);
        if (!global && this.timeBasedTimeout) {
            synchronized (this) {
                if (this.timer != null) {
                    this.timer.cancel();
                    this.timer = null;
                }
            }
        }
        this.slistener.end(status);
        if (!this.undertimeout) {
            String message = " Timeout (" + this.timeout
                    + (this.timeBasedTimeout ? "ms" : " conflicts")
                    + ") exceeded";
            throw new TimeoutException(message);
        }
        if (status == Lbool.UNDEFINED && !this.lastConflictMeansUnsat) {
            throw new TimeoutException("Cannot decide the satisfiability");
        }
        // When using a search enumerator (to compute all models)
        // the final answer is FALSE, however we are aware of at least one model
        // (the last one)
        return model != null;
    }

    public void printInfos(PrintWriter out) {
        printInfos(out, prefix);
    }

    public void printInfos(PrintWriter out, String prefix) {
        out.print(prefix);
        out.println("constraints type ");
        long total = 0;
        for (Map.Entry<String, Counter> entry : this.constrTypes.entrySet()) {
            out.println(prefix + entry.getKey() + " => " + entry.getValue());
            total += entry.getValue().getValue();
        }
        out.print(prefix);
        out.print(total);
        out.println(" constraints processed.");
    }

    /**
     * @since 2.1
     */
    public void printLearntClausesInfos(PrintWriter out, String prefix) {
        if (this.learnts.isEmpty()) {
            return;
        }
        Map<String, Counter> learntTypes = new HashMap<>();
        for (Iterator<Constr> it = this.learnts.iterator(); it.hasNext();) {
            String type = it.next().getClass().getName();
            Counter count = learntTypes.get(type);
            if (count == null) {
                learntTypes.put(type, new Counter());
            } else {
                count.inc();
            }
        }
        for (Map.Entry<String, Counter> entry : learntTypes.entrySet()) {
            out.println(prefix + "learnt constraints type " + entry.getKey()
                    + "\t: " + entry.getValue());
        }
    }

    public SolverStats getStats() {
        return this.stats;
    }

    /**
     * 
     * @param myStats
     * @since 2.2
     */
    protected void initStats(SolverStats myStats) {
        this.stats = myStats;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#getOrder()
     */
    public IOrder getOrder() {
        return this.order;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.ICDCL#setOrder(org.sat4j.minisat.core.IOrder)
     */
    public void setOrder(IOrder h) {
        this.order = h;
        this.order.setLits(this.voc);
    }

    public ILits getVocabulary() {
        return this.voc;
    }

    public void reset() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        this.trail.clear();
        this.trailLim.clear();
        this.qhead = 0;
        for (Iterator<Constr> iterator = this.constrs.iterator(); iterator
                .hasNext();) {
            iterator.next().remove(this);
        }
        this.constrs.clear();
        clearLearntClauses();
        this.voc.resetPool();
        this.dsfactory.reset();
        this.stats.reset();
        this.constrTypes.clear();
        this.undertimeout = true;
        this.declaredMaxVarId = 0;
    }

    public int nVars() {
        if (this.declaredMaxVarId == 0) {
            return this.voc.nVars();
        }
        return this.declaredMaxVarId;
    }

    /**
     * @param constr
     *            a constraint implementing the Constr interface.
     * @return a reference to the constraint for external use.
     */
    public IConstr addConstr(Constr constr) {
        if (constr == null) {
            Counter count = this.constrTypes
                    .get("ignored satisfied constraints");
            if (count == null) {
                this.constrTypes.put("ignored satisfied constraints",
                        new Counter());
            } else {
                count.inc();
            }
        } else {
            this.constrs.push(constr);
            String type = constr.getClass().getName();
            Counter count = this.constrTypes.get(type);
            if (count == null) {
                this.constrTypes.put(type, new Counter());
            } else {
                count.inc();
            }
        }
        return constr;
    }

    public DataStructureFactory getDSFactory() {
        return this.dsfactory;
    }

    public IVecInt getOutLearnt() {
        return this.moutLearnt;
    }

    /**
     * returns the ith constraint in the solver.
     * 
     * @param i
     *            the constraint number (begins at 0)
     * @return the ith constraint
     */
    public IConstr getIthConstr(int i) {
        return this.constrs.get(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.specs.ISolver#printStat(java.io.PrintStream,
     * java.lang.String)
     */
    public void printStat(PrintStream out, String prefix) {
        printStat(new PrintWriter(out, true), prefix);
    }

    public void printStat(PrintWriter out) {
        printStat(out, prefix);
    }

    public void printStat(PrintWriter out, String prefix) {
        this.stats.printStat(out, prefix);
        double cputime = (System.currentTimeMillis() - this.timebegin) / 1000;
        out.println(prefix + "speed (assignments/second)\t: " //$NON-NLS-1$
                + this.stats.getPropagations() / cputime);
        this.order.printStat(out, prefix);
        printLearntClausesInfos(out, prefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString(String prefix) {
        var stb = new StringBuilder();
        Object[] objs = { this.dsfactory, this.learner, this.params, this.order,
                this.simplifier, this.restarter,
                this.learnedConstraintsDeletionStrategy };
        stb.append(prefix);
        stb.append("--- Begin Solver configuration ---"); //$NON-NLS-1$
        stb.append("\n"); //$NON-NLS-1$
        for (Object o : objs) {
            stb.append(prefix);
            stb.append(o.toString());
            stb.append("\n"); //$NON-NLS-1$
        }
        stb.append(prefix);
        stb.append("timeout=");
        if (this.timeBasedTimeout) {
            stb.append(this.timeout / 1000);
            stb.append("s\n");
        } else {
            stb.append(this.timeout);
            stb.append(" conflicts\n");
        }
        stb.append(prefix);
        stb.append("DB Simplification allowed=");
        stb.append(this.isDBSimplificationAllowed);
        stb.append("\n");
        stb.append(prefix);
        if (isSolverKeptHot()) {
            stb.append(
                    "Heuristics kept accross calls (keep the solver \"hot\")\n");
            stb.append(prefix);
        }
        stb.append("Listener: ");
        stb.append(slistener);
        stb.append("\n");
        stb.append(prefix);
        stb.append("--- End Solver configuration ---"); //$NON-NLS-1$
        return stb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(""); //$NON-NLS-1$
    }

    public int getTimeout() {
        return (int) (this.timeBasedTimeout ? this.timeout / 1000
                : this.timeout);
    }

    /**
     * @since 2.1
     */
    public long getTimeoutMs() {
        if (!this.timeBasedTimeout) {
            throw new UnsupportedOperationException(
                    "The timeout is given in number of conflicts!");
        }
        return this.timeout;
    }

    public void setExpectedNumberOfClauses(int nb) {
        this.constrs.ensure(nb);
    }

    public Map<String, Number> getStat() {
        return this.stats.toMap();
    }

    public int[] findModel() throws TimeoutException {
        if (isSatisfiable()) {
            return model();
        }
        // DLB findbugs ok
        // A zero length array would mean that the formula is a tautology.
        return null;
    }

    public int[] findModel(IVecInt assumps) throws TimeoutException {
        if (isSatisfiable(assumps)) {
            return model();
        }
        // DLB findbugs ok
        // A zero length array would mean that the formula is a tautology.
        return null;
    }

    public boolean isDBSimplificationAllowed() {
        return this.isDBSimplificationAllowed;
    }

    public void setDBSimplificationAllowed(boolean status) {
        this.isDBSimplificationAllowed = status;
    }

    /**
     * @since 2.1
     */
    public int nextFreeVarId(boolean reserve) {
        return this.voc.nextFreeVarId(reserve);
    }

    /**
     * @since 2.1
     */
    public IConstr addBlockingClause(IVecInt literals)
            throws ContradictionException {
        IConstr blockingClause = addClause(literals);
        slistener.blockClause(literals);
        return blockingClause;
    }

    public IConstr discardCurrentModel() throws ContradictionException {
        IVecInt blockingClause = createBlockingClauseForCurrentModel();
        if (blockingClause.isEmpty()) {
            throw new ContradictionException();
        }
        return addBlockingClause(blockingClause);
    }

    public IVecInt createBlockingClauseForCurrentModel() {
        IVecInt clause = new VecInt(decisions.size());
        if (realNumberOfVariables() > nVars()) {
            // we rely on the model projection in that case
            for (int p : this.model) {
                clause.push(-p);
            }
        } else {
            for (var i = 0; i < decisions.size(); i++) {
                clause.push(-decisions.get(i));
            }
        }

        return clause;
    }

    /**
     * @since 2.1
     */
    public void unset(int p) {
        // the literal might already have been
        // removed from the trail.
        if (this.voc.isUnassigned(p) || this.trail.isEmpty()) {
            return;
        }
        int current = this.trail.last();
        while (current != p) {
            undoOne();
            if (this.trail.isEmpty()) {
                return;
            }
            int i, j, k;
            current = this.trail.last();
        }
        undoOne();
        if (!trailLim.isEmpty() && trailLim.last() == trail.size()) {
            trailLim.pop();
        }
        this.qhead = this.trail.size();
    }

    /**
     * @since 2.3.6
     */
    public int getPropagationLevel() {
        return trail.size();
    }

    /**
     * @since 2.2
     */
    public void setLogPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @since 2.2
     */
    public String getLogPrefix() {
        return this.prefix;
    }

    /**
     * @since 2.2
     */
    public IVecInt unsatExplanation() {
        if (this.unsatExplanationInTermsOfAssumptions == null) {
            return null;
        }
        IVecInt copy = new VecInt(
                this.unsatExplanationInTermsOfAssumptions.size());
        this.unsatExplanationInTermsOfAssumptions.copyTo(copy);
        return copy;
    }

    /**
     * @since 2.3.1
     */
    public int[] modelWithInternalVariables() {
        if (this.model == null) {
            throw new UnsupportedOperationException(
                    CALL_THE_SOLVE_METHOD_FIRST);
        }
        int[] nmodel;
        if (nVars() == realNumberOfVariables()) {
            nmodel = new int[this.model.length];
            System.arraycopy(this.model, 0, nmodel, 0, nmodel.length);
        } else {
            nmodel = new int[this.fullmodel.length];
            System.arraycopy(this.fullmodel, 0, nmodel, 0, nmodel.length);
        }

        return nmodel;
    }

    /**
     * @since 2.3.1
     */
    public int realNumberOfVariables() {
        return this.voc.nVars();
    }

    /**
     * @since 2.3.2
     */
    public void stop() {
        expireTimeout();
    }

    protected Constr sharedConflict;

    /**
     * @since 2.3.2
     */
    public void backtrack(int[] reason) {
        IVecInt clause = new VecInt(reason.length);
        for (int d : reason) {
            clause.push(LiteralsUtils.toInternal(d));
        }
        this.sharedConflict = this.dsfactory.createUnregisteredClause(clause);
        learn(this.sharedConflict);
    }

    /**
     * @since 2.3.2
     */
    public Lbool truthValue(int literal) {
        var p = LiteralsUtils.toInternal(literal);
        if (this.voc.isFalsified(p)) {
            return Lbool.FALSE;
        }
        if (this.voc.isSatisfied(p)) {
            return Lbool.TRUE;
        }
        return Lbool.UNDEFINED;
    }

    /**
     * @since 2.3.2
     */
    public int currentDecisionLevel() {
        return decisionLevel();
    }

    /**
     * @since 2.3.2
     */
    public int[] getLiteralsPropagatedAt(int decisionLevel) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    /**
     * @since 2.3.2
     */
    public void suggestNextLiteralToBranchOn(int l) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    protected boolean isNeedToReduceDB() {
        return this.needToReduceDB;
    }

    public void setNeedToReduceDB(boolean needToReduceDB) {
        this.needToReduceDB = needToReduceDB;
    }

    public void setLogger(ILogAble out) {
        this.out = out;
    }

    public ILogAble getLogger() {
        return this.out;
    }

    public double[] getVariableHeuristics() {
        return this.order.getVariableHeuristics();
    }

    public IVec<Constr> getLearnedConstraints() {
        return this.learnts;
    }

    /**
     * @return the current deletion strategy for learned constraints
     * @since 2.3.6
     */
    public LearnedConstraintsDeletionStrategy getLearnedConstraintsDeletionStrategy() {
        return learnedConstraintsDeletionStrategy;
    }

    /**
     * @since 2.3.2
     */
    public void setLearnedConstraintsDeletionStrategy(ConflictTimer timer,
            LearnedConstraintsEvaluationType evaluation) {
        if (this.conflictCount != null) {
            this.conflictCount.add(timer);
            this.conflictCount
                    .remove(this.learnedConstraintsDeletionStrategy.getTimer());
        }
        switch (evaluation) {
        case ACTIVITY:
            this.learnedConstraintsDeletionStrategy = new ActivityLCDS(this,
                    timer);
            break;
        case LBD:
            this.learnedConstraintsDeletionStrategy = new GlucoseLCDS<D>(this,
                    timer);
            break;
        case LBD2:
            this.learnedConstraintsDeletionStrategy = new Glucose2LCDS<D>(this,
                    timer);
            break;
        }
        if (this.conflictCount != null) {
            this.learnedConstraintsDeletionStrategy.init();
        }
    }

    /**
     * @since 2.3.2
     */
    public void setLearnedConstraintsDeletionStrategy(
            LearnedConstraintsEvaluationType evaluation) {
        ConflictTimer aTimer = this.learnedConstraintsDeletionStrategy
                .getTimer();
        switch (evaluation) {
        case ACTIVITY:
            this.learnedConstraintsDeletionStrategy = new ActivityLCDS(this,
                    aTimer);
            break;
        case LBD:
            this.learnedConstraintsDeletionStrategy = new GlucoseLCDS<D>(this,
                    aTimer);
            break;
        case LBD2:
            this.learnedConstraintsDeletionStrategy = new Glucose2LCDS<D>(this,
                    aTimer);
            break;
        }
        if (this.conflictCount != null) {
            this.learnedConstraintsDeletionStrategy.init();
        }
    }

    public boolean isSolverKeptHot() {
        return this.keepHot;
    }

    public void setKeepSolverHot(boolean keepHot) {
        this.keepHot = keepHot;
        this.timeBasedTimeout = false;
    }

    private final Comparator<Integer> trailComparator() {
        return new Comparator<Integer>() {

            private final Map<Integer, Integer> trailLevel = new HashMap<>();

            {
                for (int i = 0; i < trail.size(); i++) {
                    trailLevel.put(var(trail.get(i)), i);
                }
            }

            public int compare(Integer i1, Integer i2) {
                Integer trail1 = trailLevel.get(Math.abs(i1));
                Integer trail2 = trailLevel.get(Math.abs(i2));
                if (trail1 == null) {
                    return -1;
                }
                if (trail2 == null) {
                    return -1;
                }
                return trail2 - trail1;
            }
        };
    }

    public IConstr addClauseOnTheFly(int[] literals) {
        List<Integer> lliterals = new ArrayList<>();
        for (Integer d : literals) {
            lliterals.add(d);
        }
        Collections.sort(lliterals, trailComparator());
        IVecInt clause = new VecInt(literals.length);
        for (int d : lliterals) {
            clause.push(LiteralsUtils.toInternal(d));
        }
        this.sharedConflict = this.dsfactory.createUnregisteredClause(clause);
        this.sharedConflict.register();
        addConstr(this.sharedConflict);
        return this.sharedConflict;
    }

    public ISolver getSolvingEngine() {
        return this;
    }

    /**
     * 
     * @param literals
     */
    public IConstr addAtMostOnTheFly(int[] literals, int degree) {
        IVecInt clause = new VecInt(literals.length);
        for (int d : literals) {
            clause.push(LiteralsUtils.toInternal(-d));
        }
        IVecInt copy = new VecInt(clause.size());
        clause.copyTo(copy);
        this.sharedConflict = this.dsfactory
                .createUnregisteredCardinalityConstraint(copy,
                        literals.length - degree);
        this.sharedConflict.register();
        addConstr(this.sharedConflict);
        return this.sharedConflict;
    }

    protected Set<Integer> fromLastDecisionLevel(IVecInt lits) {
        Set<Integer> subset = new HashSet<>();
        int max = -1, q, level;
        for (var i = 0; i < lits.size(); i++) {
            q = lits.get(i);
            level = voc.getLevel(q);
            if (level > max) {
                subset.clear();
                subset.add(q);
                max = level;
            } else if (level == max) {
                subset.add(q);
            }
        }
        return subset;
    }

    public void setUnitClauseProvider(UnitClauseProvider ucp) {
        this.unitClauseProvider = ucp;
    }

    @Override
    public void setUnitClauseConsumer(UnitClauseConsumer ucc) {
        this.unitClauseConsumer = ucc;
    }

    @Override
    public void postBumpActivity(Constr constr) {
        // Nothing to do by default.
    }

    @Override
    public int[] decisions() {
        if (model == null) {
            throw new IllegalStateException(
                    "Can only call that method when the problem is satisfiable!");
        }
        int n = decisions.size();
        var outdecisions = new int[n];
        System.arraycopy(decisions.toArray(), 0, outdecisions, 0, n);
        return outdecisions;
    }

    @Override
    public void preprocessing() {
    }
}
