package com.appodealstack.demo.analytics

import android.app.Activity
import android.app.Application
import androidx.lifecycle.*
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

class AnalyticsViewModel(
    application: Application,
    private val billing: BillingUseCase
) : AndroidViewModel(application) {

    val purchases: LiveData<List<Pair<SkuDetails?, Purchase>>> = billing.purchases

    fun flowInAppPurchase(activity: Activity) {
        billing.flow(activity, SKU_COINS)
    }

    fun flowSubsPurchase(activity: Activity) {
        billing.flow(activity, SKU_INFINITE_ACCESS_MONTHLY)
    }
}

const val SKU_INFINITE_ACCESS_MONTHLY = "infinite_access_monthly"
const val SKU_COINS = "coins"

class ViewModelFactory constructor(
    private val application: Application,
    private val billing: BillingUseCase = BillingUseCase(application)
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            AnalyticsViewModel(application, billing) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}