package com.appodeal.test.banner

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.appodeal.ads.Appodeal
import com.appodeal.ads.BannerCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.test.banner.databinding.ActivityBannerBinding

class BannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSDK()
    }

    private fun setUpAppodealSDK() {
        Appodeal.setTesting(true)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.BANNER,
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val placementName = "default"
        private val TAG = BannerActivity::class.java.simpleName
    }
}