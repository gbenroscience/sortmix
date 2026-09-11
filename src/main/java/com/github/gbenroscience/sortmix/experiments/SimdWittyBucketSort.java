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

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorShape;
import jdk.incubator.vector.VectorShuffle;
import jdk.incubator.vector.VectorSpecies;

/**
 * SimdWittyBucketSort
 *
 * SIMD-accelerated Witty Bucket Sort with reusable per-sort workspace.
 *
 * <p>
 * The main optimization in this revision is allocation reduction: recursive
 * calls no longer allocate the two 256-int bookkeeping arrays ({@code count[]}
 * and {@code offset[]}) or a lane-sized temporary array. Instead, a small
 * workspace is allocated once for the whole top-level sort and one pair of
 * 256-int headers is reserved per active recursion depth. Headers are therefore
 * reused by sibling subproblems rather than allocated once for every node in
 * the recursion tree.</p>
 *
 * <p>
 * The permutation bookkeeping was also changed so that the original bucket
 * boundaries remain available after routing: {@code count[b]} stores the
 * end-exclusive boundary of bucket {@code b}, while {@code offset[b]} is the
 * current write/read position. This avoids needing another full set of bucket
 * boundaries.</p>
 *
 * <p>
 * Another important change is that the final SIMD bucket-run scan has been
 * removed. Bucket boundaries are already known from the histogram/prefix-sum
 * phase, so rescanning the entire routed segment to rediscover those boundaries
 * was redundant work.</p>
 *
 * <p>
 * The deliberately scalar parts remain scalar: binary-value detection, NaN
 * partitioning, in-place permutation routing, and insertion-sort leaves. The
 * permutation is still a data-dependent scatter/swap operation and is not made
 * SIMD merely for appearance.</p>
 *
 * <p>
 * Hardening applied on top of the above, each verified not to add measurable
 * cost to the hot paths: bucket-index computation (both the scalar
 * {@link #getIndex} and the SIMD path in {@link #classifyAndCount}) is clamped
 * to {@code [0, BUCKET_COUNT - 1]} as cheap insurance against a floating-point
 * edge case landing one slot outside the valid range; binary-detection's
 * element count is stored as a genuine {@code int} instead of being packed into
 * the double-typed workspace slot; and the per-depth {@link Workspace} reuse
 * now carries an explicit warning about the sequential-recursion assumption it
 * depends on.</p>
 *
 * @author GBEMIRO
 */
public class SimdWittyBucketSort {

    private static final int MAX_INSERTION_SORT = 32;
    private static final int BUCKET_COUNT = 256;

    /**
     * Preferred double lane width for this CPU.
     */
    private static final VectorSpecies<Double> DSPEC = DoubleVector.SPECIES_PREFERRED;

    /**
     * Int species with the same lane count as the double species. Used as the
     * D2I conversion target.
     */
    private static final VectorSpecies<Integer> ISPEC_MATCHING_DOUBLE
            = VectorSpecies.of(int.class,
                    VectorShape.forBitSize(DSPEC.length() * Integer.SIZE));

    /**
     * Per-sort reusable workspace.
     *
     * <p>
     * Each recursion depth owns one pair of 256-int headers. The arrays are
     * allocated lazily only when that depth is first reached, then reused for
     * every sibling subproblem at that depth. This is intentionally much
     * smaller than allocating headers for every recursive node.</p>
     *
     * <p>
     * The lane buffer is allocated once and reused by all classification calls
     * because classification completes before recursion descends.</p>
     *
     * <p>
     * <b>Not thread-safe, and not safe for parallel recursion.</b> This reuse
     * scheme is only correct because {@code sortRecursive} is a strictly
     * sequential depth-first traversal: at any moment, at most one call is
     * "live" at a given depth, and a parent's headers are never read again once
     * its children start (the parent has already used {@code count[]} to
     * compute every child's boundaries before the first recursive call at that
     * depth is made). If a future change parallelizes sibling bucket recursion
     * -- a natural thing to want for a bucket sort at this scale -- two threads
     * could claim the same depth's {@code count}/{@code offset} arrays
     * simultaneously and silently corrupt each other's routing. Parallelizing
     * this recursion safely requires giving each concurrently-running call its
     * own headers (e.g. one {@code Workspace} per thread, or one per in-flight
     * call), not sharing this depth-indexed one.</p>
     */
    private static final class Workspace {

