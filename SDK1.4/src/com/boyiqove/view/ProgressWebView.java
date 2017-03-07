package com.boyiqove.view;

import com.boyiqove.util.DisplayUtil;
import com.boyiqove.R;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * 带进度条的WebView
 */
public class ProgressWebView extends WebView {

    private ProgressBar 	mProgressbar;

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mProgressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        int height = DisplayUtil.dip2px(context, 3);
        mProgressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height, 0, 0));
        mProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.boyi_progress_web));
        addView(mProgressbar);
        
        
        setWebChromeClient(new WebChromeClient());
    }
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        LayoutParams lp = (LayoutParams) mProgressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        mProgressbar.setLayoutParams(lp);
    }
    
    
    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
            	mProgressbar.setVisibility(GONE);
            } else {
                if (mProgressbar.getVisibility() == GONE)
                	mProgressbar.setVisibility(VISIBLE);
                
                mProgressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
        

    }

    
}

