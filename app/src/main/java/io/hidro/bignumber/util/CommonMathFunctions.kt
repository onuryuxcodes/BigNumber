package io.hidro.bignumber.util

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.random.Random

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
                Random.nextInt(upperBound)+lowerBound
            else Random.nextInt(abs(upperBound))
        }

        const val minDelta = 0.3

        /**
         * Returns a random integer between 0.1 to 100
         * Probability of delta being smaller increases disproportionally to [currentStep]
         * Delta's range is [minDelta] to 100
         */
        fun generateRandomDelta(currentStep: Int, upperBound: Double): Double {
            var randomDelta = (Random.nextDouble(upperBound / (currentStep + 1))) / 10
            if (randomDelta < minDelta)
                randomDelta = minDelta
            if (Random.nextBoolean())
                randomDelta *= -1
            return randomDelta
        }

        /**
         * Returns a random operator defined in [CompositionOperators]
         * [CompositionOperators.SUM] has the highest probability of 50%
         * Followed by [CompositionOperators.NONE]  20%
         */
        fun pickARandomCompositionOperator(): CompositionOperators {
            val randomInteger = Random.nextInt(100)
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
        fun pickARandomCompositionType(): Int = Random.nextInt(3)+1

        /**
         * Returns [CompositionOperators.SUM] or [CompositionOperators.SUBTRACTION]
         * based on random boolean
         */
        fun pickARandomCompositionTypeBetweenSumAndSubtraction(): CompositionOperators {
            return if (Random.nextBoolean())
                CompositionOperators.SUBTRACTION
            else
                CompositionOperators.SUM
        }

        /**
         * Composes given [number] from [operation]
         */
        fun compose(
            number: Double,
            operation: CompositionOperators
        ): Triple<Double, Double?, CompositionOperators> {
            when (operation) {
                CompositionOperators.SUM -> {
                    var bound = number.toInt()
                    if (bound == 0)
                        bound++
                    val firstPart = Random.nextInt(bound)
                    val secondPart = number - firstPart
                    return Triple(
                        roundToOneDecimal(firstPart.toDouble()),
                        roundToOneDecimal(secondPart),
                        operation
                    )
                }
                CompositionOperators.SUBTRACTION -> {
                    var bound = (number / 2).toInt()
                    if (bound <= 1)
                        bound = 2
                    var difference = (Random.nextInt(bound)+1).toDouble()
                    if (Random.nextBoolean())
                        difference = +Random.nextDouble(0.1)
                    val firstPart = number + difference
                    difference = roundToOneDecimal(difference)
                    return Triple(
                        roundToOneDecimal(firstPart),
                        difference,
                        operation
                    )
                }
                CompositionOperators.MULTIPLICATION -> {
                    val coefficient = Random.nextDouble(3.0)+1
                    val firstPart = number / coefficient
                    return Triple(firstPart, coefficient, operation)
                }
                CompositionOperators.DIVISION -> {
                    val coefficient = Random.nextDouble(3.0)+1
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