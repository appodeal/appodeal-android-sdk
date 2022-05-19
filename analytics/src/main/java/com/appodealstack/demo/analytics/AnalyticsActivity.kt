package com.appodealstack.demo.analytics

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.appodeal.ads.Appodeal
import com.appodeal.ads.inapp.InAppPurchase
import com.appodeal.ads.inapp.InAppPurchaseValidateCallback
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.service.ServiceError
import com.appodeal.ads.utils.Log.*
import com.appodealstack.demo.analytics.databinding.ActivityAnalyticsBinding

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding
    private lateinit var billingClient: ExampleBillingClient

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchaseList ->
            Log.v(TAG, "onPurchasesUpdated")
            val responseCode: Int = billingResult.responseCode
            if (responseCode == BillingClient.BillingResponseCode.OK && purchaseList != null) {
                for (purchase in purchaseList) {
                    validatePurchase(purchase)
                }
            } else {
                Log.d(TAG, "Error - Null Purchase List Returned from OK response!")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        billingClient = ExampleBillingClient(
            applicationContext,
            SKU_COINS,
            SKU_INFINITE_ACCESS_MONTHLY,
            purchasesUpdatedListener
        )
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
                    if (errors.isNullOrEmpty()) {
                        showToast("Appodeal initialized")
                    } else {
                        for (error in errors) {
                            Log.e(TAG, error.message!!)
                        }
                    }
                }
            })

        binding.validateInapp.setOnClickListener {
            flowInAppPurchase()
        }
        binding.validateSubscription.setOnClickListener {
            flowSubsPurchase()
        }
        binding.logEvent.setOnClickListener {
            logEvent()
        }
    }


    fun flowInAppPurchase() {
        billingClient.flow(this, SKU_COINS)
    }

    fun flowSubsPurchase() {
        billingClient.flow(this, SKU_INFINITE_ACCESS_MONTHLY)
    }

    private fun logEvent() {
        val params: MutableMap<String, Any> = HashMap()
        params["example_param_1"] = "Param1 value"
        params["example_param_2"] = 123
        Appodeal.logEvent("appodealstack_sdk_example_test_event", params)
    }

    private fun validatePurchase(purchase: Purchase) {
        val product: String = purchase.products.first()
        val productDetails: ProductDetails? = billingClient.getProductDetails(product)
        if (productDetails == null) {
            Log.d("Appodeal App", "Product Details is null")
            return
        }
        val price = "1"
        val currency = "USD"
        val additionalEventValues: MutableMap<String, String> = java.util.HashMap()
        additionalEventValues["some_parameter"] = "some_value"


        val inAppPurchase: InAppPurchase = InAppPurchase.newBuilder(InAppPurchase.Type.InApp)
            .withPublicKey(publicKey)
            .withSignature(purchase.signature)
            .withPurchaseData(purchase.originalJson)
            .withPurchaseToken(purchase.purchaseToken)
            .withPurchaseTimestamp(purchase.purchaseTime)
            .withDeveloperPayload(purchase.developerPayload)
            .withOrderId(purchase.orderId)
            .withSku(product)
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val publicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm4QYg6oP6hxBPXTLIBIpgzUoAYd0UotmsuEQFogH4Pm5qSibwd2E2UlguDdIx4OTjBif4JfFMNhhRrENL6rPHcRudQWSGd0I54RGVYtex8B4bVkWEpBs0W5BJs6hTmgHbS2bBCyMeJRNaUwyfTbcwHQniDZ6n7eky3WPVaIA1kXit3vZFcpDCkeQKoAOf8iApFLFRuHSGtmGe56v5rZKsUuMhjwVU1NuH0lleuIWjRM42HRXlgFrCM0X7wwQr"
        private val TAG = AnalyticsActivity::class.java.simpleName
        private const val SKU_INFINITE_ACCESS_MONTHLY = "infinite_access_monthly"
        private const val SKU_COINS = "coins"
    }
}