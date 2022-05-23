package com.appodealstack.demo.analytics

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.android.billingclient.api.Purchase

class AnalyticsViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val billing = BillingUseCase(application, ID_COINS, ID_INFINITE_ACCESS_MONTHLY)
    val purchases: LiveData<List<Purchase>> = billing.purchases

    fun flowInAppPurchase(activity: Activity) = billing.flowInApp(activity, ID_COINS)

    fun flowSubsPurchase(activity: Activity) =
        billing.flowSubscription(activity, ID_INFINITE_ACCESS_MONTHLY)

    fun getProductDetails(productId: String) = billing.productDetails[productId]
}

private const val ID_COINS = "coins"
private const val ID_INFINITE_ACCESS_MONTHLY = "infinite_access_monthly"
