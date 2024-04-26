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
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.revenue.AdRevenueCallbacks
import com.appodeal.ads.revenue.RevenueInfo
import com.appodeal.ads.service.ServiceError
import com.appodeal.ads.utils.Log.LogLevel
import com.appodealstack.demo.analytics.databinding.ActivityAnalyticsBinding

class AnalyticsActivity : AppCompatActivity() {

    private val viewModel by viewModels<AnalyticsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAppodealSdk(binding)
    }

    private fun setUpAppodealSdk(binding: ActivityAnalyticsBinding) {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.initialize(
            this,
            BuildConfig.APP_KEY,
            Appodeal.NONE
        ) { errors: List<ApdInitializationError>? ->
            val initResult =
                if (errors.isNullOrEmpty()) "successfully" else "with ${errors.size} errors"
            showToast("Appodeal initialized $initResult")
            errors?.forEach {
                Log.e(TAG, "onInitializationFinished: ", it)
            }
        }
        Appodeal.setAdRevenueCallbacks(object : AdRevenueCallbacks {
            override fun onAdRevenueReceive(revenueInfo: RevenueInfo) {
                // Called whenever SDK receives revenue information for an ad
            }
        })
        with(binding) {
            validateInapp.setOnClickListener { viewModel.flowInAppPurchase(this@AnalyticsActivity) }
            validateSubscription.setOnClickListener { viewModel.flowSubsPurchase(this@AnalyticsActivity) }
            logEvent.setOnClickListener { logEvent() }
        }
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
        val productDetails =
            viewModel.getProductDetails(productId) ?: error("Product details is null")
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

/** https://support.google.com/googleplay/android-developer/answer/186113 */
private const val PUBLIC_KEY = "YOUR_PUBLIC_KEY"
private const val TAG = "AnalyticsActivity"