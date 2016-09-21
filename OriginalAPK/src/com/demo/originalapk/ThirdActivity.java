package com.demo.originalapk;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ThirdActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_third);
		// 创建一个线性布局管理器
		LinearLayout layout = new LinearLayout(this);//this?
		// 设置该Activity显示layout
		super.setContentView(layout);
		layout.setOrientation(LinearLayout.VERTICAL);
		// 创建一个TextView
		final TextView show = new TextView(this);
		// 创建一个按钮
		Button bn = new Button(this);
		bn.setText("click me!");
		bn.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		// 向layout容器中添加TextView
		layout.addView(show);
		// 向layout容器中添加按钮
		layout.addView(bn);
		// 为按钮绑定一个事件监听器
		bn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				show.setText("Hello , Android , " + new java.util.Date());
			}
		});
	}

}
