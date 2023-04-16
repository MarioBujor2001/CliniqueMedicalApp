package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.Models.Patient;
import com.example.licenta.Models.dto.BodyAnalysisDTO;
import com.example.licenta.Models.enums.ActivityLevels;
import com.example.licenta.Models.enums.Genders;
import com.example.licenta.Utils.APICommunicationV2;

import org.json.JSONException;

public class BodyAnalysisActivity extends AppCompatActivity {
    //main view - entering body details
    private EditText edtWeight, edtHeight;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Spinner spnActivityLevel;
    private Button btnObtain;

    // displaying body info that is already there
    private CardView displayBodyInfoCard;
    private TextView txtKcalBmr, txtKcalRecommended;
    private Button btnChangeBodyDetails;

    private Patient patient;

    public BroadcastReceiver receiveBodyAnalysis = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("bodyReceive", "success");
            boolean isSucces = intent.getBooleanExtra("success", false);
            if (isSucces) {
                loadBodyInfo();
                displayBodyInfoCard.setVisibility(View.VISIBLE);
            } else {
                displayBodyInfoCard.setVisibility(View.GONE);
            }
        }
    };

    private void loadBodyInfo() {
        try {
            txtKcalRecommended.setText(APICommunicationV2.bodyAnalysis.get("recommendedKcal").toString() + "Kcal");
            txtKcalBmr.setText(APICommunicationV2.bodyAnalysis.get("bmr").toString() + "Kcal");
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_analysis);
        //main view - entering body details
        edtHeight = findViewById(R.id.edtHeight);
        edtWeight = findViewById(R.id.edtWeight);
        rgGender = findViewById(R.id.rgGender);
        rbFemale = findViewById(R.id.rbFemale);
        rbMale = findViewById(R.id.rbMale);
        spnActivityLevel = findViewById(R.id.spnActivityLevel);
        btnObtain = findViewById(R.id.btnObtain);
        btnObtain.setOnClickListener(view -> {
            if(validateBodyInput()){
                updateBodyDetails();
            }
        });

        // displaying body info that is already there
        displayBodyInfoCard = findViewById(R.id.displayBodyInfoCard);
        txtKcalBmr = findViewById(R.id.txtKcalBmr);
        txtKcalRecommended = findViewById(R.id.txtKcalRecommended);
        btnChangeBodyDetails = findViewById(R.id.btnChangeBodyDetails);
        btnChangeBodyDetails.setOnClickListener(view -> {
            displayBodyInfoCard.setVisibility(View.GONE);
        });
    }

    private void updateBodyDetails() {
        BodyAnalysisDTO dto = new BodyAnalysisDTO();
        dto.setWeight(Float.parseFloat(edtWeight.getText().toString()));
        dto.setHeight(Float.parseFloat(edtHeight.getText().toString()));
        switch (rgGender.getCheckedRadioButtonId()){
            case R.id.rbFemale:
                dto.setGender(Genders.Female);
                break;
            case R.id.rbMale:
                dto.setGender(Genders.Male);
                break;
        }
        int id = spnActivityLevel.getSelectedItemPosition();
        switch (id){
            case 0:
                dto.setActivityLevel(ActivityLevels.Sedentary);
                break;
            case 1:
                dto.setActivityLevel(ActivityLevels.Light);
                break;
            case 2:
                dto.setActivityLevel(ActivityLevels.Moderate);
                break;
            case 3:
                dto.setActivityLevel(ActivityLevels.Active);
                break;
        }
        APICommunicationV2.addBodyAnalysis(getApplicationContext(), patient.getId(),dto);
    }

    private boolean validateBodyInput(){
        if(edtHeight.getText().toString().isEmpty()){
            edtHeight.setError("Cannot be empty");
            edtHeight.requestFocus();
            return false;
        }
        if(edtWeight.getText().toString().isEmpty()){
            edtWeight.setError("Cannot be empty");
            edtWeight.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        patient = (Patient) getIntent().getSerializableExtra("pacient");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiveBodyAnalysis, new IntentFilter("apiMessageGetBodyAnalysis"));
        APICommunicationV2.getBodyAnalysis(getApplicationContext(), patient.getId());
    }
}