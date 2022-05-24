package com.appodealstack.demo.nativead.adapter

import com.appodeal.ads.NativeAd

sealed interface ListItem {
    fun getItemId(): Int

    class NativeAdItem(val nativeAd: NativeAd?) : ListItem {
        override fun getItemId() = AD_ITEM

        companion object {
            const val AD_ITEM = 1
        }
    }

    data class YourDataItem(val userData: Int) : ListItem {
        override fun getItemId() = USER_ITEM

        companion object {
            const val USER_ITEM = 2
        }
    }
}