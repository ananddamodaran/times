package com.viginfotech.chennaitimes.util;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.androidnanban.chennaitime.Constants;
import com.androidnanban.chennaitime.R;

import static com.androidnanban.chennaitime.Constants.CATEGORY_BUSINESS;
import static com.androidnanban.chennaitime.Constants.CATEGORY_CINEMA;
import static com.androidnanban.chennaitime.Constants.CATEGORY_HEADLINES;
import static com.androidnanban.chennaitime.Constants.CATEGORY_INDIA;
import static com.androidnanban.chennaitime.Constants.CATEGORY_SPORTS;
import static com.androidnanban.chennaitime.Constants.CATEGORY_TAMILNADU;
import static com.androidnanban.chennaitime.Constants.CATEGORY_WORLD;


/**
 * Created by anand on 2/7/16.
 */
public class DisplayUtil {

    public static String getAppTitle(Context context, int itemSelected) {

        switch (itemSelected){
            case CATEGORY_HEADLINES: return context.getString(R.string.action_headlines);
            case CATEGORY_TAMILNADU: return context.getString(R.string.action_tamilnadu);
            case CATEGORY_INDIA: return context.getString(R.string.action_india);
            case CATEGORY_WORLD: return context.getString(R.string.action_world);
            case CATEGORY_BUSINESS: return context.getString(R.string.action_business);
            case CATEGORY_SPORTS: return context.getString(R.string.action_sports);
            case CATEGORY_CINEMA: return context.getString(R.string.action_cinema);
            default: return null;
        }
    }
    public static String getScreenTitle(Context context, int itemSelected) {

        switch (itemSelected){
            case CATEGORY_HEADLINES: return context.getString(R.string.headlines_text);
            case CATEGORY_TAMILNADU: return context.getString(R.string.tamilnadu_text);
            case CATEGORY_INDIA: return context.getString(R.string.india_text);
            case CATEGORY_WORLD: return context.getString(R.string.world_text);
            case CATEGORY_BUSINESS: return context.getString(R.string.business_text);
            case CATEGORY_SPORTS: return context.getString(R.string.sports_text);
            case CATEGORY_CINEMA: return context.getString(R.string.cinema_text);
            default: return null;
        }
    }
    public static Drawable getPublisherLogo(Context context, int source) {
        switch (source) {
            case Constants.SOURCE_DINAKARAN:
               return context.getResources().getDrawable(R.drawable.dinakaran);
            case Constants.SOURCE_DINAMALAR:
                return context.getResources().getDrawable( R.drawable.dinamalar);
            case Constants.SOURCE_BBCTAMIL:
                return context.getResources().getDrawable(R.drawable.bbc);
            case Constants.SOURCE_DINAMANI:
                return context.getResources().getDrawable(R.drawable.dinamani);

            case Constants.SOURCE_ONEINDIA:
                return context.getResources().getDrawable(R.drawable.oneindia);
            case Constants.SOURCE_NAKKHEERAN:
                return context.getResources().getDrawable(R.drawable.nakkheeran);
            default:return null;
        }

    }
    public static String getPublisherName(Context context, int source) {
        switch (source) {
            case Constants.SOURCE_DINAKARAN:
               return context.getResources().getString(R.string.dinakaran);
            case Constants.SOURCE_DINAMALAR:
                return context.getResources().getString( R.string.dinamalar);
            case Constants.SOURCE_BBCTAMIL:
                return context.getResources().getString(R.string.bbctamil);
            case Constants.SOURCE_DINAMANI:
                return context.getResources().getString(R.string.dinamani);

            case Constants.SOURCE_ONEINDIA:
                return context.getResources().getString(R.string.oneindia);
            case Constants.SOURCE_NAKKHEERAN:
                return context.getResources().getString(R.string.nakkheeran);
            default:return null;
        }

    }
    public static void displayNotification(String message, Context context){
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)

                .setSmallIcon(R.drawable.cheenaitimes_notify)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setContentTitle("சென்னை டைம்ஸ்")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());


    }


}
