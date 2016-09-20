/*
* aidl file : frameworks/base/core/java/android/os/IDpaService.aidl
* This file contains definitions of functions which are exposed by the DPA Service
*/
package android.os;

interface IDpaService {
/**
* {@hide}
*/
	boolean installDPA(String key, String dpa_str);
	String readDpaList(String key);
	String readDpa(String key, String dpaKey);
}
