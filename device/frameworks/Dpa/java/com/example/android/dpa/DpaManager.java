/* Guanjie Liu (gl1315@imperial.ac.uk)
 * Imperial College London
 *
 * Dpa Framework
 * To be imported by apps to access DPA installation methods
 * 
 * device/sample/frameworks/Dpa/java/com/example/android/dpa/DpaManager.java
 */

package com.example.android.dpa;

import android.util.Log;
import android.content.Context;
import android.app.Application;

//Imports to use the DpaService
import android.os.ServiceManager;
import android.os.IDpaService;


public final class DpaManager {    
    static {
        /*
         * Load the library.  If it's already loaded, this does nothing.
         */
        //System.loadLibrary("platform_library_jni");
    }

    private final String TAG = "DpaManager";

    private final Context mContext; 
    private final String appId;
    private final IDpaService dpa = IDpaService.Stub.asInterface(ServiceManager.getService("dpa"));

    public DpaManager(Context context) {
        //Get and store the application's id
        mContext = context;
        appId = context.getPackageName();
    }


    /* installDPA
     * used by PBD app store to install DPA when an app is downloaded
     * return true if installation succeed or false otherwise;
     */
    public boolean installDPA(String dpa_str){
        boolean result = false;
        
            result = dpa.installDPA(appId, dpa_str);
        
        return result;
    }

    /* readDPA
     * used by central_database to read the DPA of an app
     * return the dpa as string if succeed or null otherwise
     */
    public String readDPA(String dpaAppId){
        String result = dpa.readDPA(appId, dpaAppId);
        return result;
    }

}
