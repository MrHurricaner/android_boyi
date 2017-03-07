package com.boyiqove.db;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.BoyiMessage;
import com.boyiqove.util.DebugLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * 
 */
public class DBDataHelper extends SQLiteOpenHelper {
	//public static final String DATABASE_NAME = "data.db";

	public DBDataHelper(Context context, String name) {
		super(context, name, null, DBManager.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		DebugLog.d("DBHelper", "onCreate");
		// 创建书表
		db.execSQL("CREATE TABLE IF NOT EXISTS book "
				+ "(id INTEGER PRIMARY KEY, " + "path VARCHAR,"
				+  "classFication VARCHAR,"+  "clickStr VARCHAR,"
				+"name VARCHAR," + "author VARCHAR," + "coverUrl VARCHAR," + "detailUrl VARCHAR," 
				+ "lastChapterPos INTEGER," + "lastPosition INTEGER," + "chapterTotal BIGINT,"
				+ "lastDate BIGINT," + "onlineID INTEGER,"+ "status INTEGER,"  + "times INTEGER);");
		
		// 创建XN移动书表
		db.execSQL("CREATE TABLE IF NOT EXISTS boebook "
				+ "(id INTEGER PRIMARY KEY, " + "bid VARCHAR,"+ "cid VARCHAR,"
				+ "name VARCHAR," + "author VARCHAR,"+ "status INTEGER,"+ "wordNum VARCHAR," + "shortDesc VARCHAR,"
				+ "longDesc VARCHAR,"
				+ "littleCoverUrl VARCHAR," + "bigCoverUrl VARCHAR," + "classFication VARCHAR," + "clickStr VARCHAR," 
				+ "freeCount INTEGER," + "totalCount INTEGER,"+ "isUpdata INTEGER," + "lastDate VARCHAR,"
				+ "lastChapterPos INTEGER,"  +  "lastPosition INTEGER,"+"timeStamp BIGINT,"+"lastChapterName VARCHAR);");


		
		// 创建消息表
        db.execSQL("CREATE TABLE IF NOT EXISTS boyi_message "
        		+ "(id INTEGER PRIMARY KEY, " + "type VARCHAR,"
        		+ "fromID INTEGER," + "toID INTEGER," + "fromName VARCHAR," 
        		+ "content VARCHAR," + "status INTEGER, " + "time BIGINT);");

		//db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub	
//		 try {
//             db = this.getReadableDatabase();
//             String sql = "select count(*) as c from Sqlite_master where type ='table' and name ='qovebook'";
//             Cursor cursor = db.rawQuery(sql, null);
//             if(cursor.moveToNext()){
//                     int count = cursor.getInt(0);
//     } catch (Exception e) {
//             // TODO: handle exception
//     }    
		if(oldVersion == 1 && newVersion == 2){
		try {			
			String sql2 = "ALTER TABLE qvodbook RENAME TO boebook;";
			db.execSQL(sql2);    
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(oldVersion ==2 && newVersion==3)
		{
			try{
				String sql3="ALTER TABLE boebook ADD timeStamp BIGINT NOT NULL DEFAULT 0";
				String sql4="ALTER TABLE boebook ADD lastChapterName VARCHAR NOT NULL DEFAULT 0";
				db.execSQL(sql3);
				db.execSQL(sql4);
			}catch(Exception e){
				
			}
		}
        }
	}

	public void insertBookList(List<BookItem> list) {
		SQLiteDatabase db = getWritableDatabase();

		db.beginTransaction();

		try {
			for (int i = 0; i < list.size(); i++) {
				BookItem item = list.get(i);
				db.execSQL(
						"insert into book values(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] {item.path, item.name, item.author,
								item.coverUrl, item.detailUrl, 
								item.lastChapterPos, item.lastPosition, 
								item.chapterTotal, item.lastDate,
								item.onlineID, item.status, item.times});
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}

	public void insertBookMap(Map<String, BookItem> map) {
		SQLiteDatabase db = getWritableDatabase();

		db.beginTransaction();
		try {
			Iterator<Entry<String, BookItem>> iter = map.entrySet().iterator();
			while(iter.hasNext()) {
				Map.Entry<String, BookItem> entry = iter.next();
				BookItem item = entry.getValue();
				db.execSQL(
						"insert into book values(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] {item.path, item.name, item.author,
								item.coverUrl, item.detailUrl, 
								item.lastChapterPos, item.lastPosition, 
								item.chapterTotal, item.lastDate,
								item.onlineID, item.status, item.times});
			}
           
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	// 插入XN数据库
	public void insertKBBook(BookItem item) {
		SQLiteDatabase db = getWritableDatabase();

		db.beginTransaction();

		try {
			db.execSQL( 
			
					"insert into boebook values(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?)",
					
					new Object[] {item.bid, item.cid, item.name,item.author,
							item.status, item.wordNum,
							item.shortDesc, item.longDesc, 
							item.littleCoverUrl, item.bigCoverUrl,item.classFication,
							item.clickStr,  item.freeCount,  item.totalCount,item.isUpdata,item.lastDate,item.lastChapterPos,item.lastPosition,item.timeStamp,item.lastTitle});
			
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}
	public void insertBook(BookItem item) {
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		
		try {
			db.execSQL(
					"insert into book values(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					new Object[] {item.path, item.name, item.author,
							item.coverUrl, item.detailUrl,
							item.lastChapterPos, item.lastPosition, 
							item.chapterTotal, item.lastDate,
							item.onlineID, item.status, item.times});
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		
	}
	
	// 查询boe中某本书
	public BookItem getBookItem(int onlineID) {
        if(onlineID == BookItem.ON_LOCAL_TXT) {
            throw new RuntimeException();
        }
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = null;
        try{
        	cursor= db.rawQuery("select * from boebook where bid = ?", new String[]{String.valueOf(onlineID)});       
	        int count = cursor.getCount();
	        if(count == 1 && cursor.moveToNext()) {
	        	BookItem item = new BookItem();
				item.id = cursor.getInt(0);
				item.bid = cursor.getString(1);
				item.cid = cursor.getString(2);
				
				item.name = cursor.getString(3);
				item.author = cursor.getString(4);
				item.status = cursor.getInt(5);
				item.wordNum=cursor.getString(6);
				item.shortDesc=cursor.getString(7);
				item.longDesc=cursor.getString(8);
				item.littleCoverUrl=cursor.getString(9);
				item.bigCoverUrl=cursor.getString(10);
				item.classFication=cursor.getString(11);
				item.clickStr=cursor.getString(12);
				item.freeCount=cursor.getInt(13);
				item.totalCount=cursor.getInt(14);
				item.isUpdata=cursor.getInt(15);
				item.lastDate=cursor.getLong(16);
				item.lastChapterPos = cursor.getInt(17);
				item.lastPosition = cursor.getInt(18);
				item.lastTitle = cursor.getString(20);
	            return item;
//	            return cursor.getInt(0);
	        } else {
	        	throw new RuntimeException();
	        } 
		} finally { 
			  if (cursor != null) { 
			  try { 
				  cursor.close(); 
			  } catch (Exception e) { 
			  //ignore this 
			  } 
			 } 
		}
        
	}
	public int getKBBookID(int onlineID) {
        if(onlineID == BookItem.ON_LOCAL_TXT) {
            throw new RuntimeException();
        }
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = null;
        try{
        	cursor= db.rawQuery("select id from boebook where bid = ?", new String[]{String.valueOf(onlineID)});
        
	        int count = cursor.getCount();
	        if(count == 1 && cursor.moveToNext()) {
	            return cursor.getInt(0);
	        } else {
	        	throw new RuntimeException();
	        } 
		} finally { 
			  if (cursor != null) { 
			  try { 
				  cursor.close(); 
			  } catch (Exception e) { 
			  //ignore this 
			  } 
			 } 
		}
        
	}
    
	public int getBookID(int onlineID) {
        if(onlineID == BookItem.ON_LOCAL_TXT) {
            throw new RuntimeException();
        }
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = null;
        try{
        	cursor = db.rawQuery("select id from book where onlineID = ?", new String[]{String.valueOf(onlineID)});
        
        
	        if(cursor.getCount() == 1 && cursor.moveToNext()) {
	            return cursor.getInt(0);
	            
	        } else {
	        	throw new RuntimeException();
	        }
        } finally { 
		  if (cursor != null) { 
		  try { 
			  cursor.close(); 
		  } catch (Exception e) { 
		  //ignore this 
		  } 
		 } 
	}
        
	}

	// 
	public boolean foundBookLocal(String filePath, String name) {
		boolean bRet = false;

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = null;
        try{
        	cursor = db.rawQuery("select * from book where path = ? and name = ?; ", 
				new String[]{filePath, name});
			bRet = cursor.moveToNext();
			return bRet;
        } finally { 
		  if (cursor != null) { 
		  try { 
			  cursor.close(); 
		  } catch (Exception e) { 
		  //ignore this 
		  } 
		  }
        }
	}

	public boolean foundBookOnline(int onlineID) {
		boolean bRet = false;

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = null;
        try{
        	cursor = db.rawQuery("select * from book where onlineID = ?; ", 
				new String[]{String.valueOf(onlineID)});
        	bRet = cursor.moveToNext();
        	return bRet;
        }
        finally { 
  		  if (cursor != null) { 
  		  try { 
  			  cursor.close(); 
  		  } catch (Exception e) { 
  		  //ignore this 
  		  } 
  		  }
         }
	}
	
	
	public boolean foundBookBid(String  bid) {
		boolean cRet = false;
		
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = null;
        try{
        	cursor = db.rawQuery("select * from boebook where bid = ?; ", 
				new String[]{String.valueOf(bid)});
			cRet = cursor.moveToNext();
			return cRet;
        }
        finally { 
  		  if (cursor != null) { 
  		  try { 
  			  cursor.close(); 
  		  } catch (Exception e) { 
  		  //ignore this 
  		  } 
  		  }
         }
	}
    // XN查询数据库
	public List<BookItem> getKbShelfList() {
		List<BookItem> list = new ArrayList<BookItem>();

		SQLiteDatabase db = getWritableDatabase();
		Cursor cur = null;
        try{
        	cur = db.rawQuery("select * from boebook;", null);
		while (cur.moveToNext()) {
			BookItem item = new BookItem();
			item.id = cur.getInt(0);
			item.bid = cur.getString(1);
			item.cid = cur.getString(2);
			
			item.name = cur.getString(3);
			item.author = cur.getString(4);
			item.status = cur.getInt(5);
			item.wordNum=cur.getString(6);
			item.shortDesc=cur.getString(7);
			item.longDesc=cur.getString(8);
			item.littleCoverUrl=cur.getString(9);
			item.bigCoverUrl=cur.getString(10);
			item.classFication=cur.getString(11);
			item.clickStr=cur.getString(12);
			item.freeCount=cur.getInt(13);
			item.totalCount=cur.getInt(14);
			item.isUpdata=cur.getInt(15);
			item.lastDate=cur.getLong(16);
			item.lastChapterPos = cur.getInt(17);
			item.lastPosition = cur.getInt(18);
			item.timeStamp=cur.getLong(19);
			list.add(item);
//			DebugLog.e("数据库中书名", item.name);
		}
        }
        finally { 
  		  if (cur != null) { 
  		  try { 
  			cur.close(); 
  			
  		  } catch (Exception e) { 
  		  //ignore this 
  		  } 
  		  }
  		if(db!=null)
		{
				db.close();
		}
         }		
		Collections.sort(list);

		return list;
	}
	
	// 博易查询数据库
	public List<BookItem> getBookShelfList() {
		List<BookItem> list = new ArrayList<BookItem>();
		
		SQLiteDatabase db = getWritableDatabase();
		Cursor cur = null;
        try{
        	cur = db.rawQuery("select * from book;", null);
			while (cur.moveToNext()) {
				BookItem item = new BookItem();
				item.id = cur.getInt(0);
				item.path = cur.getString(1);
				item.name = cur.getString(2);
				item.author = cur.getString(3);
				item.coverUrl = cur.getString(4);
				item.detailUrl = cur.getString(5);
				item.lastChapterPos = cur.getInt(6);
				item.lastPosition = cur.getInt(7);
				item.chapterTotal = cur.getLong(8);
				item.lastDate = cur.getLong(9);
				item.onlineID = cur.getInt(10);
				item.status = cur.getInt(11);
				item.times = cur.getInt(12);
				list.add(item);
			}
        }
        finally { 
  		  if (cur != null) { 
  		  try { 
  			cur.close(); 
  		  } catch (Exception e) { 
  		  //ignore this 
  		  } 
  		  }
         }		
		
		Collections.sort(list);
		
		return list;
	}


	public boolean updateLastReadLocal(int id, int lastChapterPos, int lastPosition, long chapterTotal) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues value = new ContentValues();
		value.put("lastChapterPos", lastChapterPos);
		value.put("lastPosition", lastPosition);
		//value.put("lastDate", lastDate);
		value.put("chapterTotal", chapterTotal);

		db.update("book", value, "id = ?", new String[]{String.valueOf(id)});

		return true;
	}
	
	// 阅读时保存XN
	
	public boolean updateLastReadLocalKB(int id, int lastChapterPos, int lastPosition, long chapterTotal,long timeStamp) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues value = new ContentValues();
		value.put("lastChapterPos", lastChapterPos);
		value.put("lastPosition", lastPosition);
		value.put("chapterTotal", chapterTotal);
		//value.put("lastDate", lastDate);
		value.put("timeStamp",timeStamp);
		db.update("boebook", value, "id = ?", new String[]{String.valueOf(id)});
//		db.update("book", value, "id = ?", new String[]{String.valueOf(id)});
		
		return true;
	}

	public boolean updateLastReadOnline(int onlineID, int lastChapterPos, int lastPosition, int status) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues value = new ContentValues();
		value.put("lastChapterPos", lastChapterPos);
		value.put("lastPosition", lastPosition);
        value.put("status", status);
        
		db.update("book", value, "onlineID = ?", new String[]{String.valueOf(onlineID)});

		return true;
	}
    
	public boolean updateLastKBReadOnline(int onlineID, int lastChapterPos, int lastPosition, int status,long timeStamp) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues value = new ContentValues();
		value.put("lastChapterPos", lastChapterPos);
		value.put("lastPosition", lastPosition);
        value.put("status", status);
        value.put("timeStamp",timeStamp);
		db.update("boebook", value, "bid = ?", new String[]{String.valueOf(onlineID)});

		return true;
	}
	
	public boolean updateOnlineBook(String bid, int lastChapterPos, int lastPosition, int totalCount, String bigCoverUrl,int isUpdata) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues value = new ContentValues();
		value.put("lastChapterPos", lastChapterPos);
		value.put("lastPosition", lastPosition);
        value.put("totalCount", totalCount);
        value.put("bigCoverUrl", bigCoverUrl);
        value.put("isUpdata",isUpdata);
		db.update("boebook", value, "bid = ?", new String[]{String.valueOf(bid)});

		return true;
	}
	
