/* Guanjie Liu (gl1315@ic.ac.uk)
 * Imperial College London
 */

/*PbdService.java */

/*
This Service will run within the system process and has permission to access all API's.
Methods the PbdService class are used by the Pbd API libraries. When a data request is made, 
this service will check whether a data request is valid and if so, provide data to the application.
*/
package com.android.server;

import android.os.PbdLocation;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.IPbdService;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import android.os.Binder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.security.SecureRandom;
import java.math.BigInteger;

// Libraries for editing DPA
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

// Libraries for Dates
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// Libraries for Location
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

// Libraries for Device Id
import android.telephony.TelephonyManager;
import android.provider.Settings.Secure;

// Libraries for Contacts
import android.net.Uri;
import android.provider.ContactsContract;

// Libraries for Broadcast
import android.os.UserHandle;

import android.util.TimingLogger;

public class PbdService extends IPbdService.Stub {                   

	private static final String TAG = "PbdService";
	private PbdWorkerThread mWorker;
	private PbdWorkerHandler mHandler;
	private Context mContext;
	
	//Variables for getting location 
	private LocationManager locationManager;
	private Location lastKnown;

	private TelephonyManager tm;
	private MyLocationListener listener;
	private List<MyLocationListener> listenerList;

	// bool to swith on pbd authentication. used in testing
	private final Boolean pbdAuthentication = true;

	private TimingLogger timings;

