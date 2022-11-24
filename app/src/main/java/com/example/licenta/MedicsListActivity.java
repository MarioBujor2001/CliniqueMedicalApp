package com.example.licenta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private Pacient p;
    private MaterialButtonToggleGroup toggleGroup;
    private boolean nameSearchCriteria = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medics_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        p = (Pacient) getIntent().getSerializableExtra("pacient");

        medici = new ArrayList<>();
        APICommunication.getMedics(getApplicationContext());
        recvMedici = findViewById(R.id.recvMedics);
        searchDoctor = findViewById(R.id.searchForDoctorName);
        toggleGroup = findViewById(R.id.toggleMedicList);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                incarcaDate();
            }
        }, 1000);

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
                                .filter(medic -> (medic.getSpecialitate().toString()).contains(s.toString().toLowerCase()))
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

    private void incarcaDate() {
        try {
            for (int i = 0; i < APICommunication.mediciArray.length(); i++) {
                JSONObject currentMedic = APICommunication.mediciArray.getJSONObject(i);
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
                medici.add(m);
                Log.i("medic", m.toString());
            }
            adapter = new MedicAdapter(this, this);
            adapter.setMedici(medici);
            recvMedici.setAdapter(adapter);
            recvMedici.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            progressDialog.dismiss();
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
        Button btnProgrameaza = addPop.findViewById(R.id.btnProgrameazaPOP);
        TextView tvNumeMedic = addPop.findViewById(R.id.tvNumeMedicProgramarePOP);
        TextView tvData = addPop.findViewById(R.id.tvDataPOP);
        TextView tvOra = addPop.findViewById(R.id.tvOraPOP);
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
                APICommunication.pingProgramare(initProgramare(position, filtered, tvData.getText().toString(), tvOra.getText().toString()),
                        p, getApplicationContext());
                progressDialog = new ProgressDialog(MedicsListActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!APICommunication.invalidAppointment) {
                            btnProgrameaza.setEnabled(true);
                            Toast.makeText(MedicsListActivity.this, "This date is available", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MedicsListActivity.this, "Date and time already taken", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }, 1000);
            }
        });
        btnProgrameaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APICommunication.postProgramare(initProgramare(position, filtered, tvData.getText().toString(), tvOra.getText().toString()),
                        p, getApplicationContext());
                progressDialog = new ProgressDialog(MedicsListActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(MedicsListActivity.this, "Appointment made successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }, 1000);
            }
        });
        dialogBuilder.setView(addPop);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().
                setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private Programare initProgramare(int position, boolean filtered, String data, String ora) {
        Programare prog = new Programare();
        Medic m;
        if (filtered) {
            m = filteredMedici.get(position);
        } else {
            m = medici.get(position);
        }
        prog.setMedic(m);
        prog.setPacient(p);
        String dataOra = data.substring(6) + " " + ora.substring(5);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prog.setData(LocalDateTime.parse(dataOra, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
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