package com.appodealstack.demo.nativead.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.appodeal.ads.NativeAd
import com.appodealstack.demo.nativead.databinding.NativeAdViewCustomBinding

class NativeAdViewCustom @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding =
        NativeAdViewCustomBinding.inflate(LayoutInflater.from(context), this, true)

    fun registerView(nativeAd: NativeAd) = with(binding) {
        val nativeAdView = root
        nativeAdView.titleView = nativeCustomTitle
        nativeAdView.descriptionView = nativeCustomDescription
        nativeAdView.callToActionView = nativeCustomCta
        nativeAdView.iconView = nativeCustomIcon
        nativeAdView.mediaView = nativeCustomMedia
        nativeAdView.registerView(nativeAd)
    }

    fun unregisterView() = with(binding) {
        val nativeAdView = root
        nativeAdView.unregisterView()
    }
}