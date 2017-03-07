package com.boyiqove.ui.bookstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.config.Config;
import com.boyiqove.entity.BookSearchItem;
import com.boyiqove.library.volley.Request.Method;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.toolbox.NetworkImageView;
import com.boyiqove.library.volley.toolbox.StringRequest;
import com.boyiqove.protocol.JsonObjectPostRequest;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.task.Task;
import com.boyiqove.ui.storeadapter.ScrollListView;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.DisplayUtil;
import com.boyiqove.view.BaseActivity;
import com.boyiqove.view.FlowLayout;
import com.bytetech1.sdk.data.cmread.Search;

public class LocalSearchActivity extends BaseActivity{
	private ImageView back,iv_keyword,hotsearch_jiazai_imageView,hotkeyword_jiazai_imageView;
	private RelativeLayout menu_rl,search_rl,hotsearch_jiazai,hotkeyword_jiazai;
	private LinearLayout recomment_ll,search_ll,sousuokuang_ll,enter_user_center_ll,hotkeyword_ll,hotsearch_ll,grayview_ll;
	private TextView searchSpinner,search_result_size;
	private ScrollListView searchBookListView;
	private FlowLayout search_flowlayout;
	private List<BookSearchItem> list;
	private List<String> keyWord1,keyWord2,keyWord3,temp;
	private List<BookSearchItem> listSerchResult,recommentList_1,recommentList_2,recommentList_3,tempList;
	private ListView recomment_listView,search_listView;
	private EditText et_keyword;
	private int searchByname,searByAuthor,searchKeyWord;
	private int color[]={R.color.red,R.color.blue,R.color.pink,R.color.zhi,R.color.yellow};
	private static final String TAG="LocalSearchActivity";
	private static final int REQUEST_SUSSESS=1;
	private static final int REQUEST_FAIL=0;
	private static final int REQUEST_NOT_FOUND=2;
	private static final int REQUEST_NOT_FOUND_MORE=7;
	private static final int REQUEST_RECOMMEND_SUSSESS=3;
	private static final int REQUEST_RECOMMEND_FAIL=4;
	private static final int REQUEST_HOT_SUSSESS=5;
	private static final int REQUEST_HOT_FAIL=6;
	private BoyiSearchAdapter adapter1,adapter2;
	private int index=1;
	private SearchTask task;
	private LinearLayout v;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			//发送到主线程更新UI
			switch(msg.what)
			{
			case LocalSearchActivity.REQUEST_HOT_SUSSESS:
				switch(getSearchType())
				{
				case Search.TYPE_NAME:
					temp=keyWord1;
					break;
				case Search.TYPE_AUTHOR:
					temp=keyWord2;
					break;
				case Search.TYPE_KEYWORD:
					temp=keyWord3;
					break;
				}
				
				initFlowLayout(temp);
				
				showHotView(false);
				break;
				
			case LocalSearchActivity.REQUEST_HOT_FAIL:
				hotkeyword_ll.setVisibility(View.GONE);
				break;
			case LocalSearchActivity.REQUEST_RECOMMEND_SUSSESS:
				adapter2=new BoyiSearchAdapter(list);
				recomment_listView.setAdapter(adapter2);
				adapter2.notifyDataSetChanged();
				showSearchView(false);
				break;
			case LocalSearchActivity.REQUEST_RECOMMEND_FAIL:
				//未找到热搜书籍的推荐
				hotsearch_ll.setVisibility(View.GONE);
				grayview_ll.setVisibility(View.GONE);
				break;
			case LocalSearchActivity.REQUEST_SUSSESS:
				adapter1.notifyDataSetChanged();
				break;
			case LocalSearchActivity.REQUEST_NOT_FOUND:
				if(listSerchResult.size()==0)
				{
				  showToast("未找到该相关内容的书籍",Toast.LENGTH_LONG);
				}
				break;
			case LocalSearchActivity.REQUEST_NOT_FOUND_MORE:
				if(listSerchResult.size()>0)
				{
					showToast("没有更多内容",Toast.LENGTH_LONG);
				}
				break;
			case LocalSearchActivity.REQUEST_FAIL:
				showToast("网络异常，请稍候搜索...",Toast.LENGTH_LONG);
				break;
			}
			
