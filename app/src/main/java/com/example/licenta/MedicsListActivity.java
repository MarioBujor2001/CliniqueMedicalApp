package com.example.licenta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.example.licenta.Models.enums.Specialties;
import com.example.licenta.Utils.APICommunicationV2;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    //overlay appointment
    private CardView appointmentOverlay;
    private Button btnChoseDate;
    private Button btnChoseTime;
    private Button btnVerifDisp;
    private TextView tvNumeMedic;
    private TextView tvData;
    private TextView tvOra;
    private EditText edtMotivVizita;

    public BroadcastReceiver receivedMedicsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if (isSucces) {
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
            if (isSucces) {
                if (!APICommunicationV2.invalidAppointment) {
                    btnProgrameaza.setEnabled(true);
                    Toast.makeText(MedicsListActivity.this, "This date is available", Toast.LENGTH_SHORT).show();
                } else {
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
            cancelLoadingDialog();
            if (isSucces) {
                Toast.makeText(MedicsListActivity.this, "Appointment made successfully!", Toast.LENGTH_SHORT).show();
                appointmentOverlay.setVisibility(View.GONE);
            }
        }
    };

    private void createLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void cancelLoadingDialog() {
        progressDialog.dismiss();
    }

    private void reloadMedicsAdapter() {
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
        if (APICommunicationV2.mediciArray == null) {
            APICommunicationV2.getMedics(getApplicationContext());
        } else {
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
            if (isChecked) {
                switch (checkedId) {
                    case R.id.btnToggleName:
                        nameSearchCriteria = true;
                        searchDoctor.setHint(R.string.cauta_medic);
                        break;
                    case R.id.btnToggleSpec:
                        nameSearchCriteria = false;
                        searchDoctor.setHint(R.string.cauta_spec);
                        break;
                }
            } else {
                if (group.getCheckedButtonId() == View.NO_ID) {
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
                    if (nameSearchCriteria) {
                        filteredMedici = (ArrayList<Medic>) medici.stream()
                                .filter(medic -> (medic.getFirstName() + medic.getLastName()).toLowerCase().contains(s.toString().toLowerCase()))
                                .collect(Collectors.toList());
                        adapter.setMedici(filteredMedici);
                    } else {
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
        //overlay init
        appointmentOverlay = findViewById(R.id.appointmentOverlay);
        btnChoseDate = findViewById(R.id.btnChoseDataPOP);
        btnChoseTime = findViewById(R.id.btnChoseOraPOP);
        btnVerifDisp = findViewById(R.id.btnVerifDispPOP);
        tvNumeMedic = findViewById(R.id.tvNumeMedicProgramarePOP);
        tvData = findViewById(R.id.tvDataPOP);
        tvOra = findViewById(R.id.tvOraPOP);
        edtMotivVizita = findViewById(R.id.edtMotivVizita);
        btnProgrameaza = findViewById(R.id.btnProgrameazaPOP);
    }

    private void loadMedics() {
        try {
            medici = new ArrayList<>();
            for (int i = 0; i < APICommunicationV2.mediciArray.length(); i++) {
                JSONObject currentMedic = APICommunicationV2.mediciArray.getJSONObject(i);
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

                try {
                    JSONObject specObj = (JSONObject) currentMedic.get("specialty");
                    m.setSpecialty(new Specialty(Specialties.valueOf(specObj.getString("type")),
                            specObj.getString("description")));
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
        appointmentOverlay.setVisibility(View.VISIBLE);
        Medic m;
        if (filtered) {
            m = filteredMedici.get(position);
        } else {
            m = medici.get(position);
        }
        tvNumeMedic.setText("Dr. " + m.getFirstName() + " " + m.getLastName());
        btnChoseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int mYear = cal.get(Calendar.YEAR);
                int mMonth = cal.get(Calendar.MONTH);
                int mDay = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MedicsListActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
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
                                if(validateDate(year, month+1, dayOfMonth)) {
                                    tvData.setText(date);
                                    btnChoseTime.setEnabled(true);
                                }else{
                                    Toast.makeText(MedicsListActivity.this, "Cannot make reservation in the past!", Toast.LENGTH_SHORT).show();
                                }
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
                                if(validateDTime(hourOfDay, minute)){
                                    tvOra.setText(time);
                                    btnVerifDisp.setEnabled(true);
                                }else{
                                    Toast.makeText(MedicsListActivity.this, "Working hours 8:00-20:00, half hour increments!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.setTitle("Alege ora");
                timePickerDialog.show();
            }
        });
        btnVerifDisp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APICommunicationV2.pingProgramare(initProgramare(position, filtered,
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
                APICommunicationV2.postAppointment(initProgramare(position, filtered,
                                tvData.getText().toString(),
                                tvOra.getText().toString(),
                                edtMotivVizita.getText().toString()),
                        p, getApplicationContext());
                createLoadingDialog();
            }
        });
    }

    private boolean validateDate(int year, int month, int dayOfMonth) {
        LocalDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
            LocalDateTime selected = LocalDateTime.of(year,month, dayOfMonth,0,0);
            return selected.isAfter(now);
        }
        return false;
    }

    private boolean validateDTime(int hourOfDay, int minute) {
        if(hourOfDay < 8 || hourOfDay > 20){
            return false;
        }
        if(minute != 0 && minute != 30){
            return false;
        }
        return true;
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
        if (!observatii.equals("")) {
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

    @Override
    public void onBackPressed() {
        if (appointmentOverlay.getVisibility() == View.VISIBLE) {
            appointmentOverlay.setVisibility(View.GONE);
        } else {
            finish();
        }
    }
}