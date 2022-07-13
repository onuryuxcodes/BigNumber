package io.hidro.bignumber.vm

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.hidro.bignumber.model.BigNumberGame
import io.hidro.bignumber.model.ComposedNumber
import io.hidro.bignumber.util.CommonMathFunctions
import io.hidro.bignumber.util.CommonMathFunctions.Companion.roundToOneDecimal
import io.hidro.bignumber.util.CompositionOperators
import io.hidro.bignumber.util.GameParameters
import io.hidro.bignumber.util.GameParameters.Companion.additionalTimeInMsForEachLevel
import kotlin.random.Random

class BigNumberGameVM : ViewModel() {

    val bigNumberGame = MutableLiveData<BigNumberGame>()
    private val numberPair = MutableLiveData<Pair<Double, Double>>()
    val composedNumberPair = MutableLiveData<Pair<ComposedNumber, ComposedNumber>>()
    val score = MutableLiveData(0)
    private var timeUserStartedTheCurrentStep: Long = 0L
    private var gameStartTimeInMs: Long? = null

    //Proportional to the probability of getting a hard question
    private var currentStep = 0
    val timeIsUp = MutableLiveData(false)
    var currentLowerBound = 0
    private var currentUpperBound = 100
    private var currentDeltaUpperBound = 100.0
    private var timer: CountDownTimer? = null


    private fun incrementStepNumber() = currentStep++

    fun generateNewNumbers() {
        if (gameStartTimeInMs == null)
            gameStartTimeInMs = System.currentTimeMillis()
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
            numberPair.value = Pair(firstNumber, secondNumber)
            composedNumberPair.value = Pair(composedNumber1, composedNumber2)
        } else {
            numberPair.value = Pair(secondNumber, firstNumber)
            composedNumberPair.value = Pair(composedNumber2, composedNumber1)
        }
        timeUserStartedTheCurrentStep = System.currentTimeMillis()

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
                    updateScore()
                    generateNewNumbers()
                    restartCountdownTimer()
                }
                else -> {
                    endGame()
                }
            }
        }
    }

    private fun updateScore() {
        val timeItTookUserToRespond = System.currentTimeMillis() - timeUserStartedTheCurrentStep
        val scoreFromCurrentStep = CommonMathFunctions.generateScoreForCurrentStep(
            currentStep,
            timeItTookUserToRespond.toInt()
        )
        score.value = score.value?.plus(scoreFromCurrentStep)
    }

    private fun restartCountdownTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(getDurationForTheTurn(), 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                timeIsUp.value = true
            }
        }.start()
    }

    private fun endGame() {
        bigNumberGame.value = BigNumberGame(gameStartTimeInMs ?: 0L, System.currentTimeMillis())
    }

    private fun getCurrentLevel(): Int {
        return currentStep / 10
    }

    fun getDurationForTheTurn(): Long {
        return GameParameters.allowedTimeInMsForEachStep + (getCurrentLevel() * additionalTimeInMsForEachLevel).toLong()
    }

}