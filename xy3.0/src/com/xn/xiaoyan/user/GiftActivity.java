package com.xn.xiaoyan.user;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.config.Config;
import com.boyiqove.ui.bookstore.BookstoreMain;
import com.boyiqove.ui.bookstore.LocalSearchActivity;
import com.boyiqove.ui.bookstore.StoreMain;

public class GiftActivity extends FragmentActivity{
	
	private LinearLayout gift_ll;
	private BookstoreMain bookstoreMain;
	private ImageView search_back,search;
	private RelativeLayout boyi_book;
	private TextView search_top_title_tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boyi_gift_webview);
		initView();
		initData();
		
		
	}
	private void initData() {
		top=(TextView) findViewById(R.id.top);
		boyi_book=(RelativeLayout) findViewById(R.id.boyi_book);
		search_top_title_tv.setText(R.string.boyi_bookshelf_gift);
		search.setVisibility(View.GONE);
		boyi_book.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//导航到书架和书城

				if (popupWindow != null&&popupWindow.isShowing()) {
					popupWindow.dismiss();
					return;
				} else {
					showMenuPopupWindow();
				}
			
			}
		});
		bookstoreMain=new BookstoreMain(AppData.getUrl(AppData.getConfig().getUrl(Config.URL_USERCENTER_FREEREAD)));
		getSupportFragmentManager().beginTransaction().replace(R.id.gift_ll,bookstoreMain).commit();
	}
	private void initView() {
		search_top_title_tv=(TextView) findViewById(R.id.search_top_title_tv);
		search_back=(ImageView) findViewById(R.id.search_back);
		search_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		search=(ImageView) findViewById(R.id.search);
		boyi_book=(RelativeLayout) findViewById(R.id.boyi_book);
	}
	private PopupWindow popupWindow;
	protected void showMenuPopupWindow() {
		if(popupWindow==null)
		{
			View v=getPopupWinodwView();
			popupWindow=new PopupWindow(v,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			popupWindow.setFocusable(true);
			popupWindow.setTouchable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
		}
		popupWindow.showAsDropDown(top);
	}
	private View view;
	private LinearLayout enterLayout,bookManager,bookStore;
	private RelativeLayout usercenter_rl;
	private TextView top;
	private View getPopupWinodwView() {
		// TODO Auto-generated method stub
		if(view==null)
		{
		  view=LayoutInflater.from(this).inflate(R.layout.bookshelf_menu2,null);
		  usercenter_rl=(RelativeLayout) view.findViewById(R.id.usercenter_rl);
		  enterLayout=(LinearLayout) view.findViewById(R.id.enter_bookshelf);
		  bookManager=(LinearLayout) view.findViewById(R.id.enter_user_center);
		  bookStore=(LinearLayout) view.findViewById(R.id.enter_bookstore);
		  usercenter_rl.setVisibility(View.GONE);
		  enterLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入书架
				AppData.goToShelf(GiftActivity.this,false);
				finish();
				
			}
		});
		  view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow!= null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}

				return true;
			}
		});
		  bookStore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入书城
				Intent intent=new Intent(GiftActivity.this,StoreMain.class);
				startActivity(intent);
			}
		});
		}
		return view;
	}

}
