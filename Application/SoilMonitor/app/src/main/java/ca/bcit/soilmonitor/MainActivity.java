package ca.bcit.soilmonitor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempRead = findViewById(R.id.textViewTemp);
        humidRead = findViewById(R.id.textViewHumid);
        luxRead = findViewById(R.id.textViewLux);
        timeStamp = findViewById(R.id.textViewTimeStamp);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Data");

        mapColor.put("greenLeaf", "#7FB241");
        mapColor.put("brown", "#A07E63");
        mapColor.put("blue","#1ecbe1");

        mpLineChart = findViewById(R.id.graph);

        //LineDataSet lineDataSet1 = new LineDataSet(dataValues1(),"Temp");
        LineDataSet lineDataSet2 = new LineDataSet(dataValues2(), "Humidity");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        //dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);
        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();

        configureMPChart();
        //configureDataset(lineDataSet1, mapColor.get("greenLeaf"), mapColor.get("brown"));
        //configureDataset(lineDataSet2, mapColor.get("blue"), mapColor.get("brown"));

        retrieveData();
    }
    
    private void configureMPChart() {
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
        mpLineChart.getAxisLeft().setDrawLabels(false);
        //set X axis label to the bottom inside
        mpLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        mpLineChart.getXAxis().setTextColor(Color.parseColor("#A07E63"));
        mpLineChart.getXAxis().setTextSize(15);
        mpLineChart.getLegend().setTextColor(Color.parseColor("#A07E63"));
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



    private ArrayList<Entry> dataValues2(){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        dataVals.add(new Entry(0, 12));
        dataVals.add(new Entry(5, 14));
        dataVals.add(new Entry(15, 16));
        dataVals.add(new Entry(25, 18));
        dataVals.add(new Entry(35, 20));
        dataVals.add(new Entry(45, 22));
        dataVals.add(new Entry(55, 24));

        return dataVals;
    }

    /*private void retrieveData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double temp = snapshot.child("temperature").getValue(Double.class);

                Double humid = snapshot.child("humidity").getValue(Double.class);
                Double lux = snapshot.child("lux").getValue(Double.class);
                String tempFormat = "temperature: " + String.format("%.1f",temp) + " C";
                String humidFormat = "humidity: " + String.format("%.1f",humid) + "%";
                String luxFormat = "lux: " + String.format("%.1f",lux);
                tempRead.setText(tempFormat);
                humidRead.setText(humidFormat);
                luxRead.setText(luxFormat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }*/
    private String EpochToDate(long time, String formatString){
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(new Date(time));
    }

    private  void retrieveData() {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String timestamp = snapshot.getKey();

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String epochTime = snapshot.child(timestamp + "/timeStamp").getValue(String.class);
                        Double temp = snapshot.child(timestamp + "/temperature").getValue(Double.class);
                        Double humid = snapshot.child(timestamp + "/humidity").getValue(Double.class);
                        Double lux = snapshot.child(timestamp + "/lux").getValue(Double.class);





                        float tempFloat;
                        float humidFloat;

                        if(temp == null){
                            tempRead.setText("temperature: ...");
                        } else if (humid == null) {
                            humidRead.setText("humidty: ...");
                        } else if (lux == null) {
                            luxRead.setText("lux: ...");
                        } else if (epochTime == null) {
                            timeStamp.setText("0");
                        }


                        else {
                            String tempFormat = "temperature: " + String.format("%.1f",temp) + " C";
                            String humidFormat = "humidity: " + String.format("%.1f",humid) + "%";
                            String luxFormat = "lux: " + String.format("%.1f",lux);
                            tempRead.setText(tempFormat);
                            humidRead.setText(humidFormat);
                            luxRead.setText(luxFormat);


                            int epochTimeInt = Integer.parseInt(epochTime);
                            String epochFormat = "Epoch Time: "+ String.valueOf(epochTimeInt);
                            timeStamp.setText(epochFormat);
                            int count = 0;
                            for(int i = 0; i < 100; i++){
                                count++;
                                dataTemp.add(new Entry(count, Float.parseFloat(String.format("%.1f",temp))));
                            }

                            showChart(dataTemp, "temp");
                            mpLineChart.notifyDataSetChanged();
                            XAxis xAxis = mpLineChart.getXAxis();


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

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

    private void showChart(ArrayList<Entry> dataVals, String label) {
        lineDataSet = new LineDataSet(dataVals,label);
        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);
        mpLineChart.setData(lineData);
        mpLineChart.invalidate();
        configureDataset(lineDataSet, mapColor.get("greenLeaf"), mapColor.get("brown"));
    }






}