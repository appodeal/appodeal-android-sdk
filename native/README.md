# Native

Native ad is a flexible type of advertising. You can adapt the display to your UI by preparing a
template.

>  Appodeal provides 4 options to implement the layout of native ads **3 templates** + your **custom implementation**.
All of them are inherited from the same `NativeAdView` class.

`NativeAdView` consists of the following components:
1. `NativeIconView` - Icon of the native ad.
2. `AdAttributionView` - Advertising Indicator. This is a TextView labeled "Ad".
3. `TitleVIew` - Title of the native ad.
4. `DescriptionView` - Text descriptionView of the native ad.
5. `RatingBarView` - Rating of the app in [0-5] range.
6. `NativeMediaView` - Media content of the native ad.
7. `CallToActionView` - Button for click.
8. `AdChoiceView` - Special ad icon provided by ad network.

## **Templates**:
To display them, all you need to do is:

1. Create programmatically or in your layout file 1 View class;
2. Work as an advertising view object.

**Native template views classes:**

- `NativeAdViewNewsFeed`;
- `NativeAdViewAppWall`;
- `NativeAdViewContentStream`

## **NativeAdView for custom implementation**:

To display it, all you need to do is:
1. Create a `NativeAdVIew` class programmatically or in your layout file
2. Inside the created `NativeAdVIew`, arrange all the `View`/`IconView`/`MediaView` you need for displaying it in any style you prefer
3. Bind programmatically or in your layout file all necessary `View`/`IconView`/`MediaView`.
4. Then you can work with NativeAdVIew as an advertising view object.

**Native view for a Custom Implementation**:
- `NativeAdView`.

# Integration guide

## For Templates

1. Create programmatically or in your layout file 1 View class:

**For XML:**

```xml showLineNumbers
<com.appodeal.ads.nativead.NativeAdViewNewsFeed
    android:id="@+id/native_news_feed"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

<com.appodeal.ads.nativead.NativeAdViewAppWall
    android:id="@+id/native_app_wall"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

<com.appodeal.ads.nativead.NativeAdViewContentStream
    android:id="@+id/native_content_stream"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

**Programmatically:**

```kotlin showLineNumbers
val newsFeedView = NativeAdViewNewsFeed(context)
val appWallView = NativeAdViewAppWall(context)
val contentStreamView = NativeAdViewContentStream(context)
```

2. Get a view instance from layout **OR** add a programmatically created ViewTemplate to your View hierarchy:

**For XML:**
```jsx title=Kotlin showLineNumbers
val newsFeedView = findViewById<NativeAdViewNewsFeed>(R.id.native_news_feed);
val appWallView = findViewById<NativeAdViewAppWall>(R.id.native_app_wall);
val contentStreamView = findViewById<NativeAdViewContentStream>(R.id.native_content_stream);
```

**Programmatically:**
```kotlin showLineNumbers
rootView.addView(newsFeedView)
rootView.addView(appWallView)
rootView.addView(contentStreamView)
```

3. When the NativeAd is loaded just register it

**For single NativeAd:**

```kotlin showLineNumbers
if(Appodeal.isLoaded(Appodeal.NATIVE)) {
  newsFeedView.registerView(Appodeal.getNativeAdCount(1))
}
```

**For several NativeAd instances:**

```kotlin showLineNumbers
val needToShow = 3

