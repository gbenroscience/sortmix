/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.gbenroscience.sortmix.experiments;


import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import java.util.Arrays;
import java.util.Random;
import static com.github.gbenroscience.sortmix.util.SortUtils.*;
/**
 * 
 * 2 array version of bucket sort
 * algorithm. About 1.5 times or so slower
 * than NewSort, but can handle larger arrays
 * due to its better memory management.
 * On my development machine with 3 GB RAM(2.75 GB usable),
 * AMD Sempron(tm) SI-42 2.1 GHz processor,
 * it can support a maximum array size of 17907709.
 *
 * @author GBEMIRO
 */
public class WittyBucketSort {
    
    static int recursions = -1;

    /**
     *
     * @param x The array to be sorted
     * @param offset The offset from the beginning of the array, representing
     * the index of the element where sorting is to begin.
     * @param i The index of the element whose suggested index is needed.
     * @param smallest The smallest element found in the portion of the array
     * that is to be sorted.
     * @param range The biggest element found in the portion of the array that
     * is to be sorted, minus the smallest element found in the same portion.
     * @param n The number of entries to be sorted minus 1.
     * @return the index suggested by the algorithm for the
     * index_th entry of this array.
     */
    private static int getSuggestedIndex(double x[], int offset, int i, double smallest, double range, int n) {
        return offset + (int) (((x[i] - smallest) * n) / range);
    }

