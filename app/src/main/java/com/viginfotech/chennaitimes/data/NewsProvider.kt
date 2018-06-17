package com.viginfotech.chennaitimes.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

/**
 * Created by anand on 9/25/15.
 */
class NewsProvider : ContentProvider() {
    private var mOpenHelper: NewsDbHelper? = null
    override fun onCreate(): Boolean {
        mOpenHelper = NewsDbHelper(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val retCursor: Cursor
        when (sUriMatcher.match(uri)) {

            FEED -> {
                retCursor = mOpenHelper!!.readableDatabase.query(
                        NewsContract.NewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null,
                        sortOrder
                )
            }


            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        retCursor.setNotificationUri(context!!.contentResolver, uri)
        return retCursor
    }

    override fun getType(uri: Uri): String? {
        val match = sUriMatcher.match(uri)

        when (match) {
            FEED -> return NewsContract.NewsEntry.CONTENT_TYPE

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = mOpenHelper!!.writableDatabase
        val match = sUriMatcher.match(uri)
        val returnUri: Uri

        when (match) {
            FEED -> {
                val _id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, values)
                if (_id > 0)
                    returnUri = NewsContract.NewsEntry.buildFeedUri(_id)
                else
                    throw android.database.SQLException("Failed to insert row into $uri")
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mOpenHelper!!.writableDatabase
        val match = sUriMatcher.match(uri)
        val rowsDeleted: Int
        when (match) {
            FEED -> rowsDeleted = db.delete(
                    NewsContract.NewsEntry.TABLE_NAME, selection, selectionArgs)

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mOpenHelper!!.writableDatabase
        val match = sUriMatcher.match(uri)
        val rowsUpdated: Int

        when (match) {
            FEED -> rowsUpdated = db.update(NewsContract.NewsEntry.TABLE_NAME, values, selection,
                    selectionArgs)

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        if (rowsUpdated != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsUpdated

    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val db = mOpenHelper!!.writableDatabase
        val match = sUriMatcher.match(uri)
        when (match) {
            FEED -> {
                db.beginTransaction()
                var returnCount = 0
                try {
                    for (value in values) {
                        val _id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, value)
                        if (_id != -1L) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context!!.contentResolver.notifyChange(uri, null)
                return returnCount
            }
            else -> return super.bulkInsert(uri, values)
        }
    }

    companion object {

        private val sUriMatcher = buildUriMatcher()

        private val FEED = 100

        private fun buildUriMatcher(): UriMatcher {

            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = NewsContract.CONTENT_AUTHORITY

            // For each type of URI you want to add, create a corresponding code.
            matcher.addURI(authority, NewsContract.PATH_DB, FEED)


            return matcher
        }
    }


}
