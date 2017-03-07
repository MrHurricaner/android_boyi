package com.boyiqove.ui.bookshelf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.LoginHelper;
import com.boyiqove.R;
import com.boyiqove.ResultCode;
import com.boyiqove.config.Config;
import com.boyiqove.config.DeviceInfo;
import com.boyiqove.config.ReadConfig;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.LocalChapterInfo;
import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.entity.OnlineChapterInfo.Status;
import com.boyiqove.library.book.BookCache;
import com.boyiqove.library.book.BookCacheManager;
import com.boyiqove.library.book.BookView.PageIndex;
import com.boyiqove.library.book.OnlineBookCache;
import com.boyiqove.library.book.OnlineBookFactory;
import com.boyiqove.library.book.OnlineBookFactory.OnDrawListener;
import com.boyiqove.library.book.PageWidget;
import com.boyiqove.library.book.PageWidget.OnSizeChangedListener;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.toolbox.ImageLoader;
import com.boyiqove.library.volley.toolbox.NetworkImageView;
import com.boyiqove.library.volley.toolbox.StringRequest;
import com.boyiqove.protocol.JsonObjectPostRequest;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.task.CallBackMsg;
import com.boyiqove.task.CallBackTask;
import com.boyiqove.task.CheckContentsTask;
import com.boyiqove.task.OutputFileTask;
import com.boyiqove.task.OutputObjectTask;
import com.boyiqove.task.Task;
import com.boyiqove.task.TaskManager;
import com.boyiqove.ui.bookstore.BookDetail;
import com.boyiqove.ui.bookstore.StoreMain;
import com.boyiqove.ui.storeadapter.ContentAdapter;
import com.boyiqove.ui.storeutil.CMChargeActivity;
import com.boyiqove.ui.storeutil.JsonUtil;
import com.boyiqove.util.AES;
import com.boyiqove.util.CommonUtil;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.FileUtil;
import com.boyiqove.util.GetBookDetailUtil;
import com.boyiqove.util.GetDirectoryUtil;
import com.boyiqove.view.BaseActivity;
import com.boyiqove.view.MyAlert;

import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.Iqiyoo;
import com.bytetech1.sdk.chapter.Chapter;
import com.bytetech1.sdk.chapter.ChargeChapter;
import com.bytetech1.sdk.chapter.ContentChapter;
import com.bytetech1.sdk.chapter.LoginChapter;
import com.bytetech1.sdk.chapter.OrderChapter;
import com.bytetech1.sdk.chapter.OrderNotAllowedChapter;
import com.bytetech1.sdk.data.Detail;

public class OnlineReadingActivity extends BaseActivity {

	private final static String TAG = "OnlineReadingActivity";
	private final static int RECOMMEND_UPDATE=-1;
	private final static String ACTION_TIRED_TIMER = "com.boyiqove.USER_TIRED";
	private boolean isClose = false;
	private String mChapter;
	private int loginFast=-1; // 快速登录页面的返回值 
	public enum ChapterAction {
		INIT(0), // 页面初始化到上次阅读位置
		DOWN(1), // 翻到下一章
		UP(2), // 翻到上一章
		JUMP(3), // 章节跳转
		CACHE_PREV(4), // 加载上一章到缓存,不显示
		CACHE_NEXT(5), // 加载下一章到缓存, 不显示
		LOAD(6); // 预读章节到本地, 不显示

		int index;

		private ChapterAction(int index) {
			// TODO Auto-generated constructor stub
			this.index = index;
		}

		public static ChapterAction getAction(int index) {
			switch (index) {
			case 0:
				return INIT;
			case 1:
				return DOWN;
			case 2:
				return UP;
			case 3:
				return JUMP;
			case 4:
				return CACHE_PREV;
			case 5:
				return CACHE_NEXT;
			case 6:
				return LOAD;
			default:
				return null;
			}
		}

	}

	private PageWidget mPageWidget;

	private Bitmap mCurPageBitmap;
	private Bitmap mNextPageBitmap;

	private Canvas mCurPageCanvas;
	private Canvas mNextPageCanvas;

	private int mWidth, mHeight;

	private BookItem mBookItem;
	private String mmBid;
	private Boolean isBanner,isComeDetailDir;
	private int isBan;

	private OnlineBookFactory mBookFactory;
	private BookCacheManager mCacheManager;

	private BookRequest mRequest;

	private ReadConfig mReadConfig;

	private boolean mIsFirst = false;

	private TextView directoryName;
	private TextView telUser;
	private TextView buyInfo;

	private String username;
	private boolean isBuy = true;
	private boolean isGoBuy = false;
	private boolean lastIsFirst = false;

	private boolean isHaveMapTable = false; // 是否有映射表
	
	private int endDownloadPos;   // 批量下载结束的id
	private int freeEndPos;   // 收费的开始章节
	private Message batchDownMsg;
	// private OnlineChapterInfo info;
	private Boolean isFirstLoding=false; // 
	private OnlineChapterInfo info2;
	private OnlineChapterInfo infoYd;
	private OnlineReadingActivity.OnlineBookRequest.ReadChapterContentSDKTask buyBook;
	public WindowManagerLogin managerLogin=new WindowManagerLogin();
	private ImageLoader imageLoader=getImageLoader();
	private int batchSum; 
	private Boolean settingWindow;
	private int progressCount=0;
	private Handler mCallBack = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			switch (msg.what) {
			case CallBackMsg.CONTENTS_READ_COMPLETED:
				mRequest.getRequestChapterCache(mBookItem.lastChapterPos,
						ChapterAction.INIT);
				break;
			case CallBackMsg.CONTENTS_READ_CONTEXT:
//				mRequest.getRequestChapterCache(mBookItem.lastChapterPos,
//						ChapterAction.INIT);				
				if ((!xnBid.equals(""))) {			
//					比较目录长度
//					freeEndPos=(int)AppData.getContentHelper(mBookItem.onlineID).getFreeCount();
					freeEndPos=mBookItem.freeCount;
					if(mContentsList.size()>mXNContentsList.size()){	
						mContentsList2.clear();
						
						for (int i = 0; i < mXNContentsList.size(); i++) {
							info2=mXNContentsList.get(i);
							infoYd=mContentsList.get(i);
							info2.type=infoYd.type;
							infoYd.name=info2.name;
							mContentsList2.add(infoYd);						
						}											
						mContentsList.clear();
						mContentsList.addAll(mContentsList2);
						
					}else {
						mContentsList2.clear();
						mContentsListYd2.clear();
							for (int i = 0; i < mContentsList.size(); i++) {
								info2=mXNContentsList.get(i);
								infoYd=mContentsList.get(i);
								info2.type=infoYd.type;							
								mContentsList2.add(info2);	
								infoYd.name=info2.name;
								mContentsListYd2.add(infoYd);
							}	
							mXNContentsList.clear();
							mXNContentsList.addAll(mContentsList2);	
							mContentsList.clear();
							mContentsList.addAll(mContentsListYd2);
						}
					}else {
//						freeEndPos=(int)AppData.getContentHelper(mBookItem.onlineID).getFreeCount();
						freeEndPos=mBookItem.freeCount;
					}
					
				mCallBack
				.sendEmptyMessage(CallBackMsg.CONTENTS_READ_COMPLETED);
				
				break;

			case CallBackMsg.CHAPTER_CONTENT_COMPLETED:
				// 设置当前为UNLOAD
				// showToastByHandler("本地章节缓冲读取异常", Toast.LENGTH_LONG); 重新下载
				mRequest.getRequestChapterCache(msg.arg1,
						ChapterAction.getAction(msg.arg2));
				break;
			case CallBackMsg.CLOSE_LOGINWINDOW:
				dismissDirlogPop();
				break;
				
			case CallBackMsg.SHOW_DOWNLOAD_PROGRESS:
				if (settingWindow) {
					progressCount++;
					progresSize.setText("("+progressCount);
				}
				break;
				
			case CallBackMsg.CHCEK_CONTENTS_COMPLETED: {
				File f = new File(mBookItem.path);
				long last = f.lastModified();
				if (mBookItem.lastDate != last) {
					AppData.getDataHelper().updateLastDateLocal(mBookItem.id,
							last);
				}

				ArrayList<LocalChapterInfo> chList = (ArrayList<LocalChapterInfo>) msg.obj;
				if (null == chList || chList.size() == 0) {
					// showToastByHandler("没有找到章节信息", Toast.LENGTH_SHORT);
					DebugLog.d(TAG, "没有找到章节信息");

					setResult(ResultCode.CONTENT_NOT_FOUND);
					finish();

				} else {
					DebugLog.d(TAG, "章节信息搜索完成");
					((TxtBookRequest) mRequest).setContentList(chList);

					OutputObjectTask task = new OutputObjectTask(
							"saveContents", chList, AppData.getConfig()
									.getLocalContentsFilePath(mBookItem.id));
					AppData.getClient().getTaskManagerRead().addTask(task);

					mRequest.getRequestChapterCache(mBookItem.lastChapterPos,
							ChapterAction.INIT);
				}
			}
				break;

			case CallBackMsg.READ_CONTENTS_COMPLETED:
				mRequest.getRequestChapterCache(mBookItem.lastChapterPos,
						ChapterAction.INIT);
				break;

			case CallBackMsg.SHOW_PROGRESS_MESSAGE: {
				showProgress("", (String) msg.obj);

				break;
			}
			case CallBackMsg.SHOW_LOGINPROGRESS: {
//				showProgressDJS2("", (String) msg.obj);
				
				break;
			}
			case CallBackMsg.SHOW_PROGRESS_CANCEL: { // 展示可取消的进度条
				
				if (! isClose) {					
					showProgressCancel("", "", (String) msg.obj);
				}
				break;
			}
			case CallBackMsg.HIDE_PROGRESS_MESSAGE: {

				hideProgress();
				break;
			}			
			
			case CallBackMsg.SHOW_TOAST_MESSAGE: {
				showToast((String) msg.obj, Toast.LENGTH_LONG);
				break;
			}	
			// 批量下载,sheep 
			case CallBackMsg.SHEEP_DOWN_LOAD: {
				
				batchDownload(msg.arg1);
				break;
			}			
			case CallBackMsg.INIT_SHOW_LOGIN:	
				
				managerLogin.showPopupWindow(OnlineReadingActivity.this,true,batchSum);
				mCallBack.sendMessageDelayed(mCallBack.obtainMessage(CallBackMsg.CLOSE_SHOW_LOGIN), 5000);
				break;
			case CallBackMsg.INIT_SHOW_DOWN:	//开始下载出现设置
				progressCount=0;
				popupReadActionWindow();
				progressLayout.setVisibility(View.VISIBLE);
//				managerLogin.showPopupWindow(OnlineReadingActivity.this,false,batchSum);
				break;
			case CallBackMsg.CLOSE_SHOW_LOGIN:
				managerLogin.hidePopupWindow();
				break;
			case CallBackMsg.CLOSE_ERROR_LOGIN:
				dismissFreebackPop();
				break;

			case CallBackMsg.SHOW_DIALOGE_MESSAGE: {
				DialogContent content = (DialogContent) msg.obj;
				MyAlert.showLoginDialog(OnlineReadingActivity.this,
						content.title, content.message, content.listener);
				break;
			}
			case CallBackMsg.READING_LOOPER_STAST: {
				
				ChapterAction action=(ChapterAction) msg.obj;
				 int chapterPos=msg.arg1;		
				mRequest.getRequestChapterCache(chapterPos, action);	
				break;
			}		
			case CallBackMsg.READ_ENDPAGE_RECOMEND: {
//				// 更新三张图片推荐
				int []images=new int[3];
				Random random=new Random(10);
				images[0]=random.nextInt(9);
				while (true) {						
					images[1]=random.nextInt(9);
					if (images[1]!=images[0]) {
						break;
					}
				}
				while (true) {						
					images[2]=random.nextInt(9);
					if (images[2]!=images[0]&&images[2]!=images[1]) {
						break;
					}
				}
				
				
            	String str=jsonList.get(images[0]);
//            	final String bid1=str.substring(str.lastIndexOf("/")+1);  
            	netImageOne.setErrorImageResId(R.drawable.boyi_ic_cover_default);
            	netImageOne.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
            	netImageOne.setImageUrl(addUrl(str.substring(0,str.lastIndexOf("/"))), imageLoader);  
    	
            	String strCenter=jsonList.get(images[1]);
            	final String bid2=strCenter.substring(strCenter.lastIndexOf("/")+1);   
            	netImageTwo.setErrorImageResId(R.drawable.boyi_ic_cover_default);
            	netImageTwo.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
            	netImageTwo.setImageUrl(addUrl(strCenter.substring(0,strCenter.lastIndexOf("/"))), imageLoader);  

            	String strRight=jsonList.get(images[2]);
            	final String bid3=strRight.substring(strRight.lastIndexOf("/")+1); 
            	netImageTree.setErrorImageResId(R.drawable.boyi_ic_cover_default);
            	netImageTree.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
            	netImageTree.setImageUrl(addUrl(strRight.substring(0,strRight.lastIndexOf("/"))), imageLoader);  
            	
				break;
			}		
			case RECOMMEND_UPDATE:
				if (jsonList.size() == 0) {
					read_recommend_text.setVisibility(View.GONE);
					bottom_ll.setVisibility(View.GONE);
				}
				break;
			default:
				DebugLog.d(TAG, "unkown msg:" + Integer.toHexString(msg.what));
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		DebugLog.on(false);
		DebugLog.on(true);		
		full(true);

		DebugLog.d(TAG, "onCreate:" + Thread.currentThread().getId());
		
		initData();
		initReadPage();
		initReadListener();
	}

