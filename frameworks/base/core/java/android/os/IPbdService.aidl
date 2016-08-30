/*
* aidl file : frameworks/base/core/java/android/os/IPbdService.aidl
* This file contains definitions of functions which are exposed by the Pay-by-Data Service
*/
package android.os;

import android.location.Location;

import android.os.PbdLocation;
import android.net.Uri;

interface IPbdService {
/**
* {@hide}
*/
	//void setValue(int val);

	void onStop(String appId);

	String getString();

	String getDeviceId(String appId);

	String getSimSerialNumber(String appId);

	String getAndroidId(String appId);

	String getGroupIdLevel1(String appId);

	String getLine1Number(String appId);

	String getSubscriberId(String appId);

	String getVoiceMailAlphaTag(String appId);

	String getVoiceMailNumber(String appId);

	//Location getLoc();

	PbdLocation getPbdLoc(String appId);

	String requestPbdLocationUpdates(long minTime, float minDistance, String appId, String provider);

	String requestSingleUpdate(String appId, String provider);

	Uri getContacts(String appId);

	String getRowId(String appId);

	String getDisplayName(String appId);

	String getHasPhoneNumber(String appId);

	Uri getCdkPhoneContentUri(String appId);

	String getCdkPhoneContactId (String appId);

	String getCdkPhoneNumber (String appId);

	void removeLocationUpdates (String appId);

/*	void setIP (String ip);

	String getIP ();

	void clearTokens();
	*/
}
