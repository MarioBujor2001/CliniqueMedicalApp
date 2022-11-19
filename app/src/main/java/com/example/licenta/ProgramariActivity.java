package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.licenta.Adapters.ProgramareAdapter;
import com.example.licenta.Models.Medic;
import com.example.licenta.Models.Pacient;
import com.example.licenta.Models.Programare;
import com.example.licenta.Models.Specialitate;
import com.example.licenta.Models.Specialitati;
import com.example.licenta.Utils.APICommunication;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProgramariActivity extends AppCompatActivity {
    private RecyclerView recvAppointments;
    private ArrayList<Programare> programari;
    private ProgramareAdapter adapter;
    private ProgressDialog progressDialog;
    private Pacient p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programari);
        recvAppointments = findViewById(R.id.recvAppointments);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        p = (Pacient) getIntent().getSerializableExtra("pacient");

        programari = new ArrayList<>();
        APICommunication.getAppointments(p.getId(), getApplicationContext());
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                incarcaDate();
            }
        }, 1000);
    }

    private void incarcaDate() {
        try{
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
                programari.add(prog);
            }
            adapter = new ProgramareAdapter(this);
            adapter.setProgramari(programari);
            recvAppointments.setAdapter(adapter);
            recvAppointments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            progressDialog.dismiss();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}