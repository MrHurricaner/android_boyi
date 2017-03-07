package com.boyiqove.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.entity.OnlineChapterInfo.Status;
import com.boyiqove.util.DebugLog;

public class DBContentHelper extends SQLiteOpenHelper{
    
	public DBContentHelper(Context context, String name) {
		super(context, name, null, DBManager.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		DebugLog.d("DBContentHelper", "onCreate");

		db.execSQL("CREATE TABLE IF NOT EXISTS chapter "
				+ "(id INTEGER PRIMARY KEY, " + "name VARCHAR,"
				+ "cid VARCHAR,"
				+ "type INTEGER," + "status INTEGER);");
		//db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
    
	
	public void insertChapterList(List<OnlineChapterInfo> list) {
        SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try {
			for (int i = 0; i < list.size(); i++) {
				OnlineChapterInfo item = list.get(i);
				db.execSQL(
						"insert or ignore into chapter values(?, ?, ?, ?, ?)",
						new Object[] {item.id, item.name,item.cid,
								item.type, item.status });
			}

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
    
	public ArrayList<OnlineChapterInfo> getChapterList() {
        ArrayList<OnlineChapterInfo> list = new ArrayList<OnlineChapterInfo>();
		
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        try{
        	c = db.rawQuery("select * from chapter;", null);
	        while(c.moveToNext()) {
	            OnlineChapterInfo info = new OnlineChapterInfo();
	        	info.id = c.getInt(0);
	            info.name = c.getString(1);
	            info.cid = c.getString(2);
	            info.type = c.getInt(3);
	            info.status = Status.getStatus(c.getInt(4));
	            
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

    public void updateStatus(int id, Status status) {
        SQLiteDatabase db = getWritableDatabase();
        
		ContentValues value = new ContentValues();
        value.put("status", status.index);
        
        db.update("chapter", value, "id = ?", new String[]{String.valueOf(id)});
    	
    }
    
    
    public void updateType(int id, int type) {
        SQLiteDatabase db = getWritableDatabase();

		ContentValues value = new ContentValues();
        value.put("type", type);
        
        db.update("chapter", value, "id = ?", new String[]{String.valueOf(id)});
    	
    }
// 查询数据个数
    public long fetchPlacesCount() {
    	 SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT COUNT(*) FROM " + "chapter";
        SQLiteStatement statement = db.compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }
    // 查询免费书籍条数
    public long getFreeCount() {
    	SQLiteDatabase db = getWritableDatabase();
    	 String sql = "SELECT COUNT(*) FROM chapter where type=0 ;";
         SQLiteStatement statement = db.compileStatement(sql);
         long count = statement.simpleQueryForLong();
    	return count;
    	
    }

}
