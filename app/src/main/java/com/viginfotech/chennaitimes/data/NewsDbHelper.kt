package com.viginfotech.chennaitimes.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.viginfotech.chennaitimes.data.NewsContract.NewsEntry

/**
 * Created by anand on 9/25/15.
 */
class NewsDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_DINAMALAR_TABLE = "CREATE TABLE " + NewsEntry.TABLE_NAME + " (" +
                NewsEntry._ID + " INTEGER PRIMARY KEY," +
                NewsEntry.COLUMN_FEED_TITLE + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_FEED_DETAILED_TITLE + " TEXT, " +
                NewsEntry.COLUMN_FEED_SUMMARY + " TEXT, " +
                NewsEntry.COLUMN_FEED_PUBDATE + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_FEED_LINK + " TEXT UNIQUE NOT NULL, " +
                NewsEntry.COLUMN_FEED_THUMBNAIL + " TEXT, " +
                NewsEntry.COLUMN_FEED_IMAGE + " TEXT, " +
                NewsEntry.COLUMN_FEED_DETAILED_NEWS + " TEXT, " +
                NewsEntry.COLUMN_FEED_CATEGORY + " INTEGER NOT NULL, " +
                NewsEntry.COLUMN_FEED_SOURCE + " INTEGER NOT NULL, " +
                NewsEntry.COLUMN_READ_STATE + " INTEGER NOT NULL, " +
                "UNIQUE (" + NewsEntry.COLUMN_FEED_LINK + ") ON CONFLICT IGNORE" +
                " );"

        db.execSQL(SQL_CREATE_DINAMALAR_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME)
        onCreate(db)
    }

    companion object {
        private val DATABASE_VERSION = 1

        val DATABASE_NAME = "news.db"
    }
}
