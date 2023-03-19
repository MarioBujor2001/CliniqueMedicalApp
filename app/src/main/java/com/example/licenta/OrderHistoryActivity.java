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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import org.w3c.dom.Text;

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
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

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
                            generateMoreInfoPopUp(order.get());
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

    public void generateMoreInfoPopUp(Order order) {
        dialogBuilder = new AlertDialog.Builder(OrderHistoryActivity.this);
        final View addPop = getLayoutInflater().inflate(R.layout.order_item_popup, null);
        TextView txtComandaNo = addPop.findViewById(R.id.txtComandaNo);
        TextView txtDateOrdered = addPop.findViewById(R.id.txtDateOrdered);
        TextView txtAmmountOrder = addPop.findViewById(R.id.txtAmmountOrder);
        Button btnGenerator = addPop.findViewById(R.id.btnGenerator);
        ListView lvInvestigationOrdered = addPop.findViewById(R.id.lvInvestigationOrdered);
        txtComandaNo.setText("#" + order.getId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtDateOrdered.setText(order.getData().format(DateTimeFormatter.ISO_DATE));
        }
        float total = 0;
        for (Investigation i : order.getInvestigations()) {
            total += i.getPrice();
        }
        txtAmmountOrder.setText(total + " Ron");
        ArrayAdapter<Investigation> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, order.getInvestigations());
        lvInvestigationOrdered.setAdapter(adapter);
        dialogBuilder.setView(addPop);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().
                setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}