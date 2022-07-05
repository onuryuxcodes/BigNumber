package io.hidro.bignumber.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.hidro.bignumber.util.CommonMathFunctions
import io.hidro.bignumber.util.Constants

class BigNumberGameVM : ViewModel() {

    val numberPair = MutableLiveData<Pair<Double, Double>>()
    //Proportional to the probability of getting a hard question
    private var currentStep = 0
    val timeIsUp = MutableLiveData(false)
    var currentLowerBound = 0
    var currentUpperBound = 10000

    private fun incrementStepNumber() = currentStep++

    fun generateNewNumbers() {
        incrementStepNumber()
        val firstNumber = CommonMathFunctions.generateRandomInt(currentLowerBound, currentUpperBound).toDouble()
        val delta = CommonMathFunctions.generateRandomDelta(currentStep)
        numberPair.value = Pair(
            firstNumber,
            firstNumber+delta)
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

    private fun evaluateUsersAnswer(usersChoiceAsTheBigOne:Double, theOtherNumber:Double){
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