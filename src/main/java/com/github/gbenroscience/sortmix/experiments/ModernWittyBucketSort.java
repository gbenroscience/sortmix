package com.github.gbenroscience.sortmix.experiments;

import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.BINARY_ARRAY_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.PARTIALLY_SORTED_ARRAY_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.POS_AND_NEG_RANDOM_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.RANDOM_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.REVERSE_SORTED_FLOATS;
import static com.github.gbenroscience.sortmix.experiments.JMHWars.SORTED_FLOATS;
import com.github.gbenroscience.sortmix.util.SortUtils;

import java.util.Arrays;

/**
 * ModernWittyBucketSort
 *
 * <p>This is the scalar sibling of {@link SimdWittyBucketSort}. The two
 * classes are structurally identical -- same {@code Workspace} reuse
 * strategy, same fixed-boundary permutation scheme, same clamped
 * {@link #getIndex}, same separation of concerns between scan / classify /
 * route / recurse -- so that the only remaining difference between them is
 * whether the hot loops use {@code jdk.incubator.vector} or plain scalar
 * code. That makes a direct benchmark comparison between the two classes an
 * actual measurement of what SIMD buys you here, rather than a comparison
 * confounded by an unrelated allocation-pattern change.
 *
 * <h2>What changed from the original {@code ModernWittyBucketSort}</h2>
 * The original allocated fresh {@code count[]} and {@code offset[]}
 * (256-int each) on <i>every</i> recursive call. For a large array that
 * recurses down to insertion-sort-sized leaves, that's a large number of
 * small array allocations, and it showed up directly in benchmarks: ~215
 * MB/op allocated, against ~80 MB/op for just cloning the input array
 * (i.e. ~135 MB/op of pure recursion-tree churn), with a correspondingly
 * wide error bar consistent with GC-pause variance. This revision adopts
 * the same fix {@link SimdWittyBucketSort} uses: a {@link Workspace}
 * allocated once per top-level {@link #sort}, handing out one reusable pair
 * of 256-int headers per recursion <i>depth</i> (reused across sibling
 * subproblems at that depth) instead of one pair per recursive <i>call</i>.
 * The permutation loop was also changed so bucket boundaries are derived
 * once from the prefix sum and never overwritten during routing, which
 * removes the need for a second full-array scan afterward to rediscover
 * them (the original's step 6). Binary detection remains its own scalar
 * pass, unchanged in spirit from the original -- it's inherently
 * data-dependent branching, not something either version tries to make
 * fancier.
 *
 * <p>Bucket-index computation is also clamped to {@code [0, BUCKET_COUNT - 1]}
 * (see {@link #getIndex}), matching {@link SimdWittyBucketSort}: cheap
 * insurance against a floating-point edge case indexing one slot outside
 * the 256-int headers, in what is the hottest loop in this class.
 *
 * @author GBEMIRO
 */
public class ModernWittyBucketSort {

    private static final int MAX_INSERTION_SORT = 32;
    private static final int BUCKET_COUNT = 256;

    /**
     * Per-sort reusable workspace. Same design as the private {@code
     * Workspace} in {@link SimdWittyBucketSort} -- see that class for the
     * full rationale -- minus the lane-batch scratch buffer that only the
     * SIMD classification path needs.
     *
     * <p><b>Not thread-safe, and not safe for parallel recursion.</b> This
     * reuse scheme is only correct because {@code sortRecursive} is a
     * strictly sequential depth-first traversal: at any moment, at most one
     * call is "live" at a given depth, and a parent's headers are never
     * read again once its children start (the parent has already used
     * {@code count[]} to compute every child's boundaries before the first
     * recursive call at that depth is made). Parallelizing sibling bucket
     * recursion would require giving each concurrently-running call its own
     * headers, not sharing this depth-indexed one.</p>
     */
    private static final class Workspace {

        private int[][] countByDepth;
        private int[][] offsetByDepth;
        private double[][] scanStateByDepth;
        private double[][] binaryStateByDepth;
        private int[][] binaryCountByDepth;

        Workspace() {
            countByDepth = new int[8][];
            offsetByDepth = new int[8][];
            scanStateByDepth = new double[8][];
            binaryStateByDepth = new double[8][];
            binaryCountByDepth = new int[8][];
        }

        int[] count(int depth) {
            ensureDepth(depth);
            int[] a = countByDepth[depth];
            if (a == null) {
                a = new int[BUCKET_COUNT];
                countByDepth[depth] = a;
            }
            return a;
        }

