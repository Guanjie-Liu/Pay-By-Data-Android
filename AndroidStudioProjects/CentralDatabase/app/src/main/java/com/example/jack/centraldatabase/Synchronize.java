package com.example.jack.centraldatabase;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;

import java.net.MalformedURLException;
import java.net.URL;

public class Synchronize extends IntentService {
    public static final String TAG = "Synchronize";
    public static final String DB_NAME = "central_database";

    public Synchronize() {
        super("Synchronize");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "Synchronize onHandleIntent started");

            Manager manager = null;
            Database database = null;

            try {
                manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
                database = manager.getDatabase(DB_NAME);
            } catch (Exception e) {
                Log.e(TAG, "Error getting database", e);
            }

            try {
                startReplications(database);
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Error on starting Replications", e);
            }
        }
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
