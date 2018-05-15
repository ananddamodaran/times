package com.viginfotech.chennaitimes.util

import android.content.Context
import android.util.Log
import com.google.api.client.util.DateTime
import java.util.*


/**
 * Created by anand on 12/22/15.
 */
object TimeUtils {
    private val SECOND = 1000
    val MINUTE = 60 * SECOND
    private val HOUR = 60 * MINUTE
    private val DAY = 24 * HOUR
    val TAG="TimeUtils"

    //return new Date(now -(6*ONE_HOUR_IN_MILLIS)).getTime();
    val last6HoursInMills: Long
        get() {
            val now = System.currentTimeMillis()

            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone("IST")
            calendar.add(Calendar.HOUR_OF_DAY, -6)

            Log.d(TAG,"time " + calendar.time)
            return calendar.timeInMillis
        }

    fun getTimeAgo(time: Long): String? {
        var time = time
        // TODO: use DateUtils methods instead
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }

        val diff = now - time
        return if (diff < MINUTE) {
            " just now"
        } else if (diff < 2 * MINUTE) {
            "1 minute ago"
        } else if (diff < 50 * MINUTE) {
            (diff / MINUTE).toString() + " minutes ago"
        } else if (diff < 90 * MINUTE) {
            "1 hour ago"
        } else if (diff < 24 * HOUR) {
            (diff / HOUR).toString() + " hours ago"
        } else if (diff < 48 * HOUR) {
            "yesterday"
        } else {
            (diff / DAY).toString() + " days ago"
        }
    }

    fun formatShortDate(timeInMillis: Long): String? {
        return getTimeAgo(timeInMillis)
    }

    fun formatHumanFriendlyShortDate(context: Context, timestamp: Long): String {
        val localTimestamp: Long
        val localTime: Long
        val now = System.currentTimeMillis()

        val tz = TimeZone.getDefault()
        localTimestamp = timestamp + tz.getOffset(timestamp)
        localTime = now + tz.getOffset(now)

        val dayOrd = localTimestamp / 86400000L
        val nowOrd = localTime / 86400000L

        return if (dayOrd == nowOrd) {
            "Today"
        } else if (dayOrd == nowOrd - 1) {
            "yesterday"
        } else if (dayOrd == nowOrd + 1) {
            "tomorrow"
        } else {
            formatShortTime(context, Date(timestamp))
        }
    }

    fun formatShortTime(context: Context, time: Date): String {
        // Android DateFormatter will honor the user's current settings.
        val format = android.text.format.DateFormat.getTimeFormat(context)
        // Override with Timezone based on settings since users can override their phone's timezone
        // with Pacific time zones.
        val tz = TimeZone.getTimeZone("IST")
        if (tz != null) {
            format.timeZone = tz
        }
        return format.format(time)
    }

    fun dateTimeToMillis(dateTime: DateTime): Long {
        return 0L
    }
}
