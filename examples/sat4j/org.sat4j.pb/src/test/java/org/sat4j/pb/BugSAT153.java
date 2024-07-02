package org.sat4j.pb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

public class BugSAT153 {

    @Test(expected = IllegalStateException.class)
    public void testMixingConstraints()
            throws ContradictionException, TimeoutException {
        IVecInt lits1 = new VecInt(new int[] { 1, 2, 3 });
        IVecInt lits2 = new VecInt(new int[] { 1, 2 });
        IVecInt lits3 = new VecInt(new int[] { -2, -3 });
        IVecInt lits4 = new VecInt(new int[] { 2, 3 });
        // Working call
        IPBSolver solver = SolverFactory.newDefault();
        solver.addAtMost(lits1, 2); // add x1 + x2 + x3 <= 2
        IConstr constr = solver.addAtLeast(lits2, 2); // add x1 + x2 >= 2
        solver.addClause(lits3); // add not(x2) v not(x3)
        assertTrue(solver.isSatisfiable());
        // Buggy call
        solver.removeConstr(constr); // remove x1 + x2 >= 2
        boolean contradict = false;
        try {
            solver.addAtLeast(lits4, 2); // add x2 + x3 >= 2; should contradict
                                         // not(x2) v not(x3)
        } catch (ContradictionException ce) {
            contradict = true;
        }
        assertTrue(contradict || !solver.isSatisfiable());
    }

    @Test
    public void testRemovingLast()
            throws ContradictionException, TimeoutException {
        IVecInt lits1 = new VecInt(new int[] { 1, 2, 3 });
        IVecInt lits2 = new VecInt(new int[] { 1, 2 });
        IVecInt lits3 = new VecInt(new int[] { -2, -3 });
        IVecInt lits4 = new VecInt(new int[] { 2, 3 });
        // Working call
        IPBSolver solver = SolverFactory.newDefault();
        solver.addAtMost(lits1, 2); // add x1 + x2 + x3 <= 2
        solver.addClause(lits3); // add not(x2) v not(x3)
        IConstr constr = solver.addAtLeast(lits2, 2); // add x1 + x2 >= 2
        assertTrue(solver.isSatisfiable());
        solver.removeConstr(constr); // remove x1 + x2 >= 2
        boolean contradict = false;
        try {
            solver.addAtLeast(lits4, 2); // add x2 + x3 >= 2; should contradict
                                         // not(x2) v not(x3)
        } catch (ContradictionException ce) {
            contradict = true;
        }
        assertTrue(contradict || !solver.isSatisfiable());
    }

    @Test(expected = IllegalStateException.class)
    public void testIssueWithUnitPropagation()
            throws ContradictionException, TimeoutException {
        IVecInt lits1 = new VecInt(new int[] { 1, 2, 3 });
        IVecInt lits2 = new VecInt(new int[] { 1, 2 });
        IVecInt lits3 = new VecInt(new int[] { -2, -3 });
        IVecInt lits4 = new VecInt(new int[] { 3 });
        IVecInt lits5 = new VecInt(new int[] { 2 });
        // Working call
        IPBSolver solver = SolverFactory.newDefault();
        solver.addAtMost(lits1, 2); // add x1 + x2 + x3 <= 2
        IConstr constr = solver.addAtLeast(lits2, 2); // add x1 + x2 >= 2
        solver.addClause(lits3); // add not(x2) v not(x3)
        assertTrue(solver.isSatisfiable());
        // Buggy call
        solver.removeConstr(constr); // remove x1 + x2 >= 2
        boolean contradict = false;
        try {
            solver.addClause(lits4); // add unit clause x3 : should propagate
                                     // not x2
        } catch (ContradictionException ce) {
            contradict = true;
        }
        assertFalse(contradict);
        assertTrue(solver.isSatisfiable());
        try {
            solver.addClause(lits5); // add unit clause x2 : should because
                                     // UNSAT
        } catch (ContradictionException ce) {
            contradict = true;
        }
        assertTrue(contradict || !solver.isSatisfiable());
    }
}
