package com.appodealstack.demo.analytics

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*

class BillingUseCase(
    context: Context,
    private val inAppProductId: String,
    private val subsProductId: String
) {
    private val _productDetails = mutableMapOf<String, ProductDetails>()
    private val _purchases = MutableLiveData<List<Purchase>>()

    val productDetails: Map<String, ProductDetails> get() = _productDetails
    val purchases: LiveData<List<Purchase>> get() = _purchases

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        debug("onPurchasesUpdated: ${billingResult.responseCode} ${billingResult.debugMessage}")
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.let {
                    processPurchaseList(it)
                    _purchases.postValue(it)
                } ?: error("onPurchasesUpdated: Null Purchase List Returned from OK response!")
            }
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

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            debug("onBillingSetupFinished: ${billingResult.responseCode} ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
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

    fun flowInApp(activity: Activity, productId: String) {
        val productDetails: ProductDetails = _productDetails[productId] ?: return
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()
        flow(activity, productDetailsParams)
    }

    fun flowSubscription(activity: Activity, productId: String) {
        val productDetails: ProductDetails = _productDetails[productId] ?: return
        val offerToken = productDetails.subscriptionOfferDetails?.last()?.offerToken ?: return
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()
        flow(activity, productDetailsParams)
    }

    private fun flow(
        activity: Activity,
        productDetailsParams: BillingFlowParams.ProductDetailsParams
    ) {
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
        val detailsResponseListener =
            ProductDetailsResponseListener { billingResult, productDetailsList ->
                debug("onProductDetailsResponse: ${billingResult.responseCode} ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (productDetailsList.isEmpty()) {
                        error(
                            "onProductDetailsResponse: " +
                            "Found null or empty SkuDetails. " +
                            "Check to see if the SKUs you requested are correctly published " +
                            "in the Google Play Console."
                        )
                    } else {
                        for (productDetails: ProductDetails in productDetailsList) {
                            _productDetails[productDetails.productId] = productDetails
                        }
                    }
                }
            }
        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .setProductId(inAppProductId)
                        .build()
                )
            ).build(),
            detailsResponseListener
        )
        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .setProductId(subsProductId)
                        .build()
                )
            ).build(),
            detailsResponseListener
        )
        debug("Query product details started.")
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

    private fun processPurchaseList(purchases: List<Purchase>) {
        purchases
            .filter { purchase -> purchase.purchaseState == Purchase.PurchaseState.PURCHASED }
            .forEach { purchase ->
                val isConsumable = purchase.products.any { inAppProductId == it }
                if (isConsumable) {
                    consumePurchase(purchase)
                } else if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
            }
    }

    private fun consumePurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        billingClient.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                debug("Consumption flow successful. Delivering entitlement.")
            } else {
                error("Consumption flow error: ${billingResult.debugMessage}")
            }
        }
        debug("Consumption flow started.")
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                debug("Acknowledge flow successful.")
            } else {
                error("Acknowledge flow error: ${billingResult.debugMessage}")
            }
        }
        debug("Acknowledge flow started.")
    }
}

private val TAG = BillingClient::class.java.simpleName
private fun debug(message: String) = Log.d(TAG, message)
private fun error(message: String) = Log.e(TAG, message)
