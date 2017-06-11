package com.viginfotech.chennaitimes.firebase;

import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.viginfotech.chennaiTimesApi.ChennaiTimesApi;
import com.viginfotech.chennaitimes.sync.TriggerRefresh;
import com.viginfotech.chennaitimes.util.CloudEndpointBuilderHelper;


/**
 * Created by anand on 6/11/17.
 */

public class SyncSheduleService extends JobService {

    String TAG="SyncScheduleService";
    private ChennaiTimesApi myApiService;

    @Override
    public void onCreate() {
        super.onCreate();
        if (myApiService == null) {
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "onStartJob: ");
        syncAll();
        return false;
    }

    private void syncAll() {
       startService(new Intent(this, TriggerRefresh.class).putExtra("category",0));
       startService(new Intent(this, TriggerRefresh.class).putExtra("category",1));
       startService(new Intent(this, TriggerRefresh.class).putExtra("category",2));
       startService(new Intent(this, TriggerRefresh.class).putExtra("category",3));
       startService(new Intent(this, TriggerRefresh.class).putExtra("category",4));
       startService(new Intent(this, TriggerRefresh.class).putExtra("category",5));
       startService(new Intent(this, TriggerRefresh.class).putExtra("category",6));

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
