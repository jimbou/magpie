/**
 * 
 */
package org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

/**
 * @author wallon
 *
 */
public enum DivisionStrategy {

    /**
     * 
     */
    FULL_WEAKENING {

        @Override
        public BigInteger[] divide(BigInteger a, BigInteger b) {
            BigInteger[] tmp = a.divideAndRemainder(b);
            if (tmp[1].signum() == 0) {
                return tmp;
            }

            tmp[0] = BigInteger.ZERO;
            tmp[1] = a;
            return tmp;
        }

    },

    /**
     * 
     */
    PARTIAL_WEAKENING {

        @Override
        public BigInteger[] divide(BigInteger a, BigInteger b) {
            return a.divideAndRemainder(b);
        }

    };

    public abstract BigInteger[] divide(BigInteger a, BigInteger b);

}
