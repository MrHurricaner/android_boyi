package com.boyiqove.entity;

public class BoyiMessage {
    
	public final static int TYPE_PRIVATE = 1;
	public final static int TYPE_SYSTEM = 2;
    
    public int 		id;				// ?
    public int 		type;			// 1.私信， 2.系统消息
    public int 		fromID;			// 发送者ID
    public int 		toID;			// 接收者ID
    public String 	fromName;		// 发送者名字
    public String 	content;		// 消息内容
    //public int 		msgID;		// 消息ID?
    public int 		status;			// ?
    public long 	time;			// 消息发送时间戳
    
}
