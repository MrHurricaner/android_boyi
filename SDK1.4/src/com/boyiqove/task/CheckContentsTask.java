package com.boyiqove.task;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.boyiqove.entity.LocalChapterInfo;
import com.boyiqove.util.BoyerMoore;
import com.boyiqove.util.DebugLog;
import com.boyiqove.util.FileUtil;


/*
 * 根据关键字搜索后，利用正则表达式检查结果,来生成章节目录
 * 
 */

public class CheckContentsTask extends CallBackTask{
	private final static String TAG = "CheckContents";

	// 第:  0x7b2c,  章 : 0x7ae0

	//  [\u7b2c][\s]*[0-9]{1,}[\s]*[\u7ae0]  匹配  (第0-9章)

	// 匹配0-9|零-
	// [0-9]+|[\u96F6,\u4E00,\u4E8C,\u4E09,\u56DB,\u4E94,\u516D,\u4E03,\u516B,\u4E5D,\u5341,\u767E,\u5343]+

	//private Pattern pattern = Pattern.compile("[\\u7b2c][\\s]*[0-9]{1,}[\\s]*[\\u7ae0]");
	//private Pattern pattern = Pattern.compile("[0-9]+|[\\u96F6,\\u4E00,\\u4E8C,\\u4E09,\\u56DB,\\u4E94,\\u516D,\\u4E03,\\u516B,\\u4E5D,\\u5341,\\u767E,\\u5343]+");
	private String regex = "[0-9]+|[\\u96F6,\\u4E00,\\u4E8C,\\u4E09,\\u56DB,\\u4E94,\\u516D,\\u4E03,\\u516B,\\u4E5D,\\u5341,\\u767E,\\u5343]+";

	private String mFilePath;
	private MappedByteBuffer 	mFileBuffer;
	private int mLen;

	private String mCharset;

	private final static int BASE_STEP = 4096;

	private byte[] key_start;
	//private byte[] key_middle;
	private byte[] key_end;

	private ArrayList<LocalChapterInfo> list = new ArrayList<LocalChapterInfo>();
	
	//private int chapterID;

	public CheckContentsTask(String strTaskName, String filePath) {
		super(strTaskName);
		// TODO Auto-generated constructor stub
		mFilePath = filePath;
	}

	@Override
	protected void doTask() {
		// TODO Auto-generated method stub
		DebugLog.i(TAG, "start checking...");
		long start = System.currentTimeMillis();
		
		//chapterID = 0;

		try {
			// 1. 打开文件，获取基本信息
			mCharset = FileUtil.getFileEncoding(mFilePath);
            if(null == mCharset) {
            	mCharset = "utf-8";
            }
            
			initKey();
			
			File file = new File(mFilePath);
			mLen = (int)file.length();
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			mFileBuffer = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, mLen);

			// 2. 扫描文件
			list.clear();
			int offset = 0;
			int left = mLen;
			int end = 0;
			int step = BASE_STEP;

			while(left > 0) {
				if(left > step) {
					end  = getEndOfStep(offset + step);
				} else {
					end = offset + left;
				}

				parse(offset, end - offset);

				left -= (end - offset);

				offset = end;
			}

			raf.close();

			if(list.size() > 0) {
				if(list.size() == 1 ) {
					list.get(0).size = mLen;
				} else {
					for(int i = 1; i < list.size(); i++) {
						list.get(i - 1).size = list.get(i).start - list.get(i - 1).start;
					}
					list.get(list.size() - 1).size = mLen - list.get(list.size() - 1).start;
				}
			}
            
			if(list.size() > 0) {
				LocalChapterInfo first = list.get(0);
				if(first.start != 0) {
					LocalChapterInfo begin = new LocalChapterInfo();
					begin.name = "开始";
					begin.start = 0;
					begin.size = first.start;
                    
					list.add(0, begin);
				} 
			}

			sendMessage(CallBackMsg.CHCEK_CONTENTS_COMPLETED, list);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendMessage(CallBackMsg.CHCEK_CONTENTS_COMPLETED, null);
		}

