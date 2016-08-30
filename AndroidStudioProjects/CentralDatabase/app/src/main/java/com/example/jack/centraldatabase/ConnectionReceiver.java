package com.example.jack.centraldatabase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.StringBuilderPrinter;

public class ConnectionReceiver extends BroadcastReceiver {
    public static final String TAG = "ConnectionReceiver";

    public ConnectionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received an intent");
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            Intent i = new Intent(context, SyncService.class);
            context.startService(i);
        }
    }
}
