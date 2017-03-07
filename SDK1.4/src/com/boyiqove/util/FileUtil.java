package com.boyiqove.util;

import java.io.File;
import java.io.FileInputStream;



public class FileUtil {
	
	/*
	 * 判断文本文件的编码格式 }
	 */

	public static String getFileEncoding(String filename) throws Exception {
		FileInputStream fis = new FileInputStream(filename);
		byte buffer[] = new byte[1024];
		fis.read(buffer);
		
		String encode = EncodeDetector.getEncoding(buffer);
		fis.close();
		
		DebugLog.d("encoding", filename + ":" + encode);

		return encode;
	}

	/*
	 * 获取字符串首字符汉语拼音
	 */
	public static String getHanyuPinyinString(String name) {
//		String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(name
//				.charAt(0));
//		if (null == pinyinArray) {
//			return name;
//		} else {
//			return pinyinArray[0].substring(0, 1);
//		}
			return "";
	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * 
	 * @param dir
	 *            将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful. If a
	 *         deletion fails, the method stops attempting to delete and returns
	 *         "false".
	 */
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
    
	
                
}
