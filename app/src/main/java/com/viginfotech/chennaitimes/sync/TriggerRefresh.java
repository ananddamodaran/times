package com.viginfotech.chennaitimes.sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.viginfotech.chennaiTimesApi.ChennaiTimesApi;
import com.viginfotech.chennaiTimesApi.model.Feed;
import com.viginfotech.chennaitimes.LocalFeed;
import com.viginfotech.chennaitimes.data.NewsContract;
import com.viginfotech.chennaitimes.util.ChennaiTimesPreferences;
import com.viginfotech.chennaitimes.util.CloudEndpointBuilderHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.viginfotech.chennaitimes.Constants.CATEGORY_BUSINESS;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_CINEMA;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_HEADLINES;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_INDIA;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_SPORTS;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_TAMILNADU;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_WORLD;


/**
 * Created by anand on 9/13/16.
 */
public class TriggerRefresh extends IntentService {
    private static final String TAG = TriggerRefresh.class.getSimpleName();
    private ChennaiTimesApi myApiService;
    private ArrayList<LocalFeed> data;

    public TriggerRefresh() {
        super("CTAppTriggerRefresh");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (myApiService == null) {

            myApiService = CloudEndpointBuilderHelper.getEndpoints();


        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        int category = -1;
        if (bundle != null)
            category = bundle.getInt("category",-1);
        //Log.i(TAG, "onHandleIntent: "+category);
        List<Feed> feedList = new ArrayList<>();
        Intent syncStart = new Intent(ChennaiTimesPreferences.SYNC_START);
        LocalBroadcastManager.getInstance(this).sendBroadcast(syncStart);
        try {
        switch (category){
            case CATEGORY_HEADLINES:
                List<Feed> headlines = myApiService.getHeadLines().execute().getItems();
                if(headlines!=null)feedList.addAll(headlines);break;
            case CATEGORY_TAMILNADU:
                List<Feed> tamilnadu = myApiService.getTamilNadu().execute().getItems();
                if(tamilnadu!=null)feedList.addAll(tamilnadu);break;
            case CATEGORY_INDIA:
                List<Feed> india = myApiService.getIndiaFeeds().execute().getItems();
                if(india!=null)feedList.addAll(india);break;
            case CATEGORY_WORLD:;
                List<Feed> world = myApiService.getWorldFeeds().execute().getItems();
                if(world!=null)feedList.addAll(world);break;
            case CATEGORY_BUSINESS:
                List<Feed> business = myApiService.getBusinessFeeds().execute().getItems();
                if(business!=null)feedList.addAll(business);break;
            case CATEGORY_SPORTS:
                List<Feed> sports = myApiService.getSportsFeeds().execute().getItems();
                if(sports!=null)feedList.addAll(sports);break;
            case CATEGORY_CINEMA:
                List<Feed> cinema = myApiService.getCinemaFeeds().execute().getItems();
                if(cinema!=null)feedList.addAll(cinema);break;

        }
             data = new ArrayList<>();

            copy(feedList, data);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent syncComplete = new Intent(ChennaiTimesPreferences.SYNC_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(syncComplete);

    }

    public List<LocalFeed> copy(List<Feed> feed, List<LocalFeed> data) {
        for (Feed serverFeed : feed) {
            LocalFeed localFeed = new LocalFeed();
            localFeed.setTitle(serverFeed.getTitle());
            localFeed.setDetailedTitle(serverFeed.getDetailedTitle());
            localFeed.setSummary(serverFeed.getSummary());
            localFeed.setPubDate(serverFeed.getPubDate());
            localFeed.setGuid(serverFeed.getGuid());
            localFeed.setThumbnail(serverFeed.getThumbnail());
            localFeed.setImage(serverFeed.getImage());
            localFeed.setDetailNews(serverFeed.getDetailNews());
            localFeed.setCategoryId(serverFeed.getCategoryId());
            localFeed.setSourceId(serverFeed.getSourceId());
            data.add(localFeed);



        }
        Vector<ContentValues> cVVector = new Vector<ContentValues>(data.size());
        for (int i = 0; i < data.size(); i++) {


                LocalFeed localFeed = data.get(i);
                String title = localFeed.getTitle();
                String detailedTitle = localFeed.getDetailedTitle();
                long pubDate = localFeed.getPubDate();
                String guid = localFeed.getGuid();
                String thumbnail = localFeed.getThumbnail();
                String image = localFeed.getImage();
                String summary = localFeed.getSummary();
                String detailNews = localFeed.getDetailNews();
                int category = localFeed.getCategoryId();
                int source = localFeed.getSourceId();
                int readState = 0;

                ContentValues values = new ContentValues();
                values.put(NewsContract.NewsEntry.COLUMN_FEED_TITLE, title);
                values.put(NewsContract.NewsEntry.COLUMN_FEED_DETAILED_TITLE, detailedTitle);
                values.put(NewsContract.NewsEntry.COLUMN_FEED_PUBDATE, pubDate);
                values.put(NewsContract.NewsEntry.COLUMN_FEED_LINK, guid);
                values.put(NewsContract.NewsEntry.COLUMN_FEED_THUMBNAIL, thumbnail);
                values.put(NewsContract.NewsEntry.COLUMN_FEED_IMAGE, image);
                values.put(NewsContract.NewsEntry.COLUMN_FEED_SUMMARY, summary);
                values.put(NewsContract.NewsEntry.COLUMN_FEED_DETAILED_NEWS, detailNews);
                values.put(NewsContract.NewsEntry.COLUMN_FEED_CATEGORY, category);
                values.put(NewsContract.NewsEntry.COLUMN_FEED_SOURCE, source);

                values.put(NewsContract.NewsEntry.COLUMN_READ_STATE, readState);
                cVVector.add(i, values);


        }
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int rowsInserted = getContentResolver()
                    .bulkInsert(NewsContract.NewsEntry.CONTENT_URI, cvArray);

        }

        return data;
    }

}
