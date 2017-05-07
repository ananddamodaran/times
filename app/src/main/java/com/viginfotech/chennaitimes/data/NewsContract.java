package com.viginfotech.chennaitimes.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by anand on 9/25/15.
 */
public class NewsContract {

    public static final String CONTENT_AUTHORITY="com.chennai.times.app";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_DB="newsapp";

    public static final class NewsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DB).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_DB;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_DB;

        public static final String TABLE_NAME="feed";

        public static final String COLUMN_FEED_TITLE="title";
        public static final String COLUMN_FEED_DETAILED_TITLE="detailedTitle";
        public static final String COLUMN_FEED_SUMMARY="summary";
        public static final String COLUMN_FEED_PUBDATE="pubDate";
        public static final String COLUMN_FEED_LINK="guid";
        public static final String COLUMN_FEED_THUMBNAIL="thumbnail";
        public static final String COLUMN_FEED_IMAGE="image";
        public static final String COLUMN_FEED_DETAILED_NEWS="detailNews";
        public static final String COLUMN_FEED_CATEGORY="category";
        public static final String COLUMN_FEED_SOURCE="source";
        public static final String COLUMN_READ_STATE="readState";

        public static Uri buildFeedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
