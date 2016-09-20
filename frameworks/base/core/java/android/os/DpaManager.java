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
    public boolean installDPA(String key, String dpa_str){
        boolean result = false;

        try{
            result = dpa.installDPA(key, dpa_str);
        }catch(Exception e){
            Log.d(TAG, "FAILED to install DPA");
            e.printStackTrace(); 
        }
        
        return result;
    }

    /* readDpaList
     * used by central_database and PbdAppStore to read the DPA List in the system
     * return the list as string if succeed or "FAILED" otherwise
     */
    public String readDpaList(String key){
        String result = "FAILED";

        try{
            result = dpa.readDpaList(key);
        }catch(Exception e){
            Log.d(TAG, "FAILED to read DPA List");
            e.printStackTrace();
        }

        return result;
    }

    /* readDpa
     * used by central_database and PbdAppStore to read the DPA of an app
     * return the dpa as string if succeed or "FAILED" otherwise
     */
    public String readDpa(String key, String dpaKey){
        String result = "FAILED";

        try{
            result = dpa.readDpa(key, dpaKey);
        }catch(Exception e){
            Log.d(TAG, "FAILED to read DPA");
            e.printStackTrace(); 
        }

        return result;
    }
}
