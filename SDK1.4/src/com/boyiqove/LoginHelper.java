package com.boyiqove;

import java.util.Vector;

import android.content.Context;
import android.text.TextUtils;

import com.boyiqove.config.DeviceInfo;
import com.boyiqove.task.CallBackTask;
import com.boyiqove.util.DebugLog;
import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.chapter.Chapter;
import com.bytetech1.sdk.chapter.ContentChapter;
import com.bytetech1.sdk.chapter.LoginChapter;
import com.bytetech1.sdk.chapter.OrderChapter;
import com.bytetech1.sdk.util.Http;

public class LoginHelper {

	public final static int LOGIN_ERROR_SENDSMS_FAIL = 0;
	public final static int LOGIN_ERROR_COMMEN = 1;
	public final static String LOGIN_TASK_NAME = "login_task_LoginHelper";
	
	private Vector<LoginCallback> listeners;
	private boolean isLoginning = false;
	private static LoginHelper instance = null;
	
	private LoginHelper(){
		listeners = new Vector<LoginCallback>();
	}
	
	public static LoginHelper getInstatnce(){
		if(instance == null){
			instance = new LoginHelper();
		}
		return instance;
	}
	
	public synchronized void startLogin(final Context context,final LoginChapter loginChapter,final int loginType,LoginCallback listener){
		addLoginCallback(listener);
		if(isLoginning){
			listener.loginStart();
			return;
		}
		AppData.getClient().getTaskManager().addTask(new CallBackTask(LOGIN_TASK_NAME){

			@Override
			protected void doTask() {
				// TODO Auto-generated method stub
				try {
					isLoginning = true;
					login(context,loginChapter,loginType);
				} catch (LoginCMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					broadcastLoginError(LOGIN_ERROR_COMMEN, e.getMessage());
				}
				isLoginning = false;
			}
			
		});
	}
	
	public synchronized void startLogin(final Context context,String bid,String cid,final int loginType,LoginCallback listener){
		addLoginCallback(listener);
		if(isLoginning){
			listener.loginStart();
			return;
		}
		AppData.getClient().getTaskManager().addTask(new CallBackTask(LOGIN_TASK_NAME){

			@Override
			protected void doTask() {
				// TODO Auto-generated method stub
				try {
					isLoginning = true;
					Chapter chapter = BookHelper.loadChapter("382312165", "382445549");
					if(chapter != null && chapter instanceof LoginChapter)
						login(context,(LoginChapter)chapter,loginType);
				} catch (LoginCMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					broadcastLoginError(LOGIN_ERROR_COMMEN, e.getMessage());
				}
				isLoginning = false;
			}
			
		});
	}
	
	public static interface LoginCallback{
		public void loginStart();
		public void loginSuccess(Chapter chapter);
		public void loginFail();
		public void LoginError(int type,String error);
	}
	
	private void addLoginCallback(LoginCallback listener){
		listeners.add(listener);
	}
	
	private void removeLoginCallback(LoginCallback listener){
		listeners.remove(listener);
	}
	
	private void broadcastLoginStart(){
		DebugLog.e("LoginHelper", "login start");
		for(LoginCallback callback:listeners){
			callback.loginStart();
		}
	}
	
	private void broadcastLoginSuccess(Chapter chapter){
		DebugLog.e("LoginHelper", "login success");
		Http.save();
		for(LoginCallback callback:listeners){
			callback.loginSuccess(chapter);
		}
		listeners.clear();
	}
	
	private void broadcastLoginError(int type,String error){
		DebugLog.e("LoginHelper", error);
		for(LoginCallback callback:listeners){
			callback.LoginError(type,error);
		}
		listeners.clear();
	}
	
	private void broadcastLoginFail(){
		DebugLog.e("LoginHelper", "login fail.");
		for(LoginCallback callback:listeners){
			callback.loginFail();
		}
		listeners.clear();
	}
	
	public boolean login(Context context,LoginChapter loginChapter,int loginType) throws LoginCMException{

		if(loginChapter == null || !(loginChapter instanceof LoginChapter) || loginType == -1){
			throw new LoginCMException("login params is wrong!");
		}

		// send sms
//		showProgressByHandler("",
//				"正在向移动运营商发送一条免费短信进行登录。本条短信免费发送，如有手机助手提示是否允许发送，请选择允许");
		broadcastLoginStart();

		String number = loginChapter.getLoginViaSmsNumber(loginType);
		String content = loginChapter.getLoginViaSmsContent(loginType);
		
		if (TextUtils.isEmpty(content)) {
			broadcastLoginError(LOGIN_ERROR_SENDSMS_FAIL,"text content is empty.");
			return false;
		}
		
		
		DeviceInfo.sendTextSms(context, number,
				content);					
		long loginTime = 25000;
//		long loginTime = 15000;
		long loginStartTime = System.currentTimeMillis();
		long lTime = System.currentTimeMillis();
		boolean isFirst = true;
		boolean isShow = false;
		boolean isSuccess = false;
		Chapter c = null;
		int count=0;
		while (lTime - loginStartTime <= loginTime) {
//			Chapter c = lc.loginViaSms(loginType);
			try {
				if (isFirst) {
					Thread.sleep(7000);
					isFirst = false;
				} else {
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				// TODO: handle exception
				broadcastLoginError(LOGIN_ERROR_COMMEN,"login task is interrupted.");
				return false;
			}
			
			try {
				c = loginChapter.loginViaSms(loginType);
			} catch (Exception e) {
				// TODO: handle exception
				broadcastLoginError(LOGIN_ERROR_COMMEN,"login task is interrupted.");
				continue;
			}
			if (c == null) {
				// wait 5 seconds,then query again
				DebugLog.e("注册结果为空", "5秒后重试");							
			} else if (c instanceof LoginChapter) {
				// login error
				DebugLog.e("注册结果返回：登录错误", "提示重试");
				broadcastLoginFail();
				return false;

			} else if (c instanceof OrderChapter) {
				// success
				
				DebugLog.e("注册结果返回：登录成功", "本章是未购买章节出现弹框");
				isSuccess = true;
				broadcastLoginSuccess(c);
				break;
				
			}else if (c instanceof ContentChapter) {
				// success
				DebugLog.e("注册结果返回：登录成功", "本章是已购买章节开始玩命加载中  内容");
				isSuccess = true;
				broadcastLoginSuccess(c);
				break;
			}
			lTime = System.currentTimeMillis();
		}
		if (!isSuccess) {
			// 手动登录
			// showToast("再次重试", Toast.LENGTH_LONG);
			broadcastLoginFail();
			return false;
		}
		else{
			return true;
		}
	}
}
