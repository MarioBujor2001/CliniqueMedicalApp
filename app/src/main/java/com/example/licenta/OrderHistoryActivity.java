package com.example.licenta;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
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
    private Order currentOrder = null;

    // for pdf generation
    private final int pageHeight = 1120;
    private final int pagewidth = 792;
    private Bitmap bmp, scaledBmp;
    private static final int PERMISSION_REQUEST_CODE = 200;

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
                            currentOrder = order.get();
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
        btnGenerator.setOnClickListener(view -> {
            if (!checkPermission()) {
                requestPermission();
            } else {
                createPDF(currentOrder);
            }
        });
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

    private void createPDF(Order order) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint title = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        title.setTextSize(25);
        title.setFakeBoldText(true);

        title.setColor(ContextCompat.getColor(this, R.color.purple_200));

        canvas.drawText("Factura cu numarul: " + order.getId(), 50, 50, title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.drawText("Din data de: " + order.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), 50, 80, title);
        }


        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextSize(25);

        title.setStrokeWidth(5);
        canvas.drawLine(10, 150, pagewidth-10, 150, title);

        int startY = 200;
        for (Investigation i : order.getInvestigations()) {
            title.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(i.getName(), 50, startY, title);
            title.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(i.getPrice()) +" Ron", 700, startY, title);
            startY += 35;
        }

        canvas.drawLine(10, startY, pagewidth-10, startY, title);
        startY+=50;
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Total:", 50, startY, title);
        title.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(order.getTotal() +" Ron", 700, startY, title);

        pdfDocument.finishPage(page);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "order_" + order.getId() + ".pdf");

        try {

            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(OrderHistoryActivity.this, "PDF file generated successfully, check downloads", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
//        open_file(file);
    }

    public void open_file(File file) {
        // Get URI and MIME type of file
        Uri uri = Uri.fromFile(file);
        Intent pdfOpen = new Intent(Intent.ACTION_VIEW);
        pdfOpen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpen.setDataAndType(uri, "application/pdf");
        try {
            startActivity(pdfOpen);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (popUpCard.getVisibility() == View.VISIBLE) {
            popUpCard.setVisibility(View.GONE);
        } else {
            finish();
        }
    }
}