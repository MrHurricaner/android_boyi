package com.boyiqove;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.widget.Toast;

import com.boyiqove.config.ClientUser;
import com.boyiqove.config.Config;
import com.boyiqove.config.DeviceInfo;
import com.boyiqove.db.DBContentHelper;
import com.boyiqove.db.DBDataHelper;
import com.boyiqove.db.DBManager;
import com.boyiqove.db.DBXNContentHelper;
import com.boyiqove.entity.BookItem;
import com.boyiqove.library.volley.RequestQueue;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.toolbox.StringRequest;
import com.boyiqove.library.volley.toolbox.Volley;
import com.boyiqove.protocol.JsonObjectPostRequest;
import com.boyiqove.protocol.RequestParam;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.task.CallBackMsg;
import com.boyiqove.task.CallBackTask;
import com.boyiqove.ui.bookshelf.InterfaceShelf;
import com.boyiqove.ui.bookshelf.OnlineReadingActivity;
import com.boyiqove.ui.bookshelf.UpdateInterface;
import com.boyiqove.ui.bookstore.BookDetail;
import com.boyiqove.ui.bookstore.StoreMain;
import com.boyiqove.ui.bookstore.StoreTitleActivity;
import com.boyiqove.ui.storeutil.JsonUtil;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.GetBookDetailUtil;
import com.boyiqove.view.BaseActivity;
import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.Iqiyoo;
import com.bytetech1.sdk.data.Detail;

/**
 * @author WindowY
 * 
 */
public class AppData {
	private static DBManager db = null;
	private static Config config = null;
	private static ReadClient client;
	private static ClientUser user;
	private static RequestQueue mRequestQueue = null;
	private static Context mContext = null;
	private static String cleanUrl = null;
	private static InterfaceShelf interfaceShelf;
	public static final String VERSION_CODE="3.1";
	public static final int ENTRY_TYPE_SHELF = 1; // 进入书架
	public static final int ENTRY_TYPE_DETAIL = 2; // 进入详情
	public static final int ENTRY_TYPE_STORE = 3; // 进入书城
	public static final int ENTRY_TYPE_READBOOK = 4; // 直接进入阅读
	public static final int ENTRY_TYPE_STORETWO = 5; // 进入书城更多
	public static final int ENTRY_CODE_SLIDE = 1; // 侧边入口
	public static final int ENTRY_CODE_BANNER = 2; // banner入口
	public static final int ENTRY_CODE_NAVIJATION = 3; // 导航入口
	public final static String KEY_CHANNEL = "channel_num";// 渠道
	public static String channel = "name";
	public static String serviceName = null;
	public static String serviceAction = null;
    public static String loginUrl=null;
    public static String subChannel=null;
    private static boolean mIsOpenLast;
    private static boolean mIsAutoUpdate;
    private static boolean mShowRecommend;
    private static SharedPreferences sp;
    private static DBDataHelper helper;
    public static int  initSdk=0;
    public static final String versionName="3.1";
    public static boolean isFirst=true;
    public static boolean isForceUpdate=false;
    public static long lastTime;
	public static void init(Context cnx) {
		mContext = cnx;

		db = new DBManager(mContext);

		config = new Config(mContext);

		client = new ReadClient();
		DebugLog.on(true);
		mRequestQueue = Volley.newRequestQueue(mContext);
		int userid = config.getLastUserID(); // lastUid 默认为0
		DebugLog.e("上个用户id", userid+"");
		if (userid==0) {
			createAccount();
		}
		String name = config.getDataDBName(userid);
		helper = (DBDataHelper) db.open(name, DBManager.TYPE_DATA);

		user = new ClientUser(mContext, config.getLastUserID(), helper);
		// 初始化奇悠
		Iqiyoo.init(cnx, "M2040051", "boetech");
		
//		Iqiyoo.enableLog(false);
		Iqiyoo.enableLog(true);
		Iqiyoo.disableSmsBlock();
		
		
		channel = readMetaDataFromService(cnx,KEY_CHANNEL);		
		
		 cleanUrl=config.getUrl("URL_USER_TIME_LONG");
		/**
		 * 请求下载映射表，判断sp中是否有数据进行判断存储，并对变化的数据进行删除
		 */
//		getGoStore(); // 进入书城开始时间统计
		 
		getMapTable();
		
		// serviceAction=BoyiService.BOYISERVICE_NAME+channel;
		// if(CommonUtil.isServiceRunning(cnx, serviceName)) {
		// BoyiService.stopBoyiService(cnx,serviceAction);
		// }
		// BoyiService.startBoyiService(cnx,serviceAction);
		// Toast.makeText(cnx, "服务开启", Toast.LENGTH_LONG).show();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(mContext, config.getCrashLogDir());		
//		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//		StrictMode.setThreadPolicy(policy);
		sp=cnx.getSharedPreferences("config",Application.MODE_PRIVATE);
		mIsOpenLast=sp.getBoolean("mIsOpenLast",true);
		mIsAutoUpdate=sp.getBoolean("mIsAutoUpdate",true);
		mShowRecommend=sp.getBoolean("mShowRecommend",true);
	}
	// 设置子渠道
	public static void setSubChannel(String channelId) {	
		subChannel=channelId;
		DebugLog.e("子渠道号：", subChannel);
	}
	