        int[] offset(int depth) {
            ensureDepth(depth);
            int[] a = offsetByDepth[depth];
            if (a == null) {
                a = new int[BUCKET_COUNT];
                offsetByDepth[depth] = a;
            }
            return a;
        }

        /** scanState[0] = min, scanState[1] = max, scanState[2] = flags. */
        double[] scanState(int depth) {
            ensureDepth(depth);
            double[] a = scanStateByDepth[depth];
            if (a == null) {
                a = new double[3];
                scanStateByDepth[depth] = a;
            }
            return a;
        }

        /**
         * Holds only {@code v1} and {@code v2} for the binary-array fast
         * path. The element count (an exact integer) is NOT stored here --
         * see {@link #binaryCount(int)}.
         */
        double[] binaryState(int depth) {
            ensureDepth(depth);
            double[] a = binaryStateByDepth[depth];
            if (a == null) {
                a = new double[2];
                binaryStateByDepth[depth] = a;
            }
            return a;
        }

        /**
         * Holds {@code countV1} for the binary-array fast path as a real
         * {@code int}, kept separate from {@link #binaryState(int)}'s
         * doubles so that "count fits exactly in a double" is never an
         * invariant a future edit could quietly break.
         */
        int[] binaryCount(int depth) {
            ensureDepth(depth);
            int[] a = binaryCountByDepth[depth];
            if (a == null) {
                a = new int[1];
                binaryCountByDepth[depth] = a;
            }
            return a;
        }

        private void ensureDepth(int depth) {
            if (depth < countByDepth.length) {
                return;
            }

            int newSize = countByDepth.length;
            while (newSize <= depth) {
                newSize <<= 1;
            }

            countByDepth = Arrays.copyOf(countByDepth, newSize);
            offsetByDepth = Arrays.copyOf(offsetByDepth, newSize);
            scanStateByDepth = Arrays.copyOf(scanStateByDepth, newSize);
            binaryStateByDepth = Arrays.copyOf(binaryStateByDepth, newSize);
            binaryCountByDepth = Arrays.copyOf(binaryCountByDepth, newSize);
        }
    }

