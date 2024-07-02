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
import org.sat4j.minisat.constraints.cnf.Lits;
import org.sat4j.minisat.core.VarActivityListener;
import org.sat4j.pb.IPBSolverService;
import org.sat4j.pb.tools.PBSearchListener;
import org.sat4j.pb.tools.VoidPBTracing;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

/**
 * @author parrain
 * 
 */
public class MapPb implements IDataStructurePB {

    /*
     * During the process of cutting planes, pseudo-boolean constraints are
     * coded with a HashMap <literal, coefficient> and a BigInteger for the
     * degree.
     */
    protected InternalMapPBStructure weightedLits;

    protected PBSearchListener<IPBSolverService> listener = new VoidPBTracing();

    protected BigInteger degree;

    protected int assertiveLiteral = -1;

    private int cpCardsReduction = 0;

    private BigInteger cardDegree;

    protected final AutoDivisionStrategy autoDivisionStrategy;

    MapPb(PBConstr cpb, int level, boolean noRemove,
            AutoDivisionStrategy autoDivisionStrategy) {
        this.weightedLits = new InternalMapPBStructure(cpb, level, noRemove);
        this.degree = this.weightedLits.getComputedDegree();
        this.autoDivisionStrategy = autoDivisionStrategy;
    }

    MapPb(int size) {
        this.weightedLits = new InternalMapPBStructure(size);
        this.degree = BigInteger.ZERO;
        this.autoDivisionStrategy = AutoDivisionStrategy.ENABLED;
    }

    // temporarily : just for the case where an InternalMapPBStructure
    // is used to embed in one object literals and coefs
    public MapPb(IVecInt literals, IVec<BigInteger> coefs, BigInteger degree) {
        this.weightedLits = new InternalMapPBStructure(literals, coefs);
        this.degree = degree;
        this.autoDivisionStrategy = AutoDivisionStrategy.ENABLED;
    }

    public int reduceCoeffsByPower2() {
        if (this.weightedLits.size() > 0) {
            int nbBits = this.weightedLits.getCoef(0).bitLength();
            for (int i = 0; i < this.weightedLits.size() && nbBits > 0; i++) {
                nbBits = Math.min(nbBits,
                        this.weightedLits.getCoef(i).getLowestSetBit());
            }
            if (nbBits > 0) {
                for (int i = 0; i < this.weightedLits.size(); i++) {
                    changeCoef(i,
                            this.weightedLits.getCoef(i).shiftRight(nbBits));
                }
                // diviser le degre
                int nbBitsDegree = this.degree.getLowestSetBit();
                this.degree = this.degree.shiftRight(nbBits);
                if (nbBitsDegree < nbBits) {
                    this.degree = this.degree.add(BigInteger.ONE);
                }
            }
            return nbBits;
        } else
            return 0;
    }

    public int reduceCoeffsByGCD() {
        if (this.weightedLits.size() > 0) {
            BigInteger gcd = this.weightedLits.getCoef(0);
            for (int i = 0; i < this.weightedLits.size()
                    && gcd.compareTo(BigInteger.ONE) > 0; i++) {
                gcd = gcd.gcd(this.weightedLits.getCoef(i));
            }
            if (gcd.compareTo(BigInteger.ONE) > 0) {
                for (int i = 0; i < this.weightedLits.size(); i++) {
                    changeCoef(i, this.weightedLits.getCoef(i).divide(gcd));
                }
                // diviser le degre
                BigInteger[] result = this.degree.divideAndRemainder(gcd);
                this.degree = result[0];
                if (result[1].compareTo(BigInteger.ZERO) > 0) {
                    this.degree = this.degree.add(BigInteger.ONE);
                }
            }
            return gcd.intValue();
        } else
            return 1;
    }

