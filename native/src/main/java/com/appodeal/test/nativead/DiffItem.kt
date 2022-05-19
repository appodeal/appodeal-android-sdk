package com.appodeal.test.nativead

import com.appodeal.ads.NativeAd

sealed interface DiffItem<T> {

    fun getItemData(): T

    fun getItemId(): Int

    fun getItemHash(): Int

    class DiffNativeAd(private val nativeAd: NativeAd?) : DiffItem<NativeAd?> {

        override fun getItemData(): NativeAd? {
            return nativeAd
        }

        override fun getItemId(): Int {
            return AD_ITEM
        }

        override fun getItemHash(): Int {
            return nativeAd.hashCode()
        }

        companion object {
            const val AD_ITEM = 1
        }
    }

    class DiffUserData(private val userData: Int) : DiffItem<Int> {

        override fun getItemData(): Int {
            return userData
        }

        override fun getItemId(): Int {
            return USER_ITEM
        }

        override fun getItemHash(): Int {
            return userData.hashCode()
        }

        companion object {
            const val USER_ITEM = 2
        }
    }
}