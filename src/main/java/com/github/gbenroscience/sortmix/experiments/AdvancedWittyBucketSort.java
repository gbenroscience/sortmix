/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.gbenroscience.sortmix.experiments;

import static com.github.gbenroscience.sortmix.util.SortUtils.*;

/**
 * Powerful bucket-sort algorithm combining memory optimization with double
 * speed optimization to produce a high speed sort. Sorting an array of floating
 * points requires only an extra array of equal size and most importantly, of
 * ints, which raises the memory efficiency by almost 50%(it handles a max array
 * size of about 18E6 instead of about 12E6 that obtains when both are arrays of
 * floating-point numbers.) Optimizing speed occurs in 2 stages.
 * <ol>
 * <li> 3 stage cache optimization, which uses a quicksort partitioning stage
 * algorithm to bring similar elements close together. What this accomplishes is
 * to reduce cache-misses in the stages that compute the bucket sizes, an effect
 * which is readily observed in large arrays.
 * <li> Uses only 1 write in its bucket filling stage, instead of multiple
 * writes in an earlier version and 2 writes in a more advanced version.
 *
 * </ol>
 *
 * @author GBEMIRO
 */
public class AdvancedWittyBucketSort {

    /**
     * If the array contains negative and positive values, it partitions it into
     * negative and positive values and then stores the index of the last
     * negative value in this field.
     */
    static int negIndex = -1;
    static int recursions = -1;

    /**
     * When true, cache optimization code is used.
     */
    private static boolean cacheOptimized = true;

    public static void setCacheOptimized(boolean cacheOptimized) {
        AdvancedWittyBucketSort.cacheOptimized = cacheOptimized;
    }

    public static boolean isCacheOptimized() {
        return cacheOptimized;
    }

    /**
     *
     * @param x The array to be sorted
     * @param offset The offset from the beginning of the array, representing
     * the index of the element where sorting is to begin.
     * @param i The index of the element whose suggested suggestedIndexAt_i is
     * needed.
     * @param smallest The smallest element found in the portion of the array
     * that is to be sorted.
     * @param range The biggest element found in the portion of the array that
     * is to be sorted, minus the smallest element found in the same portion.
     * @param n The number of entries to be sorted minus 1.
     * @return the index suggested by the algorithm for the index_th entry of
     * this array.
     */
    private static int getSuggestedIndex(double x[], int offset, int i, double smallest, double range, int n) {
        return offset + (int) (((x[i] - smallest) * n) / range);
    }

    /**
     *
     * @param array the array to sort.
     */
    public static void sort(double[] array) {
        sort(array, 0, array.length - 1);
    }

