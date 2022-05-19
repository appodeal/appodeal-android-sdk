package com.appodealstack.demo.analytics

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.billingclient.api.*
import java.util.concurrent.atomic.AtomicBoolean

internal class ExampleBillingClient(
    context: Context,
    knownInappSKU: String,
    knownSubscriptionSKU: String,
    externalPurchasesUpdatedListener: PurchasesUpdatedListener
) :
    BillingClientStateListener,  ProductDetailsResponseListener, PurchasesUpdatedListener {
    private val billingClient: BillingClient
    private val knownInappSKU: List<String>
    private val knownSubscriptionSKU: List<String>
    private val externalPurchasesUpdatedListener: PurchasesUpdatedListener
    private val billingSetupComplete = AtomicBoolean(false)
    private val billingFlowInProcess = AtomicBoolean(false)
    private val productDetailsMap: MutableMap<String?, ProductDetails> = HashMap()
    private val purchaseConsumptionInProcess: MutableSet<Purchase> = HashSet()

    // how long before the data source tries to reconnect to Google play
    private var reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready. You can query purchases here.
            // This doesn't mean that your app is set up correctly in the console -- it just
            // means that you have a connection to the Billing service.
            reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS
            billingSetupComplete.set(true)
            querySkuDetailsAsync()
            refreshPurchasesAsync()
        } else {
            retryBillingServiceConnectionWithExponentialBackoff()
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.d(TAG, "onBillingServiceDisconnected")
        billingSetupComplete.set(false)
        retryBillingServiceConnectionWithExponentialBackoff()
    }

    private fun querySkuDetailsAsync() {
        val queryProductDetailsParamsInApp = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_id_example")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            ).build()
        billingClient.queryProductDetailsAsync(queryProductDetailsParamsInApp, this)

        val queryProductDetailsParamsSubs = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_id_example")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            ).build()
        billingClient.queryProductDetailsAsync(queryProductDetailsParamsSubs, this)
    }

    override fun onProductDetailsResponse(billingResult: BillingResult, productDetailsList: MutableList<ProductDetails>) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
                if (productDetailsList.isEmpty()) {
                    Log.e(TAG, "onSkuDetailsResponse: " +
                                "Found null or empty SkuDetails. " +
                                "Check to see if the SKUs you requested are correctly published " +
                                "in the Google Play Console.")
                } else {
                    for (productDetail: ProductDetails in productDetailsList) {
                        productDetailsMap[productDetail.name] = productDetail
                    }
                }
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
            BillingClient.BillingResponseCode.ERROR ->
                Log.e(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
            BillingClient.BillingResponseCode.USER_CANCELED ->
                Log.d(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
            }
            else -> Log.e(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
        }
    }


    private fun refreshPurchasesAsync() {
        if (!billingClient.isReady) {
            Log.e(TAG, "queryPurchases: BillingClient is not ready")
        }
        // Query for existing subscription products that have been purchased.
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams
                .newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Problem getting purchases: ${billingResult.debugMessage}")
            } else {
                processPurchaseList(purchaseList)
            }
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams
                .newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Problem getting subscriptions: ${billingResult.debugMessage}")
            } else {
                processPurchaseList(purchaseList)
            }
        }
        Log.d(TAG, "Refreshing purchases started.")
    }

    private fun retryBillingServiceConnectionWithExponentialBackoff() {
        handler.postDelayed(
            { billingClient.startConnection(this@ExampleBillingClient) },
            reconnectMilliseconds
        )
        reconnectMilliseconds = Math.min(
            reconnectMilliseconds * 2,
            RECONNECT_TIMER_MAX_TIME_MILLISECONDS
        )
    }

    fun flow(activity: Activity, productName: String) {
        val productDetails = productDetailsMap[productName]
        if (productDetails != null) {
            val detailLIst: BillingFlowParams.ProductDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                    //TODO OfferToken
                .setOfferToken(productDetails.oneTimePurchaseOfferDetails?.priceAmountMicros.toString())
                .build()
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(detailLIst))
                .build()
            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                billingFlowInProcess.set(true)
            } else {
                Log.e(TAG, "Billing failed: + " + billingResult.debugMessage)
            }
        } else {
            Log.e(TAG, "ProductDetails not found for: $productName")
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        list: List<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> list?.let { processPurchaseList(it) }
                ?: Log.d(TAG, "Null Purchase List Returned from OK response!")
            BillingClient.BillingResponseCode.USER_CANCELED ->
                Log.d(TAG, "onPurchasesUpdated: User canceled the purchase")
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED ->
                Log.d(TAG, "onPurchasesUpdated: The user already owns this item")
            BillingClient.BillingResponseCode.DEVELOPER_ERROR ->
                Log.e(
                    TAG, "onPurchasesUpdated: Developer error means that Google Play " +
                            "does not recognize the configuration. If you are just getting started, " +
                            "make sure you have configured the application correctly in the " +
                            "Google Play Console. The SKU product ID must match and the APK you " +
                            "are using must be signed with release keys."
                )
            else ->
                Log.d(
                    TAG,
                    "BillingResult ${billingResult.responseCode} ${billingResult.debugMessage}"
                )
        }
        billingFlowInProcess.set(false)
        externalPurchasesUpdatedListener.onPurchasesUpdated(billingResult, list)
    }

    private fun processPurchaseList(purchases: List<Purchase>?) {
        if (purchases != null) {
            for (purchase: Purchase in purchases) {
                val purchaseState = purchase.purchaseState
                if (purchaseState == Purchase.PurchaseState.PURCHASED) {
                    var isConsumable = false
                    for (sku: String in purchase.skus) {
                        if (knownInappSKU.contains(sku)) {
                            isConsumable = true
                        } else {
                            if (isConsumable) {
                                Log.e(
                                    TAG,
                                    "Purchase cannot contain a mixture of consumable and non-consumable items: ${purchase.skus}"
                                )
                                isConsumable = false
                                break
                            }
                        }
                    }
                    if (isConsumable) {
                        consumePurchase(purchase)
                    } else if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }
                }
            }
        } else {
            Log.d(TAG, "Empty purchase list.")
        }
    }

    private fun consumePurchase(purchase: Purchase) {
        if (purchaseConsumptionInProcess.contains(purchase)) {
            return
        }
        purchaseConsumptionInProcess.add(purchase)
        billingClient.consumeAsync(
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        ) { billingResult: BillingResult, _: String? ->
            purchaseConsumptionInProcess.remove(purchase)
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Consumption successful. Delivering entitlement.")
            } else {
                Log.e(TAG, "Error while consuming: ${billingResult.debugMessage}")
            }
            Log.d(TAG, "End consumption flow.")
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        billingClient.acknowledgePurchase(
            AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Acknowledge successful.")
            } else {
                Log.e(TAG, "Error while acknowledge: " + billingResult.debugMessage)
            }
            Log.d(TAG, "End acknowledge flow.")
        }
    }

    fun getProductDetails(product: String): ProductDetails? {
        return if (product.isNullOrEmpty()) {
            productDetailsMap[product]
        } else null
    }

    fun resume() {
        Log.d(TAG, "Billing Resume")
        if (billingSetupComplete.get() && !billingFlowInProcess.get()) {
            refreshPurchasesAsync()
        }
    }

    companion object {
        private const val TAG = "BillingClient"
        private val handler = Handler(Looper.getMainLooper())
        private const val RECONNECT_TIMER_START_MILLISECONDS = 1000L
        private const val RECONNECT_TIMER_MAX_TIME_MILLISECONDS = 1000L * 60L * 15L
    }

    init {
        this.knownInappSKU = listOf(knownInappSKU)
        this.knownSubscriptionSKU = listOf(knownSubscriptionSKU)
        this.externalPurchasesUpdatedListener = externalPurchasesUpdatedListener
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(this)
    }
}