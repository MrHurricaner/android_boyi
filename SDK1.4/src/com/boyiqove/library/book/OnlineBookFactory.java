package com.boyiqove.library.book;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.boyiqove.library.book.BookView.PageIndex;



import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

public class OnlineBookFactory extends BookFactory{
	//private final static String TAG = "BookDraw";
	
	public interface OnDrawListener {
		public String getChapterName();
        public String getBookName();
	}
	
	private OnDrawListener onDrawListener;

	public OnlineBookFactory(BookCacheManager cacheManager) {
		super(cacheManager);
		// TODO Auto-generated constructor stub
	}
	
	public void setOnDrawListener(OnDrawListener listener) {
		onDrawListener = listener;
	}
    
//	@Override
//	public void draw(Canvas canvas) {
//		// TODO Auto-generated method stub
//        BookCache cache = mCacheManager.getCache(PageIndex.current);
//        if(cache.getCurPage() == 0) {
//            
//        } else {
//        	
//        	super.draw(canvas);
//        }
//        
//	}

	@Override
	protected void drawHead(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint paint = config.getExtraPaint();
		
		int width = config.getWidth();
		//int height = config.getHeight();
		
		float marginWidth = config.getMarginWidth();
		//float marginHeight = config.getMarginHeight();
        float textHeight = config.getTextHeight(paint);
		
		float batteryWidth = config.getBatteryWidth();
		float batteryHeight = config.getBatteryHeight();
        
		// 1.时间
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
		String strTime = sdf.format(new Date());
		float nTimeWidth = paint.measureText(strTime);
//		canvas.drawText(strTime, width - nTimeWidth - 5, textHeight, paint);
		canvas.drawText(strTime, width - nTimeWidth - marginWidth, textHeight, paint);
		
        
		// 2. 电量
		int batteryTailWidth = 3;
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		
		float marginTime = 10;
        float left = width - nTimeWidth - marginWidth - marginTime - batteryWidth - batteryTailWidth;
        float top = textHeight - batteryHeight;
		
		canvas.drawRect(left, top, left + batteryWidth, top + batteryHeight, paint);
		canvas.drawRect(left + batteryWidth, top + batteryHeight/4, left + batteryWidth + batteryTailWidth, top + batteryHeight/4*3, paint);
		paint.setStyle(Paint.Style.FILL);
		int gap = 2;
        
		canvas.drawRect(left + gap, top + gap, left + gap + (batteryWidth - 2*gap) * config.getBatterPercent() , top + batteryHeight - gap, paint);

		// 书名
		if(onDrawListener != null) {
			String bookName = onDrawListener.getBookName();
            int size = paint.breakText(bookName, true, width - 2*(width - left), null);
            if(size < bookName.length()) {
            	bookName = bookName.substring(0, size) + "...";
            }
            
			canvas.drawText(bookName, marginWidth, textHeight, paint);
		}

	}

	@Override
	protected void drawFoot(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint paint = config.getExtraPaint();
		
		int width = config.getWidth();
		int height = config.getHeight();
		
		float marginWidth = config.getMarginWidth();
		//float marginHeight = config.getMarginHeight();

		FontMetrics fm = paint.getFontMetrics();
		float marginBottom =  (float)Math.ceil(fm.ascent - fm.top+fm.descent);
		
		
		// 进度
		BookCache curCache = mCacheManager.getCache(PageIndex.current);
		String strPercent = (curCache.getCurPage() + 1) + "/" + curCache.getPageCount();
		float nPercentWidth = paint.measureText(strPercent);
        float left = width - nPercentWidth - marginWidth;
		canvas.drawText(strPercent, left, height - marginBottom, paint);
        
		// 章节名
		if(onDrawListener != null) {
			String chapterName = onDrawListener.getChapterName();
//			String chapterName ="我爱你";
	        Pattern pat = Pattern.compile("\\s{1,}");
			Matcher m = pat.matcher(chapterName);
	        chapterName = m.replaceAll(" ");
            int size = paint.breakText(chapterName, true, width - 2*(width - left), null);
            if(size < chapterName.length()) {
            	chapterName = chapterName.substring(0, size) + "...";
            }            
			canvas.drawText(chapterName, marginWidth, height - marginBottom, paint);
		}
		
		
	}
	
}
