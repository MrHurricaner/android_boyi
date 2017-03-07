package com.xn.xiaoyan.user;

import java.io.Serializable;

public class GameItem implements Serializable {
	public int id;
	public String 	name; 		// 游戏名字
	public String 	packagename; // 游戏包名	
	public String  starnums;
	public  String 	showimage; // 图片地址
	public  String 	activityUms; // 激活次数
	public  String 	description; // 简介  
	public  String 	download; // 下载地址
	public String   downNum; // 下载数量
	public String   gamePackage; //游戏包名
	public String   cate; 
	public Long 	issueDate;
	public Long 	createDate;
	
	public int 	apkSize;		//实际下载的文件大小
	public float netApkSize;
	public int isTop;
	public String version;
	
	public Long 	updatetime;
	public Long 	lastSize;
	

}
