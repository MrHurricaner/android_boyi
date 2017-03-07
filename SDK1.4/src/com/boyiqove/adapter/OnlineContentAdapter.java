package com.boyiqove.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boyiqove.R;
import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.entity.OnlineChapterInfo.Status;

public class OnlineContentAdapter extends BaseAdapter{
    
	private LayoutInflater inflater;
	private ArrayList<OnlineChapterInfo> list;
    private int selectPosition = -1;
    
    private boolean isAsec = true; // 升序

	public OnlineContentAdapter(Context context, ArrayList<OnlineChapterInfo> list, int selectPosition) {
		inflater = LayoutInflater.from(context);
        this.list = list;
        this.selectPosition = selectPosition;
	}
    
	public void sort(boolean isAsec) {
        if(this.isAsec == isAsec) {
        	return;
        }
        
		this.isAsec = isAsec;
        notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public OnlineChapterInfo getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(null == convertView) {
			convertView = inflater.inflate(R.layout.boyi_online_contents_item, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.name_tv);
			holder.status = (TextView)convertView.findViewById(R.id.status_tv);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();

		}

        int pos;
        if(isAsec) {
            pos = position;
        } else {
            pos = getCount() - 1 - position;
        }
		OnlineChapterInfo info = getItem(pos);
        
		holder.name.setText(info.name);
        
		if(selectPosition == pos) {
			holder.name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.boyi_ic_chapter_pos, 0, 0, 0);
			holder.name.setTextColor(Color.BLACK);
		} else {
			holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			holder.name.setTextColor(Color.GRAY);
		}

		if(info.status == Status.LOADED) {
			holder.status.setText("已缓存");
			holder.status.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		} else {
			holder.status.setText("");
		}

		if(info.type == OnlineChapterInfo.TYPE_FREE && info.status != Status.LOADED ) {
//			if (info.status == Status.LOADED) {				
//				holder.status.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//			}
			holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.free_book_pos, 0, 0, 0);
		} else if (info.type != OnlineChapterInfo.TYPE_FREE) {
		
			holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.boyi_ic_vip, 0, 0, 0);
		}
			


		return convertView;
	}

	private class ViewHolder {
		TextView name;
		TextView status;
	}

}
