package org.sat4j.pb;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

public class BugSAT155 {

    @Test
    public void testBasicDump() throws ContradictionException {
        IPBSolver solver = SolverFactory.newDefault();
        IVecInt literals = new VecInt(new int[] { 1, 2, 3 });
        IConstr c1 = solver.addClause(literals);
        assertEquals("+1 x1 +1 x2 +1 x3 >= 1", c1.dump());

    }

    @Test
    public void testBasicDumpPB() throws ContradictionException {
        IPBSolver solver = SolverFactory.newCuttingPlanes();
        IVecInt literals = new VecInt(new int[] { 1, 2, 3 });
        IConstr c1 = solver.addClause(literals);
        assertEquals("+1 x1 +1 x2 +1 x3 >= 1", c1.dump());

    }

    @Test
    public void testBasicDumpPBNegativeLiteral() throws ContradictionException {
        IPBSolver solver = SolverFactory.newCuttingPlanes();
        IVecInt literals = new VecInt(new int[] { 1, -2, 3 });
        IConstr c1 = solver.addClause(literals);
        assertEquals("+1 x1 +1 ~x2 +1 x3 >= 1", c1.dump());

    }

    @Test
    public void testBasicDump4Card() throws ContradictionException {
        IPBSolver solver = SolverFactory.newDefault();
        IVecInt literals = new VecInt(new int[] { 1, 2, 3 });
        IConstr c1 = solver.addAtLeast(literals, 2);
        assertEquals("+1 x1 +1 x2 +1 x3 >= 2", c1.dump());
    }

    @Test
    public void testBasicDump4CardPB() throws ContradictionException {
        IPBSolver solver = SolverFactory.newCuttingPlanes();
        IVecInt literals = new VecInt(new int[] { 1, 2, 3 });
        IConstr c1 = solver.addAtLeast(literals, 2);
        assertEquals("+1 x1 +1 x2 +1 x3 >= 2", c1.dump());
    }

    @Test
    public void testBasicDump4CardPBNegative() throws ContradictionException {
        IPBSolver solver = SolverFactory.newCuttingPlanes();
        IVecInt literals = new VecInt(new int[] { -1, 2, 3 });
        IConstr c1 = solver.addAtLeast(literals, 2);
        assertEquals("+1 ~x1 +1 x2 +1 x3 >= 2", c1.dump());
    }

    @Test
    public void testBasicDump4PB() throws ContradictionException {
        IPBSolver solver = SolverFactory.newDefault();
        IVecInt literals = new VecInt(new int[] { 1, 2, 3 });
        IVec<BigInteger> coeffs = new Vec<BigInteger>();
        coeffs.push(BigInteger.valueOf(2)).push(BigInteger.ONE)
                .push(BigInteger.ONE);
        IConstr c1 = solver.addAtLeast(literals, coeffs, BigInteger.valueOf(2));
        assertEquals("+2 x1 +1 x2 +1 x3 >= 2", c1.dump());
    }

    @Test
    public void testBasicDump4PBNegativeLieral() throws ContradictionException {
        IPBSolver solver = SolverFactory.newDefault();
        IVecInt literals = new VecInt(new int[] { 1, 2, -3 });
        IVec<BigInteger> coeffs = new Vec<BigInteger>();
        coeffs.push(BigInteger.valueOf(2)).push(BigInteger.ONE)
                .push(BigInteger.ONE);
        IConstr c1 = solver.addAtLeast(literals, coeffs, BigInteger.valueOf(2));
        assertEquals("+2 x1 +1 x2 +1 ~x3 >= 2", c1.dump());
    }
}
