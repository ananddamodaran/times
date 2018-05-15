package com.viginfotech.chennaitimes.util

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by anand on 6/11/16.
 */
object PrefUtils {

    private val PREF_LAST_SEEN_CATEGORY = "last_seen_category"

    fun setLastSeenCategory(context: Context, category: Int) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putInt(PREF_LAST_SEEN_CATEGORY, category).apply()
    }

    fun getLastSeenCategory(context: Context): Int {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getInt(PREF_LAST_SEEN_CATEGORY, 0)
    }
}
