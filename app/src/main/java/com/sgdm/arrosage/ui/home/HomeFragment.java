package com.sgdm.arrosage.ui.home;

import android.os.Bundle;
import android.os.Handler;
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
    //String[] libarro = new String[5];
    final String TAG = "== Arrosage == ";
    private HomeViewModel homeViewModel;




    public SwipeButton   sw_arro[] = new SwipeButton[5];

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of ( this ).get ( NotificationsViewModel.class );
        View root = inflater.inflate ( R.layout.fragment_home, container, false );
        txt_synthese = (TextView) root.findViewById ( R.id.txt_home_synthese);
        txt_start = (TextView) root.findViewById ( R.id.txt_start_manu);
        txt_warning= (TextView) root.findViewById ( R.id.txt_warning_manu);
        txt_stop = (TextView) root.findViewById ( R.id.txt_stop_manu);
        sw_arro[1]= (SwipeButton) root.findViewById ( R.id.swipe_arro1);
        sw_arro[2]= (SwipeButton) root.findViewById ( R.id.swipe_arro2);
        sw_arro[3]= (SwipeButton) root.findViewById ( R.id.swipe_arro3);
        sw_arro[4]= (SwipeButton) root.findViewById ( R.id.swipe_arro4);
        txt_synthese.setText ( "" );
        retrieve_global_data();
        EchangeArduino (  getResources().getString(R.string.IP_arduino)+ "/?getstatus" );
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
        sw_arro[4].setOnActiveListener ( new OnActiveListener () {
            @Override
            public void onActive() {
                senddata ( 4, true );
            }
        } );
        return root;
    }

    public void senddata(int arroseur, boolean actif )
    {

        String FullURL = getResources().getString(R.string.IP_arduino)+ "/?arro=" + (arroseur-1);
        if (arroseur==ArrosageEnCours)
        {
            FullURL+= "false";
            ArrosageEnCours=-1;
            for (int iii= 1; iii<5;iii++) {
                sw_arro[iii].setEnabled ( true );
            }
            FancyToast.makeText ( getContext (), "Arrêt  " + MainActivity.libarro[arroseur], FancyToast.LENGTH_LONG, R.drawable.ic_watering3, false ).show ();

        }
        else
        {
            FancyToast.makeText ( getContext (), "Démarrage  " + MainActivity.libarro[arroseur], FancyToast.LENGTH_LONG, R.drawable.ic_watering3, false ).show ();
            FullURL+="true";
            ArrosageEnCours=arroseur;
            for (int iii= 1; iii<5;iii++) {
                sw_arro[iii].setEnabled ( iii == ArrosageEnCours );
            }

        }
        EchangeArduino (    FullURL);
    }

    public void retrieve_global_data() {
        /*
        FirebaseDatabase database = FirebaseDatabase.getInstance ();
        DatabaseReference myRef = database.getReference ( "arrosage" );

        myRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                max_warning = Integer.parseInt ( dataSnapshot.child ( "duree/max1" ).getValue ().toString () );
                max_arret = Integer.parseInt ( dataSnapshot.child ( "duree/max2" ).getValue ().toString () );
                int ii=0;
                for (DataSnapshot chidSnap : dataSnapshot.child ( "libelle" ).getChildren ()) {
                    ii++;
                    libarro[ii] = chidSnap.getValue ().toString ();
                    Log.d(TAG,libarro[ii]);
                    sw_arro[ii].setText ( libarro[ii] );
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v ( "XXX", "erreur Firebase" );
            }
        } );
*/
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
        txt_synthese.setText ( "" );
        txt_start.setText ( "" );
        txt_warning.setText ( "" );
        txt_stop.setText ( "" );


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
        for (int j = 0; j < manuel.length (); j++) {
            sw_arro[j + 1].setBackground ( ContextCompat.getDrawable ( getContext (), R.drawable.shape_rounded ) );
            sw_arro[j + 1].setText ( MainActivity.libarro[j + 1] );
            JSONObject c = null;
            try {
                c = manuel.getJSONObject ( j );
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            try {
                assert c != null;
                if (c.getString ( "actif" ).equals ( "on" )) {
                    sw_arro[j + 1].setText ( "Arrêter " + MainActivity.libarro[1] );
                    Get_Synthese ( c, j + 1 );
                    ArrosageEnCours = j + 1;
                    sw_arro[j + 1].setBackground ( ContextCompat.getDrawable ( getContext (), R.drawable.shape_rounded_running ) );
                    for (int iii= 1; iii<5;iii++) {
                        sw_arro[iii].setEnabled ( false );
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
            try {
                assert c != null;
                Integer fin = c.getInt ( "duree" ) + c.getInt ( "depuis" );
                Integer heure = fin /60;

                Integer minute = fin %60;

                txt_synthese.setText ( MainActivity.libarro[j + 1] + " fin : " + heure.toString ()+":" +minute.toString () );
            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }
        }



String Get_Synthese (JSONObject data_manuel, int arroseur_synt )
    {
        String manu_heuredebut, manu_heureavert, manu_heurefin;

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

        txt_start.setText ( manu_heuredebut );
        txt_warning.setText ( manu_heureavert );
        txt_stop.setText (  manu_heurefin );
        txt_synthese.setText ( MainActivity.libarro[arroseur_synt] );
        return "";


    }
    @Override
    public void onResume(){
        super.onResume();
        EchangeArduino (  getResources().getString(R.string.IP_arduino)+ "/?getstatus" );
    }
}