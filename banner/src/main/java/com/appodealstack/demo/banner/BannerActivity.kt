package com.appodealstack.demo.banner

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.BannerCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.utils.Log.LogLevel
import com.appodealstack.demo.banner.databinding.ActivityBannerBinding

class BannerActivity : AppCompatActivity() {

    private var _binding: ActivityBannerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSDK()
    }

    private fun setUpAppodealSDK() {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.BANNER,
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

        binding.showBanner.setOnClickListener {
            if (Appodeal.canShow(Appodeal.BANNER, placementName)) {
                Appodeal.show(this, Appodeal.BANNER_BOTTOM, placementName)
            } else {
                showToast("Cannot show Banner")
            }
        }
        binding.hideBanner.setOnClickListener {
            Appodeal.hide(this, Appodeal.BANNER)
        }

        Appodeal.setBannerCallbacks(object : BannerCallbacks {

            override fun onBannerLoaded(height: Int, isPrecache: Boolean) {
                showToast("Banner was loaded, isPrecache: $isPrecache")
            }

            override fun onBannerFailedToLoad() {
                showToast("Banner failed to load")
            }

            override fun onBannerClicked() {
                showToast("Banner was clicked")
            }

            override fun onBannerShowFailed() {
                showToast("Banner failed to show")
            }

            override fun onBannerShown() {
                showToast("Banner was shown")
            }

            override fun onBannerExpired() {
                showToast("Banner was expired")
            }
        })
    }
}

private const val placementName = "default"
private val TAG = BannerActivity::class.java.simpleName
private fun Context.showToast(message: String) =
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()