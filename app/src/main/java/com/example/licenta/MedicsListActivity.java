package com.example.licenta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.licenta.Adapters.MedicAdapter;
import com.example.licenta.Models.Medic;
import com.example.licenta.Models.Patient;
import com.example.licenta.Models.Appointment;
import com.example.licenta.Models.Specialty;
import com.example.licenta.Models.Specialties;
import com.example.licenta.Utils.APICommunication;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

public class MedicsListActivity extends AppCompatActivity implements RecyclerViewInterface {
    private RecyclerView recvMedici;
    private ArrayList<Medic> medici;
    private ArrayList<Medic> filteredMedici;
    private MedicAdapter adapter;
    private ProgressDialog progressDialog;
    private EditText searchDoctor;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Patient p;
    private MaterialButtonToggleGroup toggleGroup;
    private boolean nameSearchCriteria = true;
    private Button btnProgrameaza;

    public BroadcastReceiver receivedMedicsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if(isSucces){
                loadMedics();
                reloadMedicsAdapter();
                cancelLoadingDialog();
            }
        }
    };

    public BroadcastReceiver receiveAvailableReservation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if(isSucces){
                if (!APICommunication.invalidAppointment) {
                    btnProgrameaza.setEnabled(true);
                    Toast.makeText(MedicsListActivity.this, "This date is available", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MedicsListActivity.this, "Date and time already taken", Toast.LENGTH_SHORT).show();
                }
                cancelLoadingDialog();
            }
        }
    };

    public BroadcastReceiver receiveSuccessReservation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if(isSucces){
                cancelLoadingDialog();
                Toast.makeText(MedicsListActivity.this, "Appointment made successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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

    private void reloadMedicsAdapter(){
        adapter = new MedicAdapter(this, this);
        adapter.setMedici(medici);
        recvMedici.setAdapter(adapter);
        recvMedici.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receivedMedicsReceiver, new IntentFilter("apiMessageMedics"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiveAvailableReservation, new IntentFilter("apiMessageReservation"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiveSuccessReservation, new IntentFilter("apiMessageSuccessReservation"));
        createLoadingDialog();
        if(APICommunication.mediciArray==null){
            APICommunication.getMedics(getApplicationContext());
        }else{
            loadMedics();
            reloadMedicsAdapter();
            cancelLoadingDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medics_list);
        p = (Patient) getIntent().getSerializableExtra("pacient");
        recvMedici = findViewById(R.id.recvMedics);
        searchDoctor = findViewById(R.id.searchForDoctorName);
        toggleGroup = findViewById(R.id.toggleMedicList);
        toggleGroup.check(R.id.btnToggleName);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if(isChecked){
                switch (checkedId){
                    case R.id.btnToggleName:
                        nameSearchCriteria = true;
                        searchDoctor.setHint(R.string.cauta_medic);
                        break;
                    case R.id.btnToggleSpec:
                        nameSearchCriteria = false;
                        searchDoctor.setHint(R.string.cauta_spec);
                        break;
                }
            }else{
                if(group.getCheckedButtonId()==View.NO_ID){
                    toggleGroup.check(R.id.btnToggleName);
                }
            }
        });

        searchDoctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if(nameSearchCriteria){
                        filteredMedici = (ArrayList<Medic>) medici.stream()
                                .filter(medic -> (medic.getFirstName() + medic.getLastName()).toLowerCase().contains(s.toString().toLowerCase()))
                                .collect(Collectors.toList());
                        adapter.setMedici(filteredMedici);
                    }else{
                        filteredMedici = (ArrayList<Medic>) medici.stream()
                                .filter(medic -> (medic.getSpecialty().toString()).contains(s.toString().toLowerCase()))
                                .collect(Collectors.toList());
                        adapter.setMedici(filteredMedici);
                    }
                }
                if (s.toString().equals("")) {
                    filteredMedici = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadMedics() {
        try {
            medici = new ArrayList<>();
            for (int i = 0; i < APICommunication.mediciArray.length(); i++) {
                JSONObject currentMedic = APICommunication.mediciArray.getJSONObject(i);
                Medic m = new Medic();
                m.setId(currentMedic.getString("id"));
                m.setFirstName(currentMedic.getString("firstName"));
                m.setLastName(currentMedic.getString("lastName"));
                m.setEmail(currentMedic.getString("email"));
                m.setAge(currentMedic.getInt("varsta"));
                m.setAddress(currentMedic.getString("adresa"));
                m.setPhotoUrl(currentMedic.getString("photo"));
                m.setCNP(currentMedic.getString("cnp"));
                m.setRating((float) currentMedic.getDouble("rating"));
                m.setSeniority(currentMedic.getInt("vechime"));

                try {
                    JSONObject specObj = (JSONObject) currentMedic.get("specialitate");
                    m.setSpecialty(new Specialty(Specialties.valueOf(specObj.getString("tip")), specObj.getString("descriere")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                medici.add(m);
                Log.i("medic", m.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void createMedicPopUp(int position, boolean filtered) {

        dialogBuilder = new AlertDialog.Builder(MedicsListActivity.this);
        final View addPop = getLayoutInflater().inflate(R.layout.medic_popup, null);
        Medic m;
        if (filtered) {
            m = filteredMedici.get(position);
        } else {
            m = medici.get(position);
        }
        Button btnChoseDate = addPop.findViewById(R.id.btnChoseDataPOP);
        Button btnChoseTime = addPop.findViewById(R.id.btnChoseOraPOP);
        Button btnVerifDisp = addPop.findViewById(R.id.btnVerifDispPOP);
        btnProgrameaza = addPop.findViewById(R.id.btnProgrameazaPOP);
        TextView tvNumeMedic = addPop.findViewById(R.id.tvNumeMedicProgramarePOP);
        TextView tvData = addPop.findViewById(R.id.tvDataPOP);
        TextView tvOra = addPop.findViewById(R.id.tvOraPOP);
        EditText edtMotivVizita = addPop.findViewById(R.id.edtMotivVizita);
        tvNumeMedic.setText("Dr. " + m.getFirstName() + " " + m.getLastName());
        btnChoseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int mYear = cal.get(Calendar.YEAR);
                int mMonth = cal.get(Calendar.MONTH);
                int mDay = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MedicsListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = "Data: ";
                        if (dayOfMonth >= 10) {
                            date += dayOfMonth + "/";
                        } else {
                            date += "0" + dayOfMonth + "/";
                        }
                        if (month + 1 >= 10) {
                            date += (month + 1) + "/" + year;
                        } else {
                            date += "0" + (month + 1) + "/" + year;
                        }
                        tvData.setText(date);
                        btnChoseTime.setEnabled(true);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnChoseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MedicsListActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String time = "Ora: ";
                                if (hourOfDay >= 10) {
                                    time += hourOfDay + ":";
                                } else {
                                    time += "0" + hourOfDay + ":";
                                }
                                if (minute >= 10) {
                                    time += minute;
                                } else {
                                    time += "0" + minute;
                                }
                                tvOra.setText(time);
                                btnVerifDisp.setEnabled(true);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.setTitle("Alege ora");
                timePickerDialog.show();
            }
        });
        btnVerifDisp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APICommunication.pingProgramare(initProgramare(position, filtered,
                                tvData.getText().toString(),
                                tvOra.getText().toString(),
                                edtMotivVizita.getText().toString()),
                        p, getApplicationContext());
                createLoadingDialog();
            }
        });
        btnProgrameaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APICommunication.postProgramare(initProgramare(position, filtered,
                                tvData.getText().toString(),
                                tvOra.getText().toString(),
                                edtMotivVizita.getText().toString()),
                        p, getApplicationContext());
                createLoadingDialog();
            }
        });
        dialogBuilder.setView(addPop);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().
                setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private Appointment initProgramare(int position, boolean filtered, String data, String ora, String observatii) {
        Appointment prog = new Appointment();
        Medic m;
        if (filtered) {
            m = filteredMedici.get(position);
        } else {
            m = medici.get(position);
        }
        prog.setMedic(m);
        prog.setPatient(p);
        if(!observatii.equals("")){
            prog.setComments(observatii);
        }
        String dataOra = data.substring(6) + " " + ora.substring(5);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prog.setDate(LocalDateTime.parse(dataOra, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }
        return prog;
    }

    @Override
    public void onItemClick(int position) {
        if (filteredMedici != null && filteredMedici.size() != 0) {
            createMedicPopUp(position, true);
        } else {
            createMedicPopUp(position, false);
        }
    }
}