package com.imperial.jack.pbdappstore;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.os.DpaManager;

// Libraries for installing DPA
import android.content.res.AssetManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    public static final String TAG = "PbdAppStore";
    private static final String KEY = "pbdappstore_key123";

    private DpaManager dpamanager;
    private TextView myText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // install the DPA into the system
        installDPA();

        // read DPA from the system
        LinearLayout lView = new LinearLayout(this);

        String dpaKey = "pbdtrial_key123";
        String dpaString = dpamanager.readDpa(KEY, dpaKey);
        myText = new TextView(this);
        myText.setText(dpaString);

        lView.addView(myText);
        setContentView(lView);

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
            dpamanager.installDPA(KEY, dpa_str);
            Log.i(TAG, "DPA installed");
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error in writing DPA!");
        }
    }
}
