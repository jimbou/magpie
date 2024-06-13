package org.sat4j.pb.lcds;

import java.math.BigInteger;

import org.sat4j.minisat.core.ILits;
import org.sat4j.pb.constraints.pb.PBConstr;

public class SlackLBDComputerStrategy implements ILBDComputerStrategy {

    @Override
    public void init(int howmany) {
        // TODO Auto-generated method stub

    }

    @Override
    public int computeLBD(ILits voc, PBConstr constr, int propagated) {
        BigInteger slack = constr.getSumCoefs().subtract(constr.getDegree());
        if (slack.bitLength() < Integer.SIZE) {
            return slack.intValue();
        }
        return Integer.MAX_VALUE;
    }

}
