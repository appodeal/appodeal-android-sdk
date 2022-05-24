package com.appodealstack.demo.nativead

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.appodeal.ads.Appodeal
import com.appodeal.ads.NativeAd
import com.appodeal.ads.NativeCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed
import com.appodeal.ads.utils.Log.LogLevel
import com.appodealstack.demo.nativead.databinding.ActivityNativeBinding

class NativeActivity : AppCompatActivity() {
    /**
     * change to NativeAdViewNewsFeed::class || NativeAdViewContentStream::class || NativeAdViewAppWall::class to check other templates
     * */
    private val nativeAdViewType = NativeAdViewAppWall::class

    private val nativeCallback = object : NativeCallbacks {
        override fun onNativeLoaded() {
            showToast("Native was loaded")
        }

        override fun onNativeFailedToLoad() {
            showToast("Native failed to load")
        }

        override fun onNativeClicked(nativeAd: NativeAd?) {
            showToast("Native was clicked")
        }

        override fun onNativeShowFailed(nativeAd: NativeAd?) {
            showToast("Native failed to show")
        }

        override fun onNativeShown(nativeAd: NativeAd?) {
            showToast("Native was shown")
        }

        override fun onNativeExpired() {
            showToast("Native was expired")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Appodeal.setLogLevel(LogLevel.verbose)
        val binding = ActivityNativeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSDK(binding)
    }

    private fun setUpAppodealSDK(binding: ActivityNativeBinding) {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.NATIVE,
            object : ApdInitializationCallback {
                override fun onInitializationFinished(errors: List<ApdInitializationError>?) {
                    val initResult = if (errors.isNullOrEmpty()) "successfully" else "with ${errors.size} errors"
                    showToast("Appodeal initialized $initResult")
                    errors?.forEach {
                        Log.e(TAG, "onInitializationFinished: ", it)
                    }
                }
            })

        with(binding) {
            showNative.setOnClickListener {
                val availableNativeAdCount = Appodeal.getAvailableNativeAdsCount()
                val nativeAds = Appodeal.getNativeAds(availableNativeAdCount)
                val nativeAd = nativeAds.firstOrNull()
                if (nativeAd != null && nativeAd.canShow(this@NativeActivity, placementName)) {
                    when (nativeAdViewType) {
                        NativeAdViewAppWall::class -> {
                            nativeAdViewAppWall.isVisible = true
                            nativeAdViewAppWall.setNativeAd(nativeAd)
                        }
                        NativeAdViewNewsFeed::class -> {
                            nativeAdViewNewsFeed.isVisible = true
                            nativeAdViewNewsFeed.setNativeAd(nativeAd)
                        }
                        NativeAdViewContentStream::class -> {
                            nativeAdViewContentStream.isVisible = true
                            nativeAdViewContentStream.setNativeAd(nativeAd)
                        }
                    }
                } else {
                    showToast("Cannot show Native")
                }
            }

            hideNative.setOnClickListener {
                when (nativeAdViewType) {
                    NativeAdViewAppWall::class -> nativeAdViewAppWall.isVisible = false
                    NativeAdViewNewsFeed::class -> nativeAdViewNewsFeed.isVisible = false
                    NativeAdViewContentStream::class -> nativeAdViewContentStream.isVisible = false
                }
            }

            showInList.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_root_container, NativeListFragment())
                    .addToBackStack(TAG)
                    .commitAllowingStateLoss()
            }
        }
        Appodeal.setNativeCallbacks(nativeCallback)
    }
}

private const val placementName = "default"
private const val TAG = "NativeActivity"
private fun Context.showToast(message: String) =
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()