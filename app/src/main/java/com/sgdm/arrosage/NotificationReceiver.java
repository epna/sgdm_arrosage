package com.sgdm.arrosage;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;


public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)  {

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        if (remoteInput != null) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "channel_id")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                    .setContentTitle("You have approved the Request");
            NotificationManager notificationManager = (NotificationManager) context.
                    getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(200, mBuilder.build());
        }

    }


}

