package com.xn.xiaoyan.receiver;

import java.util.Calendar;
import java.util.Timer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.preference.PreferenceManager;

import com.boyiqove.AppData;
import com.boyiqove.ui.bookshelf.BookshelfUtil;
import com.boyiqove.ui.bookstore.ShowNotificationInterface;
import com.boyiqove.util.DebugLog;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xiaoyan.util.UIUtils;
import com.xn.xiaoyan.services.XyServices;



/*
 * 1. 请求更新用户信息 
 * 2.
 */
public class XyUpdateReceiver extends BroadcastReceiver {
	private final static String TAG = "XyUpdateReceiver";
	private Context mContext;
	private SharedPreferences sp;
	private Editor editor;
	private final static long TIME_REQUEST_INTERVAL = 1000 * 60 * 30;//请求时间，隔30分钟
	private final static long BOOK_UPDATE=3*60*60*1000;
    private final static long VERSION_UPDATE=24*60*60*1000;
    private final static long NO_LOGIN=3*24*60*60*1000;
    private long lastTime=0;
    private long currentTime;
	@Override
	public void onReceive(Context context, Intent intent) {
		 mContext=context;
		 Calendar now = Calendar.getInstance();
	        int hour = now.get(Calendar.HOUR_OF_DAY);
	        if(hour < 9 || hour > 21) {
	        	return;
	        }
	        if(!AppData.isIsOpenLast())
	        {
	        	return;
	        }
			sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
			editor=sp.edit();
	        
			if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
				checkUpdate();
			} else { // 网络状态变化
				ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
				State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
				State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
				if (wifiState != null && mobileState != null  
						&& State.CONNECTED != wifiState  
						&& State.CONNECTED == mobileState) {  
					checkUpdate();

				}
				//无线网络连接成功
				else if (wifiState != null && State.CONNECTED == wifiState) {  

					checkUpdate();
				}
				//手机没有任何网络
				else if (wifiState != null && mobileState != null  
						&& State.CONNECTED != wifiState  
						&& State.CONNECTED != mobileState) {  

				}  
			}

		}
	
	public void checkUpdate()
	{
		 
		currentTime=System.currentTimeMillis();
		lastTime=sp.getLong("lastTime",0);
		DebugLog.e("lastTime",lastTime+"");
		if(lastTime>0 && currentTime-lastTime>=BOOK_UPDATE)
		{
			BookshelfUtil.update();
			if(currentTime-lastTime>=VERSION_UPDATE){
				if(!AppData.isAutoUpdate())
				{
					UmengUpdateAgent.setUpdateAutoPopup(false);
					UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
						
						@Override
						public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
							 switch (updateStatus) {
						        case UpdateStatus.Yes: // has update
						        	String text="有新版本啦，快去查看更新吧。。。";
						        	UIUtils.showNotification(text,mContext,UIUtils.NOTIFICATION_VERSION_UPDATE_ID);
						            break;
						        case UpdateStatus.No: // has no update
						            break;
						        case UpdateStatus.NoneWifi: // none wifi
						            break;
						        case UpdateStatus.Timeout: // time out
						            break;
						        }
						}

						
					});
					UmengUpdateAgent.update(mContext);
				}
			}
			
		}
		lastTime=currentTime;
		editor.putLong("lastTime",lastTime);
		editor.commit();
//		//记住上次更新的时间
		
	
	}
	

	

}
