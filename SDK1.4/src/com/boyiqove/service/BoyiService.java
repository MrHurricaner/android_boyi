package com.boyiqove.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boyiqove.AppData;
import com.boyiqove.config.ClientUser;
import com.boyiqove.config.Config;
import com.boyiqove.db.DBDataHelper;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.BoyiMessage;
import com.boyiqove.entity.LoginInfo;
import com.boyiqove.entity.Notice;
import com.boyiqove.entity.PayInfo;
import com.boyiqove.library.volley.RequestQueue;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.toolbox.JsonObjectRequest;
import com.boyiqove.library.volley.toolbox.StringRequest;
import com.boyiqove.library.volley.toolbox.Volley;
import com.boyiqove.protocol.JsonObjectPostRequest;
import com.boyiqove.protocol.RequestParam;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.task.CallBackMsg;
import com.boyiqove.util.DebugLog;

import com.bytetech1.sdk.BookHelper;
import com.bytetech1.sdk.Iqiyoo;
import com.bytetech1.sdk.data.Detail;

import android.R.array;
import android.R.integer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.Toast;

public class BoyiService extends Service {
	private final static String TAG = "BoyiService";

	public static String BOYISERVICE_NAME = "com.boyiqove.service.BoyiService";

	//  private BookItem bookItem;
	//  private int inId; // 用于排重的推荐书籍的id
	// private ArrayList lindIds=new ArrayList();

	private RequestQueue 	mRequestQueue;
	protected RequestQueue getRequestQueue() {
		if(null == mRequestQueue) {
			mRequestQueue = AppData.getRequestQueue();
		}

		return mRequestQueue;
	}

	public final static int MSG_LOGIN = 1;
	public final static int MSG_PAYINFO = 2;
	public final static int MSG_RECOMMAND = 3;
	public final static int MSG_UPDATE_USERINFO = 4;
	public final static int MSG_REQUEST_INFO = 5;

	private  Handler mHanderProxy = new Handler() { //服务的handler

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch(msg.what) {
			
			case MSG_LOGIN:
			{
				LoginInfo info = (LoginInfo)msg.obj;
				login(info.username, info.password);
			}
			break;
			case MSG_PAYINFO:
			{
				getRequestPayInfo();//下载支付购买列表信息
			}
			
			
			case MSG_RECOMMAND:  // 请求服务进行首推
//			Log.e("服务首推", "快播请求服务首推了");	
			getRequestRecommand(); 
			
			break;
			
			
			case MSG_UPDATE_USERINFO:
				getRequestPostUser((HashMap<String, String>)msg.obj);
				break;
				
			case MSG_REQUEST_INFO:
				
				//注册账户
				
//				if(AppData.getUser().isLogin()) {
//					getMessageAfterLogin();
//				} else {
					getRequestLogin();
//				}

				break;
			default:
				DebugLog.d(TAG, "unknown msg:" + Integer.toHexString(msg.what));
				break;

			}
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		DebugLog.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		DebugLog.e(TAG, "博易的服务已经#####onCreate");
		
//		getRequestNotice();
		
		AppData.getClient().setProxyHandler(mHanderProxy);
//
		if(AppData.getUser().isLogin()) {
			AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_SUCCESSFUL);

		}  else {
			getRequestLogin();  // 服务开启就注册账户
			getGoStore();
			getRequestRecommand();  // 请求首推
			getRequestNotice();
			
		}
	}

	private void getGoStore() {
		// TODO Auto-generated method stub
		String url = AppData.getConfig().getUrl(Config.URL_GOSTORE_TIME);
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", AppData.getUser().getID()+"");
		DebugLog.d(TAG, url);
		getRequestQueue().add(new JsonObjectPostRequest(url, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, response.toString());
				try {
					int status = response.getInt("status");
					if(status == StatusCode.OK) {
					      

					} else {
						DebugLog.d(TAG, "用户自动注册失败:" + response.getString("msg"));
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, new ErrorListener(){

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, error.toString());
			}

		}, map));	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		DebugLog.d(TAG, "onStartCommand");

		//        if(AppData.getUser().isLogin()) {
		//            AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_SUCCESSFUL);
		//        } else {
		//            AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_FAILED);
		//        }
		//        
		//        if(AppData.getUser().getNoticeList().size() > 0) {
