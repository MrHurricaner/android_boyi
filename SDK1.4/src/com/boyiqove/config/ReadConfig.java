package com.boyiqove.config;

import java.io.IOException;
import java.io.InputStream;

import com.boyiqove.R;
import com.boyiqove.library.book.PageWidget.Mode;
import com.boyiqove.util.BitmapTool;
import com.boyiqove.util.DisplayUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;


public class ReadConfig {

	private final static String KEY_AUTO_BUY = "auto_buy";
	private final static String KEY_TEXT_SIZE = "text_size";
	private final static String KEY_LINE_SPACINE = "line_spacing";
	private final static String KEY_LINE_SPACINE_INDEX = "line_spacing_index";
	private final static String KEY_IS_PORTRAIT = "portrait";
	private final static String KEY_SCROLL_MODE = "scroll_mode";
	private final static String KEY_IS_SYSBRIGHTNESS = "system_brightness";
	private final static String KEY_READ_BRIGHTNESS = "brightness";
	private final static String KEY_COLOR_INDEX = "color_index";
	private final static String KEY_COLOR_LAST_INDEX = "color_last_index";
	private final static String KEY_BG_COLOR = "bg_color";
	///private final static String KEY_READ_FROM_LAST = "read_from_last";
	private final static String KEY_TIRED_MODE = "tired_mode";
    private final static String KEY_SIMPLE_CHINESE = "simpleChinese";


	private static int LINE_SPACING_MIN = 2;
	private static int LINE_SPACING_MAX = 32;
	private static int LINE_SPACING_DEFAULT = 18;

	private static int LINE_SPACING_0 = 24;
	private static int LINE_SPACING_1 = 18;
	private static int LINE_SPACING_2 = 10;
	private static int LINE_SPACING_3 = 5;
	private int LINE_SPACE[] = new int[]{LINE_SPACING_0, LINE_SPACING_1, LINE_SPACING_2, LINE_SPACING_3};

	

	private static int TEXT_SIZE_MIN_SP = 4;
	private static int TEXT_SIZE_MAX_SP = 50;
	private static int TEXT_SIZE_DEFAULT_SP = 20;
    private static int TEXT_SIZE_CHAPTER_NAME_SP = 24;//32;
    private static int HEAD_TEXT_SIZE_SP = 12;//14;
    
    private static int TEXT_SIZE_BOOKNAME_SP = 32;//36;
    private static int TEXT_SIZE_AUTHOR_SP = 18;

	private static final int COLOR_SIZE = 5;


	public enum TiredMode {
		Min30(0), Hour1(1), Hour3(2), None(3);

		public int index;

		private TiredMode(int index) {
			// TODO Auto-generated constructor stub
			this.index = index;
		}

		public static TiredMode getTiredMode(int index) {
			switch(index) {
			case 0:
				return Min30;
			case 1:
				return Hour1;
			case 2:
				return Hour3;
			case 3:
			default:
				return None;
			}
		}

		public int getMintutes() {
			switch(this) {
			case Min30:
				return 30;
			case Hour1:
				return 60;
			case Hour3:
				return 180;
			case None:
			default:
				return -1;
			}
		}


	}

	private SharedPreferences sp;

    private boolean 	autoBuy;		// 自动购买章节

	private	boolean 	portrait;		// 屏幕方向 true : 竖屏 ， false : 横屏
	private	Mode 		scrollMode;		// 翻页模式: 拟真， 左右滑动， 无
	private boolean 	sysBrightness;  
	private int 		readBrightness;     // 亮度

	//private boolean 	readFromLast;
	private TiredMode 	tiredMode;
    
	private boolean 	simpleChinese;	// 简体，繁体


	// 阅读显示前，必须设置
	private int 		width;
	private int 		height;

	private float 	marginWidth; 				// 左右与边缘的距离 
	private float 	marginHeight; 				// 上下与边缘的距离
	private float 	batteryHeight;
	private float	batteryWidth;
	private float	batteryPercent = 0.0f;

