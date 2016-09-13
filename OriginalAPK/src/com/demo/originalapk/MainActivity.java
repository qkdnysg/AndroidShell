package com.demo.originalapk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//ptraceTest();
		//setContentView(R.layout.activity_main);
		TextView content = new TextView(this);
		content.setText("This is the original APK!");
		content.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, SecondActivity.class);
				startActivity(intent);
			}});
		setContentView(content);
		
		Log.i("demo", "app:"+getApplicationContext());
	}

}
