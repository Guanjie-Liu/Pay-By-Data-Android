/* Guanjie Liu (gl1315@imperial.ac.uk)
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

/**
 * This class provides access to the system PBD services.  These
 * services allow applications to obtain periodic updates of the
 * device's geographical location, or to obtain the identifiers and
 * contacts information.
 *
 * <p>You do not
 * instantiate this class directly; instead, retrieve it through
 * {android.content.Context#getSystemService
 * Context.getSystemService(Context.PBD_SERVICE)}.
 *
 * All sensitive information access requires the system to have
 * the corresponding DPA installed. Each method for accessing
 * sensitive information is also required to pass in a app identity key.
 */
public final class PbdManager {    
    static {
        /*
         * Load the library.  If it's already loaded, this does nothing.
         */
        //System.loadLibrary("platform_library_jni");
    }

    /**
     * FINE_LOCATION
     * Constant string used by app when requesting 
     * updates from the GPS Location Provider
     */
    public static final String FINE_LOCATION = "finelocation";

    /**
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

    /**
     * Method used by apps to request location updates.
     * App is notified of new location by a intent broadcast.
     * Returns "Invalid Request" if request was illegal.
     */
    public String requestLocationUpdates(String key, long minTime, float minDistance, String provider){
        String listenerId = "serviceError";

        try{
            listenerId = pbd.requestPbdLocationUpdates(minTime, minDistance, key, provider);
        }
        catch (Exception e){
            Log.e(TAG, "FAILED to call requestLocationUpdates");
            e.printStackTrace(); 
        }
        return listenerId;
    }

    /**
     * Request a single location update from the Listener.
     * Returns "Invalid Request" if request was illegal.
     */

    public String requestSingleUpdate(String key, String provider){
        String listenerId = "serviceError";
        try{
            listenerId = pbd.requestSingleUpdate(key, provider);
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call requestSingleUpdate");
            e.printStackTrace(); 
        }
        return listenerId;
    }

    /**
    * Removes location updates if the app has a registered.
    * Listener. Does not unregister the listener.
    */
    public void removeLocationUpdates(String key){
           try{
            pbd.removeLocationUpdates(key);
        }
        catch (Exception e){
            Log.d(TAG, "FALLED: to call removeLocationUPdates");
            e.printStackTrace();
        }
    }

