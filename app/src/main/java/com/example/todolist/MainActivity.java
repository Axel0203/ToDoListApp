package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        applicaFragment("List");
        bottomNavigationView.setOnItemSelectedListener(item -> {
            String name = Objects.requireNonNull(item.getTitle()).toString();
            applicaFragment(name);
            return true;
        });
    }
    protected void applicaFragment(String name){
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(name) {
            case "List":
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ListFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
                break;
            case "Maps":
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MapsFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
                break;
            case "User":
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, UserFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
                break;
            default:
                // code block
        }
    }


}