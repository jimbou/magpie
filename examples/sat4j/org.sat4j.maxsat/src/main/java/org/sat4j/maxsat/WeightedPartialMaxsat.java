package org.sat4j.maxsat;

import java.math.BigInteger;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;

public interface WeightedPartialMaxsat extends ISolver {

    BigInteger SAT4J_MAX_BIG_INTEGER = new BigInteger(
            "100000000000000000000000000000000000000000");
    
    /**
     * Add a hard clause in the solver, i.e. a clause that must be satisfied.
     * 
     * @param literals
     *            the clause
     * @return the constraint is it is not trivially satisfied.
     * @throws ContradictionException
     */
    IConstr addHardClause(IVecInt literals) throws ContradictionException;

    /**
     * Add a soft clause in the solver, i.e. a clause with a weight of 1.
     * 
     * @param literals
     *            the clause.
     * @return the constraint is it is not trivially satisfied.
     * @throws ContradictionException
     */
    IConstr addSoftClause(IVecInt literals) throws ContradictionException;

    /**
     * Add a soft clause to the solver.
     * 
     * if the weight of the clause is greater of equal to the top weight, the
     * clause will be considered as a hard clause.
     * 
     * @param weight
     *            the weight of the clause
     * @param literals
     *            the clause
     * @return the constraint is it is not trivially satisfied.
     * @throws ContradictionException
     */
    IConstr addSoftClause(int weight, IVecInt literals)
            throws ContradictionException;

    IConstr addSoftClause(BigInteger weight, IVecInt literals)
            throws ContradictionException;

    void setTopWeight(BigInteger top);
    
    BigInteger violatedWeight();
}