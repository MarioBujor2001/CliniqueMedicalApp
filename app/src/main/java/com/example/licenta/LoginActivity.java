package com.example.licenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.MissingFormatArgumentException;

public class LoginActivity extends AppCompatActivity {

    private TextView txtRegister;
    private EditText edtEmail, edtPassword;
    private Button btnLogIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log in");
        mAuth = FirebaseAuth.getInstance();
        txtRegister = findViewById(R.id.txtRegister);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogIn = findViewById(R.id.btnLogIn);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()){
                    String email = edtEmail.getText().toString().trim();
                    String pass = edtPassword.getText().toString().trim();
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        assert user != null;
                                        if(user.isEmailVerified()){
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        }else{
                                            user.sendEmailVerification();
                                            Toast.makeText(LoginActivity.this, "Check your email and verify account!", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private boolean validateInput(){
        if(edtEmail.getText().toString().equals("")){
            edtEmail.setError("Cannot be empty");
            edtEmail.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString().trim()).matches()){
            edtEmail.setError("Invalid email");
            edtEmail.requestFocus();
            return false;
        }
        if(edtPassword.getText().toString().equals("")){
            edtPassword.setError("Cannot be empty");
            edtPassword.requestFocus();
            return false;
        }
        return true;
    }
}