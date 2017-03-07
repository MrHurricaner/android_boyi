package com.xn.xiaoyan;

import java.io.File;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.library.volley.Cache;
import com.boyiqove.ui.bookshelf.OnlineReadingActivity;
import com.boyiqove.view.BaseActivity;
import com.umeng.update.UmengUpdateAgent;
import com.xiaoyan.util.DataCleanManager;
import com.xiaoyan.util.MyButton;
import com.xiaoyan.util.MyButton.OnChangedListener;
import com.xiaoyan.util.UIUtils;
import com.xn.xiaoyan.R;




public class SettingActivity extends BaseActivity implements OnClickListener{
	private TextView title,tv_cache_size;
	private ImageView back,shoudong_update;
	private MyButton accept_msg,update_version;
	private RelativeLayout clear_cache,shoudong_update_rl;
	private String cachePath,text;
	private File file;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);
		initView();
		
	}
	private void initView() {
		title=(TextView) findViewById(R.id.left_activity_title);
		title.setText(getResources().getString(R.string.setting));
		back=(ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
		accept_msg=(MyButton) findViewById(R.id.accept_msg);
		update_version=(MyButton) findViewById(R.id.update_version);
		accept_msg.setChecked(AppData.isIsOpenLast());
		update_version.setChecked(AppData.isAutoUpdate());
		shoudong_update=(ImageView) findViewById(R.id.shoudong_update);
		clear_cache=(RelativeLayout) findViewById(R.id.clear_cache);
		tv_cache_size=(TextView) findViewById(R.id.tv_cache_size);
		shoudong_update_rl=(RelativeLayout) findViewById(R.id.shoudong_update_rl);
		shoudong_update_rl.setOnClickListener(this);
		cachePath=getApplicationContext().getCacheDir().getPath()+"/";
		file=new File(cachePath);
		try {
			text=getResources().getString(R.string.clear_cache)+"  ("+DataCleanManager.getCacheSize(file)+")";
			tv_cache_size.setText(text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clear_cache.setOnClickListener(this);
	    shoudong_update.setOnClickListener(this);
	    update_version.setOnClickListener(this);
	    accept_msg.setOnClickListener(this);
	    
	    accept_msg.setOnChangedListener(new OnChangedListener() {
			
			@Override
			public void OnChanged(MyButton wiperSwitch, boolean checkState) {
				if(AppData.isIsOpenLast())
				{
					accept_msg.setChecked(false);
					AppData.setIsOpenLast(false);
				}else
				{
					accept_msg.setChecked(true);
					AppData.setIsOpenLast(true);
				}
				
			}
		});
	    update_version.setOnChangedListener(new OnChangedListener() {
			
			@Override
			public void OnChanged(MyButton wiperSwitch, boolean checkState) {
				if(AppData.isAutoUpdate())
				{
					update_version.setChecked(false);
					AppData.setIsAutoUpdate(false);
				}else
				{
					update_version.setChecked(true);
					AppData.setIsAutoUpdate(true);
				}
			}
		});
		
	}
	protected void showDeleteDialog() {
		LayoutInflater inflaterDl = LayoutInflater.from(SettingActivity.this);
        RelativeLayout layout = (RelativeLayout)inflaterDl.inflate(R.layout.boy_add_to_bookshelf_dialog, null );
        final AlertDialog alertDialog=new AlertDialog.Builder(SettingActivity.this).create();
        alertDialog.show();
        alertDialog.getWindow().setContentView(layout);
		Button btnpositive= (Button) layout.findViewById(R.id.positive);
		Button btngoagain=(Button) layout.findViewById(R.id.goagain);
		Button btnNegative=(Button) layout.findViewById(R.id.negative);
		Button btnReject=(Button) layout.findViewById(R.id.reject);
		ImageView btnClose=(ImageView) layout.findViewById(R.id.guanbi);
		TextView textMsg=(TextView) layout.findViewById(R.id.text_msg);
		TextView textAsk=(TextView) layout.findViewById(R.id.ask_leave);
		btngoagain.setVisibility(View.GONE);
		btnReject.setVisibility(View.GONE);
		textAsk.setVisibility(View.GONE);
		btnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		textMsg.setText(getResources().getString(R.string.delete_cache));
		btnpositive.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DataCleanManager.deleteFolderFile(cachePath,true);
				try {
					text=getResources().getString(R.string.clear_cache)+"  ("+DataCleanManager.getCacheSize(file)+")";
					tv_cache_size.setText(text);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				alertDialog.dismiss();
				UIUtils.showToast(SettingActivity.this,"清理完毕");
			}
		});
		btnNegative.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		//接收通知消息
		case R.id.accept_msg:
			break;
		//更新版本
		case R.id.update_version:
			break;
		//手动更新版本
		case R.id.shoudong_update_rl:
			shoudong_update.setVisibility(View.VISIBLE);
			UIUtils.startAnimation(shoudong_update,SettingActivity.this);
			//友盟更新
			UIUtils.UmengUpdate(SettingActivity.this,shoudong_update);
			
			break;
		//清理缓存
		case R.id.clear_cache:
			//缓存为空
			try {
				if("0.0Byte".equals(DataCleanManager.getCacheSize(file)))
				{
					showToast("你的手机缓存已为空，无须再清理！",Toast.LENGTH_LONG);
					return;
				}
				else
				{
				  showDeleteDialog();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   break;
		}
	}
	

}
