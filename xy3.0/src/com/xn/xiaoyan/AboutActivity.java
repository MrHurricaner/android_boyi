package com.xn.xiaoyan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.view.BaseActivity;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xiaoyan.util.UIUtils;



public class AboutActivity extends BaseActivity{

	private TextView title,version_code;
	private ImageView back;
	private Button btn_usermsg,version_update;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
		initView();
	}

	private void initView() {
		version_code=(TextView) findViewById(R.id.version_code);
		version_code.setText("版本号:  "+AppData.getConfig().getDeviveInfo().getVersionName());
		title=(TextView) findViewById(R.id.left_activity_title);
		title.setText(getResources().getString(R.string.about_us));
		back=(ImageView) findViewById(R.id.back);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
		btn_usermsg=(Button) findViewById(R.id.btn_user_msg);
		version_update=(Button) findViewById(R.id.btn_version_update);
		btn_usermsg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(AboutActivity.this,UserMsgActivty.class);
				startActivity(intent);
			}
		});
		version_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//版本更新
				UIUtils.UmengUpdate(AboutActivity.this,null);
			}
		});
	}
}