	private int 	textSize;		// 单位sp
	private int 	lineSpacing;
	private int 	lineSpacingIndex;

	private int 	colorIndex;
	private int 	colorLastIndex;
	private int 	textColor[] = new int[]{R.color.boyi_read_text_sheep2, R.color.boyi_read_text_ink, R.color.boyi_read_text_girl2, R.color.boyi_read_text_boy2, R.color.boyi_read_text_night};
	private int 	titleColor[] = new int[]{R.color.boyi_read_title_sheep, R.color.boyi_read_title_ink, R.color.boyi_read_title_girl, R.color.boyi_read_title_boy, R.color.boyi_read_title_night};
	private int 	extraColor[] = new int[]{R.color.boyi_read_extra_sheep, R.color.boyi_read_extra_ink, R.color.boyi_read_extra_girl, R.color.boyi_read_extra_boy, R.color.boyi_read_text_night};
	private int 	chapterColor[] = new int[]{R.color.boyi_reader_page_title, R.color.boyi_read_chapter_ink, R.color.boyi_read_chapter_girl2, R.color.boyi_read_chapter_boy2, R.color.boyi_read_chapter_night};
    
	
	private 		LruCache<Integer, Bitmap> lruCache = new LruCache<Integer, Bitmap>(4);

	private int 	bgColor;

	private Paint 	textPaint;
	//private Paint 	titlePaint;
	private Paint 	extraPaint;
    
	private Paint 	chapterPaint;
    private int 	chapterY;
    
    private Paint 	booknamePaint;
    private Paint 	authorPaint;

	// 计算后获取
	private float 	visibleWidth; 				// 绘制内容的宽
	private float 	visibleHeight; 				// 绘制内容的高
	private int 	lineCount; 					// 
	
    //private int 	chLineCount;				// 章节首页可绘制行数
	public int     paragraphSpace = 3;

	private Context context;

	public ReadConfig(Context context) {
		this.context = context;

		sp = context.getSharedPreferences("read_action", Context.MODE_PRIVATE);

		initData();
		initColor();
		initXml();
		initExtra();
	}

	private void initData(){
        chapterY = 2*(int)(context.getResources().getDimension(R.dimen.read_action_top_height));
	}

	private void initColor() {
		Resources res = context.getResources();
		for(int i = 0; i < COLOR_SIZE; i++) {
			textColor[i] = res.getColor(textColor[i]);
			titleColor[i] = res.getColor(titleColor[i]);
			extraColor[i] = res.getColor(extraColor[i]);
			chapterColor[i] = res.getColor(chapterColor[i]);
		}
	}

	private void initXml() {
		autoBuy = sp.getBoolean(KEY_AUTO_BUY, true);
        
		textSize = sp.getInt(KEY_TEXT_SIZE, TEXT_SIZE_DEFAULT_SP);
		lineSpacing = sp.getInt(KEY_LINE_SPACINE, LINE_SPACING_DEFAULT);
		lineSpacingIndex=sp.getInt(KEY_LINE_SPACINE_INDEX,1);
		portrait = sp.getBoolean(KEY_IS_PORTRAIT, true);
		int scroll = sp.getInt(KEY_SCROLL_MODE, 0);
		scrollMode = Mode.getMode(scroll);
		sysBrightness = sp.getBoolean(KEY_IS_SYSBRIGHTNESS, true);
		readBrightness = sp.getInt(KEY_READ_BRIGHTNESS, 255);

		colorIndex = sp.getInt(KEY_COLOR_INDEX, 0);
		if(colorIndex < 0 || colorIndex > COLOR_SIZE) {
			colorIndex = 0;
		}

		bgColor = sp.getInt(KEY_BG_COLOR, 0xffffffff);

		//readFromLast = sp.getBoolean(KEY_READ_FROM_LAST, true);
        
		int tired = sp.getInt(KEY_TIRED_MODE, 3);
		tiredMode = TiredMode.getTiredMode(tired);
        
		simpleChinese = sp.getBoolean(KEY_SIMPLE_CHINESE, true);
	}

