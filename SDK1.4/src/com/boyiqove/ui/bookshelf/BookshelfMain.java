package com.boyiqove.ui.bookshelf;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.ResultCode;
import com.boyiqove.config.Config;
import com.boyiqove.db.DBDataHelper;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.Notice;
import com.boyiqove.entity.PageID;
import com.boyiqove.library.volley.RequestQueue;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.toolbox.JsonObjectRequest;
import com.boyiqove.library.volley.toolbox.ListImageListener;
import com.boyiqove.library.volley.toolbox.NetworkImageView;
import com.boyiqove.library.volley.toolbox.StringRequest;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.task.CallBackMsg;
import com.boyiqove.task.CallBackTask;
import com.boyiqove.ui.bookstore.StoreMain;
import com.boyiqove.ui.storeutil.ShelfGridView;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.FileUtil;
import com.boyiqove.view.BaseFragment;

import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.data.BookUpdateInfo;
import com.bytetech1.sdk.data.Detail;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView.FindListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BookshelfMain extends BaseFragment {

	private final static String TAG = "BookshelfMain";
	private final static int DELETE_UNSELECTE = -1;
	private final static int DELETE_SELECTEED = 1;
	private static final int ACTION_BAR_ID_SHOP = 1;
	private View mRootView;

	private TextView mNotifyTv;
	// private ImageView mMoreIv; 原始下拉菜单
	// private PullToRefreshListView pulllistview;
	private ShelfGridView mGridView;
	private View mOperatorView;

	private ListView mListView;
	// private ProgressBar bar;// 列表书籍的进度条
	private BookshelfGridAdapter mGridAdapter;

	private List<BookItem> mBookList;
	private SparseIntArray mDelteArray;

	private View mEmptyView;

	private boolean mIsGrid = false;

	private int mReadIndex = 0;

	private Notice mNotice;

	private Button goStore, goSDcard;

	private String ydBid; // 每日推荐的bid
	private Detail detail;
	private float mWidth;
	private android.view.ViewGroup.LayoutParams mParams;
	// private String bid="549843";

	private SimpleDateFormat time;
	private RelativeLayout btn_bookStore;
	private TextView top_title;
	private Button btn_back;
	private  String channel ;
	
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {

			case CallBackMsg.NOTICE_SUCCESSFUL:
				startShowNotice();

				break;
			case CallBackMsg.NOTICE_SHOW_NEXT:
				showNotice((Notice) msg.obj);

				break;
			case CallBackMsg.LOGIN_SUCCESSFUL_SYNC:
				AppData.getDataHelper().close();
				// DBDataHelper helper = AppData.getDataHelper();
				// mBookList = helper.getBookShelfList();// 帐号登录成功以后从数据库上得到书内容
				//
				// mListAdapter.notifyDataSetChanged();
				// mGridAdapter.notifyDataSetChanged();
				//
				// if (mBookList.size() > 0) {
				// getRequestUpdate();
				// }

				break;
			case CallBackMsg.UPDATE_BOOKSHELF: // 检查书籍更新后发来的消息
			{
				// 请求了跟新书架
				mBookList = AppData.getDataHelper().getKbShelfList();
				if (mEmptyView.getVisibility() == View.VISIBLE
						&& mBookList.size() > 0) {

					mEmptyView.setVisibility(View.GONE);

					showShelfView(); // 更新书籍 后初始化书架
				}
				mGridAdapter.notifyDataSetChanged();
				// pulllistview.onRefreshComplete();

			}
				break;
			default:
				DebugLog.d(TAG, "unkown msg:" + Integer.toHexString(msg.what));
				break;
			}

		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO 在此回调中初始化视图，无需再重写onCreateView;
		// super.onCreatePageView(inflater, container, savedInstanceState);

		time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		AppData.getClient().setCallBackHander(handler);

		if (null == mRootView) {
			mRootView = inflater.inflate(R.layout.boyi_bookshelf_main,
					container, false);

			WindowManager manager = getActivity().getWindowManager();
			mWidth = (manager.getDefaultDisplay().getWidth() - dip2px(
					getActivity(), (45 + 45 + 30))) / 3;
			// mParams=new LayoutParams((int )mWidth, (int )(mWidth*4/3));
			// mParams=new LayoutParams((int )mWidth, (int )(mWidth*4/3));
			// startShowNotice(); //展示公告
			
			channel=AppData.readMetaDataFromService(getActivity(),"channel_num");
			getRequestRecommand();
			getRequestNotice();
			/**
			 * 判断时间显示每日推荐
			 * */
			SharedPreferences mySharedPreferences = getActivity()
					.getSharedPreferences("everytime",
							Application.MODE_PRIVATE);

			long str = mySharedPreferences.getLong("time", 0);
			// DebugLog.e("上次时间", str+"");
			String lastHour = time.format(str).substring(11, 13);

			if (str == 0) {

				Date nowTime = new Date();
				long time1 = nowTime.getTime();

				// SimpleDateFormat time = new
				// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				String dataString = time.format(nowTime);

				lastHour = dataString.substring(11, 13);
				// String day = dataString.substring(8, 10);

				SharedPreferences.Editor editor = mySharedPreferences.edit();
				// Log.e("当前时间时间", time1+"");

				editor.putLong("time", time1);

				editor.commit();

				threadPw.start();

			} else {

				// Date curDate = new Date(System.currentTimeMillis());//获取当前时间

				long time2 = new Date().getTime();
				// DebugLog.e("当前时间是@@几点？？？？？？", time2+"");
				/**
				 * 每天首次推荐
				 * */
				if (time2 - str > (24 - Integer.parseInt(lastHour)) * 60 * 60 * 1000) {
					// if (time2-str>(24-Integer.parseInt(lastHour))*60) {

					// DebugLog.e("当前时间大于舍得时间", time2-str+"");

					threadPw.start();
				}

				/**
				 * 隔24小时推荐一次的方法
				 * */
				// if (time2-str>=(long)5184000) {
				// Log.e("时间之差", time2-str+"");
				// threadPw.start();
				//
				// }

				SharedPreferences.Editor editor = mySharedPreferences.edit();
				editor.putLong("time", time2);
				editor.commit();

			}

			initData();

			initView(mRootView);

			checkShelfView(); // 初始化书架

			checkOpenLastBook(); // 判断打开软件直接进入阅读

			// threadPw.start();
		}

		ViewGroup parent = (ViewGroup) mRootView.getParent();
		if (null != parent) {
			parent.removeView(mRootView);
		}

		return mRootView;
	}

