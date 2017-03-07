package com.boyiqove.task;

import java.io.FileInputStream;
import java.io.ObjectInputStream;


public class InputObjectTask extends CallBackTask {

	private String filePath;

	public InputObjectTask(String strTaskName, String filePath) {
		super(strTaskName);
		// TODO Auto-generated constructor stub
		this.filePath = filePath;
	}

	@Override
	protected void doTask() {
		// TODO Auto-generated method stub
		try {
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);

			sendMessage(CallBackMsg.INPUT_OBJECT_COMPLETED, ois.readObject());

			ois.close();

			fis.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendMessage(CallBackMsg.INPUT_OBJECT_ERROR);
		}

	}

}
