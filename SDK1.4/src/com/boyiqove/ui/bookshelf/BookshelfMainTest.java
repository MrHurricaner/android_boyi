package com.boyiqove.ui.bookshelf;
import com.boyiqove.util.DebugLog;
import com.boyiqove.view.BaseFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BookshelfMainTest extends BaseFragment {
	
	private final static String TAG = "BookshelfMainTest";
	private BookshelfUtil bookshelfUtil;
	private View mRootView;
	private boolean isFirst;
	private boolean isSelected=false;
	
	private BookshelfUtil getBookshelfInstance(Activity activity,LayoutInflater inflater,ViewGroup container){
		if(bookshelfUtil == null){
			bookshelfUtil=new BookshelfUtil(getActivity(),  inflater, container);
		}
		return bookshelfUtil;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		DebugLog.d(TAG,"onCreateView");
		BookshelfUtil util = getBookshelfInstance(getActivity(),  inflater, container);
		mRootView=util.getCreaView();
		return mRootView;
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		DebugLog.d(TAG,"onActivityCreated");
	}



	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		DebugLog.d(TAG,"onAttach");
		
	}



	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		DebugLog.d(TAG, "onStart");
		bookshelfUtil.showNotice();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		bookshelfUtil.stopNotice();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DebugLog.d(TAG, "onDestory");
		bookshelfUtil.onDestroy();
//		AppData.getConfig().setIsGrid(mIsGrid);
	}	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		DebugLog.d(TAG,"onResume");
		bookshelfUtil.upDataShelf();
	}
	public  void setMode(boolean select)
	{
		this.isSelected=select;
		if(isSelected)
		{
			bookshelfUtil.chageTrim(1);
			return;
		}
		bookshelfUtil.chageTrim(0);
	}
	

}
