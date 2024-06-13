/**
 * 
 */
package org.sat4j.pb.restarts;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author wallon
 *
 */
public class BigIntegerCircularBuffer implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final BigInteger[] values;
    private int index = 0;
    private BigInteger sum = BigInteger.ZERO;
    private boolean full = false;

    public BigIntegerCircularBuffer(int capacity) {
        this.values = new BigInteger[capacity];
    }

    public void push(long value) {
        push(BigInteger.valueOf(value));
    }

    public void push(BigInteger value) {
        if (!this.full) {
            this.values[this.index++] = value;
            this.sum = sum.add(value);
            if (this.index == this.values.length) {
                this.full = true;
                this.index = -1;
            }
            return;
        }
        this.index++;
        if (this.index == this.values.length) {
            this.index = 0;
        }
        // buffer full, overwrite
        this.sum = sum.subtract(this.values[this.index]);
        this.values[this.index] = value;
        this.sum = sum.add(value);
    }

    public BigInteger average() {
        if (this.full) {
            return this.sum.divide(BigInteger.valueOf(this.values.length));
        }
        if (this.index == 0) {
            return BigInteger.ZERO;
        }
        return this.sum.divide(BigInteger.valueOf(this.index));
    }

    public void clear() {
        this.index = 0;
        this.full = false;
        this.sum = BigInteger.ZERO;
    }

    public boolean isFull() {
        return this.full;
    }
}
