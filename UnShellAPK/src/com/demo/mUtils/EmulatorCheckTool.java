package com.demo.mUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;


public class EmulatorCheckTool {
	
	//	0×1
	//	检测"/dev/socket/qemud", "/dev/qemu_pipe"这两个通道（模拟器专有）
	private static String[] known_pipes =
	    {
	    "/dev/socket/qemud",
	    "/dev/qemu_pipe"
	    };
	public static Boolean checkPipes(){
	    for (int i = 0; i < known_pipes.length; i++) {
	        String pipe = known_pipes[i];
	        File qemu = new File(pipe);  //模拟处理器
	        if (qemu.exists()) {
	            Log.v("Result:", "Find pipes! Emulator!");
	            return true;
	        }
	    }
	    Log.v("Result:", "Not Find pipes! Real Device!");
	    return false;
	}
	// 0×2检测驱动文件内容//该项貌似不可靠！

	// 读取文件内容，然后检查已知qemu的驱动程序列表
	private static String[] known_qemu_drivers = { "goldfish" };
	public static Boolean CheckQemuDriverFile() {
	    File qemu_drivers = new File("/proc/tty/drivers");
	    if (qemu_drivers.exists() && qemu_drivers.canRead()) {
	        //byte[] data = new byte[(int) drivers_file.length()];
	    	byte[] data = new byte[1024];
	        try {
	            InputStream inStream = new FileInputStream(qemu_drivers);
	            inStream.read(data);
	            inStream.close();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        String driver_data = new String(data);
	        for (String known_qemu_driver : known_qemu_drivers) {
	            if (driver_data.indexOf(known_qemu_driver) != -1) {
	                Log.v("Result:", "Find known_qemu_drivers! Emulator!");
	                return true;
	            }
	        }
	    }
	    Log.v("Result:", "Not Find known_qemu_drivers! Real Device!");
	    return false;
	}
	//0×3
	//检测模拟器上特有的几个文件
	private static String[] known_files = {
		    "/system/lib/libc_malloc_debug_qemu.so",
		    "/sys/qemu_trace",
		    //"/system/bin/qemu-props" //此项在三星上有，所以不可靠！
		};
		public static Boolean CheckEmulatorFiles() {
		    for (int i = 0; i < known_files.length; i++) {
		        String file_name = known_files[i];
		        File qemu_file = new File(file_name);
		        if (qemu_file.exists()) {
		            Log.v("Result:", "Find Emulator Files! Emulator!");
		            return true;
		        }
		    }
		    Log.v("Result:", "Not Find Emulator Files! Real Device!");
		    return false;
		}
	//0×4
	//  检测模拟器默认的电话号码
		private static String[] known_numbers = { "15555215554", "15555215556",
		        "15555215558", "15555215560", "15555215562", "15555215564",
		        "15555215566", "15555215568", "15555215570", "15555215572",
		        "15555215574", "15555215576", "15555215578", "15555215580",
		        "15555215582", "15555215584", };
		 
		public static Boolean CheckPhoneNumber(Context context) {
		    TelephonyManager telephonyManager = (TelephonyManager) context
		            .getSystemService(Context.TELEPHONY_SERVICE);
		 
		    String phonenumber = telephonyManager.getLine1Number();
		 
		    for (String number : known_numbers) {
		        if (number.equalsIgnoreCase(phonenumber)) {
		            Log.v("Result:", "Find PhoneNumber! Emulator!");
		            return true;
		        }
		    }
		    Log.v("Result:", "Not Find PhoneNumber! Real Device!");
		    return false;
		}
	//0×5
	//检测设备IDS 是不是 “000000000000000”
		private static String[] known_device_ids = { "000000000000000" // 默认ID
		};
		 
		public static Boolean CheckDeviceIDS(Context context) {
		    TelephonyManager telephonyManager = (TelephonyManager) context
		            .getSystemService(Context.TELEPHONY_SERVICE);
		 
		    String device_ids = telephonyManager.getDeviceId();
		 
		    for (String know_deviceid : known_device_ids) {
		        if (know_deviceid.equalsIgnoreCase(device_ids)) {
		            Log.v("Result:", "Find ids: 000000000000000! Emulator!");
		            return true;
		        }
		    }
		    Log.v("Result:", "Not Find ids: 000000000000000! Real Device!");
		    return false;
		}
	//0×6
	//检测imsi id是不是“310260000000000”
		private static String[] known_imsi_ids = { "310260000000000" // 默认的 imsi id
		};
		public static Boolean CheckImsiIDS(Context context){
		    TelephonyManager telephonyManager = (TelephonyManager)
		            context.getSystemService(Context.TELEPHONY_SERVICE);
		 
		    String imsi_ids = telephonyManager.getSubscriberId();
		 
		    for (String know_imsi : known_imsi_ids) {
		        if (know_imsi.equalsIgnoreCase(imsi_ids)) {
		            Log.v("Result:", "Find imsi ids: 310260000000000! Emulator!");
		            return true;
		        }
		    }
		    Log.v("Result:", "Not Find imsi ids: 310260000000000! Real Device!");
		    return false;
		}
	//0×7
	//检测手机上的一些硬件信息
		public static Boolean CheckEmulatorBuild(Context context){
		    String BOARD = android.os.Build.BOARD;
		    String BOOTLOADER = android.os.Build.BOOTLOADER;
		    String BRAND = android.os.Build.BRAND;
		    String DEVICE = android.os.Build.DEVICE;
		    String HARDWARE = android.os.Build.HARDWARE;
		    String MODEL = android.os.Build.MODEL;
		    String PRODUCT = android.os.Build.PRODUCT;
		    if (BOARD == "unknown" || BOOTLOADER == "unknown"
		            || BRAND == "generic" || DEVICE == "generic"
		            || MODEL == "sdk" || PRODUCT == "sdk"
		            || HARDWARE == "goldfish")
		    {
		        Log.v("Result:", "Find Emulator by EmulatorBuild! Emulator!");
		        return true;
		    }
		    Log.v("Result:", "Not Find Emulator by EmulatorBuild! Real Device!");
		    return false;
		}
	//0×8
	//检测手机运营商家
		public static boolean CheckOperatorNameAndroid(Context context) {
		    String szOperatorName = ((TelephonyManager)
		            context.getSystemService("phone")).getNetworkOperatorName();
		 
		    if (szOperatorName.toLowerCase().equals("android") == true) {
		        Log.v("Result:", "Find Emulator by OperatorName! Emulator!");
		        return true;
		    }
		    Log.v("Result:", "Not Find Emulator by OperatorName! Real Device!");
		    return false;
		}
	
	public static boolean isEmulator(Context mContext){
		if(checkPipes()||CheckEmulatorFiles()||CheckPhoneNumber(mContext)
				||CheckDeviceIDS(mContext)||CheckImsiIDS(mContext)||
				CheckEmulatorBuild(mContext)||CheckOperatorNameAndroid(mContext)){
			return true;
			
		}else{
			return false;
		}
		
	}

}
