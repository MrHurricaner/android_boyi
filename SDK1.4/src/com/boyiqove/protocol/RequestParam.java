package com.boyiqove.protocol;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.boyiqove.AppData;
import com.boyiqove.config.DeviceInfo;
import com.boyiqove.util.CommonUtil;
import com.boyiqove.util.DebugLog;
import com.bytetech1.sdk.Iqiyoo;

/*
 * 书架首页相关接口请求
 */

public class RequestParam {
	private final static String TAG = "RequestParam";  
	/*
	 * 客户端更新参数
	 */
	public static Map<String, String> getUpdateParam() {
		   DeviceInfo info = AppData.getConfig().getDeviveInfo();
	        
			Map<String, String> map = new HashMap<String, String>();

			// 1. 设备类型
			map.put(DeviceInfo.KEY_TYPE, info.getType() + "");

            
			// 2. 客户端版本名
			map.put(DeviceInfo.KEY_VERSION_NAME, info.getVersionName());
            
            
			// 3.设备型号
			map.put(DeviceInfo.KEY_MODEL, info.getModel());
            
			// 4. 渠道编号
			map.put(DeviceInfo.KEY_CHANNEL, info.getChannel() + "");

			// 5.渠道编号
	        map.put(DeviceInfo.KEY_CHANNEL, info.getChannel() + "");
            
            // 6.客户端版本号
            map.put(DeviceInfo.KEY_VERSION_CODE, info.getVersionCode() + "");
	        
	        
			DebugLog.d(TAG, map.toString());

			return map;
	}

	/*
	 * 自动登陆参数
	 */
	public static Map<String, String> getAutoParam() {
        DeviceInfo info = AppData.getConfig().getDeviveInfo();
        
		Map<String, String> map = new HashMap<String, String>();
		// 1. 手机IMEI码
		map.put(DeviceInfo.KEY_IMEI, info.getImei());

		// 2. 设备类型
		map.put(DeviceInfo.KEY_TYPE, info.getType() + "");

		// 4. 渠道编号
		map.put(DeviceInfo.KEY_CHANNEL, info.getChannel() + "");

		// 5. 版本代码
		map.put(DeviceInfo.KEY_VERSION_NAME, info.getVersionName());

		// 6.设备型号
		map.put(DeviceInfo.KEY_MODEL, info.getModel());

		// 7.渠道编号
        map.put(DeviceInfo.KEY_CHANNEL, info.getChannel() + "");
		
		// 7.设备mac地址
		map.put(DeviceInfo.KEY_MAC, info.getMac());
		
		// 8.设备imsi
		map.put(DeviceInfo.KEY_IMSI, info.getImsi());
        
		DebugLog.d(TAG, map.toString());

		return map;
	}
	
	/*
	 * SDK用户注册参数
	 */
	public static Map<String, String> getSDKCountCreateParam() {
        DeviceInfo info = AppData.getConfig().getDeviveInfo();
        
		Map<String, String> map = new HashMap<String, String>();
		// 1. 手机IMEI码
		if (! TextUtils.isEmpty(info.getImei())) {			
			map.put(DeviceInfo.KEY_IMEI, info.getImei());
		}		
		// 2.设备imsi
		if (! TextUtils.isEmpty(info.getImsi())) {			
			map.put(DeviceInfo.KEY_IMSI, info.getImsi());
		}
		// 3.设备mac地址
		if (! TextUtils.isEmpty(info.getMac())) {			
			map.put(DeviceInfo.KEY_MAC, info.getMac());
		}	
		String mString=Iqiyoo. getPhoneNumber ();
		if (mString !=null) {
			// 4. 手机号
			map.put("mobile", Iqiyoo. getPhoneNumber ());
			
		}
		
//		// 4. 手机号
//		map.put(DeviceInfo.KEY_PHONE_NUM, info.getPhone_num());

//		// 5. 渠道编号
		if (! TextUtils.isEmpty(info.getMac())) {			
			map.put(DeviceInfo.KEY_BOOK_CHANNEL, info.getChannel());
		}
		map.put("version", AppData.versionName);
		DebugLog.d(TAG, map.toString());

		return map;
	}

	public static Map<String, String> getLoginParam(String username, String password) {
		Map<String, String> map = new HashMap<String, String>();

        DeviceInfo info = AppData.getConfig().getDeviveInfo();
        
		// 1.用户名
		map.put("username", username);

		// 2. 密码 md5
		map.put("password", CommonUtil.md5Pwd(password));

		// 3. 设备类型
		map.put(DeviceInfo.KEY_TYPE, info.getType() + "");

		// 5. 版本代码
		map.put(DeviceInfo.KEY_VERSION_NAME, info.getVersionName());

		// 6.设备型号
		map.put(DeviceInfo.KEY_MODEL, info.getModel());

		// 7.渠道编号
        map.put(DeviceInfo.KEY_CHANNEL, info.getChannel() + "");

		DebugLog.d(TAG, map.toString());

		return map;
	}

}
