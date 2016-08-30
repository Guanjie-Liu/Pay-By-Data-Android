/* Guanjie Liu (gl1315@imperial.ac.uk)
 * Imperial College London
 *
 * Dpa Framework
 * To be imported by apps to access DPA installation methods
 * 
 * device/sample/frameworks/Dpa/java/com/example/android/dpa/DpaManager.java
 */

package android.os;

import android.util.Log;
import android.content.Context;
import android.app.Application;

/*//Imports to use the DpaService
import android.os.ServiceManager;
import android.os.IDpaService;*/


public final class DpaManager {    
    static {
        /*
         * Load the library.  If it's already loaded, this does nothing.
         */
        //System.loadLibrary("platform_library_jni");
    }

    private static final String TAG = "DpaManager";
    private IDpaService dpa;

    public DpaManager(IDpaService service) {
        dpa = service;
    }

    /* installDPA
     * used by PBD app store to install DPA when an app is downloaded
     * return true if installation succeed or false otherwise;
     */
    public boolean installDPA(Context context, String dpa_str){
        boolean result = false;

        try{
            result = dpa.installDPA(context.getPackageName(), dpa_str);
        }catch(Exception e){
            Log.d(TAG, "FAILED to install DPA");
            e.printStackTrace(); 
        }
        
        return result;
    }

    /* readDPA
     * used by central_database to read the DPA of an app
     * return the dpa as string if succeed or null otherwise
     */
    public String readDPA(Context context, String dpaAppId){
        String result = "Nothing";

        try{
            result = dpa.readDPA(context.getPackageName(), dpaAppId);
        }catch(Exception e){
            Log.d(TAG, "FAILED to read DPA");
            e.printStackTrace(); 
        }

        return result;
    }

}
