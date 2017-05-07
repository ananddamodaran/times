package com.viginfotech.chennaitimes.util;

import android.content.Context;

import com.google.api.client.util.DateTime;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by anand on 12/22/15.
 */
public class TimeUtils {
    private static final int SECOND = 1000;
    public static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static String getTimeAgo(long time) {
        // TODO: use DateUtils methods instead
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE) {
            return " just now";
        } else if (diff < 2 * MINUTE) {
            return "1 minute ago";
        } else if (diff < 50 * MINUTE) {
            return diff / MINUTE + " minutes ago";
        } else if (diff < 90 * MINUTE) {
            return "1 hour ago";
        } else if (diff < 24 * HOUR) {
            return diff / HOUR + " hours ago";
        } else if (diff < 48 * HOUR) {
            return "yesterday";
        } else {
            return diff / DAY + " days ago";
        }
    }

    public static String formatShortDate(long timeInMillis) {
            return getTimeAgo(timeInMillis);
    }

    public static String formatHumanFriendlyShortDate(Context context, long timestamp) {
        long localTimestamp, localTime;
        long now = System.currentTimeMillis();

        TimeZone tz = TimeZone.getDefault();
        localTimestamp = timestamp + tz.getOffset(timestamp);
        localTime = now + tz.getOffset(now);

        long dayOrd = localTimestamp / 86400000L;
        long nowOrd = localTime / 86400000L;

        if (dayOrd == nowOrd) {
            return "Today";
        } else if (dayOrd == nowOrd - 1) {
            return "yesterday";
        } else if (dayOrd == nowOrd + 1) {
            return "tomorrow";
        } else {
            return formatShortTime(context, new Date(timestamp));
        }
    }

    public static String formatShortTime(Context context, Date time) {
        // Android DateFormatter will honor the user's current settings.
        DateFormat format = android.text.format.DateFormat.getTimeFormat(context);
        // Override with Timezone based on settings since users can override their phone's timezone
        // with Pacific time zones.
        TimeZone tz = TimeZone.getTimeZone("IST");
        if (tz != null) {
            format.setTimeZone(tz);
        }
        return format.format(time);
    }

    public static long dateTimeToMillis(DateTime dateTime) {
        return 0L;
    }

    public static long getLast6HoursInMills() {
        long now = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("IST"));
        calendar.add(Calendar.HOUR_OF_DAY,-6);

        System.out.println("time "+calendar.getTime());
        //return new Date(now -(6*ONE_HOUR_IN_MILLIS)).getTime();
        return calendar.getTimeInMillis();
    }
}
