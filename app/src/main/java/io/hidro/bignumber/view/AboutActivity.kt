package io.hidro.bignumber.view

import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import io.hidro.bignumber.R
import io.hidro.bignumber.databinding.AboutBinding
import io.hidro.bignumber.util.Constants.Companion.ONUR_PERSONAL_WEB

class AboutActivity : BaseActivity() {

    private lateinit var binding: AboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.about)
        setCloseOnClick()
        setLinkOnClick()
    }

    private fun setLinkOnClick() {
        binding.aboutLink.setOnClickListener {
            val url = ONUR_PERSONAL_WEB
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent: CustomTabsIntent = builder.build()
            builder.setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder().setToolbarColor(
                    ContextCompat.getColor(this, R.color.purple_700)
                ).build()
            )
            builder.setStartAnimations(
                this,
                R.anim.slide_in_from_bottom,
                R.anim.slide_down_from_top
            )
            customTabsIntent.launchUrl(this, Uri.parse(url))
        }
    }

    private fun setCloseOnClick() {
        binding.close.setOnClickListener {
            finish()
        }
    }
}