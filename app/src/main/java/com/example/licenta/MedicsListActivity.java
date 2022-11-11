package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.licenta.Models.Medic;
import com.example.licenta.Utils.APICommunication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MedicsListActivity extends AppCompatActivity {
    private RecyclerView recvMedici;
    private ArrayList<Medic> medici;
    private MedicAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medics_list);
        medici = new ArrayList<>();
        APICommunication.getMedics(getApplicationContext());
        recvMedici = findViewById(R.id.recvMedics);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                incarcaDate();
            }
        }, 1000);
    }

    private void incarcaDate(){
        try{
            for(int i=0;i<APICommunication.mediciArray.length();i++){
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
                medici.add(m);
                Log.i("medic",m.toString());
            }
            adapter = new MedicAdapter(this);
            adapter.setMedici(medici);
            recvMedici.setAdapter(adapter);
            recvMedici.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}