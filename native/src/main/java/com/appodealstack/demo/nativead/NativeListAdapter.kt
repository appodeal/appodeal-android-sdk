package com.appodealstack.demo.nativead

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appodeal.ads.native_ad.views.NativeAdView
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed
import com.appodealstack.demo.nativead.NativeListAdapter.ListHolder
import com.appodealstack.demo.nativead.NativeListAdapter.ListHolder.DynamicAdViewHolder
import com.appodealstack.demo.nativead.NativeListAdapter.ListHolder.YourViewHolder
import com.appodealstack.demo.nativead.adapter.DiffUtils
import com.appodealstack.demo.nativead.adapter.ListItem
import com.appodealstack.demo.nativead.adapter.ListItem.DynamicNativeAdItem.Companion.DYNAMIC_AD_ITEM
import com.appodealstack.demo.nativead.adapter.ListItem.YourDataItem.Companion.USER_ITEM
import com.appodealstack.demo.nativead.databinding.YourDataItemBinding

class NativeListAdapter : ListAdapter<ListItem, ListHolder>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        return when (viewType) {
            DYNAMIC_AD_ITEM -> {
                val nativeAdView: NativeAdView
                /**
                 * change to NativeAdViewAppWall(parent.context) || NativeAdViewContentStream(parent.context) || NativeAdViewNewsFeed(parent.context) to check other templates
                 * */
                nativeAdView = NativeAdViewNewsFeed(parent.context)
                nativeAdView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                DynamicAdViewHolder(nativeAdView)
            }
            else -> {
                val binding = YourDataItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                YourViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListItem.YourDataItem -> (holder as YourViewHolder).bind(item)
            is ListItem.DynamicNativeAdItem -> (holder as DynamicAdViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is ListItem.YourDataItem -> USER_ITEM
            is ListItem.DynamicNativeAdItem -> DYNAMIC_AD_ITEM
        }
    }

    sealed class ListHolder(root: View) : RecyclerView.ViewHolder(root) {
        class YourViewHolder(private val binding: YourDataItemBinding) : ListHolder(binding.root) {
            fun bind(item: ListItem.YourDataItem) {
                binding.root.text = item.userData.toString()
            }
        }

        class DynamicAdViewHolder(itemView: View) : ListHolder(itemView) {
            fun bind(item: ListItem.DynamicNativeAdItem) {
                (itemView as NativeAdView).setNativeAd(item.getNativeAd.invoke())
            }
        }
    }
}

