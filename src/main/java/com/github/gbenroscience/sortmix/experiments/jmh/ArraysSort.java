package com.github.gbenroscience.sortmix.experiments.jmh;

import java.util.Arrays;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * ModernWittyBucketSort vs java.util.Arrays.sort Build with: mvn clean verify
 * -U Run with: java -jar target/benchmarks.jar ".*ArraysSort.*"
 *
 * @author GBEMIRO
 */ 
public class ArraysSort extends Exec{ 

   
    @Benchmark
    public void javaStdArraysDotSort(Blackhole blackhole) {
        Arrays.sort(workingData);
        blackhole.consume(workingData);
    }
 

}
