package com.boyiqove.ui.bookshelf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.R.integer;
import android.content.ClipData.Item;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.ResultCode;
import com.boyiqove.adapter.LocalContentAdapter;
import com.boyiqove.adapter.OnlineContentAdapter;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.LocalChapterInfo;
import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.util.BitmapTool;
import com.boyiqove.util.GetDirectoryUtil;
import com.boyiqove.view.BaseActivity;

public class OnlineContentsActivity extends BaseActivity {
	private final static String TAG = "OnlineContentsActivity";

	private TextView mSortTv;
	private ImageView daoImage;
	private TextView totalCount;
	private ListView mListView;

	private BookItem mBookItem;
	private Boolean comeDetail;
	private boolean mIsAsec = true; // 升序

	private Object is;
	private Boolean isComeReading;
	private ArrayList<OnlineChapterInfo> list;
	private ArrayList<OnlineChapterInfo> list2;
	private OnlineContentAdapter adapter;
	private Boolean isGoRead=false;
	private Handler mCallBack = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
		}
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		comeDetail=getIntent().getBooleanExtra("comeDetail", false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (! comeDetail) {			
			full(true);
		}
		setContentView(R.layout.boyi_online_contents);
		AssetManager assets = getAssets();
		initView();
	}

	private void full(boolean enable) {
		if (enable) {
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(lp);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		} else {
			WindowManager.LayoutParams attr = getWindow().getAttributes();
			attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attr);

			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}

	private void initView() {
		mBookItem = (BookItem) getIntent().getSerializableExtra("BookItem");
		
		isComeReading=getIntent().getBooleanExtra("isComeRead", false);
		
		if (null == mBookItem) {
			throw new RuntimeException();
		}
		TextView tvBookName = (TextView) this
				.findViewById(R.id.content_bookname_tv);
		LinearLayout layout=(LinearLayout) findViewById(R.id.dir_title_actorbar);
		RelativeLayout titleBar=(RelativeLayout) findViewById(R.id.green_title_bar);
		LinearLayout bottomLayout=(LinearLayout) findViewById(R.id.directory_bottom_download);
		if (! isComeReading) {
			layout.setVisibility(View.GONE);
			bottomLayout.setVisibility(View.GONE);
			titleBar.setVisibility(View.VISIBLE);
			TextView textView=(TextView) findViewById(R.id.search_top_title_tv);
			LinearLayout  iViewBack=(LinearLayout) findViewById(R.id.boe_back_bt);
			iViewBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
					overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
				}
			});
			textView.setText("书籍目录");
			
		}else {
			titleBar.setVisibility(View.GONE);
		}
		TextView tvAuthor = (TextView) this
				.findViewById(R.id.content_author_tv);
		mSortTv = (TextView) this.findViewById(R.id.content_sort_tv);
		daoImage = (ImageView) this.findViewById(R.id.iv_dirctory_spinner);
		totalCount = (TextView) this.findViewById(R.id.dirctory_count);

		tvBookName.setText(mBookItem.name);
		tvAuthor.setText("作者/ "+mBookItem.author);

		mListView = (ListView) this.findViewById(R.id.content_listview);
//		if (mBookItem.onlineID == BookItem.ON_LOCAL_TXT) {
//			ArrayList<LocalChapterInfo> list = (ArrayList<LocalChapterInfo>) getIntent()
//					.getSerializableExtra("contentsList");
//			showLocalContent(list);
//
//		} else {
//		ArrayList<OnlineChapterInfo> list = (ArrayList<OnlineChapterInfo>) getIntent()
//				.getSerializableExtra("contentsList");
//		showOnlineContent(list);
//	}	
			Boolean  haveTable=getIntent().getBooleanExtra("tablelist", false);
	if (isContentsLoaded()) {
			
			if (haveTable) {
				list=new ArrayList<OnlineChapterInfo>();
				list2=new ArrayList<OnlineChapterInfo>();
				new  Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub						
						 list = AppData.getXNContentHelper(Integer.parseInt(mBookItem.bid)).getChapterList();
						 list2 = AppData.getContentHelper(Integer.parseInt(mBookItem.bid)).getChapterList();
						long count=AppData.getContentHelper(Integer.parseInt(mBookItem.bid)).fetchPlacesCount();
						if (count<list.size()) {							
							ArrayList<OnlineChapterInfo> mContentsList2=new ArrayList<OnlineChapterInfo>();
							for (int i = 0; i < count; i++) {
								list.get(i).type=list2.get(i).type;
								mContentsList2.add(list.get(i));
							}
							list.clear();
							list.addAll(mContentsList2);
						}else {
							for (int i = 0; i < list.size(); i++) {
								list.get(i).type=list2.get(i).type;
//								mContentsList2.add(list.get(i));
							}
//							list.clear();
//							list.addAll(mContentsList2);							
						}
//						if (mIsAsec) {
//							
//						}
						totalCount.setText("共"+list.size()+"章");
						mCallBack.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub								
								showOnlineContent(list);
							}
						});
						AppData.closeXNDBContent(Integer.parseInt(mBookItem.bid));
					}
				}).start();
			}else {
				
				new  Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						list = AppData.getContentHelper(Integer.parseInt(mBookItem.bid)).getChapterList();
						
						mCallBack.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub								
								showOnlineContent(list);
								totalCount.setText("共"+list.size()+"章");
							}
						});
						AppData.closeXNDBContent(Integer.parseInt(mBookItem.bid));
					}
				}).start();
			}			
		}else {

			if (haveTable) {
				list=new ArrayList<OnlineChapterInfo>();
				list2=new ArrayList<OnlineChapterInfo>();
				new  Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub	
						list2 = (ArrayList<OnlineChapterInfo>) GetDirectoryUtil.getDirectoryList(mBookItem.bid, 0, mBookItem.totalCount);
						AppData.getContentHelper(Integer.parseInt(mBookItem.bid)).insertChapterList(
								list2);
						 list = (ArrayList<OnlineChapterInfo>) GetDirectoryUtil.getXNDirectoryList(mBookItem.bid, 0);
						 AppData.getXNContentHelper(Integer.parseInt(mBookItem.bid))
							.insertChapterList(list);
						long count=AppData.getContentHelper(Integer.parseInt(mBookItem.bid)).fetchPlacesCount();
						if (count<list.size()) {							
							ArrayList<OnlineChapterInfo> mContentsList2=new ArrayList<OnlineChapterInfo>();
							for (int i = 0; i < count; i++) {
								list.get(i).type=list2.get(i).type;
								mContentsList2.add(list.get(i));
							}
							list.clear();
							list.addAll(mContentsList2);
						}else {
							for (int i = 0; i < list.size(); i++) {
								list.get(i).type=list2.get(i).type;
							}
						}
						totalCount.setText("共"+list.size()+"章");
						mCallBack.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub								
								showOnlineContent(list);
							}
						});
						AppData.closeXNDBContent(Integer.parseInt(mBookItem.bid));
					}
				}).start();
			}else {
				showProgress("", "加载中");
				new  Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						list = (ArrayList<OnlineChapterInfo>) GetDirectoryUtil.getDirectoryList(mBookItem.bid, 0, mBookItem.totalCount);
						AppData.getContentHelper(Integer.parseInt(mBookItem.bid)).insertChapterList(
								list);
						mCallBack.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub								
								showOnlineContent(list);
								totalCount.setText("共"+list.size()+"章");
								hideProgress();
							}
						});
						
