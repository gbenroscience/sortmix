package com.github.gbenroscience.sortmix.experiments.jmh;

import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import com.github.gbenroscience.sortmix.experiments.AdvancedWittyBucketSort;
import com.github.gbenroscience.sortmix.experiments.ModernWittyBucketSort;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * ModernWittyBucketSort vs AdvancedWittyBucketSort
 * Build with: mvn clean verify -U Run with: java -jar target/benchmarks.jar
 * ".*AWBS.*"
 *
 * @author GBEMIRO
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(value = 1, warmups = 1)
@Threads(1)
public class AWBS {

    public static final int POS_AND_NEG_RANDOM_FLOATS = 0;
    public static final int RANDOM_FLOATS = 1;
    public static final int REVERSE_SORTED_FLOATS = 2;
    public static final int SORTED_FLOATS = 3;
    public static final int BINARY_ARRAY_FLOATS = 4;
    public static final int PARTIALLY_SORTED_ARRAY_FLOATS = 5;
    static int dataType = RANDOM_FLOATS;

    static int INPUT_SIZE = 1_000_000;

    private double[] masterData;

    static {
        INPUT_SIZE = Integer.getInteger("inputSize", 1_000_000);
        dataType = Integer.getInteger("dataType", RANDOM_FLOATS);
    }

    @Setup(Level.Trial)
    public void setup() {
        System.out.print("SETTING UP- INPUT-ARRAY-SIZE: " + INPUT_SIZE + "\nDATA-TYPE: ");
        switch (dataType) {
            case BINARY_ARRAY_FLOATS:
                masterData = BenchMarker.initBinaryArrayFloats(INPUT_SIZE, 10, 20);
                System.out.println("BINARY_ARRAY_FLOATS");
                break;
            case PARTIALLY_SORTED_ARRAY_FLOATS:
                masterData = BenchMarker.initPartiallySortedArrayFloats(INPUT_SIZE, INPUT_SIZE / 2);
                System.out.println("PARTIALLY_SORTED_ARRAY_FLOATS");
                break;
            case POS_AND_NEG_RANDOM_FLOATS:
                masterData = BenchMarker.initArrayPos$NegRandomFloats(INPUT_SIZE);
                System.out.println("POS_AND_NEG_RANDOM_FLOATS");
                break;
            case RANDOM_FLOATS:
                masterData = BenchMarker.initArrayRandomFloats(INPUT_SIZE);
                System.out.println("RANDOM_FLOATS");
                break;
            case REVERSE_SORTED_FLOATS:
                masterData = BenchMarker.initArrayReverseSortedFloats(INPUT_SIZE);
                System.out.println("REVERSE_SORTED_FLOATS");
                break;
            case SORTED_FLOATS:
                masterData = BenchMarker.initArraySortedFloats(INPUT_SIZE);
                System.out.println("SORTED_FLOATS");
                break;
            default:
                throw new AssertionError();
        }
    }
 
    @org.openjdk.jmh.annotations.Benchmark
    public void modernWittyBucketSort(Blackhole blackhole) {
        double[] clone = generateInputs();
        ModernWittyBucketSort.sort(clone);
        blackhole.consume(clone);
    }
    
       @org.openjdk.jmh.annotations.Benchmark
    public void advancedWittyBucketSort(Blackhole blackhole) {
        double[] clone = generateInputs();
        AdvancedWittyBucketSort.sort(clone);
        blackhole.consume(clone);
    }
 

    @Benchmark
    public void baseline(Blackhole blackhole) {
        double[] clone = generateInputs(); // Measures just the overhead of creating the 30 variables
        blackhole.consume(clone);
    }
    
     public static void main(String[] args) throws RunnerException {

        int sz = 50_000_000;

        System.out.println("INPUT_SIZE: " + sz); 
 
            Options opt = new OptionsBuilder()
                    .include(AWBS.class.getSimpleName())
                    .mode(Mode.AverageTime)
                    .timeUnit(TimeUnit.NANOSECONDS)
                    .warmupIterations(5)
                    .warmupTime(TimeValue.milliseconds(200L))
                    .measurementIterations(5)
                    .measurementTime(TimeValue.milliseconds(500))
                    .forks(1)
                    .addProfiler(org.openjdk.jmh.profile.GCProfiler.class)
                    .jvmArgs("-Xms12g", "-Xmx12g") // tune heap if needed
                    .jvmArgs("-DinputSize=" + sz, "-DdataType=" + BINARY_ARRAY_FLOATS)
                    .build();

            new Runner(opt).run();
        

    }
 

    private double[] generateInputs() {
        return Arrays.copyOf(masterData, masterData.length);
    }

}
