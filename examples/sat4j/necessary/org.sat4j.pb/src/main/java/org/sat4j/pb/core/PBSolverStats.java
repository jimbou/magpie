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
package org.sat4j.pb.core;

import java.io.PrintWriter;
import java.math.BigInteger;

import org.sat4j.minisat.core.SolverStats;

public class PBSolverStats extends SolverStats {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private long numberOfReductions;

    private long numberOfReductionsByPower2;

    private long numberOfRightShiftsForCoeffs;

    private long numberOfReductionsByGCD;

    private long numberOfLearnedConstraintsReduced;

    private long numberOfResolution;

    private long numberOfCP;

    private long numberOfRoundingOperations;

    private long numberOfEasyRoundingOperations;

    private long numberOfEndingSkipping;

    private long numberOfInternalSkipping;

    private long numberOfDerivationSteps;

    private long numberOfRemainingUnassigned;

    private long numberOfRemainingAssigned;

    private long falsifiedLiteralsRemovedFromConflict;

    private long falsifiedLiteralsRemovedFromReason;

    private long timeForArtithmeticOperations;

    private BigInteger minRemoved;

    private BigInteger maxRemoved;

    private int nbRemoved;

    @Override
    public void reset() {
        super.reset();
        this.numberOfReductions = 0;
        this.numberOfLearnedConstraintsReduced = 0;
        this.numberOfResolution = 0;
        this.numberOfCP = 0;
        this.numberOfRoundingOperations = 0;
        this.numberOfReductionsByPower2 = 0;
        this.numberOfRightShiftsForCoeffs = 0;
        this.numberOfReductionsByGCD = 0;
        this.numberOfEndingSkipping = 0;
        this.numberOfInternalSkipping = 0;
        this.numberOfDerivationSteps = 0;
        this.numberOfRemainingUnassigned = 0;
        this.numberOfRemainingAssigned = 0;
    }

    @Override
    public void printStat(PrintWriter out, String prefix) {
        super.printStat(out, prefix);
        out.println(
                prefix + "number of reductions to clauses (during analyze)\t: "
                        + this.getNumberOfReductions());
        out.println(prefix
                + "number of learned constraints concerned by reduction\t: "
                + this.numberOfLearnedConstraintsReduced);
        out.println(prefix + "number of learning phase by resolution\t: "
                + this.numberOfResolution);
        out.println(prefix + "number of learning phase by cutting planes\t: "
                + this.numberOfCP);
        out.println(prefix + "number of rounding to 1 operations\t: "
                + this.numberOfRoundingOperations);
        out.println(prefix
                + "number of easy rounding to 1 operations (no literal elimination needed)\t: "
                + this.numberOfEasyRoundingOperations);
        out.println(prefix
                + "number of reductions of the coefficients by power 2 \t: "
                + this.getNumberOfReductionsByPower2());
        out.println(
                prefix + "number of right shift for reduction by power 2 \t: "
                        + this.numberOfRightShiftsForCoeffs);
        out.println(prefix
                + "number of reductions of the coefficients by GCD over coefficients \t: "
                + this.numberOfReductionsByGCD);
        out.println(prefix + "number of ending skipping \t: "
                + this.numberOfEndingSkipping);
        out.println(prefix + "number of internal skipping \t: "
                + this.numberOfInternalSkipping);
        out.println(prefix + "number of derivation steps \t: "
                + this.getNumberOfDerivationSteps());
        out.println(prefix + "number of skipped derivation steps \t: "
                + (this.numberOfInternalSkipping
                        + this.numberOfEndingSkipping));
        out.println(prefix + "number of remaining unassigned \t: "
                + this.numberOfRemainingUnassigned);
        out.println(prefix + "number of remaining assigned \t: "
                + this.numberOfRemainingAssigned);
        out.println(
                prefix + "number of falsified literals weakened from reason\t: "
                        + this.falsifiedLiteralsRemovedFromReason);
        out.println(prefix
                + "number of falsified literals weakened from conflict\t: "
                + this.falsifiedLiteralsRemovedFromConflict);
        out.println(prefix + "time for arithmetic operations\t: "
                + this.timeForArtithmeticOperations);
        out.println(prefix + "minimum degree of deleted constraints\t: "
                + this.minRemoved);
        out.println(prefix + "maximum degree of deleted constraints\t: "
                + this.maxRemoved);
        out.println(
                prefix + "number of deleted constraints\t: " + this.nbRemoved);
    }