    public boolean isCardinality() {
        boolean newcase = false;
        for (int i = 0; i < size(); i++) {
            if (!(this.weightedLits.getCoef(i).equals(BigInteger.ONE)
                    || this.weightedLits.getCoef(i).equals(BigInteger.ZERO))) {
                newcase = true;
                break;
            }
        }

        if (newcase) {
            if (autoDivisionStrategy.isCardinality(this.weightedLits)) {
                this.cpCardsReduction++;
                BigInteger[] division = degree
                        .divideAndRemainder(this.weightedLits.getCoef(0));
                if (!division[1].equals(BigInteger.ZERO)) {
                    division[0] = division[0].add(BigInteger.ONE);
                }
                this.cardDegree = division[0];

            } else {
                return false;
            }
        } else
            this.cardDegree = degree;
        return true;
    }

    public int getNumberOfCuttingPlanesCardinalities() {
        return cpCardsReduction;
    }

    public boolean isLongSufficient() {
        BigInteger som = BigInteger.ZERO;
        for (int i = 0; i < size() && som.bitLength() < Long.SIZE; i++) {
            assert this.weightedLits.getCoef(i).compareTo(BigInteger.ZERO) >= 0;
            som = som.add(this.weightedLits.getCoef(i));
        }
        return som.bitLength() < Long.SIZE;
    }

    public int getAssertiveLiteral() {
        return this.assertiveLiteral;
    }

    public BigInteger saturation() {
        assert this.degree.signum() > 0;
        BigInteger minimum = this.degree;
        boolean useSaturation = false;
        for (int ind = 0; ind < size(); ind++) {
            assert this.weightedLits.getCoef(ind).signum() >= 0;
            if (this.degree.compareTo(this.weightedLits.getCoef(ind)) < 0) {
                useSaturation = true;
                changeCoef(ind, this.degree);
            }
            assert this.weightedLits.getCoef(ind).signum() >= 0;
            if (this.weightedLits.getCoef(ind).signum() > 0)
                minimum = minimum.min(this.weightedLits.getCoef(ind));
        }
        // a clause has been learned
        if (minimum.equals(this.degree)
                && minimum.compareTo(BigInteger.ONE) > 0) {
            listener.divideConflict(this.degree);
            this.degree = BigInteger.ONE;
            for (int ind = 0; ind < size(); ind++) {
                changeCoef(ind, BigInteger.ONE);
            }
        }
        if (useSaturation) {
            listener.saturateConflict();
        }
        return this.degree;
    }

    public BigInteger cuttingPlane(PBConstr cpb, BigInteger deg,
            BigInteger[] reducedCoefs, VarActivityListener val, int p) {
        return cuttingPlane(cpb, deg, reducedCoefs, BigInteger.ONE, val, p);
    }

    public BigInteger cuttingPlane(PBConstr cpb, BigInteger degreeCons,
            BigInteger[] reducedCoefs, BigInteger coefMult,
            VarActivityListener val, int p) {
        listener.multiplyReason(coefMult);
        listener.addReasonAndConflict();
        this.degree = this.degree.add(degreeCons);
        assert this.degree.signum() > 0;
        if (reducedCoefs == null) {
            for (int i = 0; i < cpb.size(); i++) {
                val.varBumpActivity(cpb, i, p, weightedLits
                        .containsKey(LiteralsUtils.neg(cpb.get(i))));
                cuttingPlaneStep(cpb.get(i),
                        multiplyCoefficient(cpb.getCoef(i), coefMult));
            }
        } else {
            for (int i = 0; i < cpb.size(); i++) {
                val.varBumpActivity(cpb, i, p, weightedLits
                        .containsKey(LiteralsUtils.neg(cpb.get(i))));
                cuttingPlaneStep(cpb.get(i),
                        multiplyCoefficient(reducedCoefs[i], coefMult));
            }
        }
        val.postBumpActivity(cpb);

        return this.degree;
    }

    public BigInteger cuttingPlane(int[] lits, BigInteger[] reducedCoefs,
            BigInteger deg) {
        return cuttingPlane(lits, reducedCoefs, deg, BigInteger.ONE);
    }

