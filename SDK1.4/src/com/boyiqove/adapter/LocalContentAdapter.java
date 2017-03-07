package com.boyiqove.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boyiqove.R;
import com.boyiqove.entity.LocalChapterInfo;

public class LocalContentAdapter extends BaseAdapter {
    
	private LayoutInflater inflater;
	private List<LocalChapterInfo> list;
    private int selectPosition;
    
    private boolean isAsec = true;

	public LocalContentAdapter(Context context, List<LocalChapterInfo> list, int selectPosition) {
		// TODO Auto-generated constructor stub
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
	public LocalChapterInfo getItem(int position) {
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
			convertView = inflater.inflate(R.layout.boyi_local_contents_item, null);
			holder = new ViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.title);
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
        
        LocalChapterInfo item = getItem(pos);
		holder.title.setText(item.name);
		
		if(selectPosition == pos) {
			holder.title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.boyi_ic_chapter_pos, 0, 0, 0);
			holder.title.setTextColor(Color.BLACK);
		} else {
			holder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			holder.title.setTextColor(Color.GRAY);
		}

		return convertView;
	}


	public void setData(List<LocalChapterInfo> list, int selectPosition) {
		this.list = list;
        this.selectPosition = selectPosition;
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView title;
	}
}