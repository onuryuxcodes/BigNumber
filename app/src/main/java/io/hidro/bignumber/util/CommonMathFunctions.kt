package io.hidro.bignumber.util

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt


class CommonMathFunctions {
    companion object {

        fun roundToOneDecimal(number: Double): Double {
            return (number * 10.0).roundToInt() / 10.0
        }

        /**
         * Returns a random integer within given [lowerBound] and [upperBound].
         * Should [lowerBound] be sent out greater, [lowerBound] and the sign of [upperBound] are ignored
         */
        fun generateRandomInt(lowerBound: Int, upperBound: Int): Int {
            return if (lowerBound <= upperBound)
                RandomSamplerFunctionsOfJava.sampleRandomInt(lowerBound, upperBound)
            else RandomSamplerFunctionsOfJava.sampleRandomInt(0, abs(upperBound))
        }

        private const val minDelta = 0.1

        /**
         * Returns a random integer between 0.1 to 100
         * Delta's range is [minDelta] to 10
         */
        fun generateRandomDelta(currentLevel: Int): Double {
            val upperBound = 100.0 / currentLevel
            var randomDelta =
                (RandomSamplerFunctionsOfJava.sampleRandomDouble(0.0, upperBound)) / 10
            if (randomDelta < minDelta)
                randomDelta = minDelta
            if (RandomSamplerFunctionsOfJava.sampleRandomBoolean())
                randomDelta *= -1
            return randomDelta
        }

        /**
         * Returns a random operator defined in [CompositionOperators]
         * [CompositionOperators.SUM] has the highest probability of 50%
         * Followed by [CompositionOperators.NONE]  20%
         */
        fun pickARandomCompositionOperator(): CompositionOperators {
            val randomInteger = RandomSamplerFunctionsOfJava.sampleRandomInt(100)
            return if (randomInteger < 50)
                CompositionOperators.SUM
            else if (randomInteger < 60)
                CompositionOperators.SUBTRACTION
            else if (randomInteger < 70)
                CompositionOperators.MULTIPLICATION
            else if (randomInteger < 80)
                CompositionOperators.DIVISION
            else
                CompositionOperators.NONE

        }

        /**
         * Returns a random integer 1 to 3
         * 1 means the number is only composed of itself
         * 3 means it is composed by 3 numbers
         */
        fun pickARandomCompositionType(): Int = RandomSamplerFunctionsOfJava.sampleRandomInt(3) + 1

        /**
         * Returns [CompositionOperators.SUM] or [CompositionOperators.SUBTRACTION]
         * based on random boolean
         */
        fun pickARandomCompositionTypeBetweenSumAndSubtraction(): CompositionOperators {
            return if (RandomSamplerFunctionsOfJava.sampleRandomBoolean())
                CompositionOperators.SUBTRACTION
            else
                CompositionOperators.SUM
        }

        /**
         * Composes given [number] from [operation]
         */
        fun compose(
            number: Double,
            operation: CompositionOperators,
            level: Int
        ): Triple<Double, Double?, CompositionOperators> {
            when (operation) {
                CompositionOperators.SUM -> {
                    var bound = number.toInt()
                    if (bound == 0)
                        bound++
                    val randomSample = RandomSamplerFunctionsOfJava.sampleRandomInt(bound)
                    val firstPart = roundToOneDecimal(randomSample.toDouble())
                    val secondPart = roundToOneDecimal(number - firstPart)
                    return Triple(
                        firstPart,
                        secondPart,
                        operation
                    )
                }
                CompositionOperators.SUBTRACTION -> {
                    var bound = (number / 2).toInt()
                    if (bound <= 1)
                        bound = 2
                    var difference =
                        (RandomSamplerFunctionsOfJava.sampleRandomInt(1, bound)).toDouble()
                    var differenceSecondPart = 0.0
                    if (RandomSamplerFunctionsOfJava.sampleRandomBoolean() && level > 3) {
                        difference = +RandomSamplerFunctionsOfJava.sampleRandomDouble(0.0, 2.0)
                        differenceSecondPart =
                            RandomSamplerFunctionsOfJava.sampleRandomDouble(0.0, 1.0)
                    }
                    val firstPart = number + difference - differenceSecondPart
                    difference = roundToOneDecimal(difference + differenceSecondPart)
                    return Triple(
                        roundToOneDecimal(firstPart),
                        difference,
                        operation
                    )
                }
                CompositionOperators.MULTIPLICATION -> {
                    val coefficient = RandomSamplerFunctionsOfJava.sampleRandomDouble(1.0, 3.0)
                    val firstPart = number / coefficient
                    return Triple(firstPart, coefficient, operation)
                }
                CompositionOperators.DIVISION -> {
                    val coefficient = RandomSamplerFunctionsOfJava.sampleRandomDouble(1.0, 3.0)
                    val firstPart = number * coefficient
                    return Triple(firstPart, coefficient, operation)
                }
                CompositionOperators.NONE -> {
                    return Triple(number, null, operation)
                }
            }
        }

        fun generateScoreForCurrentStep(currentStep: Int, responseTimeInMs: Int): Int {
            val responseTimeInSec = ceil(responseTimeInMs / 1000.0)
            val levelWeight = ceil((currentStep + 1) / 10.0)
            return (ceil(levelWeight * 10) + ceil(responseTimeInSec * 10)).toInt()
        }

    }
}