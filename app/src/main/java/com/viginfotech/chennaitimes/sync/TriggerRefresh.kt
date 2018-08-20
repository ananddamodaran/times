package com.viginfotech.chennaitimes.sync


import android.app.IntentService
import android.content.ContentValues
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.viginfotech.chennaitimes.Constants.CATEGORY_BUSINESS
import com.viginfotech.chennaitimes.Constants.CATEGORY_CINEMA
import com.viginfotech.chennaitimes.Constants.CATEGORY_HEADLINES
import com.viginfotech.chennaitimes.Constants.CATEGORY_INDIA
import com.viginfotech.chennaitimes.Constants.CATEGORY_SPORTS
import com.viginfotech.chennaitimes.Constants.CATEGORY_TAMILNADU
import com.viginfotech.chennaitimes.Constants.CATEGORY_WORLD
import com.viginfotech.chennaitimes.LocalFeed
import com.viginfotech.chennaitimes.data.NewsContract
import com.viginfotech.chennaitimes.model.Feed
import com.viginfotech.chennaitimes.service.ChennaiTimeAPIBuilder
import com.viginfotech.chennaitimes.util.ChennaiTimesPreferences
import com.viginfotech.chennaitimes.util.NetworkUtil
import java.io.IOException
import java.util.*


/**
 * Created by anand on 9/13/16.
 */
class TriggerRefresh : IntentService("CTAppTriggerRefresh") {
    private var data: ArrayList<LocalFeed>? = null
    private val myApiService = ChennaiTimeAPIBuilder.buildChennaiTimesService()

    override fun onHandleIntent(intent: Intent?) {
        if (!NetworkUtil.isOnline(this)) return
        val bundle = intent!!.extras
        var category = -1
        if (bundle != null)
            category = bundle.getInt("category", -1)
        //Log.i(TAG, "onHandleIntent: "+category);
        val feedList = ArrayList<Feed>()
        val syncStart = Intent(ChennaiTimesPreferences.SYNC_START)
        LocalBroadcastManager.getInstance(this).sendBroadcast(syncStart)
        try {
            when (category) {
                CATEGORY_HEADLINES -> {
                    val headlines = myApiService.getHeadLines().execute().body()!!.items
                    if (headlines != null) feedList.addAll(headlines)
                }
                CATEGORY_TAMILNADU -> {
                    val tamilnadu = myApiService.getTamilNadu().execute().body()!!.items
                    if (tamilnadu != null) feedList.addAll(tamilnadu)
                }
                CATEGORY_INDIA -> {
                    val india = myApiService.getIndiaFeeds().execute().body()!!.items
                    if (india != null) feedList.addAll(india)
                }
                CATEGORY_WORLD -> {
                    val world = myApiService.getWorldFeeds().execute().body()!!.items
                    if (world != null) feedList.addAll(world)
                }
                CATEGORY_BUSINESS -> {
                    val business = myApiService.getBusinessFeeds().execute().body()!!.items
                    if (business != null) feedList.addAll(business)
                }
                CATEGORY_SPORTS -> {
                    val sports = myApiService.getSportsFeeds().execute().body()!!.items
                    if (sports != null) feedList.addAll(sports)
                }
                CATEGORY_CINEMA -> {
                    val cinema = myApiService.getCinemaFeeds().execute().body()!!.items
                    if (cinema != null) feedList.addAll(cinema)
                }
            }
            data = ArrayList()

            copy(feedList, data!!)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        val syncComplete = Intent(ChennaiTimesPreferences.SYNC_COMPLETE)
        LocalBroadcastManager.getInstance(this).sendBroadcast(syncComplete)

    }

    fun copy(feed: List<Feed>, data: MutableList<LocalFeed>): List<LocalFeed> {
        Log.d(TAG,"feeds size"+data.size)
        Log.d(TAG,"feeds size"+feed.size)
        for (serverFeed in feed) {
            val localFeed = LocalFeed()
            localFeed.title = serverFeed.title
            localFeed.detailedTitle = serverFeed.detailedTitle
            localFeed.summary = serverFeed.summary
            localFeed.pubDate = serverFeed.pubDate
            localFeed.guid = serverFeed.guid
            localFeed.thumbnail = serverFeed.thumbnail
            localFeed.image = serverFeed.image
            localFeed.detailNews = serverFeed.detailNews
            localFeed.categoryId = serverFeed.categoryId
            localFeed.sourceId = serverFeed.sourceId
            data.add(localFeed)


        }
        val cVVector = Vector<ContentValues>(data.size)
        for (i in data.indices) {


            val localFeed = data[i]
            val title = localFeed.title
            val detailedTitle = localFeed.detailedTitle
            val pubDate = localFeed.pubDate!!
            val guid = localFeed.guid
            val thumbnail = localFeed.thumbnail
            val image = localFeed.image
            val summary = localFeed.summary
            val detailNews = localFeed.detailNews
            val category = localFeed.categoryId
            val source = localFeed.sourceId
            val readState = 0

            val values = ContentValues()
            values.put(NewsContract.NewsEntry.COLUMN_FEED_TITLE, title)
            values.put(NewsContract.NewsEntry.COLUMN_FEED_DETAILED_TITLE, detailedTitle)
            values.put(NewsContract.NewsEntry.COLUMN_FEED_PUBDATE, pubDate)
            values.put(NewsContract.NewsEntry.COLUMN_FEED_LINK, guid)
            values.put(NewsContract.NewsEntry.COLUMN_FEED_THUMBNAIL, thumbnail)
            values.put(NewsContract.NewsEntry.COLUMN_FEED_IMAGE, image)
            values.put(NewsContract.NewsEntry.COLUMN_FEED_SUMMARY, summary)
            values.put(NewsContract.NewsEntry.COLUMN_FEED_DETAILED_NEWS, detailNews)
            values.put(NewsContract.NewsEntry.COLUMN_FEED_CATEGORY, category)
            values.put(NewsContract.NewsEntry.COLUMN_FEED_SOURCE, source)

            values.put(NewsContract.NewsEntry.COLUMN_READ_STATE, readState)
            cVVector.add(i, values)


        }
        Log.d(TAG,"cv vector size${cVVector.size}")
        if (cVVector.size > 0) {
            var cvArray = arrayOfNulls<ContentValues>(cVVector.size)
            cvArray=cVVector.toTypedArray()
            val rowsInserted = contentResolver
                    .bulkInsert(NewsContract.NewsEntry.CONTENT_URI, cvArray)

        }

        return data
    }

    companion object {
        private val TAG = TriggerRefresh::class.java.simpleName
    }

}// private ChennaiTimesApi myApiService;
