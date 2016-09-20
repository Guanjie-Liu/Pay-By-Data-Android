package com.example.jack.pbdtrial;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import android.os.PbdManager;
import android.os.PbdLocation;

public class MainActivity extends Activity implements LocationListener {
    public static final String TAG = "PbdTrial";
    private static final String KEY = "pbdtrial_key123";
    private PbdManager pbdmanager;
    private MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbdmanager = (PbdManager) getSystemService(Context.PBD_SERVICE);
        Log.d(TAG, "created pbdDeviceManager");
        Toast.makeText(getApplicationContext(), "Requesting Device and Telephony information" ,Toast.LENGTH_SHORT).show();

        String id = pbdmanager.getDeviceId(KEY);
        Toast.makeText(getApplicationContext(), "Device id returned: " + id ,Toast.LENGTH_SHORT).show();


        String simId = pbdmanager.getSimSerialNumber(KEY);
        Toast.makeText(getApplicationContext(), "Sim id returned: " + simId ,Toast.LENGTH_SHORT).show();


        String androidId = pbdmanager.getAndroidId(KEY);
        Toast.makeText(getApplicationContext(), "Android id returned: " + androidId , Toast.LENGTH_SHORT).show();

        //////////////////////////////////////////////////////////////////////

        Log.d(TAG, "about to set up broadcast receiver");

        Toast.makeText(getApplicationContext(), "Requesting GPS Location updates every 3 seconds" ,Toast.LENGTH_SHORT).show();
        //timings = new TimingLogger(TAG, "LocationManager");
        //timings2 = new TimingLogger(TAG, "PbdLocationManager");

//        LocationManager locMgr;
//        locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        String listenerId = pbdmanager.requestLocationUpdates(KEY, 2000, 0, PbdManager.FINE_LOCATION);
        Toast.makeText(getApplicationContext(), "Listener Id: " + listenerId ,Toast.LENGTH_SHORT).show();
        Log.d(TAG, "requested updates");

        IntentFilter filter = new IntentFilter(listenerId);
        receiver = new MyReceiver();
        registerReceiver(receiver, filter);

        Log.d(TAG, "created broadcast receiver");
    }

    @Override
    public void onLocationChanged(Location Loc) {
        Log.d(TAG, "Location Manager receive an intent");
    	/*locMgr_count++;
        timings.addSplit(Integer.toString(locMgr_count));
        timings.dumpToLog();*/

        double lat = Loc.getLatitude();
        double lon = Loc.getLongitude();
        String l = "Longitude: " + lon + " Latitude: " + lat;
        Toast.makeText(getApplicationContext(), l ,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    protected void onStop(){
        super.onStop();
        //unregister the receiver and call onStop in PbdLocManager
        unregisterReceiver(receiver);
        pbdmanager.pbdOnStop(KEY);
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "MyReceiver receive an intent");

            try{
                PbdLocation pbdLoc = pbdmanager.getPbdLocation(KEY);
				/*pbdLocMgr_count++;
				timings2.addSplit(Integer.toString(pbdLocMgr_count));
				timings2.dumpToLog();*/

                if(pbdLoc == null){
                    String str = "reached dpa limit, can't get location";
                    Toast.makeText(getApplicationContext(), str,Toast.LENGTH_SHORT).show();
                    return;
                }
                double lat = pbdLoc.getLatitude();
                double lon = pbdLoc.getLongitude();
                String l = "Pbd Longitude: " + lon + " Latitude: " + lat;
                Toast.makeText(getApplicationContext(), l ,Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SUCCESS to call getLoc function");
            }
            catch (Exception e){
                Log.d(TAG, "FAILED to call getLoc function");
                e.printStackTrace();
            }
        }
    }

    public class MockLocationProvider {
        String providerName;
        Context ctx;

        public MockLocationProvider(String name, Context ctx) {
            this.providerName = name;
            this.ctx = ctx;

            LocationManager lm = (LocationManager) ctx.getSystemService(
                    Context.LOCATION_SERVICE);
            lm.addTestProvider(providerName, false, false, false, false, false,
                    true, true, 0, 5);
            lm.setTestProviderEnabled(providerName, true);
        }

        public void pushLocation(double lat, double lon) {
            LocationManager lm = (LocationManager) ctx.getSystemService(
                    Context.LOCATION_SERVICE);

            Location mockLocation = new Location(providerName);
            mockLocation.setLatitude(lat);
            mockLocation.setLongitude(lon);
            mockLocation.setAltitude(0);
            mockLocation.setTime(System.currentTimeMillis());
            mockLocation.setElapsedRealtimeNanos(System.currentTimeMillis());
            mockLocation.setAccuracy(2);
            lm.setTestProviderLocation(providerName, mockLocation);
        }

        public void shutdown() {
            LocationManager lm = (LocationManager) ctx.getSystemService(
                    Context.LOCATION_SERVICE);
            lm.removeTestProvider(providerName);
        }
    }
}
