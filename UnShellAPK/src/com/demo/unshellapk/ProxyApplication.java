package com.demo.unshellapk;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.demo.emulatorCheck.CheckTool;
import com.demo.jnitool.JNITool;
//import com.example.product.JSONParser;
//import com.example.product.EditProductActivity;
//import com.example.product.R;
//import com.example.product.EditProductActivity.GetProductDetails;
import com.demo.jnitool.RC4;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import dalvik.system.DexClassLoader;
import android.os.StrictMode;
import android.telephony.TelephonyManager;

public class ProxyApplication extends Application{
	private static final String appkey = "APP_CLASS_NAME";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_KEYVALUE = "keyvalue";
	private static final String TAG_KEY = "key";
	private String apkFileName;
	private String odexPath;
	private String libPath;
	//private String key;
	//private int shellID; 
    private static final String url_get_key_by_ID = 
    		"http://10.0.2.2:8080/android_connect/get_key_by_ID.php";
	private static final String TAG_IIR = "ID_IMEI_RTIME";
	private static final String TAG = "com.demo.unshellapk";
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    
    class GetKeyByID extends AsyncTask<List<NameValuePair>, String, String> {
 		/**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           
        }
        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(List<NameValuePair>... inparams) {
        	 if (android.os.Build.VERSION.SDK_INT > 9) {
     		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
     		    StrictMode.setThreadPolicy(policy);
     		}
 
            // updating UI from Background Thread
//            runOnUiThread(new Runnable() {
//                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        //List<NameValuePair> params = new ArrayList<NameValuePair>();
                        //params.add(new BasicNameValuePair("kid", Integer.toString(inparams[0])));//shellID
 
                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                        		url_get_key_by_ID, "GET", inparams[0]);
 
                        // check your log for json response
                        Log.d(TAG_IIR, "服务端返回的json对象:" + json.toString());
 
                        // json success tag
                        success = json.getInt(TAG_SUCCESS);//参看PHP代码
                        if (success == 1) {
                            // successfully received product details
                            JSONArray keyObj = json
                                    .getJSONArray(TAG_KEY); // JSON Array
 
                            // get first product object from JSON Array
                            JSONObject key = keyObj.getJSONObject(0);
                            Log.d(TAG_IIR, "获得的key json对象:" + key.toString());
                            
                            return key.getString(TAG_KEYVALUE);//.getString(TAG_KEYVALUE)
                              
                        }else{
                            // product with pid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String keyresult) {
            // dismiss the dialog once got all details
            //pDialog.dismiss();
        	//key = keyresult;
        }
    }

	
	//这是context 赋值
	@Override
	protected void attachBaseContext(Context base) {
//		if (android.os.Build.VERSION.SDK_INT > 9) {
//		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//		    StrictMode.setThreadPolicy(policy);
//		}		
		super.attachBaseContext(base);
		if(JNITool.isEmulator(base)){//-模拟器检测
			Log.i(TAG, "发现了模拟器，3秒后程序退出...");
			//Toast.makeText(ProxyApplication.this, "对不起，该应用尚不支持模拟器！", Toast.LENGTH_LONG).show();
			try {
				Thread.sleep(3000);
				Log.i(TAG, "此时程序本已退出，为测试方便暂留其一条狗命...");
				//android.os.Process.killProcess(android.os.Process.myPid()); //结束程序
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		}
		
		try {
			//创建两个文件夹payload_odex，payload_lib 私有的，可写的文件目录
			File odex = this.getDir("payload_odex", MODE_PRIVATE);
			///data/data/com.demo.unshellapk/app_payload_odex
			File libs = this.getDir("payload_lib", MODE_PRIVATE);
			///data/data/com.demo.unshellapk/app_payload_lib
			odexPath = odex.getAbsolutePath();
			///data/data/com.demo.unshellapk/app_payload_odex
			libPath = libs.getAbsolutePath();
			//data/data/com.demo.unshellapk/app_payload_lib
			apkFileName = odex.getAbsolutePath() + "/payload.apk";
			//apkFileName = /data/data/com.demo.unshellapk/app_payload_odex/payload.apk
			File dexFile = new File(apkFileName);
			//dexFile=/data/data/com.demo.unshellapk/app_payload_odex/payload.apk
			Log.i("demo", "apk size:"+dexFile.length()); 
			//此时的payload.apk尚不存在，所以apk size:0 
			if (!dexFile.exists())//dexFile不存在
			{
				dexFile.createNewFile();  
				//在app_payload_odex文件夹内，创建payload.apk
				
				byte[] dexdata = this.readDexFileFromApk();
				// 从base.apk中读出classes.dex文件//存入dexdata中
				
				this.splitPayLoadFromDex(dexdata);
				//// 从dexdata中分离出原始APK文件以用于动态加载
				// 将apk中 的so文件放入app_payload_lib目录，不过貌似只考虑到了一个so的情况，多个so存在时会发生覆盖，只会保留最后一个so文件，所幸测试平台是x86的，不然毁了...
			}
			// 配置动态加载环境
			Object currentActivityThread = RefInvoke.invokeStaticMethod(
					"android.app.ActivityThread", "currentActivityThread",
					new Class[] {}, new Object[] {});//获取主线程对象 http://blog.csdn.net/myarrow/article/details/14223493
			//currentActivityThread=android.app.ActivityThread@9a1b8f0
			String packageName = this.getPackageName();
			//packageName = com.demo.unshellapk//当前apk的包名
			
			//下面两句不是太理解
			ArrayMap mPackages = (ArrayMap) RefInvoke.getFieldOjbect(
					"android.app.ActivityThread", currentActivityThread,
					"mPackages");
			//mPackages = currentActivityThread.mPackages={com.demo.unshellapk=java.lang.ref.WeakReference@b9369e4}
			WeakReference wr = (WeakReference) mPackages.get(packageName);
			//wr = java.lang.ref.WeakReference@b9369e4
			//创建原始apk的DexClassLoader对象  加载apk内的类和原生代码（c/c++代码）
			DexClassLoader dLoader = new DexClassLoader(apkFileName, odexPath,
					libPath, (ClassLoader) RefInvoke.getFieldOjbect(
							"android.app.LoadedApk", wr.get(), "mClassLoader"));
			//dLoader = new DexClassLoader(/data/data/com.demo.unshellapk/app_payload_odex/payload.apk,
			//                             /data/data/com.demo.unshellapk/app_payload_odex,
			//                             /data/data/com.demo.unshellapk/app_payload_lib,
			//                             dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.demo.unshellapk-1/base.apk"],
			//                             nativeLibraryDirectories=[/data/app/com.demo.unshellapk-1/lib/x86, /vendor/lib, /system/lib]]])
			//        =dalvik.system.DexClassLoader[DexPathList[[zip file "/data/data/com.demo.unshellapk/app_payload_odex/payload.apk"],
			//           nativeLibraryDirectories=[/data/data/com.demo.unshellapk/app_payload_lib, /vendor/lib, /system/lib]]]
			//wr.get()=android.app.LoadedApk@379ed33
			//base.getClassLoader(); 是不是就等同于 (ClassLoader) RefInvoke.getFieldOjbect()? 有空验证下//?
			//把当前进程的DexClassLoader 设置成了被加壳apk的DexClassLoader  ----有点c++中进程环境的意思~~
			RefInvoke.setFieldOjbect("android.app.LoadedApk", "mClassLoader",
					wr.get(), dLoader);
			//wr.get().mClassLoader=dLoader//置换类加载器
			
			Log.i("demo","classloader:"+dLoader);
			
			try{
				Object actObj = dLoader.loadClass("com.demo.originalapk.MainActivity");
				Log.i("demo", "actObj:"+actObj);
			}catch(Exception e){
				Log.i("demo", "activity:"+Log.getStackTraceString(e));
			}//这段Jack_Jia的版本里没有
			

		} catch (Exception e) {
			Log.i("demo", "error:"+Log.getStackTraceString(e));
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate() {
		{
			//loadResources(apkFileName);
			
			Log.i("demo", "onCreate");
			// 如果源应用配置有Appliction对象，则替换为源应用Applicaiton，以便不影响源程序逻辑。
			String appClassName = null;
			try {
				ApplicationInfo ai = this.getPackageManager()
						.getApplicationInfo(this.getPackageName(),
								PackageManager.GET_META_DATA);
				Bundle bundle = ai.metaData;
				if (bundle != null && bundle.containsKey(appkey)) {
					appClassName = bundle.getString(appkey);
					//className 是配置在xml文件中的。获取meta-data中的原始apk的Application类名：com.demo.originalapk.OriginalApplication
					
				} else {
					Log.i("demo", "failed to obtain application class name");
					return;
				}
			} catch (NameNotFoundException e) {
				Log.i("demo", "error:"+Log.getStackTraceString(e));
				e.printStackTrace();
			}
			//有值的话调用该Applicaiton
			Object currentActivityThread = RefInvoke.invokeStaticMethod(
					"android.app.ActivityThread", "currentActivityThread",
					new Class[] {}, new Object[] {});//currentActivityThread=android.app.ActivityThread@377bcfab
			Object mBoundApplication = RefInvoke.getFieldOjbect(
					"android.app.ActivityThread", currentActivityThread,
					"mBoundApplication");//currentActivityThread.mBoundApplication=AppBindData{appInfo=ApplicationInfo{27a8be23 com.demo.unshellapk}}
			Object loadedApkInfo = RefInvoke.getFieldOjbect(
					"android.app.ActivityThread$AppBindData",
					mBoundApplication, "info");//loadedApkInfo=mBoundApplication.info=android.app.LoadedApk@75c298f
			//把当前进程的mApplication 设置成了null
			RefInvoke.setFieldOjbect("android.app.LoadedApk", "mApplication",
					loadedApkInfo, null);//loadedApkInfo.mApplication=null;
			Object oldApplication = RefInvoke.getFieldOjbect(
					"android.app.ActivityThread", currentActivityThread,
					"mInitialApplication");//oldApplication = currentActivityThread.mInitialApplication=com.demo.unshellapk.ProxyApplication@1fd8e6d8
			//http://www.codeceo.com/article/android-context.html
			ArrayList<Application> mAllApplications = (ArrayList<Application>) RefInvoke
					.getFieldOjbect("android.app.ActivityThread",
							currentActivityThread, "mAllApplications");
			//mAllApplications = currentActivityThread.mAllApplications=[com.demo.unshellapk.ProxyApplication@1fd8e6d8]
			mAllApplications.remove(oldApplication);//删除oldApplication，此时mAllApplications为空：[]
			
			ApplicationInfo appinfo_In_LoadedApk = (ApplicationInfo) RefInvoke
					.getFieldOjbect("android.app.LoadedApk", loadedApkInfo,
							"mApplicationInfo");
			//appinfo_In_LoadedApk = loadedApkInfo.mApplicationInfo=ApplicationInfo{27a8be23 com.demo.unshellapk}
			ApplicationInfo appinfo_In_AppBindData = (ApplicationInfo) RefInvoke
					.getFieldOjbect("android.app.ActivityThread$AppBindData",
							mBoundApplication, "appInfo");
			//appinfo_In_AppBindData = mBoundApplication.appInfo=ApplicationInfo{27a8be23 com.demo.unshellapk}
			appinfo_In_LoadedApk.className = appClassName;
			appinfo_In_AppBindData.className = appClassName;//appClassName=com.demo.originalapk.OriginalApplication
			Application app = (Application) RefInvoke.invokeMethod(
					"android.app.LoadedApk", "makeApplication", loadedApkInfo,
					new Class[] { boolean.class, Instrumentation.class },
					new Object[] { false, null });//执行 makeApplication（false,null）
			//app = loadedApkInfo.makeApplication(false, null) = com.demo.originalapk.OriginalApplication@34f8edd3
			RefInvoke.setFieldOjbect("android.app.ActivityThread",
					"mInitialApplication", currentActivityThread, app);
			//currentActivityThread.mInitialApplication = app = com.demo.originalapk.OriginalApplication@34f8edd3


			ArrayMap mProviderMap = (ArrayMap) RefInvoke.getFieldOjbect(
					"android.app.ActivityThread", currentActivityThread,
					"mProviderMap");
			//mProviderMap = currentActivityThread.mProviderMap = {}???
			Iterator it = mProviderMap.values().iterator();
			//mProviderMap.values()=android.util.MapCollections$ValuesCollection@20bd3b04
			//it=android.util.MapCollections$ArrayIterator@138e0a6c
			while (it.hasNext()) {
				Object providerClientRecord = it.next();
				Object localProvider = RefInvoke.getFieldOjbect(
						"android.app.ActivityThread$ProviderClientRecord",
						providerClientRecord, "mLocalProvider");
				//1.localProvider = providerClientRecord.mLocalProvider=???
				RefInvoke.setFieldOjbect("android.content.ContentProvider",
						"mContext", localProvider, app);
				//1.localProvider.mContext = app
			}
			
			Log.i("demo", "app:"+app);
			
			app.onCreate();
		}
	}
//MD5方法
	public String md5(String string) {
	    byte[] hash;
	    try {
	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString();
	}
	/**
	 * 已读！！！
	 * 释放源APK文件，并提取源APK文件中的so文件到data/data/包名/payload_lib目录
	 * @param byte[] apkdata
	 * @throws IOException
	 */
	private void splitPayLoadFromDex(byte[] apkdata) throws IOException {
		int ablen = apkdata.length;
		//前边传来的参数为dexdata，这里的字节数组apkdata应为嵌入了壳APK的解壳dex文件的字节数组，
		//即classes.dex.length
		//取壳APK的长度   这里的长度取值，对应加壳时长度的赋值,都可以做些简化
		byte[] dexlen = new byte[4];
		byte[] bshellID = new byte[4];
		System.arraycopy(apkdata,ablen - 4 - 4, dexlen, 0, 4);//拷贝apkdata的最后8--4字节到dexlen
		System.arraycopy(apkdata,ablen - 4, bshellID, 0, 4);//拷贝shellID到bshellID
		ByteArrayInputStream bais = new ByteArrayInputStream(dexlen);
		DataInputStream in = new DataInputStream(bais);
		int readInt = in.readInt();//readInt中存放的应为壳APK的大小，即size_shellAPK
		ByteArrayInputStream bis = new ByteArrayInputStream(bshellID);
		DataInputStream din = new DataInputStream(bis);
		int shellID = din.readInt();//读取加固ID
		System.out.println(Integer.toHexString(readInt));
		byte[] newdex = new byte[readInt];
		//把壳APK内容拷贝到newdex中
		System.arraycopy(apkdata, ablen - 4 - 4 - readInt, newdex, 0, readInt);
		//若加壳是加密处理的话, 这里应该加上对APK的解密操作
		//对壳APK进行脱壳
		//newdex = unShell(newdex); //此时得到的newdex即为源APK
//		DButils mdb = new DButils(shellID);
//		mdb.changekey();
//		String key = mdb.getkey();
//		NetThread mNetThread = new NetThread(shellID);
//		mNetThread.run();
//		String key = mNetThread.getkey();
		//new GetKeyByID().execute();
		String KID = String.valueOf(shellID);
		TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);    
		String IMEI = mTm.getDeviceId();
		String RTIME = String.valueOf(System.currentTimeMillis());
		Log.d(TAG_IIR, "KID:" + KID + "-----IMEI:" + IMEI + "-----RTIME:" + RTIME);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("kid", KID));
        params.add(new BasicNameValuePair("IMEI", IMEI));
        params.add(new BasicNameValuePair("RTIME", RTIME));
        
