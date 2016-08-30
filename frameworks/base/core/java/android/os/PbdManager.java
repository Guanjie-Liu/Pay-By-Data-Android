/* Karun Matharu (ksm113@imperial.ac.uk)
 * Imperial College London
 *
 * Pbd Location Framework
 * To be imported by apps to access Pbd Service Location methods
 * 
 * device/sample/frameworks/Pbd/java/com/example/android/pbd/PbdLocationManager.java
 */

package android.os;

import android.util.Log;
import android.content.Context;
import android.app.Application;
import android.net.Uri;

/*//Imports to use the PbdService
import android.os.ServiceManager;
import android.os.IPbdService;

//import for the custom PbdLocation;
import android.os.PbdLocation;*/

public final class PbdManager {    
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

    private static final String TAG = "PbdManager";
    private IPbdService pbd;

    public PbdManager(IPbdService service) {
        pbd = service;
    }

    //=================================================================================================
    // Location Manager API
    //=================================================================================================

    /*
     * requestLocationUpdates
     * Method used by apps to request location updates
     * App is notified of new location by a intent broadcast
     * Returns "Invalid Request" if request was illegal
     */
    public String requestLocationUpdates(Context context, long minTime, float minDistance, String provider){
        String listenerId = "serviceError";

        try{
            listenerId = pbd.requestPbdLocationUpdates(minTime, minDistance, context.getPackageName(), provider);
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

    public String requestSingleUpdate(Context context, String provider){
        String listenerId = "serviceError";
        try{
            listenerId = pbd.requestSingleUpdate(context.getPackageName(), provider);
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
    public void removeLocationUpdates(Context context){
           try{
            pbd.removeLocationUpdates(context.getPackageName());
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
    public PbdLocation getPbdLocation(Context context){
        //PbdLocation pLoc = new PbdLocation("noProvider");
        PbdLocation pLoc = null;
        try{
            pLoc = pbd.getPbdLoc(context.getPackageName());
        }
        catch (Exception e){
            Log.d(TAG, "FALLED: to getPbdLocation");
            e.printStackTrace();
        }
        return pLoc;        
    }

    //=================================================================================================
    // Device Manager API
    //=================================================================================================

    /*
     * getDeviceId()
     * Returns the unique device ID, for example, 
     * the IMEI for GSM and the MEID or ESN for CDMA phones.
     * Returns null if unavailable
     * Returns "refused" if authenitcation is not granted
     * Returns "error" if there is an error in making authentication request
     * Returns "serviceError" if there is an error in calling system service method
     */
    public String getDeviceId(Context context){
        String devId = "serviceError";
        try{
            devId = pbd.getDeviceId(context.getPackageName());
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getDeviceId");
            e.printStackTrace(); 
        }
        return devId;
    }

    /*
     * getSimSerialNumber()
     * Returns the serial number of the SIM, if applicable. 
     * Returns null if unavailable
     * Returns "refused" if authenitcation is not granted
     * Returns "error" if there is an error in making authentication request
     * Returns "serviceError" if there is an error in calling system service method
     */
    public String getSimSerialNumber(Context context){
        String simId = "serviceError";
        try{
            simId = pbd.getSimSerialNumber(context.getPackageName());
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getSimSerialNumber");
            e.printStackTrace(); 
        }
        return simId;
    }

    /*
     * getAndroidId()
     * Returns a 64-bit number (as a hex string) that is randomly generated 
     * when the user first sets up the device and should remain constant 
     * for the lifetime of the user's device. The value may change 
     * if a factory reset is performed on the device. 
     * Returns null if unavailable
     * Returns "refused" if authenitcation is not granted
     * Returns "error" if there is an error in making authentication request
     * Returns "serviceError" if there is an error in calling system service method
     */
    public String getAndroidId(Context context){
        String androidId = "serviceError";
        try{
            androidId = pbd.getAndroidId(context.getPackageName());
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getAndroidId");
            e.printStackTrace(); 
        }
        return androidId;
    }

    /*
     * getGroupIdLevel1
     * Returns the Group Identifier Level1 for a GSM phone.
     * Returns null if unavailable
     * Returns "refused" if authenitcation is not granted
     * Returns "error" if there is an error in making authentication request
     * Returns "serviceError" if there is an error in calling system service method 
     */
    public String getGroupIdLevel1(Context context){
        String groupIdLevel1 = "serviceError";
        try{
            groupIdLevel1 = pbd.getGroupIdLevel1(context.getPackageName());
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getGroupIdLevel1()");
            e.printStackTrace(); 
        }
        return groupIdLevel1;
    }

    /*
     * getLine1Number
     * Returns the Line1Number for the device. 
     * Returns null if unavailable
     * Returns "refused" if authenitcation is not granted
     * Returns "error" if there is an error in making authentication request
     * Returns "serviceError" if there is an error in calling system service method
     */
    public String getLine1Number(Context context){
        String line1Number = "serviceError";
        try{
            line1Number = pbd.getLine1Number(context.getPackageName());
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getLine1Number()");
            e.printStackTrace(); 
        }
        return line1Number;
    }

    /*
     * getSubscriberId
     * Returns the Subscriber Id for the device. 
     * Returns null if unavailable
     * Returns "refused" if authenitcation is not granted
     * Returns "error" if there is an error in making authentication request
     * Returns "serviceError" if there is an error in calling system service method
     */
    public String getSubscriberId(Context context){
        String subscriberId = "serviceError";
        try{
            subscriberId = pbd.getSubscriberId(context.getPackageName());
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getSubscriberId()");
            e.printStackTrace(); 
        }
        return subscriberId;
    }

    /*
     * Method: getVoicemailAlphaTag
     * Returns the Voicemail Alpha Tag for the device. 
     * Returns null if unavailable
     * Returns "refused" if authenitcation is not granted
     * Returns "error" if there is an error in making authentication request
     * Returns "serviceError" if there is an error in calling system service method
     */
    public String getVoiceMailAlphaTag(Context context){
        String voicemailAlphaTag = "serviceError";
        try{
            voicemailAlphaTag = pbd.getVoiceMailAlphaTag(context.getPackageName());
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getVoiceMailAlphaTag()");
            e.printStackTrace(); 
        }
        return voicemailAlphaTag;
    }

    /*
     * getVoiceMailNumber
     * Returns the Voicemail Number for the device. 
     * Returns null if unavailable
     * Returns "refused" if authenitcation is not granted
     * Returns "error" if there is an error in making authentication request
     * Returns "serviceError" if there is an error in calling system service method
     */
    public String getVoiceMailNumber(Context context){
        String voiceMailNumber = "serviceError";
        try{
            voiceMailNumber = pbd.getVoiceMailNumber(context.getPackageName());
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getVoiceMailNumber()");
            e.printStackTrace(); 
        }
        return voiceMailNumber;
    }

    //=================================================================================================
    // Contact Manager API
    //=================================================================================================

    /*
     * getContacts()
     * Returns Uri of all contacts to the calling app
     */
    public Uri getContacts(Context context){
        Log.i(TAG, "getContacts");
        Uri allContacts = null;
        try {
            allContacts = pbd.getContacts(context.getPackageName());
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getContacts");
            e.printStackTrace();            
        }
        return allContacts;
    }

    /*
    * getRowId
    * Returns a Uri of the Row Id column 
    * from the Contacts Provider
    */
    public String getRowId(Context context){
        Log.i(TAG, "getRowId");
        String rowId = null;
        try {
            rowId = pbd.getRowId(context.getPackageName());
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getRowId");
            e.printStackTrace();            
        }
        return rowId;        
    }

    /*
    * getDisplayName
    * Returns a Uri of the Display Name column
    * from the Contacts Provider
    */
    public String getDisplayName(Context context){
        Log.i(TAG, "getDisplayName");
        String displayName = null;
        try {
            displayName = pbd.getDisplayName(context.getPackageName());
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getDisplayName");
            e.printStackTrace();            
        }
        return displayName; 
    }

    /*
    * getHasPhoneNumber
    * Returns a Uri of ''Has Phone Number' colum
    * from the Contacts Provider
    */
    public String getHasPhoneNumber(Context context){
        Log.i(TAG, "getHasPhoneNumber");
        String hasPhoneNumber = null;
        try {
            hasPhoneNumber = pbd.getHasPhoneNumber(context.getPackageName());
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getHasPhoneNumber");
            e.printStackTrace();            
        }
        return hasPhoneNumber; 
    }


    /*
    * getCdkPhoneContentUri
    * Returns CommonDataKinds Phone Content Uri
    */
    public Uri getCdkPhoneContentUri(Context context){
        Log.i(TAG, "getCdkPhoneNumber");
        Uri phoneContentUri = null;
        try {
            phoneContentUri = pbd.getCdkPhoneContentUri(context.getPackageName());
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getCdkPhoneContentUri");
            e.printStackTrace();            
        }
        return phoneContentUri; 
    }


    /*
    * getCdkPhoneContactId
    * Returns String of CommonDataKinds Phone Contact Id
    */
    public String getCdkPhoneContactId (Context context){
        Log.i(TAG, "getCdkPhoneContactId");
        String phoneContactId = null;
        try {
            phoneContactId = pbd.getCdkPhoneContactId(context.getPackageName());
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getCdkPhoneContactId");
            e.printStackTrace();            
        }
        return phoneContactId; 
    }


    /*
    * getHasPhoneNumber
    * Returns String of CommonDataKinds Phone Number
    */
    public String getCdkPhoneNumber (Context context){
        Log.i(TAG, "getCdkPhoneNumber");
        String phoneNumber = null;
        try {
            phoneNumber = pbd.getCdkPhoneNumber(context.getPackageName());
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getCdkPhoneNumber");
            e.printStackTrace();            
        }
        return phoneNumber; 
    }


    /*
    * pbdOnStop to be called when the app calls onStop()
    * invodes Pbd Service's onStop to unregister Listener
    */
    public void pbdOnStop(Context context){
        Log.d(TAG, "Called onStop");
        try{
            pbd.onStop(context.getPackageName());
        }
        catch (Exception e){
            Log.d(TAG, "FALLED: to call onStop");
            e.printStackTrace();
        }
    }
}
