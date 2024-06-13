/**
 * 
 */
package org.sat4j.pb;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.core.VecInt;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.pb.constraints.pb.SubsetSum;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

/**
 * @author Thibault Falque
 * 
 */
public class SubsetSumPreprocessingPBDecorator extends PBSolverDecorator {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final SubsetSum subset;

    private static final int MAX_ELEMENT = 100;

    private static final int MAX_SUM = 4000;

    public SubsetSumPreprocessingPBDecorator(IPBSolver solver) {
        super(solver);
        this.subset = new SubsetSum(MAX_SUM, MAX_ELEMENT);
    }

    @Override
    public IConstr addAtMost(IVecInt literals, IVecInt coeffs, int degree)
            throws ContradictionException {
        PBConstr ctr = (PBConstr) super.addAtMost(literals, coeffs, degree);
        preprocessSubsetSum(ctr);
        return ctr;
    }

    private void preprocessSubsetSum(IConstr ctr)
            throws ContradictionException {
        if (!(ctr instanceof PBConstr)) {
            return;
        }
        PBConstr pbCtr = (PBConstr) ctr;

        int[] coeffs = new int[ctr.size()];

        for (int i = 0; i < ctr.size(); i++) {
            coeffs[i] = (pbCtr.getCoefs()[i].intValue());
        }

        subset.setElements(coeffs);

        // applySubsetSum(coeffs.length, pbCtr.getSumCoefs().intValue());
        subset.sumExists(pbCtr.getSumCoefs().intValue());
        for (int i = pbCtr.getDegree().intValue(); i<=pbCtr.getSumCoefs().intValue(); i++) {
            subset.computeAllSubset(coeffs.length - 1,i, new VecInt());
        }
        addNotUsedCoeff(coeffs, pbCtr);
        addAlwaysUsedCoeff(coeffs, pbCtr);
        addBinaryUsedCoeff(coeffs, pbCtr);
    }

    private void applySubsetSum(int size, int sum) {
        subset.sumExists(sum);
        subset.computeAllSubset(size - 1, sum, new VecInt());
    }

    private void addBinaryUsedCoeff(int[] coeffs, PBConstr pbCtr)
            throws ContradictionException {
        List<Set<Integer>> sets = subset.getSubset();
        boolean always = true, aNotB = true, bNotA = true;
        for (int i = 0; i < coeffs.length; i++) {
            for (int j = 0; j < coeffs.length; j++) {
                if (i == j) {
                    continue;
                }
                int a = coeffs[i];
                int b = coeffs[j];
               //for (int k = 0; k < coeffs.length; k++) {
                //    System.out.print(coeffs[k] + " ");
                //}
                //System.out.println();
                assert (!sets.isEmpty());
                for (Set<Integer> s : sets) {
                    if (!s.contains(a) && !s.contains(b)) {
                        continue;
                    }
                    always &= s.contains(a) && s.contains(b);
                    if (always) {
                        // System.out.println(s);
                        // System.out.println("a " + a + " b " + b);
                        aNotB = false;
                        bNotA = false;
                        continue;
                    }
                    aNotB &= s.contains(a) && !s.contains(b);
                    bNotA &= !s.contains(a) && s.contains(b);
                    if (!aNotB && !bNotA) {
                        break;
                    }
                }
                if (aNotB||bNotA) {
                    System.out.println(pbCtr);
                    System.out.println("bNotA");
                    this.addClause(VecInt.of(-LiteralsUtils.toDimacs(pbCtr.get(j)),
                            -LiteralsUtils.toDimacs(pbCtr.get(i))));
                } else if (always) {
                    System.out.println(pbCtr);
                    System.out.println("always");
                    this.addExactly(VecInt.of(LiteralsUtils.toDimacs(pbCtr.get(j)),
                            LiteralsUtils.toDimacs(pbCtr.get(i))), VecInt.of(1, -1), 0);
                } else {
                    System.out.println("other case " + aNotB + " " + bNotA + " "
                            + " " + always);

                }
            }
            always = true;
            aNotB = true;
            bNotA = true;
        }

    }

    @Override
    public IConstr addAtMost(IVecInt literals, IVec<BigInteger> coeffs,
            BigInteger degree) throws ContradictionException {
        PBConstr ctr = (PBConstr) super.addAtMost(literals, coeffs, degree);
        preprocessSubsetSum(ctr);
        return ctr;
    }

    public void addNotUsedCoeff(int[] coeffs, PBConstr ctr)
            throws ContradictionException {
        List<Set<Integer>> sets = subset.getSubset();
        boolean[] used = new boolean[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            for (Set<Integer> s : sets) {
                used[i] |= s.contains(coeffs[i]);
                if (used[i]) {
                    break;
                }
            }
        }
        for (int i = 0; i < used.length; i++) {
            if (used[i]) {
                continue;
            }
            int lit = ctr.get(i);
            this.addClause(VecInt.of(LiteralsUtils.neg(lit)));
        }
    }

    public void addAlwaysUsedCoeff(int[] coeffs, PBConstr ctr)
            throws ContradictionException {
        assert coeffs.length == ctr.size();
        List<Set<Integer>> sets = subset.getSubset();
        boolean[] used = new boolean[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            for (Set<Integer> s : sets) {
                used[i] &= s.contains(coeffs[i]);
                if (!used[i]) {
                    break;
                }
            }
        }
        for (int i = 0; i < used.length; i++) {
            if (!used[i]) {
                continue;
            }
            int lit = ctr.get(i);
            this.addClause(VecInt.of(LiteralsUtils.neg(lit)));
        }
    }

    @Override
    public IConstr addAtLeast(IVecInt literals, IVecInt coeffs, int degree)
            throws ContradictionException {
        IConstr ctr = super.addAtLeast(literals, coeffs, degree);
        preprocessSubsetSum(ctr);
        return ctr;
    }

    @Override
    public IConstr addAtLeast(IVecInt literals, IVec<BigInteger> coeffs,
            BigInteger degree) throws ContradictionException {
        IConstr ctr = super.addAtLeast(literals, coeffs, degree);
        preprocessSubsetSum(ctr);
        return ctr;
    }

    @Override
    public IConstr addExactly(IVecInt literals, IVecInt coeffs, int weight)
            throws ContradictionException {
        return super.addExactly(literals, coeffs, weight);
    }

    @Override
    public IConstr addExactly(IVecInt literals, IVec<BigInteger> coeffs,
            BigInteger weight) throws ContradictionException {
        return super.addExactly(literals, coeffs, weight);
    }

}
