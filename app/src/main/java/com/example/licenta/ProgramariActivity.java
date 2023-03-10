package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.licenta.Adapters.MedicAdapter;
import com.example.licenta.Adapters.ProgramareAdapter;
import com.example.licenta.Models.Medic;
import com.example.licenta.Models.Pacient;
import com.example.licenta.Models.Programare;
import com.example.licenta.Models.Specialitate;
import com.example.licenta.Models.Specialitati;
import com.example.licenta.Utils.APICommunication;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProgramariActivity extends AppCompatActivity {
    private RecyclerView recvAppointments;
    private ArrayList<Programare> programari;
    private ProgramareAdapter adapter;
    private ProgressDialog progressDialog;
    private Pacient p;
    private MaterialButtonToggleGroup toggleGroup;

    public BroadcastReceiver receivedAppointmentsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("medicReceive", "success");
            boolean isSucces = intent.getBooleanExtra("success", false);
            if (isSucces) {
                loadAppointments();
                reloadAppointmentsAdapter();
                cancelLoadingDialog();
            }
            toggleGroup.clearChecked();
        }
    };

    public BroadcastReceiver receiverAppointmentsDelete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Programare appointment = (Programare) intent.getSerializableExtra("toDelete");
            if (appointment != null) {
                APICommunication.deleteProgramare(appointment, p.getId(), getApplicationContext());
            }
        }
    };

    private void createLoadingDialog() {
        progressDialog = new ProgressDialog(ProgramariActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void cancelLoadingDialog() {
        progressDialog.dismiss();
    }

    private void reloadAppointmentsAdapter() {
        adapter = new ProgramareAdapter(this);
        adapter.setProgramari(programari);
        recvAppointments.setAdapter(adapter);
        recvAppointments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void loadFilteredAppointmentsAdapter(List<Programare> filtered) {
        adapter = new ProgramareAdapter(this);
        adapter.setProgramari((ArrayList<Programare>) filtered);
        recvAppointments.setAdapter(adapter);
        recvAppointments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receivedAppointmentsReceiver, new IntentFilter("apiMessageAppointments"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverAppointmentsDelete, new IntentFilter("progAdapterDelete"));
        createLoadingDialog();
        p = (Pacient) getIntent().getSerializableExtra("pacient");
        APICommunication.getAppointments(p.getId(), getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programari);
        recvAppointments = findViewById(R.id.recvAppointments);
        toggleGroup = findViewById(R.id.toggleAppointments);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                switch (checkedId) {
                    case R.id.btnTogglePast:
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            List<Programare> filtered = programari
                                    .stream()
                                    .filter(programare -> programare.getData().isBefore(LocalDateTime.now()))
                                    .collect(Collectors.toList());
                            loadFilteredAppointmentsAdapter(filtered);
                        }
                        break;
                    case R.id.btnToggleFuture:
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            List<Programare> filtered = programari
                                    .stream()
                                    .filter(programare -> programare.getData().isAfter(LocalDateTime.now()))
                                    .collect(Collectors.toList());
                            loadFilteredAppointmentsAdapter(filtered);
                        }
                        break;
                }
            } else {
                if (group.getCheckedButtonId() == View.NO_ID) {
                    reloadAppointmentsAdapter();
                }
            }
        });
    }

    private void loadAppointments() {
        try {
            programari = new ArrayList<>();
            for (int i = 0; i < APICommunication.appointmentsArray.length(); i++) {
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
                try {
                    JSONObject specObj = (JSONObject) currentMedic.get("specialitate");
                    m.setSpecialitate(new Specialitate(Specialitati.valueOf(specObj.getString("tip")), specObj.getString("descriere")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                prog.setMedic(m);
                programari.add(prog);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}