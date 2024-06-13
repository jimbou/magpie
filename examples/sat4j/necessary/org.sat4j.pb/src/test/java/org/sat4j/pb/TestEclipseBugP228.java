package org.sat4j.pb;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.sat4j.pb.reader.OPBEclipseReader2007;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public class TestEclipseBugP228 {

    private static final String OPB_FILE = "* #variable= 6 #constraint= 3  \n"
            + "*\n" + "\n" + "explain:  x1 x2 x3 x4 x5 ;\n" + "\n"
            + "* P 1.0.0 requires either PP 1.0.0 \n" + "-1 x2 +1 x1 >= 0;\n"
            + "* 1223597333557 0.0.0.1223597333557 requires either P 1.0.0 \n"
            + "-1 x3 +1 x2 >= 0;\n" + "+1 x3 = 1;\n";

    @Test
    public void testExplain() throws ParseFormatException,
            ContradictionException, IOException, TimeoutException {
        IPBSolver solver = SolverFactory.newEclipseP2();
        Reader reader = new OPBEclipseReader2007(solver);
        reader.parseInstance(new ByteArrayInputStream(OPB_FILE.getBytes()));
        assertTrue(solver.isSatisfiable());
    }

}
