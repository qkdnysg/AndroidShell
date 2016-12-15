package com.demo.mUtils;

import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

public class GetKeyByID extends AsyncTask<List<NameValuePair>, String, String> {
	private static final String url_get_key_by_ID = 
    		"http://10.0.2.2:8080/android_connect/get_key_by_ID.php";

	private static final String TAG = "GetKeyByID";

	private static final String TAG_SUCCESS = "success";

	private static final String TAG_KEY = "key";

	private static final String TAG_KEYVALUE = "keyvalue";
	
	JSONParser jsonParser = new JSONParser();
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();       
    }
    /**
     * 获取keye
     * */
    public String doInBackground(List<NameValuePair>... inparams) {
    	 if (android.os.Build.VERSION.SDK_INT > 9) {
 		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
 		    StrictMode.setThreadPolicy(policy);
 		}

 		// Check for success tag
        int success;
        try {
            JSONObject json = jsonParser.makeHttpRequest(
            		url_get_key_by_ID, "GET", inparams[0]);
            Log.d(TAG, "服务端返回的json对象:" + json.toString());
            // json success tag
            success = json.getInt(TAG_SUCCESS);//参看PHP代码
            if (success == 1) {
                JSONArray keyObj = json.getJSONArray(TAG_KEY); // JSON Array
                JSONObject key = keyObj.getJSONObject(0);
                Log.d(TAG, "获得的key json对象:" + key.toString());
                return key.getString(TAG_KEYVALUE);//.getString(TAG_KEYVALUE)
            }else{
            	return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
            
        return null;
    }

    
    protected void onPostExecute(String keyresult) {
    	//key = keyresult;
    }

}
