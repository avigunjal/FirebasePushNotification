package com.firebasepushnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.firebasepushnotification.MainActivity.address;

/**
 * Created by Dreamers_soft on 5/17/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Context context = MyFirebaseMessagingService.this;
    public static String latitude = "";
    public static String longitude = "";
    public static final String pref_noti_data = "notification_data";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (remoteMessage.getData().size() > 0) {
            Log.d("Message data payload: ", remoteMessage.getData().toString());
            latitude = remoteMessage.getData().get("latitude");
            longitude = remoteMessage.getData().get("longitude");
            SharedPreferences sharedPreferences = getSharedPreferences(pref_noti_data,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
           if(latitude!="" && longitude!="") {
               editor.putString("latitude_key", latitude);
               editor.putString("longitude_key", longitude);
           }
           editor.apply();
        }

        String uri = String.format(Locale.ENGLISH, "geo:%s,%s?z=15&q=%s,%s(%s)",latitude,longitude,latitude,longitude,address);
        Uri gmmIntentUri = Uri.parse(uri);
        Intent intentMap = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
        intentMap.setPackage("com.google.android.apps.maps");
        intentMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //pendingintent for opening another application from Notification
        int num = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, num, intentMap, PendingIntent.FLAG_UPDATE_CURRENT);


        //Notification code
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.security)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .addAction(R.drawable.map, "Open map",
                        pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }

    }


