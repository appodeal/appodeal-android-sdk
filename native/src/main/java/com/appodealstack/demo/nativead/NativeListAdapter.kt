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
import com.appodealstack.demo.nativead.NativeListAdapter.ListHolder.AdViewHolder
import com.appodealstack.demo.nativead.NativeListAdapter.ListHolder.YourViewHolder
import com.appodealstack.demo.nativead.adapter.DiffUtils
import com.appodealstack.demo.nativead.adapter.ListItem
import com.appodealstack.demo.nativead.adapter.ListItem.NativeAdItem.Companion.AD_ITEM
import com.appodealstack.demo.nativead.adapter.ListItem.YourDataItem.Companion.USER_ITEM
import com.appodealstack.demo.nativead.databinding.UserItemBinding

class NativeListAdapter : ListAdapter<ListItem, ListHolder>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        return when (viewType) {
            AD_ITEM -> {
                val nativeAdView: NativeAdView
                /**
                 * change to NativeAdViewAppWall(parent.context) || NativeAdViewContentStream(parent.context) || NativeAdViewNewsFeed(parent.context) to check other templates
                 * */
                nativeAdView = NativeAdViewNewsFeed(parent.context)
                val params = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                nativeAdView.layoutParams = params
                AdViewHolder(nativeAdView)
            }
            else -> {
                val binding = UserItemBinding.inflate(
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
            is ListItem.NativeAdItem -> (holder as AdViewHolder).bind(item)
            is ListItem.YourDataItem -> (holder as YourViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is ListItem.NativeAdItem -> AD_ITEM
            is ListItem.YourDataItem -> USER_ITEM
        }
    }

    sealed class ListHolder(root: View) : RecyclerView.ViewHolder(root) {
        class YourViewHolder(private val binding: UserItemBinding) : ListHolder(binding.root) {
            fun bind(item: ListItem.YourDataItem) {
                binding.root.text = item.userData.toString()
            }
        }

        class AdViewHolder(itemView: View) : ListHolder(itemView) {
            fun bind(item: ListItem.NativeAdItem) {
                (itemView as NativeAdView).setNativeAd(item.nativeAd)
            }
        }
    }
}

