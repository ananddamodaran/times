package com.viginfotech.chennaitimes;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnanban.chennaitime.data.NewsContract;
import com.androidnanban.chennaitime.util.CloudEndpointBuilderHelper;
import com.androidnanban.chennaitime.util.DisplayUtil;
import com.androidnanban.chennaitime.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.news.digitalhunt.chennaiTimesApi.ChennaiTimesApi;
import com.news.digitalhunt.chennaiTimesApi.model.Feed;

import org.jsoup.Jsoup;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.androidnanban.chennaitime.Constants.CATEGORY_BUSINESS;
import static com.androidnanban.chennaitime.Constants.CATEGORY_CINEMA;
import static com.androidnanban.chennaitime.Constants.CATEGORY_HEADLINES;
import static com.androidnanban.chennaitime.Constants.CATEGORY_INDIA;
import static com.androidnanban.chennaitime.Constants.CATEGORY_SPORTS;
import static com.androidnanban.chennaitime.Constants.CATEGORY_TAMILNADU;
import static com.androidnanban.chennaitime.Constants.CATEGORY_WORLD;
import static com.androidnanban.chennaitime.Constants.SOURCE_BBCTAMIL;
import static com.androidnanban.chennaitime.Constants.SOURCE_DINAKARAN;
import static com.androidnanban.chennaitime.Constants.SOURCE_DINAMALAR;
import static com.androidnanban.chennaitime.Constants.SOURCE_DINAMANI;
import static com.androidnanban.chennaitime.Constants.SOURCE_NAKKHEERAN;
import static com.androidnanban.chennaitime.Constants.SOURCE_ONEINDIA;


/**
 * Created by anand on 3/27/16.
 */
