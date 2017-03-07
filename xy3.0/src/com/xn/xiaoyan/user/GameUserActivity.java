package com.xn.xiaoyan.user;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.StaticLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.xiaoyan.util.DBGameHelper;
import com.xiaoyan.util.appMannerUtils;
import com.xn.xiaoyan.downgame.DownloadUtil;
import com.xn.xiaoyan.downgame.XyDBManager;
import com.xn.xiaoyan.downgame.DownloadUtil.OnDownloadListener;
import com.boyiqove.AppData;
import com.boyiqove.config.Config;
import com.boyiqove.entity.OnlineChapterInfo;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.VolleyError;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.toolbox.ImageLoader;
import com.boyiqove.library.volley.toolbox.NetworkImageView;
import com.boyiqove.library.volley.toolbox.StringRequest;
import com.boyiqove.library.volley.toolbox.ImageLoader.ImageCache;
import com.boyiqove.protocol.StatusCode;
import com.boyiqove.ui.bookqove.ImageCacheManager;
import com.boyiqove.util.DebugLog;
import com.boyiqove.view.BaseActivity;
import com.bytetech1.sdk.data.CommentItem;
import com.bytetech1.sdk.data.DirectoryItem;
import com.bytetech1.sdk.data.cmread.SearchItem;
import com.xn.xiaoyan.R;
import com.xn.xiaoyan.R.string;

public class GameUserActivity extends BaseActivity {
	private LinearLayout backIv;
	private TextView titleTv;
	private ListView itemListView;
	private List<GameItem>gameList;
	private UserUtilAdapter adapter;
	private ProgressBar progressBar;
//	private DownloadUtil mDownloadUtil;
	private int max;
	private String localPath;
	public static XyDBManager dbManager;
	private String mReadCacheRoot;
	private DBGameHelper  dbGameHelper;
	private boolean isInApk;
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (dbGameHelper!=null) {
			dbGameHelper.close();
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_user);
		initView();
		initData();
	}
	public static XyDBManager getDBManner(Context cnx){
		if (dbManager==null) {
			dbManager=new XyDBManager(cnx);
		}
		return dbManager;
	}
	private void initData() {
		// 请求游戏列表，创建adapter
//		判断数据库是否有		
//		File dir = this.getExternalFilesDir("cache");
//		if(dir == null) {
//			mReadCacheRoot = this.getFilesDir().toString() + "/cache" ;
//		} else {
//			mReadCacheRoot = dir.toString();
//		}
//		String dirPath = mReadCacheRoot + "/game";
//        File f = new File(dirPath);
//		if(!f.exists()) {
//			f.mkdirs();
//		}
//		String name= dirPath + "/game_list.db";
		dbManager=new XyDBManager(this);
		dbGameHelper=(DBGameHelper) dbManager.open("", XyDBManager.TYPE_XY_GAME);
		long count =dbGameHelper.fetchPlacesCount();
		if (count>0) {
			
			gameList=dbGameHelper.getGameList();
			adapter=new UserUtilAdapter(this, gameList, true);			
			itemListView.setAdapter(adapter);
		}else {
			
			downLoadGame(1);
		}
	}
	private void initView() {
		// TODO Auto-generated method stub
		backIv = (LinearLayout) findViewById(R.id.boe_back_bt);
		backIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.boyi_move_left_in,
						R.anim.boyi_move_left_out);
			}
		});
		titleTv = (TextView) findViewById(R.id.search_top_title_tv);
		titleTv.setText("玩游戏");

		itemListView = (ListView) findViewById(R.id.user_game_listview);
		
		gameList=new ArrayList<GameItem>();
		adapter=new UserUtilAdapter(this, gameList, true);			
		itemListView.setAdapter(adapter);		
