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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.Adapters.CheckoutAdapter;
import com.example.licenta.Adapters.InvestigationAdapter;
import com.example.licenta.Models.Investigation;
import com.example.licenta.Models.Patient;
import com.example.licenta.Models.Specialty;
import com.example.licenta.Models.Specialties;
import com.example.licenta.Utils.APICommunication;
import com.example.licenta.Utils.APICommunicationV2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InvestigationsActivity extends AppCompatActivity {
    private RecyclerView recvInvestigations;
    private ArrayList<Investigation> investigations;
    private ArrayList<Investigation> currentInvestigationsCart;
    private InvestigationAdapter adapter;
    private TextView txtInvestigationsTotal;
    private float currentTotal = 0;
    private Button btnSeeCart;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private Patient pacient;
    public BroadcastReceiver updateCartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float itemValue = intent.getFloatExtra("value", 0);
            Investigation investigation = (Investigation) intent.getSerializableExtra("investigation");
            if (!currentInvestigationsCart.contains(investigation)) {
                currentTotal += itemValue;
                txtInvestigationsTotal.setText(currentTotal + " Ron");
                currentInvestigationsCart.add(investigation);
            } else {
                Toast.makeText(context, "Already in cart!", Toast.LENGTH_SHORT).show();
            }
        }
    };
    public BroadcastReceiver receivedInvestigationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if (isSucces) {
                loadInvestigations();
                reloadInvestigationAdapter();
                cancelLoadingDialog();
            }
        }
    };

    public BroadcastReceiver receivedOrderInsertConfirmation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if (isSucces) {
                dialog.dismiss();
                Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                currentInvestigationsCart = new ArrayList<>();
                resetOrderAmount();
            }
        }
    };

    private void resetOrderAmount(){
        currentTotal = 0;
        txtInvestigationsTotal.setText(currentTotal + " Ron");
    }

    private void loadInvestigations() {
        try {
            investigations = new ArrayList<>();
            for (int i = 0; i < APICommunicationV2.investigationsArray.length(); i++) {
                JSONObject currentInvestigation = APICommunicationV2.investigationsArray.getJSONObject(i);
                Investigation investigation = new Investigation();
                investigation.setId(currentInvestigation.getInt("id"));
                investigation.setName(currentInvestigation.getString("name"));
                investigation.setPrice(((Double) currentInvestigation.get("price")).floatValue());
                Log.i("to check invest", investigation.toString());
                try {
                    JSONObject specObj = (JSONObject) currentInvestigation.get("specialty");
                    investigation.setSpecialty(new Specialty(Specialties.valueOf(specObj.getString("type")), specObj.getString("description")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                investigations.add(investigation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reloadInvestigationAdapter() {
        adapter = new InvestigationAdapter(getApplicationContext(), R.layout.investigation_item);
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
        //reloadInvestigationAdapter();
        LocalBroadcastManager.getInstance(this).registerReceiver(updateCartReceiver, new IntentFilter("updateCart"));
        btnSeeCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCheckoutSummaryPopUp();
            }
        });
    }

    public void createCheckoutSummaryPopUp() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View checkoutPop = getLayoutInflater().inflate(R.layout.checkout_popup, null);
        ListView lvOrder = checkoutPop.findViewById(R.id.lvOrder);
        Button btnCheckout = checkoutPop.findViewById(R.id.btnCheckOut);
        CheckoutAdapter adapter = new CheckoutAdapter(InvestigationsActivity.this, currentInvestigationsCart);
        lvOrder.setAdapter(adapter);

        btnCheckout.setOnClickListener(view -> {
            if (!currentInvestigationsCart.isEmpty()) {
                btnCheckout.setEnabled(false);
                APICommunicationV2.postOrder(getApplicationContext(), pacient, currentInvestigationsCart);
            }
        });

        dialogBuilder.setView(checkoutPop);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onStart() {
        super.onStart();
        pacient = (Patient) getIntent().getSerializableExtra("pacient");
        currentInvestigationsCart = new ArrayList<>();
        LocalBroadcastManager.getInstance(this).registerReceiver(receivedInvestigationReceiver, new IntentFilter("apiMessageInvestigation"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receivedOrderInsertConfirmation, new IntentFilter("apiMessageOrderCreate"));
        createLoadingDialog();
        if (APICommunicationV2.investigationsArray == null) {
            //cerem date
            APICommunicationV2.getInvestigations(getApplicationContext());
        } else {
            //doar reincarcam datele
            loadInvestigations();
            reloadInvestigationAdapter();
            cancelLoadingDialog();
        }
        //broadcast receiver va incarca datele;
    }

    private void createLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void cancelLoadingDialog() {
        progressDialog.dismiss();
    }
}