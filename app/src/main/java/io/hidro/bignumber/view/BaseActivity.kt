package io.hidro.bignumber.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

open class BaseActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        firebaseAnalytics = Firebase.analytics
    }

    fun goToActivity(intent: Intent, content: ActivityResultLauncher<Intent>) {
        content.launch(intent)
    }

    fun goToActivity(intent: Intent) = startActivity(intent)

    fun getSharedPreferences(): SharedPreferences =  getPreferences(Context.MODE_PRIVATE)
}