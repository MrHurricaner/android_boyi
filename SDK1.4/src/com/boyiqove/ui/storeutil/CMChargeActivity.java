package com.boyiqove.ui.storeutil;

import java.net.URLDecoder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boyiqove.config.DeviceInfo;
import com.boyiqove.ui.bookshelf.OnlineReadingActivity;
import com.boyiqove.util.DebugLog;
import com.boyiqove.view.MyWebView;
import com.boyiqove.R;
import com.boyiqove.ResultCode;
import com.bytetech1.sdk.*;;

public class CMChargeActivity extends Activity {

	private WebView webView;
	private ImageView backButton,search;
	private TextView tvTitle;

	//内容提供者
//		SmsContent content=null;
//	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boyi_cm_charge_view);
		RelativeLayout boyi_book=(RelativeLayout) findViewById(R.id.boyi_book);
		boyi_book.setVisibility(View.GONE);
//		receiver = new SmsReceiver();	
//		IntentFilter filter = new IntentFilter();
//		       filter.addAction(SMS_RECEIVED_ACTION);
//		       
//		 registerReceiver(receiver,filter);
		search=(ImageView) findViewById(R.id.search);
		search.setVisibility(View.GONE);
		tvTitle=(TextView) findViewById(R.id.search_top_title_tv);
		tvTitle.setText("充值");
		backButton=(ImageView) findViewById(R.id.search_back);
		backButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
		MyWebView myView = (MyWebView)this.findViewById(R.id.myWebView_cm_charge);
		webView = myView.getWebView();
//		webView = (WebView)this.findViewById(R.id.myWebView_cm_charge);
		
		Iqiyoo.syncCookies(this);
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
//		webView.setWebViewClient(new IqiyooWebViewClient());
		webView.loadUrl("http://wap.cmread.com/r/p/pay_sjcz.jsp?vt=3&ln=9762_120982__2_&dataSrcId=26886831&sqId=L4");
		webView.requestFocus();
		
		       
		webView.setWebViewClient(new WebViewClient(){
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//					view.loadUrl(url);
					if (url.startsWith("sms://")) {
					
						       
						int subStringIndex = url.indexOf("body=");
						int smsPortEnd = url.indexOf("?");
						if (-1 != subStringIndex && -1 != smsPortEnd) {
							String port = url.substring(6, smsPortEnd);
							String body = url.substring(subStringIndex + 5);		
//							URLDecoder.decode(body);
//							body = URLDecoder.decode(body);
							DeviceInfo.sendTextSms(CMChargeActivity.this,port,URLDecoder.decode(body));
//							Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO, Uri.parse("smsto:" + port));
//							mIntent.putExtra("sms_body", body);
//							startActivity(mIntent);
							
//							Intent intent=new Intent(CMChargeActivity.this, OnlineReadingActivity.class);
//							
//							setResult(ResultCode.JUMP_TO_POSITION, intent);
						}						
						return true;
						
						
					}else {
						view.loadUrl(url);
					}
					
					return false;
				};
			
		});
		}
	
//	class SmsContent extends ContentObserver {  
//		  
//	    private Cursor cursor = null;  
//	  
//	    public SmsContent(Handler handler) {  
//	        super(handler);  
//	    }  
//	  
//	    @Override  
//	    public void onChange(boolean selfChange) {  
//	  
//	        super.onChange(selfChange);  
//	      //读取收件箱中指定号码的短信  
//	        cursor = managedQuery(Uri.parse("content://sms/inbox"), new String[]{"_id", "address", "read", "body"},  
//	                " body like ? and read=?", new String[]{"%您已成功订购%", "0"}, "_id desc");//按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
//	        
//	        if (cursor != null && cursor.getCount() > 0) {  
//	            ContentValues values = new ContentValues();  
//	            values.put("read", "1");        //修改短信为已读模式  
//	            cursor.moveToNext();  
//	        //在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃  
//	        if(Build.VERSION.SDK_INT < 14) {  
//	            cursor.close();  
//	        }  
//	        CMChargeActivity.this.finish();
//	    }  
//	}  
//	}
	
//	
//	class SmsReceiver extends BroadcastReceiver {
////		public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
//		 @Override
//		 public void onReceive(Context context, Intent intent) {
//		  // TODO Auto-generated method stub
////		  int resultCode = getResultCode();
//			 
//		  if (intent.getAction().equals(SMS_RECEIVED_ACTION)){
//			  DebugLog.e("接收到了短信", "自定的action");
//			  
////			  // 取消广播
////			  unregisterReceiver(receiver);
////			  abortBroadcast();
//			  CMChargeActivity.this.finish();			  
//		  }else {
//			  DebugLog.e("接收到了短信", "非指定的action");
//			  
//			  CMChargeActivity.this.finish();
////			  abortBroadcast();
////			  unregisterReceiver(receiver);
//		}
//		  
//		  }
//		 }
		
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
//		unregisterReceiver(receiver);
		Intent intent=new Intent(CMChargeActivity.this, OnlineReadingActivity.class);
		
		setResult(ResultCode.CM_TO_CONGZHI, intent);
	}
	
	

}
