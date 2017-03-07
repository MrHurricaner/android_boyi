package com.xiaoyan.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.util.DebugLog;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xn.xiaoyan.AboutActivity;
import com.xn.xiaoyan.MainActivity;

public class UIUtils {
	
	
	public static int NOTIFICATION_BOOK_UPDATE_ID=1;
	public static int NOTIFICATION_VERSION_UPDATE_ID=2;
	/**
	 * ��ʾtoast
	 * @param act
	 * @param msg
	 */
	public static void showToast(final Activity act,final String msg){
		
		if("main".equals(Thread.currentThread().getName())){
			Toast.makeText(act, msg, 0).show();
		}else{
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(act, msg, 0).show();
				}
			});
		}
	}
	public static void startAnimation(ImageView imageView,Context context)
	{
        Animation operatingAnim = AnimationUtils.loadAnimation(context,
				R.anim.tip);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		imageView.startAnimation(operatingAnim);
	}
	public static void UmengUpdate(final Activity context,ImageView imageView)
	{
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				 switch (updateStatus) {
			        case UpdateStatus.Yes: // has update
			            UmengUpdateAgent.showUpdateDialog(context, updateInfo);
			            break;
			        case UpdateStatus.No: // has no update
			        	UIUtils.showToast(context,"您的软件已是最新版本");
			            break;
			        case UpdateStatus.NoneWifi: // none wifi
			        	UIUtils.showToast(context,"没有wifi连接， 只在wifi下更新");
			            break;
			        case UpdateStatus.Timeout: // time out
			        	UIUtils.showToast(context,"超时");
			            break;
			        }
			}
		});
		UmengUpdateAgent.update(context);
		if(imageView!=null)
		{
			imageView.clearAnimation();
			imageView.setVisibility(View.GONE);
		}
		
	}
	//弹出notification
	public static void showNotification(String text,Context mContext,int id)
    {
	
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager nm = (NotificationManager) mContext
					.getSystemService(ns);

			CharSequence tickerText = text;
			long when = System.currentTimeMillis();

			Notification notification = new Notification(R.drawable.boyi_logo,
					tickerText, when);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			AppData.setShowRecommend(false);
			Intent intent = new Intent(mContext,MainActivity.class);
			if(id==UIUtils.NOTIFICATION_VERSION_UPDATE_ID)
			{
			  intent.putExtra("isForceUpdate",true);
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pi = PendingIntent.getActivity(mContext,1, intent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			notification.setLatestEventInfo(mContext, "笑眼看书书籍更新", text, pi);
			nm.notify(id, notification);
			AppData.isFirst=false;
		}
	
   
	
}
