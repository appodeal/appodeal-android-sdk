package com.appodealstack.demo.nativead.adapter

import com.appodeal.ads.NativeAd

sealed interface ListItem {
    fun getItemId(): Int

    class DynamicNativeAdItem(val getNativeAd: () -> NativeAd?) : ListItem {
        override fun getItemId() = DYNAMIC_AD_ITEM

        companion object {
            const val DYNAMIC_AD_ITEM = 3
        }
    }

    data class YourDataItem(val userData: Int) : ListItem {
        override fun getItemId() = USER_ITEM

        companion object {
            const val USER_ITEM = 2
        }
    }
}