    /**
     *
     * @param sortJob The array to be sorted.
     * @param left The starting index of the array where we start sorting.
     * @param right The ending index in the array.
     */
    public static void sort(double[] sortJob, int left, int right) {
        int len = right - left + 1;
        double extremities[] = checkSortState(sortJob, left, right);
        double smallest = extremities[0];
        double biggest = extremities[1];
        double range = biggest - smallest;
        int n = len - 1;
        boolean alreadySorted = extremities[2] == 0 ? true : false || (biggest == smallest);
        boolean hasNegValue = extremities[3] == 0;
        boolean hasPosValue = extremities[4] == 0;

        //biggest == smallest means that the array to be sorted has equal elements and as such is already sorted.
        ++recursions;
        if (alreadySorted) {
        }//end if 
        else {
            /**
             * Introduce some cache friendliness to the code.
             */
            if (len >= 1000000 && cacheOptimized) {
                recursivePartition(sortJob, left, right, 3);
            }
            if (recursions == 0 && (hasNegValue && hasPosValue)) {
                negIndex = partitionNegativesAndPositives(sortJob, left, right);
            }

            int[] bucketSizes = new int[len];

            /**
             * Stores the bucket sizes of suggested indices. For example if
             * bucketSizes[3] = 5; It means that the number of entries that have
             * a suggestedIndex of <code>suggestedIndices[3]</code> is 5. So say
             * sortJob[3] = 7; Entry 7 is the current element to be sorted.
             * suggestedIndices[3]=4; means that the algorithm suggests that 7
             * should be put at index 4. To know the number of entries that also
             * fall into this same bucket i.e.(have a suggestedIndex of 4), use
             * <code> int bucketSize = bucketSizes[4];</code> Its value will
             * tell you how many elements fall into the bucket.
             *
             */
            for (int i = left; i <= right; i++) {
                ++bucketSizes[getSuggestedIndex(sortJob, left, i, smallest, range, n) - left];
            }//end for loop

            // printArray("bucket sizes ", bucketSizes);
            /**
             * Converts the bucketSizes array from holding bucketSizes to
             * holding startingIndices for each bucket. This is a master-stroke
             * necessary to avoid introducing a third array for this purpose. It
             * helps us to increase our pre-recursive memory efficiency by up to
             * 33%..boosting on this machine maximum input array size handling
             * from about 11.15 million to about 17.9 million.
             *
             * This memory gain however, is made at the expense of some speed,
             * as it means that the array must be sorted in place in the next
             * stage. The algorithm for that is slightly slower as it means at
             * least one extra swap compared to that used at the same stage in
             * class <code>SpeedyBucketSort</code> An index i, in this array
             * holds in each of its indices the total number of sorted elements
             * that are before the bucket referred to by the index.
             *
             */
            int bucketSizeAccumulator = 0;
            for (int i = 0; i < len; i++) {
                if (i == 0) {
                    bucketSizeAccumulator = bucketSizes[0];
                    bucketSizes[0] = 0;
                }//end if

                /*
                 * if the current bucket size is greater than 0, that means a bucket of values
                 * exists there. Record the sum of sizes of all buckets before this bucket at that index
                 *. It tells us what index each bucket will start from.  
                 */
                if (bucketSizes[i] > 0) {
                    int x = bucketSizeAccumulator;
                    bucketSizeAccumulator += bucketSizes[i];
                    bucketSizes[i] = x;
                }//end if

            }//end for loop

            int swaps = 0, i = left;
            double temp = sortJob[left];
            while (i <= right) {

//the corresponding element in the array has been sorted.     
                if ((i <= negIndex && temp > 0) || (i > negIndex && temp <= 0)) {
                    sortJob[i++] = temp;
                    if (i <= right) {
                        temp = sortJob[i];
                    }
                }//end if
                else {
                    /**
                     * Access the index in bucketSizes that gives you info about
                     * the index where you will place the ith element of the
                     * sortJob array in order for it to be partially or fully
                     * sorted. This is the index in the array to be sorted where
                     * the array element at i should be placed in order for it
                     * to be partially or fully sorted.
                     */
                    int location = left + bucketSizes[getSuggestedIndex(temp, left, smallest, range, n) - left]++;

                    if (location >= right + 1) {
                        break;
                    }
                    if (i != location) {
                        double x = temp;
                        temp = sortJob[location];
                        sortJob[location] = -x;
                    } //ty
                    else if (i == location) {
                        sortJob[i++] = -temp;
                        if (i <= right) {
                            temp = sortJob[i];
                        }
                    }
                    swaps++;
                }//end else
                if (swaps == len) {
                    break;
                }//end if

            }//end while loop
            ///bucketSizes = null;
           
  for (int index = left; index <= right;) {
                int start = index;
//reverse polarities used to identify sorted positions in the last loop.
                if ((index <= negIndex && sortJob[index] > 0) || (index > negIndex && sortJob[index] <= 0)) {
                    sortJob[index] *= -1;
                }
                int suggestedIndex = getSuggestedIndex(sortJob, left, start, smallest, range, n);
                while (index <= right) {
                    if ((index <= negIndex && sortJob[index] > 0) || (index > negIndex && sortJob[index] <= 0)) {
                        sortJob[index] *= -1;
                    }
                    if (suggestedIndex == getSuggestedIndex(sortJob, left, index, smallest, range, n)) {
                        index++;
                    }//end if
                    else {
                        break;
                    }
                }//end inner while loop
                if (index >= right + 1) {
                    index = right;
                }

                int bucketSize = index - start + 1;

                if (bucketSize == 1 && index == right) {
                    break;
                } else if (bucketSize >= 2 && bucketSize <= MAX_INSERTION_SORT) {
                    insertionSort(sortJob, start, index);
                }//end else if
                else {
                    sort(sortJob, start, index);
                }//end else
            }//end for loop 
        }//end else

    }//end method.

