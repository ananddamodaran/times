package com.viginfotech.chennaitimes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viginfotech.chennaitimes.Constants;
import com.viginfotech.chennaitimes.LocalFeed;
import com.viginfotech.chennaitimes.R;
import com.viginfotech.chennaitimes.util.GrayscaleTransformation;
import com.viginfotech.chennaitimes.util.TimeUtils;

import org.jsoup.Jsoup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by anand on 8/30/15.
 */
public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    private static final String TAG = NewsFeedAdapter.class.getSimpleName();
    private final Context context;
    private final List<LocalFeed> mDataSet;
    private GrayscaleTransformation grayScaleTransformation;
    private OnItemClickListener onItemClickListener;


    public NewsFeedAdapter(Context context, List<LocalFeed> results, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.mDataSet = results;
        if (mDataSet != null) {
            Collections.sort(mDataSet, Collections.reverseOrder(new Comparator<LocalFeed>() {
                @Override
                public int compare(LocalFeed lhs, LocalFeed rhs) {
                    return (lhs).getPubDate().compareTo((rhs).getPubDate());
                }
            }));
        }
        this.onItemClickListener = onItemClickListener;
        grayScaleTransformation = new GrayscaleTransformation(context);

    }


    @Override
    public NewsFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_entry, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(NewsFeedAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.bind(mDataSet.get(position),
                onItemClickListener, position, 0);

    }


    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    public void clear() {
        mDataSet.clear();
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, LocalFeed feed, int p);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleText;
        private final TextView timeText;
        private final ImageView thumbnail;
        private final TextView publisher;
        private final ImageView mpublisherLogo;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.feedTitle);
            timeText = (TextView) itemView.findViewById(R.id.time);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            mpublisherLogo = (ImageView) itemView.findViewById(R.id.publisherlogo);
            publisher = (TextView) itemView.findViewById(R.id.publisher);
        }


        public void bind(final LocalFeed feed, final OnItemClickListener onItemClickListener,
                         final int pos, final int cursorStart) {
            String imgSrc = feed.getThumbnail();
            int publisherLogo = R.mipmap.ic_launcher;
            String title = Jsoup.parse(feed.getTitle().trim()).text();
            Long pubDate = feed.getPubDate();
            double sourceId = feed.getSourceId();
            boolean isRead = feed.getReadState() == 1 && feed.getDetailNews() != null;

            String source = null;
            switch ((int) sourceId) {
                case Constants.SOURCE_DINAKARAN:
                    source = context.getString(R.string.dinakaran);
                    publisherLogo = R.drawable.dinakaran;
                    break;
                case Constants.SOURCE_DINAMALAR:
                    source = context.getString(R.string.dinamalar);
                    publisherLogo = R.drawable.dinamalar;
                    break;
                case Constants.SOURCE_BBCTAMIL:
                    source = context.getString(R.string.bbctamil);
                    publisherLogo = R.drawable.bbc;

                    break;
                case Constants.SOURCE_DINAMANI:
                    source = context.getString(R.string.dinamani);
                    publisherLogo = R.drawable.dinamani;

                    break;
                case Constants.SOURCE_ONEINDIA:
                    source = context.getString(R.string.oneindia);
                    publisherLogo = R.drawable.oneindia;
                    break;
                case Constants.SOURCE_NAKKHEERAN:
                    source = context.getString(R.string.nakkheeran);
                    publisherLogo = R.drawable.nakkheeran;

                    break;

            }


            if (isRead) {

                titleText.setTextColor(context.getResources().
                        getColor(android.R.color.secondary_text_dark));
                publisher.setTextColor(context.getResources().
                        getColor(android.R.color.secondary_text_dark));
                timeText.setTextColor(context.getResources().
                        getColor(android.R.color.secondary_text_dark));
                Glide.with(context).load(publisherLogo).
                        placeholder(R.color.lighter_gray).
                        bitmapTransform(grayScaleTransformation)
                        .into(mpublisherLogo);
                if (imgSrc != null && !imgSrc.isEmpty()) {
                    thumbnail.setVisibility(View.VISIBLE);

                    Glide.with(context).load(imgSrc).
                            placeholder(R.color.lighter_gray).
                            bitmapTransform(grayScaleTransformation)
                            .into(thumbnail);
                } else {
                    thumbnail.setVisibility(View.GONE);
                }
            } else {
                titleText.setTextColor(context.getResources().
                        getColor(android.R.color.primary_text_light));
                publisher.setTextColor(context.getResources().
                        getColor(android.R.color.primary_text_light));
                timeText.setTextColor(context.getResources().
                        getColor(android.R.color.primary_text_light));
                Glide.with(context).load(publisherLogo).
                        placeholder(R.color.lighter_gray)
                        .into(mpublisherLogo);
                if (imgSrc != null && !imgSrc.isEmpty()) {
                    thumbnail.setVisibility(View.VISIBLE);
                    Glide.with(context).load(imgSrc).placeholder(R.color.lighter_gray)
                            .into(thumbnail);
                } else {
                    thumbnail.setVisibility(View.GONE);
                }

            }
            titleText.setText(title.trim());

            timeText.setText(TimeUtils.formatShortDate(pubDate));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, feed, pos);
                }
            });
            publisher.setText(source);


        }
    }
}
