package com.xn.xiaoyan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.boyiqove.AppData;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class SpashActivity extends Activity {
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			startActivity();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.spash);
		
		initView();
	}

	protected void startActivity() {
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		finish();

	}

	private void initView() {
		
		// session失效时间
		MobclickAgent.setSessionContinueMillis(30000);
		// 欢迎界面停留三秒
		handler.sendEmptyMessageDelayed(0,3000);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

}
