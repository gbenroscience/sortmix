/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.gbenroscience.sortmix.experiments;

import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import com.github.gbenroscience.sortmix.util.SortUtils;
import static com.github.gbenroscience.sortmix.util.SortUtils.*;

/**
 *
 * @author JIBOYE, OLUWAGBEMIRO OLAOLUWA
 * 
 */
public class ExpSort {

   static int blockSize = 100;
    
   /**
     *
     * @param sortJob The array to be sorted.
     */
    public static void sort(int[] sortJob) {
        
        int len = sortJob.length;//0,1,2,3,4,5,6,7,8,9,10,11,12,13,14
        int[]buckets = new int[blockSize/2];
        
      for(int i=0;i<len;i+=blockSize){
    
        int step = len - i >= blockSize? blockSize: len - i;//[9,0,8,3,-6,12,2,8,6,80]--[-6,0,2,3,6,8,9,12]
          SortUtils.insertionSort(sortJob,i,i+step-1);
          
          
      }  
        
        SortUtils.insertionSort(sortJob, 0, len-1);
    }   
    /**
     *
     * @param sortJob The array to be sorted.
     */
    public static void sort(double[] sortJob) {
        
    }  
    
    /**
     *
     * @param sortJob The array to be sorted.
     * @param left The starting index of the array where we start sorting.
     * @param right The ending index in the array.
     */
    public static void sort(int[] sortJob, int left, int right) {
        
    }
    
    /**
     *
     * @param sortJob The array to be sorted.
     * @param left The starting index of the array where we start sorting.
     * @param right The ending index in the array.
     */
    public static void sort(double [] sortJob, int left, int right) {
        
    }    
    
    
    public static void main(String args[]) {

        int len = 100000;//22343533;//22343533;//17907709; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
    //double arr[] = BenchMarker.initArrayPos$NegRandomFloats(len);
    int arr[] = BenchMarker.initArrayRandomInts(len);
   // printArray("Before sorting", arr);
        double start = System.nanoTime();
         ExpSort.sort(arr);
         
         start = (System.nanoTime()-start)/1.0E6;
         printObject(len+" items sorted using ExpSort in "+start+"ms.");     
         
         if(checkSortState(arr, 0, len-1)[2]==0){
             System.out.println("Sorted!");
         }
         else{
             System.out.println("Not Sorted");
         }
    //printArray("After sorting", arr);
       
    }//end method
}