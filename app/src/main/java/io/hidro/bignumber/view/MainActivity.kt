package io.hidro.bignumber.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.review.ReviewManagerFactory
import io.hidro.bignumber.BuildConfig
import io.hidro.bignumber.R
import io.hidro.bignumber.databinding.ActivityMainBinding
import io.hidro.bignumber.util.AdConstants.Companion.countOfGamePlayToShowAdAfter
import io.hidro.bignumber.util.AnimationUtils.Companion.createSpringAnimation
import io.hidro.bignumber.util.GeneralConstants.Companion.CORRECT_ANSWER
import io.hidro.bignumber.util.GeneralConstants.Companion.DEBUG
import io.hidro.bignumber.util.GeneralConstants.Companion.HAS_SEEN_ONBOARDING
import io.hidro.bignumber.util.GeneralConstants.Companion.HIGH_SCORE_KEY
import io.hidro.bignumber.util.GeneralConstants.Companion.PLAY_COUNT
import io.hidro.bignumber.util.GeneralConstants.Companion.SCORE_KEY
import io.hidro.bignumber.util.GeneralConstants.Companion.SUCCESSFUL_GAME_PLAY_COUNT
import io.hidro.bignumber.util.GameParameters.Companion.countOfGamePlayToShowRatePopup
import io.hidro.bignumber.util.GameParameters.Companion.successfulGamePlayMinScore


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var springAnimationXAxis: SpringAnimation
    lateinit var springAnimationYAxis: SpringAnimation

    private val receiverForActivityResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val score = it.data?.getIntExtra(SCORE_KEY, 0)
                val verbalAnswer = it.data?.getStringExtra(CORRECT_ANSWER)
                showGameEndedUI(score, verbalAnswer)
                showAdIfMeetsCondition()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setClickListeners()
        checkForHighestScoreReminder()
        initAdMob()
        setSpringAnimationToBrainIcon()
        addDebugTagIfNotProd()
        if (showRatePopupCondition())
            showRatePopup()
        loadInterstitialAd()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSpringAnimationToBrainIcon() {
        val viewForSpringAnim = binding.lottieAnimView
        viewForSpringAnim.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                springAnimationXAxis = createSpringAnimation(
                    viewForSpringAnim,
                    SpringAnimation.X,
                    viewForSpringAnim.x,
                    SpringForce.STIFFNESS_MEDIUM,
                    SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
                )
                springAnimationYAxis = createSpringAnimation(
                    viewForSpringAnim,
                    SpringAnimation.Y,
                    viewForSpringAnim.y,
                    SpringForce.STIFFNESS_MEDIUM,
                    SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
                )
                viewForSpringAnim.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        var dX = 0f
        var dY = 0f
        viewForSpringAnim.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // capture the difference between view's top left corner and touch point
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY

                    // cancel animations so we can grab the view during previous animation
                    springAnimationXAxis.cancel()
                    springAnimationYAxis.cancel()
                }
                MotionEvent.ACTION_MOVE -> {
                    //  a different approach would be to change the view's LayoutParams.
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
                MotionEvent.ACTION_UP -> {
                    springAnimationXAxis.start()
                    springAnimationYAxis.start()
                }
            }
            true
        }
    }

    private fun goToOnboarding() {
        if (!hasSeenOnboarding()) {
            setHasSeenOnboarding()
            goToActivity(Intent(this, OnboardingActivity::class.java))
            binding.play.text = getString(R.string.play)
        }
    }

    private fun setClickListeners() {
        binding.play.setOnClickListener {
            incrementPlayCount()
            goToActivity(Intent(this, BigNumberGameActivity::class.java), receiverForActivityResult)
        }
        binding.aboutText.setOnClickListener {
            goToActivity(Intent(this, AboutActivity::class.java))
        }
        if (!hasSeenOnboarding()) {
            binding.play.text = getString(R.string.get_started)
            binding.play.setOnClickListener {
                goToOnboarding()
                setClickListeners()
            }
        }
        binding.feedbackComponent.setOnClickListener {
            goToActivity(Intent(this, FeedbackActivity::class.java))
        }
    }

    private fun checkForHighestScoreReminder() {
        getHighestScore().let {
            if (it > 0) showHighestScoreReminder(it)
        }
    }

    private fun showRatePopupCondition() =
        getSuccessfulGamePlayCount() == countOfGamePlayToShowRatePopup

    private fun showAdCondition() =
        getPlayCount() % countOfGamePlayToShowAdAfter == 0

    private fun showAdIfMeetsCondition() {
        if (showAdCondition())
            showInterstitialAd()
    }

    private fun showGameEndedUI(score: Int?, correctAnswer: String?) {
        binding.play.text = getString(R.string.play_again)
        score?.let {
            val highestScore = getHighestScore()
            if (score >= successfulGamePlayMinScore)
                incrementSuccessfulGamePlayCount()
            if (highestScore < score) {
                saveNewHighScore(score)
                showANewHighScoreUI(score)
            } else
                showPlayAgainUI(score, highestScore)
        }
        correctAnswer?.let {
            binding.verbalAnswer.text = it
        }
    }

    private fun showHighestScoreReminder(highScore: Int) {
        binding.reminderText.visibility = View.VISIBLE
        binding.reminderText.text = getString(R.string.high_score_reminder, highScore)
    }

    private fun showANewHighScoreUI(score: Int?) {
        binding.reminderText.visibility = View.INVISIBLE
        binding.mainText.text = getString(R.string.new_high_score, score)
    }

    private fun showPlayAgainUI(score: Int?, highScore: Int) {
        binding.mainText.text = getString(R.string.your_score, score)
        showHighestScoreReminder(highScore)
    }

    private fun getHighestScore(): Int {
        val sharedPref = getSharedPreferences()
        return sharedPref.getInt(HIGH_SCORE_KEY, 0)
    }

    private fun saveNewHighScore(newHighScore: Int) {
        val sharedPref = getSharedPreferences()
        with(sharedPref.edit()) {
            putInt(HIGH_SCORE_KEY, newHighScore)
            apply()
        }
    }

    private fun getPlayCount(): Int {
        val sharedPref = getSharedPreferences()
        return sharedPref.getInt(PLAY_COUNT, 0)
    }

    private fun getSuccessfulGamePlayCount(): Int {
        val sharedPref = getSharedPreferences()
        return sharedPref.getInt(PLAY_COUNT, 0)
    }

    private fun hasSeenOnboarding(): Boolean {
        val sharedPref = getSharedPreferences()
        return sharedPref.getBoolean(HAS_SEEN_ONBOARDING, false)
    }

    private fun setHasSeenOnboarding() {
        val sharedPref = getSharedPreferences()
        with(sharedPref.edit()) {
            putBoolean(HAS_SEEN_ONBOARDING, true)
            apply()
        }
    }

    private fun incrementPlayCount() {
        val playCount = getPlayCount()
        val sharedPref = getSharedPreferences()
        with(sharedPref.edit()) {
            putInt(PLAY_COUNT, playCount + 1)
            apply()
        }
    }

    private fun incrementSuccessfulGamePlayCount() {
        val playCount = getSuccessfulGamePlayCount()
        val sharedPref = getSharedPreferences()
        with(sharedPref.edit()) {
            putInt(SUCCESSFUL_GAME_PLAY_COUNT, playCount + 1)
            apply()
        }
    }

    private fun showRatePopup() {
        binding.play.isEnabled = false
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { reviewInfo ->
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener {
                        binding.play.isEnabled = true
                    }
                }
            } else {
                binding.play.isEnabled = true
                Log.d("review task exception", task.exception?.message ?: "")
            }
        }
    }

    private fun addDebugTagIfNotProd() {
        if (BuildConfig.BUILD_TYPE == DEBUG)
            binding.betaTag.text = getString(R.string.debug_version)
    }

    private fun initAdMob() {
        MobileAds.initialize(this) {}
    }

    override fun onResume() {
        super.onResume()
        if (getFeedbackCount() > 0)
            binding.feedbackComponent.visibility = View.GONE
        else if (getFeedbackCount() == 0 && getPlayCount() > 10)
            binding.feedbackComponent.visibility = View.VISIBLE
    }
}