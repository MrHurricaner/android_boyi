package com.boyiqove.config;


import java.util.ArrayList;
import java.util.List;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.db.DBDataHelper;
import com.boyiqove.entity.BoyiMessage;
import com.boyiqove.entity.Notice;
import com.boyiqove.entity.PayInfo;
import com.boyiqove.util.AES;
import com.boyiqove.util.DebugLog;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.SparseIntArray;

public class ClientUser {
	private final static String TAG = "ClientUser";
    
	public final static int INVALID_USERID = -1;
    
	public final static int USER_COMMON = 1;
    public final static int USER_AUTHOR = 2;
    
    
	public final static int SEX_BOY = 1;
	public final static int SEX_GIRL = 2;
	
	private final static String KEY_USERNAME = "username";
	private final static String KEY_PASSWORD = "password";
	//private final static String KEY_USERID = "userid";
    private final static String KEY_SEX = "sex";
    private final static String KEY_RECOMMAND = "recommand";
    private final static String KEY_OPEN_LASTBOOK = "openLastBook"; 	// 是否启动后继续上次阅读
    private final static String KEY_LAST_BOOKID = "lastBookID";		// 上次阅读的书籍ID
    private final static String KEY_NOTICE = "notice";				// 是否开启消息通知
    private final static String KEY_LAST_MSG_TIME = "lastMsgTime";
    private final static String KEY_LAST_USERINFO_TIME = "lastUserInfoTime";
    
	
	private String 		mToken;				// 用户登陆信息服务器校验令牌
	private String 		mUserName;			// 用户名/手机号
	private String 		mPassword;
    
	
    private boolean 	mIsRecommand;		// 是否已经推荐过书籍
    
    private String 		nickName;			// 呢称
	private int 		userID;
    private int 		mSex;				// 1.男,2.女
    private String 		signature;
    private String 		mobile = "";
    private String 		photoUrl;
    private int 		type;				// 1.普通用户,2.作者
    
    private int 		balance;			// 余额， 即香币数量
    private int 		message;			// 消息数量
    private int 		attention;			// 关注数量
    private int 		mission;			// 任务数量
    private String 		missionUrl; //活动页url
    private int 		gift;				// 礼物数量
    private String 		giftUrl;
    private String 	signUrl;//	签到的url
    

	private int 		fans;				// 粉丝数量
    
    private boolean 	mIsNotice;
    
    private boolean 	mIsOpenLast;
    private int 		mLastBookID;
    
    
    /////////////////////////////////////////////////////////////
	
	private boolean 	mIsLogin = false;
    //private boolean 	mIsUpdate = false;
    
	//private long 		mLastUserInfoTime;		// 
	private long 		mLastMessageTime;
    
    
	
	private List<Notice>		mNoticeList = new ArrayList<Notice>();	// 系统公告列表
    
    private List<BoyiMessage>	mPrivateMsgList;
    private List<BoyiMessage>	mSystemMsgList;
    
    //private List<Attention> 	mAttentionList = new ArrayList<Attention>();
    
    private SparseIntArray 		mDiscountList = new SparseIntArray();
    
    private List<PayInfo> 		mPayList = new ArrayList<PayInfo>();
	
    private Context context;
	private SharedPreferences sp;
	
	public ClientUser(Context context, int userid, DBDataHelper helper) {
        this.context = context;
        userID = userid;
		sp = context.getSharedPreferences("user_config_" + userid, Context.MODE_PRIVATE);
		
		initXml();
        
		initData();
        
        mPrivateMsgList = helper.getBoyiMessageList(BoyiMessage.TYPE_PRIVATE, mLastMessageTime);
        mSystemMsgList = helper.getBoyiMessageList(BoyiMessage.TYPE_SYSTEM, mLastMessageTime);
	}
    
	public void init() {
		
	}
    
	
    
