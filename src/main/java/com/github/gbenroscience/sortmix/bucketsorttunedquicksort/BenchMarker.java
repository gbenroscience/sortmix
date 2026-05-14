package com.github.gbenroscience.sortmix.bucketsorttunedquicksort;


import com.github.gbenroscience.sortmix.experiments.*;
import java.util.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author GBEMIRO
 */
public class BenchMarker {

 
        
    
    
    

    /**
     * @param len the array size.
     * @return an array with <code>len</code> integer entries, randomly filled.
     */
    public static double[] initArrayPos$NegRandomFloats(int len) {
        Random r = new Random();
        double arr[] = new double[len];
        for (int i = 0; i < len; i++) {
            if(r.nextBoolean()){
            arr[i] = r.nextDouble(); 
            }
            else{
            arr[i] = -r.nextDouble(); 
            }
            }
        return arr;
    }

    /**
     * @param len the array size.
     * @return an array with <code>len</code> randomly generated float entries, randomly filled.
     */
    public static double[] initArrayRandomFloats(int len) {
        Random r = new Random();
        double arr[] = new double[len];
        for (int i = 0; i < len; i++) {
            arr[i] = r.nextDouble();
        }
        return arr;
    }
     
    /**
     * @param len the array size.
     * @return an array with <code>len</code> integer entries, randomly filled with values from 0 to 9.
     */
    public static int[] initArrayRandomDigits(int len) {
        Random r = new Random();
        int arr[] = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = r.nextInt(10); //NewSort dominates on small data sets up to 100 or 200.QuickSort dominates for large data sets.(about 2.5X faster)
      }
        return arr;
    }       
    
    
    
    
    /**
     * @param len the array size.
     * @return an array with <code>len</code> integer entries, randomly filled.
     */
    public static int[] initArrayRandomInts(int len) {
        Random r = new Random();
        int arr[] = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = r.nextInt(len); //NewSort dominates on small data sets up to 100 or 200.QuickSort dominates for large data sets.(about 2.5X faster)
      }
        return arr;
    }
    /**
     * @param len the array size.
     * @return an array with <code>len</code> integer entries, randomly filled.
     */
    public static int[] initArrayPos$NegRandomInts(int len) {
        Random r = new Random();
        int arr[] = new int[len];
        for (int i = 0; i < len; i++) {
            if(r.nextBoolean()){
            arr[i] = r.nextInt(len); 
            }
            else{
            arr[i] = -r.nextInt(len); 
            }
            }
        return arr;
    }
   
  
    /**
     * @param len the array size.
     * @return an array with <code>len</code> entries sorted in reverse order.
     */
    public static int[] initArrayReverseSortedInts(int len) {
        int arr[] = new int[len];
        int back = len;
        for (int i = 0; i < len; i++) {
            arr[i] = --back;
        }
        return arr;
    }
   /**
     * @param len the array size.
     * @return an array with <code>len</code> entries sorted in reverse order.
     */
    public static double[] initArrayReverseSortedFloats(int len) {
        double arr[] = new double[len];
        int back = len;
        for (int i = 0; i < len; i++) {
            arr[i] = --back;
        }
        return arr;
    }    
    /**
     * @param len the size of the array.
     * @return an array with <code>len</code> entries sorted in ascending order.
     */
    public static int[] initArraySortedInts(int len) {
        int arr[] = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = i;
        }
        return arr;
    }
        /**
     * @param len the size of the array.
     * @return an array with <code>len</code> entries sorted in ascending order.
     */
    public static double[] initArraySortedFloats(int len) {
        double arr[] = new double[len];
        for (int i = 0; i < len; i++) {
            arr[i] = i;
        }
        return arr;
    }
    /**
     * @param len the size of the array.
     * @param lastSortedIndex the index beyond which the array is unsorted.
     * @return an array sorted up to <code>lastSortedIndex</code> and unsorted beyond.
     */
    public static int[] initPartiallySortedArrayInts(int len, int lastSortedIndex) {
       Random r = new Random();
       int arr[] = new int[len];
        for (int i = 0; i <= lastSortedIndex; i++) {
            arr[i] = i;
        }
        for (int i = lastSortedIndex+1; i < len; i++) {
            arr[i] = r.nextInt(len);
        }
        
        return arr;
    }
    /**
     * @param len the size of the array.
     * @param lastSortedIndex the index beyond which the array is unsorted.
     * @return an array sorted up to <code>lastSortedIndex</code> and unsorted beyond.
     */
    public static double[] initPartiallySortedArrayFloats(int len, int lastSortedIndex) {
       Random r = new Random();
       double arr[] = new double[len];
        for (int i = 0; i <= lastSortedIndex; i++) {
            arr[i] = i;
        }
        for (int i = lastSortedIndex+1; i < len; i++) {
            arr[i] = r.nextInt(len);
        }
        
        return arr;
    }
    
    /**
     * @param len the size of the array.
     * @param firstValue the first kind of value allowed in the array.
     * @param secondValue the second kind of value allowed in the array.
     * @return an array containing only the 2 kinds of values specified.
     * If the 2 values are the same, it returns an array of constants.
     */
    public static int[] initBinaryArrayInts(int len, int firstValue, int secondValue) {
       Random r = new Random();
       int arr[] = new int[len];
        for (int i = 0; i < len; i++) {
          if(r.nextBoolean()){
              arr[i] =  firstValue;
          }
          else{
              arr[i] = secondValue;
          }
        }
        
        return arr;
    }
        /**
     * @param len the size of the array.
     * @param firstValue the first kind of value allowed in the array.
     * @param secondValue the second kind of value allowed in the array.
     * @return an array containing only the 2 kinds of values specified.
     * If the 2 values are the same, it returns an array of constants.
     */
    public static double[] initBinaryArrayFloats(int len, double firstValue, double secondValue) {
       Random r = new Random();
       double arr[] = new double[len];
        for (int i = 0; i < len; i++) {
          if(r.nextBoolean()){
              arr[i] =  firstValue;
          }
          else{
              arr[i] = secondValue;
          }
        }
        
        return arr;
    }
    
    // -----x&i = i; ty
    //
    public static void main(String[] args){
        double arr[] = initArrayRandomFloats(8000000);

        int iterations = 1;

        double start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
          MergeSort.sort(arr.clone());
        }
        start = (System.nanoTime() - start) / 1.0E6;
        start /= iterations;
        System.out.println("In MergeSort, average method runtime for " + iterations + " calls = " + start + " ms.");

        
        start = System.nanoTime();
        for (int j = 0; j < iterations; j++){
         NonRecursiveMergeSort.sort(arr.clone());
        }
        start = (System.nanoTime() - start) / 1.0E6;
        start /= iterations;
        System.out.println("In NonRecursiveMergeSort method, average method runtime for " + iterations + " calls = " + start + " ms.");

    start = System.nanoTime();
        for (int j = 0; j < iterations; j++){
          ShellSort.sort(arr.clone());
        }
        start = (System.nanoTime() - start) / 1.0E6;
        start /= iterations;
        System.out.println("In ShellSort method,  average method runtime for " + iterations + " calls = " + start + " ms.");
        
        
        
        start = System.nanoTime();
        for (int j = 0; j < iterations; j++){
            BucketMergeSort.sort(arr.clone());
        }
        start = (System.nanoTime() - start) / 1.0E6;
        start /= iterations;
        System.out.println("In BucketMergeSort method,  average method runtime for " + iterations + " calls = " + start + " ms.");
        
        
        
        
        
        start = System.nanoTime();
        for (int j = 0; j < iterations; j++){
          HeapSort.sort(arr.clone());
        }
        start = (System.nanoTime() - start) / 1.0E6;
        start /= iterations;
        System.out.println("In HeapSort method,  average method runtime for " + iterations + " calls = " + start + " ms.");
        
          start = System.nanoTime();
        for (int j = 0; j < iterations; j++){
            SpeedyBucketSort.setCacheOptimized(true);
          SpeedyBucketSort.sort(arr.clone());
        }
        start = (System.nanoTime() - start) / 1.0E6;
        start /= iterations;
        System.out.println("In SpeedyBucketSort method, average method runtime for " + iterations + " calls = " + start + " ms.");
          start = System.nanoTime();
        for (int j = 0; j < iterations; j++){
            AdvancedWittyBucketSort.setCacheOptimized(true);
          AdvancedWittyBucketSort.sort(arr.clone());
        }
        start = (System.nanoTime() - start) / 1.0E6;
        start /= iterations;
        System.out.println("In AdvancedWittyBucketSort method, average method runtime for " + iterations + " calls = " + start + " ms.");
          start = System.nanoTime();
        for (int j = 0; j < iterations; j++){
          WittyBucketSort.sort(arr.clone());
        }
        start = (System.nanoTime() - start) / 1.0E6;
        start /= iterations;
        System.out.println("In WittyBucketSort method, average method runtime for " + iterations + " calls = " + start + " ms.");
        
          start = System.nanoTime();
        for (int j = 0; j < iterations; j++){
          QuickSort.sort(arr.clone());
        }
        start = (System.nanoTime() - start) / 1.0E6;
        start /= iterations;
        System.out.println("In QuickSort method, average method runtime for " + iterations + " calls = " + start + " ms.");
  
           start = System.nanoTime();
        for (int j = 0; j < iterations; j++){
          RecursiveQuickSort.sort(arr.clone());
        }
        start = (System.nanoTime() - start) / 1.0E6;
        start /= iterations;
        System.out.println("In RecursiveQuickSort method, average method runtime for " + iterations + " calls = " + start + " ms.");
       
        
    }
}