package com.jaly.touchscreenor;

import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.jaly.touchscreenor.coding.ScriptInfo;
import com.jaly.touchscreenor.coding.ScriptManager;
import com.jaly.touchscreenor.sys.TaskItem;
import com.jaly.touchscreenor.sys.TaskManager;
import com.jaly.touchscreenor.sys.TaskService;
import com.jaly.touchscreenor.util.ComfirmDialog;

@ContentView(R.layout.fragment_main_task)
public class FragmentTask extends Fragment{
	
	private MainActivity mainActivity;
	private List<TaskItem> taskItems;
	
	@ViewInject(R.id.lv_task_list)
	private ListView taskList;
	private TaskListAdapter taskListAdapter;
	private TaskManager taskManager;
	
	@ViewInject(R.id.btn_task_add)
	private Button btnAddTask; 
	
	private ScriptManager scriptManager;
	private List<ScriptInfo> scriptInfos;
	private int selItemIdx; 
	private ScriptListAdapter scriptListAdapter;  
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) { 
		super.onActivityCreated(savedInstanceState); 		
		mainActivity = (MainActivity) getActivity();
		taskManager = TaskManager.getManager(mainActivity);
		scriptManager = new ScriptManager(mainActivity); 		 
		taskListAdapter = new TaskListAdapter(mainActivity, null); 
		taskList.setOnCreateContextMenuListener(this);		
		refreshTaskList();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = new MenuInflater(mainActivity);
		inflater.inflate(R.menu.menu_task_list, menu);
	}

	@Event(value={R.id.lv_task_list, R.layout.dialog_script_list}, type=OnItemClickListener.class)
	private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(parent.getId() == R.id.lv_task_list){
			TaskItem item = taskItems.get(position); 
			TaskDetailDialog detailDialog = new TaskDetailDialog(item);
			detailDialog.show(mainActivity.getFragmentManager(), null);
		} else if(parent.getId() == R.layout.dialog_script_list){
			selItemIdx = position;
			scriptListAdapter.setSeclection(position);
			scriptListAdapter.notifyDataSetChanged();
		}		
	} 
	
	/**
	 * 刷新任务列表
	 */
	public void refreshTaskList(){
		taskItems = taskManager.getAllTaskItems();
		taskListAdapter.setTaskItems(taskItems);
		if(taskList.getAdapter() == null){
			taskList.setAdapter(taskListAdapter);
		} else {
			taskListAdapter.notifyDataSetChanged();			
		}
	}
	
	private ListView getScriptList(){
		try {
			scriptInfos = scriptManager.listScripts();
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}
		ListView listView = (ListView) View.inflate(mainActivity,
				R.layout.dialog_script_list, null);
		scriptListAdapter = new ScriptListAdapter(mainActivity, scriptInfos);
		listView.setAdapter(scriptListAdapter); 
		return listView;
	} 

	@Event(value={R.id.btn_task_add}, type=OnClickListener.class)
	private void onClick(View v) {
		if(v.getId() == R.id.btn_task_add){
			if(ComfirmDialog.showComfirmDialog(mainActivity, "选择任务脚本", getScriptList())){
				ScriptInfo info = scriptInfos.get(selItemIdx);
				Intent intent = new Intent(mainActivity, TaskService.class);
				intent.putExtra("onTime", info.getFileName());
				mainActivity.startService(intent); 
			} 
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(taskItems != null && !taskItems.isEmpty()){
			TaskItem taskItem = taskItems.get(selItemIdx);
			switch (item.getItemId()) {
			case R.id.menu_task_del:  // 删除任务
				String message = "确定删除" + taskItem.getFileName() + "任务吗？";
				if(ComfirmDialog.showComfirmDialog(mainActivity, "删除任务", message)){
					taskManager.removeTask(taskItem.getId());
					refreshTaskList();
				}
				break;
			case R.id.menu_task_edt:  // 修改任务
				Intent intent = new Intent(mainActivity, TaskService.class);
				intent.putExtra("onTime", taskItem.getFileName());
				mainActivity.startService(intent); 
				break; 
			default: break;
			}
		}
		return super.onContextItemSelected(item);
	}
}
