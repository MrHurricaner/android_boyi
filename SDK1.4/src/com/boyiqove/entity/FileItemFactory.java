package com.boyiqove.entity;

import java.io.File;

import com.boyiqove.AppData;
import com.boyiqove.util.BroswerFileFliter;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.FileUtil;


public class FileItemFactory {
	
	private final static String TAG = "FileItemFactory";

	public static FileItem create(File file) {
		if (file.isDirectory()) {
			return getDirectFileItem(file);
		} else if (file.isFile()) {
			return getBookFileItem(file);
		} else {
			DebugLog.d(TAG, "未知文件");
			return null;
		}

	}

	private static FileItem getDirectFileItem(File file) {
		FileItem item = new FileItem(FileItem.FIEL_DIRECT);
		
		// 0.path
		item.path = file.getPath();
		
		// 1. name
		item.name = file.getName();

		// 2. size
		File infile = new File(file.getPath());
		File[] filelist = infile.listFiles(new BroswerFileFliter());
		if (null != filelist) {
			item.size = filelist.length;
		} else {
			item.size = 0;
		}

		// 3. type
		//item.type = FileItem.FIEL_DIRECT;

		// 4. bookstore
		item.bookstate = FileItem.BOOK_NOT;
        
		// 5. pinyin
        item.pinyin = FileUtil.getHanyuPinyinString(item.name);

		return item;
	}

	private static FileItem getBookFileItem(File file) {
		FileItem item = new FileItem(FileItem.FILE_TXT);
		// 0.path
		item.path = file.getPath();
		
		// 1. name
		item.name = file.getName();

		// 2. size
		item.size = file.length();

		// 3. type
		//item.type = FileItem.FILE_TXT;

		// 4. bookstore // 在数据库中查找是否在书架上
		if(AppData.getDataHelper().foundBookLocal(file.getPath(), file.getName())) {
			item.bookstate = FileItem.BOOK_ONSHELF;
		} else {
			item.bookstate = FileItem.BOOK_DESSELECT;
		}
		
		// 5. 日期
		item.date = file.lastModified();
        
		// 6. pinyin
        item.pinyin = FileUtil.getHanyuPinyinString(item.name);
		

		return item;

	}
}
