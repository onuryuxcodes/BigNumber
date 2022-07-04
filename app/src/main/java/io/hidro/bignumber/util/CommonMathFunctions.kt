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
    }
}