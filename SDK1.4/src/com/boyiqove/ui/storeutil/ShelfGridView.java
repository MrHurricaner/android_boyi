package com.boyiqove.ui.storeutil;

import com.boyiqove.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;


	public class ShelfGridView extends GridView{  
		  
	    private Bitmap background;  
	    private Bitmap background2;
	    private int spacePx,topPx;
	    private int mWidth;
	    private Context mContext;
	    //下拉刷新
	    
	    public ShelfGridView(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	        this.mContext=context;
	        background2 = BitmapFactory.decodeResource(getResources(),  
	                R.drawable.boyi_bookshelf_layer_center);     
//	        WindowManager manager=((Object) context).getWindowManager();
	        
//			mWidth=(getWidth()-dip2px(context, (45+45+50)))/3*4/3;
//	        spacePx=mWidth+dip2px(context, 46); // 每隔多少间距画一个
//	        topPx= mWidth+dip2px(context, 30);  // 画挡板的起点
	    } 
	    
	  
	    public static Bitmap zoomImg(Bitmap bm, int newWidth){   
		    // 获得图片的宽高   
		    int width = bm.getWidth();   
		    int height = bm.getHeight();   
		    // 计算缩放比例   
		    float scaleWidth = ((float) newWidth) / width;   
//		    float scaleHeight = ((float) newHeight) / height;   
		    // 取得想要缩放的matrix参数   
		    Matrix matrix = new Matrix();   
		    matrix.postScale(scaleWidth, (float) 1.0);   
		    // 得到新的图片  
		    Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);   
		    return newbm;   
		}
	    
	    @Override  
	    protected void dispatchDraw(Canvas canvas) {  
	    	
	    	int backgroundWidth = getWidth();  
	    	if(background == null){
	    		background=zoomImg(background2, backgroundWidth);
	    	}
	        int count = getChildCount();  
	        int top = count > 0 ? getChildAt(0).getTop() : 0;  
//	        int backgroundWidth = background.getWidth();  
	        
//	        int backgroundHeight = background.getHeight()*5/3+79+60;  
	        int childHeight=getChildAt(1).getHeight();
//	    	DebugLog.e("孩子的高度为：", childHeight+"");
//	    	mWidth=(getWidth()-dip2px(mContext, (45+45+50)))/3*4/3;
	        spacePx=childHeight+dip2px(mContext, 46); // 每隔多少间距画一个
	        topPx= childHeight+dip2px(mContext, 30);  // 画挡板的起点
	    	
	        int width = getWidth();  
	        int height = getHeight();  
	  
	        for (int y = top+topPx-(background2.getHeight()*5/6); y < height; y += spacePx) {  
	        	
	            for (int x = 0; x < width; x += backgroundWidth) {  
	                canvas.drawBitmap(background, x, y, null);  
	            }  
	        }  
	  
	        super.dispatchDraw(canvas);  
	    }  
	  
	    public static int dip2px(Context context, float dpValue) {
	    	final float scale = context.getResources().getDisplayMetrics().density;
	    	return (int) (dpValue * scale + 0.5f);
	    }
	   
       

		
        
		
	    
	}  

