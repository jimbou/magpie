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
public class ConflictMapDivideByPivot extends ConflictMap {

    final boolean divideReason;
    final DivisionStrategy divisionStrategy;

    final IReduceConflictStrategy reduceConflict;

    public ConflictMapDivideByPivot(PBConstr cpb, int level, boolean noRemove,
            SkipStrategy skip, IPreProcess preprocess,
            IPostProcess postProcessing, IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy, PBSolverStats stats,
            DivisionStrategy divisionStrategy,
            IReduceConflictStrategy reduceConflict, boolean divideReason) {
        super(cpb, level, noRemove, skip, preprocess, postProcessing,
                weakeningStrategy, autoDivisionStrategy, stats);
        this.divisionStrategy = divisionStrategy;
        this.reduceConflict = reduceConflict;
        this.divideReason = divideReason;
    }

    public static IConflict createConflict(PBConstr cpb, int level,
            boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
            IPostProcess postProcessing, IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy, PBSolverStats stats,
            DivisionStrategy divisionStrategy,
            IReduceConflictStrategy reduceConflict, boolean divideReason) {
        return new ConflictMapDivideByPivot(cpb, level, noRemove, skip,
                preprocess, postProcessing, weakeningStrategy,
                autoDivisionStrategy, stats, divisionStrategy, reduceConflict,
                divideReason);
    }

    public static IConflictFactory fullWeakeningOnReasonFactory() {
        return new IConflictFactory() {
            @Override
            public IConflict createConflict(PBConstr cpb, int level,
                    boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
                    IPostProcess postprocess,
                    IWeakeningStrategy weakeningStrategy,
                    AutoDivisionStrategy autoDivisionStrategy,
                    PBSolverStats stats) {
                return ConflictMapDivideByPivot.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats,
                        DivisionStrategy.FULL_WEAKENING,
                        NoReduceConflict.instance(), true);
            }

            @Override
            public String toString() {
                return "Divide the reason by the coefficient of the pivot when resolving, and weaken away non-divisible coefficient.";
            }
        };
    }

    public static IConflictFactory partialWeakeningOnReasonFactory() {
        return new IConflictFactory() {
            @Override
            public IConflict createConflict(PBConstr cpb, int level,
                    boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
                    IPostProcess postprocess,
                    IWeakeningStrategy weakeningStrategy,
                    AutoDivisionStrategy autoDivisionStrategy,
                    PBSolverStats stats) {
                return ConflictMapDivideByPivot.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats,
                        DivisionStrategy.PARTIAL_WEAKENING,
                        NoReduceConflict.instance(), true);
            }

            @Override
            public String toString() {
                return "Divide the reason by the coefficient of the pivot when resolving, and partially weaken non-divisible coefficient.";
            }
        };
    }

    public static IConflictFactory fullWeakeningOnBothFactory() {
        return new IConflictFactory() {
            @Override
            public IConflict createConflict(PBConstr cpb, int level,
                    boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
                    IPostProcess postprocess,
                    IWeakeningStrategy weakeningStrategy,
                    AutoDivisionStrategy autoDivisionStrategy,
                    PBSolverStats stats) {
                return ConflictMapDivideByPivot.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats,
                        DivisionStrategy.FULL_WEAKENING,
                        ReduceConflict.instance(), true);
            }

            @Override
            public String toString() {
                return "Divide both constraints by the coefficient of the pivot when resolving, and weaken away non-divisible coefficient.";
            }
        };
    }

    public static IConflictFactory partialWeakeningOnBothFactory() {
        return new IConflictFactory() {
            @Override
            public IConflict createConflict(PBConstr cpb, int level,
                    boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
                    IPostProcess postprocess,
                    IWeakeningStrategy weakeningStrategy,
                    AutoDivisionStrategy autoDivisionStrategy,
                    PBSolverStats stats) {
                return ConflictMapDivideByPivot.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats,
                        DivisionStrategy.PARTIAL_WEAKENING,
                        ReduceConflict.instance(), true);
            }

            @Override
            public String toString() {
                return "Divide both constraints by the coefficient of the pivot when resolving, and partially weaken non-divisible coefficient.";
            }
        };
    }

    public static IConflictFactory fullWeakeningOnConflictFactory() {
        return new IConflictFactory() {
            @Override
            public IConflict createConflict(PBConstr cpb, int level,
                    boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
                    IPostProcess postprocess,
                    IWeakeningStrategy weakeningStrategy,
                    AutoDivisionStrategy autoDivisionStrategy,
                    PBSolverStats stats) {
                return ConflictMapDivideByPivot.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats,
                        DivisionStrategy.FULL_WEAKENING,
                        ReduceConflict.instance(), false);
            }

            @Override
            public String toString() {
                return "Divide the conflict by the coefficient of the pivot when resolving, and weaken away non-divisible coefficient.";
            }
        };
    }

    public static IConflictFactory partialWeakeningOnConflictFactory() {
        return new IConflictFactory() {
            @Override
            public IConflict createConflict(PBConstr cpb, int level,
                    boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
                    IPostProcess postprocess,
                    IWeakeningStrategy weakeningStrategy,
                    AutoDivisionStrategy autoDivisionStrategy,
                    PBSolverStats stats) {
                return ConflictMapDivideByPivot.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats,
                        DivisionStrategy.PARTIAL_WEAKENING,
                        ReduceConflict.instance(), false);
            }

            @Override
            public String toString() {
                return "Divide the conflict by the coefficient of the pivot when resolving, and partially weaken non-divisible coefficient.";
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
        BigInteger coeffImplied = reducedCoefs[ind];
        BigInteger outputDegree = degreeReduced;
        int size = wpb.size();

        if (divideReason) {
            for (int i = 0; i < size; i++) {
                if (voc.isFalsified(wpb.get(i))) {
                    // The coefficient is not rounded up after division.
                    reducedCoefs[i] = ceildiv(reducedCoefs[i], coeffImplied);

                } else {
                    // Partially weakening the coefficient to allow its
                    // division.
                    // No rounding up here: coefficients are rounded down, and
                    // the remainder is subtracted from the degree.
                    BigInteger[] tmp = divisionStrategy.divide(reducedCoefs[i],
                            coeffImplied);
                    reducedCoefs[i] = tmp[0];
                    listener.weakenOnReason(tmp[1],
                            LiteralsUtils.toDimacs(wpb.get(i)));
                    outputDegree = outputDegree.subtract(tmp[1]);
                }
            }
            outputDegree = saturation(reducedCoefs,
                    ceildiv(outputDegree, coeffImplied), wpb);
            listener.divideReason(coeffImplied);
        }
        this.stats.incNumberOfRoundingOperations();
        this.reduceConflict.reduceConflict(this, litImplied ^ 1);
        this.coefMultCons = this.weightedLits.get(litImplied ^ 1);
        this.coefMult = reducedCoefs[ind];
        return outputDegree;
    }

    static BigInteger ceildiv(BigInteger p, BigInteger q) {
        return p.add(q).subtract(BigInteger.ONE).divide(q);
    }

}
