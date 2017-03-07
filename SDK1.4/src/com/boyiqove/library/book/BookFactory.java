package com.boyiqove.library.book;

import java.util.Vector;

import com.boyiqove.AppData;
import com.boyiqove.config.ReadConfig;
import com.boyiqove.library.book.BookView.PageIndex;
import com.boyiqove.util.DebugLog;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

abstract public class BookFactory {
	protected BookCacheManager 	mCacheManager;
	protected ReadConfig 		config;
	
	public BookFactory(BookCacheManager cacheManager) {
		mCacheManager = cacheManager;
		init();
	}
	
	public void setCache(BookCacheManager cacheManager) {
		mCacheManager = cacheManager;
	}
		
	private void init() {
		config = AppData.getConfig().getReadConfig();
	}
	
	public boolean pageUp() {
		return mCacheManager.getCache(PageIndex.current).pageUp();
	}
	
	public boolean pageDown() {
		return mCacheManager.getCache(PageIndex.current).pageDown();
	}
	
    
	public void draw(Canvas canvas) {
        if(config.getWidth() == 0 || config.getHeight() == 0) {
            return;
        }
        
        //long start = System.currentTimeMillis();
		
        BookCache cache = mCacheManager.getCache(PageIndex.current);
		Vector<String> lines = cache.getLines();
		
		if(null == lines) {
			throw new RuntimeException("book draw text is null");
		}

		// 1.绘制背景
		Bitmap bg = config.getBackBitmap();
		if (bg == null) {
			canvas.drawColor(config.getBackColor());
			
		} else {
			canvas.drawBitmap(bg, 0, 0, null);
		}

		float x = config.getMarginWidth();
		float y;
        float textHeight = config.getTextHeight(config.getTextPaint());
		
        // 1.绘制章节名
        if(cache.getCurPage() == 0) {
            y = config.getChapterY();
//			canvas.drawLine(x, y, config.getWidth() - x, y, config.getChapterPaint());
            float cTop = config.getChapterPaint().getFontMetrics().top;
            float cBottom = config.getChapterPaint().getFontMetrics().bottom;
            float cD = config.getChapterPaint().getFontMetrics().ascent;
//            y += (-cTop);
            float chHeight = config.getTextHeight(config.getChapterPaint());
            Vector<String> chLines = cache.getChLines();
            int cSize = chLines.size();
            float drawY = y - cTop;
            for(int i = 0; i < cSize; i++) {
//            	Paint paint=config.getChapterPaint();
//            	paint.setColor(AppData.getColor(R.color.boyi_reader_page_title));
				canvas.drawText(chLines.get(i), x, drawY, config.getChapterPaint());
				y += chHeight + config.getLineSpacing();
				drawY += chHeight + config.getLineSpacing();
				if(i == cSize - 1) break;
            }
//            y += 24;//config.getLineSpacing();
//            drawY += 24;
//			canvas.drawLine(x, y, config.getWidth() - x, y, config.getChapterPaint());
            y += textHeight;
            
        } else {
//        	y = config.getMarginHeight() + config.getLineSpacing();
        	y = config.getMarginHeight();
        }
		
        // 2.绘制正文
//		canvas.drawLine(x, y, config.getWidth() - x, y, config.getChapterPaint());
//        DebugLog.d("BookFactory", y+"---");
		y += (-config.getTextPaint().getFontMetrics().top);
//		canvas.drawLine(x, y, config.getWidth() - x, y, config.getChapterPaint());
//		canvas.drawLine(x, y+config.getTextPaint().getFontMetrics().bottom, config.getWidth() - x, y+config.getTextPaint().getFontMetrics().bottom, config.getChapterPaint());
        boolean isBlankFirst = true;
        boolean isFirstLine = true;
        for(int i = 0; i < lines.size(); i++) {
        	String strLine = lines.get(i);
        	if(strLine.equals("/n")){
        		if(!isBlankFirst)
        			y += config.getLineSpacing()*config.paragraphSpace;
//        		isFirstLine = true;
        	}
        	else{
//        		y += (textHeight + config.getLineSpacing());
//            	y += (-config.getTextPaint().getFontMetrics().top);
        		if(!isFirstLine){
        			float deltaY = (textHeight + config.getLineSpacing());
        			y += deltaY;
            		canvas.drawText(strLine, x, y - config.getLineSpacing(), config.getTextPaint());
//            		canvas.drawLine(x, y - config.getLineSpacing(), config.getWidth() - x, y - config.getLineSpacing(), config.getChapterPaint());
//            		DebugLog.d("BookFactorys", (y - config.getLineSpacing())+"");
        		}
        		else{
//        			y += config.getTextPaint().getFontMetrics().top;
            		canvas.drawText(strLine, x, y, config.getTextPaint());
//            		DebugLog.d("BookFactorys", y+"");
//            		canvas.drawLine(x, y, config.getWidth() - x, y, config.getChapterPaint());
            		y += config.getLineSpacing();
        		}
        		if(isBlankFirst) isBlankFirst = false;
        		if(isFirstLine) isFirstLine = false;
        	}
        }

//		canvas.drawLine(x, y, config.getWidth() - x, y, config.getChapterPaint());
//        DebugLog.d("BookFactory", (config.getHeight() - config.getMarginHeight())+"======");
//		canvas.drawLine(x, config.getHeight() - config.getMarginHeight(), config.getWidth() - x, config.getHeight() - config.getMarginHeight(), config.getChapterPaint());
		// 3. 绘制头部
		drawHead(canvas);

		// 4. 绘制底部
		drawFoot(canvas);
        
        //DebugLog.d("BookDraw", "draw page: " + cache.getCurPage() + ",used:" + (System.currentTimeMillis() - start)/1000.0f + "s");
	}
    
    
    
	public void drawFirst(Canvas canvas, String bookName, String author) {
        if(null == bookName) {
        	bookName = "";
        }
        
        if(null == author) {
            author = "";
        }
        
        // 1.绘制背景
		Bitmap bg = config.getBackBitmap();
		if (bg == null) {
			canvas.drawColor(config.getBackColor());
			
		} else {
			canvas.drawBitmap(bg, 0, 0, null);
		}
        
        Paint paint = config.getBooknamePaint();
        
        Vector<String> lines = new Vector<String>();
		while(bookName.length() > 0) {
            int size = paint.breakText(bookName, true, config.getVisibleWidth(), null);
            if(size > 0) {
                if(lines.size() > 2) {
                	lines.add("...");
                    break;
                }
            	lines.add(bookName.substring(0, size));
                bookName = bookName.substring(size);
            }
		}
        float x = config.getWidth()/2;
        float y = config.getHeight()/2 - 2*(config.getTextHeight(paint) + config.getLineSpacing());
        for(int i = 0; i < lines.size(); i++) {
            y += config.getTextHeight(paint) + config.getLineSpacing();
        	canvas.drawText(lines.get(i), x, y, paint);
        }
        
        y += config.getTextHeight(paint);
        
        paint = config.getAuthorPaint();
        
        lines.clear();
		while(author.length() > 0) {
            int size = paint.breakText(author, true, config.getVisibleWidth(), null);
            if(size > 0) {
                if(lines.size() > 2) {
                	lines.add("...");
                    break;
                }
            	lines.add(author.substring(0, size));
                author = author.substring(size);
            }
		}
        
        for(int i = 0; i < lines.size(); i++) {
        	canvas.drawText(lines.get(i), x, y, paint);
            y += config.getTextHeight(paint) + config.getLineSpacing();
        }
		
	}
	
	abstract protected void drawHead(Canvas canvas);
	abstract protected void drawFoot(Canvas canvas);
}
