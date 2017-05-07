package com.viginfotech.chennaitimes.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by anand on 9/11/16.
 */
public class CTAppFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = CTAppFirebaseInstanceIdService.class.getSimpleName();
    private static final String FRIENDLY_ENGAGE_TOPIC = "today";

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();

        FirebaseMessaging.getInstance()
                .subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);
    }
}
