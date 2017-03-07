package com.boyiqove.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.boyiqove.AppData;
import com.boyiqove.config.Config;
import com.boyiqove.entity.BookItem;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.protocol.JsonObjectPostRequest;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.ui.bookshelf.OnlineReadingActivity;

public class GetBookDetailUtil {
	public static String cmChannel="M2040002";
	public GetBookDetailUtil() {
		super();
	}
	
	public  static BookItem getNetBookItem(Context context,String bid){
		
		BookItem item=new BookItem();
		// 	检查映射表，有映射就取xn映射详情
		SharedPreferences sp = context.getSharedPreferences("bidMapTable",
				Application.MODE_PRIVATE);
		String xnBid=sp.getString(bid, "");
		if (! TextUtils.isEmpty(xnBid)) {
			String detailUrl = AppData.getConfig().getUrl(
					Config.URL_DETAIL_BOOKITEM);
			String mChannel=AppData.readMetaDataFromService(context, "channel_num");
			Map<String, String> params = new HashMap<String, String>();  
			params.put("aid", xnBid);  
			params.put("qdid", mChannel); 
			String body=HttpRequestUtil.post(detailUrl, params);
			try {
			JSONObject response=new JSONObject(body);
				int status = response
						.getInt("status");
				if (status == StatusCode.OK) {
					JSONObject jsonObject=response.getJSONObject("data");
					
						item.bid = jsonObject.getString("bid");
						item.cid = jsonObject.getString("last_cid");
						item.name = jsonObject.getString("name");
						item.author = jsonObject.getString("author");
						item.status = 1;
						item.wordNum = jsonObject.getString("word_num");
						item.shortDesc = jsonObject.getString("introduction");
						item.longDesc = jsonObject.getString("long_introduction");
						item.littleCoverUrl = jsonObject.getString("cover_url");
						item.bigCoverUrl = jsonObject.getString("cover_url");
						item.classFication = jsonObject.getString("class_name");
						item.totalCount = jsonObject.getInt("totalnum");
						item.lastCid=jsonObject.getString("last_cid");
						item.lastTitle=jsonObject.getString("last_name");	
						
						item.clickStr = jsonObject.getString("word_num");
						item.freeCount = 20;
						item.lastUpdata = 855664441+"";
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch
				// block
				e.printStackTrace();
			}
			
		}else {
			
			String detailUrl = AppData.getConfig().getUrl(
					Config.URL_DETAIL_CMBOOK);
			Map<String, String> params = new HashMap<String, String>();  
			params.put("bid", bid);  
			params.put("vt", "9"); 
			
			//	1.判读xml中的渠道，
			getCmChannel(context);
			
			params.put("cm", cmChannel); 
			String body=HttpRequestUtil.post(detailUrl, params);
			if (body!=null) {
			//	2.解析body
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(body);
				item.bid = bid;
				item.cid = jsonObject.getString("firstChpaterCid");
				item.name = jsonObject.getString("showName");
				item.author = jsonObject.getString("author");
				item.status = jsonObject.getInt("status");
				item.wordNum = jsonObject.getString("wordSize");
				item.shortDesc = jsonObject.getString("desc");
				item.longDesc = jsonObject.getString("longDesc");
				item.littleCoverUrl = "http://wap.cmread.com"+jsonObject.getString("smallCoverLogo");
				item.bigCoverUrl = "http://wap.cmread.com"+jsonObject.getString("bigCoverLogo");
				item.classFication = jsonObject.getString("category");
				item.clickStr = jsonObject.getString("clickValue");
				item.freeCount = jsonObject.getInt("freeChapterCount");
				item.totalCount = jsonObject.getInt("chapterSize");
				item.lastUpdata = jsonObject.getString("lastChapterUpdateTime");
				item.lastCid=jsonObject.getString("lastChapterCid");
				item.lastTitle=jsonObject.getString("lastChapterName");	
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		return item;
	}
	private static String imageUrl;
	public static void startReadingBook(final String bid,final String url,final Context context,final Boolean isBanner,final int num){
		final BookItem item=new BookItem();
		imageUrl=url;
//		try {
//			if (! TextUtils.isEmpty(imageUrl)) {
//				imageUrl=URLEncoder.encode(imageUrl, "utf_8");
//			}
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		if (AppData.getDataHelper().foundBookBid(bid)) {
			
			BookItem bditem=AppData.getDataHelper().getBookItem(Integer.parseInt(bid));
			if (! TextUtils.isEmpty(url)&&!url.equals("null")) {
				
				AppData.getDataHelper().updateImageUrl(Integer.parseInt(bid), imageUrl);
			}
			Intent intent = new Intent(context,
					OnlineReadingActivity.class);
			intent.putExtra("BookItem", bditem);
			context.startActivity(intent);
		}else {	
			String detailUrl = AppData.getConfig().getUrl(
					Config.URL_DETAIL_CMBOOK);
			Map<String, String> params = new HashMap<String, String>();  
			params.put("bid", bid);  
			params.put("vt", "9"); 
			
			//	1.判读xml中的渠道，
			getCmChannel(context);
			
			params.put("cm", cmChannel); 
			AppData.getRequestQueue().add(
					new JsonObjectPostRequest(detailUrl,
							new Listener<JSONObject>() {

								@Override
								public void onResponse(
										JSONObject response) {
									// TODO Auto-generated method
									try {
										item.bid = bid;
										item.cid = response.getString("firstChpaterCid");
										item.name = response.getString("showName");
										item.author = response.getString("author");
										item.status = response.getInt("status");
										item.wordNum = response.getString("wordSize");
										item.shortDesc = response.getString("desc");
										item.longDesc = response.getString("longDesc");
										item.littleCoverUrl = "http://wap.cmread.com"+response.getString("smallCoverLogo");
										if (! TextUtils.isEmpty(url)&&!url.equals("null")) {
										item.bigCoverUrl = imageUrl;
										}else {
											
											item.bigCoverUrl = "http://wap.cmread.com"+response.getString("bigCoverLogo");
										}
										item.classFication = response.getString("category");
										item.clickStr = response.getString("clickValue");
										item.freeCount = response.getInt("freeChapterCount");
										item.totalCount = response.getInt("chapterSize");
										item.lastUpdata = response.getString("lastChapterUpdateTime");
										item.lastCid=response.getString("lastChapterCid");
										item.lastTitle=response.getString("lastChapterName");	
										
										Intent intent = new Intent(context,
												OnlineReadingActivity.class);
										intent.putExtra("BookItem", item);
										intent.putExtra("isBanner", isBanner);
										intent.putExtra("buynum", num);
										context.startActivity(intent);
										
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}, new ErrorListener() {

								@Override
								public void onErrorResponse(
										VolleyError error) {
									// TODO Auto-generated method
									// stub
								}
							}, params));
		}
	}
	/**
	 * 存cm渠道号
	 * 
	 * */
	public static void putCmChannel(Context context,String cmChannel){
		SharedPreferences sharedPref =context.getSharedPreferences("CMchannel",
				Application.MODE_PRIVATE);
		sharedPref.edit().putString("cmChannel", cmChannel).commit();
		
	}
	public static String getCmChannel(Context context){
		SharedPreferences sharedPref =context.getSharedPreferences("CMchannel",
				Application.MODE_PRIVATE);
		cmChannel=sharedPref.getString("cmChannel", cmChannel);
		return cmChannel;
		
	}
}
