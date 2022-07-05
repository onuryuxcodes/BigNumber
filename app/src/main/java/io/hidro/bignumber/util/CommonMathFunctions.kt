package io.hidro.bignumber.util

import kotlin.math.abs
import kotlin.random.Random

class CommonMathFunctions {
    companion object {
        /**
         * Returns a random integer within given [lowerBound] and [upperBound].
         * Should [lowerBound] be sent out greater, [lowerBound] and the sign of [upperBound] are ignored
         */
        fun generateRandomInt(lowerBound: Int, upperBound: Int): Int {
            return if (lowerBound <= upperBound)
                Random.nextInt(lowerBound, upperBound)
            else Random.nextInt(0, abs(upperBound))
        }

        /**
         * Returns a random integer between 0.1 to 100
         * Probability of delta being smaller increases disproportionally to [currentStep]
         * Delta's range is 0.1 to 100
         */
        fun generateRandomDelta(currentStep: Int): Double {
            var randomDelta = (Random.nextDouble(0.0, 1000.0 / (currentStep + 1))) / 10
            if (randomDelta < 0.1)
                randomDelta = 0.1
            if (Random.nextBoolean())
                randomDelta *= -1
            return randomDelta
        }
    }
}