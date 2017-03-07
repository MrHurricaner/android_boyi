package com.boyiqove.ui.bookstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.config.Config;
import com.boyiqove.config.DeviceInfo;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.entity.PageID;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.protocol.JsonObjectPostRequest;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.task.CallBackTask;
import com.boyiqove.ui.bookshelf.OnlineReadingActivity;
import com.boyiqove.ui.bookshelf.OnlineReadingActivity.ChapterAction;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.GetBookDetailUtil;
import com.boyiqove.view.BaseFragment;
import com.boyiqove.view.MyAlert;
import com.boyiqove.view.MyWebView;
import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.data.Detail;

public class BookstoreMain extends BaseFragment {
	
    private final static String TAG = "BookstoreMain";

	private View mRootView;
    private  String mUrl =null;
    private  String mTitle =null;
	private 	WebView 	mWebView;
	private String mChannel;
    private Button bt;
    
	private boolean 	mIsOnAds = false;
    
    private final static int MSG_SHOW_TOAST = 111;
    
    private int mbookID,mChapterPos;// 书的id以及章节的角标
    private String chapterName;// 当前章节名
    private List<BookItem> mBookList;
    private String urlString;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
            
			if(msg.what == MSG_SHOW_TOAST) {
				showToast(msg.obj.toString(), msg.arg1);
			}
		}
	};

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return super.onCreateView(inflater, container, savedInstanceState);
		DebugLog.d(TAG, "onCreate");

		if (null == mRootView) {
			mRootView = inflater.inflate(R.layout.boyi_bookstore_main_top,
					container, false);

			initView(mRootView);
		}

		ViewGroup parent = (ViewGroup) mRootView.getParent();
		if (null != parent) {

			parent.removeView(mRootView);
		}

		return mRootView;
	}
	
    public BookstoreMain(String url) {
    	
    	this.urlString=url;
	}
    public BookstoreMain() {
    	super();
    }
	private void initView(View v) {
		
		bt=(Button) v.findViewById(R.id.hahaha);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				mWebView.loadUrl("javascript:do()");
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mWebView.loadUrl("javascript:do()");
					}
				});
			}
		});
		
		// loadHistoryUrls=new ArrayList<String>();
		// loadHistoryUrls.add(AppData.getConfig().getUrl(Config.URL_BOOKSTORE));
		// lastTv=(TextView) v.findViewById(R.id.store_recommend_title);
		// lastLayout=(RelativeLayout)
		// v.findViewById(R.id.store_recommend_window);
		// recommendTv=(TextView) v.findViewById(R.id.store_recommend_title);
		// listTv=(TextView) v.findViewById(R.id.store_list_title);
		// sortTv=(TextView) v.findViewById(R.id.store_tab_sort_title);
		// searchTv=(TextView) v.findViewById(R.id.store_tab_search_title);

		MyWebView myWebView = (MyWebView) v.findViewById(R.id.mweb_addtop);

		mWebView = myWebView.getWebView();

		mWebView.getSettings().setJavaScriptEnabled(true);
        
		mWebView.addJavascriptInterface(new JavaScriptInterface(),
				JavaScriptInterface.NAME);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setUseWideViewPort(true); 
		mWebView.getSettings().setLoadWithOverviewMode(true); 
		mWebView.getSettings().setDefaultFontSize(16);
		
//		mWebView.getSettings().setUseWideViewPort(true); 
//		mWebView.getSettings().setLoadWithOverviewMode(true); 
//		mChannel = AppData.getConfig().getDeviveInfo().getChannel();
		mChannel=AppData. readMetaDataFromService(getActivity(),"channel_num");
		DebugLog.e("渠道号", mChannel);
