package com.jaly.touchscreenor;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaly.touchscreenor.sys.TaskItem;
import com.jaly.touchscreenor.util.AppUtils; 

@ContentView(R.layout.dialog_task_detail)
public class TaskDetailDialog extends DialogFragment{
 
	@ViewInject(R.id.detail_task_icon)
	private ImageView icon;
	@ViewInject(R.id.detail_task_name)
	private TextView name; 
	@ViewInject(R.id.detail_task_start)
	private TextView start;
	@ViewInject(R.id.detail_task_status)
	private TextView status;
	@ViewInject(R.id.detail_task_params)
	private TextView params;
	
	private TaskItem taskItem;
	
	public TaskDetailDialog(TaskItem taskItem) { 
		this.taskItem = taskItem;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		return x.view().inject(this, inflater, container);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) { 
		super.onActivityCreated(savedInstanceState); 
		String fileName = taskItem.getFileName();
		name.setText(fileName);
		icon.setImageDrawable(AppUtils.getAppIcon(fileName)); 
		start.setText(taskItem.getStartTime());
		status.setText(taskItem.isRunning()?"正在运行":"停止运行"); 
		params.setText(taskItem.getParamString()); 
	}
	
}
