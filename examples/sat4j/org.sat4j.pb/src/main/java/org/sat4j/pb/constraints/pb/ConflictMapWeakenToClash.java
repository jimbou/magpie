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

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.core.VecInt;
import org.sat4j.pb.core.PBSolverStats;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

/**
 * 
 * 
 * @author Romain WALLON
 * 
 * @version 0.1.0
 */
public class ConflictMapWeakenToClash extends ConflictMap {

    public ConflictMapWeakenToClash(PBConstr cpb, int level, boolean noRemove,
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
        return new ConflictMapWeakenToClash(cpb, level, noRemove, skip,
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
                return ConflictMapWeakenToClash.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats);
            }

            @Override
            public String toString() {
                return "Weaken reason to find a multiple of the pivot";
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
        BigInteger slackResolve = BigInteger.ONE.negate();
        BigInteger slackThis = BigInteger.ZERO;
        BigInteger slackIndex;
        BigInteger slackConflict = slackConflict();
        BigInteger reducedDegree = degreeReduced;
        BigInteger previousCoefLitImplied = BigInteger.ZERO;
        BigInteger tmp;
        BigInteger[] mult = new BigInteger[] { ONE, ONE, ZERO };
        final BigInteger coefLitImplied = this.weightedLits.get(litImplied ^ 1);
        this.possReducedCoefs = possConstraint(wpb, reducedCoefs);
        // assert possReducedCoefs.compareTo(reducedDegree) >= 0;

        // Ensuring that the addition of the constraint preserves the conflict.
        do {
            if (slackResolve.signum() >= 0) {
                assert slackThis.signum() > 0;
                tmp = reduceInConstraint(wpb, reducedCoefs, ind, reducedDegree,
                        slackResolve);
                assert tmp.compareTo(reducedDegree) < 0
                        && tmp.compareTo(BigInteger.ONE) >= 0;
                reducedDegree = tmp;
            }
            // search of the multiplying coefficients
            assert this.weightedLits.get(litImplied ^ 1).signum() > 0;
            assert reducedCoefs[ind].signum() > 0;
            if (!reducedCoefs[ind].equals(previousCoefLitImplied)) {
                assert coefLitImplied
                        .equals(this.weightedLits.get(litImplied ^ 1));
                mult = findMult(coefLitImplied, reducedCoefs[ind]);
                assert mult[0].signum() > 0;
                assert mult[1].signum() > 0;

                this.coefMult = mult[0];
                this.coefMultCons = mult[1];

                assert this.coefMult.multiply(coefLitImplied)
                        .equals(this.coefMultCons.multiply(reducedCoefs[ind])
                                .subtract(mult[2]));
                previousCoefLitImplied = reducedCoefs[ind];
            }

            // slacks computed for each constraint
            slackThis = this.possReducedCoefs.subtract(reducedDegree)
                    .multiply(this.coefMultCons);
            assert slackThis
                    .equals(wpb.slackConstraint(reducedCoefs, reducedDegree)
                            .multiply(this.coefMultCons));
            assert slackConflict.equals(slackConflict());
            slackIndex = slackConflict.multiply(this.coefMult);
            assert slackIndex.signum() <= 0;
            // estimate of the slack after the cutting plane
            slackResolve = slackThis.add(slackIndex);
        } while ((slackResolve.signum() >= 0) || this.isUnsat());

        // Ensuring that the literal will be removed.
        // We need to apply weakening so that the degree becomes equal to the
        // expected value.

        // First, applying the multiplication.
        Map<BigInteger, IVecInt> coefsSat = new TreeMap<BigInteger, IVecInt>();
        Map<BigInteger, IVecInt> coefsFals = new TreeMap<BigInteger, IVecInt>();
        listener.multiplyReason(coefMultCons);
        for (int i = 0; i < reducedCoefs.length; i++) {
            BigInteger coef = reducedCoefs[i].multiply(coefMultCons);
            reducedCoefs[i] = coef;

            if (!voc.isFalsified(wpb.get(i)) && i != ind) {
                IVecInt lits = coefsSat.get(coef);
                if (lits == null) {
                    lits = new VecInt();
                    coefsSat.put(coef, lits);
                }
                lits.push(i);
            } else if (voc.isFalsified(wpb.get(i))) {
                IVecInt lits = coefsFals.get(coef);
                if (lits == null) {
                    lits = new VecInt();
                    coefsFals.put(coef, lits);
                }
                lits.push(i);

            }
        }
        reducedDegree = reducedDegree.multiply(coefMultCons);
        coefMultCons = BigInteger.ONE;

        if (mult[2].signum() == 0) {
            // There is nothing to weaken.
            reducedDegree = saturation(reducedCoefs, reducedDegree, wpb);
            if (reducedDegree.equals(BigInteger.ONE)) {
                coefMultCons = coefLitImplied;
            }
            return reducedDegree;
        }

        // Weakening the literals.
        BigInteger expected = coefMult.multiply(coefLitImplied);
        BigInteger toWeaken = reducedDegree.subtract(expected);
        assert toWeaken.signum() >= 0;
        for (Entry<BigInteger, IVecInt> entry : coefsSat.entrySet()) {
            if (toWeaken.signum() == 0) {
                break;
            }

            BigInteger coef = entry.getKey();
            for (IteratorInt it = entry.getValue().iterator(); it.hasNext();) {
                int index = it.next();
                if (coef.compareTo(toWeaken) > 0) {
                    // Partial weakening.
                    listener.weakenOnReason(toWeaken,
                            LiteralsUtils.toDimacs(wpb.get(index)));
                    reducedCoefs[index] = coef.subtract(toWeaken);
                    reducedDegree = reducedDegree.subtract(toWeaken);
                    toWeaken = BigInteger.ZERO;
                    break;
                }

                // Full weakening.
                listener.weakenOnReason(LiteralsUtils.toDimacs(wpb.get(index)));
                reducedCoefs[index] = BigInteger.ZERO;
                toWeaken = toWeaken.subtract(coef);
                reducedDegree = reducedDegree.subtract(coef);
            }
        }
        if (toWeaken.signum() > 0
                && reducedCoefs[ind].compareTo(reducedDegree) < 0) {
            // Weakening on falsified literals.
            for (Entry<BigInteger, IVecInt> entry : coefsFals.entrySet()) {
                if (toWeaken.signum() == 0) {
                    break;
                }

                BigInteger coef = entry.getKey();
                for (IteratorInt it = entry.getValue().iterator(); it
                        .hasNext();) {
                    int index = it.next();
                    if (weightedLits.containsKey(wpb.get(index) ^ 1)) {
                        continue;
                    }
                    stats.incFalsifiedLiteralsRemovedFromReason();
                    if (coef.compareTo(toWeaken) > 0) {
                        // Partial weakening.
                        listener.weakenOnReason(toWeaken,
                                LiteralsUtils.toDimacs(wpb.get(index)));
                        reducedCoefs[index] = coef.subtract(toWeaken);
                        reducedDegree = reducedDegree.subtract(toWeaken);
                        toWeaken = BigInteger.ZERO;
                        break;
                    }

                    // Full weakening.
                    listener.weakenOnReason(
                            LiteralsUtils.toDimacs(wpb.get(index)));
                    reducedCoefs[index] = BigInteger.ZERO;
                    toWeaken = toWeaken.subtract(coef);
                    reducedDegree = reducedDegree.subtract(coef);
                }
            }
        }

        if (toWeaken.signum() > 0) {
            // Weakening on the propagated literal.
            toWeaken = toWeaken.min(reducedCoefs[ind].subtract(expected));
            reducedCoefs[ind] = reducedCoefs[ind].subtract(toWeaken);
            listener.weakenOnReason(toWeaken,
                    LiteralsUtils.toDimacs(wpb.get(ind)));
            reducedDegree = reducedDegree.subtract(toWeaken);

        }

        reducedDegree = saturation(reducedCoefs, reducedDegree, wpb);
        if (reducedDegree.equals(BigInteger.ONE)) {
            coefMultCons = coefLitImplied;
            coefMult = BigInteger.ONE;
        }
        return reducedDegree;
    }

