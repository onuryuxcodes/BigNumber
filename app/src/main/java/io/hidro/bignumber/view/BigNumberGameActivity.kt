package io.hidro.bignumber.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.firebase.analytics.FirebaseAnalytics
import io.hidro.bignumber.R
import io.hidro.bignumber.databinding.GameBinding
import io.hidro.bignumber.model.isOnGoing
import io.hidro.bignumber.tracking.TrackingConstants
import io.hidro.bignumber.util.GeneralConstants.Companion.CORRECT_ANSWER
import io.hidro.bignumber.util.GeneralConstants.Companion.SCORE_KEY
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
        initializeLevel()
        initializeGame()
        observeNumberSelection()
        observeIfTimeIsUp()
        startTheGame()
        observeScore()
        observeGameObject()
        setAdIdToAdViewAndLoadBanner()
    }

    private fun setAdIdToAdViewAndLoadBanner() {
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
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
            binding.levelTitle.text = getString(R.string.level, viewModel.getCurrentLevel())
        }
    }

    private fun initializeLevel() {
        val level = getSavedLevel()
        viewModel.currentStep = level * 10
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
        viewModel.restartCountdownTimer()
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

    private fun showGameEndedUI(verballyConstructedCorrectAnswer: String? = null) {
        countDownAnimation.cancel()
        val resultIntent = Intent()
        viewModel.score.value?.let {
            resultIntent.putExtra(SCORE_KEY, it)
        }
        verballyConstructedCorrectAnswer?.let {
            resultIntent.putExtra(CORRECT_ANSWER, it)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun observeGameObject() {
        viewModel.bigNumberGame.observe(this) {
            var verballyConstructedCorrectAnswer: String? = null
            if (!it.isOnGoing()) {
                viewModel.isTheOneOnTheRightChosen?.let { isTheOneOnTheRightChosen ->
                    verballyConstructedCorrectAnswer = if (isTheOneOnTheRightChosen)
                        getString(
                            R.string.verbally_constructed_correct_answer,
                            FormattingFunctions.formatComposedNumbersForScreen(viewModel.composedNumberPair.value?.first),
                            FormattingFunctions.formatComposedNumbersForScreen(viewModel.composedNumberPair.value?.second)
                        )
                    else
                        getString(
                            R.string.verbally_constructed_correct_answer,
                            FormattingFunctions.formatComposedNumbersForScreen(viewModel.composedNumberPair.value?.second),
                            FormattingFunctions.formatComposedNumbersForScreen(viewModel.composedNumberPair.value?.first)
                        )

                }
                showGameEndedUI(verballyConstructedCorrectAnswer)
            }

        }
    }

    private fun logLevelEventToFirebaseAnalytics(levelWhenGameIsOver: Int, stepWhenGameIsOver:Int) {
        FirebaseAnalytics.getInstance(this)
            .logEvent(FirebaseAnalytics.Event.LEVEL_UP, Bundle().apply {
                putInt(TrackingConstants.LEVEL_KEY, levelWhenGameIsOver)
                putInt(TrackingConstants.STEP_KEY, stepWhenGameIsOver)
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        val currentLevel = viewModel.getCurrentLevel()
        logLevelEventToFirebaseAnalytics(levelWhenGameIsOver = currentLevel, stepWhenGameIsOver = viewModel.currentStep)
        saveLevel(currentLevel)
    }

}