package com.xn.xiaoyan;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.config.Config;
import com.boyiqove.library.volley.RequestQueue;
import com.boyiqove.library.volley.toolbox.ImageLoader;
import com.boyiqove.library.volley.toolbox.ImageLoader.ImageCache;
import com.boyiqove.library.volley.toolbox.NetworkImageView;
import com.boyiqove.task.CallBackTask;
import com.boyiqove.ui.bookshelf.BookshelfMainTest;
import com.boyiqove.ui.bookshelf.BookshelfUtil;
import com.boyiqove.ui.bookshelf.EnterGiftActivityInterface;
import com.boyiqove.ui.bookshelf.InterfaceShelf;
import com.boyiqove.ui.bookstore.BookDetail;
import com.boyiqove.ui.bookstore.ShowNotificationInterface;
import com.boyiqove.ui.bookstore.StoreMain;
import com.boyiqove.util.CommonUtil;
import com.boyiqove.util.DebugLog;
import com.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xiaoyan.util.UIUtils;
import com.xn.xiaoyan.fragment.LeftSldingFragment;
import com.xn.xiaoyan.services.XyServices;
import com.xn.xiaoyan.user.GiftActivity;

public class MainActivity extends FragmentActivity implements OnClickListener {
	
	private SlidingMenu slidingMenu;
	private ImageView user_center;
	private RelativeLayout menu_rl,user_center_rl;
	private TextView top;
	private TextView bookStoreTv;
	private BookshelfMainTest f1;
	private View mRootView;
	private boolean isShow=false;
	private String channel;
	private final static int UPDATA_IMAGE=1;
	private static boolean openUserCenter;
	private int []images;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//AppData.openLog(true);
		
//		AppData.getClient().sendProxyMsg(BoyiService.MSG_RECOMMAND);
		
