package com.boyiqove.task;

public class CallBackMsg {
	public static final int CHCEK_CONTENTS_COMPLETED 	= 0x30001; // 目录生成完成
	public static final int READ_CONTENTS_COMPLETED 	= 0x30000; // 目录生成完成
	
	public static final int INPUT_OBJECT_COMPLETED 	= 0x30002; 		// 读取的历史目录信息
	public static final int INPUT_OBJECT_ERROR 		= 0x30003;		// 读取的历史目录信息
    
    
    //public static final int ACCOUNT_SUCCESSFUL = 0x40001;		// 自动注册成功
    public static final int ACCOUNT_FAILED = 0x40001;			// 自动注册失败
    
    public static final int LOGIN_SUCCESSFUL = 0x40003;			// 用户登陆成功
    public static final int LOGIN_FAILED = 0x40004;				// 用户登陆失败
	public static final int LOGIN_SUCCESSFUL_SYNC = 0x40005;	// 用户登录后同步完成
    
	public static final int NOTICE_SUCCESSFUL = 0x40006;		// 系统通知获取完成
    
	public static final int NOTICE_SHOW_NEXT = 0x40007;
	public static final int CLOSE_SHOW_LOGIN = 0x40008;    // 

	public static final int UPDATE_USER_MESSAGE = 0x40009;
    
    public static final int UPDATE_USER_ATTENTION = 0x40010;
    public static final int UPDATE_USER_FANS = 0x40011;
    
    public static final int UPDATE_DISCOUNT_SUCCESS = 0x40012;
    public static final int UPDATE_DISCOUNT_FAILED = 0x41013;
    
    public static final int UPDATE_PAYINFO_SUCCESS = 0x40014;
    public static final int UPDATE_PAYINFO_FAILED = 0x41015;
    
    public static final int UPDATE_BOOKSHELF = 0x40016;

	public static final int UPDATE_USER_INFO = 0x400017;
	public static final int INIT_SHOW_LOGIN = 0x40018;    // 
	public static final int INIT_SHOW_DOWN = 0x40019;    // 
	public static final int CLOSE_ERROR_LOGIN = 0x40020;    // 
	
	public static final int CHAPTER_CONTENT_COMPLETED 	= 0x50001;		// 本地缓存章节读取完成
	public static final int CHAPTER_CONTENT_ERROR 		= 0x50002;
    
	public static final int CONTENTS_READ_COMPLETED = 0x50003;
	public static final int CONTENTS_WRITE_COMPLETED = 0x50004;
	public static final int CONTENTS_READ_CONTEXT = 0x50004;
	

	public final static int SHOW_PROGRESS_MESSAGE = 0x60001;
	public final static int HIDE_PROGRESS_MESSAGE = 0x60002;
	public final static int SHOW_TOAST_MESSAGE = 0x60003;
	public final static int SHOW_DIALOGE_MESSAGE = 0x60004;
	public final static int SHOW_PROGRESS_CANCEL = 0x60005;
	public final static int SHOW_LOGINPROGRESS_CANCEL = 0x60006;
	public final static int SHOW_LOGINPROGRESS = 0x60007;
	public final static int CLOSE_LOGINWINDOW = 0x60008;
	
	public final static int READING_LOOPER_STAST = 0x60009;
	public final static int READ_ENDPAGE_RECOMEND = 0x60010;
	public final static int READ_ENDPAGE_PROGRESS = 0x60011;
	public final static int SHEEP_DOWN_LOAD = 0x60012;
	public final static int SHOW_DOWNLOAD_PROGRESS = 0x60013;
}
