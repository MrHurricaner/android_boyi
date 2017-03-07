package com.boyiqove.ui.bookshelf;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boyiqove.R;
import com.boyiqove.ResultCode;
import com.boyiqove.view.BaseActivity;
import com.bytetech1.sdk.chapter.LoginChapter;

public class OneKeyFastActivity extends BaseActivity {
	private Button btCm,btUn,btCu;
	private TextView title;
	private Button storeLayout;
	private ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.akpay_login_sms);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		btCm=(Button) findViewById(R.id.btn_onekey_login_cm);
		btUn=(Button) findViewById(R.id.btn_onekey_login_un);
		btCu=(Button) findViewById(R.id.btn_onekey_login_cu);
		back=(ImageView) findViewById(R.id.search_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		title=(TextView) findViewById(R.id.search_top_title_tv);
		storeLayout=(Button) findViewById(R.id.boyi_book);
		storeLayout.setVisibility(View.GONE);
	}

	private void initData() {
		// TODO Auto-generated method stub
		title.setText(R.string.boy_ak_login);
		
		
		

		btCm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent  intent=new Intent(OneKeyFastActivity.this, OnlineReadingActivity.class);
				intent.putExtra("loginFast", LoginChapter.TYPE_CM);
				setResult(ResultCode.LOGIN_TO_FAST, intent);
				finish();
			}
		});
		btUn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent  intent=new Intent(OneKeyFastActivity.this, OnlineReadingActivity.class);
				intent.putExtra("loginFast", LoginChapter.TYPE_CU);
				setResult(ResultCode.LOGIN_TO_FAST, intent);
				finish();
			}
		});
		btCu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent  intent=new Intent(OneKeyFastActivity.this, OnlineReadingActivity.class);
				intent.putExtra("loginFast", LoginChapter.TYPE_TELCOM);
				setResult(ResultCode.LOGIN_TO_FAST, intent);
				finish();
			}
		});
		
	}

}
