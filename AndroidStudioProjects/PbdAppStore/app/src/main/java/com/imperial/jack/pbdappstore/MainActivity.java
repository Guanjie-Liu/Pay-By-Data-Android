package com.imperial.jack.pbdappstore;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.os.DpaManager;

// Libraries for installing DPA
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    public static final String TAG = "PbdAppStore";

    private DpaManager dpamanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Context context = createPackageContext("com.example.jack.pbdtrial", CONTEXT_IGNORE_SECURITY);
            Log.d(TAG, "we created a app context: "+context.getPackageName());
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "error in creating packageContext");
        }
//        installDPA();
    }

    // Method used by this app to install its DPA
    private void installDPA(){
        dpamanager = (DpaManager) getSystemService(Context.DPA_SERVICE);

        String dpa_str = null;
        try{
            AssetManager am = getApplicationContext().getAssets();
            InputStream fin = am.open("com.example.jack.pbdtrial.DPA.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                sb.append(line).append("\n");
            }
            reader.close();
            fin.close();
            dpa_str = sb.toString();
            Log.d(TAG, dpa_str);
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error in reading dpa file");
        }

        try{
            String appId = getApplicationContext().getPackageName();
            dpamanager.installDPA(this, dpa_str);
            Log.i(TAG, "My package name:"+appId);
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error in writing DPA!");
        }
    }
}
