package com.viginfotech.chennaitimes.util

import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.viginfotech.chennaitimes.Constants
import com.viginfotech.chennaitimes.Constants.*
import com.viginfotech.chennaitimes.R


/**
 * Created by anand on 2/7/16.
 */
object DisplayUtil {

    fun getAppTitle(context: Context, itemSelected: Int): String? {

        when (itemSelected) {
            CATEGORY_HEADLINES -> return context.getString(R.string.action_headlines)
            CATEGORY_TAMILNADU -> return context.getString(R.string.action_tamilnadu)
            CATEGORY_INDIA -> return context.getString(R.string.action_india)
            CATEGORY_WORLD -> return context.getString(R.string.action_world)
            CATEGORY_BUSINESS -> return context.getString(R.string.action_business)
            CATEGORY_SPORTS -> return context.getString(R.string.action_sports)
            CATEGORY_CINEMA -> return context.getString(R.string.action_cinema)
            else -> return null
        }
    }

    fun getScreenTitle(context: Context, itemSelected: Int): String? {

        when (itemSelected) {
            CATEGORY_HEADLINES -> return context.getString(R.string.headlines_text)
            CATEGORY_TAMILNADU -> return context.getString(R.string.tamilnadu_text)
            CATEGORY_INDIA -> return context.getString(R.string.india_text)
            CATEGORY_WORLD -> return context.getString(R.string.world_text)
            CATEGORY_BUSINESS -> return context.getString(R.string.business_text)
            CATEGORY_SPORTS -> return context.getString(R.string.sports_text)
            CATEGORY_CINEMA -> return context.getString(R.string.cinema_text)
            else -> return null
        }
    }

    fun getPublisherLogo(context: Context, source: Int): Drawable? {
        when (source) {
            Constants.SOURCE_DINAKARAN -> return context.resources.getDrawable(R.drawable.dinakaran)
            Constants.SOURCE_DINAMALAR -> return context.resources.getDrawable(R.drawable.dinamalar)
            Constants.SOURCE_BBCTAMIL -> return context.resources.getDrawable(R.drawable.bbc)
            Constants.SOURCE_DINAMANI -> return context.resources.getDrawable(R.drawable.dinamani)

            Constants.SOURCE_ONEINDIA -> return context.resources.getDrawable(R.drawable.oneindia)
            Constants.SOURCE_NAKKHEERAN -> return context.resources.getDrawable(R.drawable.nakkheeran)
            else -> return null
        }

    }

    fun getPublisherName(context: Context, source: Int): String? {
        when (source) {
            Constants.SOURCE_DINAKARAN -> return context.resources.getString(R.string.dinakaran)
            Constants.SOURCE_DINAMALAR -> return context.resources.getString(R.string.dinamalar)
            Constants.SOURCE_BBCTAMIL -> return context.resources.getString(R.string.bbctamil)
            Constants.SOURCE_DINAMANI -> return context.resources.getString(R.string.dinamani)

            Constants.SOURCE_ONEINDIA -> return context.resources.getString(R.string.oneindia)
            Constants.SOURCE_NAKKHEERAN -> return context.resources.getString(R.string.nakkheeran)
            else -> return null
        }

    }

    fun displayNotification(message: String, context: Context) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context)

                .setSmallIcon(R.drawable.cheenaitimes_notify)
                .setColor(context.resources.getColor(R.color.colorAccent))
                .setContentTitle("சென்னை டைம்ஸ்")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())


    }


}
