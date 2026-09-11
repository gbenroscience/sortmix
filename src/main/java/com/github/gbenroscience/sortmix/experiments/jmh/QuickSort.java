package com.github.gbenroscience.sortmix.experiments.jmh;

import org.openjdk.jmh.infra.Blackhole;

/**
 * ModernWittyBucketSort vs QuickSort
 * Build with: mvn clean verify -U Run with: java -jar target/benchmarks.jar
 * ".*QS.*"
 *
 * @author GBEMIRO
 */ 
public class QuickSort extends Exec{
 
  
       @org.openjdk.jmh.annotations.Benchmark
    public void quicksort(Blackhole blackhole) { 
           com.github.gbenroscience.sortmix.experiments.QuickSort.sort(workingData);
        blackhole.consume(workingData);
    }
  
  
  

}
