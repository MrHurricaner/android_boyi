package com.boyiqove.ui.bookshelf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.ResultCode;
import com.boyiqove.adapter.LocalContentAdapter;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.LocalChapterInfo;
import com.boyiqove.task.CallBackMsg;
import com.boyiqove.task.CheckContentsTask;
import com.boyiqove.task.InputObjectTask;
import com.boyiqove.task.OutputObjectTask;
import com.boyiqove.view.BaseActivity;


/*
 * 该视图不再使用， 对于没有目录的txt，不需要支持目录功能
 * 
 */
public class LocalContentsActivity extends BaseActivity {
    private final static String TAG = "LocalContentsActivity";

    private Button 					mCheckBtn;
    
    private BookItem 				mBookItem;
    
    private LocalContentAdapter 	mContentsAdapter;
    
    private Handler mCallBack  = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch(msg.what) {
			case CallBackMsg.CHCEK_CONTENTS_COMPLETED:
			{
				ArrayList<LocalChapterInfo> chList = (ArrayList<LocalChapterInfo>)msg.obj;
				if(null == chList || chList.size() == 0) {
					showToast("没有找到章节信息", Toast.LENGTH_SHORT);
				} else {
					mCheckBtn.setVisibility(View.GONE);
					mContentsAdapter.setData(chList, 0);
					
					//saveContentsToFile(list);
					OutputObjectTask task = new OutputObjectTask("saveContents", chList, AppData.getConfig().getLocalContentsFilePath(mBookItem.id));
					AppData.getClient().getTaskManager().addTask(task);
				}

				hideProgress();
				
			}
				break;
				
			case CallBackMsg.INPUT_OBJECT_COMPLETED:
			{
				List<LocalChapterInfo> list = (List<LocalChapterInfo>)msg.obj;
				if(list.size() == 0) {
					showToast("没有章节信息", Toast.LENGTH_SHORT);
					mCheckBtn.setVisibility(View.VISIBLE);
				} else {
					mCheckBtn.setVisibility(View.GONE);
					mContentsAdapter.setData(list, 0);
				}
			}
				break;
				
			case CallBackMsg.INPUT_OBJECT_ERROR:
			{
				showToast("章节信息读取出错", Toast.LENGTH_SHORT);
				mCheckBtn.setVisibility(View.VISIBLE);
			}
				break;
			}
		}
		
	};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        
		setContentView(R.layout.boyi_local_content);
        
		initData();
        initView();
	}
    
    
	private void initData() {
		AppData.getClient().setCallBackHander(mCallBack);
		mBookItem = (BookItem)getIntent().getSerializableExtra("BookItem");
	}
    
	private void initView() {
		TextView tvBookName = (TextView)this.findViewById(R.id.content_bookname_tv);
        TextView tvAuthor = (TextView)this.findViewById(R.id.content_author_tv);
        TextView tvSort = (TextView)this.findViewById(R.id.content_sort_tv);
        
        tvBookName.setText(mBookItem.name);
        tvAuthor.setText("作者：未知");
        
        tvSort.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
        
        mCheckBtn = (Button)this.findViewById(R.id.content_check_btn);
        mCheckBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showProgress("", "...loading...");
				CheckContentsTask task = new CheckContentsTask("checkContent", mBookItem.path);
				AppData.getClient().getTaskManager().addTask(task);
			}
		});
        
        
        ListView listView = (ListView)this.findViewById(R.id.content_listview);
    	// 1. 从本地文件中读取目录信息
		// 判断目录是否已经生成
		String filePath = AppData.getConfig().getLocalContentsFilePath(mBookItem.id);
		File listFile = new File(filePath);
		if(listFile.isFile()) {
			mCheckBtn.setVisibility(View.GONE);
			//readContentsFromFile(filePath);
			InputObjectTask task = new InputObjectTask("readContents", filePath);
			AppData.getClient().getTaskManager().addTask(task);
		}
		List<LocalChapterInfo> list = new ArrayList<LocalChapterInfo>();
		mContentsAdapter = new LocalContentAdapter(this, list, mBookItem.lastChapterPos);
		listView.setAdapter(mContentsAdapter);
        
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int filePos = mContentsAdapter.getItem(position).start;
				Intent data = new Intent();
				data.putExtra("position", filePos);
				setResult(ResultCode.JUMP_TO_POSITION, data);
				finish();
			}
		});
	}
	
    
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
        
		AppData.getClient().setNullCallBackHander(mCallBack);
	}

}
