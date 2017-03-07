package com.xn.xiaoyan.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.boyiqove.ui.bookshelf.BookshelfUtil;
import com.boyiqove.ui.bookshelf.EnterGiftActivityInterface;
import com.boyiqove.view.BaseFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.xn.xiaoyan.AboutActivity;
import com.xn.xiaoyan.ConsumeActivity;
import com.xn.xiaoyan.HelppingActivity;
import com.xn.xiaoyan.R;
import com.xn.xiaoyan.SettingActivity;
import com.xn.xiaoyan.user.FreeReadActivity;
import com.xn.xiaoyan.user.GameUserActivity;
import com.xn.xiaoyan.user.GiftActivity;
import com.xn.xiaoyan.user.OpinionUserActivity;
import com.xn.xiaoyan.view.CircleLayout;
import com.xn.xiaoyan.view.CircleLayout.OnItemClickListener;
import com.xn.xiaoyan.view.CircleLayout.OnItemSelectedListener;



public class LeftSldingFragment extends BaseFragment implements OnItemSelectedListener, OnItemClickListener, OnClickListener{


	private View mRootView=null;

	private SlidingMenu menu;
	private RelativeLayout shelf_account,shelf_sign_in,shelf_feedback,shelf_integral_wall,shelf_import;

	private CircleLayout circleMenu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mRootView==null)
		{
			mRootView=inflater.inflate(R.layout.boyi_user_left,container,false);
			RelativeLayout layout=(RelativeLayout) mRootView.findViewById(R.id.shelf_sign_in);
			CircleLayout circleMenu=(CircleLayout) mRootView.findViewById(R.id.main_circle_layout);
			layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showToast("呵呵", Toast.LENGTH_SHORT);
				}
			});
			
			circleMenu.setOnItemSelectedListener(this);
			circleMenu.setOnItemClickListener(this);
		}
		initView(mRootView);
		return mRootView;
	}

	public LeftSldingFragment(SlidingMenu menu) {
		this.menu=menu;
	}
	public LeftSldingFragment() {
		super();
	}
	private void initView(View mRootView) {
		shelf_account=(RelativeLayout) mRootView.findViewById(R.id.shelf_account);
		shelf_sign_in=(RelativeLayout) mRootView.findViewById(R.id.shelf_sign_in);
		shelf_feedback=(RelativeLayout) mRootView.findViewById(R.id.shelf_feedback);
		shelf_integral_wall=(RelativeLayout) mRootView.findViewById(R.id.shelf_integral_wall);
		shelf_import=(RelativeLayout) mRootView.findViewById(R.id.shelf_import);
		shelf_account.setOnClickListener(this);
		shelf_sign_in.setOnClickListener(this);
		shelf_feedback.setOnClickListener(this);
		shelf_integral_wall.setOnClickListener(this);
		shelf_import.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{ 
		case R.id.shelf_account:
			//系统设置
			
			Intent intent=new Intent(getActivity(),SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.shelf_sign_in:
			Intent intent1=new Intent(getActivity(),HelppingActivity.class);
			startActivity(intent1);
			//帮助中心
			break;
		case R.id.shelf_feedback:
			Intent intent2=new Intent(getActivity(),ConsumeActivity.class);
			startActivity(intent2);
			//消费说明
			break;
		case R.id.shelf_integral_wall:
			Intent intent3=new Intent(getActivity(),AboutActivity.class);
			startActivity(intent3);
			//关于我们
			break;
		case R.id.shelf_import:
			//退出软件
			getActivity().onBackPressed();
			break;
		
		
		}
		
	
	}

	@Override
	public void onItemClick(View view, int position, long id, String name) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			break;
		case 1:
			Intent gameIntent=new Intent(getActivity(), GameUserActivity.class);
			getActivity().startActivity(gameIntent);
			break;
		case 2:
            Intent intent4=new Intent(getActivity(),GiftActivity.class);
			startActivity(intent4);
			break;
		case 3:
			Intent intent=new Intent(getActivity(), OpinionUserActivity.class);
			getActivity().startActivity(intent);
			break;
		case 4:
			Intent intent3=new Intent(getActivity(),FreeReadActivity.class);
			startActivity(intent3);
			break;

		default:
			break;
		}
	}
	@Override
	public void onItemSelected(View view, int position, long id, String name) {
		// TODO Auto-generated method stub
//		switch (position) {
//		case 0:
//			showToast("点击了游戏", Toast.LENGTH_SHORT);
//			break;
//		case 1:
//			showToast("点击了礼品", Toast.LENGTH_SHORT);
//			break;
//		case 2:
//			showToast("点击了意见", Toast.LENGTH_SHORT);
//			break;
//		case 3:
//			showToast("点击了免费书", Toast.LENGTH_SHORT);
//			break;
//
//		default:
//			break;
//		}
		
	}
   
}
