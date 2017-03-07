package com.boyiqove.ui.bookshelf;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.boyiqove.AppData;
import com.boyiqove.R;
import com.boyiqove.ResultCode;
import com.boyiqove.config.ReadConfig;
import com.boyiqove.entity.BookItem;
import com.boyiqove.entity.LocalChapterInfo;
import com.boyiqove.entity.PageID;
import com.boyiqove.library.book.LocalBookCache2;
import com.boyiqove.library.book.PageWidget;
import com.boyiqove.library.book.PageWidget.OnSizeChangedListener;
import com.boyiqove.task.CallBackMsg;
import com.boyiqove.util.DebugLog;
import com.boyiqove.view.BaseActivity;
//import com.boyireader.MainActivity;

public class LocalReadingActivity extends BaseActivity {
    private final static String TAG = "LocalReadingActivity";

	private PageWidget mPageWidget;

	private Bitmap 	mCurPageBitmap;
	private Bitmap 	mNextPageBitmap;

	private Canvas 	mCurPageCanvas;
	private Canvas 	mNextPageCanvas;

	private int mWidth, mHeight;

	private BookItem 						mBookItem;
    private ArrayList<LocalChapterInfo>  	mContentsList;
    
	private LocalBookCache2 	mBookCache;
	private ReadConfig 			mReadConfig;
    
    
	private Handler mCallBack = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
            
			switch(msg.what) {
			case CallBackMsg.INPUT_OBJECT_COMPLETED:
                mContentsList = (ArrayList<LocalChapterInfo>)msg.obj;
                if(mContentsList.size() == 0) {
                    DebugLog.e(TAG, "没有章节信息");
                } else {
                    
                }
                
				break;
			case CallBackMsg.INPUT_OBJECT_ERROR:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		initData();
		
		initReadPage();

		initReadListener();

	}
	
	private void initData() {
		mBookItem = (BookItem) getIntent().getSerializableExtra("BookItem");
		mReadConfig = AppData.getConfig().getReadConfig();
	}