    /**
     *
     * @param array the array to sort.
     */
    public static void sort(double[] array) {
        sort(array, 0, array.length-1);
    }
    /**
     *
     * @param sortJob The array to be sorted.
     * @param left The starting index of the array where we start sorting.
     * @param right The ending index in the array.
     */
    public static void sort(double[] sortJob, int left, int right) {
        int len = right - left +1;
        double extremities[] = checkSortState(sortJob, left, right);
        double smallest = extremities[0];
        double biggest = extremities[1];
        double range = biggest - smallest;
        int n = len - 1;
        boolean alreadySorted = extremities[2] == 0 ? true : false || (biggest == smallest);
        //biggest == smallest means that the array to be sorted has equal elements and as such is already sorted.
++recursions;
        if (alreadySorted) {
        }//end if 
        else {
            int lim = right + 1;
            
        /**
       * Introduce some cache friendliness to the code.
       */
       if(len>=1000000){
          //recursivePartition(sortJob, left, right,3);
       }    
            
                    int[] bucketSizes = new int[len];
     /**
      * Can introduce some cache friendliness
      *   Use only at large sizes of the array.
      */    
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
 
            for (int i = left; i < lim; i++) {
             ++bucketSizes[ getSuggestedIndex(sortJob, left, i, smallest, range, n) - left];
            }//end for loop
            

 
           // printArray("suggested indexes ", indices);
           // printArray("bucket sizes ", bucketSizes);

            /**
             * Converts the bucketSizes array from holding bucketSizes to holding
             * startingIndices for each bucket.
             * This is a master-stroke necessary to avoid introducing a third array
             * for this purpose. It helps us to increase our pre-recursive memory
             * efficiency by up to 33%..boosting on this machine maximum input array
             * size handling from about 11.15 million to about 17.9 million.
             * 
             * This memory gain however, is made at the expense of some speed, as 
             * it means that the array must be sorted in place in the next stage.
             * The algorithm for that is slightly slower as it means at least one extra swap
             * compared to that used at the same stage in class <code>NewSort</code>
             * An index i, in this array holds in each of its indices the total
             * number of sorted elements that are before the bucket referred to
             * by the index.
             * 
             */  
            int bucketSizeAccumulator=0;
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

         //  printArray("WittyBucketSort: indices before", bucketSizes);

    
            
            
            int swaps = 0, i=left;
         
            while( i < lim ){
//the corresponding element in the array has been sorted.     
                if(bucketSizes[i-left]<0){
                    i++;
                }//end if
                  else{
                int diff = getSuggestedIndex(sortJob, left, i, smallest, range, n)-left;
                
               
               
//Access the index in bucketSizes that gives you info about the index where you will place the ith element
//of the sortJob array in order for it to be partially or fully sorted.                
                int entryIndexValue = bucketSizes[diff];
/**
 * This is the index in the array to be sorted where the array element at i should be placed 
 * in order for it to be partially or fully sorted.
 */
                int location = (left + ( entryIndexValue < 0 ? -entryIndexValue : entryIndexValue ) );
           int diff_1 = location - left;
           
//indicates that a new entry is about to be added, so indicate the next index to be filled in the bucket.
                    if(entryIndexValue>0){
                    ++bucketSizes[diff];
                    }//end if
                    else if(entryIndexValue<0){
                    --bucketSizes[diff];   
                    }//end else if
    //ty
        /**
         * This variable checks the mirror location of an index of sortJob
         * in bucketSizes and stamps them as sorted by negating their values or storing
         * -1 in them depending on whether or not that value will still be needed in future.
         */        
                int bucketValAtLocation =  bucketSizes[diff_1];
                
//mark this location as sorted so that it wont be sorted again if it is encountered.
                    
                  if(bucketValAtLocation > 0){
                    bucketSizes[diff_1] *= -1;
                    }//end if
                    else if(bucketValAtLocation==0){
                    bucketSizes[diff_1] = -1;
                    }//end else if                    
                    
                    
                    
                    swap(sortJob, i, location);
                    swaps++;

                    
                }//end else
         if(swaps == len){
             break;
         }//end if
            }//end while loop
             
            bucketSizes = null;
  for(int index=left;index<=right;){
   
int start = index;

 int suggestedIndex = getSuggestedIndex(sortJob, left, start, smallest, range, n);
 while( index<lim ){
 if(suggestedIndex==getSuggestedIndex(sortJob, left, index, smallest, range, n)){
     index++;
 }//end if
 else{
     break;
 }
 }//end inner while loop
if(index>=right+1){
    index = right; 
}

 int bucketSize = index - start+1;

 if(bucketSize==1 && index == right){break;}
else if(bucketSize>=2&&bucketSize<=MAX_INSERTION_SORT){
      insertionSort(sortJob, start, index);
}//end else if
else{
  sort(sortJob,start, index);
    }//end else
}//end for loop 
     }//end else
++recursions;

    }//end method.

    
    /**
     *
     * @param x The array to be sorted
     * @param offset The offset from the beginning of the array, representing
     * the index of the element where sorting is to begin.
     * @param i The index of the element whose suggested
     * suggestedIndexAt_i is needed.
     * @param smallest The smallest element found in the portion of the array
     * that is to be sorted.
     * @param range The biggest element found in the portion of the array that
     * is to be sorted, minus the smallest element found in the same portion.
     * @param n The number of entries to be sorted minus 1.
     * @return the index suggested by the algorithm for the
     * index_th entry of this array.
     */
    private static int getSuggestedIndex(int x[], int offset, int i, double smallest, double range, int n) {
        double entry = x[i];
        return offset + (int) (((entry - smallest) * n) / range);
    }    
    /**
     *
     * @param array the array to sort.
     */
    public static void sort(int[] array) {
        sort(array, 0, array.length-1);
    }
    /**
     *
     * @param sortJob The array to be sorted.
     * @param left The starting index of the array where we start sorting.
     * @param right The ending index in the array.
     */
    public static void sort(int[] sortJob, int left, int right) {
        int len = right - left +1;
        int extremities[] = checkSortState(sortJob, left, right);
        int smallest = extremities[0];
        int biggest = extremities[1];
        double range = biggest - smallest;
        int n = len - 1;
        boolean alreadySorted = extremities[2] == 0 ? true : false || (biggest == smallest);
        //biggest == smallest means that the array to be sorted has equal elements and as such is already sorted.
++recursions;
        if (alreadySorted) {
        }//end if 
        else {
            int lim = right + 1;
            
        /**
       * Introduce some cache friendliness to the code.
       */
       if(len>=1000000){
          recursivePartition(sortJob, left, right,3);
       }    
            
                    int[] bucketSizes = new int[len];
     /**
      * Can introduce some cache friendliness
      *   Use only at large sizes of the array.
      */    
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
 
            for (int i = left; i < lim; i++) {
             ++bucketSizes[ getSuggestedIndex(sortJob, left, i, smallest, range, n) - left];
            }//end for loop
            

 
           // printArray("suggested indexes ", indices);
           // printArray("bucket sizes ", bucketSizes);

            /**
             * Converts the bucketSizes array from holding bucketSizes to holding
             * startingIndices for each bucket.
             * This is a master-stroke necessary to avoid introducing a third array
             * for this purpose. It helps us to increase our pre-recursive memory
             * efficiency by up to 33%..boosting on this machine maximum input array
             * size handling from about 11.15 million to about 17.9 million.
             * 
             * This memory gain however, is made at the expense of some speed, as 
             * it means that the array must be sorted in place in the next stage.
             * The algorithm for that is slightly slower as it means at least one extra swap
             * compared to that used at the same stage in class <code>NewSort</code>
             * An index i, in this array holds in each of its indices the total
             * number of sorted elements that are before the bucket referred to
             * by the index.
             * 
             */  
            int bucketSizeAccumulator=0;
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

         //  printArray("WittyBucketSort: indices before", bucketSizes);

    
            
            
            int swaps = 0, i=left;
         
            while( i < lim ){
//the corresponding element in the array has been sorted.     
                if(bucketSizes[i-left]<0){
                    i++;
                }//end if
                  else{
                int diff = getSuggestedIndex(sortJob, left, i, smallest, range, n)-left;
                
               
               
//Access the index in bucketSizes that gives you info about the index where you will place the ith element
//of the sortJob array in order for it to be partially or fully sorted.                
                int entryIndexValue = bucketSizes[diff];
/**
 * This is the index in the array to be sorted where the array element at i should be placed 
 * in order for it to be partially or fully sorted.
 */
                int location = (left + ( entryIndexValue < 0 ? -entryIndexValue : entryIndexValue ) );
           int diff_1 = location - left;
           
//indicates that a new entry is about to be added, so indicate the next index to be filled in the bucket.
                    if(entryIndexValue>0){
                    ++bucketSizes[diff];
                    }//end if
                    else if(entryIndexValue<0){
                    --bucketSizes[diff];   
                    }//end else if
    //ty
        /**
         * This variable checks the mirror location of an index of sortJob
         * in bucketSizes and stamps them as sorted by negating their values or storing
         * -1 in them depending on whether or not that value will still be needed in future.
         */        
                int bucketValAtLocation =  bucketSizes[diff_1];
                
//mark this location as sorted so that it wont be sorted again if it is encountered.
                    
                  if(bucketValAtLocation > 0){
                    bucketSizes[diff_1] *= -1;
                    }//end if
                    else if(bucketValAtLocation==0){
                    bucketSizes[diff_1] = -1;
                    }//end else if                    
                    
                    
                    
                    swap(sortJob, i, location);
                    swaps++;

                    
                }//end else
         if(swaps == len){
             break;
         }//end if
            }//end while loop
             
            bucketSizes = null;
  for(int index=left;index<=right;){
   
int start = index;

 int suggestedIndex = getSuggestedIndex(sortJob, left, start, smallest, range, n);
 while( index<lim ){
 if(suggestedIndex==getSuggestedIndex(sortJob, left, index, smallest, range, n)){
     index++;
 }//end if
 else{
     break;
 }
 }//end inner while loop
if(index>=right+1){
    index = right; 
}

 int bucketSize = index - start+1;

 if(bucketSize==1 && index == right){break;}
else if(bucketSize>=2&&bucketSize<=MAX_INSERTION_SORT){
      insertionSort(sortJob, start, index);
}//end else if
else{
  sort(sortJob,start, index);
    }//end else
}//end for loop 
     }//end else
++recursions;

    }//end method.
    
    

    public static void main(String args[]) {

        int len = 20000000;//17907709; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
        int arr[] = BenchMarker.initArrayPos$NegRandomInts(len);
    //    printArray("Before sorting", arr);
        double start = System.nanoTime();
         WittyBucketSort.sort(arr);//.sort(arr);
         start = (System.nanoTime()-start)/1.0E6;
         printObject(len+" items sorted using WittyBucketSort in "+start+"ms, recursions = "+WittyBucketSort.recursions);     
         
         if(checkSortState(arr, 0, len-1)[2]==0){
             System.out.println("Sorted!");
         }
         else{
             System.out.println("Not Sorted");
         }
      //printArray("After sorting", arr);
       //ty
    }//end method

}