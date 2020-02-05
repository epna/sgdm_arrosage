package com.sgdm.arrosage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

public class MainActivity extends AppCompatActivity{
    public static final String TAG = "== Arrosage == ";
    public static final String CHANNEL_ID = "sgdm_arrosage";
    public static String[] libarro = new String[4];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        BottomNavigationView navView = findViewById ( R.id.nav_view );
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder (
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications )
                .build ();
        NavController navController = Navigation.findNavController ( this, R.id.nav_host_fragment );
        NavigationUI.setupActionBarWithNavController ( this, navController, appBarConfiguration );
        NavigationUI.setupWithNavController ( navView, navController );
        //newToken ();
        //newTopic ();
        loadLibArrosage();
        createNotificationChannel();
    }
    public void newToken (){

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        onNewToken ( token );
                        Log.d(TAG, token);
                        Log.d(TAG,TAG);
                    }
                });

    }

    private void createNotificationChannel() {
        // Créer le NotificationChannel, seulement pour API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "sgdm_arrosage";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("Notification channel description");
            // Enregister le canal sur le système : attention de ne plus rien modifier après
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.   v
        FirebaseDatabase database = FirebaseDatabase.getInstance ();
        DatabaseReference myRef = database.getReference ( "arrosage" );
        myRef.child ( "token" ).setValue ( token);
    }
public void loadLibArrosage() {

    FirebaseDatabase database = FirebaseDatabase.getInstance ();
    DatabaseReference myRef = database.getReference ( "arrosage" );
    myRef.addListenerForSingleValueEvent ( new ValueEventListener () {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            int i = 0;
            for (DataSnapshot chidSnap : dataSnapshot.child ( "libelle" ).getChildren ()) {
                libarro[i] = (chidSnap.getValue ().toString ());
                Log.v ( TAG, libarro[i] );
                i++;
            }
        }
        ;
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.v ( TAG, "erreur Firebase" );
        }
    } );

}
}
