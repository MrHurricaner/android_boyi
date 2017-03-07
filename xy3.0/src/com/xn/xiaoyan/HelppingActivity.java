package com.xn.xiaoyan;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.boyiqove.util.DebugLog;
import com.boyiqove.view.BaseActivity;
import com.boyiqove.view.DashedLine;
import com.xn.xiaoyan.R;




public class HelppingActivity extends BaseActivity{
	private TextView title;
	private ImageView back;
	private ExpandableListView listView;
	private MyAdapter adapter;
	private List<String> group;
	private List<List<String>> child;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helping_center);
		initData();
		initView();
		
	}

	private void initData() {
		group = new ArrayList<String>();
		child = new ArrayList<List<String>>();
		addInfo(getResources().getString(R.string.help_center_text1),getResources().getString(R.string.help_center_msg1));
		addInfo(getResources().getString(R.string.help_center_text2),getResources().getString(R.string.help_center_msg2));
		addInfo(getResources().getString(R.string.help_center_text3),getResources().getString(R.string.help_center_msg3));
		addInfo(getResources().getString(R.string.help_center_text4),getResources().getString(R.string.help_center_msg4));
		addInfo(getResources().getString(R.string.help_center_text5),getResources().getString(R.string.help_center_msg5));
		addInfo(getResources().getString(R.string.help_center_text6),getResources().getString(R.string.help_center_msg6));
		addInfo(getResources().getString(R.string.help_center_text7),getResources().getString(R.string.help_center_msg7));
		addInfo(getResources().getString(R.string.help_center_text8),getResources().getString(R.string.help_center_msg8));
		addInfo(getResources().getString(R.string.help_center_text9),getResources().getString(R.string.help_center_msg9));
		addInfo(getResources().getString(R.string.help_center_text10),getResources().getString(R.string.help_center_msg10));
		addInfo(getResources().getString(R.string.help_center_text11),getResources().getString(R.string.help_center_msg11));
		addInfo(getResources().getString(R.string.help_center_text12),getResources().getString(R.string.help_center_msg12));
		
		
	}

	private void initView() {
		title=(TextView) findViewById(R.id.left_activity_title);
		title.setText(getResources().getString(R.string.help_center));
		back=(ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
		listView=(ExpandableListView) findViewById(R.id.expandableListView);
		adapter = new MyAdapter(this,group,child);
		listView.setGroupIndicator(null);
		listView.setDivider(null);
		listView.setAdapter(adapter);
		
		

		
		
		
	}
	/**
	 * 添加数据信息
	 * @param g
	 * @param c
	 */
	private void addInfo(String g,String c) {
		group.add(g);
		List<String> list = new ArrayList<String>();
		list.add(c);
		child.add(list);
	}
	class MyAdapter extends BaseExpandableListAdapter{

		private Context context;
		private List<String> group;
		private List<List<String>> child;

		public MyAdapter(Context context, List<String> group,
				List<List<String>> child) {
			this.context = context;
			this.group = group;
			this.child = child;
		}

		@Override
		public int getGroupCount() {
			return group.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return group.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return child.get(childPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}
		
		/**
		 * 显示：group
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.list, null);
				holder = new ViewHolder();
				holder.textView = (TextView) convertView
						.findViewById(R.id.textView);
				holder.imageView=(ImageView) convertView.findViewById(R.id.imageview_spinner);
				holder.dLine=(DashedLine) convertView.findViewById(R.id.dashline);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(group.get(groupPosition));
			if(isExpanded){
//				RotateAnimation rotateAnimation =new RotateAnimation(0f,180f,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);   
//                rotateAnimation.setDuration(500);
//                rotateAnimation.setFillAfter(true);
				holder.imageView.setBackgroundResource(R.drawable.boy_dirctory_spinner_up);
                //holder.imageView.startAnimation(rotateAnimation);
				holder.dLine.setVisibility(View.GONE);
            }else{
            	holder.imageView.setBackgroundResource(R.drawable.boy_dirctory_spinner);
            	holder.dLine.setVisibility(View.VISIBLE);
            }
			return convertView;

		}
		
		/**
		 * 显示：child
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.list_item, null);
				holder = new ViewHolder();
				holder.textView = (TextView) convertView
						.findViewById(R.id.textView);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.textView.setText(child.get(groupPosition).get(0));
			return convertView;
		}

		class ViewHolder {
			TextView textView;
			ImageView imageView;
			DashedLine dLine;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

}
}