//		        	AppData.getClient().sendCallBackMsg(CallBackMsg.NOTICE_SUCCESSFUL);
		//        }

		//        if(AppData.getUser().isIsUpdate()) {
		//        	AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_USER_INFO);
		//        }

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DebugLog.d(TAG, "onDestory");

		AppData.getClient().setNullProxyHander(mHanderProxy);
	}


	/*
	 * 处理用户登陆注册相关
	 */
	private void getRequestLogin() {
//		String username = AppData.getUser().getUserName();
//		if(null == username || username.equals("")){
//			// 1. 本地用户名不存在，则自动注册用户
//			createAccount();
//		} else {
//			// 2. 用户登陆
//			String password = AppData.getUser().getPassword();
//			if(null != password && !password.equals("")) {
//				login(username, password);
//			}
//		}
		
		
	}
	

	private final static int COUNT_CREATE_ACCOUNT = 3;
	private int mCurCount = 0;
	
//	private void createAccount() {
//		if(mCurCount >= COUNT_CREATE_ACCOUNT) {
//			// 自动注册失败
//			AppData.getUser().setLogin(false);
//			AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_FAILED);
//			return;
//		}
//
//
//		mCurCount++;
//		String url = AppData.getConfig().getUrl(Config.URL_ACCOUNT_CREATE);
//		DebugLog.d(TAG, url);
////		DebugLog.e("用户的手机号是：：", Iqiyoo. getPhoneNumber ());
//		getRequestQueue().add(new JsonObjectPostRequest(url, new Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject response) {
//				// TODO Auto-generated method stub
//				DebugLog.d(TAG, response.toString());
//				try {
//					int status = response.getInt("status");
//					if(status == StatusCode.OK) {
//						JSONObject data = response.getJSONObject("data");
//						ClientUser user = AppData.getUser();
//
////						user.setToken(data.getString("token"));
//						user.setID(data.getInt("userid"));
//
//						DebugLog.e("用户id", user.getID()+"");
////						user.setLogin(true);
//
//						DebugLog.d(TAG, "用户自动注册成功");
//						
////						AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_SUCCESSFUL);
////						getMessageAfterLogin();
//
//					} else {
//						DebugLog.d(TAG, "用户自动注册失败:" + response.getString("msg"));
//						createAccount();
//					}
//
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					DebugLog.d(TAG, "服务器数据解析错误:" + response.toString());
//
//					AppData.getUser().setLogin(false);
//
//					AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_FAILED);
//				}
//
//			}
//		}, new ErrorListener(){
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				// TODO Auto-generated method stub
//				DebugLog.d(TAG, error.toString());
//
//				AppData.getUser().setLogin(false);
//				AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_FAILED);
//			}
//
//		}, RequestParam.getSDKCountCreateParam()));	
//
//	}


	/*
	 * 用户登陆 
	 */
	private void login(String username, String password) {
		String url = AppData.getConfig().getUrl(Config.URL_ACCOUNT_LOGIN);
		DebugLog.d(TAG, url);

		getRequestQueue().add(new JsonObjectPostRequest(url, new Listener<JSONObject>(){

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, response.toString());
				try {
					int status = response.getInt("status");
					if(status == StatusCode.OK) {

						JSONObject data = response.getJSONObject("data");
						ClientUser user = AppData.getUser();
						user.setToken(data.getString("token"));
						user.setID(data.getInt("id"));
						user.setNickName(data.getString("nickname"));
						user.setSex(data.getInt("sex"));
						user.setSignature(data.getString("signature"));
						user.setMobile(data.getString("mobile"));
						user.setPhotoUrl(data.getString("logo")); // 用户头像
						user.setType(data.getInt("user_type"));
						user.setBalance(data.getInt("money"));

						user.setLogin(true);

						AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_SUCCESSFUL);
						DebugLog.d(TAG, "用户登录成功");

						getMessageAfterLogin();   // 更新所有用户信息

					} else {
						String msg = response.getString("msg");
						DebugLog.d(TAG, "用户登陆失败 :" + msg);
						AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_FAILED, msg);

						AppData.getUser().setLogin(false);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					String msg = "服务器数据不匹配";
					AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_FAILED, msg);
				}

			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, error.toString());

				String msg = "网络错误";
				AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_FAILED, msg);
			}

		}, RequestParam.getLoginParam(username, password)));

	}


	private void getMessageAfterLogin() {
		getRequestNotice();
//		getRequestUserInfo();
		getRequestMessage();
		//getRequestAttention();
		getRequestPayInfo();
				
		getRequestBookSync();
	}

	/*
	 * 请求书架推荐的
	 * */

	private Detail detail;
	private List<String>bidList;
	private String bid;
	private BookItem item;
	private void getRequestRecommand() {
//		 判断推荐
		        if(AppData.getUser().isRecommand()) {
		        	
		        	return;   // 推荐过的话就直接return
		        }
	        
//		int sex = AppData.getUser().getSex();
		
		String url = AppData.getConfig().getUrl(Config.URL_BOOK_RECOMMAND) +"?tid=11&limit=9";
		
		if (bidList !=null) {
			return;
		}else {
			bidList=new ArrayList<String>();	
		}
		getRequestQueue().add(new JsonObjectRequest(url, null, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub

				try {
					int status = response.getInt("status");
					if(StatusCode.OK == status) {
						JSONArray array = response.getJSONArray("data");
						final DBDataHelper helper = AppData.getDataHelper();
						//	List<BookItem> mBookList = AppData.getDataHelper().getBookShelfList();
						
						for (int i = 0; i <array.length(); i++) {
//							for (int i = 0; i <1; i++) {

							JSONObject obj = array.getJSONObject(i);
							bid=obj.getString("aid");
							bidList.contains(bid);
							DebugLog.e("推荐书id", bid);
							if (! bidList.contains(bid)) {
								
								bidList.add(bid);
							}
						}
						
//						for (int i = 0; i < bidList.size(); i++) {
							
//							final int  j=i;
//							final String mBid=bidList.get(i);
//							DebugLog.e("首推id", mBid);
								
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									
									for (int i = 0; i < bidList.size(); i++) {
//									String mBid=bidList.get(i);
									
									Detail detail = BookHelper.loadDetail(bidList.get(i));
									
									item = new BookItem();
									if(detail !=null){										
									item.bid = detail.getBid();
									item.cid=detail.getFirstCid();
									item.name=detail.getName();
									item.author = detail.getAuthor();
									item.status = detail.getStatus();
									item.wordNum=detail.getWord();
									item.shortDesc=detail.getIntroduction();
									item.longDesc=detail.getDesc();
									item.littleCoverUrl=detail.getCoverUrl();
									item.bigCoverUrl=detail.getBigCoverUrl();
									item.classFication=detail.getClassification();
									item.clickStr=detail.getClick();
									item.freeCount=detail.getFreeChapterCount();
									item.totalCount=detail.getTotalChapterCount();
									item.isUpdata=0;
									item.timeStamp=0;
//									Log.e("书籍条目：：：", item.bid+item.cid+item.name+item.author+
//											item.status+ item.wordNum+
//											item.shortDesc+item.longDesc+
//											item.littleCoverUrl+item.bigCoverUrl+item.classFication+
//											item.clickStr+ item.freeCount+item.totalCount+"");
									if (! AppData.getDataHelper().foundBookBid(item.bid)) {
										
										DebugLog.e(item.name, "==数据库没有这本书，存上");
										
									helper.insertKBBook(item);
									
									}
									
									}
									AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_BOOKSHELF);  // 让书架更新书架
								}
							}}).start();
							
//						}
						
						/**
							for(int j = 0; j < array.length(); j++) {

								JSONObject obj = array.getJSONObject(j);	
								inId=obj.getInt("id");
								lindIds.contains(inId);// 判断 集合中是否包括的某元素

								if (! lindIds.contains(inId)) {

								BookItem item = new BookItem();
								System.out.println("数据库中已经有的id是"+inId+".....集合中不包括元素........更新到的新的id是"+obj.getInt("id"));
								item.onlineID = obj.getInt("id");
								item.author = obj.getString("author");
								item.status = obj.getInt("status");
								item.coverUrl = obj.getString("cover");
								item.detailUrl = obj.getString("url");
								item.name = obj.getString("title");
								item.lastChapterPos = 0;// 上次章回
								item.lastPosition = 0;
								helper.insertBook(item);  // 把得到的推荐存到数据库
								}
							}

						 */

						DebugLog.d(TAG, "推荐书籍获取成功");

						AppData.getUser().setRecommand(); //  %%%%%%表示已经推荐过了

						AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_BOOKSHELF);  // 让书架更新书架

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
//		
//		getRequestQueue().add(new JsonObjectRequest(url, null, new Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject response) {
//				// TODO Auto-generated method stub
//				DebugLog.d(TAG, response.toString());
//				try {
//
//					int status = response.getInt("status");
//					if(StatusCode.OK == status) {
//
//						JSONArray array = response.getJSONArray("data");
//
//						List<Notice> list = AppData.getUser().getNoticeList();
//						for(int i = 0; i < array.length(); i++) {
//							JSONObject obj = array.getJSONObject(i);
//							Notice notice = new Notice();
//							notice.title = obj.getString("title");
//							notice.content = obj.getString("content");
//							notice.date = obj.getLong("date");
//							notice.url = obj.getString("url");
//							list.add(notice);
//							DebugLog.e("获得了公告：：长度为：", list+"");
////							AppData.getUser().setmNoticeList(list);
//						}
//
//						DebugLog.d(TAG, "公告消息获取成功");
//						AppData.getClient().sendCallBackMsg(CallBackMsg.NOTICE_SUCCESSFUL);
//
//					} else {
//
//						DebugLog.d(TAG, "公告消息获取失败:" + response.getString(response.getString("msg")));
//					}
//
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					DebugLog.d(TAG, response.toString());
//				}
//
//			}
//		}, new ErrorListener(){
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				// TODO Auto-generated method stub
//				DebugLog.d(TAG, error.toString());
//			}
//
//		}));

