package org.sat4j.pb;

import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Test;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.TimeoutException;

public class BugSAT161 {

    @Test
    public void testWithResolution() throws ParseFormatException, IOException,
            ContradictionException, TimeoutException {
        String filename = System.getProperty("test.pbprefix")
                + "/uclid_pb_benchmarks_normalized-43s.smv.opb";
        IPBSolver solver = SolverFactory.newResolution();
        Reader reader = new GoodOPBReader(solver);
        IProblem problem = reader.parseInstance(filename);
        assertFalse(problem.isSatisfiable());
    }

    @Test
    public void testWithCuttingPlanes() throws ParseFormatException,
            IOException, ContradictionException, TimeoutException {
        String filename = System.getProperty("test.pbprefix")
                + "/uclid_pb_benchmarks_normalized-43s.smv.opb";
        IPBSolver solver = SolverFactory.newCuttingPlanes();
        Reader reader = new GoodOPBReader(solver);
        IProblem problem = reader.parseInstance(filename);
        assertFalse(problem.isSatisfiable());
    }

}