	// 更新XN数据库
	public boolean updateQoveBook(String bid,int status, int isUpdata,int totalCount) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues value = new ContentValues();
		value.put("bid", bid);
		value.put("status", status);
		value.put("isUpdata", isUpdata);;
		value.put("totalCount", totalCount);
		db.update("boebook", value, "bid = ?", new String[]{String.valueOf(bid)});		
		return true;
	}
	// 更新XN数据表 目录总数
	public boolean updateXnBook(String bid,int totalCount) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues value = new ContentValues();
		value.put("bid", bid);
		value.put("totalCount", totalCount);
		db.update("boebook", value, "bid = ?", new String[]{String.valueOf(bid)});		
		return true;
	}
    
	
	public void updateLastDateLocal(int id, long lastDate){
		SQLiteDatabase db = getReadableDatabase();

		ContentValues value = new ContentValues();
		value.put("lastDate", lastDate);

		db.update("book", value, "id = ?", new String[]{String.valueOf(id)});
	}

	// 上次更新时间
	public void updateLastDateOnline(int onlineID, long lastDate, int chapterTotal){
		SQLiteDatabase db = getReadableDatabase();

		ContentValues value = new ContentValues();
		value.put("lastDate", lastDate);
        value.put("chapterTotal", chapterTotal);

		db.update("boebook", value, "bid = ?", new String[]{String.valueOf(onlineID)});

	}
	// 更新图片的url
	public void updateImageUrl(int onlineID, String url){
		SQLiteDatabase db = getReadableDatabase();
		
		ContentValues value = new ContentValues();
		value.put("bigCoverUrl", url);
		
		db.update("boebook", value, "bid = ?", new String[]{String.valueOf(onlineID)});
		
	}
	
	// 上次更新时间
		public void updateLastKBDateOnline(int onlineID, long lastDate, int chapterTotal){
			SQLiteDatabase db = getReadableDatabase();

			ContentValues value = new ContentValues();
			value.put("lastDate", lastDate);
	        value.put("totalCount", chapterTotal);

			db.update("boebook", value, "bid = ?", new String[]{String.valueOf(onlineID)});

		}
    
	public void updateBook(List<BookItem> list) {
        SQLiteDatabase db = getReadableDatabase();
        
        for(int i = 0; i < list.size(); i++) {
            BookItem item = list.get(i);
        	Cursor cur = null;
            try{
	        	cur = db.rawQuery("select id from book where onlineID = ?;", new String[]{String.valueOf(item.onlineID)});
	            if(cur.moveToNext()) {
	//            	ContentValues value = new ContentValues();
	//                value.put("", value)
	//                db.update("", values, whereClause, whereArgs);
	            	
	            } else {
	                insertBook(item);
	            }
            }
            finally { 
        		  if (cur != null) { 
        		  try { 
        			cur.close(); 
        		  } catch (Exception e) { 
        		  //ignore this 
        		  } 
        		  }
               }		
        }
    	
    }
	
	public void updateLocalBookTimes(String path, int times) {
		SQLiteDatabase db = getReadableDatabase();
		ContentValues value = new ContentValues();
		value.put("times", times);
		db.update("book", value, "path = ?", new String[]{path});
	}
	
	public void updateOnlineBookTimes(int onlineID, int times) {
		SQLiteDatabase db = getReadableDatabase();
		ContentValues value = new ContentValues();
		value.put("times", times);
		db.update("book", value, "onlineID = ?", new String[]{String.valueOf(onlineID)});
	}
    

	public void deleteBook(int bookID) {
		SQLiteDatabase db = getReadableDatabase();

		db.beginTransaction();

		try {
			db.execSQL("delete from book where id = ?;", 
					new String[]{String.valueOf(bookID)});

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}
	
	public void deleteQBBook(String bid) {
		SQLiteDatabase db = getReadableDatabase();
		
		db.beginTransaction();
		
		try {
			db.execSQL("delete from boebook where bid = ?;", 
					new String[]{String.valueOf(bid)});
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		
	}
    
	
	
	public void deleteBookOnline(int onlineID) {
		SQLiteDatabase db = getReadableDatabase();

		db.beginTransaction();

		try {
			db.execSQL("delete from book where onlineID = ?;", 
					new String[]{String.valueOf(onlineID)});

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}
    
	/***********************************************************************************************************/
    
	public void insertBoyiMessage(List<BoyiMessage> list) {
        SQLiteDatabase db = getWritableDatabase();

		db.beginTransaction();

		try {
			for (int i = 0; i < list.size(); i++) {
				BoyiMessage item = list.get(i);
				db.execSQL(
						"insert or ignore into boyi_message values(?, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] {item.id, item.type, item.fromID, item.toID,
									item.fromName, item.content, item.status, item.time});
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void insertBoyiMessage(BoyiMessage item) {
		SQLiteDatabase db = getWritableDatabase();

		db.beginTransaction();

		try {
			db.execSQL(
					"insert or ignore into boyi_message values(?, ?, ?, ?, ?, ?, ?, ?)",
					new Object[] {item.id, item.type, item.fromID, item.toID,
							item.fromName, item.content, item.status, item.time});
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}
    
    /*\
     * 
     */
	public List<BoyiMessage> getBoyiMessageList(int type, long afterTime) {
        List<BoyiMessage> list = new ArrayList<BoyiMessage>();
		
		SQLiteDatabase db = getWritableDatabase();
		Cursor cur = null;
		try{
	        cur = db.rawQuery("select * from boyi_message where type = ? and time >= ?", 
	        		new String[]{String.valueOf(type), String.valueOf(afterTime)});
	        
	        while(cur.moveToNext()) {
	            BoyiMessage item = new BoyiMessage();
	        	item.id = cur.getInt(0);
	            item.type = cur.getInt(1);
	            item.fromID = cur.getInt(2);
	            item.toID = cur.getInt(3);
	            item.fromName = cur.getString(4);
	            item.content = cur.getString(5);
	            item.status = cur.getInt(6);
	            item.time = cur.getLong(7);
	            
	            list.add(item);
	        }
		}
        finally { 
  		  if (cur != null && db!=null) { 
  		  try { 
  			cur.close(); 
  			db.close();
  		  } catch (Exception e) { 
  		  //ignore this 
  		  } 
  		  }
         }		
        
        return list;
	}
    
    
	

}