//	}

	/*
	 * 用户数据更新 , 需要每隔一段时间请求一次
	 */
	private void getRequestUserInfo() {
		String token = AppData.getUser().getToken();
		if(null == token || token.equals("")) {
			throw new RuntimeException();
		}


		String url = AppData.getConfig().getUrl(Config.URL_UPDATE_USERINFO);

		//long last = AppData.getUser().getLastUserInfoTime();
		long last = AppData.getUser().getLastMessageTime();
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("timestamp", last + "");

		DebugLog.d(TAG, url);
		DebugLog.d(TAG, map.toString());

		getRequestQueue().add(new JsonObjectPostRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, response.toString());
				try {
					int status = response.getInt("status");
					if(StatusCode.OK == status) {
						JSONObject data = response.getJSONObject("data");
						ClientUser user = AppData.getUser();

						user.setMessage(data.getJSONObject("message").getInt("number"));
						user.setAttention(data.getJSONObject("attention").getInt("number"));
						user.setMission(data.getJSONObject("task").getInt("number"));
						user.setMissionUrl(data.getJSONObject("task").getString("url"));
						user.setGift(data.getJSONObject("gift").getInt("number"));
						user.setGiftUrl(data.getJSONObject("gift").getString("url"));
						user.setFans(data.getJSONObject("fans").getInt("number"));
						user.setBalance(data.getJSONObject("money").getInt("number"));

						//user.setIsUpdate(true);

						//user.setLastUserInfoTime(response.getLong("timestamp"));
						//user.setLastMessageTime(response.getLong("timestamp"));

						DebugLog.d(TAG, "用户数据更新成功");

						AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_USER_INFO);

					} else {
						DebugLog.d(TAG, "用户数据更新失败:" + response.getString("msg"));
					}


				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}, new ErrorListener(){

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}

		}, map)) ;
	}

	/*
	 * 获取消息列表
	 */
	private void getRequestMessage() {
		String token = AppData.getUser().getToken();
		if(null == token || token.equals("")) {
			throw new RuntimeException();
		}
		long last = AppData.getUser().getLastMessageTime();
		String url = AppData.getConfig().getUrl(Config.URL_MESSAGE_UPDATE);
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("timestamp", last + "");

		getRequestQueue().add(new JsonObjectPostRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, response.toString());

				try {
					int status = response.getInt("status");
					if(StatusCode.OK == status) {
						JSONArray data = response.getJSONArray("data");
						List<BoyiMessage> plist = AppData.getUser().getPrivateMsgList();
						List<BoyiMessage> slist = AppData.getUser().getSystemMsgList();

						for(int i = 0; i < data.length(); i++) {
							JSONObject obj = data.getJSONObject(i);

							BoyiMessage msg = new BoyiMessage();
							msg.type = obj.getInt("type");
							msg.id = obj.getInt("id");
							msg.content = obj.getString("content");
							//msg.msgID= obj.getInt("msg_id");
							msg.status = obj.getInt("status");
							msg.time = obj.getLong("addtime");
							msg.fromName = obj.getString("from");
							msg.fromID = obj.getInt("senderid");
							msg.toID = obj.getInt("receiverid");

							if(msg.type == BoyiMessage.TYPE_PRIVATE) {
								plist.add(msg);

							} else {
								slist.add(msg);
							}
						}
						AppData.getUser().setLastMessageTime(response.getLong("timestamp"));

						AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_USER_MESSAGE);
						DebugLog.d(TAG, "用户消息更新成功");

					} else {
						DebugLog.d(TAG, "用户消息获取失败：" + response.getString("msg"));
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

	/*
	 * 获取关注列表
	 */

	//    private void getRequestAttention() {
	//    	String token = AppData.getUser().getToken();
	//        if(null == token || token.equals("")) {
	//        	throw new RuntimeException();
	//        }
	//        String url = AppData.getConfig().getUrl(Config.URL_USER_ATTENTION);
	//    	Map<String, String> map = new HashMap<String, String>();
	//        map.put("token", token);
	//        
	//    	DebugLog.d(TAG, url);
	//        
	//    	getRequestQueue().add(new JsonObjectPostRequest(url, new Listener<JSONObject>() {
	//
	//			@Override
	//			public void onResponse(JSONObject response) {
	//				// TODO Auto-generated method stub
	//				DebugLog.d(TAG, response.toString());
	//                
	//                try {
	//					int status = response.getInt("status");
	//                    
	//					if(StatusCode.OK == status) {
	//                        
	//						JSONArray data = response.getJSONArray("data");
	//						List<Attention> list = AppData.getUser().getAttentionList();
	//                        list.clear();
	//                        
	//						for(int i = 0; i < data.length(); i++) {
	//                            JSONObject obj = data.getJSONObject(i);
	//							Attention item = new Attention();
	//                            
	//							item.authorID = obj.getInt("id");
	//                            item.photoUrl = obj.getString("logo");
	//                            item.level = obj.getString("level");
	//                            item.author = obj.getString("author");
	//                            item.notice = obj.getString("signature");
	//                            item.qqgroup = obj.getString("qq");
	//                            item.webchat = obj.getString("weixin");
	//                            
	//                            JSONArray articles = obj.getJSONArray("article");
	//                            for(int j = 0; j < articles.length(); j++) {
	//                            	Attention.Article art = new Attention.Article();
	//                                art.id = articles.getJSONObject(j).getInt("articleid");
	//                                art.name = articles.getJSONObject(j).getString("title");
	//                                item.articleList.add(art);
	//                            }
	//                            
	//                            list.add(item);
	//						}
	//                        
	//						
	//						DebugLog.d(TAG, "关注作者列表获取成功");
	//                        AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_USER_ATTENTION);
	//                        
	//					} else {
	//						DebugLog.d(TAG, "关注作者列表获取失败:" + response.getString("msg"));
	//					}
	//					
	//				} catch (JSONException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//			}
	//    		
	//    	}, new ErrorListener() {
	//
	//			@Override
	//			public void onErrorResponse(VolleyError error) {
	//				// TODO Auto-generated method stub
	//				DebugLog.d(TAG, error.toString());
	//			}
	//    		
	//    	}, map));
	//    }


	/*
	 * 支付相关的信息
	 */
	private void getRequestPayInfo() {
		String token = AppData.getUser().getToken();

		if(null == token || token.equals("")) {
			throw new RuntimeException();
		}

		String url = AppData.getConfig().getUrl(Config.URL_UPDATE_PAYINFO);
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);

		DebugLog.d(TAG, url);

		getRequestQueue().add(new JsonObjectPostRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, response.toString());

				try {
					int status = response.getInt("status");
					if(StatusCode.OK == status) {
						JSONArray array;
						array = response.getJSONObject("data").getJSONArray("discount");

						SparseIntArray dlist = AppData.getUser().getDiscountList();
						for(int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							int chapterNum = obj.getInt("number");
							int rate = obj.getInt("rate");
							dlist.append(chapterNum, rate);
						}

						DebugLog.d(TAG, "折扣信息获取成功");
						AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_DISCOUNT_SUCCESS);

						array = response.getJSONObject("data").getJSONArray("payList");
						List<PayInfo> plist = AppData.getUser().getPayList();
						plist.clear();
						for(int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							PayInfo item = new PayInfo();
							item.rmb = obj.getInt("pay");
							item.give = obj.getInt("presented");

							plist.add(item);
						}
						DebugLog.d(TAG, "支付信息获取成功");
						AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_PAYINFO_SUCCESS);

					} else {
						DebugLog.d(TAG, "支付折扣相关信息获取失败" + response.getString("msg"));
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

				//AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_DISCOUNT_FAILED);

				AppData.getClient().sendCallBackMsg(CallBackMsg.UPDATE_PAYINFO_FAILED);
			}

		}, map));

	}   

	private void getRequestPostUser(HashMap<String, String> map) {
		String url = AppData.getConfig().getUrl(Config.URL_ACCOUNT_MODIFY);

		getRequestQueue().add(new JsonObjectPostRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, response.toString());

				try {
					int status = response.getInt("status");

					if(StatusCode.OK == status) {
						DebugLog.d(TAG, "更新用户数据到服务器成功");
					} else {
						DebugLog.d(TAG, "更新用户数据到服务器失败:" + response.getString("msg"));
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

		}, map));
	}
	
	
	// 请求同步后台书架，	
	private void getRequestBookSync() {
		
		String token = AppData.getUser().getToken();
		if(null == token || token.equals("")) {
			throw new RuntimeException();
		}
		String url = AppData.getConfig().getUrl(Config.URL_BOOK_SYNC);
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);

		getRequestQueue().add(new JsonObjectPostRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, response.toString());

				try {
					int status = response.getInt("status");

					if(StatusCode.OK == status) {
                        JSONArray data = response.getJSONArray("data");
                        
                        List<BookItem> list = new ArrayList<BookItem>();
                        for(int i = 0; i < data.length(); i++) {
                        	
                            JSONObject obj = data.getJSONObject(i);
                        	BookItem item = new BookItem();
                            
                            item.onlineID = obj.getInt("id");
                            item.name = obj.getString("title");
                            item.status = obj.getInt("status");
                            item.coverUrl = obj.getString("cover");
                            item.detailUrl = obj.getString("url");
                            item.lastChapterPos = obj.getInt("lastChapter");
                            item.lastPosition = obj.getInt("lastPosition");
                        	list.add(item);
                        }
                        DebugLog.e(TAG, "正在同步书架");
                        // 更新数据库, 通知书架更新
                        AppData.getDataHelper().updateBook(list);
                        
                        DebugLog.d(TAG, "同步书架书籍成功");
//					JSONArray data1 = response.getJSONArray("data");
//
//						List<BookItem> list1 = new ArrayList<BookItem>();
//						for(int i = 0; i < data.length(); i++) {
//							JSONObject obj = data.getJSONObject(i);
//							BookItem item = new BookItem();
//
//							item.onlineID = obj.getInt("id");
//							item.name = obj.getString("title");
//							item.status = obj.getInt("status");
//							item.coverUrl = obj.getString("cover");
//							item.detailUrl = obj.getString("url");
//							item.lastChapterPos = obj.getInt("lastChapter");
//							item.lastPosition = obj.getInt("lastPosition");
//
//							list.add(item);
//						}
//
//						// 更新数据库, 通知书架更新
//						AppData.getDataHelper().updateBook(list);
//
//						DebugLog.d(TAG, "同步书架书籍成功");
				} else {
						DebugLog.d(TAG, "同步书架书籍失败:" + response.getString("msg"));
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_SUCCESSFUL_SYNC);
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				DebugLog.d(TAG, error.toString());

				AppData.getClient().sendCallBackMsg(CallBackMsg.LOGIN_SUCCESSFUL_SYNC);
			}

		}, map));
	}



	// 启动Service
		public static void startBoyiService(Context context,String action) {
			Intent intent = new Intent(action);
			context.startService(intent);
		}

		// 停止Service
		public static void stopBoyiService(Context context,String action) {
			Intent intent = new Intent(action);
			context.stopService(intent);
		}

}
