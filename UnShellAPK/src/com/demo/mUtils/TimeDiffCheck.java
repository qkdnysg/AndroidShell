package com.demo.mUtils;

import android.util.Log;

public class TimeDiffCheck {
	
	private static final String TAG = "TimeDiffCheck";
	//	0×1
	//	检测"/dev/socket/qemud", "/dev/qemu_pipe"这两个通道（模拟器专有）
	private int mCount;
	private long mTime;
	public TimeDiffCheck(){
		this.mCount = 1;
		this.mTime = 0;
	}
	
	public void TimeCheck(){
		if(1 == mCount){
			mTime = System.currentTimeMillis();
			mCount++;
		}else{
			mTime = System.currentTimeMillis() - mTime;
			Log.i(TAG, "与上次统计时刻间隔了"+mTime+"ms");
			mCount--;
		}
	  
	}
	

}
