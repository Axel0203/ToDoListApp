package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SigninActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Button signupButton = findViewById(R.id.signupButton);
        TextInputEditText signupMail = findViewById(R.id.signupMailText);
        TextInputEditText signupPassword = findViewById(R.id.signupPasswordText);
        TextView signupMessage = findViewById(R.id.signupMessage);
        Button signupBackButton = findViewById(R.id.signupBackButton);

        signupBackButton.setOnClickListener(view -> finish());

        signupButton.setOnClickListener(view -> {
            String mail = Objects.requireNonNull(signupMail.getText()).toString();
            String pass = Objects.requireNonNull(signupPassword.getText()).toString();

            if(mail.isEmpty() || pass.isEmpty()){
                Toast.makeText(SigninActivity.this, "Please enter credentials",
                        Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("signup", "createUserWithEmail:success");
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("signup", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SigninActivity.this, "Error",
                                        Toast.LENGTH_SHORT).show();
                                signupMessage.setText(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        });
            }
        });
    }
}