package org.sat4j.pb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

public class BugSAT144 {

    @Test
    public void testWithCard() throws ContradictionException, TimeoutException {
        IVecInt lits = new VecInt(new int[] { 1, 2, 3 });
        IVec<BigInteger> coeffs_good = new Vec<BigInteger>(new BigInteger[] {
                BigInteger.ONE, BigInteger.ONE, BigInteger.ONE });
        IPBSolver solver = SolverFactory.newDefault();
        solver.newVar(3);
        IConstr constr = null;
        solver.addAtMost(lits, 2);
        boolean is_sat = solver.isSatisfiable();
        assertTrue(is_sat);
        constr = solver.addPseudoBoolean(lits, coeffs_good, true,
                new BigInteger("3"));
        is_sat = solver.isSatisfiable();
        assertFalse(is_sat);
        solver.removeConstr(constr);
        is_sat = solver.isSatisfiable();
        assertTrue(is_sat);
    }

    @Test
    public void testWithPB() throws ContradictionException, TimeoutException {
        IVecInt lits = new VecInt(new int[] { 1, 2, 3 });
        IVec<BigInteger> coeffs_bug = new Vec<BigInteger>(new BigInteger[] {
                BigInteger.ONE, BigInteger.ONE, new BigInteger("2") });
        IPBSolver solver = SolverFactory.newDefault();
        solver.newVar(3);
        IConstr constr = null;
        solver.addAtMost(lits, 2);
        boolean is_sat = solver.isSatisfiable();
        assertTrue(is_sat);
        constr = solver.addPseudoBoolean(lits, coeffs_bug, true,
                new BigInteger("4"));
        is_sat = solver.isSatisfiable();
        assertFalse(is_sat);
        solver.removeConstr(constr);
        is_sat = solver.isSatisfiable();
        assertTrue(is_sat);
    }
}
