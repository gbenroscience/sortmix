/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.gbenroscience.sortmix.experiments;

import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 
import static com.github.gbenroscience.sortmix.util.SortUtils.*; 

/**
 *
 * @author http://www.growingwiththeweb.com/2015/06/bucket-sort.html
 * 
 */
public class BucketSort2 {
  private static final int DEFAULT_BUCKET_SIZE = 5;

    public static void sort(int[] array) {
        sort(array, DEFAULT_BUCKET_SIZE);
    }

    public static void sort(int[] array, int bucketSize) {
        if (array.length == 0) {
            return;
        }

        // Determine minimum and maximum values
        Integer minValue = array[0];
        Integer maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            } else if (array[i] > maxValue) {
                maxValue = array[i];
            }
        }

        // Initialise buckets
        int bucketCount = (maxValue - minValue) / bucketSize + 1;
        List<List<Integer>> buckets = new ArrayList<List<Integer>>(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<Integer>());
        }

        // Distribute input array values into buckets
        for (int i = 0; i < array.length; i++) {
            buckets.get((array[i] - minValue) / bucketSize).add(array[i]);
        }

        // Sort buckets and place back into input array
        int currentIndex = 0;
        for (int i = 0; i < buckets.size(); i++) {
            Integer[] bucketArray = new Integer[buckets.get(i).size()];
            bucketArray = buckets.get(i).toArray(bucketArray);
            Arrays.sort(bucketArray);
            for (int j = 0; j < bucketArray.length; j++) {
                array[currentIndex++] = bucketArray[j];
            }
        }
    }
    
    
    
    public static void main(String[] args) {
       
        int len = 6000000;//17907709; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
    //double arr[] = BenchMarker.initArrayPos$NegRandomFloats(len);
    int arr[] = BenchMarker.initArrayRandomInts(len);
    
   // printArray("Before sorting", arr);
        double start = System.nanoTime();
BucketSort2.sort(arr);
         
         start = (System.nanoTime()-start)/1.0E6;
         printObject(len+" items sorted using BucketSort in "+start+"ms");     
         
         if(checkSortState(arr, 0, len-1)[2]==0){
             System.out.println("Sorted!");
         }
         else{
             System.out.println("Not Sorted");
         }
     //printArray("After sorting", arr);
       //ty 
        
        
    }
}