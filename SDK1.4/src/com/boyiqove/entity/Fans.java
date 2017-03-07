package com.boyiqove.entity;

import java.io.Serializable;

public class Fans implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -9074501999102114270L;
    
	public final static int ATTENTION_ALLOW = 1;
	public final static int ATTENTION_CLOSE = 2;
    
	public String photoUrl;
    public String nickname;
    public int 	userid;
    public String signature;
    public int status;		// 1.允许关注， 2.屏蔽

}