	private void full(boolean enable) {
		if (enable) {
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			lp.flags = 1280;
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

	/**
	 * 更新阅读信息接口
	 * */
	public void onReadInfo(String chather) {

		String url = AppData.getConfig().getUrl(Config.URL_READ_BOOK);
		DebugLog.d(TAG, url);
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", AppData.getUser().getID() + "");
		map.put("imei", AppData.getConfig().getDeviveInfo().getImei() + "");
		map.put("aid", mBookItem.bid);
		map.put("cid", chather);
		DebugLog.d(TAG, url);
		getRequestQueue().add(
				new JsonObjectPostRequest(url, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						DebugLog.d(TAG, response.toString());
						try {

							DebugLog.d(TAG,
									"提交购买信息:" + response.getString("msg"));

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

				}, map));

	}

	private Detail detail;

	private BookItem getBookItem(String bid) {
		// TODO Auto-generated method stub
		detail = BookHelper.loadDetail(bid);
		mBookItem = new BookItem();
		if (detail != null) {
			mBookItem.bid = detail.getBid();
			mBookItem.cid = detail.getFirstCid();
			mBookItem.name = detail.getName();
			mBookItem.author = detail.getAuthor();
			mBookItem.status = detail.getStatus();
			mBookItem.wordNum = detail.getWord();
			mBookItem.shortDesc = detail.getIntroduction();
			mBookItem.longDesc = detail.getDesc();
			mBookItem.littleCoverUrl = detail.getCoverUrl();
			mBookItem.bigCoverUrl = detail.getBigCoverUrl();
			mBookItem.classFication = detail.getClassification();
			mBookItem.clickStr = detail.getClick();
			mBookItem.freeCount = detail.getFreeChapterCount();
			mBookItem.totalCount = detail.getTotalChapterCount();
		} else {
			DebugLog.e("得到的item", "为空");
		}
		return mBookItem;

	}

	private void initData() {
		jsonList=new ArrayList<String>();
		// check
		mBookItem = (BookItem) getIntent().getSerializableExtra("BookItem");
		
		isBanner = getIntent().getBooleanExtra("isBanner", false);
		isComeDetailDir = getIntent().getBooleanExtra("isComeDetil", false);
		batchSum=getIntent().getIntExtra("buynum", 0);
		if (mBookItem.bid==null) {
			mBookItem.onlineID = Integer.parseInt("0");
			mBookItem.bid="0";
		}else {			
			mBookItem.onlineID = Integer.parseInt(mBookItem.bid);
		}
		xnBid = getSharedPreferences("bidMapTable", Application.MODE_PRIVATE)
				.getString(mBookItem.bid, "");

		if (null == mBookItem) {
			throw new RuntimeException();
		}
		DebugLog.d(TAG, "onlineID:" + mBookItem.onlineID + ", bookName:"
				+ mBookItem.name);

		mReadConfig = AppData.getConfig().getReadConfig();

		mCacheManager = new BookCacheManager(OnlineBookCache.class);

		mBookFactory = new OnlineBookFactory(mCacheManager);

		if (mBookItem.onlineID == BookItem.ON_LOCAL_TXT) {
			mRequest = new TxtBookRequest();
		} else {
			mRequest = new OnlineBookRequest();
		}

		mBookFactory.setOnDrawListener(new OnDrawListener() {

			@Override
			public String getChapterName() {
				// TODO Auto-generated method stub
				return mRequest.getChapterName();
			}

			@Override
			public String getBookName() {
				// TODO Auto-generated method stub
				return "《"+mBookItem.name+"》";
			}
		});
		// 1.接收电池变化广播
		registerReceiver(mBatteryChangedReeciver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		AppData.getClient().setCallBackHander(mCallBack);
//		 showCancelProgressByHandler("", "加载目录中");
		getMapTable();
		// mRequest.open();
	}

	private void initReadPage() {

		loadReadSetting();

		LinearLayout.LayoutParams lp = new LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		mPageWidget = new PageWidget(this);
		mPageWidget.setLayoutParams(lp);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

			getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
			// getWindow().getCurrentFocus()

		}
		// getWindowManager().

		setContentView(mPageWidget);
		// if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){

		// mPageWidget.setSystemUiVisibility(View.INVISIBLE);
		// mPageWidget.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
		//

		// }

		mCurPageCanvas = new Canvas();
		mNextPageCanvas = new Canvas();

	}

	private void loadReadSetting() {
		// // 1.横竖屏
		// if(mReadConfig.isPortrait()) {
		// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// } else {
		// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// }

		// 2.亮度
		if (!mReadConfig.isSysBrightness()) {

			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.screenBrightness = Float
					.valueOf(mReadConfig.getReadBrightness()) * (1f / 255f);
			getWindow().setAttributes(lp);
		}

	}

	// 设置activity 的亮度
	public void setBrightness(float f) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = f;
		getWindow().setAttributes(lp);
	}

	private BroadcastReceiver mBatteryChangedReeciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			DebugLog.d(TAG, "battery onReceive:" + intent.getAction());

			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
				level = level > scale ? scale : level;
				float percent = level * 1.0f / scale;
				mReadConfig.setBatteryPercent(percent);
			}

		}

	};

	private BroadcastReceiver tiredTimerReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			DebugLog.d(TAG, "tired onReceive:" + intent.getAction());

			if (intent.getAction().equals(ACTION_TIRED_TIMER)) {
				new AlertDialog.Builder(OnlineReadingActivity.this)
						.setTitle("阅读提醒")
						.setMessage(
								"您已经看了"
										+ mReadConfig.getTiredMode()
												.getMintutes() + "分钟了，休息一下吗")
						.setNegativeButton("取消", null).show();

				unregisterReceiver(this);
			}
		}

	};

	private void initReadListener() {
		mPageWidget.setOnSizeChangedListener(new OnPageSizeChangedListener());

		mPageWidget.setOnTouchListener(new OnPageTouchListener());
	}

	private class OnPageSizeChangedListener implements OnSizeChangedListener {
		@Override
		public void onSizeChanged(int w, int h, int oldw, int oldh) {
			// TODO Auto-generated method stub

			DebugLog.d(TAG, "onSizeChanged");

			if (mWidth == w && mHeight == h) {
				return;
			}

			mWidth = w;
			mHeight = h;

			if (null != mCurPageBitmap && !mCurPageBitmap.isRecycled()) {
				mCurPageBitmap.recycle();
				mCurPageBitmap = null;
			}
			if (null != mNextPageBitmap && !mNextPageBitmap.isRecycled()) {
				mNextPageBitmap.recycle();
				mNextPageBitmap = null;
			}

			try {
				mCurPageBitmap = Bitmap.createBitmap(mWidth, mHeight,
						Bitmap.Config.RGB_565);

			} catch (OutOfMemoryError e) {
				System.gc();
				System.gc();
				mCurPageBitmap = Bitmap.createBitmap(mWidth, mHeight,
						Bitmap.Config.RGB_565);
			}

			try {
				mNextPageBitmap = Bitmap.createBitmap(mWidth, mHeight,
						Bitmap.Config.RGB_565);

			} catch (OutOfMemoryError e) {
				System.gc();
				System.gc();
				mNextPageBitmap = Bitmap.createBitmap(mWidth, mHeight,
						Bitmap.Config.RGB_565);
			}

			mCurPageCanvas.setBitmap(mCurPageBitmap);
			mNextPageCanvas.setBitmap(mNextPageBitmap);

			mReadConfig.setSize(mWidth, mHeight);
			mCacheManager.reset();

			mBookFactory.draw(mCurPageCanvas);

			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);

			mPageWidget.setScrolling(false);
			mPageWidget.postInvalidate();
		}

	};

	private boolean mAlwaysInTapRegion = false;
	private float mCurrentDownMotionX = 0.0f;
	private float mCurrentDownMotionY = 0.0f;

	private class OnPageTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			boolean bRet = false;
			do {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mAlwaysInTapRegion = true;
					mCurrentDownMotionX = event.getX();
					mCurrentDownMotionY = event.getY();

					mPageWidget.abortAnimation();
					mPageWidget.calcCornerXY(event.getX(), event.getY());

					if (mIsFirst) {
						mBookFactory.drawFirst(mCurPageCanvas, mBookItem.name,
								mBookItem.author);
					} else {
						mBookFactory.draw(mCurPageCanvas);
					}

					if (isTouchInPopupRect(event.getX(), event.getY())) {
						DebugLog.d(TAG, "touch in popup");
						bRet = true;
						break;
					}

					if (mPageWidget.DragToRight()) {

						DebugLog.d(TAG, "do pageUp at touch");
						if (!mBookFactory.pageUp()) {

							if (0 == mBookItem.lastChapterPos) {
								// 显示书封页
								mPageWidget.setScrolling(false);
								showToastByHandler("已经是第一页了",
										Toast.LENGTH_SHORT);
								
//								if (!mIsFirst) {
//									
//									mBookFactory.drawFirst(mNextPageCanvas,
//											mBookItem.name, mBookItem.author);
//									mIsFirst = true;
//
//									mPageWidget.setScrolling(true);
//									mPageWidget.doInternalTouchDown(event);
//
//									bRet = true;
//
//								} else {
//									mPageWidget.setScrolling(false);
//									showToastByHandler("已经是第一页了",
//											Toast.LENGTH_SHORT);
//								}
								break;

							} else {
								// 上一章
								if (mBookItem.lastChapterPos < mRequest.size()) {
									chapterUp();
								} else {
									showToastByHandler("没有上一页了",
											Toast.LENGTH_SHORT);
									break;
								}
							}

						}

					} else {
						if (mIsFirst) {
							mIsFirst = false;
						} else {
							if (!mBookFactory.pageDown()) {
								if (mBookItem.lastChapterPos + 1 >= mRequest
										.size()) {

									// 翻页到下一章

									mPageWidget.setScrolling(false);
									showEndPageWindow(mBookItem.status);
//									showToastByHandler("没有下一章了",
//											Toast.LENGTH_SHORT);
									break;
								} else {
									// 下一章
									chapterDown(false);
								}

							}
						}
					}

					mBookFactory.draw(mNextPageCanvas);
					mPageWidget.setScrolling(true);

					mPageWidget.doInternalTouchDown(event);

				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

					if (mAlwaysInTapRegion) {
						final int deltaX = (int) (event.getX() - mCurrentDownMotionX);
						final int deltaY = (int) (event.getY() - mCurrentDownMotionY);
						int distance = (deltaX * deltaX) + (deltaY * deltaY);
						if (distance > 20) {
							mAlwaysInTapRegion = false;

						}

					}

					mPageWidget.doInternalTouchMove(event);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					if (mAlwaysInTapRegion) {
						if (isTouchInPopupRect(event.getX(), event.getY())) {
							if (!mIsFirst) {
								mPageWidget.setScrolling(false);
								full(false);
								popupReadActionWindow();

							}
						}
					}

					mPageWidget.doInternalTouchUp(event);
				}

				bRet = true;

			} while (false);

			return bRet;
		}

	};

	// 触摸设置页面范围
	private boolean isTouchInPopupRect(float x, float y) {
		int offset = mWidth / 5;
		if (x < mWidth / 2 + offset && x > mWidth / 2 - offset
				&& y < mHeight / 2 + 3 * offset && y > mHeight / 2 - 3 * offset) {
			return true;
		}

		return false;
	}

	private void chapterUp() {

		BookCache cache = mCacheManager.getCache(PageIndex.previous);
		if (cache.getPageCount() > 0) {
			// 成功翻到上一章
			DebugLog.d(TAG, "page move up");
			cache.pageEnd();
			mCacheManager.move(false);
			mBookItem.lastChapterPos--;

			prepareCachePrevious();

		} else {
			DebugLog.d(TAG, "page load up");

			mRequest.getRequestChapterCache(mBookItem.lastChapterPos - 1,
					ChapterAction.UP);
		}
	}

	private void chapterDown(boolean isProgress) {
		BookCache cache = mCacheManager.getCache(PageIndex.next);
		if (cache.getPageCount() > 0) {
			// 成功翻到下一章
			DebugLog.d(TAG, "page move down");
			cache.pageFirst();
			mCacheManager.move(true);
			mBookItem.lastChapterPos++;
			// DebugLog.e(TAG, "翻到了新的一章，开始缓存下一章");
			if (isProgress) {
				redrawPage();
				setPageProgress();
			}

			prepareCacheNext();

		} else {
			DebugLog.d(TAG, "page load down");

			mRequest.getRequestChapterCache(mBookItem.lastChapterPos + 1,
					ChapterAction.DOWN);
		}
	}

	private void redrawPage() {
		mBookFactory.draw(mCurPageCanvas);
		// mBookFactory.draw(mNextPageCanvas);
		mPageWidget.setScrolling(false);
		mPageWidget.postInvalidate();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		DebugLog.d(TAG, "onStart");

		// 2.设置疲劳提示
		int min = mReadConfig.getTiredMode().getMintutes();
		if (min > 0) {
			registerReceiver(tiredTimerReceiver, new IntentFilter(
					ACTION_TIRED_TIMER));
			Intent intent = new Intent(ACTION_TIRED_TIMER);
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			long triggerAtTime = System.currentTimeMillis() + min * 60 * 1000;
			alarm.set(AlarmManager.RTC, triggerAtTime, sender);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		full(true);
		DebugLog.d(TAG, "onResume");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// 更新阅读位置到服务器
					int lastPosition = mCacheManager.getCache(PageIndex.current)
							.getCurPagePosition();
					long lastDate = System.currentTimeMillis();
					mBookItem.lastPosition = lastPosition;
					mBookItem.lastDate = lastDate;
					mBookItem.chapterTotal = mContentsList.size();
					mBookItem.timeStamp=System.currentTimeMillis();
		hideReadActionWindow();
		AppData.getDataHelper()
		.updateLastKBReadOnline(
				mBookItem.onlineID,
				mBookItem.lastChapterPos,
				mBookItem.lastPosition,
				mBookItem.status,mBookItem.timeStamp);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		
		if (intent.getBooleanExtra("comeDire", false)) { //来自阅读目录页
			int position = intent.getIntExtra("position", -1);
			DebugLog.i(TAG, "jump to:" + position);
			if (-1 != position) {
				mRequest.getRequestChapterCache(position, ChapterAction.JUMP);
			}
			
		}else {
			
			initData();
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		mCallBack
		.sendEmptyMessage(CallBackMsg.CLOSE_SHOW_LOGIN);
		int lastPosition = mCacheManager.getCache(PageIndex.current)
				.getCurPagePosition();
		long lastDate = System.currentTimeMillis();
		mBookItem.lastPosition = lastPosition;
		mBookItem.lastDate = lastDate;
		mBookItem.chapterTotal = mContentsList.size();
//		if (isComeDetailDir) {
//			Intent intent=new Intent(this, OnlineContentsActivity.class);
//			intent.putExtra("readPosition", mBookItem.lastPosition);
//			setResult(ResultCode.DETIAL_DIRE_TOREAD, intent);
//		}
		DebugLog.d(TAG, "onDestory");
		AppData.getDataHelper().updateQoveBook(mBookItem.bid, mBookItem.status,
				0, mBookItem.totalCount);
		if (null != mCurPageBitmap && !mCurPageBitmap.isRecycled()) {
			mCurPageBitmap.recycle();
			mCurPageBitmap = null;
		}
		if (null != mNextPageBitmap && !mNextPageBitmap.isRecycled()) {
			mNextPageBitmap.recycle();
			mNextPageBitmap = null;
		}
		mReadConfig.clearBackBitmap();
		System.gc();

		unregisterReceiver(mBatteryChangedReeciver);

		AppData.getClient().setNullCallBackHander(mCallBack);
		isClose = true;
		// AppData.closeDBContent(mBookItem.onlineID);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();

		goBack();
	}

	private void goBack() {
		// 搜索

		// hideProgressByHandler();
		mRequest.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		DebugLog.e(TAG, "onActivityResutl被调用");
		if (data==null) {
			return;
		}
		if (ResultCode.JUMP_TO_POSITION == resultCode) {
			// 跳转
			int position = data.getIntExtra("position", -1);
			DebugLog.i(TAG, "jump to:" + position);
			if (-1 != position) {
				mRequest.getRequestChapterCache(position, ChapterAction.JUMP);
			}

		} else if (ResultCode.CM_TO_CONGZHI == resultCode) { // 电信、联通，充值后回调
			// 再次执行上次的task
			if (buyBook != null) {
				// DebugLog.e("充值完成", "重新购买");
				AppData.getClient().getTaskManagerRead().addTask(buyBook);
			} else {
				mCallBack.sendEmptyMessage(CallBackMsg.CONTENTS_READ_COMPLETED);
			}

		} else if (ResultCode.LOGIN_TO_FAST == resultCode) {
			data.getIntExtra("loginFast", -1);
			if (buyBook!=null) {				
				AppData.getClient().getTaskManagerRead().addTask(buyBook);
			}else {
				return;
			}			
		}else if (ResultCode.ORDER_INFO == resultCode) {
			isGoBuy=data.getBooleanExtra("isGoBuy", false);
			mReadConfig = AppData.getConfig().getReadConfig();
			if (isGoBuy) {
				lastIsFirst = true;
				AppData.getClient().getTaskManagerRead().addTask(buyBook);
			}else {
//				lastIsFirst = false;
				showToast("支付取消", Toast.LENGTH_SHORT);
			}
			
		}
		else {
			// DebugLog.e("充值完成", "null");
			if (buyBook==null) {
				return;
			}
			AppData.getClient().getTaskManagerRead().addTask(buyBook);
			// mCallBack.sendEmptyMessage(CallBackMsg.CONTENTS_READ_COMPLETED);
		}

	}

	/********************************************************************************************************************/

	private void refreshNewChapter(String chapter, String buf, int chapterPos,
			ChapterAction action, Task mTask) {
		// if (! mTask.mbCancel) {
		
		
		if (!isClose) {
			hideProgressByHandler();
			switch (action) {
			case INIT: {
				mCacheManager.clear();
				BookCache curCache = mCacheManager.getCache(PageIndex.current);
				curCache.parse(chapter, buf);
				curCache.setPosition(mBookItem.lastPosition);
				mBookItem.lastChapterPos = chapterPos;
				redrawPage();
				if (batchSum!=0) {
					if (chapterPos<freeEndPos) {
						endDownloadPos=freeEndPos+batchSum-1;
					}else if (batchSum>100) {
						endDownloadPos=mContentsList.size();		    			
					}else{
						endDownloadPos=chapterPos+batchSum;
					}
					mCallBack.sendMessage(mCallBack.obtainMessage(CallBackMsg.INIT_SHOW_DOWN));
					batchDownload(chapterPos);
					
				}else {					
					prepareCacheNext();
				}
			}

				break;
			case DOWN: {
				BookCache curCache = mCacheManager.getCache(PageIndex.next);
				curCache.parse(chapter, buf);
				curCache.pageFirst();
				mCacheManager.move(true);
				mBookItem.lastChapterPos = chapterPos;
				redrawPage();
				
			}
				break;
			case UP: {
				BookCache curCache = mCacheManager.getCache(PageIndex.previous);
				curCache.parse(chapter, buf);
				curCache.pageEnd();
				mCacheManager.move(false);
				mBookItem.lastChapterPos = chapterPos;

				redrawPage();
			}
				break;
			case JUMP: {

				mCacheManager.clear();
				BookCache curCache = mCacheManager.getCache(PageIndex.current);
				curCache.parse(chapter, buf);
				curCache.pageFirst();
				// curCache.setPosition(mBookItem.lastPosition);

				mBookItem.lastChapterPos = chapterPos;

				redrawPage();

				prepareCacheNext();
			}
				break;
			case CACHE_PREV:
				BookCache prevCache = mCacheManager
						.getCache(PageIndex.previous);
				prevCache.parse(chapter, buf);
				prevCache.pageEnd();
				return;
			case CACHE_NEXT:
				BookCache nextCache = mCacheManager.getCache(PageIndex.next);
				nextCache.parse(chapter, buf);
				nextCache.pageFirst();
				return;
			case LOAD:
				DebugLog.d(TAG, "章节预读到本地完成 , pos:" + mBookItem.lastChapterPos
						+ ", chapterName:" + mRequest.getChapterName());
				return;
			default:
				return;
			}
		} else {
			return;
		}
	}

	// 预读上一章内容到内存
	private void prepareCachePrevious() {
		if (mBookItem.lastChapterPos - 1 >= 0) {
			mRequest.getRequestChapterCache(mBookItem.lastChapterPos - 1,
					ChapterAction.CACHE_PREV);
		}

	}

	// 预读下一章内容到内存
	private void prepareCacheNext() {
		// info = mContentsList.get(mBookItem.lastChapterPos + 1);
		if (mBookItem.lastChapterPos + 1 < mRequest.size()) {
			mRequest.getRequestChapterCache(mBookItem.lastChapterPos + 1,
					ChapterAction.CACHE_NEXT);
		}
	}
	// 缓存下一章到本地磁盘
		private void loadNextContent(int nowChapterPos){
			if (nowChapterPos + 1 < mRequest.size()) {
				int b=nowChapterPos + 1;
				mRequest.getRequestChapterCache(nowChapterPos + 1,
						ChapterAction.LOAD);
			}	
		}	
	// 设置批量下载
		/**
		 * 批量下载， 参数 ：开始章节id
		 * */
		private void batchDownload(int startChapterPos){	
			
			int countNum; // 结束章节
			if (endDownloadPos<mRequest.size()) {
				countNum=endDownloadPos;
			}else {
				countNum=mRequest.size();
			}
			DebugLog.e("batchDownload", "下载第"+startChapterPos+"将下到第"+countNum);
			if (startChapterPos + 1 < countNum) { // 开始章节自增加，直到等于 结束章节
				int b=startChapterPos + 1;
				Message msgID=new Message();
				msgID.what=CallBackMsg.READ_ENDPAGE_PROGRESS;
				msgID.arg1=startChapterPos;
				sendDownloadMsgByHandler(msgID);
				mRequest.getRequestChapterCache(startChapterPos + 1,
						ChapterAction.LOAD);
				if (startChapterPos+2==countNum) {
					DebugLog.e("下载完了", "下载到了"+countNum);
//					mCallBack
//					.sendEmptyMessage(CallBackMsg.CLOSE_SHOW_LOGIN);
					if (bookName!=null) {						
						bookName.setText("缓存完成："+mBookItem.name);
						progresSize.setVisibility(View.GONE);
						totalSize.setVisibility(View.GONE);
					}
					
				}
			}else {
				endDownloadPos=0;
				showToastByHandler("下载完成,爽文等你看", Toast.LENGTH_LONG);
				return;
			}	
		}	
		
	// 获取状态栏的高度
	public int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbar;
	}

	/********************************************************************************************************************/
	private RelativeLayout rl = null;
	private PopupWindow popupReadActionWindow = null;

	private void popupReadActionWindow() {
		settingWindow=true;
		View v = getReadActionView();
		rl = (RelativeLayout) v.findViewById(R.id.rl_popupWindow);
		int height = (int) getResources().getDimension(
				R.dimen.read_action_top_height);
		RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, height);
		l.setMargins(0, getStatusBarHeight(), 0, 0);
		rl.setLayoutParams(l);
		if (null == popupReadActionWindow) {
			popupReadActionWindow = new PopupWindow(v,
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			popupReadActionWindow.setFocusable(true);
			popupReadActionWindow.setTouchable(true);
			popupReadActionWindow.setOutsideTouchable(true);
			popupReadActionWindow.setBackgroundDrawable(new BitmapDrawable());
		}

		// mPageWidget.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
		popupReadActionWindow.showAtLocation(mPageWidget, Gravity.FILL, 0, 0);
	}

	private void hideReadActionWindow() {
		settingWindow=false;
		if (null != popupReadActionWindow) {

			popupReadActionWindow.dismiss();
		}
	}

	private View readActionView = null;
	private SeekBar seekBarChapterProgress;
	private TextView tvPageProgress;
	private int lastChapterPos, lastPagePos;
	private ImageView nightIv;
	private ImageView tvAutoBuy;
	private ImageView ivDetail;
	private ImageView ivContents;
	private ImageView ivSetting;
	private ImageView ivNightDay;
	private ImageView ivError;
	private ImageView downMore;
	private View setView;
	private View bottomView;
	private LinearLayout setMenu;
	private LinearLayout linearLayout;
	private TextView bookName ,progresSize,totalSize ;
	private LinearLayout progressLayout;
	private View getReadActionView() {
		lastChapterPos = mBookItem.lastChapterPos;
		lastPagePos = mCacheManager.getCache(PageIndex.current).getCurPage();
		if (null == readActionView) {
			readActionView = LayoutInflater.from(this).inflate(
					R.layout.boyi_read_action, null);
			readActionView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					hideReadActionWindow();
					// 隐藏状态栏
					if (!popupReadActionWindow.isShowing()) {
						getWindow().setFlags(
								WindowManager.LayoutParams.FLAG_FULLSCREEN,
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
					}
				}
			});

			final View progressView = readActionView
					.findViewById(R.id.read_progress_layout);
			setView = readActionView
					.findViewById(R.id.read_set_layout);
			setView.setVisibility(View.GONE);
			bottomView = readActionView
					.findViewById(R.id.read_bottom_layout);
			linearLayout= (LinearLayout) readActionView
					.findViewById(R.id.menu_ll);

			progressView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			setView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			
			
			bottomView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
			// 菜单
			setMenu = (LinearLayout) readActionView
					.findViewById(R.id.read_menu_iv);
			setMenu.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					if (linearLayout.getVisibility()==View.GONE) {						
						linearLayout.setVisibility(View.VISIBLE);
					}else {
						linearLayout.setVisibility(View.GONE);
					}
				}
			});
			// 2.逛书城
			LinearLayout tvBrowserBook = (LinearLayout)readActionView.findViewById(R.id.enter_bookstore);
					tvBrowserBook.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent=new Intent(OnlineReadingActivity.this, StoreMain.class);
								startActivity(intent);
								finish();
							}
							});		
						
			// 3.看书架
					LinearLayout tvOperatorBook = (LinearLayout)readActionView.findViewById(R.id.enter_bookshelf);
						tvOperatorBook.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								DebugLog.e("点击了看书架", "1");
								AppData.setShowRecommend(false);
								AppData.goToShelf(OnlineReadingActivity.this,false);	
								int lastPosition = mCacheManager.getCache(PageIndex.current)
										.getCurPagePosition();
								long lastDate = System.currentTimeMillis();
								mBookItem.lastPosition = lastPosition;
								mBookItem.lastDate = lastDate;
								mBookItem.chapterTotal = mContentsList.size();
//					hideReadActionWindow();
					AppData.getDataHelper()
					.updateLastKBReadOnline(
							mBookItem.onlineID,
							mBookItem.lastChapterPos,
							mBookItem.lastPosition,
							mBookItem.status,mBookItem.timeStamp);
								finish();
								}
						});
			// 后退
			ImageView ivBack = (ImageView) readActionView
					.findViewById(R.id.read_back_ib);
			ivBack.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					hideReadActionWindow();
					goBack();

				}
			});

			// 0.自动购买
			tvAutoBuy = (ImageView) readActionView
					.findViewById(R.id.read_auto_buy_iv);
			if (mBookItem.onlineID == BookItem.ON_LOCAL_TXT) {
				tvAutoBuy.setVisibility(View.INVISIBLE);
			} else {
				if (mReadConfig.isAutoBuy()) {
					tvAutoBuy.setImageResource(R.drawable.read_auto_buy_yes);
				} else {
					tvAutoBuy.setImageResource(R.drawable.read_auto_buy);
				}

				tvAutoBuy.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						if (mReadConfig.isAutoBuy()) {
							tvAutoBuy.setImageResource(R.drawable.read_auto_buy);
							mReadConfig.setAutoBuy(false);
						} else {
							tvAutoBuy.setImageResource(R.drawable.read_auto_buy_yes);
							mReadConfig.setAutoBuy(true);
						}
					}
				});
			}
			// 0.批量下载
			downMore=(ImageView) readActionView
					.findViewById(R.id.read_download_iv);
			downMore.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					downMore.setBackgroundResource(R.drawable.read_sett_download_yes);
					if (xnBid.equals("") || xnBid == null) {	
						popupBuyWindow(mContentsList,lastChapterPos);
//						showErrorPopuwidonw(mContentsList.get(lastChapterPos).name, mBookItem.name);
					}else {
						popupBuyWindow(mXNContentsList,lastChapterPos);
//						showErrorPopuwidonw(mXNContentsList.get(lastChapterPos).name, mBookItem.name);
					}
							
				}
			});
			
			//批量下载进度信息
			progressLayout =(LinearLayout) readActionView.findViewById(R.id.down_more_progress);
			bookName= (TextView) readActionView.findViewById(R.id.down_bookname);
			progresSize= (TextView) readActionView.findViewById(R.id.down_progress);
			totalSize= (TextView) readActionView.findViewById(R.id.down_totalsize);
			if (endDownloadPos>0) {
				progressLayout.setVisibility(View.VISIBLE);
				bookName.setText("正在缓存："+mBookItem.name);
				progresSize.setText("("+lastChapterPos);
				totalSize.setText("/"+endDownloadPos+")");
			}else {
				bookName.setText("正在缓存："+mBookItem.name);
				progresSize.setText("("+lastChapterPos);
				totalSize.setText("/"+endDownloadPos+")");
				progressLayout.setVisibility(View.GONE);
			}
			
			
			// 1.详情
			LinearLayout lldetail = (LinearLayout) readActionView
					.findViewById(R.id.read_button_bar1);
			ivDetail = (ImageView) readActionView
					.findViewById(R.id.detail_bar);
			lldetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					DebugLog.e("点击了详情", "");
					// 更新阅读位置
					ivDetail.setBackgroundResource(R.drawable.read_bottom_detil_yes);
					int lastPosition = mCacheManager.getCache(PageIndex.current)
							.getCurPagePosition();
					long lastDate = System.currentTimeMillis();
					mBookItem.lastPosition = lastPosition;
					mBookItem.lastDate = lastDate;
					mBookItem.chapterTotal = mContentsList.size();
					mBookItem.timeStamp=System.currentTimeMillis();
					AppData.getDataHelper()
					.updateLastKBReadOnline(
							mBookItem.onlineID,
							mBookItem.lastChapterPos,
							mBookItem.lastPosition,
							mBookItem.status,mBookItem.timeStamp);
					
					Intent intent=new Intent(OnlineReadingActivity.this, BookDetail.class);
					intent.putExtra("bid", mBookItem.bid);
					startActivity(intent);
				}
			});
			// 2.目录
			LinearLayout llContents = (LinearLayout) readActionView
					.findViewById(R.id.read_button_bar2);
			ivContents = (ImageView) readActionView
					.findViewById(R.id.dictory_bar);
			
			llContents.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					DebugLog.e("点击了目录", "");
					ivContents.setBackgroundResource(R.drawable.read_dictory_bar_yes);
					mRequest.showContent();
					
				}
			});
			// 2. 报错
			LinearLayout llError = (LinearLayout) readActionView
					.findViewById(R.id.read_button_bar3);
			ivError = (ImageView) readActionView
					.findViewById(R.id.error_image_bar);
			llError.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ivError.setBackgroundResource(R.drawable.read_post_error_yes);
