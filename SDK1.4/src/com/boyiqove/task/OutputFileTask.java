package com.boyiqove.task;

import java.io.FileOutputStream;

import com.boyiqove.util.AES;
import com.boyiqove.util.DebugLog;

public class OutputFileTask extends Task {
	
	private String data;
	private String filePath;
	public OutputFileTask(String strTaskName, String data, String filePath) {
		super(strTaskName);
		// TODO Auto-generated constructor stub
		this.data = data;
		this.filePath = filePath;
	}

	@Override
	protected void doTask() {
		// TODO Auto-generated method stub
		synchronized (data) {
			try {
				FileOutputStream fos = new FileOutputStream(filePath);
				String enc = AES.encrypt(data.getBytes(), "utf-8");
				fos.write(enc.getBytes());
				DebugLog.e("该章已经缓存到本地",enc.substring(0, 20));
				
				fos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
