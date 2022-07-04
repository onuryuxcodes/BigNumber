package io.hidro.bignumber.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.hidro.bignumber.util.Constants
import kotlin.random.Random

class BigNumberGameVM : ViewModel() {

    val numberPair = MutableLiveData<Pair<Int, Int>>()

    //Proportional to the probability of getting a hard question
    private var currentLevel = 0
    private var currentStep = 0
        set(value) {
            if (value > 10) {
                field = 0
                currentLevel++
            } else
                field = value
        }

    val timeIsUp = MutableLiveData(false)

    fun generateNewNumbers() {
        currentStep++
        numberPair.value = Pair(Random(10).nextInt(), Random(3).nextInt())
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

    private fun evaluateUsersAnswer(usersChoiceAsTheBigOne:Int, theOtherNumber:Int){
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

    fun getDurationForTheLevel(): Int {
        return (Constants.allowedTimeInMsForEachTurn) +
                (currentLevel * Constants.extraAllowedTimeInMsForEachLevelUp)
    }

}