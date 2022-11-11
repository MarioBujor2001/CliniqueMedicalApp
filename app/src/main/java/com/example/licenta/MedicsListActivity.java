package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.licenta.Models.Medic;
import com.example.licenta.Utils.APICommunication;

import java.util.ArrayList;

public class MedicsListActivity extends AppCompatActivity {
    private RecyclerView recvMedici;
    private ArrayList<Medic> medici;
    private MedicAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medics_list);

        recvMedici = findViewById(R.id.recvMedics);

        APICommunication.getMedics(getApplicationContext());
    }
}