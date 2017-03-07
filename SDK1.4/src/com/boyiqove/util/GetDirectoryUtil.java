package com.boyiqove.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;

import com.boyiqove.AppData;
import com.boyiqove.config.Config;
import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.protocol.StatusCode;
import com.bytetech1.sdk.data.DirectoryItem;

/**
 * @author WindowY
 *  下载目录工具必须在子线程中执行
 */
public class GetDirectoryUtil {

	public GetDirectoryUtil() {
		super();
	}
	//获取基地目录
	public static List<OnlineChapterInfo> getDirectoryList(String bid,int startPos,int endPos){
		String detailUrl = AppData.getConfig().getUrl(
				Config.URL_BOOK_DIRECTORY);
		List<OnlineChapterInfo> list=new ArrayList<OnlineChapterInfo>();
		Map<String, String> params = new HashMap<String, String>();  
		params.put("aid", bid);  
		params.put("startnums", String.valueOf(startPos)); 
		params.put("endnums", String.valueOf(endPos)); 
		String body=HttpRequestUtil.post(detailUrl, params);
		//解析body
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(body);
			int status =jsonObject
					.getInt("status");
			if (status == StatusCode.OK) {
				JSONArray jsonArray=jsonObject.getJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {
					OnlineChapterInfo info = new OnlineChapterInfo();
					
					info.id = i;
					info.cid = jsonArray.getJSONObject(i).getString("cid");

					info.name = jsonArray.getJSONObject(i).getString("chapterName");					
					info.type = jsonArray.getJSONObject(i).getInt("feeType") ;
					list.add(info);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	//获取XN目录
	public static List<OnlineChapterInfo> getXNDirectoryList(String bid,int startPos){
		List<OnlineChapterInfo> list=new ArrayList<OnlineChapterInfo>();
		String detailUrl =AppData.getConfig().getUrl(Config.URL_XNCONTENTS)+bid+"/lastChapterID/"+startPos;
		String body=HttpRequestUtil.get(detailUrl);
		//解析body
		JSONObject responseJson;
		try {
		responseJson = new JSONObject(body);
		int status = responseJson.getInt("status");
		if (status == StatusCode.OK) {
			JSONArray array = responseJson.getJSONArray("data");
			if (array == null) {
				return list;
			} else {

				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					OnlineChapterInfo info = new OnlineChapterInfo();
					info.id = obj.getInt("order");
					info.name = obj.getString("title");
					
					list.add(info);
				}		
				}
			} 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
}
