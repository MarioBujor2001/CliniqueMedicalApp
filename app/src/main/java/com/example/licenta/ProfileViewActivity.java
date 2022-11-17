package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.licenta.Models.Pacient;

public class ProfileViewActivity extends AppCompatActivity {
    private CardView btnBackProfile, btnEditProfile;
    private ImageView imgProfile;
    private TextView tvName, tvEmail;
    private Pacient p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        btnBackProfile = findViewById(R.id.backButtonProfile);
        btnEditProfile = findViewById(R.id.editProfile);
        imgProfile = findViewById(R.id.userProfile);
        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);

        p = (Pacient) getIntent().getSerializableExtra("PROFILE");
        if(p!=null){
            Glide.with(this).asBitmap().load(p.getPhoto()).into(imgProfile);
            tvName.setText(p.getFirstName()+" "+p.getLastName());
            tvEmail.setText(p.getEmail());
        }

        btnBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}