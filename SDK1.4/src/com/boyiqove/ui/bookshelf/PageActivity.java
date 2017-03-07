package com.boyiqove.ui.bookshelf;

import java.awt.font.TextAttribute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boyiqove.R;
import com.boyiqove.ui.bookstore.BookstoreMain;
import com.boyiqove.view.BaseActivity;
import com.boyiqove.view.MyWebView;

public class PageActivity extends FragmentActivity{
	private ImageView search_back,search;
	private RelativeLayout boyi_book;
	private TextView search_top_title_tv;
	private BookstoreMain bookstoreMain;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boy_notice);
		initView();
	}
	private void initView() {
		Intent intent=getIntent();
		search_top_title_tv=(TextView) findViewById(R.id.search_top_title_tv);
		search=(ImageView) findViewById(R.id.search);
		search.setVisibility(View.GONE);
		search_back=(ImageView) findViewById(R.id.search_back);
		search_top_title_tv.setText(intent.getStringExtra("title"));
		search_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		boyi_book=(RelativeLayout) findViewById(R.id.boyi_book);
		boyi_book.setVisibility(View.GONE);
		bookstoreMain=new BookstoreMain(intent.getStringExtra("url"));
		getSupportFragmentManager().beginTransaction().replace(R.id.notice_mywebview,bookstoreMain).commit();
	}

}
