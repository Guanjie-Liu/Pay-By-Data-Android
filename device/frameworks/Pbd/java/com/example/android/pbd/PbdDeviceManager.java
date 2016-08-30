/* Karun Matharu (ksm113@imperial.ac.uk)
 * Imperial College London
 *
 * Pbd Device Framework
 * To be imported by apps to access Pbd Service Device methods
 * 
 * device/sample/frameworks/Pbd/java/com/example/android/pbd/PbdDeviceManager.java
 */

package com.example.android.pbd;

import android.util.Log;
import android.content.Context;

//Imports to use the PbdService
import android.os.ServiceManager;
import android.os.IPbdService;


public final class PbdDeviceManager {    
    static {
        /*
         * Load the library.  If it's already loaded, this does nothing.
         */
        //System.loadLibrary("platform_library_jni");
    }

    private final Context mContext; 
    private final IPbdService pbd = IPbdService.Stub.asInterface(ServiceManager.getService("pbd"));
    private final String TAG = "PbdDeviceManager";
    private final String appId;

    public PbdDeviceManager(Context context) {

        //Link with the running Pbd Service 
        //pbd = IPbdService.Stub.asInterface(ServiceManager.getService("pbd"));

        //Get and store the application's id
        mContext = context;
        appId = context.getPackageName();
    }

    /*
     * getDeviceId()
     * Returns the unique device ID, for example, 
     * the IMEI for GSM and the MEID or ESN for CDMA phones.
     * Returns null if unavailable
     * Returns "refused" if authenitcation is not granted
     * Returns "error" if there is an error in making authentication request
     * Returns "serviceError" if there is an error in calling system service method
     */
    public String getDeviceId(){
        String devId = "serviceError";
        try{
            devId = pbd.getDeviceId(appId);
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
    public String getSimSerialNumber(){
        String simId = "serviceError";
        try{
            simId = pbd.getSimSerialNumber(appId);
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
    public String getAndroidId(){
        String androidId = "serviceError";
        try{
            androidId = pbd.getAndroidId(appId);
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
    public String getGroupIdLevel1(){
        String groupIdLevel1 = "serviceError";
        try{
            groupIdLevel1 = pbd.getGroupIdLevel1(appId);
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
    public String getLine1Number(){
        String line1Number = "serviceError";
        try{
            line1Number = pbd.getLine1Number(appId);
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
    public String getSubscriberId(){
        String subscriberId = "serviceError";
        try{
            subscriberId = pbd.getSubscriberId(appId);
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
    public String getVoiceMailAlphaTag(){
        String voicemailAlphaTag = "serviceError";
        try{
            voicemailAlphaTag = pbd.getVoiceMailAlphaTag(appId);
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
    public String getVoiceMailNumber(){
        String voiceMailNumber = "serviceError";
        try{
            voiceMailNumber = pbd.getVoiceMailNumber(appId);
        }
        catch (Exception e){
            Log.d(TAG, "FAILED to call getVoiceMailNumber()");
            e.printStackTrace(); 
        }
        return voiceMailNumber;
    }

    /*
     * onStop
     * To be called when the Calling app's activity calls onStop() 
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
