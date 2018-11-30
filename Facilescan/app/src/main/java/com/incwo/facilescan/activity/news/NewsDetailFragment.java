package com.incwo.facilescan.activity.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.incwo.facilescan.R;
import com.incwo.facilescan.helpers.fragments.TabFragment;
import com.incwo.facilescan.managers.SingleApp;


public class NewsDetailFragment extends TabFragment {
	private View mRoot;
	private WebView mWv;
	private ImageView nav_reload;
	private ImageView nav_stop;
	private ImageView nav_back;
	private ImageView nav_forward;
	private ProgressBar loader;
	private String current_url = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mRoot = inflater.inflate(R.layout.news_detail, null);
    	mWv = (WebView)mRoot.findViewById(R.id.WEBVIEW);
    	nav_reload = (ImageView)mRoot.findViewById(R.id.nav_reload);
    	nav_stop = (ImageView)mRoot.findViewById(R.id.nav_stop);
    	nav_back = (ImageView)mRoot.findViewById(R.id.nav_back);
    	nav_forward = (ImageView)mRoot.findViewById(R.id.nav_forward);
    	loader = (ProgressBar)mRoot.findViewById(R.id.LOADER);
    	
    	
    	// this prevent previous fragment to catch onTouch event
    	mRoot.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    	
    	nav_reload.setOnClickListener(reloadListener);
    	nav_stop.setOnClickListener(stopListener);
    	nav_back.setOnClickListener(backListener);
    	nav_forward.setOnClickListener(forwardListener);
    	
    	nav_back.setAlpha(0.3f);
    	nav_forward.setAlpha(0.3f);
    	
    	SingleApp.addReadNews(SingleApp.getNewsRss().items.get(SingleApp.getSelectedNews()).guid);
    	getTabActivity().setNewsBadgeCount(SingleApp.getCountOfUnreadNews());
    	return mRoot;
	}
	 
	 private View.OnClickListener reloadListener = new View.OnClickListener() {
		 public void onClick(View view) {
			loader.setVisibility(View.VISIBLE);
			nav_stop.setVisibility(View.VISIBLE);
			nav_reload.setVisibility(View.GONE);
			if(current_url == null)
				loadHtml(formatHtml());
			else
				mWv.loadUrl( "javascript:window.location.reload( true )" );
		 }
	 };

	 private View.OnClickListener stopListener = new View.OnClickListener() {
		 public void onClick(View view) {
			 loader.setVisibility(View.GONE);
			 nav_stop.setVisibility(View.GONE);
				nav_reload.setVisibility(View.VISIBLE);
			 mWv.stopLoading();
		 }
	 };

	 private View.OnClickListener backListener = new View.OnClickListener() {
		 public void onClick(View view) {
			 if(mWv.canGoBack())
			 {
				 loader.setVisibility(View.VISIBLE);
				 nav_stop.setVisibility(View.VISIBLE);
				 nav_reload.setVisibility(View.GONE);
				 
				 WebBackForwardList webBackForwardList = mWv.copyBackForwardList();
				 if (webBackForwardList.getCurrentIndex() == 1 || current_url == null) // if no previous page
				 {
					 mWv.goBack();
					 loadHtml(formatHtml());
				 }
				 else
					mWv.goBack();
			 }
		 }
	 };

	 private View.OnClickListener forwardListener = new View.OnClickListener() {
		 public void onClick(View view) {
			 if(mWv.canGoForward())
			 {
				 loader.setVisibility(View.VISIBLE);
				 nav_stop.setVisibility(View.VISIBLE);
				 nav_reload.setVisibility(View.GONE);
				 mWv.goForward();
			 }
		 }
	 };


	 @Override
	    public void onResume(){
	    	super.onResume();
			loadHtml(formatHtml());
	    }
	
	 private String formatHtml(){
		 String stylePrefix = "<html><head><meta name=\"viewport\" content=\"width=600\"></head><body><style>\n*{ font-family: sans-serif}\nbody{ padding:10px}\nh1{ margin: 0 0 10px 0; font-size:2em; }\n</style>\n";
	    	String item = SingleApp.getNewsRss().items.get(SingleApp.getSelectedNews()).description;
	    	String title = SingleApp.getNewsRss().items.get(SingleApp.getSelectedNews()).title;
	    	String html = stylePrefix + "<h1>" + title + "</h1>" + item;
		 return html;
	 }
	 
	 private void loadHtml(String html) {
			// Init WebView
			mWv.scrollTo(0, 0);
			mWv.getSettings().setAllowFileAccess(true);
			mWv.getSettings().setJavaScriptEnabled(true);
			mWv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
			mWv.getSettings().setLightTouchEnabled(true);
			mWv.getSettings().setNeedInitialFocus(true);
			mWv.setBackgroundColor(0xffffffff);
			mWv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
			mWv.setWebViewClient(new WebViewClient(){

				   public void onPageStarted(WebView view, String url) {
					   nav_stop.setVisibility(View.VISIBLE);
					   nav_reload.setVisibility(View.GONE); 
					   loader.setVisibility(View.VISIBLE); 
				    }
				
				   public void onPageFinished(WebView view, String url) {
					   nav_stop.setVisibility(View.GONE);
					   nav_reload.setVisibility(View.VISIBLE);
					   loader.setVisibility(View.GONE); 
					   if(!mWv.canGoBack())
					   {
						   nav_back.setAlpha(0.3f);
						   current_url = null;
					   }
					   else
					   {
						   nav_back.setAlpha(1.0f);
					   }
					   if(!mWv.canGoForward())
					   {
						   nav_forward.setAlpha(0.3f);
					   }
					   else
					   {
						   nav_forward.setAlpha(1.0f);
					   }
				    }
				   
				   
				   public boolean shouldOverrideUrlLoading(WebView view, String url) {
				        if (url.equals("theURLYouDontWantToLoadInBrowser")) { 
				            //Do your thing 
				            return true;
				        } else {
				        	loader.setVisibility(View.VISIBLE);
				        	current_url = url;
				            return false;          
				        }
				   }
				
				   
				});

			nav_stop.setVisibility(View.VISIBLE);
			nav_reload.setVisibility(View.GONE);
			mWv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	    }
	 
	 
}
