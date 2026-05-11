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
public class ShellSort {
 
    /**
     *
     * @param sortJob The array to be sorted.
     */
    public static void sort(double[] sortJob) {
   sort(sortJob, 0, sortJob.length-1);
    }
    
    
    /**
     *
     * @param array The array to be sorted.
     * @param left The starting index of the array where we start sorting.
     * @param right The ending index in the array.
     */
    public static void sort(double[] array, int left, int right) {
       int len = right-left+1;
    int increment = len / 2;
	while (increment > 0) {
		for (int i = left+increment; i <= right; i++) {
			int j = i;
			double temp = array[i];
			while (j >= left+increment && array[j - increment] > temp) {
				array[j] = array[j - increment];
				j = j - increment;
			}
			array[j] = temp;
		}
		if (increment == 2) {
			increment = 1;
		} else {
			increment *= (5.0 / 11);
		}
	}      
    }
    
   /**
     *
     * @param sortJob The array to be sorted.
     */
    public static void sort(int [] sortJob) {
        sort(sortJob, 0, sortJob.length-1);
    }    
     /**
     *
     * @param array  The array to be sorted.
     * @param left The starting index of the array where we start sorting.
     * @param right The ending index in the array.
     */
    public static void sort(int array[], int left, int right){
        int len = right-left+1;
    int increment = len / 2;
	while (increment > 0) {
		for (int i = left+increment; i <= right; i++) {
			int j = i;
			int temp = array[i];
			while (j >= left+increment && array[j - increment] > temp) {
				array[j] = array[j - increment];
				j = j - increment;
			}
			array[j] = temp;
		}
		if (increment == 2) {
			increment = 1;
		} else {
			increment *= (5.0 / 11);
		}
	}
    }
    
    
    
    

    
    public static void main(String args[]) {

        int len = 100;//17907709; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
    double arr[] = BenchMarker.initArrayPos$NegRandomFloats(len);
    printArray("Before sorting", arr);
        double start = System.nanoTime();
         ShellSort.sort(arr);
         
         start = (System.nanoTime()-start)/1.0E6;
         printObject(len+" items sorted using ShellSort in "+start+"ms, recursions = "+AdvancedWittyBucketSort.recursions);     
         
         if(checkSortState(arr, 0, len-1)[2]==0){
             System.out.println("Sorted!");
         }
         else{
             System.out.println("Not Sorted");
         }
     printArray("After sorting", arr);
       //ty
    }//end method
}