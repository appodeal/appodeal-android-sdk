package com.appodealstack.demo.analytics

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

class AnalyticsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val billing = BillingUseCase(
        application,
        listOf(SKU_COINS),
        listOf(SKU_INFINITE_ACCESS_MONTHLY)
    )

    val purchases: LiveData<List<AnalyticsPurchase>> = billing.purchases

    fun flowInAppPurchase(activity: Activity) = billing.flow(activity, SKU_COINS)

    fun flowSubsPurchase(activity: Activity) = billing.flow(activity, SKU_INFINITE_ACCESS_MONTHLY)
}

const val SKU_COINS = "coins"
const val SKU_INFINITE_ACCESS_MONTHLY = "infinite_access_monthly"

class AnalyticsPurchase(val purchase: Purchase, val purchaseDetails: SkuDetails)
