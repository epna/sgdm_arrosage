package com.sgdm.arrosage.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sgdm.arrosage.MainActivity;
import com.sgdm.arrosage.R;
import com.sgdm.arrosage.ui.notifications.NotificationsViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.util.Calendar.MINUTE;

public class HomeFragment extends Fragment {


    public TextView txt_synthese;
    public TextView txt_start;
    public TextView txt_warning;
    public TextView txt_stop;
    public int ArrosageEnCours=-1;
    public int max_warning, max_arret;
    final String TAG = "== Arrosage == ";
    public static String[] libarro = new String[4];
    public SwipeButton   sw_arro[] = new SwipeButton[5];
    public View root;
    //private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        //notificationsViewModel = ViewModelProviders.of ( this ).get ( NotificationsViewModel.class );
        root = inflater.inflate ( R.layout.fragment_home, container, false );
        txt_synthese = (TextView) root.findViewById ( R.id.txt_home_synthese);
        txt_start = (TextView) root.findViewById ( R.id.txt_start_manu);
        txt_warning= (TextView) root.findViewById ( R.id.txt_warning_manu);
        txt_stop = (TextView) root.findViewById ( R.id.txt_stop_manu);
        sw_arro[0]= (SwipeButton) root.findViewById ( R.id.swipe_arro1);
        sw_arro[1]= (SwipeButton) root.findViewById ( R.id.swipe_arro2);
        sw_arro[2]= (SwipeButton) root.findViewById ( R.id.swipe_arro3);
        sw_arro[3]= (SwipeButton) root.findViewById ( R.id.swipe_arro4);
        txt_synthese.setText ( "" );

