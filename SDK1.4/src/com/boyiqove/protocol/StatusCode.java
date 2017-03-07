package com.boyiqove.protocol;


/*
 * 服务器返回状态码
 */
public class StatusCode {
    public final static int OK  			= 100;		// 请求成功
    public final static int AUTH_FAILURE 	= 200;  	// 认证失败
    public final static int PARM_ERROR 		= 201;		//参数错误
    public final static int FAILURE 		= 202;		// 请求失败（服务器错误）
    public final static int NO_UPDATES 		= 301;		// 没有更新
    public final static int NO_DATA 		= 300;		// 没有数据
    public final static int SIGNED 			= 302;		// 已签到
    public final static int INSU_FUNDS 		= 303;		// 余额不足
}
