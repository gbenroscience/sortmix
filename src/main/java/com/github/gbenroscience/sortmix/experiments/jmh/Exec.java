package com.github.gbenroscience.sortmix.experiments.jmh;

import com.github.gbenroscience.sortmix.bucketsorttunedquicksort.BenchMarker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

/**
 *
 * @author GBEMIRO
 */
/**
 * Exec Build with: mvn clean verify -U Run with: java -jar
 * target/benchmarks.jar ".*Exec.*"
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
public class Exec {

    public static final int POS_AND_NEG_RANDOM_FLOATS = 0;
    public static final int RANDOM_FLOATS = 1;
    public static final int REVERSE_SORTED_FLOATS = 2;
    public static final int SORTED_FLOATS = 3;
    public static final int BINARY_ARRAY_FLOATS = 4;
    public static final int PARTIALLY_SORTED_ARRAY_FLOATS = 5;

    private static final int INPUT_SIZE
            = Integer.getInteger("inputSize", 1_000_000);

    private static final int DATA_TYPE
            = Integer.getInteger("dataType", RANDOM_FLOATS);

    /*
     * Immutable source dataset.
     *
     * This array is never passed to either sorting algorithm.
     */
    protected double[] masterData;

    /*
     * Fresh working array prepared before every invocation.
     *
     * @Setup(Level.Invocation) is outside the benchmark timing, so the
     * allocation/copy does not contaminate the sorting measurement.
     */
    protected double[] workingData;

    @Setup(Level.Trial)
    public void setupTrial() {

        System.out.println(
                "SETTING UP - INPUT ARRAY SIZE: " + INPUT_SIZE);

        System.out.print("DATA TYPE: ");

        switch (DATA_TYPE) {

            case BINARY_ARRAY_FLOATS:
                masterData
                        = BenchMarker.initBinaryArrayFloats(
                                INPUT_SIZE, 10, 20);
                System.out.println("BINARY_ARRAY_FLOATS");
                break;

            case PARTIALLY_SORTED_ARRAY_FLOATS:
                masterData
                        = BenchMarker.initPartiallySortedArrayFloats(
                                INPUT_SIZE,
                                INPUT_SIZE / 2);
                System.out.println("PARTIALLY_SORTED_ARRAY_FLOATS");
                break;

            case POS_AND_NEG_RANDOM_FLOATS:
                masterData
                        = BenchMarker.initArrayPos$NegRandomFloats(
                                INPUT_SIZE);
                System.out.println("POS_AND_NEG_RANDOM_FLOATS");
                break;

            case RANDOM_FLOATS:
                masterData
                        = BenchMarker.initArrayRandomFloats(
                                INPUT_SIZE);
                System.out.println("RANDOM_FLOATS");
                break;

            case REVERSE_SORTED_FLOATS:
                masterData
                        = BenchMarker.initArrayReverseSortedFloats(
                                INPUT_SIZE);
                System.out.println("REVERSE_SORTED_FLOATS");
                break;

            case SORTED_FLOATS:
                masterData
                        = BenchMarker.initArraySortedFloats(
                                INPUT_SIZE);
                System.out.println("SORTED_FLOATS");
                break;

            default:
                throw new IllegalArgumentException(
                        "Unknown dataType: " + DATA_TYPE);
        }
    }

    /**
     * Prepare a fresh unsorted input outside the timed region.
     *
     * Each benchmark invocation receives a new array, so neither sorter ever
     * receives the output produced by a previous invocation.
     */
    @Setup(Level.Invocation)
    public void setupInvocation() {
        workingData = Arrays.copyOf(
                masterData,
                masterData.length);
    }

    public static String getDataTypeName(int dataType) {
        switch (dataType) {
            case POS_AND_NEG_RANDOM_FLOATS:
                return "POS_AND_NEG_RANDOM_FLOATS";
            case RANDOM_FLOATS:
                return "RANDOM_FLOATS";
            case REVERSE_SORTED_FLOATS:
                return "REVERSE_SORTED_FLOATS";
            case SORTED_FLOATS:
                return "SORTED_FLOATS";
            case BINARY_ARRAY_FLOATS:
                return "BINARY_ARRAY_FLOATS";
            case PARTIALLY_SORTED_ARRAY_FLOATS:
                return "PARTIALLY_SORTED_ARRAY_FLOATS";
            default:
                throw new AssertionError();
        }
    }

    public static String[] discoverFiles() {
        // Replace with your actual package name
        String packageName = Exec.class.getPackageName();
        String terminalClassName = Exec.class.getSimpleName();
        System.out.println("terminalClassName: " + terminalClassName);
        Reflections reflections = new Reflections(packageName);
        Set<String> classNames = reflections.getStore().get(Scanners.SubTypes.name()).keySet();
        List<String> list = new ArrayList<>();
        for (String className : classNames) {
            // The scanner returns full binary names, let's add them to our list
            if (!className.toLowerCase().contains("jmh_generated") && !className.toLowerCase().contains(terminalClassName.toLowerCase()) && !className.toLowerCase().contains("baseline")) {
                list.add(className);
            }
        }
        return list.toArray(String[]::new);
    }

    public static void main(String[] args) throws RunnerException {
        String[] benchmarkList = discoverFiles();
        System.out.println("=========================================================");
        System.out.println("   Welcome to the SORT ALGORITHM Benchmarks");
        System.out.println("=========================================================\n");
        int size = Integer.getInteger("inputSize", 50_000_000);

        Scanner sc = new Scanner(System.in);
        StringBuilder builder = new StringBuilder();
        builder.append("[0] POS_AND_NEG_RANDOM_FLOATS\n")
                .append("[1] RANDOM_FLOATS\n")
                .append("[2] REVERSE_SORTED_FLOATS\n")
                .append("[3] SORTED_FLOATS\n")
                .append("[4] BINARY_ARRAY_FLOATS\n")
                .append("[5] PARTIALLY_SORTED_ARRAY_FLOATS\n");

        int type;
        while (true) {
            System.out.println("""
                               Choose the data type to test against:
                               """ + builder.toString() + "\n>>>");
            type = sc.nextInt();
            if (type >= 0 && type <= 5) {
                break;
            }
        }

        while (true) {
            System.out.println("""
                               Choose the input size (number of elements to sort):
                               """);
            size = sc.nextInt();
            if (size >= 0 && size <= Math.pow(2, 26)) {
                break;
            }
        }


        // Consume the leftover newline from the previous nextInt()
        sc.nextLine();
        
        System.out.println("INPUT_SIZE: " + size);
        System.out.println("DATA_TYPE: " + getDataTypeName(type));

        Map<Integer, String> mapping = new HashMap<>();
        StringBuilder sb = new StringBuilder("Select the Sorting Algorithms to benchmark (comma-separated digits):\n");
        int digits = 1;
        for (String name : benchmarkList) {
            String digitStr = String.valueOf(digits);
            sb.append("        [").append(digitStr).append(digitStr.length() == 1 ? "]  " : "] ").append(name.substring(name.lastIndexOf(".") + 1)).append("\n");
            mapping.put(digits++, name);
        }
        sb.append("   \n");
        sb.append("    Enter engines (e.g., 1,2,5):\n");

        System.out.print(sb.toString());
 
        
        String digitsCommand = sc.nextLine();  
        if (digitsCommand == null || digitsCommand.trim().isEmpty()) {
            System.err.println("No sorting algorithms selected. Exiting.");
            return;
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        

        // 3. Parse and Configure JMH Options
        try {
            OptionsBuilder opt = new OptionsBuilder();

            String[] digitsTextArray = digitsCommand.split(",");
            StringBuilder versusBuilder = new StringBuilder();
            int addedEnginesCount = 0;

            for (String token : digitsTextArray) {
                String trimmedToken = token.trim();
                if (trimmedToken.isEmpty()) {
                    continue;
                }

                int engineChoice = Integer.parseInt(trimmedToken);
                String className = mapping.get(engineChoice);
                if (className == null) {
                    System.err.println("Warning: Code [" + engineChoice + "] is invalid and skipped.");
                    return;
                }
                
                String cname = className.substring(className.lastIndexOf(".") + 1);
                
                // === FIXED HERE ===
                // Use a wildcard regex match for the simple class name instead of the full exact path
                opt.include(".*" + cname + ".*"); 
                
                versusBuilder.append(cname).append(" vs ");
                addedEnginesCount++;
            }

            if (addedEnginesCount == 0) {
                System.err.println("No valid engines were selected. Match abandoned!");
                return;
            }

            String versusString = versusBuilder.toString();
            System.out.println("\n⚔️  MATCH MATCHUP: " + versusString.substring(0, versusString.length() - 4));
            System.out.println("🚀 LET THE GAMES BEGIN!\n");

            // 4. Fluent, modern JMH Configuration
            Options configurations = opt.mode(Mode.AverageTime)
                    .timeUnit(TimeUnit.NANOSECONDS)
                    .warmupIterations(5)
                    .warmupTime(TimeValue.milliseconds(200L))
                    .measurementIterations(5)
                    .measurementTime(TimeValue.milliseconds(500))
                    .forks(2)
                    .addProfiler(org.openjdk.jmh.profile.GCProfiler.class)
                    .jvmArgs(
                            "-Xms12g",
                            "-Xmx12g")
                    .jvmArgs(
                            "-DinputSize=" + size,
                            "-DdataType=" + type)
                    .jvmArgsAppend(
                            "--add-modules",
                            "jdk.incubator.vector",
                            "-XX:+UnlockDiagnosticVMOptions")
                    .build();

            new Runner(configurations).run();

        } catch (NumberFormatException e) {
            System.err.println("Error: Input contains non-numeric characters inside the parser selections.");
        } catch (RunnerException ex) {
            System.getLogger(Exec.class.getName()).log(System.Logger.Level.ERROR, "JMH Execution failed", ex);
        }

    }

}