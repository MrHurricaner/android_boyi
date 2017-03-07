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

import com.boyiqove.ui.storeadapter.ContentAdapter;
import com.boyiqove.ui.storeadapter.ScrollListView;
import com.boyiqove.ui.storeadapter.SearchListAdapter;
import com.boyiqove.util.DebugLog;
import com.boyiqove.view.BaseActivity;
import com.boyiqove.R;
import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.data.BookItem;
import com.bytetech1.sdk.data.Comment;
import com.bytetech1.sdk.data.CommentItem;
import com.bytetech1.sdk.data.Detail;
import com.bytetech1.sdk.data.KeywordBooks;

public class BookDetailpinglun extends BaseActivity {
	private Detail detail;
	private ListView listView;
	private String bid;
	private String word;
	private List<BookItem>keyList;
	private ImageView  backButton;
	private LinearLayout  backButton1;
	//,search;
	//private RelativeLayout menu;

	private int i=1;
	private TextView titleTv;
	private Comment comment;
	private List<CommentItem> list;
	private ContentAdapter adapterPl;
	private View v;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boyi_keybook_search);
		top=(TextView) findViewById(R.id.top);
//		menu=(RelativeLayout) findViewById(R.id.boyi_book);
//		search=(ImageView) findViewById(R.id.search);
		backButton=(ImageView) findViewById(R.id.search_back);		
//		Button layout=(Button) findViewById(R.id.boyi_book);
//		layout.setVisibility(View.GONE);
		backButton1=(LinearLayout) findViewById(R.id.boe_back_bt);		
		backButton1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
		titleTv=(TextView) findViewById(R.id.search_top_title_tv);
//       menu.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (popupWindow != null&&popupWindow.isShowing()) {
//					popupWindow.dismiss();
//					return;
//				} else {
//					showMenuPopupWindow();
//				}
//			}
//		});
//       search.setOnClickListener(new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			Intent intent=new Intent(BookDetailpinglun.this,LocalSearchActivity.class);
//			startActivity(intent);
//			overridePendingTransition(R.anim.boyi_move_right_in, R.anim.boyi_move_right_out);
//		}
//	});
		
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
		word=intent.getStringExtra("word");
		titleTv.setText(word);
		showProgress("", "加载中...");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub	
				comment =BookHelper.loadComment(bid,1);
				
				if(comment!=null){
					
					list=comment.getList();	
				    adapterPl=new ContentAdapter(list, BookDetailpinglun.this,false);
				    
				}
				 mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						hideProgress();
						listView.setAdapter(adapterPl);
//						adapter.notifyDataSetChanged();
					}
				})	;		 
				 
				 }}).start();
		
		
		listView=(ListView) findViewById(R.id.keybook_lv);	
		v=LayoutInflater.from(BookDetailpinglun.this).inflate(R.layout.boyi_listview_fooer,null);
		AbsListView.LayoutParams layoutParams= 
	            new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT); 
	     v.setLayoutParams(layoutParams); 

		v.setVisibility(View.GONE);
		listView.addFooterView(v, null, false);
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				// 当不滚动时  
		        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
		            // 判断是否滚动到底部  
		            if (view.getLastVisiblePosition() == view.getCount() - 1) {  
		            	if (list.size()>=10) {							
		            		v.setVisibility(View.VISIBLE);
						
		            	i++;
						new Thread(new Runnable() {							
							@Override
							public void run() {
								// TODO Auto-generated method stub	
								
								comment =BookHelper.loadComment(bid,i);
								List<CommentItem> list2=comment.getList();
								
//								list.clear();
								list .addAll(comment.getList()) ;
//								list=comment.getList();
								 
//								 Message message=new Message();
//								 message.what=0;
//								 mHandler.sendMessage(message);
								 mHandler.post(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										hideProgress();
										adapterPl.notifyDataSetChanged();
//										listView.removeFooterView(LayoutInflater.from(BookDetailpinglun.this).inflate(R.layout.listview_fooer, null));
										v.setVisibility(View.GONE);
									}
								})	;		 
								 
								}}).start();
		            }
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
