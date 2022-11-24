package com.example.licenta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.licenta.Models.Pacient;
import com.example.licenta.Utils.APICommunication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileViewActivity extends AppCompatActivity {
    private CardView btnBackProfile, btnEditProfile, cardViewProfile, cardViewEditProfile;
    private ImageView imgProfile, imgEditProfile;
    private TextView tvName, tvEmail, tvCNP, tvAddress, tvAge;
    private Pacient p;
    private EditText edtFName, edtLName, edtAge, edtCNP, edtAddress;
    private Button btnSubmitChanges;
    private boolean btnEditToggleState;
    private ProgressDialog progressDialog;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        btnBackProfile = findViewById(R.id.backButtonProfile);
        btnEditProfile = findViewById(R.id.editProfile);
        imgProfile = findViewById(R.id.userProfile);
        imgEditProfile = findViewById(R.id.imgEditProfile);
        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvCNP = findViewById(R.id.tvProfileCNP);
        tvAge = findViewById(R.id.tvProfileAge);
        tvAddress = findViewById(R.id.tvProfileAddress);
        cardViewProfile = findViewById(R.id.cardViewInfoProfile);
        cardViewEditProfile = findViewById(R.id.cardViewEditProfile);
        edtFName = findViewById(R.id.edtEditFirstName);
        edtLName = findViewById(R.id.edtEditLastName);
        edtAddress = findViewById(R.id.edtEditAddress);
        edtAge = findViewById(R.id.edtEditAge);
        edtCNP = findViewById(R.id.edtEditCNP);
        btnSubmitChanges = findViewById(R.id.btnEditSubmitChanges);

        btnEditToggleState = false;

        p = (Pacient) getIntent().getSerializableExtra("PROFILE");
        incarcaDate();
        btnBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEditToggleState = !btnEditToggleState;
                if (btnEditToggleState) {
                    imgEditProfile.setBackgroundColor(getResources().getColor(R.color.selectedCard));
                    cardViewProfile.setVisibility(View.GONE);
                    cardViewEditProfile.setVisibility(View.VISIBLE);
                } else {
                    imgEditProfile.setBackgroundColor(getResources().getColor(R.color.CardBackgroudn));
                    cardViewProfile.setVisibility(View.VISIBLE);
                    cardViewEditProfile.setVisibility(View.GONE);
                }
            }
        });
        btnSubmitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    progressDialog = new ProgressDialog(ProfileViewActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    Map<String, Object> mapToSend = new HashMap<>();
                    mapToSend.put("id", p.getId());
                    mapToSend.put("firstName", edtFName.getText().toString().trim());
                    mapToSend.put("lastName", edtLName.getText().toString().trim());
                    mapToSend.put("age", Integer.valueOf(edtAge.getText().toString().trim()));
                    mapToSend.put("cnp", edtCNP.getText().toString().trim());
                    mapToSend.put("address", edtAddress.getText().toString().trim());
                    APICommunication.putPacient(mapToSend, ProfileViewActivity.this);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileViewActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            imgEditProfile.setBackgroundColor(getResources().getColor(R.color.CardBackgroudn));
                            cardViewProfile.setVisibility(View.VISIBLE);
                            cardViewEditProfile.setVisibility(View.GONE);

                            actualizeazaPacientCurent(mapToSend);
                            incarcaDate();

                        }
                    }, 100);
                }
            }
        });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEditToggleState) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 1);
                }
            }
        });
    }

    private void actualizeazaPacientCurent(Map<String, Object> map) {
        p.setFirstName((String) map.get("firstName"));
        p.setLastName((String) map.get("lastName"));
        p.setVarsta((Integer) map.get("age"));
        p.setCNP((String) map.get("cnp"));
        p.setAdresa((String) map.get("address"));
    }

    private void incarcaDate() {
        if (p != null) {
            Glide.with(this).asBitmap().load(p.getPhoto()).into(imgProfile);
            tvName.setText(p.getFirstName() + " " + p.getLastName());
            tvEmail.setText(p.getEmail());
            tvCNP.setText(p.getCNP());
            tvAddress.setText(p.getAdresa());
            tvAge.setText(String.valueOf(p.getVarsta()));

            edtFName.setText(p.getFirstName());
            edtLName.setText(p.getLastName());
            edtCNP.setText(p.getCNP());
//            Toast.makeText(this, p.getVarsta(), Toast.LENGTH_SHORT).show();
            edtAge.setText(String.valueOf(p.getVarsta()));
            edtAddress.setText(p.getAdresa());
        }
    }

    private boolean checkInput() {
        if (edtFName.getText().toString().isEmpty()) {
            edtFName.setError("Cannot be empty");
            edtFName.requestFocus();
            return false;
        }
        if (edtLName.getText().toString().isEmpty()) {
            edtLName.setError("Cannot be empty");
            edtLName.requestFocus();
            return false;
        }
        if (edtAge.getText().toString().isEmpty()) {
            edtAge.setError("Cannot be empty");
            edtAge.requestFocus();
            return false;
        }
        if (Integer.parseInt(edtAge.getText().toString()) < 1) {
            edtAge.setError("Cannot be this low");
            edtAge.requestFocus();
            return false;
        }
        if (edtCNP.getText().toString().isEmpty()) {
            edtCNP.setError("Cannot be empty");
            edtCNP.requestFocus();
            return false;
        }
        if (edtCNP.getText().toString().length() != 13) {
            edtCNP.setError("Incorrect CNP");
            edtCNP.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                    Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
                    Bitmap photo = null;
                    try {
                        photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgProfile.setImageBitmap(photo);
                } else {
                    //NOT IN REQUIRED FORMAT
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }
}