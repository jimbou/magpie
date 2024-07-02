/*
 * This file is a part of the fr.univartois.cril.orpheus.preprocessing.utils package.
 * 
 * It contains the BitMatrix, which implements a matrix of bits based on a BitSet.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package org.sat4j.pb.constraints.pb;

import java.util.BitSet;

/**
 * The BitMatrix implements a matrix of bits based on a {@link BitSet}.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class BitMatrix {

    /**
     * The number of rows in this matrix.
     */
    private final int nbRows;

    /**
     * The number of columns in this matrix.
     */
    private final int nbColumns;

    /**
     * The BitSet containing the bits of this matrix.
     */
    private final BitSet matrix;

    /**
     * Creates a new BitMatrix.
     * 
     * @param nbRows
     *            The number of rows in the matrix.
     * @param nbColumns
     *            The number of columns in the matrix.
     */
    public BitMatrix(int nbRows, int nbColumns) {
        this.nbRows = nbRows;
        this.nbColumns = nbColumns;
        this.matrix = new BitSet(nbRows * nbColumns);
    }

    /**
     * Sets the value of the bit at the specified position in this matrix.
     * 
     * @param row
     *            The row of the bit.
     * @param column
     *            The column of the bit.
     * @param value
     *            The value to set.
     * 
     * @throws IndexOutOfBoundsException
     *             If either {@code row} or {@code column} is out of the bounds
     *             of this matrix.
     */
    public void set(int row, int column, boolean value) {
        matrix.set(toIndex(row, column), value);
    }

    /**
     * Gives the value of the bit at the specified position in this matrix.
     * 
     * @param row
     *            The row of the bit.
     * @param column
     *            The column of the bit.
     * 
     * @return The value of the bit.
     * 
     * @throws IndexOutOfBoundsException
     *             If either {@code row} or {@code column} is out of the bounds
     *             of this matrix.
     */
    public boolean get(int row, int column) {
        return matrix.get(toIndex(row, column));
    }

    /**
     * Translates a bit position in this matrix into an index.
     * 
     * @param row
     *            The row of the bit.
     * @param column
     *            The column of the bit.
     * 
     * @return The index of the bit in {@link #matrix}.
     * 
     * @throws IndexOutOfBoundsException
     *             If either {@code row} or {@code column} is out of the bounds
     *             of this matrix.
     */
    private int toIndex(int row, int column) {
        return (row * nbColumns) + column;
    }

}