/**
	@Override
	public void onActionBarButtonClicked(ActionBarItem item) {
		// TODO Auto-generated method stub
		switch (item.getId()) {
		case ACTION_BAR_ID_SHOP:
			// TODO 按钮点击事件中添加跳转至书城的代码

			Intent intent = new Intent(getActivity(), StoreMain.class);
			startActivity(intent);

			break;
		default:
			break;
		}

	}
**/	
	/*
	 * 请求书架推荐的
	 * */

//	private Detail detail;
	private List<String>bidList;
	private Map<String , String >bidMap;
	private String bid;
	private String coverUrl;
	private BookItem item;
	private void getRequestRecommand() {
//		 判断推荐
		DebugLog.e("请求首推", "用BookHelper.loadDetail更新书籍");
		final SharedPreferences mySharedPreferences = getActivity()
				.getSharedPreferences("everytime",
						Application.MODE_PRIVATE);
		Boolean isRecommand=mySharedPreferences.getBoolean("recommanded", false);
		        if(isRecommand) {
		        	
		        	return;   // 推荐过的话就直接return
		        }	        
//		int sex = AppData.getUser().getSex();
		
//		String url = AppData.getConfig().getUrl(Config.URL_BOOK_RECOMMAND) +"?tid=11&limit=9";
		String url = AppData.getConfig().getUrl(Config.URL_BOOK_RECOMMAND) +"1?channel="+channel+"&limit=9&type=1";
		
		if (bidList !=null) {
			return;
		}else {
			bidList=new ArrayList<String>();	
		}
//		
		getRequestQueue().add(new JsonObjectRequest(url, null, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub

				try {
					int status = response.getInt("status");
					if(StatusCode.OK == status) {
						JSONArray array = response.getJSONArray("data");
						final DBDataHelper helper = AppData.getDataHelper();
						for (int i = 0; i <array.length(); i++) {
//							for (int i = 0; i <1; i++) {
							JSONObject obj = array.getJSONObject(i);
							bid=obj.getString("aid");														
							item = new BookItem();																
							item.bid = obj.getString("aid");
							item.cid=obj.getString("cpid");
							item.name=obj.getString("title");
							item.author = obj.getString("author");
							item.status = obj.getInt("isfinish");
							item.wordNum=obj.getString("wordtotal");
							item.shortDesc=obj.getString("sortdescription");
							item.longDesc=obj.getString("longdescription");
							item.littleCoverUrl=obj.getString("smallimages");
//							try {
//								item.bigCoverUrl=obj.getString("myimages");								
//							} catch (Exception e) {
//								// TODO: handle exception
//								item.bigCoverUrl=obj.getString("bigimages");
//							}
							if (obj.getString("myimages").endsWith("null")) {
								item.bigCoverUrl=obj.getString("bigimages");
							}else {	
								String myimages=obj.getString("myimages");
								DebugLog.e("封面url", myimages);
								item.bigCoverUrl=AppData.getConfig().getUrl(Config.URL_BOOK_COVER) +myimages;
							}
							item.classFication=obj.getString("ydsortname");
							item.clickStr=obj.getString("totalviews");
							item.freeCount=obj.getInt("freechapternums");
							item.totalCount=obj.getInt("totalchapters");
							item.isUpdata=0;	
							item.lastTitle="";
//							bidMap.put(bid, coverUrl);
							DebugLog.e("推荐书id:"+bid, "本地封面url："+coverUrl);

							if (! AppData.getDataHelper().foundBookBid(item.bid)) {								
								DebugLog.e(item.name, "==数据库没有这本书，存上");								
							helper.insertKBBook(item);
							AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_BOOKSHELF);  // 让书架更新书架
							}							
							if (! bidList.contains(bid)) {								
								bidList.add(bid);
							}
						}
//							new Thread(new Runnable() {
//								
//								@Override
//								public void run() {
//									// TODO Auto-generated method stub
//									
//									for (int i = 0; i < bidList.size(); i++) {
////									String mBid=bidList.get(i);									
//									Detail detail = BookHelper.loadDetail(bidList.get(i));
//									item=AppData.getDataHelper().getBookID(bidList.get(i));
//									item = new BookItem();
//									if(detail !=null){										
//									item.bid = detail.getBid( );
//									item.cid=detail.getFirstCid();
//									item.name=detail.getName();
//									item.author = detail.getAuthor();
//									item.status = detail.getStatus();
//									item.wordNum=detail.getWord();
//									item.shortDesc=detail.getIntroduction();
//									item.longDesc=detail.getDesc();
//									item.littleCoverUrl=detail.getCoverUrl();
//									item.bigCoverUrl=detail.getBigCoverUrl();
//									item.classFication=detail.getClassification();
//									item.clickStr=detail.getClick();
//									item.freeCount=detail.getFreeChapterCount();
//									item.totalCount=detail.getTotalChapterCount();
//									item.isUpdata=0;
//									
////									Log.e("书籍条目：：：", item.bid+item.cid+item.name+item.author+
////											item.status+ item.wordNum+
////											item.shortDesc+item.longDesc+
////											item.littleCoverUrl+item.bigCoverUrl+item.classFication+
////											item.clickStr+ item.freeCount+item.totalCount+"");
//									if (! AppData.getDataHelper().foundBookBid(item.bid)) {
//										
//										DebugLog.e(item.name, "==数据库没有这本书，存上");
//										
//									helper.insertKBBook(item);
//									AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_BOOKSHELF);  // 让书架更新书架
//									}
//									
//									}
//								}								
									if (helper.getKbShelfList().size()>=bidList.size()) {
										SharedPreferences.Editor editor = mySharedPreferences.edit();
										editor.putBoolean("recommanded", true);
										editor.commit();
									}									
//									AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_BOOKSHELF);  // 让书架更新书架
//							}}).start();

						DebugLog.d(TAG, "推荐书籍获取成功");

						AppData.getUser().setRecommand(); //  %%%%%%表示已经推荐过了
						
//						SharedPreferences.Editor editor = mySharedPreferences.edit();
//
//						editor.putBoolean("recommanded", true);
//						editor.commit();
//						AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_BOOKSHELF);  // 让书架更新书架

					} else {
						DebugLog.d(TAG, "推荐书籍获取失败" + response.getString("msg"));
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, error.toString());
			}
		}));
		
	}
	/*
	 * 获取系统通知
	 */
	private void getRequestNotice() {
		String url = AppData.getConfig().getUrl(Config.URL_NOTICE);
		getRequestQueue().add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, response.toString());
