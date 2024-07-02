/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004, 2012 Artois University and CNRS
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU Lesser General Public License Version 2.1 or later (the
 * "LGPL"), in which case the provisions of the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL, and not to allow others to use your version of
 * this file under the terms of the EPL, indicate your decision by deleting
 * the provisions above and replace them with the notice and other provisions
 * required by the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of the EPL or the LGPL.
 *
 * Based on the original MiniSat specification from:
 *
 * An extensible SAT solver. Niklas Een and Niklas Sorensson. Proceedings of the
 * Sixth International Conference on Theory and Applications of Satisfiability
 * Testing, LNCS 2919, pp 502-518, 2003.
 *
 * See www.minisat.se for the original solver in C++.
 *
 * Contributors:
 *   CRIL - initial API and implementation
 *******************************************************************************/
package org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.pb.core.PBSolverStats;

public final class ConflictMapReduceToClause extends ConflictMap {

    public ConflictMapReduceToClause(PBConstr cpb, int level, boolean noRemove,
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
        return new ConflictMapReduceToClause(cpb, level, noRemove, skip,
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
                return ConflictMapReduceToClause.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats);
            }

            @Override
            public String toString() {
                return "Reduce to clause during conflict analysis if necessary";
            }
        };
    }

    public static final BigInteger MAXVALUE = BigInteger
            .valueOf(Long.MAX_VALUE);

    @Override
    protected BigInteger reduceUntilConflict(int litImplied, int ind,
            BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
        BigInteger coefLitImplied = this.weightedLits.get(litImplied ^ 1);

        if ((reducedCoefs[0].multiply(coefLitImplied).compareTo(MAXVALUE) > 0)
                || (reducedCoefs[ind].multiply(this.weightedLits.getCoef(0))
                        .compareTo(MAXVALUE) > 0)) {
            degreeReduced = reduceToClause(ind, wpb, reducedCoefs);
            this.coefMultCons = this.weightedLits.get(litImplied ^ 1);
            this.coefMult = BigInteger.ONE;
            this.numberOfReductions++;
            return degreeReduced;
        } else
            return super.reduceUntilConflict(litImplied, ind, reducedCoefs,
                    degreeReduced, wpb);

    }

    private BigInteger reduceToClause(int ind, IWatchPb wpb,
            BigInteger[] reducedCoefs) {
        for (int i = 0; i < reducedCoefs.length; i++) {
            if (i == ind || wpb.getVocabulary().isFalsified(wpb.get(i))) {
                reducedCoefs[i] = BigInteger.ONE;
            } else {
                reducedCoefs[i] = BigInteger.ZERO;
                listener.weakenOnReason(LiteralsUtils.toDimacs(wpb.get(i)));
            }
        }

        // FIXME This is mostly a hack (maybe?)
        listener.divideReason(degree);
        listener.saturateReason();

        return BigInteger.ONE;
    }
}
