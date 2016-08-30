package android.os;

import android.os.IDemoService;
import android.util.Log;

public class DemoManager{
	private IDemoService mservice;
	private static final String TAG = "DemoManager";

	public DemoManager(IDemoService service){
		mservice = service;
	}

	public String set_Data(int val){
		String str = "Only Initialized from Manager!";
		
        try{
            str = mservice.setData(val);
        }
        catch (Exception e){
            Log.e(TAG, "FALLED: to setData");
            e.printStackTrace();
        }
        
		return str;
	}
}