//					弹出对话框
					if (xnBid.equals("") || xnBid == null) {						
						showErrorPopuwidonw(mContentsList.get(lastChapterPos).name, mBookItem.name);
					}else {
						showErrorPopuwidonw(mXNContentsList.get(lastChapterPos).name, mBookItem.name);
					}
				}
			});
			
			// 4.阅读设置
			LinearLayout llSetting = (LinearLayout) readActionView
					.findViewById(R.id.read_button_bar4);
			ivSetting = (ImageView) readActionView
					.findViewById(R.id.setting_image_bar);
			llSetting.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ivSetting.setBackgroundResource(R.drawable.read_setting_bar_yes);
					setView.setVisibility(View.VISIBLE);
					bottomView.setVisibility(View.GONE);
				}
			});
			// 5.夜间模式
			ivNightDay = (ImageView) readActionView
					.findViewById(R.id.boe_night_stytle);
			int dayIndex=mReadConfig.getColorIndex();
			if (dayIndex!=4) {
				ivNightDay.setBackgroundResource(R.drawable.boe_read_night);
//				mReadConfig.setColorIndex(dayIndex);
			}else {
				ivNightDay.setBackgroundResource(R.drawable.boe_read_day);
			}
			ivNightDay.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int a=mReadConfig.getColorIndex();
					if (a!=4) {						
						ivNightDay.setBackgroundResource(R.drawable.boe_read_day);
						mReadConfig.setColorIndex(4);
						mReadConfig.setLastColorIndex(a);
						redrawPage();
					}else {
						ivNightDay.setBackgroundResource(R.drawable.boe_read_night);
						a=mReadConfig.getLastColorIndex();
						if (a!=-1) {							
							mReadConfig.setColorIndex(a);
						}
						redrawPage();
					}
				}
			});
			// 2. 进度
//			TextView tvProgress = (TextView) readActionView
//					.findViewById(R.id.read_progress_tv);
//			tvProgress.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if (progressView.getVisibility() == View.GONE) {
//						progressView.setVisibility(View.VISIBLE);
//						setView.setVisibility(View.GONE);
//					} else {
//						progressView.setVisibility(View.GONE);
//						setView.setVisibility(View.VISIBLE);
//					}
//				}
//			});

			// 3.字体
			LinearLayout tvFontSub = (LinearLayout) readActionView
					.findViewById(R.id.read_font_sub_btn);
			LinearLayout tvFontAdd = (LinearLayout) readActionView
					.findViewById(R.id.read_font_add_btn);

			tvFontSub.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int size = mReadConfig.getTextSize();
					if (mReadConfig.setTextSize(--size)) {
						mCacheManager.reset();

						redrawPage();
						// mBookFactory.draw(mCurPageCanvas);
						// mPageWidget.postInvalidate();

					} else {
						showToastByHandler("已经是最小号字体了", Toast.LENGTH_SHORT);
					}
				}
			});

			tvFontAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int size = mReadConfig.getTextSize();
					if (mReadConfig.setTextSize(++size)) {
						mCacheManager.reset();
						redrawPage();
						// mBookFactory.draw(mCurPageCanvas);
						// mPageWidget.postInvalidate();

					} else {

						showToastByHandler("已经是最大号字体了", Toast.LENGTH_SHORT);
					}
				}
			});

			// 4.亮度
			SeekBar seekBar = (SeekBar) readActionView
					.findViewById(R.id.read_seekBar);
			seekBar.setMax(255);
			// 取得当前亮度
			int normal = Settings.System.getInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, 255);

			if (mReadConfig.isSysBrightness()) {

				seekBar.setProgress(normal);
			} else {

				seekBar.setProgress(mReadConfig.getReadBrightness());
			}

			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					// 取得当前进度
					int tmpInt = seekBar.getProgress();
					// 当进度小于80时，设置成80，防止太黑看不见的后果。
					if (tmpInt < 20) {
						tmpInt = 20;
					}
					float tmpFloat = (float) tmpInt / 255;
					// float tmpFloat = (float) tmpInt;
					if (tmpFloat > 0 && tmpFloat <= 1) {
						// wl.screenBrightness = tmpFloat;
						setBrightness(tmpFloat);
						// 存上亮度
						// DebugLog.e("存上亮度", tmpInt+"");
						mReadConfig.setReadBrightness(tmpInt);
						mReadConfig.setSysBrightness(false);
					}
					// getWindow().setAttributes(wl);

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					// if(progress > 5) {
					// WindowManager.LayoutParams lp =
					// getWindow().getAttributes();
					// lp.screenBrightness = Float.valueOf(progress) * (1f /
					// 255f);
					// getWindow().setAttributes(lp);
					// mReadConfig.setReadBrightness(progress);
					// }
					// 取得当前进度
					int tmpInt = seekBar.getProgress();
					// 当进度小于80时，设置成80，防止太黑看不见的后果。
					if (tmpInt < 20) {
						tmpInt = 20;
					}
					float tmpFloat = (float) tmpInt / 255;
					// float tmpFloat = (float) tmpInt;
					if (tmpFloat > 0 && tmpFloat <= 1) {
						// wl.screenBrightness = tmpFloat;
						setBrightness(tmpFloat);
						// 存上亮度
						// DebugLog.e("存上亮度", tmpInt+"");
						mReadConfig.setReadBrightness(tmpInt);
						mReadConfig.setSysBrightness(false);
					}
				}
			});

			// ；夜间模式

//			nightIv = (ImageView) readActionView
//					.findViewById(R.id.black_night_mode);
//			nightIv.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					mReadConfig.setColorIndex(4);
//					redrawPage();
//				}
//			});

			// 5.阅读背景
			final int bgRes[] = new int[] { R.drawable.boyi_ic_read_bg_0,
					R.drawable.boyi_ic_read_bg_1, 
					R.drawable.boyi_ic_read_bg_3,R.drawable.boyi_ic_read_bg_2 };
			/* , R.drawable.ic_read_bg_night */
			final int bgResSelected[] = new int[] {
					R.drawable.boyi_ic_read_bg_0_selected,
					R.drawable.boyi_ic_read_bg_1_selected,
					R.drawable.boyi_ic_read_bg_3_selected,
					R.drawable.boyi_ic_read_bg_2_selected
			};
			/*
			 * , R.drawable.ic_read_bg_night_selected
			 */

			final List<HashMap<String, Integer>> list = new ArrayList<HashMap<String, Integer>>();
			for (int i = 0; i < bgRes.length; i++) {
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				if (mReadConfig.getColorIndex() == i) {
					map.put("readBg_ic", bgResSelected[i]);
				} else {
					map.put("readBg_ic", bgRes[i]);
				}
				list.add(map);
			}

			final SimpleAdapter adapter = new SimpleAdapter(this, list,
					R.layout.boyi_read_bg_item, new String[] { "readBg_ic" },
					new int[] { R.id.read_bg_iv });

			GridView gridView = (GridView) readActionView
					.findViewById(R.id.read_bg_gridview);
			gridView.setVisibility(View.VISIBLE);
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					int index = mReadConfig.getColorIndex();
					if (index==4) {
						index=mReadConfig.getLastColorIndex();
					}
					if (index != position||index==4) {
						HashMap<String, Integer> map;

						if (index >= 0 && index < bgRes.length) {
							map = list.get(index);
							map.put("readBg_ic", bgRes[index]);
						}
						map = list.get(position);
						map.put("readBg_ic", bgResSelected[position]);
						ivNightDay.setBackgroundResource(R.drawable.boe_read_night);
						adapter.notifyDataSetChanged();

						mReadConfig.setColorIndex(position);
						redrawPage();

						// mBookFactory.draw(mCurPageCanvas);
						// mPageWidget.postInvalidate();
					}

				}
			});

			// 6.行间距
			final ImageView ivLinespacing[] = new ImageView[4];
			ivLinespacing[0] = (ImageView) readActionView
					.findViewById(R.id.read_linespacing0_iv);
			ivLinespacing[1] = (ImageView) readActionView
					.findViewById(R.id.read_linespacing1_iv);
			ivLinespacing[2] = (ImageView) readActionView
					.findViewById(R.id.read_linespacing2_iv);
			ivLinespacing[3] = (ImageView) readActionView
					.findViewById(R.id.read_linespacing3_iv);

			final int lineRes[] = new int[] { R.drawable.boyi_ic_linespacing0,
					R.drawable.boyi_ic_linespacing1,
					R.drawable.boyi_ic_linespacing2,
					R.drawable.boyi_ic_linespacing3 };
			final int lineResSelected[] = new int[] {
					R.drawable.boyi_ic_linespacing0_selected,
					R.drawable.boyi_ic_linespacing1_selected,
					R.drawable.boyi_ic_linespacing2_selected,
					R.drawable.boyi_ic_linespacing3_selected };
			// 初始化行间距
			int last0 = mReadConfig.getLineSpacingIndex();
			ivLinespacing[last0].setImageResource(lineResSelected[last0]);
			mReadConfig.setLineSpacingIndex(last0);
			for (int i = 0; i < ivLinespacing.length; i++) {
				final int index = i;
				ivLinespacing[index].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int last = mReadConfig.getLineSpacingIndex();
						ivLinespacing[last].setImageResource(lineRes[last]);
						ivLinespacing[index]
								.setImageResource(lineResSelected[index]);

						mReadConfig.setLineSpacingIndex(index);

						mCacheManager.reset();
						redrawPage();

					}
				});
			}

			// 7.简体/繁体 切换
			// TextView tvChinese = (TextView) readActionView
			// .findViewById(R.id.read_chinese_btn);
			// tvChinese.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// mReadConfig.setSimpleChinese();
			//
			// mCacheManager.reset();
			// redrawPage();
			// }
			// });

			// 8.更多选项
			// TextView tvMore = (TextView) readActionView
			// .findViewById(R.id.read_more_btn);
			// tvMore.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// Intent intent = new Intent(OnlineReadingActivity.this,
			// ReadSettingActivity.class);
			// startActivity(intent);
			// }
			// });

			// ///////////////////////////////////////////////////////////////////////////////////////
			// 进度跳转
			// ///////////////////////////////////////////////////////////////////////////////////////

			tvPageProgress = (TextView) readActionView
					.findViewById(R.id.read_page_progress_tv);

			// 1.上一章
			TextView tvChapterUp = (TextView) readActionView
					.findViewById(R.id.read_chapter_up_btn);
			tvChapterUp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (mBookItem.lastChapterPos == 0) {
						showToastByHandler("已经是第一章了", Toast.LENGTH_SHORT);
					} else {
						chapterUp();
						mCacheManager.getCache(PageIndex.current).pageFirst();
						redrawPage();

						setPageProgress();
					}
				}
			});
			// 2. 下一章
			TextView tvChapterDown = (TextView) readActionView
					.findViewById(R.id.read_chapter_down_btn);
			tvChapterDown.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (mBookItem.lastChapterPos + 1 >= mRequest.size()) {
//						showEndPageWindow(1);
						showToastByHandler("没有下一章了", Toast.LENGTH_SHORT);
					} else {
						chapterDown(false);
						redrawPage();

						setPageProgress();
					}
				}
			});

			// 3. 上一页
			TextView tvPageUp = (TextView) readActionView
					.findViewById(R.id.read_page_up_btn);
			tvPageUp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					DebugLog.d(TAG, "do pageUp at action");
					if (mBookFactory.pageUp()) {
						redrawPage();

						setPageProgress();
					} else {
						showToastByHandler("已是当前章节的第一页了", Toast.LENGTH_SHORT);
					}
				}
			});

			// 4. 下一页
			TextView tvPageDown = (TextView) readActionView
					.findViewById(R.id.read_page_down_btn);
			tvPageDown.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (mBookFactory.pageDown()) {
						redrawPage();

						setPageProgress();
					} else {
						showToastByHandler("已是当前章节的最后一页了", Toast.LENGTH_SHORT);
					}

				}
			});

			// 5. 阅读进度 -- 跳转章节
			seekBarChapterProgress = (SeekBar) readActionView
					.findViewById(R.id.read_chapter_progress_seekBar);
			// seekBarChapterProgress.setMax(100);
			seekBarChapterProgress
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							BookCache cache = mCacheManager
									.getCache(PageIndex.current);
							// int pagePos = (int)(seekBar.getProgress() *
							// cache.getPageCount() * 1.0 / seekBar.getMax());
							int pagePos = seekBar.getProgress();
							cache.setPage(pagePos);
							redrawPage();

							tvPageProgress.setText((cache.getCurPage() + 1)
									+ "/" + cache.getPageCount());
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							// TODO Auto-generated method stub
						}
					});

			// 6. 回退
			TextView tvProgressBack = (TextView) readActionView
					.findViewById(R.id.read_progress_back_btn);
			tvProgressBack.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (lastChapterPos != mBookItem.lastChapterPos) {
						mRequest.getRequestChapterCache(lastChapterPos,
								ChapterAction.JUMP);
					}

					BookCache cache = mCacheManager.getCache(PageIndex.current);
					cache.setPage(lastPagePos);
					redrawPage();

					setPageProgress();

					int progress = (int) (seekBarChapterProgress.getMax()
							* cache.getCurPage() * 1.0 / cache.getPageCount());
					seekBarChapterProgress.setProgress(progress);
				}
			});
		}else {
			ivDetail.setBackgroundResource(R.drawable.read_bottom_detil);
			ivContents.setBackgroundResource(R.drawable.read_dictory_bar);
			ivSetting.setBackgroundResource(R.drawable.read_setting_bar);
			ivError.setBackgroundResource(R.drawable.read_post_error);
			downMore.setBackgroundResource(R.drawable.read_sett_download_no);
			setView.setVisibility(View.GONE);
			bottomView.setVisibility(View.VISIBLE);
			linearLayout.setVisibility(View.GONE);
		}
		if (mReadConfig.isAutoBuy()) {
			tvAutoBuy.setImageResource(R.drawable.read_auto_buy_yes);
		} else {
			tvAutoBuy.setImageResource(R.drawable.read_auto_buy);
		}
		setPageProgress();
		View progressView = readActionView
				.findViewById(R.id.read_progress_layout);
		View setView = readActionView.findViewById(R.id.read_set_layout);
		if (progressView.getVisibility() == View.VISIBLE) {
			progressView.setVisibility(View.GONE);
		}
//		if (setView.getVisibility() == View.GONE) {
//			setView.setVisibility(View.VISIBLE);
//		}
		// BookCache cache = mCacheManager.getCache(PageIndex.current);
		// int progress = (int)(seekBarChapterProgress.getMax() *
		// cache.getCurPage() * 1.0 / cache.getPageCount());
		// seekBarChapterProgress.setProgress(progress);

		return readActionView;
	}

	private void setPageProgress() {
		BookCache cache = mCacheManager.getCache(PageIndex.current);
		tvPageProgress.setText((cache.getCurPage() + 1) + "/"
				+ cache.getPageCount());

		seekBarChapterProgress.setMax(cache.getPageCount() - 1);
		// int progress = (int)(seekBarChapterProgress.getMax() *
		// cache.getCurPage() * 1.0 / cache.getPageCount());
		int progress = cache.getCurPage();

		seekBarChapterProgress.setProgress(progress);
	}

	/********************************************************************************************************************/

	abstract private class BookRequest {

		abstract public void open();

		abstract public void close();

		abstract public void getRequestChapterCache(final int chapterPos,
				final ChapterAction action);

		abstract public int size();

		abstract public String getChapterName();

		abstract public void showContent();

	}

	/**
	 * @author WindowY
	 */
	private String xnBid;

	private ArrayList<OnlineChapterInfo> mContentsList = new ArrayList<OnlineChapterInfo>();
	private ArrayList<OnlineChapterInfo> mXNContentsList = new ArrayList<OnlineChapterInfo>();
	private ArrayList<OnlineChapterInfo> mContentsList2=new ArrayList<OnlineChapterInfo>();
	private ArrayList<OnlineChapterInfo> mContentsListYd2=new ArrayList<OnlineChapterInfo>();

	private class OnlineBookRequest extends BookRequest {

		public OnlineBookRequest() {

		}

		@Override
		public void open() {
			// TODO Auto-generated method stub
			if (xnBid.equals("") || xnBid == null) { // 该书没有映射或者映射被删除
				isHaveMapTable = false;
				if (isXNContentsLoaded()) { // 存在映射的目录文件，清理目录及缓存文件
					File f = new File(AppData.getConfig().getXNContentName(
							mBookItem.onlineID));
					if (f.exists()) {
						deleteDir(f);
					}
					
					AppData.closeXNDBContent(mBookItem.onlineID);
				}
				// 得到移动的目录，并从移动读取
				if (isContentsLoaded()) { // 从本地数据库中读取目录
					ReadContentsTask task = new ReadContentsTask("readContents"  // 可取消
							+ mBookItem.onlineID, mBookItem.onlineID);		
					AppData.getClient().getTaskManagerRead().addTask(task);
			
				} else {
					getRequestContents(0);   // 可取消,获取快播目录
				}

			} else {
				isHaveMapTable = true;
				// 存映射表对应的目录，
				if (isXNContentsLoaded()) { // 本地存在血凝目录的时候,对其进行更新
					hideProgressByHandler();
					ReadXNContentsTask task = new ReadXNContentsTask(
							"readContentsYD" // 可取消
									+ mBookItem.onlineID, mBookItem.onlineID);
					 showCancelProgressByHandler(task.getTaskName(),"更新目录");
					AppData.getClient().getTaskManagerRead().addTask(task);
				} else {
					getXNRequestContents(0,mBookItem.onlineID);
				}

			}
		}

		/**
		 * 递归删除目录下的所有文件及子目录下所有文件
		 * 
		 * @param dir
		 *            将要删除的文件目录
		 */
		private boolean deleteDir(File dir) {
			if (dir.isDirectory()) {
				String[] children = dir.list();

				for (int i = 0; i < children.length; i++) {
					boolean success = deleteDir(new File(dir, children[i]));
					if (!success) {
						return false;
					}
				}
			}
			// 目录此时为空，可以删除
			return dir.delete();
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			// ruguoyou
			
			hideBuyWindow();

			// 更新阅读位置到服务器
			int lastPosition = mCacheManager.getCache(PageIndex.current)
					.getCurPagePosition();
			long lastDate = System.currentTimeMillis();
			mBookItem.lastPosition = lastPosition;
			mBookItem.lastDate = lastDate;
			mBookItem.chapterTotal = mContentsList.size();
			// 1.检查书籍是否已在书架上
			LayoutInflater inflaterDl = LayoutInflater.from(OnlineReadingActivity.this);
	        RelativeLayout layout = (RelativeLayout)inflaterDl.inflate(R.layout.boy_add_to_bookshelf_dialog, null );
	        
	        final AlertDialog alertDialog=new AlertDialog.Builder(OnlineReadingActivity.this).create();
	        alertDialog.show();
	        alertDialog.getWindow().setContentView(layout);
			Button btnpositive= (Button) layout.findViewById(R.id.positive);
			Button btngoagain=(Button) layout.findViewById(R.id.goagain);
			Button btnNegative=(Button) layout.findViewById(R.id.negative);
			Button btnReject=(Button) layout.findViewById(R.id.reject);
			TextView textMsg=(TextView) layout.findViewById(R.id.text_msg);
			TextView textAsk=(TextView) layout.findViewById(R.id.ask_leave);
			ImageView btnClose=(ImageView) layout.findViewById(R.id.guanbi);
			btnClose.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
				}
			});
			
			//书架上没有该书
			if (!AppData.getDataHelper().foundBookBid(mBookItem.bid)) {
				if(isBanner)
				{
					textMsg.setVisibility(View.VISIBLE);
					textAsk.setVisibility(View.GONE);
					btnpositive.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							mBookItem.timeStamp=System.currentTimeMillis();
							AppData.getDataHelper()
									.insertKBBook(mBookItem);

							getRequestLastRead();
							AppData.getClient()
									.sendCallBackMsg(
											CallBackMsg.UPDATE_BOOKSHELF);
							AppData.getDataHelper()
									.updateLastKBReadOnline(
											mBookItem.onlineID,
											mBookItem.lastChapterPos,
											mBookItem.lastPosition,
											mBookItem.status,mBookItem.timeStamp);
							int id = AppData.getDataHelper()
									.getKBBookID(
											mBookItem.onlineID);
//							AppData.getUser().setLastBookID(id);

							finish();
						}
					});
					//不加入书架
					btnNegative.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							finish();
						}
					});
					btngoagain.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(
									OnlineReadingActivity.this,
									StoreMain.class);
							startActivity(intent);
						}
					});
				}else
				{
					//隐藏再逛逛
					btnpositive.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
						mBookItem.timeStamp=System.currentTimeMillis();
							AppData.getDataHelper()
							.insertKBBook(mBookItem);
					DebugLog.e("新添加的书籍章节数为",
							mBookItem.totalCount
									+ ">>>>>>>");
					getRequestLastRead();
					AppData.getClient()
							.sendCallBackMsg(
									CallBackMsg.UPDATE_BOOKSHELF);
					AppData.getDataHelper()
							.updateLastKBReadOnline(
									mBookItem.onlineID,
									mBookItem.lastChapterPos,
									mBookItem.lastPosition,
									mBookItem.status,mBookItem.timeStamp);
					int id = AppData.getDataHelper()
							.getKBBookID(
									mBookItem.onlineID);
					AppData.getUser().setLastBookID(id);

					finish();
						}
					});
					btnNegative.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							finish();
						}
					});
					btngoagain.setVisibility(View.GONE);
				}
				btnReject.setVisibility(View.GONE);
			}else{
				if(isBanner)
				{
					btnpositive.setVisibility(View.GONE);
					btnNegative.setVisibility(View.GONE);
					textMsg.setVisibility(View.GONE);
					textAsk.setVisibility(View.VISIBLE);
					btngoagain.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(
									OnlineReadingActivity.this,
									StoreMain.class);
							startActivity(intent);
						}
					});
					btnReject.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							mBookItem.timeStamp=System.currentTimeMillis();
							AppData.getClient()
							.sendCallBackMsg(
									CallBackMsg.UPDATE_BOOKSHELF);
							AppData.getDataHelper()
							.updateLastKBReadOnline(
									mBookItem.onlineID,
									mBookItem.lastChapterPos,
									mBookItem.lastPosition,
									mBookItem.status,mBookItem.timeStamp);

					int id = AppData
							.getDataHelper()
							.getKBBookID(
									Integer.parseInt(mBookItem.bid));
