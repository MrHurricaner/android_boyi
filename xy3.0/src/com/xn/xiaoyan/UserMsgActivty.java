package com.xn.xiaoyan;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.boyiqove.view.BaseActivity;

public class UserMsgActivty extends BaseActivity{
	private TextView title;
	private ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_msg);
		initView();
	}
	private void initView() {
		title=(TextView) findViewById(R.id.left_activity_title);
		title.setText(getResources().getString(R.string.user_text1));
		back=(ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
	}

}
