package com.appodealstack.demo.nativead.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appodeal.ads.nativead.NativeAdView
import com.appodeal.ads.nativead.NativeAdViewAppWall
import com.appodeal.ads.nativead.NativeAdViewContentStream
import com.appodeal.ads.nativead.NativeAdViewNewsFeed
import com.appodealstack.demo.nativead.NativeActivity
import com.appodealstack.demo.nativead.NativeActivity.Companion.configureNativeAdView
import com.appodealstack.demo.nativead.adapter.ListItem.DynamicNativeAdItem.Companion.DYNAMIC_AD_ITEM
import com.appodealstack.demo.nativead.adapter.ListItem.YourDataItem.Companion.USER_ITEM
import com.appodealstack.demo.nativead.adapter.NativeListAdapter.ListHolder
import com.appodealstack.demo.nativead.adapter.NativeListAdapter.ListHolder.DynamicAdViewHolder
import com.appodealstack.demo.nativead.adapter.NativeListAdapter.ListHolder.YourViewHolder
import com.appodealstack.demo.nativead.databinding.NativeAdViewCustomBinding
import com.appodealstack.demo.nativead.databinding.YourDataItemBinding

class NativeListAdapter : ListAdapter<ListItem, ListHolder>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        return when (viewType) {
            DYNAMIC_AD_ITEM -> {
                val nativeAdView = createNativeAdView(parent)
                DynamicAdViewHolder(nativeAdView)
            }
            else -> {
                val binding =
                    YourDataItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                val nativeAd = item.getNativeAd.invoke() ?: return
                (itemView as NativeAdView).registerView(nativeAd)
            }
        }
    }

    private fun createNativeAdView(parent: ViewGroup): NativeAdView {
        val context = parent.context
        val nativeAdView = when (NativeActivity.nativeAdViewType) {
            NativeAdViewAppWall::class -> NativeAdViewAppWall(context)
            NativeAdViewNewsFeed::class -> NativeAdViewNewsFeed(context)
            NativeAdViewContentStream::class -> NativeAdViewContentStream(context)
            else -> NativeAdViewCustomBinding.inflate(LayoutInflater.from(context), parent, false).root
        }
        configureNativeAdView(nativeAdView)
        return nativeAdView
    }
}

