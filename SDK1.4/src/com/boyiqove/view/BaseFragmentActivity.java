package com.boyiqove.view;

import com.boyiqove.AppData;
import com.boyiqove.library.volley.RequestQueue;
import com.boyiqove.library.volley.toolbox.Volley;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseFragmentActivity extends FragmentActivity {

	protected RequestQueue mRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mRequestQueue = AppData.getRequestQueue();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		mRequestQueue.cancelAll(this);
	}



	public RequestQueue getRequestQueue() {
		// TODO Auto-generated method stub
		return mRequestQueue;
	}

}