    /**
     *
     * @param x The array to be sorted
     * @param offset The offset from the beginning of the array, representing
     * the index of the element where sorting is to begin.
     * @param i The index of the element whose suggested suggestedIndexAt_i is
     * needed.
     * @param smallest The smallest element found in the portion of the array
     * that is to be sorted.
     * @param range The biggest element found in the portion of the array that
     * is to be sorted, minus the smallest element found in the same portion.
     * @param n The number of entries to be sorted minus 1.
     * @return the index suggested by the algorithm for the index_th entry of
     * this array.
     */
    private static int getSuggestedIndex(int x[], int offset, int i, double smallest, double range, int n) {
        double entry = x[i];
        return offset + (int) (((entry - smallest) * n) / range);
    }

    /**
     *
     * @param entry Entry whose index we need in the array to be sorted
     * @param offset The offset from the beginning of the array, representing
     * the index of the element where sorting is to begin.
     * @param i The index of the element whose suggested suggestedIndexAt_i is
     * needed.
     * @param smallest The smallest element found in the portion of the array
     * that is to be sorted.
     * @param range The biggest element found in the portion of the array that
     * is to be sorted, minus the smallest element found in the same portion.
     * @param n The number of entries to be sorted minus 1.
     * @return the index suggested by the algorithm for the index_th entry of
     * this array.
     */
    private static int getSuggestedIndex(double entry, int offset, double smallest, double range, int n) {
        return offset + (int) (((entry - smallest) * n) / range);
    }

    /**
     *
     * @param array the array to sort.
     */
    public static void sort(int[] array) {
        sort(array, 0, array.length - 1);
    }

