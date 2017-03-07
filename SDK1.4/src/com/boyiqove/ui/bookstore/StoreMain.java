package com.boyiqove.ui.bookstore;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.config.Config;
import com.boyiqove.util.DebugLog;
import com.boyiqove.view.MyWebView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class StoreMain extends FragmentActivity {
	
	private LinearLayout layout;
	private LinearLayout searchLayout;   // 搜索的view
		
	private RelativeLayout storeBarSearch, storeBar1, storeBar2, storeBar3, storeBar4;
	private View viewBottom1,viewBottom2,viewBottom3,viewBottom4,viewBottom5;  // 底部蓝块儿	
	private View lastView;
	private RelativeLayout  lastStoreBar;
	private  BookstoreMain  webFrag;
	private ImageView backButton,search;
	private ImageView store_top_imageView1,store_top_imageView2,store_top_imageView3,store_top_imageView4;
	private TextView store_top_textView1,store_top_textView2,store_top_textView3,store_top_textView4;
	private TextView lastTextView;
	private ImageView lastImageView;
	private String mUrl;
	private String mTitle;
	private String mChannel;
	private RelativeLayout btn_bookShelf;
	private Button btn_back;
	private int  where=1;
	private LinearLayout bottom_bar;
	private LinearLayout recommend_ll,rank_ll,sort_ll,newbook_ll;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.boyi_stroe_main2);
		//bottom_bar=(LinearLayout) findViewById(R.id.bottom_bar);
		top=(TextView)findViewById(R.id.top);
		search=(ImageView) findViewById(R.id.search);
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入搜索界面
				Intent intent=new Intent(getApplicationContext(),LocalSearchActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.boyi_move_right_in, R.anim.boyi_move_right_out);
			}
		});
		btn_bookShelf=(RelativeLayout) findViewById(R.id.boyi_book);
		btn_bookShelf.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (popupWindow != null&&popupWindow.isShowing()) {
					popupWindow.dismiss();
					return;
				} else {
					showMenuPopupWindow();
				}
			}
		});
		backButton=(ImageView) findViewById(R.id.search_back);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
		
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
				AppData.goToShelf(StoreMain.this,false);
				finish();
			}
		});
		  //回到书架，并打开侧拉篮
		  bookManager.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppData.goToShelf(StoreMain.this,true);
				finish();
				
			}
		});
		}
		return view;
	}
		
	private void initView() {
		// TODO Auto-generated method stub
		mChannel=AppData. readMetaDataFromService(StoreMain.this,"channel_num");	
		webFrag=new BookstoreMain(AppData.getUrl(AppData.getConfig().getUrl(Config.URL_BOOKSTORE)));
		
		storeBar1=(RelativeLayout) findViewById(R.id.store_top_bar11);
		store_top_imageView1=(ImageView) findViewById(R.id.store_top_imageView1);
		store_top_textView1=(TextView) findViewById(R.id.store_top_textView1);
		lastStoreBar=storeBar1;
		lastTextView=store_top_textView1;
		lastImageView=store_top_imageView1;
		//viewBottom1=findViewById(R.id.bottom_bar1);
		viewBottom1=findViewById(R.id.top_bar1);
		lastView=viewBottom1;
		storeBar1.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//storeBar1.setBackgroundColor(getResources().getColor(R.color.boyi_bottom_bg_yes));
				lastStoreBar.setBackgroundColor(getResources().getColor(R.color.boyi_white1));
				store_top_imageView1.setImageResource(R.drawable.boy_recomment_clicked);
				store_top_imageView2.setImageResource(R.drawable.boy_ranked_unclick);
				store_top_imageView3.setImageResource(R.drawable.boy_sort_unclick);
				store_top_imageView4.setImageResource(R.drawable.boy_new_book_unclick);
				store_top_textView1.setTextColor(getResources().getColor(R.color.boyi_main_top_bar));
				store_top_textView2.setTextColor(Color.parseColor("#999999"));
				store_top_textView3.setTextColor(Color.parseColor("#999999"));
				store_top_textView4.setTextColor(Color.parseColor("#999999"));
				lastStoreBar=storeBar1;
				lastTextView=store_top_textView1;
				lastImageView=store_top_imageView1;
				layout.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);				
				lastView.setVisibility(View.INVISIBLE);
				viewBottom1.setVisibility(View.VISIBLE);
				lastView=viewBottom1;
