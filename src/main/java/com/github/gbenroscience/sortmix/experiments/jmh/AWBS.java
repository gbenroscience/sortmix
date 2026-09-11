package com.github.gbenroscience.sortmix.experiments.jmh;

import com.github.gbenroscience.sortmix.experiments.AdvancedWittyBucketSort;
import org.openjdk.jmh.infra.Blackhole; 

/**
 * ModernWittyBucketSort vs AdvancedWittyBucketSort
 * Build with: mvn clean verify -U Run with: java -jar target/benchmarks.jar
 * ".*AWBS.*"
 *
 * @author GBEMIRO
 */ 
public class AWBS extends Exec{
 
  
       @org.openjdk.jmh.annotations.Benchmark
    public void advancedWittyBucketSort(Blackhole blackhole) { 
        AdvancedWittyBucketSort.sort(workingData);
        blackhole.consume(workingData);
    }
   

}