if(Appodeal.getAvailableNativeAdsCount() >= needToShow) {
    val nativeAds = Appodeal.getNativeAdCount(needToShow)
    newsFeedView.registerView(nativeAds[0])
    appWallView.registerView(nativeAds[1])
    contentStreamView.registerView(nativeAds[2])
}
```

## For custom layout

**General requirements:**
- `NativeAdView` must have min height as 32dp;
- `NativeMediaView` must have mim size as 120dp x 120dp;
- The `AdAttributionView` must clearly mark your nativeAd as "Ad" so that users don't mistake them for content;
- You are allowed to scale the `NativeIconView` or `NativeMediaView` down without modifying the aspect ratio;
- You are allowed to crop the `NativeIconView` or `NativeMediaView` symmetrically by up to 20% in only one dimension (height or width).

1. Create your markdown with `NativeAdView` as root

>> You can build a layout with any style, arrangement of elements and with any type of
`ViewGroup`(`ConstrainLayout`, `RelativeLayout`, `FrameLayout`)

```jsx title=XML showLineNumbers
<?xml version="1.0" encoding="utf-8"?>
<com.appodeal.ads.nativead.NativeAdView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nativeAdView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:callToActionViewId="@id/callToActionView"
    app:descriptionViewId="@id/descriptionView"
    app:iconViewId="@id/iconView"
    app:mediaViewId="@id/mediaView"
    app:ratingViewId="@id/ratingView"
    app:titleViewId="@id/titleView"
    app:adAttributionViewId="@+id/ad_attribution">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="5dp"
        android:orientation="vertical">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <com.appodeal.ads.nativead.NativeIconView
                android:id="@+id/iconView"
                android:layout_width="90dp"
                android:layout_height="90dp"
                android:layout_gravity="center_vertical"
                android:layout_margin="5dp" />
                
                <TextView
                android:id="@+id/ad_attribution"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                />

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">

                    <TextView
                        android:id="@+id/titleView"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"/>

                </LinearLayout>

                <TextView
                    android:id="@+id/descriptionView"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:maxLines="3" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">

                    <LinearLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_gravity="center_vertical"
                        android:layout_weight="1"
                        android:orientation="horizontal">

                        <RatingBar
                            android:id="@+id/ratingView"
                            style="?android:attr/ratingBarStyleSmall"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content" />
                    </LinearLayout>

                    <Button
                        android:id="@+id/callToActionView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:minHeight="30dp" />
                </LinearLayout>
            </LinearLayout>
        </LinearLayout>

        <com.appodeal.ads.nativead.NativeMediaView
            android:id="@+id/mediaView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />
    </LinearLayout>
</com.appodeal.ads.nativead.NativeAdView>
```

**Requirements for `NativeAdView` elements**

| Name of view        | Type            | Mandatory            | Description                                                                                                                                                                                                                                |
|---------------------|-----------------|----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `titleView`         | TextView        | Mandatory            | Title of the native ad. Maximum 25 symbols of the title should always be displayed. You can add ellipsis at the end if the title is longer.                                                                                                |
| `callToActionView`  | Button          | Mandatory            | Call-to-action text. Should be displayed without truncation on a visible button.                                                                                                                                                           |
| `descriptionView`   | TextView        | Optional             | Text description of the native ad. If you choose to display the description, you should display maximum 100 characters. You can add ellipsis at the end.                                                                                   |
| `ratingView`        | RatingBar       | Optional             | Rating of the app in [0-5] range                                                                                                                                                                                                           |
| `adAttributionView` | TextView        | Mandatory            | You can specify the color of the text and background. See [adAttribution settings](#adAttributionView-settings). You can place it anywhere inside the `NativeAdView`.                                                                      |
| `adChoicesView`     | ViewGroup       | Attach automatically | Special ad icon provided by ad network. If SDK received AdChoice from ad network, it attached it automatically. You can specify a position in one of the corners of the `NativeAdView`. [See setAdChoice](#set-adChoice-position) position |
| `iconView`          | NativeIconView  | Mandatory/Optional   | Returns `true,` if NativeAd object contains the video.                                                                                                                                                                                     |
| `mediaView`         | NativeMediaView | Mandatory/Optional   | Check if native ad can be shown with the placement.                                                                                                                                                                                        |

> `NativeAdView` must contain either `NativeIconView` or `NativeMediaView`.
The `titleView`, `callToActionView` and `providerView` has to be added in any cases.

2. Set ids of all child views of `NativeAdView`.

> You can do this either in the xml file of markdown (recommended way) or programmatically

**For XML:**

```xml showLineNumbers
<com.appodeal.ads.nativead.NativeAdView
    app:callToActionViewId="@id/callToActionView"
    app:descriptionViewId="@id/descriptionView"
    app:iconViewId="@id/iconView"
    app:mediaViewId="@id/mediaView"
    app:ratingViewId="@id/ratingView"
    app:titleViewId="@id/titleView">
