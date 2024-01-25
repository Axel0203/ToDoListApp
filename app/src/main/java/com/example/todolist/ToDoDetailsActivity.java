package com.example.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ToDoDetailsActivity extends AppCompatActivity {

    //initialize cloud firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    LocalDate selectedDate;
    LocalTime selectedTime = LocalTime.of(0,0);

    Button dateButton;
    Button timeButton;

    String selectedLat;
    String selectedLng;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        selectedLat = intent.getStringExtra("lat");
                        selectedLng = intent.getStringExtra("lng");
                        Toast.makeText(ToDoDetailsActivity.this, "Todo position selected!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_details);

        Button detailDeleteButton = findViewById(R.id.detailsToDoDeleteButton);
        Button detailsToDoBackButton = findViewById(R.id.detailsToDoBackButton);
        Button detailsToDoSaveButton = findViewById(R.id.detailsToDoSaveButton);
        TextInputEditText titleText = findViewById(R.id.detailsToDoTitleTextInput);
        TextInputEditText descText = findViewById(R.id.detailsToDoDescTextInput);
        dateButton = findViewById(R.id.detailsdateButton);
        timeButton = findViewById(R.id.detailstimeButton);
        Button positionButton = findViewById(R.id.detailsPositionButton);

        positionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,SelectPositionActivity.class);
            //startActivity(intent);
            mStartForResult.launch(intent);
        });


        Intent intent = getIntent();
        ToDo thisToDo = (ToDo) intent.getSerializableExtra("todo");
        String UID = intent.getStringExtra("UID");

        assert thisToDo != null;
        dateButton.setText(thisToDo.getDate()!=null? thisToDo.getDateString("dd/MM/yy") : "DATE");
        timeButton.setText(thisToDo.getTime().toString().equals("00:00")? "TIME" : thisToDo.getTime().toString());

        titleText.setText(thisToDo.getTitle());
        descText.setText(thisToDo.getDescription());

        detailDeleteButton.setOnClickListener(view -> {
            assert UID != null;
            db.collection("users").document(Objects.requireNonNull(mAuth.getUid())).collection("toDos").document(UID)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("delete", "ToDo successfully deleted!");
                        Toast.makeText(ToDoDetailsActivity.this, "ToDo successfully deleted!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Log.w("delete", "Error deleting toDo", e));
        });
        detailsToDoBackButton.setOnClickListener(view -> finish());

        detailsToDoSaveButton.setOnClickListener(view -> {
            String title = Objects.requireNonNull(titleText.getText()).toString();
            if (title.isEmpty()){
                Toast.makeText(ToDoDetailsActivity.this,"ToDo title is required!",Toast.LENGTH_LONG).show();
            }else {
                String desc = Objects.requireNonNull(descText.getText()).toString();
                // Create a new ToDo
                Map<String, Object> toDo = new HashMap<>();
                toDo.put("title", title);
                toDo.put("description", desc);
                toDo.put("date", selectedDate!=null? selectedDate.toString() : "noDate"  );
                toDo.put("time", selectedTime.toString());
                toDo.put("creationDateTime", thisToDo.getCreationDateTime());
                if(selectedLat != null){
                    toDo.put("positionLat",selectedLat);
                    toDo.put("positionLng",selectedLng);
                }
                // Add a new document with a generated ID
                assert UID != null;
                db.collection("users").document(Objects.requireNonNull(mAuth.getUid())).collection("toDos")
                        .document(UID)
                        .set(toDo, SetOptions.merge());
                Toast.makeText(ToDoDetailsActivity.this, "ToDo successfully saved!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        dateButton.setOnClickListener(view -> openDateDialog());
        timeButton.setOnClickListener(view -> openTimeDialog());
    }

    private void openDateDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this,(datePicker, i, i1, i2) -> {
            selectedDate = LocalDate.of(i,i1+1,i2);
            dateButton.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yy")));
        },
                ZonedDateTime.now().getYear(),
                ZonedDateTime.now().getMonthValue()-1,
                ZonedDateTime.now().getDayOfMonth()
        );
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