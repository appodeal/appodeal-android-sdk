package com.appodeal.test.rewarded

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.appodeal.ads.Appodeal
import com.appodeal.ads.RewardedVideoCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.utils.Log.*
import com.appodeal.test.rewarded.databinding.ActivityRewardedBinding

class RewardedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardedBinding.inflate(layoutInflater)
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
                        showToast("Appodeal initialized")
                    } else {
                        for (error in errors) {
                            Log.e(TAG, error.message!!)
                        }
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val placementName = "default"
        private val TAG = RewardedActivity::class.java.simpleName
    }
}