	// 打开log
	public static void openLog(boolean open) {
		DebugLog.on(open);
	}

	// 进入书城
	public static void goStore() {
		Intent intent = new Intent(mContext, StoreMain.class);
		mContext.startActivity(intent);
	}

	// 注册服务名称
	public static void setServiceName(String name) {
		serviceName = name;
	}
	
	public interface ManagerInterface {
		   public void confirmManager();
	}
	
	private static ManagerInterface managerInterface;
	public static void setInterface(ManagerInterface mInterface)
	{
		managerInterface=mInterface;
	}
	
	
	// 进入书城
	public static void isWhere(int tid, int wid) {
		final int mtid = tid;
		final int mwid = wid;
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", AppData.getUser().getID() + "");
		map.put("tid", "" + mtid);
		map.put("placeid", "" + mwid);

		String url = AppData.getConfig().getUrl(Config.URL_PLACE_TUIJIAN);
		getRequestQueue().add(
				new JsonObjectPostRequest(url, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							int status = response.getInt("status");
							if (status == StatusCode.OK) {

							} else {

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
					}

				}, map));
	}

	// 注册去书架的接口
	public static void setInterfaceShelf(InterfaceShelf faceShelf) {
		interfaceShelf = faceShelf;		
	};
	// 调用去书架的接口
	public static void goToShelf(Context context,boolean isOpenUserCenter){
		if (interfaceShelf != null) {
			interfaceShelf.onGoShelf(context,isOpenUserCenter);
		}
	}
	
	
