package com.viginfotech.chennaitimes.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by anand on 3/10/16.
 */
object NetworkUtil {

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnectedOrConnecting

    }
}
