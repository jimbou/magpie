package org.sat4j.pb.constraints.pb;

import org.sat4j.pb.core.PBSolverStats;

public interface IConflictFactory {

    IConflict createConflict(PBConstr cpb, int level, boolean noRemove,
            SkipStrategy skip, IPreProcess preprocess, IPostProcess postprocess,
            IWeakeningStrategy weakeningStrategy,
            AutoDivisionStrategy autoDivisionStrategy, PBSolverStats stats);
}
