package com.boyiqove.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Paint.Cap;

import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.entity.OnlineChapterInfo.Status;
import com.boyiqove.util.DebugLog;

public class DBXNContentHelper extends SQLiteOpenHelper{
    
	public DBXNContentHelper(Context context, String name) {
		super(context, name, null, DBManager.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		DebugLog.d("DBContentHelper", "onCreate");
	
		db.execSQL("CREATE TABLE IF NOT EXISTS xnchapter "
				+ "(id INTEGER PRIMARY KEY, " +"status INTEGER,"+"type INTEGER,"+ "name VARCHAR);"
				);
		//db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
    
	public void deleteQBBook(String bid) {
		SQLiteDatabase db = getReadableDatabase();
		
		db.beginTransaction();
		
		try {
			db.execSQL("delete from xnchapter where id = ?;", 
					new String[]{String.valueOf(bid)});
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		
	}
	
	
	public void insertChapterList(List<OnlineChapterInfo> list) {
        SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try {
			for (int i = 0; i < list.size(); i++) {
				OnlineChapterInfo item = list.get(i);
				db.execSQL(
						"insert or ignore into xnchapter values(?, ?, ?,?)",
						new Object[] {item.id,item.status,item.type, item.name});
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} finally {
//			db.endTransaction();
		}
	}
    
	public void updateStatus(int id, Status status) {
        SQLiteDatabase db = getWritableDatabase();
        
		ContentValues value = new ContentValues();
        value.put("status", status.index);
        
        db.update("xnchapter", value, "id = ?", new String[]{String.valueOf(id)});
    	
    }
	public void updateType(int id, int type) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues value = new ContentValues();
		value.put("type", type);
		
		db.update("xnchapter", value, "id = ?", new String[]{String.valueOf(id)});
		
	}
	
	public ArrayList<OnlineChapterInfo> getChapterList() {
        ArrayList<OnlineChapterInfo> list = new ArrayList<OnlineChapterInfo>();
		
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        try{
        	c = db.rawQuery("select * from xnchapter;", null);
	        while(c.moveToNext()) {
	            OnlineChapterInfo info = new OnlineChapterInfo();
	        	info.id = c.getInt(0);
	        	info.status=Status.getStatus(c.getInt(1));
	        	info.type = c.getInt(2);
	            info.name = c.getString(3);
//	            info.cid = c.getString(2);
//	            info.status = Status.getStatus(c.getInt(4));	            
	            list.add(info);
	        }
        }
        finally{
        	try{
        		if(c != null){
        			c.close();
        		}
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
        }
        
        return list;
	}

 

}