		setContentView(R.layout.activity_main);
        AppData.init(this);
		if(getIntent().getBooleanExtra("isForceUpdate",false))
		{
			UIUtils.UmengUpdate(MainActivity.this,null);
		}
		
		
		if(AppData.isAutoUpdate() && AppData.isFirst)
		{
		  UmengUpdateAgent.setUpdateOnlyWifi(false);
		  UmengUpdateAgent.update(this);
		}
		channel=AppData.readMetaDataFromService(this,"channel_num");
		mRootView=View.inflate(this,R.layout.activity_main,null);
		user_center_rl=(RelativeLayout) findViewById(R.id.user_center_rl);
		user_center=(ImageView) findViewById(R.id.user_center);
		user_center_rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				slidingMenu.toggle();
				
			}
		});
		menu_rl=(RelativeLayout) findViewById(R.id.menu_rl);
		
		menu_rl.setOnClickListener(new OnClickListener() {
			
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
		top=(TextView) findViewById(R.id.top_rl);
		bookStoreTv=(TextView) findViewById(R.id.search_top_title_tv);
		bookStoreTv.setText("我的书架");
		
		
       
		f1= new BookshelfMainTest();// 书架
		this.getSupportFragmentManager().beginTransaction().replace(R.id.vp_frame_container,f1).commit();
		//开启service

//		Intent intent=new Intent(this,XyServices.class);
//		startService(intent);

        initSlidingmenu();
        
		AppData.setInterfaceShelf(new InterfaceShelf() {					
			@Override
			public void onGoShelf(Context context ,boolean isOpenUserCenter) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(context, MainActivity.class);
				context.startActivity(intent);
				BookshelfMainTest f1= new BookshelfMainTest();// 书架
				MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.vp_frame_container, f1).commitAllowingStateLoss();
				//不弹出每日推荐,不弹更新
				AppData.isFirst=false;
				openUserCenter=isOpenUserCenter;
				AppData.setShowRecommend(false);
				
				

			
			}

		});
		BookshelfUtil
				.setShowNotificationInterface(new ShowNotificationInterface() {

					@Override
					public void showNotification(int count) {
						String text = "书架有" + count + "本书籍更新了，赶快去查看吧。。。";
						
						UIUtils.showNotification(text,MainActivity.this,
								UIUtils.NOTIFICATION_BOOK_UPDATE_ID);
					}
				});
		BookshelfUtil.setEnterGiftinterface(new EnterGiftActivityInterface() {

			@Override
			public void enterGiftActivity() {
				Intent intent2 = new Intent(MainActivity.this,
						GiftActivity.class);
				startActivity(intent2);

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
    private LinearLayout enterLayout;
    private LinearLayout bookManager;
    private boolean isSelected=false;
	private View getPopupWinodwView() {
		// TODO Auto-generated method stub
		if(view==null)
		{
		  view=LayoutInflater.from(this).inflate(R.layout.bookshelf_menu,null);
		  enterLayout=(LinearLayout) view.findViewById(R.id.enter_bookstore);
		  bookManager=(LinearLayout) view.findViewById(R.id.manager_book);
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
				//进入书城
				Intent intent = new Intent(MainActivity.this, StoreMain.class);
				startActivity(intent);
				
				popupWindow.dismiss();
			}
		});
		  //进入编辑状态
		  bookManager.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				f1.setMode(!isSelected);
				isSelected=!isSelected;
				popupWindow.dismiss();
			}
		});
		}
		return view;
	}
	
	private void initSlidingmenu() {
		
		slidingMenu = new SlidingMenu(this);
		
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowWidth(0);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setFocusable(true);
		slidingMenu.setFadeDegree(0.6f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setFadeEnabled(true);
		slidingMenu.setMenu(R.layout.sliding_menu_menu);
		
		MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame,new LeftSldingFragment(slidingMenu)).commit();
	}

	private static Boolean isExit = false;
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;// 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			// 添加一个时间间隔
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					// 取消退出
					isExit = false;
				}
			}, 2000);// 200-->两秒内没有按下返回键,取消刚才执行的任务
		} else {
			finish();
			System.exit(0);
		}
	}
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	//super.onBackPressed();
    	//exitBy2Click();
    	//弹出popupWindow
    	if(!isSelected)
    	{
    	  showExitPopupWindow();
          return;
    	}
    	isSelected=false;
    	f1.setMode(false);
    	
    }
    
    private PopupWindow exitPopupWindow;
    private View v;
    private Button confim,cancel;
    private NetworkImageView imageView1,imageView2,imageView3;
    private RelativeLayout emptly;
	private void showExitPopupWindow() {
		if(exitPopupWindow==null)
		{
			exitPopupWindow=new PopupWindow(getExitView(),LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			exitPopupWindow.setFocusable(true);
			exitPopupWindow.setTouchable(true);
			exitPopupWindow.setOutsideTouchable(true);
			exitPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			exitPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
			
		}
		initData();
		exitPopupWindow.showAtLocation(mRootView, Gravity.NO_GRAVITY,0,0);
	}
    private TextView textName1,textName2,textName3,exit_recommend_text;
    private TextView textLook1,textLook2,textLook3,look1,look2,look3;
    private ImageView guanbi;
    private LinearLayout exit_recommend;
	private View getExitView() {
		if(v==null)
		{
			
			v=LayoutInflater.from(this).inflate(R.layout.boyi_exit_book,null);
			exit_recommend_text=(TextView) v.findViewById(R.id.exit_recommend_text);
			exit_recommend=(LinearLayout) v.findViewById(R.id.exit_recommend_ll);
			emptly=(RelativeLayout) v.findViewById(R.id.empty);
			guanbi=(ImageView) v.findViewById(R.id.guanbi);
			confim=(Button) v.findViewById(R.id.confirm_exit);
			cancel=(Button) v.findViewById(R.id.cancel_exit);
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
			guanbi.setOnClickListener(this);
			confim.setOnClickListener(this);
			cancel.setOnClickListener(this);
			imageView1.setOnClickListener(this);
			imageView2.setOnClickListener(this);
			imageView3.setOnClickListener(this);
			
		}
		
		return v;
	}
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
			case UPDATA_IMAGE:
				
				//随机三张
				if(jsonList.size()==0)
		    	{
		    		exit_recommend.setVisibility(View.GONE);
		    		exit_recommend_text.setVisibility(View.GONE);
		    		UIUtils.showToast(MainActivity.this,"网络不给力啊亲，请检查网络状态");
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
			case 2:
			     break;
			}
			
		};
		
	};
    private String recommendUrl,bid;
    private boolean isClose=false;
    private List<String> jsonList;
    private List<String> jsonList2;
    private List<String> clickList;
    private List<String> nameList;
    private List<String> bidList;
    private String  imageUrl;
    private ImageLoader imageLoader;
	private void initData() {
		recommendUrl = AppData.getConfig().getUrl(
				Config.URL_BOOK_RECOMMAND);
		// 加载推荐
        
		bid = getIntent().getStringExtra("bid");
		jsonList=new ArrayList<String>();
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
        DetailTask task = new DetailTask("detail"+bid);
        AppData.getClient().getTaskManagerRead().addTask(task);
        
        
        
		
		
		
	}

	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppData.clear();
        isClose=true;
        if(CommonUtil.isServiceRunning(this, XyServices.BOYISERVICE_NAME)) {
        	XyServices.stopBoyiService(MainActivity.this);
        }
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		f1.setMode(false);
		MobclickAgent.onResume(this);
		if(openUserCenter)
		{
			slidingMenu.toggle();
		}
		
		
		
		
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
       
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.guanbi:
			exitPopupWindow.dismiss();
		     break;
		case R.id.confirm_exit:
			//保存退出时间,写入AndroidManifest.xml文件中
			AppData.saveLastTime();
			AppData.setShowRecommend(true);
			System.exit(0);
			finish();
			break;
		case R.id.cancel_exit:
			exitPopupWindow.dismiss();
			break;
		case R.id.imageview1:
			String strLeft=jsonList.get(images[0]);
        	final String bid3=strLeft.substring(strLeft.lastIndexOf("/")+1,strLeft.lastIndexOf("?")); 
			Intent intent=new Intent(MainActivity.this, BookDetail.class);
			intent.putExtra("bid", bid3);
			startActivity(intent);
			break;
		case R.id.imageview2:
			String str=jsonList.get(images[1]);
	    	final String bid1=str.substring(str.lastIndexOf("/")+1,str.lastIndexOf("?"));  
			Intent intent1=new Intent(MainActivity.this, BookDetail.class);
			intent1.putExtra("bid", bid1);
			startActivity(intent1);
			break;
		case R.id.imageview3:
			String strRight=jsonList.get(images[2]);
        	final String bid2=strRight.substring(strRight.lastIndexOf("/")+1,strRight.lastIndexOf("?"));
			Intent intent3=new Intent(MainActivity.this, BookDetail.class);
			intent3.putExtra("bid", bid2);
			startActivity(intent3);
		   break;		
		
		}
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
				mHandler.sendMessage(msg); // 向Handler发送消息,更新UI			
			}
		}
	}

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
			e.printStackTrace();
		}

		return imageUrl;

	}
	
}