    /**
     * Finds the value by which the reason must be multiplied to clash with the
     * conflict.
     * 
     * @param conflict
     *            The coefficient in the conflict.
     * @param reason
     *            The coefficient in the reason.
     * 
     * @return An array containing the value by which to multiply the conflict,
     *         the value by which to multiply the reason and the amount to
     *         weaken on the implied literal.
     */
    protected BigInteger[] findMult(BigInteger conflict, BigInteger reason) {
        int cmp = conflict.compareTo(reason);
        if (cmp == 0) {
            // Both coefficients are equal.
            // We can keep coefficients as is.
            return new BigInteger[] { ONE, ONE, ZERO };
        }

        if (cmp < 0) {
            // The coefficient is bigger in the reason.
            // It has to be weakened to a multiple of the coefficient in the
            // conflict.
            BigInteger[] divMod = reason.divideAndRemainder(conflict);
            return new BigInteger[] { divMod[0], ONE, divMod[1] };
        }

        // The coefficient is bigger in the conflict.
        BigInteger[] divMod = conflict.divideAndRemainder(reason);

        if (divMod[1].signum() == 0) {
            // In this case, the coefficient in the conflict is a multiple of
            // that in the reason.
            // We just need to multiply the reason.
            return new BigInteger[] { ONE, divMod[0], ZERO };
        }

        // We need to find a value that is greater than the coefficient to
        // allow weakening.
        return new BigInteger[] { ONE, divMod[0].add(ONE),
                reason.subtract(divMod[1]) };
    }

}
