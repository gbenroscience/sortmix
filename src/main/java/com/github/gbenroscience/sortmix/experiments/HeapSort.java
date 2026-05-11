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

public class HeapSort 
{    
    private static int N;
    
    
        /**
     *
     * @param array  The array to be sorted.
     * @param left The starting index of the array where we start sorting.
     * @param right The ending index in the array.
     */
    public static void sort(double array[]){

	int count = array.length;
 
	//first place a in max-heap order
	heapify(array, count);
 
	int end = count - 1;
	while(end > 0){
		//swap the root(maximum value) of the heap with the
		//last element of the heap
		double tmp = array[end];
		array[end] = array[0];
		array[0] = tmp;
		//put the heap back in max-heap order
		siftDown(array, 0, end - 1);
		//decrement the size of the heap so that the previous
		//max value will stay in its proper place
		end--;
	}
}
 
public static void heapify(double [] a, int count){
	//start is assigned the index in a of the last parent node
	int start = (count - 2) / 2; //binary heap
 
	while(start >= 0){
		//sift down the node at index start to the proper place
		//such that all nodes below the start index are in heap
		//order
		siftDown(a, start, count - 1);
		start--;
	}
	//after sifting down the root all nodes/elements are in heap order
}
 
public static void siftDown(double [] a, int start, int end){
	//end represents the limit of how far down the heap to sift
	int root = start;
 
	while((root * 2 + 1) <= end){      //While the root has at least one child
		int child = root * 2 + 1;           //root*2+1 points to the left child
		//if the child has a sibling and the child's value is less than its sibling's...
		if(child + 1 <= end && a[child] < a[child + 1])
			child = child + 1;           //... then point to the right child instead
		if(a[root] < a[child]){     //out of max-heap order
			double tmp = a[root];
			a[root] = a[child];
			a[child] = tmp;
			root = child;                //repeat to continue sifting down the child now
		}else
			return;
	}
}
    
    
    
    
    
    
    
    
    /**
     *
     * @param array  The array to be sorted.
     * @param left The starting index of the array where we start sorting.
     * @param right The ending index in the array.
     */
    public static void sort(int array[]){

	int count = array.length;
 
	//first place a in max-heap order
	heapify(array, count);
 
	int end = count - 1;
	while(end > 0){
		//swap the root(maximum value) of the heap with the
		//last element of the heap
		int tmp = array[end];
		array[end] = array[0];
		array[0] = tmp;
		//put the heap back in max-heap order
		siftDown(array, 0, end - 1);
		//decrement the size of the heap so that the previous
		//max value will stay in its proper place
		end--;
	}
}
 
public static void heapify(int[] a, int count){
	//start is assigned the index in a of the last parent node
	int start = (count - 2) / 2; //binary heap
 
	while(start >= 0){
		//sift down the node at index start to the proper place
		//such that all nodes below the start index are in heap
		//order
		siftDown(a, start, count - 1);
		start--;
	}
	//after sifting down the root all nodes/elements are in heap order
}
 
public static void siftDown(int[] a, int start, int end){
	//end represents the limit of how far down the heap to sift
	int root = start;
 
	while((root * 2 + 1) <= end){      //While the root has at least one child
		int child = root * 2 + 1;           //root*2+1 points to the left child
		//if the child has a sibling and the child's value is less than its sibling's...
		if(child + 1 <= end && a[child] < a[child + 1])
			child = child + 1;           //... then point to the right child instead
		if(a[root] < a[child]){     //out of max-heap order
			int tmp = a[root];
			a[root] = a[child];
			a[child] = tmp;
			root = child;                //repeat to continue sifting down the child now
		}else
			return;
	}
}
    
    
    
    
      
    public static void main(String args[]) {

        int len = 30000000;//17907709; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
    int arr[] = BenchMarker.initArrayPos$NegRandomInts(len);
   // printArray("Before sorting", arr);
        double start = System.nanoTime();
         HeapSort.sort(arr);
         
         start = (System.nanoTime()-start)/1.0E6;
         printObject(len+" items sorted using HeapSort in "+start+"ms, recursions = ");     
         
         if(checkSortState(arr, 0, len-1)[2]==0){
             System.out.println("Sorted!");
         }
         else{
             System.out.println("Not Sorted");
         }
   //  printArray("After sorting", arr);
       //ty
    }//end method
}    
