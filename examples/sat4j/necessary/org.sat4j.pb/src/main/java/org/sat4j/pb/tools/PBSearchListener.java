package org.sat4j.pb.tools;

import java.math.BigInteger;

import org.sat4j.annotations.Feature;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.specs.ISolverService;
import org.sat4j.specs.SearchListener;

/**
 * SearchListener for detailed Pseudo-Boolean solving.
 * 
 * @author leberre
 *
 * @param <S>
 * @since 2.3.6
 */
@Feature("searchlistener")
public interface PBSearchListener<S extends ISolverService>
        extends SearchListener<S> {

    /**
     * Called when a PB constraint is detected to be conflicting.
     * 
     * @param constr
     */
    default void onConflict(PBConstr constr) {

    }

    /**
     * Called when considering a reason for conflict analysis.
     * 
     * @param constr
     */
    default void withReason(PBConstr constr) {

    }

    /**
     * Weaken the current reason on a specific literal.
     * 
     * @param p
     *            a literal in dimacs format
     */
    default void weakenOnReason(int p) {

    }

    /**
     * Weaken the current reason on a specific literal.
     * 
     * @param coeff
     *            the coefficient by which the literal is weakened
     * @param p
     *            a literal in dimacs format
     */
    default void weakenOnReason(BigInteger coeff, int p) {

    }

    /**
     * Weaken the current conflict on a specific literal.
     * 
     * @param p
     *            a literal in dimacs format
     */
    default void weakenOnConflict(int p) {

    }

    /**
     * Weaken the current conflict on a specific literal.
     * 
     * @param coeff
     *            the coefficient by which the literal is weakened
     * @param p
     *            a literal in dimacs format
     */
    default void weakenOnConflict(BigInteger coeff, int p) {

    }

    /**
     * Multiply the current reason by an integer.
     * 
     * @param coeff
     *            the coefficient to apply of the reason
     */
    default void multiplyReason(BigInteger coeff) {

    }

    /**
     * Divide the current reason by an integer.
     * 
     * @param coeff
     *            the coefficient to apply of the reason
     */
    default void divideReason(BigInteger coeff) {

    }

    /**
     * Multiply the current reason by an integer.
     * 
     * @param coeff
     *            the coefficient to apply of the reason
     */
    default void multiplyConflict(BigInteger coeff) {

    }

    /**
     * Divide the current reason by an integer.
     * 
     * @param coeff
     *            the coefficient to apply of the reason
     */
    default void divideConflict(BigInteger coeff) {

    }

    /**
     * Apply saturation on the reason side.
     */
    default void saturateReason() {

    }

    /**
     * Apply saturation on the conflict side.
     */
    default void saturateConflict() {

    }

    /**
     * Add the reason and the conflict.
     */
    default void addReasonAndConflict() {

    }
}
