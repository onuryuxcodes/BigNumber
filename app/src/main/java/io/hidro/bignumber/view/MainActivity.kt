package io.hidro.bignumber.view

import android.content.Intent
import android.os.Bundle
import io.hidro.bignumber.R

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goToActivity(Intent(this, BigNumberGameActivity::class.java))
    }
}