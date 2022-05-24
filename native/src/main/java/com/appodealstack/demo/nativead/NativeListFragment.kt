package com.appodealstack.demo.nativead

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.appodeal.ads.Appodeal
import com.appodeal.ads.NativeAd
import com.appodeal.ads.NativeCallbacks
import com.appodealstack.demo.nativead.adapter.ListItem
import com.appodealstack.demo.nativead.databinding.NativeListFragmentBinding
import java.util.concurrent.CopyOnWriteArrayList

class NativeListFragment : Fragment() {
    private var detachListener: FragmentDetachListener? = null
    private val nativeListAdapter = NativeListAdapter()
    private val listItems: CopyOnWriteArrayList<ListItem> = getUserData()

    private val nativeCallbacks: NativeCallbacks = object : NativeCallbacks {
        override fun onNativeLoaded() {
            addLoadedAd()
        }

        override fun onNativeFailedToLoad() {}
        override fun onNativeShown(nativeAd: NativeAd?) {}
        override fun onNativeShowFailed(nativeAd: NativeAd?) {}
        override fun onNativeClicked(nativeAd: NativeAd?) {}
        override fun onNativeExpired() {}
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentDetachListener) {
            detachListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = NativeListFragmentBinding.inflate(inflater, container, false)
        Appodeal.setNativeCallbacks(nativeCallbacks)
        nativeListAdapter.submitList(getUserData())
        addLoadedAd()
        binding.nativeList.adapter = nativeListAdapter
        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        detachListener?.onFragmentDetached()
    }

    private fun getUserData(): CopyOnWriteArrayList<ListItem> {
        val list = CopyOnWriteArrayList<ListItem>()
        repeat(USER_DATA_SIZE) { itemData ->
            list.add(ListItem.YourDataItem(itemData))
        }
        return list
    }

    private fun addLoadedAd() {
        val nativeAdList = Appodeal.getNativeAds(Appodeal.getAvailableNativeAdsCount())
        addPack(nativeAdList)
    }

    private fun addPack(loadedAds: MutableList<NativeAd?>) {
        var tempSteps = 0
        for (item in listItems) {
            if (item is ListItem.NativeAdItem) {
                tempSteps = 0
                continue
            } else {
                tempSteps++
            }
            if (tempSteps == STEPS) {
                if (loadedAds.isNotEmpty()) {
                    listItems.add(
                        listItems.indexOf(item),
                        ListItem.NativeAdItem(loadedAds.removeAt(loadedAds.lastIndex))
                    )
                    nativeListAdapter.submitList(listItems)
                    nativeListAdapter.notifyItemChanged(listItems.indexOf(item))
                } else {
                    break
                }
            }
        }
    }
}

private const val USER_DATA_SIZE = 200
private const val STEPS = 5