//				DebugLog.e("获得了公告：：：", response);
				try {
					JSONObject jsonObject=new JSONObject(response);
					int status = jsonObject.getInt("status");
					if(StatusCode.OK == status) {

						JSONArray array = jsonObject.getJSONArray("data");

						List<Notice> list = AppData.getUser().getNoticeList();
						for(int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							Notice notice = new Notice();
							notice.title = obj.getString("title");
							notice.content = obj.getString("content");
							notice.date = obj.getString("date");
							notice.url = obj.getString("url");
							list.add(notice);
//							DebugLog.e("获得了公告：：长度为：", list+"");
//							AppData.getUser().setmNoticeList(list);
						}

						DebugLog.d(TAG, "公告消息获取成功");
						AppData.getClient().sendCallBackMsg(CallBackMsg.NOTICE_SUCCESSFUL);

					} else {

						DebugLog.d(TAG, "公告消息获取失败:" + jsonObject.getString(jsonObject.getString("msg")));
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					DebugLog.e(TAG, response.toString());
				}

			}
			}
		, new Response.ErrorListener() {  
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
//				hideProgress();
//				showToast("服务器异常", Toast.LENGTH_LONG);
//				finish();
//				overridePendingTransition(R.anim.move_left_in, R.anim.move_left_out);
			}}));}

	
	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	//
	// // return super.onCreateView(inflater, container, savedInstanceState);
	// DebugLog.d(TAG, "onCreate");
	// // 初始化奇悠
	// Iqiyoo.init(getActivity(), "M2040002", "boetech");
	// Iqiyoo.enableLog(true);
	// Iqiyoo.disableSmsBlock();
	// time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//
	// AppData.getClient().setCallBackHander(handler); // 发给全局 中间使者 readclient
	// 用于让服务间接控制他
	//
	// if (null == mRootView) {
	// mRootView = inflater.inflate(R.layout.bookshelf_main, container,
	// false);
	//
	// WindowManager manager=getActivity().getWindowManager();
	// mWidth=(manager.getDefaultDisplay().getWidth()-dip2px(getActivity(),
	// (45+45+30)))/3;
	// // mParams=new LayoutParams((int )mWidth, (int )(mWidth*4/3));
	// // mParams=new LayoutParams((int )mWidth, (int )(mWidth*4/3));
	// startShowNotice(); //展示公告
	//
	// /**
	// * 判断时间显示每日推荐
	// * */
	// SharedPreferences mySharedPreferences=
	// getActivity().getSharedPreferences("everytime",
	// Activity.MODE_WORLD_WRITEABLE);
	//
	// long str=mySharedPreferences.getLong("time", 0);
	// DebugLog.e("上次时间", str+"");
	// String lastHour=time.format(str).substring(11, 13);
	//
	// if (str==0) {
	//
	//
	// Date nowTime = new Date();
	// long time1=nowTime.getTime();
	//
	// // SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//
	// String dataString = time.format(nowTime);
	//
	// lastHour =dataString.substring(11, 13);
	// // String day = dataString.substring(8, 10);
	//
	//
	// SharedPreferences.Editor editor = mySharedPreferences.edit();
	// // Log.e("当前时间时间", time1+"");
	//
	// editor.putLong("time", time1);
	//
	// editor.commit();
	//
	// threadPw.start();
	//
	// }else {
	//
	// // Date curDate = new Date(System.currentTimeMillis());//获取当前时间
	//
	// long time2=new Date().getTime();
	// DebugLog.e("当前时间是@@几点？？？？？？", time2+"");
	// /**
	// * 每天首次推荐
	// * */
	// if (time2-str>(24-Integer.parseInt(lastHour))*60*60*1000) {
	// // if (time2-str>(24-Integer.parseInt(lastHour))*60) {
	//
	// DebugLog.e("当前时间大于舍得时间", time2-str+"");
	//
	// threadPw.start();
	// }
	//
	// /**
	// * 隔24小时推荐一次的方法
	// * */
	// // if (time2-str>=(long)5184000) {
	// // Log.e("时间之差", time2-str+"");
	// // threadPw.start();
	// //
	// // }
	//
	// SharedPreferences.Editor editor = mySharedPreferences.edit();
	// editor.putLong("time", time2);
	// editor.commit();
	//
	//
	//
	// }
	//
	//
	// initData();
	//
	// initView(mRootView);
	//
	// checkShelfView(); // 初始化书架
	//
	// checkOpenLastBook(); // 判断打开软件直接进入阅读
	//
	// // threadPw.start();
	// }
	//
	// ViewGroup parent = (ViewGroup) mRootView.getParent();
	// if (null != parent) {
	// parent.removeView(mRootView);
	// }
	//
	// return mRootView;
	// }
	// 首推的pw
	private Thread threadPw = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 开始拿每日推荐
			RequestQueue queue = AppData.getRequestQueue();
			queue.add(new StringRequest(AppData.getConfig().getUrl(
					Config.URL_EVERYDAY_COMMENT)
					
					+ "1?channel="+channel+"&limit=1&type=2", new Listener<String>() {

				@Override
				public void onResponse(String response) {
					// TODO Auto-generated method stub
					// DebugLog.e("每日推荐开始加载——", response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONArray array = jsonObject.getJSONArray("data");
						JSONObject jsonObject2 = array.getJSONObject(0);
						String ysUrl=jsonObject2.getString("myimages");
						String urlString=null;
						if (ysUrl.endsWith("null")) {							
							urlString = addUrl(jsonObject2
									.getString("bigimages"));
						}else {
							String str=AppData.getConfig().getUrl(Config.URL_BOOK_COVER) +jsonObject2
							.getString("myimages");
							urlString = addUrl(str);
						}
						ydBid = jsonObject2.getString("ydaid");
						String bookName = jsonObject2.getString("mybookname");
//						String bookName = jsonObject2.getString("title");
						String longDesc = jsonObject2
								.getString("sortdescription");
						String mWord = jsonObject2.getString("mydescription");
						showEveryDayPopuwidonw(urlString, bookName, mWord,
								bookName);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub

				}
			}));

		}
	});

	private void initData() {

		initImageCacheCount(20);

		mIsGrid = AppData.getConfig().isIsGrid();// 判断是不是网格布局
	}

	private TextView tvSelectAll;
	private TextView tvDelete;
	private TextView tvCancel;

	private void initView(View v) {
		btn_back = (Button) v.findViewById(R.id.search_back);
		btn_back.setVisibility(View.GONE);
		btn_bookStore = (RelativeLayout) v.findViewById(R.id.boyi_book);
		top_title = (TextView) v.findViewById(R.id.search_top_title_tv);
		top_title.setText("我的书架");
		btn_bookStore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentActivity parent2 = (FragmentActivity) getActivity();
				Intent intent = new Intent(getActivity(), StoreMain.class);
				startActivity(intent);
			}
		});

		mNotifyTv = (TextView) v.findViewById(R.id.bookshelf_notify_tv);

		mGridView = (ShelfGridView) v.findViewById(R.id.bookshelf_gridview);

		mGridView.setVerticalSpacing(dip2px(getActivity(), 46));

		// bar=(ProgressBar) v.findViewById(R.id.progressBar_booklist);

		// mListView = (ListView)v.findViewById(R.id.bookshelf_listview);
		// pulllistview = (PullToRefreshListView) v
		// .findViewById(R.id.pull_listview);
		// mListView = pulllistview.getRefreshableView();
