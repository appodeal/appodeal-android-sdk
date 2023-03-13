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

    fun setNativeAd(nativeAd: NativeAd) = with(binding) {
        val nativeAdView = root
        val descriptionView = nativeCustomDescription
        descriptionView.text = nativeAd.description
        descriptionView.isSelected = true
        nativeAdView.titleView = nativeCustomTitle.apply { text = nativeAd.title }
        nativeAdView.descriptionView = descriptionView
        nativeAdView.callToActionView = nativeCustomCta.apply { text = nativeAd.callToAction }
        nativeAdView.setNativeIconView(nativeCustomIcon.apply { clipToOutline = true })
        nativeAdView.setNativeMediaView(nativeCustomMedia)
        nativeAdView.registerView(nativeAd)
    }

    fun unregisterViewForInteraction() = with(binding) {
        val nativeAdView = root
        nativeAdView.unregisterViewForInteraction()
    }
}
