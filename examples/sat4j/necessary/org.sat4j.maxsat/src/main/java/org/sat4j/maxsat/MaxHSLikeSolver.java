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
package org.sat4j.maxsat;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.pb.IPBSolver;
import org.sat4j.pb.ObjectiveFunction;
import org.sat4j.pb.OptToPBSATAdapter;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.SolverDecorator;

/**
 * Simple MAXHS like maxsat solver. The unsat core computation is performed by
 * a SAT solver, and the minimal hitting set computation is handled by a PBO solver
 * (unlike the original MAXSAT which relies on a MILP solver like CPLEX).
 * 
 * @author leberre
 * @since 2.3.6
 */
public class MaxHSLikeSolver extends SolverDecorator<ISolver>
        implements WeightedPartialMaxsat {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final IPBSolver hsfinder;

    private final IVecInt lits = new VecInt();

    private final IVec<BigInteger> coefs = new Vec<>();

    private final ObjectiveFunction obj = new ObjectiveFunction(this.lits,
            this.coefs);

    private final Set<Integer> unitClauses = new HashSet<>();

    private BigInteger falsifiedWeight = BigInteger.ZERO;

    private boolean maxVarIdFixed = false;

    public MaxHSLikeSolver(IPBSolver pbsolver, ISolver satsolver) {
        super(satsolver);
        this.hsfinder = new OptToPBSATAdapter(new PseudoOptDecorator(pbsolver));
        this.hsfinder.setObjectiveFunction(obj);
    }

    @Override
    public IConstr addClause(IVecInt literals) throws ContradictionException {
        return addSoftClause(1, literals);
    }

    @Override
    public void reset() {
        decorated().reset();
        hsfinder.reset();
    }

    @Override
    public IConstr addHardClause(IVecInt literals)
            throws ContradictionException {
        return decorated().addClause(literals);
    }

    @Override
    public IConstr addSoftClause(IVecInt literals)
            throws ContradictionException {
        return addSoftClause(1, literals);
    }

    protected BigInteger top = SAT4J_MAX_BIG_INTEGER;

    public void setTopWeight(BigInteger top) {
        this.top = top;
    }

    @Override
    public int newVar(int howmany) {
        int res = super.newVar(howmany);
        this.maxVarIdFixed = true;
        return res;
    }

    @Override
    public void setExpectedNumberOfClauses(int nb) {
        this.lits.ensure(nb);
        this.falsifiedWeight = BigInteger.ZERO;
        super.setExpectedNumberOfClauses(nb);
    }

    protected void checkMaxVarId() {
        if (!this.maxVarIdFixed) {
            throw new IllegalStateException(
                    "Please call newVar(int) before adding constraints!!!");
        }
    }

    @Override
    public IConstr addSoftClause(int weight, IVecInt literals)
            throws ContradictionException {
        return addSoftClause(BigInteger.valueOf(weight), literals);
    }

    @Override
    public IConstr addSoftClause(BigInteger weight, IVecInt literals)
            throws ContradictionException {
        checkMaxVarId();
        if (weight.compareTo(this.top) < 0) {

            if (literals.size() == 1) {
                // if there is only a coefficient and a literal, no need to
                // create
                // a new variable
                // check first if the literal is already in the list:
                int lit = -literals.get(0);
                int index = this.lits.containsAt(lit);

                this.unitClauses.add(-lit);

                if (index == -1) {
                    // check if the opposite literal is already there
                    index = this.lits.containsAt(-lit);
                    if (index != -1) {
                        this.falsifiedWeight = this.falsifiedWeight.add(weight);
                        BigInteger oldw = this.coefs.get(index);
                        BigInteger diff = oldw.subtract(weight);
                        if (diff.signum() > 0) {
                            this.coefs.set(index, diff);
                        } else if (diff.signum() < 0) {
                            this.lits.set(index, lit);
                            this.coefs.set(index, diff.abs());
                            // remove from falsifiedWeight the
                            // part of the weight that will remain
                            // in the objective function
                            this.falsifiedWeight = this.falsifiedWeight
                                    .add(diff);
                        } else {
                            assert diff.signum() == 0;
                            this.lits.delete(index);
                            this.coefs.delete(index);
                        }
                        this.obj.setCorrectionOffset(this.falsifiedWeight);

                    } else {
                        hsfinder.registerLiteral(lit);
                        this.lits.push(lit);
                        this.coefs.push(weight);
                    }
                } else {
                    this.coefs.set(index, this.coefs.get(index).add(weight));
                }
                return UnitWeightedClause.instance();
            }
            this.coefs.push(weight);
            int newvar = nextFreeVarId(true);
            hsfinder.registerLiteral(newvar);
            literals.push(newvar);
            this.lits.push(newvar);
        }
        IConstr constr = decorated().addClause(literals);
        if (constr == null && isVerbose()) {
            System.out.println(getLogPrefix() + " hard constraint " + literals
                    + "(" + weight + ") is ignored");
        }
        return constr;
    }

    @Override
    public boolean isSatisfiable(boolean global) throws TimeoutException {
        return isSatisfiable();
    }

    @Override
    public boolean isSatisfiable(IVecInt assumps, boolean global)
            throws TimeoutException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSatisfiable() throws TimeoutException {
        IVecInt hs = new VecInt(hsfinder.findModel());
        while (!decorated().isSatisfiable(hs)) {
            if (isVerbose()) {
                System.out.print(".");
            }
            IVecInt core = decorated().unsatExplanation();
            IVecInt clause = new VecInt(core.size());
            for (IteratorInt it = core.iterator(); it.hasNext();) {
                clause.push(-it.next());
            }
            try {
                hsfinder.addClause(clause);
            } catch (ContradictionException e) {
                return false;
            }
            hs = new VecInt(hsfinder.findModel());
        }
        return true;
    }

    @Override
    public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger violatedWeight() {
        return hsfinder.getObjectiveFunction().calculateDegree(hsfinder);
    }

    @Override
    public String toString(String prefix) {        
        return prefix+"MaxHS like optimization"+System.lineSeparator()+super.toString(prefix);
    }

    @Override
    public String toString() {
        return "MaxHS like optimization "+super.toString();
    }

}
