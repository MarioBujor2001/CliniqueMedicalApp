package com.example.licenta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.licenta.Models.Investigation;
import com.example.licenta.Models.Specialitate;
import com.example.licenta.Models.Specialitati;

import java.util.ArrayList;

public class InvestigationsActivity extends AppCompatActivity {
    private RecyclerView recvInvestigations;
    private ArrayList<Investigation> investigations;
    private InvestigationAdapter adapter;
    private TextView txtInvestigationsTotal;
    private float currentTotal = 0;
    private Button btnSeeCart;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    public BroadcastReceiver updateCartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float itemValue = intent.getFloatExtra("value",0);
            currentTotal += itemValue;
            txtInvestigationsTotal.setText(currentTotal + " Ron");
            Log.i("broadcast", "received");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigations);
        recvInvestigations = findViewById(R.id.recvInvestigations);
        txtInvestigationsTotal = findViewById(R.id.txtTotalInvestigationOrder);
        btnSeeCart = findViewById(R.id.btnSeeCart);
        txtInvestigationsTotal.setText(currentTotal + " Ron");
        investigations = new ArrayList<>();
        investigations.add(new Investigation("Invest1", 120, new Specialitate(Specialitati.dermatologie,"ceva")));
        investigations.add(new Investigation("Invest2", 240, new Specialitate(Specialitati.cardiologie,"ceva")));
        adapter = new InvestigationAdapter(getApplication());
        adapter.setInvestigations(investigations);
        recvInvestigations.setAdapter(adapter);
        recvInvestigations.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
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
        ArrayAdapter<Investigation> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, investigations);
        lvOrder.setAdapter(adapter);

        dialogBuilder.setView(checkoutPop);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}