package org.sat4j.pb;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.sat4j.pb.reader.OPBReader2012;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;

public class BugJo {

    @Test
    public void testJoUnit()
            throws ParseFormatException, ContradictionException, IOException {
        IPBSolver solver = SolverFactory.newDefault();
        OPBReader2012 reader = new OPBReader2012(
                new PBSolverHandle(new PseudoOptDecorator(solver)));
        reader.parseInstance(new StringReader(
                "* #variable= 1 #constraint= 1\n*\n+1 x1 >= 1 ;\n"));
        assertEquals(1, solver.realNumberOfVariables());
        assertEquals(1, solver.nConstraints());

    }

    @Test
    public void testJoBinary()
            throws ParseFormatException, ContradictionException, IOException {
        IPBSolver solver = SolverFactory.newDefault();
        OPBReader2012 reader = new OPBReader2012(
                new PBSolverHandle(new PseudoOptDecorator(solver)));
        reader.parseInstance(new StringReader(
                "* #variable= 2 #constraint= 1\n*\n+1 x1 +1 x2 >= 1 ;\n"));
        assertEquals(2, solver.realNumberOfVariables());
        assertEquals(1, solver.nConstraints());

    }
}
