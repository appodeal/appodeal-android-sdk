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
import com.appodealstack.demo.nativead.databinding.NativeListFragmentBinding
import java.util.concurrent.CopyOnWriteArrayList

class NativeListFragment : Fragment() {

    private var _binding: NativeListFragmentBinding? = null
    private val binding get() = _binding!!
    private var detachListener: FragmentDetachListener? = null
    private val adapter = NativeListAdapter()
    private val recyclerList: CopyOnWriteArrayList<DiffItem<*>> = getUserData()

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
        _binding = NativeListFragmentBinding.inflate(inflater, container, false)
        Appodeal.setNativeCallbacks(nativeCallbacks)
        adapter.submitList(getUserData())
        addLoadedAd()
        binding.nativeList.adapter = adapter
        return binding.root
    }

    private fun getUserData(): CopyOnWriteArrayList<DiffItem<*>> {
        val list = CopyOnWriteArrayList<DiffItem<*>>()
        for (itemData in 0 until USER_DATA_SIZE) {
            list.add(DiffItem.DiffUserData(itemData))
        }
        return list
    }

    private fun addLoadedAd() {
        val nativeAdList = Appodeal.getNativeAds(Appodeal.getAvailableNativeAdsCount())
        addPack(nativeAdList)
    }

    private fun addPack(loadedAds: MutableList<NativeAd?>) {
        var tempSteps = 0
        for (i in recyclerList) {
            if (i is DiffItem.DiffNativeAd) {
                tempSteps = 0
                continue
            } else {
                tempSteps++
            }
            if (tempSteps == STEPS) {
                if (loadedAds.isNotEmpty()) {
                    recyclerList.add(recyclerList.indexOf(i),
                        DiffItem.DiffNativeAd(loadedAds.removeAt(loadedAds.lastIndex))
                    )
                    adapter.submitList(recyclerList)
                    adapter.notifyItemChanged(recyclerList.indexOf(i))
                } else {
                    break
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        detachListener?.onFragmentDetached()
    }
}

private const val USER_DATA_SIZE = 200
private const val STEPS = 5