    public BigInteger cuttingPlane(int lits[], BigInteger[] reducedCoefs,
            BigInteger degreeCons, BigInteger coefMult) {
        listener.multiplyReason(coefMult);
        this.degree = this.degree.add(degreeCons);
        assert this.degree.signum() > 0;

        for (int i = 0; i < lits.length; i++) {
            cuttingPlaneStep(lits[i], reducedCoefs[i].multiply(coefMult));
        }

        return this.degree;
    }

    private void cuttingPlaneStep(final int lit, final BigInteger coef) {
        assert coef.signum() >= 0;
        int nlit = lit ^ 1;
        if (coef.signum() > 0) {
            if (this.weightedLits.containsKey(nlit)) {
                assert !this.weightedLits.containsKey(lit);
                assert this.weightedLits.get(nlit) != null;
                if (this.weightedLits.get(nlit).compareTo(coef) < 0) {
                    BigInteger tmp = this.weightedLits.get(nlit);
                    setCoef(lit, coef.subtract(tmp));
                    assert this.weightedLits.get(lit).signum() > 0;
                    this.degree = this.degree.subtract(tmp);
                    removeCoef(nlit);
                } else {
                    if (this.weightedLits.get(nlit).equals(coef)) {
                        this.degree = this.degree.subtract(coef);
                        removeCoef(nlit);
                    } else {
                        decreaseCoef(nlit, coef);
                        assert this.weightedLits.get(nlit).signum() > 0;
                        this.degree = this.degree.subtract(coef);
                    }
                }
            } else {
                assert !this.weightedLits.containsKey(lit)
                        || this.weightedLits.get(lit).signum() > 0;
                if (this.weightedLits.containsKey(lit)) {
                    increaseCoef(lit, coef);
                } else {
                    setCoef(lit, coef);
                }
                assert this.weightedLits.get(lit).signum() > 0;
            }
        }
        assert !this.weightedLits.containsKey(nlit)
                || !this.weightedLits.containsKey(lit);
    }

    public void buildConstraintFromConflict(IVecInt resLits,
            IVec<BigInteger> resCoefs) {
        resLits.clear();
        resCoefs.clear();
        for (int i = 0; i < this.weightedLits.size(); i++) {
            if (!this.weightedLits.getCoef(i).equals(BigInteger.ZERO)) {
                resLits.push(this.weightedLits.getLit(i));
                resCoefs.push(this.weightedLits.getCoef(i));
            }
        }
    };

    public void buildConstraintFromMapPb(int[] resLits, BigInteger[] resCoefs) {
        // On recherche tous les litt?raux concern?s
        assert resLits.length == resCoefs.length;
        assert resLits.length == size();
        this.weightedLits.copyCoefs(resCoefs);
        this.weightedLits.copyLits(resLits);
    };

    public BigInteger getDegree() {
        return this.degree;
    }

    public int size() {
        return this.weightedLits.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        for (int ind = 0; ind < size(); ind++) {
            stb.append(this.weightedLits.getCoef(ind));
            stb.append(".");
            stb.append(Lits.toString(this.weightedLits.getLit(ind)));
            stb.append(" ");
        }
        return stb.toString() + " >= " + this.degree; //$NON-NLS-1$
    }

    private BigInteger multiplyCoefficient(BigInteger coef, BigInteger mult) {
        if (coef.equals(BigInteger.ONE)) {
            return mult;
        }
        return coef.multiply(mult);
    }

    void increaseCoef(int lit, BigInteger incCoef) {
        this.weightedLits.put(lit, this.weightedLits.get(lit).add(incCoef));
    }

    void decreaseCoef(int lit, BigInteger decCoef) {
        this.weightedLits.put(lit,
                this.weightedLits.get(lit).subtract(decCoef));
    }

    void setCoef(int lit, BigInteger newValue) {
        this.weightedLits.put(lit, newValue);
    }

    void changeCoef(int indLit, BigInteger newValue) {
        this.weightedLits.changeCoef(indLit, newValue);
    }

    void removeCoef(int lit) {
        this.weightedLits.remove(lit);
    }

    public BigInteger getCardDegree() {
        return this.cardDegree;
    }

}
