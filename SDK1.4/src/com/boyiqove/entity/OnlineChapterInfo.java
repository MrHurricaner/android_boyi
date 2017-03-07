package com.boyiqove.entity;

import java.io.Serializable;

/*
 * 在线小说章节信息
 */
public class OnlineChapterInfo implements Serializable{
    
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8349122332042561411L;
    
	public final static int TYPE_FREE = 0;		// 免费
	public final static int TYPE_NOT_BUY = 1;	// 未购买
	public final static int TYPE_HAS_BUY = 2;	// 已购买
    
	public final static int STATUS_UNLOAD = 1;
	public final static int STATUS_LOADING = 2;
	public final static int STATUS_LOADED = 3;
    
	public enum Status{
		UNLOAD(1),  // 未缓存到本地文件
		LOADING(2), // 正在读取章节， 从网络或本地缓存
		LOADED(3);	// 已经缓存到本地文件, 并且没有被读取
        
        public int index;
        
        Status(int index) {
        	this.index = index;
        }
        
        public static Status getStatus(int index) {
            switch(index) {
        	case 1:
                return UNLOAD;
        	case 2:
        		return LOADING;
        	case 3:
        		return LOADED;
        	default:
                return null;
            }
        }
		
	}
    
    public int 		type;
	public int 		id;
    public String 	name;
    public Status 	status;
    public String 	cid;
    public String   direName;
}
