package com.jaly.touchscreenor;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter{

	private List<AppInfo> data;
	private Context context;
	private AppInfo selectedInfo = null;
	
	private int selIdx;
	
	public AppListAdapter(Context context, List<AppInfo> data) {
		this.context = context;
		this.data = data;
	}
	
	public void setSeclection(int position) {
		selIdx = position;
	}
	
	/**
	 * 返回选中的App
	 * @return
	 */
	public AppInfo getSelectedInfo() {
		return selectedInfo;
	}

	@Override
	public int getCount() { 
		return data.size();
	}

	@Override
	public Object getItem(int position) { 
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) { 
		if(convertView==null) {
			convertView = View.inflate(context, R.layout.item_app_list, null);
		}
		
		LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.item_app);
		
		if (selIdx == position) {
			layout.setBackgroundResource(android.R.color.holo_blue_bright);
		} else {
			layout.setBackgroundResource(android.R.color.transparent);
		}
		
		final AppInfo appInfo = data.get(position); 
		TextView textView = (TextView) convertView.findViewById(R.id.item_app_name);
		textView.setText(appInfo.getAppLabel());
		
		ImageView appIcon = (ImageView) convertView.findViewById(R.id.item_app_icon);
		appIcon.setImageDrawable(appInfo.getAppIcon()); 
		
		return convertView;
	}

}
