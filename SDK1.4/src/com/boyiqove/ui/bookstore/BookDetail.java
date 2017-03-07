package com.boyiqove.ui.bookstore;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.Inflater;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.boyiqove.config.Config;
import com.boyiqove.config.DeviceInfo;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.entity.PageID;
import com.boyiqove.library.volley.RequestQueue;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.toolbox.ImageLoader;
import com.boyiqove.library.volley.toolbox.NetworkImageView;
import com.boyiqove.protocol.JsonObjectPostRequest;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.task.CallBackTask;
import com.boyiqove.ui.bookqove.MoreDirectory;
import com.boyiqove.ui.bookshelf.BookshelfUtil.GetTaskItem;
import com.boyiqove.ui.bookshelf.OnlineContentsActivity;
import com.boyiqove.ui.bookshelf.OnlineReadingActivity;
import com.boyiqove.ui.storeadapter.ContentAdapter;
import com.boyiqove.ui.storeadapter.ScrollListView;
import com.boyiqove.ui.storeutil.JsonUtil;
import com.boyiqove.ui.storeutil.MyFlowLayout;
import com.boyiqove.util.CommonUtil;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.DisplayUtil;
import com.boyiqove.util.GetBookDetailUtil;
import com.boyiqove.util.GetDirectoryUtil;
import com.boyiqove.util.HttpRequestUtil;
import com.boyiqove.view.BaseActivity;
import com.boyiqove.AppData;
import com.boyiqove.R;
import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.data.Comment;
import com.bytetech1.sdk.data.CommentItem;
import com.bytetech1.sdk.data.Detail;
import com.bytetech1.sdk.data.Directory;
import com.bytetech1.sdk.data.DirectoryItem;

public class BookDetail extends BaseActivity {	
	private final static int DETAIL_REQUSET = 1;
	private final static  int keybook_result=2;
	private TextView bookName,actorName,sectionNum,wordNum,clickTv,updataNum,intorDesc,updataTime,noReadNum; 
	private  Button  	readBookBt,downloadBt;//shelfButton;
	private LinearLayout backButton;

	private ScrollListView 	mlContent,plContent;
	private NetworkImageView  networkImageView ,bookCoverLeft,otherbookCoverCenter,otherbookCoverRight;
	private LinearLayout  recommentContent;
	private  RelativeLayout	abstructRl ,directoryAll,pinglunAll;
	private ImageView  bookStata;
	private TextView  lastButton,newPosTv;
	private RequestQueue queue;
	private final int UPDATA_IMAGE=1;
	private final int UPDATA_INFO=2;
	// 推荐
	private 	String recommendUrl;
	private TextView  directoryMore;
	private boolean isColse=false;
	private boolean isRun=true;
	private String channel;
	private ImageView refrenIv;
	private TextView refrenTv;
	private TextView likeThisTv;
	private Boolean onClick=false;
	private LinearLayout bodyLayout;
	private  String mChannel;
	private  int mOper;
	private View listTop;
	private ScrollView  parentScrollview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boyi_store_bookdetail);
		channel=AppData.readMetaDataFromService(this,"channel_num");
		initView();
		initData();		