//					AppData.getUser().setLastBookID(id); // 记录上次阅读的书籍
					finish();
						}
					});
						
						
				}else
				{
					//本地保存，不弹出对话框
					alertDialog.dismiss();
					mBookItem.timeStamp=System.currentTimeMillis();
					DebugLog.e("时间戳",mBookItem.name+"::"+mBookItem.timeStamp);
					AppData.getClient()
					.sendCallBackMsg(
							CallBackMsg.UPDATE_BOOKSHELF);
					AppData.getDataHelper().updateLastKBReadOnline(
							mBookItem.onlineID, mBookItem.lastChapterPos,
							mBookItem.lastPosition, mBookItem.status,mBookItem.timeStamp);
					int id = AppData.getDataHelper().getKBBookID(
							Integer.parseInt(mBookItem.bid));
//					AppData.getUser().setLastBookID(id); // 记录上次阅读的书籍
					setResult(1);
					// 2.服务器保存
//					getRequestLastRead();
					finish();
				}
				
			}
			//排序
			

		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mContentsList.size();
		}

		@Override
		public String getChapterName() {
			// TODO Auto-generated method stub
			if (xnBid.equals("")||xnBid ==null) {
				if (mContentsList.size() == 0
						|| mBookItem.lastChapterPos >= mContentsList.size()) {
					return "";
				} else {
					return mContentsList.get(mBookItem.lastChapterPos).name;
				}				
			}else {
				if (mXNContentsList.size() == 0
						|| mBookItem.lastChapterPos >= mXNContentsList.size()) {
					return "";
				} else {
					return mXNContentsList.get(mBookItem.lastChapterPos).name;
				}
				
			}
		}

		@Override
		public void showContent() {
			// TODO Auto-generated method stub
			
			Intent intent = new Intent(OnlineReadingActivity.this,
					OnlineContentsActivity.class);
			intent.putExtra("BookItem", mBookItem);
			intent.putExtra("isComeRead", true);
			if (xnBid.equals("")||xnBid ==null) {
				
				intent.putExtra("tablelist", false);
				
//				intent.putExtra("contentsList", mContentsList);
			}else {
				intent.putExtra("tablelist", true);
//				intent.putExtra("contentsList", mXNContentsList);				
			}
			// startActivity(intent);
			startActivityForResult(intent,ResultCode.JUMP_TO_POSITION);
		}

		private boolean isContentsLoaded() {
			File f = new File(AppData.getConfig().getContentDBName(
					mBookItem.onlineID));
			return f.exists();
		}

		private boolean isXNContentsLoaded() { // 是否有血凝的目录数据库
			File f = new File(AppData.getConfig().getXNContentDBName(
					mBookItem.onlineID));
			return f.exists();
		}

		/*
		 * 获取更新yd小说目录信息
		 */
		private void getRequestContents(int lastCid) {
//			hideProgressByHandler();
			TaskManager tm = AppData.getClient().getTaskManagerRead();
			ReadContentFromSDK contentFromNet = new ReadContentFromSDK("getYDCount",
					mBookItem.bid, lastCid,false);
//			showCancelProgressByHandler(contentFromNet.getTaskName(),"获取目录中");
			tm.addTask(contentFromNet);
		}
		/*
		 * 有映射表后获取更新yd小说目录信息
		 */
		private void getRequestYDContents(int lastCid) {
			
			TaskManager tm = AppData.getClient().getTaskManagerRead();
			ReadYDContentFromSDK contentFromNet = new ReadYDContentFromSDK("有映射获取yd目录task",
					mBookItem.bid, lastCid);
//			showCancelProgressByHandler(contentFromNet.getTaskName(),"联网获取目录..");
			tm.addTask(contentFromNet);
		}

		/*
		 * 获取映射表小说目录信息
		 */
		private  int mBid;
		private void getXNRequestContents(int lastCid,int bid) {			

			int lastChapterID = lastCid;

			mBid=bid;
			String url = AppData.getConfig().getUrl(Config.URL_XNCONTENTS)+xnBid+"/lastChapterID/"+lastChapterID;
			DebugLog.d(TAG, url);
			getRequestQueue().add(

			new StringRequest(url, new Listener<String>() {

				@Override
				public void onResponse(String response) {
					// TODO Auto-generated method stub
					try {
						JSONObject responseJson = new JSONObject(response);
						int status = responseJson.getInt("status");
						if (status == StatusCode.OK) {
							JSONArray array = responseJson.getJSONArray("data");
							if (array == null) {
								return;
							} else {

								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									OnlineChapterInfo info = new OnlineChapterInfo();
									info.id = obj.getInt("order");
									info.name = obj.getString("title");
									
									mXNContentsList.add(info);
								}
								DebugLog.d(TAG, "映射表..目录获取成功");
								
								if(!TextUtils.isEmpty(""))
								{
								 WriteXNContentsTask task = new WriteXNContentsTask(
										"writeXNContent" + mBookItem.onlineID,
										Integer.parseInt(xnBid));
								 AppData.getClient().getTaskManager()
										.addTask(task);
								}
										// 得到快播的目录，并从快播读取
										if (isContentsLoaded()) { // 从本地数据库中读取目录
											ReadYDContentsTask taskYD = new ReadYDContentsTask("读本地映射yd目录task"  // 可取消
													+ mBookItem.onlineID, mBookItem.onlineID);
											AppData.getClient().getTaskManagerRead().addTask(taskYD);
										} else {
											getRequestYDContents(0);
										}
										
									}
								} 

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								if (mXNContentsList.size()>0) {									
//								检查快播目录
									if (isContentsLoaded()) { // 从本地数据库中读取目录
										ReadYDContentsTask taskYD = new ReadYDContentsTask("读本地映射yd目录task"  // 可取消
												+ mBookItem.onlineID, mBookItem.onlineID);
										AppData.getClient().getTaskManagerRead().addTask(taskYD);
										
//										showCancelProgressByHandler(taskYD.getTaskName());//读取yd本地数据库，获取移动目录  玩命加载中..
										
									} else {
										getRequestYDContents(0);  // 可取消,获取快播目录
									}
								}else {
									xnBid="";
									mRequest.open();
								}

							}
							
						
					}}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					xnBid="";
					mRequest.open();
				}
			}));

		}

		// 判断xn是否有缓存
		private boolean isXNChapterLoaded(int chapterID) {
			File f = new File(AppData.getConfig().getXNOnlineChapterFilePath(
					mBookItem.onlineID, chapterID));
			return f.exists();
		}

		private boolean isChapterLoaded(int chapterID) { // 判断移动是否有缓存
			File f = new File(AppData.getConfig().getOnlineChapterFilePath(
					mBookItem.onlineID, chapterID));
			return f.exists();
		}

		// 存映射表章节目录
		private class WriteXNContentsTask extends Task {
			private int bid;

			public WriteXNContentsTask(String strTaskName, int xnBid) {
				super(strTaskName);
				// TODO Auto-generated constructor stub
				this.bid = xnBid;
			}
			@Override
			protected void doTask() {
				// TODO Auto-generated method stub
				if (mXNContentsList.size()>0) {					
					AppData.getXNContentHelper(mBookItem.onlineID)
					.insertChapterList(mXNContentsList);
				}
			}
		}
		
		@Override
		public void getRequestChapterCache( int chapterPos,

		final ChapterAction action) {
			// hideProgress();	
			//当前没有目录。就加载
			if (mContentsList.size()==0) {
				if (!isClose) {
					showDialogByHandler("", "获取目录失败，请检查网络状态并重试", 3000,
							new MyAlert.DialogOnClickListener() {

								@Override
								public void doPositive() {
									// TODO Auto-generated method stub
									getMapTable();
								}

								@Override
								public void doNegative() {
									// TODO Auto-generated method stub
									return;
								}
							});
				}
				return;
			}
			int chapterPos2=chapterPos;
			if (chapterPos>=mXNContentsList.size()&&(!xnBid.equals(""))) {
				chapterPos2=0;
			}
			chapterPos=chapterPos2;
			if (chapterPos>=mContentsList.size()) {
				chapterPos2=mContentsList.size()-1;
			}
			chapterPos=chapterPos2;
			final OnlineChapterInfo info=mContentsList.get(chapterPos);// 根据章节脚本从章节集合中取出来;
			if (info.status == Status.LOADING) { // 判断章节状态是正在从网络加载中,
				if (action.index < ChapterAction.CACHE_PREV.index && !isFirstLoding) { // 当章节内容是
					DebugLog.d(TAG, "showProgress...");
					showCancelProgressByHandler("","加载中..");
					isFirstLoding=true;
				}
				DebugLog.w(
						TAG,
						"this chapter is already loading (id:" + info.id
								+ ", pos:" + chapterPos + ",action:"
								+ action.toString() + ",status:" + info.status
								+ ")");
				final Message msg=new Message();
				msg.what=CallBackMsg.READING_LOOPER_STAST;
				msg.arg1=chapterPos;
				msg.obj=action;	
				mCallBack.sendMessageDelayed(msg, 600);
				return;
			}
			OnlineChapterInfo infoXN = null;
			
			if (chapterPos==17 && (action.index ==ChapterAction.CACHE_NEXT.index  || action.index ==ChapterAction.JUMP.index )) {
				int operatorType = DeviceInfo
						.getOperator(OnlineReadingActivity.this);
				int loginType = -1;				
				if (operatorType == DeviceInfo.OPERATOR_CM) {
					loginType = LoginChapter.TYPE_CM;
				} else if (operatorType == DeviceInfo.OPERATOR_CU) {
					loginType = LoginChapter.TYPE_CU;
				}
				else if (operatorType == DeviceInfo.OPERATOR_TC) {
					loginType = LoginChapter.TYPE_TELCOM;
				}
				LoginHelper.getInstatnce().startLogin(OnlineReadingActivity.this, "400000091", "400051394",loginType, new LoginHelper.LoginCallback() {
					
					@Override
					public void loginSuccess(Chapter chapter) {
						// TODO Auto-generated method stub
//						Http.save();
						mCallBack.sendMessage(mCallBack.obtainMessage(CallBackMsg.SHOW_TOAST_MESSAGE, OnlineReadingActivity.this.getResources().getText(R.string.boyi_readbook_login_success,"login success")));
					}
					
					@Override
					public void loginStart() {
						// TODO Auto-generated method stub
						mCallBack.sendMessage(mCallBack.obtainMessage(CallBackMsg.INIT_SHOW_LOGIN));
					}
					
					@Override
					public void loginFail() {
						// TODO Auto-generated method stub
						mCallBack.sendMessage(mCallBack.obtainMessage(CallBackMsg.SHOW_TOAST_MESSAGE, OnlineReadingActivity.this.getResources().getText(R.string.boyi_readbook_login_fail,"login fail")));
					}
					
					@Override
					public void LoginError(int type, String error) {
						// TODO Auto-generated method stub
						mCallBack.sendMessage(mCallBack.obtainMessage(CallBackMsg.SHOW_TOAST_MESSAGE, OnlineReadingActivity.this.getResources().getText(R.string.boyi_readbook_login_fail,"login fail")));
					}
				});
			}	
		
			buyBook = new ReadChapterContentSDKTask("load " + mBookItem.name
					+ "--" + info.cid+action.index, mBookItem.bid, info,infoXN, chapterPos, action);

			// 章节信息
			onReadInfo(info.cid);
			
			if (action.index < ChapterAction.CACHE_PREV.index) { // 当章节内容是

				DebugLog.d(TAG, "showProgress...");
				showCancelProgressByHandler(buyBook.getTaskName(),"加载中");
			}
			
			// if (isHaveMapTable ) { // 映射表存在,且章节数大于要读的章节
			if (isHaveMapTable && chapterPos < mXNContentsList.size()) { // 映射表存在,且章节数大于要读的章节
				// 获取血凝内容
				if (isXNChapterLoaded(info.id)) {
					
					if (endDownloadPos !=0 && action.index == ChapterAction.LOAD.index) {
						batchDownload(chapterPos);  // 本地有缓存就直接调到下一张
						return;
					}
					info.status = Status.LOADING;
					ReadChapterTask task = new ReadChapterTask("chLocal雪凝本地线程"
							+ chapterPos, AppData.getConfig()
							.getXNOnlineChapterFilePath(mBookItem.onlineID,
									info.id), chapterPos, info, infoXN,action);
					AppData.getClient().getTaskManager().addTask(task);

					DebugLog.d(TAG, "start read chapter from cache file:"
							+ info.id + ", pos:" + chapterPos);

				} else {
					if (action.index > ChapterAction.JUMP.index && action.index < ChapterAction.LOAD.index && info.type >= OnlineChapterInfo.TYPE_NOT_BUY ) {						
						return;
					}
					AppData.getClient().getTaskManagerRead().addTask(buyBook);
				}

			} else { // 没有映射表就读取 移动的
				if (isChapterLoaded(info.id)) {
					
					if (endDownloadPos !=0 && action.index == ChapterAction.LOAD.index) {
						batchDownload(chapterPos);
					}
					info.status = Status.LOADING;
					ReadChapterTask task = new ReadChapterTask("chLocal移动本地线程"
							+ chapterPos, AppData.getConfig()
							.getOnlineChapterFilePath(mBookItem.onlineID,
									info.id), chapterPos, info,infoXN, action);
					AppData.getClient().getTaskManagerRead().addTask(task);

					DebugLog.d(TAG, "start read chapter from cache file:"
							+ info.id + ", pos:" + chapterPos);

				} else {
					// =====modify by qiaowei
					if (action.index > ChapterAction.JUMP.index && action.index < ChapterAction.LOAD.index && info.type >= OnlineChapterInfo.TYPE_NOT_BUY ) {	
						return;
					}
					AppData.getClient().getTaskManagerRead().addTask(buyBook);
				}
			}

		}

		private void getRequestLastRead() {
			if (mContentsList.size() <= mBookItem.lastChapterPos) {
				return;
			}

			String token = AppData.getUser().getToken();
			if (null == token || token.equals("")) {
				return;
			}

			JSONObject books = new JSONObject();
			try {
				JSONArray array = new JSONArray();
				JSONObject obj = new JSONObject();
				obj.put("id", mBookItem.onlineID);
				obj.put("chapter_id",
						mContentsList.get(mBookItem.lastChapterPos).id);
				obj.put("lastChapter", mBookItem.lastChapterPos);
				obj.put("lastPosition", mBookItem.lastPosition);
				array.put(0, obj);

				books.put("books", array);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

			Map<String, String> map = new HashMap<String, String>();
			map.put("books", books.toString());
			map.put("token", token);

			String url = AppData.getConfig().getUrl(Config.URL_UPDATE_PROGRESS);
			DebugLog.d(TAG, url);
			DebugLog.d(TAG, map.toString());

			getRequestQueue().add(
					new JsonObjectPostRequest(url, new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							// TODO Auto-generated method stub
							DebugLog.d(TAG, response.toString());

							try {
								int status = response.getInt("status");
								if (StatusCode.OK == status) {
									DebugLog.d(TAG, mBookItem.name
											+ "：阅读进度更新到服务器成功");
								} else {
									DebugLog.d(TAG,
											mBookItem.name
													+ "：阅读进度更新到服务器失败, msg:"
													+ response.getString("msg"));
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
							DebugLog.d(TAG, mBookItem.name
									+ "：阅读进度更新到服务器失败, 网络错误");
						}

					}, map));

		}

		private PopupWindow popWindowBuy = null;

		private void hideBuyWindow() {
			if (null != popWindowBuy) {
				popWindowBuy.dismiss();
			}

		}

		// 读移动本地目录
		private class ReadContentsTask extends CallBackTask {
			private int onlineID;

			public ReadContentsTask(String strTaskName, int online) {
				super(strTaskName);
				// TODO Auto-generated constructor stub
				this.onlineID = online;
			}

			@Override
			protected void doTask() {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, "readContentTask:"
						+ Thread.currentThread().toString());
				long start = System.currentTimeMillis();
				mContentsList = AppData.getContentHelper(onlineID)
						.getChapterList();
				DebugLog.e("目录长度"+mContentsList.size(), "最后阅读章节"+mBookItem.lastChapterPos);
				if (mContentsList.size() == 0
						|| mContentsList.size() < mBookItem.totalCount || mContentsList.size() <= mBookItem.lastChapterPos) {
					// 尝试从网络读取目录
					DebugLog.e("目录过短", "更新目录");
					getRequestContents(mContentsList.size());
				} else {
					mCallBack
					.sendEmptyMessage(CallBackMsg.CONTENTS_READ_CONTEXT);
				}

				DebugLog.d(TAG,
						"readContent used:"
								+ (System.currentTimeMillis() - start)
								/ 1000.0f + "s");

			}
		}
		// 有映射时读移动本地目录
		private class ReadYDContentsTask extends CallBackTask {
			private int onlineID;
			private int isBuyId;
			public ReadYDContentsTask(String strTaskName, int online) {
				super(strTaskName);
				// TODO Auto-generated constructor stub
				this.onlineID = online;
			}
			
			@Override
			protected void doTask() {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, "readContentTask:"
						+ Thread.currentThread().toString());
				long start = System.currentTimeMillis();
				mContentsList = AppData.getContentHelper(onlineID)
						.getChapterList();
				if (mContentsList.size() == 0
						|| mContentsList.size() < mBookItem.chapterTotal) {
					// 尝试从网络读取目录
					getRequestYDContents(mContentsList.size());
				} else {					
					hideProgressByHandler();
					mCallBack
					.sendEmptyMessage(CallBackMsg.CONTENTS_READ_CONTEXT);
					
				}
			}
		}

		// 读血凝本地目录
		private class ReadXNContentsTask extends CallBackTask {
			private int onlineID;

			public ReadXNContentsTask(String strTaskName, int online) {
				super(strTaskName);
				// TODO Auto-generated constructor stub
				this.onlineID = online;
			}

			@Override
			protected void doTask() {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, "readContentTask:"
						+ Thread.currentThread().toString());
				long start = System.currentTimeMillis();

				mXNContentsList = AppData.getXNContentHelper(onlineID)
						.getChapterList();

					// 尝试从网络更新目录
					getXNRequestContents(mXNContentsList.size(),onlineID);
					
				DebugLog.d(TAG,
						"readContent used:"
								+ (System.currentTimeMillis() - start)
								/ 1000.0f + "s");

			}
		}

		// 取章节本地内容
		private class ReadChapterTask extends CallBackTask {

			private String filePath;
			private ChapterAction action;
			private int chapterPos;
			private OnlineChapterInfo info;
			private OnlineChapterInfo infoxn;

			public ReadChapterTask(String strTaskName, String filePath,
					int chapterPos, OnlineChapterInfo info,OnlineChapterInfo infoXN, ChapterAction action) {
				super(strTaskName);
				// TODO Auto-generated constructor stub
				this.filePath = filePath;
				this.action = action;
				this.chapterPos = chapterPos;

				this.info=info;
				this.infoxn=infoXN;
			} 

			@Override
			protected void doTask() {
				// TODO Auto-generated method stub

				try {

					long start = System.currentTimeMillis();
					File f = new File(filePath);
					long len = f.length();

					RandomAccessFile raFile = new RandomAccessFile(filePath,
							"r");
					MappedByteBuffer mbBuffer = raFile.getChannel().map(
							FileChannel.MapMode.READ_ONLY, 0, len);

					byte[] byteBuffer = new byte[(int) len];
					for (int i = 0; i < (int) len; i++) {
						byteBuffer[i] = mbBuffer.get(i);
					}

					String strBuffer = AES.decrypt(byteBuffer, "utf-8");
					raFile.close();

					if (strBuffer==null || strBuffer.equals("")) {
						
						info.status = Status.UNLOAD;
						AppData.getContentHelper(mBookItem.onlineID).updateStatus(
								info.id, info.status);
//						if ((!xnBid.equals(""))|| (xnBid!=null) ){
							if (infoxn!=null){							
							AppData.getXNContentHelper(mBookItem.onlineID).updateStatus(
									info.id, info.status);
						}						
						AppData.getClient().getTaskManagerRead().addTask(buyBook);
						return;
					}
					DebugLog.d(TAG, "get chapter from cache file:" + info.name);
					// DebugLog.d(TAG, strBuffer.toString());
					DebugLog.d(
							TAG,
							"ReadChapterTask read used:"
									+ (System.currentTimeMillis() - start)
									/ 1000.0f + "s");
					if (!isClose) {
						// if (! ReadChapterTask.this.mbCancel) {
						refreshNewChapter(info.name, strBuffer, chapterPos,
								action, this);
					}

					// 缓存下一章
//					prepareCacheNext();

					info.status = Status.LOADED;
						if (infoxn!=null) {							
							infoxn.status=Status.LOADED;
						}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					sendMessage(CallBackMsg.CHAPTER_CONTENT_ERROR, chapterPos,
							action.index);
					info.status = Status.UNLOAD;
					AppData.getContentHelper(mBookItem.onlineID).updateStatus(
							info.id, info.status);
//					if ((!xnBid.equals(""))|| (xnBid!=null) ){					
						if (infoxn!=null){					
						AppData.getXNContentHelper(mBookItem.onlineID).updateStatus(
								info.id, info.status);
					}
				}
				if (action.index < ChapterAction.CACHE_PREV.index && !isClose) {
					hideProgressByHandler();
				}

			}
		}
		// ============================
		// sdk task 从网上读取普通章节
		private void readChapterContentBySyc(int chapterPos,
				ChapterAction action, Chapter chapter,
				final ReadChapterContentSDKTask ptTask) {
			final OnlineChapterInfo info = mContentsList.get(chapterPos);
			ContentChapter cc = ((ContentChapter) chapter);

			if (chapter == null
					&& action.index < ChapterAction.CACHE_PREV.index) {
				// showProgressByHandler("","获取失败，请检查网络状态");
				// if (! ptTask.mbCancel) {
				if (!isClose) {
					showDialogByHandler("", "获取失败，请检查网络状态并重试", 3000,
							new MyAlert.DialogOnClickListener() {

								@Override
								public void doPositive() {
									// TODO Auto-generated method stub
									showCancelProgressByHandler(ptTask.getTaskName(), "加载中");
									AppData.getClient().getTaskManagerRead()
											.addTask(ptTask);
								}
								@Override
								public void doNegative() {
									// TODO Auto-generated method stub
									return;
								}
							});
				}
			}
			info.status =Status.LOADING;
			mContentsList.set(chapterPos, info);
			String content = cc.getContent();
			int count;
			DebugLog.d(TAG, Thread.currentThread().toString());

			if (content == null || content.equals("")) {

				AppData.getClient().getTaskManagerRead().addTask(ptTask);
				return;
			}			
			if (endDownloadPos !=0 && action.index == ChapterAction.LOAD.index) {
				batchDownMsg=new Message();
				batchDownMsg.what=CallBackMsg.SHEEP_DOWN_LOAD;
				batchDownMsg.arg1=chapterPos;
				mCallBack.sendMessageDelayed(batchDownMsg, 200);
				
			}		
			if (!isClose) {
				refreshNewChapter(info.name, content, chapterPos, action,
						ptTask);
			}
//			info.status = Status.LOADED;
			// 写入本地缓存
			String filePath = AppData.getConfig().getOnlineChapterFilePath(
					Integer.parseInt(mBookItem.bid), info.id);

			OutputFileTask task = new OutputFileTask("chWrite_"
					+ mBookItem.onlineID + "_" + info.id, content, filePath);

			AppData.getClient().getTaskManagerRead().addTask(task);
			// 注意1
			info.status = OnlineChapterInfo.Status.LOADED;
			AppData.getContentHelper(mBookItem.onlineID).updateStatus(info.id,
					Status.LOADED);
			
//			if (isHaveMapTable) {				
				AppData.getXNContentHelper(mBookItem.onlineID).updateStatus(info.id,
						Status.LOADED);
//			}

			if (action.index < ChapterAction.CACHE_PREV.index) {
				// if (! ptTask.mbCancel) {
				if (!isClose) {
					hideProgressByHandler();
				}
			}
		}

