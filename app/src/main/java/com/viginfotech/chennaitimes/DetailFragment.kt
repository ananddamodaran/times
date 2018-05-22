package com.viginfotech.chennaitimes

import android.content.ContentValues
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

import com.viginfotech.chennaitimes.data.NewsContract
import com.viginfotech.chennaitimes.util.CloudEndpointBuilderHelper
import com.viginfotech.chennaitimes.util.DisplayUtil
import com.viginfotech.chennaitimes.util.TimeUtils

import org.jsoup.Jsoup

import java.io.IOException

import butterknife.Bind
import butterknife.ButterKnife
import com.viginfotech.chennaitimes.model.Feed
import com.viginfotech.chennaitimes.service.ChennaiTimeAPIBuilder


/**
 * Created by anand on 3/27/16.
 */
class DetailFragment : Fragment() {
    @Bind(R.id.detail)
    internal var detailNews: TextView? = null

    @Bind(R.id.publisherlogo)
    internal var publisherLogo: ImageView? = null
    @Bind(R.id.publisher)
    internal var publisher: TextView? = null
    @Bind(R.id.time)
    internal var time: TextView? = null
    @Bind(R.id.title)
    internal var title: TextView? = null
    @Bind(R.id.thumbnail)
    internal var thumbnail: ImageView? = null
    private var detailTask: GetDetailTask? = null
    @Bind(R.id.pgbar)
    internal var pgbar: ProgressBar? = null
    @Bind(R.id.divider)
    internal var divider: View? = null
    private var localFeed: LocalFeed? = null
    private var mAdView: AdView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.content_detail_page, container, false)
        ButterKnife.bind(this, view)
        mAdView = view.findViewById<View>(R.id.ad_view) as AdView

        // getDeepLinkInvitation();

        val json = arguments!!.getParcelable<LocalFeed>(Constants.EXTRA_LOCAL_FEED)
        localFeed = json
        if (localFeed != null && localFeed!!.detailNews == null) {

            detailTask = GetDetailTask()
            detailTask!!.execute(localFeed)

        } else {

            if (localFeed != null) {
                var titleText: String? = null
                if (localFeed!!.detailedTitle != null) {
                    titleText = Jsoup.parse(localFeed!!.detailedTitle.trim { it <= ' ' }).text()
                } else {
                    titleText = Jsoup.parse(localFeed!!.title.trim { it <= ' ' }).text()
                }
                publisherLogo!!.visibility = View.VISIBLE
                publisherLogo!!.setImageDrawable(DisplayUtil.getPublisherLogo(context, localFeed!!.sourceId))
                title!!.text = titleText
                divider!!.visibility = View.VISIBLE
                publisher!!.text = DisplayUtil.getPublisherName(context, localFeed!!.sourceId)
                time!!.text = TimeUtils.formatShortDate(localFeed!!.pubDate!!)
                mAdView!!.visibility = View.VISIBLE
                if (localFeed!!.image != null) {
                    thumbnail!!.visibility = View.VISIBLE
                    Glide.with(this).load(localFeed!!.image).placeholder(R.color.lighter_gray).into(thumbnail!!)
                } else {
                    thumbnail!!.visibility = View.GONE
                }

                detailNews!!.text = localFeed!!.detailNews
            }


        }


        val adRequest = AdRequest.Builder()
                .build()

        mAdView!!.loadAd(adRequest)

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
                var feed: Feed? = null
                val source = params[0].sourceId
                val category = params[0].categoryId
                val guid = params[0].guid
                feed = myApiService!!.getNewsDetail(category, guid, source).execute()

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
                var titleText: String? = null

                if (localFeed!!.getDetailedTitle() != null) {
                    titleText = Jsoup.parse(localFeed!!.getDetailedTitle().trim()).text()
                } else {
                    titleText = Jsoup.parse(localFeed!!.getTitle().trim()).text()
                }
                publisherLogo!!.visibility = View.VISIBLE
                publisherLogo!!.setImageDrawable(DisplayUtil.getPublisherLogo(context, localFeed!!.getSourceId()))
                publisher!!.text = DisplayUtil.getPublisherName(context, localFeed!!.getSourceId())
                time!!.text = TimeUtils.formatShortDate(localFeed!!.getPubDate())
                divider!!.visibility = View.VISIBLE

                title!!.text = titleText
                detailNews!!.setText(localFeed!!.getDetailNews())
                if (localFeed!!.getImage() != null) {
                    thumbnail!!.visibility = View.VISIBLE
                    Glide.with(activity).load(localFeed!!.getImage()).placeholder(R.color.lighter_gray).into(thumbnail)
                }
                mAdView!!.visibility = View.VISIBLE

                val contentValues = ContentValues()
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_DETAILED_TITLE, localFeed!!.getDetailedTitle())
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_THUMBNAIL, localFeed!!.getThumbnail())
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_IMAGE, localFeed!!.getImage())
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_DETAILED_NEWS, localFeed!!.getDetailNews())
                activity!!.contentResolver.update(NewsContract.NewsEntry.CONTENT_URI,
                        contentValues, NewsContract.NewsEntry.COLUMN_FEED_LINK + " = ?", arrayOf(localFeed!!.getGuid()))

            } else {
                pgbar!!.visibility = View.GONE
                detailNews!!.setText(R.string.retry_detail)

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


    }

    companion object {
        private val TAG = "DetailFragment"
    }


}
