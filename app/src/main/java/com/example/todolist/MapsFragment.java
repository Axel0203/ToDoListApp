package com.example.todolist;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;



import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapsFragment extends Fragment implements
        LocationListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener{

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button openButton;
    GoogleMap googleMap;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap map) {
            googleMap = map;
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                db.collection("users")
                        .document(Objects.requireNonNull(mAuth.getUid()))
                        .collection("toDos")
                        .get()
                        .addOnCompleteListener(task -> {
                            List<ToDo> toDoArrayList = new ArrayList<>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("read", document.getId() + " => " + document.getData());
                                    Map<String, Object> toDoMap = document.getData();
                                    toDoArrayList.add(new ToDo(
                                            document.getId(),
                                            (String) toDoMap.get("title"),
                                            (String)toDoMap.get("description"),
                                            Objects.equals(toDoMap.get("date"), "noDate") ? null : LocalDate.parse((String)toDoMap.get("date")),
                                            LocalTime.parse((String)toDoMap.get("time")),
                                            (String)toDoMap.get("creationDateTime"),
                                            (String) toDoMap.get("positionLat"),
                                            (String) toDoMap.get("positionLng")
                                    ));
                                }
                                setToDosArray(toDoArrayList, googleMap);
                            } else {
                                Log.w("read", "Error getting documents.", task.getException());
                            }
                        });
            }else {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }

            googleMap.setOnMarkerClickListener(marker -> {
                openButton.setVisibility(View.VISIBLE);
                openButton.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(),ToDoDetailsActivity.class);
                    intent.putExtra("todo",(ToDo) marker.getTag());
                    intent.putExtra("UID", ((ToDo) Objects.requireNonNull(marker.getTag())).getUID());
                    startActivity(intent);
                });
                return false;
            });

            googleMap.setOnMapClickListener(latLng -> openButton.setVisibility(View.INVISIBLE));
        }
    };

    private void setToDosArray(List<ToDo> toDoArrayList, GoogleMap map) {
        openButton.setVisibility(View.INVISIBLE);
        map.clear();
        for (ToDo currentTodo :
                toDoArrayList) {
            if(currentTodo.getPositionLat() != null){
                LatLng toDoLatLng = new LatLng(Double.parseDouble(currentTodo.getPositionLat()),Double.parseDouble(currentTodo.getPositionLng()));
                Marker currentToDoMarker =  map.addMarker(new MarkerOptions().position(toDoLatLng).title(currentTodo.getTitle()));
                assert currentToDoMarker != null;
                currentToDoMarker.setTag(currentTodo);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        openButton = view.findViewById(R.id.mapOpenToDoButton);
        openButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            db.collection("users")
                    .document(Objects.requireNonNull(mAuth.getUid()))
                    .collection("toDos")
                    .get()
                    .addOnCompleteListener(task -> {
                        List<ToDo> toDoArrayList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("read", document.getId() + " => " + document.getData());
                                Map<String, Object> toDoMap = document.getData();
                                toDoArrayList.add(new ToDo(
                                        document.getId(),
                                        (String) toDoMap.get("title"),
                                        (String)toDoMap.get("description"),
                                        Objects.equals(toDoMap.get("date"), "noDate") ? null : LocalDate.parse((String)toDoMap.get("date")),
                                        LocalTime.parse((String)toDoMap.get("time")),
                                        (String)toDoMap.get("creationDateTime"),
                                        (String) toDoMap.get("positionLat"),
                                        (String) toDoMap.get("positionLng")
                                ));
                            }
                            setToDosArray(toDoArrayList, googleMap);
                        } else {
                            Log.w("read", "Error getting documents.", task.getException());
                        }
                    });
        }else {
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }
    }
}