	private void initData() {
//        giftUrl = context.getResources().getString(R.string.url_gift_default);
//        missionUrl = context.getResources().getString(R.string.url_mission_default);
//        signUrl=context.getResources().getString(R.string.url_sign_default);//签到的url
        mDiscountList.append(10, 10);
        mDiscountList.append(20, 10);
        mDiscountList.append(50, 10);
        mDiscountList.append(100, 10);
        
        nickName = context.getResources().getString(R.string.boyi_auto_account_nickname);
        signature = context.getResources().getString(R.string.boyi_auto_account_signature);
        
	}
    
	
	private void initXml() {
		mUserName = sp.getString(KEY_USERNAME, "");
		String data = sp.getString(KEY_PASSWORD, "");
		if(!data.equals("")) {
			try {
				mPassword = AES.decrypt(data.getBytes(), "utf-8");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        //userID = sp.getInt(KEY_USERID, INVALID_USERID);
        
        mIsRecommand = sp.getBoolean(KEY_RECOMMAND, false);
        
        mSex = sp.getInt(KEY_SEX, 0);
        
        mIsNotice = sp.getBoolean(KEY_NOTICE, true);
        mIsOpenLast = sp.getBoolean(KEY_OPEN_LASTBOOK, false);
        mLastBookID = sp.getInt(KEY_LAST_BOOKID, 0);
        
        long current = System.currentTimeMillis();
        long month = 1000*60*60*24*30;
        long defTime = current - month;
        mLastMessageTime = sp.getLong(KEY_LAST_MSG_TIME, defTime);
        //mLastUserInfoTime = sp.getLong(KEY_LAST_USERINFO_TIME, defTime);
	}
    
	public int getID() {
//		if(userID ==0) {
//			SharedPreferences sp1 = context.getSharedPreferences("config", Context.MODE_PRIVATE);
//			userID = sp1.getInt("lastUserID", 0);			
//		}
		if (userID==0) {
			
			userID=AppData.getConfig().getLastUserID();
		}
		return userID;
	}
    
	public void setID(int userid) {
		if(userID == userid) {
			return;
		}
        userID = userid;        
		sp = context.getSharedPreferences("user_config_" + userid, Context.MODE_PRIVATE);
        mIsRecommand = sp.getBoolean(KEY_RECOMMAND, false);
        
        AppData.getConfig().setLastUserID(userid);
	}
    
	public boolean isRecommand() {
		return mIsRecommand;
	}
    
	public void setRecommand() {
		mIsRecommand = true;
        Editor editor = sp.edit();
        editor.putBoolean(KEY_RECOMMAND, mIsRecommand);
        editor.commit();
	}
	
	
	public boolean isIsOpenLast() {
		return mIsOpenLast;
	}

	public void setIsOpenLast(boolean mIsOpenLast) {
        if(this.mIsOpenLast == mIsOpenLast) {
        	return;
        }
		this.mIsOpenLast = mIsOpenLast;
        Editor editor = sp.edit();
        editor.putBoolean(KEY_OPEN_LASTBOOK, this.mIsOpenLast);
        editor.commit();
	}

	public int getLastBookID() {
		return mLastBookID;
	}

	public void setLastBookID(int mLastBookID) {
        if(this.mLastBookID == mLastBookID) {
        	return;
        }
		this.mLastBookID = mLastBookID;
        Editor editor = sp.edit();
        editor.putInt(KEY_LAST_BOOKID, this.mLastBookID);
        editor.commit();
	}
	

	public boolean isNotice() {
		return mIsNotice;
	}

	public void setIsNotice(boolean mIsNotice) {
        if(this.mIsNotice == mIsNotice) {
        	return;
        }
		this.mIsNotice = mIsNotice;
        Editor editor = sp.edit();
        editor.putBoolean(KEY_NOTICE, this.mIsNotice);
        editor.commit();
	}
    
//    public long getLastUserInfoTime() {
//    	return mLastUserInfoTime;
//    }
//	
//    public void setLastUserInfoTime(long last) {
//        if(mLastUserInfoTime == last) {
//			return;
//		}
//        mLastUserInfoTime = last;
//        Editor editor = sp.edit();
//        editor.putLong(KEY_LAST_USERINFO_TIME, mLastUserInfoTime);
//        editor.commit();
//    }
//	
    
	public long getLastMessageTime() {
		return mLastMessageTime;
	}
    
	public void setLastMessageTime(long last) {
		if(mLastMessageTime == last) {
			return;
		}
        mLastMessageTime = last;
        Editor editor = sp.edit();
        editor.putLong(KEY_LAST_MSG_TIME, mLastMessageTime);
        editor.commit();
	}
    
	
    ////////////////////////////////////////////////////

	public List<Notice> getmNoticeList() {
		return mNoticeList;
	}

	public void setmNoticeList(List<Notice> mNoticeList) {
		this.mNoticeList = mNoticeList;
	}

	public void setToken(String token) {
		mToken = token;
	}
	
	public String getToken() {
		return mToken;
	}
	
	public void setUserName(String username) {
		if(mUserName.equals(username)) {
			return;
		}
		mUserName = username;
		Editor editor = sp.edit();
		editor.putString(KEY_USERNAME, mUserName);
		editor.commit();
	}
	
	public String getUserName() {
		return mUserName;
	}
	
	public void setPassword(String password) {
		if(mPassword != null && mPassword.equals(password)) {
			return;
		}

		mPassword = password;
		Editor editor = sp.edit();

		editor.putString(KEY_PASSWORD, AES.encrypt(mPassword.getBytes(), "utf-8"));
		//editor.putString(KEY_PASSWORD, new String(AES.encrypt(mPassword.getBytes())));
		editor.commit();
	}

	public String getPassword() {
		return mPassword;
	}
	
//	public int getID() {
//		return userID;
//	}
	
//    /*
//     * true : 新用户， 得通知更新数据
//     */
//	public boolean setID(int id) {
//        if(this.userID == id) {
//        	return false;
//        }
//        
//        this.userID = id;
//        Editor editor = sp.edit();
//        editor.putInt(KEY_USERID, userID);
//        editor.commit();
//        
//        return true;
//	}
    
	public String getNickName() {
        return nickName;
	}
    
	public void setNickName(String nickName) {
        this.nickName = nickName;
	}
    
    public int getSex() {
    	return mSex;
    }
    
	public String getSexString() {
        switch(mSex) {
        case SEX_BOY:
        	return "男";
        case SEX_GIRL:
        	return "女";
        default:
            return "";
        }
	}
    
	public void setSex(int sex) {
        if(this.mSex == sex) {
        	return;
        }
		this.mSex = sex;
        Editor editor = sp.edit();
        editor.putInt(KEY_SEX, this.mSex);
        editor.commit();
	}
	
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	// 		签到的url
	public String getSignUrl() {
		return signUrl;
	}
	public void setSignUrl(String signUrl) {
		this.signUrl = signUrl;
	}

	
	public SharedPreferences getSp() {
		return sp;
	}

	public void setSp(SharedPreferences sp) {
		this.sp = sp;
	}
    
	public void setLogin(boolean isLogin) {
		mIsLogin = isLogin;
	}
	
	public boolean isLogin() {
		return mIsLogin;
	}
    
	public List<Notice> getNoticeList() {
		return mNoticeList;
	}
    
	
	////////////////////////////////////////
    public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getMessage() {
		return message;
	}

	public void setMessage(int message) {
		this.message = message;
	}

	public int getAttention() {
		return attention;
	}

	public void setAttention(int attention) {
		this.attention = attention;
	}

	public int getMission() {
		return mission;
	}

	public void setMission(int mission) {
		this.mission = mission;
	}

	public int getGift() {
		return gift;
	}

	public void setGift(int gift) {
		this.gift = gift;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public String getMissionUrl() {
		return missionUrl;
	}
	

	public void setMissionUrl(String missionUrl) {
		this.missionUrl = missionUrl;
	}

	public String getGiftUrl() {
		return giftUrl;
	}

	public void setGiftUrl(String giftUrl) {
		this.giftUrl = giftUrl;
	}

//	public boolean isIsUpdate() {
//		return mIsUpdate;
//	}
//
//	public void setIsUpdate(boolean isUpdate) {
//		this.mIsUpdate = isUpdate;
//	}
    
    public List<BoyiMessage> getPrivateMsgList() {
    	return mPrivateMsgList;
    }
    
    public List<BoyiMessage> getPrivateMessage(int fromID) {
    	List<BoyiMessage> list = new ArrayList<BoyiMessage>();
        int size = mPrivateMsgList.size();
        for(int i = 0; i < size; i++) {
        	BoyiMessage msg = mPrivateMsgList.get(i);
            if( (msg.fromID == fromID && msg.toID == userID) ||
            	(msg.fromID == userID && msg.toID == fromID)	) {
                
            	list.add(msg);
            }
        }
        
        return list;
    }
    
    public List<BoyiMessage> getSystemMsgList() {
    	return mSystemMsgList;
    }
    
    public void removePrivateMsg(int position) {
        mPrivateMsgList.remove(position);
    }
    
    public void removeSystemMsg(int position) {
    	mSystemMsgList.remove(position);
    }
    
    public void removePrivateMsg(Object obj) {
        mPrivateMsgList.remove(obj);
    }
    
    public void removeSystemMsg(Object obj) {
    	mSystemMsgList.remove(obj);
    }
    
//    public List<Attention> getAttentionList() {
//    	return mAttentionList;
//    }

	public SparseIntArray getDiscountList() {
		return mDiscountList;
	}

	public List<PayInfo> getPayList() {
		return mPayList;
	}
    
    
}
