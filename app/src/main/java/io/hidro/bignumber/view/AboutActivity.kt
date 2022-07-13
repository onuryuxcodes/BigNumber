package io.hidro.bignumber.view

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import io.hidro.bignumber.R
import io.hidro.bignumber.databinding.AboutBinding

class AboutActivity : BaseActivity() {

    private lateinit var binding: AboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.about)
        setCloseOnClick()
    }

    private fun setCloseOnClick() {
        binding.close.setOnClickListener {
            finish()
        }
    }
}