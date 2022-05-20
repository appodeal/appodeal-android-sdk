package com.appodealstack.demo.analytics

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.android.billingclient.api.SkuDetails as PurchaseDetails

class BillingUseCase(
    context: Context,
    private val knownInappProducts: List<String>,
    private val knownSubscriptionProducts: List<String>,
    private val purchasesDetails: MutableSet<PurchaseDetails> = mutableSetOf<PurchaseDetails>()
) {
    private val _purchases = MutableLiveData<List<AnalyticsPurchase>>()
    val purchases: LiveData<List<AnalyticsPurchase>> get() = _purchases

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        debug("onPurchasesUpdated: ${billingResult.responseCode} ${billingResult.debugMessage}")
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> purchases?.let {
                processPurchaseList(purchases)
                val skuPurchaseList = mutableListOf<AnalyticsPurchase>()
                purchases.forEach { purchase ->
                    purchase.skus.firstOrNull()?.let { product ->
                        purchasesDetails.find { it.sku == product }?.let { purchaseDetails ->
                            skuPurchaseList.add(AnalyticsPurchase(purchase, purchaseDetails))
                        }
                    }
                }
                _purchases.postValue(skuPurchaseList)
            } ?: error("onPurchasesUpdated: Null Purchase List Returned from OK response!")
            BillingClient.BillingResponseCode.USER_CANCELED -> debug("onPurchasesUpdated: User canceled the purchase")
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> debug("onPurchasesUpdated: The user already owns this item")
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> error(
                "onPurchasesUpdated: Developer error means that Google Play " +
                        "does not recognize the configuration. If you are just getting started, " +
                        "make sure you have configured the application correctly in the " +
                        "Google Play Console. The SKU product ID must match and the APK you " +
                        "are using must be signed with release keys."
            )
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()
        .apply { startConnection(billingClientStateListener) }

    fun flow(activity: Activity, product: String) {
        val productDetails = purchasesDetails.find { it.sku == product } ?: return
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(productDetails)
            .build()
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            debug("Flow billing success")
        } else {
            error("Flow billing failed: ${billingResult.responseCode} ${billingResult.debugMessage}")
        }
    }

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            debug("onBillingSetupFinished: ${billingResult.responseCode} ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // The billing client is ready. You can query purchases here.
                // This doesn't mean that your app is set up correctly in the console -- it just
                // means that you have a connection to the Billing service.
                querySkuDetailsAsync()
                refreshPurchasesAsync()
            } else {
                error("onBillingSetupFinished error: ${billingResult.responseCode} ${billingResult.debugMessage}")
            }
        }

        override fun onBillingServiceDisconnected() {
            debug("onBillingServiceDisconnected")
        }
    }

    private fun querySkuDetailsAsync() {
        val onSkuDetailsResponse = SkuDetailsResponseListener { billingResult, skuDetailsList ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            debug("onSkuDetailsResponse: $responseCode $debugMessage")
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                if (skuDetailsList.isNullOrEmpty()) {
                    error(
                        "onSkuDetailsResponse: " +
                                "Found null or empty SkuDetails. " +
                                "Check to see if the SKUs you requested are correctly published " +
                                "in the Google Play Console."
                    )
                } else {
                    purchasesDetails.addAll(skuDetailsList)
                }
            }
        }
        billingClient.querySkuDetailsAsync(
            SkuDetailsParams.newBuilder()
                .setType(BillingClient.ProductType.INAPP)
                .setSkusList(knownInappProducts)
                .build(), onSkuDetailsResponse
        )
        billingClient.querySkuDetailsAsync(
            SkuDetailsParams.newBuilder()
                .setType(BillingClient.ProductType.SUBS)
                .setSkusList(knownSubscriptionProducts)
                .build(), onSkuDetailsResponse
        )
    }

    private fun refreshPurchasesAsync() {
        val purchasesResponseListener = PurchasesResponseListener { billingResult, purchaseList ->
            debug("onQueryPurchasesResponse: ${billingResult.responseCode} ${billingResult.responseCode}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchaseList(purchaseList)
            } else {
                error("onQueryPurchasesResponse error: ${billingResult.responseCode} ${billingResult.responseCode}")
            }
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            purchasesResponseListener
        )
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            purchasesResponseListener
        )
        debug("Refreshing purchases started.")
    }

    private fun processPurchaseList(purchases: List<Purchase>?) {
        purchases
            ?.filter { purchase -> purchase.purchaseState == Purchase.PurchaseState.PURCHASED }
            ?.forEach { purchase ->
                var isConsumable = false
                // TODO: 19/05/2022 [glavatskikh] simple
                for (product: String in purchase.products) {
                    if (knownInappProducts.contains(product)) {
                        isConsumable = true
                    } else {
                        if (isConsumable) {
                            error("Purchase cannot contain a mixture of consumable and non-consumable items: ${purchase.products}")
                            isConsumable = false
                            break
                        }
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    if (isConsumable) {
                        consumePurchase(purchase)
                    } else if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }
                }
            } ?: debug("Empty purchase list.")
    }

    private suspend fun consumePurchase(purchase: Purchase) {
        debug("Start consumption flow.")
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val billingResult = withContext(Dispatchers.IO) {
            billingClient.consumePurchase(consumeParams).billingResult
        }
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            debug("Consumption successful. Delivering entitlement.")
        } else {
            error("Error while consuming: ${billingResult.debugMessage}")
        }
        debug("End consumption flow.")
    }

    private suspend fun acknowledgePurchase(purchase: Purchase) {
        debug("Start acknowledge flow.")
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
        val ackPurchaseResult = withContext(Dispatchers.IO) {
            billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
        }
        if (ackPurchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
            debug("Acknowledge successful.")
        } else {
            error("Error while acknowledge: ${ackPurchaseResult.debugMessage}")
        }
        debug("End acknowledge flow.")
    }
}