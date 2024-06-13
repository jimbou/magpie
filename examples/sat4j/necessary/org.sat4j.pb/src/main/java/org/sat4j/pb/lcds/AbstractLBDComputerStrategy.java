package org.sat4j.pb.lcds;

import org.sat4j.minisat.core.ILits;
import org.sat4j.pb.constraints.pb.PBConstr;

public abstract class AbstractLBDComputerStrategy
        implements ILBDComputerStrategy {

    protected int[] flags = new int[0];

    protected int flag = 0;

    @Override
    public void init(int howmany) {
        if (this.flags.length <= howmany) {
            this.flags = new int[howmany + 1];
        }
        this.flag = 0;
    }

    @Override
    public int computeLBD(ILits voc, PBConstr constr, int propagated) {
        int nblevel = 1;
        this.flag++;
        startComputeLBD(constr, propagated);
        for (int i = 0; i < constr.size(); i++) {
            int currentLevel = voc.getLevel(constr.get(i));
            if (currentLevel < 0) {
                nblevel += unassignedLiteral(voc, constr, i);
                continue;
            }
            if (this.flags[currentLevel] != this.flag) {
                this.flags[currentLevel] = this.flag;
                nblevel += assignedLiteral(voc, constr, i);
            }
        }
        return fixLbd(constr, nblevel);
    }

    protected void startComputeLBD(PBConstr constr, int propagated) {

    }

    protected abstract int unassignedLiteral(ILits voc, PBConstr constr, int i);

    protected abstract int assignedLiteral(ILits voc, PBConstr constr, int i);

    protected int fixLbd(PBConstr constr, int lbd) {
        return lbd;
    }

}
