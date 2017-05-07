package com.viginfotech.chennaitimes.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by anand on 3/10/16.
 */
public class NetworkUtil {

    public static boolean isOnline(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
       return connectivityManager.getActiveNetworkInfo() != null &&
               connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();

    }
}