//		initOnclick();
	}

	private void initView() {
		// TODO Auto-generated method stub
		top=(TextView) findViewById(R.id.top);
		bodyLayout=(LinearLayout) findViewById(R.id.detail_layout);
		parentScrollview=(ScrollView) findViewById(R.id.pare_scrollview);
		queue =getRequestQueue();
		jsonList=new ArrayList<String>();
		
		abstructRl=(RelativeLayout) findViewById(R.id.Relative_window);
		//search=(ImageView) findViewById(R.id.search);
		//menu=(RelativeLayout) findViewById(R.id.boyi_book);
//		backButton=(ImageView) findViewById(R.id.search_back);
		//shelfButton=(Button) findViewById(R.id.boyi_book_shelf);
		//shelfButton.setVisibility(View.GONE)	;
//		abstructBt=(TextView) findViewById(R.id.button_content);
//		lastButton=abstructBt;
//		bookStata=(TextView) findViewById(R.id.book_state);		
		likeThisTv=(TextView) findViewById(R.id.detail_like_tv);
		
		likeThisTv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!onClick) {
					onClick=true;
					String str=Integer.parseInt(likeThisTv.getText().toString())+1+"";
					likeThisTv.setText(str);
					likeThisTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.detail_click_like, 0, 0, 0);
				}else {
					showToast("已经喜欢过了", Toast.LENGTH_SHORT);
				}
			}
		});
		
		backButton=(LinearLayout) findViewById(R.id.boe_back_bt);
		bookStata=(ImageView) findViewById(R.id.book_state);		
		recommentContent=(LinearLayout) findViewById(R.id.recommend_bottom);		
		bookName=(TextView) findViewById(R.id.bookname_tv);
		actorName=(TextView) findViewById(R.id.actor_name);
		sectionNum=(TextView) findViewById(R.id.section_num);
		wordNum=(TextView) findViewById(R.id.word_num);
		clickTv=(TextView) findViewById(R.id.click_num);
		updataNum=(TextView) findViewById(R.id.updata_num);
		intorDesc=(TextView) findViewById(R.id.intor_desc);
		updataTime=(TextView) findViewById(R.id.updata_time);
		noReadNum=(TextView) findViewById(R.id.no_read_copter);
		
		readBookBt=(Button) findViewById(R.id.read_bt);
		downloadBt=(Button) findViewById(R.id.read_download);
		
		networkImageView=(NetworkImageView) findViewById(R.id.netimage_cover);
		networkImageView.setErrorImageResId(R.drawable.boyi_ic_cover_default);
		networkImageView.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
		
		otherbookCoverCenter=(NetworkImageView) findViewById(R.id.otherbook_cover_conter);
		 
		otherbookCoverRight=(NetworkImageView) findViewById(R.id.otherbook_cover_rlght);
	
		bookCoverLeft=(NetworkImageView) findViewById(R.id.otherbook_cover_left);

		mlContent=(ScrollListView) findViewById(R.id.content_lv);
		listTop=View.inflate(BookDetail.this, R.layout.boyi_directory_item, null);
		newPosTv=(TextView) listTop.findViewById(R.id.direction_tv2);
		mlContent.addHeaderView(listTop);
		plContent=(ScrollListView) findViewById(R.id.content_PL);
		
		directoryAll=(RelativeLayout) findViewById(R.id.directory_all);		
		
		pinglunAll=(RelativeLayout) findViewById(R.id.pinglun_more_all);	
		
		refrenIv=(ImageView) findViewById(R.id.detail_refresh_comiv);	
		refrenTv=(TextView) findViewById(R.id.detail_recom_bottom);	
		
		TextView title1=(TextView) findViewById(R.id.detail_note1);
		TextView title2=(TextView) findViewById(R.id.detail_note2);
		TextView title3=(TextView) findViewById(R.id.detail_note3);
		TextView title4=(TextView) findViewById(R.id.detail_note4);
		int noteSize= (int)this.getResources().getDimension(R.dimen.detail_title_tv);
		noteSize=DisplayUtil.px2sp(this, noteSize);
		title1.setTextSize(noteSize);
		title2.setTextSize(noteSize);
		title3.setTextSize(noteSize);
		title4.setTextSize(noteSize);
		
	}
	private ImageLoader imageLoader;
	private  String bid;
	private ContentAdapter adapterPl;
	private ContentAdapter adapterMl;
	private Boolean isMl;
	private List<CommentItem> list;
	private List<DirectoryItem>mItem;
	private Map<String , Boolean>map;
	private Detail detail ;
	private String urlImage;
	private refrenTask refrenTask;
	private int []images;
	private List<OnlineChapterInfo>mlList;
	/**
	 * 更新view
	 * */
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what) { 
			
            case UPDATA_INFO:  
           	
            	updataNum.setText(item.lastUpdata);
				bookName.setText(item.name);
				actorName.setText(item.author);
				sectionNum.setText(item.totalCount+"");
				wordNum.setText(item.wordNum);
				clickTv.setText(item.clickStr);
				if (item.status==1) {
					
					bookStata.setBackgroundResource(R.drawable.boy_book_over);
				}else {
					bookStata.setBackgroundResource(R.drawable.boy_book_serial);
				}

				intorDesc.setText(item.shortDesc);	// 长简介
				
				urlImage=item.bigCoverUrl;   
				networkImageView.setImageUrl(urlImage, imageLoader);	  
//				String time=item.lastUpdata;
				SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lastHour = time.format(Long.parseLong(item.lastDate+"000")).substring(5, 10);
				updataTime.setText(lastHour+" 更新");
				newPosTv.setText("【最新章节:"+item.lastTitle+"】");
				if (isContentsLoaded()) {					
					int a=mlList.size()-item.lastChapterPos;
					readBookBt.setBackgroundResource(R.drawable.boyi_read_gotobt);
					readBookBt.setTextColor(getResources().getColor(R.color.boyi_white));
					readBookBt.setText(getResources().getString(R.string.boyi_goto_reading));
					noReadNum.setText(a+"章未读");
					newPosTv.setVisibility(View.VISIBLE);
				}else {
					newPosTv.setText("【最新章节:"+item.lastTitle+"】");
					newPosTv.setVisibility(View.VISIBLE);
					noReadNum.setText("未阅读");
				}
				mlContent.setAdapter(adapterMl);	
				hideProgress();
				initOnclick();
				return;
            case UPDATA_IMAGE:
            	// 更新三张图片推荐
            	DebugLog.e("更新三张推荐", "推荐数是"+jsonList.size());
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
//            	final String bid1=str.substring(str.lastIndexOf("/")+1);  
            	bookCoverLeft.setErrorImageResId(R.drawable.boyi_ic_cover_default);
            	bookCoverLeft.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
            	bookCoverLeft.setImageUrl(addUrl(str.substring(0,str.lastIndexOf("/"))), imageLoader);  
    	
            	String strCenter=jsonList.get(images[1]);
//            	final String bid2=strCenter.substring(strCenter.lastIndexOf("/")+1);   
            	otherbookCoverCenter.setErrorImageResId(R.drawable.boyi_ic_cover_default);
            	otherbookCoverCenter.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
            	otherbookCoverCenter.setImageUrl(addUrl(strCenter.substring(0,strCenter.lastIndexOf("/"))), imageLoader);  
            	
            	String strRight=jsonList.get(images[2]);
//            	final String bid3=strRight.substring(strRight.lastIndexOf("/")+1); 
            	otherbookCoverRight.setErrorImageResId(R.drawable.boyi_ic_cover_default);
            	otherbookCoverRight.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
            	otherbookCoverRight.setImageUrl(addUrl(strRight.substring(0,strRight.lastIndexOf("/"))), imageLoader);  
            	if (refrenIv!=null) {					
            		refrenIv.clearAnimation();
				}
            	return;
            }  
			
		};
	};	
	private BookItem item;
	private Thread thread1,thread2;
	private void initData() {
		// TODO Auto-generated method stub
		int titleSize= (int)this.getResources().getDimension(R.dimen.bookdetail_title);
		titleSize=DisplayUtil.px2sp(this, titleSize);
		int detailSize= (int)this.getResources().getDimension(R.dimen.imageview_margin_others);
		detailSize=DisplayUtil.px2sp(this, detailSize);
		bookName.setTextSize(titleSize);
		actorName.setTextSize(detailSize);
		sectionNum.setTextSize(detailSize);
		wordNum.setTextSize(detailSize);
		
		TextView textView1=(TextView) findViewById(R.id.actor_tv);
		TextView textView2=(TextView) findViewById(R.id.section_tv);
		TextView textView3=(TextView) findViewById(R.id.word_tv);
		TextView textView4=(TextView) findViewById(R.id.book_come_by);
		textView1.setTextSize(detailSize);
		textView2.setTextSize(detailSize);
		textView3.setTextSize(detailSize);
		textView4.setTextSize(detailSize);
		
		recommendUrl=AppData.getConfig().getUrl(Config.URL_BOOK_RECOMMAND);
		// 加载推荐
//		startImage();
		
//		imageLoader=new ImageLoader(queue, ImageCacheManager.getInstance(this));
		imageLoader=getImageLoader();
		
		bid =getIntent().getStringExtra("bid");
		if (CommonUtil.isNetworkConnected(BookDetail.this)) {
			
			DetailTask task = new DetailTask("detail"+ bid);	
			
			AppData.getClient().getTaskManagerRead().addTask(task);
			showProgressCancel(task.getTaskName(), "", "加载中");
//			showProgress("", "玩命加载中...");
		}else {
			showToast("网络不给力啊亲，请检查网络状态", Toast.LENGTH_LONG);
		}
//		if (AppData.getDataHelper().foundBookBid(bid)){
////			if (isContentsLoaded()){
//			readBookBt.setBackgroundResource(R.drawable.boyi_read_gotobt);
//			readBookBt.setTextColor(getResources().getColor(R.color.boyi_white));
//			readBookBt.setText(getResources().getString(R.string.boyi_goto_reading));
//		}
	}
	
	
	private List<String >jsonList;
	private List<String >jsonList2;
	private String  imageUrl;	

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
		
		return imageUrl;
				
	}
	
	private void initOnclick() {
		// TODO Auto-generated method stub	
		refrenTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Animation operatingAnim = AnimationUtils.loadAnimation(BookDetail.this,
						R.anim.tip);
				LinearInterpolator lin = new LinearInterpolator();
				operatingAnim.setInterpolator(lin);
				refrenIv.startAnimation(operatingAnim);
				if (refrenTask!=null) {
					AppData.getClient().getTaskManagerRead().delTask(refrenTask.getTaskName());
					refrenTask = new refrenTask("refren"
							+ bid);	
				}else {
					refrenTask = new refrenTask("refren"
							+ bid);	
				}
				AppData.getClient().getTaskManagerRead().addTask(refrenTask);
				
			}
		});
		
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
			}
		});
