package com.boyiqove.util;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapTool {

	public static synchronized Bitmap decodeSampledBitmapFromStream(
			InputStream in, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
        if(null == in) {
        	throw new RuntimeException();
        }
		return BitmapFactory.decodeStream(in, null, options);
	}

	/**
	 * Calculate an inSampleSize for use in a {@link BitmapFactory.Options}
	 * object when decoding bitmaps using the decode* methods from
	 * {@link BitmapFactory}. This implementation calculates the closest
	 * inSampleSize that will result in the final decoded bitmap having a width
	 * and height equal to or larger than the requested width and height. This
	 * implementation does not ensure a power of 2 is returned for inSampleSize
	 * which can be faster when decoding but results in a larger bitmap which
	 * isn't as useful for caching purposes.
	 * 
	 * @param options
	 *            An options object with out* params already populated (run
	 *            through a decode* method with inJustDecodeBounds==true
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		//先根据宽度进行缩小
		while (width / inSampleSize > reqWidth) {
			inSampleSize++;
		}
		//然后根据高度进行缩小
		while (height / inSampleSize > reqHeight) {
			inSampleSize++;
		}
		return inSampleSize;
	}
    
    
	public static Bitmap decodeZoomBitmap(InputStream is, int reqWidth, int reqHeight) {
        Bitmap oldBitmap = BitmapFactory.decodeStream(is);
        
        int oldWidth = oldBitmap.getWidth();
        int oldHeight = oldBitmap.getHeight();
        
        //计算缩放比
        float scaleWidth = (float)reqWidth / oldWidth;
        float scaleHeight = (float)reqHeight / oldHeight;
        
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        
        return Bitmap.createBitmap(oldBitmap, 0, 0, oldWidth, oldHeight, matrix, true);
	}

}
