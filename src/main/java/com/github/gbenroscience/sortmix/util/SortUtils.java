/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.gbenroscience.sortmix.util;

/**
 *
 * @author GBEMIRO
 */
public class SortUtils {

    private static int partitionSteps;
    public static int MAX_INSERTION_SORT = 10;  // parameter to tune for speed

    /**
     *
     * @param array An array of numbers to be sorted.
     * @param left the starting index of the array where sorting should start
     * @param right the ending index
     * @return an array containing the smallest entry in index 0 and the biggest
     * number in index 1 and 0 in index 2 if the array is already sorted or 1 in
     * index 2 if the array is not already sorted; 0 in index 3 if it has
     * negative entries and 1 if it doesn't and finally 0 in index 4 if it has
     * positive entries and 1 if not.ty
     */
    public static int[] checkSortState(int[] array, int left, int right) {
        boolean alreadySortedAscending = true;//ty
        boolean alreadySortedDescending = true;//ty
        int smallest = array[left], biggest = array[left];

        int limit = right + 1;
        int i = left;
        boolean hasNegValue = false;
        boolean hasPosValue = false;

        for (; i < limit; i++) {
            if (smallest > array[i]) {
                smallest = array[i];
            }
            if (biggest < array[i]) {
                biggest = array[i];
            }
            if (!hasNegValue) {
                if (array[i] < 0) {
                    hasNegValue = true;
                }
            }
            if (!hasPosValue) {
                if (array[i] >= 0) {
                    hasPosValue = true;
                }
            }
            if (alreadySortedAscending && i < limit - 1) {
                if (array[i] > array[i + 1]) {
                    alreadySortedAscending = false;
                }
            }
        }//end for loop
        if (alreadySortedAscending) {
//printObject("Sorted Ascending!");
            return new int[]{smallest, biggest, 0, hasNegValue ? 0 : 1, hasPosValue ? 0 : 1};
        }

        i = left;
        for (; i < limit - 1; i++) {
            if (alreadySortedDescending) {
                if (array[i] < array[i + 1]) {
                    alreadySortedDescending = false;
                    break;
                }
            }

        }//end for loop

        if (alreadySortedDescending) {
//printObject("Sorted Descending!");
            reverseArray(array, left, right);
            return new int[]{smallest, biggest, 0, hasNegValue ? 0 : 1, hasPosValue ? 0 : 1};
        }
        if (!alreadySortedAscending && !alreadySortedDescending) {
//        printObject( "Not Sorted Ascending at index = "+j+". array["+j+"] = "+array[j]+", array["+(j+1)+"] = "+array[j+1] );    
//        printObject( "Not Sorted Descending at index = "+i+". array["+i+"] = "+array[i]+", array["+(i+1)+"] = "+array[i+1] );
            return new int[]{smallest, biggest, 1, hasNegValue ? 0 : 1, hasPosValue ? 0 : 1};
        }
        return new int[]{smallest, biggest, 1, hasNegValue ? 0 : 1, hasPosValue ? 0 : 1};
    }

    /**
     *
     * @param sortJob the array to be sorted between <code>left</code> and
     * <code>right</code>
     * @param left the index to start sorting from.
     * @param right the index to stop sorting at.
     * @return the index of the last negative number after moving all negatives
     * to the left side of the array.
     */
    public static int partitionNegativesAndPositives(double[] sortJob, int left, int right) {
        int lim = right + 1;
        //Monitors the index of the last negative number.
        int $negIndex = left - 1;
        for (int i = left; i < lim; i++) {
            if (sortJob[i] < 0) {
                swap(sortJob, i, ++$negIndex);
            }
        }//end for loop      
        return $negIndex;
    }

    /**
     *
     * @param sortJob the array to be sorted between <code>left</code> and
     * <code>right</code>
     * @param left the index to start sorting from.
     * @param right the index to stop sorting at.
     * @return the index of the last negative number after moving all negatives
     * to the left side of the array.
     */
    public static int partitionNegativesAndPositives(int[] sortJob, int left, int right) {
        int lim = right + 1;
        //Monitors the index of the last negative number.
        int $negIndex = left - 1;
        for (int i = left; i < lim; i++) {
            if (sortJob[i] < 0) {
                swap(sortJob, i, ++$negIndex);
            }
        }//end for loop      
        return $negIndex;
    }

