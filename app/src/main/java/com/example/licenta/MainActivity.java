package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.licenta.Models.Pacient;
import com.example.licenta.Utils.APICommunication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView tvUserName;
    private FirebaseAuth mAuth;
    private CardView allMedicsCard;
    private ImageView imgProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main");
        mAuth = FirebaseAuth.getInstance();
        tvUserName = findViewById(R.id.txtForNameDisplay);
        allMedicsCard = findViewById(R.id.allMedicsCard);
        imgProfile = findViewById(R.id.userProfile);
        allMedicsCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MedicsListActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //verificam daca este vreun user logat deja
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else{
//            Toast.makeText(this, "Welcome "+user.getUid(), Toast.LENGTH_SHORT).show();
            APICommunication.getPacient(user.getUid(),getApplicationContext());
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    incarcaDate(user.getUid());
                }
            }, 1000);
        }
    }

    private void incarcaDate(String id){
//        APICommunication.getPacient(id,getApplicationContext());
        Pacient p = new Pacient();
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