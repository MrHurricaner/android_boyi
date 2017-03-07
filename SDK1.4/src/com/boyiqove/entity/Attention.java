package com.boyiqove.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * 用户关注信息
 */
public class Attention implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 861851851378835490L;
    
	
	public int 		authorID;
    public String 	photoUrl;
    public String 	author;
    public String 	notice;
    public String 	qqgroup;
    public String 	webchat;
    public String	level;
    public List<Article> articleList = new ArrayList<Article>();
    

    public static class Article implements Serializable{
        /**
		 * 
		 */
		private static final long serialVersionUID = -1019237504957639006L;
        
		public int 		id;
        public String 	name;
    }
}