// 注册从书城直接进入阅读的统一跳转接口
//	public static void setReadBook(int goWhere, int bid, String url,
//			String title, Context context) {
//		if (url.equals("") || url == null) {
//			final String mbid = bid + "";
//			final Context mContext = context;
//			mHandler.post(new Runnable() {
//				public void run() {
//					try {
//						// startBookDetil(mbid);
////						startReadBook(mbid, mContext,"");
//					} catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();
//					}
//				}
//
//			});
//		}
//	};
	
	// 除了书架，其他地方直接进入阅读的接口
	public static void startBookReading(Context context,String bid, String url,Boolean isBanner , int a) {
		// TODO Auto-generated method stub
		// 检查本地数据库		
		
		GetBookDetailUtil.startReadingBook(bid, url, context, isBanner, 0);
		
//				if (AppData.getDataHelper().foundBookBid(bid)) {
//					
//					item=AppData.getDataHelper().getBookItem(Integer.parseInt(bid));
//					AppData.getDataHelper().updateImageUrl(Integer.parseInt(bid), url);
//					Intent intent = new Intent(context,
//							OnlineReadingActivity.class);
//					intent.putExtra("BookItem", item);
//					context.startActivity(intent);
//					
////					AppData.closeDBContent(Integer.parseInt(bid));
//				}else {			
////					getBookItem(bid,url,context,isBanner,0);
////					GetTaskItem taskItem=new AppData().new GetTaskItem("startread",context, bid, url,isBanner);
////					AppData.getClient().getTaskManagerRead().addTask(taskItem);
//				}
	}
	
	private static void getGoStore() {
		// TODO Auto-generated method stub
		String url = AppData.getConfig().getUrl(Config.URL_GOSTORE_TIME);
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", AppData.getUser().getID() + "");
		getRequestQueue().add(
				new JsonObjectPostRequest(url, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							int status = response.getInt("status");
							if (status == StatusCode.OK) {

							} else {
								// DebugLog.d(TAG, "用户自动注册失败:" +
								// response.getString("msg"));
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
						// DebugLog.d(TAG, error.toString());
					}

				}, map));
	}

	public class GetTaskItem extends CallBackTask {
		private String mBid;
		private String mUrl;
		private Boolean isBanner;
		private Context context;
		public GetTaskItem(String strTaskName,Context context, String bid,String url , Boolean isBanner) {
			super(strTaskName);
			// TODO Auto-generated constructor stub
			this.mBid = bid;
			this.mUrl=url;	
			this.isBanner=isBanner;
			this.context=context;
		}
		@Override
		protected void doTask() {
			getBookItem(mBid,mUrl);			
			mHandler.post(new Runnable() {
				public void run() {
					try {
						Intent intent = new Intent(context,
								OnlineReadingActivity.class);
						intent.putExtra("BookItem", item);
						intent.putExtra("isBanner", isBanner);
						context.startActivity(intent);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			});
			
		}
	}
	
	public static String readMetaDataFromService(Context context, String key) {
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			String bookChn = appInfo.metaData.getString(key);
			if (! TextUtils.isEmpty(subChannel)) {
				bookChn=bookChn+"_"+subChannel;
				DebugLog.e("渠道号：", bookChn);
			}
			
			DebugLog.e("MainActivity", "bookchannel=" + bookChn);
			return bookChn;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

		};
	};
	
	// 合作方web调用直接进入阅读的方法
	public static void goReadBook(Context context,String bid ,int  tid,int wid,String url) {
		final Context mContext = context;
		final String mBid = bid;
		final String mUrl = url;
		final int mtid=tid;
		final int mwid=wid;
		// startReadBook(mBid,mContext);
		mHandler.post(new Runnable() {
			public void run() {
				try {
					startReadBook(mBid,mtid,mwid, mContext,mUrl);
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	};
	
	/**
	 * @param where
	 *            进入博易的入口位置 0 侧边 1主界面 2 bander页
	 * @param type
	 *            进入的是什么类型 0书架 1书城 2详情
	 * @param pram
	 *            当进入书籍详情时 需要传入书籍的bid，为“”时表示进入的不是书籍详情
	 */
	public static void goBoyiSdk(int where, int type, String bid,
			Context context, String url, String title) {

		reportEntry(where);

		switch (type) {
		case ENTRY_TYPE_SHELF:
			// 上传where
			if (interfaceShelf != null) {
				interfaceShelf.onGoShelf(context,false);
			}
			break;
		case ENTRY_TYPE_STORE:
			Intent intent = new Intent(mContext, StoreMain.class);
			mContext.startActivity(intent);
			break;
		case ENTRY_TYPE_DETAIL:
			Intent intent2 = new Intent(context, BookDetail.class);
			intent2.putExtra("bid", bid);
			mContext.startActivity(intent2);
			break;

		case ENTRY_TYPE_READBOOK: // 直接进入阅读
			final Context mContext = context;
			final String mBid = bid;
			// startReadBook(mBid,mContext);
			mHandler.post(new Runnable() {
				public void run() {
					try {
						startReadBook(mBid,0,0 ,mContext,"");
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			});

			break;

		case ENTRY_TYPE_STORETWO:
			final Context oContext = context;
			final String mUrl = url;
			final String mTitle = title;
			mHandler.post(new Runnable() {
				public void run() {
					try {
						startSecondVoid(oContext, mUrl, mTitle);

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				}
			});
			break;

		default:
			break;
		}

	}

	private static void startSecondVoid(Context oContext, String mUrl,
			String mTitle) {
		// DebugLog.e("查看详情++++++++++子线程进行跳转", mUrl);
		Intent intent = new Intent(oContext, StoreTitleActivity.class);
		intent.putExtra("url", "http://sdk.boetech.cn" + mUrl);
		intent.putExtra("title", mTitle);
		oContext.startActivity(intent);
		// getActivity().overridePendingTransition(R.anim.left_activity_scale,
		// R.anim.move_right_in);
		// oContext.overridePendingTransition(R.anim.boyi_move_right_in,R.anim.boyi_left_activity_scale);
	};

	public static void reportEntry(int where) {
		String url = AppData.getConfig().getUrl(Config.URL_GOBOESDK_ENTRY);
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", AppData.getUser().getID() + "");
		map.put("rukouid", where + "");

		getRequestQueue().add(
				new JsonObjectPostRequest(url, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							int status = response.getInt("status");
							if (status == StatusCode.OK) {

							} else {
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
					}

				}, map));
	}
	/**
	 *   直接进入阅读的接口，带上传位置(在合作方阅读中调用 goReadBook)
	 * */
	private static ProgressDialog mProgressDialog;
	public static void startReadBook(String bid, int tid,int wid, Context context,String url) {
		// TODO Auto-generated method stub		
		GetBookDetailUtil.startReadingBook(bid, url, context, true, 0);
		
		if (tid != -1) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("uid", AppData.getUser().getID() + "");
			map.put("tid", "" + tid);
			map.put("placeid", "" + wid);

			String wereUrl = AppData.getConfig().getUrl(
					Config.URL_PLACE_TUIJIAN);
			getRequestQueue().add(
					new JsonObjectPostRequest(wereUrl,
							new Listener<JSONObject>() {

								@Override
								public void onResponse(
										JSONObject response) {
									// TODO Auto-generated method
									try {
										int status = response
												.getInt("status");
										if (status == StatusCode.OK) {
											DebugLog.d("",
													"位置上传成功");
										} else {
											DebugLog.d("","位置上传失败:"+ response.getString("msg"));
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch
										// block
										e.printStackTrace();
										DebugLog.d(
												"",
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
	}

	// 数据库没有bookitem时合成item，并直接跳转到阅读
	private static BookItem item;
	private static String imageUrl;
//	public static void getBookItem(final String bid,String url,final Context context,Boolean isBanner,final int num) {
//		// TODO Auto-generated method stub
//		final boolean  banner=isBanner;
//		imageUrl=url;
//		try {
//			if (TextUtils.isEmpty(imageUrl)) {
//				imageUrl=URLEncoder.encode(imageUrl, "utf_8");
//			}
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				item=GetBookDetailUtil.getNetBookItem(mContext, bid);
//				item.bigCoverUrl = imageUrl;
////				UpdateAsyncTask asyncTask=AppData.new UpdateAsyncTask(mContext);
//				
//			}
//		}).start();
//		
//		Intent intent = new Intent(context,
//				OnlineReadingActivity.class);
//		intent.putExtra("BookItem", item);
//		intent.putExtra("isBanner", banner);
//		intent.putExtra("buynum", num);
//		context.startActivity(intent);
//		
////		String yysid="1";
////		item = new BookItem();
////		mChannel=AppData.readMetaDataFromService(mContext, "channel_num");
////		mOper = getConfig().getDeviveInfo().getOperator(mContext);
////
////		if (TextUtils.isEmpty(mChannel)) {
////			mChannel = "default";
////		}
////
////		switch (mOper) {
////		case DeviceInfo.OPERATOR_CM:
////			mOperator = "移动";
////			yysid="1";
////			break;
////		case DeviceInfo.OPERATOR_CU:
////			mOperator = "联通";
////			yysid="2";
////			break;
////		case DeviceInfo.OPERATOR_TC:
////			mOperator = "电信";
////			yysid="3";
////			break;
////
////		default:
////
////			break;
////		}
////		Map<String, String> map = new HashMap<String, String>();
////		map.put("aid", bid);
////		map.put("qdid", "" + mChannel);
////		map.put("yysid", "" + yysid);
////
////		String detailUrl = AppData.getConfig().getUrl(
////				Config.URL_DETAIL_BOOKITEM);
////		getRequestQueue().add(
////				new JsonObjectPostRequest(detailUrl,
////						new Listener<JSONObject>() {
////
////							@Override
////							public void onResponse(
////									JSONObject response) {
////								// TODO Auto-generated method
////								try {
////									int status = response
////											.getInt("status");
////									if (status == StatusCode.OK) {
////										JSONObject jsonObject=response.getJSONObject("data");
////										
////											item.bid = jsonObject.getString("bid");
////											item.cid = jsonObject.getString("cid");
////											item.name = jsonObject.getString("name");
////											item.author = jsonObject.getString("author");
////											item.status = jsonObject.getInt("status");
////											item.wordNum = jsonObject.getString("word_num");
////											item.shortDesc = jsonObject.getString("introduction");
////											item.longDesc = jsonObject.getString("long_introduction");
////											item.littleCoverUrl = jsonObject.getString("cover_url");
////											item.bigCoverUrl = imageUrl;
////											item.classFication = jsonObject.getString("class_name");
////											item.clickStr = jsonObject.getString("click_num");
////											item.freeCount = jsonObject.getInt("freenum");
////											item.totalCount = jsonObject.getInt("totalnum");
////											item.lastUpdata = jsonObject.getString("date");
////											item.lastCid=jsonObject.getString("last_cid");
////											item.lastTitle=jsonObject.getString("last_name");	
////											
////											Intent intent = new Intent(context,
////													OnlineReadingActivity.class);
////											intent.putExtra("BookItem", item);
////											intent.putExtra("isBanner", banner);
////											intent.putExtra("buynum", num);
////											context.startActivity(intent);
////									}
////
////								} catch (JSONException e) {
////									// TODO Auto-generated catch
////									// block
////									e.printStackTrace();
////								}
////							}
////						}, new ErrorListener() {
////
////							@Override
////							public void onErrorResponse(
////									VolleyError error) {
////								// TODO Auto-generated method
////								// stub
////							}
////						}, map));
//	}
	private static Detail detail;
	
	/**
	 * 映射表更改后获取书籍详情，并存到数据表中
	 * */
	public static void getBookDetailItem(final String bid,final Context context){
		
//		String yysid="1";
//		item = new BookItem();
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				item=GetBookDetailUtil.getNetBookItem(mContext, bid);
				helper.insertKBBook(item);
			}
		});
		thread.start();
//		mChannel=AppData.readMetaDataFromService(mContext, "channel_num");
//		mOper = getConfig().getDeviveInfo().getOperator(mContext);
//
//		if (TextUtils.isEmpty(mChannel)) {
//			mChannel = "default";
//		}
//
//		switch (mOper) {
//		case DeviceInfo.OPERATOR_CM:
//			mOperator = "移动";
//			yysid="1";
//			break;
//		case DeviceInfo.OPERATOR_CU:
//			mOperator = "联通";
//			yysid="2";
//			break;
//		case DeviceInfo.OPERATOR_TC:
//			mOperator = "电信";
//			yysid="3";
//			break;
//
//		default:
//
//			break;
//		}
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("aid", bid);
//		map.put("qdid", "" + mChannel);
//		map.put("yysid", "" + yysid);
//
//		String detailUrl = AppData.getConfig().getUrl(
//				Config.URL_DETAIL_BOOKITEM);
//		getRequestQueue().add(
//				new JsonObjectPostRequest(detailUrl,
//						new Listener<JSONObject>() {
//
//							@Override
//							public void onResponse(
//									JSONObject response) {
//								// TODO Auto-generated method
//								try {
//									int status = response
//											.getInt("status");
//									if (status == StatusCode.OK) {
//										JSONObject jsonObject=response.getJSONObject("data");
//										
//											item.bid = jsonObject.getString("bid");
//											item.cid = jsonObject.getString("cid");
//											item.name = jsonObject.getString("name");
//											item.author = jsonObject.getString("author");
//											item.status = jsonObject.getInt("status");
//											item.wordNum = jsonObject.getString("word_num");
//											item.shortDesc = jsonObject.getString("introduction");
//											item.longDesc = jsonObject.getString("long_introduction");
//											item.littleCoverUrl = jsonObject.getString("cover_url");
//											item.bigCoverUrl = jsonObject.getString("cover_url");
//											item.classFication = jsonObject.getString("class_name");
//											item.clickStr = jsonObject.getString("click_num");
//											item.freeCount = jsonObject.getInt("freenum");
//											item.totalCount = jsonObject.getInt("totalnum");
//											item.lastUpdata = jsonObject.getString("date");
//											item.lastCid=jsonObject.getString("last_cid");
//											item.lastTitle=jsonObject.getString("last_name");	
//											
//											helper.insertKBBook(item);
//									}
//
//								} catch (JSONException e) {
//									// TODO Auto-generated catch
//									// block
//									e.printStackTrace();
//								}
//							}
//						}, new ErrorListener() {
//
//							@Override
//							public void onErrorResponse(
//									VolleyError error) {
//								// TODO Auto-generated method
//								// stub
//							}
//						}, map));
	}
	
	/**
	 * 		old获取详情接口
	 * */
	private BookItem getBookItem(String bid,String url) {
		// TODO Auto-generated method stub
		try {		
			detail = BookHelper.loadDetail(bid);
		} catch (Exception e) {
			// TODO: handle exception
			((BaseActivity) mContext).showToast("参数有误", Toast.LENGTH_LONG);
		}

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
			item.littleCoverUrl = detail.getCoverUrl();
			if (url.equals("null")||TextUtils.isEmpty(url)) {
				
				item.bigCoverUrl = detail.getBigCoverUrl();
			}else {
				item.bigCoverUrl = url;
			}
			item.classFication = detail.getClassification();
			item.clickStr = detail.getClick();
			item.freeCount = detail.getFreeChapterCount();
			item.totalCount = detail.getTotalChapterCount();
		} else {
			DebugLog.e("得到的item", "为空");
		}
		return item;

	}

	/**
	 * 开启详情页
	 * */
	public static void startBookDetil(String bid) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(mContext, BookDetail.class);
		intent.putExtra("bid", bid);
		mContext.startActivity(intent);
	}

	public static DBDataHelper getDataHelper() {
		int userid = AppData.getUser().getID();
		String name = AppData.getConfig().getDataDBName(userid);
		DBDataHelper helper = (DBDataHelper) db.open(name, DBManager.TYPE_DATA);
		return helper;
	}

	// 得到移动章节的数据库helper
	public static DBContentHelper getContentHelper(int onlineID) {
		String name = AppData.getConfig().getContentDBName(onlineID);
		DBContentHelper helper = (DBContentHelper) db.open(name,
				DBManager.TYPE_CHAPTER);
		return helper;
	}

	// 得到XN章节的数据库helper
	public static DBXNContentHelper getXNContentHelper(int onlineID) {
		String name = AppData.getConfig().getXNContentDBName(onlineID);
		DBXNContentHelper helper = (DBXNContentHelper) db.open(name,
				DBManager.TYPE_XN_CHAPTER);
		return helper;
	}
	
	// 得到游戏apk推荐列表helper
//	public static DBGameHelper getGameDBHelper() {
//		String name = AppData.getConfig().getApkDBName();
//		DBGameHelper helper = (DBGameHelper) db.open(name,
//				DBManager.TYPE_XY_GAME);
//		return helper;
//	}
//	public static void closeDBGame(int onlineID) {
//		String name = AppData.getConfig().getApkDBName();
//		db.close(name);
//	}
	
	
	public static void closeDBContent(int onlineID) {
		String name = AppData.getConfig().getContentDBName(onlineID);
		db.close(name);
	}

	public static void closeXNDBContent(int onlineID) {
		String name = AppData.getConfig().getXNContentDBName(onlineID);
		db.close(name);
	}

	public static Config getConfig() {
		return config;
	}
	public static int getColor(int a ) {
		return mContext.getResources().getColor(a);
	}

	public static ReadClient getClient() {
		return client;
	}

	public static ClientUser getUser() {
		return user;
	}

	public static RequestQueue getRequestQueue() {
		return mRequestQueue;
	}
	
	@SuppressLint("NewApi")
	public static void clear() {
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		clearUser();
		client.getTaskManager().delAllTask();
		client.getTaskManagerRead().delAllTask();
		db.clear();
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Iqiyoo.destroy();
			}
		});
		thread.start();
		// if(CommonUtil.isServiceRunning(mContext,serviceName)) {
		// BoyiService.stopBoyiService(mContext,serviceAction);
		// }
	}

	private final static int COUNT_CREATE_ACCOUNT = 3;
	private static int mCurCount = 0;

	public static void createAccount() {
		loginUrl = AppData.getConfig().getUrl(Config.URL_ACCOUNT_CREATE);
		if (mCurCount >= COUNT_CREATE_ACCOUNT) {
			// 自动注册失败
			AppData.getUser().setLogin(false);
			AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_FAILED);
			return;
		}
		DebugLog.e("注册次数", mCurCount+"");
		DebugLog.e("注册的URL是：", loginUrl);
		mCurCount++;
		getRequestQueue().add(
				new JsonObjectPostRequest(loginUrl, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							int status = response.getInt("status");
							if (status == StatusCode.OK) {
								JSONObject data = response
										.getJSONObject("data");
								ClientUser user = AppData.getUser();
								int uid=data.getInt("userid");
								if (uid != 0) {									
									user.setID(data.getInt("userid"));
								}
                                DebugLog.e("用户id", user.getID() + "网络获取id"+data.getInt("userid"));
								getGoStore();
								return;
							} else {								
//								createAccount();
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							AppData.getUser().setLogin(false);
//							AppData.getClient().sendCallBackMsg(
//									CallBackMsg.LOGIN_FAILED);
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
//						createAccount();
//						AppData.getUser().setLogin(false);
//						AppData.getClient().sendCallBackMsg(
//								CallBackMsg.LOGIN_FAILED);
					}

				}, RequestParam.getSDKCountCreateParam()));
	}

	private static void clearUser() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", AppData.getUser().getID() + "");

		String url = AppData.getConfig().getUrl(Config.URL_USER_TIME_LONG);
		getRequestQueue().add(
				new JsonObjectPostRequest(url, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							int status = response.getInt("status");
							if (status == StatusCode.OK) {
								DebugLog.e("用户推出应用", user.getID() + "");

							} else {
//								createAccount();
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
					}

				}, map));
	}

	/**
	 * 请求映射表
	 * */
	private static String mOperator = "";
	private static String mChannel;
	private static int mOper;

	public static void getMapTable() {

		SharedPreferences mySharedPreferences = mContext
				.getSharedPreferences("everytime",
						Application.MODE_PRIVATE);

		long str = mySharedPreferences.getLong("mapTableTime", 0);
		// DebugLog.e("上次时间", str+"");

		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastHour = time.format(str).substring(11, 13);

		if (str == 0) {

			Date nowTime = new Date();
			long time1 = nowTime.getTime();


			String dataString = time.format(nowTime);

			lastHour = dataString.substring(11, 13);
			// String day = dataString.substring(8, 10);

			SharedPreferences.Editor editor = mySharedPreferences.edit();
			// Log.e("当前时间时间", time1+"");

			editor.putLong("mapTableTime", time1);

			editor.commit();

			//handler.postDelayed(dayCommandTask, 500);

		} else {

			long time2 = new Date().getTime();
			
			if (time2 - str >  60 * 60 * 1000) {
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				editor.clear();
				editor.putLong("mapTableTime", time2);
				editor.commit();
				
				mChannel=AppData.readMetaDataFromService(mContext, "channel_num");
				mOper = getConfig().getDeviveInfo().getOperator(mContext);
				
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
										JSONObject array = response.getJSONObject("data");
										if (array == null) {
											return;
										} else {
											JSONObject objChannel = array
													.getJSONObject(mChannel);
											JSONObject objOperator = objChannel
													.getJSONObject("operator");
											
											String mapTable = objOperator.getJSONObject(mOperator)
													.getString("mappingid");
											mapTable = mapTable.substring(1,
													mapTable.length() - 1);
											String[] array1 = mapTable.split(",");
											SharedPreferences sp = mContext
													.getSharedPreferences("bidMapTable",
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
											}
											for (int i = 0; i < keys.size(); i++) {
												if (!values.get(i).equals(
														sp.getString(keys.get(i), ""))) {
													DebugLog.e(
															"映射表有变化",
															"进行删除"
																	+ AppData
																	.getConfig()
																	.getXNContentName(
																			Integer.parseInt(keys
																					.get(i))));
													File f = new File(AppData.getConfig()
															.getXNContentName(
																	Integer.parseInt(keys
																			.get(i))));
													if (f.exists()) {
														deleteDir(f);
													}
													if (AppData.getDataHelper().foundBookBid(keys.get(i))) {		
														helper.deleteQBBook(keys.get(i));
														getBookDetailItem(keys.get(i), mContext);
														
													}
												}
											}
											SharedPreferences.Editor editor = sp.edit();
											editor.clear();
											for (int i = 0; i < keys.size(); i++) {
												DebugLog.e("键是--" + keys.get(i), "值是--"
														+ values.get(i));
												//
												editor.putString(keys.get(i), values.get(i));
												editor.commit();
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
								DebugLog.e("appdata", "请求映射表失败");
							}
						}, map));
			}
		}
	}
	
	/**
	 *   推荐位，九本书
	 * */
	public static List<String> getTuijianList(){
		List<String>jsonList=new ArrayList<String>();
		SharedPreferences myShared = mContext
				.getSharedPreferences("everyTuijian",
						Application.MODE_PRIVATE);
		String jsonString=myShared.getString("tuijian", "");
		if (! TextUtils.isEmpty(jsonString)) {
			jsonList=JsonUtil.getJson(jsonString);
		}else {
		
		HttpClient client=new DefaultHttpClient();
		String recommendUrl=AppData.getConfig().getUrl(Config.URL_BOOK_RECOMMAND);
		HttpGet get=new HttpGet(recommendUrl+"1?channel="+channel+"&limit=9&type=3");
		HttpResponse response;
		try {
			response=client.execute(get);
			if (response.getStatusLine().getStatusCode()==200) {
				HttpEntity entity=response.getEntity();
				
				String jsonStr=EntityUtils.toString(entity, "utf_8");
				myShared.edit().putString("tuijian", jsonStr).commit();
				jsonList=JsonUtil.getJson(jsonStr);
				
			}	
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		return jsonList;
	}
	public static int count = 0;
	public static void getMapTable1() {
		mChannel = getConfig().getDeviveInfo().getChannel();
		mOper = getConfig().getDeviveInfo().getOperator(mContext);
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
		getRequestQueue().add(new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				try {
					JSONObject responseJson = new JSONObject(response);
					int status = responseJson.getInt("status");
					if (status == StatusCode.OK) {
						JSONObject array = responseJson.getJSONObject("data");
						if (array == null) {
							return;
						} else {
							JSONObject obj = array.getJSONObject(mChannel)
									.getJSONObject("operator");

							if (!obj.has(mOperator)) {
								if (count < 4) {
									getMapTable1();
									count++;
								}
								return;
							}

							String mapTable = obj.getJSONObject(mOperator)
									.getString("mappingid");
							mapTable = mapTable.substring(1,
									mapTable.length() - 1);
							String[] array1 = mapTable.split(",");
							SharedPreferences sp = mContext
									.getSharedPreferences("bidMapTable",
											Application.MODE_PRIVATE);
							for (int i = 0; i < array1.length; i++) {
								String[] array2 = array1[i].split(":");

								String key = array2[0].substring(1,
										array2[0].length() - 1);
								String value = array2[1].substring(1,
										array2[1].length() - 1);
								// DebugLog.e("键是--"+key, "值是--"+value);
								// SharedPreferences
								// sp=mContext.getSharedPreferences("bidMapTable",Application.MODE_PRIVATE
								// );
								String mValue = sp.getString(key, "");
								DebugLog.e("映射表内容", "老的是" + mValue + "新的；"
										+ value);
								if (!mValue.equals("") && !value.equals(mValue)) {
									DebugLog.e("映射表内容变化", "老的是" + mValue
											+ "新的；" + value);
									File f = new File(AppData.getConfig()
											.getXNContentName(
													Integer.parseInt(key))); // 缓存文件目录是根据移动id键的
									if (f.exists()) {
										deleteDir(f);
									}
								}
							}
							SharedPreferences.Editor editor = sp.edit();
							editor.clear();

							for (int i = 0; i < array1.length; i++) {
								String[] array2 = array1[i].split(":");
								String key = array2[0].substring(1,
										array2[0].length() - 1);
								String value = array2[1].substring(1,
										array2[1].length() - 1);
								DebugLog.e("键是--" + key, "值是--" + value);
								// SharedPreferences
								// sp=mContext.getSharedPreferences("bidMapTable",Application.MODE_PRIVATE
								// );
								// SharedPreferences.Editor editor=sp.edit();
								editor.putString(key, value);
								editor.commit();
							}

						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		}));
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
	public static boolean isIsOpenLast() {
		return mIsOpenLast;
	}

	public static void setIsOpenLast(boolean isOpenLast) {
        if(mIsOpenLast == isOpenLast) {
        	return;
        }
		mIsOpenLast = isOpenLast;
        Editor editor = sp.edit();
        editor.putBoolean("mIsOpenLast", mIsOpenLast);
        editor.commit();
	}
	public static boolean isAutoUpdate() {
		return mIsAutoUpdate;
	}
	
	public static void setIsAutoUpdate(boolean isAutoUpdate) {
		if(mIsAutoUpdate == isAutoUpdate) {
			return;
		}
		mIsAutoUpdate = isAutoUpdate;
		Editor editor = sp.edit();
		editor.putBoolean("mIsAutoUpdate", mIsAutoUpdate);
		editor.commit();
	}
	public static boolean showRecommend()
	{
		return mShowRecommend;
	}
	public static void setShowRecommend(boolean showRecommend)
	{
		if(mShowRecommend==showRecommend)
		{
			return;
		}
		mShowRecommend=showRecommend;
		Editor editor=sp.edit();
		editor.putBoolean("mShowRecommend", mShowRecommend);
		editor.commit();
	}
	
	public static String getUrl(String orginUrl)
	{
		return orginUrl+"/channel/"+mChannel+"/version/"+AppData.VERSION_CODE;
	}
	public static void saveLastTime()
	{
		lastTime=System.currentTimeMillis();
	}
}
