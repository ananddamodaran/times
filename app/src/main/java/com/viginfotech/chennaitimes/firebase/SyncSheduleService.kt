package com.viginfotech.chennaitimes.firebase

import android.content.Intent
import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.viginfotech.chennaitimes.Config
import com.viginfotech.chennaitimes.data.NewsContract
import com.viginfotech.chennaitimes.service.ChennaiTimeAPIBuilder
import com.viginfotech.chennaitimes.sync.TriggerRefresh


/**
 * Created by anand on 6/11/17.
 */

class SyncSheduleService : JobService() {

    internal var TAG = "SyncScheduleService"
     val myApiService = ChennaiTimeAPIBuilder.buildChennaiTimesService();

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Log.i(TAG, "onStartJob: ")
        if (jobParameters.tag == Config.SYNCSCHEDULE_TAG)
            syncAll()
        else if (jobParameters.tag == Config.TRUNCATE_TAG) {
            contentResolver.delete(NewsContract.NewsEntry.CONTENT_URI, null, null)
        }
        return false
    }

    private fun syncAll() {
        startService(Intent(this, TriggerRefresh::class.java).putExtra("category", 0))
        startService(Intent(this, TriggerRefresh::class.java).putExtra("category", 1))
        startService(Intent(this, TriggerRefresh::class.java).putExtra("category", 2))
        startService(Intent(this, TriggerRefresh::class.java).putExtra("category", 3))
        startService(Intent(this, TriggerRefresh::class.java).putExtra("category", 4))
        startService(Intent(this, TriggerRefresh::class.java).putExtra("category", 5))
        startService(Intent(this, TriggerRefresh::class.java).putExtra("category", 6))

    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }
}
