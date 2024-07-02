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
package org.sat4j.pb.core;

import org.sat4j.minisat.core.IOrder;
import org.sat4j.minisat.core.LearningStrategy;
import org.sat4j.minisat.core.RestartStrategy;
import org.sat4j.minisat.core.SearchParams;
import org.sat4j.pb.constraints.pb.AutoDivisionStrategy;
import org.sat4j.pb.constraints.pb.ConflictMapReduceToClause;
import org.sat4j.pb.constraints.pb.IConflict;
import org.sat4j.pb.constraints.pb.IWeakeningStrategy;
import org.sat4j.pb.constraints.pb.NoPreProcess;
import org.sat4j.pb.constraints.pb.PBConstr;
import org.sat4j.pb.constraints.pb.PostProcessToCard;
import org.sat4j.pb.constraints.pb.SkipStrategy;

public class PBSolverCPCardLearning extends PBSolverCPLong {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PBSolverCPCardLearning(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, IOrder order) {
        super(learner, dsf, order);
    }

    public PBSolverCPCardLearning(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            RestartStrategy restarter) {
        super(learner, dsf, params, order, restarter);
    }

    public PBSolverCPCardLearning(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order) {
        super(learner, dsf, params, order);
    }

    public PBSolverCPCardLearning(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, IOrder order, boolean noRemove,
            SkipStrategy skipAllow) {
        super(learner, dsf, order, noRemove, skipAllow);
    }

    public PBSolverCPCardLearning(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            RestartStrategy restarter, boolean noRemove,
            SkipStrategy skipAllow) {
        super(learner, dsf, params, order, restarter, noRemove, skipAllow);
    }

    public PBSolverCPCardLearning(
            LearningStrategy<PBDataStructureFactory> learner,
            PBDataStructureFactory dsf, SearchParams params, IOrder order,
            boolean noRemove, SkipStrategy skipAllow) {
        super(learner, dsf, params, order, noRemove, skipAllow);
    }

    @Override
    protected IConflict chooseConflict(PBConstr myconfl, int level) {
        return ConflictMapReduceToClause.createConflict(myconfl, level,
                isNoRemove(), isSkipAllow(), NoPreProcess.instance(),
                PostProcessToCard.instance(),
                IWeakeningStrategy.UNASSIGNED_FIRST,
                AutoDivisionStrategy.ENABLED, pbStats);
    }

    @Override
    public String toString(String prefix) {
        return super.toString(prefix) + "\n" + prefix
                + "Performs a post-processing after conflict analysis in order to learn only cardinality constraints (Dixon's procedure)";
    }

}
