package com.boyiqove.library.book;

import java.util.Vector;

abstract public class BookCache {
	
	abstract public void reset(boolean dealy);
	abstract public void parse(String chapter, String buf);
	
	abstract public int getPageCount();
	abstract public int getCurPage();
	abstract public int getCurPagePosition();		// 当前偏移字符
	
	
	abstract public void setPage(int pagePos);
	abstract public void setPosition(int position);	// 根据偏移，设置页数
	
	abstract public Vector<String> getLines();
    abstract public Vector<String> getChLines();
	abstract public boolean pageUp();
	abstract public boolean pageDown();
	
	abstract public void pageFirst();
	abstract public void pageEnd();
	
	
	abstract public void clear();
	
}
