package org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

import org.sat4j.minisat.core.ILits;

/**
 * Strategy used to reduce a PB constraint before applying the canceling
 * addition operation.
 * 
 * @author leberre
 * @since 2.3.6
 *
 */
public interface IWeakeningStrategy {
    IWeakeningStrategy UNASSIGNED_FIRST = new IWeakeningStrategy() {
        @Override
        public int findLiteralToRemove(ILits voc, IWatchPb wpb,
                final BigInteger[] coefsBis, final int indLitImplied,
                final BigInteger degreeBis) {
            int lit = -1;
            int size = wpb.size();
            for (int ind = 0; ind < size && lit == -1; ind++) {
                if (coefsBis[ind].signum() != 0
                        && voc.isUnassigned(wpb.get(ind))) {
                    assert coefsBis[ind].compareTo(degreeBis) < 0;
                    lit = ind;
                }
            }

            if (lit == -1) {
                for (int ind = 0; ind < size && lit == -1; ind++) {
                    if (coefsBis[ind].signum() != 0
                            && voc.isSatisfied(wpb.get(ind))
                            && ind != indLitImplied) {
                        lit = ind;
                    }
                }
            }
            return lit;
        }

        @Override
        public String toString() {
            return "Weaken first using unassigned literals";
        }
    };

    IWeakeningStrategy SATISFIED_FIRST = new IWeakeningStrategy() {
        @Override
        public int findLiteralToRemove(ILits voc, IWatchPb wpb,
                final BigInteger[] coefsBis, final int indLitImplied,
                final BigInteger degreeBis) {
            int lit = -1;
            int size = wpb.size();
            for (int ind = 0; ind < size && lit == -1; ind++) {
                if (coefsBis[ind].signum() != 0
                        && voc.isSatisfied(wpb.get(ind))) {
                    assert coefsBis[ind].compareTo(degreeBis) < 0;
                    lit = ind;
                }
            }

            if (lit == -1) {
                for (int ind = 0; ind < size && lit == -1; ind++) {
                    if (coefsBis[ind].signum() != 0
                            && voc.isUnassigned(wpb.get(ind))
                            && ind != indLitImplied) {
                        lit = ind;
                    }
                }
            }
            return lit;
        }

        @Override
        public String toString() {
            return "Weaken first using satisfied literals";
        }
    };

    IWeakeningStrategy ANY = new IWeakeningStrategy() {
        @Override
        public int findLiteralToRemove(ILits voc, IWatchPb wpb,
                final BigInteger[] coefsBis, final int indLitImplied,
                final BigInteger degreeBis) {
            int lit = -1;
            int size = wpb.size();
            for (int ind = 0; ind < size && lit == -1; ind++) {
                if (coefsBis[ind].signum() != 0
                        && !voc.isFalsified(wpb.get(ind))
                        && ind != indLitImplied) {
                    assert coefsBis[ind].compareTo(degreeBis) < 0;
                    lit = ind;
                }
            }

            return lit;
        }

        @Override
        public String toString() {
            return "Weaken with no priority";
        }
    };

    int findLiteralToRemove(ILits voc, IWatchPb wpb,
            final BigInteger[] coefsBis, final int indLitImplied,
            final BigInteger degreeBis);
}
