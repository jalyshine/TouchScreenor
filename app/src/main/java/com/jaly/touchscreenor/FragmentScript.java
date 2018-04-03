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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jaly.touchscreenor.coding.ScriptInfo;
import com.jaly.touchscreenor.coding.ScriptManager;
import com.jaly.touchscreenor.floatwnd.FloatWndService;
import com.jaly.touchscreenor.sys.TaskService;
import com.jaly.touchscreenor.util.ComfirmDialog;

/**
 * 任务页面
 * 
 * @author Administrator
 * 
 */
@ContentView(R.layout.fragment_main_script)
public class FragmentScript extends Fragment {

	private final static String TAG = FragmentScript.class.getSimpleName();

	@ViewInject(R.id.bk_auto_coding)
	private RelativeLayout bkAutoBtn;
	@ViewInject(R.id.btn_auto_coding)
	private Button autoBtn;
	@ViewInject(R.id.lv_scirpt_list)
	private ListView listView;
	
	private MainActivity mainActivity;
	private ScriptListAdapter scriptListAdapter;
	private List<ScriptInfo> scriptInfos;
	private ScriptManager scriptManager; 
	private int curIdx;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		return x.view().inject(this, inflater, container);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) { 
		super.onActivityCreated(savedInstanceState);
		ViewTreeObserver vto = bkAutoBtn.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				int height = bkAutoBtn.getMeasuredHeight();
				int width = bkAutoBtn.getMeasuredWidth();
				int wh = (int) (Math.min(width, height) * 0.9);
				ViewGroup.LayoutParams params = autoBtn.getLayoutParams();
				params.width = wh;
				params.height = wh;
				autoBtn.setLayoutParams(params);
				return true;  
			}
		}); 
		
		mainActivity = (MainActivity) getActivity();
		scriptManager = new ScriptManager(mainActivity);		
	    scriptListAdapter = new ScriptListAdapter(mainActivity, null); 
		listView.setOnCreateContextMenuListener(this); 
		refreshScriptList();
	}
	
	@Event(value={R.id.btn_auto_coding})
	private void onClick(View view){
		if(view.getId() == R.id.btn_auto_coding){
			Intent intent = new Intent(mainActivity, FloatWndService.class); 
			intent.putExtra("app", "");
			mainActivity.startService(intent);
		}
	}
	
	@Event(value={R.id.lv_scirpt_list}, type=OnItemClickListener.class)
	private void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		Intent intent = new Intent(mainActivity, EditScriptActivity.class);
		intent.putExtra("file", scriptInfos.get(position).getFileName());
		startActivity(intent);
	}
	
	@Event(value={R.id.lv_scirpt_list}, type=OnItemLongClickListener.class)
	private boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		curIdx = position;
		return false;
	} 

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = new MenuInflater(mainActivity);
		inflater.inflate(R.menu.menu_script_list, menu);
	}

	/**
	 * 刷新脚本列表
	 */
	public void refreshScriptList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					scriptInfos = scriptManager.listScripts();
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				} 
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() { 
						scriptListAdapter.setInfos(scriptInfos);
						scriptListAdapter.setSeclection(-1);
						if(listView.getAdapter() == null){
							listView.setAdapter(scriptListAdapter);
						} else {
							scriptListAdapter.notifyDataSetChanged(); 							
						}
					}
				});
			}
		}).start();
	} 

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final String fileName = scriptInfos.get(curIdx).getFileName();
		switch (item.getItemId()) {
		case R.id.menu_script_run: // 立即运行 
			Intent intent = new Intent(mainActivity, TaskService.class);
			intent.putExtra("atOnce", fileName);
			mainActivity.startService(intent);
			break;
		case R.id.menu_script_rnm: // 重命名
			final EditText editText = new EditText(mainActivity);
			editText.setText(fileName);
			if (ComfirmDialog.showComfirmDialog(mainActivity, "重命名", editText)) {
				String newName = editText.getText().toString();
				scriptManager.rename(fileName, newName);
				refreshScriptList();
			}
			break;
		case R.id.menu_script_del: // 删除脚本
			String message = "你确定删除脚本" + fileName + "吗？";
			if (ComfirmDialog.showComfirmDialog(mainActivity, "删除脚本", message)) {
				scriptManager.delete(fileName);
				refreshScriptList();
			}
			break;
		case R.id.menu_script_upload:
			new Thread(new Runnable() {
				@Override
				public void run() {
					scriptManager.upload(fileName, "http://192.168.2.103/admin/Site/upload");
					mainActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(mainActivity, "upload is ok", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}).start();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

}