    /**
     *
     * @param array An array of numbers to be sorted.
     * @param left the starting index of the array where sorting should start
     * @param right the ending index
     * @return an array containing the smallest entry in index 0 and the biggest
     * number in index 1 and 0 in index 2 if the array is already sorted or 1 in
     * index 2 if the array is not already sorted; 0 in index 3 if it has
     * negative entries and 1 if it doesn't and finally 0 in index 4 if it has
     * positive entries and 1 if not.
     */
    public static double[] checkSortState(double[] array, int left, int right) {
        boolean alreadySortedAscending = true;//ty
        boolean alreadySortedDescending = true;//ty
        double smallest = array[left], biggest = array[left];
        boolean hasNegValue = false;
        boolean hasPosValue = false;

        int limit = right + 1;
        int i = left;

        for (; i < limit; i++) {
            if (smallest > array[i]) {
                smallest = array[i];
            }
            if (biggest < array[i]) {
                biggest = array[i];
            }
            if (!hasNegValue) {
                if (array[i] < 0) {
                    hasNegValue = true;
                }
            }
            if (!hasPosValue) {
                if (array[i] >= 0) {
                    hasPosValue = true;
                }
            }
            if (alreadySortedAscending && i < limit - 1) {
                if (array[i] > array[i + 1]) {
                    alreadySortedAscending = false;
                }
            }
        }//end for loop
        if (alreadySortedAscending) {
//printObject("Sorted Ascending!");
            return new double[]{smallest, biggest, 0, hasNegValue ? 0 : 1, hasPosValue ? 0 : 1};
        }

        i = left;
        for (; i < limit - 1; i++) {
            if (alreadySortedDescending) {
                if (array[i] < array[i + 1]) {
                    alreadySortedDescending = false;
                    break;
                }
            }

        }//end for loop

        if (alreadySortedDescending) {
//printObject("Sorted Descending!");
            reverseArray(array, left, right);
            return new double[]{smallest, biggest, 0, hasNegValue ? 0 : 1, hasPosValue ? 0 : 1};
        }
        if (!alreadySortedAscending && !alreadySortedDescending) {
//        printObject( "Not Sorted Ascending at index = "+j+". array["+j+"] = "+array[j]+", array["+(j+1)+"] = "+array[j+1] );    
//        printObject( "Not Sorted Descending at index = "+i+". array["+i+"] = "+array[i]+", array["+(i+1)+"] = "+array[i+1] );
            return new double[]{smallest, biggest, 1, hasNegValue ? 0 : 1, hasPosValue ? 0 : 1};
        }
        return new double[]{smallest, biggest, 1, hasNegValue ? 0 : 1, hasPosValue ? 0 : 1};
    }

    /**
     *
     * @param array An array of numbers to be sorted.
     * @param left the starting index of the array where sorting should start
     * @param right the ending index
     * @return an array containing the smallest entry in index 0 and the biggest
     * number in index 1 and 0 in index 2 if the array is already sorted or 1 in
     * index 2 if the array is not already sorted.
     */
    private static double[] getSmallestAndBiggestEntriesAndSortState(double[] array, int left, int right) {
        boolean alreadysorted = true;

        double smallest = array[left], biggest = array[left];
        int limit = right + 1;

        for (int i = left; i < limit; i++) {

            if (smallest > array[i]) {
                smallest = array[i];
            }
            if (biggest < array[i]) {
                biggest = array[i];
            }

            if (alreadysorted) {
                if (i + 1 < limit && array[i] > array[i + 1]) {
                    alreadysorted = false;
                }
            }

        }//end for loop
        return new double[]{smallest, biggest, alreadysorted ? 0 : 1};
    }//end method

    private static int partitionFloat(double[] data, int first, int last) {
        // This version relies on sentinels to make inner loops faster.
        // Precondition: last>first, and data from index first through last.
        // Postcondition: The method has selected some "pivot value" that occurs
        // in data[first]...data[last]. The elements of data have then been
        // rearranged and the method returns a pivot index so that
        // -- data[pivot index] is equal to the pivot;
        // -- each element before data[pivot index] is <= the pivot;
        // -- each element after data[pivot index] is >= the pivot.
        int iLo = first + 1, iHi = last; //lowest, highest untested indices
        int mid = (first + last) / 2; // take pivot from here
        double pivot = data[mid];        //    so in-order not worst case

        // Almost the body of main while loop follows, except for non-sentinel search
        //Swap the chosen pivot value into beginning
        data[mid] = data[first];
        data[first] = pivot;  //serves as first sentinel for downward sweep

        while (data[iHi] > pivot) { // normal downward scan
            iHi--;
        }

        while (iLo <= iHi && data[iLo] < pivot) { // no sentinel upward yet
            iLo++;
        }
        if (iLo <= iHi) {
            double temp = data[iLo];
            data[iLo] = data[iHi];
            data[iHi] = temp;
            iHi--;
            iLo++;
        }

        while (iLo <= iHi) { // now have sentinels both ways
            while (data[iHi] > pivot) {
                iHi--;
            }
            while (data[iLo] < pivot) { // have sentenel swapped in place now
                iLo++;
            }
            if (iLo <= iHi) {
                double temp = data[iLo];
                data[iLo] = data[iHi];
                data[iHi] = temp;
                iHi--;
                iLo++;
            }
        }
        int iPivot = iLo - 1;          // place of last smaller value
        data[first] = data[iPivot];  // swap with pivot
        data[iPivot] = pivot;
        return iPivot;
    }

