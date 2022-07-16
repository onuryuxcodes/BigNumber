package io.hidro.bignumber.util;

import java.util.Random;

public class RandomSamplerFunctionsOfJava {

    public static int sampleRandomInt(int upperBound) {
        Random rand = new Random();
        int randomSample = rand.nextInt(upperBound);
        return randomSample;
    }

    public static int sampleRandomInt(int lowerBound, int upperBound) {
        Random rand = new Random();
        int randomSample = rand.nextInt(upperBound);
        return randomSample + lowerBound;
    }

    public static double sampleRandomDouble(double lowerBound, double upperBound) {
        Random rand = new Random();
        double randomSample = rand.nextDouble();
        return lowerBound + (upperBound - lowerBound) * randomSample;
    }

    public static boolean sampleRandomBoolean() {
        Random rand = new Random();
        return rand.nextBoolean();
    }
}
