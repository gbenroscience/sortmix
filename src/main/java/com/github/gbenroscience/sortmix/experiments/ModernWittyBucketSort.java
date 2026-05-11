package com.github.gbenroscience.sortmix.experiments;

/**
 *
 * @author GBEMIRO
 */ 

import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import java.util.Arrays;

/**
 * ModernWittyBucketSort
 * 
 * Optimized for cache-locality and minimal memory footprint.
 * Features:
 * - O(1) Auxiliary Space: Always uses a fixed 256-integer header.
 * - Strength Reduction: Multiplication-based indexing.
 * - TLB-Friendly: Fixed bucket count prevents memory mapping stalls.
 */
public class ModernWittyBucketSort {

    private static final int MAX_INSERTION_SORT = 32;
    private static final int BUCKET_COUNT = 256; 

    /**
     * Primary entry point.
     * @param array 
     */
    public static void sort(double[] array) {
        if (array == null || array.length < 2) return;
        sortRecursive(array, 0, array.length - 1);
    }

    private static void sortRecursive(double[] array, int left, int right) {
        int len = right - left + 1;

        // Base case: Insertion Sort for small subarrays
        if (len <= MAX_INSERTION_SORT) {
            insertionSort(array, left, right);
            return;
        }

        // 1. Find Extremities (One pass, O(n))
        double min = array[left];
        double max = array[left];
        for (int i = left + 1; i <= right; i++) {
            if (array[i] < min) min = array[i];
            else if (array[i] > max) max = array[i];
        }

        double range = max - min;
        if (range <= 0) return; // Array is already uniform

        // 2. Setup Fixed-Size Headers
        // Using 256 ensures we stay in L1 cache and TLB limits
        int[] count = new int[BUCKET_COUNT];
        int[] offset = new int[BUCKET_COUNT];
        
        // Strength Reduction: Pre-calculate the multiplier for scaling
        // Index calculation: index = (value - min) * scale
        double scale = (double) (BUCKET_COUNT - 1) / range;

        // 3. Counting Pass
        for (int i = left; i <= right; i++) {
            count[getIndex(array[i], min, scale)]++;
        }

        // 4. Calculate Offsets (Prefix Sum)
        offset[0] = left;
        for (int i = 1; i < BUCKET_COUNT; i++) {
            offset[i] = offset[i - 1] + count[i - 1];
        }

        // 5. In-Place Routing (The Permutation Loop)
        // We use 'offset' to place elements and 'count' to track remaining work per bucket.
        // This is highly cache-efficient because only 256 pointers are active.
        for (int b = 0; b < BUCKET_COUNT; b++) {
            while (count[b] > 0) {
                int currIdx = offset[b];
                double val = array[currIdx];
                int destBucket = getIndex(val, min, scale);

                if (destBucket == b) {
                    offset[b]++;
                    count[b]--;
                } else {
                    // Swap logic to route element to its correct bucket
                    int targetPos = offset[destBucket];
                    array[currIdx] = array[targetPos];
                    array[targetPos] = val;
                    
                    // Decrement work for the target bucket, increment its write pointer
                    offset[destBucket]++;
                    count[destBucket]--;
                }
            }
        }

        // 6. Hierarchical Recursion
        // Scan the partitioned array for bucket boundaries and recurse
        int p = left;
        while (p <= right) {
            int bucketStart = p;
            int bucketId = getIndex(array[p], min, scale);
            
            // Advance p to find the end of the current bucket
            while (p <= right && getIndex(array[p], min, scale) == bucketId) {
                p++;
            }
            
            // Only recurse if the bucket has more than one element
            if (p - bucketStart > 1) {
                sortRecursive(array, bucketStart, p - 1);
            }
        }
    }

    /**
     * Map value to a bucket index using pre-calculated scale.
     */
    private static int getIndex(double val, double min, double scale) {
        return (int) ((val - min) * scale);
    }

