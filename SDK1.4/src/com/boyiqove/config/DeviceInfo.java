package com.boyiqove.config;

import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.boyiqove.AppData;
import com.boyiqove.util.DebugLog;

public class DeviceInfo {
    
	public final static String KEY_TYPE = "client_type";
	public final static String KEY_VERSION_NAME = "version";
	public final static String KEY_VERSION_CODE = "version_code";
	public final static String KEY_VERSION_SYSTEM = "system_version";
	public final static String KEY_MODEL = "device_model";
	public final static String KEY_CHANNEL = "channel_num";
	public final static String KEY_IMEI = "imei";
	public final static String KEY_IMSI = "imsi";
	public final static String KEY_MAC = "mac";
    public final static String KEY_WIDTH = "width";
    public final static String KEY_HEIGHT = "height";
//	public final static String KEY_BOOK_CHANNEL = "book_channel_num";
	public final static String KEY_BOOK_CHANNEL = "channelid";
	public final static String KEY_PHONE_NUM = "phone_num";
	
	public final static int OPERATOR_CM = 0;
	public final static int OPERATOR_CU = 1;
	public final static int OPERATOR_TC = 2;
    
	private final int 	type = 1;					// 设备类型 Android:1
    
    private String 	versionName;			// 客户端版本名字
    private int 	versionCode;			// 客户端版本代号
    
    private String 	model;					// 设备型号
    private String	 	channel;				// 渠道编号
    private String 	systemVersion;			// 系统版本
    private String 	imei;					// 手机IMEI码
    private String 	imsi;					
    private String 	mac;
    private int 	width;
    private int 	height;
    private String sdk_version = "1.0.0";
    private String phone_num = "";
    private String book_channel = "";
    
	public String getSdk_version() {
		return sdk_version;
	}
	public void setSdk_version(String sdk_version) {
		this.sdk_version = sdk_version;
	}
	public DeviceInfo(Context context) {
		init(context);
	}   
	private void init(Context context) {

        PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			versionName = info.versionName;
			versionCode = info.versionCode;
            
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            versionName = "";
            versionCode = 0;
		}
        
        model = Build.MODEL;        
        channel = AppData.readMetaDataFromService(context,KEY_CHANNEL);
        
//        book_channel = readMetaDataFromService(context,KEY_BOOK_CHANNEL);
       // book_channel = "xiaoyan";
//        book_channel = "qiyou";
        
        WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
//            mac = info.getMacAddress();
//            ip = int2ip(info.getIpAddress());
            
            	for(int i=0;i<3;i++)
            	{
            		mac=info.getMacAddress();
            		 if(TextUtils.isEmpty(mac)||mac.equals("00:00:00:00:00:00"))
                     {
            			 continue;
                     }else {
						break;
					}
            	}

        }
        
        systemVersion = Build.VERSION.RELEASE; 
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		imei = tm.getDeviceId();
        imsi = tm.getSubscriberId();
        phone_num = tm.getLine1Number();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.widthPixels;
		
	}

	public String getBook_channel() {
		return book_channel;
	}
	public void setBook_channel(String book_channel) {
		this.book_channel = book_channel;
	}
	public String getPhone_num() {
		if(phone_num!=null){
			return phone_num;
		}
		else{
			return "";
		}
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}

	public int getType() {
		return type;
	}

	public String getVersionName() {
		return versionName;
	}

	public String getModel() {
		return model;
	}

	public String getChannel() {
		return channel;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public String getImei() {
		return imei;
	}
    
	public String getImsi() {
        return imsi;
	}
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
    	return height;
    }
    
    public String getMac() {
    	return mac;
    }
    

	public final static String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
	public final static String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
	public static void sendTextSms(Context context,String number, String content){
	    SmsManager smsManager = SmsManager.getDefault();
		List<String> divideContents = smsManager.divideMessage(content);
		Intent itSend = new Intent(SMS_SEND_ACTIOIN);
		Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);
		/* sentIntent参数为传送后接受的广播信息PendingIntent */
		PendingIntent mSendPI = PendingIntent.getBroadcast(context.getApplicationContext(), 0, itSend, 0);
		/* deliveryIntent参数为送达后接受的广播信息PendingIntent */
		PendingIntent mDeliverPI = PendingIntent.getBroadcast(context.getApplicationContext(), 0, itDeliver, 0);
	
		for (String text : divideContents) {
			try {
				smsManager.sendTextMessage(number, null, text, mSendPI, mDeliverPI);
			} catch (SecurityException se) {
				se.printStackTrace();
				return;
			}
		}
	}
    
    public static int getOperator(Context context){
    	TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		String operator = tm.getSimOperator();
		
		
		if(operator!=null){
			
				if(operator.equals("46000") || operator.equals("46002")|| operator.equals("46007")){
					//中国移动
					return OPERATOR_CM;
				}else if(operator.equals("46001")){
					//中国联通
					return OPERATOR_CU;
				}else if(operator.equals("46003")){
					//中国电信
					return OPERATOR_TC;
				}
		}
		else{
			if(imsi.startsWith("46000") || imsi.startsWith("46002")|| imsi.startsWith("46007"))
			{
				//因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号 //中国移动
				return OPERATOR_CM;
			}else if(imsi.startsWith("46001")){
				//中国联通
				return OPERATOR_CU;
			}else if(imsi.startsWith("46003")||imsi.startsWith("46011")){
				//中国电信
				return OPERATOR_TC;
			}
		}
		
		
		return -1;
    }	
}
