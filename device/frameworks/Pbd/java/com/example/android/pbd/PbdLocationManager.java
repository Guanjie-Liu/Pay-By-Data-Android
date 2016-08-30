/* Karun Matharu (ksm113@imperial.ac.uk)
 * Imperial College London
 *
 * Pbd Location Framework
 * To be imported by apps to access Pbd Service Location methods
 * 
 * device/sample/frameworks/Pbd/java/com/example/android/pbd/PbdLocationManager.java
 */

package com.example.android.pbd;

import android.util.Log;
import android.content.Context;
import android.app.Application;

//Imports to use the PbdService
import android.os.ServiceManager;
import android.os.IPbdService;


//import for the custom PbdLocation;
import android.os.PbdLocation;


public final class PbdLocationManager {    
    static {
        /*
         * Load the library.  If it's already loaded, this does nothing.
         */
        //System.loadLibrary("platform_library_jni");
    }

    /*
     * FINE_LOCATION
     * Constant string used by app when requesting 
     * updates from the GPS Location Provider
     */
    public static final String FINE_LOCATION = "finelocation";

    /*
     * COARSE_LOCATION
     * Constant string used by app when requesting 
     * updates from the Network Location Provider
     */
    public static final String COARSE_LOCATION = "coarselocation";

    private final Context mContext; 
    private final IPbdService pbd = IPbdService.Stub.asInterface(ServiceManager.getService("pbd"));
    private final String TAG = "PbdLocationManager";
    private final String appId;

    public PbdLocationManager(Context context) {
        //Get and store the application's id
        mContext = context;
        appId = context.getPackageName();
    }


    /*
     * requestLocationUpdates
     * Method used by apps to request location updates
     * App is notified of new location by a intent broadcast
     * Returns "Invalid Request" if request was illegal
     */
    public String requestLocationUpdates(long minTime, float minDistance, String provider){
        String listenerId = "serviceError";

        try{
            listenerId = pbd.requestPbdLocationUpdates(minTime, minDistance, appId, provider);
        }
        catch (Exception e){
            Log.e(TAG, "FAILED to call requestLocationUpdates");
            e.printStackTrace(); 
        }
        return listenerId;
    }

    /*
     * requestSingleUpdate
     * Request a single location update from the Listener
     * Returns "Invalid Request" if request was illegal
     */

    public String requestSingleUpdate(String provider){
        String listenerId = "serviceError";
        try{
            listenerId = pbd.requestSingleUpdate(appId, provider);
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call requestSingleUpdate");
            e.printStackTrace(); 
        }
        return listenerId;
    }


    /*
    * Removes location updates if the app has a registered
    * Listener. Does not unregister the listener.
    */
    public void removeLocationUpdates(){
           try{
            pbd.removeLocationUpdates(appId);
        }
        catch (Exception e){
            Log.d(TAG, "FALLED: to call removeLocationUPdates");
            e.printStackTrace();
        }
    }

    /*
    * Gets the most recent PbdLocation
    * Called by app when it receives broadcast intent
    * to fetch the location received by the listener
    */
    public PbdLocation getPbdLocation(){
        //PbdLocation pLoc = new PbdLocation("noProvider");
        PbdLocation pLoc = null;
        try{
            pLoc = pbd.getPbdLoc(appId);
        }
        catch (Exception e){
            Log.d(TAG, "FALLED: to getPbdLocation");
            e.printStackTrace();
        }
        return pLoc;        
    }


    /*
    * pbdOnStop to be called when the app calls onStop()
    * invodes Pbd Service's onStop to unregister Listener
    */
    public void pbdOnStop(){
        Log.d(TAG, "Called onStop");
        try{
            pbd.onStop(appId);
        }
        catch (Exception e){
            Log.d(TAG, "FALLED: to call onStop");
            e.printStackTrace();
        }
    }

}