    /**
     *
     * @param sortJob The array to partitionFloat using suggested-index-based
     * left-right partitioning.
     * @param left The index in the array where we begin the partitionFloat.
     * @param right The index in the array where partitioning ends.
     * @param partitionSteps The number of times to partitionFloat. This method
     * first checks if the sub-array is sorted and its smallest and largest
     * entries. ty If the sub-array has 100 entries or less and is already
     * sorted, it returns the negative of <code>right</code> e.g.
     * <code>-right</code>. If the sub-array has 100 entries or less and is not
     * yet sorted, it sorts it using an insertion sort algorithm and returns
     * <code>-right</code>. If the number of entries in the sub-array is greater
     * than 100 and it is already sorted, it returns <code>-right</code>. if the
     * number of entries in the sub-array is greater than 100 and its not yet
     * sorted, it partitions the array and returns the partitionFloat index.
     */
    public static void recursivePartition(double[] sortJob, int left, int right, int partitionSteps) {
// similar to book version: recursive, no sentinels
        if (right > left) {
            if (right - left <= MAX_INSERTION_SORT) {
                insertionSort(sortJob, left, right);
            } else {
                int pivotIndex = partitionFloat(sortJob, left, right);

                if (partitionSteps > SortUtils.partitionSteps++) {
                    recursivePartition(sortJob, left, pivotIndex - 1, partitionSteps);
                    recursivePartition(sortJob, pivotIndex + 1, right, partitionSteps);
                } else {
                    SortUtils.partitionSteps = 0;
                }
            }
        }
    }

    /**
     *
     * @param sortJob The array to partitionFloat using suggested-index-based
     * left-right partitioning.
     * @param left The index in the array where we begin the partitionFloat.
     * @param right The index in the array where partitioning ends.
     * @param partitionSteps The number of times to partitionFloat. This method
     * first checks if the sub-array is sorted and its smallest and largest
     * entries. ty If the sub-array has 100 entries or less and is already
     * sorted, it returns the negative of <code>right</code> e.g.
     * <code>-right</code>. If the sub-array has 100 entries or less and is not
     * yet sorted, it sorts it using an insertion sort algorithm and returns
     * <code>-right</code>. If the number of entries in the sub-array is greater
     * than 100 and it is already sorted, it returns <code>-right</code>. if the
     * number of entries in the sub-array is greater than 100 and its not yet
     * sorted, it partitions the array and returns the partitionFloat index.
     */
    public static void recursivePartition(int[] sortJob, int left, int right, int partitionSteps) {
// similar to book version: recursive, no sentinels
        if (right > left) {
            if (right - left <= MAX_INSERTION_SORT) {
                insertionSort(sortJob, left, right);
            } else {
                int pivotIndex = partition(sortJob, left, right);

                if (partitionSteps > SortUtils.partitionSteps++) {
                    recursivePartition(sortJob, left, pivotIndex - 1, partitionSteps);
                    recursivePartition(sortJob, pivotIndex + 1, right, partitionSteps);
                } else {
                    SortUtils.partitionSteps = 0;
                }
            }
        }
    }

    /**
     * @param array Array to be reversed.
     * @param left the index in the array where we begin reversals.
     * @param right the index in the array where we stop reversals.
     */
    private static void reverseArray(int[] array, int left, int right) {

        if (left >= 0 && right - left + 1 <= array.length) {

            while (left < right) {
                int temp = array[left];
                array[left] = array[right];
                array[right] = temp;
                left++;
                right--;
            }//end while loop

        } else {
            throw new ArrayIndexOutOfBoundsException("Index out of range offset = " + left + " should be greater than or equal to zero and"
                    + " offset+number_of_characters should be less than or equal to the array size which is " + array.length);
        }
    }

