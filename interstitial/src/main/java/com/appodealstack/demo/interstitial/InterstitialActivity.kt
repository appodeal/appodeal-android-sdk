package com.appodealstack.demo.interstitial

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.InterstitialCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.utils.Log.LogLevel
import com.appodealstack.demo.interstitial.databinding.ActivityInterstitialBinding

class InterstitialActivity : AppCompatActivity() {

    private var _binding: ActivityInterstitialBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInterstitialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSDK()
    }

    private fun setUpAppodealSDK() {
        Appodeal.setTesting(true)
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.INTERSTITIAL,
            object : ApdInitializationCallback {
                override fun onInitializationFinished(errors: List<ApdInitializationError>?) {
                    if (errors.isNullOrEmpty()) {
                        showToast("Appodeal initialized successfully")
                    } else {
                        showToast("Appodeal initialized with ${errors.size} errors")
                        errors.forEach { Log.e(TAG, "onInitializationFinished: ", it) }
                    }
                }
            }
        )

        binding.showInterstitial.setOnClickListener {
            if (Appodeal.canShow(Appodeal.INTERSTITIAL, placementName)) {
                Appodeal.show(this, Appodeal.INTERSTITIAL, placementName)
            } else {
                showToast("Cannot show interstitial")
            }
        }

        binding.cacheInterstitial.setOnClickListener {
            Appodeal.cache(this, Appodeal.INTERSTITIAL)
        }

        binding.autocacheInterstitial.setOnCheckedChangeListener { _, isChecked ->
            binding.cacheInterstitial.isEnabled = !isChecked
            Appodeal.setAutoCache(Appodeal.INTERSTITIAL, isChecked)
        }

        Appodeal.setInterstitialCallbacks(object : InterstitialCallbacks {
            override fun onInterstitialLoaded(isPrecache: Boolean) {
                showToast("Interstitial was loaded, isPrecache: $isPrecache")
            }

            override fun onInterstitialFailedToLoad() {
                showToast("Interstitial failed to load")
            }

            override fun onInterstitialClicked() {
                showToast("Interstitial was clicked")
            }

            override fun onInterstitialShowFailed() {
                showToast("Interstitial failed to show")
            }

            override fun onInterstitialShown() {
                showToast("Interstitial was shown")
            }

            override fun onInterstitialClosed() {
                showToast("Interstitial was closed")
            }

            override fun onInterstitialExpired() {
                showToast("Interstitial was expired")
            }
        })
    }
}

private const val placementName = "default"
private val TAG = InterstitialActivity::class.java.simpleName
private fun Context.showToast(message: String) =
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()