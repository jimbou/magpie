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

public class ConflictMapRounding extends ConflictMap {

    public ConflictMapRounding(PBConstr cpb, int level, boolean noRemove,
            SkipStrategy skip, IPreProcess preprocess,
            IPostProcess postProcessing, IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy, PBSolverStats stats) {
        super(cpb, level, noRemove, skip, preprocess, postProcessing,
                weakeningStrategy, autoDivisionStrategy, stats);
    }

    public static IConflict createConflict(PBConstr cpb, int level,
            boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
            IPostProcess postProcessing, IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy, PBSolverStats stats) {
        return new ConflictMapRounding(cpb, level, noRemove, skip, preprocess,
                postProcessing, weakeningStrategy, autoDivisionStrategy, stats);
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
                return ConflictMapRounding.createConflict(cpb, level, noRemove,
                        skip, preprocess, postprocess, weakeningStrategy,
                        autoDivisionStrategy, stats);
            }

            @Override
            public String toString() {
                return "Always round the coefficient of the propagating literal to 1 during conflict analysis.";
            }
        };
    }

    static BigInteger ceildiv(BigInteger p, BigInteger q) {
        return p.add(q).subtract(BigInteger.ONE).divide(q);
    }

    @Override
    protected BigInteger reduceUntilConflict(int x, int xindex,
            BigInteger[] abc, BigInteger t, IWatchPb xyz) {
        final BigInteger a = abc[xindex];
        int n = xyz.size();
        BigInteger sprime = BigInteger.ZERO;
        for (int k = 0; k < n; k++) {
            if (!voc.isFalsified(xyz.get(k))) {
                sprime = sprime.add(ceildiv(abc[k], a));
            }
        }
        BigInteger tprime = ceildiv(t, a);
        sprime = sprime.subtract(tprime);
        BigInteger bigt = t;
        BigInteger tnewprime;
        boolean easyRounding = true;
        for (int k = 0; k < n; k++) {
            if (!sprime.equals(BigInteger.ZERO) && !voc.isFalsified(xyz.get(k))
                    && !abc[k].mod(a).equals(BigInteger.ZERO)) {
                // incremental computation of the slack proposed by Jakob
                bigt = bigt.subtract(abc[k]);
                tnewprime = ceildiv(bigt, a);
                sprime = sprime.add(tprime).subtract(ceildiv(abc[k], a))
                        .subtract(tnewprime);
                tprime = tnewprime;
                abc[k] = BigInteger.ZERO;
                listener.weakenOnReason(LiteralsUtils.toDimacs(xyz.get(k)));
                easyRounding = false;
            } else
                abc[k] = ceildiv(abc[k], a);
        }
        tprime = saturation(abc, tprime, xyz);
        this.coefMultCons = this.weightedLits.get(x ^ 1);
        this.coefMult = BigInteger.ONE;
        this.stats.incNumberOfRoundingOperations();
        if (easyRounding) {
            this.stats.incNumberOfEasyRoundingOperations();
        }
        listener.divideReason(a);
        return tprime;
    }

}