	private void initExtra() {
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextAlign(Align.LEFT);
		textPaint.setTextSize(DisplayUtil.sp2px(context, textSize));
		textPaint.setColor(textColor[colorIndex]);

		extraPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		extraPaint.setTextAlign(Align.LEFT);
		extraPaint.setTextSize(DisplayUtil.sp2px(context, HEAD_TEXT_SIZE_SP));
		extraPaint.setColor(extraColor[colorIndex]);

//		titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		titlePaint.setTextAlign(Align.LEFT);
//		titlePaint.setTextSize(DisplayUtil.sp2px(context, HEAD_TEXT_SIZE_SP));
//		titlePaint.setColor(titleColor[colorIndex]);

        float textHeight = getExtraTextHeight();
		//marginHeight = getExtraTextHeight();
        marginHeight = DisplayUtil.dip2px(context, 32.0f);
        marginWidth = DisplayUtil.dip2px(context, 15.0f);
		batteryHeight = textHeight *2 / 3.0f;
        batteryWidth = 2 * batteryHeight;
        
		chapterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        chapterPaint.setTextAlign(Align.LEFT);
		chapterPaint.setTextSize(DisplayUtil.sp2px(context, textSize+2));
        chapterPaint.setColor(chapterColor[colorIndex]);
        chapterPaint.setTypeface(Typeface.DEFAULT_BOLD);
        
        
        booknamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        booknamePaint.setTextAlign(Align.CENTER);
		booknamePaint.setTextSize(DisplayUtil.sp2px(context, TEXT_SIZE_BOOKNAME_SP));
        booknamePaint.setColor(chapterColor[colorIndex]);
        booknamePaint.setTypeface(Typeface.DEFAULT_BOLD);
        
        authorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        authorPaint.setTextAlign(Align.CENTER);
		authorPaint.setTextSize(DisplayUtil.sp2px(context, TEXT_SIZE_AUTHOR_SP));
        authorPaint.setColor(extraColor[colorIndex]);
        authorPaint.setTypeface(Typeface.DEFAULT_BOLD);
	}


	public boolean isAutoBuy() {
		return autoBuy;
	}

	public void setAutoBuy(boolean autoBuy) {
        if(this.autoBuy == autoBuy) {
        	return;
        }
		this.autoBuy = autoBuy;
        Editor editor = sp.edit();
        editor.putBoolean(KEY_AUTO_BUY, autoBuy);
        editor.commit();
	}

	public int getTextSize() {
		return textSize;
	}

