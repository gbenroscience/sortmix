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
 * @author JIBOYE, OLUWAGBEMIRO OLAOLUWA
 * 
 */
public class BucketSort {

    
    
    public static void sort(int array[], int N){
        int min = array[0];
        int max = min;
        
        for(int i=1;i<N;i++){
            if(array[i] > max){
                max = array[i];
            }
            else if(array[i] < min){
                min = array[i];
            }
        }//end for loop
        
        int buckets[] = new int[max-min+1];
      for(int i=0;i<N; i++){
          buckets[array[i]-min]++;
      }  
      int i=0;
      for(int b=0;b<buckets.length;b++){
          for(int j=0;j<buckets[b];j++){
              array[i++] = b+min;
          }
      }  
    }
    
    
    public static void sort(int array[], int left, int right){
        int N = right - left + 1;
        int min = array[left];
        int max = min;
        
        for(int i=left;i<N;i++){
            if(array[i] > max){
                max = array[i];
            }
            else if(array[i] < min){
                min = array[i];
            }
        }//end for loop
        
        int buckets[] = new int[max-min+1];
      for(int i=left;i<N; i++){
          buckets[array[i]-min]++;
      }  
      int i=left;
      for(int b=0;b<buckets.length;b++){
          for(int j=0;j<buckets[b];j++){
              array[i++] = b+min;
          }
      }  
    }
    
    public static void sort(int[]array){
        sort(array, 0, array.length - 1);
    }
    
    ///////////////////////////////////////////////////////////////////////
    
    
    public static void sort(double array[], int N){
        double min = array[0];
        double max = min;
        
        for(int i=1;i<N;i++){
            if(array[i] > max){
                max = array[i];
            }
            else if(array[i] < min){
                min = array[i];
            }
        }//end for loop
        
        int buckets[] = new int[(int)(max-min)+1];
      for(int i=0;i<N; i++){
          buckets[(int)(array[i]-min)]++;
      }  
      int i=0;
      for(int b=0;b<buckets.length;b++){
          for(int j=0;j<buckets[b];j++){
              array[i++] = b+min;
          }
      }  
    }
    
    
    public static void sort(double array[], int left, int right){
        int N = right - left + 1;
        double min = array[left];
        double max = min;
        
        for(int i=left;i<N;i++){
            if(array[i] > max){
                max = array[i];
            }
            else if(array[i] < min){
                min = array[i];
            }
        }//end for loop
        
        int buckets[] = new int[(int)(max-min)+1];
      for(int i=left;i<N; i++){
          buckets[(int)(array[i]-min)]++;
      }  
      int i=left;
      for(int b=0;b<buckets.length;b++){
          for(int j=0;j<buckets[b];j++){
              array[i++] = b+min;
          }
      }  
    }
    
    public static void sort(double[]array){
        sort(array, 0, array.length - 1);
    }
    
    
    
     
    public static void main(String args[]) {

        int len = 20000000;//17907709; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
    //double arr[] = BenchMarker.initArrayPos$NegRandomFloats(len);
    int arr[] = BenchMarker.initArrayRandomInts(len);
    
   // printArray("Before sorting", arr);
        double start = System.nanoTime();
BucketSort.sort(arr);
         
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
    }//end method
}