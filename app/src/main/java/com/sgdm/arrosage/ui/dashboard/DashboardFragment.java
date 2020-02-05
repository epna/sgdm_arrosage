package com.sgdm.arrosage.ui.dashboard;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sgdm.arrosage.Adapter;
import com.sgdm.arrosage.R;
import com.sgdm.arrosage.sessions_details;

import java.util.ArrayList;


public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    RecyclerView recyclerView;
    public struct_session session_courante;
    public Adapter adapter;
    public ArrayList<struct_session> sessions;
    public FloatingActionButton floating_plus;
    //public String[] libarro = new String[5];

public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of ( this ).get ( DashboardViewModel.class );
        View root = inflater.inflate ( R.layout.fragment_dashboard, container, false );
        sessions =  new ArrayList<> (  );
        recyclerView = root.findViewById ( R.id.recyclerview );
        //loaddata();

    floating_plus = root.findViewById(R.id.floating_plus);
    floating_plus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FirebaseDatabase database = FirebaseDatabase.getInstance ();
            DatabaseReference myRef = database.getReference ( "arrosage/arro_auto" );
            final String new_key = myRef.push ().getKey ();
            myRef = database.getReference ( "arrosage/arro_auto/"+ new_key );
            myRef.child ( "duree" ).setValue ( 15 );
            myRef.child ( "arroseur" ).setValue ( 0 );
            myRef.child ( "debut" ).setValue ( 720);
            myRef.child ( "actif" ).setValue ( false);
            myRef.child ( "frequence" ).setValue ( "" );
            Intent intent = new Intent ( getContext (), sessions_details.class );
            intent.putExtra ( "Key", new_key);
            intent.putExtra ( "Position", -1);
            getContext ().startActivity ( intent );
        }
    });
        return root;
    }

    public void loaddata()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance ();
        DatabaseReference myRef = database.getReference ( "arrosage" );

        myRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                int i = 0;
                for (DataSnapshot chidSnap : dataSnapshot.child ( "libelle" ).getChildren ()) {
                    libarro[i] = (chidSnap.getValue ().toString ());
                    Log.v ( "ppp", libarro[i] );
                    i++;
                }
                i = 0;

                 */
                for (DataSnapshot chidSnap : dataSnapshot.child ( "arro_auto" ).getChildren ()) {
                    session_courante = new struct_session ();
                    //sessions.add ( session_courante );
                    session_courante.key = chidSnap.getKey ();
                    session_courante.actif = chidSnap.child ( "actif" ).getValue ( boolean.class );
                    session_courante.arroseur = chidSnap.child ( "arroseur" ).getValue ( Integer.class );
                    session_courante.frequence = chidSnap.child ( "frequence" ).getValue ( String.class );
                    session_courante.debut = chidSnap.child ( "debut" ).getValue ( Integer.class );
                    session_courante.duree = chidSnap.child ( "duree" ).getValue ( Integer.class );
                    sessions.add ( session_courante );
                }
                recyclerView.setLayoutManager ( new LinearLayoutManager ( getContext () ) );
                adapter = new Adapter ( getContext (), sessions );
                recyclerView.setAdapter ( adapter );
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v ( "XXX", "erreur Firebase" );
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        sessions.clear ();
        loaddata ();
    }



    public static class struct_session extends Application {

        private Integer arroseur;
        private Integer duree;
        private Boolean actif;
        private String frequence;
        private Integer debut;
        private String key;
        private String urlPhoto;

        public Integer getArroseur(){return arroseur;}
        public Integer getDuree() {
            return duree;
        }
        public boolean getActif() {
            return actif;
        }
        public String getfrequence() {return frequence;}
        public Integer getDebut() {
            return debut;
        }
        public String getKey() {
            return key;
        }
        public String getUrlPhoto() {
            return urlPhoto;
        }

        public void setArroseur(Integer arroseur) {this.arroseur = arroseur;}
        public void setDuree(Integer duree) {
            this.duree = duree;
        }
        public void setActif(Boolean actif) {
            this.actif = actif;
        }
        public void setFrequence(String frequence) {
            this.frequence = frequence;
        }
        public void setDebut(Integer debut) {
            this.debut = debut;
        }
        public void setKey(String key) {
            this.key= key;
        }
        public void setUrlPhoto(String key) {
            this.urlPhoto= urlPhoto;
        }


    }
}




