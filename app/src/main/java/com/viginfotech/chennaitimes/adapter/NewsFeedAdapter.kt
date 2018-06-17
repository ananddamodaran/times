package com.viginfotech.chennaitimes.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.viginfotech.chennaitimes.Constants.SOURCE_BBCTAMIL
import com.viginfotech.chennaitimes.Constants.SOURCE_DINAKARAN
import com.viginfotech.chennaitimes.Constants.SOURCE_DINAMALAR
import com.viginfotech.chennaitimes.Constants.SOURCE_DINAMANI
import com.viginfotech.chennaitimes.Constants.SOURCE_NAKKHEERAN
import com.viginfotech.chennaitimes.Constants.SOURCE_ONEINDIA
import com.viginfotech.chennaitimes.LocalFeed
import com.viginfotech.chennaitimes.R
import com.viginfotech.chennaitimes.util.GrayscaleTransformation
import com.viginfotech.chennaitimes.util.TimeUtils
import org.jsoup.Jsoup
import java.util.*




/**
 * Created by anand on 8/30/15.
 */
class NewsFeedAdapter(private val context:
                      Context,
                      private val mDataSet: MutableList<LocalFeed>?,
                      private val onItemClickListener: OnItemClickListener) :
                        RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>() {
    private val grayScaleTransformation: GrayscaleTransformation


    private var requestOptions: RequestOptions

    init {
        if (mDataSet != null) {
            Collections.sort(mDataSet, Collections.reverseOrder { lhs, rhs -> lhs.pubDate!!.compareTo(rhs.pubDate) })
        }
        grayScaleTransformation = GrayscaleTransformation(context)
        requestOptions = RequestOptions()
        requestOptions.placeholder(R.color.lighter_gray)
        requestOptions.transform(grayScaleTransformation)


    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NewsFeedAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.feed_entry, viewGroup, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: NewsFeedAdapter.ViewHolder, position: Int) {

        viewHolder.bind(mDataSet!![position],
                onItemClickListener, position)

    }


    override fun getItemCount(): Int {
        return mDataSet?.size ?: 0
    }

    fun clear() {
        mDataSet!!.clear()
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(view: View, feed: LocalFeed, p: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleText: TextView
        private val timeText: TextView
        private val thumbnail: ImageView
        private val publisher: TextView
        private val mpublisherLogo: ImageView

        init {
            titleText = itemView.findViewById<View>(R.id.feedTitle) as TextView
            timeText = itemView.findViewById<View>(R.id.time) as TextView
            thumbnail = itemView.findViewById<View>(R.id.thumbnail) as ImageView
            mpublisherLogo = itemView.findViewById<View>(R.id.publisherlogo) as ImageView
            publisher = itemView.findViewById<View>(R.id.publisher) as TextView
        }


        fun bind(feed: LocalFeed, onItemClickListener: OnItemClickListener,
                 pos: Int) {
            val imgSrc = feed.thumbnail
            var publisherLogo = R.mipmap.ic_launcher
            val title = Jsoup.parse(feed.title.trim { it <= ' ' }).text()
            val pubDate = feed.pubDate
            val sourceId = feed.sourceId.toDouble()
            val isRead = feed.readState == 1 && feed.detailNews != null

            var source: String? = null
            when (sourceId.toInt()) {
                SOURCE_DINAKARAN -> {
                    source = context.getString(R.string.dinakaran)
                    publisherLogo = R.drawable.dinakaran
                }
                SOURCE_DINAMALAR -> {
                    source = context.getString(R.string.dinamalar)
                    publisherLogo = R.drawable.dinamalar
                }
                SOURCE_BBCTAMIL -> {
                    source = context.getString(R.string.bbctamil)
                    publisherLogo = R.drawable.bbc
                }
                SOURCE_DINAMANI -> {
                    source = context.getString(R.string.dinamani)
                    publisherLogo = R.drawable.dinamani
                }
                SOURCE_ONEINDIA -> {
                    source = context.getString(R.string.oneindia)
                    publisherLogo = R.drawable.oneindia
                }
                SOURCE_NAKKHEERAN -> {
                    source = context.getString(R.string.nakkheeran)
                    publisherLogo = R.drawable.nakkheeran
                }
            }


            if (isRead) {

                titleText.setTextColor(context.resources.getColor(android.R.color.secondary_text_dark))
                publisher.setTextColor(context.resources.getColor(android.R.color.secondary_text_dark))
                timeText.setTextColor(context.resources.getColor(android.R.color.secondary_text_dark))
                Glide.with(context).setDefaultRequestOptions(requestOptions).
                        load(publisherLogo)
                        .into(mpublisherLogo)
                if (imgSrc != null && !imgSrc.isEmpty()) {
                    thumbnail.visibility = View.VISIBLE

                    Glide.with(context)
                            .setDefaultRequestOptions(requestOptions)
                            .load(imgSrc)
                            .into(thumbnail)
                } else {
                    thumbnail.visibility = View.GONE
                }
            } else {
                titleText.setTextColor(context.resources.getColor(android.R.color.primary_text_light))
                publisher.setTextColor(context.resources.getColor(android.R.color.primary_text_light))
                timeText.setTextColor(context.resources.getColor(android.R.color.primary_text_light))
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(publisherLogo)
                        .into(mpublisherLogo)
                if (imgSrc != null && !imgSrc.isEmpty()) {
                    thumbnail.visibility = View.VISIBLE
                    Glide.with(context)
                            .setDefaultRequestOptions(requestOptions)
                            .load(imgSrc)
                            .into(thumbnail)
                } else {
                    thumbnail.visibility = View.GONE
                }

            }
            titleText.text = title.trim { it <= ' ' }

            timeText.text = TimeUtils.formatShortDate(pubDate!!)
            itemView.setOnClickListener { v -> onItemClickListener.onItemClick(v, feed, pos) }
            publisher.text = source


        }
    }

    companion object {

        private val TAG = NewsFeedAdapter::class.java.simpleName
    }
}
