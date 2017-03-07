package com.boyiqove.view;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.library.volley.RequestQueue;
import com.boyiqove.library.volley.toolbox.ImageLoader;
import com.boyiqove.library.volley.toolbox.ImageLoader.ImageCache;
import com.boyiqove.ui.bookqove.ImageCacheManager;
import com.boyiqove.ui.bookshelf.OnlineReadingActivity;
import com.boyiqove.util.DebugLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	
	private int mCacheCount = 0;
//	private String  hideTask;
	protected RequestQueue getRequestQueue() {
		return AppData.getRequestQueue();
	}
	
	protected void initImageCacheCount(int count) {
		if(null != mImageLoader){
			throw new RuntimeException("BaseFragment: bitmap cache count must set before getImageLoader");
		}
		
		mCacheCount = count;
	}
	
	private ImageLoader mImageLoader = null;
	protected ImageLoader getImageLoader() {
//		if(mCacheCount <= 0) {
//			throw new RuntimeException("Bitmap cache count <= 0");
//		}
//		
//		
//		final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(mCacheCount);
//		
//		ImageCache imageCache = new ImageCache() {
//			
//			@Override
//			public void putBitmap(String url, Bitmap bitmap) {
//				// TODO Auto-generated method stub
//				lruCache.put(url, bitmap);
//			}
//			
//			@Override
//			public Bitmap getBitmap(String url) {
//				// TODO Auto-generated method stub
//				return lruCache.get(url);
//			}
//		};
		ImageCache	imageCache=ImageCacheManager.getInstance(this);
		if(mImageLoader == null) {
			mImageLoader = new ImageLoader(getRequestQueue(), imageCache);
		}
		
		return mImageLoader;
	}
	
	private Toast 			mToast;
	public ProgressDialog 	mProgressDialog;
	public ProgressDialog dialog;
	public CustomProgressDialog 	progressDialog;
	
	
	
	public void showToast(String text, int duration) {
		if(null == mToast) {
			mToast = Toast.makeText(this, text, duration);
		} else {
			mToast.setText(text);
			mToast.setDuration(duration);
		}
		
		mToast.show();
	}
	
	public void cancelToast() {
		if(null != mToast) {
			mToast.cancel();
		}
	}

	public void showProgress(String title, String message) {
//		if(null == mProgressDialog) {
//			//mProgressDialog = ProgressDialog.show(this, title, message);
//			mProgressDialog = ProgressDialog.show(this, title, message);
//		} else {
//			mProgressDialog.setTitle(title);
//			mProgressDialog.setMessage(message);
//			if(!mProgressDialog.isShowing()) {
//				mProgressDialog.show();
//			}
//		}
//		mProgressDialog.setCanceledOnTouchOutside(false);
		showProgressCancel("", "",message);
//		mProgressDialog.setCancelable(true);
	}	
	public void showProgressCancel(final String taskName,String title, String message) {
		 	LayoutInflater inflaterDl = LayoutInflater.from(this);
	        RelativeLayout layout = (RelativeLayout)inflaterDl.inflate(R.layout.boy_zdy_dialog, null );
	        ImageView imageView=(ImageView) layout.findViewById(R.id.boy_img);
	        Animation operatingAnim = AnimationUtils.loadAnimation(this,
					R.anim.tip);
			LinearInterpolator lin = new LinearInterpolator();
			operatingAnim.setInterpolator(lin);
			imageView.startAnimation(operatingAnim); 
			
			TextView tView= (TextView) layout.findViewById(R.id.by_dialog_text);
			tView.setText(message);
		if(null == mProgressDialog) {
			mProgressDialog = ProgressDialog.show(this, title, message);
			mProgressDialog.getWindow().setContentView(layout);
		} else {
			mProgressDialog.setTitle(title);
			mProgressDialog.setMessage(message);
			if(!mProgressDialog.isShowing()) {
				mProgressDialog.show();
				mProgressDialog.getWindow().setContentView(layout);
			}
		}	
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		if (taskName!=null || taskName !="") {
			
			mProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
//					DebugLog.e("", "线程--"+taskName+"--需要关闭");
					AppData.getClient().getTaskManagerRead().delTask(taskName);
					
				}
			});
		}
	}
	public void mCancelTask(String hideTask ){
		
		AppData.getClient().getTaskManager().delTask(hideTask);
		
	}
	public void hideProgress() {
		if(null != mProgressDialog && mProgressDialog.isShowing()){
//			AppData.getClient().getTaskManager().delTask(hideTask);
			mProgressDialog.dismiss();
			
		}
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		if (AppData.initSdk==0) {
//			AppData.createAccount();
//			AppData.getMapTable();
//			AppData.initSdk=1;
//		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		cancelToast();
		hideProgress();
	}

	public interface HideTask {
		public void hideMyTask();
	}
	
	public static ProgressDialog createLoadingDialog(Context context, String msg) {  
		  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.boy_djs_dialog, null);// 得到加载view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        // main.xml中的ImageView  
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.boy_dialog_iv);  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        // 加载动画  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.boyi_dirlog_jiazai_in);  
        // 使用ImageView显示动画  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        tipTextView.setText(msg);// 设置加载信息  
  
        ProgressDialog loadingDialog = new ProgressDialog(context, R.style.loading_dialog);// 创建自定义样式dialog  
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消  
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.FILL_PARENT,  
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局  
        return loadingDialog;  
  
    } 
	
	public class CustomProgressDialog extends Dialog {  
	    public CustomProgressDialog(Context context, String strMessage) {  
	        this(context, R.style.loading_dialog, strMessage);  
	    }  
	  
	    public CustomProgressDialog(Context context, int theme, String strMessage) {  
	        super(context, theme);  
	        this.setContentView(R.layout.boy_djs_dialog);  
	        this.getWindow().getAttributes().gravity = Gravity.CENTER;  
	        TextView tvMsg = (TextView) this.findViewById(R.id.tipTextView);  
	        if (tvMsg != null) {  
	            tvMsg.setText(strMessage);  
	        }  
	    }  
	  
	    @Override  
	    public void onWindowFocusChanged(boolean hasFocus) {  
	  
	        if (!hasFocus) {  
	            dismiss();  
	        }  
	    }  
	}  

}
