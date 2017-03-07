package com.boyiqove.task;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.boyiqove.util.DebugLog;

public class TaskManager {
	private ExecutorService 		mThreadPool;
	private HashMap<String, Task> 	mMapTask;
	
	private static final int THREAD_COUNT = 10;
	
	synchronized public void init(int nThreadNum) {
		if(1 == nThreadNum) {
			mThreadPool = Executors.newSingleThreadExecutor();
			
		} else if(-1 == nThreadNum) {
			mThreadPool = Executors.newCachedThreadPool();
			
		} else if(0 == nThreadNum) {
			mThreadPool = Executors.newFixedThreadPool(THREAD_COUNT);
			
		} else {
			mThreadPool = Executors.newFixedThreadPool(nThreadNum);
		}
		
		mMapTask = new HashMap<String, Task>();
		
	}
	
	synchronized public void shutdown() {
		mThreadPool.shutdown();
		for(Task task : mMapTask.values()) {
			if(null != task) {
				task.cancelTask();
			}
		}
		mMapTask.clear();
	}
	
	synchronized public boolean addTask(Task task) {
		if(mThreadPool.isShutdown()) {
			return false;
		}
		
		if(null != mMapTask.get(task.getTaskName())) {
			return false;
		}
		
		task.setTaskManager(this);
		mMapTask.put(task.getTaskName(), task);
		mThreadPool.execute(task);
		
		return true;
	}
	
	synchronized public Task findTask(String strTaskName) {
		return mMapTask.get(strTaskName);
	}
	
	synchronized public void delTask(String strTaskName) {
		
		
		Task task = mMapTask.get(strTaskName);
		if(null != task) {
			DebugLog.e("删除了线程", task.getTaskName());
			task.cancelTask();
			mMapTask.remove(strTaskName);
		}
	}
	
	synchronized public void delAllTask() {
		for(Task task : mMapTask.values()) {
			if(null != task) {
				task.cancelTask();
			}
		}
		
		mMapTask.clear();
	}
	

}