//				mChannel=AppData.getConfig().getDeviveInfo().getChannel();
				if (where!=1) {
					webFrag.replaceUrl(AppData.getUrl(AppData.getConfig().getUrl(Config.URL_BOOKSTORE)));
					where=1;
				}
			}
		});
		
		
		storeBar2=(RelativeLayout) findViewById(R.id.store_top_bar22);
		store_top_imageView2=(ImageView) findViewById(R.id.store_top_imageView2);
		store_top_textView2=(TextView) findViewById(R.id.store_top_textView2);
		//viewBottom2=findViewById(R.id.bottom_bar2);
		viewBottom2=findViewById(R.id.top_bar2);
		storeBar2.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//storeBar2.setBackgroundColor(getResources().getColor(R.color.boyi_bottom_bg_yes));
				lastStoreBar.setBackgroundColor(getResources().getColor(R.color.boyi_white1));
				store_top_imageView1.setImageResource(R.drawable.boy_recomment_unclick);
				store_top_imageView2.setImageResource(R.drawable.boy_ranked_clicked);
				store_top_imageView3.setImageResource(R.drawable.boy_sort_unclick);
				store_top_imageView4.setImageResource(R.drawable.boy_new_book_unclick);
				store_top_textView1.setTextColor(Color.parseColor("#999999"));
				store_top_textView2.setTextColor(getResources().getColor(R.color.boyi_main_top_bar));
				store_top_textView3.setTextColor(Color.parseColor("#999999"));
				store_top_textView4.setTextColor(Color.parseColor("#999999"));
				lastStoreBar=storeBar2;
				lastTextView=store_top_textView2;
				lastImageView=store_top_imageView2;
				layout.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				lastView.setVisibility(View.INVISIBLE);
				viewBottom2.setVisibility(View.VISIBLE);
				lastView=viewBottom2;

				if (where!=2) {					
					webFrag.replaceUrl(AppData.getUrl(AppData.getConfig().getUrl(Config.URL_BOOKSTORE_URL1)));
					where=2;
				}
				webFrag.replaceUrl(AppData.getUrl(AppData.getConfig().getUrl(Config.URL_BOOKSTORE_URL1)));
				DebugLog.e("牌型url",AppData.getUrl(AppData.getConfig().getUrl(Config.URL_BOOKSTORE_URL1)));
			}
		});
		
		storeBar3=(RelativeLayout) findViewById(R.id.store_top_bar33);
		store_top_imageView3=(ImageView) findViewById(R.id.store_top_imageView3);
		store_top_textView3=(TextView) findViewById(R.id.store_top_textView3);
		//viewBottom3=findViewById(R.id.bottom_bar3);
		viewBottom3=findViewById(R.id.top_bar3);
		storeBar3.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//storeBar3.setBackgroundColor(getResources().getColor(R.color.boyi_bottom_bg_yes));
				lastStoreBar.setBackgroundColor(getResources().getColor(R.color.boyi_white1));
				store_top_imageView1.setImageResource(R.drawable.boy_recomment_unclick);
				store_top_imageView2.setImageResource(R.drawable.boy_ranked_unclick);
				store_top_imageView3.setImageResource(R.drawable.boy_sort_clicked);
				store_top_imageView4.setImageResource(R.drawable.boy_new_book_unclick);
				store_top_textView1.setTextColor(Color.parseColor("#999999"));
				store_top_textView2.setTextColor(Color.parseColor("#999999"));
				store_top_textView3.setTextColor(getResources().getColor(R.color.boyi_main_top_bar));
				store_top_textView4.setTextColor(Color.parseColor("#999999"));
				lastStoreBar=storeBar3;
				lastTextView=store_top_textView3;
				lastImageView=store_top_imageView3;
				layout.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
