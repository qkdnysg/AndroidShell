package com.demo.originalapk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SecondActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_second);
		TextView content = new TextView(this);
		content.setText("This is the second activity!");
		
		setContentView(content);
		
		Log.i("demo", "app:"+getApplicationContext());
	}

}
