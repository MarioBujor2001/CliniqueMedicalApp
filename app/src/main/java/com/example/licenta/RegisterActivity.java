package com.example.licenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.licenta.Models.Patient;
import com.example.licenta.Utils.APICommunication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtFirstName, edtLastName,edtAge, edtCNP, edtAddress, edtPass, edtPassRepeat, edtEmail;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");
        mAuth = FirebaseAuth.getInstance();
        edtFirstName = findViewById(R.id.edtRegisterFirstName);
        edtLastName = findViewById(R.id.edtRegisterLastName);
        edtAge = findViewById(R.id.edtAge);
        edtCNP = findViewById(R.id.edtCNP);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtRegEmail);
        edtPass = findViewById(R.id.edtRegPass1);
        edtPassRepeat = findViewById(R.id.edtRegPass2);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    String email = edtEmail.getText().toString().trim();
                    String pass = edtPass.getText().toString().trim();
                    mAuth.createUserWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        assert user != null;
                                        user.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "Check email for verification!"+user.getUid(), Toast.LENGTH_SHORT).show();
                                        APICommunication.postPacient(new Patient(user.getUid(),
                                                edtFirstName.getText().toString(),
                                                edtLastName.getText().toString(),
                                                edtEmail.getText().toString(),
                                                edtCNP.getText().toString(),
                                                Integer.parseInt(edtAge.getText().toString()),
                                                edtAddress.getText().toString(),
                                                0), getApplicationContext());
                                        finish();
                                    }else{
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("err", task.getException().getMessage());
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean validateInput(){
        if(edtFirstName.getText().toString().equals("")){
            edtFirstName.setError("Cannot be empty");
            edtFirstName.requestFocus();
            return false;
        }
        if(edtLastName.getText().toString().equals("")){
            edtLastName.setError("Cannot be empty");
            edtLastName.requestFocus();
            return false;
        }
        if(edtAge.getText().toString().equals("")){
            edtAge.setError("Cannot be empty");
            edtAge.requestFocus();
            return false;
        }
        if(edtCNP.getText().toString().equals("")){
            edtCNP.setError("Cannot be empty");
            edtCNP.requestFocus();
            return false;
        }
        if(edtAddress.getText().toString().equals("")){
            edtAddress.setError("Cannot be empty");
            edtAddress.requestFocus();
            return false;
        }
        if(edtAddress.getText().toString().equals("")){
            edtAddress.setError("Cannot be empty");
            edtAddress.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString().trim()).matches()){
            edtEmail.setError("Invalid email");
            edtEmail.requestFocus();
            return false;
        }
        if(edtPass.getText().toString().equals("")){
            edtPass.setError("Cannot be empty");
            edtPass.requestFocus();
            return false;
        }
        if(edtPass.getText().toString().length()<6){
            edtPass.setError("Password too short!");
            edtPass.requestFocus();
            return false;
        }
        if(edtPassRepeat.getText().toString().equals("")){
            edtPassRepeat.setError("Cannot be empty");
            edtPassRepeat.requestFocus();
            return false;
        }
        if(!edtPass.getText().toString().equals(edtPassRepeat.getText().toString())){
            edtPassRepeat.setError("Password doesn't match");
            edtPassRepeat.requestFocus();
            return false;
        }
        return true;
    }
}