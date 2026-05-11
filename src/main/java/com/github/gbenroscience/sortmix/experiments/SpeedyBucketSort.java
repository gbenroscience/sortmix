/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.gbenroscience.sortmix.experiments;

import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import java.util.Random;
import static com.github.gbenroscience.sortmix.util.SortUtils.*;
/**
 * Objects of this class operate a kind
 * of distribution sort, which is highly
 * optimized for speed.
 * On my machine, only quicksort is faster.
 * This algorithm can sort arrays of up to
 * size 11175743.
 * Quicksort can do double this size on my machine.
 * @author GBEMIRO
 * @since 02:46am, Sunday, 7/20/2014
 */
public class SpeedyBucketSort{

        static int recursions = -1;
        /**
         * When true, cache optimization code is used.
         */
        private static boolean cacheOptimized = true;

    public static void setCacheOptimized(boolean cacheOptimized) {
        SpeedyBucketSort.cacheOptimized = cacheOptimized;
    }

    public static boolean isCacheOptimized() {
        return cacheOptimized;
    }
    
        
        
        
        
/**
 * 
 * @param x The array to be sorted
 * @param offset The offset from the beginning of the array, representing the index of the element where sorting is to begin.
 * @param i The index of the element whose suggested index is needed.
 * @param smallest The smallest element found in the portion of the array that is to be sorted.
 * @param range The biggest element found in the portion of the array that is to be sorted, minus the smallest element found in the same portion.
 * @param n The number of entries to be sorted minus 1.
 * @return the index suggested by the algorithm for the index'th entry of this array.
 */   
    private static int getSuggestedIndex(double x[], int offset, int i, double smallest, double range, int n){
       return offset + (int) ( (  (x[i] - smallest) * n )/range );
    }
    
   
/**
 * 
 * @param array the array to sort. 
 */   
public static void sort(double[]array){
    sort(array, 0,array.length-1);
}
    /**
     *
     * @param sortJob The array to be sorted.
     * @param left  The starting index of the array where we start sorting.
     * @param right The ending index.
     */
    public static void sort(double[] sortJob,int left,int right){
         int len = right - left +1;
        double extremities[] = checkSortState(sortJob,left,right);
        double smallest = extremities[0];
        double biggest =  extremities[1];
        double range = biggest - smallest;
        int n = len-1;
        boolean alreadySorted = extremities[2]==0?true:false || biggest==smallest;
        //biggest == smallest means that the array to be sorted has equal elements and as such is already sorted.
        ++recursions;
        if(alreadySorted){}//end if 
        
        else{
         

      
      
       int lim = right + 1;
      /**
       * Introduce some cache friendliness to the code.
       */
       if(len>=1000000 && cacheOptimized){
          recursivePartition(sortJob, left, right, 3);
       }
 /**
     * Stores the bucket sizes of suggested indices.
     * For example if bucketSizes[3] = 5; It means that the number
     * of entries that have a suggestedIndex of <code>suggestedIndices[3]</code>
     * is 5.
     * So say sortJob[3] = 7; Entry 7 is the current element to be sorted.
     * suggestedIndices[3]=4; means that the algorithm suggests that 7 should be
     * put at index 4.
     * To know the number of entries that also fall into this same bucket i.e.(have a suggestedIndex of 4),
     * use <code> int bucketSize = bucketSizes[4];</code> Its value will tell you how many elements fall into the bucket. 
     * 
     */       
      double[]  bucketSizes = new double[len];
      
 for(int i=left;i<lim;i++){
                ++bucketSizes[ getSuggestedIndex(sortJob, left, i, smallest, range, n)  - left ];
               }
  
  
            
 /**
  * An index i, in this array holds in each of its indices the total number of sorted elements that
  * are before the bucket referred to by the index
  * 
  * 
  */
      int[] numOfElementsBeforeBucket = new int[len];
      double x;
            int bucketSizeAccumulator=0;
for(int i=0;i<len;i++){
/*
    // if the current bucket size is greater than 0, that means a bucket of values
    exists there. Record the sum of sizes of all buckets before this bucket at that index
    of the numOfElementsBeforeBucket array. It tells us what index each bucket will start from.  
    */    
    if((x=bucketSizes[i])>0){
numOfElementsBeforeBucket[i] = bucketSizeAccumulator; 
    }
    bucketSizeAccumulator += x;//Accumulate bucket sizes in bucketSizeAccumulator
}

   //printArray("SpeedyBucketSort end entryIndices!", numOfElementsBeforeBucket);
//From this point, the bucketSizes array ceases to hold bucket sizes and becomes
// a temporary holder for the buckets themselves.

for(int i=left;i<lim;i++){
 /* 
    Note that bucketSizeBeforeIndex is equal to the starting index of the new bucket.
    Also note that the next lines enclosed within the code tags does exactly what the next uncommented line does. 
    <code> 
   int suggestedIndex = getSuggestedIndex(sortJob, left, j, smallest, range, n);
    int index = suggestedIndex-left;
    int bucketSizeBeforeIndex = numOfElementsBeforeBucket[index]; 
    bucketSizes[bucketSizeBeforeIndex] = sortJob[j];
    ++numOfElementsBeforeBucket[index]; 
    </code>
      the increment on numOfElementsBeforeBucket[index]++
    helps us to account for elements occuring before bucket + elements already added to bucket,
      so we get the exact index to place the next entry of this bucket.
    */
    
bucketSizes[ numOfElementsBeforeBucket[  getSuggestedIndex(sortJob, left, i, smallest, range, n) - left ]++  ] = sortJob[i];
 
 }//end for loop

    
//transfer the buckets into the portion of the array being currently sorted and then
//sort each of them recursively.
    System.arraycopy(bucketSizes, 0, sortJob, left, len);

        bucketSizes=null;
        numOfElementsBeforeBucket=null;
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
  
}
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
public static void sort(int []array){
    sort(array, 0,array.length-1);
}
    /**
     *
     * @param sortJob The array to be sorted.
     * @param left  The starting index of the array where we start sorting.
     * @param right The ending index.
     */
    public static void sort(int [] sortJob,int left,int right){
         int len = right - left +1;
        int extremities[] = checkSortState(sortJob,left,right);
        double smallest = extremities[0];
        double biggest =  extremities[1];
        double range = biggest - smallest;
        int n = len-1;
        boolean alreadySorted = extremities[2]==0?true:false || biggest==smallest;
        //biggest == smallest means that the array to be sorted has equal elements and as such is already sorted.
        ++recursions;
        if(alreadySorted){}//end if 
        
        else{
         

      
      
       int lim = right + 1;
      /**
       * Introduce some cache friendliness to the code.
       */
       if(len>=1000000 && cacheOptimized){
          recursivePartition(sortJob, left, right, 3);
       }
 /**
     * Stores the bucket sizes of suggested indices.
     * For example if bucketSizes[3] = 5; It means that the number
     * of entries that have a suggestedIndex of <code>suggestedIndices[3]</code>
     * is 5.
     * So say sortJob[3] = 7; Entry 7 is the current element to be sorted.
     * suggestedIndices[3]=4; means that the algorithm suggests that 7 should be
     * put at index 4.
     * To know the number of entries that also fall into this same bucket i.e.(have a suggestedIndex of 4),
     * use <code> int bucketSize = bucketSizes[4];</code> Its value will tell you how many elements fall into the bucket. 
     * 
     */       
      int[]  bucketSizes = new int[len];
      
 for(int i=left;i<lim;i++){
                ++bucketSizes[ getSuggestedIndex(sortJob, left, i, smallest, range, n)  - left ];
               }
  
  
            
 /**
  * An index i, in this array holds in each of its indices the total number of sorted elements that
  * are before the bucket referred to by the index
  * 
  * 
  */
      int[] numOfElementsBeforeBucket = new int[len];
      double x;
            int bucketSizeAccumulator=0;
for(int i=0;i<len;i++){
/*
    // if the current bucket size is greater than 0, that means a bucket of values
    exists there. Record the sum of sizes of all buckets before this bucket at that index
    of the numOfElementsBeforeBucket array. It tells us what index each bucket will start from.  
    */    
    if((x=bucketSizes[i])>0){
numOfElementsBeforeBucket[i] = bucketSizeAccumulator; 
    }
    bucketSizeAccumulator += x;//Accumulate bucket sizes in bucketSizeAccumulator
}

   //printArray("SpeedyBucketSort end entryIndices!", numOfElementsBeforeBucket);
//From this point, the bucketSizes array ceases to hold bucket sizes and becomes
// a temporary holder for the buckets themselves.

for(int i=left;i<lim;i++){
 /* 
    Note that bucketSizeBeforeIndex is equal to the starting index of the new bucket.
    Also note that the next lines enclosed within the code tags does exactly what the next uncommented line does. 
    <code> 
   int suggestedIndex = getSuggestedIndex(sortJob, left, j, smallest, range, n);
    int index = suggestedIndex-left;
    int bucketSizeBeforeIndex = numOfElementsBeforeBucket[index]; 
    bucketSizes[bucketSizeBeforeIndex] = sortJob[j];
    ++numOfElementsBeforeBucket[index]; 
    </code>
      the increment on numOfElementsBeforeBucket[index]++
    helps us to account for elements occuring before bucket + elements already added to bucket,
      so we get the exact index to place the next entry of this bucket.
    */
    
bucketSizes[ numOfElementsBeforeBucket[  getSuggestedIndex(sortJob, left, i, smallest, range, n) - left ]++  ] = sortJob[i];
 
 }//end for loop

    
//transfer the buckets into the portion of the array being currently sorted and then
//sort each of them recursively.
    System.arraycopy(bucketSizes, 0, sortJob, left, len);

        bucketSizes=null;
        numOfElementsBeforeBucket=null;
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
  
}
    }//end method.
    
    
    public static void main(String args[]){
Random r = new Random();
        int len = 15000000;//17907709; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
       int arr[] = BenchMarker.initArrayRandomInts(len);
     double start = System.nanoTime();
     SpeedyBucketSort.sort(arr);
        start = (System.nanoTime()-start)/1.0E6;
 
    printObject(len+" items sorted using SpeedyBucketSort in "+start+"ms");
        if(checkSortState(arr, 0, len-1)[2]==0){
             System.out.println("Sorted!");
         }
         else{
             System.out.println("Not Sorted");
         }
 // }
  
        

    // printArray("Sorted array is ", arr);
     
    }//end method
    
}//end class