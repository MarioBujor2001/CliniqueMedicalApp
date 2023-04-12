package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.licenta.Adapters.ProgramareMainAdapter;
import com.example.licenta.Models.Medic;
import com.example.licenta.Models.Patient;
import com.example.licenta.Models.Appointment;
import com.example.licenta.Models.Specialty;
import com.example.licenta.Models.Specialties;
import com.example.licenta.Utils.APICommunicationV2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView tvUserName, txtYouDontHaveAppointments;
    private FirebaseAuth mAuth;
    private CardView allMedicsCard, viewProfile, allProgramari, aboutCard, investigationCard, ordersCard, newsCard, bodyAnalysisCard;
    private RecyclerView recvAppointments;
    private ImageView imgProfile;
    private ProgressDialog progressDialog;
    private Patient p;
    private ArrayList<Appointment> programari;
    private ProgramareMainAdapter adapter;
    private FirebaseUser user;

    public BroadcastReceiver receivePersonalInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if(isSucces){
                loadPersonalInfo(user.getUid());
                APICommunicationV2.getAppointments(user.getUid(), getApplicationContext());
            }
        }
    };

    public BroadcastReceiver receiveAppointmentsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if(isSucces){
                loadAppointments();
                reloadAppointmentsAdapter();
                cancelLoadingDialog();
            }
        }
    };

    private void createLoadingDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void cancelLoadingDialog(){
        progressDialog.dismiss();
    }

    private void reloadAppointmentsAdapter(){
        if(programari.isEmpty()){
            txtYouDontHaveAppointments.setVisibility(View.VISIBLE);
        }else{
            txtYouDontHaveAppointments.setVisibility(View.GONE);
        }
        adapter = new ProgramareMainAdapter(this);
        adapter.setProgramari(programari);
        recvAppointments.setAdapter(adapter);
        recvAppointments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receivePersonalInfoReceiver, new IntentFilter("apiMessagePersonalInfo"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiveAppointmentsReceiver, new IntentFilter("apiMessageAppointments"));
        createLoadingDialog();
        //verificam daca este vreun user logat deja
        user = mAuth.getCurrentUser();
        if(user == null){
            cancelLoadingDialog();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else{
            APICommunicationV2.getPacient(user.getUid(),getApplicationContext());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main");
        mAuth = FirebaseAuth.getInstance();

        tvUserName = findViewById(R.id.txtForNameDisplay);
        txtYouDontHaveAppointments = findViewById(R.id.txtYouDontHaveAppointments);
        allMedicsCard = findViewById(R.id.allMedicsCard);
        allProgramari = findViewById(R.id.allProgramariCard);
        aboutCard = findViewById(R.id.aboutCliniqueCard);
        ordersCard = findViewById(R.id.ordersCard);
        newsCard = findViewById(R.id.newsCard);
        bodyAnalysisCard = findViewById(R.id.bodyAnalysisCard);
        investigationCard = findViewById(R.id.investigationCard);
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
            Intent i = new Intent(MainActivity.this, AppointmentsActivity.class);
            i.putExtra("pacient", p);
            startActivity(i);
        });
        aboutCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });
        investigationCard.setOnClickListener(v -> {
//            startActivity(new Intent(MainActivity.this, InvestigationsActivity.class));
            Intent i = new Intent(MainActivity.this, InvestigationsActivity.class);
            i.putExtra("pacient", p);
            startActivity(i);
        });
        ordersCard.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, OrderHistoryActivity.class);
            i.putExtra("pacient", p);
            startActivity(i);
        });
        newsCard.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, DailyNewsActivity.class));
        });
        bodyAnalysisCard.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, BodyAnalysisActivity.class);
            i.putExtra("pacient", p);
            startActivity(i);
        });
    }

    private void loadAppointments() {
        try{
            programari = new ArrayList<>();
            for(int i=0;i<APICommunicationV2.appointmentsArray.length();i++){
//                setarea info programare
                JSONObject currentApp = APICommunicationV2.appointmentsArray.getJSONObject(i);
                Appointment prog = new Appointment();
                prog.setId(currentApp.getInt("id"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    prog.setDate(LocalDateTime.parse(currentApp.getString("date"),
                            DateTimeFormatter.ISO_DATE_TIME));
                }
                prog.setComments(currentApp.getString("comments"));

//                setarea medicului programarii
                JSONObject currentMedic = currentApp.getJSONObject("medic");
                Medic m = new Medic();
                m.setId(currentMedic.getString("id"));
                m.setFirstName(currentMedic.getString("firstName"));
                m.setLastName(currentMedic.getString("lastName"));
                m.setEmail(currentMedic.getString("email"));
                m.setAge(currentMedic.getInt("age"));
                m.setAddress(currentMedic.getString("address"));
                m.setPhotoUrl(currentMedic.getString("photoUrl"));
                m.setCNP(currentMedic.getString("cnp"));
                m.setRating((float) currentMedic.getDouble("rating"));
                m.setSeniority(currentMedic.getInt("seniority"));
                try{
                    JSONObject specObj = (JSONObject) currentMedic.get("specialty");
                    m.setSpecialty(new Specialty(Specialties.valueOf(specObj.getString("type")), specObj.getString("description")));
                }catch (Exception e){
                    e.printStackTrace();
                }
                prog.setMedic(m);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(LocalDateTime.now().until(prog.getDate(), ChronoUnit.DAYS) < 7 &&
                        LocalDateTime.now().isBefore(prog.getDate()))
                        programari.add(prog);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void loadPersonalInfo(String id){
        p = new Patient();
        try {
            p.setId(APICommunicationV2.currentOBJ.getString("id"));
            p.setFirstName(APICommunicationV2.currentOBJ.getString("firstName"));
            p.setLastName(APICommunicationV2.currentOBJ.getString("lastName"));
            p.setEmail(APICommunicationV2.currentOBJ.getString("email"));
            p.setAge(APICommunicationV2.currentOBJ.getInt("age"));
            p.setAddress(APICommunicationV2.currentOBJ.getString("address"));
            p.setPhotoUrl(APICommunicationV2.currentOBJ.getString("photoUrl"));
//            p.setGrad_urgenta(APICommunicationV2.currentOBJ.getInt("grad_urgenta"));
            p.setCNP(APICommunicationV2.currentOBJ.getString("cnp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvUserName.setText(p.getFirstName()+" "+p.getLastName());
        Glide.with(getApplicationContext()).asBitmap().load(p.getPhotoUrl()).into(imgProfile);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}