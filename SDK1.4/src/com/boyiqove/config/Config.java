package com.boyiqove.config;

import java.io.File;
import java.util.HashMap;

import com.boyiqove.R;
import com.boyiqove.util.DebugLog;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

public class Config {
	private final static String TAG = "Config";
    
	private final static String KEY_SHELF_MODE = "isGrid";
	private final static String KEY_LAST_USERID = "lastUserID";
    public final static String KEY_LAST_OPEN = "lastOpen";
    public final static String KEY_LAST_NOTIFY = "lastNotify";
	
	public final static String URL_UPDATE_CLIENT = "updateClient";
	public final static String URL_BOOKSTORE = "bookstore";
	public final static String URL_BOOKSTORE_URL1 = "bookstore2";
	public final static String URL_BOOKSTORE_URL2  = "bookstore3";
	public final static String URL_BOOKSTORE_URL3  = "bookstore4";
	public final static String URL_BOOKSTORE_RECOMMEND = "recommend";
	public final static String URL_ACCOUNT_LOGIN = "account_login";
	public final static String URL_ACCOUNT_CREATE = "account_create";
	public final static String URL_ACCOUNT_MODIFY = "account_modify";
	public final static String URL_NOTICE = "notice";
	public final static String URL_CONTENTS = "contents";
	public final static String URL_XNCONTENTS = "xncontents";
	public final static String URL_CHATPER = "chapter";
	public final static String URL_XNCHATPER = "xnchapter";
    public final static String URL_UPDATE_USERINFO = "updateUserInfo";
    public final static String URL_USER_REGIST = "userRegist";
    public final static String URL_MESSAGE_UPDATE = "messageUpdate";
    public final static String URL_MESSAGE_DELETE = "messageDelete";
    public final static String URL_UPDATE_PROGRESS = "updateProgress";
    public final static String URL_BOOK_DETAIL = "bookDetail";
    public final static String URL_BOOK_UPDATE = "bookUpdate";
    public final static String URL_USER_ATTENTION = "userAttention";
    public final static String URL_BOOK_BUY_RECOREDS = "bookBuyRecords";
    public final static String URL_RECHARED_RECORDS = "rechargeRecords";
    public final static String URL_MESSAGE_SEND = "messageSend";
    public final static String URL_UPDATE_PAYINFO = "updatePayinfo";
    public final static String URL_WEB_PAY = "webPay";
    public final static String URL_SMS_PAY = "smsPay";
    public final static String URL_SUGGESTION_SEND = "suggestionSend";
    public final static String URL_BOOK_RECOMMAND = "bookRecommand";
    public final static String URL_CHAPTER_BUY = "chapterBuy";
    public final static String URL_ATTENTION_TO = "attentionTo";
    public final static String URL_ATTENTION_CANCEL = "attentionCancel";
    public final static String URL_USER_FANS = "fansList";
    public final static String URL_ATTENTION_CLOSE = "attentionClose";
    public final static String URL_GIVE_ICON = "giveIcon";
    public final static String URL_FANS_BOOKS = "fansBooks";
    public final static String URL_PHONE_AUTHCODE = "phoneAuthCode";
    public final static String URL_PHONE_BIND = "phoneBind";
    public final static String URL_PWD_MODIFY = "pwdModify";
    public final static String URL_BOOK_SYNC = "bookSync";
    public final static String URL_EVERYDAY_COMMENT = "urlEveydayComment";
    public final static String URL_PLACE_TUIJIAN = "urlPlaceTuijian";
    public final static String URL_USER_TIME_LONG = "urlUserTimeLong";
    public final static String URL_READ_BOOK= "urlReadbook";
    public final static String URL_READ_BUYBOOK= "urlReadBuybook";
    public final static String URL_GOSTORE_TIME= "urlGoStoreTime";
    public final static String URL_GOBOESDK_ENTRY= "urlGoBoeSdkEntry";
    public final static String URL_XN_MAPTABLE= "urlXNMapTable";
    public final static String URL_BOOK_COVER= "urlBookCover";
    public final static String URL_BOOK_SEARCH= "urlBookSearch";
    public final static String URL_POST_CONTENT_ERROR= "urlPostContentError";
    public final static String URL_DETAIL_BOOKITEM= "urldetailbook";
    public final static String URL_DETAIL_CMBOOK= "urldetailcmbook";
    public final static String URL_GAME_LIST= "urlgamelist";
    public final static String UPDATA_BOOKSHELF_COUNT= "updatabookcount";
    public final static String URL_BOOK_DIRECTORY= "urlbookdirectory";
    public final static String URL_BOOKSTORE_SKIP="urlBookStoreSkip";
    public final static String URL_SEARCH_RECOMMEND="urlSearchRecommend";
    public final static String URL_SEARCH_HOTKEYWIORD="urlSearchHotKeyWord";
    //用户中心的两个接扣
    public final static String URL_USERCENTER_GIFT="urlUserCenterGift";
    public final static String URL_USERCENTER_FREEREAD="urlUserCenterFreeRead";
    
	
	private String mPageCacheFileDir;			// 页面数据的缓存文件路径
	private String mLocalContentsFileDir;		// 本地小说生成的目录文件存放路径
	private String mReadCacheRoot;
	
	
	private HashMap<String, String> mUrlMap;
	
