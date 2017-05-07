package com.viginfotech.chennaitimes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.viginfotech.chennaitimes.data.NewsContract.NewsEntry;

/**
 * Created by anand on 9/25/15.
 */
public class NewsDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "news.db";

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DINAMALAR_TABLE = "CREATE TABLE " + NewsEntry.TABLE_NAME + " (" +
                NewsEntry._ID + " INTEGER PRIMARY KEY," +
                NewsEntry.COLUMN_FEED_TITLE + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_FEED_DETAILED_TITLE + " TEXT, " +
                NewsEntry.COLUMN_FEED_SUMMARY + " TEXT, " +
                NewsEntry.COLUMN_FEED_PUBDATE + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_FEED_LINK + " TEXT UNIQUE NOT NULL, " +
                NewsEntry.COLUMN_FEED_THUMBNAIL + " TEXT, " +
                NewsEntry.COLUMN_FEED_IMAGE + " TEXT, " +
                NewsEntry. COLUMN_FEED_DETAILED_NEWS+ " TEXT, " +
                NewsEntry.COLUMN_FEED_CATEGORY + " INTEGER NOT NULL, " +
                NewsEntry.COLUMN_FEED_SOURCE + " INTEGER NOT NULL, "+
                NewsEntry.COLUMN_READ_STATE+" INTEGER NOT NULL, "+
                 "UNIQUE (" + NewsEntry.COLUMN_FEED_LINK +") ON CONFLICT IGNORE"+
                " );";

        db.execSQL(SQL_CREATE_DINAMALAR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME);
        onCreate(db);
    }
}
