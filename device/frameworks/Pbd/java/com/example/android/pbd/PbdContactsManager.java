/* Karun Matharu (ksm113@imperial.ac.uk)
 * Imperial College London
 *
 * Pbd Contacts Framework
 * To be imported by apps to access Pbd Service Contacts methods
 * 
 * device/sample/frameworks/Pbd/java/com/example/android/pbd/PbdContactsManager.java
 */

package com.example.android.pbd;

import android.util.Log;
import android.content.Context;

//Imports to use the PbdService
import android.os.ServiceManager;
import android.os.IPbdService;

//Import specific to the PbdContactsManager
import android.net.Uri;


public final class PbdContactsManager {    
    static {
        /*
         * Load the library.  If it's already loaded, this does nothing.
         */
        //System.loadLibrary("platform_library_jni");
    }

    private final Context mContext; 
    private final IPbdService pbd = IPbdService.Stub.asInterface(ServiceManager.getService("pbd"));
    private final String TAG = "PbdContactManager";
    private final String appId;

    public PbdContactsManager(Context context) {

        //Get and store the application's id
        mContext = context;
        appId = context.getPackageName();
    }


    /*
     * getContacts()
     * Returns Uri of all contacts to the calling app
     */
    public Uri getContacts(){
        Log.i(TAG, "getContacts");
        Uri allContacts = null;
        try {
            allContacts = pbd.getContacts(appId);
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
    public String getRowId(){
        Log.i(TAG, "getRowId");
        String rowId = null;
        try {
            rowId = pbd.getRowId(appId);
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
    public String getDisplayName(){
        Log.i(TAG, "getDisplayName");
        String displayName = null;
        try {
            displayName = pbd.getDisplayName(appId);
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
    public String getHasPhoneNumber(){
        Log.i(TAG, "getHasPhoneNumber");
        String hasPhoneNumber = null;
        try {
            hasPhoneNumber = pbd.getHasPhoneNumber(appId);
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
    public Uri getCdkPhoneContentUri(){
        Log.i(TAG, "getCdkPhoneNumber");
        Uri phoneContentUri = null;
        try {
            phoneContentUri = pbd.getCdkPhoneContentUri(appId);
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
    public String getCdkPhoneContactId (){
        Log.i(TAG, "getCdkPhoneContactId");
        String phoneContactId = null;
        try {
            phoneContactId = pbd.getCdkPhoneContactId(appId);
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
    public String getCdkPhoneNumber (){
        Log.i(TAG, "getCdkPhoneNumber");
        String phoneNumber = null;
        try {
            phoneNumber = pbd.getCdkPhoneNumber(appId);
        }
        catch (Exception e){
            Log.i(TAG, "FAILED: to call getCdkPhoneNumber");
            e.printStackTrace();            
        }
        return phoneNumber; 
    }

}
