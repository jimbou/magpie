package org.sat4j.pb.constraints.pb;

public class PostProcessDivideByGCD implements IPostProcess {

    private static final PostProcessDivideByGCD INSTANCE = new PostProcessDivideByGCD();

    private PostProcessDivideByGCD() {
        // no instantiation
    }

    public static final PostProcessDivideByGCD instance() {
        return INSTANCE;
    }

    public void postProcess(int dl, ConflictMap conflictMap) {
        int gcd = conflictMap.reduceCoeffsByGCD();
        if (gcd > 1) {
            conflictMap.stats.incNumberOfReductionsByGCD();
        }

    }

    @Override
    public String toString() {
        return "Performs a post-processing after conflict analysis in order to divide by gcd over coefficients of learned constraints";
    }
}
