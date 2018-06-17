package com.viginfotech.chennaitimes.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.viginfotech.chennaitimes.R
import com.viginfotech.chennaitimes.ui.HomeActivity

/**
 * Created by anand on 9/11/16.
 */
class CTAppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

    }

    private fun old(remoteMessage: RemoteMessage) {
        // try {
        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title
            val message = remoteMessage.notification!!.body
            // Uri uri=remoteMessage.getNotification().;
            val click_action = remoteMessage.notification!!.clickAction
            if (remoteMessage.data.size > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.data)
                // Toast.makeText(this, "mesage received", Toast.LENGTH_SHORT).show();

                if (/* Check if data needs to be processed by long running job */ true) {
                    // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                    // scheduleJob();
                    sendNotification(title, message)
                } else {
                    // Handle message within 10 seconds
                    //handleNow();
                    Log.d(TAG, "Message data payload:null ")

                }

            }
            /* Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
          //  Bitmap remote_picture = BitmapFactory.decodeStream((InputStream) new URL(iconurl).getContent());


            Intent intent = new Intent(click_action);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            URL url= null;
            Bitmap aBigBitmap=null;

                url = new URL("http://img.dinamalar.com/data/thumbnew/Tamil_News_thumb_1797349_150_100.jpg");
              aBigBitmap  = BitmapFactory.decodeStream(url.openConnection().getInputStream());



            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_ct)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(aBigBitmap))
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());
*/
        }
        /* } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private fun sendNotification(messageTitle: String?, messageBody: String?) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pattern = longArrayOf(500, 500, 500, 500, 500)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ct)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.BLUE, 1, 1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent) as NotificationCompat.Builder

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {

        private val TAG = CTAppFirebaseMessagingService::class.java.simpleName
    }
}
