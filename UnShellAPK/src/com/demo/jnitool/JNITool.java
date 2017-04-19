package com.demo.jnitool;

import android.content.Context;

public class JNITool {
	static {
		System.loadLibrary("shell");
	}
	public static native byte[] decrypt(String key, byte[] srcdata);
	public static native byte[] unShell(byte[] srcdata);
	public static native String checkey(String key);
	public static native boolean isEmulator(Context mContext);
	public static native boolean removeOdex(String odexPath);
	
	//javah -classpath bin/classes -d jni com.demo.jnitool.JNITool
	//javah -classpath bin/classes -d jni com.ggndktest1.JniGg

}
