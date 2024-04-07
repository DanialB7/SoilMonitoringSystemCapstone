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
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LineChart mpLineChart;
    LineDataSet lineDataSet = new LineDataSet(null,null);

    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;
    HashMap<String, String> mapColor = new HashMap<>();
    TextView tempRead;
    TextView humidRead;
    TextView luxRead;
    TextView timeStamp;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ArrayList<Entry> dataTemp = new ArrayList<Entry>();
    TextView test1;
    TextView test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test1 = findViewById(R.id.testing1);
        test2 = findViewById(R.id.testing2);
        tempRead = findViewById(R.id.textViewTemp);
        humidRead = findViewById(R.id.textViewHumid);
        luxRead = findViewById(R.id.textViewLux);
        timeStamp = findViewById(R.id.textViewTimeStamp);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Sensor 1/Data");
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

        mpLineChart = findViewById(R.id.graph);

        //LineDataSet lineDataSet2 = new LineDataSet(dataValues2(), "Humidity");
        //ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        //dataSets.add(lineDataSet2);
        //ineData data = new LineData(dataSets);
        //mpLineChart.setData(data);
        //mpLineChart.invalidate();
        //configureMPChart();
        //configureDataset(lineDataSet2, mapColor.get("blue"), mapColor.get("brown"));

        updateData();
        //retrieveData();
    }
    
    private void configureMPChart() {
        LineData data = new LineData();
        //no description text
        mpLineChart.getDescription().setEnabled(false);
        //enable touch gesture
        mpLineChart.setTouchEnabled(true);
        //enable scaling and dragging
        mpLineChart.setDragEnabled(true);
        mpLineChart.setScaleEnabled(true);
        //remove gridline
        mpLineChart.getAxisRight().setDrawGridLines(false);
        mpLineChart.getAxisLeft().setDrawGridLines(false);
        mpLineChart.getXAxis().setDrawGridLines(false);
        //remove outer line
        mpLineChart.getAxisRight().setDrawAxisLine(false);
        mpLineChart.getAxisLeft().setDrawAxisLine(false);
        mpLineChart.getXAxis().setDrawAxisLine(false);
        //remove axis labels
        mpLineChart.getAxisRight().setDrawLabels(false);
        //mpLineChart.getAxisLeft().setDrawLabels(false);
        //set X axis label to the bottom inside
        mpLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        mpLineChart.getXAxis().setTextColor(Color.parseColor("#A07E63"));
        mpLineChart.getXAxis().setTextSize(15);
        mpLineChart.getLegend().setTextColor(Color.parseColor("#A07E63"));

        mpLineChart.setData(data);
    }

    private void configureDataset(LineDataSet data, String lineColour, String textColour) {

        //set color of the data line
        data.setColor(Color.parseColor(lineColour));
        //enable color filled
        data.setDrawFilled(true);
        //set color filled
        data.setFillColor(Color.parseColor(lineColour));
        //set color of circle
        data.setCircleColor(Color.parseColor(lineColour));
        //set color of value
        data.setValueTextColor(Color.parseColor(textColour));
        data.setValueTextSize(12);
        data.setMode(LineDataSet.Mode.CUBIC_BEZIER);

    }
    private void updateData() {
        myRef.orderByKey().limitToLast(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String timeKey = snapshot.getKey();
                myRef.addValueEventListener(new ValueEventListener() {
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



    private void showChart(ArrayList<Entry> dataVals, String label) {
        mpLineChart.refreshDrawableState();
        lineDataSet = new LineDataSet(dataVals,label);
        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);
        mpLineChart.notifyDataSetChanged();
        mpLineChart.setData(lineData);
        mpLineChart.invalidate();
        configureDataset(lineDataSet, mapColor.get("greenLeaf"), mapColor.get("brown"));
    }

}