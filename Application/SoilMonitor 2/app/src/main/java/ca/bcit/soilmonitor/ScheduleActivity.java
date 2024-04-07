package ca.bcit.soilmonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity {

    private DatabaseReference sensorControlRef;
    private TimePicker timePicker; // Make sure to have a TimePicker in your layout
    private Button setScheduleButton;
    private String sensorName;
    private ToggleButton waterToggle;
    private Button scheduleButton;
    private EditText editTextReadingInterval;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        sensorName = getIntent().getStringExtra("SENSOR_NAME");
        sensorControlRef = FirebaseDatabase.getInstance().getReference(sensorName + "/Control");

        timePicker = findViewById(R.id.timePicker);
        scheduleButton = findViewById(R.id.setScheduleButton); // And a Button for setting the schedule
        waterToggle = findViewById(R.id.waterToggleButton); // And a ToggleButton for waterOn/off

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                setSchedule(hour, minute);
            }
        });

        waterToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sensorControlRef.child("waterOn").setValue(isChecked);
        });
        editTextReadingInterval = findViewById(R.id.editTextReadingInterval);

        // Handle the enter key press to update the readingInterval
        editTextReadingInterval.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Get the value from EditText and update Firebase
                String newInterval = editTextReadingInterval.getText().toString();
                if (!newInterval.isEmpty()) {
                    updateReadingInterval(Integer.parseInt(newInterval));
                }
                return true;
            }
            return false;
        });
    }
    private void updateReadingInterval(int interval) {
        sensorControlRef.child("readingInterval").setValue(interval)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ScheduleActivity.this, "Interval updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ScheduleActivity.this, "Failed to update interval", Toast.LENGTH_SHORT).show();
                });
    }
    private void setSchedule(int hourOfDay, int minute) {
        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

        // Set the single schedule time in Firebase
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("time", time);
        scheduleData.put("state", waterToggle.isChecked() ? "on" : "off"); // Use the state of the toggle button

        sensorControlRef.child("Schedule").setValue(scheduleData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ScheduleActivity.this, "Schedule set successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ScheduleActivity.this, "Failed to set schedule.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