		GetKeyByID mGetKeyByID = new GetKeyByID();
		//JSONArray keyjson = mGetKeyByID.doInBackground(params);
		String keye = mGetKeyByID.doInBackground(params);
		//int jsonlen = keyjson.length();
		//byte[] bkeyjson = new byte[jsonlen];
		//String keyrc4 = "";
		/*for(int i = 0, tmp=0; i < jsonlen; i++) {
			try {
				tmp = Integer.parseInt(keyjson.getString(i));
				bkeyjson[i] =  (byte)(tmp);//0xFF ^  
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}*/
		//Log.d(TAG_IIR, "keye:" + keye);
		byte[] bkeyjson = Base64.decode(keye.getBytes(), Base64.DEFAULT);//得到的值与服务端发送的一致，证明到此是正确的
		//Log.d(TAG_IIR, "keye2:" + keye2);
		String keymd5 = md5(KID + IMEI + RTIME);
		Log.d(TAG_IIR, "keymd5:" + keymd5);
		//String keye2 = new String(bkeyjson,"utf-8");
		//keyrc4 = 
		String key = RC4.decry_RC4(bkeyjson, keymd5);
//		String hexStr = RC4.encry_RC4_string(keyrc4, keymd5);
//		byte[] bkey = RC4.HexString2Bytes(hexStr);
//		String key = new String(bkey,"utf-8");
		Log.d(TAG_IIR, "key:" + key);
		
