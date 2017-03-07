package com.boyiqove.receiver;

import java.util.Calendar;

import com.boyiqove.AppData;
import com.boyiqove.config.Config;
import com.boyiqove.service.BoyiService;
import com.boyiqove.util.CommonUtil;
import com.boyiqove.util.DebugLog;
//import com.boyireader.SplashActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

/*
 * 1. 请求更新用户信息 
 * 2.
 */
public class BoyiReceiver extends BroadcastReceiver {
	private final static String TAG = "BoyiReceiver";
    
    private final static long TIME_REQUEST_INTERVAL = 1000*60*30; 			// 请求时间间隔30min

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        if(hour < 9 || hour > 21) {
        	return;
        }
		
        //DebugLog.d(TAG, "service(" + BoyiService.BOYISERVICE_NAME + ") is running");

		if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) { // 用户解锁屏幕
			DebugLog.d(TAG, "屏幕解锁");
            updateInfo(context);

		} else { // 网络状态变化
			ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (wifiState != null && mobileState != null  
					&& State.CONNECTED != wifiState  
					&& State.CONNECTED == mobileState) {  
				DebugLog.d(TAG, "手机网络连接成功");

                updateInfo(context);

			} else if (wifiState != null && State.CONNECTED == wifiState) {  
				DebugLog.d(TAG, "无线网络连接成功");

                updateInfo(context);

			} else if (wifiState != null && mobileState != null  
					&& State.CONNECTED != wifiState  
					&& State.CONNECTED != mobileState) {  
				DebugLog.d(TAG, "手机没有任何的网络  ");

			}  
		}

	}
    
	
	private void updateInfo(Context context) {
//		long cur = System.currentTimeMillis();
//		
//
//		if( CommonUtil.isServiceRunning(context, BoyiService.BOYISERVICE_NAME)) {
//			// 1.请求更新用户信息 
//			//long interval = cur - AppData.getUser().getLastUserInfoTime();
//			long interval = cur - AppData.getUser().getLastMessageTime();
//			if(interval >= TIME_REQUEST_INTERVAL) {
//				DebugLog.d(TAG, "send MSG_UPDATE_USERINFO");
//				AppData.getClient().sendProxyMsg(BoyiService.MSG_REQUEST_INFO);
//			}
//
//		} else {
//			SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
//            long lastNotify = sp.getLong(Config.KEY_LAST_NOTIFY, 0);;
//			long interval = cur - lastNotify;
//            if(interval >= TIME_REQUEST_INTERVAL) {
//            	long lastOpen = sp.getLong(Config.KEY_LAST_OPEN, 0);
//            	long time = System.currentTimeMillis() - lastOpen;
//            	long max = 1000 * 60 * 60 * 24 * 3;
//            	//long max = 1000 * 10;
//            	if(time > max) {
//            		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//            		Notification nf =new Notification();
//            		nf.icon = R.drawable.ic_launcher;
//            		nf.tickerText = "来自易阅读";
//            		nf.defaults = Notification.DEFAULT_SOUND;
//            		nf.flags |= Notification.FLAG_AUTO_CANCEL;
//
//            		Intent intent = new Intent(context, SplashActivity.class);
//            		PendingIntent pd = PendingIntent.getActivity(context, 0, intent, 0);
//            		nf.setLatestEventInfo(context, "易阅读", "您已有3天没登陆了", pd);
//
//            		nm.notify(1, nf);
//            		DebugLog.d(TAG, "send notify open recommand");
//
//            		Editor editor = sp.edit();
//            		editor.putLong(Config.KEY_LAST_NOTIFY, System.currentTimeMillis());
//            		editor.commit();
//            	}
//            }
//            
//		}
	}

	
    
}
