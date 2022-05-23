package com.appodealstack.demo.rewarded

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.RewardedVideoCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.utils.Log.LogLevel
import com.appodealstack.demo.rewarded.databinding.ActivityRewardedBinding

class RewardedActivity : AppCompatActivity() {

    private var _binding: ActivityRewardedBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRewardedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSDK()
    }

    private fun setUpAppodealSDK() {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.REWARDED_VIDEO,
            object : ApdInitializationCallback {
                override fun onInitializationFinished(errors: List<ApdInitializationError>?) {
                    if (errors.isNullOrEmpty()) {
                        showToast("Appodeal initialized successfully")
                    } else {
                        showToast("Appodeal initialized with ${errors.size} errors")
                        errors.forEach { Log.e(TAG, "onInitializationFinished: ", it) }
                    }
                }
            })

        binding.cacheRewarded.setOnClickListener {
            Appodeal.cache(this, Appodeal.REWARDED_VIDEO)
        }

        binding.autocacheRewarded.setOnCheckedChangeListener { _, isChecked ->
            binding.cacheRewarded.isEnabled = !isChecked
            Appodeal.setAutoCache(Appodeal.REWARDED_VIDEO, isChecked)
        }

        binding.showRewarded.setOnClickListener {
            if (Appodeal.canShow(Appodeal.REWARDED_VIDEO, placementName)) {
                Appodeal.show(this, Appodeal.REWARDED_VIDEO, placementName)
            } else {
                showToast("Cannot show rewarded video")
            }
        }

        Appodeal.setRewardedVideoCallbacks(object : RewardedVideoCallbacks {

            override fun onRewardedVideoLoaded(isPrecache: Boolean) {
                showToast("Rewarded video was loaded, isPrecache: $isPrecache")
            }

            override fun onRewardedVideoFailedToLoad() {
                showToast("Rewarded video failed to load")
            }

            override fun onRewardedVideoClicked() {
                showToast("Rewarded video was clicked")
            }

            override fun onRewardedVideoShowFailed() {
                showToast("Rewarded video failed to show")
            }

            override fun onRewardedVideoShown() {
                showToast("Rewarded video was shown")
            }

            override fun onRewardedVideoClosed(finished: Boolean) {
                showToast("Rewarded video was closed, isVideoFinished: $finished")
            }

            override fun onRewardedVideoFinished(amount: Double, name: String?) {
                showToast("Rewarded video was finished, amount: $amount, currency: $name")
            }

            override fun onRewardedVideoExpired() {
                showToast("Rewarded video was expired")
            }
        })
    }
}

private const val placementName = "default"
private val TAG = RewardedActivity::class.java.simpleName
private fun Context.showToast(message: String) =
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()