        private int[][] countByDepth;
        private int[][] offsetByDepth;
        private double[][] scanStateByDepth;
        private double[][] binaryStateByDepth;
        private int[][] binaryCountByDepth;

        private final int[] lane = new int[DSPEC.length()];

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
         * Holds only {@code v1} and {@code v2} for the binary-array fast path.
         * The element count (an exact integer) is NOT stored here -- see
         * {@link #binaryCount(int)}.
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
         * {@code int}, kept separate from {@link #binaryState(int)}'s doubles
         * so that "count fits exactly in a double" is never an invariant a
         * future edit could quietly break.
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
        // ANALYTICS--remove these before production use or before running benchmarks
        double[] stateData = SortUtils.checkSortState(array, 0, array.length - 1);
        System.out.println("ARRAY IS " + (stateData[2] == 0 ? "SORTED." : "NOT SORTED."));
    }

    /*
     * The allocation-free scan state used by sortRecursive.
     *
     * scanState[0] = min
     * scanState[1] = max
     * scanState[2] = flags:
     *                bit 0 = hasNaN
     *                bit 1 = isSorted
     *                bit 2 = isReverseSorted
     */
    private static void scanInto(
            double[] array,
            int left,
            int right,
            double[] scanState) {

        final int len = right - left + 1;
        final int L = DSPEC.length();

        DoubleVector vMin = DoubleVector.broadcast(DSPEC, array[left]);
        DoubleVector vMax = vMin;

        boolean hasNaN = Double.isNaN(array[left]);
        boolean isSorted = true;
        boolean isReverseSorted = true;

        int idx = 0;

        final int pairBound = (len >= 2) ? DSPEC.loopBound(len - 1) : 0;

        for (; idx < pairBound; idx += L) {
            DoubleVector v
                    = DoubleVector.fromArray(DSPEC, array, left + idx);

            vMin = vMin.min(v);
            vMax = vMax.max(v);

            if (!hasNaN) {
                VectorMask<Double> nanMask
                        = v.compare(VectorOperators.NE, v);

                if (nanMask.anyTrue()) {
                    hasNaN = true;
                }
            }

            if (isSorted || isReverseSorted) {
                DoubleVector vNext
                        = DoubleVector.fromArray(DSPEC, array, left + idx + 1);

                if (isSorted
                        && !v.compare(VectorOperators.LE, vNext).allTrue()) {
                    isSorted = false;
                }

                if (isReverseSorted
                        && !v.compare(VectorOperators.GE, vNext).allTrue()) {
                    isReverseSorted = false;
                }
            }
        }

        final int fullBound = DSPEC.loopBound(len);

        for (; idx < fullBound; idx += L) {
            DoubleVector v
                    = DoubleVector.fromArray(DSPEC, array, left + idx);

            vMin = vMin.min(v);
            vMax = vMax.max(v);

            if (!hasNaN) {
                VectorMask<Double> nanMask
                        = v.compare(VectorOperators.NE, v);

                if (nanMask.anyTrue()) {
                    hasNaN = true;
                }
            }
        }

        double min = vMin.reduceLanes(VectorOperators.MIN);
        double max = vMax.reduceLanes(VectorOperators.MAX);

        for (; idx < len; idx++) {
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

    private static void sortRecursive(
            double[] array,
            int left,
            int right,
            Workspace workspace,
            int depth) {

        int len = right - left + 1;

        if (len <= MAX_INSERTION_SORT) {
            insertionSort(array, left, right);
            return;
        }

        /*
         * One reusable primitive scan state.
         *
         * A single scanState array cannot be shared across recursion because
         * child calls would overwrite parent state. Therefore we keep it
         * depth-local through the workspace.
         */
        double[] scanState = workspace.scanState(depth);

        scanInto(array, left, right, scanState);

        double min = scanState[0];
        double max = scanState[1];
        int flags = (int) scanState[2];

        boolean hasNaN = (flags & 1) != 0;
        boolean isSorted = (flags & 2) != 0;
        boolean isReverseSorted = (flags & 4) != 0;

        if (hasNaN) {
            int nanBoundary = partitionNaNs(array, left, right);

            if (nanBoundary - left > 1) {
                sortRecursive(
                        array, left, nanBoundary - 1, workspace, depth + 1);
            }
            return;
        }

        if (isSorted) {
            return;
        }

        if (isReverseSorted) {
            reverseRange(array, left, right);
            return;
        }

        double range = max - min;

        if (range <= 0) {
            return;
        }

        /*
         * Binary detection remains scalar because the two-value state is
         * inherently data-dependent.
         */
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

        int[] count = workspace.count(depth);
        int[] offset = workspace.offset(depth);

        /*
         * count[] is cleared once for this recursion depth.
         *
         * After the histogram, count[] is converted to end-exclusive bucket
         * boundaries. It is then deliberately NOT mutated during routing.
         * offset[] contains the current position for each bucket.
         *
         * Thus count[] remains available to define the child ranges after
         * permutation without requiring a third 256-int boundary array.
         */
        Arrays.fill(count, 0);

        double scale = (double) (BUCKET_COUNT - 1) / range;

        classifyAndCount(
                array, left, len, min, scale, count, workspace.lane);

        count[0] += left;

        for (int b = 1; b < BUCKET_COUNT; b++) {
            count[b] += count[b - 1];
        }

        offset[0] = left;

        for (int b = 1; b < BUCKET_COUNT; b++) {
            offset[b] = count[b - 1];
        }

        /*
         * In-place routing.
         *
         * count[b] = fixed end-exclusive boundary of bucket b.
         * offset[b] = current position in bucket b.
         *
         * Since count[] is no longer decremented, the bucket boundaries
         * survive the permutation.
         */
        for (int b = 0; b < BUCKET_COUNT; b++) {
            while (offset[b] < count[b]) {
                int currIdx = offset[b];
                double val = array[currIdx];

                int destBucket = getIndex(val, min, scale);

                if (destBucket == b) {
                    offset[b]++;
                } else {
                    int targetPos = offset[destBucket];

                    /*
                     * A valid permutation guarantees that a value destined
                     * for destBucket finds an unfilled position there.
                     */
                    double targetVal = array[targetPos];

                    array[targetPos] = val;
                    array[currIdx] = targetVal;

                    offset[destBucket]++;
                }
            }
        }

        /*
         * The old implementation performed another SIMD pass here to
         * rediscover bucket runs. That was unnecessary: count[] already holds
         * the exact bucket boundaries.
         *
         * Important recursion rule:
         * parent count[] belongs to this depth and must remain intact while
         * child recursion runs. Therefore each depth gets its own reusable
         * header pair. Siblings reuse the same pair after the previous sibling
         * returns.
         */
        int bucketStart = left;

        for (int b = 0; b < BUCKET_COUNT; b++) {
            int bucketEndExclusive = count[b];

            int bucketLen = bucketEndExclusive - bucketStart;

            if (bucketLen > 1) {
                if (array[bucketStart] != array[bucketEndExclusive - 1]) {
                    sortRecursive(
                            array,
                            bucketStart,
                            bucketEndExclusive - 1,
                            workspace,
                            depth + 1);
                }
            }

            bucketStart = bucketEndExclusive;
        }
    }

    /**
     * SIMD classification fused with histogram building.
     *
     * <p>
     * The lane buffer is supplied by the reusable per-sort workspace, so this
     * method performs no per-call allocation.</p>
     */
    private static void classifyAndCount(
            double[] array,
            int left,
            int len,
            double min,
            double scale,
            int[] count,
            int[] lane) {

        final int L = DSPEC.length();

        DoubleVector vMin = DoubleVector.broadcast(DSPEC, min);
        DoubleVector vScale = DoubleVector.broadcast(DSPEC, scale);

        int idx = 0;
        final int bound = DSPEC.loopBound(len);

        for (; idx < bound; idx += L) {
            DoubleVector v
                    = DoubleVector.fromArray(DSPEC, array, left + idx);

            DoubleVector scaled
                    = v.sub(vMin).mul(vScale);

            IntVector bucketIds
                    = (IntVector) scaled.convertShape(
                            VectorOperators.D2I,
                            ISPEC_MATCHING_DOUBLE,
                            0);

            // Same clamp as the scalar getIndex() fallback below, and kept
            // in sync with it for the same reason: cheap insurance against
            // a pathological (val - min) * scale landing outside
            // [0, BUCKET_COUNT - 1]. This branch-free min/max pair is
            // essentially free next to the convert it follows.
            bucketIds = bucketIds.max(0).min(BUCKET_COUNT - 1);

            bucketIds.intoArray(lane, 0);

            for (int k = 0; k < L; k++) {
                count[lane[k]]++;
            }
        }

        for (; idx < len; idx++) {
            count[getIndex(
                    array[left + idx],
                    min,
                    scale)]++;
        }
    }

    /**
     * Allocation-free binary-value detection.
     *
     * <p>
     * {@code state[0] = v1}, {@code state[1] = v2}. The count of {@code v1} is
     * returned via {@code countHolder[0]} rather than packed into the
     * double-typed state array -- countV1 is an exact element count, and
     * storing it as a genuine {@code int} avoids relying on "small enough to
     * round-trip through a double" as an invariant a future edit could quietly
     * break.</p>
     */
    private static boolean detectBinary(
            double[] array,
            int left,
            int right,
            double[] state,
            int[] countHolder) {

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
     * In-place partitioning tool that pushes all NaN values to the end.
     *
     * @return index of first NaN element
     */
    private static int partitionNaNs(
            double[] array,
            int left,
            int right) {

        int i = left;
        int j = right;

        while (i <= j) {
            if (Double.isNaN(array[i])) {
                double temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                j--;
            } else {
                i++;
            }
        }

        return i;
    }

    /**
     * Scalar fallback: map value to a bucket index.
     *
     * <p>
     * Clamped to {@code [0, BUCKET_COUNT - 1]}. Mathematically, for any
     * {@code val} in {@code [min, max]}, {@code (val - min) * scale} lands in
     * {@code [0, BUCKET_COUNT - 1]} by construction (scale is derived from the
     * same min/max). In practice this holds to within floating-point rounding
     * for ordinary data, but the clamp is here so that no pathological input
     * (extreme magnitudes, values sitting right at the range boundary) can ever
     * throw an ArrayIndexOutOfBoundsException out of the hottest loop in this
     * class. The branch is essentially free: it is almost never taken, so the
     * CPU predicts it correctly every time.</p>
     */
    private static int getIndex(
            double val,
            double min,
            double scale) {

        int idx = (int) ((val - min) * scale);

        if (idx < 0) {
            return 0;
        }
        if (idx >= BUCKET_COUNT) {
            return BUCKET_COUNT - 1;
        }
        return idx;
    }

    /**
     * SIMD broadcast-fill of array[from .. to) with value.
     */
    private static void fillRange(
            double[] array,
            int from,
            int to,
            double value) {

        int len = to - from;

        if (len <= 0) {
            return;
        }

        final int L = DSPEC.length();

        DoubleVector filler
                = DoubleVector.broadcast(DSPEC, value);

        int idx = 0;
        final int bound = DSPEC.loopBound(len);

        for (; idx < bound; idx += L) {
            filler.intoArray(array, from + idx);
        }

        for (; idx < len; idx++) {
            array[from + idx] = value;
        }
    }

    /**
     * SIMD in-place reversal of array[left..right].
     */
    private static void reverseRange(
            double[] array,
            int left,
            int right) {

        final int L = DSPEC.length();

        final VectorShuffle<Double> reverseLanes
                = VectorShuffle.fromOp(
                        DSPEC,
                        i -> DSPEC.length() - 1 - i);

        int l = left;
        int r = right;

        while (r - l + 1 >= 2 * L) {
            DoubleVector vLeft
                    = DoubleVector.fromArray(DSPEC, array, l);

            DoubleVector vRight
                    = DoubleVector.fromArray(
                            DSPEC, array, r - L + 1);

            DoubleVector vLeftRev
                    = vLeft.rearrange(reverseLanes);

            DoubleVector vRightRev
                    = vRight.rearrange(reverseLanes);

            vRightRev.intoArray(array, l);
            vLeftRev.intoArray(array, r - L + 1);

            l += L;
            r -= L;
        }

        while (l < r) {
            double temp = array[l];
            array[l++] = array[r];
            array[r--] = temp;
        }
    }

    /**
     * Cache-friendly insertion sort for recursion leaves.
     */
    private static void insertionSort(
            double[] a,
            int lo,
            int hi) {

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
                masterData
                        = BenchMarker.initBinaryArrayFloats(
                                arraySize, 10, 20);
                System.out.println("BINARY_ARRAY_FLOATS");
                break;

            case PARTIALLY_SORTED_ARRAY_FLOATS:
                masterData
                        = BenchMarker.initPartiallySortedArrayFloats(
                                arraySize, arraySize / 2);
                System.out.println("PARTIALLY_SORTED_ARRAY_FLOATS");
                break;

            case POS_AND_NEG_RANDOM_FLOATS:
                masterData
                        = BenchMarker.initArrayPos$NegRandomFloats(
                                arraySize);
                System.out.println("POS_AND_NEG_RANDOM_FLOATS");
                break;

            case RANDOM_FLOATS:
                masterData
                        = BenchMarker.initArrayRandomFloats(
                                arraySize);
                System.out.println("RANDOM_FLOATS");
                break;

            case REVERSE_SORTED_FLOATS:
                masterData
                        = BenchMarker.initArrayReverseSortedFloats(
                                arraySize);
                System.out.println("REVERSE_SORTED_FLOATS");
                break;

            case SORTED_FLOATS:
                masterData
                        = BenchMarker.initArraySortedFloats(
                                arraySize);
                System.out.println("SORTED_FLOATS");
                break;

            default:
                throw new AssertionError();
        }

        final double[] md = masterData;

        if (arraySize < 50_000_000) {
            runBenchmark(
                    "AdvancedWittyBucketSort",
                    () -> AdvancedWittyBucketSort.sort(clone(md)));

            runBenchmark(
                    "ModernWittyBucketSort",
                    () -> ModernWittyBucketSort.sort(clone(md)));

            runBenchmark(
                    "MergeSort",
                    () -> MergeSort.sort(clone(md)));

            runBenchmark(
                    "HeapSort",
                    () -> HeapSort.sort(clone(md)));
        }

        runBenchmark(
                "QuickSort",
                () -> QuickSort.sort(clone(md)));

        runBenchmark(
                "java.util.Arrays.sort",
                () -> Arrays.sort(clone(md)));

        runBenchmark(
                "SimdWittyBucketSort",
                () -> sort(clone(md)));
    }

    public static void main(String[] args) {

        int[] dataSizes = {
            /*1000, 10_000, 100_000, 1_000_000, 10_000_000, */
            100_000_000
        };

        for (int n : dataSizes) {
            System.out.println("==========".repeat(10));
            System.out.println(
                    "Benchmarking with " + n + " elements...\n");

            runner(n, JMHWars.POS_AND_NEG_RANDOM_FLOATS);
            runner(n, JMHWars.RANDOM_FLOATS);
            runner(n, JMHWars.REVERSE_SORTED_FLOATS);
            runner(n, JMHWars.SORTED_FLOATS);
            runner(n, JMHWars.PARTIALLY_SORTED_ARRAY_FLOATS);
            runner(n, JMHWars.BINARY_ARRAY_FLOATS);

            System.out.println(
                    "Done benchmarking with " + n + " elements...\n");
            System.out.println("==========".repeat(10));
        }
    }

    private static void runBenchmark(
            String name,
            Runnable sortTask) {

        System.gc();

        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

        Runtime runtime = Runtime.getRuntime();

        long memoryBefore
                = runtime.totalMemory() - runtime.freeMemory();

        long startTime = System.nanoTime();

        sortTask.run();

        long endTime = System.nanoTime();

        long memoryAfter
                = runtime.totalMemory() - runtime.freeMemory();

        long durationMs
                = (endTime - startTime) / 1_000_000;

        long memoryUsedMb
                = (memoryAfter - memoryBefore)
                / (1024 * 1024);

        System.out.printf(
                "%-25s | Time: %7d ms | Peak Memory Change: %4d MB%n",
                name,
                durationMs,
                Math.max(0, memoryUsedMb));

        System.out.println(
                "-----------------------------------------------------------------------");
    }

    private static double[] clone(double[] original) {
        return Arrays.copyOf(original, original.length);
    }
}
