package org.sat4j.pb.tools;

import org.sat4j.pb.IPBSolverService;

public class VoidPBTracing implements PBSearchListener<IPBSolverService> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "none";
    }
}
