package com.boyiqove.library.book;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import taobe.tec.jcc.JChineseConvertor;

import com.boyiqove.AppData;
import com.boyiqove.config.ReadConfig;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.FileUtil;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class LocalBookCache2 {
	private final static String TAG = "LocalBookCache";
    
    public interface OnDrawListener {
        public String getBookName();
    }
	
	private RandomAccessFile	mRAFile;
	private MappedByteBuffer 	m_mbBuf;

	private int m_mbBufLen = 0;
	private int m_mbBufBegin = 0;
	private int m_mbBufEnd = 0;
	
	private Vector<String> m_lines = new Vector<String>();

	private String 	m_strCharsetName = "GBK";
	
	private ReadConfig 	config;
    
	private boolean m_isfirstPage,m_islastPage;
    
	private String bookName;

	public LocalBookCache2() {
		// TODO Auto-generated constructor stub
		config = AppData.getConfig().getReadConfig();
	}

	public void openbook(String strFilePath) throws Exception {
		m_strCharsetName = FileUtil.getFileEncoding(strFilePath);
        if(m_strCharsetName == null) {
            //throw new Exception("无法解析的文本编码格式");
        	DebugLog.d(TAG, "无法解析的文本编码格式, 用utf-8代替");
            m_strCharsetName = "utf-8";
        }
        DebugLog.d(TAG, strFilePath + ", charset:" + m_strCharsetName);
		
		File book_file = new File(strFilePath);
		long lLen = book_file.length();
		m_mbBufLen = (int) lLen;
		mRAFile = new RandomAccessFile(book_file, "r");
		m_mbBuf = mRAFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, lLen);
        
		int index = strFilePath.lastIndexOf("/");
        if(index != -1) {
        	bookName = strFilePath.substring(index + 1, strFilePath.length());
        } else {
        	bookName = strFilePath;
        }
	}
	
	public void closebook() {
		if(null != mRAFile) {
			try {
				mRAFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	// 读取上一段落 
	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}

		} else if (m_strCharsetName.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = m_mbBuf.get(i + j);
		}
		return buf;
	}


	// 读取下一段
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// 根据编码格式判断换行
		if (m_strCharsetName.equals("UTF-16LE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < m_mbBufLen) {
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		return buf;
	}

	protected Vector<String> pageDown() {
		//DebugLog.d(TAG, "pageDown");
		
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < config.getLineCount() && m_mbBufEnd < m_mbBufLen) {
			byte[] paraBuf = readParagraphForward(m_mbBufEnd); // 读取一个段落
			m_mbBufEnd += paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
                
                //DebugLog.d(TAG, m_strCharsetName + ",par:" + strParagraph);
                
                if(config.isSimpleChinese()) {
                    try {
						strParagraph = JChineseConvertor.getInstance().t2s(strParagraph);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                } else {
                    try {
						strParagraph = JChineseConvertor.getInstance().s2t(strParagraph);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = config.getTextPaint().breakText(strParagraph, true, config.getVisibleWidth(), null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
				if (lines.size() >= config.getLineCount()) {
					break;
				}
			}
			if (strParagraph.length() != 0) {
				try {
					m_mbBufEnd -= (strParagraph + strReturn).getBytes(m_strCharsetName).length;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	protected void pageUp() {
		if (m_mbBufBegin < 0)
			m_mbBufBegin = 0;
		
		//DebugLog.d(TAG, "pageUp");
		
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < config.getLineCount() && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			m_mbBufBegin -= paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = config.getTextPaint().breakText(strParagraph, true, config.getVisibleWidth(), null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > config.getLineCount()) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_mbBufEnd = m_mbBufBegin;
		return;
	}

	public void prePage() throws IOException {
		if (m_mbBufBegin <= 0) {
			m_mbBufBegin = 0;
			m_isfirstPage=true;
			return;
		}else m_isfirstPage=false;
		m_lines.clear();
		pageUp();
		m_lines = pageDown();
	}

	public void nextPage() throws IOException {
		if (m_mbBufEnd >= m_mbBufLen) {
			m_islastPage=true;
			return;
		} else {
			m_islastPage=false;
		}
		m_lines.clear();
		m_mbBufBegin = m_mbBufEnd;
		m_lines = pageDown();
	}

	public void draw(Canvas canvas) {
		// 1. 获取文本内容
		if (m_lines.size() == 0)
			m_lines = pageDown();

		// 2. 绘制文本内容
		if (m_lines.size() > 0) {
			if (config.getBackBitmap() == null) {
				canvas.drawColor(config.getBackColor());
			} else {
				canvas.drawBitmap(config.getBackBitmap(), 0, 0, null);
			}
			
			float y = config.getMarginHeight();
			for (String strLine : m_lines) {
				y += (config.getTextHeight(config.getTextPaint()) + config.getLineSpacing());
				canvas.drawText(strLine, config.getMarginWidth(), y - config.getLineSpacing(), config.getTextPaint());
			}
		}

		// 3. 绘制头部
		drawHead(canvas);


		// 4. 绘制底部
		drawFoot(canvas);
		
	}
	
	private void drawHead(Canvas canvas) {
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
		canvas.drawText(strTime, width - nTimeWidth - 5, textHeight, paint);
		
        
		// 2. 电量
		int batteryTailWidth = 3;
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		
		float marginTime = 10;
        float left = width - nTimeWidth - 5 - marginTime - batteryWidth - batteryTailWidth;
        float top = textHeight - batteryHeight;
		
		canvas.drawRect(left, top, left + batteryWidth, top + batteryHeight, paint);
		canvas.drawRect(left + batteryWidth, top + batteryHeight/4, left + batteryWidth + batteryTailWidth, top + batteryHeight/4*3, paint);
		paint.setStyle(Paint.Style.FILL);
		int gap = 2;
		canvas.drawRect(left + gap, top + gap, left + gap + (batteryWidth - 2*gap) * config.getBatterPercent(), top + batteryHeight - gap, paint);

        
		canvas.drawText(bookName, marginWidth, textHeight, paint);
	}
	
	private void drawFoot(Canvas canvas) {
		Paint paint = config.getExtraPaint();
		float height = config.getHeight();
		float width = config.getWidth();
        
		// 3. 进度
		float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = df.format(fPercent * 100) + "%";
		int nPercentWidth = (int) paint.measureText("999.9%") + 1;
		canvas.drawText(strPercent, width - nPercentWidth, height - 5, paint);
	}
	
	public void setPercent(float fPercent) {
		if(fPercent < 0.0f || fPercent > 1.0f) {
			throw new RuntimeException("book percent is out of file lenght");
		}

		int position = (int)(fPercent * m_mbBufLen);
		position = position - readParagraphBack(position).length;
		
		m_mbBufBegin =  position;
		m_mbBufEnd = m_mbBufBegin;
		m_lines.clear();
	}

	public void setPosition(int position) {
		if(position < 0 || position >= m_mbBufLen) {
			throw new RuntimeException("book position("+ position + ") is out of file lenght");
		}

		m_mbBufBegin = position;
		
		reset();
	}

	public boolean isfirstPage() {
		return m_isfirstPage;
	}

	public boolean islastPage() {
		return m_islastPage;
	}

	public float getPercent() {
		float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
		return fPercent;
	}
	
	public void reset() {
		m_mbBufEnd = m_mbBufBegin;
		m_lines.clear();
	}
	
	public int getBegin() {
		return m_mbBufBegin;
	}
	
	public Vector<String> getLines() {
		return m_lines;
	}
	
	public int getFileLen() {
		return m_mbBufLen;
	}
}