	public float getTextHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
//		return (float)Math.ceil(fm.descent - fm.ascent + fm.leading);
		return (float)Math.ceil(fm.bottom - fm.top);
	}

	public float getExtraTextHeight() {
		FontMetrics fm = extraPaint.getFontMetrics();
//		return (float)Math.ceil(fm.descent - fm.ascent + fm.leading);
		return (float)Math.ceil(fm.bottom - fm.top);
	}


	public boolean setTextSize(int textSize) {
		if(this.textSize == textSize) {
			return true;
		}

		if(textSize < TEXT_SIZE_MIN_SP || textSize > TEXT_SIZE_MAX_SP) {
			return false;
		}

		this.textSize = textSize;
		this.textPaint.setTextSize(DisplayUtil.sp2px(context, this.textSize));
		chapterPaint.setTextSize(DisplayUtil.sp2px(context, textSize+2));
		calLineCount();

		Editor editor = sp.edit();
		editor.putInt(KEY_TEXT_SIZE, this.textSize);
		editor.commit();

		return true;
	}

	public int getLineSpacing() {
		return lineSpacing;
	}


	public int getLineSpacingIndex() {
		return lineSpacingIndex;
	}

	public void setLineSpacingIndex(int index) {
		if(this.lineSpacingIndex == index) {
			return;
		}

		if(index < 0 || index >= LINE_SPACE.length) {
			throw new RuntimeException("this line index is impossible, size:" + LINE_SPACE.length + ",index:" + index);
		}

		this.lineSpacingIndex = index;
		this.lineSpacing = LINE_SPACE[lineSpacingIndex];
		calLineCount();

		Editor editor = sp.edit();
		editor.putInt(KEY_LINE_SPACINE, this.lineSpacing);
		editor.putInt(KEY_LINE_SPACINE_INDEX,index);
		editor.commit();
	}

	public void setSize(int w, int h) {
		if(w != width || h != height) {
			width = w;
			height = h;

			visibleWidth = width - marginWidth * 2;
			visibleHeight = height - marginHeight * 2;
            
			clearBackBitmap();
			calLineCount();
		}
	}

	private void calLineCount() {
		lineCount = (int)(visibleHeight / (getTextHeight(textPaint) + lineSpacing));
        
	}

	public boolean isPortrait() {
		return portrait;
	}

	public void setPortrait(boolean portrait) {
		if(this.portrait == portrait) {
			return;
		}
		this.portrait = portrait;

		Editor editor = sp.edit();
		editor.putBoolean(KEY_IS_PORTRAIT, this.portrait);
		editor.commit();
	}

	public Mode getScrollMode() {
		return scrollMode;
	}


	public void setScrollMode(Mode scrollMode) {
		if(this.scrollMode == scrollMode) {
			return;
		}
		this.scrollMode = scrollMode;

		Editor editor = sp.edit();
		editor.putInt(KEY_SCROLL_MODE, this.scrollMode.index);
		editor.commit();
	}

	public boolean isSysBrightness() {
		return sysBrightness;
	}

	public void setSysBrightness(boolean sysBrightness) {
		if(this.sysBrightness == sysBrightness) {
			return;
		}

		this.sysBrightness = sysBrightness;

		Editor editor = sp.edit();
		editor.putBoolean(KEY_IS_SYSBRIGHTNESS, this.sysBrightness);
		editor.commit();
	}

	public int getReadBrightness() {
		return readBrightness;
	}

	public void setReadBrightness(int readBrightness) {
		if(this.readBrightness == readBrightness) {
			return;
		}

		this.readBrightness = readBrightness;

		Editor editor = sp.edit();
		editor.putInt(KEY_READ_BRIGHTNESS, this.readBrightness);
		editor.commit();
	}




	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}


	public float getMarginWidth() {
		return marginWidth;
	}

	public float getMarginHeight() {
		return marginHeight;
	}

	public float getVisibleWidth() {
		return visibleWidth;
	}

	public float getVisibleHeight() {
		return visibleHeight;
	}

	public int getLineCount() {
		return lineCount;
	}
    
//	public int getChLineCount(){
//		return chLineCount;
//	}


	public float getBatteryHeight() {
		return batteryHeight;
	}

//	public void setBatteryHeight(int batteryHeight) {
//		this.batteryHeight = batteryHeight;
//	}

	public float getBatteryWidth() {
		return batteryWidth;
	}

//	public void setBatteryWidth(int batteryWidth) {
//		this.batteryWidth = batteryWidth;
//	}

	public float getBatterPercent() {
		return batteryPercent;
	}

	public void setBatteryPercent(float percent) {
		batteryPercent = percent;
	}


	public int getColorIndex() {
		return this.colorIndex;
	}

	public void setColorIndex(int index) {
		if(this.colorIndex == index) {
			return;
		}

		if(index < 0 || index > COLOR_SIZE) {
			throw new RuntimeException("this color is not exist");
		}

		this.colorIndex = index;
		textPaint.setColor(textColor[colorIndex]);
		extraPaint.setColor(extraColor[colorIndex]);
		//titlePaint.setColor(titleColor[colorIndex]);
		chapterPaint.setColor(chapterColor[colorIndex]);

		Editor editor = sp.edit();
		editor.putInt(KEY_COLOR_INDEX, this.colorIndex);
		editor.commit();
	}
	
	public int getLastColorIndex() {
		this.colorLastIndex=sp.getInt(KEY_COLOR_LAST_INDEX, -1);
		return this.colorLastIndex;
	}
	public void setLastColorIndex(int index) {
		
		if(index < 0 || index > COLOR_SIZE) {
			throw new RuntimeException("this color is not exist");
		}
		
		this.colorLastIndex = index;

		Editor editor = sp.edit();
		editor.putInt(KEY_COLOR_LAST_INDEX, this.colorLastIndex);
		editor.commit();
	}

	public Paint getTextPaint() {
		return textPaint;
	}

	public Paint getExtraPaint() {
		return extraPaint;
	}

