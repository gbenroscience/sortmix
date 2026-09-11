package com.github.gbenroscience.sortmix.experiments.jmh;

import org.openjdk.jmh.infra.Blackhole;

/**
 * ModernWittyBucketSort vs ShellSort Build with: mvn clean verify -U Run with:
 * java -jar target/benchmarks.jar ".*ShSort.*"
 *
 * @author GBEMIRO
 */
public class ShellSort extends Exec {

    @org.openjdk.jmh.annotations.Benchmark
    public void shellSort(Blackhole blackhole) {
        com.github.gbenroscience.sortmix.experiments.ShellSort.sort(workingData);
        blackhole.consume(workingData);
    }

}
