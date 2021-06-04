package com.example.mytravel;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class OfflineBroadcastReceiver extends BroadcastReceiver {
    public OfflineBroadcastReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if(!Utils.checkConnection(context))
        {
            Intent offline = new Intent(context, OfflineActivity.class);
            context.startActivity(offline);
        }
    }
}