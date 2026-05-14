package com.github.gbenroscience.sortmix.experiments;

import java.util.Arrays;
import org.openjdk.jmh.runner.RunnerException;

/**
 *
 * @author GBEMIRO
 */
public class Main {

    public static final int MAX_INPUT_SIZE = 200_000_000;

    public static void main(String[] args) throws RunnerException {
        //If no args supplied, run the full suite of benchmarks
        if (args.length == 0) {
            for (int i = 0; i <= 5; i++) {
                int inpSz = (int) (1000 * Math.pow(10, i));
                if (inpSz < MAX_INPUT_SIZE) {
                     args = new String[]{"-sz",String.valueOf(inpSz)};
                     System.out.println("args: "+Arrays.toString(args));
                    JMHWars.main(args);
                }
            }
        } else if (args.length > 0) {
              JMHWars.main(args);
        }

    }

}