	private void initReadPage() {
		loadReadSetting();
		
		LinearLayout.LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		mPageWidget = new PageWidget(this);
		mPageWidget.setLayoutParams(lp);
		
		setContentView(mPageWidget);

		mCurPageCanvas = new Canvas();
		mNextPageCanvas = new Canvas();

		mBookCache = new LocalBookCache2();
		
		registerReceiver(mBatteryChangedReeciver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		try {
			mBookCache.openbook(mBookItem.path);
			mBookCache.setPosition((int)mBookItem.lastPosition);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			showToast( mBookItem.path + "," + e1.getMessage() , Toast.LENGTH_LONG);

			Message msg = Message.obtain();
			msg.what = ResultCode.OPEN_BOOK_FAILED;
			Intent intent = new Intent();
			intent.putExtra("message", msg);

			setResult(PageID.Bookshelf, intent);
			finish();
			overridePendingTransition(R.anim.boyi_move_left_in, R.anim.boyi_move_left_out);
		}

	}
	
	private void loadReadSetting() {
//        // 1.横竖屏
//		if(mReadConfig.isPortrait()) {
//			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		} else {
//			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		}
		
		// 2.亮度
		if(!mReadConfig.isSysBrightness()) {
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.screenBrightness = Float.valueOf(mReadConfig.getReadBrightness()) * (1f / 255f);
			getWindow().setAttributes(lp);
		}

	}
	
	private BroadcastReceiver mBatteryChangedReeciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
                level = level > scale ? scale : level;
				float percent = level * 1.0f / scale;
				mReadConfig.setBatteryPercent(percent);
			}
			
		}
		
	};

	private void initReadListener() {
		mPageWidget.setOnSizeChangedListener(new OnPageSizeChangedListener());

		mPageWidget.setOnTouchListener(new OnPageTouchListener());
	}

	private class OnPageSizeChangedListener implements OnSizeChangedListener {
		@Override
		public void onSizeChanged(int w, int h, int oldw, int oldh) {
			// TODO Auto-generated method stub

			mWidth = w;
			mHeight = h;

			if(null != mCurPageBitmap) {
				mCurPageBitmap.recycle();
				mCurPageBitmap = null;
			} 
			if(null != mNextPageBitmap) {
				mNextPageBitmap.recycle();
				mNextPageBitmap = null;
			}

			mCurPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
			mNextPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

			mCurPageCanvas.setBitmap(mCurPageBitmap);
			mNextPageCanvas.setBitmap(mNextPageBitmap);

			mReadConfig.setSize(mWidth, mHeight);
            
			mBookCache.setPosition(mBookCache.getBegin());
			mBookCache.draw(mCurPageCanvas);

			mPageWidget.setScrolling(false);
			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
		}

	};


	private boolean mAlwaysInTapRegion = false;
	private float mCurrentDownMotionX = 0.0f;
	private float mCurrentDownMotionY = 0.0f;
	private class OnPageTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			boolean bRet = false;
			do
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					mAlwaysInTapRegion = true;
					mCurrentDownMotionX = event.getX();
					mCurrentDownMotionY = event.getY();

					mPageWidget.abortAnimation();
					mPageWidget.calcCornerXY(event.getX(), event.getY());

					mBookCache.draw(mCurPageCanvas);
					
					if(isTouchInPopupRect(event.getX(), event.getY())) {
						bRet = true;
						break;
					}
					
					if(mPageWidget.DragToRight()) {
						try {
							mBookCache.prePage();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(mBookCache.isfirstPage()) {
							mPageWidget.setScrolling(false);
							showToast("已经是第一页了", Toast.LENGTH_SHORT);

							break;
						}

					} else {
						try {
							mBookCache.nextPage();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(mBookCache.islastPage()) {
							mPageWidget.setScrolling(false);
							showToast("已经是最后一页了", Toast.LENGTH_SHORT);
							
							break;
						}
					}

					mBookCache.draw(mNextPageCanvas);
					mPageWidget.setScrolling(true);

					mPageWidget.doInternalTouchDown(event);

				} else if(event.getAction() == MotionEvent.ACTION_MOVE){

					if(mAlwaysInTapRegion){
						final int deltaX = (int) (event.getX() - mCurrentDownMotionX);
						final int deltaY = (int) (event.getY() - mCurrentDownMotionY);
						int distance = (deltaX * deltaX) + (deltaY * deltaY);
						if(distance > 20) {
							mAlwaysInTapRegion = false;
						}
					}


					mPageWidget.doInternalTouchMove(event);

				} else if(event.getAction() == MotionEvent.ACTION_UP) {
					if(mAlwaysInTapRegion){
						if(isTouchInPopupRect(event.getX(), event.getY())) {
							mPageWidget.setScrolling(false);

							popupReadActionWindow();
                            
							//mReadActionView.setVisibility(View.VISIBLE);

							break;
						}
					}

					mPageWidget.doInternalTouchUp(event);
				}

				bRet = true;

			}while(false);


			return bRet;
		}

	};

	private boolean isTouchInPopupRect(float x, float y) {
		int offset = mWidth/4;
		if(x < mWidth/2 + offset && x > mWidth/2 - offset
				&& y < mHeight/2 + offset && y > mHeight/2 - offset) {
			return true;
		}

		return false;
	}
    
    
    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
        
		hideReadActionWindow();
	}
    

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        
		unregisterReceiver(mBatteryChangedReeciver);
	}
    
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
        goBack();
	}

	private void goBack() {
        mBookItem.lastChapterPos = mBookCache.getBegin();
        mBookItem.lastPosition = mBookCache.getBegin();
        mBookItem.chapterTotal = mBookCache.getFileLen();
        
		AppData.getDataHelper().updateLastReadLocal(mBookItem.id, mBookItem.lastChapterPos, 
											mBookItem.lastPosition, mBookItem.chapterTotal);
        
		AppData.getUser().setLastBookID(mBookItem.id);
        
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("BookItem", mBookItem);
//        
//        setResult(ResultCode.UPDATE_LASTREAD, intent);
        
        finish();
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		DebugLog.i(TAG, "onActivityResutl");

		if(ResultCode.JUMP_TO_POSITION == resultCode) {
			//跳转
			int position = data.getIntExtra("position", -1);
			DebugLog.i(TAG, "jump to:" + position);
			if(-1 != position) {
				mBookCache.setPosition(position);
				mBookCache.draw(mCurPageCanvas);
			}

		}
	}
    

	/********************************************************************************************************************/
    
	private PopupWindow popupReadActionWindow = null;
	private void popupReadActionWindow() {
        View v = getReadActionView();
		if(null == popupReadActionWindow) {
			popupReadActionWindow = new PopupWindow(v, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			popupReadActionWindow.setFocusable(true);
			popupReadActionWindow.setTouchable(true);
			popupReadActionWindow.setOutsideTouchable(true);
			popupReadActionWindow.setBackgroundDrawable(new BitmapDrawable());
		}

		popupReadActionWindow.showAtLocation(mPageWidget, Gravity.FILL, 0, 0);
	}
    
	private void hideReadActionWindow() {
        if(null != popupReadActionWindow) {
        	popupReadActionWindow.dismiss();
        }
	}
    
	
    private View readActionView = null;
    private SeekBar seekBarChapterProgress;
    private TextView tvPageProgress;
    private int lastPosition;
	private View getReadActionView() {
        lastPosition = mBookCache.getBegin();
        if(null == readActionView) {
        	readActionView = LayoutInflater.from(this).inflate(R.layout.boyi_read_action, null);
            readActionView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
                    hideReadActionWindow();
				}
			});
            
            final View progressView = readActionView.findViewById(R.id.read_progress_layout);
			final View setView = readActionView.findViewById(R.id.read_set_layout);
            progressView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
            setView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
            
            ImageView ivBack = (ImageView)readActionView.findViewById(R.id.read_back_ib);
            ivBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
                    goBack();
				}
			});
            