//	public Paint getTitlePaint() {
//		return titlePaint;
//	}

	public int getBackColor() {
		return bgColor;
	}

	public void setBackColor(int bgColor) {
		if(this.bgColor == bgColor) {
			return;
		}
		this.bgColor = bgColor;
		Editor editor = sp.edit();
		editor.putInt(KEY_BG_COLOR, this.bgColor);
		editor.commit();
	}

	private final static String bgFile[] = new String[]{"bg_read0.png", "bg_read2.png", "bg_read1.png", "bg_read3.png"};
	public Bitmap getBackBitmap() {
		if(this.colorIndex == COLOR_SIZE - 1) {
			bgColor = 0xff000000; // 夜间
			return null;
		}
		if(this.colorIndex == COLOR_SIZE - 2) {
			bgColor = 0xff051c2c; // 黑色
			return null;
		}
		if(this.colorIndex == COLOR_SIZE - 3) {
			bgColor = 0xff383432; // 棕色
			return null;
		}
		Bitmap newBitmap = null;
		newBitmap = lruCache.get(this.colorIndex);
		if(null == newBitmap) {

			AssetManager am = null;  
			am = context.getAssets();  
			try {
				InputStream is = am.open(bgFile[this.colorIndex]);

				try {
					newBitmap = BitmapTool.decodeZoomBitmap(is, width, height);

				} catch(OutOfMemoryError e) {
					e.printStackTrace();

					System.gc();
					System.gc();

					newBitmap = BitmapTool.decodeZoomBitmap(is, width, height);
				}

				//DebugLog.d("ReadConfig", "bitmap 处理后大小 width:" + newBitmap.getWidth() + ", height:" + newBitmap.getHeight());

				is.close();

				lruCache.put(this.colorIndex, newBitmap);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return newBitmap;
	}
    
	public void clearBackBitmap() {
        lruCache.evictAll();
	}
    

//	public boolean isReadFromLast() {
//		return readFromLast;
//	}
//
//	public void setReadFromLast(boolean readFromLast) {
//		if(this.readFromLast == readFromLast) {
//			return;
//		}
//
//		this.readFromLast = readFromLast;
//		Editor editor = sp.edit();
//		editor.putBoolean(KEY_READ_FROM_LAST, this.readFromLast);
//		editor.commit();
//	}

	public TiredMode getTiredMode() {
		return tiredMode;
	}

	public void setTiredMode(TiredMode mode) {
		if(this.tiredMode == mode) {
			return;
		}
		this.tiredMode = mode;
		Editor editor = sp.edit();
		editor.putInt(KEY_TIRED_MODE, this.tiredMode.index);
		editor.commit();
	}
    
	public Paint getChapterPaint() {
		return chapterPaint;
	}
    
	public int getChapterY() {
        return chapterY;
	}

	public boolean isSimpleChinese() {
		return simpleChinese;
	}

	public void setSimpleChinese() {
		simpleChinese = !simpleChinese;
        Editor editor = sp.edit();
        editor.putBoolean(KEY_SIMPLE_CHINESE, this.simpleChinese);
        editor.commit();
	}
    
    
	public Paint getBooknamePaint() {
		return booknamePaint;
	}
    
	public Paint getAuthorPaint() {
		return authorPaint;
	}

} 