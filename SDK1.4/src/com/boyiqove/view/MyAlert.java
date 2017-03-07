package com.boyiqove.view;

import com.boyiqove.R;
import com.boyiqove.entity.PageID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyAlert {

    // 提示重新登录
    public static AlertDialog showPormptLogin(final Context context) {
        
    	return new AlertDialog.Builder(context).setTitle("阅读提醒")
    	.setMessage("此账号已在其他设备登录，请重新登录!")
    	.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
                if(context instanceof Activity) {
//                	Intent intent = new Intent(context, LoginActivity.class);
//                    ((Activity)context).startActivityForResult(intent, PageID.User);
                }
			}
		})
		.setNegativeButton("取消", null)
		.show();
    }
    
    public static interface DialogOnClickListener{
    	public void doPositive();
    	public void doNegative();
    }
    
    public static AlertDialog showCustomDialog(final Context context,String title,String message,DialogOnClickListener listener) {
        
    	return new AlertDialog.Builder(context).setTitle("阅读提醒")
    	.setMessage("此账号已在其他设备登录，请重新登录!")
    	.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
                if(context instanceof Activity) {
//                	Intent intent = new Intent(context, LoginActivity.class);
//                    ((Activity)context).startActivityForResult(intent, PageID.User);
                }
				
				
			}
		})
		.setNegativeButton("取消", null)
		.show();
    }
//    public static AlertDialog showLoginDialog(final Context context,String title,String message,final DialogOnClickListener listener) {
//    	
//    	return new AlertDialog.Builder(context).setTitle(title)
//    			.setMessage(message)
//    			.setPositiveButton("重试", 
//    					new DialogInterface.OnClickListener() {
//    				
//    				@Override
//    				public void onClick(DialogInterface dialog, int which) {
//    					// TODO Auto-generated method stub
//    					listener.doPositive();
//    				}
//    			})
//    			.setNegativeButton("取消", null)
//    			.show();
//    }
   
	public static AlertDialog showLoginDialog(final Context context,String title,String message,final DialogOnClickListener listener) {
		LayoutInflater inflaterDl = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout)inflaterDl.inflate(R.layout.boy_loginagain_dialog, null );
        
        final AlertDialog alertDialog=new AlertDialog.Builder(context).create();
        alertDialog.show();
        alertDialog.getWindow().setContentView(layout);
		TextView tView= (TextView) layout.findViewById(R.id.text_msg);
//		if(alertDialog!=null && alertDialog.isShowing())
//		{
		tView.setText(message);
		Button button_positive=(Button) layout.findViewById(R.id.positive);
		Button button_negative=(Button) layout.findViewById(R.id.negative);
		button_positive.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.doPositive();
				alertDialog.dismiss();
			}
		});
		button_negative.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				  
				
			}
		});
//		}
		
		return alertDialog;
	}
}
