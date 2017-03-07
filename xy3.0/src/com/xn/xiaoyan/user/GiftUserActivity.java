package com.xn.xiaoyan.user;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boyiqove.view.BaseActivity;
import com.xn.xiaoyan.R;

public class GiftUserActivity extends BaseActivity {
	private LinearLayout backIv;
	private TextView titleTv;
	private ImageView opinImage1, opinImage2, opinImage3, opinImage4,
			opinImage5;
	private ListView itemListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.geft_user);
//		initView();
//		initData();
	}

	private void initData() {
		// 设置点击进入新页面

		// 请求列表，创建adapter
	}

//	private void initView() {
//		// TODO Auto-generated method stub
//		backIv = (LinearLayout) findViewById(R.id.boe_back_bt);
//		backIv.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//				overridePendingTransition(R.anim.boyi_move_left_in,
//						R.anim.boyi_move_left_out);
//			}
//		});
//		titleTv = (TextView) findViewById(R.id.search_top_title_tv);
//		titleTv.setText("精选好书");
//		// 推荐书籍
//		itemListView = (ListView) findViewById(R.id.user_geft_listview);
//
//		opinImage1 = (ImageView) findViewById(R.id.user_geft_top_iv1);
//		opinImage2 = (ImageView) findViewById(R.id.user_geft_top_iv2);
//		opinImage3 = (ImageView) findViewById(R.id.user_geft_top_iv3);
//		opinImage4 = (ImageView) findViewById(R.id.user_geft_top_iv4);
//
//	}
}