	private ReadConfig				mReadConfig;
	
    private DeviceInfo 				mDeviceInfo;
    
    private boolean 				mIsGrid;
    
    private int 					mLastUserID;
    
	private SharedPreferences 		sp;
	
	public Config(Context context) {
		super();
		// TODO Auto-generated constructor stub
		
		if(!init(context)) {
			DebugLog.d(TAG, "应用程序配置数据未初始化");
		}
		
	}
	
	private boolean init(Context context) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        
		
		mPageCacheFileDir = context.getFilesDir().toString();
		mLocalContentsFileDir = context.getFilesDir().toString();
		
		//mReadCacheRoot = Environment.getExternalStorageDirectory() + "/" + "CWReader";
		//sd卡的files目录
		File dir = context.getExternalFilesDir("cache");
		//sd卡的cache，目录
		if(dir == null) {
			//自带内存的files目录
			mReadCacheRoot = context.getFilesDir().toString() + "/cache" ;
		} else {
			mReadCacheRoot = dir.toString();
		}
		
		
		
		loadUrl(context);
		loadXmlConfig();
		
		mReadConfig = new ReadConfig(context);
        
		mDeviceInfo = new DeviceInfo(context);
				
		return true;
	}
	
	private void loadXmlConfig() {
        mIsGrid = sp.getBoolean(KEY_SHELF_MODE, true);
        mLastUserID = sp.getInt(KEY_LAST_USERID, 0);
        
        Editor editor = sp.edit();
        editor.putLong(KEY_LAST_OPEN, System.currentTimeMillis());
        editor.commit();
	}
	
	private void loadUrl(Context context) {
		mUrlMap = new HashMap<String, String>();
        
		Resources rs = context.getResources();
        
		//String urlUpdate = rs.getString(R.string.url_update_client);
		String urlLogin = rs.getString(R.string.url_login);
		String urlAccountCreate = rs.getString(R.string.url_account_create);
		String urlAccountModify = rs.getString(R.string.url_account_modify);
		String urlNotice = rs.getString(R.string.url_notice);
		String urlBookstore = rs.getString(R.string.url_bookstore);
		String urlBookstore2 = rs.getString(R.string.url_qovestore_bt2);
		String urlBookstore3 = rs.getString(R.string.url_qovestore_bt3);
		String urlBookstore4 = rs.getString(R.string.url_qovestore_bt4);
		
		String urlStoreRecommend=rs.getString(R.string.url_bookstore_recommend);
		String urlContents = rs.getString(R.string.url_contents);
		String urlXNContents = rs.getString(R.string.url_xncontents);
		String urlChapter = rs.getString(R.string.url_chapter);
		String urlXNChapter = rs.getString(R.string.url_xnchapter);
		String urlUserInfo = rs.getString(R.string.url_update_userinfo);
		String urlUserRegist = rs.getString(R.string.url_user_regist);
		String urlMessageUpdate = rs.getString(R.string.url_message_update);
		String urlMessageDelete = rs.getString(R.string.url_message_delete);
		String urlUpdateProgress = rs.getString(R.string.url_update_progress);
		String urlBookDetail = rs.getString(R.string.url_book_detail);
		String urlBookUpdate = rs.getString(R.string.url_book_update);
		String urlUserAttention = rs.getString(R.string.url_user_attention);
		String urlBookBuyRe = rs.getString(R.string.url_book_buy_records);
		String urlRechargeRe = rs.getString(R.string.url_recharge_records);
		String urlMessageSend = rs.getString(R.string.url_message_send);
		String urlUpdatePayinfo = rs.getString(R.string.url_update_payinfo);
		String urlWebPay = rs.getString(R.string.url_web_pay);
		String urlSmsPay = rs.getString(R.string.url_sms_pay);
		String urlSuggestionSend = rs.getString(R.string.url_suggestion_send);
		String urlBookRecommand = rs.getString(R.string.url_book_recommand);
		String urlChapterBuy = rs.getString(R.string.url_chapter_buy);
		String urlAttentionTo = rs.getString(R.string.url_attention_to);
		String urlAttentionCancel = rs.getString(R.string.url_attention_cancel);
		String urlUserFans = rs.getString(R.string.url_user_fans);
		String urlAttentionClose = rs.getString(R.string.url_fans_back);
		String urlGiveIcon = rs.getString(R.string.url_give_icon);
		String urlFansBooks = rs.getString(R.string.url_fans_books);
		String urlPhoneAuthCode = rs.getString(R.string.url_phone_authcode);
		String urlPhoneBind = rs.getString(R.string.url_phone_bind);
		String urlPwdModify = rs.getString(R.string.url_password_modify);
		String urlBookSync = rs.getString(R.string.url_book_sync);
		String urlEveydayComment = rs.getString(R.string.url_eveyday_commented);
		String urlPlaceTuijian = rs.getString(R.string.url_book_placetongji);
		String urlUserTimeLong = rs.getString(R.string.url_user_timelong);
		String urlReadbook = rs.getString(R.string.url_read_book);
		String urlReadBuybook = rs.getString(R.string.url_read_buybook);
		String urlGoStoreTime = rs.getString(R.string.url_gostore_time);
		String urlGoBoeSdkEntry = rs.getString(R.string.url_goboesdk_entry);
		String urlXNMapTable = rs.getString(R.string.url_xn_maptable);
		String urlmBookCover = rs.getString(R.string.url_book_conver);
		String urlBookSearch=rs.getString(R.string.url_book_search);
		String urlPostContentError = rs.getString(R.string.url_post_conver_error);
		String urldetailbook = rs.getString(R.string.url_detail_book);
		String urldetailcmbook = rs.getString(R.string.url_detail_cm_book);
		String urlgamelist = rs.getString(R.string.url_game_book);

		String urlSearchRecommend=rs.getString(R.string.url_search_recommend);
		String urlSearchHotKeyWord=rs.getString(R.string.url_search_hotkeyword);

		String updatabookcount = rs.getString(R.string.url_updata_book);
		String urlbookdirectory = rs.getString(R.string.url_book_direciory);

		
		String urlUserCenterGift=rs.getString(R.string.url_usercenter_gift);
		String urlUserCenterFreeRead=rs.getString(R.string.url_usercenter_freeread);
		String urlBookStoreSkip=rs.getString(R.string.url_bookstore_skip);
		
		mUrlMap.put(URL_EVERYDAY_COMMENT, urlEveydayComment);// 每日推荐
		mUrlMap.put(URL_PLACE_TUIJIAN, urlPlaceTuijian);// 推荐位
		mUrlMap.put(URL_USER_TIME_LONG, urlUserTimeLong);
		mUrlMap.put(URL_READ_BOOK, urlReadbook);
		mUrlMap.put(URL_READ_BUYBOOK, urlReadBuybook);
		mUrlMap.put(URL_GOSTORE_TIME, urlGoStoreTime);
		mUrlMap.put(URL_GOBOESDK_ENTRY, urlGoBoeSdkEntry);
		mUrlMap.put(URL_XN_MAPTABLE, urlXNMapTable);
		mUrlMap.put(URL_BOOK_COVER, urlmBookCover);
		mUrlMap.put(URL_POST_CONTENT_ERROR, urlPostContentError);
		mUrlMap.put(URL_DETAIL_BOOKITEM, urldetailbook);
		mUrlMap.put(URL_DETAIL_CMBOOK, urldetailcmbook);
		mUrlMap.put(URL_GAME_LIST, urlgamelist);
		mUrlMap.put(UPDATA_BOOKSHELF_COUNT, updatabookcount);
		mUrlMap.put(URL_BOOK_DIRECTORY, urlbookdirectory);
		
		//mUrlMap.put(URL_UPDATE_CLIENT, urlUpdate);
		mUrlMap.put(URL_ACCOUNT_LOGIN, urlLogin);
		mUrlMap.put(URL_ACCOUNT_CREATE, urlAccountCreate);
		mUrlMap.put(URL_ACCOUNT_MODIFY, urlAccountModify);
        
        mUrlMap.put(URL_NOTICE, urlNotice);
        mUrlMap.put(URL_BOOKSTORE, urlBookstore);
        mUrlMap.put(URL_BOOKSTORE_URL1, urlBookstore2);
        mUrlMap.put(URL_BOOKSTORE_URL2, urlBookstore3);
        mUrlMap.put(URL_BOOKSTORE_URL3, urlBookstore4);
        
        mUrlMap.put(URL_BOOKSTORE_RECOMMEND, urlStoreRecommend);
        mUrlMap.put(URL_CONTENTS, urlContents);
        mUrlMap.put(URL_XNCONTENTS, urlXNContents);
        mUrlMap.put(URL_CHATPER, urlChapter);
        mUrlMap.put(URL_XNCHATPER, urlXNChapter);
        mUrlMap.put(URL_UPDATE_USERINFO, urlUserInfo);
        mUrlMap.put(URL_USER_REGIST, urlUserRegist);
        mUrlMap.put(URL_MESSAGE_UPDATE, urlMessageUpdate);
        mUrlMap.put(URL_MESSAGE_DELETE, urlMessageDelete);
        mUrlMap.put(URL_UPDATE_PROGRESS, urlUpdateProgress);
        mUrlMap.put(URL_BOOK_DETAIL, urlBookDetail);
        mUrlMap.put(URL_BOOK_UPDATE, urlBookUpdate);
        mUrlMap.put(URL_USER_ATTENTION, urlUserAttention);
        mUrlMap.put(URL_BOOK_BUY_RECOREDS, urlBookBuyRe);
        mUrlMap.put(URL_RECHARED_RECORDS, urlRechargeRe);
        mUrlMap.put(URL_MESSAGE_SEND, urlMessageSend);
        mUrlMap.put(URL_UPDATE_PAYINFO, urlUpdatePayinfo);
        mUrlMap.put(URL_WEB_PAY, urlWebPay);
        mUrlMap.put(URL_SMS_PAY, urlSmsPay);
        mUrlMap.put(URL_SUGGESTION_SEND, urlSuggestionSend);
        mUrlMap.put(URL_BOOK_RECOMMAND, urlBookRecommand);
        mUrlMap.put(URL_CHAPTER_BUY, urlChapterBuy);
        mUrlMap.put(URL_ATTENTION_TO, urlAttentionTo);
        mUrlMap.put(URL_ATTENTION_CANCEL, urlAttentionCancel);
        mUrlMap.put(URL_USER_FANS, urlUserFans);
        mUrlMap.put(URL_ATTENTION_CLOSE, urlAttentionClose);
        mUrlMap.put(URL_GIVE_ICON, urlGiveIcon);
        mUrlMap.put(URL_FANS_BOOKS, urlFansBooks);
        mUrlMap.put(URL_PHONE_AUTHCODE, urlPhoneAuthCode);
        mUrlMap.put(URL_PHONE_BIND, urlPhoneBind);
        mUrlMap.put(URL_PWD_MODIFY, urlPwdModify);
        mUrlMap.put(URL_BOOK_SYNC, urlBookSync);
        mUrlMap.put(URL_BOOK_SEARCH,urlBookSearch);
        
        mUrlMap.put(URL_SEARCH_RECOMMEND,urlSearchRecommend);
        mUrlMap.put(URL_SEARCH_HOTKEYWIORD,urlSearchHotKeyWord);
        
        mUrlMap.put(URL_USERCENTER_GIFT,urlUserCenterGift);
        mUrlMap.put(URL_USERCENTER_FREEREAD,urlUserCenterFreeRead);
        
        mUrlMap.put(URL_BOOKSTORE_SKIP,urlBookStoreSkip);
	}
	
	public String getPageCacheFileDir() {
		return mPageCacheFileDir;
	}
	
	public String getLocalContentsFilePath(int bookID) {
        String dir = mLocalContentsFileDir + "/local";
		File f = new File(dir);
		if(!f.exists()) {
			f.mkdirs();
		}
		return dir + "/" + "contents_" + bookID;
	}
	
	public String getOnlineBookDir(int onlineID) {
		String dir = mReadCacheRoot + "/book/" + onlineID;
		return  dir;
	}
	
	
	public String getOnlineChapterFilePath(int onlineID, int chapterID) { // 移动章节缓存文件
		String dir = mReadCacheRoot + "/book/" + onlineID;

		File f = new File(dir);
		if(!f.exists()) {
			f.mkdirs();
		}
		
		return  dir + "/" + chapterID + ".BCP";
	}
	public String getXNOnlineChapterFilePath(int onlineID, int chapterID) { //血凝章节缓存文件
		String dir = mReadCacheRoot + "/xnbook/" + onlineID; 
		File f = new File(dir);
		if(!f.exists()) {
			f.mkdirs();
		}
		
		return  dir + "/" + chapterID + ".BCP";
	}
	// 检查是否有缓存文件
	public Boolean isHaveYDChapterFilePath(int onlineID, int chapterID) { // 移动章节是否有缓存文件
		String dir = mReadCacheRoot + "/book/" + onlineID+ "/" + chapterID + ".BCP";

		File f = new File(dir);
		
		return  f.exists();
	}
	public Boolean isHaveXNChapterFilePath(int onlineID, int chapterID) { //血凝章节是否缓存文件
		String dir = mReadCacheRoot + "/xnbook/" + onlineID+ "/" + chapterID + ".BCP"; 
		File f = new File(dir);
	
		return   f.exists();
	}
	
	
	
    public String getDataDBName(int userid) {
//        return "boe_data_" + userid + ".db";
        return "boe_data_" + "0" + ".db";
    }

    
	public String getContentDBName(int onlineID) {  // 移动章节目录的数据库
		String dir = mReadCacheRoot + "/book/" + onlineID;
        File f = new File(dir);
		if(!f.exists()) {
			f.mkdirs();
		}
		
		return  dir + "/contents_" + onlineID + ".db";
	}
	

	public String getXNContentDBName(int onlineID) {  // 移动映射的XN章节目录的数据库,还是根据移动的id来添加的
		String dir = mReadCacheRoot + "/xnbook/" + onlineID;
		File f = new File(dir);
		if(!f.exists()) {
			f.mkdirs();
		}
		
		return  dir + "/contents_" + onlineID + ".db";
	}
	public String getXNContentName(int onlineID) {  // 得到血凝的缓存目录
		String dir = mReadCacheRoot + "/xnbook/" + onlineID;

		return  dir ;
	}
    
	public String getDownloadFileDir() {
        String dir = mReadCacheRoot + "/download";
        File f = new File(dir);
		if(!f.exists()) {
			f.mkdirs();
		}
        return dir;
	}
	
	public String getCrashLogDir() {
        String dir = mReadCacheRoot + "/crash";
        File f = new File(dir);
		if(!f.exists()) {
			f.mkdirs();
		}
        return dir;
	}
	
	public String getUrl(String key) {
		return mUrlMap.get(key);
	}
	
	public ReadConfig getReadConfig() {
		return mReadConfig;
	}
	
    public DeviceInfo getDeviveInfo() {
    	return mDeviceInfo;
    }

	public boolean isIsGrid() {
		return mIsGrid;
	}

	public void setIsGrid(boolean isGrid) {
		// 往配置文件中设置是否是表格布局
        if(this.mIsGrid == isGrid) {
        	return;
        }
		this.mIsGrid = isGrid;
        Editor editor = sp.edit();
        editor.putBoolean(KEY_SHELF_MODE, mIsGrid);
        editor.commit();
	}
    
    public int getLastUserID() {
    	return mLastUserID;
    }
    
    public void setLastUserID(int userid) {
    	if(mLastUserID == userid) {
    		return;
    	}
        mLastUserID = userid;
        Editor editor = sp.edit();
        editor.putInt(KEY_LAST_USERID, mLastUserID);
        editor.commit();
    }
    
}