			if(msg.what==REQUEST_SUSSESS ||msg.what==REQUEST_NOT_FOUND_MORE ){
				recomment_ll.setVisibility(View.GONE);
				search_ll.setVisibility(View.VISIBLE);
				search_listView.setVisibility(View.GONE);
				search_listView.setVisibility(View.VISIBLE);
			}else if(msg.what==REQUEST_RECOMMEND_FAIL)
			{
				showSearchView(false);
			}else if(msg.what==REQUEST_HOT_SUSSESS || msg.what==REQUEST_HOT_FAIL)
			{
				RecommendTask task2 = new RecommendTask("");
				AppData.getClient().getTaskManagerRead().addTask(task2);
			}
			else{
				recomment_ll.setVisibility(View.VISIBLE);
				search_ll.setVisibility(View.GONE);
			}
			
			hideProgress();
			v.setVisibility(View.GONE);
			
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boyi_search);
		initView();
		initData();
		
	}

	private void initData() {
		searchByname = Search.TYPE_NAME;
		searByAuthor = Search.TYPE_AUTHOR;
		searchKeyWord = Search.TYPE_KEYWORD;
		keyWord1=new ArrayList<String>();
		keyWord2=new ArrayList<String>();
		keyWord3=new ArrayList<String>();
		list=new ArrayList<BookSearchItem>();
		listSerchResult=new ArrayList<BookSearchItem>();
		recommentList_1=new ArrayList<BookSearchItem>();
		recommentList_2=new ArrayList<BookSearchItem>();
		recommentList_3=new ArrayList<BookSearchItem>();
		tempList=new ArrayList<BookSearchItem>();
		search_listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		adapter1=new BoyiSearchAdapter(listSerchResult);
		search_listView.setAdapter(adapter1);
		initRecommendData();
		list=recommentList_1;
		
		
		
	}

	private void initRecommendData() {
		//初始化热词推荐
		HotKeyTask task1=new HotKeyTask("");
		AppData.getClient().getTaskManagerRead().addTask(task1);
		showHotView(true);
		
		showSearchView(true);
		
		
		
		
	}

	
	private void showHotView(boolean isHotting) {
		if(isHotting)
		{
		 search_flowlayout.setVisibility(View.GONE);
		 hotkeyword_jiazai.setVisibility(View.VISIBLE);
		 cicleImageView(hotkeyword_jiazai_imageView);
		}else
		{
			 search_flowlayout.setVisibility(View.VISIBLE);
			 hotkeyword_jiazai.setVisibility(View.GONE);
			 hotkeyword_jiazai_imageView.clearAnimation();
		}
	}

	private void showSearchView(boolean isSearching) {
		if(isSearching)
		{
		recomment_listView.setVisibility(View.GONE);
		hotsearch_jiazai.setVisibility(View.VISIBLE);
		cicleImageView(hotsearch_jiazai_imageView);
		}else
		{
			recomment_listView.setVisibility(View.VISIBLE);
			hotsearch_jiazai.setVisibility(View.GONE);
			hotsearch_jiazai_imageView.clearAnimation();
		}
	} 

	private void cicleImageView(ImageView view)
	{
		Animation operatingAnim = AnimationUtils.loadAnimation(this,
				R.anim.tip);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		view.startAnimation(operatingAnim); 
	}

	private void initView() {
		hotkeyword_jiazai=(RelativeLayout) findViewById(R.id.hotkeyword_jiazai);
		hotsearch_jiazai=(RelativeLayout) findViewById(R.id.hotsearch_jiazai);
		hotkeyword_jiazai_imageView=(ImageView) findViewById(R.id.hotkeyword_jiazai_imageView);
		hotsearch_jiazai_imageView=(ImageView) findViewById(R.id.hotsearch_jiazai_imageView);
		v = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.boyi_listview_fooer, null);
		search_result_size=(TextView) findViewById(R.id.search_result_size);
		recomment_ll=(LinearLayout) findViewById(R.id.recomment_ll);
		search_ll=(LinearLayout) findViewById(R.id.search_ll);
		hotkeyword_ll=(LinearLayout) findViewById(R.id.hotkeyword_ll);
		hotsearch_ll=(LinearLayout) findViewById(R.id.hotsearch_ll);
		sousuokuang_ll=(LinearLayout) findViewById(R.id.sousuokuang_ll);
		recomment_listView=(ListView) findViewById(R.id.recomment_listview);
		search_listView=(ListView) findViewById(R.id.search_listview);
		grayview_ll=(LinearLayout) findViewById(R.id.grayview_ll);
		et_keyword=(EditText) findViewById(R.id.et_keyword);
		et_keyword.setHintTextColor(Color.parseColor("#d9d8d8"));
		et_keyword.setHint("请输入搜索内容");
		top=(TextView) findViewById(R.id.top);
		findViewById(R.id.search).setVisibility(View.GONE);
		search_flowlayout=(FlowLayout) findViewById(R.id.search_flowlayout);
		
		iv_keyword=(ImageView) findViewById(R.id.iv_keyword);
		back=(ImageView) findViewById(R.id.search_back);
		menu_rl=(RelativeLayout) findViewById(R.id.boyi_book);
		search_rl=(RelativeLayout) findViewById(R.id.search_rl);
        TextView title=(TextView) findViewById(R.id.search_top_title_tv);
        title.setText("搜索");
		searchBookListView = (ScrollListView)findViewById(R.id.search_list);
		v.setVisibility(View.GONE);
		ImageView imageView = (ImageView)v.findViewById(R.id.progressBar1);
		Animation operatingAnim = AnimationUtils.loadAnimation(LocalSearchActivity.this,
					R.anim.tip);
		imageView.startAnimation(operatingAnim);
		search_listView.addFooterView(v, null, false);
		search_listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(OnScrollListener.SCROLL_STATE_IDLE==scrollState)
				{
					if(view.getLastVisiblePosition()==view.getCount()-1)
					{
						if(view.getCount()>10)
						{
						
						//拖到底部了
						++index;
						v.setVisibility(View.VISIBLE);
						requestData(index);
						DebugLog.e("index的值为",index+"");
						}
						
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		
		search_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				
					BookSearchItem item = listSerchResult.get(position);
					Intent intent = new Intent(LocalSearchActivity.this,
							BookDetail.class);
					intent.putExtra("bid", item.bookId);
					startActivity(intent);
				
			}

		});
		recomment_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				BookSearchItem item = list.get(position);
				Intent intent = new Intent(LocalSearchActivity.this, BookDetail.class);
				intent.putExtra("bid", item.bookId);
				startActivity(intent);
			}
		});
		searchSpinner =(TextView)findViewById(R.id.show_keyword);
        
		searchSpinner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPopupWindowMore != null && mPopupWindowMore.isShowing()) {
					mPopupWindowMore.dismiss();
					return;
				} else {
					showPopupMore();
				}

			}
		});
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeKeyBoard();
				finish();
			}
		});
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
		
		//开始搜索
		search_rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			//没加载出来之前，不进行clear
			//初始化
			if(!TextUtils.isEmpty(et_keyword.getText().toString()))
			{
			 requestData(index);
			 showProgressCancel(task.getTaskName(), "", "加载中...");
			}else
			{
				showToast("搜索内容不能为空", Toast.LENGTH_LONG);
				return;
			}
			
			 

			}
		});
		
		
		
	}
	

	private void initFlowLayout(List<String> keyword) {
		// 初始化热搜推荐
		
		// showProgressCancel(task.getTaskName(), "", "加载中...");
		
		search_flowlayout.removeAllViews();
		for(int k=0;k<keyword.size();k++)
		{
			TextView textView=new TextView(LocalSearchActivity.this,null,R.style.text_flag_01);
			MarginLayoutParams params=new MarginLayoutParams(LayoutParams.WRAP_CONTENT,DisplayUtil.dip2px(getApplicationContext(),28));
			int margin=DisplayUtil.dip2px(getApplicationContext(), 4);
			int padding=margin;
			params.setMargins(margin, margin, margin, margin);
			Random random=new Random();
			int r=random.nextInt(5);
			textView.setBackgroundResource(color[r]);
			textView.setText(keyword.get(k));
			textView.setGravity(Gravity.CENTER);
			textView.setPadding(padding, padding, padding, padding);
			textView.setLayoutParams(params);
			search_flowlayout.addView(textView);
			textView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listSerchResult.clear();
					TextView view=(TextView)v;
					et_keyword.setText(view.getText().toString());
					requestData(index);
					showProgressCancel(task.getTaskName(), "", "加载中...");
				}
			});
			DebugLog.e("initFlowLayout",keyword.get(k));
		}
	}
	public void closeKeyBoard()
	{
		if(getCurrentFocus()!=null)  
        {  
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))  
            .hideSoftInputFromWindow(getCurrentFocus()  
                    .getWindowToken(),  
                    InputMethodManager.HIDE_NOT_ALWAYS);   
        }   
		
	}

	protected void requestData(int index) {
		 Map<String,String> map=new HashMap<String, String>();
		 //&kw=校园&ot=1&it=1&st=1&vt=9&page=1&ssr=3
		 map.put("vt","9");
		 map.put("kw",et_keyword.getText().toString());
		 map.put("ot","1");
		 map.put("it","1");
		 map.put("st",getSearchType()+"");
		 DebugLog.e("类型>>>",getSearchType()+"");
		 map.put("vt","9");
		 map.put("page",index+"");
		 map.put("ssr","3");
		 //map.put("qdid",AppData.readMetaDataFromService(LocalSearchActivity.this, "channel_num"));
		 //加载第一页
		 task=new SearchTask("search",map);
		 AppData.getClient().getTaskManagerRead().addTask(task);
	     
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
	private LinearLayout enterLayout,bookManager,bookStore;
	private TextView top;
	private View getPopupWinodwView() {
		// TODO Auto-generated method stub
		if(view==null)
		{
		  view=LayoutInflater.from(this).inflate(R.layout.bookshelf_menu2,null);
		  enterLayout=(LinearLayout) view.findViewById(R.id.enter_bookshelf);
		  bookManager=(LinearLayout) view.findViewById(R.id.enter_user_center);
		  bookStore=(LinearLayout) view.findViewById(R.id.enter_bookstore);
		  bookManager.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳到用户中心
				AppData.goToShelf(LocalSearchActivity.this,true);
				finish();
				
			}
		});
		  enterLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入书架
				AppData.goToShelf(LocalSearchActivity.this,false);
				finish();
				
			}
		});
		  view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow!= null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}

				return false;
			}
		});
		  bookStore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入书城
				Intent intent=new Intent(LocalSearchActivity.this,StoreMain.class);
				startActivity(intent);
			}
		});
		}
		return view;
	}
	private PopupWindow mPopupWindowMore = null;

	private void showPopupMore() {
		if (null == mPopupWindowMore) {

			mPopupWindowMore = new PopupWindow(getMoreView(),
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			mPopupWindowMore.setFocusable(true);
			mPopupWindowMore.setTouchable(true);
			mPopupWindowMore.setOutsideTouchable(true);
			mPopupWindowMore.setBackgroundDrawable(new BitmapDrawable());

		}

		mPopupWindowMore.showAsDropDown(sousuokuang_ll);
	}

	private View moreView = null;

	private View getMoreView() {
		if (null == moreView) {
			moreView = LayoutInflater.from(this).inflate(
					R.layout.boyi_menu_popup_selector, null);
			moreView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if (mPopupWindowMore != null
							&& mPopupWindowMore.isShowing()) {

						mPopupWindowMore.dismiss();
					}
					return false;
				}
			});
			

			RelativeLayout selLayout = (RelativeLayout) moreView
					.findViewById(R.id.search_bookname);
			//按书名
			selLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// orderBt.setText("按销量");
					searchBookListView.setVisibility(View.GONE);
					searchSpinner.setText(getResources().getString(
							R.string.boyi_search_by_name));
                    list=recommentList_1;
                    mHandler.sendEmptyMessage(LocalSearchActivity.REQUEST_RECOMMEND_SUSSESS);
                    mHandler.sendEmptyMessage(LocalSearchActivity.REQUEST_HOT_SUSSESS);
					hidePopupMore();
				}
			});
            //按作者
			RelativeLayout upDataLayout = (RelativeLayout) moreView
					.findViewById(R.id.search_auctor);
			upDataLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					searchBookListView.setVisibility(View.GONE);
					searchSpinner.setText(getResources().getString(
							R.string.boyi_search_by_info));
					list=recommentList_2;
					mHandler.sendEmptyMessage(LocalSearchActivity.REQUEST_RECOMMEND_SUSSESS);
					mHandler.sendEmptyMessage(LocalSearchActivity.REQUEST_HOT_SUSSESS);
					hidePopupMore();
				}
			});

			//按关键字
			RelativeLayout searchwordLayout = (RelativeLayout) moreView
					.findViewById(R.id.search_keywords);
			searchwordLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					searchBookListView.setVisibility(View.GONE);
					searchSpinner.setText(getResources().getString(
							R.string.boyi_search_by_word));

					list=recommentList_3;
					mHandler.sendEmptyMessage(LocalSearchActivity.REQUEST_RECOMMEND_SUSSESS);
					mHandler.sendEmptyMessage(LocalSearchActivity.REQUEST_HOT_SUSSESS);
					hidePopupMore();
				}
			});

		}
		return moreView;
	}
	private void hidePopupMore() {
		if (null != mPopupWindowMore && mPopupWindowMore.isShowing()) {
			mPopupWindowMore.dismiss();
		}
	}	
	public int getSearchType() {
		int searchType = Search.TYPE_NAME;
		if (searchSpinner != null) {
			if (searchSpinner.getText().equals(
					getResources().getString(R.string.boyi_search_by_name))) {
				searchType = Search.TYPE_NAME;
			} else if (searchSpinner.getText().equals(
					getResources().getString(R.string.boyi_search_by_info))) {
				searchType = Search.TYPE_AUTHOR;
			} else {
				searchType = Search.TYPE_KEYWORD;
			}
		}
		return searchType;

	}
	class BoyiSearchAdapter extends BaseAdapter{
		private ViewHolder holder;
        private List<BookSearchItem> list;
		public BoyiSearchAdapter(List<BookSearchItem> listSerchResult) {
			this.list=listSerchResult;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView==null)
			{
				holder=new ViewHolder();
				convertView=LayoutInflater.from(LocalSearchActivity.this).inflate(R.layout.boyi_bookstore_search_item,null);
				holder.cover_book=(NetworkImageView) convertView.findViewById(R.id.cover_book);
				holder.book_detail_Desc=(TextView) convertView.findViewById(R.id.book_detail_Desc);
				holder.book_detail_words=(TextView) convertView.findViewById(R.id.book_detail_words);
				holder.book_author_name=(TextView) convertView.findViewById(R.id.book_author_name);
				convertView.setTag(holder);
			}else
			{
				holder=(ViewHolder) convertView.getTag();
			}
			BookSearchItem item=list.get(position);
			holder.cover_book.setErrorImageResId(R.drawable.boyi_ic_cover_default);
			holder.cover_book.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
			holder.cover_book.setImageUrl("http://wap.cmread.com"+item.bookCoverLogo,getImageLoader());
			holder.book_detail_Desc.setText(item.bookBrief);
			holder.book_detail_words.setText(item.bookName);
			holder.book_author_name.setText(item.bookAuthor);
			return convertView;
		}
		
	}
	class ViewHolder{
		private NetworkImageView cover_book;
		private TextView book_detail_words,book_detail_Desc,book_author_name;
		
	}
	class SearchTask extends Task{
		private Map<String,String> map;
		public SearchTask(String strTaskName,Map<String, String> map2) {
			super(strTaskName);
			this.map=map2;
		}

		
		
        @Override
		protected void doTask() {
			 getRequestQueue().add(new JsonObjectPostRequest(AppData.getConfig().getUrl(Config.URL_BOOK_SEARCH), new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						
						try {
							
//							int status = response.getInt("status");
//							DebugLog.e("TAG",status+"");
//							int size=response.getJSONObject("data").getInt("SearchResultTotalSize");
//							if(StatusCode.OK==status && size>0)
//							{
								//另外一个搜索
								if(map.get("page").equals("1"))
								{
									listSerchResult.clear();
								}
								//JSONObject data=response.getJSONObject("data");
								int size=response.getInt("SearchResultTotalSize");
								JSONArray  jsonArray=response.getJSONArray("SearchResultList");
							if(size>0)
							{
								for(int i=0;i<jsonArray.length();i++){
								   BookSearchItem item=new BookSearchItem();
								   JSONObject object=jsonArray.getJSONObject(i);
								   item.bookCoverLogo=object.getString("BookCoverLogo");
								   item.bookCategory=object.getString("BookCategory");
								   item.bookName=object.getString("BookName");
								   item.bookAuthor=object.getString("BookAuthor");
								   item.bookId=object.getString("BookId");
								   item.bookUpdateTime=object.getString("BookUpdateTime");
								   item.bookBrief=object.getString("BookBrief");
								   item.bookStatus=object.getString("BookStatus");
								   item.bookChapterTotalSize=object.getInt("BookChapterTotalSize");
								   listSerchResult.add(item);
								}
								search_result_size.setText("共找到"+size+"本书");
								mHandler.sendEmptyMessage(REQUEST_SUSSESS);
						    }else
						    {
						    	if(listSerchResult.size()>0)
						    	{
						    		mHandler.sendEmptyMessage(REQUEST_NOT_FOUND_MORE);
						    	}else
						    	{
						    		mHandler.sendEmptyMessage(REQUEST_NOT_FOUND);
						    	}
						    }
							//}
//							if(StatusCode.NO_DATA==status && size>0)
//							{
//								
//							}if(StatusCode.NO_DATA==status && size==0)
//							{
//								
//							}
						} catch (JSONException e) {
							mHandler.sendEmptyMessage(REQUEST_FAIL);
							e.printStackTrace();
						}
						
						
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(REQUEST_FAIL);
					}
				},map));
		}
       
		
		
	} 
	//热词推荐
	class RecommendTask extends Task{
        
		public RecommendTask(String strTaskName) {
			super(strTaskName);
		}

		@Override
		protected void doTask() {
			getRequestQueue().add(new StringRequest(AppData.getConfig().getUrl(Config.URL_SEARCH_RECOMMEND),new Listener<String>() {

				@Override
				public void onResponse(String response) {
					try {
						JSONObject object=new JSONObject(response);
						String status=object.getString("status");
						if(status.equals("100") && !TextUtils.isEmpty(response))
						{
							JSONObject jsonObject=object.getJSONObject("data");
							for(int j=1;j<4;j++)
							{
								JSONArray array=jsonObject.getJSONArray(j+"");
								for(int i=0;i<array.length();i++)
								{
									BookSearchItem item=new BookSearchItem();
									JSONObject arrayObject=array.getJSONObject(i);
									item.bookCoverLogo=arrayObject.getString("bigimages");
									item.bookCategory=arrayObject.getString("ydsortname");
									item.bookName=arrayObject.getString("title");
									item.bookAuthor=arrayObject.getString("author");
									item.bookId=arrayObject.getString("aid");
								    item.bookUpdateTime=arrayObject.getString("updatetime");
									item.bookBrief=arrayObject.getString("sortdescription");
									item.bookStatus=arrayObject.getString("isfinish");
									item.bookChapterTotalSize=arrayObject.getInt("totalchapters");
									if(j==1)
									{
									  recommentList_1.add(item);
									 }else if(j==2){
									  recommentList_2.add(item);
									 }else
									 {
									  recommentList_3.add(item);
									 }
								}
							}
							
							mHandler.sendEmptyMessage(REQUEST_RECOMMEND_SUSSESS);
							
						}else
						{
							//未找到数据
							mHandler.sendEmptyMessage(REQUEST_RECOMMEND_FAIL);
						}
					} catch (JSONException e) {
						mHandler.sendEmptyMessage(REQUEST_RECOMMEND_FAIL);
						e.printStackTrace();
					}
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					mHandler.sendEmptyMessage(REQUEST_RECOMMEND_FAIL);
				}
			}));
		}
		
	}
	class HotKeyTask extends Task{

		public HotKeyTask(String strTaskName) {
			super(strTaskName);
		}

		@Override
		protected void doTask() {
			getRequestQueue().add(new StringRequest(AppData.getConfig().getUrl(Config.URL_SEARCH_HOTKEYWIORD),new Listener<String>() {

				@Override
				public void onResponse(String response) {
					try{
						JSONObject object=new JSONObject(response);
						String status=object.getString("status");
						if(status.equals("100") && !TextUtils.isEmpty(response))
						{
							JSONArray jsonArray=object.getJSONArray("data");
							for(int j=0;j<jsonArray.length();j++)
							{
					      	JSONArray array=jsonArray.getJSONObject(j).getJSONArray("childs");
					      	JSONArray jb=array.getJSONObject(0).getJSONArray("childs");
					      	for(int i=0;i<jb.length();i++)
					      	{
					      		
					      		if(j==0)
					      		{
					      		   keyWord1.add(jb.getJSONObject(i).getString("title"));
					      		}else if(j==1)
					      		{
					      			keyWord2.add(jb.getJSONObject(i).getString("title"));
					      			
					      		}else if(j==2)
					      		{
					      			keyWord3.add(jb.getJSONObject(i).getString("title"));
					      		}
					      	}
							}
						  DebugLog.e("第二个",keyWord2.get(0)+":::"+keyWord2.get(1));
					      mHandler.sendEmptyMessage(REQUEST_HOT_SUSSESS);	
						}else
						{
							//未找到热词推荐
							mHandler.sendEmptyMessage(REQUEST_HOT_FAIL);
						}
					}catch(Exception e){
						mHandler.sendEmptyMessage(REQUEST_HOT_FAIL);
					}
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					mHandler.sendEmptyMessage(REQUEST_HOT_FAIL);
				}
			}));
		}
		
	}
	
}
