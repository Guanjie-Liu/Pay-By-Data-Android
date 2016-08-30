package com.example.android.demo;

import android.os.IDemoService;
import android.os.ServiceManager;
import android.util.Log;

public class DemoManager{
	private IDemoService service;
	private static final String TAG = "DemoManager";

	public DemoManager(){
		try{
			service = IDemoService.Stub.asInterface(ServiceManager.getService("demo"));
		}
		catch(Exception e){
			Log.e(TAG, "Failed to bind service");
			e.printStackTrace();
		}
	}


	public String set_Data(int val){
		String str = "Only Initialized from Manager!";
		
        try{
            str = service.setData(val);
        }
        catch (Exception e){
            Log.e(TAG, "FALLED: to setData");
            e.printStackTrace();
        }
        
		return str;
	}
}
