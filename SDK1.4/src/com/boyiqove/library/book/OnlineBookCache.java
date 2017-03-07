package com.boyiqove.library.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taobe.tec.jcc.JChineseConvertor;

import com.boyiqove.AppData;
import com.boyiqove.config.ReadConfig;
import com.boyiqove.util.DebugLog;


public class OnlineBookCache extends BookCache {
	private final static String TAG = "OnlineBookCache";
	
	public class PagePoint{
		private PagePoint(int begin, int size) {
			super();
			this.begin = begin;
			this.size = size;
		}
		public int begin;
		public int size;
	}
    
	private	String feed = "\n";
	
    private String chapter;
	private String buf;
	
	private List<Vector<PagePoint>> list;
	private int curPage;
	
    private Vector<String> chLines; 		// 章节名
	private Vector<String> lines;		// 内容
	
	private ReadConfig 		config;
	
	private boolean 		isRest = false;
    private boolean 		isSimple = true;
	
	public OnlineBookCache() {
        chLines = new Vector<String>();
        
		list  = new ArrayList<Vector<PagePoint>>();
		lines = new Vector<String>();
		
		config = AppData.getConfig().getReadConfig();
		
		curPage = 0;
	}
	
	@Override
	public void reset(boolean dealy) {
		// TODO Auto-generated method stub
		if(this.buf == null) {
			return;
		}
		if(dealy) {
			isRest = true;
		} else {
			int lastPosition = getCurPagePosition();
            
			parse();
			setPosition(lastPosition);
			isRest = false;
		}
	}

	@Override
	public void parse(String chapter, String content) {
		// TODO Auto-generated method stub
		if(null == content || null == chapter){
			throw new RuntimeException("this buf is null");
		}
        this.chapter = chapter;
        this.buf = content;
		this.curPage = 0;
        
        composeBuf();
        parse();
	}
    
    // 1.排版  读取所有的段落
	private void composeBuf() {
        long s = System.currentTimeMillis();
        		
        StringBuilder strBuilder = new StringBuilder();
        int start = 0;
		while(start < buf.length()){

			int offset = 0;
			// 首先读取一个段落
			String strParagrah;
			int end = this.buf.indexOf(feed, start);
			if(end != -1) {
				strParagrah = this.buf.substring(start, end + feed.length());
				offset =  end + feed.length();
			} else {
				strParagrah = this.buf.substring(start);
				offset = buf.length();
			}

			if(strParagrah != null){
//				Pattern patPar = Pattern.compile("\\s{1,}|\t|\r|\n");
				Pattern patPar = Pattern.compile("\\s*|\t|\r|\n");
				Matcher mPar = patPar.matcher(strParagrah);
				strParagrah = mPar.replaceAll("");
			}
			
            if(strParagrah.length() <= 0) {
                
            } else {
            	//DebugLog.d(TAG, "pargrah(" + strParagrah.length() + "):" + strParagrah);

            	int blank = 0;
            	for(int i = 0; i < strParagrah.length(); i++) {
            		if(strParagrah.charAt(i) == '　') {
            			blank++;
            		} else {
            			break;
            		}
            	}

            	String add = null;
            	if(blank < 2) {
            		switch(blank) {
            		case 0:
            			add = "　　";
            			break;
            		case 1:
            			add = "　";
            			break;
            		}

            		strParagrah = add + strParagrah;

            	} else if(blank > 3){
            		strParagrah = strParagrah.substring(blank - 2);
            	}

            	strBuilder.append(strParagrah);

            	strBuilder.append("\n");
            }

			start = offset;
		}

		this.buf = strBuilder.toString();
        
		
		DebugLog.d(TAG, "compose used:" + (System.currentTimeMillis() - s) / 1000.0f + "s");
	}
	