//		mEmptyView = v.findViewById(R.id.bookshelf_empty_layout);
//		mOperatorView = v.findViewById(R.id.bookshelf_operator_layout);
//		tvSelectAll = (TextView) mOperatorView.findViewById(R.id.all_tv);
//		tvDelete = (TextView) mOperatorView.findViewById(R.id.delete_tv);
//		tvCancel = (TextView) mOperatorView.findViewById(R.id.cancel_tv);
		Button btnGoStore = (Button) v.findViewById(R.id.bookshelf_gostore_btn);
		btnGoStore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentActivity parent = (FragmentActivity) getActivity();
				// if (parent instanceof MainActivity) {
				// ((MainActivity) parent).setPage(Page.bookstore);
				// }
			}
		});

		mNotifyTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != mNotice) {
					// showToast("click on:" + mNotice.url, Toast.LENGTH_LONG);
					final String xName = mNotice.url.substring(mNotice.url
							.lastIndexOf("=") + 1);

					handler.post(new Runnable() {

						public void run() {
							try {

								startBookDetil(xName,mNotice.url);

							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}

						}

					});

				}

			}
		});

		mBookList = AppData.getDataHelper().getKbShelfList(); // 从数据库中取书籍 ，
//		if (mBookList.size() > 0) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
//					mBookList = AppData.getDataHelper().getKbShelfList(); // 从数据库中取书籍 ，
					if (mBookList.size() > 0) {						
						getRequestUpdate(); // 更新书籍
					}					
				}
			}).start();

//		}

		mDelteArray = new SparseIntArray();
		mGridAdapter = new BookshelfGridAdapter(this.getActivity());
		mGridView.setAdapter(mGridAdapter);
		mGridView.onTouchModeChanged(false);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mGridAdapter.isSelecteMode()) {
					int value = mDelteArray.get(position, DELETE_UNSELECTE);
					if (DELETE_UNSELECTE == value) {
						mDelteArray.append(position, DELETE_SELECTEED);
					} else {
						mDelteArray.delete(position);
					}
					// mGridAdapter.notifyDataSetChanged();
					mGridView.setAdapter(mGridAdapter);

				} else {
					if (position == mBookList.size() + 1) { // 最后一个添加符号

						// 直接去书城
						FragmentActivity parent2 = (FragmentActivity) getActivity();
						Intent intent = new Intent(getActivity(),
								StoreMain.class);
						startActivity(intent);

					} else if (position == mBookList.size()) {
						showPopupAddBook();

					} else {
						// 取消更新的标签
						BookItem item = mBookList.get(position);
						if (item.isUpdata == 1) {
							item.isUpdata=0;
//							AppData.getDataHelper().updateOnlineBook(item.bid, item.lastChapterPos, 
//									item.lastPosition, item.totalCount, 
//									item.bigCoverUrl,item.isUpdata);
							AppData.getDataHelper().updateQoveBook(item.bid,item.status,item.isUpdata, item.totalCount);
							
						}
						startReadingActivity(position);
					}
				}

			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position >= mBookList.size()) {
					return false;
				}
				showPopupGridAction(position);

				return true;
			}
		});

		// mListView.setAdapter(mListAdapter);

		tvSelectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeSlectMode();
			}
		});

		tvDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 删除grid中选中的书籍
				Collection<BookItem> collection = new ArrayList<BookItem>();
				while (mDelteArray.size() > 0) {
					int key = mDelteArray.keyAt(0);
					BookItem item = mBookList.get(key);
					AppData.getDataHelper().deleteBook(item.id);
					collection.add(item);
					mDelteArray.removeAt(0);

				}
				mBookList.removeAll(collection);

				// mGridAdapter.notifyDataSetChanged();
				mGridView.setAdapter(mGridAdapter);

				if (mBookList.size() == 0) {

					mGridAdapter.setSelecteMode(false);

				}

				checkShelfView();
			}
		});

		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mGridAdapter.setSelecteMode(false);
				// mGridAdapter.notifyDataSetChanged();
				mGridView.setAdapter(mGridAdapter);

			}

		});

	}

	// 展示书架的方法 调用1.更新完数据以后，在handler的处理中调用

	private void changeSlectMode() {

		if (tvSelectAll.getText().equals("全选")) {
			for (int i = 0; i < mBookList.size(); i++) {

				mDelteArray.append(i, DELETE_SELECTEED);

			}
			tvSelectAll.setText("反选");
		} else {
			for (int i = 0; i < mBookList.size(); i++) {
				// 如果被选定了
				if (mDelteArray.get(i, DELETE_UNSELECTE) == DELETE_SELECTEED) {
					mDelteArray.delete(i);
				} else {
					mDelteArray.append(i, DELETE_SELECTEED);
				}
			}
			tvSelectAll.setText("全选");
		}
		// mGridAdapter.notifyDataSetChanged();
		mGridView.setAdapter(mGridAdapter);

	}

	private void showShelfView() {
		if (mIsGrid) {
			mGridView.setVisibility(View.VISIBLE);
			// mListView.setVisibility(View.GONE);
			// pulllistview.setVisibility(View.GONE);

		} else {
			mGridView.setVisibility(View.GONE);
			// pulllistview.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.VISIBLE);
		}

		mGridAdapter.setSelecteMode(false);// 设置其适配器条目长点击菜单是否打开
	}

	private void hideShelfView() {
		mGridView.setVisibility(View.GONE);
		// mListView.setVisibility(View.GONE);
	}

	private void checkShelfView() {
		// if (mBookList.size() == 0) {
		// mEmptyView.setVisibility(View.VISIBLE);
		// hideShelfView();
		// } else {
		mEmptyView.setVisibility(View.GONE);
		showShelfView();
		// }
	}

	/**
	 * 展示公告详情
	 * */
	private void showNotice(Notice notice) {
		if (null == notice || mNotice == notice) {
			return;
		}
		mNotice = notice;
		mNotifyTv.setText(mNotice.content);
	}

	private void checkOpenLastBook() { // 继续上次阅读的界面
		if (AppData.getUser().isIsOpenLast()) {
			int id = AppData.getUser().getLastBookID();
			for (int i = 0; i < mBookList.size(); i++) {
				if (id == mBookList.get(i).id) {
					startReadingActivity(i);
					break;
				}
			}
		}
	}

	private void startReadingActivity(int position) { // 有gridView点击事件调用
		BookItem item = mBookList.get(position); // 从书的集合中得到
		mReadIndex = position;

		item.times++;
		if (item.onlineID == BookItem.ON_LOCAL_TXT) {
			AppData.getDataHelper().updateLocalBookTimes(item.path, item.times);
		} else {
			AppData.getDataHelper().updateOnlineBookTimes(item.onlineID,
					item.times);
		}

		Intent intent = new Intent(getActivity(), OnlineReadingActivity.class);
		intent.putExtra("BookItem", item);

		startActivityForResult(intent, PageID.Bookshelf);
	}

	private void startBookDetailActivity(int position) {
		BookItem item = mBookList.get(position);
		if (item.onlineID != BookItem.ON_LOCAL_TXT) {
			startReadingActivity(position);
//			startBookDetil(item.bid);
		}
		// else {
		// showToast("这是本地书籍", Toast.LENGTH_SHORT);
		// }
	}

	private void startShareActivity(int position) {
		BookItem item = mBookList.get(position);
		if (item.onlineID == BookItem.ON_LOCAL_TXT) {
			showToast("本地书籍不支持分享", Toast.LENGTH_LONG);

		} else {
			// Intent intent = new Intent(getActivity(), WXEntryActivity.class);
			// intent.putExtra("BookItem", item);
			// // hidePopupGirdAction();
			// startActivity(intent);
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		DebugLog.d(TAG, "onStart");

		// if (AppData.getUser().isNotice()) {
		// mNotifyTv.setVisibility(View.VISIBLE);
		// startShowNotice();
		// } else {
		// mNotifyTv.setVisibility(View.INVISIBLE);
		// stopShowNotice();
		startShowNotice();
		// }
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		stopShowNotice();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DebugLog.d(TAG, "onDestory");

		AppData.getConfig().setIsGrid(mIsGrid);
	}

	/**********************************************************************************************************/
	// 书架的帮助页面
	private PopupWindow mPopupWindowAddBook = null;

	private void showPopupAddBook() {
		if (null == mPopupWindowAddBook) {
			mPopupWindowAddBook = new PopupWindow(getAddBookView(),
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			mPopupWindowAddBook.setFocusable(true);
			mPopupWindowAddBook.setTouchable(true);
			mPopupWindowAddBook.setOutsideTouchable(true);
			mPopupWindowAddBook.setBackgroundDrawable(new BitmapDrawable()); // 加上该语句后
																				// 可在popupWindow外点击
			mPopupWindowAddBook.setBackgroundDrawable(null);
		}
		mPopupWindowAddBook.showAtLocation(mGridView, Gravity.NO_GRAVITY, 0, 0);

	}

	private View getAddBookView() {
		if (null == addBookView) {
			addBookView = LayoutInflater.from(getActivity()).inflate(
					R.layout.boyi_shelf_help, null);
			addBookView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (null != mPopupWindowAddBook) {
						mPopupWindowAddBook.dismiss();
					}
				}
			});

		}

		return addBookView;
	}

	/*****************************************************************************************************/

	private PopupWindow mPopupWindowGridAction = null;

	private void showPopupGridAction(int position) {
		if (null == mPopupWindowGridAction) {
			mPopupWindowGridAction = new PopupWindow(getGridActionView(),
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			mPopupWindowGridAction.setFocusable(true);
			mPopupWindowGridAction.setTouchable(true);
			mPopupWindowGridAction.setOutsideTouchable(true);
			mPopupWindowGridAction.setBackgroundDrawable(new BitmapDrawable());

		}

		setGridActionData(position);
		mPopupWindowGridAction.showAtLocation(mGridView, Gravity.NO_GRAVITY, 0,
				0);
	}

	private void hidePopupGirdAction() {
		if (null != mPopupWindowGridAction) {
			mPopupWindowGridAction.dismiss();
		}
	}

	private View gridActionView = null;
	private View addBookView = null;
	private TextView bookNameTv, descTv, bookSizeTv, bookProgressTv;
	private Button btnDetail, btnDelete;
	private NetworkImageView bookCover;

	private View getGridActionView() {

		if (null == gridActionView) {
			gridActionView = LayoutInflater.from(getActivity()).inflate(
					R.layout.boyi_shelf_grid_action, null);
			gridActionView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					hidePopupGirdAction();
				}
			});

			bookCover = (NetworkImageView) gridActionView
					.findViewById(R.id.cover_book);
			bookNameTv = (TextView) gridActionView
					.findViewById(R.id.book_detail_words);
			descTv = (TextView) gridActionView.findViewById(R.id.book_detail_Desc);
