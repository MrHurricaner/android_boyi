package com.boyiqove.task;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class OutputObjectTask extends Task {
	
	private Object objOut;
	private String filePath;

	public OutputObjectTask(String strTaskName, Object obj, String filePath) {
		super(strTaskName);
		// TODO Auto-generated constructor stub
		this.objOut = obj;
		this.filePath = filePath;
	}

	@Override
	public void cancelTask() {
		// TODO Auto-generated method stub
		super.cancelTask();
	}

	@Override
	protected void doTask() {
		// TODO Auto-generated method stub
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(objOut);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