public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";
    @Bind(R.id.detail)
    TextView detailNews;

    @Bind(R.id.publisherlogo)
    ImageView publisherLogo;
    @Bind(R.id.publisher)
    TextView publisher;
    @Bind(R.id.time)
    TextView time;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.thumbnail)
    ImageView thumbnail;
    private GetDetailTask detailTask;
    @Bind(R.id.pgbar)
    ProgressBar pgbar;
    @Bind(R.id.divider)View divider;
    private LocalFeed localFeed;
    private AdView mAdView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.content_detail_page,container,false);
        ButterKnife.bind(this,view);
        LocalFeed json = getArguments().getParcelable(Constants.EXTRA_LOCAL_FEED);
        localFeed = json;
        if (localFeed!=null&&localFeed.getDetailNews() == null) {

            detailTask = new GetDetailTask();
            detailTask.execute(localFeed);

        } else {

            if(localFeed!=null) {
                String titleText =null;
                if (localFeed.getDetailedTitle() != null){
                    titleText = Jsoup.parse(localFeed.getDetailedTitle().trim()).text();
                }else{
                    titleText =Jsoup.parse(localFeed.getTitle().trim()).text() ;
                }
                publisherLogo.setVisibility(View.VISIBLE);
                publisherLogo.setImageDrawable(DisplayUtil.getPublisherLogo(getContext(),localFeed.getSourceId()));
                title.setText(titleText);
                divider.setVisibility(View.VISIBLE);
                publisher.setText(DisplayUtil.getPublisherName(getContext(),localFeed.getSourceId()));
                time.setText(TimeUtils.formatShortDate(localFeed.getPubDate()));
                if (localFeed.getImage() != null) {
                    thumbnail.setVisibility(View.VISIBLE);
                    Glide.with(this).load(localFeed.getImage()). placeholder(R.color.lighter_gray).into(thumbnail);
                } else {
                    thumbnail.setVisibility(View.GONE);
                }

                detailNews.setText(localFeed.getDetailNews());
            }


        }

        mAdView = (AdView) view.findViewById(R.id.ad_view);
        mAdView.setVisibility(View.VISIBLE);

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mAdView.loadAd(adRequest);

        return view;
    }
    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    public class GetDetailTask extends AsyncTask<LocalFeed, Void, Feed> {
        private ChennaiTimesApi myApiService;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pgbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Feed doInBackground(LocalFeed... params) {
            if (myApiService == null) {

                myApiService = CloudEndpointBuilderHelper.getEndpoints();
            }
            try {
                Feed feed = null;
                final int source = params[0].getSourceId();
                final int category= params[0].getCategoryId();
                final String guid=params[0].getGuid();
                feed  = myApiService.getNewsDetail(category, guid, source).execute();

                return feed;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Feed localFeed) {
            super.onPostExecute(localFeed);
            if (localFeed != null) {
                pgbar.setVisibility(View.GONE);
                String titleText=null;

                if(localFeed.getDetailedTitle()!=null) {
                    titleText = Jsoup.parse(localFeed.getDetailedTitle().trim()).text();
                }else{
                    titleText=Jsoup.parse(localFeed.getTitle().trim()).text();
                }
                publisherLogo.setVisibility(View.VISIBLE);
                publisherLogo.setImageDrawable(DisplayUtil.getPublisherLogo(getContext(), localFeed.getSourceId()));
                publisher.setText(DisplayUtil.getPublisherName(getContext(),localFeed.getSourceId()));
                time.setText(TimeUtils.formatShortDate(localFeed.getPubDate()));
                divider.setVisibility(View.VISIBLE);

                title.setText(titleText);
                detailNews.setText(localFeed.getDetailNews());
                if(localFeed.getImage()!=null) {
                    thumbnail.setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).load(localFeed.getImage()). placeholder(R.color.lighter_gray).into(thumbnail);
                }

                ContentValues contentValues=new ContentValues();
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_DETAILED_TITLE,localFeed.getDetailedTitle());
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_THUMBNAIL,localFeed.getThumbnail());
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_IMAGE,localFeed.getImage());
                contentValues.put(NewsContract.NewsEntry.COLUMN_FEED_DETAILED_NEWS,localFeed.getDetailNews());
              //  contentValues.put(NewsContract.NewsEntry.COLUMN_READ_STATE,1);
                getActivity(). getContentResolver().
                        update(NewsContract.NewsEntry.CONTENT_URI,
                                contentValues, NewsContract.NewsEntry.COLUMN_FEED_LINK +
                                        " = ?", new String[]{localFeed.getGuid()});

            } else {
                pgbar.setVisibility(View.GONE);
                detailNews.setText("null");

            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (detailTask != null) {
            detailTask.cancel(true);
        }
    }
    @Override
    public void onStart() {
        super.onStart();

       /* mTracker.send(new HitBuilders.EventBuilder()

                .setCategory(getCategory(localFeed))
                .setAction("View")
                .setLabel(getSource(localFeed))
                .set("&t", "screenview")

                .build());
*/

    }

    private String getSource(LocalFeed localFeed) {
        switch (localFeed.getSourceId()){
            case SOURCE_DINAKARAN:
                return getString(R.string.dinakaran_text);
            case SOURCE_DINAMALAR:
                return getString(R.string.dinamalar_text);
            case SOURCE_DINAMANI:
                return getString(R.string.dinamani_text);
            case SOURCE_NAKKHEERAN:
                return getString(R.string.nakkheeran_text);
            case SOURCE_ONEINDIA:
                return getString(R.string.oneindia_text);
            case SOURCE_BBCTAMIL:
                return getString(R.string.bbctamil_text);

            default: return null;
        }
    }

    private String getCategory(LocalFeed localFeed) {
        switch (localFeed.getCategoryId()){
            case CATEGORY_HEADLINES:
                return getString(R.string.headlines_text);
            case CATEGORY_TAMILNADU:
                return getString(R.string.tamilnadu_text);
            case CATEGORY_INDIA:
                return getString(R.string.india_text);
            case CATEGORY_WORLD:
                return getString(R.string.world_text);
            case CATEGORY_BUSINESS:
                return getString(R.string.business_text);
            case CATEGORY_SPORTS:
                return getString(R.string.sports_text);
            case CATEGORY_CINEMA:
                return getString(R.string.cinema_text);
            default: return null;
        }
    }


}
