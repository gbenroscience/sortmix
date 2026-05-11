/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.gbenroscience.sortmix.experiments;

import java.util.Arrays;
import java.util.Random;

/**
 * Max array size that this sort can handle on host machine is 8940705.
 * @author GBEMIRO
 */
public class ClusterSortOptimized {
 
    private double[]sorted;
    double sortTime;
   
    public ClusterSortOptimized( double array[] ){
        this.sorted = array;
    }

    
    
    
    /**
     * 
     * @param array An array of numbers to be sorted.
     * @param offset the index of the array where sorting should start/
     * @param numofchars the number of items to sort starting from offset
     * @return an array containing the index of the smallest number 
     * in index 0 and the index of the biggest number in index 1
     * and 0 in index 2 if the array is already sorted.Else it contains
     * 1 in index 2 if the array is not already sorted.
     */
    public int[] getSmallestAndBiggestIndicesAndSortState( double[] array,int offset,int numofchars ){
    boolean alreadysorted=true;
        double smallest = array[offset];
        double biggest = array[offset];
        
        int smallestindex=offset;
        int biggestindex=offset;
        int limit = offset+numofchars;
        
        for(int i=offset;i<limit;i++){
            if(smallest>array[i]){
                smallest = array[i];
                smallestindex = i;
            }
            if(biggest<array[i]){
                biggest = array[i];
                biggestindex = i;
            }
            
            if(alreadysorted){
            if( i+1 <limit && array[i]>array[i+1] ){
                alreadysorted=false;
            }
            }
            
        }//end for loop
        return new int[]{smallestindex,biggestindex,alreadysorted?0:1};
    }//end method
/**
 * 
 * @param array the array to sort. 
 */   
public static void sort(double[]array){
    new ClusterSortOptimized(array).sort(array, 0,array.length);
}
    /**
     * 
     * @param sortJob The array to be sorted.
     * We break down the array to be sorted into an array containing
     * the indices suggested for each of the numbers in the original
     * array.
     */
    private void sort(double[] sortJob,int offset,int len){
        int extremities[] = getSmallestAndBiggestIndicesAndSortState(sortJob,offset,len);
        double smallest = sortJob[extremities[0]];
        double biggest = sortJob[extremities[1]];
        boolean alreadySorted = extremities[2]==0?true:false || biggest==smallest;

        //biggest == smallest means that the array to be sorted has equal elements and as such is already sorted.
        
        if(alreadySorted){
         return; 
        }//end if 
        else{
         
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
      
    /**
     * Stores the suggested indices.
     */    
      int[]  suggestedIndices = new int[len];
        
        /**
         * This variable represents the number of indices that will be unbounded to any sort value after 
         * the sort index generator has run. If we know this value, then we can minimize the size of the
         * data array to be used in the <code>generatePartialSort</code> block and so win back some memory and speed.
         */
         int countZeroIndices=len;
 
        reduceToSuggestedIndices:{
            double range = biggest-smallest; 
            int n = len-1;
            for(int i=offset;i<offset+len;i++){
             int suggestedIndex = offset+(int) ( (  (sortJob[i] - smallest) * n )/  (range) );
                /**
                 * Record the number of occurrences of each suggested index.
                 */
                ++bucketSizes[suggestedIndex-offset];
                //Store the suggestedIndex at a location that is offset steps away after the second half of the array.
                suggestedIndices[i-offset] = suggestedIndex;
                if(bucketSizes[suggestedIndex-offset]==1){
                --countZeroIndices;
                        }
          }
        }//end reduction block

         /**
          * Records the valid starting index for 
          * the clusters to be formed and the sorted numbers to be generated.
          */
        int[] startingIndices=new int[len];
int count=0;
int lastValidIndex=0;
double []data = new double[len];
/**
 * This for loop generates the startingIndices.
 * The startingIndices array is such that:
 * For a given i,where i represents a suggestedIndex,
 * startingIndices[i] stores the accurate value of the index where
 * the cluster formed by i will start in the partially sorted array.
 * If i is not a valid suggestedIndex,then startingIndices[i] will store -1.
 * 
 */
for(int i=offset;i<offset+len;i++){
    data[i-offset]=Double.NaN;
 //means: if the frequency(stored at(i-offset) of the suggestedIndex (stored at len+i-offset) is greater than 0, then run the if block.   
    if(bucketSizes[i-offset]>0){
    if(i==offset){//suggestedIndex==0
     startingIndices[i-offset] = offset;
     lastValidIndex=i-offset;
     count=offset;
    }//end if
    else{
    if(startingIndices[i-offset-1] != -1){
          count=startingIndices[i-offset-1]+bucketSizes[i-offset-1];
          lastValidIndex = i-offset;
    }//end if
    else{
        count=startingIndices[lastValidIndex]+bucketSizes[lastValidIndex];
        lastValidIndex=i-offset;
    }//end else
     startingIndices[i-offset] = count;
    }//end else
    }
    //the index is not suggested at all..i.e. its frequency of suggestion is 0..
    else{
        startingIndices[i-offset]=-1;
    }
}//end for loop


        /**
         * Generate primordial soup
         * of sorted clusters and free sorted numbers.
         */
        generatePartialSort:{
             for(int i=offset;i<offset+len;i++){
                 int suggestedIndex = suggestedIndices[i-offset];
//The starting index for the first element in the cluster.
               int startIndex = startingIndices[suggestedIndex-offset];
//Find the stop index for this cluster in the last index of the cluster.
               int stopIndex = startIndex+bucketSizes[suggestedIndex-offset]-1;
                 //simple value,sortable instantly after this stage
                 if(bucketSizes[suggestedIndex-offset] == 1 ){
                     data[startIndex-offset] = sortJob[i];
                 }//end else if
                 else if(bucketSizes[suggestedIndex-offset] > 1 ){

               int index = (int) (startIndex+data[stopIndex-offset]);//......
                     if(!Double.isNaN( data[stopIndex-offset] ) ){
                       if( index<stopIndex){
                            data[index-offset] = sortJob[i];
 // While you are not yet at the end of the array,increment the current index just accessed in the cluster
//  which is stored in the last index of the array
                            ++data[stopIndex-offset];
                       }
                       else{
//Now you are at the end of the array, you don't need to access this array again after this, so you can overwrite
//the number of times the array has been accessed with the rightful occupant, the element in backup that ought to be stored there.                           
                           data[stopIndex-offset] = sortJob[i];
                        }
                     }//end if
                     else{
                         //A new cluster is forming!
                         //Get the index where the last element of the cluster will be.
                         //At that position, store the number of items you have added to the cluster.
                        data[startIndex-offset] = sortJob[i]; //append value and exit.
                            data[stopIndex-offset]=1;//put the value of the current index of the cluster that has been accessed here.
                     }//end else
                 }//end else if    
            }//end for loop
           
    if(recursions==0){ 
    sorted=sortJob=data;//force sorted,sortJob and data to refer to the same object.
    }
               }//end generate partial sort
            
 
data=new double[0];
            
int startIndex ,nextStartIndex = 0;
for(int i=offset;i<offset+len;){
if(startingIndices[i-offset]!=-1){
             //The starting index for the first element in the cluster.
               startIndex = startingIndices[i-offset];
                   while(i < offset+len-1 && startingIndices[++i-offset] == -1 ){}
                   nextStartIndex = startingIndices[i-offset];
               int freq = nextStartIndex-startIndex;
               
               if(freq!=1){
               
                   if( freq>=2 && freq<=7 ){
                      smallArraySort(sortJob,startIndex,freq); 
                   }
                   else{
                       ++recursions;
                     sort(sortJob,startIndex,freq);    
                   }
               }
if(startIndex==offset+len-1 || startIndex==nextStartIndex){
    break;
}   //end if
}//end if              

             }//end for loop
             
        
        }    }//end method.
    
