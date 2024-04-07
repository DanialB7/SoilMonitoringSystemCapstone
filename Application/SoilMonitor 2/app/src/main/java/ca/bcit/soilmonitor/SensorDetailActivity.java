package ca.bcit.soilmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SensorDetailActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String sensorName;
    private SensorData sensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail);

        Intent intent = getIntent();
        sensorName = intent.getStringExtra("SENSOR_NAME");
        sensorData = (SensorData) intent.getSerializableExtra("SENSOR_DATA");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("s").child(sensorName);

        Button waterOnButton = findViewById(R.id.waterOnButton);
        waterOnButton.setOnClickListener(v -> toggleWater(true));

        Button waterOffButton = findViewById(R.id.waterOffButton);
        waterOffButton.setOnClickListener(v -> toggleWater(false));
    }

    private void toggleWater(boolean state) {
        databaseReference.child("waterOn").setValue(state);
    }
}
