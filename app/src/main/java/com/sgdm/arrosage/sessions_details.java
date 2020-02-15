package com.sgdm.arrosage;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.github.florent37.androidslidr.Slidr;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class sessions_details extends AppCompatActivity {

    public TextView det_debut;
    public String id_key;
    public Slidr det_duree;
    public ToggleButton det_actif;
    public Integer fb_heure, fb_minute;
    public Spinner det_arroseur;
    public MultiStateToggleButton det_frequence;
    public Integer mHour,mMinute;
    //public static String[] libarro = new String[4];
    final String TAG = "====>Arrosage<====";
    public Boolean det_modifie = false;
    public String mFrequence;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.sessions_details );

        id_key = getIntent ().getStringExtra ( "Key" );
        TextView det_key = findViewById ( R.id.det_key );
        det_key.setText ( id_key );
        det_debut = findViewById ( R.id.det_debut );
        det_actif = findViewById ( R.id.det_actif );
        det_duree = findViewById ( R.id.det_duree );
        det_arroseur = findViewById ( R.id.det_arroseur );
        det_frequence = findViewById ( (R.id.det_mstb_multi_id) );
        det_duree.setMax ( 120 );
        loaddata ();

        /*FirebaseDatabase database = FirebaseDatabase.getInstance ();
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
        */
    }



