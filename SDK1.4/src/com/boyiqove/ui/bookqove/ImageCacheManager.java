package com.boyiqove.ui.bookqove;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import com.boyiqove.library.volley.toolbox.ImageLoader.ImageCache;
import com.boyiqove.util.DebugLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

public class ImageCacheManager implements ImageCache {
	private static ImageCacheManager imageCacheManager;
	private static Context mContext;
	public SoftCacheManager softCacheManager=SoftCacheManager.getInstance();
	// 利用单例模式生成对象
	@SuppressWarnings("unused")
	public static ImageCacheManager getInstance(Context context ) {
		mContext=context;
		if (imageCacheManager == null) {
			imageCacheManager = new ImageCacheManager();

		}
		return imageCacheManager;
	}
	
	// 创建一个LruCache对象
	LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
			40*1024*1024) {

				@Override
				protected void entryRemoved(boolean evicted, String key,
						Bitmap oldValue, Bitmap newValue) {
					// 在此应把被驱逐的图片放入软引用中,gc会被频繁调用，所以软引用不断被回收
					//判断是否为被驱逐
					if (evicted) {
						//oldValue就是被驱逐的图片
						softCacheManager.putBitmap(key, oldValue);
					}
				}

				@Override
				protected int sizeOf(String key, Bitmap value) {
					// TODO Auto-generated method stub
					return value.getRowBytes()*value.getHeight();
				}
				
		//第一个 sizeOf
		//第二个 驱逐
		
	};

	@Override
	public Bitmap getBitmap(String url) {
		// 自定义一级缓存与Volley接口结合的入口
		//当Volley本身获取图片时，会先执行该方法
		//如果重写了就执行自定义的一级缓存
		if (lruCache.get(url)==null) {
			if (softCacheManager.getBitmap(url)!=null) {
				lruCache.put(url, softCacheManager.getBitmap(url));
				softCacheManager.removeBitmap(url);
			}else if (isFileExist(url, mContext)) {
//				从本地读
				Bitmap bitmap=getBitmap(url, mContext);
				lruCache.put(url, bitmap);
			}
		}
		return lruCache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// 自定义一级缓存与Volley接口结合的入口
		DebugLog.e("请求封面信息：", url);
		saveBitmap(bitmap, url, mContext);
		lruCache.put(url, bitmap);
	}
	
	public static void saveBitmap(Bitmap mBitmap, String imageURL, Context cxt) {
		String cidName=imageURL.substring(0, imageURL.lastIndexOf("/"));
		cidName=cidName.substring(cidName.lastIndexOf("/") + 1);
		String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1); //传入一个远程图片的url，然后取最后的图片名字
		bitmapName=cidName+bitmapName;
		DebugLog.e("存的书籍的封面是：", bitmapName);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();


		FileOutputStream fos = null;
		ObjectOutputStream oos = null;


		try {
		fos = cxt.openFileOutput(bitmapName, Context.MODE_PRIVATE);
		oos = new ObjectOutputStream(fos);
		oos.writeObject(byteArray);
		} catch (Exception e) {
		e.printStackTrace();
		// 这里是保存文件产生异常
		} finally {
		if (fos != null) {
		try {
		fos.close();
		} catch (IOException e) {
		// fos流关闭异常
		e.printStackTrace();
		}
		}
		if (oos != null) {
		try {
		oos.close();
		} catch (IOException e) {
		// oos流关闭异常
		e.printStackTrace();
		}
		}
		}
		}
	public static Bitmap getBitmap(String fileName, Context cxt) {
		String cidName=fileName.substring(0, fileName.lastIndexOf("/"));
		cidName=cidName.substring(cidName.lastIndexOf("/") + 1);
		String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1); //传入一个远程图片的url，然后取最后的图片名字
		bitmapName=cidName+bitmapName;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			
		fis = cxt.openFileInput(bitmapName);
		ois = new ObjectInputStream(fis);
		byte[] byteArray = (byte[]) ois.readObject();
		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
		byteArray.length);
		return bitmap;
		} catch (Exception e) {
		e.printStackTrace();
		// 这里是读取文件产生异常
		// 读取产生异常，返回null
		return null;
		} finally {
		if (fis != null) {
		try {
		fis.close();
		} catch (IOException e) {
		// fis流关闭异常
		e.printStackTrace();
		}
		}
		if (ois != null) {
		try {
		ois.close();
		} catch (IOException e) {
		// ois流关闭异常
		e.printStackTrace();
		}
		}
		}
		
		}

	/**
	* 判断本地的私有文件夹里面是否存在当前名字的文件
	*/
	public static boolean isFileExist(String fileName, Context cxt) {
		String cidName=fileName.substring(0, fileName.lastIndexOf("/"));
		cidName=cidName.substring(cidName.lastIndexOf("/") + 1);
		String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1); //传入一个远程图片的url，然后取最后的图片名字
		bitmapName=cidName+bitmapName;
	List<String> nameLst = Arrays.asList(cxt.fileList());
	if (nameLst.contains(bitmapName)) {
	return true;
	} else {
	return false;
	}
	}
	
}
