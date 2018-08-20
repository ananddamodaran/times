package com.viginfotech.chennaitimes.data

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns
import com.viginfotech.chennaitimes.BuildConfig

/**
 * Created by anand on 9/25/15.
 */
object NewsContract {

    val CONTENT_AUTHORITY = if(BuildConfig.DEBUG)
        "com.viginfotech.chennaitimes.app.debug"
            else "com.viginfotech.chennaitimes.app"
    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

    val PATH_DB = "newsapp"

    class NewsEntry : BaseColumns {
        companion object {
            val _ID = "_id"
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DB).build()

            val CONTENT_TYPE =
                    "vnd.android.cursor.dir/$CONTENT_AUTHORITY/$PATH_DB"
            val CONTENT_ITEM_TYPE =
                    "vnd.android.cursor.item/$CONTENT_AUTHORITY/$PATH_DB"

            val TABLE_NAME = "feed"

            val COLUMN_FEED_TITLE = "title"
            val COLUMN_FEED_DETAILED_TITLE = "detailedTitle"
            val COLUMN_FEED_SUMMARY = "summary"
            val COLUMN_FEED_PUBDATE = "pubDate"
            val COLUMN_FEED_LINK = "guid"
            val COLUMN_FEED_THUMBNAIL = "thumbnail"
            val COLUMN_FEED_IMAGE = "image"
            val COLUMN_FEED_DETAILED_NEWS = "detailNews"
            val COLUMN_FEED_CATEGORY = "category"
            val COLUMN_FEED_SOURCE = "source"
            val COLUMN_READ_STATE = "readState"

            fun buildFeedUri(id: Long): Uri {
                return ContentUris.withAppendedId(CONTENT_URI, id)
            }
        }

    }
}
