/**
 * 
 */
package org.sat4j.pb.constraints.pb;

/**
 * @author romainwallon
 *
 */
public class NoReduceConflict implements IReduceConflictStrategy {

    private static final IReduceConflictStrategy INSTANCE = new NoReduceConflict();

    private NoReduceConflict() {
        // TODO Auto-generated constructor stub
    }

    public static final IReduceConflictStrategy instance() {
        return INSTANCE;
    }

    @Override
    public void reduceConflict(ConflictMapDivideByPivot conflict, int literal) {
        // TODO Auto-generated method stub
    }

}
