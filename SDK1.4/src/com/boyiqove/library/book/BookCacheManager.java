package com.boyiqove.library.book;

import com.boyiqove.library.book.BookView.PageIndex;

public class BookCacheManager {
	private final int SIZE = 3; 	
	private BookCache mCache[] = new BookCache[SIZE];
	private PageIndex mIndexs[] = new PageIndex[SIZE];
	private Class<?>  mCacheClass;
	
	public BookCacheManager(Class<?> cacheClass) {
		mCacheClass = cacheClass;
	}
	
	public BookCache getCache(PageIndex index) {
		
		for(int i = 0; i < SIZE; i++) {
			if(index == mIndexs[i]) {
				return mCache[i];
			}
		}
		
		int i = getInternalIndex();
		mIndexs[i] = index;
		if(null == mCache[i]) {
			try {
				mCache[i] = (BookCache) mCacheClass.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mCache[i].clear();
		
		return mCache[i];
	}
	
	// 找到一个还未使用的cache
	private int getInternalIndex() {
		for(int i = 0; i < SIZE; i++) {
			if(null == mIndexs[i]) {
				return i;
			}
		}
		
		for(int i = 0; i < SIZE; i++) {
			if(mIndexs[i] != PageIndex.current) {
				return i;
			}
		}
		
		throw new RuntimeException("the PageIndex of cache is impossible");
	}
	
	// 重新解析cache
	public void reset() {
		for(int i = 0; i < SIZE; i++) {
			if(null != mCache[i]) {
				if(mIndexs[i] == PageIndex.current) {
					mCache[i].reset(false);
				} else {
					mCache[i].reset(true);
				}
			}
		}
	}
	
	// 清除索引
	public void clear() {
		for(int i = 0; i < SIZE; i++) {
			mIndexs[i] = null;
		}
	}
	
	public void move(boolean forward) {
		for(int i = 0; i < SIZE; i++) {
			if(null == mIndexs[i]) {
				continue;
			}
			mIndexs[i] = forward ? mIndexs[i].getPrevious() : mIndexs[i].getNext();
			if(null == mIndexs[i]) {
				// 清空cache
				mCache[i].clear();
			}
		}
	}
}
