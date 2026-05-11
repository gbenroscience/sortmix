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
public class CountingSort {

    public static void sort(int a[], int maxVal) {
        int[] bucket = new int[maxVal + 1];
        //ty 
        for (int i = 0; i < a.length; i++) {
            bucket[i] = 0;
        }
        for (int i = 0; i < a.length; i++) {
            bucket[(int) a[i]]++;
        }
        int outPos = 0;
        for (int i = 0; i < bucket.length; i++) {
            for (int j = 0; j < bucket[i]; j++) {
                a[outPos++] = i;
            }
        }

    }

    public static void sort(double a[], double maxVal) {
        double[] bucket = new double[(int) maxVal + 1];
        //ty 
        for (int i = 0; i < a.length; i++) {
            bucket[i] = 0;
        }
        for (int i = 0; i < a.length; i++) {
            bucket[(int) a[i]]++;
        }
        int outPos = 0;
        for (int i = 0; i < bucket.length; i++) {
            for (int j = 0; j < bucket[i]; j++) {
                a[outPos++] = i;
            }
        }
    }

    public static void sort(double a[]) {
        double maxVal = Double.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > maxVal) {
                maxVal = a[i];
            }
        }
        double[] bucket = new double[(int) maxVal + 1];
        //ty 
        for (int i = 0; i < a.length; i++) {
            bucket[i] = 0;
        }
        for (int i = 0; i < a.length; i++) {
            bucket[(int) a[i]]++;
        }
        int outPos = 0;
        for (int i = 0; i < bucket.length; i++) {
            for (int j = 0; j < bucket[i]; j++) {
                a[outPos++] = i;
            }
        }
    }

    public static void main1(String args[]) {

        int len = 30;// ;17907709; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
        int arr[] = BenchMarker.initArrayRandomInts(len);
        printArray("Before sorting", arr);
        double start = System.nanoTime();
        CountingSort.sort(arr, len);//.sort(arr);
        start = (System.nanoTime() - start) / 1.0E6;
        printObject(len + " items sorted using CountingSort in " + start + "ms");

        if (checkSortState(arr, 0, len - 1)[2] == 0) {
            System.out.println("Sorted!");
        } else {
            System.out.println("Not Sorted");
        }
        printArray("After sorting", arr);
        //ty
    }//end method

    public static void main(String[] args) {
        System.out.println("com.github.gbenroscience.sortmix.experiments.MergeSort.main()");
        int n = 24_000_000;
        double[] data = new double[n];
        double max = Double.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            double d = Math.random() * 1000;
            data[i] = d;
            if (d > max) {
                max = d;
            }
        }

        long start = System.currentTimeMillis();
        sort(data, max);
        long end = System.currentTimeMillis();

        System.out.println("Sorted " + n + " elements in " + (end - start) + "ms");

        // Final validation
        boolean sorted = true;
        for (int i = 0; i < n - 1; i++) {
            if (data[i] > data[i + 1]) {
                sorted = false;
                break;
            }
        }
        System.out.println("Verification: " + (sorted ? "PASSED" : "FAILED"));
    }
}
