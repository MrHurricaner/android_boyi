package com.xiaoyan.util;


import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xn.xiaoyan.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.widget.Toast;

/**
 * 
 * @Description: 工具类，提供应用管理常用方法
 *
 */

public class appMannerUtils {


	/**
	 * long转时间格式
	 * 
	 * @param time
	 *            时间
	 * @return
	 */
	public static String transitionTime(long time) {
		long temp1 = time / 1000;
		long temp2 = temp1 / 60;
		long temp3 = temp2 / 60;
		String h = temp3 % 60 + "";
		String m = temp2 % 60 + "";
		String s = temp1 % 60 + "";

		if (m.length() < 2) {
			m = "0" + m;
		}
		if (s.length() < 2) {
			s = "0" + s;
		}
		String time1 = h + ":" + m + ":" + s;
		System.out.println(time1);
		return time1;
	}

	/**
	 * 转换文件或文件夹的大小
	 * 
	 * @param file
	 *            文件或文件夹
	 * @return
	 */
	public static String FormetFileSize(long file) {
		// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (file < 1024) {
			fileSizeString = df.format((double) file) + "B";
		} else if (file < 1048576) {
			fileSizeString = df.format((double) file / 1024) + "K";
		} else if (file < 1073741824) {
			fileSizeString = df.format((double) file / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) file / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 文件或文件夹的时间
	 * 
	 * @param path
	 *            路径
	 * @return
	 */
	public static String getFileLastModifiedTime(String path) {
		File file = new File(path);
		Calendar calendar = Calendar.getInstance();
		long time = file.lastModified();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		calendar.setTimeInMillis(time);

		return format.format(calendar.getTime());

	}

	/**
	 * 获取SDCard根路径的方法
	 * 
	 * @return
	 */
	public static String getSDCardPath() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {// 判断是否加载SDCard
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}

	/**
	 * 获取ListView要显示的数据源
	 * 
	 * @param path
	 *            图片路径
	 * @return
	 */
	public static List<Map<String, Object>> getListData(Context context,
			String path) {
		/** 创建List数据集合 */
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		/** 获取文件对象 */
		File file = new File(path);
		/** 如果不为空,也不是"mnt"目录,则创建一个Map集合,保存"返回上级目录" */
		if (file.getParent() != null && !"/mnt".equals(file.getParent())) {
			/** item的数据集 */
			Map<String, Object> item = new HashMap<String, Object>();
			// 图标
			// 名称
			item.put("name", "返回上级目录");
			// 路径
			item.put("path", file.getParent());
			// 保存到List集合中
			list.add(item);
		}
		/** 获取文件夹下的所有文件以及文件的信息 */
		File[] files = getFilesOrder(file);
		/** 满足条件,则遍历数据 */
		if (files != null && files.length > 0) {
			/** 遍历根目录 */
			for (File file2 : files) {
				/** 保存ListView中的每一行 */
				Map<String, Object> item = new HashMap<String, Object>();
				/** 判断是否存在目录 */
				if (file2.isDirectory()) {
					// 文件图标
//					item.put("icon", R.drawable.folder);
					// 文件名称
					item.put("name", file2.getName());
					// 绝对路径
					item.put("path", file2.getAbsolutePath());
					// 保存在List集合中
					list.add(item);
					/** 图片文件显示操作 */
				} else if (file2.isFile()) {
					// 文件图标
//					item.put("icon", R.drawable.app_icon);
					// 文件名称
					item.put("name", file2.getName());
					// 绝对路径
					item.put("path", file2.getAbsolutePath());
					// 保存在List集合中
					list.add(item);
				}

			}
		}
		return list;
	}

	/**
	 * 获取某个路径下所有的文件夹,按一定顺序返回
	 * 
	 * @param file
	 *            文件以及文件夹的数组
	 * @return
	 */
	public static File[] getFilesOrder(File file) {
		/** 定义File数组 */
		File[] files = null;

		/** 判断这个路径是否存在 */
		if (file.exists()) {
			/** 获取所有文件或文件夹 */
			files = file.listFiles();
			/** 满足条件,则遍历数据 */
			if (files != null && files.length > 0) {
				/** 进行冒泡排序 */
				for (int i = 0; i < files.length; i++) {
					for (int j = 0; j < files.length - i - 1; j++) {
						/** 用compareTo根据字典顺序排序 */
						if (files[j].getName()
								.compareTo(files[j + 1].getName()) > 0) {
							File temp;
							temp = files[j];
							files[j] = files[j + 1];
							files[j + 1] = temp;
						}
					}
				}
			}
		}
		return files;
	}

	/**
	 * 刪除文件夹或者文件
	 * 
	 * @param folderPath
	 *            文件夹路径或者文件的绝对路径
	 */
	public static void deleteDirectoy(String folderPath) {
		try {
			/** 删除文件夹里说的所有的文件及文件夹 */
			deleteDirectoy(folderPath);
			File lastFile = new File(folderPath);
			if (lastFile.exists()) {
				/** 最后删除空文件夹 */
				lastFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param path
	 *            路径
	 */
	public static void deleteAllFile(Context context, String path) {
		System.out.println("deleteAllFile");
		/** 在内存开辟一个文件空间,但是没有创建 */
		File file = new File(path);
		/** 判断路径是否存在 */
		if (!file.exists()) {
			return;
		}
		/** 判断是否是文件 */
		if (file.isFile()) {
			/** 删除文件 */
			file.delete();
			/** 如果是文件夹 */
		} else if (file.isDirectory()) {
			String[] tempList = file.list();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				if (path.endsWith(File.separator)) {
					temp = new File(path + tempList[i]);
				} else {
					temp = new File(path + File.separator + tempList[i]);
				}
				if (temp.isFile()) {
					temp.delete();
				}
				if (temp.isDirectory()) {
					/** 先刪除文件夹里面的文件 */
					deleteAllFile(context, path + "/" + tempList[i]);
					/** 再删除空文件夹 */
					deleteDirectoy(path + "/" + tempList[i]);
				}
			}
		}
	}

	/**
	 * 修改文件或文件夹名称
	 * 
	 * @param path
	 *            路径
	 * @param new_name
	 *            新名称
	 */
	public static void updateAllFile(String path, String new_name) {
		/** 在内存开辟一个文件空间,但是没有创建 */
		File file = new File(path);
		String sdcardFile = file.getParent();
		File from = new File(sdcardFile, file.getName());
		File to = new File(sdcardFile, new_name);
		from.renameTo(to);
	}

	/**
	 * 各种拓展名不同处理
	 * 
	 * @param context
	 *            上下文
	 * @param path
	 *            路径
	 */
	public static void isExpandname(Context context, String path) {
		/** 获取文件对象 */
		File file = new File(path);
		/** 满足条件,则取其扩展名 */
		if (path != null && !"".equals(path) && file.isFile()) {
			/** 获取文件的后缀名 */
			String fileExt = path.substring(path.lastIndexOf(".") + 1,
					path.length());
			System.out.println("fileExt:::" + fileExt);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			System.out.println(file.getName());
			System.out.println(file.getPath());
			Uri uri = Uri.parse("file://" + file.getPath());
			if ("txt".equalsIgnoreCase(fileExt)
					|| "htm".equalsIgnoreCase(fileExt)
					|| "html".equalsIgnoreCase(fileExt)
					|| "lrc".equalsIgnoreCase(fileExt)) {

				intent.setDataAndType(uri, "text/*");

			} else if ("chm".equalsIgnoreCase(fileExt)) {

				intent.setDataAndType(uri, "application/x-chm");

			} else if ("xlsx".equalsIgnoreCase(fileExt)
					|| "xls".equalsIgnoreCase(fileExt)) {

				intent.setDataAndType(uri, "application/vnd.ms-excel");

			} else if ("docx".equalsIgnoreCase(fileExt)
					|| "doc".equalsIgnoreCase(fileExt)) {

				intent.setDataAndType(uri, "application/msword");

			} else if ("pptx".equalsIgnoreCase(fileExt)
					|| "ppt".equalsIgnoreCase(fileExt)) {

				intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

			} else if ("jpg".equalsIgnoreCase(fileExt)
					|| "png".equalsIgnoreCase(fileExt)
					|| "gif".equalsIgnoreCase(fileExt)
					|| "jpeg".equalsIgnoreCase(fileExt)
					|| "apk".equalsIgnoreCase(fileExt)
					|| "html".equalsIgnoreCase(fileExt)
					|| "htm".equalsIgnoreCase(fileExt)) {

				intent.setDataAndType(uri, "image/*");

			} else if ("mp3".equalsIgnoreCase(fileExt)
					|| "wav".equalsIgnoreCase(fileExt)
					|| "ogg".equalsIgnoreCase(fileExt)
					|| "mid".equalsIgnoreCase(fileExt)
					|| "wma".equalsIgnoreCase(fileExt)) {

				intent.setDataAndType(uri, "audio/*");

			} else if ("jar".equalsIgnoreCase(fileExt)
					|| "zip".equalsIgnoreCase(fileExt)
					|| "rar".equalsIgnoreCase(fileExt)
					|| "gz".equalsIgnoreCase(fileExt)) {

				intent.setDataAndType(uri, "*/*");

			} else if ("mp4".equalsIgnoreCase(fileExt)
					|| "rm".equalsIgnoreCase(fileExt)
					|| "mpg".equalsIgnoreCase(fileExt)
					|| "avi".equalsIgnoreCase(fileExt)
					|| "mpeg".equalsIgnoreCase(fileExt)
					|| "3gp".equalsIgnoreCase(fileExt)) {

				intent.setDataAndType(uri, "video/*");

			} else if ("apk".equalsIgnoreCase(fileExt)) {

				intent.setDataAndType(uri,
						"application/vnd.android.package-archive");
			} else {
				showMsg(context, "无法找到应用程序以执行该操作");
				return;
			}
			context.startActivity(intent);

		}
	}
	
	/**
	 * 获取已安装应用信息列表
	 * 
	 * @param context
	 * @return
	 */
	public static List<AppPackageInfo> getAppsList(Context context) {

		List<AppPackageInfo> appPackageInfos = new ArrayList<AppPackageInfo>();
		try {
			PackageManager pManager = context.getPackageManager();
			List<PackageInfo> appList = getAllApkList(context);
			AppPackageInfo appPackageInfo;
			String dir;

			for (int i = 0; i < appList.size(); i++) {
				PackageInfo packageInfo = appList.get(i);
				appPackageInfo = new AppPackageInfo();
				appPackageInfo.packageName = packageInfo.packageName;
				appPackageInfo.appVersion = packageInfo.versionName;
				appPackageInfo.appVersion_code = packageInfo.versionCode;
				appPackageInfo.isSysFlag = ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) ? false
						: true;
				dir = packageInfo.applicationInfo.publicSourceDir;
				appPackageInfo.appSize = getSize(new File(dir).length());
				appPackageInfo.appName = (String) packageInfo.applicationInfo
						.loadLabel(pManager);
				appPackageInfo.appIcon = packageInfo.applicationInfo
						.loadIcon(pManager);

				appPackageInfos.add(appPackageInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return appPackageInfos;
	}

	/**
	 * 格式转换应用大小 单位"M"
	 */
	public static String getSize(long size) {
		return new DecimalFormat("0.##").format(size * 1.0 / (1024 * 1024))
				+ "M";
	}

	/**
	 * 获取系统中已安装应用的包信息
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getAllApkList(Context context) {
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pm = context.getPackageManager();

		// 获取系统中已安装应用的包信息
		List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
		for (int i = 0; i < packageInfoList.size(); i++) {
			PackageInfo packageInfo = (PackageInfo) packageInfoList.get(i);
			// 不添加本应用包信息
			if (!packageInfo.packageName.equals(context.getPackageName())) {
				// 移除不可打开界面的应用
				// Intent intent =
				// context.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
				// if(intent!=null){
				apps.add(packageInfo);
				// }
			}
		}
		return apps;
	}

	

	/**
	 * 获取手机的内部存储的可用空间
	 */
	public static String getInternalAvailSize(Context context) {
		StatFs statFs = new StatFs("/data");
		long blockSize = statFs.getBlockSize();
		long availableBlocks = statFs.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

	/**
	 * 打开应用
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean openPackage(Context context, String packageName) {

		try {
			Intent intent = new Intent();
			intent = context.getPackageManager().getLaunchIntentForPackage(
					packageName);
			if (intent != null) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 卸载程序
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static void uninstallApk(Activity context, String packageName,
			int requestCode) {
		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		context.startActivityForResult(uninstallIntent, requestCode);
	}

	/**
	 * 提示
	 * 
	 * @param context
	 *            上下文
	 * @param text
	 *            内容
	 */
	public static void showMsg(Context context, String text) {
		Toast.makeText(context, text, 1000).show();
	}
}

