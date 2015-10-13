package net.coderodde.gsp.model.queue.support;

import java.util.NoSuchElementException;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class DaryHeapTest {
    
    private static final long seed = System.currentTimeMillis();
    
    @BeforeClass
    public static void initClass() {
        System.out.println("DaryHeapTest.java, seed: " + seed);
    }
    
    @Test
    public void testAdd() {
        testAddOn(new DaryHeap<>(), seed);
        testAddOn(new DaryHeap<>(3), seed);
        testAddOn(new DaryHeap<>(4), seed);
        testAddOn(new DaryHeap<>(5), seed);
    }

    @Test
    public void testDecreasePriority() {
        testDecreasePriorityOn(new DaryHeap<>());
        testDecreasePriorityOn(new DaryHeap<>(3));
        testDecreasePriorityOn(new DaryHeap<>(4));
        testDecreasePriorityOn(new DaryHeap<>(5));
    }

    @Test
    public void testExtractMinimum() {
        testExtractMinimumOn(new DaryHeap<>());
        testExtractMinimumOn(new DaryHeap<>(3));
        testExtractMinimumOn(new DaryHeap<>(4));
        testExtractMinimumOn(new DaryHeap<>(5));
    }

    @Test
    public void testSize() {
        testSizeOn(new DaryHeap<>());
        testSizeOn(new DaryHeap<>(3));
        testSizeOn(new DaryHeap<>(4));
        testSizeOn(new DaryHeap<>(5));
    }
    
    @Test
    public void testIsEmpty() {
        testIsEmptyOn(new DaryHeap<>());
        testIsEmptyOn(new DaryHeap<>(3));
        testIsEmptyOn(new DaryHeap<>(4));
        testIsEmptyOn(new DaryHeap<>(5));
    }

    @Test
    public void testClear() {
        testClearOn(new DaryHeap<>());
        testClearOn(new DaryHeap<>(3));
        testClearOn(new DaryHeap<>(4));
        testClearOn(new DaryHeap<>(5));
    }

    @Test
    public void testSpawn() {
        testSpawnOn(new DaryHeap<>());
        testSpawnOn(new DaryHeap<>(3));
        testSpawnOn(new DaryHeap<>(4));
        testSpawnOn(new DaryHeap<>(5));
    }
    
    @Test
    public void testPeekOn() {
        testPeekOn(new DaryHeap<>(2));
        testPeekOn(new DaryHeap<>(3));
        testPeekOn(new DaryHeap<>(4));
        testPeekOn(new DaryHeap<>(5));
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testPeekingEmptyHeapThrows2() {
        new DaryHeap<Integer>(2).min();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testPeekingEmptyHeapThrows3() {
        new DaryHeap<Integer>(3).min();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testPeekingEmptyHeapThrows4() {
        new DaryHeap<Integer>(4).min();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testPeekingEmptyHeapThrows5() {
        new DaryHeap<Integer>(5).min();
    }
    
    @Test
    public void testMin() {
        testMinOn(new DaryHeap<>(2));
        testMinOn(new DaryHeap<>(3));
        testMinOn(new DaryHeap<>(4));
        testMinOn(new DaryHeap<>(5));
    }
    
    private void testAddOn(DaryHeap<Float> heap, long seed) {
        final int sz = 100000;
        final Random rnd = new Random(seed);
        
        for (int i = 0; i != sz; ++i) {
            final Float f = rnd.nextFloat();
            heap.add(f, f);
        }
        
        Float prev = null;
        
        while (!heap.isEmpty()) {
            Float current = heap.extractMinimum();
            
            if (prev != null && prev > current) {
                fail("The sequence was not monotonically increasing. " +
                     "Previous: " + prev + ", current: " + current + ".");
            }
            
            prev = current;
        }
    }
    
    private void testDecreasePriorityOn(DaryHeap<Integer> heap) {
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
    
    private void testExtractMinimumOn(DaryHeap<Integer> heap) {
        final int sz = 10000;
        final Random rnd = new Random(seed);
        
        for (int i = 0; i < sz; ++i) {
            Integer e = rnd.nextInt();
            heap.add(e, e);
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
    
    private void testSizeOn(DaryHeap<Integer> heap) {
        assertTrue(heap.isEmpty());
        
        final long sz = 10000;
        
        for (int i = 0; i < sz; ++i) {
            assertEquals(i, heap.size());
            heap.add(i, i);
        }
        
        assertEquals(sz, heap.size());
        assertFalse(heap.isEmpty());
    }
    
    private void testIsEmptyOn(DaryHeap<Integer> heap) {
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
    
    private void testClearOn(DaryHeap<Integer> heap) {
        assertTrue(heap.isEmpty());
        
        final int sz = 10000;
        
        for (int i = 0; i < sz; ++i) {
            heap.add(i, i);
            assertFalse(heap.isEmpty());
        }
        
        heap.clear();
        
        assertTrue(heap.isEmpty());
    }
    
    private void testSpawnOn(DaryHeap<Integer> heap) {
        heap.add(1, 2);
       
        DaryHeap<Integer> heap2 = (DaryHeap<Integer>) heap.spawn();
       
        assertTrue(heap2 instanceof DaryHeap);
        assertEquals(heap.getDegree(), heap2.getDegree());
        assertFalse(heap.isEmpty());
        assertTrue(heap2.isEmpty());
    }
    
    private void testPeekOn(DaryHeap<Integer> heap) {
        heap.add(3, 3);
        
        assertEquals((Integer) 3, heap.min());
        
        heap.add(2, 2);
        
        assertEquals((Integer) 2, heap.min());
        
        assertEquals(2, heap.size());
        
        heap.decreasePriority(3, 1);
        
        assertEquals((Integer) 3, heap.min());
        
        assertEquals(2, heap.size());
    }
    
    private void testMinOn(DaryHeap<Integer> heap) {
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
        } catch (NoSuchElementException nsee) {
            
        }
    }
}
