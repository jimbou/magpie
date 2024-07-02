package org.sat4j.pb.lcds;

import org.sat4j.minisat.core.ILits;
import org.sat4j.pb.constraints.pb.PBConstr;

public interface ILBDComputerStrategy {

    void init(int howmany);

    int computeLBD(ILits voc, PBConstr constr, int propagated);

}