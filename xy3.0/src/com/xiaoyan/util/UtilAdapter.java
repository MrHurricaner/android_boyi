package com.xiaoyan.util;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.boyiqove.AppData;
import com.boyiqove.library.volley.toolbox.ImageLoader;
import com.boyiqove.library.volley.toolbox.ImageLoader.ImageCache;
import com.boyiqove.library.volley.toolbox.NetworkImageView;
import com.boyiqove.ui.bookqove.ImageCacheManager;
import com.bytetech1.sdk.data.CommentItem;
import com.bytetech1.sdk.data.cmread.SearchItem;
import com.xn.xiaoyan.R;
import com.xn.xiaoyan.user.GameItem;
import com.xn.xiaoyan.user.GameUserActivity.btOnclick;

public class UtilAdapter extends BaseAdapter {


	private Context context;
	
	private List<CommentItem> list;
	private List<GameItem>mItems;
	private Boolean isSelector;  // 书籍推荐位是真，
	
	private NetworkImageView  itemImage;
	private List<SearchItem> searchList;
	private ImageLoader imageLoader;
	private btOnclick onClickLister;
	public void setClickListerner (btOnclick click){
		onClickLister=click;
	}
	
	public UtilAdapter(Context context,List<GameItem>mItem, Boolean isSelector) {
		super();
		this.mItems = mItem;
		this.context = context;
		this.isSelector=isSelector;
		ImageCache	imageCache=ImageCacheManager.getInstance(context);
		if(imageLoader == null) {
			imageLoader = new ImageLoader(AppData.getRequestQueue(), imageCache);
		}
		
	}

	
	public UtilAdapter(List<CommentItem> list, Context context,Boolean isSelector) {
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
//			list.get(position).
			
		}else {
			Viewhelder helderMl;
			if (convertView == null) {
				helderMl=new Viewhelder();
				convertView=LayoutInflater.from(context).inflate(R.layout.user_gift_list_item, null);
				// 游戏名
				helderMl.bookName=(TextView) convertView.findViewById(R.id.user_geft_item_name);
				// 大小+下载量
				helderMl.actorName=(TextView) convertView.findViewById(R.id.user_acother);
				// 简介
				helderMl.contentTv=(TextView) convertView.findViewById(R.id.user_geft_item_Desc);
				// 图片
				helderMl.imageView=(NetworkImageView) convertView.findViewById(R.id.everyday_cover_book);
				//下载按钮
				helderMl.downButton=(Button) convertView.findViewById(R.id.user_game_bt);
				
				convertView.setTag(helderMl);

			}else {
				helderMl=(Viewhelder) convertView.getTag();
			}			
			helderMl.actorName.setText(mItems.get(position).apkSize+"M   "+mItems.get(position).downNum+"人下载");
			helderMl.imageView.setErrorImageResId(R.drawable.boyi_ic_cover_default);
			helderMl.imageView.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
//			helderMl.imageView.setImageUrl(mItems.get(position).showimage, imageLoader);
			helderMl.contentTv.setText(mItems.get(position).description);
			helderMl.bookName.setText(mItems.get(position).name);
			helderMl.downButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onClickLister.BtonClick();
				}
			});
		}
		
		return convertView;
	}
	class Viewhelder{
		TextView   actorName ;
		TextView   bookName ;
		TextView	contentTv ;
		TextView   data;
		NetworkImageView imageView;
		Button downButton;
	}
}
