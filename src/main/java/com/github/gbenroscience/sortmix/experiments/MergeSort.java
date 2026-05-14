/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.gbenroscience.sortmix.experiments;

import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import static com.github.gbenroscience.sortmix.util.SortUtils.*;

public class MergeSort {

   // ====================== PUBLIC API ======================

    public static void sort(double[] sortJob) {
        if (sortJob == null || sortJob.length <= 1) return;
        double[] aux = new double[sortJob.length];   // One temporary array
        sort(sortJob, aux, 0, sortJob.length - 1);
    }

    public static void sort(double[] sortJob, int left, int right) {
        if (sortJob == null || right - left < 1) return;
        double[] aux = new double[right - left + 1];
        sort(sortJob, aux, left, right);
    }

    // ====================== Integer overloads ======================

    public static void sort(int[] sortJob) {
        if (sortJob == null || sortJob.length <= 1) return;
        int[] aux = new int[sortJob.length];
        sort(sortJob, aux, 0, sortJob.length - 1);
    }

    public static void sort(int[] sortJob, int left, int right) {
        if (sortJob == null || right - left < 1) return;
        int[] aux = new int[right - left + 1];
        sort(sortJob, aux, left, right);
    }

    // ====================== PRIVATE RECURSIVE METHODS ======================

    private static void sort(double[] arr, double[] aux, int low, int high) {
        if (low >= high) return;

        int mid = low + (high - low) / 2;

        sort(arr, aux, low, mid);
        sort(arr, aux, mid + 1, high);

        merge(arr, aux, low, mid, high);
    }

    private static void merge(double[] arr, double[] aux, int low, int mid, int high) {
        // Copy to auxiliary array
        System.arraycopy(arr, low, aux, low, high - low + 1);

        int left = low;
        int right = mid + 1;
        int current = low;

        while (left <= mid && right <= high) {
            if (aux[left] <= aux[right]) {
                arr[current++] = aux[left++];
            } else {
                arr[current++] = aux[right++];
            }
        }

        // Copy remaining elements from left half
        while (left <= mid) {
            arr[current++] = aux[left++];
        }
        // Right half elements are already in place (no need to copy)
    }

    // ====================== Integer version ======================

    private static void sort(int[] arr, int[] aux, int low, int high) {
        if (low >= high) return;

        int mid = low + (high - low) / 2;

        sort(arr, aux, low, mid);
        sort(arr, aux, mid + 1, high);

        merge(arr, aux, low, mid, high);
    }

    private static void merge(int[] arr, int[] aux, int low, int mid, int high) {
        System.arraycopy(arr, low, aux, low, high - low + 1);

        int left = low;
        int right = mid + 1;
        int current = low;

        while (left <= mid && right <= high) {
            if (aux[left] <= aux[right]) {
                arr[current++] = aux[left++];
            } else {
                arr[current++] = aux[right++];
            }
        }

        while (left <= mid) {
            arr[current++] = aux[left++];
        }
    }
    public static void main1(String[] args) {
        int len = 22343533;//17907000; //8940705;
        //double arr[] = new double[80];
        //double arr[] = {1.0000000000001,1.00000000000011,1.00000000000012,1.000000000000115,1.000000000000113,1.000000000000121,};
        int arr[] = BenchMarker.initArrayRandomInts(len);
        //    printArray("Before sorting", arr);
        double start = System.nanoTime();
        MergeSort.sort(arr);//.sort(arr);
        start = (System.nanoTime() - start) / 1.0E6;
        printObject(len + " items sorted using MergeSort in " + start + "ms");

        if (checkSortState(arr, 0, len - 1)[2] == 0) {
            System.out.println("Sorted!");
        } else {
            System.out.println("Not Sorted");
        }
        //printArray("After sorting", arr);
        //ty

    }

    public static void main(String[] args) {
        System.out.println("com.github.gbenroscience.sortmix.experiments.MergeSort.main()");
        int n = 100_000_000;
        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = Math.random() * 1000;
        }
        //QuickSort.sort(data);

        long start = System.currentTimeMillis();
        sort(data);
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

} // end class MergeSort
