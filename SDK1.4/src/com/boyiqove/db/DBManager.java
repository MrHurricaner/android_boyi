package com.boyiqove.db;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.boyiqove.util.DebugLog;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager {
	public static final int DATABASE_VERSION = 3;
    
	public static final int TYPE_DATA = 1;
	public static final int TYPE_CHAPTER = 2;
	public static final int TYPE_XN_CHAPTER = 3;
	public static final int TYPE_XY_GAME = 4; // 游戏列表
	
    private Context context;
	private Map<String, SQLiteOpenHelper>  mapHelper;

	public DBManager(Context context) {
		this.context = context;
        mapHelper = new HashMap<String, SQLiteOpenHelper>();
	}

    public SQLiteOpenHelper open(String name, int type) {
    	SQLiteOpenHelper helper = mapHelper.get(name);
        if(helper == null) {
            helper = createSQLiteHelper(name, type);
            mapHelper.put(name, helper);
        }
        
        return helper;
    }
    
    public void close(String name) {
    	SQLiteOpenHelper helper = mapHelper.get(name);
        if(null != helper) {
            mapHelper.remove(helper);
            DebugLog.d("DBManager", "close db:" + name);
        	helper.close();
        }
    }

    public synchronized void clear() {
    	Iterator<Entry<String, SQLiteOpenHelper>> iter = mapHelper.entrySet().iterator();
    	while(iter.hasNext()) {
    		Map.Entry<String, SQLiteOpenHelper> entry = iter.next();
    		entry.getValue().close();
    	}
        mapHelper.clear();
    }
	
    private SQLiteOpenHelper createSQLiteHelper(String name, int type) {
    	switch(type) {
    	case TYPE_DATA:
            return new DBDataHelper(context, name);
    	case TYPE_CHAPTER:
    		return new DBContentHelper(context, name);
    	case TYPE_XN_CHAPTER:
    		return new DBXNContentHelper(context, name);
//    	case TYPE_XY_GAME:
//    		return new DBGameHelper(context, name);
        default:
            throw new RuntimeException("this sqlite helper is impossible");
    	}
    }
	
}
