package com.boyiqove.ui.bookstore;

import java.util.List;

import android.R.integer;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.boyiqove.ui.storeadapter.SearchListAdapter;
import com.boyiqove.util.DebugLog;
import com.boyiqove.view.BaseActivity;
import com.boyiqove.R;
import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.data.BookItem;
import com.bytetech1.sdk.data.Detail;
import com.bytetech1.sdk.data.KeywordBooks;

public class KeyBookDetail extends BaseActivity {
	private  int keybook_result=2;
	private Detail detail;
	private ListView listView;
	private String bid;
	private String word;
	private List<BookItem>keyList;
	private SearchListAdapter adapter;
	private ImageView  backButton,search;
	private RelativeLayout menu;
	private int i=1;
	private TextView titleTv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boyi_keybook_search);
		top=(TextView) findViewById(R.id.top);
		backButton=(ImageView) findViewById(R.id.search_back);		
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
		menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					return;
				} else {
					showMenuPopupWindow();
				}
			}
		});
       search.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent(KeyBookDetail.this,LocalSearchActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.boyi_move_right_in, R.anim.boyi_move_right_out);
		}
	});
		titleTv=(TextView) findViewById(R.id.search_top_title_tv);
		
		
		initView();
		
	}
	private PopupWindow popupWindow;
	protected void showMenuPopupWindow() {
		if(popupWindow==null)
		{
			View v=getPopupWinodwView();
			popupWindow=new PopupWindow(v,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			popupWindow.setFocusable(true);
			popupWindow.setTouchable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
		}
		popupWindow.showAsDropDown(top);
	}
	private View view;
	private LinearLayout enterLayout,bookManager;
	//private RelativeLayout rl_bookstore;
	private TextView top;
	private View getPopupWinodwView() {
		// TODO Auto-generated method stub
		if(view==null)
		{
		  view=LayoutInflater.from(this).inflate(R.layout.bookshelf_menu3,null);
//		  rl_bookstore=(RelativeLayout) view.findViewById(R.id.rl_bookstore);
//		  rl_bookstore.setVisibility(View.GONE);
		  enterLayout=(LinearLayout) view.findViewById(R.id.enter_bookshelf1);
		  bookManager=(LinearLayout) view.findViewById(R.id.enter_user_center1);
		  view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow!= null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}

				return false;
			}
		});
		  enterLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入书架
				finish();
			}
		});
		}
		return view;
	}
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
			}
			
		};
	
	private void initView() {
		// TODO Auto-generated method stub
		Intent intent=getIntent();
		 bid=intent.getStringExtra("bid");
		word=intent.getStringExtra("keyword");
		titleTv.setText(word);
		
//		DebugLog.e("标签是"+word, "对应的书架的bid是"+bid)	;
		showProgress("", "加载中...");
//		detail = BookHelper.loadDetail(bid);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub	
								
				 detail = BookHelper.loadDetail(bid);
				 KeywordBooks keywordBooks = detail.loadKeywordBooks(word, i, 10);
				 if (keywordBooks == null) {
					 mHandler.post(new Runnable() {
						public void run() {
							hideProgress();
							showToast("没有找到相应书籍", Toast.LENGTH_LONG);
							Intent intent=new Intent(KeyBookDetail.this, BookDetail.class);
							setResult(keybook_result, intent);
							
							KeyBookDetail.this.finish();
						}
					});
					 
					return ;
				}
				 keyList = keywordBooks.getBookItem();
				 
				 Message message=new Message();
				 message.what=0;
				 mHandler.sendMessage(message);
				 mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						hideProgress();
						DebugLog.e("下载到"+keyList.get(0).name, keyList.size()+"");
						adapter=new SearchListAdapter(keyList,KeyBookDetail.this,false);
						listView.setAdapter(adapter);
//						adapter.notifyDataSetChanged();
					}
				})	;		 
				 
				 }}).start();
		
		
		listView=(ListView) findViewById(R.id.keybook_lv);
		adapter=new SearchListAdapter(keyList,this,false);	
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				BookItem item=keyList.get(position);
//				String bidString=item.bid;
				Intent intent=new Intent(KeyBookDetail.this, BookDetail.class);
				intent.putExtra("bid", item.bid);
				startActivity(intent);
			}
			
		});
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				// 当不滚动时  
		        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
		            // 判断是否滚动到底部  
		            if (view.getLastVisiblePosition() == view.getCount() - 1) {  
		                //加载更多功能的代码  
		            	i++;
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub	
								
								
								 detail = BookHelper.loadDetail(bid);
								 KeywordBooks keywordBooks = detail.loadKeywordBooks(word, i, 10);
								 
								 if (keywordBooks==null) {
									return;
								}
								 keyList .addAll(keywordBooks.getBookItem()) ;
								 
								 Message message=new Message();
								 message.what=0;
								 mHandler.sendMessage(message);
								 mHandler.post(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										hideProgress();
										DebugLog.e("下载到"+keyList.get(0).name, keyList.size()+"");
//										adapter=new SearchListAdapter(keyList,KeyBookDetail.this,false);
//										listView.setAdapter(adapter);
										adapter.notifyDataSetChanged();
									}
								})	;		 
								 
								 }}).start();
		            	
		            }  
		        }  
			}			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			
			}
		});
		
	}
	
	
	
}
