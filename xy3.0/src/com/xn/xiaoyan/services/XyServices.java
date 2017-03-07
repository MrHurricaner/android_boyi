package com.xn.xiaoyan.services;


import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.ui.bookshelf.BookshelfUtil;
import com.boyiqove.util.DebugLog;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xiaoyan.util.UIUtils;

public class XyServices extends Service{
	private long BOOK_UPDATE=3*60*60*1000;
    private long VERSION_UPDATE=24*60*60*1000;
    private long NO_LOGIN=3*24*60*60*1000;
    private Timer timer1,timer2;
    private TimerTask timeTask1,timeTask2,timeTask3;
    public final static String BOYISERVICE_NAME="com.xn.xiaoyan.services.XyServices"; 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//创建三个定时器
		timer1=new Timer();
		timer2=new Timer();
		timeTask1=new TimerTask() {
			
			@Override
			public void run() {
				//每隔3s钟，打印log
				BookshelfUtil.update();
				//ShowVersionNotification();
			}
		};
		
		timeTask2=new TimerTask() {
			
			@Override
			public void run() {
//				//版本更新
				if(!AppData.isAutoUpdate())
				{
					UmengUpdateAgent.setUpdateAutoPopup(false);
					UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
						
						@Override
						public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
							 switch (updateStatus) {
						        case UpdateStatus.Yes: // has update
						        	String text="有新版本啦，快去查看更新吧。。。";
						        	//有更新,弹notification
						            //UmengUpdateAgent.showUpdateDialog(getApplicationContext(), updateInfo);
						        	UIUtils.showNotification(text,XyServices.this,UIUtils.NOTIFICATION_VERSION_UPDATE_ID);
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
					UmengUpdateAgent.update(getApplicationContext());
				}
			}
		};
		if(AppData.isIsOpenLast())
		{
			timer1.schedule(timeTask1,0,BOOK_UPDATE);
			timer2.schedule(timeTask2,0,1000);
			
			
			
			
			
			
			
			
		}
		
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	// 启动Service
		public static void startBoyiService(Context context) {
			Intent intent = new Intent(BOYISERVICE_NAME);
			context.startService(intent);
		}
	// 停止Service
		public static void stopBoyiService(Context context) {
			Intent intent = new Intent(BOYISERVICE_NAME);
			context.stopService(intent);
		}

}
