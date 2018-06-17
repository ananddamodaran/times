package com.viginfotech.chennaitimes.ui


import android.content.ContentValues
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.viginfotech.chennaitimes.Constants
import com.viginfotech.chennaitimes.LocalFeed
import com.viginfotech.chennaitimes.R
import com.viginfotech.chennaitimes.data.NewsContract
import com.viginfotech.chennaitimes.model.Feed
import com.viginfotech.chennaitimes.service.ChennaiTimeAPIBuilder
import com.viginfotech.chennaitimes.util.DisplayUtil
import com.viginfotech.chennaitimes.util.TimeUtils
import kotlinx.android.synthetic.main.content_detail_page.*
import org.jsoup.Jsoup
import java.io.IOException


/**
 * Created by anand on 3/27/16.
 */
class DetailFragment : Fragment() {

    private var detailTask: GetDetailTask? = null


    private var localFeed: LocalFeed? = null
    private var mAdView: AdView? = null
    private var requestOptions = RequestOptions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestOptions.placeholder(R.color.lighter_gray)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.content_detail_page, container, false)

        mAdView = view.findViewById<View>(R.id.ad_view) as AdView

        // getDeepLinkInvitation();



        return view
    }


    /** Called when leaving the activity  */
    override fun onPause() {
        if (mAdView != null) {
            mAdView!!.pause()
        }
        super.onPause()
    }

    /** Called when returning to the activity  */
    override fun onResume() {
        super.onResume()
        if (mAdView != null) {
            mAdView!!.resume()
        }
    }

    /** Called before the activity is destroyed  */
    override fun onDestroy() {
        if (mAdView != null) {
            mAdView!!.destroy()
        }
        super.onDestroy()
    }


    inner class GetDetailTask : AsyncTask<LocalFeed, Void, Feed>() {
        val myApiService = ChennaiTimeAPIBuilder.buildChennaiTimesService();


        override fun onPreExecute() {
            super.onPreExecute()
            pgbar!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: LocalFeed): Feed? {
            try {
                var feed: Feed
                val source = params[0].sourceId
                val category = params[0].categoryId
                val guid = params[0].guid
                feed = myApiService!!.getNewsDetail(category, guid, source).execute().body()!!

                return feed

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }


        override fun onPostExecute(localFeed: Feed?) {
            super.onPostExecute(localFeed)
            if (localFeed != null) {
                pgbar!!.visibility = View.GONE
                var titleText: String?

                if (localFeed.detailedTitle != null) {
                    titleText = Jsoup.parse(localFeed.detailedTitle.trim()).text()
                } else {
                    titleText = Jsoup.parse(localFeed.title.trim()).text()
                }
                publisherlogo!!.visibility = View.VISIBLE
                publisherlogo!!.setImageDrawable(DisplayUtil.getPublisherLogo(context!!, localFeed.sourceId))
                publisher!!.text = DisplayUtil.getPublisherName(context!!, localFeed.sourceId)
                time!!.text = TimeUtils.formatShortDate(localFeed!!.pubDate)
                divider!!.visibility = View.VISIBLE

                title!!.text = titleText
                detail!!.setText(localFeed.detailNews)
                if (localFeed!!.image != null) {
                    thumbnail!!.visibility = View.VISIBLE
                    Glide.with(activity!!)
                            .setDefaultRequestOptions(requestOptions)
                            .load(localFeed.image).into(thumbnail)
                }
                mAdView!!.visibility = View.VISIBLE

                val contentValues = ContentValues()
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_DETAILED_TITLE, localFeed.detailedTitle)
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_THUMBNAIL, localFeed.thumbnail)
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_IMAGE, localFeed.image)
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_DETAILED_NEWS, localFeed.detailNews)
                activity!!.contentResolver.update(NewsContract.NewsEntry.CONTENT_URI,
                        contentValues, NewsContract.NewsEntry.COLUMN_FEED_LINK + " = ?", arrayOf(localFeed.guid))

            } else {
                pgbar!!.visibility = View.GONE
                detail!!.setText(R.string.retry_detail)

            }

        }
    }

    override fun onStop() {
        super.onStop()
        if (detailTask != null) {
            detailTask!!.cancel(true)
        }
    }

    override fun onStart() {
        super.onStart()
        val json = arguments!!.getParcelable<LocalFeed>(Constants.EXTRA_LOCAL_FEED)
        localFeed = json
        if (localFeed != null && localFeed!!.detailNews == null) {

            detailTask = GetDetailTask()
            detailTask!!.execute(localFeed)

        } else {

            if (localFeed != null) {
                var titleText: String?
                if (localFeed!!.detailedTitle != null) {
                    titleText = Jsoup.parse(localFeed!!.detailedTitle.trim { it <= ' ' }).text()
                } else {
                    titleText = Jsoup.parse(localFeed!!.title.trim { it <= ' ' }).text()
                }
                publisherlogo!!.visibility = View.VISIBLE
                publisherlogo!!.setImageDrawable(DisplayUtil.getPublisherLogo(context!!, localFeed!!.sourceId))
                title!!.text = titleText
                divider!!.visibility = View.VISIBLE
                publisher!!.text = DisplayUtil.getPublisherName(context!!, localFeed!!.sourceId)
                time!!.text = TimeUtils.formatShortDate(localFeed!!.pubDate!!)
                mAdView!!.visibility = View.VISIBLE
                if (localFeed!!.image != null) {
                    thumbnail!!.visibility = View.VISIBLE
                    Glide.with(this).setDefaultRequestOptions(requestOptions)
                            .load(localFeed!!.image).into(thumbnail!!)
                } else {
                    thumbnail!!.visibility = View.GONE
                }

                detail!!.text = localFeed!!.detailNews
            }


        }


        val adRequest = AdRequest.Builder()
                .build()

        mAdView!!.loadAd(adRequest)
    }

    companion object {
        private val TAG = "DetailFragment"
    }


}