//		menu.setOnClickListener(new OnClickListener() {
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
//			Intent intent=new Intent(BookDetail.this,LocalSearchActivity.class);
//			startActivity(intent);
//			overridePendingTransition(R.anim.boyi_move_right_in, R.anim.boyi_move_right_out);
//		}
//	});
		

		readBookBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GetBookDetailUtil.startReadingBook(item.bid, item.bigCoverUrl, BookDetail.this, false, 0);
//				AppData.startBookReading(BookDetail.this,item.bid, item.bigCoverUrl,false);
			}
		});
		downloadBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (item!=null) {
					
					if (item.lastChapterPos>=item.freeCount) {
						popupBuyWindow(item.lastChapterPos+1);
					}else if (item.lastChapterPos==item.totalCount) {
						showToast("无可购买章节", Toast.LENGTH_SHORT);
					}
					else {
						popupBuyWindow(item.freeCount);						
					}
				}
			}
		});
		
		

		otherbookCoverCenter.setOnClickListener(new OnClickListener() {       			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str=jsonList.get(images[1]);
		    	final String bid1=str.substring(str.lastIndexOf("/")+1,str.lastIndexOf("?"));  
				Intent intent=new Intent(BookDetail.this, BookDetail.class);
				intent.putExtra("bid", bid1);
				startActivity(intent);
			}
		});  
		
		otherbookCoverRight.setOnClickListener(new OnClickListener() {       			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strRight=jsonList.get(images[2]);
            	final String bid2=strRight.substring(strRight.lastIndexOf("/")+1,strRight.lastIndexOf("?"));
				Intent intent=new Intent(BookDetail.this, BookDetail.class);
				intent.putExtra("bid", bid2);
				startActivity(intent);
			}
		});
		

		bookCoverLeft.setOnClickListener(new OnClickListener() {       			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				String strLeft=jsonList.get(images[0]);
            	final String bid3=strLeft.substring(strLeft.lastIndexOf("/")+1,strLeft.lastIndexOf("?")); 
				Intent intent=new Intent(BookDetail.this, BookDetail.class);
				intent.putExtra("bid", bid3);
				startActivity(intent);
			}
		});	

	// 目录点击，进入阅读
	
	mlContent.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(BookDetail.this, OnlineReadingActivity.class);
			if (TextUtils.isEmpty(item.bid)) {
				return;
			}
			if (position==0) {
				item.lastChapterPos=item.totalCount-1;
			}else {				
				item.lastChapterPos = position-1;
			}
            intent.putExtra("BookItem", item);
            startActivityForResult(intent, PageID.Bookshelf);
		}
	});
	
	directoryAll.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		//开启目录页
			if (AppData.getDataHelper().foundBookBid(bid)) {					
				item=AppData.getDataHelper().getBookItem(Integer.parseInt(bid));
				
			}
			Intent intent=new Intent(BookDetail.this, OnlineContentsActivity.class);
			intent.putExtra("BookItem", item);
			intent.putExtra("comeDetail", true);
			startActivity(intent);
		}
	});
	
	pinglunAll.setOnClickListener(new OnClickListener() {			
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			打开评论页
			Intent intent=new Intent(BookDetail.this, BookDetailpinglun.class);
			intent.putExtra("bid", bid);
			intent.putExtra("word", "更多评论");
			startActivity(intent);
			
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case keybook_result:
			
			showToast("没有找到书籍内容", Toast.LENGTH_LONG);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		hideProgress();
		finish();
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		 if (AppData.getDataHelper().foundBookBid(bid)) {
				item=AppData.getDataHelper().getBookItem(Integer.parseInt(bid));
				noReadNum.setText(item.totalCount-item.lastChapterPos+"章未读");
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		bid =getIntent().getStringExtra("bid");
		parentScrollview.scrollTo(10, 10); 
		initData();
	}
	
	public static int dip2px(Context context, float dpValue) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (dpValue * scale + 0.5f);
    } 

	private ArrayList<OnlineChapterInfo> mXNContentsList = new ArrayList<OnlineChapterInfo>();
	public class refrenTask extends CallBackTask{

		public refrenTask(String strTaskName) {
			super(strTaskName);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void doTask() {
			// TODO Auto-generated method stub
			jsonList.clear();
			jsonList=AppData.getTuijianList();
//			 设置3个封面
			if (! isColse && jsonList.size()>0) {
				Message msg = new Message();     
				msg.what = UPDATA_IMAGE;
				mHandler.sendMessage(msg); // 向Handler发送消息,更新UI			
			}
		}
		
	}
	public class DetailTask extends CallBackTask{

		public DetailTask(String strTaskName) {
			super(strTaskName);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void doTask() {
			// TODO Auto-generated method stub
		jsonList=AppData.getTuijianList();
//		 设置3个封面
		if (! isColse && jsonList.size()>0) {
			Message msg = new Message();     
			msg.what = UPDATA_IMAGE;
			mHandler.sendMessage(msg); // 向Handler发送消息,更新UI			
		}

//===========================================			
				// TODO Auto-generated method stub
				String yysid="1";
				item = new BookItem();
				newPosTv.setVisibility(View.VISIBLE);
		   if (AppData.getDataHelper().foundBookBid(bid)) {
			   
				item=AppData.getDataHelper().getBookItem(Integer.parseInt(bid));

			}else {		
				item=GetBookDetailUtil.getNetBookItem(BookDetail.this, bid);
//				mChannel=AppData.readMetaDataFromService(BookDetail.this, "channel_num");
//				mOper = AppData.getConfig().getDeviveInfo().getOperator(BookDetail.this);
//				if (TextUtils.isEmpty(mChannel)) {
//					mChannel = "default";
//				}
//
//				switch (mOper) {
//				case DeviceInfo.OPERATOR_CM:
//					yysid="1";
//					break;
//				case DeviceInfo.OPERATOR_CU:
//					yysid="2";
//					break;
//				case DeviceInfo.OPERATOR_TC:
//					yysid="3";
//					break;
//
//				default:
//
//					break;
//				}
//				
//				
//				Map<String, String> map = new HashMap<String, String>();
//				map.put("aid", bid);
//				map.put("qdid", "" + mChannel);
//				map.put("yysid", "" + yysid);
//
//				String detailUrl = AppData.getConfig().getUrl(
//						Config.URL_DETAIL_BOOKITEM);
//				String responseStr=HttpRequestUtil.post(detailUrl, map);
//				try {
//					JSONObject responJson=new JSONObject(responseStr);
//					int status = responJson
//							.getInt("status");
//					if (status == StatusCode.OK) {
//						JSONObject jsonObject=responJson.getJSONObject("data");
//							item.bid = jsonObject.getString("bid");
//							item.cid = jsonObject.getString("cid");
//							item.name = jsonObject.getString("name");
//							item.author = jsonObject.getString("author");
//							item.status = jsonObject.getInt("status");
//							item.wordNum = jsonObject.getString("word_num");
//							item.shortDesc = jsonObject.getString("introduction");
//							item.longDesc = jsonObject.getString("long_introduction");
//							item.littleCoverUrl = jsonObject.getString("cover_url");
//							item.bigCoverUrl = jsonObject.getString("cover_url");
//							item.classFication = jsonObject.getString("class_name");
//							item.clickStr = jsonObject.getString("click_num");
//							item.freeCount = jsonObject.getInt("freenum");
//							item.totalCount = jsonObject.getInt("totalnum");
//							item.lastUpdata= jsonObject.getString("date");
//							item.lastCid=jsonObject.getString("last_cid");
//							item.lastTitle=jsonObject.getString("last_name");	
//							
////							if (! isColse) {
////								Message msg = new Message();     
////								msg.what = UPDATA_INFO;
////								mHandler.sendMessage(msg); // 向Handler发送消息,更新UI
////								}
//					}
//				} catch (JSONException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				}
		   
		   if (isContentsLoaded()) {					
				mlList=AppData.getContentHelper(Integer.parseInt(bid)).getChapterList();
			}else {
				mlList=GetDirectoryUtil.getDirectoryList(bid, 0, item.freeCount+3);
			}
		   if(mlList.size()>0)
		   {
			   adapterMl=new ContentAdapter(BookDetail.this, mlList.subList(0, 3), true);
		   }	

			if (! isColse) {
				Message msg = new Message();     
				msg.what = UPDATA_INFO;
				mHandler.sendMessage(msg); // 向Handler发送消息,更新UI
				}
//=========================================================	
				
//				mItem=new ArrayList<DirectoryItem>();
//				for (int i = 1; i < 4; i++) {
//					Directory directory=BookHelper.loadDir(bid, i, 10, true);
//					mItem.addAll(directory.getList());
//				}
//				AppData.getContentHelper(Integer.parseInt(bid)).insertChapterList(
//						listDire);
				/**
				 * 	获取评论
				 * */
				Comment comment =BookHelper.loadComment(bid,1);
				
						if(comment!=null){
							list=comment.getList();
							if (list.size()>3) {
								
								list=comment.getList().subList(0, 2);	
							}
						    adapterPl=new ContentAdapter(list, BookDetail.this,false);
						}
						if (! isColse) {
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								
//								mlContent.setAdapter(adapterMl);								
								plContent.setAdapter(adapterPl);		
							}
						});
						}
		}
	}	

	// 批量购买下载接口
		private PopupWindow popWindowBuy = null;
		private void popupBuyWindow(int pos) {
			int a=pos;
			DebugLog.e("阅读到的章节：", pos+"");
			View v = getBuyView(pos);
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
			popWindowBuy.showAtLocation(bodyLayout, Gravity.BOTTOM, 0, 0);
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
	    private int count;
	   private String name ;
	    private View getBuyView(int pos) {
	    	
	    	if (pos>=mlList.size()) {
				showToast("没有可购买章节哦亲", Toast.LENGTH_SHORT);
				return null;
			}
	    	name =mlList.get(pos).name;
			
	    	if (null == buyView) {

	    		buyView=LayoutInflater.from(BookDetail.this).inflate(R.layout.boy_reading_online,null);
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
	    	tvName=(TextView) buyView.findViewById(R.id.buy_number);
	    	tvName.setText("您将从"+name+" 开始购买.");
	    	// 购买后10章
	    	layout10.setOnClickListener(new OnClickListener() {

	    		@Override
	    		public void onClick(View v) {
	    			// TODO Auto-generated method stub
//	    			if (item.lastChapterPos<item.freeCount) {
////	    				count=item.freeCount+10;
//	    				count=10;
//					}else{
////						count=item.lastChapterPos+10;
//						count=item.lastChapterPos+10;
//					}
	    			goReadingDown(11); //加1是结束的章节是下载到哪一张，
	    			hideBuyWindow();
	    		}
	    	});

	    	// 购买后20章
	    	layout20.setOnClickListener(new OnClickListener() {

	    		@Override
	    		public void onClick(View v) {
	    			// TODO Auto-generated method stub
//	    			if (item.lastChapterPos<item.freeCount) {
//	    				count=item.freeCount+20;
//					}else{
//						count=item.lastChapterPos+20;
//					}
	    			goReadingDown(21);
	    			hideBuyWindow();
	    		}
	    	});
	    	// 购买后100章
	    	layout100.setOnClickListener(new OnClickListener() {

	    		@Override
	    		public void onClick(View v) {
	    			// TODO Auto-generated method stub
//	    			if (item.lastChapterPos<item.freeCount) {
//	    				count=item.freeCount+100;
//					}else{
//						count=item.lastChapterPos+100;
//					}
	    			goReadingDown(101);
	    			hideBuyWindow();
	    		}
	    	});
	    	// 购买后所有
	    	layoutAll.setOnClickListener(new OnClickListener() {

	    		@Override
	    		public void onClick(View v) {
	    			// TODO Auto-generated method stub
//	    			goReadingDown(mItem.size());
	    			goReadingDown(150); // 传一个大于100的数进去那边就能够根据该数来选择下载多少章
	    			hideBuyWindow();
	    		}
	    	});
	    	return buyView;
	    }
	public void goReadingDown(int num){
		
		if (AppData.getDataHelper().foundBookBid(bid)) {					
			item=AppData.getDataHelper().getBookItem(Integer.parseInt(bid));
			Intent intent = new Intent(BookDetail.this,
					OnlineReadingActivity.class);
			intent.putExtra("BookItem", item);
			intent.putExtra("buynum", num);
			startActivity(intent);
		}else {			
			GetBookDetailUtil.startReadingBook(bid,item.bigCoverUrl,this,false,num);
		}
	}
	
	public boolean isNetworkConnected(Context context) {  
		    if (context != null) {  
		          ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
		                 .getSystemService(Context.CONNECTIVITY_SERVICE);  
		        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
		         if (mNetworkInfo != null) {  
		             return mNetworkInfo.isAvailable();  
		         }  
		     }  
		     return false;  
		 }  
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isColse=true;
	}
	
	private boolean isContentsLoaded() { // 判断移动目录是否存在
		
		  File f = new File(AppData.getConfig().getContentDBName(
				Integer.parseInt(bid)));
		  return f.exists();
		
		
	}
	private boolean isXNContentsLoaded() { // 是否有血凝的目录数据库
		File f = new File(AppData.getConfig().getXNContentDBName(
				Integer.parseInt(item.bid)));
		return f.exists();
	}
}
