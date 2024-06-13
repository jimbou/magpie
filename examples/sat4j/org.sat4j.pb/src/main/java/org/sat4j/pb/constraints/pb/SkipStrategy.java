/**
 * 
 */
package org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

import org.sat4j.core.VecInt;

/**
 * @author wallon
 *
 */
public enum SkipStrategy {

    NO_SKIP {
        @Override
        public boolean skip(ConflictMap map, int litImplied) {
            return false;
        }

        @Override
        public String getDescription() {
            return "never skip resolution steps";
        }

    },

    SKIP {
        @Override
        public boolean skip(ConflictMap map, int litImplied) {
            int nLitImplied = litImplied ^ 1;
            if (map.weightedLits.get(nLitImplied).negate()
                    .compareTo(map.slackConflict()) > 0) {
                int litLevel = ConflictMap
                        .levelToIndex(map.voc.getLevel(litImplied));
                map.byLevel[litLevel].remove(nLitImplied);
                if (map.byLevel[0] == null) {
                    map.byLevel[0] = new VecInt();
                }
                map.byLevel[0].push(nLitImplied);
                return true;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "skip resolution steps when possible";
        }
    },

    WEAKEN_AND_SKIP {

        @Override
        public boolean skip(ConflictMap map, int litImplied) {
            int nLitImplied = litImplied ^ 1;
            if (map.weightedLits.get(nLitImplied).negate()
                    .compareTo(map.slackConflict()) > 0) {
                BigInteger coef = map.weightedLits.get(nLitImplied);
                map.removeCoef(nLitImplied);
                map.degree = map.degree.subtract(coef);
                map.saturation();
                return true;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "weaken literals when skipping resolution";
        }
    };

    public abstract boolean skip(ConflictMap map, int litImplied);

    public abstract String getDescription();

}