    /**
     * @param array Array to be reversed.
     * @param left the index in the array where we begin reversals.
     * @param right the index in the array where we stop reversals.
     */
    private static void reverseArray(double[] array, int left, int right) {

        if (left >= 0 && right - left + 1 <= array.length) {

            while (left < right) {
                double temp = array[left];
                array[left] = array[right];
                array[right] = temp;
                left++;
                right--;
            }//end while loop

        } else {
            throw new ArrayIndexOutOfBoundsException("Index out of range offset = " + left + " should be greater than or equal to zero and"
                    + " offset+number_of_characters should be less than or equal to the array size which is " + array.length);
        }
    }

    /**
     * @param descr A string describing what is being printed.
     * @param arr The array whose content is to be printed.
     * @param offset The starting suggestedIndexAt_i in the array where printing
     * is to begin.
     * @param len The number of characters to print.
     *
     */
    public static void printArray(String descr, double[] arr, int offset, int len) {
        StringBuilder builder = new StringBuilder(descr + ":\n");
        builder.append("[");
        for (int i = offset; i < offset + len; i++) {
            builder.append(arr[i]);
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("]");
        System.out.println(builder);

    }

    /**
     * @param descr A string describing what is being printed.
     * @param arr The array whose content is to be printed.
     */
    public static void printArray(String descr, double[] arr) {
        printArray(descr, arr, 0, arr.length);
    }

    /**
     * @param descr A string describing what is being printed.
     * @param arr The array whose content is to be printed.
     */
    public static void printArray(String descr, double[][] arr) {
        StringBuilder builder = new StringBuilder("{");
        for (double[] ar : arr) {
            StringBuilder sb = new StringBuilder("{");
            for (double a : ar) {
                sb.append(a);
                sb.append(",");
            }
            builder.append(sb.substring(0, sb.length() - 1));
            builder.append("},");
        }
        StringBuilder b = new StringBuilder(builder.substring(0, builder.length() - 1));
        b.append("}");
        System.out.println(descr + ":\n" + b.toString());
    }

    /**
     * @param descr A string describing what is being printed.
     * @param arr The array whose content is to be printed.
     */
    public static void printArray(String descr, int[][] arr) {
        StringBuilder builder = new StringBuilder("{");
        for (int[] ar : arr) {
            StringBuilder sb = new StringBuilder("{");
            for (double a : ar) {
                sb.append(a);
                sb.append(",");
            }
            builder.append(sb.substring(0, sb.length() - 1));
            builder.append("},");
        }
        StringBuilder b = new StringBuilder(builder.substring(0, builder.length() - 1));
        b.append("}");
        System.out.println(descr + ":\n" + b.toString());
    }

    /**
     * @param descr A string describing what is being printed.
     * @param arr The array whose content is to be printed.
     */
    public static void printArray(String descr, int[] arr) {
        printArray(descr, arr, 0, arr.length);
    }

    /**
     * @param descr A string describing what is being printed.
     * @param arr The array whose content is to be printed.
     * @param offset The starting index in the array where printing is to begin.
     * @param len The number of characters to print.
     *
     */
    public static void printArray(String descr, int[] arr, int offset, int len) {
        StringBuilder builder = new StringBuilder(descr + ":\n");
        builder.append("[");
        for (int i = offset; i < offset + len; i++) {
            builder.append(arr[i]);
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("]");
        System.out.println(builder);

    }

    public static void printObject(Object obj) {
        System.out.println(obj);
    }

    /**
     * Swaps x[index1] with x[index2].
     *
     * @param x the array that contains data to be swapped
     * @param index1 the first index
     * @param index2 the second index.
     */
    public static void swap(int x[], int index1, int index2) {
        int t = x[index1];
        x[index1] = x[index2];
        x[index2] = t;
    }

    /**
     * Swaps x[index1] with x[index2].
     *
     * @param x the array that contains data to be swapped
     * @param index1 the first index
     * @param index2 the second index.
     */
    public static void swap(double x[], int index1, int index2) {
        double t = x[index1];
        x[index1] = x[index2];
        x[index2] = t;
    }

    /**
     *
     * @param x The array to be sorted.
     * @param left The starting index where we left sorting
     * @param right The ending index where we stop sorting. This method is used
     * for small array sorting by the main cluster sort code.
     *
     * For an array having <code>n<code> elements,
     * If there are no duplicate elements,then
     * If n is even,
     * this method performs a total of (n*(n+2))/4 sorting operations.
     * If n is odd however, this method performs a total of (n*(n+2)+1)/4 = ((n+1)/2)^2.
     * If there are duplicate elements, the count will be even less.
     */
    public static void selectionSort(double[] x, int left, int right) {

        int len = right - left + 1;
        double sm;
        double bg;
        int smallIndex;
        int bigIndex;
        int j = 0;
        int lim = left + len / 2;
        int $lim = 2 * left + len;
        for (int i = left; i < lim; i++) {
            smallIndex = bigIndex = i;
            sm = bg = x[smallIndex];
            for (j = i; j < $lim - i; j++) {
                if (sm > x[j]) {
                    sm = x[j];
                    smallIndex = j;
                }
                if (bg < x[j]) {
                    bg = x[j];
                    bigIndex = j;
                }
            }//end inner for loop

            if (sm < bg) {
                //small guy is coming to where big guy currently is and small guy is currently at where big guy is coming.      
                if (i == bigIndex && j - 1 == smallIndex) {
                    swap(x, bigIndex, j - 1);
                } //small guy is coming to where big guy currently is and small guy is not at big guy's final destination. 
                else if (i == bigIndex && j - 1 != smallIndex) {
                    swap(x, bigIndex, j - 1);
                    swap(x, smallIndex, i);
                } //big guy is coming to where small guy currently is and big guy is not at small guy's final destination. 
                else if (i != bigIndex && j - 1 == smallIndex) {
                    swap(x, smallIndex, i);
                    swap(x, bigIndex, j - 1);
                } else if (i != bigIndex) {
                    swap(x, smallIndex, i);
                    swap(x, bigIndex, j - 1);
                }
            } else if (sm == bg) {
                break;
            }

            // System.out.println("On pass "+suggestedIndexAt_i);
            // printArray(x);
        }//end outer for loop
    }//end method

    /**
     *
     * @param x The array to be sorted.
     * @param left The starting index where we left sorting
     * @param right The ending index where we stop sorting. This method is used
     * for small array sorting by the main cluster sort code.
     *
     * For an array having <code>n<code> elements,
     * If there are no duplicate elements,then
     * If n is even,
     * this method performs a total of (n*(n+2))/4 sorting operations.
     * If n is odd however, this method performs a total of (n*(n+2)+1)/4 = ((n+1)/2)^2.
     * If there are duplicate elements, the count will be even less.
     */
    public static void selectionSort(int[] x, int left, int right) {

        int len = right - left + 1;
        int sm;
        int bg;
        int smallIndex;
        int bigIndex;
        int j = 0;
        int lim = left + len / 2;
        int $lim = 2 * left + len;
        for (int i = left; i < lim; i++) {
            smallIndex = bigIndex = i;
            sm = bg = x[smallIndex];
            for (j = i; j < $lim - i; j++) {
                if (sm > x[j]) {
                    sm = x[j];
                    smallIndex = j;
                }
                if (bg < x[j]) {
                    bg = x[j];
                    bigIndex = j;
                }
            }//end inner for loop

            if (sm < bg) {
                //small guy is coming to where big guy currently is and small guy is currently at where big guy is coming.      
                if (i == bigIndex && j - 1 == smallIndex) {
                    swap(x, bigIndex, j - 1);
                } //small guy is coming to where big guy currently is and small guy is not at big guy's final destination. 
                else if (i == bigIndex && j - 1 != smallIndex) {
                    swap(x, bigIndex, j - 1);
                    swap(x, smallIndex, i);
                } //big guy is coming to where small guy currently is and big guy is not at small guy's final destination. 
                else if (i != bigIndex && j - 1 == smallIndex) {
                    swap(x, smallIndex, i);
                    swap(x, bigIndex, j - 1);
                } else if (i != bigIndex) {
                    swap(x, smallIndex, i);
                    swap(x, bigIndex, j - 1);
                }
            } else if (sm == bg) {
                break;
            }

            // System.out.println("On pass "+suggestedIndexAt_i);
            // printArray(x);
        }//end outer for loop
    }//end method

    /**
     *
     * @param data The array to be sorted.
     * @param left The starting index where we left sorting
     * @param right The ending index where we stop sorting. This method is used
     * for small array sorting by the main cluster sort code.
     */
    public static void insertionSort(double[] data, int left, int right) {
        for (int next = left + 1; next <= right; next++) {
            if (data[next - 1] > data[next]) {
                double val = data[next];
                int gap = next - 1;
                data[next] = data[gap];
                while (gap > left && data[gap - 1] > val) {
                    data[gap] = data[gap - 1];
                    gap--;
                }
                data[gap] = val;
            } // right: if not already in place
        } // right: for each element to be inserted
    }

    public static int partition(int[] data, int first, int last) {
        // This version relies on sentinels to make inner loops faster.
        // Precondition: last>first, and data from index first through last.
        // Postcondition: The method has selected some "pivot value" that occurs
        // in data[first]...data[last]. The elements of data have then been
        // rearranged and the method returns a pivot index so that
        // -- data[pivot index] is equal to the pivot;
        // -- each element before data[pivot index] is <= the pivot;
        // -- each element after data[pivot index] is >= the pivot.
        int iLo = first + 1, iHi = last; //lowest, highest untested indices
        int mid = (first + last) / 2; // take pivot from here
        int pivot = data[mid];        //    so in-order not worst case

        // Almost the body of main while loop follows, except for non-sentinal search
        //Swap the chosen pivot value into beginning
        data[mid] = data[first];
        data[first] = pivot;  //serves as first sentinel for downward sweep

        while (data[iHi] > pivot) { // normal downward scan
            iHi--;
        }

        while (iLo <= iHi && data[iLo] < pivot) { // no sentinel upward yet
            iLo++;
        }
        if (iLo <= iHi) {
            int temp = data[iLo];
            data[iLo] = data[iHi];
            data[iHi] = temp;
            iHi--;
            iLo++;
        }

        while (iLo <= iHi) { // now have sentinels both ways
            while (data[iHi] > pivot) {
                iHi--;
            }
            while (data[iLo] < pivot) { // have sentenel swapped in place now
                iLo++;
            }
            if (iLo <= iHi) {
                int temp = data[iLo];
                data[iLo] = data[iHi];
                data[iHi] = temp;
                iHi--;
                iLo++;
            }
        }
        int iPivot = iLo - 1;          // place of last smaller value
        data[first] = data[iPivot];  // swap with pivot
        data[iPivot] = pivot;
        return iPivot;
    }

    public static void insertionSort(int[] data, int start, int end) {
        for (int next = start + 1; next <= end; next++) {
            if (data[next - 1] > data[next]) {
                int val = data[next];
                int gap = next - 1;
                data[next] = data[gap];
                while (gap > start && data[gap - 1] > val) {
                    data[gap] = data[gap - 1];
                    gap--;
                }
                data[gap] = val;
            } // end: if not already in place
        } // end: for each element to be inserted
    }

    public static int partition(double[] data, int first, int last) {
        // This version relies on sentinels to make inner loops faster.
        // Precondition: last>first, and data from index first through last.
        // Postcondition: The method has selected some "pivot value" that occurs
        // in data[first]...data[last]. The elements of data have then been
        // rearranged and the method returns a pivot index so that
        // -- data[pivot index] is equal to the pivot;
        // -- each element before data[pivot index] is <= the pivot;
        // -- each element after data[pivot index] is >= the pivot.
        int iLo = first + 1, iHi = last; //lowest, highest untested indices
        int mid = (first + last) / 2; // take pivot from here
        double pivot = data[mid];        //    so in-order not worst case

        // Almost the body of main while loop follows, except for non-sentinal search
        //Swap the chosen pivot value into beginning
        data[mid] = data[first];
        data[first] = pivot;  //serves as first sentinel for downward sweep

        while (data[iHi] > pivot) { // normal downward scan
            iHi--;
        }

        while (iLo <= iHi && data[iLo] < pivot) { // no sentinel upward yet
            iLo++;
        }
        if (iLo <= iHi) {
            double temp = data[iLo];
            data[iLo] = data[iHi];
            data[iHi] = temp;
            iHi--;
            iLo++;
        }

        while (iLo <= iHi) { // now have sentinels both ways
            while (data[iHi] > pivot) {
                iHi--;
            }
            while (data[iLo] < pivot) { // have sentenel swapped in place now
                iLo++;
            }
            if (iLo <= iHi) {
                double temp = data[iLo];
                data[iLo] = data[iHi];
                data[iHi] = temp;
                iHi--;
                iLo++;
            }
        }
        int iPivot = iLo - 1;          // place of last smaller value
        data[first] = data[iPivot];  // swap with pivot
        data[iPivot] = pivot;
        return iPivot;
    }

}
