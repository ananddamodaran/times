package com.viginfotech.chennaitimes;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.viginfotech.chennaitimes.adapter.NewsFeedAdapter;
import com.viginfotech.chennaitimes.data.NewsContract;
import com.viginfotech.chennaitimes.sync.TriggerRefresh;
import com.viginfotech.chennaitimes.util.ChennaiTimesPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.viginfotech.chennaitimes.util.NetworkUtil.isOnline;


/**
 * Created by anand on 2/9/16.
 */
public class NewsFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = NewsFragment.class.getSimpleName();
    private static final int LOADER_ID = 1000;
    private static final String STATE_POSITION = "selected_feed_position";
    private static final String STATE_POSITION_OFFSET ="selected_feed_position_offset" ;

    private int page;
    @BindView(R.id.listView) RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private LinearLayoutManager mLayoutManager;
    private NewsFeedAdapter adapter;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefresh;
    private int selectedFeedToRead;
    private int overalldx;
    private BroadcastReceiver mSyncBroadCastReceiver;



    public interface FeedsQuery {
        String[] PROJECTIONS = new String[]{
                BaseColumns._ID,
                NewsContract.NewsEntry.COLUMN_FEED_TITLE,
                NewsContract.NewsEntry.COLUMN_FEED_DETAILED_TITLE,
                NewsContract.NewsEntry.COLUMN_FEED_SUMMARY,
                NewsContract.NewsEntry.COLUMN_FEED_PUBDATE,
                NewsContract.NewsEntry.COLUMN_FEED_LINK,
                NewsContract.NewsEntry.COLUMN_FEED_THUMBNAIL,
                NewsContract.NewsEntry.COLUMN_FEED_IMAGE,
                NewsContract.NewsEntry.COLUMN_FEED_DETAILED_NEWS,
                NewsContract.NewsEntry.COLUMN_FEED_CATEGORY,
                NewsContract.NewsEntry.COLUMN_FEED_SOURCE,
                NewsContract.NewsEntry.COLUMN_READ_STATE
        };
        int _ID = 0;
        int TITLE = 1;
        int DETAILED_TITLE = 2;
        int SUMMARY = 3;
        int PUBDATE = 4;
        int LINK = 5;
        int THUMBNAIL = 6;
        int IMAGE = 7;
        int DETAILED_NEWS = 8;
        int CATEGORY = 9;
        int SOURCE = 10;
        int READ_STATE = 11;

    }


    public NewsFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if(savedInstanceState!=null){
            selectedFeedToRead=savedInstanceState.getInt(STATE_POSITION);
            overalldx=savedInstanceState.getInt(STATE_POSITION_OFFSET);
        }
        page=getArguments().getInt(ARG_SECTION_NUMBER);

        mSyncBroadCastReceiver=new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
               String action= intent.getAction();
               if(action.equals(ChennaiTimesPreferences.INSTANCE.getSYNC_START())){
                   swipeRefresh.setRefreshing(true);

               }else {
                   swipeRefresh.setRefreshing(false);


                   GetFeedFromDisk getFeedFromDisk=new GetFeedFromDisk();
                   getFeedFromDisk.execute(page);
               }
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    //    Log.i(TAG, "onCreateView: ");
        View rootView = inflater.inflate(R.layout.content_main, container, false);
        ButterKnife.bind(this,rootView);


        progressBar.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeRefresh.setColorSchemeResources(R.color.colorAccent,android.R.color.holo_blue_light, android.R.color.holo_green_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().startService(new Intent(getContext(), TriggerRefresh.class).putExtra("category",page));

            }
        });


        adapter = new NewsFeedAdapter(getContext(), null,null);


        recyclerView.setAdapter(adapter);

       recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
           @Override
           public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);
               overalldx=0;
               overalldx=overalldx+dx;
           }
       });
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, selectedFeedToRead);
        outState.putInt(STATE_POSITION_OFFSET, overalldx);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ChennaiTimesPreferences.INSTANCE.getSYNC_START());
        intentFilter.addAction(ChennaiTimesPreferences.INSTANCE.getSYNC_COMPLETE());

        LocalBroadcastManager.getInstance(getContext()).
                registerReceiver(mSyncBroadCastReceiver,
                intentFilter);

    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mSyncBroadCastReceiver);

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {

            getActivity().startService(new Intent(getContext(), TriggerRefresh.class).putExtra("category",page));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }





    @Override
    public void onStart() {
        super.onStart();


        GetFeedFromDisk getFeedFromDisk=new GetFeedFromDisk();
        getFeedFromDisk.execute(page);



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null) {
            selectedFeedToRead = savedInstanceState.getInt(STATE_POSITION);
            overalldx = savedInstanceState.getInt(STATE_POSITION_OFFSET);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class GetFeedFromDisk  extends AsyncTask<Integer,Void,List<LocalFeed>> {
        @Override
        protected List<LocalFeed> doInBackground(Integer... params) {
             Cursor cursor=null;
            try {

                int category = params[0];
                 cursor= getActivity().getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,

                        FeedsQuery.PROJECTIONS, NewsContract.NewsEntry.COLUMN_FEED_CATEGORY + " = ?"
                        , new String[]{String.valueOf(category)}, null);
                if (cursor != null && cursor.getCount() == 0) {
                    getActivity().startService(new Intent(getContext(), TriggerRefresh.class).putExtra("category",category));

                } else {

                    return  getLocalFeeds(cursor);
                }
                return null;
            }finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        }


        @Override
        protected void onPostExecute(List<LocalFeed> feedList) {
            super.onPostExecute(feedList);
            adapter = new NewsFeedAdapter(getActivity(),feedList, new NewsFeedAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View view, LocalFeed feed, int pos) {
                    Log.d(TAG, "onItemClick: "+pos);

                    selectedFeedToRead=pos;
                    if(feed.getDetailNews()==null && !isOnline(getActivity())){
                        Snackbar.make(recyclerView,"Check Internet",Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent = new Intent(getActivity().getBaseContext(), Detail.class);
                    intent.putExtra("category",feed.getCategoryId());
                    intent.putExtra("position",selectedFeedToRead);
                    startActivity(intent);

                }
            });
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            mLayoutManager.scrollToPositionWithOffset(selectedFeedToRead,overalldx);
        }
    }

    @NonNull
    public static List<LocalFeed> getLocalFeeds(Cursor cursor) {
        ArrayList<LocalFeed> feedList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {

            String title = cursor.getString(FeedsQuery.TITLE);
            String detailedTitle = cursor.getString(FeedsQuery.DETAILED_TITLE);
            String summary = cursor.getString(FeedsQuery.SUMMARY);
            long pubDate = cursor.getLong(FeedsQuery.PUBDATE);
            String guid = cursor.getString(FeedsQuery.LINK);
            String thumbnail = cursor.getString(FeedsQuery.THUMBNAIL);
            String image = cursor.getString(FeedsQuery.IMAGE);
            String detailedNews = cursor.getString(FeedsQuery.DETAILED_NEWS);
            int cat = cursor.getInt(FeedsQuery.CATEGORY);
            int source = cursor.getInt(FeedsQuery.SOURCE);
            int readState = cursor.getInt(FeedsQuery.READ_STATE);

            LocalFeed feed = new LocalFeed();
            feed.setTitle(title);
            feed.setDetailedTitle(detailedTitle);
            feed.setSummary(summary);
            feed.setPubDate(pubDate);
            feed.setGuid(guid);
            feed.setThumbnail(thumbnail);
            feed.setImage(image);
            feed.setDetailNews(detailedNews);
            feed.setCategoryId(cat);
            feed.setSourceId(source);
            feed.setReadState(readState);
            feedList.add(feed);


        }
        return feedList;
    }




}