		key = JNITool.checkey(key);
		
		Log.i("demo", "开始脱壳");
		newdex = JNITool.decrypt(key, newdex);
		newdex = JNITool.unShell(newdex);		 
		Log.i("demo", "脱壳完成");
		
		
		//写入apk文件   
		File file = new File(apkFileName);
		try {
			FileOutputStream localFileOutputStream = new FileOutputStream(file);
			localFileOutputStream.write(newdex);
			//将脱壳后的字节数组newdex通过输出流写入文件“apkFileName”中，此时的“apkFileName”即为源APK//此时的app_payload_odex/payload.apk即为原始apk
			localFileOutputStream.close();
		} catch (IOException localIOException) {
			throw new RuntimeException(localIOException);
		}
		
		//分析源apk文件
		ZipInputStream localZipInputStream = new ZipInputStream(
				new BufferedInputStream(new FileInputStream(file)));
		while (true) {
			ZipEntry localZipEntry = localZipInputStream.getNextEntry();
			if (localZipEntry == null) {
				localZipInputStream.close();//遍历完APK中的项目时关闭流跳出循环
				break;
			}
			//取出源APK用到的so文件，放到 libPath中（data/data/包名/payload_lib)
			String name = localZipEntry.getName();
			if (name.startsWith("lib/") && name.endsWith(".so")) {//只考虑到了一个so的情况！
				File storeFile = new File(libPath + "/"
						+ name.substring(name.lastIndexOf('/')));
				storeFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(storeFile);
				byte[] arrayOfByte = new byte[1024];
				while (true) {
					int i = localZipInputStream.read(arrayOfByte);//此时输入流指针已经指向so文件了？
					if (i == -1)
						break;
					fos.write(arrayOfByte, 0, i);
				}
				fos.flush();
				fos.close();
			}
			localZipInputStream.closeEntry();
		}
		localZipInputStream.close();


	}

	

	/**
	 * 已读！！！
	 * 从apk包里面获取dex文件内容（byte）
	 * @return byte[]
	 * @throws IOException
	 */
	private byte[] readDexFileFromApk() throws IOException {
		ByteArrayOutputStream dexByteArrayOutputStream = new ByteArrayOutputStream();
		ZipInputStream localZipInputStream = new ZipInputStream(
				new BufferedInputStream(new FileInputStream(
						this.getApplicationInfo().sourceDir)));
		//this.getApplicationInfo().sourceDir=data/app/com.demo.unshellapk-1.apk/base.apk这个可能是初始安装时未优化的apk
		while (true) {
			ZipEntry localZipEntry = localZipInputStream.getNextEntry();//获取base.apk中的子项
			if (localZipEntry == null) {
				localZipInputStream.close();
				break;
			}
			if (localZipEntry.getName().equals("classes.dex")) {
				byte[] arrayOfByte = new byte[1024];
				while (true) {
					int i = localZipInputStream.read(arrayOfByte);
					if (i == -1)
						break;
					dexByteArrayOutputStream.write(arrayOfByte, 0, i);
				}
			}
			localZipInputStream.closeEntry(); //非classes.dex时关闭该子项//获取项后还需要关闭项？
		}
		localZipInputStream.close();//遍历完所有子项后关闭流，不止一个classes.dex时都会写入输出流？
		return dexByteArrayOutputStream.toByteArray();////以字节数组形式返回classes.dex
	}


	/**
	 * 解壳方法
	 * 此处的解壳方法简化为逐字节取反
	 */ 
	private byte[] unShell(byte[] srcdata) {
		for(int i=0;i<srcdata.length;i++){
			srcdata[i] = (byte)(0xFF ^ srcdata[i]);//相当于逐字节取反
		}
		return srcdata;
	}
	
	
	//以下是加载资源
	protected AssetManager mAssetManager;//资源管理器 
	protected Resources mResources;//资源  
	protected Theme mTheme;///主题  
	
	protected void loadResources(String dexPath) {  
        try {  
            AssetManager assetManager = AssetManager.class.newInstance();  
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);  
            addAssetPath.invoke(assetManager, dexPath);  
            mAssetManager = assetManager;  
        } catch (Exception e) {  
        	Log.i("inject", "loadResource error:"+Log.getStackTraceString(e));
            e.printStackTrace();  
        }  
        Resources superRes = super.getResources();  
        superRes.getDisplayMetrics();  
        superRes.getConfiguration();  
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),superRes.getConfiguration());  
        mTheme = mResources.newTheme();  
        mTheme.setTo(super.getTheme());
    }  
	
	@Override  
	public AssetManager getAssets() {  
	    return mAssetManager == null ? super.getAssets() : mAssetManager;  
	}  
	
	@Override  
	public Resources getResources() {  
	    return mResources == null ? super.getResources() : mResources;  
	}  
	
	@Override  
	public Theme getTheme() {  
	    return mTheme == null ? super.getTheme() : mTheme;  
	} 
	
}
