/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.gbenroscience.sortmix.experiments;


import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import static com.github.gbenroscience.sortmix.util.SortUtils.*;

/**
 *
 * @author GBEMIRO
 */
// File: Quicksort.java 

/** Data for Quicksort from TestSort
   Original is based on Baase, 
   opt is based on Knuth, ending in an insertion sort

Sort times in millisecs, (10 times averaged), list size 100000:
    Avg    Low   High  Version Description-----
     19     15     32 opt, insert 25 random, 316 values
    127     79    313 original random, 316 values
     31     31     32 opt, insert 25 random
     52     46     63 original random
      5      0     16 opt, insert 25 in order
  18769  18359  19578 original in order
     17     15     31 opt, insert 25 in order except for 5%
     94     78    109 original in order except for 5%
     14      0     31 opt, insert 25 reversed order
  40605  39797  42985 original reversed order
     20     15     31 opt, insert 25 reversed except for 5%
     80     62    109 original reversed except for 5%
     14      0     31 opt, insert 25 all same
  20630  18313  22063 original all same

Random list of size 1000000:
Enter next sort insertion parameter: 1
Average time: 450
Enter next sort insertion parameter: 25
Average time: 383
Enter next sort insertion parameter: 20
Average time: 394
Enter next sort insertion parameter: 30
Average time: 392
Enter next sort insertion parameter: 25
Average time: 383

*/
public class QuickSort {
  public static int MAX_INSERTION_SORT = 25;  // parameter to tune for speed
  
      /**
     *
     * @param array the array to sort.
     */
    public static void sort(int[] array) {
        sort(array, 0, array.length-1);
    }
    /**
     *
     * @param array the array to sort.
     */
    public static void sort(double[] array) {
        sort(array, 0, array.length-1);
    }  
  /**
   * Sort with a variant of Hoare's sort algorithm.
   * @param data 
   *   array containing elements to sort
   * @param start 
   *   index of first element to be sorted
   * @param end 
   *   index of final element to be sorted
   */
  public static void sort(int [] data, int start, int end) {
    // nonrecursive, efficient inner loop,
    // limited stack size, OK if list already sorted
    
    int pivotIndex; // Array index for the pivot element
    int first, last;  // start, end of current part
    int n1, n2; // Number of elements before and after the pivot element
    int[] stack = new int[100]; //Stack to hold index ranges
    // allows 2^50 elements to be sorted!
    int top = -1;

    //Fill stack with initial values in preparation for loop
    if (end - start >= MAX_INSERTION_SORT) {
        stack[++top] = start;
        stack[++top] = end;  
    }
    while(top >= 0) {
      //Get segment length & first index values
      last = stack[top--]; 
      first = stack[top--];

      // Partition the array, and set the pivot index.
      pivotIndex = partition(data, first, last);  
//      System.out.println("Pivot index: " + pivotIndex);
//      TestSort.printArray(data);

      // Compute the sizes of the two pieces.
      n1 = pivotIndex - first;
      n2 = last - pivotIndex;

      // Push the n & first values of the array segments before & after
      // the pivotIndex onto the stack.  Make sure the larger of the
      // two segments is pushed first.  Only push a segment if its
      // length is > 1
      if(n2 < n1) {
        if(n1 > MAX_INSERTION_SORT) {
           stack[++top] = first;
           stack[++top] = pivotIndex-1;
        }
        if(n2 > MAX_INSERTION_SORT) {
           stack[++top] = pivotIndex+1;
           stack[++top] = last;
        }
      }
      else {
        if(n2 > MAX_INSERTION_SORT){
          stack[++top] = pivotIndex+1;
          stack[++top] = last;
        }
        if(n1 > MAX_INSERTION_SORT){
           stack[++top] = first;
           stack[++top] = pivotIndex - 1;
         } 
      } // end push depending on size
    } // end while loop for stack
    if (MAX_INSERTION_SORT > 1)  // test allows a check of qsort part
        insertionSort(data, start, end); //use original ends of region
  } // end sort


