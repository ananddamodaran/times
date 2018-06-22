package com.viginfotech.chennaitimes.ui


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.provider.BaseColumns
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.viginfotech.chennaitimes.LocalFeed
import com.viginfotech.chennaitimes.R
import com.viginfotech.chennaitimes.adapter.NewsFeedAdapter
import com.viginfotech.chennaitimes.adapter.NewsFeedAdapter.OnItemClickListener
import com.viginfotech.chennaitimes.data.NewsContract
import com.viginfotech.chennaitimes.sync.TriggerRefresh
import com.viginfotech.chennaitimes.util.ChennaiTimesPreferences
import com.viginfotech.chennaitimes.util.NetworkUtil.isOnline
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


/**
 * Created by anand on 2/9/16.
 */
class NewsFragment : Fragment() {

    private var page: Int = 0
    val TAG="NewsFragment"
    val  STATE_POSITION = "selected_feed_position";
    val STATE_POSITION_OFFSET ="selected_feed_position_offset"
     val ARG_SECTION_NUMBER = "section_number"


    // internal var recyclerView: RecyclerView? = null

    //internal var progressBar: ProgressBar? = null

    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: NewsFeedAdapter? = null

   // internal var swipeRefresh: SwipeRefreshLayout? = null

    private var selectedFeedToRead: Int = 0
    private var overalldx: Int = 0
    private var mSyncBroadCastReceiver: BroadcastReceiver? = null


    interface FeedsQuery {
        companion object {
            val PROJECTIONS = arrayOf(BaseColumns._ID, NewsContract.NewsEntry.COLUMN_FEED_TITLE, NewsContract.NewsEntry.COLUMN_FEED_DETAILED_TITLE, NewsContract.NewsEntry.COLUMN_FEED_SUMMARY, NewsContract.NewsEntry.COLUMN_FEED_PUBDATE, NewsContract.NewsEntry.COLUMN_FEED_LINK, NewsContract.NewsEntry.COLUMN_FEED_THUMBNAIL, NewsContract.NewsEntry.COLUMN_FEED_IMAGE, NewsContract.NewsEntry.COLUMN_FEED_DETAILED_NEWS, NewsContract.NewsEntry.COLUMN_FEED_CATEGORY, NewsContract.NewsEntry.COLUMN_FEED_SOURCE, NewsContract.NewsEntry.COLUMN_READ_STATE)
            val _ID = 0
            val TITLE = 1
            val DETAILED_TITLE = 2
            val SUMMARY = 3
            val PUBDATE = 4
            val LINK = 5
            val THUMBNAIL = 6
            val IMAGE = 7
            val DETAILED_NEWS = 8
            val CATEGORY = 9
            val SOURCE = 10
            val READ_STATE = 11
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        if (savedInstanceState != null) {
            selectedFeedToRead = savedInstanceState.getInt(STATE_POSITION)
            overalldx = savedInstanceState.getInt(STATE_POSITION_OFFSET)
        }
        page = arguments!!.getInt(ARG_SECTION_NUMBER)

        mSyncBroadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action == ChennaiTimesPreferences.SYNC_START) {
                    swipe_refresh_layout!!.isRefreshing = true

                } else {
                    swipe_refresh_layout!!.isRefreshing = false


                    val getFeedFromDisk = GetFeedFromDisk()
                    getFeedFromDisk.execute(page)
                }
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //    Log.i(TAG, "onCreateView: ");
        val rootView = inflater.inflate(R.layout.content_main, container, false)