    /**
     * Cache-friendly Insertion Sort for the leaves of the recursion tree.
     */
    private static void insertionSort(double[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            double key = a[i];
            int j = i - 1;
            while (j >= lo && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

 
    static double[]masterData = null;
    
    public static void main(String[] args) {
        int n = 24_000_000;
        System.out.println("Benchmarking with " + n + " elements...\n");

        // Generate master data

         masterData = BenchMarker.initArrayPos$NegRandomFloats(n);
        
        System.out.println("DATA-TYPE----initArrayPos$NegRandomFloats");

        // Run benchmarks
        runBenchmark("ModernWittyBucketSort", () -> sort(clone(masterData)));
        runBenchmark("AdvancedWittyBucketSort", () -> AdvancedWittyBucketSort.sort(clone(masterData)));
        runBenchmark("QuickSort", () -> QuickSort.sort(clone(masterData)));
        runBenchmark("MergeSort", () -> MergeSort.sort(clone(masterData)));
        runBenchmark("java.util.Arrays.sort", () -> Arrays.sort(clone(masterData))); 
        
        masterData = BenchMarker.initArrayRandomFloats(n);
        
        System.out.println("DATA-TYPE----initArrayRandomFloats");

        // Run benchmarks
        runBenchmark("ModernWittyBucketSort", () -> sort(clone(masterData)));
        runBenchmark("AdvancedWittyBucketSort", () -> AdvancedWittyBucketSort.sort(clone(masterData)));
        runBenchmark("QuickSort", () -> QuickSort.sort(clone(masterData)));
        runBenchmark("MergeSort", () -> MergeSort.sort(clone(masterData)));
        runBenchmark("java.util.Arrays.sort", () -> Arrays.sort(clone(masterData))); 
        
        
        
        masterData = BenchMarker.initArrayReverseSortedFloats(n);
        
        System.out.println("DATA-TYPE----initArrayReverseSortedFloats");

        // Run benchmarks
        runBenchmark("ModernWittyBucketSort", () -> sort(clone(masterData)));
        runBenchmark("AdvancedWittyBucketSort", () -> AdvancedWittyBucketSort.sort(clone(masterData)));
        runBenchmark("QuickSort", () -> QuickSort.sort(clone(masterData)));
        runBenchmark("MergeSort", () -> MergeSort.sort(clone(masterData)));
        runBenchmark("java.util.Arrays.sort", () -> Arrays.sort(clone(masterData))); 
        
        
        masterData = BenchMarker.initArraySortedFloats(n);
        
        System.out.println("DATA-TYPE----initArraySortedFloats");

        // Run benchmarks
        runBenchmark("ModernWittyBucketSort", () -> sort(clone(masterData)));
        runBenchmark("AdvancedWittyBucketSort", () -> AdvancedWittyBucketSort.sort(clone(masterData)));
        runBenchmark("QuickSort", () -> QuickSort.sort(clone(masterData)));
        runBenchmark("MergeSort", () -> MergeSort.sort(clone(masterData)));
        runBenchmark("java.util.Arrays.sort", () -> Arrays.sort(clone(masterData))); 
        
        
        masterData = BenchMarker.initBinaryArrayFloats(n, 10, 20);
        
        System.out.println("DATA-TYPE----initBinaryArrayFloats");
        
        // Run benchmarks
        runBenchmark("ModernWittyBucketSort", () -> sort(clone(masterData)));
        runBenchmark("AdvancedWittyBucketSort", () -> AdvancedWittyBucketSort.sort(clone(masterData)));
        runBenchmark("QuickSort", () -> QuickSort.sort(clone(masterData)));
        runBenchmark("MergeSort", () -> MergeSort.sort(clone(masterData)));
        runBenchmark("java.util.Arrays.sort", () -> Arrays.sort(clone(masterData))); 
        
        
                
        
        masterData = BenchMarker.initPartiallySortedArrayFloats(n, n/2);
        
        System.out.println("DATA-TYPE----initPartiallySortedArrayFloats");
        
        // Run benchmarks
        runBenchmark("ModernWittyBucketSort", () -> sort(clone(masterData)));
        runBenchmark("AdvancedWittyBucketSort", () -> AdvancedWittyBucketSort.sort(clone(masterData)));
        runBenchmark("QuickSort", () -> QuickSort.sort(clone(masterData)));
        runBenchmark("MergeSort", () -> MergeSort.sort(clone(masterData)));
        runBenchmark("java.util.Arrays.sort", () -> Arrays.sort(clone(masterData))); 
        
        
        
        
        
        
        
        
        
        
    }

    private static void runBenchmark(String name, Runnable sortTask) {
        // 1. Suggest Garbage Collection to clear heap before start
        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // 2. Start Time
        long startTime = System.nanoTime();

        // 3. Execute Sort
        sortTask.run();

        // 4. End Time
        long endTime = System.nanoTime();
        
        // 5. Measure Memory immediately after
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        long durationMs = (endTime - startTime) / 1_000_000;
        long memoryUsedMb = (memoryAfter - memoryBefore) / (1024 * 1024);

        // Format and print results
        System.out.printf("%-25s | Time: %7d ms | Peak Memory Change: %4d MB%n", 
                          name, durationMs, Math.max(0, memoryUsedMb));
        System.out.println("-----------------------------------------------------------------------");
    }

    private static double[] clone(double[] original) {
        return Arrays.copyOf(original, original.length);
    }
}