  /**
   * Sort with a variant of Hoare's sort algorithm.
   * @param data 
   *   array containing elements to sort
   * @param start 
   *   index of first element to be sorted
   * @param end 
   *   index of final element to be sorted
   */
  public static void sort(double[] data, int start, int end) {
    // nonrecursive, efficient inner loop,
    // limited stack size, OK if list already sorted
    
    int pivotIndex; // Array index for the pivot element
    int first, last;  // start, end of current part
    int n1, n2; // Number of elements before and after the pivot element
    int[] stack = new int[100]; //Stack to hold index ranges
    // allows 2^50 elements to be sorted!
    int top = -1;

    //Fill stack with initial values in preparation for loop
    if (end - start >= MAX_INSERTION_SORT) {
        stack[++top] = start;
        stack[++top] = end;  
    }
    while(top >= 0) {
      //Get segment length & first index values
      last = stack[top--]; 
      first = stack[top--];

      // Partition the array, and set the pivot index.
      pivotIndex = partition(data, first, last);  
//      System.out.println("Pivot index: " + pivotIndex);
//      TestSort.printArray(data);

      // Compute the sizes of the two pieces.
      n1 = pivotIndex - first;
      n2 = last - pivotIndex;

      // Push the n & first values of the array segments before & after
      // the pivotIndex onto the stack.  Make sure the larger of the
      // two segments is pushed first.  Only push a segment if its
      // length is > 1
      if(n2 < n1) {
        if(n1 > MAX_INSERTION_SORT) {
           stack[++top] = first;
           stack[++top] = pivotIndex-1;
        }
        if(n2 > MAX_INSERTION_SORT) {
           stack[++top] = pivotIndex+1;
           stack[++top] = last;
        }
      }
      else {
        if(n2 > MAX_INSERTION_SORT){
          stack[++top] = pivotIndex+1;
          stack[++top] = last;
        }
        if(n1 > MAX_INSERTION_SORT){
           stack[++top] = first;
           stack[++top] = pivotIndex - 1;
         } 
      } // end push depending on size
    } // end while loop for stack
    if (MAX_INSERTION_SORT > 1)  // test allows a check of qsort part
        insertionSort(data, start, end); //use original ends of region
  } // end sort


 
  
  
  
    public static void main1(String args[]) {

        int len = 15000000;//22343533;// ;17907709; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
        //int arr[] = BenchMarker.initBinaryArrayInts(len,4,6);
        int arr[] = BenchMarker.initArrayRandomInts(len);
    //    printArray("Before sorting", arr);
        double start = System.nanoTime();
         QuickSort.sort(arr,0,len-1);//.sort(arr);
         start = (System.nanoTime()-start)/1.0E6;
         printObject(len+" items sorted using simple Quicksort in "+start);     
         
         if(checkSortState(arr, 0, len-1)[2]==0){
             System.out.println("Sorted!");
         }
         else{
             System.out.println("Not Sorted");
         }
      //printArray("After sorting", arr);
       //ty
    }//end method  
    
    
    public static void main(String[] args) {
             System.out.println("com.github.gbenroscience.sortmix.experiments.QuickSort.main()");
        int n = 24_000_000;
        double[] data = new double[n];
        for (int i = 0; i < n; i++) data[i] = Math.random() * 1000;

        long start = System.currentTimeMillis();
        sort(data);
        long end = System.currentTimeMillis();

        System.out.println("Sorted " + n + " elements in " + (end - start) + "ms");
        
        // Final validation
        boolean sorted = true;
        for (int i = 0; i < n - 1; i++) {
            if (data[i] > data[i+1]) {
                sorted = false;
                break;
            }
        }
        System.out.println("Verification: " + (sorted ? "PASSED" : "FAILED"));
       
    }
}