package io.hidro.bignumber.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.hidro.bignumber.model.ComposedNumber
import io.hidro.bignumber.util.CommonMathFunctions
import io.hidro.bignumber.util.CommonMathFunctions.Companion.roundToOneDecimal
import io.hidro.bignumber.util.CompositionOperators
import io.hidro.bignumber.util.Constants
import kotlin.random.Random

class BigNumberGameVM : ViewModel() {

    private val numberPair = MutableLiveData<Pair<Double, Double>>()
    val composedNumberPair = MutableLiveData<Pair<ComposedNumber, ComposedNumber>>()

    //Proportional to the probability of getting a hard question
    private var currentStep = 0
    val timeIsUp = MutableLiveData(false)
    var currentLowerBound = 0
    private var currentUpperBound = 100
    private var currentDeltaUpperBound = 100.0

    private fun incrementStepNumber() = currentStep++

    fun generateNewNumbers() {
        incrementStepNumber()
        val firstNumber = roundToOneDecimal(
            CommonMathFunctions.generateRandomInt(currentLowerBound, currentUpperBound).toDouble()
        )
        val delta = CommonMathFunctions.generateRandomDelta(currentStep, currentDeltaUpperBound)
        val secondNumber = roundToOneDecimal(firstNumber + delta)
        val composedNumber1 = ComposedNumber(
            unit1 = CommonMathFunctions.compose(
                firstNumber,
                CompositionOperators.SUM
            )
        )
        val composedNumber2 = ComposedNumber(
            unit1 = CommonMathFunctions.compose(
                secondNumber,
                CompositionOperators.NONE
            )
        )
        if (Random.nextBoolean()) {
            composedNumberPair.value = Pair(composedNumber1, composedNumber2)
            numberPair.value = Pair(firstNumber, secondNumber)
        } else {
            numberPair.value = Pair(secondNumber, firstNumber)
            composedNumberPair.value = Pair(composedNumber2, composedNumber1)
        }


    }

    fun numberOnTheLeftIsChosen() {
        numberPair.value?.let { numberPair ->
            evaluateUsersAnswer(numberPair.first, numberPair.second)
        }
    }

    fun numberOnTheRightIsChosen() {
        numberPair.value?.let { numberPair ->
            evaluateUsersAnswer(numberPair.second, numberPair.first)
        }
    }

    private fun evaluateUsersAnswer(usersChoiceAsTheBigOne: Double, theOtherNumber: Double) {
        numberPair.value?.let {
            when {
                usersChoiceAsTheBigOne >= theOtherNumber -> {
                    generateNewNumbers()
                }
                else -> {
                    endGame()
                }
            }
        }
    }

    private fun endGame() {

    }

    private fun getDurationForTheTurn(): Int {
        return Constants.allowedTimeInMsForEachStep
    }

}