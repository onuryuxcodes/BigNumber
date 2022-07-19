package io.hidro.bignumber.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import io.hidro.bignumber.util.AdConstants
import io.hidro.bignumber.util.GeneralConstants
import io.hidro.bignumber.util.GeneralConstants.Companion.BASE_ACTIVITY_TAG


open class BaseActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        firebaseAnalytics = Firebase.analytics
    }

    fun goToActivity(intent: Intent, content: ActivityResultLauncher<Intent>) {
        content.launch(intent)
    }

    fun goToActivity(intent: Intent) = startActivity(intent)

    fun getSharedPreferences(): SharedPreferences = getPreferences(Context.MODE_PRIVATE)

    fun loadInterstitialAd(tag: String? = null) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            AdConstants.INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(tag ?: BASE_ACTIVITY_TAG, adError.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(tag ?: BASE_ACTIVITY_TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })
    }

    fun showInterstitialAd() {
        mInterstitialAd?.show(this)
    }

    fun setInterstitialAdCallback(tag: String? = null) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(tag ?: BASE_ACTIVITY_TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(tag ?: BASE_ACTIVITY_TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                Log.e(tag ?: BASE_ACTIVITY_TAG, "Ad failed to show fullscreen content.")
                super.onAdFailedToShowFullScreenContent(p0)
            }

            override fun onAdImpression() {
                Log.d(tag ?: BASE_ACTIVITY_TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(tag ?: BASE_ACTIVITY_TAG, "Ad showed fullscreen content.")
            }
        }
    }
    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view: View? = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getFeedbackCount(): Int {
        val sharedPref = getSharedPreferences()
        return sharedPref.getInt(GeneralConstants.FEEDBACK_COUNT, 0)
    }
}