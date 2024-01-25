package com.example.todolist;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddToDoActivity extends AppCompatActivity {

    //initialize cloud firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    LocalDate selectedDate;
    LocalTime selectedTime = LocalTime.of(0,0);

    String selectedLat;
    String selectedLng;

    Button dateButton;
    Button timeButton;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        selectedLat = intent.getStringExtra("lat");
                        selectedLng = intent.getStringExtra("lng");
                        Toast.makeText(AddToDoActivity.this, "Todo position selected!",
                                Toast.LENGTH_LONG).show();
                    }
                }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);

        Button addToDoBackButton = findViewById(R.id.addToDoBackButton);
        Button addToDoSaveButton = findViewById(R.id.addToDoSaveButton);
        TextInputEditText titleText = findViewById(R.id.addToDoTitleTextInput);
        TextInputEditText descText = findViewById(R.id.addToDoDescTextInput);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        Button positionButton = findViewById(R.id.positionButton);

        positionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,SelectPositionActivity.class);
            //startActivity(intent);
            mStartForResult.launch(intent);
        });

        addToDoBackButton.setOnClickListener(view -> finish());
        addToDoSaveButton.setOnClickListener(view -> {
            String title = Objects.requireNonNull(titleText.getText()).toString();
            if (title.isEmpty()){
                Toast.makeText(AddToDoActivity.this,"ToDo title is required!",Toast.LENGTH_LONG).show();
            }else {
                String desc = Objects.requireNonNull(descText.getText()).toString();
                // Create a new ToDo
                Map<String, Object> toDo = new HashMap<>();
                toDo.put("title", title);
                toDo.put("description", desc);
                toDo.put("date", selectedDate!=null? selectedDate.toString() : "noDate"  );
                toDo.put("time", selectedTime.toString());
                toDo.put("creationDateTime",LocalDateTime.now().toString());
                if(selectedLat != null){
                    toDo.put("positionLat",selectedLat);
                    toDo.put("positionLng",selectedLng);
                }
                // Add a new document with a generated ID
                db.collection("users")
                        .document(Objects.requireNonNull(mAuth.getUid()))
                        .collection("toDos")
                        .add(toDo)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("add", "DocumentSnapshot added with ID: " + documentReference.getId());
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.w("add", "Error adding document", e);
                            Toast.makeText(AddToDoActivity.this, "Error",
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });
        dateButton.setOnClickListener(view -> openDateDialog());
        timeButton.setOnClickListener(view -> openTimeDialog());
    }

    private void openDateDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this,(datePicker, i, i1, i2) -> {
            selectedDate = LocalDate.of(i,i1+1,i2);
            dateButton.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yy")));
        }, ZonedDateTime.now().getYear(),ZonedDateTime.now().getMonthValue()-1,ZonedDateTime.now().getDayOfMonth());
        dialog.show();
    }

    private void openTimeDialog(){
        TimePickerDialog dialog = new TimePickerDialog(this,(timePicker, i, i1) -> {
            selectedTime = LocalTime.of(i,i1);
            timeButton.setText(selectedTime.toString());
        },LocalTime.now().getHour(),LocalTime.now().getMinute(),true);
        dialog.show();
    }
}