//		// 下载XN的映射章节
//		private class downXNChapterTask extends CallBackTask {
//			private String response;
//			private int chapterPos;
//			private ChapterAction action;
//
//			public downXNChapterTask(String strTaskName, String response,
//					int chapterPos, ChapterAction action) {
//				super(strTaskName);
//				// TODO Auto-generated constructor stub
//				this.response = response;
//				this.action = action;
//				this.chapterPos = chapterPos;
//			}
//
//			@Override
//			protected void doTask() {
//				// TODO Auto-generated method stub
//				OnlineChapterInfo info = mContentsList.get(chapterPos);
//
//				DebugLog.d(TAG, Thread.currentThread().toString());
//
//				refreshNewChapter(info.name, response, chapterPos, action,
//						downXNChapterTask.this);
//
//				// 写入本地缓存
//				// String filePath =
//				// AppData.getConfig().getOnlineChapterFilePath(mBookItem.onlineID,
//				// info.id);
//				// OutputFileTask task = new OutputFileTask("chWrite_" +
//				// mBookItem.onlineID + "_" + info.id, response, filePath);
//				// AppData.getClient().getTaskManager().addTask(task);
//
//				info.status = OnlineChapterInfo.Status.LOADED;
//				AppData.getContentHelper(mBookItem.onlineID).updateStatus(
//						info.id, Status.LOADED);
//
//				if (action.index < ChapterAction.CACHE_PREV.index) {
//					hideProgress();
//				}
//			}
//
//		}

		private class ReadChapterContentSDKTask extends CallBackTask { // 可取消

			private String bid;
			private String cid;
			private int chapterPos;
			private ChapterAction action;
			private OnlineChapterInfo info;
			private OnlineChapterInfo infoxn;
			public ReadChapterContentSDKTask(String taskName,String bid,OnlineChapterInfo info,OnlineChapterInfo infoxn,int chapterPos,ChapterAction action){
				super(taskName);
				this.bid = bid;
				this.cid = info.cid;
				this.chapterPos = chapterPos;
				this.action = action;
				this.info = info;
				this.infoxn=infoxn;
			}
			
			public ReadChapterContentSDKTask clone(){
				ReadChapterContentSDKTask task = new ReadChapterContentSDKTask(getTaskName(),bid,info,infoxn,chapterPos,action);
				return task;				
			}

			@Override
			protected void doTask() {
				// TODO Auto-generated method stub
//				info.status =Status.LOADING;
				long timeNew=System.currentTimeMillis();
				Chapter chapter=null;
				try {
					chapter = BookHelper.loadChapter(bid, cid);
					
				} catch (Exception e) {
					// TODO: handle exception
					if (action.index < ChapterAction.CACHE_PREV.index) {
						// showProgressByHandler("","获取失败，请检查网络状态");
						hideProgressByHandler();
						// if (! ReadChapterContentSDKTask.this.mbCancel) {
						if (!isClose) {

							showDialogByHandler("", "获取失败，请检查网络状态并重试", 3000,
									new MyAlert.DialogOnClickListener() {

										@Override
										public void doPositive() {
											// TODO Auto-generated method stub
											AppData.getClient()
													.getTaskManagerRead()
													.addTask(
															ReadChapterContentSDKTask.this);
											showCancelProgressByHandler(ReadChapterContentSDKTask.this.getTaskName(), "加载中");
										}

										@Override
										public void doNegative() {
											// TODO Auto-generated method stub
											hideProgressByHandler();
											return;
										}
									});
						}
					}else if (action.index == ChapterAction.LOAD.index  ) {
						if (endDownloadPos !=0 && action.index == ChapterAction.LOAD.index) {
							batchDownMsg=new Message();
							batchDownMsg.what=CallBackMsg.SHEEP_DOWN_LOAD;
							batchDownMsg.arg1=chapterPos;
							mCallBack.sendMessageDelayed(batchDownMsg, 200);
//							batchDownload(chapterPos);
						}
					}
				}
				DebugLog.d("", "==========chapter: " + chapter);
				if (chapter == null) {
					hideProgressByHandler();
					info.status = Status.UNLOAD;
					if (infoxn!=null) {
						infoxn.status=Status.UNLOAD;
					}
					if (endDownloadPos !=0 && action.index == ChapterAction.LOAD.index) {
//						batchDownload(chapterPos);
						batchDownMsg=new Message();
						batchDownMsg.what=CallBackMsg.SHEEP_DOWN_LOAD;
						batchDownMsg.arg1=chapterPos;
						mCallBack.sendMessageDelayed(batchDownMsg, 200);
					}
				}
				if (chapter == null
						&& action.index < ChapterAction.CACHE_PREV.index) {
					// showProgressByHandler("","获取失败，请检查网络状态");
					hideProgressByHandler();
					// if (! ReadChapterContentSDKTask.this.mbCancel) {
					if (!isClose) {

						showDialogByHandler("", "获取失败，请检查网络状态并重试", 3000,
								new MyAlert.DialogOnClickListener() {

									@Override
									public void doPositive() {
										// TODO Auto-generated method stub
										AppData.getClient()
												.getTaskManagerRead()
												.addTask(
														ReadChapterContentSDKTask.this);
										showCancelProgressByHandler(ReadChapterContentSDKTask.this.getTaskName(), "加载中");
									}

									@Override
									public void doNegative() {
										// TODO Auto-generated method stub
										hideProgressByHandler();
										info.status = Status.UNLOAD;
										if (infoxn!=null) {
											infoxn.status=Status.UNLOAD;
										}
										return;
									}
								});
					}
				}

				if (chapter instanceof ContentChapter) {
					if (isHaveMapTable && chapterPos < mXNContentsList.size()) {
						// if (isHaveMapTable) {
						// 加载xn网络章节并进行加解密
						getXNcontent();

					} else {
						readChapterContentBySyc(chapterPos, action, chapter,
								ReadChapterContentSDKTask.this);
					}

				}
				// else if(chapter instanceof LoginChapter && !
				// ReadChapterContentSDKTask.this.mbCancel){
				else if (chapter instanceof LoginChapter && !isClose) {

					AppData.getClient().getTaskManagerRead().delTask("initLoginTask");
					LoginChapter lc = ((LoginChapter) chapter);
					lc.toString();
					TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					int simState = telMgr.getSimState();
					if (simState == TelephonyManager.SIM_STATE_ABSENT) {
						hideProgressByHandler();
						info.status = Status.UNLOAD;
						showToastByHandler("请确认sim卡是否插入或者sim卡暂时不可用！",
								Toast.LENGTH_LONG);
						
						return;
					}

					// 判断运营商
					int operatorType = DeviceInfo
							.getOperator(OnlineReadingActivity.this);
					int loginType = -1;
					
					if (loginFast !=-1) {
//						1.快速登录页面返回的值
						loginType=loginFast;
					}else {	
						if (operatorType == DeviceInfo.OPERATOR_CM) {
							loginType = LoginChapter.TYPE_CM;
						} else if (operatorType == DeviceInfo.OPERATOR_CU) {
							loginType = LoginChapter.TYPE_CU;
						}
						else if (operatorType == DeviceInfo.OPERATOR_TC) {
							loginType = LoginChapter.TYPE_TELCOM;
						}
					}
					

					LoginHelper.getInstatnce().startLogin(OnlineReadingActivity.this,lc,loginType, new LoginHelper.LoginCallback() {
						
						@Override
						public void loginSuccess(Chapter chapter) {
							// TODO Auto-generated method stub
//							Toast.makeText(mContext, R.string.boyi_readbook_login_success, 1000).show();
							closeLoginWindowByHandler();
							if(!chapter.getBid().equals(bid) || !chapter.getCid().equals(cid)){
								AppData.getClient().getTaskManagerRead().addTask(ReadChapterContentSDKTask.this.clone());
								return;
							}
							if (chapter instanceof OrderChapter) {
								// success
								AppData.getClient().getTaskManagerRead().addTask(ReadChapterContentSDKTask.this.clone());
								closeLoginWindowByHandler();
//								dismissDirlogPop();
								return;
								
							}else if (chapter instanceof ContentChapter) {
								// success
								closeLoginWindowByHandler();
								showCancelProgressByHandler(ReadChapterContentSDKTask.this
										.getTaskName(),"加载中");
//								if (isHaveMapTable&& chapterPos < mXNContentsList.size()) {
								if (isHaveMapTable) {
									getXNcontent();
								} else {		
									readChapterContentBySyc(chapterPos, action, chapter,
											ReadChapterContentSDKTask.this);
								}
							}
						}
						
						@Override
						public void loginStart() {
							// TODO Auto-generated method stub
							mCallBack.post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									hideProgress();
									showDirLogwidonw("为了让您能看到更多更好的作品，系统将会向移动运营商发送一条免费短信进行快速登录，精彩内容在召唤。如有手机助手提示是否允许发送，请选择允许!");
								}
							});
						}
						
						@Override
						public void loginFail() {
							// TODO Auto-generated method stub
//							Toast.makeText(mContext, R.string.boyi_readbook_login_fail, 1000).show();
							closeLoginWindowByHandler();
							
							info.status = Status.UNLOAD;
							if (infoxn!=null) {
								infoxn.status = Status.UNLOAD;
							}
							Intent intent=new Intent(getApplication(), OneKeyFastActivity.class);
							startActivityForResult(intent, ResultCode.LOGIN_TO_FAST);
						}
						
						@Override
						public void LoginError(int type, String error) {
							// TODO Auto-generated method stub
//							Toast.makeText(mContext, R.string.boyi_readbook_login_fail, 1000).show();
							closeLoginWindowByHandler();

							info.status = Status.UNLOAD;
							if (infoxn!=null) {
								infoxn.status = Status.UNLOAD;
							closeLoginWindowByHandler();				
							Intent intent=new Intent(getApplication(), OneKeyFastActivity.class);
							startActivityForResult(intent, ResultCode.LOGIN_TO_FAST);
							AppData.getClient().getTaskManagerRead().delTask(ReadChapterContentSDKTask.this.getTaskName());
						}
					}});
					// send sms