    /**
     * 
     * @param array An array of double numbers.
     * If the array size is 2 it uses a direct swap to sort it.
     * Else it uses an insertion sort to solve it.
     */
    public static void smallArraySort( double[] array,int offset,int len){
        if( len == 2 ){
          if(array[offset]>array[offset+1]){
              swap(array, offset, offset+1);
          }
          
        }//end if
        else{
            insertionSort(array, offset, len);
        }
  //    System.out.println("Optimizing for array.length = "+len);  
  //    printArray(array);
    }//end method

    
    
    int recursions;
 
    
    /**
     * @param descr A string describing what is being printed.
     * @param arr The array whose content is to be printed.
     * @param offset  The starting index in the array where printing is to begin.
     * @param len The number of characters to print.
     * 
     */
    public static void printArray(String descr,double[] arr,int offset,int len){
        StringBuilder builder = new StringBuilder(descr+":\n");
        builder.append("[");
       for(int i=offset;i<offset+len;i++){
              builder.append(arr[i]);
              builder.append(",");
         }
        builder.deleteCharAt(builder.length()-1);
        builder.append("]");
        System.out.println(builder);

        
    }
    /**
     * @param descr A string describing what is being printed.
     * @param arr The array whose content is to be printed.
     */
    public static void printArray(String descr,double[] arr){
printArray(descr, arr, 0, arr.length);
    }
    /**
     * @param descr A string describing what is being printed.
     * @param arr The array whose content is to be printed.
     */
    public static void printArray(String descr,int[] arr){
printArray(descr, arr, 0, arr.length);
    }
    /**
     * @param descr A string describing what is being printed.
     * @param arr The array whose content is to be printed.
     * @param offset  The starting index in the array where printing is to begin.
     * @param len The number of characters to print.
     * 
     */
    public static void printArray(String descr,int[] arr,int offset,int len){
        StringBuilder builder = new StringBuilder(descr+":\n");
        builder.append("[");
       for(int i=offset;i<offset+len;i++){
              builder.append(arr[i]);
              builder.append(",");
         }
        builder.deleteCharAt(builder.length()-1);
        builder.append("]");
        System.out.println(builder);
  
        
    }
    
