package com.github.gbenroscience.sortmix.experiments.jmh;

import com.github.gbenroscience.sortmix.experiments.ModernWittyBucketSort;
import com.github.gbenroscience.sortmix.experiments.SimdWittyBucketSort;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Fair comparison of ModernWittyBucketSort vs SimdWittyBucketSort.
 *
 * The input preparation/copy is performed at Level.Invocation, outside the
 * measured benchmark methods. Both algorithms therefore receive an identical
 * fresh copy of the same unsorted master dataset for every invocation.
 *
 * Build with: mvn clean verify -U
 *
 * Run with: java -jar target/benchmarks.jar ".*SimdWBS.*"
 *
 * Examples: -DinputSize=1000000 -DdataType=1
 */
public class SimdWBS extends Exec{


   
 

    @Benchmark
    public void simdWittyBucketSort(Blackhole blackhole) {
        SimdWittyBucketSort.sort(workingData);
        blackhole.consume(workingData);
    }
     
}