    /**
     *
     * @param sortJob The array to be sorted.
     * @param left The starting index of the array where we start sorting.
     * @param right The ending index in the array.
     */
    public static void sort(int[] sortJob, int left, int right) {
        int len = right - left + 1;
        int extremities[] = checkSortState(sortJob, left, right);
        double smallest = extremities[0];
        double biggest = extremities[1];
        double range = biggest - smallest;
        int n = len - 1;
        boolean alreadySorted = extremities[2] == 0 ? true : false || (biggest == smallest);
        boolean hasNegValue = extremities[3] == 0;
        boolean hasPosValue = extremities[4] == 0;

        //biggest == smallest means that the array to be sorted has equal elements and as such is already sorted.
        ++recursions;
        if (alreadySorted) {
        }//end if 
        else {
            /**
             * Introduce some cache friendliness to the code.
             */
            if (len >= 1000000 && cacheOptimized) {
                recursivePartition(sortJob, left, right, 3);
                //  recursivePartition(sortJob, left, negIndex, 3); recursivePartition(sortJob, negIndex+1, right, 3);
            }
            if (recursions == 0 && (hasNegValue && hasPosValue)) {
                negIndex = partitionNegativesAndPositives(sortJob, left, right);
            }

            int[] bucketSizes = new int[len];

            /**
             * Stores the bucket sizes of suggested indices. For example if
             * bucketSizes[3] = 5; It means that the number of entries that have
             * a suggestedIndex of <code>suggestedIndices[3]</code> is 5. So say
             * sortJob[3] = 7; Entry 7 is the current element to be sorted.
             * suggestedIndices[3]=4; means that the algorithm suggests that 7
             * should be put at index 4. To know the number of entries that also
             * fall into this same bucket i.e.(have a suggestedIndex of 4), use
             * <code> int bucketSize = bucketSizes[4];</code> Its value will
             * tell you how many elements fall into the bucket.
             *
             */
            for (int i = left; i <= right; i++) {
                ++bucketSizes[getSuggestedIndex(sortJob, left, i, smallest, range, n) - left];
            }//end for loop

            /**
             * Converts the bucketSizes array from holding bucketSizes to
             * holding startingIndices for each bucket. This is a master-stroke
             * necessary to avoid introducing a third array for this purpose. It
             * helps us to increase our pre-recursive memory efficiency by up to
             * 33%..boosting on this machine maximum input array size handling
             * from about 11.15 million to about 17.9 million.
             *
             * This memory gain however, is made at the expense of some speed,
             * as it means that the array must be sorted in place in the next
             * stage. The algorithm for that is slightly slower as it means at
             * least one extra swap compared to that used at the same stage in
             * class <code>SpeedyBucketSort</code> An index i, in this array
             * holds in each of its indices the total number of sorted elements
             * that are before the bucket referred to by the index.
             *
             */
            int bucketSizeAccumulator = 0;
            for (int i = 0; i < len; i++) {
                if (i == 0) {
                    bucketSizeAccumulator = bucketSizes[0];
                    bucketSizes[0] = 0;
                }//end if

                /*
                 * if the current bucket size is greater than 0, that means a bucket of values
                 * exists there. Record the sum of sizes of all buckets before this bucket at that index
                 *. It tells us what index each bucket will start from.  
                 */
                if (bucketSizes[i] > 0) {
                    int x = bucketSizeAccumulator;
                    bucketSizeAccumulator += bucketSizes[i];
                    bucketSizes[i] = x;
                }//end if

            }//end for loop

            int swaps = 0, i = left;
            int temp = sortJob[left];
            while (i <= right) {

//the corresponding element in the array has been sorted.     
                if ((i <= negIndex && temp > 0) || (i > negIndex && temp <= 0)) {
                    sortJob[i++] = temp;
                    if (i <= right) {
                        temp = sortJob[i];
                    }
                }//end if
                else {
                    /**
                     * Access the index in bucketSizes that gives you info about
                     * the index where you will place the ith element of the
                     * sortJob array in order for it to be partially or fully
                     * sorted. This is the index in the array to be sorted where
                     * the array element at i should be placed in order for it
                     * to be partially or fully sorted.
                     */
                    int location = left + bucketSizes[getSuggestedIndex(temp, left, smallest, range, n) - left]++;
                    if (location >= right + 1) {
                        break;
                    }
                    if (i != location) {
                        int x = temp;
                        temp = sortJob[location];
                        sortJob[location] = -x;
                    } //ty
                    else if (i == location) {
                        sortJob[i++] = -temp;
                        if (i <= right) {
                            temp = sortJob[i];
                        }
                    }
                    swaps++;
                }//end else
                if (swaps == len) {
                    break;
                }//end if

            }//end while loop
            // bucketSizes = null;

            for (int index = left; index <= right;) {
                int start = index;
                if ((index <= negIndex && sortJob[index] > 0) || (index > negIndex && sortJob[index] <= 0)) {
                    sortJob[index] *= -1;
                }
                int suggestedIndex = getSuggestedIndex(sortJob, left, start, smallest, range, n);
//Tackle the buckets..this is where the algorithm could decay into O(n^2) if bucket sizes are too large.
                while (index <= right) {
                    if ((index <= negIndex && sortJob[index] > 0) || (index > negIndex && sortJob[index] <= 0)) {
                        sortJob[index] *= -1;
                    }
                    if (suggestedIndex == getSuggestedIndex(sortJob, left, index, smallest, range, n)) {
                        index++;
                    }//end if
                    else {
                        break;
                    }
                }//end inner while loop
                if (index >= right + 1) {
                    index = right;
                }

                int bucketSize = index - start + 1;

                if (bucketSize == 1 && index == right) {
                    break;
                } else if (bucketSize >= 2 && bucketSize <= MAX_INSERTION_SORT) {
                    insertionSort(sortJob, start, index);
                }//end else if
                else {
                    sort(sortJob, start, index);
                }//end else
            }//end for loop 
        }//end else

    }//end method.    

    public static void main(String args[]) {

        System.out.println("com.github.gbenroscience.sortmix.experiments.AdvancedWittyBucketSort.main()");

        int n = 24_000_000;
        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = Math.random() * 1000;
        }

        long start = System.currentTimeMillis();
        sort(data);
        long end = System.currentTimeMillis();

        System.out.println("Sorted " + n + " elements in " + (end - start) + "ms");

        // Final validation
        boolean sorted = true;
        for (int i = 0; i < n - 1; i++) {
            if (data[i] > data[i + 1]) {
                sorted = false;
                break;
            }
        }
        System.out.println("Verification: " + (sorted ? "PASSED" : "FAILED"));
    }//end method

}