        initdata ();
        sw_arro[0].setOnActiveListener ( new OnActiveListener () {
           @Override
                public void onActive() {
                    senddata ( 0, true );
                }
            } );
        sw_arro[1].setOnActiveListener ( new OnActiveListener () {
            @Override
            public void onActive() {
                senddata ( 1, true );
            }
        } );
        sw_arro[2].setOnActiveListener ( new OnActiveListener () {
            @Override
            public void onActive() {
                senddata ( 2, true );
            }
        } );
        sw_arro[3].setOnActiveListener ( new OnActiveListener () {
            @Override
            public void onActive() {
                senddata ( 3, true );
            }
        } );
        return root;
    }

    public void senddata(int arroseur, boolean actif )
    {

        String FullURL = getResources().getString(R.string.IP_arduino)+ "/?arro=" + (arroseur);
        if (arroseur==ArrosageEnCours)
        {
            FullURL+= "false";
            ArrosageEnCours=-1;
            for (int iii= 0; iii<4;iii++) {
                sw_arro[iii].setEnabled ( true );
            }
            FancyToast.makeText ( getContext (), "Arrêt  " + MainActivity.libarro[arroseur], FancyToast.LENGTH_LONG, R.drawable.ic_watering3, false ).show ();

        }
        else
        {
            FancyToast.makeText ( getContext (), "Démarrage  " + MainActivity.libarro[arroseur], FancyToast.LENGTH_LONG, FancyToast.WARNING, false ).show ();
            FullURL+="true";
            ArrosageEnCours=arroseur;
            for (int iii= 0; iii<4;iii++) {
                sw_arro[iii].setEnabled ( iii == ArrosageEnCours );
            }

        }
        EchangeArduino (    FullURL);
    }



    public void EchangeArduino(String MyURL) {
        final OkHttpClient client = new OkHttpClient ();
        Request request = new Request.Builder ()
                .url ( MyURL )
                .get ()
                .build ();
        client.newCall ( request ).enqueue ( new Callback () {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage ().toString ();
                Log.w ( TAG, mMessage );
                Log.v ( TAG, "Erreur " );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body ().string ();
                if (response.isSuccessful ()) {
                    try {
                        Log.v(TAG, mMessage);
                        retourArduino   (mMessage);
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                }
            }
        } );
    }


    public void retourArduino(String DataArduino) {

        new Handler( Looper.getMainLooper()).post( new Runnable(){
            @Override
            public void run() {
                txt_synthese.setText ( "" );
                txt_start.setText ( "" );
                txt_warning.setText ( "" );
                txt_stop.setText ( "" );
            }
        });






        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject ( DataArduino );
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        JSONArray manuel = null;
        JSONArray automatique = null;

        try {
            manuel = jsonObj.getJSONArray ( "manuel" );
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        for ( int j = 0; j < manuel.length (); j++) {
            final Integer mj=j;
            new Handler( Looper.getMainLooper()).post( new Runnable(){
                @Override
                public void run() {
                    sw_arro[mj ].setBackground ( ContextCompat.getDrawable ( getContext (), R.drawable.shape_rounded ) );
                    sw_arro[mj ].setText ( MainActivity.libarro[mj ] );
                }
            });


            JSONObject c = null;
            try {
                c = manuel.getJSONObject ( mj );
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            try {
                assert c != null;
                if (c.getString ( "actif" ).equals ( "on" )) {
                    Get_Synthese ( c, mj );
                    ArrosageEnCours = mj ;
                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                        @Override
                        public void run() {
                            sw_arro[mj].setText ( "Arrêter " + MainActivity.libarro[mj] );
                            sw_arro[mj].setBackground ( ContextCompat.getDrawable ( getContext (), R.drawable.shape_rounded_running ) );

                        }
                    });

                    for ( int iii= 0; iii<4;iii++) {
                        final Integer miii =iii;
                        new Handler(Looper.getMainLooper()).post(new Runnable(){
                            @Override
                            public void run() {
                                sw_arro[miii].setEnabled ( mj==miii );
                            }
                        });

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }


        try {
            automatique = jsonObj.getJSONArray ( "automatique" );
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        for (int j = 0; j < automatique.length (); j++) {
            JSONObject c = null;
            try {
                c = automatique.getJSONObject ( j );
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            final Integer mj = j;
            try {
                assert c != null;
                Integer fin = c.getInt ( "duree" ) + c.getInt ( "depuis" );
                final Integer heure = fin /60;
                final Integer minute = fin %60;
                Snackbar snb_automattique= Snackbar.make ( root.findViewById(R.id.navigation) , "arrosage automatique  " + MainActivity.libarro[mj ] + " fin : " + heure.toString ()+":" +minute.toString (),Snackbar.LENGTH_LONG );
                snb_automattique.show ();
                for ( int iii= 0; iii<4;iii++) {
                    final Integer miii =iii;
                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                        @Override
                        public void run() {
                            sw_arro[miii].setEnabled ( false);
                        }
                    });
                };

            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }
        }
String Get_Synthese (JSONObject data_manuel, final int arroseur_synt )
    {
        final String manu_heuredebut, manu_heureavert, manu_heurefin;

        DateFormat df = new SimpleDateFormat ( "HH:mm" );
        Calendar now = Calendar.getInstance ();
        try {
            now.add ( MINUTE, - data_manuel.getInt ( "duree" ) );
        }
        catch (JSONException e) {
            e.printStackTrace ();
        }
        manu_heuredebut = df.format ( now.getTime () ).toString ();
        now.add ( MINUTE, max_warning );
        manu_heureavert = df.format ( now.getTime () ).toString ();
        now.add ( MINUTE, max_arret - max_warning);
        manu_heurefin = df.format ( now.getTime () ).toString ();


        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                txt_start.setText ( manu_heuredebut );
                txt_warning.setText ( manu_heureavert );
                txt_stop.setText (  manu_heurefin );
                txt_synthese.setText ( MainActivity.libarro[arroseur_synt] );

            }
        });

        return "";


    }



    public void initdata(){

        FirebaseDatabase database = FirebaseDatabase.getInstance ();
        DatabaseReference myRef = database.getReference ( "arrosage" );
        myRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                max_warning = ( Integer.parseInt ( dataSnapshot.child ( "duree/max1" ).getValue ().toString () ));
                max_arret = ( Integer.parseInt ( dataSnapshot.child ( "duree/max2" ).getValue ().toString () ));
                int i = 0;
                for (DataSnapshot chidSnap : dataSnapshot.child ( "libelle" ).getChildren ()) {
                    libarro[i] = (chidSnap.getValue ().toString ());
                    Log.v ( TAG, libarro[i] );
                    i++;
                }
                EchangeArduino (  getResources().getString(R.string.IP_arduino)+ "/?getstatus" );
            }
            ;
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v ( TAG, "erreur Firebase" );
            }
        } );

    }
    @Override
    public void onResume(){
        super.onResume();
        EchangeArduino (  getResources().getString(R.string.IP_arduino)+ "/?getstatus" );
    }
}