        return rootView
    }



    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_POSITION, selectedFeedToRead)
        outState.putInt(STATE_POSITION_OFFSET, overalldx)

        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ChennaiTimesPreferences.SYNC_START)
        intentFilter.addAction(ChennaiTimesPreferences.SYNC_COMPLETE)

        LocalBroadcastManager.getInstance(context!!).registerReceiver(mSyncBroadCastReceiver!!,
                intentFilter)
        adapter = NewsFeedAdapter(context!!, mutableListOf(), onItemClickListener)




        recylerView_!!.adapter = adapter

    }


    override fun onPause() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(mSyncBroadCastReceiver!!)

        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId

        if (id == R.id.action_refresh) {

            activity!!.startService(Intent(context, TriggerRefresh::class.java).putExtra("category", page))
            return true
        }
        if(id==R.id.action_meme_ac){
            startActivity(Intent(context!!,MemeActivity::class.java))
        }

        return super.onOptionsItemSelected(item)

    }


    override fun onStart() {
        super.onStart()


        progressBar_!!.visibility = View.GONE
        mLayoutManager = LinearLayoutManager(context!!)
        recylerView_!!.layoutManager = mLayoutManager
        recylerView_!!.itemAnimator = DefaultItemAnimator()
        swipe_refresh_layout!!.setColorSchemeResources(R.color.colorAccent, android.R.color.holo_blue_light, android.R.color.holo_green_light)
        swipe_refresh_layout!!.setOnRefreshListener { activity!!.startService(Intent(context, TriggerRefresh::class.java).putExtra("category", page)) }

        recylerView_!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                overalldx = 0
                overalldx = overalldx + dx
            }
        })
        val getFeedFromDisk = GetFeedFromDisk()
        getFeedFromDisk.execute(page)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            selectedFeedToRead = savedInstanceState.getInt(STATE_POSITION)
            overalldx = savedInstanceState.getInt(STATE_POSITION_OFFSET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private inner class GetFeedFromDisk : AsyncTask<Int, Void, MutableList<LocalFeed>>() {
        override fun doInBackground(vararg params: Int?): MutableList<LocalFeed>? {
            var cursor: Cursor? = null
            try {

                val category = params[0]
                cursor = activity!!.contentResolver.query(NewsContract.NewsEntry.CONTENT_URI,

                        FeedsQuery.PROJECTIONS, NewsContract.NewsEntry.COLUMN_FEED_CATEGORY + " = ?", arrayOf(category.toString()), null)
                if (cursor != null && cursor.count == 0) {
                    activity!!.startService(Intent(context, TriggerRefresh::class.java).putExtra("category", category))

                } else {

                    return getLocalFeeds(cursor)
                }
                return null
            } finally {
                cursor?.close()
            }
        }

        override fun onPostExecute(result: MutableList<LocalFeed>?) {

            adapter = NewsFeedAdapter(activity!!, result,onItemClickListener )
            adapter!!.notifyDataSetChanged()
            recylerView_!!.adapter = adapter
            mLayoutManager!!.scrollToPositionWithOffset(selectedFeedToRead, overalldx)

        }
    }
   val onItemClickListener= object :OnItemClickListener {
        override fun onItemClick(view: View, feed: LocalFeed, pos: Int) {

            Log.d(TAG, "onItemClick: $pos")

            selectedFeedToRead = pos
            if (feed.detailNews == null && !isOnline(context!!)) {
                Snackbar.make(recylerView_!!, "Check Internet", Snackbar.LENGTH_LONG).show()

            }

            val intent = Intent(activity!!.baseContext, Detail::class.java)
            intent.putExtra("category", feed.categoryId)
            intent.putExtra("position", selectedFeedToRead)
            startActivity(intent)
        }
    }

    fun getLocalFeeds(cursor: Cursor?): MutableList<LocalFeed> {
        val feedList = ArrayList<LocalFeed>()
        while (cursor != null && cursor.moveToNext()) {

            val title = cursor.getString(FeedsQuery.TITLE)
            val detailedTitle = cursor.getString(FeedsQuery.DETAILED_TITLE)
            val summary = cursor.getString(FeedsQuery.SUMMARY)
            val pubDate = cursor.getLong(FeedsQuery.PUBDATE)
            val guid = cursor.getString(FeedsQuery.LINK)
            val thumbnail = cursor.getString(FeedsQuery.THUMBNAIL)
            val image = cursor.getString(FeedsQuery.IMAGE)
            val detailedNews = cursor.getString(FeedsQuery.DETAILED_NEWS)
            val cat = cursor.getInt(FeedsQuery.CATEGORY)
            val source = cursor.getInt(FeedsQuery.SOURCE)
            val readState = cursor.getInt(FeedsQuery.READ_STATE)

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
        return feedList
    }

        private val LOADER_ID = 1000





}
