package com.xn.xiaoyan.user;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;


import com.boyiqove.AppData;
import com.boyiqove.config.Config;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.protocol.JsonObjectPostRequest;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.task.CallBackMsg;
import com.boyiqove.util.DebugLog;
import com.boyiqove.view.BaseActivity;
import com.xn.xiaoyan.R;

public class OpinionUserActivity extends BaseActivity{
	private LinearLayout backIv;
	private TextView titleTv;
	private LinearLayout opinLayout1,opinLayout2,opinLayout3,opinLayout4,opinLayout5,opinLayout6;
	private ImageView opinImage1,opinImage2,opinImage3,opinImage4,opinImage5,opinImage6;
	private Button  postBotton;
	private View mRootView;
	int[]errorArray;
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CallBackMsg.CLOSE_ERROR_LOGIN:
				dismissFreebackPop();
				break;

			default:
				break;
			}
		};
	};
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.opinion_user);
	initView();
	initData();
}

private void initData() {
	// TODO Auto-generated method stub
	errorArray=new int[]{ 0,0,0,0 };
	opinLayout1.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (errorArray[0]==0) {
				errorArray[0]=1;
				opinImage1.setBackgroundResource(R.drawable.boy_posterror_yes);
			}else {
				errorArray[0]=0;
				opinImage1.setBackgroundResource(R.drawable.boy_posterror_no);
			}
		}
	});
	opinLayout2.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (errorArray[1]==0) {
				errorArray[1]=1;
				opinImage2.setBackgroundResource(R.drawable.boy_posterror_yes);
			}else {
				errorArray[1]=0;
				opinImage2.setBackgroundResource(R.drawable.boy_posterror_no);
			}
		}
	});
	opinLayout3.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (errorArray[2]==0) {
				errorArray[2]=1;
				opinImage3.setBackgroundResource(R.drawable.boy_posterror_yes);
			}else {
				errorArray[2]=0;
				opinImage3.setBackgroundResource(R.drawable.boy_posterror_no);
			}
		}
	});
	opinLayout4.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (errorArray[3]==0) {
				errorArray[3]=1;
				opinImage4.setBackgroundResource(R.drawable.boy_posterror_yes);
			}else {
				errorArray[3]=0;
				opinImage4.setBackgroundResource(R.drawable.boy_posterror_no);
			}
		}
	});
	opinLayout5.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (errorArray[3]==0) {
				errorArray[3]=1;
				opinImage5.setBackgroundResource(R.drawable.boy_posterror_yes);
			}else {
				errorArray[3]=0;
				opinImage5.setBackgroundResource(R.drawable.boy_posterror_no);
			}
		}
	});
	opinLayout6.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (errorArray[3]==0) {
				errorArray[3]=1;
				opinImage6.setBackgroundResource(R.drawable.boy_posterror_yes);
			}else {
				errorArray[3]=0;
				opinImage6.setBackgroundResource(R.drawable.boy_posterror_no);
			}
		}
	});
	final EditText editText=(EditText) findViewById(R.id.suggest_content_et);	
	postBotton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String errorText=editText.getText().toString();
			StringBuffer  bufferError=new StringBuffer();
			for (int i = 0; i < errorArray.length; i++) {
				if (errorArray[i]==1) {
					bufferError.append(errorArray[i]+",");
				}
			}
			bufferError.append("$$##"+errorText);
			if (bufferError.length()<=4) {
				showToast("您为输入任何内容，请先输入您的意见。",Toast.LENGTH_SHORT);
			}else {
				postDownLoad("" ,"" ,bufferError.toString());
				showFreebackwidonw();
			}
		}
	});
	
}

private void initView() {
	// TODO Auto-generated method stub
	backIv=(LinearLayout) findViewById(R.id.boe_back_bt);		
	backIv.setOnClickListener(new OnClickListener() {		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
			overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
		}
	});
	titleTv=(TextView) findViewById(R.id.search_top_title_tv);
	titleTv.setText("提意见");
	mRootView=View.inflate(this,R.layout.opinion_user,null);
	// 意见选项
	opinLayout1=(LinearLayout) findViewById(R.id.opinion_layout_bar1);
	opinLayout2=(LinearLayout) findViewById(R.id.opinion_layout_bar2);
	opinLayout3=(LinearLayout) findViewById(R.id.opinion_layout_bar3);
	opinLayout4=(LinearLayout) findViewById(R.id.opinion_layout_bar4);
	opinLayout5=(LinearLayout) findViewById(R.id.opinion_layout_bar5);
	opinLayout6=(LinearLayout) findViewById(R.id.opinion_layout_bar6);
	// 
	opinImage1=(ImageView) findViewById(R.id.opinion_layout_iv1);
	opinImage2=(ImageView) findViewById(R.id.opinion_layout_iv2);
	opinImage3=(ImageView) findViewById(R.id.opinion_layout_iv3);
	opinImage4=(ImageView) findViewById(R.id.opinion_layout_iv4);
	opinImage5=(ImageView) findViewById(R.id.opinion_layout_iv5);
	opinImage6=(ImageView) findViewById(R.id.opinion_layout_iv6);
	
	postBotton=(Button) findViewById(R.id.user_opinion_post);
	
}

	/** 
	 * 	上传报错信息
	 */  
	public String type="0";	
	public  void postDownLoad(String bid ,String cid ,String advice){
	String url = AppData.getConfig().getUrl(Config.URL_POST_CONTENT_ERROR);
	
	Map<String, String> map = new HashMap<String, String>();
	map.put("uid", AppData.getUser().getID() + "");
	map.put("type", type);
	map.put("advice", advice);
	map.put("aid", bid);
	map.put("cid", cid);	
	getRequestQueue().add(
			new JsonObjectPostRequest(url,
					new Listener<JSONObject>() {
	
						@Override
						public void onResponse(
								JSONObject response) {
							// TODO Auto-generated method
							try {
								int status = response
										.getInt("status");
								DebugLog.e("status", status
										+ "");
								if (status == StatusCode.OK) {
									DebugLog.e("上传状态======", "上传意见成功");
									
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch
								e.printStackTrace();
							}
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(
								VolleyError error) {
							// TODO Auto-generated method
							DebugLog.e("上传状态======", "上传意见失败");
						}
					}, map));	
		}
	/**
	 *  报错反馈对话框
	 * */
	private PopupWindow freebackPop;
	private View freebackPw;
	private void dismissFreebackPop() {
		if (freebackPop != null && freebackPop.isShowing()) {
			freebackPop.dismiss();
			freebackPop = null;
		}
	}
	protected void showFreebackwidonw() {
	
		freebackPw = View.inflate(this, R.layout.boy_freeback_dialog, null);
		dismissFreebackPop();
		freebackPop = null;
		if (freebackPop == null) {
			freebackPop = new PopupWindow(freebackPw, LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			freebackPop.setFocusable(true);
			freebackPop.setTouchable(true);
			freebackPop.setOutsideTouchable(true);
			freebackPop.setBackgroundDrawable(new BitmapDrawable());
		}		
		TextView freeBackTv=(TextView) freebackPw.findViewById(R.id.error_freeback_tv);
		freeBackTv.setText("已提交，感谢您的宝贵意见");
		freebackPw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissFreebackPop();
			}
		});
		freebackPop.setOnDismissListener(new OnDismissListener() {
	
			@Override
			public void onDismiss() {
	
			}
		});
		
		freebackPop.showAtLocation(mRootView, Gravity.CENTER, 0, 0);
	
		handler.sendMessageDelayed(handler.obtainMessage(CallBackMsg.CLOSE_ERROR_LOGIN), 2000);
	}
}
