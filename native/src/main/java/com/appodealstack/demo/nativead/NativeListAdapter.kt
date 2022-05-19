package com.appodealstack.demo.nativead

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appodeal.ads.NativeAd
import com.appodeal.ads.native_ad.views.NativeAdView
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed
import com.appodealstack.demo.nativead.databinding.UserItemBinding

class NativeListAdapter() : ListAdapter<DiffItem<*>, RecyclerView.ViewHolder>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AD_ITEM -> {
                val nativeAdView: NativeAdView
                /**
                 * change to NativeAdViewAppWall(parent.context) || NativeAdViewContentStream(parent.context) || NativeAdViewNewsFeed(parent.context) to check other templates
                 * */
                nativeAdView = NativeAdViewNewsFeed(parent.context)
//                nativeAdView =  NativeAdViewAppWall(parent.context)
//                nativeAdView =  NativeAdViewContentStream(parent.context)
                val params = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                nativeAdView.layoutParams = params
                AdViewHolder(nativeAdView)
            }
            else -> {
                val binding =
                    UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            AD_ITEM -> (holder as AdViewHolder).bind(getItem(position) as DiffItem<NativeAd>)
            USER_ITEM -> (holder as UserViewHolder).bind(getItem(position) as DiffItem<Int>)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(currentList[position]) {
            is DiffItem.DiffNative -> AD_ITEM
            is DiffItem.DiffUserData -> USER_ITEM
        }
    }

    inner class UserViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userValue: DiffItem<Int>) {
            binding.root.text = userValue.getItemData().toString()
        }
    }

    inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(nativeAd: DiffItem<NativeAd>) {
            (itemView as NativeAdView).setNativeAd(nativeAd.getItemData())
        }
    }

    companion object {
        const val STEP = 5
        private const val AD_ITEM = 0
        private const val USER_ITEM = 1
    }
}

private class DiffUtils : DiffUtil.ItemCallback<DiffItem<*>>() {

    override fun areItemsTheSame(oldItem: DiffItem<*>, newItem: DiffItem<*>): Boolean {
        return oldItem.getItemId() == newItem.getItemId()
    }

    override fun areContentsTheSame(oldItem: DiffItem<*>, newItem: DiffItem<*>): Boolean {
        return oldItem.getItemHash() == newItem.getItemHash()
    }
}