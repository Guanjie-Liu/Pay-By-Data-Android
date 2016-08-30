package com.example.jack.centraldatabase;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            Log.d(TAG, "Manager created");
            database = manager.getDatabase(DB_NAME);
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

                updateDoc(database, appId, location);
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
                startReplications(database);
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Error on starting Replications", e);
            }
        }
    }

    private void updateDoc(Database database, String documentId, Location location) {
        Document document = database.getDocument(documentId);
        try {
            // Update the document with more data
            Map<String, Object> updatedProperties = new HashMap<String, Object>();
            if(document.getProperties() != null) {
                updatedProperties.putAll(document.getProperties());
            }
            updatedProperties.put("Latitude", location.getLatitude());
            updatedProperties.put("Longitude", location.getLongitude());
            // Save to the Couchbase local Couchbase Lite DB
            document.putProperties(updatedProperties);
            Log.d(TAG, "Document updated");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
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
        String host = "http://129.31.182.225";
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
