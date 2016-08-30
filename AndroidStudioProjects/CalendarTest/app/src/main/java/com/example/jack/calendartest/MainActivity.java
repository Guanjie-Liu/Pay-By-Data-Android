package com.example.jack.calendartest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  SensorEventListener{
    public static String TAG = "Calendar Test";
    private SensorManager mSensorManager;
    private Sensor mLight;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (locationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Log.d()


/*
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged (SensorEvent event){
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        float lux = event.values[0];
        // Do something with this sensor value.
        Log.d(TAG, "onSensorChanged: " + lux);
    }

    @Override
    protected void onResume () {
        super.onResume();
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause () {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}

        /*
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        int listSize = deviceSensors.size();

        Log.d(TAG, "listSize is " + listSize);

        int i = listSize - 1;
        while(i >= 0){
            Sensor sensor = deviceSensors.get(i);
            Log.d(TAG, "Sensor " + i + " : " + sensor.getName());
            i--;
        }
*/
        /*
        try {

            Calendar calendar = Calendar.getInstance();
            Log.d(TAG, "now is: " + calendar.toString());

            calendar.add(Calendar.DAY_OF_MONTH, 10);
            Log.d(TAG, "later is: " + calendar.toString());

            Date expiredate = calendar.getTime();
            Log.d(TAG,"string is " +expiredate.toString());

            Date now = new Date();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            Date date2 = simpleDateFormat.parse(expiredate.toString());

            if(date2.after(now)){
                Log.d(TAG, date2.toString());
                Log.d(TAG, now.toString());
            }

        }catch(Exception e){
            Log.e(TAG, "Error in Calendar",e);
        }


    }
}
*/
