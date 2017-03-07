package com.boyiqove.ui.storeadapter;

import java.util.List;
import java.util.Map;

import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.library.volley.toolbox.NetworkImageView;
import com.boyiqove.R;
import com.bytetech1.sdk.data.CommentItem;
import com.bytetech1.sdk.data.DirectoryItem;
import com.bytetech1.sdk.data.cmread.SearchItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContentAdapter extends BaseAdapter {


	private Context context;
	
	private List<CommentItem> list;
	private List<OnlineChapterInfo>mItems;
	private Boolean isSelector;  // 目录为真，
	
	private NetworkImageView  itemImage;
	private List<SearchItem> searchList;
	
	
	public ContentAdapter(Context context,List<OnlineChapterInfo>mItem, Boolean isSelector) {
		super();
		this.mItems = mItem;
		this.context = context;
		this.isSelector=isSelector;
	
	}

	
	public ContentAdapter(List<CommentItem> list, Context context,Boolean isSelector) {
		super();
		this.list = list;
		this.context = context;
		this.isSelector = isSelector;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (isSelector) {			
			return mItems == null ? 0 : mItems.size();
		}else {
			return list == null ? 0 : list.size();
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
		if (! isSelector) {
			Viewhelder helderPl;
			if (convertView == null) {
				helderPl=new Viewhelder();
				convertView=LayoutInflater.from(context).inflate(R.layout.boyi_comment_item, null);
				helderPl.actorName=(TextView) convertView.findViewById(R.id.actor_nc);
				helderPl.contentTv=(TextView) convertView.findViewById(R.id.content_pl);
				helderPl.data=(TextView) convertView.findViewById(R.id.content_data);
//				helderPl.data=(TextView) convertView.findViewById(R.id.actor_data);
				
				convertView.setTag(helderPl);
			}else {
				helderPl=(Viewhelder) convertView.getTag();
			}
			if (list.get(position).date !=null) {
				
//			helderPl.data.setText(list.get(position).date);
			}
			helderPl.actorName.setText(list.get(position).author);
			helderPl.contentTv.setText(list.get(position).content);
			helderPl.data.setText(list.get(position).date);
//			list.get(position).
			
		}else {
			Viewhelder helderMl;
			if (convertView == null) {
				helderMl=new Viewhelder();
				convertView=LayoutInflater.from(context).inflate(R.layout.boyi_directory_item, null);
				helderMl.actorName=(TextView) convertView.findViewById(R.id.direction_tv);
				helderMl.actorName.setVisibility(View.VISIBLE);
				helderMl.direName=(TextView) convertView.findViewById(R.id.direction_tv2);
				helderMl.contentTv=(TextView) convertView.findViewById(R.id.toll_tv);
				
				convertView.setTag(helderMl);
			}else {
				helderMl=(Viewhelder) convertView.getTag();
			}
			String str=mItems.get(position).name;
			String[] strArr = str.split("\\ ");

			if (strArr.length > 1) {
				helderMl.actorName.setText("【" + strArr[0] + "】");
				helderMl.direName.setVisibility(View.VISIBLE);
				helderMl.direName.setText(strArr[1]);
			}else {
				
				helderMl.actorName.setText("【"+strArr[0]+"】");
			}


//			if (strArr.length>1) {
//				
//				helderMl.actorName.setText("【"+strArr[0]+"】"+strArr[1]);
//			}else {
//				helderMl.actorName.setText("【"+strArr[0]+"】");
//			}

			
			if (mItems.get(position).type==0) {
				helderMl.contentTv.setText("免费");
				
			}else {
				helderMl.contentTv.setText("收费");
			}
		}
		
		return convertView;
	}
	class Viewhelder{
		TextView   actorName ;
		TextView   direName ;
		TextView	contentTv ;
		TextView   data;
	}
}
