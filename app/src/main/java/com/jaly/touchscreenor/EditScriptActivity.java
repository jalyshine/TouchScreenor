package com.jaly.touchscreenor;

import java.io.IOException;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaly.touchscreenor.coding.ScriptManager;
import com.jaly.touchscreenor.coding.TagAction;
import com.jaly.touchscreenor.coding.TagClick;
import com.jaly.touchscreenor.coding.TagInput;
import com.jaly.touchscreenor.coding.TagSelect;
import com.jaly.touchscreenor.coding.TagSwipe;
import com.jaly.touchscreenor.util.ComfirmDialog;

@ContentView(R.layout.activity_edit_script)
public class EditScriptActivity extends Activity{ 
	
	private final static String TAG = EditScriptActivity.class.getSimpleName();
	
	@ViewInject(R.id.edit_script_code)
	private EditText editText; 
	@ViewInject(R.id.text_file_name)
	private TextView textView;
	
	private String fileName = null;
	private boolean isModifing;
	private static int num = 1;
	private OperationManager optMgr;
	
	private ScriptManager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		x.view().inject(this);
		optMgr = OperationManager.setup(editText); 
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		manager = new ScriptManager(this);
		isModifing = false;
		Intent intent = getIntent(); 
		String content = "";
		if(intent.hasExtra("file")){
			isModifing = true;
			fileName = intent.getStringExtra("file"); 
			try {
				content = manager.readSource(fileName);
			} catch (IOException e) { 
				Log.e(TAG, e.getMessage());
			} 
		} else if(intent.hasExtra("content")){
			fileName = "新建脚本" + (num++);
			content = intent.getStringExtra("content");
		}
		editText.setText(content);
		textView.setText(fileName);
	}
	
	@Event(value={R.id.btn_code_undo, R.id.btn_code_redo, R.id.btn_code_save})
	private void fileEvent(View view){
		switch(view.getId()){
		case R.id.btn_code_undo:
			if(optMgr.canUndo()){
				optMgr.undo();			
			}
			break;
		case R.id.btn_code_redo:
			if(optMgr.canRedo()){
				optMgr.redo();			
			}
			break;
		case R.id.btn_code_save:
			if(isModifing){  
				saveScript();
			} else { 
				EditText editInput = new EditText(this);
				editInput.setText(fileName);
				if(ComfirmDialog.showComfirmDialog(this, null, editInput)){
					fileName = editInput.getText().toString();
					if(!"".equals(fileName.trim())){
						saveScript();
					} 
				} 
			} 
			break;
		}
	} 
	
	/**
	 * 保存脚本到文件
	 */
	private void saveScript(){
		new Thread(new Runnable() { 
			@Override
			public void run() {
				boolean flag = false;
				try {
					manager.save(fileName, editText.getText().toString());
					flag = true;
				} catch (Exception e) { 
					Log.e(TAG, e.getMessage());
				} 
				final String msg = flag?"脚本保存成功！":"脚本保存失败！";
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(EditScriptActivity.this, msg, Toast.LENGTH_SHORT).show();
						Intent it = new Intent();  
						it.setAction(MainActivity.ACTION_REFRESHSCRIPT);
						sendBroadcast(it);
						finish();
					}
				});
			}
		}).start();
	}
	

	@Event(value={R.id.btn_insert_action, R.id.btn_insert_click, 
			R.id.btn_insert_input, R.id.btn_insert_swipe, 
			R.id.btn_insert_select, R.id.btn_delete_tag})
	private void codeEvent(View v){
		String code = "";
		switch(v.getId()){ 
		case R.id.btn_insert_action:
			code = new TagAction().toString();
			break;
		case R.id.btn_insert_click:
			code = new TagClick().toString();
			break;
		case R.id.btn_insert_input:
			code = new TagInput().toString();
			break;
		case R.id.btn_insert_swipe:
			code = new TagSwipe().toString();
			break;
		case R.id.btn_insert_select:
			code = new TagSelect().toString();
			break;
		case R.id.btn_delete_tag:
			int start = editText.getSelectionStart();
			int end = editText.getSelectionEnd();
			if(start == end){
				Layout layout = editText.getLayout();
				int line = layout.getLineForOffset(start);
				start = layout.getLineStart(line);
				end = layout.getLineEnd(line);
			}
			Editable editable = editText.getText();
			editable.delete(start, end); 
			break;
		default:
			break;
		}
		int index = editText.getSelectionStart();
		Editable editable = editText.getText();
		editable.insert(index, code);
	} 
	
}
