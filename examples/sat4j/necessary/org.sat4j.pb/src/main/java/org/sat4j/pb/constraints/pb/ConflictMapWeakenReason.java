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

/**
 * 
 * 
 * @author Romain WALLON
 * 
 * @version 0.1.0
 */
public class ConflictMapWeakenReason extends ConflictMap {

    public ConflictMapWeakenReason(PBConstr cpb, int level, boolean noRemove,
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
        return new ConflictMapWeakenReason(cpb, level, noRemove, skip,
                preprocess, postProcessing, weakeningStrategy,
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
                return ConflictMapWeakenReason.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats);
            }

            @Override
            public String toString() {
                return "Weaken the reasons to as few literals as possible";
            }
        };
    }

    /**
     * Divides the constraint so as to get a coefficient equal to
     * {@code BigInteger#ONE} for {@code litImplied}.
     */
    @Override
    protected BigInteger reduceUntilConflict(int litImplied, int ind,
            BigInteger[] reducedCoefs, BigInteger degreeReduced, IWatchPb wpb) {
        BigInteger degree = degreeReduced;
        for (int i = 0; i < wpb.size(); i++) {
            if (i == ind) {
                // The implied literal is not touched.
                continue;
            }

            if (!voc.isFalsified(wpb.get(i))) {
                // Weakening on this literal preserves the propagation.
                degree = degree.subtract(reducedCoefs[i]);
                reducedCoefs[i] = BigInteger.ZERO;
                listener.weakenOnReason(LiteralsUtils.toDimacs(wpb.get(i)));
            }
        }

        // The coefficient of the literal is now at least equal to degree.
        // Any other coefficient having strictly less than the degree may be
        // safely weakened: a clause will be derived.
        // This approach may be use to lazily detect irrelevant literals.

        for (int i = 0; i < wpb.size()
                && degree.compareTo(BigInteger.ONE) > 0; i++) {
            if (!voc.isFalsified(wpb.get(i))) {
                // Satisfied literals have already been considered.
                continue;
            }

            if (reducedCoefs[i].compareTo(degree) < 0) {
                // Weakening on this literal preserves the propagation.
                degree = degree.subtract(reducedCoefs[i]);
                reducedCoefs[i] = BigInteger.ZERO;
                stats.incFalsifiedLiteralsRemovedFromReason();
                listener.weakenOnReason(LiteralsUtils.toDimacs(wpb.get(i)));
            }
        }

        degree = saturation(reducedCoefs, degree, wpb);
        return super.reduceUntilConflict(litImplied, ind, reducedCoefs, degree,
                wpb);
    }

}
