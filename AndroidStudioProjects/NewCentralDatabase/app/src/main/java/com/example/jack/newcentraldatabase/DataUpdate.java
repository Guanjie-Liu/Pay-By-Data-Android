package com.example.jack.newcentraldatabase;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.Date;
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

            if (ACTION_FINE_LOC.equals(action)) {
                Location location = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateDoc(database, location);
            }
            else if (ACTION_COARSE_LOC.equals(action)) {
                Location location = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                updateDoc(database, location);
            }
            else{
                Log.d(TAG, "No action handler for " + action);
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
            Date ttl = new Date(System.currentTimeMillis() + 86400000);
            Document document = database.createDocument();
            document.putProperties(properties);
            document.setExpirationDate(ttl);
        }catch (Exception e){
            Log.e(TAG, "Error updating document", e);
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
        String host = "http://129.31.179.34";
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
