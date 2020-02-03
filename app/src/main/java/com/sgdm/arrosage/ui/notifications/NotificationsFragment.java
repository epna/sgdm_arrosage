package com.sgdm.arrosage.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.florent37.androidslidr.Slidr;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sgdm.arrosage.R;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    public Slidr sliderAF, sliderWA;
    public TextInputEditText edit_arro1, edit_arro2, edit_arro3, edit_arro4;
    public int valAF, valWA;
    String[] libarro = new String[5];

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of ( this ).get ( NotificationsViewModel.class );
        View root = inflater.inflate ( R.layout.fragment_notifications, container, false );





        sliderAF = (Slidr) root.findViewById ( R.id.sliderAF);
        sliderWA = (Slidr) root.findViewById ( R.id.sliderWA);

        edit_arro1 = (TextInputEditText) root.findViewById ( R.id.edit_arro1);
        edit_arro2 = (TextInputEditText) root.findViewById ( R.id.edit_arro2);
        edit_arro3 = (TextInputEditText) root.findViewById ( R.id.edit_arro3);
        edit_arro4 = (TextInputEditText) root.findViewById ( R.id.edit_arro4);

        FirebaseDatabase database = FirebaseDatabase.getInstance (  );
        DatabaseReference myRef = database.getReference ( "arrosage" );

        myRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                valWA = ( Integer.parseInt ( dataSnapshot.child ( "duree/max1" ).getValue ().toString () ));
                valAF = ( Integer.parseInt ( dataSnapshot.child ( "duree/max2" ).getValue ().toString () ));

                int ii = 0;
                for (DataSnapshot chidSnap : dataSnapshot.child ( "libelle" ).getChildren ()) {
                    ii++;
                    libarro[ii] = chidSnap.getValue ().toString () ;
                }
                edit_arro1.setText ( libarro[1] );
                edit_arro2.setText ( libarro[2] );
                edit_arro3.setText ( libarro[3] );
                edit_arro4.setText ( libarro[4] );
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
                Log.v("XXX", "erreur Firebase");
            }
        } );
        ////////////////////////////////////////////////////////////////
        /// Event
        ////////////////////////////////////////////////////////////////


        root.findViewById(R.id.edit_arro1).setOnFocusChangeListener( new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

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
                    //sliderAF.addStep(new Slidr.Step("test", sliderWA.getCurrentValue (), Color.parseColor("#007E90"), Color.RED));
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
}
