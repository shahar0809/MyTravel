package com.example.mytravel;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import androidx.core.app.NotificationCompat;

public class BackgroundOfflineReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if(Utils.checkConnection(context))
        {
            //PendingIntent pIntent = new PendingIntent(context, MainActivity.class);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MyTravel")
                    // Set Icon
                    .setSmallIcon(R.drawable.logo_only_transperent)
                    // Set Ticker Message
                    .setTicker("MyTravel")
                    // Set Title
                    .setContentTitle("You're Online again!")
                    // Set Text
                    .setContentText("Check out what you've missed")
                    // Add an Action Button below Notification
                    //.addAction(R.drawable.ic_launcher, "Action Button", pIntent)
                    // Set PendingIntent into Notification
                    //.setContentIntent(pIntent)
                    // Dismiss Notification
                    .setAutoCancel(true);

            NotificationManager notificationmanager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            // Build Notification with Notification Manager
            notificationmanager.notify(0, builder.build());
        }
    }
}
