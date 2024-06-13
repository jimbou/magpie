package org.sat4j.pb.orders;

import org.sat4j.minisat.core.ILits;
import org.sat4j.minisat.core.IOrder;
import org.sat4j.pb.constraints.pb.PBConstr;

public enum Bumper implements IBumper {

    ANY {
        @Override
        boolean isBumpable(ILits voc, int i, int p) {
            return true;
        }
    },

    ASSIGNED {
        @Override
        boolean isBumpable(ILits voc, int i, int p) {
            return !voc.isUnassigned(i);
        }
    },

    FALSIFIED {
        @Override
        boolean isBumpable(ILits voc, int i, int p) {
            return voc.isFalsified(i);
        }
    },

    FALSIFIED_AND_PROPAGATED {
        @Override
        boolean isBumpable(ILits voc, int i, int p) {
            return voc.isFalsified(i) || (voc.getLevel(i) == voc.getLevel(p));
        }
    };

    public void varBumpActivity(ILits voc, BumpStrategy bumpStrategy,
            IOrder order, PBConstr constr, int i, int propagated,
            boolean conflicting) {
        if (isBumpable(voc, constr.get(i), constr.get(propagated))) {
            bumpStrategy.varBumpActivity(order, constr, i);
        }
    }

    @Override
    public void postBumpActivity(IOrder order, PBConstr constr) {
        // Everything has already been done.
    }

    abstract boolean isBumpable(ILits voc, int i, int p);

}
