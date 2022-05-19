package com.appodealstack.demo.nativead

import com.appodeal.ads.NativeAd

sealed interface DiffItem<T> {

    fun getItemData(): T

    fun getItemId(): Long

    fun getItemHash(): Int

    class DiffNative(private val nativeAd: NativeAd?) : DiffItem<NativeAd?> {

        override fun getItemData(): NativeAd? {
            return nativeAd
        }

        override fun getItemId(): Long {
            return 1
        }

        override fun getItemHash(): Int {
            return super.hashCode()
        }
    }

    class DiffUserData(private val userData: Int) : DiffItem<Int> {

        override fun getItemData(): Int {
            return userData
        }

        override fun getItemId(): Long {
            return 2
        }

        override fun getItemHash(): Int {
            return super.hashCode()
        }
    }
}