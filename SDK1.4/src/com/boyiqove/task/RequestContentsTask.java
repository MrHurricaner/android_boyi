package com.boyiqove.task;

import com.boyiqove.library.volley.RequestQueue;

public class RequestContentsTask extends CallBackTask {
	
    private RequestQueue queue;
    private int onlineID;

	public RequestContentsTask(String strTaskName,RequestQueue queue, int onlineID) {
		super(strTaskName);
		// TODO Auto-generated constructor stub
        this.queue = queue;
        this.onlineID = onlineID;
	}

	@Override
	protected void doTask() {
		// TODO Auto-generated method stub

	}

}
