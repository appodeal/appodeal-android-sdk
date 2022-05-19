package com.appodealstack.demo.analytics

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.android.billingclient.api.Purchase

class AnalyticsViewModel(
    application: Application,
    private val billing: BillingUseCase = BillingUseCase(application)
) : AndroidViewModel(application) {

    val purchases: LiveData<List<Purchase>> = billing.purchases

    fun flowInAppPurchase(activity: Activity) {
        billing.flow(activity, SKU_COINS)
    }

    fun flowSubsPurchase(activity: Activity) {
        billing.flow(activity, SKU_INFINITE_ACCESS_MONTHLY)
    }
}

private const val SKU_INFINITE_ACCESS_MONTHLY = "infinite_access_monthly"
private const val SKU_COINS = "coins"