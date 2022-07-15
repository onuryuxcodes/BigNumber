package io.hidro.bignumber.util;

import java.util.Random;

public class RandomSamplerFunctions {
    public static int sampleRandomInt(int lowerBound, int upperBound) {
        Random rand = new Random();
        int randomSample = rand.nextInt(upperBound);
        return randomSample + lowerBound;
    }

    public static double sampleRandomDouble(int lowerBound, int upperBound) {
        Random rand = new Random();
        double randomSample = rand.nextDouble();
        return lowerBound + (upperBound - lowerBound) * randomSample;
    }
}
