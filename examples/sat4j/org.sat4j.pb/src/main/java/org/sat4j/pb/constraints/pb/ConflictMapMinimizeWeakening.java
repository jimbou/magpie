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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.core.VarActivityListener;
import org.sat4j.pb.core.PBSolverStats;
import org.sat4j.specs.IVecInt;

/**
 * 
 * 
 * @author Romain WALLON
 * 
 * @version 0.1.0
 */
public class ConflictMapMinimizeWeakening extends ConflictMap {

    private final Map<BigInteger, IVecInt> map = new TreeMap<BigInteger, IVecInt>();

    public ConflictMapMinimizeWeakening(PBConstr cpb, int level,
            boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
            IPostProcess postProcessing, IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy, PBSolverStats stats) {
        super(cpb, level, noRemove, skip, preprocess, postProcessing,
                weakeningStrategy, autoDivisionStrategy, stats);
    }

    public static IConflict createConflict(PBConstr cpb, int level,
            boolean noRemove, SkipStrategy skip, IPreProcess preprocess,
            IPostProcess postProcessing, IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy, PBSolverStats stats) {
        return new ConflictMapMinimizeWeakening(cpb, level, noRemove, skip,
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
                return ConflictMapMinimizeWeakening.createConflict(cpb, level,
                        noRemove, skip, preprocess, postprocess,
                        weakeningStrategy, autoDivisionStrategy, stats);
            }

            @Override
            public String toString() {
                return "Weaken as few literals as possible in the reason";
            }
        };
    }

    @Override
    public BigInteger reduceInConstraint(IWatchPb wpb, BigInteger[] coefsBis,
            int indLitImplied, BigInteger degreeBis, BigInteger slackResolve) {
        assert degreeBis.compareTo(BigInteger.ONE) > 0;
        // search of a literal to remove
        int lit = findLiteralToRemove(wpb, coefsBis, indLitImplied, degreeBis);

        // a literal has been found
        assert lit != -1;
        assert lit != indLitImplied;
        // reduction can be done
        BigInteger degUpdate = degreeBis.subtract(coefsBis[lit]);
        this.possReducedCoefs = this.possReducedCoefs.subtract(coefsBis[lit]);
        coefsBis[lit] = BigInteger.ZERO;
        assert this.possReducedCoefs.equals(possConstraint(wpb, coefsBis));
        listener.weakenOnReason(LiteralsUtils.toDimacs(wpb.get(lit)));

        // saturation of the constraint
        degUpdate = saturation(coefsBis, degUpdate, wpb);

        assert coefsBis[indLitImplied].signum() > 0;
        assert degreeBis.compareTo(degUpdate) > 0;
        assert this.possReducedCoefs.equals(possConstraint(wpb, coefsBis));
        return degUpdate;
    }

    private int findLiteralToRemove(IWatchPb wpb, BigInteger[] coefsBis,
            int indLitImplied, BigInteger degreeBis) {
        if (map.isEmpty()) {
            for (int i = 0; i < coefsBis.length; i++) {
                BigInteger coef = coefsBis[i];
                int lit = wpb.get(i);
                if (i != indLitImplied && !voc.isFalsified(lit)) {
                    IVecInt vec = map.get(coef);
                    if (vec == null) {
                        vec = new VecInt();
                        map.put(coef, vec);
                    }
                    vec.push(i);
                }
            }
        }
        IVecInt vec = map.entrySet().stream().findFirst()
                .orElseThrow(NoSuchElementException::new).getValue();
        int lit = vec.last();
        vec.pop();
        return lit;
    }

    @Override
    public BigInteger resolve(PBConstr cpb, int litImplied,
            VarActivityListener val) {
        BigInteger res = super.resolve(cpb, litImplied, val);
        map.clear();
        return res;
    }
}
