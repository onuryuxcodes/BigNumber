package io.hidro.bignumber.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    fun goToActivity(intent: Intent, content: ActivityResultLauncher<Intent>) {
        content.launch(intent)
    }

    fun goToActivity(intent: Intent) = startActivity(intent)

    fun getSharedPreferences(): SharedPreferences =  getPreferences(Context.MODE_PRIVATE)
}