    /**
     * Primary entry point.
     *
     * @param array the array to sort in place; null or length < 2 is a no-op
     */
    public static void sort(double[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        Workspace workspace = new Workspace();
        sortRecursive(array, 0, array.length - 1, workspace, 0);
    }

    private static void runChecks(double[] array) {
        //ANALYTICS--remove these before production use or before running benchmarks
        double[] stateData = SortUtils.checkSortState(array, 0, array.length - 1);
        System.out.println("ARRAY IS " + (stateData[2] == 0 ? "SORTED." : "NOT SORTED."));
    }

    /*
     * Scalar counterpart of SimdWittyBucketSort.scanInto: identical
     * algorithm (adjacent-pair comparisons for sortedness, running min/max,
     * NaN presence, all in one pass), just without vector instructions.
     * Writes into a workspace-owned scanState array instead of returning a
     * record, matching the SIMD version's calling convention exactly.
     *
     * scanState[0] = min
     * scanState[1] = max
     * scanState[2] = flags: bit 0 = hasNaN, bit 1 = isSorted, bit 2 = isReverseSorted
     */
    private static void scanInto(double[] array, int left, int right, double[] scanState) {
        int len = right - left + 1;

        double min = array[left];
        double max = array[left];
        boolean hasNaN = Double.isNaN(min);
        boolean isSorted = true;
        boolean isReverseSorted = true;

        for (int idx = 0; idx < len; idx++) {
            double val = array[left + idx];

            if (Double.isNaN(val)) {
                hasNaN = true;
            } else {
                if (val < min) {
                    min = val;
                }
                if (val > max) {
                    max = val;
                }
            }

            if (idx < len - 1) {
                double next = array[left + idx + 1];
                if (Double.isNaN(val) || Double.isNaN(next)) {
                    isSorted = false;
                    isReverseSorted = false;
                } else {
                    if (val > next) {
                        isSorted = false;
                    }
                    if (val < next) {
                        isReverseSorted = false;
                    }
                }
            }
        }

        int flags = 0;
        if (hasNaN) {
            flags |= 1;
        }
        if (isSorted) {
            flags |= 2;
        }
        if (isReverseSorted) {
            flags |= 4;
        }

        scanState[0] = min;
        scanState[1] = max;
        scanState[2] = flags;
    }

    private static void sortRecursive(double[] array, int left, int right, Workspace workspace, int depth) {
        int len = right - left + 1;

        // Base case: Insertion Sort for small subarrays
        if (len <= MAX_INSERTION_SORT) {
            insertionSort(array, left, right);
            return;
        }

        // 1. One reusable scan state: min, max, NaN presence, sorted / reverse-sorted.
        // A single scanState array cannot be shared across recursion because child
        // calls would overwrite parent state, so it's kept depth-local via the workspace.
        double[] scanState = workspace.scanState(depth);
        scanInto(array, left, right, scanState);

        double min = scanState[0];
        double max = scanState[1];
        int flags = (int) scanState[2];

        boolean hasNaN = (flags & 1) != 0;
        boolean isSorted = (flags & 2) != 0;
        boolean isReverseSorted = (flags & 4) != 0;

        // Guard 1 Resolution: Handle NaNs gracefully by partitioning them to the far right
        if (hasNaN) {
            int nanBoundary = partitionNaNs(array, left, right);
            // Recurse only on the valid numeric portion remaining on the left
            if (nanBoundary - left > 1) {
                sortRecursive(array, left, nanBoundary - 1, workspace, depth + 1);
            }
            return;
        }

        // Early Exit 1: Already Sorted
        if (isSorted) {
            return;
        }

        // Early Exit 2: Reverse Sorted
        if (isReverseSorted) {
            reverseRange(array, left, right);
            return;
        }

        double range = max - min;
        if (range <= 0) {
            return; // Array is already uniform
        }

        // Early Exit 4: True Binary Array Pointer Blitz (O(n) Time, O(1) Memory).
        // Distinct-value tracking is inherently sequential/branchy, kept as its own scalar pass.
        double[] binaryState = workspace.binaryState(depth);
        int[] binaryCount = workspace.binaryCount(depth);

        if (detectBinary(array, left, right, binaryState, binaryCount)) {
            double v1 = binaryState[0];
            double v2 = binaryState[1];
            int countV1 = binaryCount[0];

            double lowValue = Math.min(v1, v2);
            double highValue = Math.max(v1, v2);

            int countHigh = len - countV1;
            if (v1 == highValue) {
                countHigh = countV1;
            }

            int highBoundary = (right + 1) - countHigh;

            fillRange(array, left, highBoundary, lowValue);
            fillRange(array, highBoundary, right + 1, highValue);
            return;
        }

        // 2. Fixed-size headers, reused across sibling calls at this depth
        //    (see Workspace) instead of allocated fresh on every call.
        int[] count = workspace.count(depth);
        int[] offset = workspace.offset(depth);

        Arrays.fill(count, 0);

        // Strength Reduction: Pre-calculate the multiplier for scaling
        double scale = (double) (BUCKET_COUNT - 1) / range;

        // 3. Counting Pass
        for (int i = left; i <= right; i++) {
            count[getIndex(array[i], min, scale)]++;
        }

        // 4. Calculate Offsets (Prefix Sum). count[] is turned into
        //    end-exclusive bucket boundaries here and, unlike the original,
        //    deliberately NOT mutated during routing below -- offset[]
        //    alone tracks the current write/read position. That means the
        //    boundaries survive the permutation and don't need to be
        //    rediscovered with a second full-array scan afterward.
        count[0] += left;
        for (int b = 1; b < BUCKET_COUNT; b++) {
            count[b] += count[b - 1];
        }

        offset[0] = left;
        for (int b = 1; b < BUCKET_COUNT; b++) {
            offset[b] = count[b - 1];
        }

        // 5. In-Place Routing (The Permutation Loop)
        for (int b = 0; b < BUCKET_COUNT; b++) {
            while (offset[b] < count[b]) {
                int currIdx = offset[b];
                double val = array[currIdx];
                int destBucket = getIndex(val, min, scale);
                if (destBucket == b) {
                    offset[b]++;
                } else {
                    // Swap logic to route element to its correct bucket
                    int targetPos = offset[destBucket];
                    double targetVal = array[targetPos];

                    array[targetPos] = val;
                    array[currIdx] = targetVal;

                    offset[destBucket]++;
                }
            }
        }

        // 6. Hierarchical Recursion with Guard 2. Bucket boundaries come
        //    straight from count[] (the fixed prefix sum) -- no rescan needed.
        int bucketStart = left;
        for (int b = 0; b < BUCKET_COUNT; b++) {
            int bucketEndExclusive = count[b];
            int bucketLen = bucketEndExclusive - bucketStart;

            if (bucketLen > 1) {
                // Guard 2: Prevent Infinite Loop / StackOverflow on large duplicate clusters.
                // If the first and last elements of the bucket are identical, the entire bucket
                // consists of duplicate elements. Recursing it would process the same range infinitely.
                if (array[bucketStart] != array[bucketEndExclusive - 1]) {
                    sortRecursive(array, bucketStart, bucketEndExclusive - 1, workspace, depth + 1);
                }
            }

            bucketStart = bucketEndExclusive;
        }
    }

    /**
     * Allocation-free binary-value detection.
     *
     * <p>{@code state[0] = v1}, {@code state[1] = v2}. The count of
     * {@code v1} is returned via {@code countHolder[0]} rather than packed
     * into the double-typed state array -- countV1 is an exact element
     * count, and storing it as a genuine {@code int} avoids relying on
     * "small enough to round-trip through a double" as an invariant a
     * future edit could quietly break.</p>
     */
    private static boolean detectBinary(double[] array, int left, int right, double[] state, int[] countHolder) {
        double v1 = array[left];
        double v2 = v1;
        int countV1 = 1;
        boolean binaryOnly = true;

        for (int i = left + 1; i <= right; i++) {
            double val = array[i];
            if (val == v1) {
                countV1++;
            } else if (v1 == v2) {
                v2 = val;
            } else if (val != v2) {
                binaryOnly = false;
                break;
            }
        }

        state[0] = v1;
        state[1] = v2;
        countHolder[0] = countV1;

        return binaryOnly;
    }

    /**
     * In-place partitioning tool that pushes all NaN values to the end of the
     * array segment. Matches standard Java sorting conventions where NaNs are
     * placed last.
     *
     * @return the index of the first NaN element.
     */
    private static int partitionNaNs(double[] array, int left, int right) {
        int i = left;
        int j = right;
        while (i <= j) {
            if (Double.isNaN(array[i])) {
                // Swap the NaN with the element at index j
                double temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                j--;
            } else {
                i++;
            }
        }
        return i; // Everything from this index to 'right' is now cleanly a NaN
    }

    /**
     * Map value to a bucket index using pre-calculated scale.
     *
     * <p>Clamped to {@code [0, BUCKET_COUNT - 1]}. Mathematically, for any
     * {@code val} in {@code [min, max]}, {@code (val - min) * scale} lands
     * in {@code [0, BUCKET_COUNT - 1]} by construction (scale is derived
     * from the same min/max). In practice this holds to within
     * floating-point rounding for ordinary data, but the clamp is here so
     * that no pathological input (extreme magnitudes, values sitting right
     * at the range boundary) can ever throw an
     * ArrayIndexOutOfBoundsException out of the hottest loop in this class.
     * The branch is essentially free: it is almost never taken, so the CPU
     * predicts it correctly every time.</p>
     */
    private static int getIndex(double val, double min, double scale) {
        int idx = (int) ((val - min) * scale);
        if (idx < 0) {
            return 0;
        }
        if (idx >= BUCKET_COUNT) {
            return BUCKET_COUNT - 1;
        }
        return idx;
    }

    /** Scalar broadcast-fill of array[from .. to) with value. */
    private static void fillRange(double[] array, int from, int to, double value) {
        for (int i = from; i < to; i++) {
            array[i] = value;
        }
    }

    /** In-place reversal of array[left..right] (O(n/2) swaps). */
    private static void reverseRange(double[] array, int left, int right) {
        int l = left;
        int r = right;
        while (l < r) {
            double temp = array[l];
            array[l++] = array[r];
            array[r--] = temp;
        }
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
        if (arraySize < 50_000_000) {
            runBenchmark("AdvancedWittyBucketSort", () -> AdvancedWittyBucketSort.sort(clone(md)));

            runBenchmark("MergeSort", () -> MergeSort.sort(clone(md)));
            runBenchmark("HeapSort", () -> HeapSort.sort(clone(md)));
        }
        runBenchmark("QuickSort", () -> QuickSort.sort(clone(md)));
        runBenchmark("java.util.Arrays.sort", () -> Arrays.sort(clone(md)));
        runBenchmark("ModernWittyBucketSort", () -> sort(clone(md)));

    }

    public static void main(String[] args) {

        int[] dataSizes = {/*1000, 10_000, 100_000, 1_000_000, 10_000_000, */100_000_000};

        for (int n : dataSizes) {
            System.out.println("==========".repeat(10));
            System.out.println("Benchmarking with " + n + " elements...\n");

            runner(n, JMHWars.POS_AND_NEG_RANDOM_FLOATS);
            runner(n, JMHWars.RANDOM_FLOATS);
            runner(n, JMHWars.REVERSE_SORTED_FLOATS);
            runner(n, JMHWars.SORTED_FLOATS);
            runner(n, JMHWars.PARTIALLY_SORTED_ARRAY_FLOATS);

            runner(n, JMHWars.BINARY_ARRAY_FLOATS);
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