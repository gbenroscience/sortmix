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
public class NonRecursiveMergeSort {



	// Bottom-up merge sort
	public static void sort(int[] array) {
		if(array.length < 2) {
			// We consider the array already sorted, no change is done
			return;
		}
		// The size of the sub-arrays . Constantly changing .
		int step = 1;
		// startL - start index for left sub-array
		// startR - start index for the right sub-array
		int startL, startR;

		while(step < array.length) {
			startL = 0;
			startR = step;
			while(startR + step <= array.length) {
				mergeArrays(array, startL, startL + step, startR, startR + step);
				// System.out.printf("startL=%d, stopL=%d, startR=%d, stopR=%dn",
					// startL, startL + step, startR, startR + step);
				startL = startR + step;
				startR = startL + step;
			}
			// System.out.printf("- - - with step = %dn", step);
			if(startR < array.length) {
				mergeArrays(array, startL, startL + step, startR, array.length);
				// System.out.printf("* startL=%d, stopL=%d, startR=%d, stopR=%dn",
					// startL, startL + step, startR, array.length);
			}
			step *= 2;
		}
	}

        
   	// Bottom-up merge sort
	public static void sort(double[] array) {
		if(array.length < 2) {
			// We consider the array already sorted, no change is done
			return;
		}
		// The size of the sub-arrays . Constantly changing .
		int step = 1;
		// startL - start index for left sub-array
		// startR - start index for the right sub-array
		int startL, startR;

		while(step < array.length) {
			startL = 0;
			startR = step;
			while(startR + step <= array.length) {
				mergeArrays(array, startL, startL + step, startR, startR + step);
				// System.out.printf("startL=%d, stopL=%d, startR=%d, stopR=%dn",
					// startL, startL + step, startR, startR + step);
				startL = startR + step;
				startR = startL + step;
			}
			// System.out.printf("- - - with step = %dn", step);
			if(startR < array.length) {
				mergeArrays(array, startL, startL + step, startR, array.length);
				// System.out.printf("* startL=%d, stopL=%d, startR=%d, stopR=%dn",
					// startL, startL + step, startR, array.length);
			}
			step *= 2;
		}
	}     
        
        
	// Merge to already sorted blocks
	public static void mergeArrays(int[] array, int startL, int stopL,
		int startR, int stopR) {
		// Additional arrays needed for merging
		int[] right = new int[stopR - startR + 1];
		int[] left = new int[stopL - startL + 1];

		// Copy the elements to the additional arrays
		for(int i = 0, k = startR; i < (right.length - 1); ++i, ++k) {
			right[i] = array[k];
		}
		for(int i = 0, k = startL; i < (left.length - 1); ++i, ++k) {
			left[i] = array[k];
		}

		// Adding sentinel values
		right[right.length-1] = Integer.MAX_VALUE;
		left[left.length-1] = Integer.MAX_VALUE;

		// Merging the two sorted arrays into the initial one
		for(int k = startL, m = 0, n = 0; k < stopR; ++k) {
			if(left[m] <= right[n]) {
				array[k] = left[m];
				m++;
			}
			else {
				array[k] = right[n];
				n++;
			}
		}
	}

	// Merge to already sorted blocks
	public static void mergeArrays(double[] array, int startL, int stopL,
		int startR, int stopR) {
		// Additional arrays needed for merging
		double[] right = new double[stopR - startR + 1];
		double[] left = new double[stopL - startL + 1];

		// Copy the elements to the additional arrays
		for(int i = 0, k = startR; i < (right.length - 1); ++i, ++k) {
			right[i] = array[k];
		}
		for(int i = 0, k = startL; i < (left.length - 1); ++i, ++k) {
			left[i] = array[k];
		}

		// Adding sentinel values
		right[right.length-1] = Integer.MAX_VALUE;
		left[left.length-1] = Integer.MAX_VALUE;

		// Merging the two sorted arrays into the initial one
		for(int k = startL, m = 0, n = 0; k < stopR; ++k) {
			if(left[m] <= right[n]) {
				array[k] = left[m];
				m++;
			}
			else {
				array[k] = right[n];
				n++;
			}
		}
	}        
        
        
    public static void main(String args[]) {

       // int len = 17907000;//17907709; //8940705;
        int len = 22343533;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
    int arr[] = BenchMarker.initArrayRandomInts(len);
    //printArray("Before sorting", arr);
        double start = System.nanoTime();
         NonRecursiveMergeSort.sort(arr);
         
         start = (System.nanoTime()-start)/1.0E6;
         printObject(len+" items sorted using NonRecursiveMergeSort in "+start+"ms, recursions = "+AdvancedWittyBucketSort.recursions);     
         
         if(checkSortState(arr, 0, len-1)[2]==0){
             System.out.println("Sorted!");
         }
         else{
             System.out.println("Not Sorted");
         }
    // printArray("After sorting", arr);
       //ty
    }//end method
}