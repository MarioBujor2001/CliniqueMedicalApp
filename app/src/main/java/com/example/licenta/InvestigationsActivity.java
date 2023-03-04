package com.example.licenta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.licenta.Adapters.InvestigationAdapter;
import com.example.licenta.Models.Investigatie;
import com.example.licenta.Models.Specialitate;
import com.example.licenta.Models.Specialitati;
import com.example.licenta.Utils.APICommunication;
import com.google.android.gms.common.api.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InvestigationsActivity extends AppCompatActivity {
    private RecyclerView recvInvestigations;
    private ArrayList<Investigatie> investigations;
    private InvestigationAdapter adapter;
    private TextView txtInvestigationsTotal;
    private float currentTotal = 0;
    private Button btnSeeCart;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    public BroadcastReceiver updateCartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float itemValue = intent.getFloatExtra("value",0);
            currentTotal += itemValue;
            txtInvestigationsTotal.setText(currentTotal + " Ron");
            Log.i("broadcast", "received");
        }
    };
    public BroadcastReceiver receivedInvestigationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success",false);
            if(isSucces){
                loadInvestigations();
                reloadInvestigationAdapter();
                cancelLoadingDialog();
            }
        }
    };

    private void loadInvestigations(){
        try{
            investigations = new ArrayList<>();
            for(int i=0;i<APICommunication.investigationsArray.length();i++){
                JSONObject currentInvestigation = APICommunication.investigationsArray.getJSONObject(i);
                Investigatie investigation = new Investigatie();
                investigation.setName(currentInvestigation.getString("nume"));
                investigation.setPrice(((Double)currentInvestigation.get("pret")).floatValue());
                try{
                    JSONObject specObj = (JSONObject) currentInvestigation.get("specialitate");
                    investigation.setSpecialty(new Specialitate(Specialitati.valueOf(specObj.getString("tip")), specObj.getString("descriere")));
                }catch (Exception e){
                    e.printStackTrace();
                }
                investigations.add(investigation);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void reloadInvestigationAdapter(){
        adapter = new InvestigationAdapter(getApplicationContext());
        adapter.setInvestigations(investigations);
        recvInvestigations.setAdapter(adapter);
        recvInvestigations.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigations);
        recvInvestigations = findViewById(R.id.recvInvestigations);
        txtInvestigationsTotal = findViewById(R.id.txtTotalInvestigationOrder);
        btnSeeCart = findViewById(R.id.btnSeeCart);
        txtInvestigationsTotal.setText(currentTotal + " Ron");
        investigations = new ArrayList<>();
        investigations.add(new Investigatie("Invest1", 120, new Specialitate(Specialitati.dermatologie,"ceva"), "ceva"));
        investigations.add(new Investigatie("Invest2", 240, new Specialitate(Specialitati.cardiologie,"ceva"), "ceva"));
        //reloadInvestigationAdapter();
        LocalBroadcastManager.getInstance(this).registerReceiver(updateCartReceiver, new IntentFilter("updateCart"));
        btnSeeCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCheckoutSummaryPopUp();
            }
        });
    }

    public void createCheckoutSummaryPopUp(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View checkoutPop = getLayoutInflater().inflate(R.layout.checkout_popup, null);
        ListView lvOrder = checkoutPop.findViewById(R.id.lvOrder);
        Button btnCheckout = checkoutPop.findViewById(R.id.btnCheckOut);
        ArrayAdapter<Investigatie> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, investigations);
        lvOrder.setAdapter(adapter);

        dialogBuilder.setView(checkoutPop);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receivedInvestigationReceiver, new IntentFilter("apiMessageInvestigation"));
        createLoadingDialog();
        if(APICommunication.investigationsArray==null){
            //cerem date
            APICommunication.getInvestigations(getApplication());
        }else {
            //doar reincarcam datele
            loadInvestigations();
            reloadInvestigationAdapter();
            cancelLoadingDialog();
        }
        //broadcast receiver va incarca datele;
    }

    private void createLoadingDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void cancelLoadingDialog(){
        progressDialog.dismiss();
    }
}