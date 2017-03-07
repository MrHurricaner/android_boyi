package com.boyiqove.ui.bookshelf;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.task.CallBackMsg;
import com.boyiqove.util.DebugLog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * 弹窗辅助类
 *
 * @ClassName WindowUtils
 *
 *
 */
public class WindowManagerLogin {

    private static final String LOG_TAG = "WindowUtils";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    private static Boolean isLogin=true;
    private static int countNum=0;
    private static int  mCount=1;
    public static Boolean isShown = false;
    public static ProgressBar mProgress=null ;
    public static Handler handler=new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case CallBackMsg.READ_ENDPAGE_PROGRESS:
				mProgress.setProgress(++mCount);
				DebugLog.e("更新进度", "到了"+mCount);
//				mProgress.setProgress(msg.arg1);
				break;
			default:
				break;
			}
    	};
    };
    /**
     * 显示弹出框
     *
     * @param context
     * @param view
     */
    public static void showPopupWindow(final Context context,Boolean comeLogin,int posNum) {
    	isLogin=comeLogin;
    	countNum=posNum;
        if (isShown) {
            return;
        }
        AppData.getClient().setCallBackHander(handler);
        isShown = true;

        // 获取应用的Context
        mContext = context.getApplicationContext();
        // 获取WindowManager
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);

        mView = setUpView(context);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        // 类型
        params.type = WindowManager.LayoutParams.TYPE_PHONE;

        // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

        // 设置flag

        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        		WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
//        		LayoutParams.FLAG_NOT_FOCUSABLE;
        // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
        // 不设置这个flag的话，home页的划屏会有问题

        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.TOP | Gravity.LEFT;

        mWindowManager.addView(mView, params);


    }

    /**
     * 隐藏弹出框
     */
    public static void hidePopupWindow() {
        if (isShown && null != mView) {
            mWindowManager.removeView(mView);
            isShown = false;
        }

    }

    private static View setUpView(final Context context) {
    	View view;
    	if (isLogin) {
         view = LayoutInflater.from(context).inflate(R.layout.boy_float_frame,
                null);
        view.setFocusable(false);
        ImageView imageView=(ImageView) view.findViewById(R.id.boy_img_login);
        Animation operatingAnim = AnimationUtils.loadAnimation(mContext,
				R.anim.tip);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		imageView.startAnimation(operatingAnim); 

        ImageView negativeBtn = (ImageView) view.findViewById(R.id.by_dialog_colse);
        negativeBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                WindowManagerLogin.hidePopupWindow();

            }
        });
        // 点击back键可消除
        view.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                	WindowManagerLogin.hidePopupWindow();
                    return true;
                default:
                    return false;
                }
            }
        });

    	}else {
    		view = LayoutInflater.from(context).inflate(R.layout.boy_float_frame_download,
                    null);
    		mCount = 1;
    		mProgress = (ProgressBar) view.findViewById(R.id.progress_download);
    		mProgress.setMax(countNum-1);
    		mProgress.setProgress(0);
    		mProgress.setIndeterminate(false);
    		
		}
    	return view;
    }
}