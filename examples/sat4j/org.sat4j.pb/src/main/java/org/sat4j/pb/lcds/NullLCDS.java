/**
 * 
 */
package org.sat4j.pb.lcds;

import org.sat4j.minisat.core.ConflictTimer;
import org.sat4j.minisat.core.LearnedConstraintsDeletionStrategy;
import org.sat4j.specs.Constr;
import org.sat4j.specs.IVec;

/**
 * @author romain
 *
 */
public class NullLCDS implements LearnedConstraintsDeletionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final LearnedConstraintsDeletionStrategy decorated;

    public NullLCDS(LearnedConstraintsDeletionStrategy decorated) {
        super();
        this.decorated = decorated;
    }

    @Override
    public void init() {
        decorated.init();
    }

    @Override
    public ConflictTimer getTimer() {
        return decorated.getTimer();
    }

    @Override
    public void reduce(IVec<Constr> learnedConstrs) {

    }

    @Override
    public void onClauseLearning(Constr outLearnt) {
        decorated.onClauseLearning(outLearnt);
    }

    @Override
    public void onConflictAnalysis(Constr reason) {
        decorated.onConflictAnalysis(reason);
    }

    @Override
    public void onPropagation(Constr from, int propagated) {
        decorated.onPropagation(from, propagated);
    }
}
