package io.hidro.bignumber.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import io.hidro.bignumber.R
import io.hidro.bignumber.databinding.ActivityMainBinding
import io.hidro.bignumber.util.Constants.Companion.HIGH_SCORE_KEY
import io.hidro.bignumber.util.Constants.Companion.SCORE_KEY

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private val receiverForActivityResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                showGameEndedUI(it.data?.getIntExtra(SCORE_KEY, 0))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.play.setOnClickListener {
            goToActivity(Intent(this, BigNumberGameActivity::class.java), receiverForActivityResult)
        }
        getHighestScore().let {
            if (it > 0) showHighestScoreReminder(it)
        }
    }

    private fun showGameEndedUI(score: Int?) {
        binding.play.text = getString(R.string.play_again)
        score?.let {
            val highestScore = getHighestScore()
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
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return 0
        return sharedPref.getInt(HIGH_SCORE_KEY, 0)
    }

    private fun saveNewHighScore(newHighScore: Int) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(HIGH_SCORE_KEY, newHighScore)
            apply()
        }
    }
}