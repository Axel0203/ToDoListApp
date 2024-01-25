package com.example.todolist;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Button loginButton = findViewById(R.id.loginButton);
        Button goToSignupButton = findViewById(R.id.goToSignupButton);
        TextInputEditText loginMail = findViewById(R.id.loginMailText);
        TextInputEditText loginPassword = findViewById(R.id.loginPasswordText);
        TextView loginMessage = findViewById(R.id.loginMessage);

        loginButton.setOnClickListener(view -> {
            String mail = Objects.requireNonNull(loginMail.getText()).toString();
            String pass = Objects.requireNonNull(loginPassword.getText()).toString();

            if(mail.isEmpty() || pass.isEmpty()){
                Toast.makeText(LoginActivity.this, "Please enter credentials",
                        Toast.LENGTH_SHORT).show();
            } else {
                //auth with firebase
                mAuth.signInWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("login", "signInWithEmail:success");
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("login", Objects.requireNonNull(task.getException()).getMessage(), task.getException());
                                loginMessage.setText(task.getException().getMessage());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        goToSignupButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,SigninActivity.class);
            startActivity(intent);
        });
    }
}