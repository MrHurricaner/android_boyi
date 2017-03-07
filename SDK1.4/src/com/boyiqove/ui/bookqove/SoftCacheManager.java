package com.boyiqove.ui.bookqove;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

public class SoftCacheManager {
	private static SoftCacheManager softCacheManager;
	private HashMap<String, SoftReference<Bitmap>> softHashMap = new HashMap<String, SoftReference<Bitmap>>();

	public static SoftCacheManager getInstance() {
		if (softCacheManager == null) {
			softCacheManager = new SoftCacheManager();
		}
		return softCacheManager;
	}

	// 从软引用区中获取指定图片
	public Bitmap getBitmap(String url) {
		if (softHashMap.get(url) != null) {
			Bitmap bitmap = softHashMap.get(url).get();
			return bitmap;
		}
		return null;
	}
	//向软引用区放入指定图片
	public void putBitmap(String url, Bitmap bitmap) {
		softHashMap.put(url, new SoftReference<Bitmap>(bitmap));
	}
	//从软引用区中移除指定图片
	public void removeBitmap(String url) {
		softHashMap.remove(url);
		
	}
	
}
