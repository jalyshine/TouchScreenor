package com.jaly.touchscreenor.util;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jaly.touchscreenor.R;

@ContentView(R.layout.fragment_dialog)
public class CommenDialog extends DialogFragment {

	@ViewInject(R.id.fd_cancle)
	private Button cancleBtn;
	@ViewInject(R.id.fd_ok)
	private Button okBtn;
	@ViewInject(R.id.fd_view)
	private LinearLayout layout;

	private String title;
	private View view; 

	public CommenDialog(String title, View view) { 
		this.title = title;
		this.view = view;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (title == null) {
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); 
		} else {
			getDialog().setTitle(title);
		}
		Drawable back = getResources().getDrawable(android.R.drawable.alert_dark_frame);
		getDialog().getWindow().setBackgroundDrawable(back);
		return x.view().inject(this, inflater, container);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		cancleBtn.setText("取消");
		okBtn.setText("确定");
		if (view != null) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			layout.addView(view, 0, params);
			layout.addView(view);
		}
	}

	@Event(value = { R.id.fd_cancle, R.id.fd_ok })
	private void onClick(View view) {
		dismiss();
	}

	public void show() { 
	}

}
