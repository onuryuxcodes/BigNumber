package io.hidro.bignumber.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import io.hidro.bignumber.R
import io.hidro.bignumber.databinding.GameBinding
import io.hidro.bignumber.model.isOnGoing
import io.hidro.bignumber.util.Constants.Companion.SCORE_KEY
import io.hidro.bignumber.util.FormattingFunctions
import io.hidro.bignumber.vm.BigNumberGameVM


class BigNumberGameActivity : BaseActivity() {

    private lateinit var binding: GameBinding
    private lateinit var countDownAnimation: Animation

    private val viewModel: BigNumberGameVM by lazy {
        ViewModelProvider(this)[BigNumberGameVM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.game)
        loadCountDownAnimation()
        initializeGame()
        observeNumberSelection()
        observeIfTimeIsUp()
        startTheGame()
        observeScore()
        observeGameObject()
    }

    private fun observeNumberSelection() {
        binding.leftCard.setOnClickListener {
            viewModel.numberOnTheLeftIsChosen()
        }
        binding.rightCard.setOnClickListener {
            viewModel.numberOnTheRightIsChosen()
        }
    }

    private fun initializeGame() {
        viewModel.composedNumberPair.observe(this) {
            binding.left.text = FormattingFunctions.formatComposedNumbersForScreen(it.first)
            binding.right.text = FormattingFunctions.formatComposedNumbersForScreen(it.second)
            startCountDownAnimation()
        }
    }

    private fun observeScore() {
        viewModel.score.observe(this) {
            binding.score.text = getString(R.string.score, it.toString())
        }
    }

    private fun observeIfTimeIsUp() {
        viewModel.timeIsUp.observe(this) {
            if (it) {
                showGameEndedUI()
            }
        }
    }

    private fun startTheGame() {
        viewModel.generateNewNumbers()
        binding.timeIndicator.visibility = View.VISIBLE
    }

    private fun loadCountDownAnimation() {
        countDownAnimation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_left)
    }

    private fun startCountDownAnimation() {
        if (this::countDownAnimation.isInitialized) {
            countDownAnimation.cancel()
            countDownAnimation.duration = viewModel.getDurationForTheTurn()
            binding.timeIndicator.startAnimation(countDownAnimation)
        }
    }

    private fun showGameEndedUI() {
        countDownAnimation.cancel()
        val resultIntent = Intent()
        viewModel.score.value?.let {
            resultIntent.putExtra(SCORE_KEY, it)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun observeGameObject() {
        viewModel.bigNumberGame.observe(this) {
            if (!it.isOnGoing())
                showGameEndedUI()
        }
    }
}