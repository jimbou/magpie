package org.sat4j.pb.multiobjective;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.sat4j.pb.multiobjective.MinSumOWAOptimizer.PermutationComputer;

public class PermutationIteratorTest {

    private Iterator<List<Integer>> iterator;

    @Before
    public void setup() {
        iterator = new MinSumOWAOptimizer.PermutationComputer(3).iterator();
    }

    @Test
    public void testExpectedBehavior() {
        for (int i = 0; i < 6; i++) {
            assertTrue(iterator.hasNext());
            assertNotNull(iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testExceptionWhenNoMoreSolutions() {
        for (int i = 0; i < 6; i++) {
            assertTrue(iterator.hasNext());
            assertNotNull(iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertNotNull(iterator.next());
    }

    @Test
    public void testPermutationComputer() {
        PermutationComputer p = new PermutationComputer(3);
        Set<List<Integer>> actual = StreamSupport.stream(p.spliterator(), false).collect(Collectors.toSet());
        Set<List<Integer>> expected = Stream.of(
            Stream.of(0, 1, 2).collect(Collectors.toList()),
            Stream.of(0, 2, 1).collect(Collectors.toList()),
            Stream.of(1, 0, 2).collect(Collectors.toList()),
            Stream.of(1, 2, 0).collect(Collectors.toList()),
            Stream.of(2, 0, 1).collect(Collectors.toList()),
            Stream.of(2, 1, 0).collect(Collectors.toList())
        ).collect(Collectors.toSet());
        assertEquals(expected, actual);
    }

}