public void loaddata()
    {
        //////////////////////////////////////////////////////////////////
        ////////// Chgt des donnéees initiales
        //////////////////////////////////////////////////////////////////




        FirebaseDatabase database = FirebaseDatabase.getInstance ();
        DatabaseReference myRef2 = database.getReference ( "arrosage/arro_auto/"+ id_key );
        myRef2.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /// Chargement initial duree
                det_duree.setCurrentValue ( Integer.parseInt ( dataSnapshot.child ( "duree" ).getValue ().toString () ));

                //  Chargement inital actif
                det_actif.setChecked (Boolean.valueOf ( dataSnapshot.child("actif").getValue ().toString ())) ;

                // Chargement initial arroseur
                Log.d(TAG, dataSnapshot.child ( "arroseur" ).getValue ().toString ());
                det_arroseur.setSelection ( (Integer.parseInt ( dataSnapshot.child ( "arroseur" ).getValue ().toString () ) ) );

                //Chargement initial Heure
                Integer tmp_Minute = dataSnapshot.child ( "debut" ).getValue ( Integer.class ) % 60;
                Integer tmp_Heure= dataSnapshot.child ( "debut" ).getValue ( Integer.class ) / 60;

                fb_heure = dataSnapshot.child ( "debut" ).getValue ( Integer.class ) / 60;
                fb_minute = dataSnapshot.child ( "debut" ).getValue ( Integer.class ) % 60;
                mHour =dataSnapshot.child ( "debut" ).getValue ( Integer.class ) / 60;
                mMinute=dataSnapshot.child ( "debut" ).getValue ( Integer.class ) % 60;
                det_debut.setText (  String.format ( "%02d", tmp_Heure) +":"+String.format ( "%02d", tmp_Minute ));

                //Chargement initial frequence
                det_frequence.enableMultipleChoice(true);
                boolean[] listday2 = {false, false, false, false, false, false, false  };
                listday2[0] = ( dataSnapshot.child ( "frequence" ).toString ().contains ( "1" ) );
                listday2[1] = ( dataSnapshot.child ( "frequence" ).toString ().contains ( "2" ) );
                listday2[2] = ( dataSnapshot.child ( "frequence" ).toString ().contains ( "3" ) );
                listday2[3] = ( dataSnapshot.child ( "frequence" ).toString ().contains ( "4" ) );
                listday2[4] = ( dataSnapshot.child ( "frequence" ).toString ().contains ( "5" ) );
                listday2[5] = ( dataSnapshot.child ( "frequence" ).toString ().contains ( "6" ) );
                listday2[6] = ( dataSnapshot.child ( "frequence" ).toString ().contains ( "0" ) );
                mFrequence = dataSnapshot.child ( "frequence" ).getValue ().toString ();
                det_frequence.setStates ( listday2 );
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("XXX", "erreur Firebase");
            }
        } );

        ///duree
        //=========================================================================================================
        det_duree.setTextFormatter(new Slidr.TextFormatter() {

            @Override
            public String format(float value) {
                det_modifie=true;
                return String.valueOf((int) value + " mn" );
            }
        });
        det_duree.setListener ( new Slidr.Listener () {
            @Override
            public void valueChanged(Slidr slidr, float currentValue) {
                if (currentValue > 0) {
                    int intValue = (int) currentValue;
                    FirebaseDatabase database = FirebaseDatabase.getInstance ();
                    DatabaseReference myRef = database.getReference ( "arrosage/arro_auto/" + id_key );
                    myRef.child ( "duree" ).setValue ( intValue );
                    det_modifie=true;
                }
            }
            @Override
            public void bubbleClicked(Slidr slidr) {
            }
        } );

        // debut
        //=========================================================================================================
        det_debut.setOnClickListener ( new View.OnClickListener () {
            public void onClick(View v) {
                TimePickerDialog heurepicker = new TimePickerDialog ( sessions_details.this,
                        new TimePickerDialog.OnTimeSetListener () {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String retour = String.format ( "%02d", hourOfDay ) + ":" + String.format ( "%02d", minute );
                                det_debut.setText ( retour );
                                FirebaseDatabase database = FirebaseDatabase.getInstance ();
                                DatabaseReference myRef = database.getReference ( "arrosage/arro_auto/" + id_key );
                                myRef.child ( "debut" ).setValue ( (hourOfDay*60) + minute );
                                mHour = hourOfDay;
                                mMinute= minute;
                                det_modifie=true;
                            }
                        }, fb_heure, fb_minute, true );
                heurepicker.show ();
            }
        } );

        // Arroseur
        //=========================================================================================================
        det_arroseur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                FirebaseDatabase database = FirebaseDatabase.getInstance ();
                DatabaseReference myRef = database.getReference ( "arrosage/arro_auto/" + id_key );
                myRef.child ( "arroseur" ).setValue ( position );
                det_modifie=true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        // Frequence
        //=========================================================================================================
        det_frequence.setOnValueChangedListener(new MultiStateToggleButton.OnValueChangedListener () {
            @Override
            public void onValueChanged(int position) {

                boolean[] listday =  det_frequence.getStates ();
                mFrequence="";
                if (listday[0]) mFrequence+= "1";
                if (listday[1]) mFrequence+= "2";
                if (listday[2]) mFrequence += "3";
                if (listday[3]) mFrequence += "4";
                if (listday[4]) mFrequence += "5";
                if (listday[5]) mFrequence += "6";
                if (listday[6]) mFrequence += "0";
                FirebaseDatabase database = FirebaseDatabase.getInstance ();
                DatabaseReference myRef = database.getReference ( "arrosage/arro_auto/" + id_key );
                myRef.child ( "frequence" ).setValue ( mFrequence );
                det_modifie=true;
            }
        });


        // Actif
        //=========================================================================================================
        det_actif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener () {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseDatabase database = FirebaseDatabase.getInstance ();
                DatabaseReference myRef = database.getReference ( "arrosage/arro_auto/" + id_key );
                myRef.child ( "actif" ).setValue ( isChecked );
                det_modifie=true;
                }
        });

        //=========================================================================================================
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MainActivity.libarro);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        det_arroseur.setAdapter(adapter);
        //=========================================================================================================
    }





    public void MajArduino1(String MyAction, String MyClef) {
        String req = "";
        Integer  mDebut = (mHour*60) + mMinute;
        if (MyAction == "modif") {
            req = "action=modifarro&{"
                    + MyClef + ";"
                    + (det_actif.isChecked () ? "1" : "0" ) + ";"
                    + (int) det_duree.getCurrentValue () + ";"
                    + det_arroseur.getSelectedItemPosition () + ";"
                    + (mDebut.toString ())  + ";"
                    + mFrequence +"}"
            ;
        }





        if (MyAction == "create") {
            req = "action=createarro&{"
                    + MyClef + ";"
                    + (det_actif.isChecked () ? "1" : "0" ) + ";"
                    + (int) det_duree.getCurrentValue () + ";"
                    + det_arroseur.getSelectedItemPosition () + ";"
                    + (mDebut.toString ())  + ";"
                    +mFrequence+ "}";
}

        if (MyAction == "suppr")   {
            req = "action=supprarro&key=" + MyClef ;
        }
        Log.d(TAG, req );
        Echange_centrale (  ( getResources ().getString ( R.string.IP_arduino )+"/?")+ req );
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
                Log.w (TAG, mMessage );
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body ().string ();
                if (response.isSuccessful ()) {
                    try {
                        Log.v ( TAG, mMessage );
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                }
            }
        } );
    }

    @Override
    public void onPause(){
        super.onPause();
        if (det_modifie) MajArduino1 ("modif", id_key  );
    }
}




