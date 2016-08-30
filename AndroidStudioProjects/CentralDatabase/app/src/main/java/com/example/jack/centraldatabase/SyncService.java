package com.example.jack.centraldatabase;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.router.ByteBuffer;

import java.net.MalformedURLException;
import java.net.URL;

public class SyncService extends Service {
    public static final String TAG = "SyncService";
    public static final String DB_NAME = "central_database";

    public SyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "SyncService onCreate");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"SyncService onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SyncService onStartCommand");

        NetworkInfo activeNetwork;
        boolean isConnected;
        ConnectivityManager cm=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        do{
            Log.d(TAG, "SyncService in while loop");

            Intent syncIntent = new Intent(getApplicationContext(), Synchronize.class);
            startService(syncIntent);
            try {
                Thread.currentThread().sleep(60000);
            }catch(Exception e){}

            activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

        }while(isConnected == true);


        return START_NOT_STICKY;
    }
}
