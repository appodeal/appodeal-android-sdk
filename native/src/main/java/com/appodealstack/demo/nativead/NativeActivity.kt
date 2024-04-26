package com.appodealstack.demo.nativead

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.appodeal.ads.Appodeal
import com.appodeal.ads.NativeAd
import com.appodeal.ads.NativeCallbacks
import com.appodeal.ads.NativeMediaViewContentType
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.nativead.NativeAdView
import com.appodeal.ads.nativead.NativeAdViewAppWall
import com.appodeal.ads.nativead.NativeAdViewContentStream
import com.appodeal.ads.nativead.NativeAdViewNewsFeed
import com.appodeal.ads.nativead.Position
import com.appodeal.ads.utils.Log.LogLevel
import com.appodealstack.demo.nativead.databinding.ActivityNativeBinding

class NativeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNativeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSDK(binding)
    }

    private fun setUpAppodealSDK(binding: ActivityNativeBinding) {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.setPreferredNativeContentType(NativeMediaViewContentType.Auto)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.NATIVE
        ) { errors: List<ApdInitializationError>? ->
            val initResult =
                if (errors.isNullOrEmpty()) "successfully" else "with ${errors.size} errors"
            showToast("Appodeal initialized $initResult")
            errors?.forEach {
                Log.e(TAG, "onInitializationFinished: ", it)
            }
        }

        with(binding) {
            showNative.setOnClickListener {
                val nativeAd = Appodeal.getNativeAds(1).firstOrNull()
                if (nativeAd == null) {
                    showToast("Native ad has not loaded")
                    return@setOnClickListener
                }
                if (nativeAd.canShow(this@NativeActivity, placementName)) {
                    when (nativeAdViewType) {
                        NativeAdViewAppWall::class -> {
                            configureNativeAdView(nativeAdViewAppWall)
                            nativeAdViewAppWall.registerView(nativeAd)
                        }

                        NativeAdViewNewsFeed::class -> {
                            configureNativeAdView(nativeAdViewNewsFeed)
                            nativeAdViewNewsFeed.registerView(nativeAd)
                        }

                        NativeAdViewContentStream::class -> {
                            configureNativeAdView(nativeAdViewContentStream)
                            nativeAdViewContentStream.registerView(nativeAd)
                        }

                        else -> {
                            configureNativeAdView(binding.nativeAdViewCustom.root)
                            binding.nativeAdViewCustom.root.registerView(nativeAd)
                        }
                    }
                } else {
                    showToast("Cannot show Native")
                }
            }
            hideNative.setOnClickListener {
                when (nativeAdViewType) {
                    NativeAdViewAppWall::class -> {
                        nativeAdViewAppWall.isVisible = false
                        nativeAdViewAppWall.unregisterView()
                    }

                    NativeAdViewNewsFeed::class -> {
                        nativeAdViewNewsFeed.isVisible = false
                        nativeAdViewNewsFeed.unregisterView()
                    }

                    NativeAdViewContentStream::class -> {
                        nativeAdViewContentStream.isVisible = false
                        nativeAdViewContentStream.unregisterView()
                    }

                    else -> {
                        nativeAdViewCustom.root.isVisible = false
                        nativeAdViewCustom.root.unregisterView()
                    }
                }
            }
            showInList.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_root_container, NativeListFragment())
                    .addToBackStack(TAG)
                    .commitAllowingStateLoss()
            }
        }

        Appodeal.setNativeCallbacks(object : NativeCallbacks {
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
        })
    }

    companion object {
        /**
         * Use NativeAdView::class to checking your custom layout view.
         * Use NativeAdViewNewsFeed::class or
         * NativeAdViewContentStream::class or
         * NativeAdViewAppWall::class to check native templates
         * */
        val nativeAdViewType = NativeAdView::class

        fun configureNativeAdView(nativeAdView: NativeAdView) {
            nativeAdView.setAdChoicesPosition(Position.END_TOP)
            nativeAdView.setAdAttributionBackground(Color.RED)
            nativeAdView.setAdAttributionTextColor(Color.WHITE)
        }
    }
}

private const val placementName = "default"
private const val TAG = "NativeActivity"
private fun Context.showToast(message: String) =
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()