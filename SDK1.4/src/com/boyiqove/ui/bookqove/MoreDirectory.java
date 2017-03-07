package com.boyiqove.ui.bookqove;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boyiqove.R;
import com.boyiqove.adapter.OnlineContentAdapter;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.entity.PageID;
import com.boyiqove.ui.bookshelf.OnlineReadingActivity;
import com.boyiqove.ui.storeadapter.ContentAdapter;
import com.boyiqove.util.DebugLog;
import com.boyiqove.view.BaseActivity;
import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.data.Directory;
import com.bytetech1.sdk.data.DirectoryItem;

public class MoreDirectory extends BaseActivity {
	
	private ImageView backButton;
	private TextView  titleTop;
	private  ContentAdapter  adapterMl;
	private  String  bid;
	private ListView moreListView;
	private ListView  pageListView;
	private List<DirectoryItem>mItem;
	private PopupWindow pw;
	private int listPage;
	private List<String >pageList;
	private View view;
	private Button lastPage,nextPage;
	private TextView chosePage;
	private int newPage=1;
	private BookItem item;
	
	private Handler handler=new Handler(){
		
		public void handleMessage(Message msg) {
			
			
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.boyi_direction_all);
		initView();
		initPage();
	}


	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.boyi_book).setVisibility(View.GONE);
		TextView textView=(TextView) findViewById(R.id.search_top_title_tv);
		textView.setText("书籍目录");
		bid =getIntent().getStringExtra("bid");
		item=(BookItem) getIntent().getSerializableExtra("bookItem");
		backButton=(ImageView) findViewById(R.id.search_back);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
		
		lastPage=(Button) findViewById(R.id.dir_lastpage);
		lastPage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				int  num=Integer.parseInt(chosePage.getText().toString().substring(1, chosePage.length()-1));
				int num=newPage;
				if (num>1) {
					newPage--;
					chosePage.setText("第"+newPage+"页");
					final int lastPage=num-1;
					showProgress("", "目录加载中...");
					new Thread(new Runnable() {					
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							Directory directory=BookHelper.loadDir(bid, lastPage, 15, true);	
							mItem.clear();
							mItem.addAll(directory.getList());							
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									adapterMl.notifyDataSetChanged();
									
									hideProgress();
								}
							});
						}
					}).start();	 
				}
			}
		});
		
		nextPage=(Button) findViewById(R.id.dir_nextpage);
		nextPage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int num=newPage;
				if (num<listPage) {
					newPage++;
					chosePage.setText("第"+newPage+"页");
					final int lastPage=num+1;
					showProgress("", "目录加载中...");
					new Thread(new Runnable() {					
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							Directory directory=BookHelper.loadDir(bid, lastPage, 15, true);	
							mItem.clear();
							mItem.addAll(directory.getList());							
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									adapterMl.notifyDataSetChanged();
									
									hideProgress();
								}
							});
						}
					}).start();
				}
			}
		});
		
		
		chosePage=(TextView) findViewById(R.id.chose_page);
		
//		DebugLog.e("", chosePage.getText().toString().substring(1,3));
		DebugLog.e(" ", chosePage.getText().toString().substring(1, chosePage.length()-1));
		chosePage.getText().subSequence(1, chosePage.length()-1);
		titleTop=(TextView) findViewById(R.id.search_top_title_tv);
		titleTop.setText(getResources().getString(R.string.boyi_directory_text_title));
				
		moreListView=(ListView) findViewById(R.id.directory_listview);
		showProgress("", "加载中...");
		
		
		moreListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MoreDirectory.this, OnlineReadingActivity.class);
				item.lastChapterPos = 15*(newPage-1)+position;
                intent.putExtra("BookItem", item);
                startActivityForResult(intent, PageID.Bookshelf);
			}
		});
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub				
				Directory directory=BookHelper.loadDir(bid, 1, 15, true);	
				
				listPage=directory.getPageCount();		
				pageList=new ArrayList<String>();
				for (int i = 1; i <= listPage; i++) {
					pageList.add("第"+i+"页");
//					DebugLog.e("", "第"+i+"张");					
				}
				mItem=directory.getList();	
				
//				adapterMl=new ContentAdapter(MoreDirectory.this, mItem, true);		
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						moreListView.setAdapter(adapterMl);
						hideProgress();
					}
				});
			}
		});
	
	}

	private void initPage() {
		// TODO Auto-generated method stub
		chosePage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				showPagePopuwidonw();
			}
		});
	}
	
	protected void showPagePopuwidonw() {

		WindowManager wm = getWindowManager();
		
		Display d = wm.getDefaultDisplay(); 
		
		int height = (int) (d.getHeight() * 0.9); 
		int width = (int) (d.getWidth() * 0.9); 
		view = View.inflate(this, R.layout.boyi_chose_page, null);
		if (pw == null) {
			pw = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			pw.setFocusable(true);
			pw.setTouchable(true);
			pw.setOutsideTouchable(true);
			pw.setBackgroundDrawable(new BitmapDrawable());
		}
		pageListView = (ListView) view.findViewById(R.id.chose_listview);
		ViewGroup.LayoutParams params = pageListView.getLayoutParams();
		params.height=height;
		params.width=width;
		pageListView.setLayoutParams(params);
		
		ArrayAdapter< String > adapter=new ArrayAdapter<String>(MoreDirectory.this, android.R.layout.simple_list_item_1, pageList);
		
		pageListView.setAdapter(adapter);
		
		pageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				final int i=position+1;
				newPage=i;
				chosePage.setText("第"+i+"页");
				hidePopupGirdAction();
				showProgress("", "目录加载中..");
				new Thread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						Directory directory=BookHelper.loadDir(bid, i, 15, true);	
						mItem.clear();
						mItem.addAll(directory.getList());							
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								adapterMl.notifyDataSetChanged();
								
								hideProgress();
							}
						});
					}
				}).start();		
				
			}
		});
		DebugLog.e("展示", "请选择第几页");

		pw.showAtLocation(chosePage, Gravity.CENTER, 0, 0);

	}
	
	private void hidePopupGirdAction() {
		if (null != pw) {
			pw.dismiss();
		}
	}
}
