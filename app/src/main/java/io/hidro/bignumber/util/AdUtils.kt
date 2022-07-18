package io.hidro.bignumber.util

import com.google.android.gms.ads.nativead.NativeAd
import io.hidro.bignumber.databinding.NativeAdViewBinding

class AdUtils {
    companion object {
        fun showAdOnView(ad: NativeAd, viewBinding:NativeAdViewBinding) {
            viewBinding.adTitle.text = ad.headline
            viewBinding.adSubtitle.text = ad.advertiser
        }
    }
}