//		localPath = this.getFilesDir().toString() + "/cache";
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isInApk) {
			adapter.notifyDataSetChanged();
		}
	}
	
	
	
	//请求游戏列表
	private void downLoadGame( int page ){
		
		String url = AppData.getConfig().getUrl(Config.URL_GAME_LIST);
		url=url+"/"+page;
		getRequestQueue().add(new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				try {
					JSONObject responseJson = new JSONObject(response);
					DebugLog.e("游戏结果是：", response);
					int status = responseJson.getInt("status");
					if (status == StatusCode.OK) {						
						// 解析json，封装bean
						JSONArray array=responseJson.getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							GameItem gameItem=new GameItem();
							JSONObject jsonObject=array.getJSONObject(i);
							gameItem.id=jsonObject.getInt("id");
							gameItem.name=jsonObject.getString("name");
							gameItem.starnums=jsonObject.getString("starnums");
//							gameItem.showimage=jsonObject.getString("showimage").substring(0, 20)+"ydapp"+jsonObject.getString("showimage").substring(21, jsonObject.getString("showimage").length());
							gameItem.showimage=jsonObject.getString("showimage");
							gameItem.activityUms=jsonObject.getString("jihuonums");
							gameItem.description=jsonObject.getString("description");
							gameItem.download=jsonObject.getString("download");
							gameItem.downNum=jsonObject.getString("downcounts");
							gameItem.cate=jsonObject.getString("cate");
							gameItem.issueDate=jsonObject.getLong("issuedate");
							gameItem.createDate=jsonObject.getLong("createdate");
							gameItem.netApkSize=(float) jsonObject.getDouble("apksize");
							gameItem.apkSize=0;
							gameItem.isTop=jsonObject.getInt("istop");
							gameItem.version=jsonObject.getString("version");
							gameItem.updatetime=jsonObject.getLong("updatetime");
							gameItem.packagename=jsonObject.getString("sdkname");
							gameItem.lastSize=(long) 0;
							gameList.add(gameItem);	
						}
						adapter.notifyDataSetChanged();
						// 更新ui
//						itemListView.setAdapter(adapter);
						dbGameHelper.insertGameList(gameList);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				DebugLog.e("直接失败", "请求失败");
				return;
			}
		}));
	}
	
	
	private boolean isGameListLoaded() {
		
		String dir = mReadCacheRoot + "/game/game_list.db";
		File f = new File(dir);
		return f.exists();
	}
	
	
	public interface btOnclick{
		
		abstract void BtonClick();
	}
	
	public class UserUtilAdapter extends BaseAdapter {

		private Context context;
		
		private List<CommentItem> list;
		private List<GameItem>mItems;
		private Boolean isSelector;  // 书籍推荐位是真，
		
		private NetworkImageView  itemImage;
		private List<SearchItem> searchList;
		private ImageLoader imageLoader;
		private btOnclick onClickLister;
		private  long timeS;
		private Viewhelder helderMl;
		
		public UserUtilAdapter(Context context,List<GameItem>mItem, Boolean isSelector) {
			super();
			this.mItems = mItem;
			this.context = context;
			this.isSelector=isSelector;
			ImageCache	imageCache=ImageCacheManager.getInstance(context);
			if(imageLoader == null) {
				imageLoader = new ImageLoader(AppData.getRequestQueue(), imageCache);
			}
			
		}

		
		public UserUtilAdapter(List<CommentItem> list, Context context,Boolean isSelector) {
			super();
			this.list = list;
			this.context = context;
			this.isSelector = isSelector;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (isSelector) {			
				return mItems == null ? 0 : mItems.size();
			}else {
				return list == null ? 0 : list.size();
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
				
				if (convertView == null) {
					helderMl=null;
					helderMl=new Viewhelder();
					helderMl.id=position;
					convertView=LayoutInflater.from(context).inflate(R.layout.user_gift_list_item, null);
					// 游戏名
					helderMl.bookName=(TextView) convertView.findViewById(R.id.user_geft_item_name);
					// 大小+下载量
					helderMl.actorName=(TextView) convertView.findViewById(R.id.user_acother);
					helderMl.stopTV=(TextView) convertView.findViewById(R.id.user_stop);
					// 简介
					helderMl.contentTv=(TextView) convertView.findViewById(R.id.user_geft_item_Desc);
					// 图片
					helderMl.imageView=(NetworkImageView) convertView.findViewById(R.id.everyday_cover_book);
					//下载按钮
					helderMl.downButton=(Button) convertView.findViewById(R.id.user_game_bt);
					
					helderMl.progressBar=(ProgressBar) convertView.findViewById(R.id.user_down_progress);
//					localPath = GameUserActivity.this.getFilesDir().toString() + "/cache"+"/gameApk";
//					helderMl.mDownloadUtil=new DownloadUtil(2, localPath, "00.png", urlString,
//							context);
//					String urlString = "http://bbra.cn/Uploadfiles/imgs/20110303/fengjin/013.jpg";
//					String urlString = "http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk";
					String urlString = "http://sdk.boetech.cn/Uploads/apk/11.apk";
					
					localPath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/boegame";
					helderMl.mDownloadUtil=new DownloadUtil(1, localPath, mItems.get(position).name+position+".apk", mItems.get(position).download,
							context,position+"",position);
//					helderMl.mDownloadUtil=new DownloadUtil(1, localPath, mItems.get(position).name+position+".png", urlString,
//							context,mItems.get(position).packagename);
					convertView.setTag(helderMl);

				}else {
					helderMl=(Viewhelder) convertView.getTag();
				}	
				String spkSize=fnum.format((float)mItems.get(position).apkSize/(1024*1024));
				
				helderMl.actorName.setText(mItems.get(position).netApkSize+"M   "+mItems.get(position).downNum+"人下载");
				helderMl.imageView.setErrorImageResId(R.drawable.boyi_ic_cover_default);
				helderMl.imageView.setDefaultImageResId(R.drawable.boyi_ic_cover_default);
				DebugLog.e("封面的url", mItems.get(position).showimage);
				helderMl.imageView.setImageUrl(mItems.get(position).showimage, imageLoader);
//				DebugLog.e("游戏描述", mItems.get(position).description);
				String desString=mItems.get(position).description;
				if ("\\ ".endsWith(desString.substring(0, 1))) {
//					DebugLog.e("描述开头包含空格", "去除空格");
					
					desString=mItems.get(position).description.substring(1, mItems.get(position).description.length());
				}
				helderMl.contentTv.setText(desString);
				helderMl.bookName.setText(mItems.get(position).name);
				helderMl.downButton.setOnClickListener(new ButtonListener(helderMl,position));
				//获取相应数据库中的进度，并设置给进度条
				if (checkApkExist(context, mItems.get(position).packagename)) {					
					helderMl.downButton.setText("打开");
					helderMl.progressBar.setVisibility(View.GONE);
					helderMl.progressBar.setProgress(0);
					helderMl.stopTV.setVisibility(View.GONE);
				}else {
					
					if (mItems.get(position).lastSize >= (long)(mItems.get(position).apkSize)&&mItems.get(position).apkSize>0) {
						
						helderMl.progressBar.setVisibility(View.VISIBLE);
						
//					helderMl.progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.user_progress_pink));
						helderMl.progressBar.setBackgroundColor(R.color.boyi_progress_gray);
						ClipDrawable d = new ClipDrawable(new ColorDrawable(getResources().getColor(R.color.boyi_green_tiao)), Gravity.LEFT, ClipDrawable.HORIZONTAL);
						helderMl.progressBar.setProgressDrawable(d);		
						helderMl.progressBar.setProgress(mItems.get(position).lastSize.intValue());
						helderMl.downButton.setText("安装");
						helderMl.downButton.setBackgroundResource(R.drawable.user_game_open);
						helderMl.stopTV.setText("已完成");
					}else if (0 < mItems.get(position).lastSize && mItems.get(position).lastSize<(long)(mItems.get(position).apkSize)&&mItems.get(position).apkSize>0) {
						helderMl.progressBar.setVisibility(View.VISIBLE);
						
						helderMl.progressBar.setBackgroundResource(R.drawable.boe_progress_back);
						helderMl.progressBar.setMax(mItems.get(position).apkSize);
						ClipDrawable d = new ClipDrawable(new ColorDrawable(Color.RED), Gravity.LEFT, ClipDrawable.HORIZONTAL);
						helderMl.progressBar.setProgressDrawable(d);
						helderMl.progressBar.setProgress(mItems.get(position).lastSize.intValue());
						DebugLog.e("初始化进度条进度是：", mItems.get(position).lastSize.intValue()+"");
						DebugLog.e("总进度：", (int)(mItems.get(position).apkSize*1024*1024)+"");
						helderMl.downButton.setText("继续");
//					helderMl.progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.user_progress_pink));
						helderMl.stopTV.setText("已暂停");
					}else if (mItems.get(position).apkSize==0) {
						helderMl.downButton.setText("下载");
						helderMl.progressBar.setVisibility(View.GONE);
						helderMl.progressBar.setProgress(0);
						helderMl.stopTV.setVisibility(View.GONE);
					}
				}
				
				
				
			return convertView;
		}
		private  DecimalFormat   fnum   =   new   DecimalFormat("##0.00");  
		private DecimalFormat   fnum2   =   new   DecimalFormat("##0.0"); 
		class ButtonListener implements OnClickListener {
	        private int position;
	        private Viewhelder helderMl;

	        ButtonListener(Viewhelder helderMl,int position) {
	            this.helderMl = helderMl;
	            this.position = position;
	        }
	        
	        @Override
	        public void onClick(View v) {
	            int vid=v.getId();
	            if (helderMl.downButton.getText().toString().equals("下载")){
	            	
	            DebugLog.e("相应的helder的id是", helderMl.id+"");
				helderMl.progressBar.setVisibility(View.VISIBLE);
				helderMl.downButton.setText("暂停");	
				helderMl.mDownloadUtil.start();
				helderMl.stopTV.setVisibility(View.VISIBLE);
	            	helderMl.mDownloadUtil.setOnDownloadListener(new OnDownloadListener() {

						@Override
						public void downloadStart(int fileSize) {
							// TODO Auto-generated method stub
							max = fileSize;			
							helderMl.progressBar.setMax(max);
							dbGameHelper.updateTotalSize(position, max);
							DebugLog.e("下载初始化总大小", (int)(mItems.get(position).netApkSize*1024*1024)+"---"+fileSize);
							Date nowTime = new Date();
							timeS = nowTime.getTime();
						}
						@Override
						public void downloadProgress(int downloadedSize) {
							// TODO Auto-generated method stub
							DebugLog.e("下载的大小是：", downloadedSize+"");
							dbGameHelper.updateEndTop(position, downloadedSize);
							helderMl.progressBar.setProgress(downloadedSize);
							helderMl.progressBar.setSecondaryProgress(helderMl.progressBar.getProgress()+10);
							double nowPress=(double)((double)downloadedSize /((double)1449616));
							   String   nowPreStr=fnum.format(nowPress);  
							float count=(float)((double)max/(1449616));
							String   totalPreStr=fnum2.format(count);
							DebugLog.e("下载了::"+nowPreStr, "总共有"+totalPreStr);
							helderMl.actorName.setText(nowPreStr+"M/"+totalPreStr+"M");
							Date endDate = new Date();
							long timeE= endDate.getTime(); 						
							double size=(double)downloadedSize/1024 /(timeE-timeS);
							String   vSize=fnum.format(size);
							helderMl.stopTV.setText(vSize+"KB/s");
						}
						@Override
						public void downloadEnd() {
							// TODO Auto-generated method stub
							
//							dbGameHelper.updateEndTop(position, compeleteSize);
							helderMl.actorName.setText(mItems.get(position).netApkSize+"M   "+mItems.get(position).downNum+"人下载");
							helderMl.stopTV.setVisibility(View.GONE);
							helderMl.downButton.setText("安装");
							helderMl.downButton.setBackgroundResource(R.drawable.user_game_open);
						}
					});
	        }else if (helderMl.downButton.getText().toString().endsWith("暂停")) {
	        	helderMl.downButton.setText("继续");	
//	        	helderMl.progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.user_progress_pink));
//	        	helderMl.progressBar.setBackgroundColor(R.color.boyi_dark_blue);
	        	helderMl.progressBar.setBackgroundResource(R.drawable.boe_progress_back);
	        	
	        	ClipDrawable d = new ClipDrawable(new ColorDrawable(Color.RED), Gravity.LEFT, ClipDrawable.HORIZONTAL);
	        	helderMl.progressBar.setProgressDrawable(d);
	        	helderMl.mDownloadUtil.pause();
				helderMl.stopTV.setText("已暂停");
				
			}else if ((helderMl.downButton.getText().toString().endsWith("继续"))) {
				
				helderMl.progressBar.setVisibility(View.VISIBLE);
				helderMl.downButton.setText("暂停");	
//				helderMl.progressBar.setBackgroundColor(R.color.boyi_black_touming);
	        	ClipDrawable d = new ClipDrawable(new ColorDrawable(getResources().getColor(R.color.boyi_green_tiao)), Gravity.LEFT, ClipDrawable.HORIZONTAL);
	        	helderMl.progressBar.setProgressDrawable(d);
				helderMl.mDownloadUtil.start();
				helderMl.stopTV.setVisibility(View.VISIBLE);
				helderMl.stopTV.setText("");
	            helderMl.mDownloadUtil.setOnDownloadListener(new OnDownloadListener() {

						@Override
						public void downloadStart(int fileSize) {
							// TODO Auto-generated method stub
							max = fileSize;			
							helderMl.progressBar.setMax(max);
							DebugLog.e("下载初始化总大小", (mItems.get(position).apkSize)+"---"+max);
							Date nowTime = new Date();
							timeS = nowTime.getTime();
						}

						@Override
						public void downloadProgress(int downloadedSize) {
							// TODO Auto-generated method stub
							DebugLog.e("下载的大小是：", downloadedSize+"");
							helderMl.progressBar.setProgress(downloadedSize);
							helderMl.progressBar.setSecondaryProgress(helderMl.progressBar.getProgress()+10);
							double nowPress=(double)((double)downloadedSize /((double)1449616));
							   String   nowPreStr=fnum.format(nowPress);  
							float count=(float)((double)max/(1449616));
							String   totalPreStr=fnum2.format(count);
							DebugLog.e("下载了::"+nowPreStr, "总共有"+totalPreStr);
							helderMl.actorName.setText(nowPreStr+"M/"+totalPreStr+"M");
							Date endDate = new Date();
							long timeE= endDate.getTime(); 						
							double size=(double)downloadedSize/1024 /(timeE-timeS);
							String   vSize=fnum.format(size);
							helderMl.stopTV.setText(vSize+"KB/s");
						}

						@Override
						public void downloadEnd() {
							// TODO Auto-generated method stub
							helderMl.actorName.setText(mItems.get(position).netApkSize+"M   "+mItems.get(position).downNum+"人下载");
							helderMl.stopTV.setVisibility(View.GONE);
							helderMl.downButton.setText("安装");
							helderMl.downButton.setBackgroundResource(R.drawable.user_game_open);
						}
					});
			}else if (helderMl.downButton.getText().toString().endsWith("安装")) {
				
				String saveFileName=localPath+"/"+mItems.get(position).name+position+".apk";
				installApk(saveFileName);
				
//				if (checkApkExist(context, mItems.get(position).packagename)) {
//					DebugLog.e("该包名存在", mItems.get(position).packagename);
//					helderMl.downButton.setText("打开");
//					helderMl.downButton.setBackgroundResource(R.drawable.user_game_open);
//					helderMl.progressBar.setVisibility(View.GONE);
//				}else {
//					DebugLog.e("该包名不存在", mItems.get(position).packagename);
//					helderMl.downButton.setText("失败");
//					helderMl.downButton.setBackgroundResource(R.drawable.user_game_open);
//					helderMl.progressBar.setVisibility(View.GONE);
//				}
				
			}else if ((helderMl.downButton.getText().toString().endsWith("打开"))) {
				appMannerUtils.openPackage(context, mItems.get(position).packagename);
			}
	            
	        }
	    }
		
		
		class Viewhelder{
			int id;
			TextView   actorName ;
			TextView   bookName ;
			TextView	contentTv ;
			TextView   data;
			TextView   stopTV;
			NetworkImageView imageView;
			Button downButton;
			ProgressBar progressBar;
			DownloadUtil mDownloadUtil;
		}
		/**
		 * 安装apk
		 */
		
		private void installApk(String saveFileName) {
			
			File apkfile = new File(saveFileName);
			if (!apkfile.exists()) {
				return;
			}
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
					"application/vnd.android.package-archive");
			context.startActivity(i);
			isInApk=true;
		}
		public boolean checkApkExist(Context context, String packageName) {
			DebugLog.e("包名是", packageName);
			if (packageName == null || "".equals(packageName)) {
				return false;
			}
			try {
				context.getPackageManager().getApplicationInfo(packageName,
						PackageManager.GET_UNINSTALLED_PACKAGES);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		
	}
	
	
}
