package com.demo.jnitool;

public class JNITool {
	static {
		System.loadLibrary("shell");
	}
	public static native byte[] decrypt(String key, byte[] srcdata);
	public static native byte[] unShell(byte[] srcdata);
	public static native String checkey(String key);
	
	//javah -classpath bin/classes -d jni com.demo.jnitool.JNITool
	//javah -classpath bin/classes -d jni com.ggndktest1.JniGg

}
