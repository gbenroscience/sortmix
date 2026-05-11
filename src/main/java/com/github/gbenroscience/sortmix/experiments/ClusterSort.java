/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.gbenroscience.sortmix.experiments;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author GBEMIRO
 */
public class ClusterSort {
 
    private static double[]sorted;
    private static double data[];
    private static int occurrences[];   
    private static int recursions;
 
   

    
    
    
    /**
     * 
     * @param array An array of numbers to be sorted.
     * @return an array containing the index of the smallest number 
     * in index 0 and the index of the biggest number in index 1
     * and 0 in index 2 if the array is already sorted.Else it contains
     * 1 in index 2 if the array is not already sorted.
     */
    private static int[] getSmallestAndBiggestIndicesAndSortState( double[] array,int offset,int numofchars ){
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
     * @param sortJob The array to be sorted.
     * We break down the array to be sorted into an array containing
     * the indices suggested for each of the numbers in the original
     * array.
     */
    private static void doSort(double[] sortJob,int offset,int len){
        int extremities[] = getSmallestAndBiggestIndicesAndSortState(sortJob,offset,len);
        double smallest = sortJob[extremities[0]];
        double biggest = sortJob[extremities[1]];
        boolean alreadySorted = extremities[2]==0?true:false || biggest==smallest;
        //biggest == smallest means that the array to be sorted has equal elements and as such is already sorted.
        
  if(alreadySorted){
      return;
  }
  else{
        
        /**
         * Counts the occurrences of each index.
         */
        occurrences = new int[2*len];
        /**
         * This variable represents the number of indices that will be unbounded to any sort value after 
         * the sort index generator has run. If we know this value, then we can minimize the size of the
         * data array to be used in the <code>generatePartialSort</code> block and so win back some memory and speed.
         */
         int countZeroIndices=len;
 int max = offset+len;
        reduceToSuggestedIndices:{
            double range = biggest-smallest; 
            double n = len-1;
            double ratio = n/range;
            int biggestIndex = (int) (offset+n);
            
             /**
              * The correct formula in full is:
              * int suggestedIndex = offset+(int) ( (  (sortJob[i] - smallest) * n )/  (range) );
              * But to optimize the loop below by avoiding repetitive calculations, we perform the constant parts
              * of the formula outside the loop and use the result in the loop..giving
              * <code> int suggestedIndex = offset+(int) (   (sortJob[i] - smallest) * ratio  ); </code>
              */
             //System.out.println("Done 3");
            
            for(int i=offset;i<max;i++){
             // int suggestedIndex = offset+(int) (   (sortJob[i] - smallest) * ratio  );
             int suggestedIndex = (sortJob[i] == biggest)?biggestIndex:offset+(int) (   (sortJob[i] - smallest) * ratio  );
               
                //int suggestedIndex =  offset+(int) (   (sortJob[i] - smallest) * n/range );
                /**
                 * Record the number of occurrences of each suggested index.
                 * Based on these occurrences, we are going to create an object array, determine its size
                 * and then generate arrays or numbers entry into this object array.
                 * If occurrences = 1, we place the number at that index,if greater, we stuff the numbers into an array and
                 * insert them at that index.
                 * Also record the suggested keys in the second half of the occurrences array.
                 */
                ++occurrences[suggestedIndex-offset];
                occurrences[i-offset+len] =   suggestedIndex;
                
                if(occurrences[suggestedIndex-offset]==1){
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
data = new double[len];

/**
 * This for loop generates the startingIndices.
 * The startingIndices array is such that:
 * For a given i,where i represents a suggestedIndex,
 * startingIndices[i] stores the accurate value of the index where
 * the cluster formed by i will start in the partially sorted array.
 * If i is not a valid suggestedIndex,then startingIndices[i] will store -1.
 * 
 */int currindex=0;
for(int i=offset;i<max;i++){
       currindex = i-offset;
    data[currindex]=Double.NaN;

    
    if(occurrences[currindex]>0){
    if(currindex==0){
     startingIndices[currindex] = offset;
     lastValidIndex=currindex;
     count=offset;
    }//end if
    else{
    if(startingIndices[currindex-1] != -1){
          count=startingIndices[currindex-1]+occurrences[currindex-1];
          lastValidIndex=currindex;
    }//end if
    else{
        count=startingIndices[lastValidIndex]+occurrences[lastValidIndex];
        lastValidIndex=currindex;
    }//end else
     startingIndices[currindex] = count;
    }//end else
    }
    //the index is not suggested at all..i.e. its frequency of suggestion is 0..
    else{
        startingIndices[currindex]=-1;
    }
}//end for loop


        /**
         * Generate primordial soup
         * of sorted clusters and free sorted numbers.
         */
        generatePartialSort:{
            int value = len-offset;
             for(int i=offset;i<max;i++){
                 int suggestedIndex = occurrences[i+value];
//The starting index for the first element in the cluster.
               int startIndex = startingIndices[suggestedIndex-offset];
//Find the stop index for this cluster in the last index of the cluster.
               int stopIndex = startIndex+occurrences[suggestedIndex-offset]-1;
                 //simple value,sortable instantly after this stage
                 if(occurrences[suggestedIndex-offset] == 1 ){
                     data[startIndex-offset] = sortJob[i];
                 }//end else if
                 else if(occurrences[suggestedIndex-offset] > 1 ){

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
            


data=null;
occurrences=null;
            
int startIndex ,nextStartIndex,freq= 0;

for(int i=offset;i<max;){
if(startingIndices[i-offset]!=-1){
             //The starting index for the first element in the cluster.
               startIndex = startingIndices[i-offset];
                   while(i < max-1 && startingIndices[++i-offset] == -1 ){}
                   nextStartIndex = startingIndices[i-offset];
               freq = nextStartIndex-startIndex;
               
               if(freq!=1){
               
                   if( freq>=2 && freq<=7 ){
                      smallArraySort(sortJob,startIndex,freq); 
                   }
                   else{
                       ++recursions;
                     doSort(sortJob,startIndex,freq);    
                   }
               }
if(startIndex==max-1 || startIndex==nextStartIndex){
    break;
}   //end if
}//end if              

             }//end for loop
             
        
        }//end else
    }//end method.
    
    
    
    public static double[] sort(double x[]){
       doSort(x, 0, x.length);
       return sorted;
    }
    /**
     * 
     * @param x The array to be sorted.
     * @param offset The index where sorting is to start.
     * @param len The number of characters to sort.
     * @return an array similar to the input array but with values between
     * indices <code>offset</code> and <code>offset+len</code> sorted

    public static double[] sort(double x[], int offset, int len){
       doSort(x, offset, len);
       return sorted;
    }    
        */ 
    /**
     * 
     * @param array An array of double numbers.
     * If the array size is 2 it uses a direct swap to sort it.
     * Else it uses an insertion sort to solve it.
     */
    private static void smallArraySort( double[] array,int offset,int len){
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
     
        
    
    // double arr[] = {0,7,1,9,4,16,-2,11,122,73,12,-10,-1,15,22,20,31,38,52,20};
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
        
     double arr[] = new double[1000000];
        int len = arr.length;
           // System.out.println("Array has "+len+" characters.");
        for(int i=0;i<len;i++){
           // arr[i] = r.nextInt(len*(1+r.nextInt(len)));
        //arr[i] = r.nextInt((int) Math.pow(len,10));
           arr[i] = r.nextInt(len); 
           //arr[i]=( 1.0 * ( r.nextInt(2) ) )/( 10.0* ( 1+r.nextInt(10) ) );
          //  arr[i]=10;
           //arr[i] = r.nextInt(4);
         //   arr[i]=Math.pow(2, 0.1*i);
        }
        //printArray("Raw array",arr);
       // ClusterSort clusterSort = new ClusterSort( arr );
        double start = System.nanoTime();

           //     printArray("Unsorted array is",arr);
        
        arr=ClusterSort.sort(arr);
        
        
        //Arrays.sort(arr,0,len);
        start = (System.nanoTime()-start)/1.0E6;
        
      printArray("Sort time is "+start+" ms and sort output array is",new double[]{});
        //printArray("Sort time is "+start+" ms and sort output array is",arr);
      
     
    }//end method
    
}//end class