package com.boyiqove.entity;

import java.io.Serializable;
import java.text.DecimalFormat;

import android.R.integer;

public class BookItem implements Serializable, Comparable<BookItem>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8312208161903673504L;

	// online 参数值
	public final static int ON_LOCAL_TXT = -1;		// 本地书籍
    
	public final static int STATUS_SERIAL = 0;
    public final static int STATUS_FINISH = 1;
	
	public int 		id;
	
	public String 		bid;		//移动bid      2
	public String 		cid;		// 第一章 cid  2
	public String 	name; 		// 书名
	public String 	author;		//  作者
	public int 		status;				
	public String   wordNum; // 字数   	2
	public  String 	shortDesc; // 短简介   2
	public  String 	longDesc; // 长简介	2
	public  String  littleCoverUrl; // 小图片url
	public  String  bigCoverUrl; // 大图片url
	
	public String   classFication;  // 分类 2
	public String 	clickStr;		// 点击量    2
	public  int   	freeCount;     // 免费章节数
	public  int   	totalCount;     // 总章节数
	public  int      isUpdata;    // 书籍的是否有更新的标识
	
	public String 	path;
	public String 	coverUrl;
    public String 	detailUrl;
    public String 	lastCid;   	// 最后章节的cid(最新更新)
    public String 	lastTitle;	// 最后章节的目录名
	public int 		lastChapterPos;  // 上次阅读到的最后章节
    public int 		lastPosition; // 最后阅读到的最后章节的页码偏移
	public long 	lastDate;			// 书籍上次更新时间
	public int 		onlineID;			// 本地书籍id -1，  网络书籍id从服务器取得
    public long 	chapterTotal;		// 本地缓存的在线目录中章节总数,  对于没有目录的书籍则存储书籍大小
    public int		times;				// 阅读次数

    public long		timeStamp;          //进入书架的时间

    public String  lastUpdata;    		// 上次更新时间
		


    public String getPercent() {
    	double percent = getPercentDouble();

    	DecimalFormat df = new DecimalFormat("#0.0");
    	String strPercent = df.format(percent * 100) + "%";
        
        return strPercent;
    }
//	@Override
//	public int compareTo(BookItem another) {
//		// TODO Auto-generated method stub
//		// 1.阅读频次
//		// 2.阅读进度
//		// 3.书名
////		if(times == another.times) {
////			int th = (int)this.getPercentDouble() * 100;
////			int other = (int)another.getPercentDouble() * 100;
////			int com = other - th;
////			if(0 == com) {
////				return name.compareTo(another.name);
////			} else {
////				return com;
////			}
////		} else {
////			return another.times - times;
////		}
//		return 1;
//	}
    //根绝时间戳排序
    @Override
    public int compareTo(BookItem another) {
    	if(timeStamp>another.timeStamp)
    	     return -1;
    	else
    		 return 1;
    		
    	
    }
	
	private double getPercentDouble() {
		double percent;
		if(lastChapterPos == 0 && lastPosition == 0) {
			percent = 0;
		} else {
			percent = (lastChapterPos) * 1.0 /chapterTotal;
		}
		return percent;
	}

}
