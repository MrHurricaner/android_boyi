package com.xiaoyan.util;

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
import com.xn.xiaoyan.user.GameItem;

public class DBGameHelper extends SQLiteOpenHelper{
	private static final String SQL_NAME = "gamelist.db";
	public DBGameHelper(Context context, String name) {
		super(context, SQL_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		DebugLog.d("DBContentHelper", "onCreate");

		db.execSQL("CREATE TABLE IF NOT EXISTS game"
				+ "(id INTEGER PRIMARY KEY, " + "name VARCHAR,"+"package VARCHAR,"+"starnums VARCHAR,"
				+"showimage VARCHAR,"+"activityUms VARCHAR,"+"description VARCHAR,"+"download VARCHAR,"
				+"downNum VARCHAR,"+"cate VARCHAR,"+"issueDate INTEGER,"+"createDate VARCHAR,"+"apkSize INTEGER,"
				+ "netSize INTEGER,"+ "isTop INTEGER,"+ "lastSize BIGINT,"+ "version VARCHAR,"+ "updatetime INTEGER);");
		//db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
    
	
	public void insertGameList(List<GameItem> list) {
        SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try {
			for (int i = 0; i < list.size(); i++) {
				GameItem item = list.get(i);
				db.execSQL(
						"insert or ignore into game values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)",
						new Object[] {item.name,item.packagename,item.starnums,item.showimage,item.activityUms,
								item.description,item.download,item.downNum,item.cate,item.issueDate,item.createDate,
								item.apkSize,item.netApkSize, item.isTop,item.lastSize,  item.version, item.updatetime});
			}

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
    
	public ArrayList<GameItem> getGameList() {
        ArrayList<GameItem> list = new ArrayList<GameItem>();
		
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        try{
        	c = db.rawQuery("select * from game;", null);
	        while(c.moveToNext()) {
	        	GameItem item = new GameItem();
	        	item.id = c.getInt(0);
	            item.name = c.getString(1);
	            item.packagename = c.getString(2);
	            item.starnums= c.getString(3);
	            item.showimage= c.getString(4);
	            item.activityUms= c.getString(5);
				item.description= c.getString(6);
				item.download= c.getString(7);
				item.downNum= c.getString(8);
				item.cate= c.getString(9);
				item.issueDate= c.getLong(10);
				item.createDate= c.getLong(11);
				item.apkSize= c.getInt(12);
				item.netApkSize=c.getFloat(13);
				item.isTop= c.getInt(14);
				item.lastSize= c.getLong(15);
				item.version= c.getString(16);
				item.updatetime= c.getLong(17);
	            list.add(item);
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

    public void updateEndTop(int id, int downSize) {
    	
        SQLiteDatabase db = getWritableDatabase();
        id++;
		ContentValues value = new ContentValues();
        value.put("lastSize", (long)downSize);
//        DebugLog.e("更新itop的值为", "第"+id+"条的itop"+downSize+"");
        db.update("game", value, "id = ?", new String[]{String.valueOf(id)});
    	
    }
    public void updateTotalSize(int id, int downSize) {
    	
    	SQLiteDatabase db = getWritableDatabase();
    	id++;
    	ContentValues value = new ContentValues();
    	value.put("apkSize", downSize);
//        DebugLog.e("更新itop的值为", "第"+id+"条的itop"+downSize+"");
    	db.update("game", value, "id = ?", new String[]{String.valueOf(id)});
    	
    }
  
//    public void updateType(int id, int type) {
//        SQLiteDatabase db = getWritableDatabase();
//
//		ContentValues value = new ContentValues();
//        value.put("type", type);
//        
//        db.update("game", value, "id = ?", new String[]{String.valueOf(id)});
//    	
//    }
// 查询数据个数
    public long fetchPlacesCount() {
    	 SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT COUNT(*) FROM " + "game";
        SQLiteStatement statement = db.compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }
//    // 查询免费书籍条数
//    public long getFreeCount() {
//    	SQLiteDatabase db = getWritableDatabase();
//    	 String sql = "SELECT COUNT(*) FROM chapter where type=0 ;";
//         SQLiteStatement statement = db.compileStatement(sql);
//         long count = statement.simpleQueryForLong();
//    	return count;
//    	
//    }

}
