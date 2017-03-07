package com.boyiqove.ui.bookstore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.library.volley.RequestQueue;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.toolbox.ImageLoader;
import com.boyiqove.library.volley.toolbox.StringRequest;
import com.boyiqove.ui.storeadapter.ScrollListView;
import com.boyiqove.ui.storeadapter.SearchListAdapter;
import com.boyiqove.ui.storeadapter.listerScrollView;
import com.boyiqove.ui.storeadapter.listerScrollView.ScrollBottomListener;
import com.boyiqove.ui.storeutil.JsonUtil;
import com.boyiqove.ui.storeutil.MyFlowLayout;
import com.boyiqove.util.DebugLog;
import com.boyiqove.view.BaseFragment;
import com.bytetech1.sdk.Iqiyoo;
import com.bytetech1.sdk.data.cmread.Search;
import com.bytetech1.sdk.data.cmread.SearchItem;

public class SearchFragment extends BaseFragment {
	private Button orderBt;
	Drawable leftIcon;
	private Drawable noLeftIcon;
	private boolean isOpen;
	private TextView bookName, actorName, keyWord;
	private TextView lastTextView;
	private LinearLayout searchIv;
	private EditText editText;
	private int isWhere = 0;
	private Boolean isSell; // 按销量排序为false
	private int searchByname, searchBySell, searByAuthor, searchKeyWord,
			searchByUpdata;
	private ScrollListView searchBookListView;
	private listerScrollView scrollView;
	private TextView searchSpinner;
	private List<String> spinStrList;
	private MyFlowLayout mflowoutTop, mflowoutCenter, mflowoutbBottom;
	private RequestQueue queue;
	private String lastWork;
	private View mRootView;
	private int i = 1;
	private LinearLayout searchBodyll1, searchBodyll2, searchBodyll3;

