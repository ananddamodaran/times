package com.viginfotech.chennaitimes.ui

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.google.android.gms.appinvite.AppInvite
import com.google.android.gms.appinvite.AppInviteReferral
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.viginfotech.chennaitimes.Config
import com.viginfotech.chennaitimes.Constants
import com.viginfotech.chennaitimes.LocalFeed
import com.viginfotech.chennaitimes.R
import com.viginfotech.chennaitimes.data.NewsContract
import io.fabric.sdk.android.Fabric
import org.jsoup.Jsoup
import java.util.*



class Detail : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    private var mPager: ViewPager? = null
    private var mPagerAdapter: DetailViewPagerAdapter? = null
    private var selectedFeedToRead = 0
    private var mCurrentPagerItem: Int = 0
    private var feeds: List<LocalFeed>? = null
    private var category: Int = 0
    private var guid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fabric.with(this, Crashlytics())
        setContentView(R.layout.content_detail)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        FirebaseAnalytics.getInstance(this)
        val action = intent.action
        if (action != null) {
            getDeepLinkInvitation()
        } else {
            category = intent.getIntExtra("category", -1)
            selectedFeedToRead = intent.getIntExtra("position", -1)
            mPager = findViewById<View>(R.id.detailPager) as ViewPager
            val shareFAB = findViewById<View>(R.id.fab) as FloatingActionButton
            shareFAB.visibility = View.VISIBLE
            setupViewPager()

        }
    }

    fun setupViewPager() {

        mPagerAdapter = DetailViewPagerAdapter(supportFragmentManager)
        mPager!!.adapter = mPagerAdapter
        mPager!!.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                markRead(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })

        if (selectedFeedToRead != 0) {
            val localFeed = LocalFeed()
            localFeed.guid = guid
            if (feeds!!.contains(localFeed)) {
                selectedFeedToRead = feeds!!.indexOf(localFeed)
            }
        }

        mPager!!.currentItem = selectedFeedToRead
    }

    private fun markRead(position: Int) {
        val contentValues = ContentValues()
        contentValues.put(NewsContract.NewsEntry.COLUMN_READ_STATE, 1)
        contentResolver.update(NewsContract.NewsEntry.CONTENT_URI,
                contentValues, NewsContract.NewsEntry.COLUMN_FEED_LINK + " = ?", arrayOf(feeds!![position].guid))
    }

    fun getFeedFromCursor(category: Int): List<LocalFeed> {
        val feedList = ArrayList<LocalFeed>()


        val cursor = contentResolver.query(NewsContract.NewsEntry.CONTENT_URI,
                NewsFragment.FeedsQuery.PROJECTIONS, NewsContract.NewsEntry.COLUMN_FEED_CATEGORY + " = ?", arrayOf(category.toString()), null)

        try {
            while (cursor != null && cursor.moveToNext()) {

                val title = cursor.getString(NewsFragment.FeedsQuery.TITLE)
                val detailedTitle = cursor.getString(NewsFragment.FeedsQuery.DETAILED_TITLE)
                val summary = cursor.getString(NewsFragment.FeedsQuery.SUMMARY)
                val pubDate = cursor.getLong(NewsFragment.FeedsQuery.PUBDATE)
                val guid = cursor.getString(NewsFragment.FeedsQuery.LINK)
                val thumbnail = cursor.getString(NewsFragment.FeedsQuery.THUMBNAIL)
                val image = cursor.getString(NewsFragment.FeedsQuery.IMAGE)
                val detailedNews = cursor.getString(NewsFragment.FeedsQuery.DETAILED_NEWS)
                val cat = cursor.getInt(NewsFragment.FeedsQuery.CATEGORY)
                val source = cursor.getInt(NewsFragment.FeedsQuery.SOURCE)
                val readState = cursor.getInt(NewsFragment.FeedsQuery.READ_STATE)

                val feed = LocalFeed()
                feed.title = title
                feed.detailedTitle = detailedTitle
                feed.summary = summary
                feed.pubDate = pubDate
                feed.guid = guid
                feed.thumbnail = thumbnail
                feed.image = image
                feed.detailNews = detailedNews
                feed.categoryId = cat
                feed.sourceId = source
                feed.readState = readState
                feedList.add(feed)


            }
            Collections.sort(feedList, Collections.reverseOrder { lhs, rhs -> lhs.pubDate!!.compareTo(rhs.pubDate) })

        } finally {
            cursor?.close()
        }
        return feedList
    }

    override fun onStop() {
        super.onStop()

    }

    fun getDeepLinkInvitation() {
        val mGoogleApiClient = GoogleApiClient.Builder(applicationContext)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build()
        val autoLaunchDeepLink = false
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback { result ->
                    if (result.status.isSuccess) {
                        val intent = result.invitationIntent
                        val deepLink = AppInviteReferral.getDeepLink(intent)
                        if (deepLink != null) {
                            val uri = Uri.parse(deepLink)
                            val categoryQueryParam = uri.getQueryParameter("category")
                            if (categoryQueryParam != null) {
                                category = Integer.parseInt(categoryQueryParam)
                                guid = uri.getQueryParameter("guid")
                                mPager = findViewById<View>(R.id.detailPager) as ViewPager

                                val shareFAB = findViewById<View>(R.id.fab) as FloatingActionButton
                                shareFAB.visibility = View.VISIBLE
                                setupViewPager()
                            } else {

                                val home = Intent(applicationContext, HomeActivity::class.java)
                                startActivity(home)
                                finish()
                            }
                        }


                    } else {
                        Log.d(TAG, "getInvitation: no deep link found.")
                    }
                }
    }

    fun buildDeepLink(deepLink: Uri, minVersion: Int, isAd: Boolean): Uri {
        val appCode = getString(R.string.app_code)

        val packageName = applicationContext.packageName

        val builder = Uri.Builder()
                .scheme("https")
                .authority("$appCode.app.goo.gl")
                .path("/")
                .appendQueryParameter("link", deepLink.toString())
                .appendQueryParameter("apn", packageName)

        if (isAd) {
            builder.appendQueryParameter("ad", "1")
        }

        if (minVersion > 0) {
            builder.appendQueryParameter("amv", Integer.toString(minVersion))
        }
        return builder.build()
    }

    fun shareContent(view: View) {
        feeds = getFeedFromCursor(category)
        val feed = feeds!![if (feeds!!.size == mCurrentPagerItem + 1) mCurrentPagerItem else mCurrentPagerItem - 1]
        /*  String guid=feed.getGuid();
        String category=String.valueOf(feed.getCategoryId());
        String source=String.valueOf(feed.getSourceId());
        // Build the link with all required parameters
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("chennaitimes-news.appspot.com")
                .path("/")
                .appendQueryParameter("category", category)
                .appendQueryParameter("source", source)
                .appendQueryParameter("guid",guid);

        final Uri deepLink = buildDeepLink(builder.build(), 0, false);
        getShortURL(deepLink);*/

        val title = Jsoup.parse(feed.title!!.trim { it <= ' ' }).text()

        share(title + "\n" + Config.DYNAMIC_LINK + "\n Install " + getString(R.string.app_name_tamil))

    }

    private fun getShortURL(deepLink: Uri) {

    }

    private fun share(msg: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, msg)
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.send_intent_title)))
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.w(TAG, "onConnectionFailed:$connectionResult")
        Toast.makeText(this, "Google Play Services Error: " + connectionResult.errorCode,
                Toast.LENGTH_SHORT).show()
    }


    private inner class DetailViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private var result: List<LocalFeed>? = null

        init {
            feeds = getFeedFromCursor(category)
            this.result = feeds
        }

        override fun getItem(position: Int): Fragment {
            this.result = getFeedFromCursor(category)
            mCurrentPagerItem = position
            selectedFeedToRead = position
            val fragment = DetailFragment()
            val args = Bundle()
            args.putParcelable(Constants.EXTRA_LOCAL_FEED, result!![position])
            fragment.arguments = args

            return fragment

        }

        override fun getCount(): Int {
            return result!!.size
        }


    }

    companion object {

        private val TAG = Detail::class.java.simpleName
    }


}