//            TextView tvAutoBuy = (TextView)readActionView.findViewById(R.id.read_auto_buy_tv);
//            tvAutoBuy.setVisibility(View.GONE);
            
            // 1.目录
//            TextView tvContents = (TextView)readActionView.findViewById(R.id.read_content_tv);
//            tvContents.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showToast("该书籍没有目录", Toast.LENGTH_LONG);
//				}
//			});
//            tvContents.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//                    Intent intent = new Intent(LocalReadingActivity.this, LocalContentsActivity.class);
//                    intent.putExtra("BookItem", mBookItem);
//					
//                    startActivityForResult(intent, ResultCode.JUMP_TO_POSITION);
//				}
//			});
            
            // 2. 进度
//            TextView tvProgress = (TextView)readActionView.findViewById(R.id.read_progress_tv);
//            tvProgress.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(progressView.getVisibility() == View.GONE) {
//                        progressView.setVisibility(View.VISIBLE);
//                        setView.setVisibility(View.GONE);
//                    } else {
//                        progressView.setVisibility(View.GONE);
//                        setView.setVisibility(View.VISIBLE);
//                    }
//				}
//			});
//				
            
            // 3.字体 
        	TextView tvFontSub = (TextView)readActionView.findViewById(R.id.read_font_sub_btn);
        	TextView tvFontAdd = (TextView)readActionView.findViewById(R.id.read_font_add_btn);
            
        	tvFontSub.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int size = mReadConfig.getTextSize();
					if( mReadConfig.setTextSize(--size)) {
                        
                        mBookCache.reset();
						mBookCache.draw(mCurPageCanvas);
						mPageWidget.postInvalidate();

					} else {
						showToast("已经是最小号字体了", Toast.LENGTH_SHORT);
					}
				}
			});
            
        	tvFontAdd.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int size = mReadConfig.getTextSize();
					if(mReadConfig.setTextSize(++size)) {
                        mBookCache.reset();
						mBookCache.draw(mCurPageCanvas);
						mPageWidget.postInvalidate();

					} else {

						showToast("已经是最大号字体了", Toast.LENGTH_SHORT);
					}
				}
			});
            
            // 4.亮度
        	SeekBar seekBar = (SeekBar)readActionView.findViewById(R.id.read_seekBar);
        	seekBar.setMax(255);
			seekBar.setProgress(mReadConfig.getReadBrightness());
            
        	seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

        		@Override
        		public void onStopTrackingTouch(SeekBar seekBar) {
        			// TODO Auto-generated method stub

        		}

        		@Override
        		public void onStartTrackingTouch(SeekBar seekBar) {
        			// TODO Auto-generated method stub
        		}

        		@Override
        		public void onProgressChanged(SeekBar seekBar, int progress,
        				boolean fromUser) {
        			// TODO Auto-generated method stub
        			if(progress > 5) {
        				WindowManager.LayoutParams lp = getWindow().getAttributes();
        				lp.screenBrightness = Float.valueOf(progress) * (1f / 255f);
        				getWindow().setAttributes(lp);
        				mReadConfig.setReadBrightness(progress);
        			}
        		}
        	});
            
        	
            
            
            // 5.阅读背景
