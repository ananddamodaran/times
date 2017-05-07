package com.viginfotech.chennaitimes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidnanban.chennaitime.data.NewsContract;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.androidnanban.chennaitime.NewsFragment.FeedsQuery;


public class Detail extends AppCompatActivity {

    private static final String TAG = Detail.class.getSimpleName();
    private ViewPager mPager;
    private DetailViewPagerAdapter mPagerAdapter;
    private int selectedFeedToRead;
    private int mCurrentPagerItem;
    private List<LocalFeed> feeds;
    private int category;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detail);

         category=getIntent().getIntExtra("category",-1);
        selectedFeedToRead=getIntent().getIntExtra("position",-1);

        mPager = (ViewPager) findViewById(R.id.detailPager);
        feeds= getFeedFromCursor(category);
        setupViewPager();


    }



    public void setupViewPager(){
        mPagerAdapter = new DetailViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(NewsContract.NewsEntry.COLUMN_READ_STATE, 1);
                getContentResolver().
                        update(NewsContract.NewsEntry.CONTENT_URI,
                                contentValues, NewsContract.NewsEntry.COLUMN_FEED_LINK +
                                        " = ?", new String[]{feeds.get(position).getGuid()});
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });
        mPager.setCurrentItem(selectedFeedToRead);
    }

    public List<LocalFeed> getFeedFromCursor(int category) {
        final List<LocalFeed> feedList = new ArrayList<>();


        final Cursor cursor =getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,
                FeedsQuery.PROJECTIONS, NewsContract.NewsEntry.COLUMN_FEED_CATEGORY + " = ?"
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

    public void shareContent(View view) {
        feeds=getFeedFromCursor(category);
        LocalFeed feed= feeds.get((feeds.size()==mCurrentPagerItem+1)?mCurrentPagerItem:mCurrentPagerItem-1);
        share(( Jsoup.parse(feed.getTitle().trim()).text() + "\n" + feed.getDetailNews()));
    }
    private void share(String msg) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.send_intent_title)));
    }


    private class DetailViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<LocalFeed> result;

        public DetailViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.result=getFeedFromCursor(category);
        }

        @Override
        public Fragment getItem(int position) {
            this.result=getFeedFromCursor(category);
            mCurrentPagerItem=position;
            selectedFeedToRead=position;
            DetailFragment fragment = new DetailFragment();
            LocalFeed localFeed=result.get(position);
          //  Log.i(TAG, "getItem: Fragment "+localFeed.getTitle());
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