//			bookSizeTv = (TextView) gridActionView.findViewById(R.id.size_tv);
//			bookProgressTv = (TextView) gridActionView
//					.findViewById(R.id.progress_tv);

			btnDetail = (Button) gridActionView.findViewById(R.id.book_detail_lookgo);
			btnDelete = (Button) gridActionView.findViewById(R.id.book_detail_delete);
		}

		return gridActionView;
	}

	private void setGridActionData(final int position) {
		final BookItem item = mBookList.get(position);

		bookCover.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
		bookCover.setErrorImageResId(R.drawable.boyi_ic_cover_default);
		bookCover.setImageUrl(item.bigCoverUrl, getImageLoader());

		bookNameTv.setText(item.name);
		descTv.setText(item.shortDesc);
//		bookSizeTv.setText("简介:" + item.shortDesc);
//		bookProgressTv.setText("阅读进度:" + item.getPercent());
//		bookProgressTv.setVisibility(View.GONE);

		btnDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startBookDetailActivity(position);
				hidePopupGirdAction();
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteBook(position);
				hidePopupGirdAction();

			}
		});
	}

	private PopupWindow everyPW;
	private NetworkImageView everyCover;
	private TextView everyWord, everyDesc;
	private Button buttonYes, buttonNo;
	private ImageView close_popupWindow;

	private void holderEveryDayPw() {
		if (everyPW != null && everyPW.isShowing()) {
			everyPW.dismiss();
			everyPW = null;
		}
	}

	protected void showEveryDayPopuwidonw(String url, String bookName,
			String word, String mDesc) {

		// DebugLog.e("展示每日推荐的pw", "pw 开始加载");

		everyDay = View.inflate(getActivity(), R.layout.boyi_shelf_every_day,
				null);
		if (everyPW == null) {
			everyPW = new PopupWindow(everyDay,
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			// DebugLog.e("展示每日推荐的pw", "pw 开始加载");
			everyPW.setFocusable(true);
			everyPW.setTouchable(true);
			everyPW.setOutsideTouchable(true);
			everyPW.setBackgroundDrawable(new BitmapDrawable());
		}

		everyCover = (NetworkImageView) everyDay
				.findViewById(R.id.everyday_cover_book);
		everyCover.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
		everyCover.setErrorImageResId(R.drawable.boyi_ic_cover_default);
		everyCover.setImageUrl(url, getImageLoader());

		// everyBookName = (TextView) everyDay
		// .findViewById(R.id.everyday_comment_tv);
		// everyBookName.setText(bookName);

		everyWord = (TextView) everyDay.findViewById(R.id.everyday_words);
//		everyWord.setText(word);
		everyWord.setText(mDesc);
		everyDesc = (TextView) everyDay.findViewById(R.id.everyday_book_Desc);
		everyDesc.setText(word);
//		everyDesc.setText(mDesc);

		buttonYes = (Button) everyDay.findViewById(R.id.everyday_lookgo);
		buttonNo = (Button) everyDay.findViewById(R.id.everyday_look_no);

		buttonYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// showToast("进入详情界面", Toast.LENGTH_SHORT);
                AppData.goBoyiSdk(AppData.ENTRY_CODE_NAVIJATION,
						AppData.ENTRY_TYPE_READBOOK, ydBid, getActivity(), "",
						"");
				holderEveryDayPw();

			}
		});
		buttonNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// showToast("进入详情界面", Toast.LENGTH_SHORT);
				holderEveryDayPw();
			}
		});
		close_popupWindow = (ImageView) everyDay
				.findViewById(R.id.close_popupWindow);
		close_popupWindow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				holderEveryDayPw();
			}
		});

		everyPW.showAtLocation(mGridView, Gravity.CENTER, 0, 0);

	}

	/**
	 * 开启详情页
	 * */
	private void startBookDetil(String bid,String url) {
		// TODO Auto-generated method stub
		GetTaskItem task = new GetTaskItem("合成item的线程", bid,url);
		AppData.getClient().getTaskManagerRead().addTask(task);
		
//		Intent intent = new Intent(getActivity(), BookDetail.class);
//		intent.putExtra("bid", bid);
//		startActivity(intent);

	}
	// 合成item
		private Detail detailHC;
		private BookItem itemHC;

		public class GetTaskItem extends CallBackTask {
			private String mBid;
			private String mUrl;
			public GetTaskItem(String strTaskName, String bid,String url) {
				super(strTaskName);
				// TODO Auto-generated constructor stub
				this.mBid = bid;
				this.mUrl = url;
			}

			@Override
			protected void doTask() {

				getBookItem(mBid,mUrl);
				Intent intent = new Intent(getActivity(),
						OnlineReadingActivity.class);
				intent.putExtra("BookItem", item);
				startActivityForResult(intent, PageID.Bookshelf);

			}
		}
		private BookItem getBookItem(String bid,String url) {
			// TODO Auto-generated method stub
			detail = BookHelper.loadDetail(bid);

			item = new BookItem();
			if (detail != null) {
				item.bid = detail.getBid();
				item.cid = detail.getFirstCid();
				item.name = detail.getName();
				item.author = detail.getAuthor();
				item.status = detail.getStatus();
				item.wordNum = detail.getWord();
				item.shortDesc = detail.getIntroduction();
				item.longDesc = detail.getDesc();
				item.littleCoverUrl = detail.getBigCoverUrl();
//				item.littleCoverUrl = detail.getCoverUrl();				
//				item.bigCoverUrl = url;
				item.bigCoverUrl = detail.getBigCoverUrl();
				item.classFication = detail.getClassification();
				item.clickStr = detail.getClick();
				item.freeCount = detail.getFreeChapterCount();
				item.totalCount = detail.getTotalChapterCount();
			} else {
				DebugLog.e("得到的item", "为空");
			}
			return item;

		}
	
	private PopupWindow pw;
	private ImageView shareQq;
	private ImageView shareWx;
	private View view;
	private View everyDay;

	private void dismissPop() {
		if (pw != null && pw.isShowing()) {
			pw.dismiss();
			pw = null;
		}
	}

	/*****************************************************************************************************/

	private class BookshelfGridAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		private boolean isSelect = false;

		public BookshelfGridAdapter(Context context) {

			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mBookList.size() + 2;
		}

		@Override
		public BookItem getItem(int position) {
			// TODO Auto-generated method stub
			return mBookList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder;
			if (null == convertView) {
				convertView = mLayoutInflater.inflate(
						R.layout.boyi_bookshelf_grid_item, parent, false);
				// AbsListView.LayoutParams params=new
				// LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				// dip2px(getActivity(), 79));
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, dip2px(
								getActivity(), 96));
				convertView.setLayoutParams(params);
				holder = new ViewHolder();
				holder.cover = (ImageView) convertView
						.findViewById(R.id.cover_niv);
				// holder.cover.setLayoutParams(params);
				holder.shadow = (ImageView) convertView
						.findViewById(R.id.cover_shadow_iv);
				holder.select = (ImageView) convertView
						.findViewById(R.id.cover_select_iv);

				holder.status = (ImageView) convertView
						.findViewById(R.id.status_iv);
				holder.bookname = (TextView) convertView
						.findViewById(R.id.bookname_tv);
				// holder.progress =
				// (TextView)convertView.findViewById(R.id.read_progress_tv);
				holder.layout = (RelativeLayout) convertView
						.findViewById(R.id.rl_bg);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			mParams = holder.layout.getLayoutParams();
			mParams.width = (int) mWidth;
			mParams.height = (int) mWidth * 4 / 3;
			// mParams.height=(int) mWidth;
			holder.layout.setLayoutParams(mParams);
			// // test
			// BadgeView badge = new BadgeView(getActivity(), holder.cover);
			// badge.setText("11");
			// badge.show();

			if (isSelect) {
				// 整理书架时，隐藏+号
				if (position == mBookList.size()
						|| position == mBookList.size() + 1) {
					// 隐藏掉
					convertView.setVisibility(View.GONE);
				} else {
					holder.shadow.setVisibility(View.VISIBLE);
					holder.select.setVisibility(View.VISIBLE);

					int value = mDelteArray.get(position, DELETE_UNSELECTE);
					// if (DELETE_UNSELECTE == value) {
					// holder.select
					// .setImageResource(R.drawable.radio_button_unselecte);
					// } else {
					// holder.select
					// .setImageResource(R.drawable.radio_button_selected);
					// }
				}
			} else {
				if (position == mBookList.size()
						|| position == mBookList.size() + 1) {
					// 显示出来
					convertView.setVisibility(View.VISIBLE);
				}
				holder.shadow.setVisibility(View.INVISIBLE);
				holder.select.setVisibility(View.INVISIBLE);
			}

			if (position < mBookList.size()) {

				BookItem item = getItem(position);

				if (item.onlineID == BookItem.ON_LOCAL_TXT) {
					// 本地TXT书籍
					holder.cover.setTag("local_txt");

					// holder.status.setImageResource(R.drawable.bendi);
					// holder.cover.setImageResource(R.drawable.bdfm);
					holder.status.setVisibility(View.VISIBLE);
				} else {
					holder.cover.setTag(item.littleCoverUrl);
					ListImageListener listener = new ListImageListener(
							holder.cover, R.drawable.boyi_ic_cover_default,
							R.drawable.boyi_ic_cover_default,
							item.littleCoverUrl);
					if (item.bigCoverUrl.equals("http://sdk.boetech.cn/Uploads/null")) {
						getImageLoader().get(item.littleCoverUrl, listener);
					}else {
						
						getImageLoader().get(item.bigCoverUrl, listener);
					}

					if (item.isUpdata == 1) { // 判断更新状态
						holder.status.setImageResource(R.drawable.boyi_update_tubiao);
						holder.status.setVisibility(View.VISIBLE);

					} else {
						// 连载状态
						if (item.status == BookItem.STATUS_SERIAL) {
//							holder.status
//									.setImageResource(R.drawable.boyi_continue);
							holder.status.setVisibility(View.VISIBLE);

						}
						// 完结
//						else if (item.status == BookItem.STATUS_FINISH) {
//							holder.status.setVisibility(View.VISIBLE);
//							holder.status
//									.setImageResource(R.drawable.boyi_finish);
//						} else {
//							holder.status.setVisibility(View.INVISIBLE);
//						}
					}

					// 未读章节数,上角标
				}
				holder.bookname.setText(item.name);
				// holder.progress.setText("已读" + item.getPercent());
			} else if (position == mBookList.size()) {

				holder.cover.setTag("store_gift");
				holder.cover.setImageResource(R.drawable.boyi_gift);
				holder.bookname.setText("");
				holder.status.setVisibility(View.INVISIBLE);

			} else {
				holder.cover.setTag("add_book");
				holder.cover.setImageResource(R.drawable.boyi_addbook);
				holder.bookname.setText("");
				holder.status.setVisibility(View.INVISIBLE);

			}

			return convertView;
		}

		public boolean isSelecteMode() {
			return this.isSelect;
		}

		public void setSelecteMode(boolean select) {

			if (this.isSelect == select) { // 用于设置config配置文件

				return;
			}

			this.isSelect = select;
			if (isSelect) {
				mDelteArray.clear();
				// mOperatorView.setVisibility(View.VISIBLE);
				// getActivity().findViewById(R.id.main_bottom_menu)
				// .setVisibility(View.GONE);
			} else {
				mOperatorView.setVisibility(View.GONE);
				// getActivity().findViewById(R.id.main_bottom_menu)
				// .setVisibility(View.VISIBLE);
			}
			notifyDataSetChanged();
			// mGridView.setAdapter(mGridAdapter);
		}

		private class ViewHolder {
			ImageView cover;
			ImageView shadow;
			ImageView select;
			RelativeLayout layout;
			ImageView status;
			TextView bookname;
			TextView progress;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		DebugLog.d(TAG, "onActivityResult");

		// hidePopupMore();

		switch (resultCode) {
		case ResultCode.ADD_MULTI_TO_BOOKSHELF: {
			if (mEmptyView.getVisibility() == View.VISIBLE) {
				mEmptyView.setVisibility(View.GONE);
				showShelfView();
			}
			mBookList = AppData.getDataHelper().getBookShelfList();

			mGridAdapter.notifyDataSetChanged();
		}

			break;

		case ResultCode.ADD_ONE_TO_BOOKSHELF: {
			if (mEmptyView.getVisibility() == View.VISIBLE) {
				mEmptyView.setVisibility(View.GONE);
				showShelfView();
			}

			// mBookList = AppData.getDataHelper().getBookShelfList();

			BookItem item1 = (BookItem) data.getSerializableExtra("BookItem");
			mBookList.add(item1);
			Collections.sort(mBookList);

			// mBookList.
			mGridAdapter.notifyDataSetChanged();
		}

			break;
		case ResultCode.UPDATE_LASTREAD:
			BookItem item2 = (BookItem) data.getSerializableExtra("BookItem");
			BookItem update = mBookList.get(mReadIndex);
			update.lastPosition = item2.lastPosition;
			update.lastChapterPos = item2.lastChapterPos;
			update.chapterTotal = item2.chapterTotal;

			mGridAdapter.notifyDataSetChanged();

			break;
		case ResultCode.CHANGE_USER: {
			mBookList = AppData.getDataHelper().getBookShelfList();
			if (mEmptyView.getVisibility() == View.VISIBLE
					&& mBookList.size() > 0) {
				mEmptyView.setVisibility(View.GONE);
				showShelfView();
			}

			mGridAdapter.notifyDataSetChanged();
		}
			break;
		case ResultCode.OPEN_BOOK_FAILED: {
			showToast("该书籍打开失败,或已不存在", Toast.LENGTH_LONG);
		}
			break;
		case ResultCode.CONTENT_NOT_FOUND: {
			Intent intent = new Intent(getActivity(),
					LocalReadingActivity.class);
			BookItem item = mBookList.get(mReadIndex);
			intent.putExtra("BookItem", item);
			startActivityForResult(intent, PageID.Bookshelf);
		}
			break;

		default:
			DebugLog.d(TAG,
					"unkown result code:" + Integer.toHexString(resultCode));
			break;
		}

	}

	private void deleteBook(int position) {

		BookItem item = mBookList.get(position);
		if (item.onlineID == BookItem.ON_LOCAL_TXT) {

//			AppData.getDataHelper().deleteBook(item.id);
			AppData.getDataHelper().deleteQBBook(item.id+"");
			// 删除缓存
			String path = AppData.getConfig().getLocalContentsFilePath(item.id);
			File f = new File(path);
			f.delete();
			// String path =
			// AppData.getConfig().getOnlineBookDir(item.onlineID);
			// File dir = new File(path);
			// FileUtil.deleteDir(dir);

		} else {
			AppData.getDataHelper().deleteQBBook(item.bid);
			String path = AppData.getConfig().getOnlineBookDir(item.onlineID);
			File dir = new File(path);
			FileUtil.deleteDir(dir);
            
		}

		mBookList.remove(position);
		mGridAdapter.notifyDataSetChanged();

		checkShelfView();
	}

	private final static int TIMER_NOTICE = 10000;
	private Timer timerNotice = null;
	private TimerTask timerTask = null;
	private int noticeIndex = 0;
	private boolean isCancel = false;

	private void startShowNotice() {

		if (AppData.getUser().getNoticeList().size() <= 0) {
			return;
		}
		noticeIndex = 0;

		timerNotice = new Timer();

		isCancel = false;
		timerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!isCancel) {
					Notice obj = AppData.getUser().getNoticeList()
							.get(noticeIndex);
					AppData.getClient().sendCallBackMsg(
							CallBackMsg.NOTICE_SHOW_NEXT, obj);

					noticeIndex = (noticeIndex + 1)
							% (AppData.getUser().getNoticeList().size());
				}
			}
		};

		timerNotice.schedule(timerTask, 0, TIMER_NOTICE);

		DebugLog.d(TAG, "timer notice start");
	}

	private void stopShowNotice() {
		isCancel = true;
		if (null != timerTask) {
			timerTask.cancel();
			timerTask = null;
		}

		if (null != timerNotice) {

			timerNotice.cancel();
			timerNotice = null;
		}

		DebugLog.d(TAG, "timer notice stop");
	}

	/**
	 * 更新书架书籍连载信息
	 * */
	private void getRequestUpdate() {

		for (int i = 0; i < mBookList.size(); i++) {

			BookItem item = mBookList.get(i);

			BookUpdateInfo info = BookHelper.loadBookUpdateInfo(item.bid,
					item.cid);

			if (info != null) {
				
                
				item.status = info.status; // (1完本 ,0连载)

				item.cid = info.nextCid; // 下一章cid
                
				int updata = info.leftCount - (item.totalCount - 1); // >0说明是更新了，
				DebugLog.e("书籍" + info.bid, "新总章节数" + (info.leftCount+1)
						+ "本地存储的章节数是:" + item.totalCount);
				if (updata > 0) {
					item.isUpdata = 1; // 更新了
					AppData.getDataHelper().updateQoveBook(item.bid,
							item.status, item.isUpdata, info.leftCount+1);
//					AppData.getDataHelper().updateOnlineBook(item.bid, item.lastChapterPos, 
//							item.lastPosition, item.totalCount, 
//							item.bigCoverUrl,item.isUpdata);
//					handler.sendMessage(msg);
					
					AppData.getClient().sendCallBackMsg(
							CallBackMsg.UPDATE_BOOKSHELF); // 数据更新完毕存到数据库后，发一个消息用于重新获取书籍
				} else {
					item.isUpdata = 0; // 没有更新
				}
				// AppData.getDataHelper().updateQoveBook(item.bid,
				// item.status,item.isUpdata,info.leftCount+1
				// );
				//
				// AppData.getClient().sendCallBackMsg(
				// CallBackMsg.UPDATE_BOOKSHELF); // 数据更新完毕存到数据库后，发一个消息用于重新获取书籍

			}
		}

	}

	// Map<String, String> map = new HashMap<String, String>();
	// map.put("token", token);
	//
	// JSONObject obj = new JSONObject();
	// try {
	// JSONArray array = new JSONArray();
	// for (int i = 0; i < mBookList.size(); i++) {
	//
	// BookItem item = mBookList.get(i);
	// if (item.onlineID != BookItem.ON_LOCAL_TXT) {
	// JSONObject json = new JSONObject();
	// json.put("id", item.onlineID);
	// json.put("chapter_id", item.chapterTotal);
	// array.put(json);
	//
	// }
	// }
	// obj.put("booklist", array);
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// map.put("booklist", obj.toString());
	//
	// getRequestQueue().add(
	// new JsonObjectPostRequest(url, new Listener<JSONObject>() {
	//
	// @Override
	// public void onResponse(JSONObject response) {
	// // TODO Auto-generated method stub
	// DebugLog.d(TAG, response.toString());
	//
	// try {
	// int status = response.getInt("status");
	//
	// if (StatusCode.OK == status) {
	// // 1.更新数据库
	// JSONArray data = response.getJSONArray("data");
	// DBDataHelper helper = AppData.getDataHelper();
	//
	// for (int i = 0; i < data.length(); i++) {
	// JSONObject obj = data.getJSONObject(i);
	// int id = obj.getInt("id");
	// int lastChapterPos = obj
	// .getInt("lastChapter");
	// int lastPosition = obj
	// .getInt("lastPosition");
	// // int number = obj.getInt("number");
	// String detailUrl = obj.getString("url");
	// int chapterTotal = obj.getInt("chapter_id");
	//
	// helper.updateOnlineBook(id, lastChapterPos,
	// lastPosition, chapterTotal,
	// detailUrl);
	// }
	//
	// AppData.getClient().sendCallBackMsg(
	// CallBackMsg.UPDATE_BOOKSHELF); // 数据更新完毕存到数据库后，发一个消息用于重新获取书籍
	//
	// } else {
	// DebugLog.d(
	// TAG,
	// "书籍更新信息获取失败:"
	// + response.getString("msg"));
	// }
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }, new ErrorListener() {
	//
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// // TODO Auto-generated method stub
	// DebugLog.d(TAG, error.toString());
	// }
	//
	// }, map));

	public void chageTrim(int i) {

		switch (i) {
		case 0:
			// 0 是更改横竖切换
			if (mBookList.size() > 0) {
				mIsGrid = !mIsGrid; //
				showShelfView();
			}

			break;
		case 1:
			if (mBookList.size() > 0) {
				mGridAdapter.setSelecteMode(true);

			}

			break;
		case 2:
			if (mGridAdapter.isSelecteMode()) {
				mGridAdapter.setSelecteMode(false);
			}

			// Intent intent = new Intent(getActivity(),
			// FileBrowserActivity.class);
			// startActivityForResult(intent, PageID.Bookshelf);

			break;

		default:
			break;
		}
	}

	private String imageUrl;

	private String addUrl(String str) {

		String xName = str.substring(str.lastIndexOf("/") + 1);
		// System.out.println("中文部分"+xName);
		String filename = str.substring(0, str.length() - xName.length());
		// System.out.println("英文部分"+filename);
		try {
			URLEncoder.encode(str.substring(str.lastIndexOf("/") + 1), "utf_8");
			String imageUrl = filename
					+ URLEncoder.encode(
							str.substring(str.lastIndexOf("/") + 1), "utf_8");
			return imageUrl;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return imageUrl;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mBookList = AppData.getDataHelper().getKbShelfList();
		

		if (mEmptyView.getVisibility() == View.VISIBLE && mBookList.size() > 0) {

			mEmptyView.setVisibility(View.GONE);

			showShelfView(); // 更新书籍 后初始化书架
		}
		mGridAdapter.notifyDataSetChanged();
		
		// pulllistview.onRefreshComplete();

	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
