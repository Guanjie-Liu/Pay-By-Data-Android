package com.example.jack.newcentraldatabase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateReceiver extends BroadcastReceiver {
    public static final String TAG = "Database Receiver";

    public UpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received an intent");
        Intent serviceIntent = new Intent(context, DataUpdate.class);
        serviceIntent.setAction(intent.getExtras().getString("updateType"));
        context.startService(serviceIntent);
    }
}
