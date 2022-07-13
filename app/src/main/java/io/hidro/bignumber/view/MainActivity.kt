package io.hidro.bignumber.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import io.hidro.bignumber.R
import io.hidro.bignumber.databinding.ActivityMainBinding
import io.hidro.bignumber.util.Constants.Companion.HIGH_SCORE_KEY
import io.hidro.bignumber.util.Constants.Companion.PLAY_COUNT
import io.hidro.bignumber.util.Constants.Companion.SCORE_KEY
import io.hidro.bignumber.util.Constants.Companion.SUCCESSFUL_GAME_PLAY_COUNT
import io.hidro.bignumber.util.GameParameters.Companion.countOfGamePlayToShowRatePopup
import io.hidro.bignumber.util.GameParameters.Companion.successfulGamePlayMinScore

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private val receiverForActivityResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val score = it.data?.getIntExtra(SCORE_KEY, 0)
                showGameEndedUI(score)
                if (showRatePopupCondition())
                    showRatePopup()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.play.setOnClickListener {
            incrementPlayCount()
            goToActivity(Intent(this, BigNumberGameActivity::class.java), receiverForActivityResult)
        }
        getHighestScore().let {
            if (it > 0) showHighestScoreReminder(it)
        }
    }

    private fun showRatePopupCondition() =
        getSuccessfulGamePlayCount() == countOfGamePlayToShowRatePopup

    private fun showGameEndedUI(score: Int?) {
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
        val sharedPref = getSharedPreferences() ?: return 0
        return sharedPref.getInt(HIGH_SCORE_KEY, 0)
    }

    private fun saveNewHighScore(newHighScore: Int) {
        val sharedPref = getSharedPreferences() ?: return
        with(sharedPref.edit()) {
            putInt(HIGH_SCORE_KEY, newHighScore)
            apply()
        }
    }

    private fun getPlayCount(): Int {
        val sharedPref = getSharedPreferences() ?: return 0
        return sharedPref.getInt(PLAY_COUNT, 0)
    }

    private fun getSuccessfulGamePlayCount(): Int {
        val sharedPref = getSharedPreferences() ?: return 0
        return sharedPref.getInt(PLAY_COUNT, 0)
    }

    private fun incrementPlayCount() {
        val playCount = getPlayCount()
        val sharedPref = getSharedPreferences() ?: return
        with(sharedPref.edit()) {
            putInt(PLAY_COUNT, playCount + 1)
            apply()
        }
    }

    private fun incrementSuccessfulGamePlayCount() {
        val playCount = getSuccessfulGamePlayCount()
        val sharedPref = getSharedPreferences() ?: return
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
                    flow.addOnCompleteListener { _ ->
                        binding.play.isEnabled = true
                    }
                }
            } else {
                binding.play.isEnabled = true
                Log.d("review task exception", task.exception?.message ?: "")
            }
        }

    }
}