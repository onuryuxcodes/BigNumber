package io.hidro.bignumber.view

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import io.hidro.bignumber.R

class OnboardingActivity : BaseActivity() {

    private lateinit var binding: io.hidro.bignumber.databinding.ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)
        setCloseOnClick()
    }

    private fun setCloseOnClick() {
        binding.close.setOnClickListener {
            finish()
        }
        binding.done.setOnClickListener {
            finish()
        }
    }
}
