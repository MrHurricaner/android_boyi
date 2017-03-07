package com.boyiqove.ui.storeutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boyiqove.entity.BookItem;



public class JsonUtil {
	
	private static List<Map<String, List<keyWordBean>>> beanList;
	
	public static List<String>  getJson(String result) {
		List<String>urlList=new ArrayList<String>();

		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				
				JSONObject jsonObject2 = jsonArray.getJSONObject(i);
				
				urlList.add(jsonObject2.getString("bigimages")+"/"+jsonObject2.getString("ydaid")+"?"
				+jsonObject2.getString("title")+"!"+jsonObject2.getString("totalviews"));
				
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return urlList;
	}

	
	public static List<BookItem>  getBookItemList(String result) {
		
		List<BookItem>bookList=new ArrayList<BookItem>();
		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject2 = jsonArray.getJSONObject(i);
				BookItem item= new BookItem();
				item.bid = jsonObject2.getString("ydaid");
				item.cid=jsonObject2.getString("firstcid");
				item.name=jsonObject2.getString("title");
				item.author = jsonObject2.getString("author");
//				item.status = jsonObject2.getInt("status");
				item.wordNum=jsonObject2.getString("wordtotal");
				item.shortDesc=jsonObject2.getString("sortdescription");
				item.longDesc=jsonObject2.getString("longdescription");
				item.littleCoverUrl=jsonObject2.getString("smallimages");
				item.bigCoverUrl=jsonObject2.getString("bigimages");
				item.classFication=jsonObject2.getString("firstcid");
				item.clickStr=jsonObject2.getString("totalviews");
				item.freeCount=jsonObject2.getInt("freechapternums");
				item.totalCount=jsonObject2.getInt("totalchapters");
				bookList.add(item);
				
			}
			return bookList;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bookList;
	}

	public static String getweb(String content) {
		String data = null;
		try {
			JSONObject jsonObject = new JSONObject(content);
			JSONObject jsonObject2 = jsonObject.getJSONObject("content");
			data = jsonObject2.optString("content");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static Map<String, List<String>> getTitle(String content,int num) {
		beanList=new ArrayList<Map<String , List<keyWordBean>>>();
		
		Map<String , List<String>> mMap=new HashMap<String , List<String>>();
		String data = null;
		JSONObject jsonObject3;
//		List<keyWordBean>beans = null;
		
		try {
			
			JSONObject jsonObject = new JSONObject(content);
			
			JSONArray jsonArray = jsonObject.getJSONArray("data");    // 总的 
			JSONObject jsonObject2=jsonArray.getJSONObject(num);   // 二级 选择书名还是作者还是关键字
			JSONArray jsonArray2=jsonObject2.getJSONArray("childs");  // 最后级 分类显示
									
			for (int i = 0; i < jsonArray2.length(); i++) {
				String name=jsonArray2.getJSONObject(i).getString("title");	
				JSONArray jsonArray3=jsonArray2.getJSONObject(i).getJSONArray("childs");
				List<String>titleList=new ArrayList<String>();
				for (int j = 0; j < jsonArray3.length(); j++) {
					jsonObject3=(JSONObject) jsonArray3.get(j);
					titleList.add(jsonObject3.getString("title"));
//					jsonObject3.get("title");
				}
//				List<keyWordBean>beanList2=JSON.parseArray(jsonArray2.getJSONObject(i).getString("childs"),  keyWordBean.class);
				mMap.put(name, titleList);
				
			}	
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mMap;
	}
	
	
}
