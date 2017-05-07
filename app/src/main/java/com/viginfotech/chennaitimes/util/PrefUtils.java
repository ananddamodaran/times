package com.viginfotech.chennaitimes.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by anand on 6/11/16.
 */
public class PrefUtils {

   private static final String PREF_LAST_SEEN_CATEGORY = "last_seen_category";

   public static void setLastSeenCategory(final Context context, int category){
      SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
      sp.edit().putInt(PREF_LAST_SEEN_CATEGORY, category).apply();
   }

   public static int getLastSeenCategory(final Context context){
      SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
      return sp.getInt(PREF_LAST_SEEN_CATEGORY,0);
   }
}
