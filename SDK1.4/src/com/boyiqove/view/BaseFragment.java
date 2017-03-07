package com.boyiqove.view;

import java.util.zip.Inflater;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.library.volley.RequestQueue;
import com.boyiqove.library.volley.toolbox.ImageLoader;
import com.boyiqove.library.volley.toolbox.ImageLoader.ImageCache;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {

	private int mCacheCount;
	private static final String TAG = "BaseFragment";
	private static final int ACTION_BAR_ID_MENU = 0;
	private static final int ACTION_BAR_ID_SHOP = 1;
	private LayoutInflater mInflater;
	// @Override
	// public ActionBarInfo initActionBarInfo() {
	// // 初始化 Actionbar，无需修改即可
	// ActionBarInfo info = new ActionBarInfo();        
	// info.displayMode = ActionBar.DISPLAY_MODE_NORMAL;
	// info.centerText = getResString(R.string.boyi_by_shelf_title);
	// info.leftItem = new ActionBarItem(ACTION_BAR_ID_MENU,
	// ActionBar.ACTION_ITEM_TYPE_LEFT_MENU);
	//
	// info.rightItem = new ActionBarItem(ACTION_BAR_ID_SHOP,
	// ActionBar.ACTION_ITEM_TYPE_RIGHT_NORMAL);
	// info.rightItem.nomralText = getResString(R.string.boyi_by_shop_title);
	// Context context = getApplicationContext();
	// if (context != null) {
	// info.rightItem.leftCompoundDrawable =
	// context.getResources().getDrawable(R.drawable.boyi_by_ic_book);
	// }
	// return info;
	// }
	//
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		this.mInflater=inflater;
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	protected RequestQueue getRequestQueue() {
		// FragmentActivity parent = this.getActivity();
		// if(parent instanceof BaseFragmentActivity) {
		// RequestQueue queue =
		// ((BaseFragmentActivity)parent).getRequestQueue();
		// return queue;
		// }
		return AppData.getRequestQueue();
		// throw new RuntimeException("not found RequestQueue");
	}

	protected void initImageCacheCount(int count) {
		if (null != mImageLoader) {
			throw new RuntimeException(
					"BaseFragment: bitmap cache count must set before getImageLoader");
		}

		mCacheCount = count;
	}

	private ImageLoader mImageLoader = null;

	protected ImageLoader getImageLoader() {
		if (mCacheCount <= 0) {
			throw new RuntimeException("Bitmap cache count <= 0");
		}

		final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
				mCacheCount);
		ImageCache imageCache = new ImageCache() {

			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				// TODO Auto-generated method stub
				lruCache.put(url, bitmap);
			}

			@Override
			public Bitmap getBitmap(String url) {
				// TODO Auto-generated method stub
				return lruCache.get(url);
			}
		};

		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(getRequestQueue(), imageCache);
		}

		return mImageLoader;
	}

	private Toast mToast;
	private ProgressDialog mProgressDialog;

	public void showToast(String text, int duration) {
		if (null == mToast) {
			mToast = Toast.makeText(this.getActivity(), text, duration);
		} else {
			mToast.setText(text);
			mToast.setDuration(duration);
		}

		mToast.show();
	}

	public void cancelToast() {
		if (null != mToast) {
			mToast.cancel();
		}
	}

	public void showProgress(String title, String message) {
		// if(null == mProgressDialog) {
		// mProgressDialog = ProgressDialog.show(this.getActivity(), title,
		// message);
		// } else {
		// if(!mProgressDialog.isShowing()) {
		// mProgressDialog.show();
		// }
		// }

		showProgressCancel("", "", message);
	}
	
	public void showProgressCancel(final String taskName, String title,
			String message) {
		LayoutInflater inflaterDl = LayoutInflater.from(getActivity());
		RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(
				R.layout.boy_zdy_dialog, null);
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(params);
		ImageView imageView = (ImageView) layout.findViewById(R.id.boy_img);
		Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.tip);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		imageView.startAnimation(operatingAnim);

		TextView tView = (TextView) layout.findViewById(R.id.by_dialog_text);
		tView.setText(message);
		if (null == mProgressDialog) {
			mProgressDialog = ProgressDialog
					.show(getActivity(), title, message);
			mProgressDialog.getWindow().setContentView(layout);
		} else {
			mProgressDialog.setTitle(title);
			mProgressDialog.setMessage(message);
			if (!mProgressDialog.isShowing()) {
				mProgressDialog.show();
				mProgressDialog.getWindow().setContentView(layout);
			}
		}
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		if (taskName != null || taskName != "") {

			mProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					// DebugLog.e("", "线程--"+taskName+"--需要关闭");
					AppData.getClient().getTaskManagerRead().delTask(taskName);

				}
			});
		}
	}

	public void hideProgress() {
		if (null != mProgressDialog && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	public void mCancelTask(String hideTask) {

		AppData.getClient().getTaskManager().delTask(hideTask);

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		cancelToast();
		hideProgress();
	}

	public void handleMessage(Message msg) {

	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		// super.startActivityForResult(intent, requestCode);
		getActivity().startActivityForResult(intent, requestCode);
	}
	
	

}
