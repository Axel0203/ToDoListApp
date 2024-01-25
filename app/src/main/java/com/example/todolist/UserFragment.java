package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class UserFragment extends Fragment {
    private FirebaseAuth mAuth;
    TextView userMail;
    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        userMail = view.findViewById(R.id.userMailTextView);
        Button logOutButton = view.findViewById(R.id.userLogoutButton);

        userMail.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        logOutButton.setOnClickListener(view1 -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        userMail.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
    }
}