/**
 * 
 */
package org.sat4j.pb.constraints.pb;

/**
 * @author romainwallon
 *
 */
public interface IReduceConflictStrategy {

    /**
     * Reduces the conflict w.r.t. the implied literal.
     * 
     * @param conflict
     *            The conflict to reduce.
     * @param literal
     *            The literal on which the next resolution will be performed.
     */
    void reduceConflict(ConflictMapDivideByPivot conflict, int literal);

}
