/*
 * @project :QvodBookDemo
 * @author  :huqiming 
 * @date    :2015年1月22日
 */
package com.xn.xiaoyan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boyiqove.view.BaseFragment;

/**
 *
 */
public class BookShelfFragment extends BaseFragment {
	
	private static final String TAG = "BookShelfFragment";
	private static final int ACTION_BAR_ID_MENU = 0;
	private static final int ACTION_BAR_ID_SHOP = 1;

//	@Override
//	public ActionBarInfo initActionBarInfo() {
//		// 初始化 Actionbar，无需修改即可
//		ActionBarInfo info = new ActionBarInfo();
//		info.displayMode = ActionBar.DISPLAY_MODE_NORMAL;
//		info.centerText = getResString(R.string.bk_shelf_title);
//		info.leftItem = new ActionBarItem(ACTION_BAR_ID_MENU, ActionBar.ACTION_ITEM_TYPE_LEFT_MENU);
//
//		info.rightItem = new ActionBarItem(ACTION_BAR_ID_SHOP, ActionBar.ACTION_ITEM_TYPE_RIGHT_NORMAL);
//		info.rightItem.nomralText = getResString(R.string.bk_shop_title);
//		Context context = getApplicationContext();
//		if (context != null) {
//			info.rightItem.leftCompoundDrawable = context.getResources().getDrawable(R.drawable.ic_book);
//		}
//		return info;
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO 在此回调中初始化视图，无需再重写onCreateView;
		return super.onCreateView(inflater, container, savedInstanceState);
		
	}

//	@Override
//	public void onActionBarButtonClicked(ActionBarItem item) {
//		Log.v(TAG, "onActionBarButtonClicked " + item.getId());
//		switch (item.getId()) {
//		case ACTION_BAR_ID_SHOP:
//			// TODO 按钮点击事件中添加跳转至书城的代码
//			break;
//		default:
//			break;
//		}
//	}

	/**
	 * 常用工具类使用示例，如有需要，请按照以下示例代码直接使用，无需再额外增加第三方工具库；
	 */
	private void testUtils() {
		// 图片加载器：
		// ImageLoader.getInstance().displayImage(uri, imageView);
		// ImageLoader.getInstance().loadImage(uri, listener);

		// http请求：
		// Request request = new Request(url);
		// request.setHttpHead(httpHead);
		// request.setParser(new JsonParser(parseClass));
		// request.setOnRequestListener(l);
		// HttpConnectManager.getInstance(getApplicationContext()).doGet(request);
		// HttpConnectManager.getInstance(getApplicationContext()).doPost(request);

		// json数据解析:
		// JacksonUtils.shareJacksonUtils().parseJson2List(json, c);
		// JacksonUtils.shareJacksonUtils().parseJson2Obj(jsonStr, c);
		// JacksonUtils.shareJacksonUtils().parseObj2Json(bean);
	}

	

}
