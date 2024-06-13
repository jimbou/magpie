package org.sat4j.maxsat;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public class TestMaxHSLikeSolver {

    private MaxHSLikeSolver pwms;
    
    @Before
    public void init() {
        this.pwms = new MaxHSLikeSolver(org.sat4j.pb.SolverFactory.newDefault(), org.sat4j.minisat.SolverFactory.newDefault());
    }
    
    @Test
    public void testOnePMS() throws ContradictionException, TimeoutException {
        pwms.newVar(4);
        pwms.addHardClause(new VecInt(new int[] {1,2,3,4}));
        pwms.addSoftClause(new VecInt(new int[] {-1,-2}));
        pwms.addSoftClause(new VecInt(new int[] {-1,2}));
        pwms.addSoftClause(new VecInt(new int[] {1,-3}));
        pwms.addSoftClause(new VecInt(new int[] {1,3}));
        assertTrue(pwms.isSatisfiable());
        assertEquals(pwms.violatedWeight(),BigInteger.ONE);
        
    }

    @Test
    public void testAnotherPMS() throws ContradictionException, TimeoutException {
        pwms.newVar(4);
        pwms.addHardClause(new VecInt(new int[] {1,2,3,4}));
        pwms.addSoftClause(new VecInt(new int[] {-1,-2}));
        pwms.addSoftClause(new VecInt(new int[] {1}));
        pwms.addSoftClause(new VecInt(new int[] {2}));
        pwms.addSoftClause(new VecInt(new int[] {-1,-3}));
        pwms.addSoftClause(new VecInt(new int[] {3}));
        assertTrue(pwms.isSatisfiable());
        assertEquals(pwms.violatedWeight(),BigInteger.ONE);
        
    }
    
    @Test
    public void testAnotherPMSRelaxingTwoConstraints() throws ContradictionException, TimeoutException {
        pwms.newVar(4);
        pwms.addHardClause(new VecInt(new int[] {1,2,3,4}));
        pwms.addSoftClause(new VecInt(new int[] {-1,-2}));
        pwms.addSoftClause(new VecInt(new int[] {1}));
        pwms.addSoftClause(new VecInt(new int[] {2}));
        pwms.addSoftClause(new VecInt(new int[] {-3,-4}));
        pwms.addSoftClause(new VecInt(new int[] {3}));
        pwms.addSoftClause(new VecInt(new int[] {4}));
        assertTrue(pwms.isSatisfiable());
        assertEquals(pwms.violatedWeight(),BigInteger.valueOf(2));        
    }
    
    @Test
    public void testOneWPMS() throws ContradictionException, TimeoutException {
        pwms.newVar(4);
        pwms.addHardClause(new VecInt(new int[] {1,2,3,4}));
        pwms.addSoftClause(1000,new VecInt(new int[] {-1,-2}));
        pwms.addSoftClause(500,new VecInt(new int[] {-1,2}));
        pwms.addSoftClause(200,new VecInt(new int[] {1,-3}));
        pwms.addSoftClause(100,new VecInt(new int[] {1,3}));
        assertTrue(pwms.isSatisfiable());
        assertEquals(pwms.violatedWeight(),BigInteger.valueOf(100));     
    }
    
}
