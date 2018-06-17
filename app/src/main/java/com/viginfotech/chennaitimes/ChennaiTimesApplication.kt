package com.viginfotech.chennaitimes

import android.app.Application
import android.content.Context

import com.google.android.gms.ads.MobileAds

/**
 * Created by anand on 2/5/16.
 */
class ChennaiTimesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id_prod))
        context = this
    }

    companion object {
        private var context: ChennaiTimesApplication? = null

        fun getContext(): Context? {
            return context
        }
    }


}
