package com.boyiqove.entity;

public class FileItem implements Comparable<FileItem>{
	public String path;
	public String name;		// 目录或文件名
	public long size;		// 目录下文件夹数或文件大小
	public final int type;	// 目录，txt文件
	public int bookstate;	// 1.已加入书架， 2.未选中， 3.选中加入书架, 4.其他文件
	public long date;		// 文件日期
    
	public String pinyin;	// 文件名首字符
	
	public final static	int FIEL_DIRECT = 1;
	public final static int FILE_TXT = 2;
	
	public final static int BOOK_NOT 		= -1;
	public final static int BOOK_ONSHELF 	= 0;
	public final static int BOOK_DESSELECT 	= 1;
	public final static int BOOK_SELECTED 	= 2;
    
	public FileItem(int type) {
		this.type = type;
	}
	
	@Override
	public int compareTo(FileItem another) {
		// TODO Auto-generated method stub
        
		if(another.type == type) {
			return pinyin.compareTo(another.pinyin);
		} else {
			return another.type - type;
		}
		
	}
	
}