//		mWebView.loadUrl(AppData.getConfig().getUrl(Config.URL_BOOKSTORE)
//				+ "?version="+AppData.getConfig().getDeviveInfo().getVersionName());
		mWebView.loadUrl(urlString);
		// mWebView.loadUrl("http://sdk.boetech.cn/index.php");

		mWebView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
					DebugLog.d(TAG, "onKey, goBack");
					//
					getActivity().finish();
					getActivity()
							.overridePendingTransition(
									R.anim.boyi_move_left_in,
									R.anim.boyi_move_left_out);
					return true;
				}

				return false;
			}
		});

		mWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (mIsOnAds) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						v.getParent().requestDisallowInterceptTouchEvent(true);
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						v.getParent().requestDisallowInterceptTouchEvent(false);
						mIsOnAds = false;
						break;
					}
				}

				return false;
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		DebugLog.d(TAG, "onActivityResult");
	}

	public void replaceUrl(String url) {
		
        mWebView.loadUrl(url);

	}

	private class JavaScriptInterface {

		public final static String NAME = "androidjs";

		@JavascriptInterface
		public void startOnlineReading(int bookID, int chapterPos) {
			DebugLog.d("startOnlineReading", "bookID:" + bookID
					+ ", chapterPos:" + chapterPos);
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
		public void buyBook(int bookID, int chapterPos, String name) { // 优惠购买
			mbookID = bookID;
			mChapterPos = chapterPos;
			chapterName = name;
			// showPopupWindowExit() ;
			// 获取整本书的章节信息
			getRequestContents();

		}

		@JavascriptInterface
		public void startUrl(String url) {
			mWebView.loadUrl(url);

		}

		@JavascriptInterface
		public void startUrlActivity(final String url, final String title) {
			final String mUrl = url;
			final String mTitle = title;
			DebugLog.e("查看详情+++++++++++++++++++", mUrl);
			mHandler.post(new Runnable() {
				public void run() {
					try {
						startSecondVoid(mUrl, mTitle);
						// AppData.goBoyiSdk(AppData.ENTRY_CODE_NAVIJATION,AppData.ENTRY_TYPE_STORETWO,"",getActivity(),url,title);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				}
			});

		}

		@JavascriptInterface
		public void startDetilActivity(int bid) {
			final String mbid = bid + "";
			final int cbid = bid;
			mHandler.post(new Runnable() {
				public void run() {
					try {
						DebugLog.e("调用js直接进入阅读", "");
						AppData.goBoyiSdk(AppData.ENTRY_CODE_NAVIJATION,
								AppData.ENTRY_TYPE_READBOOK, mbid,
								getActivity(), "", "");

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

			});

		}

		@JavascriptInterface
		public void setImageUrl(int bid, int tid, int wid,String url){
			final String  imgUrl = url;
			if (bid==0) {
				return;
			}
			final String mbid = bid + "";
			final int mtid = tid;
			final int mwid = wid;

			mHandler.post(new Runnable() {
				public void run() {
					try {		
//						showProgressCancel("", "", "加载中");
						GetBookDetailUtil.startReadingBook(mbid, imgUrl, getActivity(), false, 0);
//						AppData.startBookReading(getActivity(),mbid, imgUrl,false);
//						hideProgress()
						if (mtid != -1) {
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
		
		@JavascriptInterface
		public void startDetilActivity(int bid, int tid, int wid) {

		}

		@JavascriptInterface
		public void showToast(String message, int show) {
			Message msg = Message.obtain();
			msg.what = MSG_SHOW_TOAST;
			msg.arg1 = show;
			msg.obj = message;

			handler.sendMessage(msg);
		}

		@JavascriptInterface
		public void isOnAds() {
			mIsOnAds = true;
			DebugLog.d(TAG, "set OnAds:" + mIsOnAds);
		}
	}

	/**
	 * 跳转
	 * */

	private void startSecondVoid(String oUrl, String oTitle) {
		// DebugLog.e("查看详情++++++++++子线程进行跳转", mUrl);
		Intent intent = new Intent(getActivity(), StoreTitleActivity.class);
		intent.putExtra("url", AppData.getConfig().getUrl(Config.URL_BOOKSTORE_SKIP) + oUrl);
		intent.putExtra("title", oTitle);
		startActivity(intent);
		// getActivity().overridePendingTransition(R.anim.left_activity_scale,
		// R.anim.move_right_in);
		getActivity().overridePendingTransition(R.anim.boyi_move_right_in,
				R.anim.boyi_left_activity_scale);
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				Intent intent = new Intent(getActivity(),
						StoreTitleActivity.class);
				intent.putExtra("url", mUrl);
				startActivity(intent);
			}
		};
	};

	/*
	 * 获取书籍详情
	 */
	private void getRequestBookDetail(final int bookID, final int chapterPos) {
		String token = AppData.getUser().getToken();
		if (null == token || token.equals("")) {
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

		getRequestQueue().add(
				new JsonObjectPostRequest(url, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						DebugLog.d(TAG, response.toString());

						hideProgress();

						try {
							int status = response.getInt("status");

							if (StatusCode.OK == status) {
								JSONObject data = response.getJSONArray("data")
										.getJSONObject(0);

								BookItem item = new BookItem();
								item.name = data.getString("title");
								item.author = data.getString("author");
								item.coverUrl = data.getString("cover");
								item.detailUrl = data.getString("url");
								item.lastChapterPos = chapterPos;
								item.lastPosition = 0;
								item.onlineID = bookID;
								item.status = data.getInt("status");

								Intent intent = new Intent(getActivity(),
										OnlineReadingActivity.class);
								intent.putExtra("BookItem", item);
								startActivityForResult(intent, PageID.Bookshelf);

							} else if (StatusCode.AUTH_FAILURE == status) {
								// token失效,需要重新登录

								MyAlert.showPormptLogin(getActivity());

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

	private PopupWindow popupWindowExit = null;

	private void hidePopupWindowExit() {
		if (null != popupWindowExit) {
			popupWindowExit.dismiss();
		}
	}

	private Button bt_section1, bt_section2, bt_section3, bt_section4;
	private TextView tvName;
	private View exitView = null;

	/*
	 * 购买章节 chapterPos: 购买后下载该章节 start: to:
	 */
	private void getRequestBuy(int start, int to, final int chapterPos,
			final ChapterAction action) {
		String token = AppData.getUser().getToken();
		if (null == token || token.equals("")) {
			showToast("您还未登陆， 不能进行购买", Toast.LENGTH_LONG);
			return;
		}

		String url = AppData.getConfig().getUrl(Config.URL_CHAPTER_BUY);
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("id", String.valueOf(mbookID));
		map.put("start", String.valueOf(start));
		map.put("to", String.valueOf(to));

		getRequestQueue().add(
				new JsonObjectPostRequest(url, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						DebugLog.d(TAG, response.toString());

						try {
							int status = response.getInt("status");
							if (StatusCode.OK == status) {
								// JSONObject data =
								// response.getJSONObject("data");
								// int number = data.getInt("number");
								// int price = data.getInt("price");
								// double discount = data.getDouble("discount");
								//
								// showToast("您已成功购买" + number + "章," + "花费" +
								// price + "香币(" + discount + "折)",
								// Toast.LENGTH_LONG);
								showToast("购买成功", Toast.LENGTH_LONG);

								// getRequestChapterCache(chapterPos, action);

							} else if (StatusCode.INSU_FUNDS == status) {
								showToast("您的余额不足，请充值", Toast.LENGTH_LONG);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						hideProgress();
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						DebugLog.d(TAG, error.toString());

						hideProgress();

						showToast("无法购买， 请检查网络状态", Toast.LENGTH_LONG);
					}

				}, map));
	}

	/*
	 * 获取小说目录信息
	 */
	private void getRequestContents() {

		final ArrayList<OnlineChapterInfo> mContentsList = new ArrayList<OnlineChapterInfo>();

		String token = AppData.getUser().getToken();
		String url = AppData.getConfig().getUrl(Config.URL_CONTENTS);
		// http://boyue.boetech.cn/api/article/getChapters
		DebugLog.d(TAG, url);

		int lastChapterID = 0;

		// if(mContentsList.size() > 0) {
		// lastChapterID = mContentsList.get(mContentsList.size() - 1).id;
		// }

		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("id", String.valueOf(mbookID));
		map.put("limit", String.valueOf(lastChapterID));

		getRequestQueue().add(
				new JsonObjectPostRequest(url, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						// DebugLog.d(TAG, response.toString());
						try {
							int status = response.getInt("status");
							if (status == StatusCode.OK) {
								JSONArray array = response
										.getJSONObject("data").getJSONArray(
												"chapters");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									OnlineChapterInfo info = new OnlineChapterInfo();
									info.id = obj.getInt("chapterIndex");
									info.name = obj.getString("title");
									info.type = obj.getInt("isvip");
									System.out.println(info.name);
									mContentsList.add(info);
								}

								DebugLog.d(TAG, "章节目录获取成功");
								// AppData.getContentHelper(mBookItem.onlineID).insertChapterList(mContentsList);

								// WriteContentsTask task = new
								// WriteContentsTask("writeContent");
								// AppData.getClient().getTaskManager().addTask(task);
								// getRequestChapterCache(mBookItem.lastChapterPos,
								// ChapterAction.INIT);

							} else {
								String msg = "章节目录获取失败:"
										+ response.getString("msg");
								DebugLog.d(TAG, msg);
								showToast(msg, Toast.LENGTH_LONG);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						hideProgress();

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						DebugLog.d(TAG, error.toString());
						hideProgress();
						showToast("无法更新目录，请检查网络状态", Toast.LENGTH_LONG);
					}

				}, map));

	}

	private void onBackPressed() {
		// TODO Auto-generated method stub
		getActivity().finish();
	}

}
