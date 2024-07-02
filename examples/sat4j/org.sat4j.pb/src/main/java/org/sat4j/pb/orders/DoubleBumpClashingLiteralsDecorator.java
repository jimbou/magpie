package org.sat4j.pb.orders;

import org.sat4j.minisat.core.ILits;
import org.sat4j.minisat.core.IOrder;
import org.sat4j.pb.constraints.pb.PBConstr;

public class DoubleBumpClashingLiteralsDecorator implements IBumper {

    private final IBumper decorated;

    public DoubleBumpClashingLiteralsDecorator(IBumper decorated) {
        this.decorated = decorated;
    }

    @Override
    public void varBumpActivity(ILits voc, BumpStrategy bumpStrategy,
            IOrder order, PBConstr constr, int i, int propagated,
            boolean conflicting) {
        decorated.varBumpActivity(voc, bumpStrategy, order, constr, i,
                propagated, conflicting);
        if (conflicting)
            decorated.varBumpActivity(voc, bumpStrategy, order, constr, i,
                    propagated, conflicting);
    }

    @Override
    public void postBumpActivity(IOrder order, PBConstr constr) {
        decorated.postBumpActivity(order, constr);
    }

}
