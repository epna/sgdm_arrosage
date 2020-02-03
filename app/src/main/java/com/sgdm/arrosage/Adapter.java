package com.sgdm.arrosage;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.florent37.androidslidr.Slidr;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sgdm.arrosage.ui.dashboard.DashboardFragment;
import com.squareup.picasso.Picasso;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Adapter extends  RecyclerView.Adapter<Adapter.ViewHolder> {
    List<DashboardFragment.struct_session> sessions;
    LayoutInflater inflater;
    Context micontext;
    public MultiStateToggleButton frm_MultiButton;
    public  Spinner frm_Arroseur;
    public  Slidr frm_Duree;
    public  ToggleButton frm_Actif;
    public  TextView frm_Time;
    public  DashboardFragment.struct_session session_courante;
    final String TAG = "====>Arrosage<====";

    //ItemLongClickListener itemLongClickListener;
    public static String[] libarro = new String[4];
    public Adapter(Context ctx, List<DashboardFragment.struct_session> fullSession){
        this.sessions = fullSession;
        this.inflater = LayoutInflater.from(ctx);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_view,parent,false);
        return new ViewHolder(view);
    }
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String fb_Frequence = sessions.get(position).getfrequence ();
        Integer fb_heure = sessions.get(position).getDebut  ()/ 60;
        Integer fb_arroseur = sessions.get(position).getArroseur ();
        Integer fb_minute = sessions.get(position).getDebut () % 60;
        String minute_format = fb_minute.toString ().format ( "%02d", fb_minute );
        String heure_format = fb_minute.toString ().format ( "%02d", fb_heure );
        Integer  fb_duree = sessions.get(position).getDuree ();

        String  param_session = "";
        if (fb_Frequence.contains ( "0" )) param_session += "D ";
        if (fb_Frequence.contains ( "1" )) param_session += "L ";
        if (fb_Frequence.contains ( "2" )) param_session += "Ma ";
        if (fb_Frequence.contains ( "3" )) param_session += "Me ";
        if (fb_Frequence.contains ( "4" )) param_session += "J ";
        if (fb_Frequence.contains ( "5" )) param_session += "V ";
        if (fb_Frequence.contains ( "6" )) param_session += "S ";
        param_session +=  " " + heure_format + ":" + minute_format + " durée " + fb_duree + " m";
        FirebaseDatabase database = FirebaseDatabase.getInstance ();
        DatabaseReference myRef = database.getReference ().child ( "arrosage/libelle/"+sessions.get ( position ).getArroseur ().toString ()  );
        myRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.txt2Cust.setText(dataSnapshot.getValue ().toString () );
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        } );

        holder.txt3Cust.setText(param_session);
        //holder.txt2Cust.setTextColor ( ContextCompat.getColor(holder.btn_delete.getContext (), R.color.md_black_1000) );
        //
        //
        holder.txt3Cust.setTextColor ( Color.BLUE);




        // photos
        if (sessions.get ( position ).getActif ()) {

            DatabaseReference myRef2 = database.getReference ().child ( "arrosage/images/arro"+ sessions.get ( position ).getArroseur ().toString ()  );
            myRef2.addListenerForSingleValueEvent ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String MyUrl = dataSnapshot.getValue ().toString () ;
                    Picasso.get().load(MyUrl).into(holder.imageViewcust);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            } );
        }
        else
        {
            Picasso.get().load(R.drawable.ic_stop).into(holder.imageViewcust);
            //Picasso.with(mContext).pl .into(customViewHolder.imageview1);

        }
    }


    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt2Cust;
        TextView txt3Cust;
        ImageView imageViewcust;
        Button btn_delete;


         public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt2Cust = itemView.findViewById(R.id.txt2Cust);
            txt3Cust = itemView.findViewById(R.id.txt3Cust);
            imageViewcust = itemView.findViewById ( R.id.imageViewcust );



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Clicked -> " + getAdapterPosition() + " "+ txt2Cust.getText () , Toast.LENGTH_SHORT).show();
                    String temp = sessions.get(getAdapterPosition ()).getKey ();
                    page_detail ( temp,  v.getContext (),getAdapterPosition()  );
                }
            });


             itemView.setOnLongClickListener(new View.OnLongClickListener() {
                 @Override
                 public boolean onLongClick(final View v) {
                     Toast.makeText(v.getContext(), "Long Position is " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                     Log.v ( "DDD", sessions.get ( getAdapterPosition() ).getKey () );

                     AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( v.getContext ());
                     alertDialogBuilder.setMessage ( "Confirmer la suppression ? " );
                     alertDialogBuilder.setPositiveButton ( "Oui",
                             new DialogInterface.OnClickListener () {
                                 @Override
                                 public void onClick(DialogInterface arg0, int arg1) {
                                     Toast.makeText ( v.getContext (), "Suppression  " + sessions.get ( getAdapterPosition() ).getKey (), Toast.LENGTH_LONG ).show ();
                                     FirebaseDatabase database2 = FirebaseDatabase.getInstance ();
                                     DatabaseReference myRef5 = database2.getReference ( "arrosage/arro_auto/" + sessions.get ( getAdapterPosition() ).getKey () );
                                     Echange_centrale (   v.getResources ().getString ( R.string.IP_arduino )+ "/?action=supprarro&key="+ sessions.get ( getAdapterPosition() ).getKey ());
                                     myRef5.removeValue ();
                                     sessions.remove ( getAdapterPosition() );

                                 }
                             } );

                     alertDialogBuilder.setNegativeButton ( "Non", new DialogInterface.OnClickListener () {

                         public void onClick(DialogInterface dialog, int which) {
                             Toast.makeText ( v.getContext (), "Suppression annulée", Toast.LENGTH_LONG ).show ();
                         }
                     } );

                     AlertDialog alertDialog = alertDialogBuilder.create ();
                     alertDialog.show ();
                     return true;
                 }
             });
        }
    }

        public void page_detail (@NotNull final String myKey, final Context mContext, final Integer mPosition) {
                Intent intent = new Intent ( mContext, sessions_details.class );
            intent.putExtra ( "Key", sessions.get(mPosition ).getKey ());
            intent.putExtra ( "Position", mPosition);
            mContext.startActivity ( intent );
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


}