//						AppData.closeXNDBContent(Integer.parseInt(mBookItem.bid));
					}
				}).start();
			}	
			}
			
		mListView.setSelection(mBookItem.lastChapterPos);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(OnlineContentsActivity.this,
						OnlineReadingActivity.class);
				isGoRead=true;
				// 判断升序和降序
				if (!mIsAsec) {
					position = parent.getAdapter().getCount() - 1 - position;
				}
				intent.putExtra("position", position);
				mBookItem.lastChapterPos = position;
				intent.putExtra("BookItem", mBookItem);
				if (isComeReading) {					
					intent.putExtra("comeDire", true);
				}
				startActivity(intent);
//				if (isComeReading) {					
//					setResult(ResultCode.JUMP_TO_POSITION, intent);
//				}else {
//					mBookItem.lastChapterPos = position;
//					intent.putExtra("BookItem", mBookItem);
//					startActivity(intent);
//				}
							
			if (! comeDetail) {				
				finish();
			}
			}
		});
		if (isComeReading) {
		
		AssetManager am = null;
		am = this.getAssets();
		InputStream is = null;
		Bitmap newBitmap = null;
		try {
			is = am.open("bg_read0.png");
			newBitmap = BitmapTool.decodeZoomBitmap(is, 480, 800);

		} catch (Exception e) {
			e.printStackTrace();

			System.gc();
			System.gc();

			newBitmap = BitmapTool.decodeZoomBitmap(is, 480, 800);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if(newBitmap!=null)
			getWindow().setBackgroundDrawable(new BitmapDrawable(newBitmap));
		
		}
	}

	private void showOnlineContent(ArrayList<OnlineChapterInfo> list) {
		adapter = new OnlineContentAdapter(this,
				list, mBookItem.lastChapterPos);
		mListView.setAdapter(adapter);
		mListView.setSelection(mBookItem.lastChapterPos);
		mSortTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIsAsec = !mIsAsec;
				adapter.sort(mIsAsec);

				if (mIsAsec) {
					mSortTv.setText("正序章节");
					daoImage.setBackgroundResource(R.drawable.boy_dirctory_spinner);
				} else {
					mSortTv.setText("逆序章节");
					daoImage.setBackgroundResource(R.drawable.boy_dirctory_spinner_up);
				}
			}
		});
	}

	private void showLocalContent(ArrayList<LocalChapterInfo> list) {
		
		final LocalContentAdapter adapter = new LocalContentAdapter(this, list,
				mBookItem.lastChapterPos);
		mListView.setAdapter(adapter);

		mSortTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIsAsec = !mIsAsec;
				adapter.sort(mIsAsec);
				
				if (mIsAsec) {
					mSortTv.setText("正序");
				} else {
					mSortTv.setText("逆序");
				}
			}
		});
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (isGoRead) {  // 来自阅读页面的时候
			mBookItem=AppData.getDataHelper().getBookItem(Integer.parseInt(mBookItem.bid));
			list = AppData.getContentHelper(Integer.parseInt(mBookItem.bid)).getChapterList();
			adapter = new OnlineContentAdapter(this,
					list, mBookItem.lastChapterPos);
			mListView.setAdapter(adapter);
			mListView.setSelection(mBookItem.lastChapterPos);
		}
	}
	
	private boolean isContentsLoaded() { // 判断移动目录是否存在
		File f = new File(AppData.getConfig().getContentDBName(
				Integer.parseInt(mBookItem.bid)));
		return f.exists();
	}
	
}
