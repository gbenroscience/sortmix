package com.github.gbenroscience.sortmix.experiments;

/**
 *
 * @author GBEMIRO
 */
import java.util.Arrays;
import java.util.function.ToDoubleFunction;

/**
 * ModernWittyBucketSort (Generic Edition)
 *
 * Optimized for cache-locality and minimal memory footprint. Features: - O(1)
 * Auxiliary Workspace for headers (256-integer structures). - High-speed
 * pointer routing using a localized Key-Array mapping cache.
 *
 * You can use it like this: 
 * User[] users = getMassiveUserArray();
 *
 * // Sorts 100% in place sorted by internal account balance metrics
 * ModernWittyBucketSort.sort(users, user -> user.getBalance());
 *
 *
 * @author GBEMIRO
 */
public class GenericModernWittyBucketSort {

    private static final int MAX_INSERTION_SORT = 32;
    private static final int BUCKET_COUNT = 256;

    /**
     * Primary entry point for Generic Sorting.
     *
     * @param <T> The type of elements to be sorted
     * @param array The destination target array containing object references
     * @param keyExtractor Functional expression mapping each object instance to
     * a double sort key
     */
    public static <T> void sort(T[] array, ToDoubleFunction<? super T> keyExtractor) {
        if (array == null || array.length < 2 || keyExtractor == null) {
            return;
        }

        // Phase 1: Handle null references gracefully by packing them to the extreme left
        int firstNonNull = partitionNulls(array);
        if (firstNonNull >= array.length - 1) {
            return; // Array contains only nulls or a single non-null object
        }

        // Phase 2: Build a primitive key cache to protect L1 cache lines from object pointer hopping
        double[] keys = new double[array.length];
        for (int i = firstNonNull; i < array.length; i++) {
            keys[i] = keyExtractor.applyAsDouble(array[i]);
        }

        // Phase 3: Recurse over the valid non-null elements
        sortRecursive(array, keys, firstNonNull, array.length - 1);
    }

    private static <T> void sortRecursive(T[] array, double[] keys, int left, int right) {
        int len = right - left + 1;

        // Base case: Insertion Sort for small subarrays
        if (len <= MAX_INSERTION_SORT) {
            insertionSort(array, keys, left, right);
            return;
        }

        // 1. Find Extremities and Check Sorted State (One pass, O(n))
        double min = keys[left];
        double max = keys[left];
        double v1 = min;
        double v2 = v1;
        boolean binaryOnly = true;
        int countV1 = 1;
        boolean isSorted = true;
        boolean isReverseSorted = true;

        // Guard 1: Detect if the initial element key is NaN
        boolean hasNaN = Double.isNaN(min);

        for (int i = left + 1; i <= right; i++) {
            double val = keys[i];

            // Guard 1 Cont.: Track if any NaNs exist in this range
            if (!hasNaN && Double.isNaN(val)) {
                hasNaN = true;
            }

            // Fast Binary Detection & Counting
            if (binaryOnly) {
                if (val == v1) {
                    countV1++;
                } else if (v1 == v2) {
                    v2 = val;
                } else if (val != v2) {
                    binaryOnly = false;
                }
            }

            if (val < min) {
                min = val;
                isSorted = false;
            } else if (val > max) {
                max = val;
                isReverseSorted = false;
            } else {
                if (isSorted && val < max) {
                    isSorted = false;
                }
                if (isReverseSorted && val > min) {
                    isReverseSorted = false;
                }
            }
        }

        // Guard 1 Resolution: Handle NaNs gracefully by partitioning them to the far right
        if (hasNaN) {
            int nanBoundary = partitionNaNs(array, keys, left, right);
            if (nanBoundary - left > 1) {
                sortRecursive(array, keys, left, nanBoundary - 1);
            }
            return;
        }

        // Early Exit 1: Already Sorted
        if (isSorted) {
            return;
        }

        // Early Exit 2: Reverse Sorted
        if (isReverseSorted) {
            int l = left;
            int r = right;
            while (l < r) {
                swap(array, keys, l++, r--);
            }
            return;
        }

        double range = max - min;
        if (range <= 0) {
            return; // Subarray keys are completely uniform
        }

        // Early Exit 4: True Binary Array Pointer Blitz
        if (binaryOnly) {
            double lowValue = (v1 < v2) ? v1 : v2;
            double highValue = (v1 < v2) ? v2 : v1;

            int countHigh = len - countV1;
            if (v1 == highValue) {
                countHigh = countV1;
            }

            // Binary object permutation tracking pointers
            int lowPtr = left;
            int highPtr = (right + 1) - countHigh;
            int scanPtr = left;

            // In-place object sorting bucket sweep
            while (scanPtr < highPtr) {
                if (keys[scanPtr] == lowValue) {
                    scanPtr++;
                } else {
                    swap(array, keys, scanPtr, highPtr);
                    highPtr++; // Lock element into position from the right
                    // Do not increment scanPtr, must evaluate swapped object key
                    highPtr = (highPtr - 2 < scanPtr) ? scanPtr : highPtr - 2;
                }
            }
            return;
        }

        // 2. Setup Fixed-Size Headers
        int[] count = new int[BUCKET_COUNT];
        int[] offset = new int[BUCKET_COUNT];

        // Strength Reduction: Pre-calculate the multiplier for scaling
        double scale = (double) (BUCKET_COUNT - 1) / range;

        // 3. Counting Pass
        for (int i = left; i <= right; i++) {
            count[getIndex(keys[i], min, scale)]++;
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
                double val = keys[currIdx];
                int destBucket = getIndex(val, min, scale);
                if (destBucket == b) {
                    offset[b]++;
                    count[b]--;
                } else {
                    int targetPos = offset[destBucket];
                    swap(array, keys, currIdx, targetPos);
                    offset[destBucket]++;
                    count[destBucket]--;
                }
            }
        }

