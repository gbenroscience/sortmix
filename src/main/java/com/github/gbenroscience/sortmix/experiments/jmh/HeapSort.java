package com.github.gbenroscience.sortmix.experiments.jmh;
 
import org.openjdk.jmh.infra.Blackhole; 

 


public class HeapSort extends Exec{  
 
 
    
       @org.openjdk.jmh.annotations.Benchmark
    public void heapSort(Blackhole blackhole) { 
           com.github.gbenroscience.sortmix.experiments.HeapSort.sort(workingData);
        blackhole.consume(workingData);
    }
   
 

}
