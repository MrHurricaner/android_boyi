package com.boyiqove.ui.storeadapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class listerScrollView extends ScrollView {
	
		  
		  
		private ScrollBottomListener  scrollBottomListener;  
		  
		public listerScrollView(Context context) {  
		super(context);  
		}  
		  
		public listerScrollView(Context context, AttributeSet attrs) {  
		super(context, attrs);  
		}  
		  
		public listerScrollView(Context context, AttributeSet attrs,int defStyle) {  
		super(context, attrs, defStyle);  
		}  
		  
		@Override  
		protected void onScrollChanged(int l, int t, int oldl, int oldt){  
		if(t + getHeight() >=  computeVerticalScrollRange()){  
		//ScrollView滑动到底部了  
		scrollBottomListener.scrollBottom();  
		}  
		}  
		  
		public void setScrollBottomListener(ScrollBottomListener scrollBottomListener){  
		this.scrollBottomListener = scrollBottomListener;  
		}  
		  
		public interface ScrollBottomListener{  
		public void scrollBottom();  
		
		}  
		  
}
