package com.appodealstack.demo.nativead

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class NativeActivity : AppCompatActivity(), FragmentDetachListener {

    private var _binding: ActivityNativeBinding? = null
    private val binding get() = _binding!!

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
        _binding = ActivityNativeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSDK()
    }

    private fun setUpAppodealSDK() {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.NATIVE,
            object : ApdInitializationCallback {
                override fun onInitializationFinished(errors: List<ApdInitializationError>?) {
                    showToast("Appodeal initialized "
                            + if(errors.isNullOrEmpty()) "successfully" else "with ${errors.size} errors")
                    if (!errors.isNullOrEmpty()) {
                        errors.forEach {
                            Log.e(TAG, "onInitializationFinished: ", it)
                        }
                    }
                }
            })

        binding.showNative.setOnClickListener {
            val availableNativeAdCount = Appodeal.getAvailableNativeAdsCount()
            val nativeAds = Appodeal.getNativeAds(availableNativeAdCount)
            if (nativeAds.isNullOrEmpty()) {
                showToast("Native ad has not loaded")
                return@setOnClickListener
            }
            val nativeAd = nativeAds[0]
            if (nativeAd != null && nativeAd.canShow(this, placementName)) {
                when (nativeAdViewType) {
                    NativeAdViewAppWall::class -> {
                        binding.nativeAdViewAppWall.visibility = VISIBLE
                        binding.nativeAdViewAppWall.setNativeAd(nativeAd)
                    }
                    NativeAdViewNewsFeed::class -> {
                        binding.nativeAdViewNewsFeed.visibility = VISIBLE
                        binding.nativeAdViewNewsFeed.setNativeAd(nativeAd)
                    }
                    NativeAdViewContentStream::class -> {
                        binding.nativeAdViewContentStream.visibility = VISIBLE
                        binding.nativeAdViewContentStream.setNativeAd(nativeAd)
                    }
                }
            } else {
                showToast("Cannot show Native")
            }
        }
        binding.hideNative.setOnClickListener {
            when (nativeAdViewType) {
                NativeAdViewAppWall::class -> binding.nativeAdViewAppWall.visibility = GONE
                NativeAdViewNewsFeed::class -> binding.nativeAdViewNewsFeed.visibility = GONE
                NativeAdViewContentStream::class -> binding.nativeAdViewContentStream.visibility =
                    GONE
            }
        }

        binding.showInList.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_root_container, NativeListFragment())
                .addToBackStack(TAG)
                .commitAllowingStateLoss()
        }

        Appodeal.setNativeCallbacks(nativeCallback)
    }

    override fun onFragmentDetached() {
        Appodeal.setNativeCallbacks(nativeCallback)
    }
}

interface FragmentDetachListener {

    fun onFragmentDetached()
}

private const val placementName = "default"
private const val TAG = "NativeActivity"
private fun Context.showToast(message: String) =
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()