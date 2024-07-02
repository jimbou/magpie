package org.sat4j.pb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sat4j.pb.tools.CombinationIterator;

public class CombinationIteratorTest {

    private Iterator<Set<Integer>> iterator;

    @Before
    public void setup() {
        iterator = new CombinationIterator(2, 3).IntSetIterator();
    }

    @Test
    public void testExpectedBehavior() {
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testExceptionWhenNoMoreSolutions() {
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertFalse(iterator.hasNext());
        assertNotNull(iterator.next());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCombSizeIsNotEnough() {
        new CombinationIterator(0, 3).IntSetIterator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCombSizeIsTooMuch() {
        new CombinationIterator(4, 3).IntSetIterator();
    }
}
