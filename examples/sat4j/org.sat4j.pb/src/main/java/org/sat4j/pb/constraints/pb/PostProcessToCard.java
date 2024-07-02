/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004, 2012 Artois
 * University and CNRS
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU Lesser General Public License Version 2.1 or later (the
 * "LGPL"), in which case the provisions of the LGPL are applicable instead of
 * those above. If you wish to allow use of your version of this file only under
 * the terms of the LGPL, and not to allow others to use your version of this
 * file under the terms of the EPL, indicate your decision by deleting the
 * provisions above and replace them with the notice and other provisions
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
 * Contributors: CRIL - initial API and implementation
 *******************************************************************************/
package org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

import org.sat4j.core.VecInt;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

public class PostProcessToCard implements IPostProcess {

    private static final PostProcessToCard INSTANCE = new PostProcessToCard();

    private PostProcessToCard() {
        // no instantiation
    }

    public static final PostProcessToCard instance() {
        return INSTANCE;
    }

    public void postProcess(int dl, ConflictMap conflictMap) {
        // procedure Reduce-to-cardinality 4.3.9 proposed by H. Dixon
        // (Dixon's dissertation, page 67)
        if (conflictMap.isAssertive(dl)
                && (!conflictMap.degree.equals(BigInteger.ONE))) {
            int lit, litLevel, ilit;
            BigInteger coefLit;
            conflictMap.setDecisionLevel(dl);
            if (conflictMap.assertiveLiteral != -1) {
                conflictMap.assertiveLiteral = this.chooseAssertiveLiteral(dl,
                        conflictMap);
                coefLit = conflictMap.weightedLits
                        .getCoef(conflictMap.assertiveLiteral);

                // compute sum of coefficients of confl
                BigInteger sumCoefsTmp = BigInteger.ZERO;
                for (int i = 0; i < conflictMap.size(); i++) {
                    sumCoefsTmp = sumCoefsTmp
                            .add(conflictMap.weightedLits.getCoef(i));
                }

                // if it is already a cardinality constraint, return
                if (sumCoefsTmp.compareTo(
                        BigInteger.valueOf(conflictMap.size())) == 0) {
                    return;
                }
                IVecInt compLSet = new VecInt();
                BigInteger coefMax = coefLit;
                BigInteger coefTmp;

                // construct lSet with all falsified literals s.t. sum of
                // the coefs of compl(lSet) < degree
                // first we add the assertive literal
                sumCoefsTmp = sumCoefsTmp.subtract(coefLit);
                conflictMap.changeCoef(conflictMap.assertiveLiteral,
                        BigInteger.ONE);
                // then the needed falsified literals
                for (int i = 0; i < conflictMap.size(); i++) {
                    ilit = conflictMap.weightedLits.getLit(i);
                    lit = conflictMap.weightedLits
                            .getLit(conflictMap.assertiveLiteral);
                    litLevel = conflictMap.voc.getLevel(ilit);
                    coefTmp = conflictMap.weightedLits.getCoef(i);
                    if (ilit != lit) {
                        if (litLevel < this.assertiveLevel
                                && conflictMap.voc.isFalsified(ilit)) {
                            conflictMap.changeCoef(i, BigInteger.ONE);
                            sumCoefsTmp = sumCoefsTmp.subtract(coefTmp);
                            if (coefMax.compareTo(coefTmp) < 0)
                                coefMax = coefTmp;
                        } else
                            compLSet.push(ilit);
                    }
                }
                assert sumCoefsTmp.compareTo(conflictMap.degree) < 0;

                // add into lSet the sSet literals which are not already in
                // L and with coef > coefMax

                lit = conflictMap.weightedLits
                        .getLit(conflictMap.assertiveLiteral);
                int degreeCard = 1;
                for (int i = 0; i < compLSet.size(); i++) {
                    ilit = conflictMap.weightedLits
                            .getFromAllLits(compLSet.get(i));
                    if (coefMax.compareTo(
                            conflictMap.weightedLits.getCoef(ilit)) <= 0) {
                        conflictMap.changeCoef(ilit, BigInteger.ONE);
                        degreeCard++;
                    } else {
                        conflictMap.removeCoef(compLSet.get(i));
                    }
                }

                conflictMap.degree = BigInteger.valueOf(degreeCard);
                conflictMap.assertiveLiteral = conflictMap.weightedLits
                        .getFromAllLits(lit);

                assert conflictMap.backtrackLevel == conflictMap
                        .oldGetBacktrackLevel(dl);
            }
        }
    }

    private int assertiveLevel;

    private int chooseAssertiveLiteral(int maxLevel, ConflictMap conflictMap) {
        // we are looking for a level higher than maxLevel
        // where the constraint is still assertive
        VecInt lits;
        int level;
        int indStop = ConflictMap.levelToIndex(maxLevel); // ou maxLevel - 1 ???
        int indStart = ConflictMap.levelToIndex(0);
        BigInteger slack = conflictMap.computeSlack(0)
                .subtract(conflictMap.degree);
        int previous = 0;
        IVecInt literals = new VecInt();
        for (int indLevel = indStart; indLevel <= indStop; indLevel++) {
            if (conflictMap.byLevel[indLevel] != null) {
                level = ConflictMap.indexToLevel(indLevel);
                assert conflictMap.computeSlack(level)
                        .subtract(conflictMap.degree).equals(slack);
                if (conflictMap.isImplyingLiteralOrdered(level, slack,
                        literals)) {
                    this.assertiveLevel = level;
                    conflictMap.backtrackLevel = previous;
                    break;
                }
                // updating the new slack
                lits = conflictMap.byLevel[indLevel];
                int lit;
                for (IteratorInt iterator = lits.iterator(); iterator
                        .hasNext();) {
                    lit = iterator.next();
                    if (conflictMap.voc.isFalsified(lit)
                            && conflictMap.voc.getLevel(lit) == ConflictMap
                                    .indexToLevel(indLevel)) {
                        slack = slack
                                .subtract(conflictMap.weightedLits.get(lit));
                    }
                }
                if (!lits.isEmpty()) {
                    previous = level;
                }
            }
        }

        assert literals.size() > 0;
        BigInteger coef;
        int maxLit = literals.get(0);
        BigInteger maxCoef = conflictMap.weightedLits.getCoef(maxLit);
        for (int i = 1; i < literals.size(); i++) {
            coef = conflictMap.weightedLits.getCoef(literals.get(i));
            if (coef.compareTo(maxCoef) > 0) {
                maxLit = literals.get(i);
                maxCoef = coef;
            }
        }

        assert conflictMap.backtrackLevel == conflictMap
                .oldGetBacktrackLevel(maxLevel);
        assert literals.size() > 0;
        return maxLit;
    }

    @Override
    public String toString() {
        return "Performs a post-processing after conflict analysis in order to learn only cardinality constraints (Dixon's procedure)";
    }
}