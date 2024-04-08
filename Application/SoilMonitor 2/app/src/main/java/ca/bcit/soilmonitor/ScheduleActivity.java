package ca.bcit.soilmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        EditText editTextDuration = findViewById(R.id.editTextDuration);

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                int duration = Integer.parseInt(editTextDuration.getText().toString());
                setSchedule(hour, minute, duration);
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
        updateToggleButtonFromFirebase();
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
    private void updateToggleButtonFromFirebase() {
        sensorControlRef.child("waterOn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean waterOn = dataSnapshot.getValue(Boolean.class);
                    if (waterOn != null) {
                        waterToggle.setChecked(waterOn);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ScheduleActivity.this, "Failed to read waterOn state.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSchedule(int hourOfDay, int minute, int duration) {
        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

        // Set the single schedule time in Firebase
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("time", time);
        scheduleData.put("duration", duration);
        scheduleData.put("state", waterToggle.isChecked() ? "on" : "off");

        sensorControlRef.child("Schedule").setValue(scheduleData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ScheduleActivity.this, "Schedule set successfully!", Toast.LENGTH_SHORT).show();

                        // Schedule the start alarm after successfully setting the schedule
                        Calendar startTime = Calendar.getInstance();
                        startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startTime.set(Calendar.MINUTE, minute);
                        setStartAlarm(startTime, sensorName); // Schedule start alarm

                        // Schedule the end alarm
                        Calendar endTime = (Calendar) startTime.clone();
                        endTime.add(Calendar.MINUTE, duration);
                        setEndAlarm(endTime, duration, sensorName); // Schedule end alarm

                    } else {
                        Toast.makeText(ScheduleActivity.this, "Failed to set schedule.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void setStartAlarm(Calendar startTime, String sensorName) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, WateringStartReceiver.class);
        intent.putExtra("SENSOR_NAME", sensorName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), pendingIntent);
    }
    public static class WateringStartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sensorName = intent.getStringExtra("SENSOR_NAME");
            DatabaseReference controlRef = FirebaseDatabase.getInstance().getReference(sensorName + "/Control");
            controlRef.child("waterOn").setValue(true);
        }
    }
    private void setEndAlarm(Calendar startTime, int durationMinutes, String sensorName) {
        long endTimeMillis = startTime.getTimeInMillis() + TimeUnit.MINUTES.toMillis(durationMinutes);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, WateringEndReceiver.class);
        intent.putExtra("SENSOR_NAME", sensorName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0); // Use a different request code

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, endTimeMillis, pendingIntent);
    }
    public static class WateringEndReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sensorName = intent.getStringExtra("SENSOR_NAME");
            DatabaseReference controlRef = FirebaseDatabase.getInstance().getReference(sensorName + "/Control");
            controlRef.child("waterOn").setValue(false);
        }
    }
}
