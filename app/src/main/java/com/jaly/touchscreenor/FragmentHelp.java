package com.jaly.touchscreenor;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

@ContentView(R.layout.fragment_main_help)
public class FragmentHelp extends Fragment {

	@ViewInject(R.id.wv_help_content)
	private WebView webView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		return x.view().inject(this, inflater, container);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) { 
		super.onActivityCreated(savedInstanceState);
		webView.loadUrl("file:///android_asset/help.html"); 
	}
	
	@Override
	public void onDestroy() {
		if(webView != null){
			webView.destroy();			
		}
		super.onDestroy();
	}
}
