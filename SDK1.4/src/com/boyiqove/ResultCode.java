package com.boyiqove;

/*
 * resultCode , requestCode 请使用PageID
 */
public class ResultCode {

	public static final int ADD_MULTI_TO_BOOKSHELF = 0x20004;// 添加本地书籍到书架
    
	public static final int ADD_ONE_TO_BOOKSHELF = 0x20005;	// 添加在线书籍到书架
    
	public static final int OPEN_BOOK_FAILED = 0x20006;		// 打开书籍失败
	
	public static final int UPDATE_LASTREAD = 0x20007;		// 更新书架中书籍上次阅读位置
    
	public final static int JUMP_TO_POSITION = 0x20008; 	// 章节跳转
	
    
    public final static int RESULT_UPDATE = 0x20009;		// 更新用户资料
    
    public final static int CHANGE_USER = 0x200010;			// 切换用户

	public static final int CONTENT_NOT_FOUND = 0x200011;	// 未找到目录信息
    
	public static final int PHONE_BINDED = 0x200012;		// 手机号绑定成功
	
	public final static int CM_TO_CONGZHI = 0x20013; 	// cm 充值成功
	public final static int LOGIN_TO_FAST = 0x20014; 	// 请求快速登录
	public final static int ORDER_INFO = 0x20015; 	// 请求付费详情页面
	public final static int DETIAL_DIRE_TOREAD= 0x20016; 	// 从书籍详情的目录进入阅读的请求码
}