//					showProgressByHandler("",
//							"正在向移动运营商发送一条免费短信进行登录。本条短信免费发送，如有手机助手提示是否允许发送，请选择允许");
//					mCallBack.post(new Runnable() {
//						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							hideProgress();
//							DebugLog.e("开始登录", "提示发短信中");
//							showDirLogwidonw("正在向移动运营商发送一条免费短信进行登录。本条短信免费发送，如有手机助手提示是否允许发送，请选择允许");
//						}
//					});
//				
//					int loginType2=loginType;
//					String number = lc.getLoginViaSmsNumber(loginType);
//					String content = lc.getLoginViaSmsContent(loginType);
//					
//					if (TextUtils.isEmpty(content)) {						
////						2.开启快速登录页面
//						closeLoginWindowByHandler();
//						
//						info.status = Status.UNLOAD;
//						Intent intent=new Intent(getApplication(), OneKeyFastActivity.class);
//						startActivityForResult(intent, ResultCode.LOGIN_TO_FAST);
//						return;
//					}
//					
//					
//					DeviceInfo.sendTextSms(OnlineReadingActivity.this, number,
//							content);					
//					long loginTime = 30000;
////					long loginTime = 15000;
//					long loginStartTime = System.currentTimeMillis();
//					long lTime = System.currentTimeMillis();
//					boolean isFirst = true;
//					boolean isShow = false;
//					boolean isSuccess = false;
//					Chapter c =null;
//					int count=0;
//					while (lTime - loginStartTime <= loginTime) {
////						Chapter c = lc.loginViaSms(loginType);
//						try {
//							if (isFirst) {
//								Thread.sleep(8000);
//								isFirst = false;
//							} else {
//								Thread.sleep(5000);
//							}
//						} catch (Exception e) {
//							// TODO: handle exception
//							hideProgressByHandler();
//							return;
//						}
//						
//						try {
//							c = lc.loginViaSms(loginType);	
//							
//						} catch (Exception e) {
//							// TODO: handle exception
//							DebugLog.e("注册崩溃", "提示重试");	
//							e.printStackTrace();
//							closeLoginWindowByHandler();
//							isSuccess = false;					
//							Intent intent=new Intent(getApplication(), OneKeyFastActivity.class);
//							startActivityForResult(intent, ResultCode.LOGIN_TO_FAST);
//							AppData.getClient().getTaskManagerRead().delTask(ReadChapterContentSDKTask.this.getTaskName());
//							break;
//						}
//						if (c == null) {
//							// wait 5 seconds,then query again
//							DebugLog.e("注册结果为空", "5秒后重试");							
//						} else if (c instanceof LoginChapter) {
//							// login error
//							DebugLog.e("注册结果返回：登录错误", "提示重试");
//							closeLoginWindowByHandler();
//							lc = ((LoginChapter) c);
//							String errorMsg = lc.getErrorMsg();
//							info.status = Status.UNLOAD;
//							
//							showDialogByHandler("", "登录失败了，请重试!", 3000,
//									new MyAlert.DialogOnClickListener() {
//
//										@Override
//										public void doPositive() {
//											// TODO Auto-generated method stub
//											if (buyBook!=null) {								
//												AppData.getClient().getTaskManagerRead().addTask(buyBook);	
//											}else {				
//												buyBook = new ReadChapterContentSDKTask("load " + mBookItem.name
//														+ "--" + info.cid, mBookItem.bid, info,infoxn, chapterPos, action);
//												AppData.getClient().getTaskManagerRead().addTask(buyBook);	
//											}
//										}
//										@Override
//										public void doNegative() {
//											// TODO Auto-generated method stub
//											hideProgressByHandler();
//											return;
//										}
//									});
//							return;
//
//						} else if (c instanceof OrderChapter) {
//							// success
//							DebugLog.e("注册结果返回：登录成功", "本章是未购买章节出现弹框");
//							isSuccess = true;
//							if (buyBook!=null) {								
//								AppData.getClient().getTaskManagerRead().addTask(buyBook);	
//							}else {				
//								buyBook = new ReadChapterContentSDKTask("load " + mBookItem.name
//										+ "--" + info.cid, mBookItem.bid, info,infoxn, chapterPos, action);
//								AppData.getClient().getTaskManagerRead().addTask(buyBook);	
//							}
//							closeLoginWindowByHandler();
////							dismissDirlogPop();
//							break;
//							
//						}else if (c instanceof ContentChapter) {
//							// success
//							DebugLog.e("注册结果返回：登录成功", "本章是已购买章节开始玩命加载中  内容");
//							isSuccess = true;
//							hideProgressByHandler();
//							closeLoginWindowByHandler();
//							showCancelProgressByHandler(ReadChapterContentSDKTask.this
//									.getTaskName(),"加载中");
////							if (isHaveMapTable&& chapterPos < mXNContentsList.size()) {
//							if (isHaveMapTable) {
//								isShow=true; // 图书展示了
//								getXNcontent();
//							} else {		
//								isShow=true;
//								readChapterContentBySyc(chapterPos, action, c,
//										ReadChapterContentSDKTask.this);
//							}
//							break;
//						}
//						lTime = System.currentTimeMillis();
//					}
//					if (!isSuccess) {
//						// 手动登录
//						// showToast("再次重试", Toast.LENGTH_LONG);
//						hideProgressByHandler();
//						closeLoginWindowByHandler();
//																					
//								showDialogByHandler("注意", "登录失败，请重试!", 3000,
//										new MyAlert.DialogOnClickListener() {
//									
//									@Override
//									public void doPositive() {
//										// TODO Auto-generated method stub
//										if (buyBook!=null) {								
//											AppData.getClient().getTaskManagerRead().addTask(buyBook);	
//										}else {				
//											buyBook = new ReadChapterContentSDKTask("load " + mBookItem.name
//													+ "--" + info.cid, mBookItem.bid, info,infoxn, chapterPos, action);
//											AppData.getClient().getTaskManagerRead().addTask(buyBook);	
//										}
//									}
//									
//									@Override
//									public void doNegative() {
//										// TODO Auto-generated method stub
//										return;
//									}
//								});
//								info.status = Status.UNLOAD;
//								if (infoxn!=null) {
//									infoxn.status = Status.UNLOAD;
//								}
//							}

				}
				else if (chapter instanceof OrderNotAllowedChapter && !isClose) {
					
					if (endDownloadPos !=0 && action.index == ChapterAction.LOAD.index) {
						batchDownload(chapterPos);	
						return;
					}
					
					OrderNotAllowedChapter onc = ((OrderNotAllowedChapter) chapter);
					// String notAllowedMessage = onc. getErrorMsg();
					info.status = Status.UNLOAD;

					if (infoxn!=null) {
						infoxn.status = Status.UNLOAD;
					}
					//显示订购不允许相关信息给用户
//					showToastByHandler("暂时无法购买该章节", Toast.LENGTH_LONG);
					showDialogByHandler("", "暂时无法购买该章节,请重试", 60000, new MyAlert.DialogOnClickListener() {
						
						@Override
						public void doPositive() {
							// TODO Auto-generated method stub
							if (buyBook!=null) {								
								AppData.getClient().getTaskManagerRead().addTask(buyBook);	
							}else {				
								buyBook = new ReadChapterContentSDKTask("load " + mBookItem.name
										+ "--" + info.cid, mBookItem.bid, info,infoxn, chapterPos, action);
								AppData.getClient().getTaskManagerRead().addTask(buyBook);	
							}
						}
						
						@Override
						public void doNegative() {
							// TODO Auto-generated method stub
							
						}
					});
				}
				else if (chapter instanceof OrderChapter && !isClose) {
					// chapter.getPrevCid();
					final OnlineChapterInfo info = mContentsList
							.get(chapterPos);
					OrderChapter oc = ((OrderChapter) chapter);
					final String orderInfo = oc.getOrderInfo();
					username = oc.getUserName();
					DebugLog.d(TAG, "orderInfo: " + orderInfo + "---username："
							+ username);
					
		if (action.index != ChapterAction.LOAD.index) {	
					if (!isGoBuy) { // 没有点击购买
						if (!lastIsFirst) {// 扣费 弹框没有出现过
							mCallBack.post(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									String catherName = info.name;
									isBuy = true;
									hideProgress();
									Intent intent=new Intent(OnlineReadingActivity.this, PaymentActivity.class);
									intent.putExtra("chargeInfo", orderInfo);
									startActivityForResult(intent, ResultCode.ORDER_INFO);
//									lastIsFirst = true; // 扣费弹框出现过了
								}
							});
							return;
						}
					}
					if (!mReadConfig.isAutoBuy() && !isGoBuy) { // 没有点确定购买 且
																// 没勾选自动购买
						mCallBack.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								String catherName = info.name;
								isBuy = true;
								hideProgress();
								Intent intent=new Intent(OnlineReadingActivity.this, PaymentActivity.class);
								intent.putExtra("chargeInfo", orderInfo);
								startActivityForResult(intent, ResultCode.ORDER_INFO);
							}
						});
						return;

					}
					isGoBuy = false;
					// showProgressByHandler("","正在加载章节...");
					showCancelProgressByHandler(ReadChapterContentSDKTask.this
							.getTaskName(),"加载中");
			}
		Chapter cc;
		String price;
		if (endDownloadPos==10) {
				cc = oc.orderTen();
				price=oc.getOrderTenPrice();
		}else if (endDownloadPos==20) {
			cc = oc.orderTween();
			price=oc.getOrderTwentyPrice();
		}else {
			cc = oc.order();
			price = oc.getPrice();
		}

		if (cc instanceof ContentChapter) {
						// success
						info.status = Status.LOADING;
						mContentsList.set(chapterPos, info);
						if (infoxn!=null) {
							infoxn.status = Status.LOADING;
						}
						/**
						 * 购买成功后，回调购买接口
						 * */
						String url = AppData.getConfig().getUrl(
								Config.URL_READ_BUYBOOK);
						DebugLog.d(TAG, url);
						Map<String, String> map = new HashMap<String, String>();
						map.put("uid", AppData.getUser().getID() + "");
						String uid = AppData.getUser().getID() + "";
						DebugLog.e("uid", uid);
						map.put("imei", AppData.getConfig().getDeviveInfo()
								.getImei()
								+ "");
						String imei = AppData.getConfig().getDeviveInfo()
								.getImei()
								+ "";
						DebugLog.e("imei", imei);
						map.put("aid", mBookItem.bid);
						String aid = mBookItem.bid;
						DebugLog.e("aid", aid);
						// map.put("cid", mBookItem.cid);
						map.put("cid", cid);
						String cid = mBookItem.cid;
						
						
						if (TextUtils.isEmpty(price)) {
							price = 0.12 + "";
						}
						map.put("price", price);
						
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
												hideProgressByHandler();
												try {
													int status = response
															.getInt("status");
													DebugLog.e("status", status
															+ "");
													if (status == StatusCode.OK) {

														DebugLog.d(
																TAG,
																"提交购买信息:"
																		+ response
																				.getString("msg"));
													}

												} catch (JSONException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
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
						if (isHaveMapTable
								&& chapterPos < mXNContentsList.size()) {
							// if (isHaveMapTable ) {
							getXNcontent();
						} else {
							readChapterContentBySyc(chapterPos, action, cc,
									ReadChapterContentSDKTask.this);
						}
					}
				}
				else if (chapter instanceof ChargeChapter && !isClose) {
					final OnlineChapterInfo info = mContentsList
							.get(chapterPos);
					info.status = Status.UNLOAD;
					if (action.index == ChapterAction.LOAD.index) {
						endDownloadPos=0;
						batchDownload(chapterPos);
						return;
					}
					
					if (infoxn!=null) {						
						infoxn.status=Status.UNLOAD;
					}
					ChargeChapter cc = ((ChargeChapter)chapter); 
				    final String chargeInfo = cc.getChargeInfo();
				    isBuy=false;
				    mCallBack.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							DebugLog.e("", chargeInfo);
							hideProgressByHandler();
							Intent intent = new Intent(OnlineReadingActivity.this,
									CMChargeActivity.class);
							startActivityForResult(intent, ResultCode.CM_TO_CONGZHI);
						}
					});
				}
			}

			private void getXNcontent() {
				final Chapter chapter = BookHelper.loadChapter(bid, cid);
				if (isXNChapterLoaded(info.id)) {
					
					if (endDownloadPos !=0 && action.index == ChapterAction.LOAD.index) {
						batchDownload(chapterPos);
					}
					
//					info.status = Status.LOADING;
					ReadChapterTask task = new ReadChapterTask("chLocal读雪凝缓存线程"
							+ chapterPos, AppData.getConfig()
							.getXNOnlineChapterFilePath(mBookItem.onlineID,
									info.id), chapterPos, info,infoxn, action);
					AppData.getClient().getTaskManager().addTask(task);

					DebugLog.d(TAG, "start read chapter from cache file:"
							+ info.id + ", pos:" + chapterPos);

				} else {
					// 下载章节内容
					int a = chapterPos + 1;
					String url = AppData.getConfig().getUrl(
							Config.URL_XNCHATPER)
							+ xnBid + "/order/" + a;
					// String url =
					// AppData.getConfig().getUrl(Config.URL_XNCHATPER)+100+"/order/"+1;
					// String url =
					// AppData.getConfig().getUrl(Config.URL_XNCHATPER);
					DebugLog.d(TAG, url);

					info.status = Status.LOADING;
					mContentsList.set(chapterPos, info);
					getRequestQueue().add(new StringRequest(url,

					new Listener<String>() {

						@Override
						public void onResponse(String response) {
							// TODO Auto-generated method stub
							try {
								JSONObject object = new JSONObject(response);
								response = object.getJSONObject("data")
										.getString("content");
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							if (null == response) {
								DebugLog.d(TAG, "response chapter is null");

								response = "";
								readChapterContentBySyc(chapterPos, action,
										chapter, ReadChapterContentSDKTask.this);
								// info.status = Status.UNLOAD;
								if (action.index < ChapterAction.CACHE_PREV.index) {
									hideProgressByHandler();
								}

								} else {	
									
									if (endDownloadPos !=0 && action.index == ChapterAction.LOAD.index) {
//										batchDownload(chapterPos);
										batchDownMsg=new Message();
										batchDownMsg.what=CallBackMsg.SHEEP_DOWN_LOAD;
										batchDownMsg.arg1=chapterPos;
										mCallBack.sendMessageDelayed(batchDownMsg, 200);
									}
										// 写入本地缓存
										 String filePath =
										 AppData.getConfig().getXNOnlineChapterFilePath(mBookItem.onlineID,
										 info.id);
										info.status = OnlineChapterInfo.Status.LOADED;
//										infoxn.status=Status.LOADED;
//										AppData.getContentHelper(mBookItem.onlineID).updateStatus(
//												info.id, Status.LOADED);						
										AppData.getXNContentHelper(mBookItem.onlineID).updateStatus(
												info.id+1, Status.LOADED);
										byte[] byteBuffer;
										try {
											byteBuffer = response.getBytes("utf-8");
											String strBuffer = AES.decrypt(byteBuffer, "utf-8");
											
											if (strBuffer.contains("&")) {
												strBuffer = strBuffer.replace("&quot;", "\"");
											}
											// 存本地并加密
											OutputFileTask task2 = new OutputFileTask("chWrite_" +
													 mBookItem.onlineID + "_" + info.id, strBuffer, filePath);
													 AppData.getClient().getTaskManager().addTask(task2);
											// 读取并展示出来		 
											ResponseChapterTask task = new ResponseChapterTask(
													"chOnline网络读雪凝线程" + chapterPos,
													strBuffer, chapterPos,
													action);
											
											AppData.getClient()
											.getTaskManager()
											.addTask(task);

											
											
										} catch (UnsupportedEncodingException e) {
											// TODO Auto-generated catch block
											AppData.getXNContentHelper(mBookItem.onlineID).updateStatus(
													info.id, OnlineChapterInfo.Status.UNLOAD);
											e.printStackTrace();
										}

							}

						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							info.status = Status.UNLOAD;
							infoxn.status=Status.UNLOAD;
							if (action.index < ChapterAction.CACHE_PREV.index) {
								hideProgress();
							}

							showToast("无法下载章节， 网速不给力啊亲!", Toast.LENGTH_LONG);
						}
					}));
				}
			}

		}

		// 下载xn的书内容
		private class ResponseChapterTask extends CallBackTask {
			private String response;
			private int chapterPos;
			private ChapterAction action;

			public ResponseChapterTask(String strTaskName, String response,
					int chapterPos, ChapterAction action) {
				super(strTaskName);
				// TODO Auto-generated constructor stub
				this.response = response;
				this.action = action;
				this.chapterPos = chapterPos;
			}

			@Override
			protected void doTask() {
				// TODO Auto-generated method stub
				// OnlineChapterInfo info;
				// if (mXNContentsList.size()-1>chapterPos) {
				// info = mXNContentsList.get(chapterPos);
				// }else {
				// info = mContentsList.get(chapterPos);
				// }
				// OnlineChapterInfo info = mXNContentsList.get(chapterPos);

				DebugLog.d(TAG, Thread.currentThread().toString());

				refreshNewChapter(mContentsList.get(chapterPos).name, response,
						chapterPos, action, ResponseChapterTask.this);

				// 写入本地缓存
				// String filePath =
				// AppData.getConfig().getXNOnlineChapterFilePath(mBookItem.onlineID,
				// info.id);
				// OutputFileTask task = new OutputFileTask("chWrite_" +
				// mBookItem.onlineID + "_" + info.id, response, filePath);
				// AppData.getClient().getTaskManager().addTask(task);

				// info.status = OnlineChapterInfo.Status.LOADED;
				// AppData.getContentHelper(mBookItem.onlineID).updateStatus(
				// info.id, Status.LOADED);

				if (action.index < ChapterAction.CACHE_PREV.index) {
					hideProgress();
				}
				mContentsList.get(chapterPos).status = Status.LOADED;
			}

		}

		private class ReadContentFromSDK extends CallBackTask { // 可取消
			
			private String bid;
			private int mid; // 末尾章节的id
			private String name;
			private Boolean isFirstContent;
			public ReadContentFromSDK(String taskName, String id, int mmid ,Boolean FirstContent) {
				super(taskName);
				bid = id;
				mid = mmid;
				name=taskName;
				isFirstContent=FirstContent;
			}

			@Override
			protected void doTask() {
				int index = 0;
				int pageCap = 0;
				
				int diff = mBookItem.totalCount-mid;
				DebugLog.e("reading 4034", "获取目录开始"+mid+"结束"+mBookItem.totalCount);
				List<OnlineChapterInfo> list=GetDirectoryUtil.getDirectoryList(bid, mid, mBookItem.totalCount);
				mContentsList.addAll(list);
				
				
//				if(diff >= 100){
//					pageCap = 100;
//				}
//				else{
//					pageCap = 10;
//				}
//				index = mid/pageCap+1;
//				while (true) {
//					Directory dir;
//					try {						
//						 dir = BookHelper.loadDir(bid, index, pageCap, true);
//					} catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();
//						break;
//					}
//					
//					if ((index*pageCap > 10000 || dir == null) && !isClose) {
//						hideProgressByHandler();
//						showDialogByHandler("", "连接超时，请重试。", 6000,
//								new MyAlert.DialogOnClickListener() {
//
//									@Override
//									public void doPositive() {
//										// TODO Auto-generated method stub
//										showCancelProgressByHandler(name, "加载目录");
//										AppData.getClient()
//												.getTaskManagerRead()
//												.addTask(
//														ReadContentFromSDK.this);
//									}
//
//									@Override
//									public void doNegative() {
//										// TODO Auto-generated method stub
//
//									}
//								});
//						return;
//					}
//					List<DirectoryItem> list = dir.getList();
//					int startIndex = (index-1)*pageCap;
//					int savedContentSize = mContentsList.size();
//					for (int i = 0; i < list.size(); i++) {
//						if(startIndex+i<=savedContentSize-1){
//							continue;
//						}
//						OnlineChapterInfo info = new OnlineChapterInfo();
//						DirectoryItem item = list.get(i);
//						info.id = item.index;
//						info.cid = item.cid;
//
//						info.name = item.name;						
//						info.type = item.free?OnlineChapterInfo.TYPE_FREE:OnlineChapterInfo.TYPE_NOT_BUY;
//						
//						mContentsList.add(info);
//					}
//					if (isFirstContent) {
//						break;
//					}
//					index++;
//					if ((!dir.isEmpty() && dir.isLastPage()) || index > dir.getPageCount())
//						break;
//				}

				DebugLog.d(TAG, "章节目录获取成功");
				// if (!ReadContentFromSDK.this.mbCancel) {
				if (!isClose) {
					AppData.getContentHelper(mBookItem.onlineID)
							.insertChapterList(mContentsList);
					hideProgressByHandler();
					getRequestChapterCache(mBookItem.lastChapterPos,
							ChapterAction.INIT);
					AppData.getDataHelper().updateXnBook(mBookItem.bid,
							mContentsList.size());
				} else {
					// DebugLog.e("获取目录线程关闭", "不再更新ui");
				}

				// TODO Auto-generated method stub
			}			
		}
		/**
		 * 获取移动目录
		 * */
		private class ReadYDContentFromSDK extends CallBackTask{  // 可取消
			private String bid;
			private int mid; // 末尾章节的id
			private String name;
			public ReadYDContentFromSDK(String taskName,String id,int mmid){
				super(taskName);
				bid = id;
				mid = mmid;
				name = taskName;
			}

			@Override
			protected void doTask() {
//				
				List<OnlineChapterInfo> list=GetDirectoryUtil.getDirectoryList(bid, mid, mBookItem.totalCount);
				mContentsList.addAll(list);
				DebugLog.d(TAG, "章节目录获取成功");
				if (!isClose) {
					AppData.getContentHelper(mBookItem.onlineID).insertChapterList(
							mContentsList);
					DebugLog.e("有映射的移动目录net获取完毕", "开始发消息阅读章节");
					mCallBack
					.sendEmptyMessage(CallBackMsg.CONTENTS_READ_CONTEXT);
					hideProgressByHandler();
				} else {
					
				}
				// TODO Auto-generated method stub
			}			
		}

	}

	// ooooooooo
	private class TxtBookRequest extends BookRequest {
		private final static String TAG = "TxtBook";

		private RandomAccessFile RAFile;
		private MappedByteBuffer mbBuffer;

		private int bufLen;

		private String strCharsetName = "GBK";

		private ArrayList<LocalChapterInfo> contentList = new ArrayList<LocalChapterInfo>();

		public TxtBookRequest() {
		}

		@Override
		public void open() {
			// TODO Auto-generated method stub
			String filePath = mBookItem.path;
			try {
				strCharsetName = FileUtil.getFileEncoding(filePath);
				if (strCharsetName == null) {
					// throw new Exception("无法解析的文本编码格式");
					strCharsetName = "utf-8";
				}

				File book_file = new File(filePath);
				long lLen = book_file.length();
				bufLen = (int) lLen;
				RAFile = new RandomAccessFile(book_file, "r");
				mbBuffer = RAFile.getChannel().map(
						FileChannel.MapMode.READ_ONLY, 0, lLen);

				getRequestContent();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				setResult(ResultCode.OPEN_BOOK_FAILED);
				finish();
			}

		}

		@Override
		public void close() {
			try {
				RAFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 更新最后阅读时间
			int lastPosition = mCacheManager.getCache(PageIndex.current)
					.getCurPagePosition();
			mBookItem.lastPosition = lastPosition;
			mBookItem.lastDate = System.currentTimeMillis();
			mBookItem.chapterTotal = contentList.size();

			AppData.getDataHelper().updateLastReadLocal(mBookItem.id,
					mBookItem.lastChapterPos, mBookItem.lastPosition,
					mBookItem.chapterTotal);

			AppData.getUser().setLastBookID(mBookItem.id);

			// Intent intent = new Intent(OnlineReadingActivity.this,
			// MainActivity.class);
			// intent.putExtra("BookItem", mBookItem);
			//
			// setResult(ResultCode.UPDATE_LASTREAD, intent);

			finish();
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return contentList.size();
		}

		@Override
		public String getChapterName() {
			// TODO Auto-generated method stub
			if (contentList.size() == 0
					|| mBookItem.lastChapterPos >= contentList.size()) {
				return "";
			} else {
				return contentList.get(mBookItem.lastChapterPos).name;
			}

		}

		@Override
		public void showContent() {
			// TODO Auto-generated method stub
			Intent intent = new Intent(OnlineReadingActivity.this,
					OnlineContentsActivity.class);
			intent.putExtra("BookItem", mBookItem);
			intent.putExtra("contentsList", contentList);

			startActivityForResult(intent, ResultCode.JUMP_TO_POSITION);
		}

		private void getRequestContent() {
			String filepath = AppData.getConfig().getLocalContentsFilePath(
					mBookItem.id);
			File f = new File(filepath);
			File b = new File(mBookItem.path);
			if (f.exists() && mBookItem.lastDate == b.lastModified()) {
				DebugLog.d(TAG, "读取本地目录缓存...");
				ReadLocalContentTask task = new ReadLocalContentTask(
						"contentTask" + mBookItem.id, f);
				AppData.getClient().getTaskManagerRead().addTask(task);

			} else {
				// 尝试解析文件目录

				DebugLog.d(TAG, "正在搜索目录信息...");

				CheckContentsTask task = new CheckContentsTask("checkContent",
						mBookItem.path);
				showCancelProgressByHandler(task.getTaskName(),"正在搜索目录信息...");
				// showProgressCancel(task.getTaskName(),"", "正在搜索目录信息...");
				AppData.getClient().getTaskManagerRead().addTask(task);
			}
		}

		public void setContentList(ArrayList<LocalChapterInfo> list) {
			contentList = list;
		}

		@Override
		public void getRequestChapterCache(final int chapterPos,
				final ChapterAction action) {
			// to task

			ReadChapterTask task = new ReadChapterTask("readCh" + chapterPos,
					chapterPos, action);
			if (action.index < ChapterAction.CACHE_PREV.index) {
				showCancelProgressByHandler(task.getTaskName(),"正在搜索目录信息...");
			}

			AppData.getClient().getTaskManagerRead().addTask(task);

		}

		private class ReadLocalContentTask extends Task {

			private File file;

			public ReadLocalContentTask(String strTaskName, File file) {
				super(strTaskName);
				// TODO Auto-generated constructor stub
				this.file = file;
			}

			@Override
			protected void doTask() {
				// TODO Auto-generated method stub
				try {
					FileInputStream fis = new FileInputStream(file);
					ObjectInputStream ois = new ObjectInputStream(fis);

					contentList = (ArrayList<LocalChapterInfo>) ois
							.readObject();

					DebugLog.d(TAG, "txt章节信息读取完成, size:" + contentList.size());

					ois.close();
					fis.close();

					mCallBack
							.sendEmptyMessage(CallBackMsg.READ_CONTENTS_COMPLETED);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					DebugLog.d(TAG, "没有获取到章节信息");
					contentList = null;

					setResult(ResultCode.CONTENT_NOT_FOUND);
					finish();
				}

			}

		}

		private class ReadChapterTask extends CallBackTask {

			private ChapterAction action;
			private int chapterPos;

			public ReadChapterTask(String strTaskName, int chapterPos,
					ChapterAction action) {
				super(strTaskName);
				// TODO Auto-generated constructor stub
				this.action = action;
				this.chapterPos = chapterPos;
			}

			@Override
			protected void doTask() {
				// TODO Auto-generated method stub

				LocalChapterInfo info = contentList.get(chapterPos);
				byte[] byteBuffer = new byte[info.size];
				DebugLog.d(TAG, "read size:" + info.size);

				for (int i = 0; i < info.size; i++) {
					byteBuffer[i] = mbBuffer.get(info.start + i);
				}

				String buffer;
				try {
					buffer = new String(byteBuffer, strCharsetName);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					buffer = new String(byteBuffer);
				}
				buffer = buffer.replaceAll("\r\n", "\n");

				// if (!this.mbCancel) {
				if (!isClose) {

					refreshNewChapter(info.name, buffer, chapterPos, action,
							this);
				} else {

					// DebugLog.e("线程"+this.mTaskName, "已经关闭");
					return;
				}

				if (action.index < ChapterAction.CACHE_PREV.index) {
					hideProgress();
				}

			}

		}

	}

	public void showProgressByHandler(String title, String message) {
		mCallBack.sendMessage(mCallBack.obtainMessage(
				CallBackMsg.SHOW_PROGRESS_MESSAGE, message));
	}
	public void showLoginProgressByHandler(String title, String message) {
		mCallBack.sendMessage(mCallBack.obtainMessage(
				CallBackMsg.SHOW_LOGINPROGRESS, message));
	}

	public void showCancelProgressByHandler(String taskName, String message) {
		Message msg = new Message();
		msg.what = CallBackMsg.SHOW_PROGRESS_CANCEL;
		msg.obj = taskName;
//		mCallBack.sendMessage(msg);
		 mCallBack.sendMessage(mCallBack.obtainMessage(CallBackMsg.SHOW_PROGRESS_CANCEL,
		 message));
	}

	public void hideProgressByHandler() {
		mCallBack.sendMessage(mCallBack
				.obtainMessage(CallBackMsg.HIDE_PROGRESS_MESSAGE));
	}
	public void closeLoginWindowByHandler() {
		mCallBack
		.sendEmptyMessage(CallBackMsg.CLOSE_LOGINWINDOW);
	}
	public void sendDownloadMsgByHandler(Message msgID) {
//		AppData.getClient().sendBackMsg(msgID);	
		mCallBack
		.sendEmptyMessage(CallBackMsg.SHOW_DOWNLOAD_PROGRESS);
		
	}
	public void showToastByHandler(String message, int showtime) {
		mCallBack.sendMessage(mCallBack.obtainMessage(
				CallBackMsg.SHOW_TOAST_MESSAGE, message));
	}

	public void showDialogByHandler(String title, String message, int showtime,
			MyAlert.DialogOnClickListener listener) {
		DialogContent content = new DialogContent(title, message, listener);
		mCallBack.sendMessage(mCallBack.obtainMessage(
				CallBackMsg.SHOW_DIALOGE_MESSAGE, content));
	}

	public class DialogContent {
		String title;
		String message;
		MyAlert.DialogOnClickListener listener;

		public DialogContent(String title, String message,
				MyAlert.DialogOnClickListener listener) {
			this.title = title;
			this.message = message;
			this.listener = listener;
		}
	}

	private PopupWindow dirlogPw;
	private View dirlogView;
	private TimeCount time;
	private void dismissDirlogPop() {
		if (dirlogPw != null && dirlogPw.isShowing()) {
			dirlogPw.dismiss();
			dirlogPw = null;
		}
	}
	/**
	 *  倒计时对话框
	 * */
	protected void showDirLogwidonw(String oderInfo) {

		dirlogView = View.inflate(this, R.layout.boy_djs_dialog, null);
		dismissDirlogPop();
		dirlogPw = null;
		if (dirlogPw == null) {
			dirlogPw = new PopupWindow(dirlogView, LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			dirlogPw.setFocusable(true);
			dirlogPw.setTouchable(true);
			dirlogPw.setOutsideTouchable(true);
			dirlogPw.setBackgroundDrawable(new BitmapDrawable());
		}		
		 ImageView spaceshipImage = (ImageView) dirlogView.findViewById(R.id.boy_img);  
	        TextView tipTextView = (TextView) dirlogView.findViewById(R.id.tipTextView);// 提示文字 
	        TextView dialogTv=(TextView) dirlogView.findViewById(R.id.dialog_djs);
	        time = new TimeCount(30000, 1000,dialogTv);
	        time.start();
//	        time.cancel()
	        Animation operatingAnim = AnimationUtils.loadAnimation(OnlineReadingActivity.this,
					R.anim.tip);
			LinearInterpolator lin = new LinearInterpolator();
			operatingAnim.setInterpolator(lin);
			spaceshipImage.startAnimation(operatingAnim); 
		    ImageView back =(ImageView) dirlogView.findViewById(R.id.boy_dialog_iv); 	
		    back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// info.status = Status.UNLOAD;
				dismissDirlogPop();
			}
		});
		
		tipTextView.setText(oderInfo);

		dirlogPw.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

			}
		});

		dirlogPw.showAtLocation(mPageWidget, Gravity.CENTER, 0, 0);
	}
	private PopupWindow freebackPop;
	private View freebackPw;
	private void dismissFreebackPop() {
		if (freebackPop != null && freebackPop.isShowing()) {
			freebackPop.dismiss();
			freebackPop = null;
		}
	}
	/**
	 *  报错反馈对话框
	 * */
	protected void showFreebackwidonw(String oderInfo) {

		freebackPw = View.inflate(this, R.layout.boy_freeback_dialog, null);
		dismissFreebackPop();
		freebackPop = null;
		if (freebackPop == null) {
			freebackPop = new PopupWindow(freebackPw, LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			freebackPop.setFocusable(true);
			freebackPop.setTouchable(true);
			freebackPop.setOutsideTouchable(true);
			freebackPop.setBackgroundDrawable(new BitmapDrawable());
		}		
		TextView freeBackTv=(TextView) freebackPw.findViewById(R.id.error_freeback_tv);
		freeBackTv.setText(oderInfo);
		freebackPw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissFreebackPop();
			}
		});
		freebackPop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

			}
		});
		
		freebackPop.showAtLocation(mPageWidget, Gravity.CENTER, 0, 0);

		mCallBack.sendMessageDelayed(mCallBack.obtainMessage(CallBackMsg.CLOSE_ERROR_LOGIN), 2000);
	}
	
	private PopupWindow pw;
	private View view;
	private String errorStr1;
	private String errorStr2;
	private String errorStr3;
	private String errorStr4;
	int[]errorArray;
	
	private void dismissPop() {
		if (pw != null && pw.isShowing()) {
			pw.dismiss();
			pw = null;
		}
	}	
	// 报错对话框
	protected void showErrorPopuwidonw(String catherName, String BookName) {
		
		view = View.inflate(this, R.layout.boyi_readbook_buy_pw, null);
		errorStr1=this.getResources().getString(R.string.boyi_readbook_fail1);
		errorStr2=this.getResources().getString(R.string.boyi_readbook_fail2);
		errorStr3=this.getResources().getString(R.string.boyi_readbook_fail3);
		errorStr4=this.getResources().getString(R.string.boyi_readbook_fail4);
		final String []errorTextArray={errorStr1,errorStr2,errorStr3,errorStr4};
		dismissPop();
		hideReadActionWindow();
		errorArray=new int[]{ 0,0,0,0 };
		
		pw = null;
		if (pw == null) {
			pw = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			pw.setFocusable(true);
			pw.setTouchable(true);
			pw.setOutsideTouchable(true);
			pw.setBackgroundDrawable(new BitmapDrawable());
		}
		ImageView backView=(ImageView) view.findViewById(R.id.boy_error_backiv);
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissPop();
			}
		});
		directoryName = null;
		String oderInfo=getResources().getString(R.string.boyi_readbook_fail_title)+"  " +BookName+"  "+catherName+"  出现的错误吗？";
		buyInfo = (TextView) view.findViewById(R.id.buy_info_couther);
		buyInfo.setText(oderInfo);
		
		LinearLayout layoutError1=(LinearLayout) view.findViewById(R.id.error_layout_bar1);
		LinearLayout layoutError2=(LinearLayout) view.findViewById(R.id.error_layout_bar2);
		LinearLayout layoutError3=(LinearLayout) view.findViewById(R.id.error_layout_bar3);
		LinearLayout layoutError4=(LinearLayout) view.findViewById(R.id.error_layout_bar4);
		final ImageView imageError1=(ImageView) view.findViewById(R.id.error_layout_iv1);
		final ImageView imageError2=(ImageView) view.findViewById(R.id.error_layout_iv2);
		final ImageView imageError3=(ImageView) view.findViewById(R.id.error_layout_iv3);
		final ImageView imageError4=(ImageView) view.findViewById(R.id.error_layout_iv4);
		
		layoutError1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (errorArray[0]==0) {
					errorArray[0]=1;
					imageError1.setBackgroundResource(R.drawable.boy_posterror_yes);
				}else {
					errorArray[0]=0;
					imageError1.setBackgroundResource(R.drawable.boy_posterror_no);
				}
			}
		});
		layoutError2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (errorArray[1]==0) {
					errorArray[1]=1;
					imageError2.setBackgroundResource(R.drawable.boy_posterror_yes);
				}else {
					errorArray[1]=0;
					imageError2.setBackgroundResource(R.drawable.boy_posterror_no);
				}
			}
		});
		layoutError3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (errorArray[2]==0) {
					errorArray[2]=1;
					imageError3.setBackgroundResource(R.drawable.boy_posterror_yes);
				}else {
					errorArray[2]=0;
					imageError3.setBackgroundResource(R.drawable.boy_posterror_no);
				}
			}
		});
		layoutError4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (errorArray[3]==0) {
					errorArray[3]=1;
					imageError4.setBackgroundResource(R.drawable.boy_posterror_yes);
				}else {
					errorArray[3]=0;
					imageError4.setBackgroundResource(R.drawable.boy_posterror_no);
				}
			}
		});
		
		final EditText editText=(EditText) view.findViewById(R.id.suggest_content_et);	
		final String freeBackFail=getResources().getString(R.string.boyi_error_freeback_fail);
		Button button = (Button) view.findViewById(R.id.readbook_btn_yes);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String errorText=editText.getText().toString();
				StringBuffer  bufferError=new StringBuffer();
				for (int i = 0; i < errorArray.length; i++) {
					if (errorArray[i]==1) {
						bufferError.append(errorTextArray[i]+",");
					}
				}
				bufferError.append("$$##"+errorText);
				if (bufferError.length()<=4) {
					showFreebackwidonw(freeBackFail);
				}else {
					postDownLoad(mBookItem.bid ,mContentsList.get(lastChapterPos).cid ,bufferError.toString());
				}
				dismissPop();
			}
		});

		Button button2 = (Button) view.findViewById(R.id.readbook_btn_no);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// info.status = Status.UNLOAD;
				dismissPop();
			}
		});

		pw.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

			}
		});

		pw.showAtLocation(mPageWidget, Gravity.CENTER, 0, 0);

	}
	// 批量购买下载接口
	private PopupWindow popWindowBuy = null;
	private void popupBuyWindow(ArrayList<OnlineChapterInfo> mContentsList,int pos) {
		if (pos==mBookItem.totalCount) {
			showToast("无可购买章节", Toast.LENGTH_SHORT);
			return;
		}
		if (endDownloadPos>0) {
			LayoutInflater inflaterDl = LayoutInflater.from(OnlineReadingActivity.this);
	        RelativeLayout layout = (RelativeLayout)inflaterDl.inflate(R.layout.boy_add_to_bookshelf_dialog, null );
	        
	        final AlertDialog alertDialog=new AlertDialog.Builder(OnlineReadingActivity.this).create();
	        alertDialog.show();
	        alertDialog.getWindow().setContentView(layout);
			Button btnpositive= (Button) layout.findViewById(R.id.positive);  //停止
			btnpositive.setText("停止");
			Button btnNegative=(Button) layout.findViewById(R.id.negative);
			Button btnGoStore=(Button) layout.findViewById(R.id.goagain);
			btnGoStore.setVisibility(View.GONE);
			btnNegative.setText("继续");
			TextView textMsg=(TextView) layout.findViewById(R.id.text_msg);
			textMsg.setText("正在缓存中，是否停止？");
			ImageView btnClose=(ImageView) layout.findViewById(R.id.guanbi);
			btnClose.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
				}
			});
			btnNegative.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					alertDialog.dismiss();
				}
			});
			btnpositive.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					endDownloadPos=0;
					alertDialog.dismiss();
				}
			});
			return ;
		}
