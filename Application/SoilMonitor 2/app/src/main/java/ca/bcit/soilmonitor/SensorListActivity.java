package ca.bcit.soilmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class SensorListActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> sensorNames = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_list);

        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorNames);
        listView.setAdapter(arrayAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://onlyplants-2a2cd-default-rtdb.firebaseio.com/");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sensorNames.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String sensorName = snapshot.getKey();
                    sensorNames.add(sensorName);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSensorName = sensorNames.get(position);
                Intent intent = new Intent(SensorListActivity.this, SensorDetailActivity.class);
                intent.putExtra("SENSOR_NAME", selectedSensorName);
                startActivity(intent);
            }
        });
    }
}
