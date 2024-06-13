package org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.pb.core.PBSolverStats;

/**
 * This class implements the reduction to cardinality algorithm proposed by
 * Heidi Dixon in her dissertation. Procedure 4.3.9 page 67, in Automating
 * pseudo-boolean inference within a DPLL framework, Ph.D. thesis by Heidi
 * Dixon, University of Oregon, December 2004
 * 
 * @author anne
 * 
 */
public class ConflictMapReduceToCard extends ConflictMap {

    public static final BigInteger MAXVALUE = BigInteger
            .valueOf(Long.MAX_VALUE);

    public ConflictMapReduceToCard(PBConstr cpb, int level, boolean noRemove,
            SkipStrategy skip, IPreProcess preprocess, IPostProcess postprocess,
            IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy, PBSolverStats stats) {
        super(cpb, level, noRemove, skip, preprocess, postprocess,
                weakeningStrategy, autoDivisionStrategy, stats);
    }

    public static IConflict createConflict(PBConstr cpb, int level,
            boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
            IPostProcess postprocess, IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy, PBSolverStats stats) {
        return new ConflictMapReduceToCard(cpb, level, noRemove, skip,
                preprocess, postprocess, weakeningStrategy,
                autoDivisionStrategy, stats);
    }

    public static IConflictFactory factory() {
        return new IConflictFactory() {
            @Override
            public IConflict createConflict(PBConstr cpb, int level,
                    boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
                    IPostProcess postprocess,
                    IWeakeningStrategy weakeningStrategy,
                    AutoDivisionStrategy autoDivisionStrategy,
                    PBSolverStats stats) {
                return ConflictMapReduceToCard.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats);
            }

            @Override
            public String toString() {
                return "Reduce to cardinality constraint during conflict analysis if necessary";
            }
        };
    }

    /**
     * reduces the constraint defined by wpb until the result of the cutting
     * plane is a conflict. this reduction returns a cardinality.
     * 
     * @param litImplied
     * @param ind
     * @param reducedCoefs
     * @param wpb
     * @return degree of the cardinality constraint resulting
     */
    @Override
    protected BigInteger reduceUntilConflict(int litImplied, int ind,
            BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
        BigInteger degreeFurtherReduced = reduceToCard(ind, wpb, reducedCoefs);
        this.coefMultCons = this.weightedLits.get(litImplied ^ 1);
        this.coefMult = BigInteger.ONE;
        this.numberOfReductions++;
        return degreeFurtherReduced;
    }

    private BigInteger reduceToCard(int ind, IWatchPb wpb,
            BigInteger[] reducedCoefs) {
        BigInteger[] tmpCoefs = new BigInteger[reducedCoefs.length];
        BigInteger maxCoef = BigInteger.ZERO;
        for (int i = 0; i < reducedCoefs.length; i++) {
            if (i == ind || wpb.getVocabulary().isFalsified(wpb.get(i))) {
                tmpCoefs[i] = BigInteger.ONE;
                if (reducedCoefs[i].compareTo(maxCoef) > 0) {
                    maxCoef = reducedCoefs[i];
                }
            } else {
                tmpCoefs[i] = BigInteger.ZERO;
            }
        }
        int cpt = 0;
        for (int i = 0; i < reducedCoefs.length; i++) {
            if ((tmpCoefs[i].equals(BigInteger.ZERO))
                    && (reducedCoefs[i].compareTo(maxCoef) > 0)) {
                reducedCoefs[i] = BigInteger.ONE;
                cpt++;
            } else {
                reducedCoefs[i] = tmpCoefs[i];
                if (tmpCoefs[i].equals(BigInteger.ZERO)) {
                    listener.weakenOnReason(LiteralsUtils.toDimacs(wpb.get(i)));
                }
            }
        }

        // FIXME We must notify the listener that the degree has changed, but
        // how?
        // listener.?

        return BigInteger.valueOf(cpt + 1L);
    }

}