		long used = System.currentTimeMillis() - start;
		DebugLog.i(TAG, "check end, used:" + used);
	}

	private void initKey() throws UnsupportedEncodingException {
		String strKeyStart = "第";
		String strKeyEnd = "章";
		//String strKeyMiddle = "01234567879零一二三四五六七八九十百千";

		key_end = strKeyEnd.getBytes(mCharset);
		key_start = strKeyStart.getBytes(mCharset);
		//key_middle = strKeyMiddle.getBytes(mCharset);
	}


	private int getEndOfStep(int position) {
		int pos = position;
		if(pos < 0 || pos >= mLen) {
			throw new RuntimeException("\nerror position:" + position);
		}

		byte b0, b1;
		// 根据编码格式找到换行
		if (mCharset.equals("UTF-16LE")) {
			while (pos < mLen - 1) {
				b0 = mFileBuffer.get(pos++);
				b1 = mFileBuffer.get(pos++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (mCharset.equals("UTF-16BE")) {
			while (pos < mLen - 1) {
				b0 = mFileBuffer.get(pos++);
				b1 = mFileBuffer.get(pos++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (pos < mLen) {
				b0 = mFileBuffer.get(pos++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}

		return pos;
	}

	private int getEndOfLine(int position) {
		int pos = position;
		if(pos < 0 || pos >= mLen) {
			throw new RuntimeException("\nerror position:" + position);
		}

		byte b0, b1;
		// 根据编码格式找到换行
		if (mCharset.equals("UTF-16LE")) {
			while (pos < mLen - 1) {
				b0 = mFileBuffer.get(pos++);
				b1 = mFileBuffer.get(pos++);
				if (b0 == 0x0a && b1 == 0x00) {
					pos -= 2;
					break;
				}
			}
		} else if (mCharset.equals("UTF-16BE")) {
			while (pos < mLen - 1) {
				b0 = mFileBuffer.get(pos++);
				b1 = mFileBuffer.get(pos++);
				if (b0 == 0x00 && b1 == 0x0a) {
					pos -= 2;
					break;
				}
			}
		} else {
			while (pos < mLen) {
				b0 = mFileBuffer.get(pos++);
				if (b0 == 0x0a) {
					pos -= 1;
					break;
				}
			}
		}

		return pos;
	}

	private void parse(int offset, int len) {
		byte[] dst = new byte[len];
		for(int i = 0; i < len; i++) {
			dst[i] = mFileBuffer.get(offset + i);
		}

		List<Integer> matches2 = BoyerMoore.match(key_start, dst);

		for(Integer i : matches2) {
			// 过滤不符合条件的项
			checkKey(offset + i);
		}
	}

	/*
	 * 成功则返回结束位置，  失败则返回-1 
	 */
	private boolean checkKey(int position) {
		// 1.找到段落尾
		int end = getEndOfLine(position);

		byte[] dst = new byte[end - position];
		for(int i = position; i < end; i++) {
			dst[i - position] = mFileBuffer.get(i);
		}

		int endPos = BoyerMoore.find(key_end, dst);
		if(-1 != endPos) {
			// 检测中间部分是否符合要求---- 可采用正则表达式匹配
			byte[] middle = new byte[endPos - key_start.length];
			System.arraycopy(dst, key_start.length, middle, 0, middle.length);

			try {
				String strMiddle = new String(middle, mCharset);
				strMiddle.replace(" ", "");
				if(strMiddle.matches(regex)) {
					// 匹配成功

					//String num = new String(dst, 0, endPos + key_end.length, mCharset);
					String name = new String(dst, mCharset);

					LocalChapterInfo item = new LocalChapterInfo();
					item.name = name;
					item.start = position;

					list.add(item);

					//DebugLog.i(TAG,"position:" + position + "->" + num + ":" + name);

					return true;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;
	}
}
