package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.licenta.Models.Medic;
import com.example.licenta.Models.Specialitate;
import com.example.licenta.Models.Specialitati;
import com.example.licenta.Utils.APICommunication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MedicsListActivity extends AppCompatActivity {
    private RecyclerView recvMedici;
    private ArrayList<Medic> medici;
    private ArrayList<Medic> filteredMedici;
    private MedicAdapter adapter;
    private ProgressDialog progressDialog;
    private EditText searchDoctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medics_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        medici = new ArrayList<>();
        APICommunication.getMedics(getApplicationContext());
        recvMedici = findViewById(R.id.recvMedics);
        searchDoctorName = findViewById(R.id.searchForDoctorName);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                incarcaDate();
            }
        }, 1000);

        searchDoctorName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    filteredMedici = (ArrayList<Medic>) medici.stream()
                            .filter(medic -> (medic.getFirstName() + medic.getLastName()).contains(s))
                            .collect(Collectors.toList());
                    adapter.setMedici(filteredMedici);
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
                if (currentMedic.getJSONArray("specialitati").length() > 0 && currentMedic.getJSONArray("specialitati").get(0) != null) {
                    try {
                        JSONObject obj = (JSONObject) currentMedic.getJSONArray("specialitati").get(0);
                        obj.getString("tip");
                        m.getSpecialitati().add(new Specialitate(Specialitati.valueOf(obj.getString("tip")), obj.getString("descriere")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                medici.add(m);
                Log.i("medic", m.toString());
            }
            adapter = new MedicAdapter(this);
            adapter.setMedici(medici);
            recvMedici.setAdapter(adapter);
            recvMedici.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            progressDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}