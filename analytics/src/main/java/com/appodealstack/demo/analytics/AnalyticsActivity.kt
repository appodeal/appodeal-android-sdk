package com.appodealstack.demo.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.appodeal.ads.Appodeal
import com.appodeal.ads.inapp.InAppPurchase
import com.appodeal.ads.inapp.InAppPurchaseValidateCallback
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.service.ServiceError
import com.appodeal.ads.utils.Log.LogLevel
import com.appodealstack.demo.analytics.databinding.ActivityAnalyticsBinding

class AnalyticsActivity : AppCompatActivity() {

    private var _binding: ActivityAnalyticsBinding? = null
    private val binding get() = _binding!!
    private var _viewModel: AnalyticsViewModel? = null
    private val viewModel get() = _viewModel!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _viewModel = ViewModelFactory(application).create(AnalyticsViewModel::class.java)
        setUpAppodealSdk()
    }

    private fun setUpAppodealSdk() {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.REWARDED_VIDEO,
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
        binding.validateInapp.setOnClickListener { viewModel.flowInAppPurchase(this) }
        binding.validateSubscription.setOnClickListener { viewModel.flowSubsPurchase(this) }
        binding.logEvent.setOnClickListener { logEvent() }
        viewModel.purchases.observe(this) { purchases ->
            purchases.forEach {
                validatePurchase(it)
            }
        }
    }

    private fun logEvent() {
        val params: MutableMap<String, Any> = HashMap()
        params["example_param_1"] = "Param1 value"
        params["example_param_2"] = 123
        Appodeal.logEvent("appodealstack_sdk_example_test_event", params)
    }

    private fun validatePurchase(purchasePair: Pair<SkuDetails?, Purchase>) {
        val skuDetails = purchasePair.first
        val purchase = purchasePair.second
        if (skuDetails == null) {
            Log.d("Appodeal App", "Product Details is null")
            return
        }
        val price = skuDetails.price
        val currency = skuDetails.priceCurrencyCode

        val additionalEventValues: MutableMap<String, String> = java.util.HashMap()
        additionalEventValues["some_parameter"] = "some_value"

        val inAppPurchase: InAppPurchase = InAppPurchase.newBuilder(InAppPurchase.Type.InApp)
            .withPublicKey(PUBLIC_KEY)
            .withSignature(purchase.signature)
            .withPurchaseData(purchase.originalJson)
            .withPurchaseToken(purchase.purchaseToken)
            .withPurchaseTimestamp(purchase.purchaseTime)
            .withDeveloperPayload(purchase.developerPayload)
            .withOrderId(purchase.orderId)
            .withSku(skuDetails.sku)
            .withPrice(price)
            .withCurrency(currency)
            .withAdditionalParams(additionalEventValues)
            .build()

        // Validate InApp purchase
        Appodeal.validateInAppPurchase(this, inAppPurchase, object : InAppPurchaseValidateCallback {
            override fun onInAppPurchaseValidateSuccess(
                purchase: InAppPurchase,
                errors: List<ServiceError>?
            ) {
                Log.v(TAG, "onInAppPurchaseValidateSuccess")
                if (errors != null) {
                    for (error in errors) {
                        Log.e(TAG, "onInAppPurchaseValidateSuccess - $error")
                    }
                }
            }

            override fun onInAppPurchaseValidateFail(
                purchase: InAppPurchase,
                errors: List<ServiceError>
            ) {
                Log.v(TAG, "onInAppPurchaseValidateFail")
                for (error in errors) {
                    Log.e(TAG, "onInAppPurchaseValidateFail - $error")
                }
            }
        })
    }
}

private fun Context.showToast(message: String) =
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

private const val PUBLIC_KEY =
    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm4QYg6oP6hxBPXTLIBIpgzUoAYd0UotmsuEQFogH4Pm5qSibwd2E2UlguDdIx4OTjBif4JfFMNhhRrENL6rPHcRudQWSGd0I54RGVYtex8B4bVkWEpBs0W5BJs6hTmgHbS2bBCyMeJRNaUwyfTbcwHQniDZ6n7eky3WPVaIA1kXit3vZFcpDCkeQKoAOf8iApFLFRuHSGtmGe56v5rZKsUuMhjwVU1NuH0lleuIWjRM42HRXlgFrCM0X7wwQr"
private val TAG = AnalyticsActivity::class.java.simpleName