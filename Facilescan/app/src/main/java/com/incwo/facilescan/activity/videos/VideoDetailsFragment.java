package com.incwo.facilescan.activity.videos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.incwo.facilescan.R;
import com.incwo.facilescan.helpers.fragments.TabFragment;
import com.incwo.facilescan.managers.SingleApp;
import com.incwo.facilescan.managers.URLProvider;
import com.incwo.facilescan.managers.WebService;

public class VideoDetailsFragment extends TabFragment {
	private View mRoot;
	private WebView mWebView;
	
	 @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	mRoot = inflater.inflate(R.layout.video_details, null);
	    	
	    	// this prevent previous fragment to catch onTouch event
	    	mRoot.setOnTouchListener(new View.OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                return true;
	            }
	        });
	    
	    	return mRoot;
		}
	 
	 
	 @Override
	    public void onResume(){
	    	super.onResume();
	    	
	    	mWebView = (WebView)mRoot.findViewById(R.id.WEBVIEW);
            mWebView.clearView();
            mWebView.scrollTo(0, 0);
            mWebView.getSettings().setAllowFileAccess(true);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mWebView.getSettings().setLightTouchEnabled(true);
            mWebView.getSettings().setNeedInitialFocus(true);
            mWebView.setBackgroundColor(0xffffffff);
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

            mWebView.loadUrl(URLProvider.getVideoUrl(SingleApp.getSelectedVideo()));
	    }
	 

	 @Override
	    public void onStop (){
	    	super.onStop();
	    	
	    	mWebView.destroy();
	 }
			   
	
}
