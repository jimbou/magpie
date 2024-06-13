package org.sat4j.pb.constraints;

import org.sat4j.pb.IPBSolver;
import org.sat4j.pb.SolverFactory;

public class PBCPSkipMaxClauseCardConstrLearningReduceCoefsByPowersOf2Test
        extends AbstractPseudoBooleanAndPigeonHoleTest {

    public PBCPSkipMaxClauseCardConstrLearningReduceCoefsByPowersOf2Test(
            String arg) {
        super(arg);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected IPBSolver createSolver() {
        return SolverFactory.newCuttingPlanesReduceByPowersOf2Skip();
    }

}
