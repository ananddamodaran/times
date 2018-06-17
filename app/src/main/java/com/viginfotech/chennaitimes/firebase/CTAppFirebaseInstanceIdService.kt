package com.viginfotech.chennaitimes.firebase

import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Created by anand on 9/11/16.
 */
class CTAppFirebaseInstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {

        val token = FirebaseInstanceId.getInstance().token
        Log.i(TAG, "onTokenRefresh: " + token!!)
        FirebaseMessaging.getInstance()
                .subscribeToTopic(FRIENDLY_ENGAGE_TOPIC)
    }

    companion object {

        private val TAG = CTAppFirebaseInstanceIdService::class.java.simpleName
        private val FRIENDLY_ENGAGE_TOPIC = "today"
    }
}
