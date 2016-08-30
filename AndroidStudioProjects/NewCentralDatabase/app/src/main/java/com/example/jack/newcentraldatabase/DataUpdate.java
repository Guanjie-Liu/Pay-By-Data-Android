package com.example.jack.newcentraldatabase;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.DocumentsContract;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataUpdate extends IntentService {

    public static final String DB_NAME = "central_database";
    public static final String TAG = "DataUpdate";

    private static final String ACTION_FINE_LOC = "action_fine_loc";
    private static final String ACTION_COARSE_LOC = "action_coarse_loc";

    LocationManager locMgr;
    Manager manager = null;
    Database database = null;

    public DataUpdate() {
        super("DataUpdate");
        Log.d(TAG, "Service is constructed");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "Service is Created");

        try {
            locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }catch(Exception e){
            Log.e(TAG, "failed at creating location manager", e);
        }

        try {
            // Encrypt the local database
            String key = "password123456";
            DatabaseOptions options = new DatabaseOptions();
            options.setCreate(true);
            options.setEncryptionKey(key);

            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            Log.d(TAG, "Manager created");
            database = manager.openDatabase(DB_NAME, options);
            Log.d(TAG, "database is retrieved");
        } catch (Exception e) {
            Log.e(TAG, "Error getting database", e);
        }

    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "DataUpdate service is started");

        if (intent != null) {
            final String action = intent.getAction();
            final String appId = intent.getExtras().getString("appid");

            if (ACTION_FINE_LOC.equals(action)) {
                Location location = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                try{
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String l = appId + " gets us LocationManager Longitude: " + lon + " Latitude: " + lat;
                    Log.d(TAG, l);
                }
                catch (Exception e){
                    Log.d(TAG, "FAILED to get location");
                    e.printStackTrace();
                }

                updateDoc(database, location);
                outputContents(database, appId);
            }
            else if (ACTION_COARSE_LOC.equals(action)) {
                Location location = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                try{
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String l = "LocationManager Longitude: " + lon + " Latitude: " + lat;
                    Log.d(TAG, l);

                }
                catch (Exception e){
                    Log.d(TAG, "FAILED to get location");
                    e.printStackTrace();
                }
            }
            else{
                Log.d(TAG, "No action handle for " + action);
            }
            try {
                // synchronize the database only when the device is connected to internet
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected) {
                    startReplications(database);
                }
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Error on starting Replications", e);
            }
        }
    }

    private void updateDoc(Database database, Location loc) {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put("Type", "Location");
            properties.put("PBD_ID", "Benson2015");
            properties.put("Latitude", loc.getLatitude());
            properties.put("Longitude", loc.getLongitude());
            properties.put("Accuracy", loc.getAccuracy());
            properties.put("Time", loc.getTime());

            //  time to live each location data is 12 Hrs
            Date ttl = new Date(System.currentTimeMillis() + 86400000/2);
            Document document = database.createDocument();
            document.putProperties(properties);
            document.setExpirationDate(ttl);
        }catch (Exception e){
            Log.e(TAG, "Error updating document", e);
        }

        /*
        // Store location as JSON object array
        Document document = database.getDocument(documentId);
        try {
            Map<String, Object> updatedProperties = new HashMap<>();
            ArrayList<Map<String, Object>> locations = new ArrayList<>();
            Map<String, Object> location = new HashMap<>();
            location.put("Latitude", loc.getLatitude());
            location.put("Longitude", loc.getLongitude());
            location.put("Accuracy", loc.getAccuracy());
            location.put("Time", loc.getTime());
            // If the document is NOT empty
            if(document.getProperties() != null) {
                // Retrieve the original data first if document is not empty
                updatedProperties.putAll(document.getProperties());
                // Retrieve the original location data if it exists.
                if(updatedProperties.containsKey("Location")){
                    locations = (ArrayList<Map<String, Object>>) updatedProperties.get("Location");
                }
            }
            // update document with new data
            locations.add(location);
            updatedProperties.put("Location", locations);
            document.putProperties(updatedProperties);
            Log.d(TAG, "Document updated");
        }catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error updating document", e);
        }*/

/*
// Store location in arrays
        try {
            Map<String, Object> updatedProperties = new HashMap<String, Object>();
            Map<String, ArrayList> location = new HashMap<String, ArrayList>();

            ArrayList latitude = new ArrayList();
            ArrayList longitude = new ArrayList();

            // If the document is empty
            if(document.getProperties() == null) {

                // update document with new data
                latitude.add(loc.getLatitude());
                longitude.add(loc.getLongitude());
                location.put("Latitude", latitude);
                location.put("Longitude", longitude);
                updatedProperties.put("Location", location);
            }
            else{
                // Retrieve the original data first if document is not empty
                updatedProperties.putAll(document.getProperties());
                // Retrieve the original location data if it exists.
                if(updatedProperties.containsKey("Location")){
                    location = (Map<String, ArrayList>) updatedProperties.get("Location");
                    latitude = location.get("Latitude");
                    longitude = location.get("Longitude");
                }
                // update document with new data
                latitude.add(loc.getLatitude());
                longitude.add(loc.getLongitude());
                location.put("Latitude", latitude);
                location.put("Longitude", longitude);
                updatedProperties.put("Location", location);
            }
            // Save data to the local Couchbase Lite DB
            document.putProperties(updatedProperties);
            Log.d(TAG, "Document updated");
        }catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error updating document", e);
        }
        */

    }

    private void outputContents(Database database, String documentId) {
        // retrieve the document from the database
        Document retrievedDocument = database.getDocument(documentId);
        // display the retrieved document
        Log.d(TAG, "retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));
    }

    private void startReplications(Database database) throws CouchbaseLiteException {
        Log.d(TAG, "Start Replications");
        Replication pull = database.createPullReplication(createSyncURL(false));
        Replication push = database.createPushReplication(createSyncURL(false));
        pull.setContinuous(false);
        push.setContinuous(false);
        pull.start();
        push.start();
    }

    private URL createSyncURL(boolean isEncrypted) {
        URL syncURL = null;
        String host = "http://146.179.201.244";
        String port = "4984";
        String dbName = "central_database";
        try {
            syncURL = new URL(host + ":" + port + "/" + dbName);
        } catch (MalformedURLException me) {
            me.printStackTrace();
        }
        return syncURL;
    }
}
