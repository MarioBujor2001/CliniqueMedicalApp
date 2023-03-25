package com.example.licenta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.licenta.Adapters.InvestigationAdapter;
import com.example.licenta.Adapters.OrdersAdapter;
import com.example.licenta.Models.Investigation;
import com.example.licenta.Models.Order;
import com.example.licenta.Models.Pacient;
import com.example.licenta.Models.Specialitate;
import com.example.licenta.Models.Specialitati;
import com.example.licenta.Utils.APICommunication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recvOrders;
    private OrdersAdapter adapter;
    private List<Order> orderList;
    private Pacient pacient;
    private ProgressDialog progressDialog;

    //forPopup
    private CardView popUpCard;
    private TextView txtComandaNo;
    private TextView txtDateOrdered;
    private TextView txtAmmountOrder;
    private Button btnGenerator;
    private RecyclerView recvInvestigationOrdered;
    private InvestigationAdapter investigationAdapter;

    private BroadcastReceiver receiveMoreInfoOrder = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if (isSucces) {
                int orderId = intent.getIntExtra("orderId", -1);
                if (orderId != -1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Optional<Order> order = orderList.stream().filter(o -> o.getId() == orderId).findFirst();
                        if (order.isPresent()) {
                            generateMoreInfo(order.get());
                        }
                    }
                }
            }
        }
    };

    private BroadcastReceiver receiveOrders = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if (isSucces) {
                loadOrders();
                reloadOrdersAdapter();
                cancelLoadingDialog();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        recvOrders = findViewById(R.id.recvOrders);
        popUpCard = findViewById(R.id.popUpCard);
        txtComandaNo = findViewById(R.id.txtComandaNo);
        txtDateOrdered = findViewById(R.id.txtDateOrdered);
        txtAmmountOrder = findViewById(R.id.txtAmmountOrder);
        btnGenerator = findViewById(R.id.btnGenerator);
        recvInvestigationOrdered = findViewById(R.id.lvInvestigationOrdered);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pacient = (Pacient) getIntent().getSerializableExtra("pacient");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiveOrders, new IntentFilter("apiMessageOrdersReceived"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiveMoreInfoOrder, new IntentFilter("receiveMoreInfoOrder"));
        if (pacient != null) {
            APICommunication.getOrders(getApplicationContext(), pacient);
        }
        createLoadingDialog();
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

    public void loadOrders() {
        try {
            orderList = new ArrayList<>();
            for (int i = 0; i < APICommunication.ordersArray.length(); i++) {
                JSONObject currentOrder = APICommunication.ordersArray.getJSONObject(i);
                Order order = new Order();
                order.setId(Integer.valueOf(currentOrder.getString("id")));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    order.setData(LocalDateTime.parse(currentOrder.getString("data"),
                            DateTimeFormatter.ISO_DATE_TIME));
                }
                JSONArray investigationsArray = currentOrder.getJSONArray("investigations");
                List<Investigation> investigations = new ArrayList<>();
                for (int j = 0; j < investigationsArray.length(); j++) {
                    JSONObject currentInvestigation = investigationsArray.getJSONObject(j);
                    Investigation investigation = new Investigation();
                    investigation.setId(currentInvestigation.getInt("id"));
                    investigation.setName(currentInvestigation.getString("nume"));
                    investigation.setPrice(((Double) currentInvestigation.get("pret")).floatValue());
                    Log.i("to check invest", investigation.toString());
                    try {
                        JSONObject specObj = (JSONObject) currentInvestigation.get("specialitate");
                        investigation.setSpecialty(new Specialitate(Specialitati.valueOf(specObj.getString("tip")), specObj.getString("descriere")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    investigations.add(investigation);
                }
                order.setInvestigations(investigations);
                orderList.add(order);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void reloadOrdersAdapter() {
        adapter = new OrdersAdapter(this);
        adapter.setOrders(orderList);
        recvOrders.setAdapter(adapter);
        recvOrders.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    public void generateMoreInfo(Order order) {
        txtComandaNo.setText("#" + order.getId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtDateOrdered.setText(order.getData().format(DateTimeFormatter.ISO_DATE));
        }
        float total = 0;
        for (Investigation i : order.getInvestigations()) {
            total += i.getPrice();
        }
        txtAmmountOrder.setText(total + " Ron");
        reloadInvestigationAdapter(order.getInvestigations());
        popUpCard.setVisibility(View.VISIBLE);
    }

    private void reloadInvestigationAdapter(List<Investigation> investigations) {
        investigationAdapter = new InvestigationAdapter(getApplicationContext(), R.layout.investigation_nobutton_item);
        investigationAdapter.setInvestigations((ArrayList<Investigation>) investigations);
        recvInvestigationOrdered.setAdapter(investigationAdapter);
        recvInvestigationOrdered.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void createPDF() throws FileNotFoundException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath,"user.pdf");
        OutputStream outStream = new FileOutputStream(file);
    }

    @Override
    public void onBackPressed() {
        if(popUpCard.getVisibility()==View.VISIBLE){
            popUpCard.setVisibility(View.GONE);
        }else{
            finish();
        }
    }
}