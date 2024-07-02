package org.sat4j.pb;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.core.Counter;
import org.sat4j.pb.tools.OptimalModelIterator;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

public class ModelIteratorTest {

    private IPBSolver solver;

    @Before
    public void setUp() throws Exception {
        solver = SolverFactory.newDefault();
    }

    @Test
    public void testTimeoutWhenIterate()
            throws ContradictionException, TimeoutException {
        IVecInt clause = new VecInt();
        for (int i = 1; i <= 100; i++) {
            clause.push(i);
        }
        IVec<BigInteger> coeffs = new Vec<BigInteger>();
        for (int i = 0; i < 100; i++) {
            coeffs.push(BigInteger.ONE);
        }
        solver.addAtLeast(clause, coeffs, BigInteger.valueOf(98));

        final Counter nbSolutions = new Counter(0);

        PseudoIteratorDecorator decorator = new PseudoIteratorDecorator(solver);
        // decorator.setTimeout(2);
        try {
            while (decorator.isSatisfiable(true)) {
                decorator.model();
                decorator.discardCurrentSolution();
                nbSolutions.inc();
            }
            assertEquals(5051, nbSolutions.getValue());
        } catch (TimeoutException te) {
            // good
        }
    }

    @Test
    public void testTimeoutWhenIterateAll()
            throws ContradictionException, TimeoutException {
        IVecInt clause = new VecInt();
        for (int i = 1; i <= 100; i++) {
            clause.push(i);
        }
        IVec<BigInteger> coeffs = new Vec<BigInteger>();
        for (int i = 0; i < 100; i++) {
            coeffs.push(BigInteger.ONE);
        }
        solver.addAtLeast(clause, coeffs, BigInteger.valueOf(98));

        final Counter nbSolutions = new Counter(0);

        OptimalModelIterator decorator = new OptimalModelIterator(
                new OptToPBSATAdapter(
                        new PBSolverHandle(new PseudoOptDecorator(solver))));
        // decorator.setTimeout(2);
        try {
            while (decorator.isSatisfiable(true)) {
                solver.model();
                decorator.discardCurrentModel();
                nbSolutions.inc();
            }
            assertEquals(5051, nbSolutions.getValue());
        } catch (ContradictionException ce) {
            System.out.println(nbSolutions.getValue());
            ce.printStackTrace();
        }
    }
}