    /**
    * Gets the most recent PbdLocation.
    * Called by app when it receives broadcast intent
    * to fetch the location received by the listener.
    */
    public PbdLocation getPbdLocation(String key){
        //PbdLocation pLoc = new PbdLocation("noProvider");
        PbdLocation pLoc = null;
        try{
            pLoc = pbd.getPbdLoc(key);
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

    /**
     * Returns the unique device ID, for example, 
     * the IMEI for GSM and the MEID or ESN for CDMA phones.
     * Returns null if unavailable.
     * Returns "refused" if authenitcation is not granted.
     * Returns "error" if there is an error in making authentication request.
     */
    public String getDeviceId(String key){
        String devId = "serviceError";
        try{
            devId = pbd.getDeviceId(key);
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getDeviceId");
            e.printStackTrace(); 
        }
        return devId;
    }

    /**
     * Returns the serial number of the SIM, if applicable. 
     * Returns null if unavailable.
     * Returns "refused" if authenitcation is not granted.
     * Returns "error" if there is an error in making authentication request.
     */
    public String getSimSerialNumber(String key){
        String simId = "serviceError";
        try{
            simId = pbd.getSimSerialNumber(key);
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getSimSerialNumber");
            e.printStackTrace(); 
        }
        return simId;
    }

    /**
     * Returns a 64-bit number (as a hex string) that is randomly generated 
     * when the user first sets up the device and should remain constant 
     * for the lifetime of the user's device. The value may change 
     * if a factory reset is performed on the device. 
     *
     * Returns null if unavailable.
     * Returns "refused" if authenitcation is not granted.
     * Returns "error" if there is an error in making authentication request.
     */
    public String getAndroidId(String key){
        String androidId = "serviceError";
        try{
            androidId = pbd.getAndroidId(key);
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getAndroidId");
            e.printStackTrace(); 
        }
        return androidId;
    }

    /**
     * Returns the Group Identifier Level1 for a GSM phone.
     * Returns null if unavailable.
     * Returns "refused" if authenitcation is not granted.
     * Returns "error" if there is an error in making authentication request.
     */
    public String getGroupIdLevel1(String key){
        String groupIdLevel1 = "serviceError";
        try{
            groupIdLevel1 = pbd.getGroupIdLevel1(key);
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getGroupIdLevel1()");
            e.printStackTrace(); 
        }
        return groupIdLevel1;
    }

    /**
     * Returns the Line1Number for the device. 
     * Returns null if unavailable.
     * Returns "refused" if authenitcation is not granted.
     * Returns "error" if there is an error in making authentication request.
     */
    public String getLine1Number(String key){
        String line1Number = "serviceError";
        try{
            line1Number = pbd.getLine1Number(key);
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getLine1Number()");
            e.printStackTrace(); 
        }
        return line1Number;
    }

    /**
     * Returns the Subscriber Id for the device. 
     * Returns null if unavailable.
     * Returns "refused" if authenitcation is not granted.
     * Returns "error" if there is an error in making authentication request.
     */
    public String getSubscriberId(String key){
        String subscriberId = "serviceError";
        try{
            subscriberId = pbd.getSubscriberId(key);
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getSubscriberId()");
            e.printStackTrace(); 
        }
        return subscriberId;
    }

    /**
     * Returns the Voicemail Alpha Tag for the device. 
     * Returns null if unavailable.
     * Returns "refused" if authenitcation is not granted.
     * Returns "error" if there is an error in making authentication request.
     */
    public String getVoiceMailAlphaTag(String key){
        String voicemailAlphaTag = "serviceError";
        try{
            voicemailAlphaTag = pbd.getVoiceMailAlphaTag(key);
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getVoiceMailAlphaTag()");
            e.printStackTrace(); 
        }
        return voicemailAlphaTag;
    }

    /**
     * Returns the Voicemail Number for the device. 
     * Returns null if unavailable.
     * Returns "refused" if authenitcation is not granted.
     * Returns "error" if there is an error in making authentication request.
     */
    public String getVoiceMailNumber(String key){
        String voiceMailNumber = "serviceError";
        try{
            voiceMailNumber = pbd.getVoiceMailNumber(key);
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

    /**
     * Returns Uri of all contacts to the calling app.
     */
    public Uri getContacts(String key){
        Log.i(TAG, "getContacts");
        Uri allContacts = null;
        try {
            allContacts = pbd.getContacts(key);
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getContacts");
            e.printStackTrace();            
        }
        return allContacts;
    }

    /**
    * Returns a Uri of the Row Id column 
    * from the Contacts Provider.
    */
    public String getRowId(String key){
        Log.i(TAG, "getRowId");
        String rowId = null;
        try {
            rowId = pbd.getRowId(key);
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getRowId");
            e.printStackTrace();            
        }
        return rowId;        
    }

    /**
    * Returns a Uri of the Display Name column
    * from the Contacts Provider.
    */
    public String getDisplayName(String key){
        Log.i(TAG, "getDisplayName");
        String displayName = null;
        try {
            displayName = pbd.getDisplayName(key);
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getDisplayName");
            e.printStackTrace();            
        }
        return displayName; 
    }

    /**
    * Returns a Uri of ''Has Phone Number' colum
    * from the Contacts Provider.
    */
    public String getHasPhoneNumber(String key){
        Log.i(TAG, "getHasPhoneNumber");
        String hasPhoneNumber = null;
        try {
            hasPhoneNumber = pbd.getHasPhoneNumber(key);
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getHasPhoneNumber");
            e.printStackTrace();            
        }
        return hasPhoneNumber; 
    }


    /**
    * Returns CommonDataKinds Phone Content Uri.
    */
    public Uri getCdkPhoneContentUri(String key){
        Log.i(TAG, "getCdkPhoneNumber");
        Uri phoneContentUri = null;
        try {
            phoneContentUri = pbd.getCdkPhoneContentUri(key);
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getCdkPhoneContentUri");
            e.printStackTrace();            
        }
        return phoneContentUri; 
    }


    /**
    * Returns String of CommonDataKinds Phone Contact Id.
    */
    public String getCdkPhoneContactId (String key){
        Log.i(TAG, "getCdkPhoneContactId");
        String phoneContactId = null;
        try {
            phoneContactId = pbd.getCdkPhoneContactId(key);
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getCdkPhoneContactId");
            e.printStackTrace();            
        }
        return phoneContactId; 
    }


    /**
    * Returns String of CommonDataKinds Phone Number.
    */
    public String getCdkPhoneNumber (String key){
        Log.i(TAG, "getCdkPhoneNumber");
        String phoneNumber = null;
        try {
            phoneNumber = pbd.getCdkPhoneNumber(key);
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getCdkPhoneNumber");
            e.printStackTrace();            
        }
        return phoneNumber; 
    }

    /**
    * pbdOnStop to be called when the app calls onStop()
    * invodes Pbd Service's onStop to unregister Listener.
    */
    public void pbdOnStop(String key){
        Log.d(TAG, "Called onStop");
        try{
            pbd.onStop(key);
        }
        catch (Exception e){
            Log.d(TAG, "FALLED: to call onStop");
            e.printStackTrace();
        }
    }
}
