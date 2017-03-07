package com.xn.xiaoyan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.boyiqove.AppData;
import com.boyiqove.util.DebugLog;
import com.umeng.analytics.MobclickAgent;
import com.xn.xiaoyan.view.CirclePageIndicator;

public class SpashActivity2 extends Activity implements OnTouchListener{
	private ViewPager viewpager;
	private CirclePageIndicator circlePageIndicator;
	private List<View> list;
	private MyPageAdapter adapter;
	private SharedPreferences sp;
	private boolean isFirstEnter=false;
	private int currentIndex=0;
	private int flaggingWidth;
	private int lastX = 0;
	private boolean locker = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		sp=getSharedPreferences("config",Context.MODE_PRIVATE);
		isFirstEnter=sp.getBoolean("isFirstEnter",false);
		if(!isFirstEnter)
		{
		  setContentView(R.layout.spash2);
		  initView();
		}else
		{
			startActivity2();
		}
	}

	protected void startActivity() {
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.boyi_move_right_in, R.anim.boyi_move_right_out);
		finish();
	}
	protected void startActivity2() {
		Intent intent = new Intent(this,SpashActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.boyi_move_right_in, R.anim.boyi_move_right_out);
		finish();
	}

	private void initView() {
		
		isFirstEnter=true;
		Editor editor=sp.edit();
		editor.putBoolean("isFirstEnter",isFirstEnter);
		editor.commit();
		MobclickAgent.setSessionContinueMillis(30000);
		viewpager=(ViewPager) findViewById(R.id.pager_splash_ad);
		circlePageIndicator=(CirclePageIndicator) findViewById(R.id.viewflowindic);
		list=new ArrayList<View>();
		initData();
		

	}

	private void initData() {
		
		View view=LayoutInflater.from(this).inflate(R.layout.xy_viewpager_item,null);
		list.add(view);
		
		View view1=LayoutInflater.from(this).inflate(R.layout.xy_viewpager_item,null);
		ImageView imageView1=(ImageView) view1.findViewById(R.id.phone);
		ImageView imageViewText1=(ImageView) view1.findViewById(R.id.imageView_text);
		view1.setBackgroundColor(Color.parseColor("#6cb3e8"));
		imageView1.setBackgroundResource(R.drawable.xy_usercenter_phone);
		imageViewText1.setBackgroundResource(R.drawable.xy_usercenter);
		list.add(view1);
		
		View view2=LayoutInflater.from(this).inflate(R.layout.xy_viewpager_item,null);
		ImageView imageView2=(ImageView) view2.findViewById(R.id.phone);
		ImageView imageViewText2=(ImageView) view2.findViewById(R.id.imageView_text);
		view2.setBackgroundColor(Color.parseColor("#fea062"));
		imageView2.setBackgroundResource(R.drawable.xy_book_dingdan_phone);
		imageViewText2.setBackgroundResource(R.drawable.xy_book_dingdan);
		list.add(view2);
		
		View view3=LayoutInflater.from(this).inflate(R.layout.xy_viewpager_item,null);
		ImageView imageView3=(ImageView) view3.findViewById(R.id.phone);
		ImageView imageViewText3=(ImageView) view3.findViewById(R.id.imageView_text);
		ImageView imageViewButton=(ImageView)view3.findViewById(R.id.start_look_iv);
		imageViewButton.setVisibility(View.VISIBLE);
		imageViewButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity();
			}
		});
		view3.setBackgroundColor(Color.parseColor("#fd8585"));
		imageView3.setBackgroundResource(R.drawable.xy_reading_phone);
		imageViewText3.setBackgroundResource(R.drawable.xy_reading);
		list.add(view3);
		adapter=new MyPageAdapter(this,list);
		viewpager.setAdapter(adapter);
		viewpager.setOnTouchListener(this);
		circlePageIndicator.setmListener(new MypageChangeListener());
		circlePageIndicator.setViewPager(viewpager);
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	class MyPageAdapter extends PagerAdapter{
		private Context context;
		private List<View> listAdapter;
		public MyPageAdapter(Context context,List<View> listview) {
			this.context=context;
			this.listAdapter=listview;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return  listAdapter == null ? 0 : listAdapter.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(listAdapter.get(position));
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(listAdapter.get(position), 0);
			return listAdapter.get(position);
		}
	}
	private EdgeEffectCompat leftEdge ;

	private EdgeEffectCompat rightEdge ;
	private class MypageChangeListener implements OnPageChangeListener {
		public MypageChangeListener() {  
            try {  
                Field leftEdgeField = viewpager.getClass().getDeclaredField("mLeftEdge");  
                Field rightEdgeField = viewpager.getClass().getDeclaredField("mRightEdge");  
                if (leftEdgeField != null && rightEdgeField != null) {  
                    leftEdgeField.setAccessible(true);  
                    rightEdgeField.setAccessible(true);  
                    leftEdge = (EdgeEffectCompat) leftEdgeField.get(viewpager);  
                    rightEdge = (EdgeEffectCompat) rightEdgeField.get(viewpager);  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  

		@Override
		public void onPageScrollStateChanged(int position) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

			 if ( leftEdge != null && rightEdge != null ){

	             leftEdge .finish();

	             rightEdge .finish();

	             leftEdge .setSize(0, 0);

	             rightEdge .setSize(0, 0);

	        }
		}

		@Override
		public void onPageSelected(int arg0) {
			currentIndex = arg0;
			DebugLog.e("欢迎页",arg0+"");
		}

	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int)event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if((lastX - event.getX()) > 0 && (currentIndex == list.size() -1) && locker){
				locker = false;
				startActivity();
			}
			
			break;
		default:
			break;
		}
		return false;
	}
}
