package com.boyiqove.library.book;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Vector;

import com.boyiqove.entity.LocalChapterInfo;
import com.boyiqove.util.FileUtil;

public class TxtBookCache{
    private final static String TAG = "TxtBook";
    
    private RandomAccessFile	RAFile;
	private MappedByteBuffer 	mbBuffer;

	private int bufLen;
	//private int bufBegin; 		// 当前页的开始位置
	//private int bufEnd;			// 当前页的结束位置
    
	private String 	m_strCharsetName = "GBK";
    
	private Vector<String> lines = new Vector<String>();
    
    private int chapterPos = 0;
    private int position = 0;
	private List<LocalChapterInfo> contentList;
	
	public TxtBookCache(String filePath, List<LocalChapterInfo> list) {
        
        contentList = list;
        
		try{
			m_strCharsetName = FileUtil.getFileEncoding(filePath);
			if(m_strCharsetName == null) {
				//throw new Exception("无法解析的文本编码格式");
				m_strCharsetName = "utf-8";
			}

			File book_file = new File(filePath);
			long lLen = book_file.length();
			bufLen = (int) lLen;
			RAFile = new RandomAccessFile(book_file, "r");
			mbBuffer = RAFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, lLen);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    
    
	public void close() {
		try {
			RAFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
	

	
//	// 读取上一段落 
//	protected byte[] readParagraphBack(int nFromPos, int nToMax) {
//		int nEnd = nFromPos;
//		int i;
//		byte b0, b1;
//		if (m_strCharsetName.equals("UTF-16LE")) {
//			i = nEnd - 2;
//			while (i > nToMax) {
//				b0 = mbBuffer.get(i);
//				b1 = mbBuffer.get(i + 1);
//				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
//					i += 2;
//					break;
//				}
//				i--;
//			}
//
//		} else if (m_strCharsetName.equals("UTF-16BE")) {
//			i = nEnd - 2;
//			while (i > nToMax) {
//				b0 = mbBuffer.get(i);
//				b1 = mbBuffer.get(i + 1);
//				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
//					i += 2;
//					break;
//				}
//				i--;
//			}
//		} else {
//			i = nEnd - 1;
//			while (i > nToMax) {
//				b0 = mbBuffer.get(i);
//				if (b0 == 0x0a && i != nEnd - 1) {
//					i++;
//					break;
//				}
//				i--;
//			}
//		}
//		if (i < nToMax)
//			i = nToMax;
//		int nParaSize = nEnd - i;
//		int j;
//		byte[] buf = new byte[nParaSize];
//		for (j = 0; j < nParaSize; j++) {
//			buf[j] = mbBuffer.get(i + j);
//		}
//		return buf;
//	}
//
//
//	// 读取下一段
//	protected byte[] readParagraphForward(int nFromPos, int nToMax) {
//		int nStart = nFromPos;
//		int i = nStart;
//		byte b0, b1;
//		// 根据编码格式判断换行
//		if (m_strCharsetName.equals("UTF-16LE")) {
//			while (i < nToMax - 1) {
//				b0 = mbBuffer.get(i++);
//				b1 = mbBuffer.get(i++);
//				if (b0 == 0x0a && b1 == 0x00) {
//					break;
//				}
//			}
//		} else if (m_strCharsetName.equals("UTF-16BE")) {
//			while (i < nToMax - 1) {
//				b0 = mbBuffer.get(i++);
//				b1 = mbBuffer.get(i++);
//				if (b0 == 0x00 && b1 == 0x0a) {
//					break;
//				}
//			}
//		} else {
//			while (i < nToMax) {
//				b0 = mbBuffer.get(i++);
//				if (b0 == 0x0a) {
//					break;
//				}
//			}
//		}
//        
//		int nParaSize = i - nStart;
//		byte[] buf = new byte[nParaSize];
//		for (i = 0; i < nParaSize; i++) {
//			buf[i] = mbBuffer.get(nFromPos + i);
//		}
//		return buf;
//	}

}
