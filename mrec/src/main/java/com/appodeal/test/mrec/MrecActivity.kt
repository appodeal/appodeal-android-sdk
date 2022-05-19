package com.appodeal.test.mrec

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.appodeal.ads.Appodeal
import com.appodeal.ads.MrecCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.test.mrec.databinding.ActivityMrecBinding

class MrecActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMrecBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMrecBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSDK()
    }

    private fun setUpAppodealSDK() {
        Appodeal.setTesting(true)
        Appodeal.setMrecViewId(R.id.appodealMrecView)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.MREC,
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

        binding.showMrec.setOnClickListener {
            if (Appodeal.canShow(Appodeal.MREC, placementName)) {
                Appodeal.show(this, Appodeal.MREC, placementName)
            } else {
                showToast("Cannot show MREC")
            }
        }
        binding.hideMrec.setOnClickListener {
            Appodeal.hide(this, Appodeal.MREC)
        }

        Appodeal.setMrecCallbacks(object : MrecCallbacks {
            override fun onMrecLoaded(isPrecache: Boolean) {
                showToast("MREC was loaded, isPrecache: $isPrecache")
            }

            override fun onMrecFailedToLoad() {
                showToast("MREC failed to load")
            }

            override fun onMrecClicked() {
                showToast("MREC was clicked")
            }

            override fun onMrecShowFailed() {
                showToast("MREC failed to show")
            }

            override fun onMrecShown() {
                showToast("MREC was shown")
            }

            override fun onMrecExpired() {
                showToast("MREC was expired")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val placementName = "default"
        private val TAG = MrecActivity::class.java.simpleName
    }
}