    /**
     * Swaps x[index1] with x[index2].
     */
    private static void swap(double x[], int index1, int index2) {
        double t = x[index1];
        x[index1] = x[index2];
        x[index2] = t;
    }
    /**
     * 
     * @param x The array to be sorted.
     * @param offset The index where we start sorting
     * @param len The number of characters to sort
     * This method is used for small array sorting by the
     * main cluster sort code.
     * 
     * For an array having <code>n<code> elements,
     * If there are no duplicate elements,then
     * If n is even,
     * this method performs a total of (n*(n+2))/4 sorting operations.
     * If n is odd however, this method performs a total of (n*(n+2)+1)/4 = ((n+1)/2)^2.
     * If there are duplicate elements, the count will be even less.
     */
    private static void insertionSort(double[]x,int offset,int len){
        
       double sm;
      double bg ;
      int smallIndex;
    int bigIndex;
    int j=0;
  for(int i=offset;i<offset+len/2;i++){
      smallIndex=bigIndex=i;
      sm = bg = x[smallIndex];
      for(j=i;j<2*offset+len-i;j++){
      if(sm>x[j]){
          sm=x[j];
          smallIndex=j;
      }
      if(bg<x[j]){
          bg=x[j];
          bigIndex=j;
      }      
  }//end inner for loop
      
           
      if(sm<bg){
    //small guy is coming to where big guy currently is and small guy is currently at where big guy is coming.      
          if(i==bigIndex && j-1 == smallIndex ){
        swap(x, bigIndex,j-1);
           } 
          //small guy is coming to where big guy currently is and small guy is not at big guy's final destination. 
         else if(i==bigIndex && j-1 != smallIndex ){ 
        swap(x, bigIndex,j-1); 
        swap(x, smallIndex,i); 
           } 
          //big guy is coming to where small guy currently is and big guy is not at small guy's final destination. 
          else if(i!=bigIndex && j-1 == smallIndex ){
        swap(x, smallIndex,i); 
        swap(x, bigIndex,j-1);
        } 
          
          else if( i != bigIndex ){
        swap(x, smallIndex,i);  
        swap(x, bigIndex,j-1);
           }
      }
      else if(sm==bg){
          break;
      }
      
     // System.out.println("On pass "+i);
     // printArray(x);
 
  }//end outer for loop
    }//end method
    
    public static void main(String args[]){
        
        Random r = new Random();
        
    
     //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
        
       double arr[] = new double[10000000];
        int len = arr.length;
           // System.out.println("Array has "+len+" characters.");
        for(int i=0;i<len;i++){
             arr[i] = r.nextDouble();//Int(len*10000); 
        }
        //printArray("Raw array",arr);
        ClusterSortOptimized clusterSort = new ClusterSortOptimized(arr);
        double start = System.nanoTime();
        
        clusterSort.sort(arr);
        
        //Arrays.sort(arr,0,len);
        start = (System.nanoTime()-start)/1.0E6;
        
      if(len<=10000000){
        printArray("Sort time is "+start+" ms and sort output array is",new double[]{});
          System.out.println("recursions = "+clusterSort.recursions);
      }
      else {
            printArray("Sort time is "+start+" ms and sort output array is",new double[]{});
        }
     
    }//end method
    
}//end class