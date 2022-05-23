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

class BillingUseCase(context: Context) {

    private val _purchases: MutableLiveData<List<Pair<ProductDetails?, Purchase>>> = MutableLiveData()
    val purchases: LiveData<List<Pair<ProductDetails?, Purchase>>> get() = _purchases
    private val knownInAppProducts: List<String> = listOf("coins")
    private val knownSubscriptionProducts: List<String> = listOf("infinite_access_monthly")

    private val productDetailsMap: MutableMap<String, ProductDetails> = HashMap()
    private val purchaseConsumptionInProcess: MutableSet<Purchase> = HashSet()

    private val onProductDetailsResponse =
        ProductDetailsResponseListener { billingResult, productDetailsList ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            debug("onProductDetailsResponse: $responseCode $debugMessage")
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                if (productDetailsList.isEmpty()) {
                    error(
                        "onProductDetailsResponse: " +
                                "Found null or empty SkuDetails. " +
                                "Check to see if the SKUs you requested are correctly published " +
                                "in the Google Play Console."
                    )
                } else {
                    for (productDetail: ProductDetails in productDetailsList) {
                        productDetailsMap[productDetail.name] = productDetail
                    }
                }
            }
        }

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
        val detailsPurchaseList: MutableList<Pair<ProductDetails?, Purchase>> = mutableListOf()
        purchases?.forEach {
            detailsPurchaseList.add(
                Pair(getProductDetails(firstOrNull<String>(it.products)), it)
            )
        }
        _purchases.postValue(detailsPurchaseList)
    }

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            val responseCode = billingResult.responseCode
            debug("onBillingSetupFinished: $responseCode ${billingResult.debugMessage}")
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                // The billing client is ready. You can query purchases here.
                // This doesn't mean that your app is set up correctly in the console -- it just
                // means that you have a connection to the Billing service.
                queryProductDetailsAsync()
                refreshPurchasesAsync()
            }
        }

        override fun onBillingServiceDisconnected() {
            debug("onBillingServiceDisconnected")
        }
    }

    private val billingClient =
        BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
            .apply { startConnection(billingClientStateListener) }

    fun flow(activity: Activity, product: String) {
        val productDetails: ProductDetails = productDetailsMap[product] ?: return
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            debug("Flow billing success")
        } else {
            error("Flow billing failed: ${billingResult.debugMessage}")
        }
    }

    private fun queryProductDetailsAsync() {
        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .setProductId(SKU_COINS)
                        .build()
                )
            ).build(),
            onProductDetailsResponse
        )
        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .setProductId(SKU_INFINITE_ACCESS_MONTHLY)
                        .build()
                )
            ).build(),
            onProductDetailsResponse
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
            QueryPurchasesParams
                .newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
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
                    if (knownInAppProducts.contains(product)) {
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

    private fun getProductDetails(product: String?): ProductDetails? {
        return if (!product.isNullOrEmpty()) {
            productDetailsMap[product]
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