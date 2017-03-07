package com.boyiqove.ui.bookshelf;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.config.Config;
import com.boyiqove.ui.bookstore.BookstoreMain;

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
		search_top_title_tv.setText(R.string.boyi_bookshelf_gift);
		boyi_book.setVisibility(View.GONE);
		search.setVisibility(View.GONE);
		bookstoreMain=new BookstoreMain(AppData.getConfig().getUrl(Config.URL_USERCENTER_FREEREAD));
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

}
