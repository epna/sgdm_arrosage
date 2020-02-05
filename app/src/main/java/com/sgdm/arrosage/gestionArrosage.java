package com.sgdm.arrosage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class gestionArrosage extends BroadcastReceiver {

    private static final String KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID";
    private static final String KEY_REPLY = "KEY_REPLY";
    private static final String REPLY_ACTION = "REPLY_ACTION";
    private static final String CHANNEL_ID = "CHANNEL_ID";



    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (REPLY_ACTION.equals(intent.getAction())) {
            CharSequence message = getMessageText(intent);
            int notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, 0);

            Toast.makeText(context, "Message: " + message, Toast.LENGTH_SHORT).show();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText("Content sent");

            notificationManager.notify(notificationId, builder.build());
        }
    }
    public void Echange_centrale(String MyURL) {
        final OkHttpClient client = new OkHttpClient ();
        Request request = new Request.Builder ()
                .url ( MyURL )
                .get ()
                .build ();
        client.newCall ( request ).enqueue ( new Callback () {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage ();
                Log.w (MainActivity.TAG, mMessage );
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body ().string ();
                if (response.isSuccessful ()) {
                    try {
                        Log.v ( MainActivity.TAG, mMessage );
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                }
            }
        } );
    }
}

