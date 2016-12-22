package com.demo.mUtils;

import android.util.Log;

public class TimeDiffCheck {
	
	private static final String TAG = "TimeDiffCheck";

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
			
			if(mTime > 1000){
				Log.i(TAG, "时间差异常，程序退出...");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//android.os.Process.killProcess(android.os.Process.myPid());
			}
		}
	  
	}
	

}
