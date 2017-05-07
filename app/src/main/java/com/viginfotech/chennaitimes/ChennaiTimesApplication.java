package com.viginfotech.chennaitimes;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;

/**
 * Created by anand on 2/5/16.
 */
public class ChennaiTimesApplication extends Application {
    private static ChennaiTimesApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id_dev));
        context=this;




    }

    public static Context getContext(){return context;}


}
