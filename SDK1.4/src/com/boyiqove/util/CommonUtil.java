package com.boyiqove.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

public class CommonUtil {

	public static String md5Pwd(String password) {
		String pwd = "www.xiang5.com" + password;
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(pwd.getBytes("UTF-8"));

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);

		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);

		for (byte b : hash) {
			if ((b & 0xFF) < 0x10) hex.append("0");

			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();
	}
    
	public static String getLocalMacAddress(Context context) {  
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info = wifi.getConnectionInfo();  
        if(null == info) {
        	return "";
        } else {
        	return info.getMacAddress();  
        }
    }  
    
	/*
	 * 获取两个日期之间相差的天数
	 */
    public static long getIntervalDays(long from, long to) {
    	return (from - to)/(1000*60*60*24);
    }
    
    
    /*
	 * 安装apk
	 * @param file 要安装的apk目录
	 */
	public static void install(Context context, File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
	}   
    
    
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
    
	public static boolean isServiceRunning(Context context, String name) {
		if (TextUtils.isEmpty(name)) {
			 return false;
		}
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (name.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	public static Bitmap getBitmapFromAsset(Context context,String photoName)
	{
		AssetManager am = null;
		am = context.getAssets();
		InputStream is = null;
		Bitmap newBitmap = null;
		try {
			is = am.open(photoName);
			newBitmap = BitmapTool.decodeZoomBitmap(is, 180, 240);
            
		} catch (Exception e) {
			e.printStackTrace();
 
			System.gc();
			System.gc();

			newBitmap = BitmapTool.decodeZoomBitmap(is, 180, 240);
		} finally {
			if (is != null)
				try {
					is.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return newBitmap;
	}
	public static boolean isNetworkConnected(Context context) {  
	    if (context != null) {  
	          ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                 .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	         if (mNetworkInfo != null) {  
	             return mNetworkInfo.isAvailable();  
	         }  
	     }  
	     return false;  
	 }  
}
