package com.sgdm.arrosage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int mArroseur = intent.getIntExtra("arroseur", 0);
        Log.d ( "ooo", "oliviieriitre" );
        Echange_centrale (( context.getResources ().getString ( R.string.IP_arduino )+"/?arro="+ mArroseur +"false") );
        throw new UnsupportedOperationException ( "Not yet implemented" );
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
                Log.w ( MainActivity.TAG, mMessage );
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