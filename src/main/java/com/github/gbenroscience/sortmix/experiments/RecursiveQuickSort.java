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
public class RecursiveQuickSort {

  
   
    /**
     *
     * @param array the array to sort.
     */
    public static void sort(double[] array) {
        sort(array, 0, array.length-1);
        recursions=-1;
    }



  /**
   * Sort with a variant of Hoare's sort algorithm.  
   * Behavior can be bad on large lists due to O(n) stack.
   * @param data 
   *   array containing elements to sort
   * @param first 
   *   index of first element to be sorted
   * @param last 
   *   index of final element to be sorted
   */
  public static void sort(double[] data, int first, int last) {
    // similar to book version: recursive, no sentinels
    if (last > first) {
      if(last-first <= MAX_INSERTION_SORT){
          insertionSort(data, first, last);
      }
      else{
        int pivotIndex = partition(data, first, last);
      
     
      sort(data, first, pivotIndex - 1);          
      sort(data, pivotIndex + 1, last);  
      }
    }
  } // end sort
  
    /**
     *
     * @param array the array to sort.
     */
    public static void sort(int[] array) {
        sort(array, 0, array.length-1);
        recursions=-1;
    }
  
  /**
   * Sort with a variant of Hoare's sort algorithm.  
   * Behavior can be bad on large lists due to O(n) stack.
   * @param data 
   *   array containing elements to sort
   * @param first 
   *   index of first element to be sorted
   * @param last 
   *   index of final element to be sorted
   */
  public static void sort(int[] data, int first, int last) {
    // similar to book version: recursive, no sentinels
    if (last > first) {
      if(last-first <= MAX_INSERTION_SORT){
          insertionSort(data, first, last);
      }
      else{
        int pivotIndex = partition(data, first, last);
      
     
      sort(data, first, pivotIndex - 1);          
      sort(data, pivotIndex + 1, last);  
      }
    }
  } // end sort
    

    
    static int recursions = -1;



    public static void main(String args[]) {

        int len = 22343533;// ;17907709; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
        int arr[] = BenchMarker.initArrayRandomInts(len);
//ty
        double start= System.nanoTime();
      
              
    RecursiveQuickSort.sort(arr);
         start = (System.nanoTime()-start)/1.0E6;
         printObject(len+" items sorted using RecursiveQuickSort in "+start+"ms, recursions = "+recursions);     
  if( checkSortState(arr, 0, len-1)[2] == 0){
            System.out.println("Sorted!");
        }
        else{
            System.out.println("Not sorted!");
        }
       //printArray("see the sorted array!", arr);
       //ty
    }//end method       
}