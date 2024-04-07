package ca.bcit.soilmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import android.widget.Button;
import androidx.annotation.NonNull;

public class SensorDetailActivity extends AppCompatActivity {

    private ListView dataList;
    private ArrayAdapter<String> adapter;
    private List<String> sensorDataList = new ArrayList<>();
    private DatabaseReference sensorDataRef;
    private DatabaseReference sensorControlRef;
    private Button scheduleButton;
    private String sensorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail);

        sensorName = getIntent().getStringExtra("SENSOR_NAME");
        sensorDataRef = FirebaseDatabase.getInstance().getReference(sensorName + "/Data");
        sensorControlRef = FirebaseDatabase.getInstance().getReference(sensorName + "/Control");

        dataList = findViewById(R.id.detailsListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorDataList);
        dataList.setAdapter(adapter);

        scheduleButton = findViewById(R.id.scheduleButton);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SensorDetailActivity.this, ScheduleActivity.class);
                intent.putExtra("SENSOR_NAME", sensorName); // Pass the sensor name to ScheduleActivity
                startActivity(intent);
            }
        });

        loadSensorData();
    }

    private void loadSensorData() {
        sensorDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sensorDataList.clear();
                for (DataSnapshot timestampSnapshot : dataSnapshot.getChildren()) {
                    String timestamp = timestampSnapshot.getKey();
                    String humidity = String.valueOf(timestampSnapshot.child("humidity").getValue());
                    String temperature = String.valueOf(timestampSnapshot.child("temperature").getValue());
                    String lux = String.valueOf(timestampSnapshot.child("lux").getValue());
                    String formattedData = String.format("Timestamp: %s\nTemperature: %s°C\nHumidity: %s%%\nLux: %s",
                            timestamp, temperature, humidity, lux);
                    sensorDataList.add(formattedData);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void setWaterOn(boolean waterState) {
        sensorControlRef.child("waterOn").setValue(waterState);
    }
}
