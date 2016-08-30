package com.example.systemlagtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PbdLocation;
import android.util.Log;
import android.widget.Toast;

//Import the pbd Library
import com.example.android.pbd.PbdLocationManager;
import com.example.android.pbd.PbdContactsManager;
import com.example.android.pbd.PbdDeviceManager;
import com.example.android.dpa.DpaManager;

import org.json.JSONObject;
import java.lang.StringBuilder;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import android.content.res.AssetManager;

// libraries for testing system lag induced by PBD
import android.util.TimingLogger;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;

public class MainActivity extends Activity implements LocationListener {

	private PbdLocationManager pbdLocManager;
	private PbdDeviceManager pbdDevManager;
	private final String TAG = "SystemLagTest";
	private MyReceiver receiver;

	private TimingLogger timings;
	private TimingLogger timings2;
	private LocationManager locMgr;

	private int noLocations = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "into Oncreate method");

		String dpa_str = null;

		try{
			AssetManager am = getApplicationContext().getAssets();
			InputStream fin = am.open("com.example.systemlagtest.DPA.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				sb.append(line).append("\n");
			}
			reader.close();
			fin.close();
			dpa_str = sb.toString();
			Log.d(TAG, dpa_str);
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG, "Error in reading dpa file");
		}
		

		try{
			String appId = getApplicationContext().getPackageName();
			DpaManager dpa = new DpaManager(getApplicationContext());
			dpa.installDPA(dpa_str);
			Log.i(TAG, "My package name:"+appId);
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG, "Error in writing DPA!");
		}



		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		pbdLocManager = new PbdLocationManager(getApplicationContext());

		Log.d(TAG, "about to initialize pbdDevice Manager");

		//Testing obtining device id
		/*pbdDevManager = new PbdDeviceManager(getApplicationContext());
		Log.d(TAG, "created pbdDeviceManager");	
		Toast.makeText(getApplicationContext(), "Requesting Device and Telephony information" ,Toast.LENGTH_LONG).show();

		String id = pbdDevManager.getDeviceId();
		Toast.makeText(getApplicationContext(), "Device id returned: " + id ,Toast.LENGTH_SHORT).show();

		
		String simId = pbdDevManager.getSimSerialNumber();
		Toast.makeText(getApplicationContext(), "Sim id returned: " + simId ,Toast.LENGTH_SHORT).show();
	

		String androidId = pbdDevManager.getAndroidId();
		Toast.makeText(getApplicationContext(), "Android id returned: " + androidId ,Toast.LENGTH_SHORT).show();

		
		Toast.makeText(getApplicationContext(), "GroupidLev1 id returned: " + pbdDevManager.getGroupIdLevel1() ,Toast.LENGTH_SHORT).show();

		Toast.makeText(getApplicationContext(), "Line1number id returned: " + pbdDevManager.getLine1Number() ,Toast.LENGTH_SHORT).show();

		Toast.makeText(getApplicationContext(), "Subscriber id returned: " + pbdDevManager.getSubscriberId() ,Toast.LENGTH_SHORT).show();

		Toast.makeText(getApplicationContext(), "VoiceMainAlpha id returned: " + pbdDevManager.getVoiceMailAlphaTag() ,Toast.LENGTH_SHORT).show();

		Toast.makeText(getApplicationContext(), "VoiceMail Number returned: " + pbdDevManager.getVoiceMailNumber() ,Toast.LENGTH_SHORT).show();*/

		//////////////////////////////////////////////////////////////////////

		Log.d(TAG, "about to set up broadcast receiver");

		Toast.makeText(getApplicationContext(), "Requesting GPS Location updates every 3 seconds" ,Toast.LENGTH_LONG).show();
		//String listenerId = pbdLocManager.requestSingleUpdate(PbdLocationManager.FINE_LOCATION);
		timings = new TimingLogger(TAG, "LocationManager");
		timings2 = new TimingLogger(TAG, "PbdLocationManager");

		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
		String listenerId = pbdLocManager.requestLocationUpdates(3000, 0, PbdLocationManager.FINE_LOCATION);
		Toast.makeText(getApplicationContext(), "Listener Id: " + listenerId ,Toast.LENGTH_LONG).show();
		Log.d(TAG, "requested updates");

		IntentFilter filter = new IntentFilter(listenerId);
		receiver = new MyReceiver();
		registerReceiver(receiver, filter);

		Log.d(TAG, "created broadcast receiver");		
	}

	@Override
    public void onLocationChanged(Location pbdLoc) {
    	Log.d(TAG, "Location Manager receive an intent");
    	/*locMgr_count++;
        timings.addSplit(Integer.toString(locMgr_count));
        timings.dumpToLog();*/

        double lat = pbdLoc.getLatitude();
				double lon = pbdLoc.getLongitude();
				String l = "Pbd Longitude: " + lon + " Latitude: " + lat;
				Toast.makeText(getApplicationContext(), l ,Toast.LENGTH_SHORT).show();
				Log.d(TAG, "SUCCESS to call getLoc function");
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
		pbdLocManager.pbdOnStop();
	}

	public class MyReceiver extends BroadcastReceiver {
	
		@Override
		public void onReceive(Context context, Intent intent) {

				Log.d(TAG, "MyReceiver receive an intent");

				try{
				PbdLocation pbdLoc = pbdLocManager.getPbdLocation();
				timings2.addSplit("PbdLocationManager received location");
				timings2.dumpToLog();
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
}
