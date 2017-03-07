package com.boyiqove.view;

import com.boyiqove.util.DebugLog;
import com.boyiqove.R;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MyWebView extends FrameLayout {
    
	private View 		mErrorView;
    private WebView		mWebView;
    
    private ImageView 	mProgressbar;
    
    private String 		mFailingUrl;
	private boolean 	mIsError = false;

	public MyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
        init(context);
	}

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
        init(context);
	}

	public MyWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
        init(context);
	}
    
	private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.boyi_webview_layout, this, true);
        mErrorView = v.findViewById(R.id.web_retry_layout);
        mWebView = (WebView)v.findViewById(R.id.webview);
        mProgressbar = (ImageView) v.findViewById(R.id.web_progressbar);
        
        Button btnRetry = (Button)v.findViewById(R.id.web_retry_btn);
        
        btnRetry.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                if(mFailingUrl != null) {
                	mWebView.loadUrl(mFailingUrl);
                    
                	mIsError = false;
                }
				
			}
		});
        
        if(Build.VERSION.SDK_INT >= 19) {
        	mWebView.getSettings().setLoadsImagesAutomatically(true);
        } else {
        	mWebView.getSettings().setLoadsImagesAutomatically(false);
        }
        
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
	}
    
	public WebView getWebView() {
		return mWebView;
	}

    
	private class MyWebViewClient extends WebViewClient
	{
        
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			view.loadUrl(url);
			return true;
		}
        
		

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
            
			if(!mWebView.getSettings().getLoadsImagesAutomatically()) {
				mWebView.getSettings().setLoadsImagesAutomatically(true);
			}

			if(!mIsError && mErrorView.getVisibility() == View.VISIBLE) {
				mErrorView.setVisibility(View.GONE);
			}
          
            DebugLog.d("webView", "onPageFinished()");
          
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
            
            mFailingUrl = failingUrl;
			mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
			mErrorView.setVisibility(View.VISIBLE);
				mIsError = true;
            
            DebugLog.d("webView", "onReceivedError()");
		}
        
		
	}
    
	
	private class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
            	mProgressbar.setVisibility(GONE);
            	mProgressbar.clearAnimation();
            } else {
                if (mProgressbar.getVisibility() == GONE)
                	mProgressbar.setVisibility(VISIBLE);
                roateImageView();
                //mProgressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
        

    }
	private void roateImageView() {
		Animation operatingAnim = AnimationUtils.loadAnimation(getContext(),
				R.anim.tip);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		mProgressbar.startAnimation(operatingAnim);
	}
    
}
