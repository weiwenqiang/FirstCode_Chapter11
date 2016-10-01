package com.example.chapter11;

import com.example.chapter11.baidumap.TestBaiduMap;
import com.example.chapter11.location.TestLocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		findViewById(R.id.c11_b1).setOnClickListener(this);
		findViewById(R.id.c11_b2).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.c11_b1:
			startActivity(new Intent(MainActivity.this, TestLocation.class));
			break;
		case R.id.c11_b2:
			startActivity(new Intent(MainActivity.this, TestBaiduMap.class));
			break;
		}
	}
}