//				mChannel=AppData.getConfig().getDeviveInfo().getChannel();
				if (where!=3) {					
					webFrag.replaceUrl(AppData.getUrl(AppData.getConfig().getUrl(Config.URL_BOOKSTORE_URL2)));
					where=3;
				}
				
				lastView.setVisibility(View.INVISIBLE);
				viewBottom3.setVisibility(View.VISIBLE);
				lastView=viewBottom3;
			}
		});
		
		storeBar4=(RelativeLayout) findViewById(R.id.store_top_bar44);
		store_top_imageView4=(ImageView) findViewById(R.id.store_top_imageView4);
		store_top_textView4=(TextView) findViewById(R.id.store_top_textView4);
		//viewBottom4=findViewById(R.id.bottom_bar4);
		viewBottom4=findViewById(R.id.top_bar4);
		storeBar4.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//storeBar4.setBackgroundColor(getResources().getColor(R.color.boyi_bottom_bg_yes));
				lastStoreBar.setBackgroundColor(getResources().getColor(R.color.boyi_white1));
				store_top_imageView1.setImageResource(R.drawable.boy_recomment_unclick);
				store_top_imageView2.setImageResource(R.drawable.boy_ranked_unclick);
				store_top_imageView3.setImageResource(R.drawable.boy_sort_unclick);
				store_top_imageView4.setImageResource(R.drawable.boy_new_book_clicked);
				store_top_textView1.setTextColor(Color.parseColor("#999999"));
				store_top_textView2.setTextColor(Color.parseColor("#999999"));
				store_top_textView3.setTextColor(Color.parseColor("#999999"));
				store_top_textView4.setTextColor(getResources().getColor(R.color.boyi_main_top_bar));
				lastStoreBar=storeBar4;
				lastTextView=store_top_textView4;
				lastImageView=store_top_imageView4;
				layout.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
//				mChannel=AppData.getConfig().getDeviveInfo().getChannel();
				lastView.setVisibility(View.INVISIBLE);
				viewBottom4.setVisibility(View.VISIBLE);
				lastView=viewBottom4;
				if (where!=4) {					
					webFrag.replaceUrl(AppData.getUrl(AppData.getConfig().getUrl(Config.URL_BOOKSTORE_URL3)));
					where=4;
				}
			}
		});
		
		layout=(LinearLayout) findViewById(R.id.store_main_frg);
		searchLayout=(LinearLayout) findViewById(R.id.store_search_view);
		getSupportFragmentManager().beginTransaction().replace(R.id.store_main_frg,webFrag).commit();
		storeBarSearch=(RelativeLayout) findViewById(R.id.store_bottom_bar5) ;
		viewBottom5=findViewById(R.id.bottom_bar5);
		storeBarSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 打开搜索页面
				storeBarSearch.setBackgroundColor(getResources().getColor(R.color.boyi_bottom_bg_yes));
				lastStoreBar.setBackgroundColor(getResources().getColor(R.color.boyi_white));
				lastStoreBar=storeBarSearch;
				layout.setVisibility(View.GONE);
				searchLayout.setVisibility(View.VISIBLE);
				lastView.setVisibility(View.INVISIBLE);
				viewBottom5.setVisibility(View.VISIBLE);
				lastView=viewBottom5;
				if (where!=5) {					
					getSupportFragmentManager().beginTransaction().replace(R.id.store_search_view,new SearchFragment()).commit();
					where=5;
				}
				
				
			}
		});
	}
	
	
