package com.sgdm.arrosage.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.github.florent37.androidslidr.Slidr;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sgdm.arrosage.MainActivity;
import com.sgdm.arrosage.R;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParametresFragment extends Fragment {

    //private NotificationsViewModel notificationsViewModel;
    public Slidr sliderAF, sliderWA;
    public TextInputEditText edit_arro1, edit_arro2, edit_arro3, edit_arro4;
    public int valAF, valWA;
    public Button btn_refresh_Sessions;
    public boolean modifMax = false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate ( R.layout.fragment_parametres, container, false );
        sliderAF = root.findViewById ( R.id.sliderAF);
        sliderWA = root.findViewById ( R.id.sliderWA);
        edit_arro1 = root.findViewById ( R.id.edit_arro1);
        edit_arro2 = root.findViewById ( R.id.edit_arro2);
        edit_arro3 = root.findViewById ( R.id.edit_arro3);
        edit_arro4 = root.findViewById ( R.id.edit_arro4);
        btn_refresh_Sessions = root.findViewById ( R.id.resfreshSession );
        FirebaseDatabase database = FirebaseDatabase.getInstance (  );
        DatabaseReference myRef = database.getReference ( "arrosage" );
        myRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                valWA = ( Integer.parseInt ( dataSnapshot.child ( "duree/max1" ).getValue ().toString () ));
                valAF = ( Integer.parseInt ( dataSnapshot.child ( "duree/max2" ).getValue ().toString () ));
                edit_arro1.setText ( MainActivity.libarro[0] );
                edit_arro2.setText ( MainActivity.libarro[1] );
                edit_arro3.setText ( MainActivity.libarro[2] );
                edit_arro4.setText ( MainActivity.libarro[3] );
                sliderAF.setTextMax ( "Max" );
                sliderAF.setTextMin ( "Min" );
                sliderWA.setTextMax ( "Max" );
                sliderWA.setTextMin ( "Min" );
                sliderWA.setMin(0);
                sliderWA.setMax(valAF);
                sliderAF.setMax(120);
                sliderAF.setMin(valWA);
                sliderWA.setCurrentValue ( valWA);
                sliderAF.setCurrentValue ( valAF);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(MainActivity.TAG, "erreur Firebase");
            }
        } );
        ////////////////////////////////////////////////////////////////
        /// Event
        ////////////////////////////////////////////////////////////////
        btn_refresh_Sessions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {Echange_centrale (( getResources ().getString ( R.string.IP_arduino )+"/?") + "refresh" );// Do something in response to button click
            }
        });

        root.findViewById(R.id.edit_arro1).setOnFocusChangeListener( new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    MainActivity.libarro[0]=edit_arro1.getText ().toString ();
                    FirebaseDatabase database = FirebaseDatabase.getInstance ();
                    DatabaseReference myRef = database.getReference ( "arrosage" );
                    myRef.child ( "libelle" ).child ( "0" ).setValue ( edit_arro1.getText ().toString ());
                }
            }
        });

        root.findViewById(R.id.edit_arro2).setOnFocusChangeListener( new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    MainActivity.libarro[1]=edit_arro2.getText ().toString ();
                    FirebaseDatabase database = FirebaseDatabase.getInstance ();
                    DatabaseReference myRef = database.getReference ( "arrosage" );
                    myRef.child ( "libelle" ).child ( "1" ).setValue ( edit_arro2.getText ().toString ());
                }
            }
        });
        root.findViewById(R.id.edit_arro3).setOnFocusChangeListener( new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    MainActivity.libarro[2]=edit_arro3.getText ().toString ();
                    FirebaseDatabase database = FirebaseDatabase.getInstance ();
                    DatabaseReference myRef = database.getReference ( "arrosage" );
                    myRef.child ( "libelle" ).child ( "2" ).setValue ( edit_arro3.getText ().toString ());
                }
            }
        });

        root.findViewById(R.id.edit_arro4).setOnFocusChangeListener( new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    MainActivity.libarro[3]=edit_arro4.getText ().toString ();
                    FirebaseDatabase database = FirebaseDatabase.getInstance ();
                    DatabaseReference myRef = database.getReference ( "arrosage" );
                    myRef.child ( "libelle" ).child ( "3" ).setValue ( edit_arro4.getText ().toString ());
                }
            }
        });




        sliderWA.setListener(new Slidr.Listener() {
            @Override
            public void valueChanged(Slidr slidr, float currentValue) {

                if (currentValue >0) {
                    int intValue = (int) currentValue;
                    //Toast.makeText ( getApplicationContext (), "Stopped tracking seekbar", Toast.LENGTH_SHORT ).show ();
                    FirebaseDatabase database = FirebaseDatabase.getInstance ();
                    DatabaseReference myRef = database.getReference ( "arrosage" );
                    myRef.child ( "duree" ).child ( "max1" ).setValue ( intValue );

                    sliderAF.setMin(sliderWA.getCurrentValue ());
                    sliderAF.clearSteps ();
                    modifMax=true;
                }
            }

            @Override
            public void bubbleClicked(Slidr slidr) {

            }

        });


        sliderAF.setListener(new Slidr.Listener() {
            @Override
            public void valueChanged(Slidr slidr, float currentValue) {
                if (currentValue >0)
                {
                    int intValue = (int) currentValue;
                    //Toast.makeText ( getApplicationContext (), "Stopped tracking seekbar", Toast.LENGTH_SHORT ).show ();
                    FirebaseDatabase database = FirebaseDatabase.getInstance ();
                    DatabaseReference myRef = database.getReference ( "arrosage" );
                    myRef.child ( "duree" ).child ( "max2" ).setValue (  intValue );
                    sliderWA.setMax(sliderAF.getCurrentValue ());
                    modifMax=true;

                }
            }
            @Override
            public void bubbleClicked(Slidr slidr) {
            }
        });

        sliderWA.setTextFormatter(new Slidr.TextFormatter() {
            @Override
            public String format(float value) {
                return String.valueOf((int) value + " mn" );
            }
        });

        sliderAF.setTextFormatter(new Slidr.TextFormatter() {
            @Override
            public String format(float value) {
                return String.valueOf((int) value + " mn" );
            }
        });



        return root;
    }

    @Override
    public void onPause(){
        super.onPause();
        if (modifMax) Echange_centrale (( getResources ().getString ( R.string.IP_arduino )+"/?") + "modiflistemax" );
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

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body ().string ();
                if (response.isSuccessful ()) {
                    try {
                        Log.v ( MainActivity.TAG , mMessage );
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                }
            }
        } );
    }
}