	private Search search;
	private SearchListAdapter adapter;
	private List<SearchItem> list;
	private ListView listview;
	private boolean isSearch = false;
	private LinearLayout v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		search = new Search();
		if (null == mRootView) {
			mRootView = inflater.inflate(R.layout.boyi_search_activity,
					container, false);
			searchBodyll1 = (LinearLayout) mRootView
					.findViewById(R.id.search_body);
			searchBodyll2 = (LinearLayout) mRootView
					.findViewById(R.id.search_body2);
			searchBodyll3 = (LinearLayout) mRootView
					.findViewById(R.id.search_body3);
			scrollView = (listerScrollView) mRootView
					.findViewById(R.id.search_scrollview);
			listview = (ListView) mRootView.findViewById(R.id.search_list2);
			v = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
					R.layout.boyi_listview_fooer, null);
			initView();
			initFlowLayout(0, searchBodyll1);
			initFlowLayout(1, searchBodyll2);
			initFlowLayout(2, searchBodyll3);
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		return mRootView;
	}

	/**
	 * 取默认标签
	 * */
	private void initFlowLayout(final int num, final LinearLayout searchBodyll) {
		// TODO Auto-generated method stub
		// 获取linearlayout子view的个数
		// searchBodyll=Bodyll;
		queue = AppData.getRequestQueue();
		StringRequest requestKeyWord = new StringRequest(getResources()
				.getString(R.string.boyi_search_hot_keyword),
				new Listener<String>() {

					@Override
					public void onResponse(String arg0) {
						// TODO Auto-generated method stub

						Map<String, List<String>> beans = JsonUtil.getTitle(
								arg0, num); //

						// DebugLog.e("开始解析了", beans.get(0)+"");
						Set keySet = beans.keySet(); // 获取key集合对象

						int cout = 0;
						for (Object keyName : keySet) {
							cout++;
							DebugLog.e("键名：", keyName + ""); // 输出键名
							List<String> list = beans.get(keyName);

							View view = LayoutInflater.from(getActivity())
									.inflate(R.layout.boyi_search_biaoqian,
											null);
							TextView tv = (TextView) view
									.findViewById(R.id.search_biaoqian);
							tv.setText(keyName + "");
							if (cout % 2 == 1) {
							} else {
								tv.setTextColor(getResources().getColor(
										R.color.boyi_search_biaoqian_red));

							}

							searchBodyll.addView(view);
							MyFlowLayout flowLayout = new MyFlowLayout(
									getActivity());
							LayoutParams params1 = new LayoutParams(
									LinearLayout.LayoutParams.WRAP_CONTENT,
									LinearLayout.LayoutParams.WRAP_CONTENT);
							flowLayout.setLayoutParams(params1);
							searchBodyll.addView(flowLayout);

							for (int k = 0; k < list.size(); k++) {
								// list.get(k).getTitle();
								final String nameString = list.get(k);
								TextView tvTextView = new TextView(
										getActivity());
								// final String
								// nameString=list.get(k).getTitle();
								tvTextView.setText(nameString);
								LayoutParams params = new LayoutParams(
										LinearLayout.LayoutParams.WRAP_CONTENT,
										LinearLayout.LayoutParams.WRAP_CONTENT);
								params.setMargins(16, 20, 0, 0);
								Drawable drawable = getResources().getDrawable(
										R.drawable.boyi_search_tag_bg);
								tvTextView.setBackgroundDrawable(drawable);
								tvTextView.setTextColor(getResources()
										.getColor(R.color.boyi_white));
								tvTextView.setLayoutParams(params);
								flowLayout.addView(tvTextView);

								tvTextView
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												// TODO Auto-generated method
												// stub
												editText.setText("");
												editText.setText(nameString);
												String word = editText
														.getText().toString();
												DebugLog.e("当前搜索选择的是：：：：", word);
												showProgress("", "加载中..");
												if (word == null) {
													DebugLog.e("搜索分类为空：：：：",
															word);

												}

												if ("按书名".equals(searchSpinner
														.getText())) {
													new searchBookThread(word,
															searchByname, 1)
															.start();

												} else if ("按作者"
														.equals(searchSpinner
																.getText())) {

													new searchBookThread(word,
															searByAuthor, 1)
															.start();

												} else {

													new searchBookThread(word,
															searchKeyWord, 1)
															.start();
												}
											}
										});

							}

						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub

					}
				});

		queue.add(requestKeyWord);

	}

	private ImageLoader imageLoader;

	private void initView() {
		// TODO Auto-generated method stub
		// imageLoader=new ImageLoader(queue, ImageCacheManager.getInstance());

		list = new ArrayList<SearchItem>();
		adapter = new SearchListAdapter(getActivity(), list, true);
		listview.setAdapter(adapter);
		mflowoutTop = (MyFlowLayout) mRootView.findViewById(R.id.recommend1);
		mflowoutCenter = (MyFlowLayout) mRootView.findViewById(R.id.recommend1);
		mflowoutbBottom = (MyFlowLayout) mRootView
				.findViewById(R.id.recommend1);

		// hotSearchBoy=(TextView) mRootView.findViewById(R.id.hotsearch_boy);
		// hotSearchGirl=(TextView) mRootView.findViewById(R.id.hotsearch_girl);
		// hotSearchPublish=(TextView)
		// mRootView.findViewById(R.id.hotsearch_publish);

		spinStrList = new ArrayList<String>();
		spinStrList.add("按书名");
		spinStrList.add("按作者");
		spinStrList.add("关键字");

		searchBookListView = (ScrollListView) mRootView
				.findViewById(R.id.search_list);
		searchSpinner = (TextView) mRootView.findViewById(R.id.search_spinner);

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

		String[] itemList = getResources().getStringArray(
				R.array.spinner_degress);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, itemList);
		// spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAdapter.setDropDownViewResource(R.layout.boyi_spinner_layout);

		searchByname = Search.TYPE_NAME;
		searByAuthor = Search.TYPE_AUTHOR;
		searchKeyWord = Search.TYPE_KEYWORD;

		searchBySell = Search.ORDER_TYPE_SELL;
		searchByUpdata = Search.ORDER_TYPE_UPDATE;

		editText = (EditText) mRootView.findViewById(R.id.search_edit);

		// noLeftIcon=getResources().getDrawable(R.drawable.radio_btn_normal);
		// leftIcon=getResources().getDrawable(R.drawable.radio_btn_checked);

		searchIv = (LinearLayout) mRootView.findViewById(R.id.search_textview);

		searchIv.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				// TODO Auto-generated method stub
				list.clear();
				i=1;
				String word = editText.getText().toString();
				if (word.equals("")) {
					showToast("请输入搜索内容", Toast.LENGTH_LONG);
					return;
				}

				if (word.equals("logout")) {
					Iqiyoo.logout(getActivity());
					Toast.makeText(getActivity(), "退出登录", 1000).show();
					return;
				}

				showProgress("", "加载中...");

				if ("按书名".equals(searchSpinner.getText())) {

					new searchBookThread(word, searchByname, 1).start();

				} else if ("按作者".equals(searchSpinner.getText())) {

					new searchBookThread(word, searByAuthor, 1).start();

				} else {

					new searchBookThread(word, searchKeyWord, 1).start();
				}

			}
		});
		bookName = (TextView) mRootView.findViewById(R.id.search_book_name);
		lastTextView = bookName;
		actorName = (TextView) mRootView.findViewById(R.id.search_actor_name);
		keyWord = (TextView) mRootView.findViewById(R.id.search_keyword_name);
		bookName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// lastTextView.setTextColor(color)
				isWhere = 0;
				noLeftIcon.setBounds(0, 0, noLeftIcon.getMinimumWidth(),
						noLeftIcon.getMinimumHeight());
				lastTextView.setCompoundDrawables(noLeftIcon, null, null, null);
				leftIcon.setBounds(0, 0, leftIcon.getMinimumWidth(),
						leftIcon.getMinimumHeight());
				bookName.setCompoundDrawables(leftIcon, null, null, null);
				lastTextView = bookName;
			}
		});
		actorName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isWhere = 1;
				noLeftIcon.setBounds(0, 0, noLeftIcon.getMinimumWidth(),
						noLeftIcon.getMinimumHeight());
				lastTextView.setCompoundDrawables(noLeftIcon, null, null, null);
				leftIcon.setBounds(0, 0, leftIcon.getMinimumWidth(),
						leftIcon.getMinimumHeight());
				actorName.setCompoundDrawables(leftIcon, null, null, null);

				lastTextView = actorName;
			}
		});
		keyWord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isWhere = 2;
				noLeftIcon.setBounds(0, 0, noLeftIcon.getMinimumWidth(),
						noLeftIcon.getMinimumHeight());
				lastTextView.setCompoundDrawables(noLeftIcon, null, null, null);
				leftIcon.setBounds(0, 0, leftIcon.getMinimumWidth(),
						leftIcon.getMinimumHeight());
				keyWord.setCompoundDrawables(leftIcon, null, null, null);
				lastTextView = keyWord;
			}
		});
		orderBt = (Button) mRootView.findViewById(R.id.order_bt);
		orderBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPopupWindowMore != null && mPopupWindowMore.isShowing()) {
					mPopupWindowMore.dismiss();
					return;
				} else {
					showPopupMore();
				}

				// if (!isOpen) {
				// showPopupMore();// 展示下拉调整菜单
				// isOpen=true;
				// }else {
				// hidePopupMore(); // 收起来
				// // isOpen=false;
				//
				// }
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				SearchItem item = list.get(position);
				Intent intent = new Intent(getActivity(), BookDetail.class);
				intent.putExtra("bid", item.bid);
				startActivity(intent);
			}
		});

		// searchBookListView.setOnScrollListener(new OnScrollListener() {
		//
		// @Override
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		// // TODO Auto-generated method stub
		// // 当不滚动时
		// if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
		// // 判断是否滚动到底部
		// if (view.getLastVisiblePosition() == view.getCount() - 1) {
		// //加载更多功能的代码
		// DebugLog.e("到底部了", "等待刷新");
		//
		// if (isSearch) {
		// new searchBookThread(lastWork, lastSearchBy, i+1).start();
		// }
		//
		// }
		// }
		// }
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

		// scrollView.setScrollBottomListener(new ScrollBottomListener() {
		//
		// @Override
		// public void scrollBottom() {
		// // TODO Auto-generated method stub
		// if (isSearch) {
		// DebugLog.e("搜索页面滑倒底部了", lastWork+":::"+lastSearchBy+":::"+i);
		// new searchBookThread(lastWork, lastSearchBy,++i).start();
		// }
		// DebugLog.e("滑倒底部了", "加载更多");
		// }
		// });
		ImageView imageView = (ImageView)v.findViewById(R.id.progressBar1);
		Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.tip);
		imageView.startAnimation(operatingAnim);
		listview.addFooterView(v, null, false);
		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (isSearch) {
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
						if (view.getLastVisiblePosition() == view.getCount() - 1) {
							if (list.size() >= 10) {
								v.setVisibility(View.VISIBLE);
								i++;
								new searchBookThread(lastWork,getSearchType(), i)
										.start();
							}
						}
					}
				}
				
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (search.getList() != null) {
					list.addAll(search.getList());
					isSearch = true;
					listview.setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
					listview.setVisibility(View.VISIBLE);
					
					searchBodyll1.setVisibility(View.GONE);
					searchBodyll2.setVisibility(View.GONE);
					searchBodyll3.setVisibility(View.GONE);

				} else {
					if (list == null) {
						Toast.makeText(getActivity(), "没有找到内容", 1).show();

					} else {
						Toast.makeText(getActivity(), "没有找到更多内容", 1).show();
					}
					isSearch = false;
					
				}
				v.setVisibility(View.GONE);
				hideProgress();
				break;
			case 2:
				isSearch = false;
				hideProgress();
				Toast.makeText(getActivity(), "没有找到内容", Toast.LENGTH_LONG)
						.show();
				break;

			}

		};
	};

	public class searchBookThread extends Thread {
		private String word;
		private int searchByname, page;

		public searchBookThread(String word, int searchByname, int page) {
			super();
			this.word = word;
			lastWork = word;
			this.searchByname =getSearchType();
			this.page = page;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			if (word == null) {
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
				return;
			} else {
				boolean res = search.search(word, page, searchByname,
						Search.ORDER_TYPE_SELL);

				// if (res) {

				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg); // 向Handler发送消息,更新UI
				// }
				// else {
				// isSearch=false;
				// Message msg = new Message();
				// msg.what = 2;
				// mHandler.sendMessage(msg);
				//
				// }

			}
		}

	}

	// 展示整理书籍的下拉菜单
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

		mPopupWindowMore.showAsDropDown(searchSpinner);
	}

	private View moreView = null;

	private View getMoreView() {
		if (null == moreView) {
			moreView = LayoutInflater.from(getActivity()).inflate(
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
			// moreView.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			//
			// hidePopupMore();
			// }
			// });

			RelativeLayout selLayout = (RelativeLayout) moreView
					.findViewById(R.id.search_bookname);
			selLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// orderBt.setText("按销量");
					searchBookListView.setVisibility(View.GONE);
					searchSpinner.setText(getResources().getString(
							R.string.boyi_search_by_name));

					searchBodyll1.setVisibility(View.VISIBLE);
					isSearch = true;
					searchBodyll2.setVisibility(View.GONE);
					searchBodyll3.setVisibility(View.GONE);
					isSell = false;
					hidePopupMore();
				}
			});

			RelativeLayout upDataLayout = (RelativeLayout) moreView
					.findViewById(R.id.search_auctor);
			upDataLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isSearch = true;
					searchBookListView.setVisibility(View.GONE);
					searchSpinner.setText(getResources().getString(
							R.string.boyi_search_by_info));

					searchBodyll1.setVisibility(View.GONE);
					searchBodyll2.setVisibility(View.VISIBLE);
					searchBodyll3.setVisibility(View.GONE);
					isSell = true;
					hidePopupMore();
				}
			});

			RelativeLayout searchwordLayout = (RelativeLayout) moreView
					.findViewById(R.id.search_keywords);
			searchwordLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isSearch = true;
					searchBookListView.setVisibility(View.GONE);
					searchSpinner.setText(getResources().getString(
							R.string.boyi_search_by_word));

					searchBodyll1.setVisibility(View.GONE);
					searchBodyll2.setVisibility(View.GONE);
					searchBodyll3.setVisibility(View.VISIBLE);
					isSell = true;
					hidePopupMore();
				}
			});

		}
		return moreView;
	}

	// 收起菜单的方法
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
					getResources().getString(R.string.boyi_search_girl_author))) {
				searchType = Search.TYPE_AUTHOR;
			} else {
				searchType = Search.TYPE_KEYWORD;
			}
		}
		return searchType;

	}

}
