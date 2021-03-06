package com.xn.xiaoyan.downgame;

import java.io.File;
import java.util.List;

import com.xn.xiaoyan.user.GameItem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 利用数据库来记录下载信息
 * @author acer
 */
public class DownLoadHelper extends SQLiteOpenHelper{

	private static final String SQL_NAME = "download.db";
	private static final int DOWNLOAD_VERSION=1;
	
	public DownLoadHelper(Context context,String DBname) {
		
		super(context, DBname, null, DOWNLOAD_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	 /**
     * 在download.db数据库下创建一个download_info表存储下载信息
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table download_info(_id integer PRIMARY KEY AUTOINCREMENT, thread_id integer, "
                + "start_pos integer, end_pos integer, compelete_size integer,url char)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