    // 2.解析
    private void parse() {
        if(config.getWidth() == 0 || config.getHeight() == 0) {
        	return;
        }
        
        changeSimple();
    	
        chLines.clear();
		lines.clear();
		list.clear();
        
        long s = System.currentTimeMillis();
		
		int start = 0;
		
        DebugLog.d(TAG, "start at:" + chapter);
        
        // 1. 标题
//<<<<<<< .mine

//        String ch = chapter;
        //处理多余空格  modify by qiaowei
        Pattern pat = Pattern.compile("\\s{1,}");
		Matcher m = pat.matcher(chapter);

        String ch = m.replaceAll(" ");
        
		while(ch.length() > 0 ) {
			int nSize = config.getChapterPaint().breakText(ch, true, config.getVisibleWidth(), null);
			chLines.add(ch.substring(0, nSize));
			ch = ch.substring(nSize);
//=======
//        String strCht = chapter;
//		while(strCht.length() > 0 ) {
//			int nSize = config.getChapterPaint().breakText(strCht, true, config.getVisibleWidth(), null);
//			chLines.add(strCht.substring(0, nSize));
//			strCht = strCht.substring(nSize);
//>>>>>>> .r166
		}

		// 2. 正文
        float textHeight = config.getTextHeight(config.getTextPaint());
        float visibleHeight = config.getHeight() - config.getMarginHeight() - config.getChapterY() - (config.getTextHeight(config.getChapterPaint()) + config.getLineSpacing())*chLines.size() - textHeight;
		int lineCount = (int)(visibleHeight  / (textHeight + config.getLineSpacing()));
        DebugLog.d("cache", "first page lineCount:" + lineCount);
		
		float textLineSpace = config.getLineSpacing();
		float minContentShowY = config.getChapterY() + (config.getTextHeight(config.getChapterPaint()) + config.getLineSpacing())*chLines.size() + textHeight;//+24;
		float maxContentShowY = config.getHeight() - config.getMarginHeight();
        DebugLog.d(TAG, "minContentShowY:"+minContentShowY + "---maxContentShowY:"+maxContentShowY);
        int page = 0;
		
		Vector<PagePoint> v = new Vector<OnlineBookCache.PagePoint>();
        
        start = 0;
        //int nSize = 0;
        float y = 0;
        DebugLog.d(TAG, "chapter content len:" + buf.length());
        
        float[] widths = new float[1];
        float visibleWidth = config.getVisibleWidth();
        
		while(start < buf.length()){
			
			int offset = 0;
			// 首先读取一个段落
			String strParagrah;
			int end = this.buf.indexOf(feed, start);
			if(end != -1) {
				strParagrah = this.buf.substring(start, end + feed.length());
				offset =  end + feed.length();
			} else {
				strParagrah = this.buf.substring(start);
				offset = buf.length();
			}
            //DebugLog.d(TAG, "offset:" + offset);
			
			if(strParagrah.length() <= 0){//  feed.length()) {
//				PagePoint point = new PagePoint(start, strParagrah.length());
//				v.add(point);
				//DebugLog.d(TAG, "------------add feed--------------");

//<<<<<<< .mine
//				if(v.size() == lineCount) {
//					//DebugLog.d(TAG, "-----------page add---------------");
//					list.add(v);
//					page++;
//					v = new Vector<OnlineBookCache.PagePoint>();
//				}
//=======
//				if(v.size() == lineCount) {
//					//DebugLog.d(TAG, "-----------page add---------------");
//					list.add(v);
//					page++;
//					if(page == 1) {
//						lineCount = config.getLineCount();
//					}
//					v = new Vector<OnlineBookCache.PagePoint>();
//				}
//>>>>>>> .r166

			} else {
				int begin = start;
                //DebugLog.d(TAG, "parese pargrah(" + strParagrah.length() + "):" + strParagrah);
//<<<<<<< .mine
				while(strParagrah.length() > 0 ) {
					int nSize = config.getTextPaint().breakText(strParagrah, true, config.getVisibleWidth(), null);

//					DebugLog.d(TAG, "nsize="+nSize+"--"+strParagrah.length());
					//DebugLog.d(TAG, strParagrah.substring(0, nSize));
					
					if(nSize > 0) {
						PagePoint point = new PagePoint(begin, nSize);
//=======
//				
//				char ch;
//				int w = 0;
//				int iStart = 0;
//				int count = strParagrah.length();
//				for (int i = 0; i < count; i++) {  
//					ch = strParagrah.charAt(i);  
//					String str = String.valueOf(ch);  
//					config.getTextPaint().getTextWidths(str, widths);  
//
//					w += (int) Math.ceil(widths[0]);  
//					if (w > visibleWidth) {  
//						// 行数+1
//						PagePoint point = new PagePoint(begin + iStart, i - iStart);
//>>>>>>> .r166
//						DebugLog.d(TAG, buf.substring(begin,begin+nSize));
//						v.add(point);
//<<<<<<< .mine
//						if(v.size() == lineCount) {
//							//DebugLog.d(TAG, "-----------page add---------------");
//							list.add(v);
//                            page++;
//							v = new Vector<OnlineBookCache.PagePoint>();
//						}

						if(!strParagrah.equals(feed)){
							y += textHeight + textLineSpace;
							if(minContentShowY+y>maxContentShowY){
								page++;
								if(page == 1){
									minContentShowY = config.getMarginHeight();
							        DebugLog.d(TAG, "minContentShowY:"+minContentShowY + "---maxContentShowY:"+maxContentShowY);
								}
								list.add(v);
								v = new Vector<OnlineBookCache.PagePoint>();
								v.add(point);
								y = 0;
								y += textHeight + textLineSpace;
							}
							else{
								v.add(point);
							}
//=======
//						if(v.size() == lineCount) {
//							//DebugLog.d(TAG, "-----------page add---------------");
//							list.add(v);
//							page++;
//
//							if(page == 1) {
//								lineCount = config.getLineCount();
//							}
//
//							v = new Vector<OnlineBookCache.PagePoint>();
//>>>>>>> .r166
						}
//<<<<<<< .mine
//=======
//						
//						iStart = i;
//						i--;  
//						w = 0;  
//					} else {  
//						if (i == count - 1) {  
//							// 最后一行 +1
//							PagePoint point = new PagePoint(begin + iStart, i - iStart);
//							v.add(point);
//							if(v.size() == lineCount) {
//								//DebugLog.d(TAG, "-----------page add---------------");
//								list.add(v);
//								page++;
//								
//								if(page == 1) {
//									lineCount = config.getLineCount();
//								}
//
//>>>>>>> .r166
//<<<<<<< .mine
						begin += nSize;
					} else {
						DebugLog.d(TAG, "size is 0");
					}
					
					strParagrah = strParagrah.substring(nSize);
					if(strParagrah.length() == 0){
						y += textLineSpace*config.paragraphSpace;
						if(minContentShowY+y>maxContentShowY){
							page++;
							if(page == 1){
								minContentShowY = config.getMarginHeight();
						        DebugLog.d(TAG, "minContentShowY:"+minContentShowY + "---maxContentShowY:"+maxContentShowY);
							}
							list.add(v);
							v = new Vector<OnlineBookCache.PagePoint>();
							y = 0;
						}
						else{
							PagePoint point = new PagePoint(-1, -1);
							v.add(point);
						}
					}
				}
//=======
//								v = new Vector<OnlineBookCache.PagePoint>();
//							}
//
//							iStart = i;
//						}  
//					}  
//				}  
//
//>>>>>>> .r166
			}
			
			start = offset;
            
//<<<<<<< .mine
//			if(page == 1) {
//				lineCount = config.getLineCount();
//			}
//=======
//			
//>>>>>>> .r166
		}
		
		if(v.size() > 0) {
			list.add(v);
		}
        
		DebugLog.d(TAG, "parse used:" + (System.currentTimeMillis() - s) / 1000.0f + "s, thread:" + Thread.currentThread().getId());
    }
    
    
	
    
	private void changeSimple() {
		if(isSimple != config.isSimpleChinese()) {
			if(config.isSimpleChinese()) {
                try {
                    chapter = JChineseConvertor.getInstance().t2s(chapter);
					buf = JChineseConvertor.getInstance().t2s(buf);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
                try {
                    chapter = JChineseConvertor.getInstance().s2t(chapter);
					buf = JChineseConvertor.getInstance().s2t(buf);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
            
			isSimple = config.isSimpleChinese();
            
		}
	}

	@Override
	public int getPageCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public int getCurPagePosition() {
		// TODO Auto-generated method stub
		
		if(list.size() == 0 || curPage > list.size()) {
			return 0;
		} else {
			return list.get(curPage).get(0).begin;
		}
		
	}

	@Override
	public Vector<String> getLines() {
		// TODO Auto-generated method stub
		if(isRest) {
			reset(false);
		}
		
		if(lines.size() == 0) {
			if(list.size() > 0) {
				Vector<PagePoint> v = list.get(curPage); 
				for(PagePoint p : v) {
					//DebugLog.d(TAG, "begin: " + p.begin + ", size:" + p.size);
					if(p.size == -1){
						lines.add("/n");
					}
					else{
						String line = this.buf.substring(p.begin, p.begin + p.size);
						lines.add(line);
					}
				}
			}
		}
		
		return lines;
	}

	@Override
	public boolean pageUp() {
		// TODO Auto-generated method stub
		if(curPage <= 0) {
			return false;
		}
		curPage--;
		lines.clear();
		
		return true;
	}

	@Override
	public boolean pageDown() {
		// TODO Auto-generated method stub
		if(curPage + 1 >= list.size()) {
			return false;
		}
		curPage++;
		lines.clear();
		
		return true;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		list.clear();
		lines.clear();
		buf = null;
		DebugLog.d(TAG, "set buf to null");
		curPage = -1;
	}

	@Override
	public void pageFirst() {
		// TODO Auto-generated method stub
		curPage = 0;
	}

	@Override
	public void pageEnd() {
		// TODO Auto-generated method stub
		if(list.size() > 0) {
			curPage = list.size() - 1;
		} else {
			curPage = 0;
		}
	}
	
	@Override
	public void setPosition(int position) {
		// TODO Auto-generated method stub
		if(list.size() == 0) {
			return;
		}

		if(position < 0 || position >= this.buf.length()) {
			throw new RuntimeException("Page Position is out of Index, position:" + position + ", size:" + list.size());
		}

		for(int i = 0; i < list.size(); i++) {
			Vector<PagePoint> v = list.get(i);

			PagePoint first = v.get(0);
			PagePoint end = v.get(v.size() - 1);
			
			for(int j = v.size()-1;j>=0;j--){
				PagePoint epoint = v.get(j);
				if(epoint.begin != -1 && epoint.size != -1){
					end = epoint;
					break;
				}
			}

			if(position >= first.begin && position < end.begin + end.size) {
				curPage = i;
				return ;
			}
		}
		
		DebugLog.d(TAG, "this position is error, set curPage to first");
		
		curPage = 0;
	}

	@Override
	public int getCurPage() {
		// TODO Auto-generated method stub
		return curPage;
	}

	@Override
	public void setPage(int pagePos) {
		// TODO Auto-generated method stub
		if(pagePos < 0 || pagePos >= list.size()) {
			return;
		}
        
		curPage = pagePos;
		lines.clear();
	}

	@Override
	public Vector<String> getChLines() {
		// TODO Auto-generated method stub
		return chLines;
	}

}
