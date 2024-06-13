/**
 * 
 */
package org.sat4j.pb.constraints.pb;

/**
 * @author romainwallon
 *
 */
public class NoPreProcess implements IPreProcess {

    private static final IPreProcess INSTANCE = new NoPreProcess();

    private NoPreProcess() {
        // TODO Auto-generated constructor stub
    }

    public static final IPreProcess instance() {
        return INSTANCE;
    }

    @Override
    public void preProcess(int dl, ConflictMap conflictMap) {
        // Nothing to do: Null Object Design Pattern.
    }

    @Override
    public String toString() {
        return "Conflict is considered as is when resolving";
    }
}
