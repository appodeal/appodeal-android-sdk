package com.appodealstack.demo.mrec

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.MrecCallbacks
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.utils.Log.*
import com.appodealstack.demo.mrec.databinding.ActivityMrecBinding

class MrecActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMrecBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSDK(binding)
    }

    private fun setUpAppodealSDK(binding: ActivityMrecBinding) {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.setMrecViewId(R.id.appodealMrecView)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.MREC
        ) { errors: List<ApdInitializationError>? ->
            val initResult =
                if (errors.isNullOrEmpty()) "successfully" else "with ${errors.size} errors"
            showToast("Appodeal initialized $initResult")
            errors?.forEach {
                Log.e(TAG, "onInitializationFinished: ", it)
            }
        }

        with(binding) {
            showMrec.setOnClickListener {
                if (Appodeal.canShow(Appodeal.MREC, placementName)) {
                    Appodeal.show(this@MrecActivity, Appodeal.MREC, placementName)
                } else {
                    showToast("Cannot show MREC")
                }
            }
            hideMrec.setOnClickListener {
                Appodeal.hide(this@MrecActivity, Appodeal.MREC)
            }
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
}

private const val placementName = "default"
private const val TAG = "MrecActivity"
private fun Context.showToast(message: String) =
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()