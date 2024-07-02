package org.sat4j.pb.orders;

import org.sat4j.minisat.core.ILits;
import org.sat4j.minisat.core.IOrder;
import org.sat4j.pb.constraints.pb.PBConstr;

public interface IBumper {

    void varBumpActivity(ILits voc, BumpStrategy bumpStrategy, IOrder order,
            PBConstr constr, int i, int propagated, boolean conflicting);

    void postBumpActivity(IOrder order, PBConstr constr);

}
