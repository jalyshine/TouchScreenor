package com.jaly.touchscreenor;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaly.touchscreenor.coding.ScriptInfo;

public class ScriptListAdapter extends BaseAdapter{

	private List<ScriptInfo> infos;
	private Context context;
	
	private int selIdx;
	
	public ScriptListAdapter(Context context, List<ScriptInfo> infos) {
		this.context = context;
		this.infos = infos;
	}
	
	public void setSeclection(int position) {
		selIdx = position;
	}
	
	public void setInfos(List<ScriptInfo> infos) {
		this.infos = infos;
	}
	
	@Override
	public int getCount() { 
		return infos.size();
	}

	@Override
	public Object getItem(int position) { 
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) { 
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null) {
			convertView = View.inflate(context, R.layout.item_script_list, null);
		}
		
		if(selIdx >= 0){
			RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.item_script);		
			if (selIdx == position) {
				layout.setBackgroundResource(android.R.color.holo_blue_bright);
			} else {
				layout.setBackgroundResource(android.R.color.transparent);
			}			
		}
		
		if(position >= 0){
			ScriptInfo info = infos.get(position); 
			
			ImageView icon = (ImageView) convertView.findViewById(R.id.img_app_icon);
			icon.setImageDrawable(info.getAppIcon()); 
			
			TextView name = (TextView) convertView.findViewById(R.id.txt_script_name);
			name.setText(info.getFileName());
			
			TextView pkg = (TextView) convertView.findViewById(R.id.txt_app_pkg); 
			pkg.setText(info.getPkgName());
			
			TextView time = (TextView) convertView.findViewById(R.id.txt_script_time);		
			SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd  hh:mm", Locale.CHINA);
			time.setText(format.format(info.getUpateTime()));			
		}
		
		return convertView;
	}

}
