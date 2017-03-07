package com.boyiqove.ui.bookshelf;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.ResultCode;
import com.boyiqove.config.ReadConfig;
import com.boyiqove.view.BaseActivity;
import com.bytetech1.sdk.chapter.LoginChapter;

public class PaymentActivity extends BaseActivity {
	private Button btButton;
	private ImageView btBlack;
	private TextView orderTv,title;
	private RelativeLayout storeLayout;
	private TextView iView;
	private ReadConfig mReadConfig;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boyi_bookdetail_order_enter);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.boyi_book).setVisibility(View.GONE);
		findViewById(R.id.search).setVisibility(View.GONE);
		orderTv=(TextView) findViewById(R.id.txt_warn_bodys);
		orderTv.setText(getIntent().getStringExtra("chargeInfo"));
		iView=(TextView) findViewById(R.id.iv_Automatic);
		mReadConfig = AppData.getConfig().getReadConfig();
		if (mReadConfig.isAutoBuy()) {
			iView.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.boyi_gou_buy_yes, 0, 0, 0);
		} else {
			iView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.boyi_gou_buy,
					0, 0, 0);
		}

		btButton=(Button) findViewById(R.id.btn_enter_order);	
		btBlack=(ImageView) findViewById(R.id.search_back);	
		title=(TextView) findViewById(R.id.search_top_title_tv);
		storeLayout=(RelativeLayout) findViewById(R.id.boyi_book);
		storeLayout.setVisibility(View.GONE);
	}

	private void initData() {
		// TODO Auto-generated method stub
		title.setText(R.string.akpay_login_ishugui_title);

		iView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mReadConfig.isAutoBuy()) {
					iView.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.boyi_gou_buy, 0, 0, 0);
					mReadConfig.setAutoBuy(false);

				} else {
					iView.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.boyi_gou_buy_yes, 0, 0, 0);
					mReadConfig.setAutoBuy(true);
				}
			}
		});

		btButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent  intent=new Intent(PaymentActivity.this, OnlineReadingActivity.class);
				intent.putExtra("isGoBuy", true);
				setResult(ResultCode.ORDER_INFO, intent);
				finish();
			}
		});
		
		btBlack.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent  intent=new Intent(PaymentActivity.this, OnlineReadingActivity.class);
				intent.putExtra("isGoBuy", false);
				setResult(ResultCode.ORDER_INFO, intent);
				finish();
			}
		});
	
	}

}
