package com.viginfotech.chennaitimes.firebase;

import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.viginfotech.chennaiTimesApi.ChennaiTimesApi;
import com.viginfotech.chennaitimes.Config;
import com.viginfotech.chennaitimes.data.NewsContract;
import com.viginfotech.chennaitimes.sync.TriggerRefresh;
import com.viginfotech.chennaitimes.util.CloudEndpointBuilderHelper;
import com.viginfotech.chennaitimes.util.NetworkUtil;




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
        if(jobParameters.getTag().equals(Config.SYNCSCHEDULE_TAG))
        syncAll();
        else if(jobParameters.getTag().equals(Config.TRUNCATE_TAG)){
            getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI, null,null);
        }
        return false;
    }

    private void syncAll() {
        if(!NetworkUtil.isOnline(this)) return;
       startService(new Intent(this, TriggerRefresh.class).putExtra("category",0).setAction("autosync"));
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
