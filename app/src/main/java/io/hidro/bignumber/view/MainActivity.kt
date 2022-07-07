package io.hidro.bignumber.view

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import io.hidro.bignumber.R
import io.hidro.bignumber.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.play.setOnClickListener {
            goToActivity(Intent(this, BigNumberGameActivity::class.java))
        }
    }
}