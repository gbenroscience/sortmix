package com.github.gbenroscience.sortmix.experiments;

/**
 *
 * @author GBEMIRO
 */
import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.BINARY_ARRAY_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.INPUT_SIZE;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.PARTIALLY_SORTED_ARRAY_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.POS_AND_NEG_RANDOM_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.RANDOM_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.REVERSE_SORTED_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.SORTED_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.dataType;
import com.github.gbenroscience.sortmix.util.SortUtils;
import java.util.Arrays;

/**
 * ModernWittyBucketSort
 *
 * Optimized for cache-locality and minimal memory footprint. Features: - O(1)
 * Auxiliary Space: Always uses a fixed 256-integer header. - Strength
 * Reduction: Multiplication-based indexing. - TLB-Friendly: Fixed bucket count
 * prevents memory mapping stalls.
 */
public class ModernWittyBucketSort {

    private static final int MAX_INSERTION_SORT = 32;
    private static final int BUCKET_COUNT = 256;

    /**
     * Primary entry point.
     *
     * @param array
     */
    public static void sort(double[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        sortRecursive(array, 0, array.length - 1);
    }

    private static void runChecks(double[] array) {
        //ANALYTICS--remove these before production use or before running benchmarks
        double stateData[] = SortUtils.checkSortState(array, 0, array.length - 1);
        System.out.println("ARRAY IS " + (stateData[2] == 0 ? "SORTED." : "NOT SORTED."));
    }

    private static void sortRecursive(double[] array, int left, int right) {
        int len = right - left + 1;

        // Base case: Insertion Sort for small subarrays
        if (len <= MAX_INSERTION_SORT) {
            insertionSort(array, left, right);
            return;
        }

        // 1. Find Extremities and Check Sorted State (One pass, O(n))
        double min = array[left];
        double max = array[left];
        boolean isSorted = true; // Assume sorted until proven otherwise
        boolean isReverseSorted = true;

        for (int i = left + 1; i <= right; i++) {
            double val = array[i];

            if (val < min) {
                min = val;
                isSorted = false;        // Dropped below global minimum
            } else if (val > max) {
                max = val;
                isReverseSorted = false; // Spiked above global maximum
            } else {
                // Value is between current min and max. Did it break a local trend?
                if (isSorted && val < max) {
                    isSorted = false;
                }
                if (isReverseSorted && val > min) {
                    isReverseSorted = false;
                }
            }
        }

        // Early Exit 1: Already Sorted
        if (isSorted) {
            return;
        }

        // Early Exit 2: Reverse Sorted
        if (isReverseSorted) {
            // Flip the array in-place (O(n/2) swaps)
            int l = left;
            int r = right;
            while (l < r) {
                double temp = array[l];
                array[l++] = array[r];
                array[r--] = temp;
            }
            return;
        }
        double range = max - min;
        if (range <= 0) {
            return; // Array is already uniform
        }

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

    static void runner(int arraySize, int dataType) {
        double[] masterData = null;
        switch (dataType) {
            case BINARY_ARRAY_FLOATS:
                masterData = BenchMarker.initBinaryArrayFloats(arraySize, 10, 20);
                System.out.println("BINARY_ARRAY_FLOATS");
                break;
            case PARTIALLY_SORTED_ARRAY_FLOATS:
                masterData = BenchMarker.initPartiallySortedArrayFloats(arraySize, arraySize / 2);
                System.out.println("PARTIALLY_SORTED_ARRAY_FLOATS");
                break;
            case POS_AND_NEG_RANDOM_FLOATS:
                masterData = BenchMarker.initArrayPos$NegRandomFloats(arraySize);
                System.out.println("POS_AND_NEG_RANDOM_FLOATS");
                break;
            case RANDOM_FLOATS:
                masterData = BenchMarker.initArrayRandomFloats(arraySize);
                System.out.println("RANDOM_FLOATS");
                break;
            case REVERSE_SORTED_FLOATS:
                masterData = BenchMarker.initArrayReverseSortedFloats(arraySize);
                System.out.println("REVERSE_SORTED_FLOATS");
                break;
            case SORTED_FLOATS:
                masterData = BenchMarker.initArraySortedFloats(arraySize);
                System.out.println("SORTED_FLOATS");
                break;
            default:
                throw new AssertionError();
        }

        final double[] md = masterData;

        runBenchmark("AdvancedWittyBucketSort", () -> AdvancedWittyBucketSort.sort(clone(md)));
        runBenchmark("QuickSort", () -> QuickSort.sort(clone(md)));
        if (arraySize < 100_000_000) {
            runBenchmark("MergeSort", () -> MergeSort.sort(clone(md)));
            runBenchmark("HeapSort", () -> HeapSort.sort(clone(md)));
        }
        runBenchmark("java.util.Arrays.sort", () -> Arrays.sort(clone(md)));
        runBenchmark("ModernWittyBucketSort", () -> sort(clone(md)));

    }

    public static void main(String[] args) {

        int dataSizes[] = {/*1000, 10_000, 100_000, 1_000_000, 10_000_000, */100_000_000};

        for (int i = 0; i < dataSizes.length; i++) {
            int n = dataSizes[i];

            System.out.println("==========".repeat(10));
            System.out.println("Benchmarking with " + n + " elements...\n");
            
            
            runner(n, JMHWars.POS_AND_NEG_RANDOM_FLOATS);
            runner(n, JMHWars.RANDOM_FLOATS);
            runner(n, JMHWars.REVERSE_SORTED_FLOATS);
            runner(n, JMHWars.SORTED_FLOATS);
            runner(n, JMHWars.BINARY_ARRAY_FLOATS);
            runner(n, JMHWars.PARTIALLY_SORTED_ARRAY_FLOATS);
            System.out.println("Done benchmarking with " + n + " elements...\n");
            System.out.println("==========".repeat(10));
            
             
        }

    }

    private static void runBenchmark(String name, Runnable sortTask) {
        // 1. Suggest Garbage Collection to clear heap before start
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

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
