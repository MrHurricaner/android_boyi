package com.boyiqove.ui.bookshelf;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.telephony.TelephonyManager;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.config.Config;
import com.boyiqove.config.DeviceInfo;
import com.boyiqove.db.DBDataHelper;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.Notice;
import com.boyiqove.entity.PageID;
import com.boyiqove.library.volley.RequestQueue;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.toolbox.ImageLoader;
import com.boyiqove.library.volley.toolbox.ImageLoader.ImageCache;
import com.boyiqove.library.volley.toolbox.JsonObjectRequest;
import com.boyiqove.library.volley.toolbox.ListImageListener;
import com.boyiqove.library.volley.toolbox.NetworkImageView;
import com.boyiqove.library.volley.toolbox.StringRequest;
import com.boyiqove.protocol.JsonObjectPostRequest;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.task.CallBackMsg;
import com.boyiqove.task.CallBackTask;
import com.boyiqove.ui.bookqove.ImageCacheManager;
import com.boyiqove.ui.bookstore.BookDetail;
import com.boyiqove.ui.bookstore.ShowNotificationInterface;
import com.boyiqove.ui.bookstore.StoreMain;


import com.boyiqove.ui.storeutil.JsonUtil;

import com.boyiqove.ui.storeutil.RefreshableView;
import com.boyiqove.ui.storeutil.RefreshableView.PullToRefreshListener;
import com.boyiqove.ui.storeutil.ShelfGridView;
import com.boyiqove.util.CommonUtil;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.FileUtil;
import com.boyiqove.util.GetBookDetailUtil;
import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.chapter.Chapter;
import com.bytetech1.sdk.chapter.ContentChapter;
import com.bytetech1.sdk.chapter.LoginChapter;
import com.bytetech1.sdk.chapter.OrderChapter;
import com.bytetech1.sdk.data.BookUpdateInfo;
import com.bytetech1.sdk.data.Detail;
import com.bytetech1.sdk.util.Http;
public class BookshelfUtil {

	private final static String TAG = "BookshelfUtil";
	private final static int DELETE_UNSELECTE = -1;
	private final static int DELETE_SELECTEED = 1;
	private static final int ACTION_BAR_ID_SHOP = 1;
	private View mRootView;

