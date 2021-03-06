package com.sgdm.arrosage;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String NOTIFICATION_REPLY = "NotificationReply";
    public static final int NOTIFICATION_ID = 200;
    public static final int REQUEST_CODE_APPROVE = 101;
    public static final String KEY_INTENT_APPROVE = "keyintentaccept";
    public  String mMessage;
    public static final String TAG = "== Arrosage == ";
    public  String[] messLibarro = new String[5];
    public Map<String, String> data;
    public Boolean fini= false, mAction, mPhoto;
    public Integer requestcode=0;
    public Integer mType;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        data = remoteMessage.getData();
        retrieve_global_data() ;
        while (!fini)  {};
        fini=false;
        retrievemessage();
        while (!fini)  {};
        sendNotification2 ( Integer.parseInt ( data.get ( "arroseur" ).toString ()));
    }





    public void sendNotification2(Integer mArroseur ) {
        Intent snoozeIntent = new Intent(this, MyReceiver.class);
        snoozeIntent.setAction(Intent.ACTION_SEND);
        snoozeIntent.putExtra("arroseur", mArroseur);
        requestcode= requestcode+1;
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_watering3)
                .setContentTitle(mMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        /// Cas psécial de l'action sur la notification
        if (  mArroseur > -1) notifBuilder.setContentText(messLibarro[ ( mArroseur)]);
        if (mAction) notifBuilder.addAction(R.drawable.ic_stop2,"Arrêter maintenant",snoozePendingIntent);
        if (mPhoto) {
            Bitmap bitmap_large=null;
            if (mArroseur==0)  bitmap_large=BitmapFactory.decodeResource(this.getResources(),R.drawable.arro1);
            if (mArroseur==1)  bitmap_large=BitmapFactory.decodeResource(this.getResources(),R.drawable.arro2);
            if (mArroseur==2)  bitmap_large=BitmapFactory.decodeResource(this.getResources(),R.drawable.arro3);
            if (mArroseur==3)  bitmap_large=BitmapFactory.decodeResource(this.getResources(),R.drawable.arro4);
            Drawable d = new BitmapDrawable (getResources(), bitmap_large);

            notifBuilder.setLargeIcon(bitmap_large )
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap_large)
                            .bigLargeIcon(null));
        }
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, notifBuilder.build());
    }


    public void retrieve_global_data() {
        FirebaseDatabase database = FirebaseDatabase.getInstance ();
        DatabaseReference myRef = database.getReference ( "arrosage" );

        myRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int ii = 0;
                for (DataSnapshot chidSnap : dataSnapshot.child ( "libelle" ).getChildren ()) {

                    messLibarro[ii] = chidSnap.getValue ().toString ();
                    ii++;
                }
               fini=true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v ( "XXX", "erreur Firebase" );
            }
        } );

    }
    public void retrievemessage()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance ();
        DatabaseReference myRef = database.getReference ( "message" );

        myRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessage = (String) dataSnapshot.child ( data.get("message")+"/libelle" ).getValue ();
                mType = Integer.parseInt (   dataSnapshot.child ( data.get("message")+"/type" ).getValue ().toString ());
                mPhoto= Boolean.valueOf (   dataSnapshot.child ( data.get("message")+"/photo" ).getValue ().toString ());
                mAction= Boolean.valueOf (   dataSnapshot.child ( data.get("message")+"/action" ).getValue ().toString ());
                fini=true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v ( MainActivity.TAG, "erreur Firebase" );
            }
        } );
    }

}