//            final int bgRes[] = new int[]{R.drawable.boyi_ic_read_bg_0, R.drawable.boyi_ic_read_bg_1, R.drawable.boyi_ic_read_bg_2,R.drawable.boyi_ic_read_bg_3,R.drawable.boyi_ic_read_bg_night};
//            final int bgResSelected[] = new int[]{R.drawable.boyi_ic_read_bg_0_selected, R.drawable.boyi_ic_read_bg_1_selected, R.drawable.boyi_ic_read_bg_2_selected,R.drawable.boyi_ic_read_bg_3_selected,R.drawable.boyi_ic_read_bg_night_selected};
        	final int bgRes[] = new int[]{};
            final int bgResSelected[] = new int[]{};
			final List<HashMap<String, Integer>> list = new ArrayList<HashMap<String,Integer>>();
			for(int i = 0; i < bgRes.length; i++) {
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				if(mReadConfig.getColorIndex() == i) {
					map.put("readBg_ic", bgResSelected[i]);
				} else {
					map.put("readBg_ic", bgRes[i]);
				}
				list.add(map);
			}
			
			final SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.boyi_read_bg_item, new String[]{"readBg_ic"}, new int[]{R.id.read_bg_iv});
            
        	GridView gridView = (GridView)readActionView.findViewById(R.id.read_bg_gridview);
            gridView.setVisibility(View.VISIBLE);
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					int index = mReadConfig.getColorIndex();
                    
					if(index != position) {
						HashMap<String, Integer> map;
                        
						if(index >= 0 && index < bgRes.length) {
							map = list.get(index);
							map.put("readBg_ic", bgRes[index]);
						}
						map = list.get(position);
						map.put("readBg_ic", bgResSelected[position]);
						adapter.notifyDataSetChanged();

						mReadConfig.setColorIndex(position);
                        
						mBookCache.draw(mCurPageCanvas);
						mPageWidget.postInvalidate();
					}
					
				}
			});
            
            // 6.行间距
            final ImageView ivLinespacing[] = new ImageView[4];
            ivLinespacing[0] = (ImageView)readActionView.findViewById(R.id.read_linespacing0_iv);
            ivLinespacing[1] = (ImageView)readActionView.findViewById(R.id.read_linespacing1_iv);
            ivLinespacing[2] = (ImageView)readActionView.findViewById(R.id.read_linespacing2_iv);
            ivLinespacing[3] = (ImageView)readActionView.findViewById(R.id.read_linespacing3_iv);
            
            final int lineRes[] = new int[]{R.drawable.boyi_ic_linespacing0, R.drawable.boyi_ic_linespacing1, R.drawable.boyi_ic_linespacing2, R.drawable.boyi_ic_linespacing3};
            final int lineResSelected[] = new int[]{R.drawable.boyi_ic_linespacing0_selected, R.drawable.boyi_ic_linespacing1_selected, R.drawable.boyi_ic_linespacing2_selected, R.drawable.boyi_ic_linespacing3_selected};

            for(int i = 0; i < ivLinespacing.length; i++) {
                final int index = i;
            	ivLinespacing[index].setOnClickListener(new OnClickListener() {

            		@Override
            		public void onClick(View v) {
            			// TODO Auto-generated method stub
                        int last = mReadConfig.getLineSpacingIndex();
                        ivLinespacing[last].setImageResource(lineRes[last]);
                        ivLinespacing[index].setImageResource(lineResSelected[index]);
                        mReadConfig.setLineSpacingIndex(index);
                        
                        mBookCache.reset();
                        mBookCache.draw(mCurPageCanvas);
                        mPageWidget.postInvalidate();
                        
            		}
            	});
            }
            
            // 7.简体/繁体 切换
        	TextView tvChinese = (TextView)readActionView.findViewById(R.id.read_chinese_btn);
            tvChinese.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mReadConfig.setSimpleChinese();

					mBookCache.reset();
					mBookCache.draw(mCurPageCanvas);
					mPageWidget.postInvalidate();
				}
            });

            // 8.更多选项
            TextView tvMore = (TextView)readActionView.findViewById(R.id.read_more_btn);
            tvMore.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					Intent intent = new Intent(LocalReadingActivity.this, ReadSettingActivity.class);