	private TextView mNotifyTv;
	// private ImageView mMoreIv; 原始下拉菜单
	// private PullToRefreshListView pulllistview;
	private ShelfGridView mGridView;
	private RefreshableView refreshableView;
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
	private final static int UPDATA_IMAGE=1;
	private RelativeLayout btn_bookStore;
	private TextView top_title;
	private ImageView btn_back;
	private  String channel ;
	private boolean selected=false;
    private static UpdateInterface update;
	private static ShowNotificationInterface sni;
	private static EnterGiftActivityInterface egi;
	private int[] images;
	public static void setShowNotificationInterface(ShowNotificationInterface snfi)
	{
		sni=snfi;
	}
	public static void setUpdateinterface(UpdateInterface uInterface)
	{
		update=uInterface;
	}
	public static void setEnterGiftinterface(EnterGiftActivityInterface egai)
	{
		egi=egai;
	}
	public WindowManagerLogin managerLogin=new WindowManagerLogin();
	
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
				break;
//			case CallBackMsg.INIT_SHOW_LOGIN:				
//				managerLogin.showPopupWindow(mContext);
//				handler.sendMessageDelayed(handler.obtainMessage(CallBackMsg.CLOSE_SHOW_LOGIN), 5000);
//				break;
//			case CallBackMsg.CLOSE_SHOW_LOGIN:
//				managerLogin.hidePopupWindow();
//				break;
			case CallBackMsg.SHOW_TOAST_MESSAGE:
				showToast((String)msg.obj);
				break;
			case CallBackMsg.UPDATE_BOOKSHELF: // 检查书籍更新后发来的消息
			{
				// 请求了跟新书架
				mBookList = AppData.getDataHelper().getKbShelfList();
				
				Collections.sort(mBookList,new Comparator<BookItem>() {

					@Override
					public int compare(BookItem item1, BookItem item2) {
						if(item1.timeStamp<item2.timeStamp)
						{
							return 1;
						}
						return -1;
					}
					
				});
				
				mGridAdapter.notifyDataSetChanged();
				if (mEmptyView.getVisibility() == View.VISIBLE
						&& mBookList.size() > 0) {
					mEmptyView.setVisibility(View.GONE);

					showShelfView(); // 更新书籍 后初始化书架
				}
//				mGridAdapter.notifyDataSetChanged();
				// pulllistview.onRefreshComplete();

			}
				break;
			case 1:
				//随机三张
				if(jsonList.size()==0)
		    	{
					delete_recommend_ll.setVisibility(View.GONE);
					delete_recommend_text.setVisibility(View.GONE);
		    		showToast("网络不给力啊亲，请检查网络状态");
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
				textName1.setText(str.substring(str.lastIndexOf("?")+1,str.lastIndexOf("!")));
				textLook1.setText(str.substring(str.lastIndexOf("!")+1));
            	imageView1.setErrorImageResId(R.drawable.boyi_ic_cover_default);
            	imageView1.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
            	imageView1.setImageUrl(addUrl(str.substring(0,str.lastIndexOf("/"))), imageLoader);  
    	
            	String strCenter=jsonList.get(images[1]);
            	textName2.setText(strCenter.substring(strCenter.lastIndexOf("?")+1,strCenter.lastIndexOf("!")));
				textLook2.setText(strCenter.substring(strCenter.lastIndexOf("!")+1));
            	imageView2.setErrorImageResId(R.drawable.boyi_ic_cover_default);
            	imageView2.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
            	imageView2.setImageUrl(addUrl(strCenter.substring(0,strCenter.lastIndexOf("/"))), imageLoader);  
            	
            	String strRight=jsonList.get(images[2]);
            	textName3.setText(strRight.substring(strRight.lastIndexOf("?")+1,strRight.lastIndexOf("!")));
				textLook3.setText(strRight.substring(strRight.lastIndexOf("!")+1));
            	imageView3.setErrorImageResId(R.drawable.boyi_ic_cover_default);
            	imageView3.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
            	imageView3.setImageUrl(addUrl(strRight.substring(0,strRight.lastIndexOf("/"))), imageLoader);
            	look1.setText("在看");
        		look2.setText("在看");
        		look3.setText("在看");
				break;
			default:
				DebugLog.d(TAG, "unkown msg:" + Integer.toHexString(msg.what));
				break;
			}

		};
	};
	private Context mContext;
	private LayoutInflater mInflater;
	private ViewGroup mContainer;
    LinearLayout mFloatLayout;  
    WindowManager.LayoutParams wmParams;  
    WindowManager mWindowManager;
    
	public BookshelfUtil(final Context mContext,LayoutInflater inflater, ViewGroup container) {
		super();
		this.mContext = mContext;
		this.mInflater=inflater;
		this.mContainer=container;
//		loginTask task=new loginTask("initLoginTask");
//		AppData.getClient().getTaskManagerRead().addTask(task);

//		int operatorType = DeviceInfo
//				.getOperator(mContext);
//		int loginType = -1;				
//		if (operatorType == DeviceInfo.OPERATOR_CM) {
//			loginType = LoginChapter.TYPE_CM;
//		} else if (operatorType == DeviceInfo.OPERATOR_CU) {
//			loginType = LoginChapter.TYPE_CU;
//		}
//		else if (operatorType == DeviceInfo.OPERATOR_TC) {
//			loginType = LoginChapter.TYPE_TELCOM;
//		}
//		LoginHelper.getInstatnce().startLogin(mContext, "400000091", "400051394",loginType, new LoginHelper.LoginCallback() {
//			
//			@Override
//			public void loginSuccess(Chapter chapter) {
//				// TODO Auto-generated method stub
//				Http.save();
//				handler.sendMessage(handler.obtainMessage(CallBackMsg.SHOW_TOAST_MESSAGE, mContext.getResources().getText(R.string.boyi_readbook_login_success,"login success")));
//			}
//			
//			@Override
//			public void loginStart() {
//				// TODO Auto-generated method stub
//				handler.sendMessage(handler.obtainMessage(CallBackMsg.INIT_SHOW_LOGIN));
//			}
//			
//			@Override
//			public void loginFail() {
//				// TODO Auto-generated method stub
//				handler.sendMessage(handler.obtainMessage(CallBackMsg.SHOW_TOAST_MESSAGE, mContext.getResources().getText(R.string.boyi_readbook_login_fail,"login fail")));
//			}
//			
//			@Override
//			public void LoginError(int type, String error) {
//				// TODO Auto-generated method stub
//				handler.sendMessage(handler.obtainMessage(CallBackMsg.SHOW_TOAST_MESSAGE, mContext.getResources().getText(R.string.boyi_readbook_login_fail,"login fail")));
//			}
//		});
		
//		AppData.getClient().getTaskManagerRead().delTask(ReadChapterContentSDKTask.this.getTaskName());
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
////				Chapter chapter = BookHelper.loadChapter("382312165", "393150223");
//				Chapter chapter = BookHelper.loadChapter("382312165", "382445549");
//				if (chapter instanceof LoginChapter) {
//					TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
//					int simState = telMgr.getSimState();
//					if (simState == TelephonyManager.SIM_STATE_ABSENT) {
//						return;
//					}
//					createFloatView();
////					Timer timer = new Timer();  
////			        timer.schedule(new TimerTask() {  
////			            public void run() {  
////			            	AppData.getClient().sendCallBackMsg(CallBackMsg.CLOSE_SHOW_LOGIN);
////			            }  
////			        }, 4000);
//	                try {
//						Thread.sleep(2000);
//					} catch (InterruptedException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//	                
//					LoginChapter lc = ((LoginChapter) chapter);
//					// 判断运营商
//					int operatorType = DeviceInfo
//							.getOperator(mContext);
//					int loginType = -1;				
//						if (operatorType == DeviceInfo.OPERATOR_CM) {
//							loginType = LoginChapter.TYPE_CM;
//						} else if (operatorType == DeviceInfo.OPERATOR_CU) {
//							loginType = LoginChapter.TYPE_CU;
//						}
//						else if (operatorType == DeviceInfo.OPERATOR_TC) {
//							loginType = LoginChapter.TYPE_TELCOM;
//						}
//
//						String number = lc.getLoginViaSmsNumber(loginType);
//						String content = lc.getLoginViaSmsContent(loginType);
//						
//						DeviceInfo.sendTextSms(mContext, number,
//								content);					
//						long loginTime = 30000;
//						long loginStartTime = System.currentTimeMillis();
//						long lTime = System.currentTimeMillis();
//						boolean isFirst = true;
//						Chapter c =null;						
//						while (lTime - loginStartTime <= loginTime) {
//							try {
//								if (isFirst) {
//									Thread.sleep(8000);
//									isFirst = false;
//								} else {
//									Thread.sleep(5000);
//								}
//							} catch (Exception e) {
//								// TODO: handle exception
//								return;
//							}							
//							try {
//								c = lc.loginViaSms(loginType);	
//								DebugLog.e("Http", c.toString());
//							} catch (Exception e) {
//								// TODO: handle exception
//								DebugLog.e("注册崩溃", "提示重试");
////								AppData.getClient().sendCallBackMsg(CallBackMsg.CLOSE_SHOW_LOGIN);
//								continue;
//							}
//							if (c == null) {
//								
//							} else if (c instanceof LoginChapter) {
//								// login error
//								continue;
//
//							} else if (c instanceof OrderChapter) {
//								// success
////								AppData.getClient().sendCallBackMsg(CallBackMsg.CLOSE_SHOW_LOGIN);
////								Chapter order = ((OrderChapter) chapter).order();
//								
//								break;
//								
//							}else if (c instanceof ContentChapter) {
//								// success
////								AppData.getClient().sendCallBackMsg(CallBackMsg.CLOSE_SHOW_LOGIN); 
//								break;
//							}
//							lTime = System.currentTimeMillis();
//						}
//				}
//			}
//		});
		
		
	}
	

	


	// 首次登录的
	
	public class loginTask extends CallBackTask{

		public loginTask(String strTaskName) {
			super(strTaskName);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void doTask() {
			// TODO Auto-generated method stub
			Chapter chapter = BookHelper.loadChapter("400000091", "400051394");
			if (chapter instanceof LoginChapter) {
				TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
				int simState = telMgr.getSimState();
				if (simState == TelephonyManager.SIM_STATE_ABSENT) {
					return;
				}
//				handler.sendMessage(handler.obtainMessage(CallBackMsg.CLOSE_SHOW_LOGIN));
				handler.sendMessage(handler.obtainMessage(CallBackMsg.INIT_SHOW_LOGIN));

			
                try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
				LoginChapter lc = ((LoginChapter) chapter);
				// 判断运营商
				int operatorType = DeviceInfo
						.getOperator(mContext);
				int loginType = -1;				
					if (operatorType == DeviceInfo.OPERATOR_CM) {
						loginType = LoginChapter.TYPE_CM;
					} else if (operatorType == DeviceInfo.OPERATOR_CU) {
						loginType = LoginChapter.TYPE_CU;
					}
					else if (operatorType == DeviceInfo.OPERATOR_TC) {
						loginType = LoginChapter.TYPE_TELCOM;
					}

					String number = lc.getLoginViaSmsNumber(loginType);
					String content = lc.getLoginViaSmsContent(loginType);
					
					DeviceInfo.sendTextSms(mContext, number,
							content);					
					long loginTime = 30000;
					long loginStartTime = System.currentTimeMillis();
					long lTime = System.currentTimeMillis();
					boolean isFirst = true;
					Chapter c =null;						
					while (lTime - loginStartTime <= loginTime) {
						try {
							if (isFirst) {
								Thread.sleep(8000);
								isFirst = false;
							} else {
								Thread.sleep(5000);
							}
						} catch (Exception e) {
							// TODO: handle exception
							return;
						}							
						try {
							c = lc.loginViaSms(loginType);	
							DebugLog.e("Http", c.toString());
						} catch (Exception e) {
							// TODO: handle exception
							DebugLog.e("注册崩溃", "提示重试");
//							AppData.getClient().sendCallBackMsg(CallBackMsg.CLOSE_SHOW_LOGIN);
							continue;
						}
						if (c == null) {
							
						} else if (c instanceof LoginChapter) {
							// login error
							continue;

						} else if (c instanceof OrderChapter) {
							// success
//							AppData.getClient().sendCallBackMsg(CallBackMsg.CLOSE_SHOW_LOGIN);
//							Chapter order = ((OrderChapter) chapter).order();
							Http.save();
							break;
							
						}else if (c instanceof ContentChapter) {
							// success
//							AppData.getClient().sendCallBackMsg(CallBackMsg.CLOSE_SHOW_LOGIN); 
							Http.save();
							break;
						}
						lTime = System.currentTimeMillis();
					}
			}
		}
		
	}
	
	public View getCreaView(){

		AppData.getClient().setCallBackHander(handler);
		//进入编辑状态
		
		if (null == mRootView) {
//			mRootView = LayoutInflater.from(mContext).inflate(R.layout.boyi_bookshelf_main,
//					null);
			mRootView = mInflater.inflate(R.layout.boyi_bookshelf_main,
					mContainer, false);
			WindowManager manager = ((Activity) mContext).getWindowManager();
			mWidth = (manager.getDefaultDisplay().getWidth() - dip2px(
					((Activity) mContext), (45 + 45 + 30))) / 3;
			// mParams=new LayoutParams((int )mWidth, (int )(mWidth*4/3));
			
			
			channel=AppData.readMetaDataFromService(mContext,"channel_num");
			getRequestRecommand();
			//监听网络变化
			getRequestNotice();
			if(AppData.showRecommend())
			{
			  requestDayCommand();
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
	
	public void requestDayCommand(){
		/**
		 * 判断时间显示每日推荐
		 * */
		SharedPreferences mySharedPreferences = mContext
				.getSharedPreferences("everytime",
						Application.MODE_PRIVATE);

		long str = mySharedPreferences.getLong("time", 0);
		// DebugLog.e("上次时间", str+"");

		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

			//handler.postDelayed(dayCommandTask, 500);

		} else {

			// Date curDate = new Date(System.currentTimeMillis());//获取当前时间

			long time2 = new Date().getTime();
			// DebugLog.e("当前时间是@@几点？？？？？？", time2+"");
			/**
			 * 每天首次推荐
			 * */
			if (time2 - str > (24 - Integer.parseInt(lastHour)) * 60 * 60 * 1000) {
				// 请求推荐列表
				AppData.getTuijianList();
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				editor.putLong("time", time2);
				editor.commit();
			}

		}
		handler.postDelayed(dayCommandTask, 500);
	}
	
//	 private void createFloatView()  
//	    {  
//	        wmParams = new WindowManager.LayoutParams();  
//	        //获取的是WindowManagerImpl.CompatModeWrapper  
//	        mWindowManager = (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);  
//	        Log.i(TAG, "mWindowManager--->" + mWindowManager);  
//	        //设置window type  
////	        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;   
//	        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;   
//	        //设置图片格式，效果为背景透明  
//	        wmParams.format = PixelFormat.RGBA_8888;   
//	        //设置浮动窗口不可聚焦  
////	        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;        
//	        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//					|android.view.WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
//	        //调整悬浮窗显示的停靠位置为左侧置顶  
//	        wmParams.gravity = Gravity.LEFT | Gravity.TOP;         
//	        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity  
//	        wmParams.x = 0;  
//	        wmParams.y = 0;  
//	  
//	        //设置悬浮窗口长宽数据    
//	        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;  
//	        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
//	  
//	         /*// 设置悬浮窗口长宽数据 
//	        wmParams.width = 200; 
//	        wmParams.height = 80;*/  	     
//	        LayoutInflater inflater = LayoutInflater.from(mContext);  
//	        //获取浮动窗口视图所在布局  
//	        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.boy_float_frame, null);  
//	        ImageView closeIv=(ImageView) mFloatLayout.findViewById(R.id.by_dialog_colse);
//	        ImageView imageView=(ImageView) mFloatLayout.findViewById(R.id.boy_img_login);
//	        Animation operatingAnim = AnimationUtils.loadAnimation(mContext,
//					R.anim.tip);
//			LinearInterpolator lin = new LinearInterpolator();
//			operatingAnim.setInterpolator(lin);
//			imageView.startAnimation(operatingAnim); 
//<<<<<<< .mine
//
////			mFloatLayout.setOnClickListener(new OnClickListener() {
////				
////				@Override
////				public void onClick(View v) {
////					// TODO Auto-generated method stub
////					AppData.getClient().sendCallBackMsg(CallBackMsg.CLOSE_SHOW_LOGIN);
////				}
////			});
//			
//	        //添加mFloatLayout  
//					mWindowManager.addView(mFloatLayout, wmParams);  
//					handler.sendMessageDelayed(handler.obtainMessage(CallBackMsg.CLOSE_SHOW_LOGIN),5000);
//	        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,  
//	                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec  
//	                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));  	       
//
//	        
//			closeIv.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					handler.sendMessage(handler.obtainMessage(CallBackMsg.CLOSE_SHOW_LOGIN));
//				}
//			});
//=======
////			mFloatLayout.setOnClickListener(new OnClickListener() {
////				
////				@Override
////				public void onClick(View v) {
////					// TODO Auto-generated method stub
////					AppData.getClient().sendCallBackMsg(CallBackMsg.CLOSE_SHOW_LOGIN);
////				}
////			});
//			
//>>>>>>> .r512
//	        //添加mFloatLayout  
//					mWindowManager.addView(mFloatLayout, wmParams);  
//					handler.sendMessageDelayed(handler.obtainMessage(CallBackMsg.CLOSE_SHOW_LOGIN),5000);
//	        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,  
//	                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec  
//	                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));  
//	        
//	        closeIv.setOnClickListener(new OnClickListener() {				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					
//					AppData.getClient().sendCallBackMsg(CallBackMsg.CLOSE_SHOW_LOGIN);
////					handler.sendMessage(handler.obtainMessage(CallBackMsg.CLOSE_SHOW_LOGIN));
//				}
//			});
//	    }  
	
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
//		DebugLog.e("请求首推", "用BookHelper.loadDetail更新书籍");
		final SharedPreferences mySharedPreferences = mContext
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
		AppData.getRequestQueue().add(new JsonObjectRequest(url, null, new Listener<JSONObject>() {

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
							if (obj.getString("myimages").endsWith("null")) {
								item.bigCoverUrl=obj.getString("bigimages");
							}else {	
								String myimages=obj.getString("myimages");
//								DebugLog.e("封面url", myimages);
								item.bigCoverUrl=AppData.getConfig().getUrl(Config.URL_BOOK_COVER) +myimages;
							}
							item.classFication=obj.getString("ydsortname");
							item.clickStr=obj.getString("totalviews");
							item.freeCount=obj.getInt("freechapternums");
							item.totalCount=obj.getInt("totalchapters");
							item.isUpdata=0;	
							item.timeStamp=System.currentTimeMillis();
							item.lastDate=obj.getLong("updatetime");
							item.lastTitle=obj.getString("last_name");
//							bidMap.put(bid, coverUrl);
//							DebugLog.e("推荐书id:"+bid, "本地封面url："+coverUrl);

							if (! AppData.getDataHelper().foundBookBid(item.bid)) {								
//								DebugLog.e(item.name, "==数据库没有这本书，存上");								
							helper.insertKBBook(item);
//							Message msg = Message.obtain();
//							msg.what= CallBackMsg.UPDATE_BOOKSHELF;
//							handler.sendMessage(msg);
//							if (! mBookList.contains(item)) {								
//								mBookList.add(item);
//							}						
							AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_BOOKSHELF);  // 让书架更新书架
							}							
							if (! bidList.contains(bid)) {								
								bidList.add(bid);
							}
						}
									if (helper.getKbShelfList().size()>=bidList.size()) {
										SharedPreferences.Editor editor = mySharedPreferences.edit();
										editor.putBoolean("recommanded", true);
										editor.commit();
									}									

						DebugLog.d(TAG, "推荐书籍获取成功");

						AppData.getUser().setRecommand(); //  %%%%%%表示已经推荐过了

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
		String url = AppData.getConfig().getUrl(Config.URL_NOTICE)+"/qdid/"+AppData.readMetaDataFromService(mContext, "channel_num");
		DebugLog.e("获取公告","获取公告");
		AppData.getRequestQueue().add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
           
				try {
					JSONObject jsonObject=new JSONObject(response);
					int status = jsonObject.getInt("status");
					if(StatusCode.OK == status) {

						JSONArray array = jsonObject.getJSONArray("data");

						List<Notice> listNotice =new ArrayList<Notice>();
						for(int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							Notice notice = new Notice();
							notice.title = obj.getString("title");
							notice.content = obj.getString("content");
							notice.date = obj.getString("date");
							notice.url = obj.getString("url");
							listNotice.add(notice);
							
							
						}
						DebugLog.e("获得了公告：：长度为：", listNotice.size()+"");
						AppData.getUser().setmNoticeList(listNotice);
						AppData.getClient().sendCallBackMsg(CallBackMsg.NOTICE_SUCCESSFUL);

					} else {
                       //公告获取失败
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
			}}));

		
	}

	// 首推的pw
	private Runnable dayCommandTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 开始拿每日推荐
			if(mRootView != null && mRootView.getWidth() != 0 && mRootView.getHeight() != 0){
				
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
						}finally{
							showEveryDayPopuwidonw("", "", "",
									"");
						}
	
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						showEveryDayPopuwidonw("http://testsdk.boetech.cn/Uploads/./2015-05-08/1431080168.jpg", "", "",
								"");
	
					}
				}));
			}
			else{
				handler.postDelayed(dayCommandTask, 500);
			}
			
		}
	};

	private void initData() {

		initImageCacheCount(20);

		mIsGrid = AppData.getConfig().isIsGrid();// 判断是不是网格布局
		setUpdateinterface(new UpdateInterface() {
			
			@Override
			public void updateText() {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						getRequestUpdate(true);
						
						
					}
				}).start();
				
				
				
			}
		});
		
	}
	public static void update()
	{
		update.updateText();
	}

	private TextView tvSelectAll;
	private TextView tvDelete;
	private TextView tvCancel;
    private LinearLayout edit_ll;
    
	private void initView(View v) {
		edit_ll=(LinearLayout) v.findViewById(R.id.edit_ll);
		selectAll=(Button)v.findViewById(R.id.selectAll);
		detele=(Button)v.findViewById(R.id.delete);
		selectAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeSlectMode(false);
			}
		});
		//全部删除
		detele.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				list = new ArrayList<Integer>();
				if (mDelteArray.size() > 0) {
					for (int i = 0; i < mDelteArray.size(); i++) {
						list.add(mDelteArray.keyAt(i));
						
					}
					//hideAllDeletePopupWindow();
					edit_ll.setVisibility(View.GONE);
					showDeletePopupWindow(list);
				} else {
					showToast("删除的书籍数量不能为0");
					return;
				}
				
				
			}
		});
		btn_back = (ImageView) v.findViewById(R.id.search_back);
		btn_back.setVisibility(View.GONE);
		btn_bookStore =(RelativeLayout) v.findViewById(R.id.boyi_book);
		top_title = (TextView) v.findViewById(R.id.search_top_title_tv);
		//btn_bookStore.setText("书城");
		top_title.setText("我的书架");
		btn_bookStore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentActivity parent2 = (FragmentActivity) mContext;
				Intent intent = new Intent(mContext, StoreMain.class);
				mContext.startActivity(intent);
			}
		});

		mNotifyTv = (TextView) v.findViewById(R.id.bookshelf_notify_tv);
		refreshableView=(RefreshableView) v.findViewById(R.id.refreshable_view);
		
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			
			@Override
			public void onRefresh() {
				try {
					
					getRequestUpdate(false);
					
					Thread.sleep(500);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				refreshableView.finishRefreshing();
				
			}
		}, 0);
		mGridView = (ShelfGridView) v.findViewById(R.id.bookshelf_gridview);
		mGridView.setVerticalSpacing(dip2px(mContext, 46));
		mGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mEmptyView = v.findViewById(R.id.bookshelf_empty_layout);
		mOperatorView = v.findViewById(R.id.bookshelf_operator_layout);
		tvSelectAll = (TextView) mOperatorView.findViewById(R.id.all_tv);
		tvDelete = (TextView) mOperatorView.findViewById(R.id.delete_tv);
		tvCancel = (TextView) mOperatorView.findViewById(R.id.cancel_tv);
		Button btnGoStore = (Button) v.findViewById(R.id.bookshelf_gostore_btn);
		btnGoStore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentActivity parent = (FragmentActivity) mContext;
				// if (parent instanceof MainActivity) {
				// ((MainActivity) parent).setPage(Page.bookstore);
				// }
			}
		});

		mNotifyTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//跳转至活动、阅读、游戏界面
				// TODO Auto-generated method stub
				if (null != mNotice) {
					//跳到活动界面
					if(mNotice.url.startsWith("http"))
					{
						String title=mNotice.title;
						enterActivity(title,mNotice.url);
					}else
					{
					//判断是跳到阅读界面还是活动界面
//					final String xName = mNotice.url.substring(mNotice.url
//							.lastIndexOf("=") + 1);
//					String urlString=mNotice.url;
//					DebugLog.e("公告封面：", urlString);
					
					handler.post(new Runnable() {

						public void run() {
							try {
								GetBookDetailUtil.startReadingBook(bid, mNotice.url, mContext, false, 0);
//								AppData.startBookReading(mContext,mNotice.url, "",false);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}

						}

					});
				}

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
						getRequestUpdate(false); // 更新书籍
					}					
				}
			}).start();

