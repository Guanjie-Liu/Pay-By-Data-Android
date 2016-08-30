package com.example.hello2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.appcompat.*;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.android.demo.DemoManager;

public class MainActivity extends Activity {
	private DemoManager dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// IDemoService ds = IDemoService.Stub.asInterface(ServiceManager.getService("demo"));
		
		try{
			dm = new DemoManager();
		}
		catch(Exception e){
			Log.e("DemoTest", "FAILED to initizlize PbdLocationManager");
			e.printStackTrace();
		}
		

		//dm = new DemoManager();

		TextView txt = (TextView) findViewById(R.id.textview1);
		txt.setText(dm.set_Data(1234));
	}
}
