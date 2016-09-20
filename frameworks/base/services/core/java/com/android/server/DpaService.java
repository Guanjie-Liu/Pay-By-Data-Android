/* Guanjie Liu (gl1315@ic.ac.uk)
 * Imperial College London
 */

/*DpaService.java */

/*
This Service will run within the system process and has permission to access all API's.
Methods the DpaService class are used by the DPA API libraries. 
*/
package com.android.server;

import android.content.Context;
import android.os.IDpaService;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

// Libraries for editing DPA
import org.json.JSONObject;
import org.json.JSONArray;
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
import java.io.File;

// Libraries for Dates
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DpaService extends IDpaService.Stub {

	private Context mContext;
	private MyReceiver receiver;
	private ConnectivityManager cm;

	private static final String TAG = "DpaService";
	private static final String DPA_LIST = "/data/data/dpaList.json";

	public DpaService(Context context) {
		super();
		mContext = context;
		cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		receiver = new MyReceiver();
		mContext.registerReceiver(receiver, filter);
	}

	/* MyReceiver is used for detecting any connectivity changed,
	 * if an valid internet connection is established, it checks
	 * which DPA has expired. If it found one, it will broadcast
	 * the info to the PBD App Store.
	 */
	public class MyReceiver extends BroadcastReceiver{
	
		@Override
		public void onReceive(Context context, Intent intent_in) {
			Log.d(TAG, "DpaService receiver receive an CONNECTIVITY_CHANGE intent");
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
			// if there is no internet connection, return
			if(!isConnected){ return; }

			try{
				String dpaList_str = reader(DPA_LIST);
				JSONArray dpaList = new JSONArray(dpaList_str);
				int length = dpaList.length();
				// check all DPAs in the system
				while(length > 0){
					String filepath = "/data/data/" + dpaList.getString(length-1);
					JSONObject dpa = new JSONObject(reader(filepath));

					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			        Date expireDate = simpleDateFormat.parse(dpa.getString("Valid_Until"));
			        Calendar calendar = Calendar.getInstance();
					Date currentDate = calendar.getTime();
					if(currentDate.after(expireDate)){
						/*Send an intent to the PBD App Store with the location usage of the app
						to allow the app to calculate how much monetary return the user should get
						from the app developer*/
						Log.i(TAG, "Broadcasting Intent to PBD App Store");

						String appId = dpa.getString("AppId");
						JSONObject location = dpa.getJSONObject("Location");
						JSONObject finelocation = location.getJSONObject("finelocation");
						JSONObject coarselocation = location.getJSONObject("coarselocation");
						Intent intent = new Intent("pbd1234_dpaExpire");
						intent.putExtra("appid", appId);
						intent.putExtra("fine_location_usage", finelocation.getInt("Total_Used"));
						intent.putExtra("coarse_location_usage", coarselocation.getInt("Total_Used"));

						mContext.sendBroadcast(intent);

						File file = new File("/data/data/"+dpaList.getString(length-1));
						file.delete();
						dpaList.remove(length-1);
					}
					length--;
				}
				writer(dpaList.toString(4), DPA_LIST);
			}catch(Exception e){}		
		}
	}

	/* installDPA
 	 * used by PBD app store to install DPA when an app is downloaded
 	 * return true if installation succeed or false otherwise;
 	 */
	public boolean installDPA(String key, String dpa_str){
		if(key.equals("pbdappstore_key123") == false){
			Log.e(TAG, "Illegal attempt to install DPA");
			return false;
		}

		try{
			// Convert the read-in content into JSON object
			JSONObject dpa = new JSONObject(dpa_str);

			// Set up the valid time of the dpa
			int duration = dpa.getInt("Duration");
			Calendar calendar = Calendar.getInstance();
			Date date = calendar.getTime();
			dpa.put("Valid_From", date.toString());
			calendar.add(Calendar.DAY_OF_YEAR, duration);
			date = calendar.getTime();
			dpa.put("Valid_Until", date.toString());

			// Add more information for location service authentication
			JSONObject location = dpa.getJSONObject("Location");
			JSONObject fine_location = location.getJSONObject("finelocation");
			JSONObject coarse_location = location.getJSONObject("coarselocation");

			fine_location.put("Used", 0);
			fine_location.put("Total_Used", 0);
			fine_location.put("Last_Accessed","null");
			coarse_location.put("Used", 0);
			coarse_location.put("Total_Used", 0);
			coarse_location.put("Last_Accessed","null");

			// store JSON object as string in /data/data
			String dpaKey = dpa.getString("Key");
			String filepath = "/data/data/"+dpaKey+".DPA.json";

			String new_dpa_str = dpa.toString(4);
			writer(new_dpa_str, filepath);
			Log.d(TAG, new_dpa_str);

			// Update the dpaList document
			// dpaList.json is used to record the existing DPA in the system
			JSONArray dpaList = new JSONArray();
			File file = new File(DPA_LIST);
			// read in the dpaList if it already exists
			if(file.exists()){
				String string = reader(DPA_LIST);
				dpaList = new JSONArray(string);
			}
			// go through the dpaList to see if the installing DPA is already recorded
			int length = dpaList.length();
			while(length > 0){
				if((dpaKey+".DPA.json").equals(dpaList.getString(length-1)))
					return true;	
				else
					length--;	
			}
			// Add the name of the DPA to the dpaList
			dpaList.put(dpaKey+".DPA.json");
			String dpaList_str = dpaList.toString(4);
			writer(dpaList_str, DPA_LIST);
			Log.d(TAG, dpaList_str);
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG, "Error in installing DPA!");
			return false;
		}
		return true;
	}

	/* readDpaList
 	 * used by central_database and PbdAppStore to read the DPA List in the system
 	 * return the list as string if succeed or null otherwise
 	 */
	public String readDpaList(String key){
		if(key.equals("newcentraldatabase_key123") == false
			&& key.equals("pbdappstore_key123") == false){
			Log.e(TAG, "Illegal attempt to read DPA");
			return "You are not allowed to read DPA List!";
		}

		String string = reader("/data/data/dpaList.json");
		return string;
	}

	/* readDpa
 	 * used by central_database and PbdAppStore to read the DPA of an app
 	 * return the dpa as string if succeed or null otherwise
 	 */
	public String readDpa(String key, String dpaKey){
		if(key.equals("newcentraldatabase_key123") == false
			&& key.equals("pbdappstore_key123") == false){
			Log.e(TAG, "Illegal attempt to read DPA");
			return "You are not allowed to read DPA!";
		}

		String string = reader("/data/data/"+dpaKey+".DPA.json");
		return string;
	}


	/* private helper function to read file into string
	 */
	private String reader(String filepath){
		try{
	     	InputStream fin = new FileInputStream(filepath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				sb.append(line).append("\n");
			}
			reader.close();
			fin.close();
			return sb.toString();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG, "Error in reading "+filepath);
			return null;
		}
	}

	/* private helper function to write content into
	 * file and overwrite any original content
	 */	
	private void writer(String content, String filepath){
		try{
			Writer output;
			output = new BufferedWriter(new FileWriter(filepath));
			output.append(content);
			output.close();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG, "Error in writing "+filepath);
		}
	}
}