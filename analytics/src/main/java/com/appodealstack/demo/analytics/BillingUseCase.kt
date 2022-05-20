package com.appodealstack.demo.analytics

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillingUseCase(context: Context) {

    private val _purchases: MutableLiveData<List<Pair<SkuDetails?, Purchase>>> = MutableLiveData()
    val purchases: LiveData<List<Pair<SkuDetails?, Purchase>>> get() = _purchases
    private val knownInappProducts: List<String> = listOf("coins")
    private val knownSubscriptionProducts: List<String> = listOf("infinite_access_monthly")

    fun flow(activity: Activity, product: String) {
        val productDetails: SkuDetails = skuDetailsMap[product] ?: return
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(productDetails)
            .build()
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            debug("Flow billing success")
        } else {
            error("Flow billing failed: ${billingResult.debugMessage}")
        }
    }

    private val skuDetailsMap: MutableMap<String, SkuDetails> = mutableMapOf()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        debug("onPurchasesUpdated: ${billingResult.responseCode} ${billingResult.debugMessage}")
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.let {
                    processPurchaseList(it)
                } ?: error("onPurchasesUpdated: Null Purchase List Returned from OK response!")
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> debug("onPurchasesUpdated: User canceled the purchase")
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> debug("onPurchasesUpdated: The user already owns this item")
            BillingClient.BillingResponseCode.DEVELOPER_ERROR ->
                error(
                    "onPurchasesUpdated: Developer error means that Google Play " +
                            "does not recognize the configuration. If you are just getting started, " +
                            "make sure you have configured the application correctly in the " +
                            "Google Play Console. The SKU product ID must match and the APK you " +
                            "are using must be signed with release keys."
                )
        }
        val skuPurchaseList: MutableList<Pair<SkuDetails?, Purchase>> = mutableListOf()
        purchases?.forEach {
            skuPurchaseList.add(
                Pair(getSkuDetails(firstOrNull<String>(it.skus)), it)
            )
        }
        _purchases.postValue(skuPurchaseList)
    }

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            val responseCode = billingResult.responseCode
            debug("onBillingSetupFinished: $responseCode ${billingResult.debugMessage}")
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                // The billing client is ready. You can query purchases here.
                // This doesn't mean that your app is set up correctly in the console -- it just
                // means that you have a connection to the Billing service.
                querySkuDetailsAsync()
                refreshPurchasesAsync()
            }
        }

        override fun onBillingServiceDisconnected() {
            debug("onBillingServiceDisconnected")
        }
    }

    private val onSkuDetailsResponse =
        SkuDetailsResponseListener { billingResult, skuDetailsList ->
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
                    skuDetailsList.forEach { skuDetail ->
                        skuDetailsMap[skuDetail.sku] = skuDetail
                    }
                }
            }
        }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()
        .apply { startConnection(billingClientStateListener) }

    private fun querySkuDetailsAsync() {
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
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                error("Problem getting purchases: ${billingResult.debugMessage}")
            } else {
                processPurchaseList(purchaseList)
            }
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build(),
            purchasesResponseListener
        )
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams
                .newBuilder()
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
                            Log.e(
                                TAG,
                                "Purchase cannot contain a mixture of consumable and non-consumable items: ${purchase.products}"
                            )
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
        val consumeParams =
            ConsumeParams.newBuilder()
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

    private fun getSkuDetails(sku: String?): SkuDetails? {
        return if (!TextUtils.isEmpty(sku)) {
            skuDetailsMap[sku]
        } else null
    }
}

private fun <T> firstOrNull(list: List<T>?): T? {
    return if (list != null && list.isNotEmpty()) {
        list[0]
    } else null
}

private fun debug(message: String) = Log.d(TAG, message)
private fun error(message: String) = Log.e(TAG, message)

private const val TAG = "BillingClient"