    public long getNumberOfReductions() {
        return numberOfReductions;
    }

    public void incNumberOfReductions(long increment) {
        this.numberOfReductions += increment;
    }

    public long getNumberOfReductionsByPower2() {
        return numberOfReductionsByPower2;
    }

    public void incNumberOfReductionsByPower2() {
        this.numberOfReductionsByPower2++;
    }

    public long getNumberOfRightShiftsForCoeffs() {
        return numberOfRightShiftsForCoeffs;
    }

    public void incNumberOfRightShiftsForCoeffs(int increment) {
        this.numberOfRightShiftsForCoeffs += increment;
    }

    public long getNumberOfReductionsByGCD() {
        return numberOfReductionsByGCD;
    }

    public void incNumberOfReductionsByGCD() {
        this.numberOfReductionsByGCD++;
    }

    public long getNumberOfLearnedConstraintsReduced() {
        return numberOfLearnedConstraintsReduced;
    }

    public void incNumberOfLearnedConstraintsReduced() {
        this.numberOfLearnedConstraintsReduced++;
    }

    public long getNumberOfResolution() {
        return numberOfResolution;
    }

    public void incNumberOfResolution() {
        this.numberOfResolution++;
    }

    public long getNumberOfCP() {
        return numberOfCP;
    }

    public void incNumberOfCP() {
        this.numberOfCP++;
    }

    public long getNumberOfRoundingOperations() {
        return numberOfRoundingOperations;
    }

    public void incNumberOfRoundingOperations() {
        this.numberOfRoundingOperations++;
    }

    public long getNumberOfEasyRoundingOperations() {
        return numberOfEasyRoundingOperations;
    }

    public void incNumberOfEasyRoundingOperations() {
        this.numberOfEasyRoundingOperations++;
    }

    public long getNumberOfEndingSkipping() {
        return numberOfEndingSkipping;
    }

    public void incNumberOfEndingSkipping() {
        this.numberOfEndingSkipping++;
    }

    public long getNumberOfInternalSkipping() {
        return numberOfInternalSkipping;
    }

    public void incNumberOfInternalSkipping() {
        this.numberOfInternalSkipping++;
    }

    public long getNumberOfDerivationSteps() {
        return numberOfDerivationSteps;
    }

    public void incNumberOfDerivationSteps() {
        this.numberOfDerivationSteps++;
    }

    public long getNumberOfRemainingUnassigned() {
        return numberOfRemainingUnassigned;
    }

    public void incNumberOfRemainingUnassigned() {
        this.numberOfRemainingUnassigned++;
    }

    public long getNumberOfRemainingAssigned() {
        return numberOfRemainingAssigned;
    }

    public void incNumberOfRemainingAssigned() {
        this.numberOfRemainingAssigned++;
    }

    public void incFalsifiedLiteralsRemovedFromReason() {
        this.falsifiedLiteralsRemovedFromReason++;

    }

    public void incFalsifiedLiteralsRemovedFromConflict() {
        this.falsifiedLiteralsRemovedFromConflict++;

    }

    public void incTimeForArithmeticOperations(long time) {
        this.timeForArtithmeticOperations += time;
    }

    public void setMinRemoved(BigInteger minRemoved) {
        if (this.minRemoved == null) {
            this.minRemoved = minRemoved;
        } else {
            this.minRemoved = this.minRemoved.min(minRemoved);
        }
    }

    public void setMaxRemoved(BigInteger maxRemoved) {
        if (this.maxRemoved == null) {
            this.maxRemoved = maxRemoved;
        } else {
            this.maxRemoved = this.maxRemoved.max(maxRemoved);
        }
    }

    public void incNbRemoved() {
        this.nbRemoved++;
    }

}
