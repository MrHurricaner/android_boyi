package com.boyiqove.ui.bookstore;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.config.Config;
import com.boyiqove.config.DeviceInfo;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.PageID;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.protocol.JsonObjectPostRequest;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.ui.bookshelf.OnlineReadingActivity;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.GetBookDetailUtil;
import com.boyiqove.view.BaseActivity;
import com.boyiqove.view.MyAlert;
import com.boyiqove.view.MyWebView;
import com.boyiqove.R;

/*
 * 书城的二级点击页面
 */
public class StoreTitleActivity extends BaseActivity {
	private final static String TAG = "StoreDetailActivity";

	private WebView 	mWebView;
	private String 			mUrl;
	private String 			title;
    private BookItem 	mBookItem;    
    private int 		mResultCode;
    private ImageView search;
    private RelativeLayout menu;
    private int mbookID,mChapterPos;// 书的id以及章节的角标
    private String chapterName;// 当前章节名
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.boyi_bookdetail_web);

		initData();
		initView();
	}

	private void initData() {
		
		mUrl=getIntent().getStringExtra("url");
		DebugLog.e("url", mUrl);
		if (null == mUrl) {
			throw new RuntimeException();
		}
		title=getIntent().getStringExtra("title");
	}

	private void initView() {
		search=(ImageView) this.findViewById(R.id.search);
		menu=(RelativeLayout) this.findViewById(R.id.boyi_book);
		search.setVisibility(View.GONE);
		menu.setVisibility(View.GONE);
		top=(TextView)this.findViewById(R.id.top);
        ImageView ivBack = (ImageView)this.findViewById(R.id.search_back);
		ImageView search=(ImageView) this.findViewById(R.id.search);
		RelativeLayout menu =(RelativeLayout)this.findViewById(R.id.boyi_book);
		menu.setOnClickListener(new OnClickListener() {
			
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
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入搜索页面
				Intent intent=new Intent(StoreTitleActivity.this,LocalSearchActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.boyi_move_right_in, R.anim.boyi_move_right_out);
			}
		});
        ivBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//                goBack();
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
        
        TextView tvTitle = (TextView)this.findViewById(R.id.search_top_title_tv);
        
        tvTitle.setText(title);
        
		MyWebView myWeb = (MyWebView) this.findViewById(R.id.bookdetail_webview);
        mWebView = myWeb.getWebView();
        mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUseWideViewPort(true); 
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setDefaultFontSize(16);
		mWebView.addJavascriptInterface(new JavaScriptInterface(), JavaScriptInterface.NAME);

		
		mWebView.loadUrl(mUrl);

		mWebView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
					DebugLog.d(TAG, "onKey, goBack");
					mWebView.goBack();
					return true;
				}

				return false;
			}
		});

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
    
	private class JavaScriptInterface {
		
		public final static String NAME = "androidjs";

		@JavascriptInterface
		public void startOnlineReading(int bookID, int chapterPos) {
			DebugLog.d("startOnlineReading", "bookID:" + bookID + ", chapterPos:" + chapterPos);
            getRequestBookDetail(bookID, chapterPos);
            
		}
        
        @JavascriptInterface
		public String getDeviceInfo() {
            DeviceInfo info = AppData.getConfig().getDeviveInfo();
			JSONObject obj = new JSONObject();
            try {
				obj.put(DeviceInfo.KEY_IMEI, info.getImei());
                obj.put(DeviceInfo.KEY_IMSI, info.getImsi());
				obj.put(DeviceInfo.KEY_WIDTH, info.getWidth());
				obj.put(DeviceInfo.KEY_HEIGHT, info.getHeight());
                obj.put(DeviceInfo.KEY_MAC, info.getMac());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            return obj.toString();
		}
        
        @JavascriptInterface
        public String getToken() {
        	return AppData.getUser().getToken();
        }
        
        
        @JavascriptInterface
        public void buyBook(int bookID, int chapterPos,String name) { // 优惠购买
        	mbookID=bookID;
        	mChapterPos=chapterPos;
        	chapterName=name;
//        	showPopupWindowExit() ;
        	// 获取整本书的章节信息
//        	getRequestContents();
        	
        }
        
        @JavascriptInterface
        public void startUrl(String url) {
        	mWebView.loadUrl(url);
        }
        
        @JavascriptInterface
        public void startDetilActivity( int bid) {
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
//        	    }	
//        	});
        	
        } 
        @JavascriptInterface
		public void setImageUrl(int bid, int tid, int wid,String url){
			final String  imgUrl = url;
			final String mbid = bid + "";
			final int mtid = tid;
			final int mwid = wid;
			
			mHandler.post(new Runnable() {
				public void run() {
					try {	
						GetBookDetailUtil.startReadingBook(mbid, imgUrl, StoreTitleActivity.this, false, 0);
//						AppData.startBookReading(StoreTitleActivity.this,mbid, imgUrl,false);
						if (mtid != -1) {					
//						 AppData.goBoyiSdk(AppData.ENTRY_CODE_NAVIJATION,AppData.ENTRY_TYPE_READBOOK,mbid,StoreTitleActivity.this,"","");
						Map<String, String> map = new HashMap<String, String>();
						map.put("uid", AppData.getUser().getID() + "");
						map.put("tid", "" + mtid);
						map.put("placeid", "" + mwid);

						String url = AppData.getConfig().getUrl(
								Config.URL_PLACE_TUIJIAN);
						getRequestQueue().add(
								new JsonObjectPostRequest(url,
										new Listener<JSONObject>() {

											@Override
											public void onResponse(
													JSONObject response) {
												// TODO Auto-generated method
												// stub
												DebugLog.d(TAG,
														response.toString());
												try {
													int status = response
															.getInt("status");
													if (status == StatusCode.OK) {
														DebugLog.d(TAG,
																"位置上传成功");

													} else {
														DebugLog.d(
																TAG,
																"位置上传失败:"
																		+ response
																				.getString("msg"));

													}

												} catch (JSONException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
													DebugLog.d(
															TAG,
															"服务器数据解析错误:"
																	+ response
																			.toString());
												}

											}
										}, new ErrorListener() {

											@Override
											public void onErrorResponse(
													VolleyError error) {
												// TODO Auto-generated method
												// stub
											}

										}, map));
							
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				}

			});
		}
        
//        @JavascriptInterface
//        public void showToast(String message, int show) {
//            Message msg = Message.obtain();
//            msg.what = MSG_SHOW_TOAST;
//            msg.arg1 = show;
//            msg.obj = message;
//            
//            handler.sendMessage(msg);
//        }
//        
//        @JavascriptInterface
//        public void isOnAds() {
//        	mIsOnAds = true;
//            DebugLog.d(TAG, "set OnAds:" + mIsOnAds);
//        }
	}
	
	/*
     * 用于js跳转页面的handler
     */
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			
		};
	};	
	/*
     * 获取书籍详情
     */
	private void getRequestBookDetail(final int bookID, final int chapterPos) {
		String token = AppData.getUser().getToken();
		if(null == token || token.equals("")) {
			showToast("用户还未登录，不能进行此操作", Toast.LENGTH_LONG);
			return;
		}

		String url = AppData.getConfig().getUrl(Config.URL_BOOK_DETAIL);
        Map<String, String> map = new HashMap<String, String>();
        map.put("token", token);
        map.put("books", bookID + "");
        
        DebugLog.d(TAG, url);
        DebugLog.d(TAG, map.toString());
        
        showProgress("", "正在加载书籍信息...");
        
        getRequestQueue().add(new JsonObjectPostRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, response.toString());
                
				hideProgress();
                
                try {
					int status = response.getInt("status");
                    
                    if(StatusCode.OK == status) {
                        JSONObject data = response.getJSONArray("data").getJSONObject(0);
                        
                        BookItem item = new BookItem();
                        item.name = data.getString("title");
                        item.author = data.getString("author");
                        item.coverUrl = data.getString("cover");
                        item.detailUrl = data.getString("url");
                      	item.lastChapterPos = chapterPos;
                        item.lastPosition = 0;
                        item.onlineID = bookID;
                        item.status = data.getInt("status");
                        
                        Intent intent = new Intent(StoreTitleActivity.this, OnlineReadingActivity.class);
                        intent.putExtra("BookItem", item);
                        startActivityForResult(intent, PageID.Bookshelf);

                    } else if(StatusCode.AUTH_FAILURE == status){
                        // token失效,需要重新登录
                        
                    	MyAlert.showPormptLogin(StoreTitleActivity.this);

                    } else {
                    	String msg = response.getString("msg");
                    	DebugLog.d(TAG, msg);

                    	showToast("书籍信息获取失败", Toast.LENGTH_LONG);

                    }
					
                    
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
                    showToast("服务器数据异常", Toast.LENGTH_LONG);
				}
			}
        	
        }, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, error.toString());
                
                hideProgress();
                
				showToast("请检查网络状态", Toast.LENGTH_LONG);
			}
        	
        }, map));
           
	}



	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
        goBack();
	}
    
	
	private void goBack() {
		Intent data = new Intent();
        data.putExtra("BookItem", mBookItem);
        setResult(mResultCode, data);
        
        finish();
        overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
	}

	 private PopupWindow popupWindowExit = null;
		
		
	    
	    private void hidePopupWindowExit() {
	    	if(null != popupWindowExit) {
	    		popupWindowExit.dismiss();
	    	}
	    }
	    private Button  bt_section1,bt_section2,bt_section3,bt_section4;
	    private TextView tvName;
	    private View exitView = null;
}
