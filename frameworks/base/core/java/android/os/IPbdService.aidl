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

	void onStop(String key);

	String getString();

	String getDeviceId(String key);

	String getSimSerialNumber(String key);

	String getAndroidId(String key);

	String getGroupIdLevel1(String key);

	String getLine1Number(String key);

	String getSubscriberId(String key);

	String getVoiceMailAlphaTag(String key);

	String getVoiceMailNumber(String key);

	PbdLocation getPbdLoc(String key);

	String requestPbdLocationUpdates(long minTime, float minDistance, String key, String provider);

	String requestSingleUpdate(String key, String provider);

	Uri getContacts(String key);

	String getRowId(String key);

	String getDisplayName(String key);

	String getHasPhoneNumber(String key);

	Uri getCdkPhoneContentUri(String key);

	String getCdkPhoneContactId (String key);

	String getCdkPhoneNumber (String key);

	void removeLocationUpdates (String key);
}