//		}

		mDelteArray = new SparseIntArray();
		mGridAdapter = new BookshelfGridAdapter(mContext);
		
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
					mGridAdapter.notifyDataSetChanged();

				} else {
					if (position == mBookList.size() + 1) { // 最后一个添加符号

						// 直接去书城
						Intent intent = new Intent(mContext,
								StoreMain.class);
						mContext.startActivity(intent);

					} else if (position == mBookList.size()) {
						egi.enterGiftActivity();
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
				if (!selected) {
					if (position >= mBookList.size()) {
						return false;
					}
					showPopupGridAction(position);
					return true;
				} else {
					return false;
				}
			}
		});
		
		// mListView.setAdapter(mListAdapter);
		
//		tvSelectAll.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				changeSlectMode(false);
//			}
//		});

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

	protected void enterActivity(String title,String url) {
		//跳到详情页
		Intent intent2 = new Intent(mContext, PageActivity.class);
		intent2.putExtra("title",title);
		intent2.putExtra("url",url);
		mContext.startActivity(intent2);
	}


	// 展示书架的方法 调用1.更新完数据以后，在handler的处理中调用

	private void changeSlectMode(boolean isBookManager) {
		if(isBookManager)
		{
		     selectAll.setText("全选");
		     return;
		}
		if (selectAll.getText().equals("全选")) {
			for (int i = 0; i < mBookList.size(); i++) {

				mDelteArray.append(i, DELETE_SELECTEED);

			}
			selectAll.setText("取消选择");
		} else {
			for (int i = 0; i < mBookList.size(); i++) {
				// 如果被选定了
//				if (mDelteArray.get(i, DELETE_UNSELECTE) == DELETE_SELECTEED) {
//					mDelteArray.delete(i);
//				} else {
//					mDelteArray.append(i, DELETE_SELECTEED);
//				}
				mDelteArray.delete(i);
			}
			selectAll.setText("全选");
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

		Intent intent = new Intent(mContext, OnlineReadingActivity.class);
		intent.putExtra("BookItem", item);

		((Activity) mContext).startActivityForResult(intent,PageID.Bookshelf);
	}

	private void startBookDetailActivity(int position) {
		BookItem item = mBookList.get(position);
//		if (item.onlineID != BookItem.ON_LOCAL_TXT) {
//			startReadingActivity(position);
////			startBookDetil(item.bid);
//		}
		Intent intent=new Intent(mContext,BookDetail.class);
		intent.putExtra("bid",item.bid);
		mContext.startActivity(intent);
		
		// else {
		// showToast("这是本地书籍", Toast.LENGTH_SHORT);
		// }
	}




	public void showNotice() {
		// TODO Auto-generated method stub
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


	public void stopNotice() {
		// TODO Auto-generated method stub

		stopShowNotice();
	}
	public void upDataShelf(){
		Message msg=new Message();
		msg.what=CallBackMsg.UPDATE_BOOKSHELF;
		handler.sendMessage(msg);
	}	
	public void onDestroy() {
	AppData.getClient().setNullCallBackHander(handler);
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
			addBookView = LayoutInflater.from(mContext).inflate(
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
			mPopupWindowGridAction.setAnimationStyle(R.style.mypopwindow_anim_style);

		}

		setGridActionData(position);
		mPopupWindowGridAction.showAtLocation(mGridView, Gravity.NO_GRAVITY, 0,
				0);
	}

	private void hidePopupGirdAction() {
		if (null != mPopupWindowGridAction) {
			mPopupWindowGridAction.dismiss();
		}
		hideProgress();
	}

	private View gridActionView = null;
	private View addBookView = null;
	private TextView bookNameTv, descTv;
	private Button btnDetail, btnDelete;
	private NetworkImageView bookCover;

	private View getGridActionView() {

		if (null == gridActionView) {
			gridActionView = LayoutInflater.from(mContext).inflate(
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
			

			btnDetail = (Button) gridActionView.findViewById(R.id.book_detail_lookgo);
			btnDelete = (Button) gridActionView.findViewById(R.id.book_detail_delete);
		}

		return gridActionView;
	}
	private List<Integer> list=null;
	private int xposition=0;
	private void setGridActionData(int position) {
		BookItem item = mBookList.get(position);

		bookCover.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
		bookCover.setErrorImageResId(R.drawable.boyi_ic_cover_default);
		bookCover.setImageUrl(item.bigCoverUrl, getImageLoader());

		bookNameTv.setText(item.name);
		descTv.setText(item.longDesc);
//		authorTv.setText("作者:" + item.author);
//		bookSizeTv.setText("简介:" + item.shortDesc);
//		bookProgressTv.setText("阅读进度:" + item.getPercent());
//		bookProgressTv.setVisibility(View.GONE);
		xposition=position;
		btnDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startBookDetailActivity(xposition);
				hidePopupGirdAction();
			}
		});
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                list=new ArrayList<Integer>();
				list.add(xposition);
				showDeletePopupWindow(list);
				hidePopupGirdAction();
			}
		});
	}
	private PopupWindow detele_popupwindow;
	private void dismissPopupWindow()
	{
		if(detele_popupwindow!=null)
		{
			detele_popupwindow.dismiss();
		}
		hideProgress();
	}
	private void showDeletePopupWindow(List<Integer> list) {
		if(detele_popupwindow==null)
		{
			detele_popupwindow=new PopupWindow(getDeleteView(list),LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			detele_popupwindow.setFocusable(true);
			detele_popupwindow.setTouchable(true);
			detele_popupwindow.setOutsideTouchable(true);
			detele_popupwindow.setBackgroundDrawable(new BitmapDrawable());
			detele_popupwindow.setAnimationStyle(R.style.mypopwindow_anim_style);
		}
		initPopupwindoData();
		detele_popupwindow.showAtLocation(mRootView, Gravity.NO_GRAVITY,0,0);
	}
    private View v;
    private Button confirm;
    private Button cancel;
    private NetworkImageView imageView1,imageView2,imageView3;
    private TextView textName1,textName2,textName3,textLook1,textLook2,textLook3,look1,look2,look3,delete_recommend_text;
    private ImageView guanbi;
    private LinearLayout delete_recommend_ll;
	private View getDeleteView(List<Integer> list2) {
		if(v==null)
		{
			v=LayoutInflater.from(mContext).inflate(R.layout.boyi_delete_book,null);
			delete_recommend_ll=(LinearLayout) v.findViewById(R.id.delete_recommend_ll);
			delete_recommend_text=(TextView) v.findViewById(R.id.delete_recommend_text);
			guanbi=(ImageView) v.findViewById(R.id.guanbi);
			confirm=(Button) v.findViewById(R.id.confirm_delete);
			cancel=(Button) v.findViewById(R.id.cancel_delete);
			textName1=(TextView) v.findViewById(R.id.bookname1);
			textName2=(TextView) v.findViewById(R.id.bookname2);
			textName3=(TextView) v.findViewById(R.id.bookname3);
			textLook1=(TextView) v.findViewById(R.id.sum1);
			textLook2=(TextView) v.findViewById(R.id.sum2);
			textLook3=(TextView) v.findViewById(R.id.sum3);
			look1=(TextView) v.findViewById(R.id.look1);
			look2=(TextView) v.findViewById(R.id.look2);
			look3=(TextView) v.findViewById(R.id.look3);
			imageView1=(NetworkImageView)v.findViewById(R.id.imageview1);
			imageView2=(NetworkImageView)v.findViewById(R.id.imageview2);
			imageView3=(NetworkImageView)v.findViewById(R.id.imageview3);
			list=list2;
			
			guanbi.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					chageTrim(0);
					dismissPopupWindow();
				}
			});
			confirm.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					List<BookItem> listItem=new ArrayList<BookItem>();
					for(int i=0;i<list.size();i++)
					{
						BookItem item=mBookList.get(list.get(i));
						listItem.add(item);
						deleteBook(item);
					}
					mBookList.removeAll(listItem);
					chageTrim(0);
					mGridAdapter.notifyDataSetChanged();
					hidePopupGirdAction();
					dismissPopupWindow();
					showToast("删除成功");
					
				}
					
			});
			
			
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					chageTrim(0);
					dismissPopupWindow();
					
				}
			});
			imageView1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String strLeft=jsonList.get(images[0]);
		        	final String bid3=strLeft.substring(strLeft.lastIndexOf("/")+1,strLeft.lastIndexOf("?")); 
					Intent intent=new Intent(mContext, BookDetail.class);
					intent.putExtra("bid", bid3);
					mContext.startActivity(intent);
				}
			});
			imageView2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String str=jsonList.get(images[1]);
			    	final String bid1=str.substring(str.lastIndexOf("/")+1,str.lastIndexOf("?"));  
					Intent intent1=new Intent(mContext, BookDetail.class);
					intent1.putExtra("bid", bid1);
					mContext.startActivity(intent1);
				}
			});
			imageView3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String strRight=jsonList.get(images[2]);
		        	final String bid2=strRight.substring(strRight.lastIndexOf("/")+1,strRight.lastIndexOf("?"));
					Intent intent3=new Intent(mContext, BookDetail.class);
					intent3.putExtra("bid", bid2);
					mContext.startActivity(intent3);
				}
			});
		}
		return v;
	}
	
	private String recommendUrl,mbid;
    private boolean isClose=false;
    private List<String> jsonList,jsonList2;
    private String  imageUrl2;
    private ImageLoader imageLoader;
	private void initPopupwindoData() {
		recommendUrl = AppData.getConfig().getUrl(
				Config.URL_BOOK_RECOMMAND);
		// 加载推荐
		jsonList=new ArrayList<String>();
        
		try {
			mbid =Intent.getIntent(Uri.decode(recommendUrl)).getStringExtra("bid");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestQueue mRequestQueue = AppData.getRequestQueue();  
        final LruCache<String, Bitmap> mImageCache = new LruCache<String, Bitmap>(  
        		40*1024*1024);  
        ImageCache imageCache = new ImageCache() {  
            @Override  
            public void putBitmap(String key, Bitmap value) {  
                mImageCache.put(key, value);  
            }  
  
            @Override  
            public Bitmap getBitmap(String key) {  
                return mImageCache.get(key);  
            }  
        };  
        imageLoader=new ImageLoader(mRequestQueue,imageCache);
        DetailTask task = new DetailTask("detail"+mbid);
        AppData.getClient().getTaskManagerRead().addTask(task);
           
		
	}
	public class DetailTask extends CallBackTask{

		public DetailTask(String strTaskName) {
			super(strTaskName);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void doTask() {
			jsonList.clear();
			jsonList=AppData.getTuijianList();
//			 设置3个封面
			if (!isClose) {
				Message msg = new Message();     
				msg.what = UPDATA_IMAGE;
				handler.sendMessage(msg); // 向Handler发送消息,更新UI			
			}
		}
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

	protected void showEveryDayPopuwidonw(final String url, String bookName,
			String word, String mDesc) {

		// DebugLog.e("展示每日推荐的pw", "pw 开始加载");

		everyDay = View.inflate(mContext, R.layout.boyi_shelf_every_day,
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
//		ListImageListener listener = new ListImageListener(
//				everyCover, R.drawable.boyi_ic_cover_default,
//				R.drawable.boyi_ic_cover_default,
//				"everyDay");
//		getImageLoader().get(url, listener,210,280);
		
		everyCover.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
		everyCover.setErrorImageResId(R.drawable.boyi_ic_cover_default);		
		everyCover.setImageUrl(url, getImageLoader());
        DebugLog.e("每人推荐的图片url",url);
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
//                AppData.goBoyiSdk(AppData.ENTRY_CODE_NAVIJATION,
//						AppData.ENTRY_TYPE_READBOOK, ydBid, mContext, "",
//						"");
				GetBookDetailUtil.startReadingBook(ydBid, url, mContext, false, 0);
//                AppData.startBookReading(mContext,ydBid, url,false);
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

		everyPW.showAtLocation(mRootView, Gravity.CENTER, 0, 0);

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
				Intent intent = new Intent(mContext,
						OnlineReadingActivity.class);
				intent.putExtra("BookItem", item);
				((Activity) mContext).startActivityForResult(intent, PageID.Bookshelf);

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

	private Bitmap imgMarker;
	private int width,height;   //图片的高度和宽带
	private Bitmap imgTemp;
	private Drawable createDrawable(String letter) {
		imgMarker = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.boyi_ic_cover_default);
		width = imgMarker.getWidth();
		height = imgMarker.getHeight();
		imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(imgTemp);
		Paint paint = new Paint(); // 建立画笔
		paint.setDither(true);
		paint.setFilterBitmap(true);
		Rect src = new Rect(0, 0, width, height);
		Rect dst = new Rect(0, 0, width, height);
		canvas.drawBitmap(imgMarker, src, dst, paint);

		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		
		textPaint.setTextSize(40.0f);
		textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度
		textPaint.setColor(Color.parseColor("#0AC090"));
		StaticLayout layout = new StaticLayout(letter,textPaint,160,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
		canvas.translate(width/6,height/3);
		layout.draw(canvas);
//		canvas.drawText(letter,width/4,height/2,
//				textPaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		
		return (Drawable) new BitmapDrawable(mContext.getResources(), imgTemp);

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
			if(isSelect)
			{
				return mBookList.size();
			}else
			{
				return mBookList.size()+2;
			}
			    
			  
			   
			
			
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
				
//				AbsListView.LayoutParams params = new AbsListView.LayoutParams(
//						ViewGroup.LayoutParams.WRAP_CONTENT,dip2px(mContext,108));
//				
//				convertView.setLayoutParams(params);
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
				holder.update=(ImageView) convertView.findViewById(R.id.status_update);
				holder.bookSelected=(ImageView) convertView.findViewById(R.id.book_selected);
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
			mParams.height = (int) mWidth *  4/3;
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
					holder.bookSelected.setVisibility(View.VISIBLE);
					int value = mDelteArray.get(position, DELETE_UNSELECTE);
					if (DELETE_UNSELECTE == value) {
						holder.bookSelected
								.setImageResource(R.drawable.boyi_chosed);
					} else {
						holder.bookSelected
								.setImageResource(R.drawable.boyi_unchose);
					}
				}
			} else {
				if (position == mBookList.size()
						|| position == mBookList.size() + 1) {
					// 显示出来
					convertView.setVisibility(View.VISIBLE);
				}else
				{
					convertView.setVisibility(View.VISIBLE);
				}
				holder.shadow.setVisibility(View.INVISIBLE);
				holder.bookSelected.setVisibility(View.GONE);
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
					if(item.bigCoverUrl.startsWith("http"))
					{
					
					if (item.bigCoverUrl.equals("http://sdk.boetech.cn/Uploads/null")) {
						if(!TextUtils.isEmpty(item.littleCoverUrl))
						{
						  //本地缓存起来小图片,并且设置给响应的控件
						  getImageLoader().get(item.littleCoverUrl, listener,210,280);
						}else
						{
							
							
							holder.cover.setImageDrawable(createDrawable(item.name));
						}
					}else {
						getImageLoader().get(item.bigCoverUrl, listener,210,280);
					}
					}else if(item.bigCoverUrl.startsWith("asset"))
					{
						String path=item.bigCoverUrl.substring(9);
						Bitmap bitmap=CommonUtil.getBitmapFromAsset(mContext,path);
						holder.cover.setImageBitmap(bitmap);
					}

					 
						// 连载状态
						if (item.status == BookItem.STATUS_SERIAL) {
//							holder.status
//							.setImageResource(R.drawable.boyi_continue);
					       holder.status.setVisibility(View.GONE);
					       holder.update.setVisibility(View.GONE);
							if (item.isUpdata == 1) { // 判断更新状态
								holder.update.setImageResource(R.drawable.boyi_update_tubiao);
								holder.update.setVisibility(View.VISIBLE);

							}
							

						}
						// 完结
						else if (item.status == BookItem.STATUS_FINISH) {
							holder.status.setVisibility(View.GONE);
//							holder.status
//									.setImageResource(R.drawable.boyi_finish);
							holder.update.setVisibility(View.GONE);
							
						} else {
							holder.status.setVisibility(View.INVISIBLE);
							holder.update.setVisibility(View.GONE);
						}
						
					

					// 未读章节数,上角标
				}
				convertView.setVisibility(View.VISIBLE);
				holder.bookname.setText(item.name);
				// holder.progress.setText("已读" + item.getPercent());
			} else if (position == mBookList.size()) {

				holder.cover.setTag("store_gift");
				holder.cover.setImageResource(R.drawable.boyi_gift);
				holder.bookname.setText("");
				holder.status.setVisibility(View.INVISIBLE);
				holder.update.setVisibility(View.INVISIBLE);

			} else {
				holder.cover.setTag("add_book");
				holder.cover.setImageResource(R.drawable.boyi_addbook);
				holder.bookname.setText("");
				holder.status.setVisibility(View.INVISIBLE);
				holder.update.setVisibility(View.INVISIBLE);

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
				
			} 
			notifyDataSetChanged();
			
		}

		private class ViewHolder {
			ImageView cover;
			ImageView shadow;
			ImageView select;
			RelativeLayout layout;
			ImageView status;
			TextView bookname;
			TextView progress;
			ImageView update;
			ImageView bookSelected;
		}

	}
   
	private void deleteBook(BookItem item) {
		//BookItem item = mBookList.get(position);
		
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
			//删除映射表
			//进行判断是否有映射

		}
//////		//删除多本
//		if(list.size()==1)
//		{
//		  mBookList.remove(position);
//		}
		

	}

	private void showToast(String message) {
		Toast.makeText(mContext, message, 1000).show();
	}
	
	private final static int TIMER_NOTICE = 5000;
	private Timer timerNotice = null;
	private TimerTask timerTask = null;
	private int noticeIndex = 0;
	private boolean isCancel = false;

	private void startShowNotice() {

		if (AppData.getUser().getmNoticeList().size() <= 0) {
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
					List<Notice> listNotices=AppData.getUser().getmNoticeList();
					if(listNotices.size()>0)
					{
						DebugLog.e("公告长度ssss",listNotices.size()+"");
						Notice obj = listNotices.get(noticeIndex);
						
						AppData.getClient().sendCallBackMsg(
								CallBackMsg.NOTICE_SHOW_NEXT, obj);

						noticeIndex = (noticeIndex + 1)
								% (AppData.getUser().getNoticeList().size());
					}
					
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
	private ProgressDialog mProgressDialog;
	public void showProgressCancel(final String taskName, String title,
			String message) {
		LayoutInflater inflaterDl = LayoutInflater.from(mContext);
		RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(
				R.layout.boy_zdy_dialog, null);
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(params);
		ImageView imageView = (ImageView) layout.findViewById(R.id.boy_img);
		Animation operatingAnim = AnimationUtils.loadAnimation(mContext,
				R.anim.tip);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		imageView.startAnimation(operatingAnim);

		TextView tView = (TextView) layout.findViewById(R.id.by_dialog_text);
		tView.setText(message);
		if (null == mProgressDialog) {
			mProgressDialog = ProgressDialog
					.show(mContext, title, message);
			mProgressDialog.getWindow().setContentView(layout);
		} else {
			mProgressDialog.setTitle(title);
			mProgressDialog.setMessage(message);
			if (!mProgressDialog.isShowing()) {
				mProgressDialog.show();
				mProgressDialog.getWindow().setContentView(layout);
			}
		}
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		if (taskName != null || taskName != "") {

			mProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					// DebugLog.e("", "线程--"+taskName+"--需要关闭");
					AppData.getClient().getTaskManagerRead().delTask(taskName);

				}
			});
		}
	}
	public void hideProgress() {
		if (null != mProgressDialog && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
	/**
	 * 更新书架书籍连载信息
	 * */
	private int count=0;
	private  String mOperator = "";
	private  String mChannel;
	private  int mOper;
	private String[]bidArr;
	private String updataBidS;
	private void getRequestUpdate(boolean isNotification) {
		DebugLog.e("请求更新","请求更新");
		bidArr=new String[mBookList.size()];
		StringBuffer buffer=new StringBuffer();
		buffer.append("[");
		for (int i = 0; i < mBookList.size(); i++) {
			bidArr[i]=mBookList.get(i).bid;
			buffer.append(bidArr[i]+",");
		}
		String jsonBuffer=buffer.toString().substring(0, buffer.toString().length()-1);
		JSONArray jsonArray;
		updataBidS=jsonBuffer+"]";
		Log.e("上传更新的json串", updataBidS);
//		try {
//			jsonArray = new JSONArray(String.valueOf(bidArr));
//			updataBidS=jsonArray.toString();
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		String yysid="1";
		item = new BookItem();
		mChannel=AppData.readMetaDataFromService(mContext, "channel_num");
		mOper = AppData.getConfig().getDeviveInfo().getOperator(mContext);

		if (TextUtils.isEmpty(mChannel)) {
			mChannel = "default";
		}

		switch (mOper) {
		case DeviceInfo.OPERATOR_CM:
			mOperator = "移动";
			yysid="1";
			break;
		case DeviceInfo.OPERATOR_CU:
			mOperator = "联通";
			yysid="2";
			break;
		case DeviceInfo.OPERATOR_TC:
			mOperator = "电信";
			yysid="3";
			break;

		default:

			break;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("aids", updataBidS);
		map.put("qdid", "" + mChannel);
		map.put("yysid", "" + yysid);

		String detailUrl = AppData.getConfig().getUrl(
				Config.UPDATA_BOOKSHELF_COUNT);
		AppData.getRequestQueue().add(
				new JsonObjectPostRequest(detailUrl,
						new Listener<JSONObject>() {

							@Override
							public void onResponse(
									JSONObject response) {
								// TODO Auto-generated method
								try {
									int status = response
											.getInt("status");
									if (status == StatusCode.OK) {
										JSONObject jsonObject=response.getJSONObject("data");
										JSONObject object2;
										for (int i = 0; i < bidArr.length; i++) {											
											object2=jsonObject.getJSONObject(bidArr[i]);
											int upCount=object2.getInt("remainChapterNum")-mBookList.get(i).totalCount;
											DebugLog.e("书"+bidArr[i]+"表中有"+mBookList.get(i).totalCount, "网上"+object2.getInt("remainChapterNum"));
											if (upCount>0) {
												AppData.getDataHelper().updateQoveBook(bidArr[i],
														object2.getInt("status"), 1, object2.getInt("remainChapterNum"));
												
												AppData.getClient().sendCallBackMsg(
														CallBackMsg.UPDATE_BOOKSHELF);	
												count++;
											}
										}
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
								DebugLog.e("booksheldutil", "更新出错");
							}
						}, map));
		if(count>0 && isNotification)
		{
			sni.showNotification(count);
			count=0;
		}
	}
	
	 @SuppressWarnings("deprecation")
	
	public void chageTrim(int i) {

		switch (i) {
		case 0:
			
				mGridAdapter.setSelecteMode(false);
				selected=false;
				//hideAllDeletePopupWindow();
				edit_ll.setVisibility(View.GONE);
			    

			break;
		case 1:
			if (mBookList.size()>0) {
				mGridAdapter.setSelecteMode(true);
				selected=true;
				mDelteArray.clear();
				edit_ll.setVisibility(View.VISIBLE);
				//弹出批量删除popupwindow
//				showAllDeletePopupWindow();
				changeSlectMode(true);
				

			}
			

			break;
		case 2:
			// 0 是更改横竖切换
						if (mBookList.size() > 0) {
							mIsGrid = !mIsGrid; //
							showShelfView();
						}

			// Intent intent = new Intent(getActivity(),
			// FileBrowserActivity.class);
			// startActivityForResult(intent, PageID.Bookshelf);

			break;

		default:
			break;
		}
	}
	private void hideAllDeletePopupWindow()
	{
		if(dPopupWindow!=null)
		{
			dPopupWindow.dismiss();
		}
	}
	
    private PopupWindow dPopupWindow;
    
	private void showAllDeletePopupWindow() {
		if(dPopupWindow==null)
		{
			dPopupWindow=new PopupWindow(getAllDeleteView(),LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			dPopupWindow.setTouchable(true);
			dPopupWindow.setFocusable(true);
			dPopupWindow.setOutsideTouchable(true);
			dPopupWindow.setBackgroundDrawable(null);
			dPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
		}
		
		dPopupWindow.showAtLocation(mRootView, Gravity.BOTTOM,0,0);
		
	}
	
    private View dView;
    private Button selectAll,detele;
	private View getAllDeleteView() {
		if(dView==null)
		{
			dView=LayoutInflater.from(mContext).inflate(R.layout.delete_all,null);
			dView.setFocusable(true);
			dView.setFocusableInTouchMode(true);
			dView.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						dPopupWindow.dismiss();
						chageTrim(0);
						dPopupWindow = null;
						return true;
					}
					return false;
				}
			});
				
			selectAll=(Button) dView.findViewById(R.id.selectAll);
			detele=(Button) dView.findViewById(R.id.delete);
			selectAll.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					changeSlectMode(false);
				}
			});
			//全部删除
			detele.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					list = new ArrayList<Integer>();
					if (mDelteArray.size() > 0) {
						for (int i = 0; i < mDelteArray.size(); i++) {
							list.add(mDelteArray.keyAt(i));
							
						}
						hideAllDeletePopupWindow();
						showDeletePopupWindow(list);
					} else {
						showToast("删除的书籍数量不能为0");
						return;
					}
					
					
				}
			});
		}
		return dView;
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

	public void resumeShelf() {
		// TODO Auto-generated method stub
		mBookList = AppData.getDataHelper().getKbShelfList();

		if (mEmptyView.getVisibility() == View.VISIBLE && mBookList.size() > 0) {

			mEmptyView.setVisibility(View.GONE);

			showShelfView(); // 更新书籍 后初始化书架
		}
		mGridAdapter.notifyDataSetChanged();

	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	private int mCacheCount;
	protected void initImageCacheCount(int count) {
		if(null != mImageLoader){
			throw new RuntimeException("BaseFragment: bitmap cache count must set before getImageLoader");
		}
		
		mCacheCount = count;
	}
	
	private ImageLoader mImageLoader = null;
	
	protected ImageLoader getImageLoader() {
//		if(mCacheCount <= 0) {
//			throw new RuntimeException("Bitmap cache count <= 0");
//		}
		ImageCache	imageCache=ImageCacheManager.getInstance(mContext);
		if(mImageLoader == null) {
			mImageLoader = new ImageLoader(AppData.getRequestQueue(), imageCache);
		}		
		return mImageLoader;
	}
    	
 	/** 
     * 将数组转换为JSON格式的数据。 
     */  
    public static String changeArrayDateToJson(ArrayList<keyVale> contentList){  
        try {  
            JSONArray array = new JSONArray();  
            JSONObject object = new JSONObject();  
            int length = contentList.size();  
            for (int i = 0; i < length; i++) {  
            	keyVale  stone = contentList.get(i);  
                String title = stone.getName();  
                String displayorder = stone.getDisplayorder();  
                String content = stone.getContent();  
                String cid = stone.getCid();  
                JSONObject stoneObject = new JSONObject();  
                stoneObject.put("title", title);  
                stoneObject.put("displayorder", displayorder);  
                stoneObject.put("cid", cid);  
                stoneObject.put("content", content);  
                array.put(stoneObject);  
            }  
            object.put("stones", array);  
            return array.toString();  
        } catch (JSONException e) {  
            e.printStackTrace();  
        }  
        return null;  
    } 
    
    public class keyVale{
		private String name;
		private String content;
		private String displayorder;
		private String cid;
		public keyVale(String name,String content,String displayorder,String cid){
			super();
			this.name=name;
			this.content=content;
			this.displayorder=displayorder;
			this.cid=cid;
		}
		public String getDisplayorder() {
			return displayorder;
		}
		public void setDisplayorder(String displayorder) {
			this.displayorder = displayorder;
		}
		public String getCid() {
			return cid;
		}
		public void setCid(String cid) {
			this.cid = cid;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
	}
	
	
}
