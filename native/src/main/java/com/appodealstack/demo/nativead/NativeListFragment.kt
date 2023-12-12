package com.appodealstack.demo.nativead

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.appodeal.ads.Appodeal
import com.appodeal.ads.NativeAd
import com.appodealstack.demo.nativead.adapter.ListItem
import com.appodealstack.demo.nativead.adapter.NativeListAdapter
import com.appodealstack.demo.nativead.databinding.NativeListFragmentBinding

class NativeListFragment : Fragment() {

    /**
     * Retrieves a native ad instance for binding.
     *
     * @return A [NativeAd] instance if available, or null if no native ads are present.
     */
    private val getNativeAd: () -> NativeAd? = { Appodeal.getNativeAds(1).firstOrNull() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = NativeListFragmentBinding.inflate(inflater, container, false)
        val nativeListAdapter = NativeListAdapter()
        binding.recyclerView.adapter = nativeListAdapter
        obtainData(nativeListAdapter)
        return binding.root
    }

    private fun obtainData(nativeListAdapter: NativeListAdapter) {
        val yourDataItems = generateYourData()
        nativeListAdapter.submitList(yourDataItems.addNativeAdItems())
    }

    private fun List<ListItem>.addNativeAdItems() =
        this.foldIndexed(
            initial = listOf(),
            operation = { index: Int, acc: List<ListItem>, yourDataItem: ListItem ->
                val shouldAdd = index % STEPS == 0 && index != 0
                if (shouldAdd) {
                    acc + createDynamicNativeAd() + yourDataItem
                } else {
                    acc + yourDataItem
                }
            }
        )

    private fun generateYourData(): List<ListItem> =
        (1..USER_DATA_SIZE).toList().map { ListItem.YourDataItem(userData = it) }

    private fun createDynamicNativeAd(): ListItem.DynamicNativeAdItem =
        ListItem.DynamicNativeAdItem(getNativeAd = getNativeAd)
}

private const val USER_DATA_SIZE = 200
private const val STEPS = 5