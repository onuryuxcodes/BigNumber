package io.hidro.bignumber.vm

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.hidro.bignumber.api.FeedbackApi
import io.hidro.bignumber.model.BigNumberGame
import io.hidro.bignumber.model.ComposedNumber
import io.hidro.bignumber.model.actualValue
import io.hidro.bignumber.util.CommonMathFunctions
import io.hidro.bignumber.util.CommonMathFunctions.Companion.roundToOneDecimal
import io.hidro.bignumber.util.CompositionOperators
import io.hidro.bignumber.util.GameParameters
import io.hidro.bignumber.util.GameParameters.Companion.additionalTimeInMsForEachLevel
import io.hidro.bignumber.util.GameParameters.Companion.stepsEachLevel
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random

class BigNumberGameVM : ViewModel() {

    val bigNumberGame = MutableLiveData<BigNumberGame>()
    val composedNumberPair = MutableLiveData<Pair<ComposedNumber, ComposedNumber>>()
    val score = MutableLiveData(0)
    private var timeUserStartedTheCurrentStep: Long = 0L
    private var gameStartTimeInMs: Long? = null

    var currentStep = 0
    val timeIsUp = MutableLiveData(false)
    var currentLowerBound = 0
    private var currentUpperBound = 100
    private var timer: CountDownTimer? = null
    var isTheOneOnTheRightChosen: Boolean? = null

    private fun incrementStepNumber() = currentStep++

    fun generateNewNumbers() {
        if (gameStartTimeInMs == null)
            gameStartTimeInMs = System.currentTimeMillis()
        incrementStepNumber()
        val firstNumber = roundToOneDecimal(
            CommonMathFunctions.generateRandomInt(currentLowerBound, currentUpperBound).toDouble()
        )
        val delta = CommonMathFunctions.generateRandomDelta(getCurrentLevel())
        val secondNumber = roundToOneDecimal(firstNumber + delta)
        val randomCompositionType =
            CommonMathFunctions.pickARandomCompositionTypeBetweenSumAndSubtraction()
        val composedNumber1 = ComposedNumber(
            unit1 = CommonMathFunctions.compose(
                firstNumber,
                randomCompositionType,
                getCurrentLevel()
            )
        )
        val composedNumber2 = ComposedNumber(
            unit1 = CommonMathFunctions.compose(
                secondNumber,
                CompositionOperators.NONE,
                getCurrentLevel()
            )
        )
        if (Random.nextBoolean()) {
            composedNumberPair.value = Pair(composedNumber1, composedNumber2)
        } else {
            composedNumberPair.value = Pair(composedNumber2, composedNumber1)
        }
        timeUserStartedTheCurrentStep = System.currentTimeMillis()
    }

    fun numberOnTheLeftIsChosen() {
        composedNumberPair.value?.let { numberPair ->
            isTheOneOnTheRightChosen = false
            evaluateUsersAnswer(numberPair.first.actualValue(), numberPair.second.actualValue())
        }
    }

    fun numberOnTheRightIsChosen() {
        composedNumberPair.value?.let { numberPair ->
            isTheOneOnTheRightChosen = true
            evaluateUsersAnswer(numberPair.second.actualValue(), numberPair.first.actualValue())
        }
    }

    private fun evaluateUsersAnswer(usersChoiceAsTheBigOne: Double, theOtherNumber: Double) {
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

    private fun updateScore() {
        val timeItTookUserToRespond = System.currentTimeMillis() - timeUserStartedTheCurrentStep
        val scoreFromCurrentStep = CommonMathFunctions.generateScoreForCurrentStep(
            currentStep,
            timeItTookUserToRespond.toInt()
        )
        score.value = score.value?.plus(scoreFromCurrentStep)
    }

    fun restartCountdownTimer() {
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

    fun getCurrentLevel(): Int {
        return ceil(currentStep / stepsEachLevel).toInt()
    }

    fun getDurationForTheTurn(): Long {
        return GameParameters.allowedTimeInMsForEachStep + (getCurrentLevel() * additionalTimeInMsForEachLevel).toLong()
    }

}