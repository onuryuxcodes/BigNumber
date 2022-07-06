package io.hidro.bignumber.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    fun goToActivity(intent: Intent) = startActivity(intent)
}