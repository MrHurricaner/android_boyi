package com.boyiqove.ui.storeadapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.boyiqove.library.volley.toolbox.ImageLoader;
import com.boyiqove.library.volley.toolbox.NetworkImageView;
import com.boyiqove.library.volley.toolbox.Volley;
import com.boyiqove.ui.bookqove.ImageCacheManager;
import com.boyiqove.util.DebugLog;
import com.boyiqove.AppData;
import com.boyiqove.R;

import com.bytetech1.sdk.data.BookItem;
import com.bytetech1.sdk.data.cmread.SearchItem;

import android.content.Context;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.TextView;

/**
 * 搜索以及关键字的adapter
 * */
public class SearchListAdapter extends BaseAdapter {

	private String imageUrl;
	private Context context;	
	private List<SearchItem> searchList;
	private Boolean isSelector;  // 搜索为真，
	private List<BookItem>keyList;
	
	private  ImageLoader imageLoader;
	public SearchListAdapter(Context context, 
			List<SearchItem> searchList,Boolean isSelector) {
		super();
		this.context = context;
		this.searchList = searchList;
		this.isSelector=isSelector;
		DebugLog.e("适配搜索结果长度为：", searchList.size()+"");
		imageLoader=new ImageLoader(AppData.getRequestQueue(), ImageCacheManager.getInstance(context));
//		imageLoader=context.getImageLoader();
	}
	

	public SearchListAdapter( List<BookItem> keyList,Context context,Boolean isSelector) {
		super();
		this.context = context;
		this.keyList = keyList;
		this.isSelector=isSelector;
		imageLoader=new ImageLoader(AppData.getRequestQueue(), ImageCacheManager.getInstance(context));
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (isSelector) {			
			return searchList == null ? 0 : searchList.size();
		}else {
			return keyList == null ? 0 : keyList.size();			
		}	
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (isSelector) {
			Viewhelder helderPl;
			
			if (convertView == null) {				
				helderPl=new Viewhelder();
				convertView=LayoutInflater.from(context).inflate(R.layout.boyi_search_list_item, null);
				helderPl.imageView=(NetworkImageView) convertView.findViewById(R.id.search_item_cover);
				helderPl.actorName=(TextView) convertView.findViewById(R.id.search_item_actor_tv);
				helderPl.bookName=(TextView) convertView.findViewById(R.id.search_item_bookname);
				helderPl.satus=(TextView) convertView.findViewById(R.id.search_item_book_state);
				helderPl.intor=(TextView) convertView.findViewById(R.id.search_item_intor);
//				helderPl.data=(TextView) convertView.findViewById(R.id.actor_data);				
				convertView.setTag(helderPl);
			}else {
				helderPl=(Viewhelder) convertView.getTag();
			}
//			imageLoader=new ImageLoader(Volley.newRequestQueue(context), ImageCacheManager.getInstance());
			
			helderPl.imageView.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
			helderPl.imageView.setErrorImageResId(R.drawable.boyi_ic_cover_default);
			helderPl.imageView.setImageUrl(searchList.get(position).coverUrl,imageLoader );
				
				helderPl.actorName.setText(searchList.get(position).author);
				helderPl.bookName.setText(searchList.get(position).name);
				if (searchList.get(position).status==0) {				
					helderPl.satus.setText("连载");
				}else {
					helderPl.satus.setText("完本");
				}				
				helderPl.intor.setText(searchList.get(position).introduction);
				
			}else {
				Viewhelder helderP2;
				DebugLog.e("下载到"+keyList.get(position).name, keyList.size()+"");
				if (convertView == null) {
					helderP2=new Viewhelder();
					convertView=LayoutInflater.from(context).inflate(R.layout.boyi_search_list_item, null);
					helderP2.imageView=(NetworkImageView) convertView.findViewById(R.id.search_item_cover);
					helderP2.actorName=(TextView) convertView.findViewById(R.id.search_item_actor_tv);
					helderP2.bookName=(TextView) convertView.findViewById(R.id.search_item_bookname);
					helderP2.satus=(TextView) convertView.findViewById(R.id.search_item_book_state);
					helderP2.intor=(TextView) convertView.findViewById(R.id.search_item_intor);
//					helderPl.data=(TextView) convertView.findViewById(R.id.actor_data);				
					convertView.setTag(helderP2);
				}else {
					helderP2=(Viewhelder) convertView.getTag();
				}
				
				BookItem item=keyList.get(position);
				DebugLog.e("每个条目的封面是", item.coverUrl+"");
				helderP2.imageView.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
				helderP2.imageView.setErrorImageResId(R.drawable.boyi_ic_cover_default);
				
				helderP2.imageView.setImageUrl(addUrl(item.coverUrl),imageLoader );
				helderP2.actorName.setText(item.author);
				helderP2.bookName.setText(item.name);
				if (item.status==0) {				
					helderP2.satus.setText("连载");
				}else {
					helderP2.satus.setText("完本");
				}				
				helderP2.intor.setText(item.introduction);
				
			}
		
		return convertView;
	}
	class Viewhelder{
		
		NetworkImageView   imageView;
		
		TextView   actorName ;
		TextView   bookName ;
		TextView	satus ;
		TextView   intor;
	}
	
private String  addUrl(String str ){
		
		String   xName=str.substring(str.lastIndexOf("/")+1);   
//		System.out.println("中文部分"+xName);
		String   filename=str.substring(0,str.length()-xName.length());
//		System.out.println("英文部分"+filename);
		try {
			URLEncoder.encode(str.substring(str.lastIndexOf("/")+1), "utf_8");
			imageUrl=filename+URLEncoder.encode(str.substring(str.lastIndexOf("/")+1), "utf_8");
			return imageUrl;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return imageUrl;
				
	}
}
