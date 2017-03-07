package com.boyiqove.library.volley.toolbox;

import android.widget.ImageView;

import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.toolbox.ImageLoader.ImageContainer;
import com.boyiqove.library.volley.toolbox.ImageLoader.ImageListener;

public class ListImageListener implements ImageListener{
	
	private ImageView 	mImageView;
	private int 		mDefaultImageResId;
	private int 		mErrorImageResId;
	private String 		mTag;
	
	public ListImageListener(ImageView imageView, int defaultImageResId, int errorImageResId, String tag) {
		mImageView = imageView;
		mDefaultImageResId = defaultImageResId;
		mErrorImageResId = errorImageResId;
		mTag = tag;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		if(mErrorImageResId != 0 && mImageView.getTag() != null && mTag.equals(mImageView.getTag())) {
			mImageView.setImageResource(mErrorImageResId);
		}
	}

	@Override
	public void onResponse(ImageContainer response, boolean isImmediate) {
		// TODO Auto-generated method stub
		if( mImageView.getTag() != null && mTag.equals(mImageView.getTag())) {
			if (response.getBitmap() != null ) {
				mImageView.setImageBitmap(response.getBitmap());
			} else if (mDefaultImageResId != 0 ) {
				mImageView.setImageResource(mDefaultImageResId);
			}
		}
	}

}
