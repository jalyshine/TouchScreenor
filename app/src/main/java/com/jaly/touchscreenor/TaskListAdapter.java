package com.jaly.touchscreenor;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaly.touchscreenor.sys.TaskItem;
import com.jaly.touchscreenor.sys.TaskManager;
import com.jaly.touchscreenor.util.AppUtils;

public class TaskListAdapter extends BaseAdapter{

	private List<TaskItem> items;
	private Context context; 
	private TaskManager taskManager;
	
	public TaskListAdapter(Context context, List<TaskItem> items) {
		this.context = context;
		this.items = items; 
		taskManager = TaskManager.getManager(context);
	}
	
	public void setTaskItems(List<TaskItem> items) { 
		this.items = items;
	}
	
	@Override
	public int getCount() { 
		return items.size();
	}

	@Override
	public Object getItem(int position) { 
		return items.get(position);
	}

	@Override
	public long getItemId(int position) { 
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		if(convertView==null) {
			convertView = View.inflate(context, R.layout.item_task_list, null);
		}		
		final TaskItem item = items.get(position);
		String fileName = item.getFileName();
		// app图标 
		ImageView imageView = (ImageView) convertView.findViewById(R.id.img_task_icon);
		imageView.setImageDrawable(AppUtils.getAppIcon(fileName));
		// 脚本名
		TextView name = (TextView) convertView.findViewById(R.id.txt_task_name);
		name.setText(fileName);
		// 启动时间
		TextView time = (TextView) convertView.findViewById(R.id.txt_task_time); 
		time.setText(item.getStartTime()); 
		// 任务启动和终止
		Drawable drawable = context.getResources()
				.getDrawable(item.isRunning()?R.drawable.task_on: R.drawable.task_off); 
		ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.img_task_status);
		imgBtn.setImageDrawable(drawable);		
		imgBtn.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) { 
				boolean flag = !item.isRunning();
				Drawable drawable = context.getResources()
						.getDrawable(flag?R.drawable.task_on: R.drawable.task_off);
				((ImageButton)v).setImageDrawable(drawable);
				item.setRunning(flag);
				taskManager.updateTask(item);
			}
		});
		return convertView;
	}

}
