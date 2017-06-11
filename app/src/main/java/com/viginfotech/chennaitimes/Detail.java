package com.viginfotech.chennaitimes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.viginfotech.chennaitimes.data.NewsContract;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.viginfotech.chennaitimes.NewsFragment.FeedsQuery;


public class Detail extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG = Detail.class.getSimpleName();
    private ViewPager mPager;
    private DetailViewPagerAdapter mPagerAdapter;
    private int selectedFeedToRead=0;
    private int mCurrentPagerItem;
    private List<LocalFeed> feeds;
    private int category;
    private String guid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detail);
        FirebaseAnalytics.getInstance(this);
        String action=getIntent().getAction();
        if(action!=null){
            getDeepLinkInvitation();
        }else{
            category=getIntent().getIntExtra("category",-1);
            selectedFeedToRead=getIntent().getIntExtra("position",-1);
            mPager = (ViewPager) findViewById(R.id.detailPager);
            FloatingActionButton shareFAB = (FloatingActionButton) findViewById(R.id.fab);
            shareFAB.setVisibility(View.VISIBLE);
            setupViewPager();

        }



    }



    public void setupViewPager(){

        mPagerAdapter = new DetailViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
               markRead(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });

        if(selectedFeedToRead!=0){
            LocalFeed localFeed=new LocalFeed();
            localFeed.setGuid(guid);
            if(feeds.contains(localFeed)) {
               selectedFeedToRead= feeds.indexOf(localFeed);
            }
        }

        mPager.setCurrentItem(selectedFeedToRead);
    }

    private void markRead(int position) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsContract.NewsEntry.COLUMN_READ_STATE, 1);
        getContentResolver().
                update(NewsContract.NewsEntry.CONTENT_URI,
                        contentValues, NewsContract.NewsEntry.COLUMN_FEED_LINK +
                                " = ?", new String[]{feeds.get(position).getGuid()});
    }

    public List<LocalFeed> getFeedFromCursor(int category) {
        final List<LocalFeed> feedList = new ArrayList<>();


        final Cursor cursor =getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,
                NewsFragment.FeedsQuery.PROJECTIONS, NewsContract.NewsEntry.COLUMN_FEED_CATEGORY + " = ?"
                , new String[]{String.valueOf(category)}, null);

        try {
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
            Collections.sort(feedList, Collections.reverseOrder(new Comparator<LocalFeed>() {
                @Override
                public int compare(LocalFeed lhs, LocalFeed rhs) {
                    return lhs.getPubDate().compareTo(rhs.getPubDate());

                }
            }));

        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return feedList;
        }

       @Override
    protected void onStop() {
        super.onStop();

    }
    public void getDeepLinkInvitation() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    if(deepLink!=null) {
                                        Uri uri = Uri.parse(deepLink);
                                        String categoryQueryParam=uri.getQueryParameter("category");
                                        if(categoryQueryParam!=null) {
                                            category = Integer.parseInt(categoryQueryParam);
                                            guid = uri.getQueryParameter("guid");
                                            mPager = (ViewPager) findViewById(R.id.detailPager);

                                            FloatingActionButton shareFAB = (FloatingActionButton) findViewById(R.id.fab);
                                            shareFAB.setVisibility(View.VISIBLE);
                                            setupViewPager();
                                        }else{

                                            Intent home=new Intent(getApplicationContext(),HomeActivity.class);
                                            startActivity(home);
                                            finish();
                                        }
                                    }


                                } else {
                                    Log.d(TAG, "getInvitation: no deep link found.");
                                }
                            }
                        });
    }

    public Uri buildDeepLink(@NonNull Uri deepLink, int minVersion, boolean isAd) {
        String appCode = getString(R.string.app_code);

        String packageName = getApplicationContext().getPackageName();

        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority(appCode + ".app.goo.gl")
                .path("/")
                .appendQueryParameter("link", deepLink.toString())
                .appendQueryParameter("apn", packageName);

        if (isAd) {
            builder.appendQueryParameter("ad", "1");
        }

        if (minVersion > 0) {
            builder.appendQueryParameter("amv", Integer.toString(minVersion));
        }
        return builder.build();
    }

    public void shareContent(View view) {
        feeds=getFeedFromCursor(category);
        LocalFeed feed= feeds.get((feeds.size()==mCurrentPagerItem+1)?mCurrentPagerItem:mCurrentPagerItem-1);
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

       String title=Jsoup.parse(feed.getTitle().trim()).text();

        share(title +"\n"+Config.DYNAMIC_LINK+"\n via "+getString(R.string.app_name_tamil));

    }

    private void getShortURL(Uri deepLink) {

    }

    private void share(String msg) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.send_intent_title)));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services Error: " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }


    private class DetailViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<LocalFeed> result;

        public DetailViewPagerAdapter(FragmentManager fm) {
            super(fm);
            feeds=getFeedFromCursor(category);
            this.result=feeds;
        }

        @Override
        public Fragment getItem(int position) {
            this.result=getFeedFromCursor(category);
            mCurrentPagerItem=position;
            selectedFeedToRead=position;
            DetailFragment fragment = new DetailFragment();
            Bundle args = new Bundle();
            args.putParcelable(Constants.EXTRA_LOCAL_FEED, result.get(position));
            fragment.setArguments(args);

            return fragment;

        }

        @Override
        public int getCount() {
            return result.size();
        }


    }


}