//		hideReadActionWindow();	
		View v = getBuyView(mContentsList,pos);
		if (v==null) {
			return;
		}
		if (null == popWindowBuy) {
			popWindowBuy = new PopupWindow(v,
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);

			popWindowBuy.setFocusable(true);
			popWindowBuy.setTouchable(true);
			popWindowBuy.setOutsideTouchable(true);
			popWindowBuy.setBackgroundDrawable(new BitmapDrawable());
		}		
		popWindowBuy.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 0);
	}
	private void hideBuyWindow() {
		if (null != popWindowBuy) {
			popWindowBuy.dismiss();
		}
	}

	private View buyView;
	private TextView tvName;
	private View layout10, layout20, layout100, layoutAll,layoutRemain;
    private ImageView ivHide;
    private View getBuyView(final ArrayList<OnlineChapterInfo> mContentsList,int pos) {   
    	final int mPosId=pos;
    	String name="";
    	if (mPosId+1>=mContentsList.size()) {
			showToast("没有可购买章节哦亲", Toast.LENGTH_SHORT);
			return null;
		}
    	if (mPosId<mBookItem.freeCount&& mContentsList.size()>mBookItem.freeCount) {
    		
    			name =mContentsList.get(mBookItem.freeCount).name;
			
		}else {
			name =mContentsList.get(mPosId+1).name;
		}
    	
    	if (null == buyView) {

    		buyView=LayoutInflater.from(OnlineReadingActivity.this).inflate(R.layout.boy_reading_online,null);
//    		buyView.setOnClickListener(new OnClickListener() {
//    			@Override
//    			public void onClick(View v) {
//    				// TODO Auto-generated method stub
//    				hideBuyWindow();
//    			}
//    		});
    		layout10 = buyView.findViewById(R.id.buy_bt1);
    		layout20 = buyView.findViewById(R.id.buy_bt2);
    		layout100 = buyView.findViewById(R.id.buy_bt3);
    		layoutAll = buyView.findViewById(R.id.buy_bt4);
    		ivHide=(ImageView) buyView.findViewById(R.id.poupwindow_hide);

    		ivHide.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				hideBuyWindow();
    			}
    		});

    	}
    	/**
    	 * 	 批量购买，
    	 * */
    	tvName=(TextView) buyView.findViewById(R.id.buy_number);
    	tvName.setText("您将从"+name+" 开始购买。");
//    	final int start = mContentsList.get(pos).id;
    	// 购买后10章
    	layout10.setOnClickListener(new OnClickListener() {

    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			if (mPosId<mBookItem.freeCount) {
					endDownloadPos=mBookItem.freeCount+10;
					totalSize.setText("/"+endDownloadPos+")");
				}else {
					endDownloadPos=mPosId+11;
					totalSize.setText("/"+10+")");
				}
//    			batchSum=endDownloadPos;
    			batchSum=endDownloadPos-mPosId;
    			DebugLog.e("点击下载后10章"+"免费章节"+freeEndPos, "将从"+mPosId+"下载到"+endDownloadPos);
    			bookName.setText("缓存中："+mBookItem.name);
    			progresSize.setVisibility(View.VISIBLE);
    			totalSize.setVisibility(View.VISIBLE);
				progresSize.setText("("+0);
				
    			mCallBack.sendMessage(mCallBack.obtainMessage(CallBackMsg.INIT_SHOW_DOWN));
    			hideBuyWindow();
    			batchDownload(mPosId);
    		}
    	});

    	// 购买后20章
    	layout20.setOnClickListener(new OnClickListener() {

    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			if (mPosId<freeEndPos) {
					endDownloadPos=freeEndPos+20;
				}else {
					endDownloadPos=mPosId+21;
				}
//    			batchSum=endDownloadPos;
    			batchSum=endDownloadPos-mPosId;
    			bookName.setText("缓存中："+mBookItem.name);
    			progresSize.setVisibility(View.VISIBLE);
    			totalSize.setVisibility(View.VISIBLE);
    			progresSize.setText("("+0);
				totalSize.setText("/"+20+")");
    			mCallBack.sendMessage(mCallBack.obtainMessage(CallBackMsg.INIT_SHOW_DOWN));
    			batchDownload(mPosId);
    			hideBuyWindow();
    		}
    	});
    	// 购买后100章
    	layout100.setOnClickListener(new OnClickListener() {

    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			if (mPosId<freeEndPos) {
					endDownloadPos=freeEndPos+100;
				}else {
					endDownloadPos=mPosId+101;
				}
    			batchSum=endDownloadPos-mPosId;
//    			batchSum=endDownloadPos;
    			bookName.setText("缓存中："+mBookItem.name);
    			progresSize.setVisibility(View.VISIBLE);
    			totalSize.setVisibility(View.VISIBLE);
    			progresSize.setText("("+0);
				totalSize.setText("/"+100+")");
    			mCallBack.sendMessage(mCallBack.obtainMessage(CallBackMsg.INIT_SHOW_DOWN));
    			batchDownload(mPosId);
    			hideBuyWindow();
    		}
    	});
    	// 购买后所有
    	layoutAll.setOnClickListener(new OnClickListener() {

    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			batchSum=mContentsList.size()-mPosId;
    			progresSize.setText("("+0);
    			bookName.setText("缓存中："+mBookItem.name);
    			progresSize.setVisibility(View.VISIBLE);
    			totalSize.setVisibility(View.VISIBLE);
				totalSize.setText("/"+mContentsList.size()+")");
    			mCallBack.sendMessage(mCallBack.obtainMessage(CallBackMsg.INIT_SHOW_DOWN));
    			endDownloadPos=mContentsList.size();
    			batchDownload(mPosId);
    			hideBuyWindow();
    		}
    	});
    	//购买剩余所有章节
//    	layoutRemain.setOnClickListener(new OnClickListener() {
//
//    		@Override
//    		public void onClick(View v) {
//    			// TODO Auto-generated method stub
//
//    		}
//    	});
    	return buyView;
    }
	
    private PopupWindow endPageWindow;
	private View endPageView;
	private void dismissEndPageWindow() {
		if (endPageWindow != null && endPageWindow.isShowing()) {
			endPageWindow.dismiss();
			endPageWindow = null;
		}
	}
	/**
	 *  最后一页的推荐页面
	 * */
	private String recommendUrl;
	private NetworkImageView netImageOne;
	private NetworkImageView netImageTwo;
	private NetworkImageView netImageTree;
	private TextView read_recommend_text;
	private LinearLayout bottom_ll;
	private List<BookItem>recomList;
	private int[] images;
	protected void showEndPageWindow(int status) {
		recomList=new ArrayList<BookItem>();
		recommendUrl=AppData.getConfig().getUrl(Config.URL_BOOK_RECOMMAND)+"1?channel="+mChannel+"&limit=9&type=3";
		endPageView = View.inflate(this, R.layout.boyi_end_page, null);
				
		dismissEndPageWindow();
		endPageWindow = null;
		if (endPageWindow == null) {
			endPageWindow = new PopupWindow(endPageView, LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			endPageWindow.setFocusable(true);
			endPageWindow.setTouchable(true);
			endPageWindow.setOutsideTouchable(true);
			endPageWindow.setBackgroundDrawable(new BitmapDrawable());
		}		
		TextView freetishiTv=(TextView) endPageView.findViewById(R.id.read_endpage_text);
		ImageView backView=(ImageView) endPageView.findViewById(R.id.guanbi);
		netImageOne=(NetworkImageView) endPageView.findViewById(R.id.endpage_imageview1);
		netImageTwo=(NetworkImageView) endPageView.findViewById(R.id.endpage_imageview2);
		netImageTree=(NetworkImageView) endPageView.findViewById(R.id.endpage_imageview3);
		read_recommend_text=(TextView) endPageView.findViewById(R.id.read_recommend_text);
		bottom_ll=(LinearLayout) endPageView.findViewById(R.id.bottom_ll);
		Button  goShelf=(Button) endPageView.findViewById(R.id.confirm_exit);
		//看书架
		goShelf.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AppData.setShowRecommend(false);
				AppData.goToShelf(OnlineReadingActivity.this,false);
				finish();

			}
		});
		final Button  goStore=(Button) endPageView.findViewById(R.id.cancel_exit);
		goStore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(OnlineReadingActivity.this, StoreMain.class);
				startActivity(intent);
				finish();
			}
		});
		final TextView book1=(TextView) endPageView.findViewById(R.id.bookname1);
		final TextView click1=(TextView) endPageView.findViewById(R.id.sum1);
		final TextView look1=(TextView) endPageView.findViewById(R.id.look1);
		final TextView look2=(TextView) endPageView.findViewById(R.id.look2);
		final TextView look3=(TextView) endPageView.findViewById(R.id.look3);
		final TextView book2=(TextView) endPageView.findViewById(R.id.bookname2);
		final TextView click2=(TextView) endPageView.findViewById(R.id.sum2);
		final TextView book3=(TextView) endPageView.findViewById(R.id.bookname3);
		final TextView click3=(TextView) endPageView.findViewById(R.id.sum3);
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
//				HttpClient client=new DefaultHttpClient();
//				
//				HttpGet get=new HttpGet(recommendUrl);
//				HttpResponse response;
//					response=client.execute(get);
//					if (response.getStatusLine().getStatusCode()==200) {
//						HttpEntity entity=response.getEntity();
//						
//						String jsonStr=EntityUtils.toString(entity, "utf_8");
//						
//						jsonList=JsonUtil.getJson(jsonStr);
//						DebugLog.e("推荐书籍的size",jsonList.size()+"");
//						recomList.clear();
//						recomList=JsonUtil.getBookItemList(jsonStr);

					jsonList.clear();
					jsonList=AppData.getTuijianList();