        // 6. Hierarchical Recursion with Guard 2
        int p = left;
        while (p <= right) {
            int bucketStart = p;
            int bucketId = getIndex(keys[p], min, scale);

            while (p <= right && getIndex(keys[p], min, scale) == bucketId) {
                p++;
            }

            int bucketLen = p - bucketStart;
            if (bucketLen > 1) {
                if (keys[bucketStart] != keys[p - 1]) {
                    sortRecursive(array, keys, bucketStart, p - 1);
                }
            }
        }
    }

    /**
     * Segregates null values to the beginning of the array.
     *
     * @return Index location of the first non-null element reference.
     */
    private static <T> int partitionNulls(T[] array) {
        int nextAvailable = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                if (i != nextAvailable) {
                    T temp = array[i];
                    array[i] = array[nextAvailable];
                    array[nextAvailable] = temp;
                }
                nextAvailable++;
            }
        }
        return nextAvailable;
    }

    /**
     * In-place partitioning tool that pushes NaN values to the end of the
     * range.
     */
    private static <T> int partitionNaNs(T[] array, double[] keys, int left, int right) {
        int i = left;
        int j = right;
        while (i <= j) {
            if (Double.isNaN(keys[i])) {
                swap(array, keys, i, j);
                j--;
            } else {
                i++;
            }
        }
        return i;
    }

    private static int getIndex(double val, double min, double scale) {
        return (int) ((val - min) * scale);
    }

    private static <T> void swap(T[] array, double[] keys, int i, int j) {
        T tempObj = array[i];
        array[i] = array[j];
        array[j] = tempObj;

        double tempKey = keys[i];
        keys[i] = keys[j];
        keys[j] = tempKey;
    }

    /**
     * Cache-friendly Insertion Sort for generic subarrays.
     */
    private static <T> void insertionSort(T[] array, double[] keys, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            T keyObj = array[i];
            double keyVal = keys[i];
            int j = i - 1;
            while (j >= lo && keys[j] > keyVal) {
                array[j + 1] = array[j];
                keys[j + 1] = keys[j];
                j--;
            }
            array[j + 1] = keyObj;
            keys[j + 1] = keyVal;
        }
    }
}
