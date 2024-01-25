package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

public class ListFragment extends Fragment {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView mainActivityRecyclerView;

    public ListFragment() {
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
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mainActivityRecyclerView = view.findViewById(R.id.mainActivityRecyclerView);
        ImageView mainActivityAddToDoButton = view.findViewById(R.id.mainActivityAddToDoButton);

        mainActivityAddToDoButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(),AddToDoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
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
                                        (String)toDoMap.get("creationDateTime")
                                ));
                            }
                            setToDosArray(toDoArrayList);
                        } else {
                            Log.w("read", "Error getting documents.", task.getException());
                        }
                    });
        }else {
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }
        db.collection("toDos")
                .whereEqualTo("user", mAuth.getUid())
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("listen", "Listen failed.", e);
                        return;
                    }
                    List<ToDo> toDoArrayList = new ArrayList<>();
                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        toDoArrayList.add(new ToDo(
                                doc.getId(),
                                (String) doc.get("title"),
                                (String)doc.get("description"),
                                Objects.equals(doc.get("date"), "noDate") ? null : LocalDate.parse((String)doc.get("date")),
                                LocalTime.parse((String)doc.get("time")),
                                (String)doc.get("creationDateTime")
                        ));
                    }
                    Log.d("listen", "doneListening");
                    setToDosArray(toDoArrayList);
                });
    }

    public void setToDosArray(List<ToDo> ToDoList){
        ToDo[] array = ToDoList.toArray(new ToDo[0]);
        ToDosAdapter toDosAdapter = new ToDosAdapter(array);
        mainActivityRecyclerView.setAdapter(toDosAdapter);
        toDosAdapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(getActivity(),ToDoDetailsActivity.class);
            intent.putExtra("todo", ToDoList.get(position));
            intent.putExtra("UID", ToDoList.get(position).getUID());
            startActivity(intent);
        });
    }
}