```

**Programmatically:**

```kotlin showLineNumbers
val nativeAdView: NativeAdView = ...

nativeAdView.titleViewId = R.id.titleView
nativeAdView.callToActionViewId = R.id.callToActionView
nativeAdView.descriptionViewId = R.id.descriptionView
nativeAdView.ratingViewId = R.id.ratingView
nativeAdView.iconViewId = R.id.iconView
nativeAdView.mediaViewId = R.id.mediaView

// OR

nativeAdView.titleView = findViewById(R.id.titleView)
nativeAdView.callToActionView = findViewById(R.id.callToActionView)
nativeAdView.descriptionView = findViewById(R.id.descriptionView)
nativeAdView.ratingView = findViewById(R.id.ratingView)
nativeAdView.iconView = findViewById(R.id.iconView)
nativeAdView.mediaView = findViewById(R.id.mediaView)
```

3. When the `NativeAd` is loaded just register it

**For single NativeAd:**

```kotlin showLineNumbers
if(Appodeal.isLoaded(Appodeal.NATIVE)) {
  newsFeedView.registerView(Appodeal.getNativeAdCount(1))
}
```

**For several NativeAd instances:**

```kotlin showLineNumbers
val needToShow = 3

if(Appodeal.getAvailableNativeAdsCount() >= needToShow) {
    val nativeAds = Appodeal.getNativeAdCount(needToShow)
    nativeAdView1.registerView(nativeAds[0])
    nativeAdView2.registerView(nativeAds[1])
    nativeAdView3.registerView(nativeAds[2])
}
```

4. When the display has been terminated and you no longer plan to use the NativeAdView, you should
   call the destroy method:

```kotlin showLineNumbers
nativeAdView.destroy()
```

> If you want to show a new NativeAd inside used nativeAdView, just call
`nativeAdView.registerView(newNativeAd)` method.

## Check If Ad Is Loaded

To check if at least 1 instance of `NativeAd` is loaded, use the method:

```kotlin showLineNumbers
Appodeal.isLoaded(Appodeal.NATIVE)
```

To get how many `NativeAd` instances are loaded, use the method:

```kotlin showLineNumbers
val nativeAmount = Appodeal.getAvailableNativeAdsCount()
```

> By default, the Appodeal SDK with AutoCahce enabled loads 2 instances of `NativeAd` each
We recommend you always check whether an ad is available before trying to show it.

## Get Loaded Native Ads

To get loaded native ads, use the following method:

```kotlin showLineNumbers
val nativeAds: List<NativeAd> = Appodeal.getNativeAds(amount)
```

> **Once you get the ads, they are removed from our SDK cache.**

## Display

To display `NativeAd`, you need to call the following code:

```kotlin showLineNumbers
NativeAdView.registerView(nativeAd: NativeAd)
```

> SDK can't show ads without a network connection!

`NativeAdView.registerView()` returns a **boolean** value indicating whether the show
method call was passed to the appropriate SDK.

> Before the `registerView(nativeAd)` method is called, `NativeAdView` is in the `visibility == GONE`
state. After the call, the state will automatically change to `visibility == VISIBLE`.

> You don't need to change the visibility state, Appodeal SDK does it automatically.

> After calling `destroy()`, the state will automatically change to `visibility == GONE`.

>`NativeAdView` and its successors have a built-in attribute `tools:visibility="visible"` so the view
will be displayed in your IDE markup during development.


## Placements

Appodeal SDK allows you to tag each impression with different placement.
To use placements, you need to create placements in Appodeal Dashboard.
[Read more about placements](https://faq.appodeal.com/en/collections/107523-placements).

To show an ad with placement, you have to call show method:

```kotlin showLineNumbers
NativeAdView.registerView(nativeAd: NativeAd, yourPlacementName: String)
```

> If the loaded ad can’t be shown for a specific placement, nothing will be shown.

If auto caching is enabled, sdk will start to cache another ad, which can affect display rate.
To save the loaded ad for future use (for instance, for another placement) check if the ad can be
shown before calling the show method:

```kotlin showLineNumbers
if(NativeAd.canShow(context: Context, yourPlacementName: String)){
    NativeAdView.registerView(nativeAd: NativeAd, yourPlacementName: String)
}
```

You can configure your impression logic for each placement.


> If you have no placements, or call `NativeAdView.registerView` with a placement that does not exist,
the impression will be tagged with 'default' placement  and its settings will be applied.


> Placement settings affect **ONLY** ad presentation, not loading or caching.

## UnregisterView

To unregister the view from displaying the currently registered native ad use the method:

```kotlin showLineNumbers
NativeAdView.unregisterView()
```

> `UnregisterView` method **does not hide** the `NativeAdView`. It suspends the NativeAd display tracking.
It will also be automatically called when the [`NativeAdView.onDetachedFromWindow()`](https://developer.android.com/reference/android/view/View#onDetachedFromWindow()) method is triggered.

> UnregisterView makes sense to use, for example, if the NativeAdView is out of the screen while
scrolling in the list, or is temporarily overlapped by another `View`/`Fragment`/`Activity`

## Destroy

To destroy the native ad view and perform any necessary cleanup, and hide `NativeAdView` use the method:

```kotlin showLineNumbers
NativeAdView.destroy()
```


> This method should be called when the native ad is no longer needed.
Also, when `destroy()` is called, the `unregisterView` logic is triggered.

## Callbacks

```kotlin showLineNumbers
Appodeal.setNativeCallbacks(object : NativeCallbacks {
  override fun onNativeLoaded() {
    // Called when native ads are loaded
  }
  override fun onNativeFailedToLoad() {
    // Called when native ads are failed to load
  }
  override fun onNativeShown(NativeAd nativeAd) {
    // Called when native ad is shown
  }
  override fun onNativeShowFailed(NativeAd nativeAd) {
    // Called when native ad show failed
  }
  override fun onNativeClicked(NativeAd nativeAd) {
    // Called when native ads is clicked
  }
  override fun onNativeExpired() {
    // Called when native ads is expired
  }
})
```

> All callbacks are called on the main thread.

## Cache Manually

To disable automatic caching for native ads, use the code below before the SDK initialization:

```kotlin showLineNumbers
Appodeal.setAutoCache(Appodeal.NATIVE, false)
```

> Read more on manual caching in our [**FAQ**](https://faq.appodeal.com/en/articles/2658522-sdk-caching).

## Cache

To cache native ads, use:

```kotlin showLineNumbers
Appodeal.cache(this, Appodeal.NATIVE)
```

To cache multiple native ads, use:

```kotlin showLineNumbers
Appodeal.cache(this, Appodeal.NATIVE, 3)
```

> You may request a **maximum of 5** `NativeAd`
The number of cached ads is not guaranteed and could be less than requested.

## Check If Ad Is Initialized

To check if `NativeAd` was initialized, you can use the method:

```kotlin showLineNumbers
Appodeal.isInitialized(Appodeal.NATIVE)
```

Returns`true`, if the `NativeAd` was initialized.

## Check If Autocache Is Enabled

To check if autocache is enabled for `NativeAd`, you can use the method:

```kotlin showLineNumbers
Appodeal.isAutoCacheEnabled(Appodeal.NATIVE)
```

Returns`true`, if autocache is enabled for native.

## Get Predicted eCPM

To get the predicted eCPM from the next block in the caching queue, use the method:

```kotlin showLineNumbers
NativeAd.predictedEcpm
```

# Configuration

## Set preferred media content type

You can tell the Appodeal SDK your preferred content type for NativeAd.
To do this, use the method:

```kotlin showLineNumbers
// both static image and video native ads will be loaded
Appodeal.setPreferredNativeContentType(NativeMediaViewContentType.Auto)
// only static image native ads will be loaded
Appodeal.setPreferredNativeContentType(NativeMediaViewContentType.NoVideo)
// only video native ads will be loaded.
Appodeal.setPreferredNativeContentType(NativeMediaViewContentType.Video)
```

> Setting a video type does not guarantee that it will be loaded, but only indicates the preferred type.

To check if the downloaded advertisement contains video you can use the method:

```kotlin showLineNumbers
NativeAd.containsVideo()
```

Return `true` if `NativeAd` contains video

Use the method to retrieve the preferred content type:

```kotlin showLineNumbers
Appodeal.getPreferredNativeContentType()
```

> Only affects content inside the `NativeMediaView`. Therefore, it makes sense to use it only in
case of `NativeAdViewContentStream` template or your out custom implementation of `NativeAdView`.
Content for `NativeIconView` is always a static image

## Set adChoice position

You can specify a position in one of the corners of the `NativeAdView`:

```kotlin showLineNumbers
NativeAdView.setAdChoicesPosition(Position.END_TOP)
```

> As a `Position` you can specify one of 4 options:
`START_TOP` - matches to the upper left corner of `NativeAdView`;
`START_BOTTOM` - matches to the lower left corner of `NativeAdView`;
`END_TOP` - matches the upper right corner of `NativeAdView`;
`END_BOTTOM` - matches the bottom right corner of `NativeAdView`.

## AdAttributionView settings

You may set text color and background color for AdAttributionView:

```kotlin showLineNumbers
nativeAdView.setAdAttributionBackground(Color.RED)
nativeAdView.setAdAttributionTextColor(Color.WHITE)
```

> Color should have ColorInt format. See [`android.graphics.Color`](https://developer.android.com/reference/android/graphics/Color).

> You may do the same via your xml markup using attributes `android:textColor` and `android:background`

## Works with lists

To use `NativeAd` in `RecyclerView`, you can use the following example:

1. Create an ListItem entity that will serve to define the `itemViewType` in RecyclerView.ListAdapter

```kotlin showLineNumbers
sealed interface ListItem {
    fun getItemId(): Int