//private class JavaScriptInterface {
//		
//		public final static String NAME = "androidjs";
//
//		@JavascriptInterface
//		public void startOnlineReading(int bookID, int chapterPos) {
//			DebugLog.d("startOnlineReading", "bookID:" + bookID + ", chapterPos:" + chapterPos);
////            getRequestBookDetail(bookID, chapterPos);
//            
//		}
//        
//        @JavascriptInterface
//		public String getDeviceInfo() {
//            DeviceInfo info = AppData.getConfig().getDeviveInfo();
//			JSONObject obj = new JSONObject();
//            try {
//				obj.put(DeviceInfo.KEY_IMEI, info.getImei());
//                obj.put(DeviceInfo.KEY_IMSI, info.getImsi());
//				obj.put(DeviceInfo.KEY_WIDTH, info.getWidth());
//				obj.put(DeviceInfo.KEY_HEIGHT, info.getHeight());
//                obj.put(DeviceInfo.KEY_MAC, info.getMac());
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            
//            return obj.toString();
//		}
//        
//        @JavascriptInterface
//        public String getToken() {
//        	return AppData.getUser().getToken();
//        }
//        
//        
//        @JavascriptInterface
//        public void buyBook(int bookID, int chapterPos,String name) { // 优惠购买
////        	mbookID=bookID;
////        	mChapterPos=chapterPos;
////        	chapterName=name;
////        	showPopupWindowExit() ;
////        	// 获取整本书的章节信息
////        	getRequestContents();
//        	
//        }
//        
//        @JavascriptInterface
//        public void startUrl(String url) {
////        	mWebView.loadUrl(url);
//        	
//        }
//        
//        @JavascriptInterface
//        public void startDetilActivity(int bid) {
//        	final String mbid=bid+"";
//        	mHandler.post(new Runnable(){
//        	    public void run(){
//        	    	try {
//        	    		startBookDetil(mbid);
//            			
//					} catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();
//					}
//        			
//        	    }
//
//				
//        	});       	
//        }             
//        
//        @JavascriptInterface
//        public void startUrlActivity(String url,String title) {
////        	mWebView.loadUrl(url);
//        	 mUrl="http://boyue.boetech.cn"+url;
//        	 mTitle=title;
//        	DebugLog.e("查看详情+++++++++++++++++++", mUrl);       	
////        	thread.start();
//        	mHandler.post(new Runnable(){
//        	    public void run(){
//        	    	try {
//        	    		
//        	    		startSecondVoid();  
//        	    		
//					} catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();
//					}
//        			
//        	    }
//        	});
//        	
//        }
//        
////        @JavascriptInterface
////        public void showToast(String message, int show) {
////            Message msg = Message.obtain();
////            msg.what = MSG_SHOW_TOAST;
////            msg.arg1 = show;
////            msg.obj = message;
////            
////            handler.sendMessage(msg);
////        }
////        
////        @JavascriptInterface
////        public void isOnAds() {
////        	mIsOnAds = true;
////            DebugLog.d(TAG, "set OnAds:" + mIsOnAds);
////        }
//        
//	}

//		private void startBookDetil(String bid) {
//	// TODO Auto-generated method stub
//	
//			Intent  intent=new Intent(StoreMain.this, BookDetail.class);
//			Bundle bundle=new Bundle();
////			bundle.putSerializable("detail", detail);
//			intent.putExtra("bid", bid);
//			startActivity(intent);
//			}
//		private Handler mHandler = new Handler(){
//			public void handleMessage(Message msg) {
//				
//			};
//		};	
//		
//		private void startSecondVoid(){
//			Intent intent=new Intent(StoreMain.this, StoreTitleActivity.class);
//			intent.putExtra("url", mUrl);
//			intent.putExtra("title", mTitle);
//			startActivity(intent);	
////			getActivity().overridePendingTransition(R.anim.left_activity_scale, R.anim.move_right_in); 
//			overridePendingTransition(R.anim.boyi_move_right_in,R.anim.boyi_left_activity_scale); 		
//		};
		
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
			return super.onKeyDown(keyCode, event);
		}
}
