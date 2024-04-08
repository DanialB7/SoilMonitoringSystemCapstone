package ca.bcit.soilmonitor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LineChart lineChart;

    HashMap<String, String> mapColor = new HashMap<>();
    TextView tempRead;
    TextView humidRead;
    TextView luxRead;
    TextView timeStamp;
    FirebaseDatabase database;
    DatabaseReference sensor1Ref;
    ArrayList<Entry> yData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempRead = findViewById(R.id.textViewTemp);
        humidRead = findViewById(R.id.textViewHumid);
        luxRead = findViewById(R.id.textViewLux);
        timeStamp = findViewById(R.id.textViewTimeStamp);
        database = FirebaseDatabase.getInstance();
        sensor1Ref = database.getReference("Sensor 1/Data");
        lineChart = findViewById(R.id.graph);

        Button seeAllDevicesBtn = findViewById(R.id.seeAllDevicesBtn);

        seeAllDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SensorListActivity.class);
                startActivity(intent);
            }
        });

        mapColor.put("greenLeaf", "#7FB241");
        mapColor.put("brown", "#A07E63");
        mapColor.put("blue","#1ecbe1");

        updateChart();
        updateData();

    }
    
    private void configureMPChart() {
        YAxis yaxis = lineChart.getAxisLeft();
        XAxis xaxis = lineChart.getXAxis();
        //no description text
        lineChart.getDescription().setEnabled(false);
        //enable touch gesture
        lineChart.setTouchEnabled(true);
        //enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        //remove gridline
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        //remove outer line
        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawAxisLine(false);
        //remove axis labels
        lineChart.getAxisRight().setDrawLabels(false);
        //mpLineChart.getAxisLeft().setDrawLabels(false);
        //set X axis label to the bottom inside
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setTextColor(Color.parseColor("#A07E63"));
        lineChart.getXAxis().setTextSize(15);
        lineChart.getLegend().setTextColor(Color.parseColor("#A07E63"));
        xaxis.setGranularity(1f);
        xaxis.setGranularityEnabled(true);
        lineChart.setVisibleXRange(10f,10f);
        xaxis.setLabelRotationAngle(-45f);

    }

    private void configureDataSet (LineDataSet dataset, String lineColour, String textColour) {
        //set color of the data line
        dataset.setColor(Color.parseColor(lineColour));
        //enable color filled
        dataset.setDrawFilled(true);
        //set color filled
        dataset.setFillColor(Color.parseColor(lineColour));
        //set color of circle
        dataset.setCircleColor(Color.parseColor(lineColour));
        //set color of value
        dataset.setValueTextColor(Color.parseColor(textColour));
        dataset.setValueTextSize(12);
        dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataset.setCubicIntensity(0.2f);
    }

<<<<<<< HEAD
    private void updateChart() {
        sensor1Ref.limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                yData = new ArrayList<>();
                final List<Long> xLabels = new ArrayList<>(); // This will store the timestamps
                int count = 0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    Float temp = ds.child("temperature").getValue(Float.class);
                    Long timestamp = ds.child("timeStamp").getValue(Long.class); // Directly retrieve the timestamp as Long
                    if(temp != null && timestamp != null){
                        yData.add(new Entry(count, temp));
                        if (xLabels.size() >= 20) {
                            xLabels.remove(0); // Remove the oldest element
                        }
                        xLabels.add(timestamp); // Store the timestamp
                        count++;
                    }
                }

                final LineDataSet lineDataSet = new LineDataSet(yData, "Temp");
                configureDataSet(lineDataSet, mapColor.get("blue"), mapColor.get("brown"));
                LineData data = new LineData(lineDataSet);
                data.setDrawValues(false);
                lineChart.setData(data);

                // Apply a custom ValueFormatter that formats the timestamp into HH:mm:ss
                lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value;
                        if (index >= 0 && index < xLabels.size()) {
                            // Convert the timestamp to a Date object
                            Date date = new Date(xLabels.get(index));
                            // Return the formatted date string
                            return format.format(date);
                        } else {
                            return "";
                        }
                    }
                });

                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
                configureMPChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
=======

>>>>>>> ce3fcce7cc9e5e4caf5dc7e3b23c29b433cc6447



    private void updateData() {
        sensor1Ref.orderByKey().limitToLast(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String timeKey = snapshot.getKey();
                sensor1Ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long epochTime = snapshot.child(timeKey+"/timeStamp").getValue(Long.class);
                        Float temp = snapshot.child(timeKey+"/temperature").getValue(Float.class);
                        Double humid = snapshot.child(timeKey+"/humidity").getValue(Double.class);
                        Double lux = snapshot.child(timeKey+"/lux").getValue(Double.class);
                        if(temp!= null & humid != null && lux != null & epochTime != null){
                            String tempFormat = "Temperature: " + String.format("%.1f", temp) + " C";
                            String humidFormat = "Humidity: " + String.format("%.1f", humid) + "%";
                            String luxFormat = "Lux: " + String.format("%.1f", lux);
                            String epochFormat = convertEpochToLocalDateTime(epochTime);
                            String localDateTime = "Last updated: " + epochFormat;
                            tempRead.setText(tempFormat);
                            humidRead.setText(humidFormat);
                            luxRead.setText(luxFormat);
                            timeStamp.setText(localDateTime);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            //to be added in the future
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private String convertEpochToLocalDateTime(long epochTime){
        Instant instant = Instant.ofEpochMilli(epochTime);
        ZoneId zoneId = ZoneId.systemDefault(); //Use system default time zone
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }


}