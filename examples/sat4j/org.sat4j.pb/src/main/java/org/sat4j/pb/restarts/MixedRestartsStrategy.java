/**
 * 
 */
package org.sat4j.pb.restarts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sat4j.minisat.core.RestartStrategy;
import org.sat4j.minisat.core.SearchParams;
import org.sat4j.minisat.core.SolverStats;
import org.sat4j.specs.Constr;

/**
 * @author wallon
 *
 */
public class MixedRestartsStrategy implements RestartStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final List<RestartStrategy> strategies;

    private MixedRestartsStrategy(List<RestartStrategy> strategies) {
        this.strategies = strategies;
    }

    public static RestartStrategy of(RestartStrategy first,
            RestartStrategy... others) {
        List<RestartStrategy> strategies = new ArrayList<RestartStrategy>(
                others.length + 1);
        strategies.add(first);
        Collections.addAll(strategies, others);
        return new MixedRestartsStrategy(strategies);
    }

    @Override
    public void reset() {
        for (RestartStrategy strategy : strategies) {
            strategy.reset();
        }
    }

    @Override
    public void newConflict() {
        for (RestartStrategy strategy : strategies) {
            strategy.newConflict();
        }
    }

    @Override
    public void init(SearchParams params, SolverStats stats) {
        for (RestartStrategy strategy : strategies) {
            strategy.init(params, stats);
        }
    }

    @Override
    public boolean shouldRestart() {
        for (RestartStrategy strategy : strategies) {
            if (strategy.shouldRestart()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onRestart() {
        for (RestartStrategy strategy : strategies) {
            strategy.onRestart();
        }
    }

    @Override
    public void onBackjumpToRootLevel() {
        for (RestartStrategy strategy : strategies) {
            strategy.onBackjumpToRootLevel();
        }
    }

    @Override
    public void newLearnedClause(Constr learned, int trailLevel) {
        for (RestartStrategy strategy : strategies) {
            strategy.newLearnedClause(learned, trailLevel);
        }
    }

    @Override
    public String toString() {
        return "Mixed Restarts strategy of: " + strategies;
    }

}