//                    startActivity(intent);
				}
			});
            
            
            /////////////////////////////////////////////////////////////////////////////////////////
            // 进度跳转
            /////////////////////////////////////////////////////////////////////////////////////////
            
			tvPageProgress = (TextView)readActionView.findViewById(R.id.read_page_progress_tv);
            
            // 1.上一章
            TextView tvChapterUp = (TextView)readActionView.findViewById(R.id.read_chapter_up_btn);
            tvChapterUp.setVisibility(View.INVISIBLE);
            
            
            // 2. 下一章
            TextView tvChapterDown = (TextView)readActionView.findViewById(R.id.read_chapter_down_btn);
            tvChapterDown.setVisibility(View.INVISIBLE);
            
            
            // 3. 上一页
            TextView tvPageUp = (TextView)readActionView.findViewById(R.id.read_page_up_btn);
            tvPageUp.setVisibility(View.INVISIBLE);
            
            
            // 4. 下一页
            TextView tvPageDown = (TextView)readActionView.findViewById(R.id.read_page_down_btn);
            tvPageDown.setVisibility(View.INVISIBLE);
            
			
            // 5. 阅读进度 -- 跳转章节
            seekBarChapterProgress = (SeekBar)readActionView.findViewById(R.id.read_chapter_progress_seekBar);
            seekBar.setMax(100);
            seekBarChapterProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					float percent = progress * 1.0f / seekBar.getMax();
                    
					mBookCache.setPercent(percent);
                    
					mBookCache.draw(mCurPageCanvas);
					mPageWidget.postInvalidate();

					DecimalFormat df = new DecimalFormat("#0.0");
					String strPercent = df.format(percent * 100) + "%";
					tvPageProgress.setText(strPercent);
				}
			});
            
            // 6. 回退
            TextView tvProgressBack = (TextView)readActionView.findViewById(R.id.read_progress_back_btn);
            tvProgressBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
                    
					mBookCache.setPosition(lastPosition);
					mBookCache.draw(mCurPageCanvas);
					mPageWidget.postInvalidate();
                    
                    setPageProgress();
				}
			});
        }
        
        setPageProgress();
		
        return readActionView;
	}
    
	private void setPageProgress() {
        DecimalFormat df = new DecimalFormat("#0.00");
        float percent = mBookCache.getPercent();
		String strPercent = df.format(percent * 100) + "%";
        tvPageProgress.setText(strPercent);
        seekBarChapterProgress.setProgress((int)(percent * 100));
	}

    /********************************************************************************************************************/
	
}
