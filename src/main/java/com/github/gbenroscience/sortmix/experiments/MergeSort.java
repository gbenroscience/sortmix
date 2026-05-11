/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.gbenroscience.sortmix.experiments;

import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import static com.github.gbenroscience.sortmix.util.SortUtils.*;

public class MergeSort {

    private static int recursions = -1;
    private static double[] combined; // working array
    private static int[] combined_int; // working array

    // calls recursive split method to begin merge sorting
    public static void sort(double sortJob[]) {
        combined = new double[sortJob.length];
        sortArray(sortJob, 0, sortJob.length - 1); // split entire array
        combined = null;
    } // end method sort

    // calls recursive split method to begin merge sorting
    /**
     *
     * @param sortJob the array to sort
     * @param left the starting index of the sort
     * @param right the final index of the sort
     */
    public static void sort(double sortJob[], int left, int right) {
        combined = new double[right - left + 1];
        sortArray(sortJob, left, right);
        combined = null;
    } // end method sort

    // splits array, sorts subarrays and merges subarrays into sorted array
    private static void sortArray(double[] sortJob, int low, int high) {

        ++recursions;
        // test base case; size of array equals 1     
        if ((high - low) >= 1) // if not base case
        {
            int middle1 = (low + high) / 2; // calculate middle of array
            int middle2 = middle1 + 1; // calculate next element over     
            /*
             // output split step
             System.out.println( "split:   " + subarray( low, high ) );
             System.out.println( "         " + subarray( low, middle1 ) );
             System.out.println( "         " + subarray( middle2, high ) );
             System.out.println();
             */
            // split array in half; sort each half (recursive calls)
            sortArray(sortJob, low, middle1); // first half of array       
            sortArray(sortJob, middle2, high); // second half of array     

            // merge two sorted arrays after split calls return
            merge(sortJob, low, middle1, middle2, high);
        } // end if                                           
    } // end method split                                    

    // merge two sorted subarrays into one sorted subarray             
    private static void merge(double sortJob[], int left, int middle1, int middle2, int right) {
        int leftIndex = left; // index into left subarray              
        int rightIndex = middle2; // index into right subarray         
        int combinedIndex = left; // index into temporary working array

        /*   // output two subarrays before merging
          System.out.println( "merge:   " + subarray( left, middle1 ) );
          System.out.println( "         " + subarray( middle2, right ) );
         */
        // merge arrays until reaching end of either         
        while (leftIndex <= middle1 && rightIndex <= right) {
            // place smaller of two current elements into result  
            // and move to next space in arrays                   
            if (sortJob[leftIndex] <= sortJob[rightIndex]) {
                combined[combinedIndex++ - left] = sortJob[leftIndex++];
            } else {
                combined[combinedIndex++ - left] = sortJob[rightIndex++];
            }
        } // end while                                           

        // if left array is empty                                
        if (leftIndex == middle2) {
            // copy in rest of right array                        
            while (rightIndex <= right) {
                combined[combinedIndex++ - left] = sortJob[rightIndex++];
            }
        } else // right array is empty                             
        // copy in rest of left array                         
        {
            while (leftIndex <= middle1) {
                combined[combinedIndex++ - left] = sortJob[leftIndex++];
            }
        }
        // copy values back into original array
        System.arraycopy(combined, 0, sortJob, left, right - left + 1);
    } // end method merge       

    // calls recursive split method to begin merge sorting
    public static void sort(int sortJob[]) {
        combined_int = new int[sortJob.length];
        sortArray(sortJob, 0, sortJob.length - 1); // split entire array
        combined_int = null;
    } // end method sort

    // calls recursive split method to begin merge sorting
    /**
     *
     * @param sortJob the array to sort
     * @param left the starting index of the sort
     * @param right the final index of the sort
     */
    public static void sort(int sortJob[], int left, int right) {
        combined_int = new int[right - left + 1];
        sortArray(sortJob, left, right);
        combined_int = null;
    } // end method sort

    // splits array, sorts subarrays and merges subarrays into sorted array
    private static void sortArray(int[] sortJob, int low, int high) {

        ++recursions;
        // test base case; size of array equals 1     
        if ((high - low) >= 1) // if not base case
        {
            int middle1 = (low + high) / 2; // calculate middle of array
            int middle2 = middle1 + 1; // calculate next element over     
            /*
             // output split step
             System.out.println( "split:   " + subarray( low, high ) );
             System.out.println( "         " + subarray( low, middle1 ) );
             System.out.println( "         " + subarray( middle2, high ) );
             System.out.println();
             */
            // split array in half; sort each half (recursive calls)
            sortArray(sortJob, low, middle1); // first half of array       
            sortArray(sortJob, middle2, high); // second half of array     

            // merge two sorted arrays after split calls return
            merge(sortJob, low, middle1, middle2, high);
        } // end if                                           
    } // end method split           

    // merge two sorted subarrays into one sorted subarray             
    private static void merge(int sortJob[], int left, int middle1, int middle2, int right) {
        int leftIndex = left; // index into left subarray              
        int rightIndex = middle2; // index into right subarray         
        int combinedIndex = left; // index into temporary working array

        /*   // output two subarrays before merging
          System.out.println( "merge:   " + subarray( left, middle1 ) );
          System.out.println( "         " + subarray( middle2, right ) );
         */
        // merge arrays until reaching end of either         
        while (leftIndex <= middle1 && rightIndex <= right) {
            // place smaller of two current elements into result  
            // and move to next space in arrays                   
            if (sortJob[leftIndex] <= sortJob[rightIndex]) {
                combined_int[combinedIndex++ - left] = sortJob[leftIndex++];
            } else {
                combined_int[combinedIndex++ - left] = sortJob[rightIndex++];
            }
        } // end while                                           

        // if left array is empty                                
        if (leftIndex == middle2) {
            // copy in rest of right array                        
            while (rightIndex <= right) {
                combined_int[combinedIndex++ - left] = sortJob[rightIndex++];
            }
        } else // right array is empty                             
        // copy in rest of left array                         
        {
            while (leftIndex <= middle1) {
                combined_int[combinedIndex++ - left] = sortJob[leftIndex++];
            }
        }
        // copy values back into original array
        System.arraycopy(combined_int, 0, sortJob, left, right - left + 1);
    } // end method merge 

    public static void main1(String[] args) {
        int len = 22343533;//17907000; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
        int arr[] = BenchMarker.initArrayRandomInts(len);
        //    printArray("Before sorting", arr);
        double start = System.nanoTime();
        MergeSort.sort(arr);//.sort(arr);
        start = (System.nanoTime() - start) / 1.0E6;
        printObject(len + " items sorted using MergeSort in " + start + "ms, recursions = " + MergeSort.recursions);

        if (checkSortState(arr, 0, len - 1)[2] == 0) {
            System.out.println("Sorted!");
        } else {
            System.out.println("Not Sorted");
        }
        //printArray("After sorting", arr);
        //ty

    }

    public static void main(String[] args) {
        System.out.println("com.github.gbenroscience.sortmix.experiments.MergeSort.main()");
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
    }

} // end class MergeSort