//					 设置3个封面
					if (!isClose) {
						 // 向Handler发送消息,更新UI
                     mCallBack.post(new Runnable() {
							
							@Override
							public void run() {
							if(jsonList.size()==0)
					    	{
								read_recommend_text.setVisibility(View.GONE);
								bottom_ll.setVisibility(View.GONE);
					    		showToast("网络不给力啊亲，请检查网络状态",Toast.LENGTH_LONG);
					    		return;
					    	}
			                if (jsonList.size()<6) {
			            		Random random=new Random();	
			            		if (images==null) {
									images=new int[3];            		
								}
			    				images[0]=random.nextInt(jsonList.size()-1);				
			    				if (jsonList.size()>3) {
			    					while (true) {						
			    						images[1]=random.nextInt(jsonList.size()-1);
			    						if (images[1]!=images[0]) {
			    							break;
			    						}
			    					}
			    					while (true) {						
			    						images[2]=random.nextInt(jsonList.size()-1);
			    						if (images[2]!=images[0]&&images[2]!=images[1]) {
			    							break;
			    						}
			    					}
			    				}else {
			    					images[0]=0;
			    					images[1]=1;
			    					images[2]=2;
			    				}
							}else {
								jsonList2=jsonList;
								if (images==null) {
									images=new int[3];            		
								}else {
									for (int i = 0; i < images.length; i++) {	
										DebugLog.e("移除", Integer.valueOf(images[i])+"");
										jsonList2.remove(Integer.valueOf(images[i]));
									}
								}
								Random random=new Random();				
								images[0]=random.nextInt(jsonList2.size()-1);				
								if (jsonList2.size()>3) {
									while (true) {						
										images[1]=random.nextInt(jsonList2.size()-1);
										if (images[1]!=images[0]) {
											break;
										}
									}
									while (true) {						
										images[2]=random.nextInt(jsonList2.size()-1);
										if (images[2]!=images[0]&&images[2]!=images[1]) {
											break;
										}
									}
								}else {
									images[0]=0;
									images[1]=1;
									images[2]=2;
								}
							}
			                
			        		
							String str=jsonList.get(images[0]);
							book1.setText(str.substring(str.lastIndexOf("?")+1,str.lastIndexOf("!")));
							click1.setText(str.substring(str.lastIndexOf("!")+1));
			            	netImageOne.setErrorImageResId(R.drawable.boyi_ic_cover_default);
			            	netImageOne.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
			            	netImageOne.setImageUrl(addUrl(str.substring(0,str.lastIndexOf("/"))), imageLoader);  
			    	
			            	String strCenter=jsonList.get(images[1]);
			            	book2.setText(strCenter.substring(strCenter.lastIndexOf("?")+1,strCenter.lastIndexOf("!")));
							click2.setText(strCenter.substring(strCenter.lastIndexOf("!")+1));
			            	netImageTwo.setErrorImageResId(R.drawable.boyi_ic_cover_default);
			            	netImageTwo.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
			            	netImageTwo.setImageUrl(addUrl(strCenter.substring(0,strCenter.lastIndexOf("/"))), imageLoader);  
			            	
			            	String strRight=jsonList.get(images[2]);
			            	book3.setText(strRight.substring(strRight.lastIndexOf("?")+1,strRight.lastIndexOf("!")));
							click3.setText(strRight.substring(strRight.lastIndexOf("!")+1));
			            	netImageTree.setErrorImageResId(R.drawable.boyi_ic_cover_default);
			            	netImageTree.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
			            	netImageTree.setImageUrl(addUrl(strRight.substring(0,strRight.lastIndexOf("/"))), imageLoader);
			            	look1.setText("在看");
			        		look2.setText("在看");
			        		look3.setText("在看");
			        		}
						});
					}
				
						
						

					
					
						
			}
		});
		
		thread.start();
		
		
//		recomendBook recomenTask=new recomendBook("endpageTask",netImageOne,netImageTwo,netImageTree);
//		AppData.getClient().getTaskManagerRead().addTask(recomenTask);
		
		if (status==1) {			
			freetishiTv.setText("此书已完结，没有下一章了。");
		}else {
			freetishiTv.setText(getResources().getString(R.string.boyi_readbook_endpage));
		}
		DebugLog.e(TAG,"推荐的书籍数__"+jsonList.size());
		
		
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissEndPageWindow();
			}
		});
		endPageWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

			}
		});
		
		endPageWindow.showAtLocation(mPageWidget, Gravity.CENTER, 0, 0);
	}
	
	// 设置中下拉菜单：
		private PopupWindow  mPopupWindowMore = null;
		
		private void showPopupMore(View view) {
			
					if(null == mPopupWindowMore) {
						mPopupWindowMore = new PopupWindow(getMoreView(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						mPopupWindowMore.setFocusable(true);
						mPopupWindowMore.setTouchable(true);
						mPopupWindowMore.setOutsideTouchable(true);
						mPopupWindowMore.setBackgroundDrawable(new BitmapDrawable());  // 加上该语句后， 可在popupWindow外点击

					}
					int[] location = new int[2];  
					setMenu.getLocationOnScreen(location);
//					mPopupWindowMore.showAtLocation(mPageWidget, Gravity.NO_GRAVITY, location[0]-moreView.getWidth(), location[1]+setMenu.getHeight());
					mPopupWindowMore.showAsDropDown(setMenu);
					
			}
				
		private View moreView = null;
		private View getMoreView() {
				if(null == moreView) {
						moreView = LayoutInflater.from(this).inflate(R.layout.bookshelf_menu2, null);

						moreView.setOnTouchListener(new OnTouchListener() {
							
							@Override
							public boolean onTouch(View v, MotionEvent event) {
								if (mPopupWindowMore != null && mPopupWindowMore.isShowing()) {
									mPopupWindowMore.dismiss();
								}

								return false;
							}
						});

//						 1.个人中心
						LinearLayout userCenter=(LinearLayout) moreView.findViewById(R.id.enter_user_center);
						
						userCenter.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								
							}
						});
						// 2.逛书城
						LinearLayout tvBrowserBook = (LinearLayout)moreView.findViewById(R.id.enter_bookstore);
								tvBrowserBook.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											Intent intent=new Intent(OnlineReadingActivity.this, StoreMain.class);
											startActivity(intent);
//											finish();
											
										}
										});		
									
						// 3.看书架
								LinearLayout tvOperatorBook = (LinearLayout)moreView.findViewById(R.id.enter_bookshelf);
									tvOperatorBook.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											AppData.setShowRecommend(false);
											AppData.goToShelf(OnlineReadingActivity.this,false);
											
											}
									});

						}
					return moreView;
					}
	
	/**
	 * 请求映射表
	 * */
	private static String mOperator = "移动";
	private static String mChannel;
	private static int mOper;

	public void getMapTable() {
		showCancelProgressByHandler("", "加载目录中");
		mChannel=AppData.readMetaDataFromService(this, "channel_num");
		mOper = AppData.getConfig().getDeviveInfo().getOperator(this);
		if (TextUtils.isEmpty(mChannel)) {
			mChannel = "default";
		}

		switch (mOper) {
		case DeviceInfo.OPERATOR_CM:
			mOperator = "移动";
			break;
		case DeviceInfo.OPERATOR_CU:
			mOperator = "联通";
			break;
		case DeviceInfo.OPERATOR_TC:
			mOperator = "电信";
			break;

		default:

			break;
		}

		String url = AppData.getConfig().getUrl(Config.URL_XN_MAPTABLE);
		Map<String, String> map = new HashMap<String, String>();
		map.put("qdid", "" + mChannel);
		AppData.getRequestQueue().add(
				new JsonObjectPostRequest(url,
						new Listener<JSONObject>() {

							@Override
							public void onResponse(
									JSONObject response) {
								// TODO Auto-generated method
								try {
									int status = response
											.getInt("status");
									if (status == StatusCode.OK) {
										DebugLog.e("映射json", response.toString());
										JSONObject array = response.getJSONObject("data");
										if (array == null) {
											xnBid = "";
											mRequest.open();
											return;
										} else {
											JSONObject objChannel = array
													.getJSONObject(mChannel);
											
											if (mOperator == null || mOperator.equals("")) {
												xnBid = "";
												mRequest.open();
												return;
											}
											JSONObject objOperator = objChannel
													.getJSONObject("operator");

											String mapTable = objOperator.getJSONObject(mOperator)
													.getString("mappingid");
											mapTable = mapTable.substring(1,
													mapTable.length() - 1);
											String[] array1 = mapTable.split(",");
											SharedPreferences sp = getSharedPreferences("bidMapTable",
															Application.MODE_PRIVATE);
											List<String> keys = new ArrayList<String>();
											List<String> values = new ArrayList<String>();
											for (int i = 0; i < array1.length; i++) {
												String[] array2 = array1[i].split(":");
												String key = array2[0].substring(1,
														array2[0].length() - 1);
												String value = array2[1].substring(1,
														array2[1].length() - 1);
												
												keys.add(key);
												values.add(value);
												DebugLog.e("键"+key, "值"+value);
											}
											DebugLog.e("判断该书是否有映射", mBookItem.bid);
											SharedPreferences.Editor editor = sp.edit();
											if (keys.contains(mBookItem.bid)) {// 映射表中有当前书
												
												String mValue = sp.getString(mBookItem.bid,
														"");
												int a = keys.indexOf(mBookItem.bid);
												xnBid = values.get(a);
												DebugLog.e("存在映射", xnBid);
												DebugLog.e("请求到的映射："+mBookItem.bid, "-------"+xnBid+"老映射是："+mValue);
												if (!xnBid.equals(mValue)) {
													AppData.getXNContentHelper(mBookItem.onlineID).deleteQBBook(mBookItem.bid);
													AppData.closeXNDBContent(Integer.parseInt(mBookItem.bid));
													File f = new File(
															AppData.getConfig()
																	.getXNContentName(
																			Integer.parseInt(mBookItem.bid)));
													if (f.exists()) {
														deleteDir(f);
													}
													
													if (AppData.getDataHelper().foundBookBid(mBookItem.bid)) {		
														AppData.getDataHelper().deleteQBBook(mBookItem.bid);
														AppData.getBookDetailItem(mBookItem.bid, OnlineReadingActivity.this);
													}
													editor.putString(mBookItem.bid, xnBid);
													editor.commit();
												}

											} else {
												String mValue = sp.getString(mBookItem.bid,
														"");
												if (!TextUtils.isEmpty(mValue)) {
													if (AppData.getDataHelper().foundBookBid(mBookItem.bid)) {		
														AppData.getDataHelper().deleteQBBook(mBookItem.bid);
														AppData.getBookDetailItem(mBookItem.bid, OnlineReadingActivity.this);
													}
													
													File f = new File(
															AppData.getConfig()
															.getXNContentName(
																	Integer.parseInt(mBookItem.bid))); // 缓存文件目录是根据移动id键的
													AppData.closeXNDBContent(Integer.parseInt(mBookItem.bid));
													if (f.exists()) {
														deleteDir(f);
													}
													editor.putString(mBookItem.bid, "");
													editor.commit();
												}
												xnBid = "";
												
											}
											
//											SharedPreferences.Editor editor = sp.edit();
//											editor.clear();
//											for (int i = 0; i < keys.size(); i++) {
//												DebugLog.e("键是--" + keys.get(i), "值是--"
//														+ values.get(i));
//												editor.putString(keys.get(i), values.get(i));
//												editor.commit();
//											}
											mRequest.open();
										}
									}

								} catch (JSONException e) {
									// TODO Auto-generated catch
									e.printStackTrace();
									xnBid = "";
									mRequest.open();
								}
							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(
									VolleyError error) {
								// TODO Auto-generated method
								DebugLog.e("onlinereading", "请求映射表失败");
							}
						}, map));
		
//		getRequestQueue().add(new StringRequest(url, new Listener<String>() {
//			@Override
//			public void onResponse(String response) {
//				// TODO Auto-generated method stub
//				try {
//					JSONObject responseJson = new JSONObject(response);
//					int status = responseJson.getInt("status");
//					if (status == StatusCode.OK) {
//						JSONObject array = responseJson.getJSONObject("data");
//						if (array == null) {
//							xnBid = "";
//							mRequest.open();
//							return;
//						} else {
//							JSONObject objChannel = array
//									.getJSONObject(mChannel);
//
//							if (mOperator == null || mOperator.equals("")) {
//					
//							} else {
//								JSONObject obj = objChannel
//										.getJSONObject("operator");
//								if (!obj.has(mOperator)) {
//									xnBid = "";
//									mRequest.open();
//									return;
//								}
//
//								String mapTable = obj.getJSONObject(mOperator)
//										.getString("mappingid");
//								mapTable = mapTable.substring(1,
//										mapTable.length() - 1);
//								String[] array1 = mapTable.split(",");
//								SharedPreferences sp = getSharedPreferences(
//										"bidMapTable", Application.MODE_PRIVATE);
//								List<String> keys = new ArrayList<String>();
//								List<String> values = new ArrayList<String>();
//
//								for (int i = 0; i < array1.length; i++) {
//									String[] array2 = array1[i].split(":");
//
//									String key = array2[0].substring(1,
//											array2[0].length() - 1);
//									String value = array2[1].substring(1,
//											array2[1].length() - 1);
//									keys.add(key);
//									values.add(value);
//								}
//								if (keys.contains(mBookItem.bid)) {// 映射表中有当前书
//									String mValue = sp.getString(mBookItem.bid,
//											"");
//									int a = keys.indexOf(mBookItem.bid);
//									xnBid = values.get(a);
//									if (!xnBid.equals(mValue)) {
//										AppData.getXNContentHelper(mBookItem.onlineID).deleteQBBook(mBookItem.bid);
//										AppData.closeXNDBContent(Integer.parseInt(mBookItem.bid));
//										File f = new File(
//												AppData.getConfig()
//														.getXNContentName(
//																Integer.parseInt(mBookItem.bid)));
//										if (f.exists()) {
//											deleteDir(f);
//										}
//									}
//
//								} else {
//									File f = new File(
//											AppData.getConfig()
//													.getXNContentName(
//															Integer.parseInt(mBookItem.bid))); // 缓存文件目录是根据移动id键的
//									AppData.closeXNDBContent(Integer.parseInt(mBookItem.bid));
//									if (f.exists()) {
//										deleteDir(f);
//									}
//									xnBid = "";
//								}
//								//
//								SharedPreferences.Editor editor = sp.edit();
//								editor.clear();
//								for (int i = 0; i < array1.length; i++) {
//									String[] array2 = array1[i].split(":");
//									String key = array2[0].substring(1,
//											array2[0].length() - 1);
//									String value = array2[1].substring(1,
//											array2[1].length() - 1);
//									editor.putString(key, value);
//									editor.commit();
//								}
//								mRequest.open();
//							}
//						}
//					}
//
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					
//					xnBid = "";
//					mRequest.open();
//					// e.printStackTrace();
//				}
//
//			}
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				// TODO Auto-generated method stub
//				xnBid = "";
//				mRequest.open();
//			}
//		}));
	}

	private static boolean deleteDir(File dir) {
		
		if (dir.isDirectory()) {
			String[] children = dir.list();

			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	
	
	class TimeCount extends CountDownTimer {
		private TextView pAuthcodeTv;
		public TimeCount(long millisInFuture, long countDownInterval,TextView Tv) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
			this.pAuthcodeTv=Tv;
		}

		@SuppressLint({ "NewApi", "ResourceAsColor" })
		@Override
		public void onFinish() {// 计时完毕时触发
			dismissDirlogPop();
//			AppData.getClient().getTaskManagerRead().delTask(buyBook.getTaskName());
			
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			pAuthcodeTv.setClickable(false);
			pAuthcodeTv.setText(millisUntilFinished / 1000 + "秒");
		}
	}
	/** 
     * 		上传报错信息
     */  
	public String type="1";	
	public  void postDownLoad(String bid ,String cid ,String advice){
	String url = AppData.getConfig().getUrl(Config.URL_POST_CONTENT_ERROR);
	final String freeBackSucess=getResources().getString(R.string.boyi_error_freeback_sucess);

	Map<String, String> map = new HashMap<String, String>();
	map.put("uid", AppData.getUser().getID() + "");
	map.put("type", type);
	map.put("advice", advice);
	map.put("aid", bid);
	map.put("cid", cid);	
	getRequestQueue().add(
			new JsonObjectPostRequest(url,
					new Listener<JSONObject>() {

						@Override
						public void onResponse(
								JSONObject response) {
							// TODO Auto-generated method
							try {
								int status = response
										.getInt("status");
								DebugLog.e("status", status
										+ "");
								if (status == StatusCode.OK) {
									DebugLog.e("上传状态======", "返回100，上传成功开始下一章");
									if (! isClose) {
										showFreebackwidonw(freeBackSucess);
										
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch
								e.printStackTrace();
							}
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(
								VolleyError error) {
							// TODO Auto-generated method
							if (! isClose) {
								showFreebackwidonw("上传失败");
							}
						}
					}, map));	
	}
	private List<String> jsonList,jsonList2;
	
	public class recomendBook extends CallBackTask{
		private NetworkImageView one,two,tree;
		public recomendBook(String strTaskName,NetworkImageView one,NetworkImageView two,NetworkImageView tree) {
			super(strTaskName);
			// TODO Auto-generated constructor stub
			this.one=one;
			this.two=two;
			this.tree=tree;
		}
		public recomendBook(String strTaskName) {
			super(strTaskName);
			// TODO Auto-generated constructor stub
		
		}
		
		@Override
		protected void doTask() {
			// TODO Auto-generated method stub
			HttpClient client=new DefaultHttpClient();
			
			HttpGet get=new HttpGet(recommendUrl);
			HttpResponse response;
			try {
				response=client.execute(get);
				if (response.getStatusLine().getStatusCode()==200) {
					HttpEntity entity=response.getEntity();
					
					String jsonStr=EntityUtils.toString(entity, "utf_8");
					jsonList=JsonUtil.getJson(jsonStr);
					mCallBack.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							// 更新三张图片推荐
							int []images=new int[3];
							Random random=new Random(10);
							images[0]=random.nextInt(9);
							while (true) {						
								images[1]=random.nextInt(9);
								if (images[1]!=images[0]) {
									break;
								}
							}
							while (true) {						
								images[2]=random.nextInt(9);
								if (images[2]!=images[0]&&images[2]!=images[1]) {
									break;
								}
							}
							
							
			            	String str=jsonList.get(images[0]);
//			            	final String bid1=str.substring(str.lastIndexOf("/")+1);  
			            	one.setErrorImageResId(R.drawable.boyi_ic_cover_default);
			            	one.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
			            	one.setImageUrl(addUrl(str.substring(0,str.lastIndexOf("/"))), imageLoader);  
			    	
			            	String strCenter=jsonList.get(images[1]);
			            	final String bid2=strCenter.substring(strCenter.lastIndexOf("/")+1);   
			            	two.setErrorImageResId(R.drawable.boyi_ic_cover_default);
			            	two.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
			            	two.setImageUrl(addUrl(strCenter.substring(0,strCenter.lastIndexOf("/"))), imageLoader);  

			            	String strRight=jsonList.get(images[2]);
			            	final String bid3=strRight.substring(strRight.lastIndexOf("/")+1); 
			            	tree.setErrorImageResId(R.drawable.boyi_ic_cover_default);
			            	tree.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
			            	tree.setImageUrl(addUrl(strRight.substring(0,strRight.lastIndexOf("/"))), imageLoader); 
						}
					});

				}	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
//			 detail = BookHelper.loadDetail(bid);				 
//			 BookItem item = new BookItem();
//				if(detail !=null){						
//				item.bid = detail.getBid();
//				item.cid=detail.getFirstCid();
//				item.name=detail.getName();
//				item.author = detail.getAuthor();
//				item.status = detail.getStatus();
//				item.wordNum=detail.getWord();
//				item.shortDesc=detail.getIntroduction();
//				item.longDesc=detail.getDesc();
//				item.littleCoverUrl=detail.getCoverUrl();
//				item.bigCoverUrl=detail.getBigCoverUrl();
//				item.classFication=detail.getClassification();
//				item.clickStr=detail.getClick();
//				item.freeCount=detail.getFreeChapterCount();
//				item.totalCount=detail.getTotalChapterCount();
//				detail.getBid(); // 书id
//				if (! isColse) {
//				Message msg = new Message();     
//				msg.what = UPDATA_INFO;
////                msg.obj=list;             
//				mHandler.sendMessage(msg); // 向Handler发送消息,更新UI
//				}
////				AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_BOOKSHELF);  // 让书架更新书架
//			}else {
//				return;
//			}

		}
	}
  	//取图片地址
  private String  addUrl(String str ){
		
		String   xName=str.substring(str.lastIndexOf("/")+1);   
//		System.out.println("中文部分"+xName);
		String   filename=str.substring(0,str.length()-xName.length());
//		System.out.println("英文部分"+filename);
		try {
			URLEncoder.encode(str.substring(str.lastIndexOf("/")+1), "utf_8");
			String imageUrl=filename+URLEncoder.encode(str.substring(str.lastIndexOf("/")+1), "utf_8");
			return imageUrl;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
  // 取随机数
  	private int giveInt(Random random){
  		
  		return random.nextInt(9);
  	}
 
    
}