    class NativeAdItem(val getNativeAd: () -> NativeAd?) : ListItem {
        override fun getItemId() = NATIVE_AD_ITEM

        companion object {
            const val NATIVE_AD_ITEM = 3
        }
    }

    data class YourDataItem(val userData: Int) : ListItem {
        override fun getItemId() = USER_ITEM

        companion object {
            const val USER_ITEM = 2
        }
    }
}
```

2. Create a `DiffUtil.ItemCallback<ListItem>` entity that will show the `ListAdapter` the differences
   between items

```kotlin showLineNumbers
internal class DiffUtils : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.getItemId() == newItem.getItemId()

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.hashCode() == newItem.hashCode()
}
```

3. Create a `ListAdapter` entity, which will be an adapter for `RecyclerView`

```kotlin showLineNumbers
class NativeListAdapter : ListAdapter<ListItem, ListHolder>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        return when (viewType) {
            NATIVE_AD_ITEM -> { DynamicAdViewHolder(NativeAdViewContentStream(parent.context)) }
            else -> {
                YourViewHolder(
                    YourDataItemBinding.inflate(LayoutInflater.from(parent.context),
                                                parent,
                                                false
                    ))
            }
        }
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListItem.YourDataItem -> (holder as YourViewHolder).bind(item)
            is ListItem.NativeAdItem -> (holder as DynamicAdViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is ListItem.YourDataItem -> USER_ITEM
            is ListItem.NativeAdItem -> NATIVE_AD_ITEM
        }
    }

    sealed class ListHolder(root: View) : RecyclerView.ViewHolder(root) {
        class YourViewHolder(private val binding: YourDataItemBinding) : ListHolder(binding.root) {
            fun bind(item: ListItem.YourDataItem) {
                binding.root.text = item.userData.toString()
            }
        }

        class DynamicAdViewHolder(itemView: View) : ListHolder(itemView) {
            fun bind(item: ListItem.NativeAdItem) {
                val nativeAd = item.getNativeAd.invoke()
                if (nativeAd != null) {
                    (itemView as NativeAdView).registerView(nativeAd)
                }
            }
        }
    }
}
```

> `NATIVE_AD_ITEM` - `NativeAdItem.NATIVE_AD_ITEM`

4. As the markup of your Activity/Fragment, we use the markup

```jsx title=activity_main.xml showLineNumbers
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?android:attr/colorBackground">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />

</FrameLayout>
```

We'll use the example as the markup of your YourDataItem:

```jsx title=yout_data_item.xml showLineNumbers
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="16dp"
    android:textSize="64sp"
    tools:text="1" />
```

5. In your `Activity`/`Fragment`, add the following code:

```kotlin showLineNumbers
class NativeActivity : AppCompatActivity() {

    private val getNativeAd: () -> NativeAd? = { Appodeal.getNativeAds(1).firstOrNull() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNativeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nativeListAdapter = NativeListAdapter()
        binding.recyclerView.adapter = nativeListAdapter

        setUpAppodealSDK()
    }

    private fun setUpAppodealSDK() {
        Appodeal.setLogLevel(LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.initialize(this, APPODEAL_APP_KEY, Appodeal.NATIVE) { errors ->
            val initResult = if (errors.isNullOrEmpty()) "successfully" else "with ${errors.size} errors"
            Log.d("TAG", "onInitializationFinished: $initResult")
        }
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
                    acc + createNativeAdItem() + yourDataItem
                } else {
                    acc + yourDataItem
                }
            }
        )

    private fun generateYourData(): List<ListItem> =
        (1..USER_DATA_SIZE).toList().map { ListItem.YourDataItem(userData = it) }

    private fun createNativeAdItem(): ListItem.NativeAdItem =
        ListItem.NativeAdItem(getNativeAd = getNativeAd)
}
private const val USER_DATA_SIZE = 200
private const val STEPS = 5
```

> `STEPS` - step through which the insertion of `NativeAd` will be repeated;
`addNativeAdItems()` - logic of inserting `NativeAd` into the list through a certain number of `STEPS`.

Done! When you want to insert native ads into `RecyclerView`, simply call the `obtainData()` method.

## Common Mistakes

- **No ad providerView**

The majority of ad networks require publishers to add a special mark to a native ad, so users don’t
mistake them for content. That’s why you always need to make sure, that native ads in your app have
the ad attribution (e.g., “Ad”) or the AdChoices icon.

- **Absence of the required native ad elements**

Every native ad should contain:
- titleView;
- callToActionView Button;
- adAttribution TextView;
- `NativeIconView` or `NativeMedaiaView`.

- **Native ad elements alteration**

Advertisers expect that their ads will be displayed clearly and without any alteration. You can scale
buttons and images, but you shouldn't crop, cover or distort them.

- **Overlaying elements of native ads on each other**

Make sure, that all elements of a native ad are visible and not overlaid.

Native ads requirements:

- All of the fields of native ad marked as mandatory must be displayed.
- Image assets can be resized to fit your ad space but should not be significantly distorted or cropped.

## Check Viewability

You can always check in logs if show was tracked and your ad is visible.

You will see the **Native \[Notify Shown\]** log if show was tracked successfully.

```logcat showLineNumbers
Appodeal                com.example.app                    D Native [Notify Shown]
```