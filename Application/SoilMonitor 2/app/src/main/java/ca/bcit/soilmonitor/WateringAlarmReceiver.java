package ca.bcit.soilmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WateringAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String sensorName = intent.getStringExtra("SENSOR_NAME");

        DatabaseReference sensorControlRef = FirebaseDatabase.getInstance().getReference(sensorName + "/Control");
        sensorControlRef.child("waterOn").setValue(true);

        // If you want to perform network operations, you should start a Service or use WorkManager as receivers have a short execution window
    }
}
