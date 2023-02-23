package com.example.licenta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.licenta.Adapters.ProgramareAdapter;
import com.example.licenta.Adapters.ProgramareMainAdapter;
import com.example.licenta.Models.Medic;
import com.example.licenta.Models.Pacient;
import com.example.licenta.Models.Programare;
import com.example.licenta.Models.Specialitate;
import com.example.licenta.Models.Specialitati;
import com.example.licenta.Utils.APICommunication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView tvUserName;
    private FirebaseAuth mAuth;
    private CardView allMedicsCard, viewProfile, allProgramari,aboutCard;
    private RecyclerView recvAppointments;
    private ImageView imgProfile;
    private ProgressDialog progressDialog;
    private Pacient p;
    private ArrayList<Programare> programari;
    private ProgramareMainAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main");
        mAuth = FirebaseAuth.getInstance();

        tvUserName = findViewById(R.id.txtForNameDisplay);
        allMedicsCard = findViewById(R.id.allMedicsCard);
        allProgramari = findViewById(R.id.allProgramariCard);
        aboutCard = findViewById(R.id.aboutCliniqueCard);
        imgProfile = findViewById(R.id.userProfile);
        viewProfile = findViewById(R.id.viewProfile);
        recvAppointments = findViewById(R.id.recvAppointments);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileViewActivity.class);
                i.putExtra("PROFILE",p);
                startActivity(i);
            }
        });
        allMedicsCard.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, MedicsListActivity.class);
            i.putExtra("pacient",p);
            startActivity(i);
        });
        allProgramari.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ProgramariActivity.class);
            i.putExtra("pacient", p);
            startActivity(i);
        });
        aboutCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });
        programari = new ArrayList<>();
//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                incarcaProgramari();
//            }
//        }, 1000);
    }

    private void incarcaProgramari() {
        try{
            programari = new ArrayList<>();
            for(int i=0;i<APICommunication.appointmentsArray.length();i++){
//                setarea info programare
                JSONObject currentApp = APICommunication.appointmentsArray.getJSONObject(i);
                Programare prog = new Programare();
                prog.setId(currentApp.getInt("id"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    prog.setData(LocalDateTime.parse(currentApp.getString("data"),
                            DateTimeFormatter.ISO_DATE_TIME));
                }
                prog.setObservatii(currentApp.getString("observatii"));

//                setarea medicului programarii
                JSONObject currentMedic = currentApp.getJSONObject("medic");
                Medic m = new Medic();
                m.setId(currentMedic.getString("id"));
                m.setFirstName(currentMedic.getString("firstName"));
                m.setLastName(currentMedic.getString("lastName"));
                m.setEmail(currentMedic.getString("email"));
                m.setVarsta(currentMedic.getInt("varsta"));
                m.setAdresa(currentMedic.getString("adresa"));
                m.setPhoto(currentMedic.getString("photo"));
                m.setCNP(currentMedic.getString("cnp"));
                m.setRating((float) currentMedic.getDouble("rating"));
                m.setVechime(currentMedic.getInt("vechime"));
                try{
                    JSONObject specObj = (JSONObject) currentMedic.get("specialitate");
                    m.setSpecialitate(new Specialitate(Specialitati.valueOf(specObj.getString("tip")), specObj.getString("descriere")));
                }catch (Exception e){
                    e.printStackTrace();
                }
                prog.setMedic(m);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(LocalDateTime.now().until(prog.getData(), ChronoUnit.DAYS) < 7 &&
                        LocalDateTime.now().isBefore(prog.getData()))
                        programari.add(prog);
                }
            }
            adapter = new ProgramareMainAdapter(this);
            adapter.setProgramari(programari);
            recvAppointments.setAdapter(adapter);
            recvAppointments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            progressDialog.dismiss();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //verificam daca este vreun user logat deja
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else{
            APICommunication.getPacient(user.getUid(),getApplicationContext());
            APICommunication.getAppointments(user.getUid(), getApplicationContext());
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    incarcaDate(user.getUid());
                    incarcaProgramari();
                }
            }, 1000);
        }
    }

    private void incarcaDate(String id){
        p = new Pacient();
        try {
            p.setId(APICommunication.currentOBJ.getString("id"));
            p.setFirstName(APICommunication.currentOBJ.getString("firstName"));
            p.setLastName(APICommunication.currentOBJ.getString("lastName"));
            p.setEmail(APICommunication.currentOBJ.getString("email"));
            p.setVarsta(APICommunication.currentOBJ.getInt("varsta"));
            p.setAdresa(APICommunication.currentOBJ.getString("adresa"));
            p.setPhoto(APICommunication.currentOBJ.getString("photo"));
            p.setGrad_urgenta(APICommunication.currentOBJ.getInt("grad_urgenta"));
            p.setCNP(APICommunication.currentOBJ.getString("cnp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvUserName.setText(p.getFirstName()+" "+p.getLastName());
        Glide.with(this).asBitmap().load(p.getPhoto()).into(imgProfile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}