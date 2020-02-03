package com.sgdm.arrosage;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import static android.R.drawable.ic_delete;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String NOTIFICATION_REPLY = "NotificationReply";
    public static final int NOTIFICATION_ID = 200;
    public static final int REQUEST_CODE_APPROVE = 101;
    public static final String KEY_INTENT_APPROVE = "keyintentaccept";



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        sendNotification(notification, data);
    }
    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        PendingIntent approvePendingIntent = PendingIntent.getBroadcast(
                this,
                REQUEST_CODE_APPROVE,
                new Intent(this, NotificationReceiver.class)
                        .putExtra(KEY_INTENT_APPROVE, REQUEST_CODE_APPROVE),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        RemoteInput remoteInput = new RemoteInput.Builder((NOTIFICATION_REPLY))
                .setLabel("Approve Comments")
                .build();
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(ic_delete,
                        "Approve", approvePendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "sgdm_arrosage")
                .setContentTitle("Titre Olivicer")
                .setContentText("Body Olivier")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(approvePendingIntent)
                .setContentInfo("Titre Olivicer")
                //.setLargeIcon(icon)
                //.setColor(Color.GRAY)
                //.setLights(Color.GRAY, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                //.setSmallIcon(R.mipmap.ic_launcher)
                .addAction(action);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}





