package io.hidro.bignumber.view

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import io.hidro.bignumber.R
import io.hidro.bignumber.databinding.ActivityFeedbackBinding
import io.hidro.bignumber.util.GeneralConstants
import io.hidro.bignumber.vm.FeedbackVM

class FeedbackActivity : BaseActivity() {

    private lateinit var binding: ActivityFeedbackBinding

    private val viewModel: FeedbackVM by lazy {
        ViewModelProvider(this)[FeedbackVM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback)
        senOnClickListeners()
    }

    private fun senOnClickListeners() {
        binding.close.setOnClickListener {
            finish()
        }
        binding.send.setOnClickListener {
            val inputEditable = binding.feedbackEdittext.text
            inputEditable.let {
                val inputString = it.toString()
                if (inputString.length > 2) {
                    viewModel.saveUserFeedback(inputString)
                    binding.send.isEnabled = false
                    hideKeyboard(this)
                    Toast.makeText(this, getString(R.string.thank_you), Toast.LENGTH_LONG).show()
                    saveFeedbackCount()
                    finish()
                }
            }
        }
    }

    private fun saveFeedbackCount() {
        val newFeedbackCount = getFeedbackCount() + 1
        val sharedPref = getSharedPreferences()
        with(sharedPref.edit()) {
            putInt(GeneralConstants.FEEDBACK_COUNT, newFeedbackCount)
            apply()
        }
    }
}