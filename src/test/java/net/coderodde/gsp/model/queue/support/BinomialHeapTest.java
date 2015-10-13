package net.coderodde.gsp.model.queue.support;

import java.util.NoSuchElementException;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BinomialHeapTest {
    
    private static final long seed = System.currentTimeMillis();
    
    private final BinomialHeap<Integer> heap;
    
    public BinomialHeapTest() {
        this.heap = new BinomialHeap<>();
    }
    
    @BeforeClass
    public static void initClass() {
        System.out.println("BinomialHeapTest.java, seed: " + seed);
    }
    
    /**
     * Clears the heap before any test.
     */
    @Before
    public void init() {
        heap.clear();
    }
    
    @Test
    public void testAddAndExtractMinimum() {
        final int sz = 100000;
        final Random rnd = new Random(seed);
        
        for (int i = 0; i != sz; ++i) {
            Integer ii = rnd.nextInt();
            heap.add(ii, ii);
        }
        
        Integer prev = null;
        
        while (!heap.isEmpty()) {
            Integer current = heap.extractMinimum();
            
            if (prev != null && prev > current) {
                fail("The sequence was not monotonically increasing. " +
                     "Previous: " + prev + ", current: " + current + ".");
            }
            
            prev = current;
        }
    }
    
    @Test
    public void testDecreasePriority() {
        for (int i = 10; i != 0; --i) {
            heap.add(i, i);
        }
        
        heap.decreasePriority(10, -1);
        
        assertEquals((Integer) 10, heap.extractMinimum());
        
        int i = 1;
        while (!heap.isEmpty()) {
            assertEquals((Integer) i, heap.extractMinimum());
            i++;
        }
    }
    
    @Test
    public void testSize() {
        assertTrue(heap.isEmpty());
        
        final long sz = 10000;
        
        for (int i = 0; i < sz; ++i) {
            assertEquals(i, heap.size());
            heap.add(i, i);
        }
        
        assertEquals(sz, heap.size());
        assertFalse(heap.isEmpty());
    }
    
    @Test
    public void testIsEmpty() {
        assertTrue(heap.isEmpty());
        
        heap.add(0, 0);
        
        assertFalse(heap.isEmpty());
        
        heap.add(1, -1);
        
        assertFalse(heap.isEmpty());
        
        heap.extractMinimum();
        
        assertFalse(heap.isEmpty());
        
        heap.extractMinimum();
        
        assertTrue(heap.isEmpty());
        
        heap.add(100, 10);
        heap.add(10, 1);
        
        assertFalse(heap.isEmpty());
        
        heap.clear();
        
        assertTrue(heap.isEmpty());
    }
    
    @Test
    public void testClear() {
        assertTrue(heap.isEmpty());
        
        final int sz = 10000;
        
        for (int i = 0; i < sz; ++i) {
            heap.add(i, i);
            assertFalse(heap.isEmpty());
        }
        
        heap.clear();
        
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }
    
    @Test
    public void testSpawn() {
        heap.add(1, 2);
        
        BinomialHeap<Integer> heap2 = 
                (BinomialHeap<Integer>) heap.spawn();
        
        assertTrue(heap2 instanceof BinomialHeap);
        assertFalse(heap.isEmpty());
        assertTrue(heap2.isEmpty());
    }
    
    /**
     * Additional test.
     */
    @Test
    public void additionalTest() {
        heap.add(2, 2);
        heap.add(1, 1);
        heap.add(3, 7);
        heap.add(4, 6);
        heap.decreasePriority(3, -1);
        heap.decreasePriority(4, 10);
        heap.add(4, -4);
        
        assertEquals(4, heap.size());
        assertFalse(heap.isEmpty());
        
        assertEquals((Integer) 3, heap.extractMinimum());
        assertEquals((Integer) 1, heap.extractMinimum());
        assertEquals((Integer) 2, heap.extractMinimum());
        assertEquals((Integer) 4, heap.extractMinimum());
        
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testExtractingFromEmptyThrows() {
        heap.add(10, 10);
        heap.add(1, 29);
        
        heap.extractMinimum();
        heap.extractMinimum();
        heap.extractMinimum();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testPeekingEmptyHeapThrows() {
        heap.min();
    }
    
    @Test
    public void testMin() {
        heap.add(3, 3); // (3, 3)
        
        assertEquals((Integer) 3, heap.min());
        
        heap.add(2, 2); // (2, 2) (3, 3)
        
        assertEquals((Integer) 2, heap.min());
        
        assertEquals(2, heap.size());
        
        heap.decreasePriority(3, 1); // (3, 1) (2, 2)
        
        assertEquals((Integer) 3, heap.min());
        
        assertEquals(2, heap.size());
        
        assertEquals((Integer) 3, heap.extractMinimum()); // (2, 2)

        assertEquals(1, heap.size());
        
        assertEquals((Integer) 2, heap.min());
        
        assertEquals((Integer) 2, heap.extractMinimum());
        
        assertEquals(0, heap.size());
        
        try {
            heap.min();
            fail("BinomialHeap did not throw on being read while empty.");
        } catch (final NoSuchElementException nsee) {
            
        }
    }
}
