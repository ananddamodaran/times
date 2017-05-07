package com.viginfotech.chennaitimes.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by anand on 9/11/16.
 */
public class CTAppFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = CTAppFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    }
}
