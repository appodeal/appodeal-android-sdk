package com.appodealstack.demo.rewarded

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.RewardedVideoCallbacks
import com.appodeal.ads.utils.Log.*
import com.appodealstack.demo.rewarded.databinding.ActivityRewardedBinding

class RewardedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRewardedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSDK(binding)
    }

    private fun setUpAppodealSDK(binding: ActivityRewardedBinding) {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.REWARDED_VIDEO
        ) { errors ->
            val initResult =
                if (errors.isNullOrEmpty()) "successfully" else "with ${errors.size} errors"
            showToast("Appodeal initialized $initResult")
            errors?.forEach {
                Log.e(TAG, "onInitializationFinished: ", it)
            }
        }

        with(binding) {
            showRewarded.setOnClickListener {
                if (Appodeal.canShow(Appodeal.REWARDED_VIDEO, placementName)) {
                    Appodeal.show(this@RewardedActivity, Appodeal.REWARDED_VIDEO, placementName)
                } else {
                    showToast("Cannot show rewarded video")
                }
            }

            cacheRewarded.setOnClickListener {
                Appodeal.cache(this@RewardedActivity, Appodeal.REWARDED_VIDEO)
            }

            autocacheRewarded.setOnCheckedChangeListener { _, isChecked ->
                cacheRewarded.isEnabled = !isChecked
                Appodeal.setAutoCache(Appodeal.REWARDED_VIDEO, isChecked)
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

            override fun onRewardedVideoFinished(amount: Double, currency: String) {
                showToast("Rewarded video was finished, amount: $amount, currency: $currency")
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