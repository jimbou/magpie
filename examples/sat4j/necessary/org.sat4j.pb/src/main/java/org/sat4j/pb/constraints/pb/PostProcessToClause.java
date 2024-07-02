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

import org.sat4j.core.VecInt;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

public class PostProcessToClause implements IPostProcess {

    private static final PostProcessToClause INSTANCE = new PostProcessToClause();

    private PostProcessToClause() {
        // no instantiation
    }

    public static final PostProcessToClause instance() {
        return INSTANCE;
    }

    public void postProcess(int dl, ConflictMap conflictMap) {
        if (conflictMap.isAssertive(dl)
                && (!conflictMap.degree.equals(BigInteger.ONE))) {
            int litLevel, ilit;
            conflictMap.setDecisionLevel(dl);
            if (conflictMap.assertiveLiteral != -1) {
                conflictMap.assertiveLiteral = this.chooseAssertiveLiteral(dl,
                        conflictMap);
                int lit = conflictMap.weightedLits
                        .getLit(conflictMap.assertiveLiteral);

                IVecInt toSuppress = new VecInt();

                for (int i = 0; i < conflictMap.size(); i++) {
                    ilit = conflictMap.weightedLits.getLit(i);
                    litLevel = conflictMap.voc.getLevel(ilit);
                    if ((litLevel < this.assertiveLevel)
                            && conflictMap.voc.isFalsified(ilit))
                        conflictMap.weightedLits.changeCoef(i, BigInteger.ONE);
                    else if (ilit != lit) {
                        toSuppress.push(ilit);
                    }
                }

                conflictMap.weightedLits.changeCoef(
                        conflictMap.assertiveLiteral, BigInteger.ONE);

                for (int i = 0; i < toSuppress.size(); i++)
                    conflictMap.removeCoef(toSuppress.get(i));

                conflictMap.degree = BigInteger.ONE;
                conflictMap.assertiveLiteral = conflictMap.weightedLits
                        .getFromAllLits(lit);
                assert conflictMap.backtrackLevel == conflictMap
                        .oldGetBacktrackLevel(dl);
            }
        }
    }

    private int assertiveLevel;

    public int chooseAssertiveLiteral(int maxLevel, ConflictMap conflictMap) {
        // we are looking for a level higher than maxLevel
        // where the constraint is still assertive
        // update ConflictMap.this.assertiveLiteral
        VecInt lits;
        int level;

        int indStop = ConflictMap.levelToIndex(maxLevel); // ou maxLevel - 1 ???
        int indStart = ConflictMap.levelToIndex(0);
        BigInteger slack = conflictMap.computeSlack(0)
                .subtract(conflictMap.degree);
        int previous = 0;
        for (int indLevel = indStart; indLevel <= indStop; indLevel++) {
            if (conflictMap.byLevel[indLevel] != null) {
                level = ConflictMap.indexToLevel(indLevel);
                assert conflictMap.computeSlack(level)
                        .subtract(conflictMap.degree).equals(slack);
                if (conflictMap.isImplyingLiteralOrdered(level, slack)) {
                    conflictMap.backtrackLevel = previous;
                    assertiveLevel = level;
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

        assert conflictMap.backtrackLevel == conflictMap
                .oldGetBacktrackLevel(maxLevel);
        return conflictMap.assertiveLiteral;
    }

    @Override
    public String toString() {
        return "Performs a post-processing after conflict analysis in order to learn clauses";
    }
}