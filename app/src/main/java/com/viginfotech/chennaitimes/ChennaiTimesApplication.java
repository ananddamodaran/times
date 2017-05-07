package com.viginfotech.chennaitimes;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;

import io.fabric.sdk.android.Fabric;

/**
 * Created by anand on 2/5/16.
 */
public class ChennaiTimesApplication extends Application {
    private static ChennaiTimesApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id_prod));

        context=this;




    }

    public static Context getContext(){return context;}


}