	public PbdService(Context context) {
		super();
		mContext = context;
		mWorker = new PbdWorkerThread("PbdServiceWorker");
		mWorker.start();
		Log.i(TAG, "Spawned worker thread");

		//Initialize the locationManager when the PbdService starts
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		//Initialize the Listener List
		listenerList = new ArrayList<MyLocationListener>();

		//Initialize telephony manager
		tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////
//   PbdDeviceManager's system service
/////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Service Method: getDeviceId
     * Returns the unique device ID, for example, 
     * the IMEI for GSM and the MEID or ESN for CDMA phones.
     */
    
    public String getDeviceId(String key){
    	Log.i(TAG, "into getDeviceId");
    	int resultCode;
		//Attempt authentication and return string accordingly
    	if (pbdAuthentication){
    		resultCode = authenticateIdentifier(key, "DeviceId");
    		if (resultCode == 1)
    			return "refused";
    		else if (resultCode == 2)
    			return "error";
    	}
    	Log.i(TAG, "Device Id GRANTED to " + key);
    	return tm.getDeviceId();
    }


	/*
     * Service Method: getSimSerialNumber
     * Returns the serial number of the SIM, if applicable. 
     * Return null if it is unavailable. 
     */
	public String getSimSerialNumber(String key){
		Log.i(TAG, "into getSimSerialNumber");
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateIdentifier(key, "SimSerialNumber");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		Log.i(TAG, "Sim Serial GRANTED to " + key);
		return tm.getSimSerialNumber();
	}


	 /*
     * Service Method getAndroidId
     * Returns a 64-bit number (as a hex string) that is randomly generated 
     * when the user first sets up the device and should remain constant 
     * for the lifetime of the user's device. The value may change 
     * if a factory reset is performed on the device. 
     */
	 public String getAndroidId(String key){
	 	Log.i(TAG, "into getAndroidId");
	 	int resultCode;
		//Attempt authentication and return string accordingly
	 	if (pbdAuthentication){
	 		resultCode = authenticateIdentifier(key, "AndroidId");
	 		if (resultCode == 1)
	 			return "refused";
	 		else if (resultCode == 2)
	 			return "error";
	 	}
	 	Log.i(TAG, "Android Id GRANTED to " + key);
	 	return Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
	 }


	/*
     * Service Method: getGroupIdLevel1
     * Returns the Group Identifier Level1 for a GSM phone. 
     * Return null if it is unavailable. 
     */
	public String getGroupIdLevel1(String key){
		Log.i(TAG, "into getGroupIdLevel1");
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateIdentifier(key, "GroupIdLevel1");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		Log.i(TAG, "Group Id Level 1 GRANTED to " + key);
		return tm.getGroupIdLevel1();
	}


	/*
     * Service Method: getLine1Number
     * Returns the Line1Number for the device. 
     * Return null if it is unavailable. 
     */
	public String getLine1Number(String key){
		Log.i(TAG, "into getLine1Number");
		int resultCode;
		if (pbdAuthentication){
			resultCode = authenticateIdentifier(key, "Line1Number");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		Log.i(TAG, "Line 1 Number GRANTED to " + key);
		return tm.getLine1Number();
	}


	/*
     * Service Method: getSubscriberId
     * Returns the Subscriber Id for the device. 
     * Return null if it is unavailable. 
     */
	public String getSubscriberId(String key){
		Log.i(TAG, "into getSubscriberId");
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateIdentifier(key, "SubscriberId");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		Log.i(TAG, "Subscriber Id granted to " + key);
		return tm.getSubscriberId();
	}


	/*
     * Service Method: getVoicemailAlphaTag
     * Returns the Voicemail Alpha Tag for the device. 
     * Return null if it is unavailable. 
     */
	public String getVoiceMailAlphaTag(String key){
		Log.i(TAG, "into getVoiceMailAlphaTag");
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateIdentifier(key, "VoiceMailAlphaTag");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		Log.i(TAG, "Voice Mail alpha tag granted to " + key);
		return tm.getVoiceMailNumber();
	}


	/*
     * Service Method: getVoiceMailNumber
     * Returns the Voicemail Number for the device. 
     * Return null if it is unavailable. 
     */
	public String getVoiceMailNumber(String key){
		Log.i(TAG, "into getVoiceMailNumber");
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateIdentifier(key, "VoiceMailNumber");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		Log.i(TAG, "Voice Mail number granted to " + key);
		return tm.getVoiceMailNumber();
	}


/////////////////////////////////////////////////////////////////////////////////////////////////////////
//   PbdLocationManager's system service
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	* Function used by Pbd Location Manager to when location updates
	* are requested by an app.
	* Returns the unique listener id for the app.
	* If request is invalid, will return null to Pbd Location Manager
	* If request is legal, PbdService notifies the 
	* app of location updates by a broadcast intent.
	*/
	public String requestPbdLocationUpdates(long minTime, float minDistance, String key, String provider) {
		
		Log.i(TAG, "Searching/Registering listener. key is: " + key);

		//Check that location updates request is legal
		if(pbdAuthentication){
			int resultCode = authenticateLocationUpdates(key, provider);
			if(resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}

		int listenerNo = registerListener(key);  // returns the listener index in the list
		String listenerId = listenerList.get(listenerNo).getListenerId(); // returns the unique listener id
		String locationProvider = "none";

		Log.i(TAG, "Checking Provider");
		//Check Provider
		if (provider.equals("finelocation")){
			Log.i(TAG, "Fine Location Requested");
			locationProvider = LocationManager.GPS_PROVIDER;
			listenerList.get(listenerNo).setProvider(provider);
		}
		else if (provider.equals("coarselocation")){
			Log.i(TAG, "Coarse Location Requested");
			locationProvider = LocationManager.NETWORK_PROVIDER;
			listenerList.get(listenerNo).setProvider(provider);
		}

		//Clear calling identity and register the Location Listener
		final long ident = Binder.clearCallingIdentity();
		locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, 
			listenerList.get(listenerNo), Looper.getMainLooper());
		Binder.restoreCallingIdentity(ident);

		return listenerId; // will be used by the app to setup filter in the broadcast receiver
	}


	/*
	* Function used by apps to request a single location update
	* Returns the unique listener id for the app.
	* If request is invalid, will return null to Pbd Location Manager
	* If request is legal, PbdService notifies the 
	* app of the location update update by a broadcast intent.
	*/
	public String requestSingleUpdate(String key, String provider){
		Log.i(TAG, "Going to attempt to requestSingleUpdate: " + key);

		//Check that location updates request is legal
		if(pbdAuthentication){
			int resultCode = authenticateLocationUpdates(key, provider);
			if(resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}

		int listenerNo = registerListener(key);
		String listenerId = listenerList.get(listenerNo).getListenerId();
		String locationProvider = "none";

		//Check Provider
		if (provider.equals("finelocation")){
			Log.i(TAG, "Fine Location Requested");
			locationProvider = LocationManager.GPS_PROVIDER;
			listenerList.get(listenerNo).setProvider(provider);
		}
		else if (provider.equals("coarselocation")){
			Log.i(TAG, "Coarse Location Requested");
			locationProvider = LocationManager.NETWORK_PROVIDER;
			listenerList.get(listenerNo).setProvider(provider);
		}

		//ADD CHECK TO SEE IF REQUEST IS LEGAL

		//Clear calling identiy and register the listener for a
		//single location update
		final long ident = Binder.clearCallingIdentity();
		locationManager.requestSingleUpdate(locationProvider,  
			listenerList.get(listenerNo), Looper.getMainLooper());
		Binder.restoreCallingIdentity(ident);

		return listenerId;
	}


	/*
	* registerListener
	* Searches listener list to check if app already has
	* a location listerner.
	* If no listener is found for the app, creates a new listener
	* with a new unique listenerId
	* returns the index of the location listener.
	*/
	protected int registerListener(String key){
		int listenerNo = searchListenerList(key);
		String listenerId;

		if (listenerNo  < 0){
			try {
				// Generate listener Id and add new listener to the arraylist
				// of location listeners
				listenerId = newListenerId();
				listenerList.add(new MyLocationListener(key, listenerId));
			}
			catch (Exception e){
				Log.e(TAG, "FAILED to create MyLocationListener");
				e.printStackTrace();
			}
			listenerNo = listenerList.size() - 1;
			Log.i(TAG, "New Listener Created. listenerNo is " + listenerNo);
		}
		else {
			Log.i(TAG, "listener was already found at " + listenerNo);			
		}
		return listenerNo;
	}


	/*
	* Function to retrive an available PbdLocation updates
	* Used by apps through the Pbd Location Manager Library
	* when they receive a broadcast of a new available location update
	*/
	public PbdLocation getPbdLoc(String key){
		Log.i(TAG, "getPbdLoc with key: " + key);

		//find listener of calling up
		int listenerNo = searchListenerList(key);

		Log.i(TAG, "Listener search returned: " + listenerNo);

		//return null if listener does not exist for calling app
		if (listenerNo == -1)
			return null;

		// if the updateAvailable bool is true for the
		// listener, return the location
		// else return null
		if (listenerList.get(listenerNo).isUpdateAvailable()){
			Log.i(TAG, "getPbdLoc: update Available. returning PbdLocation to App");

			//get the listeners location provider type
			String provider = listenerList.get(listenerNo).getProvider();

			//Check that location updates request is legal
			if(pbdAuthentication){
				int resultCode = authenticateLocationUpdates(key, provider);
				if(resultCode != 0){ return null; }
			}

			try{
				// reset the DPA
				JSONObject dpa = dpaReader(key);
				JSONObject location = dpa.getJSONObject("Location");
				JSONObject locationScope = location.getJSONObject(provider);
				int used = locationScope.getInt("Used");
				int total_used = locationScope.getInt("Total_Used");
				locationScope.put("Used", used+1);
				locationScope.put("Total_Used", total_used+1);

				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				locationScope.put("Last_Accessed", now.toString());
				dpaWriter(key, dpa);
			}catch(Exception e){}
			
			if (provider.equals("finelocation")){
				provider = LocationManager.GPS_PROVIDER;
			}
			else if (provider.equals("coarselocation")){
				provider = LocationManager.NETWORK_PROVIDER;
			}

			//Initialize a location
			Location rtnLocation = new Location("paybydata");

			//Clear calling identiy and get the Location Manger's last Known Location
			final long ident = Binder.clearCallingIdentity();
			rtnLocation = locationManager.getLastKnownLocation(provider);
			Binder.restoreCallingIdentity(ident);

			// Convert the Location from android.Location to PbdLocation
			// so that the app does not require location permissions
			PbdLocation pbdLocation = new PbdLocation(rtnLocation);

			// signal that the update has been taken so that
			// updateAvailable can be set to false
			listenerList.get(listenerNo).updateTaken();

			/*timings.addSplit("getPbdLoc");
       		timings.dumpToLog();*/

			return pbdLocation;
		}

		return null;
	}

	/*
	* Returns a String
	* Used during testing to check whether
	* the Pbd Service is running correctly.
	*/
	public String getString(){
		return "PbdService Running Correctly";
	}


	/*
	* Service Method: removeLocationUpdates
	* To be called when 
	*/
	public void removeLocationUpdates(String key){

		Log.i(TAG, "Called removeLocationUpdates onStop");
		Log.i(TAG, "Searching for listener with key " + key);

		//Find listnerNo of calling app
		int listenerNo = searchListenerList(key);

		Log.i(TAG, "Listener was found at location: " + listenerNo);

		if (listenerNo >= 0){
			Log.i(TAG, "Attempting to remove Location updates for listener");
			try {
				//Stop updates and remove listner
				final long ident = Binder.clearCallingIdentity();
				locationManager.removeUpdates(listenerList.get(listenerNo));
				Binder.restoreCallingIdentity(ident);
			}
			catch (Exception e){
				Log.e(TAG, "FAILED: to remove Location updates for listener");
				e.printStackTrace();
			}
		}
		else{
			Log.i(TAG, "No listener found for " + key);
		}
	}

	/*
	* Service Method: onStop
	* to be called by PbdFramework with app calls OnStop
	* will remove the location listener
	*/
	public void onStop(String key){

		Log.i(TAG, "Called PbdService onStop");
		Log.i(TAG, "Searching for listener with key " + key);

		//Find listnerNo of calling app
		int listenerNo = searchListenerList(key);

		Log.i(TAG, "Listener was found at location: " + listenerNo);

		if (listenerNo >= 0){
			Log.i(TAG, "Attempting to remove listener");
			try {
				//Stop updates and remove listner
				final long ident = Binder.clearCallingIdentity();
				locationManager.removeUpdates(listenerList.get(listenerNo));
				Binder.restoreCallingIdentity(ident);

				//Delete the listener from the listenerList
				listenerList.remove(listenerNo);
				Log.i(TAG, "SUCCESS: deleting listener");
			}
			catch (Exception e){
				Log.e(TAG, "FAILED: deleting listener");
				e.printStackTrace();
			}
		}
		else{
			Log.i(TAG, "No listener found for " + key);
		}
	}

	/*
	* Thread class which the PbdService runs in
	* Each service running withing the root system process
	* normally runs in its own thread.
	* PbdWorkerThread is initialized and run in PbdService's constructor
	*/
	private class PbdWorkerThread extends Thread {
		public PbdWorkerThread(String name) {
			super(name);
		}
		public void run() {
			// Initialize the current thread as a looper
			// this thread can have a MessageQueue now
			Looper.prepare();
			mHandler = new PbdWorkerHandler();
			// Run the message queue in this thread
			Looper.loop();
		}
	}


	/*
	* Handler class to deal with messages
	* 
	*/
	private class PbdWorkerHandler extends Handler {
		private static final int MESSAGE_SET = 0;
		@Override
		public void handleMessage(Message msg) {
			try {
				if (msg.what == MESSAGE_SET) {
					Log.i(TAG, "set message received: " + msg.arg1);
				}
			} catch (Exception e) {
				// Log, don't crash!
				Log.e(TAG, "Exception in PbdWorkerHandler.handleMessage:", e);
			}
		}
	}


	/*
	* Helper Function to search for a
	* MyLocationListener within the Listenerlist
	* returns -1 if not in list
	*/
	private int searchListenerList(String key){
		Log.i(TAG, "into searchListenerlist with key: " + key);
		int s = listenerList.size();
		for(int i = 0; i < listenerList.size(); i++){
			if ((listenerList.get(i).getKey()).equals(key)){
				return i;
			}
		}
		return -1;
	}
	

	/*
	* Helper Function to generate a
	* new unique listener Id for a location listener
	* returns a 32 character string.
	*/
	private String newListenerId(){
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	/*
	* My Location Listener is uses to trigger methods when a location 
	* update is received. The OnLocationChanged sends a intent Broadcast
	* notifying an app of available location update
	*/
	public class MyLocationListener implements LocationListener {

		//The id of the calling app is stored as a string
		// and initialized by the MyLocationListener constuctor
		private String key;
		private String listenerId;
		private String provider;
		private Boolean updateAvailable;
		private Boolean replication;


		public MyLocationListener(String _key, String _listenerId){
			key = _key;
			listenerId = _listenerId;
			provider = null;
			updateAvailable = false;
			// If replication is granted, location data will be replicated to
			// to the central database.
			try{
				JSONObject dpa = dpaReader(_key);
				if(dpa.getString("Replication").equals("granted"))
					replication = true;
				else 
					replication = false;
			}catch(Exception e){}
		}

		/*
		* getKey
		* Accessor function for the Key
		*/
		public String getKey(){
			return this.key;
		}

		/*
		* getListenerId
		* Accessor function for the ListenerId
		*/
		public String getListenerId(){
			return this.listenerId;
		}

		/*
		* getProvider
		* Accessor function for the provider
		*/
		public String getProvider(){
			return this.provider;
		}


		/*
		* getProvider
		* Sets the provider to the argument passed
		* will either be set to "coarselocation" or "finelocation"
		*/
		public void setProvider(String _provider){
			provider = _provider;			
		}

		/*
		* isUpdateAvailable 
		* Returns the status of updateAvailable
		*/
		public Boolean isUpdateAvailable(){
			return updateAvailable;
		}

		/*
		* updateTaken 
		* used when an app takes the location update
		* sets updateAvailable to false;
		*/
		public void updateTaken(){
			updateAvailable = false;
		}

		/*
		* onLocationChanged
		* Function invoked when the MyLocationListener receives
		* a location update.
		* Broadcasts an intent with the MyLocationListeners unique
		* listenerId. The broadcast is intended to be received by
		* the app as a notification of a new location update.
		*/
		@Override
		public void onLocationChanged(Location loc)
		{
        	//Broadcast a notification when the location has changed
			Log.i(TAG, "Broadcasting Intent with action: " + listenerId);
			//Set updateAvailable to true so location can be accessed.

			// timings = new TimingLogger(TAG, "onLocationChanged");

			updateAvailable = true;
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			intent.setAction(listenerId);
			mContext.sendBroadcast(intent);

			// Broadcast a notification to Central Database if replication is granted
			if(!replication){ return; }
			
			String updateType = "";
			if(provider.equals("finelocation")){
				updateType = "action_fine_loc";
			}
			else if(provider.equals("coarselocation")){
				updateType = "action_coarse_loc";
			}

			Log.i(TAG, "Broadcasting Intent to CDB");
			Intent intent2 = new Intent("pbd1234");
			intent2.putExtra("key", key);
			intent2.putExtra("updateType", updateType);
			mContext.sendBroadcast(intent2);
		}

		@Override
		public void onProviderDisabled(String provider)
		{
	    	//Method Unused
		}

		@Override
		public void onProviderEnabled(String provider)
		{
	    	//Method Unused	    }
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
	    	//Method Unused
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//   PbdContactsManager's system service
/////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
	* getContacts
	* Returns a Uri of all contacts from Contacts.Contract
	* 
	*/
	public Uri getContacts(String key){
		Log.i(TAG, "into getContacts: " + key);
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateContact(key, "contacts");
			if (resultCode == 1)
				return null;
			else if (resultCode == 2)
				return null;
		}
		Log.i(TAG, "Voice Mail number granted to " + key);
		return ContactsContract.Contacts.CONTENT_URI;
	}

	/*
	* getRowId
	* Returns a Uri of the Row Id column 
	* from the Contacts Provider
	*/
	public String getRowId(String key){
		Log.i(TAG, "into getRowId: " + key);
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateContact(key, "rowId");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		return ContactsContract.Contacts._ID;
	}

	/*
	* getDisplayName
	* Returns a Uri of the Display Name column
	* from the Contacts Provider
	*/
	public String getDisplayName(String key){
		Log.i(TAG, "into getDisplayName: " + key);
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateContact(key, "displayName");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		return ContactsContract.Contacts.DISPLAY_NAME;
	}

	/*
	* getHasPhoneNumber
	* Returns a Uri of ''Has Phone Number' colum
	* from the Contacts Provider
	*/
	public String getHasPhoneNumber(String key){
		Log.i(TAG, "into getHasPhoneNumber: " + key);
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateContact(key, "hasPhoneNumber");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		return ContactsContract.Contacts.HAS_PHONE_NUMBER;
	}

	/*
	* getCdkPhoneContentUri
	* Returns CommonDataKinds Phone Content Uri
	*/
	public Uri getCdkPhoneContentUri(String key){
		Log.i(TAG, "into getCdkPhoneContentUri: " + key);
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateContact(key, "cdkPhoneContentUri");
			if (resultCode == 1)
				return null;
			else if (resultCode == 2)
				return null;
		}
		return ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	}

	/*
	* getCdkPhoneContactId
	* Returns String of CommonDataKinds Phone Contact Id
	*/
	public String getCdkPhoneContactId (String key){
		Log.i(TAG, "into getCdkPhoneContactId: " + key);
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateContact(key, "cdkPhoneContactId");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		return ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
	}

	/*
	* getCdkPhoneNumber
	* Returns String of CommonDataKinds Phone Number
	*/
	public String getCdkPhoneNumber (String key){
		Log.i(TAG, "into getCdkPhoneNumber: " + key);
		int resultCode;
		//Attempt authentication and return string accordingly
		if (pbdAuthentication){
			resultCode = authenticateContact(key, "cdkPhoneNumber");
			if (resultCode == 1)
				return "refused";
			else if (resultCode == 2)
				return "error";
		}
		return ContactsContract.CommonDataKinds.Phone.NUMBER;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//   Authentication Methods
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
     * dpaReader
     * Used by authentication methods to read DPA
     * and return it as a Json Object
     */	
     private JSONObject dpaReader(String key) throws Exception{

     	String filename = "/data/data/"+key+".DPA.json";
     	InputStream fin = new FileInputStream(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null){
			sb.append(line).append("\n");
		}
		reader.close();
		fin.close();
		String dpa_str = sb.toString();

		// Convert the content into JSON object
		JSONObject dpa = new JSONObject(dpa_str);
		return dpa;
     }

     private void dpaWriter(String key, JSONObject dpa) throws Exception{
     	String filename = "/data/data/"+key+".DPA.json";

     	// put JSON object back into the file.
		String new_dpa_str = dpa.toString(4);
		Writer output;
		output = new BufferedWriter(new FileWriter(filename));
		output.append(new_dpa_str);
		output.close();
     }

     private boolean isDpaValid(JSONObject dpa){
		Date now = null;
     	Date expireDate = null;
     	try{
     		// create a calendar for the current time
	     	//Calendar calendar = Calendar.getInstance();
	     	now = new Date();

	     	// create a calendar for the expire time
	     	//Calendar calendar2 = Calendar.getInstance();
	     	String expireDate_str = dpa.getString("Valid_Until");

	     	// the SimpleDateFormat must match with the format of the valid_until string in DPA
	     	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
	        expireDate = simpleDateFormat.parse(expireDate_str);
     	}catch(Exception e){
     		e.printStackTrace();
     		Log.e(TAG, "error in isDpaValid");
     	}
     	return expireDate.after(now);
     }

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//   Authentication Methods for Location Manager
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
     * authenticateLocationUpdates
     * Used by PbdLocationManager system service methods to check
     * whether request is legal
     * returns 0 if request granted
     * returns 1 if request refused
     * returns 2 if there is a error in request process
     */	
	private int authenticateLocationUpdates(String key, String scope){
		Log.i(TAG, "authenticate location updates with key: " + key + " scope: " + scope);

		// Read in the contents in the DPA file
		JSONObject dpa;
		JSONObject location;
		JSONObject locationScope;
		try{
			dpa = dpaReader(key);
			location = dpa.getJSONObject("Location");
			locationScope = location.getJSONObject(scope);
		}catch(Exception e){
			Log.e(TAG, "Error in parsing the DPA file",e);
			return 2;
		}

		try{
			// see if the DPA is still valid
			if(isDpaValid(dpa) == false){ return 1; }

			// see if the location used counter needs to reset
			Date now = new Date();
			String last_accessed_str = locationScope.getString("Last_Accessed");
			if(last_accessed_str.equals("null") == false){
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		        Date last_accessed = simpleDateFormat.parse(last_accessed_str);
		        if(last_accessed.getDate() != now.getDate() ||
		        	last_accessed.getMonth() != now.getMonth() ||
		        	last_accessed.getYear() != now.getYear()){

		        	locationScope.put("Used", 0);
		        	dpaWriter(key, dpa);
		        	Log.d(TAG, "It's a new day, so reset counter!");
	        	}
			}
			
			if(locationScope.getInt("Used") >= locationScope.getInt("Frequency")){ return 1; }
			else{ return 0; }
		}catch(Exception e){
			Log.e(TAG, "Error in parsing the DPA file", e);
			return 2;
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//   Authentication Methods for Device Manager
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
     * authenticateIdentifier
     * Used by PbdDeviceManager system service methods to check
     * whether request is legal
     * returns 0 if request granted
     * returns 1 if request refused
     * returns 2 if there is a error in request process
     */	
	private int authenticateIdentifier(String key, String scope){
		Log.i(TAG, "authenticate Identifier with key: " + key + " scope: " + scope);	

		// Read in the contents in the DPA file
		JSONObject dpa;
		JSONObject identifier;
		try{
			dpa = dpaReader(key);
			identifier = dpa.getJSONObject("Identifier");
		}catch(Exception e){
			Log.e(TAG, "Error in parsing the DPA file",e);
			return 2;
		}

		try{
			// see if the DPA is still valid
			if(isDpaValid(dpa) == false){ return 1; }

			if(identifier.getString(scope).equals("granted")){ return 0; }
			else if(identifier.getString(scope).equals("refused")){ return 1; }
			else{ 
				Log.e(TAG, "NOT granted or refused");
				return 2; 
			}
		}catch(Exception e){
			Log.e(TAG, "Error in reading the JSON object", e);
			return 2;
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//   Authentication Methods for Contact Manager
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
     * authenticateContact
     * Used by PbdContactsManager system service methods to check
     * whether request is legal
     * returns 0 if request granted
     * returns 1 if request refused
     * returns 2 if there is a error in request process
     */	
	private int authenticateContact(String key, String scope){
		Log.i(TAG, "authenticate contact with key: " + key + " scope: " + scope);	

		// Read in the contents in the DPA file
		JSONObject dpa;
		JSONObject contact;

		try{
			dpa = dpaReader(key);
			contact = dpa.getJSONObject("Contact");
		}catch(Exception e){
			Log.e(TAG, "Error reading the DPA file",e);
			return 2;
		}

		try{
			// see if the DPA is still valid
			if(isDpaValid(dpa) == false){ return 1; }

			if(contact.getString(scope).equals("granted")){ return 0; }
			else if(contact.getString(scope).equals("refused")){ return 1; }
			else{ return 2; }
		}catch(Exception e){
			Log.e(TAG, "Error in reading the JSON object", e);
			return 2;
		}
	}
}