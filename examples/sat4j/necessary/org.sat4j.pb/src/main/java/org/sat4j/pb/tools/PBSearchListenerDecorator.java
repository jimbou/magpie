package org.sat4j.pb.tools;

import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolverService;
import org.sat4j.specs.Lbool;
import org.sat4j.specs.RandomAccessModel;
import org.sat4j.specs.SearchListener;

/**
 * Allow the basic SearchListener objects to be treated as PBSearchListener
 * objects in cutting planes based PB solvers.
 * 
 * @author leberre
 *
 * @param <S>
 * @since 3.0.0
 */
public class PBSearchListenerDecorator<S extends ISolverService>
        implements PBSearchListener<S> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final SearchListener<S> listener;

    public PBSearchListenerDecorator(SearchListener<S> listener) {
        this.listener = listener;
    }

    @Override
    public void init(S solverService) {
        listener.init(solverService);
    }

    @Override
    public void assuming(int p) {
        listener.assuming(p);
    }

    @Override
    public void propagating(int p) {
        listener.propagating(p);
    }

    @Override
    public void enqueueing(int p, IConstr reason) {
        listener.enqueueing(p, reason);
    }

    @Override
    public void backtracking(int p) {
        listener.backtracking(p);
    }

    @Override
    public void adding(int p) {
        listener.adding(p);
    }

    @Override
    public void learn(IConstr c) {
        listener.learn(c);
    }

    @Override
    public void delete(IConstr c) {
        listener.delete(c);
    }

    @Override
    public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
        listener.conflictFound(confl, dlevel, trailLevel);
    }

    @Override
    public void conflictFound(int p) {
        listener.conflictFound(p);
    }

    @Override
    public void solutionFound(int[] model, RandomAccessModel lazyModel) {
        listener.solutionFound(model, lazyModel);
    }

    @Override
    public void beginLoop() {
        listener.beginLoop();
    }

    @Override
    public void start() {
        listener.start();
    }

    @Override
    public void end(Lbool result) {
        listener.end(result);
    }

    @Override
    public void restarting() {
        listener.restarting();
    }

    @Override
    public void backjump(int backjumpLevel) {
        listener.backjump(backjumpLevel);
    }

    @Override
    public void cleaning() {
        listener.cleaning();
    }

    @Override
    public void learnUnit(int p) {
        listener.learnUnit(p);
    }
}
