package com.example.jack.newcentraldatabase;

import android.content.Context;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

public class MainActivity extends AppCompatActivity {
    public static final String DB_NAME = "central_database";
    public static final String TAG = "DataUpdate";

    private static final String ACTION_FINE_LOC = "action_fine_loc";
    private static final String ACTION_COARSE_LOC = "action_coarse_loc";

    LocationManager locMgr;
    Manager manager = null;
    Database database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*try {
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
            database.delete();
            Log.d(TAG, "database is deleted");
        } catch (Exception e) {
            Log.e(TAG, "Error getting database", e);
        }
*/
    }
}
