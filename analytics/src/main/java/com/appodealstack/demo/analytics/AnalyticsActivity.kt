package com.appodealstack.demo.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.appodeal.ads.Appodeal
import com.appodeal.ads.inapp.InAppPurchase
import com.appodeal.ads.inapp.InAppPurchaseValidateCallback
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.service.ServiceError
import com.appodeal.ads.utils.Log.LogLevel
import com.appodealstack.demo.analytics.databinding.ActivityAnalyticsBinding

class AnalyticsActivity : AppCompatActivity() {

    private val viewModel by viewModels<AnalyticsViewModel>()
    private var _binding: ActivityAnalyticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSdk()
    }

    private fun setUpAppodealSdk() {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.NONE,
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
        binding.validateInapp.setOnClickListener { viewModel.flowInAppPurchase(this) }
        binding.validateSubscription.setOnClickListener { viewModel.flowSubsPurchase(this) }
        binding.logEvent.setOnClickListener { logEvent() }
        viewModel.purchases.observe(this) { purchases ->
            purchases.forEach { validatePurchase(it) }
        }
    }

    private fun logEvent() {
        val params = mapOf(
            "example_param_1" to "Param1 value",
            "example_param_2" to 123
        )
        Appodeal.logEvent("appodealstack_sdk_example_test_event", params)
    }

    private fun validatePurchase(purchase: Purchase) = purchase.products.forEach { productId ->
        val productDetails = viewModel.getProductDetails(productId) ?: error("Product details is null")
        val apdPurchaseBuilder = when (productDetails.productType) {
            BillingClient.ProductType.INAPP -> {
                InAppPurchase.newInAppBuilder().apply {
                    productDetails.oneTimePurchaseOfferDetails?.let {
                        withPrice(it.formattedPrice)
                        withCurrency(it.priceCurrencyCode)
                    }
                }
            }
            BillingClient.ProductType.SUBS -> {
                InAppPurchase.newSubscriptionBuilder().apply {
                    productDetails.subscriptionOfferDetails?.let {
                        val pricingPhase = it.first().pricingPhases.pricingPhaseList.first()
                        withPrice(pricingPhase.formattedPrice)
                        withCurrency(pricingPhase.priceCurrencyCode)
                    }
                }
            }
            else -> error("Product type is incorrect")
        }
        val apdPurchase: InAppPurchase = apdPurchaseBuilder
            .withPublicKey(PUBLIC_KEY)
            .withSignature(purchase.signature)
            .withPurchaseData(purchase.originalJson)
            .withPurchaseToken(purchase.purchaseToken)
            .withPurchaseTimestamp(purchase.purchaseTime)
            .withDeveloperPayload(purchase.developerPayload)
            .withOrderId(purchase.orderId)
            .withSku(productId)
            .withAdditionalParams(mapOf("some_parameter" to "some_value"))
            .build()

        // Validate InApp purchase
        Appodeal.validateInAppPurchase(this, apdPurchase, object : InAppPurchaseValidateCallback {
            override fun onInAppPurchaseValidateSuccess(
                purchase: InAppPurchase,
                errors: List<ServiceError>?
            ) {
                Log.v(TAG, "onInAppPurchaseValidateSuccess")
                errors?.forEach { error ->
                    Log.e(TAG, "onInAppPurchaseValidateSuccess - $error")
                }
            }

            override fun onInAppPurchaseValidateFail(
                purchase: InAppPurchase,
                errors: List<ServiceError>
            ) {
                Log.v(TAG, "onInAppPurchaseValidateFail")
                errors.forEach { error ->
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