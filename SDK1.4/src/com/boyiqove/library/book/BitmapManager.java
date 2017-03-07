package com.boyiqove.library.book;

import com.boyiqove.library.book.BookView.PageIndex;

import android.graphics.Bitmap;

 
public class BitmapManager {
	private final int SIZE = 2;			// 两张位图，一张绘制当前页，一张绘制一下页
	private final Bitmap[] mBitmaps = new Bitmap[SIZE];
	private final PageIndex[] mIndexes = new PageIndex[SIZE];
	
	private int mWidth;
	private int mHeight;
	
	public BitmapManager() {
	}
	
	public void setSize(int w, int h) {
		if(mWidth != w ||mHeight != h){
			mWidth = w;
			mHeight = h;
			for(int i = 0; i < SIZE; i++) {
				mBitmaps[i] = null;
				mIndexes[i] = null;
			}
		}
	}
	
	public Bitmap getBitmap(PageIndex index) {
		for(int i = 0; i < SIZE; i++) {
			if(mIndexes[i] == index) {
				// 该位图在已存在
				return mBitmaps[i];
			}
		}
		
		// 找一块空闲的bitmap
		int nIndex = getInternalIndex(index);
		if(mBitmaps[nIndex] == null) {
			// 防止OutOfMemoryError
            try{
            	mBitmaps[nIndex] = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
            	
            } catch(OutOfMemoryError e) {
            	System.gc();
            	System.gc();
            	mBitmaps[nIndex] = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
            }
		}
		
		// 绘制位图
		drawOnBitmap(mBitmaps[nIndex], index);
		
		return mBitmaps[nIndex];
	}
	
	private int getInternalIndex(PageIndex index) {
		for(int i = 0; i < SIZE; i++) {
			if(mIndexes[i] == null) {
				// 有空闲的bitmap
				return i;
			}
		}
		
		for(int i = 0; i < SIZE; i++) {
			if(mIndexes[i] != PageIndex.current) {
				// 有当前bitmap之外的
				return i;
			}
		}
		
		throw new RuntimeException("that index is impossible");
	}
	
	public void shift(boolean forward) {
		for(int i = 0; i < SIZE; i++) {
			if(mIndexes[i] == null) {
				continue;
			}
			mIndexes[i] = forward ? mIndexes[i].getPrevious() : mIndexes[i].getNext();
		}
	}
	
	public void reset() {
		for(int i = 0; i < SIZE; i++) {
			mIndexes[i] = null;
		}
	}
    
	
	public interface OnDrawBitmapListener {
        public void onDrawBitmap(Bitmap bitmap, PageIndex index);
	}
    
    private OnDrawBitmapListener mDrawBitmapListener = null;
    
    public void setOnDrawBitmapListener(OnDrawBitmapListener listener) {
    	this.mDrawBitmapListener = listener;
    }
    
	public void drawOnBitmap(Bitmap bitmap, PageIndex index) {
		if(null != mDrawBitmapListener) {
			mDrawBitmapListener.onDrawBitmap(bitmap, index);
		}
	}
}
