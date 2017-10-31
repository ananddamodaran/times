package com.viginfotech.chennaitimes.sync;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.viginfotech.chennaiTimesApi.ChennaiTimesApi;
import com.viginfotech.chennaiTimesApi.model.Feed;
import com.viginfotech.chennaitimes.Detail;
import com.viginfotech.chennaitimes.LocalFeed;
import com.viginfotech.chennaitimes.NewsFragment;
import com.viginfotech.chennaitimes.R;
import com.viginfotech.chennaitimes.data.NewsContract;
import com.viginfotech.chennaitimes.util.ChennaiTimesPreferences;
import com.viginfotech.chennaitimes.util.CloudEndpointBuilderHelper;
import com.viginfotech.chennaitimes.util.NetworkUtil;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
        if(!NetworkUtil.isOnline(this)) return;
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
           String action= intent.getAction();
            if(action!=null&&action.equals("autosync")){
                showNotification();
            }

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


    private void showNotification() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);
        int ampm = cal.get(Calendar.AM_PM);
        if (hour == 7 || hour == 10 || (hour == 1 && ampm == 1) || (hour
                == 13 && ampm == 1) || (hour == 4 && ampm == 1) || (hour == 16 && ampm == 1)||
                (hour == 5 && ampm == 1) || (hour == 17 && ampm == 1)||
                (hour == 6 && ampm == 1) || (hour == 18 && ampm == 1)
                ) {
            Cursor cursor = getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,
                    NewsFragment.FeedsQuery.PROJECTIONS, NewsContract.NewsEntry.COLUMN_FEED_CATEGORY + " = ?"
                    , new String[]{String.valueOf(0)}, null);

            List<LocalFeed> mDataSet=NewsFragment.getLocalFeeds(cursor);
            Collections.sort(mDataSet, Collections.reverseOrder(new Comparator<LocalFeed>() {
                @Override
                public int compare(LocalFeed lhs, LocalFeed rhs) {
                    return (lhs).getPubDate().compareTo((rhs).getPubDate());
                }
            }));
            LocalFeed feed = mDataSet.get(0);


            try {
                String title = feed.getTitle();
                String message = Jsoup.parse(feed.getSummary().trim()).text();
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                Bitmap remote_picture = null;
                if (feed.getThumbnail() != null) {
                    remote_picture = BitmapFactory.decodeStream((InputStream) new URL(feed.getThumbnail()).getContent());
                }

                Intent intent = new Intent(this, Detail.class);
                intent.putExtra("category", feed.getCategoryId());
                intent.putExtra("position", 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                URL url = null;

                NotificationCompat.BigPictureStyle bigPicStyle = null;
                if (remote_picture != null)
                    bigPicStyle = new NotificationCompat.BigPictureStyle()
                            .bigPicture(remote_picture);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_ct)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)

                        .setContentIntent(pendingIntent);
                if (bigPicStyle != null) {
                    notificationBuilder.setStyle(bigPicStyle);
                }


                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
