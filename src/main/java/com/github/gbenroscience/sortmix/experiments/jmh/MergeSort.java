package com.github.gbenroscience.sortmix.experiments.jmh;

import org.openjdk.jmh.infra.Blackhole;

/**
 * ModernWittyBucketSort vs MergeSort Build with: mvn clean verify -U Run with:
 * java -jar target/benchmarks.jar ".*MS.*"
 *
 * @author GBEMIRO
 */
public class MergeSort extends Exec {

    @org.openjdk.jmh.annotations.Benchmark
    public void mergeSort(Blackhole blackhole) {
        com.github.gbenroscience.sortmix.experiments.MergeSort.sort(workingData);
        blackhole.consume(workingData);
    }

}
