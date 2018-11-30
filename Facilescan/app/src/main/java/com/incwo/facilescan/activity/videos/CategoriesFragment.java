
package com.incwo.facilescan.activity.videos;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.incwo.facilescan.R;
import com.incwo.facilescan.app.FacilescanApp;
import com.incwo.facilescan.helpers.Unnacenter;
import com.incwo.facilescan.helpers.fragments.BaseListFragment;
import com.incwo.facilescan.activity.application.BaseTabActivity;
import com.incwo.facilescan.helpers.videos.VideoItem;
import com.incwo.facilescan.helpers.videos.VideoXml;
import com.incwo.facilescan.managers.SingleApp;
import com.incwo.facilescan.managers.WebService;

import java.util.ArrayList;


public class CategoriesFragment extends BaseListFragment {

	private AsyncTask<?, ?, ?> GetVideos = null;
	private AsyncTask<?, ?, ?> GetImage = null;
	private VideoXml xml = null;
	private View mRoot;
	private ArrayList<String> allCategories = new ArrayList<String>();
	private String allVideos;
	private AutoCompleteTextView search_bar = null;
	private ArrayList<VideoItem> selectedVideos = new ArrayList<VideoItem>();

	private ListView mListView;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Resources resources = FacilescanApp.getInstance().getResources();
    	mRoot = inflater.inflate(R.layout.categories_fragment, null);
    	allVideos = resources.getString(R.string.All_videos);
    	
    	search_bar = (AutoCompleteTextView)mRoot.findViewById(R.id.search_bar_plan);
		search_bar.setCursorVisible(false);
		search_bar.dismissDropDown();
		Button clear_button = (Button)mRoot.findViewById(R.id.clear_search_bar_plan);
		
		clear_button.setOnClickListener(mClearSearchBarListener);
		search_bar.setOnTouchListener(mShowDarkOverlayListener);
		search_bar.setOnEditorActionListener(mSearchBarListener);
		search_bar.addTextChangedListener(mToggleClearButtonListener);

		mListView = mRoot.findViewById(android.R.id.list);
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == SCROLL_STATE_TOUCH_SCROLL) {
					// Hide the virtual keyboard
						InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity
								.INPUT_METHOD_SERVICE);
						if(inputMethodManager != null) {
							inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
						}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});

		
		return mRoot;
	}

    
 // clear search_bar listener
 	View.OnClickListener mClearSearchBarListener = new View.OnClickListener() {

 		@Override
 		public void onClick(View v) {
 			Button clear_button = (Button)mRoot.findViewById(R.id.clear_search_bar_plan);
 			search_bar.setText("");
 			clear_button.setVisibility(View.GONE);
 		}
 	};
 	
 // show darkOverlay when user touch search_bar
 	View.OnTouchListener mShowDarkOverlayListener = new View.OnTouchListener() {

 		@Override
 		public boolean onTouch(View v, MotionEvent event) {
 			if (event.getAction() == android.view.MotionEvent.ACTION_UP)
 			{
 				search_bar.setCursorVisible(true);
 				
 			}
 			return false;
 		}
 	};
 	
 // handle research query
 	TextView.OnEditorActionListener mSearchBarListener = new TextView.OnEditorActionListener() {

 		@Override
 		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

 			if (actionId == EditorInfo.IME_NULL  
 					&& event.getAction() == KeyEvent.ACTION_DOWN) 
 			{
 				  InputMethodManager imm = (InputMethodManager)CategoriesFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
 					imm.hideSoftInputFromWindow(((EditText)mRoot.findViewById(R.id.search_bar_plan)).getWindowToken(), 0);
 			}

 			return false;
 		}
 	};
    
 	
 	
 // handle clear_search_bar_plan button visibility
 	TextWatcher mToggleClearButtonListener = new TextWatcher() {

 		public void afterTextChanged(Editable s) {

 		}


 		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

 		}

 		public void onTextChanged(CharSequence s, int start, int before, int count) {
 			Button clear_button = (Button)mRoot.findViewById(R.id.clear_search_bar_plan);
 			if (search_bar.getText().toString() == "")
 				clear_button.setVisibility(View.GONE);		
 			else
 				clear_button.setVisibility(View.VISIBLE);	

 			selectedVideos.clear();
 			if(search_bar.getText().length() > 0)
 			{
 				for(int i = 0; i < xml.items.size(); i++){
 								
	        		String temp = Unnacenter.removeAccents(xml.items.get(i).title.toLowerCase());
	        		String temp2 = Unnacenter.removeAccents(search_bar.getText().toString().toLowerCase());
 					
 					if(temp.contains(temp2))
 						selectedVideos.add(xml.items.get(i));
 				}
 				VideosAdapter videosAdapter = new VideosAdapter(CategoriesFragment.this.getActivity(), selectedVideos);
 				setListAdapter(videosAdapter);
 			}
 			else
 			{
 				CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getActivity(), allCategories);
				setListAdapter(categoriesAdapter);
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
 			}
 		}
 	};
 	
    @Override
    public void onResume() {
    	super.onResume();
		if (GetVideos != null && GetVideos.getStatus() == AsyncTask.Status.RUNNING) {
			GetVideos.cancel(true);
		}
		
		GetVideos = new AsyncTaskGetVideos().execute();
		xml = SingleApp.getVideosXml();
		if(xml.items != null){
			if(xml.items.size() > 0)
				mRoot.findViewById(R.id.LOADING).setVisibility(View.GONE);
	    	allCategories.add(allVideos);
	    	allCategories.addAll(xml.categories);
	    	
	    	
	    	
	    	if(selectedVideos.isEmpty()){
	    		CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this.getActivity(), xml.categories);
	    		setListAdapter(categoriesAdapter);
	    	}
	    	else{
	    		if(search_bar.getText().length() > 0){
 				VideosAdapter videosAdapter = new VideosAdapter(CategoriesFragment.this.getActivity(), selectedVideos);
 				setListAdapter(videosAdapter);
	    		}
	    		else
	    			selectedVideos.clear();
	    	}
    	}	
		
		
    }

    
    public void update(){
    	selectedVideos.clear();
    	for(int i = 0; i < xml.items.size(); i++){
    		String temp = Unnacenter.removeAccents(xml.items.get(i).title.toLowerCase());
    		String temp2 = Unnacenter.removeAccents(search_bar.getText().toString().toLowerCase());
				
				if(temp.contains(temp2))
					selectedVideos.add(xml.items.get(i));
			}
   
    }
    
    @Override
    public void onDestroy() {
    	if (GetVideos != null && GetVideos.getStatus() == AsyncTask.Status.RUNNING) {
    		GetVideos.cancel(true);
    	}
    	super.onDestroy();
    }
    
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(selectedVideos.isEmpty()){
        	 SingleApp.setSelectedCategorie(allCategories.get(position));
        	 getTabActivity().pushFragment(BaseTabActivity.TAB_VIDEO, new VideosFragment());
        }
        else{
        	VideoItem item = selectedVideos.get(position);
            SingleApp.setSelectedVideo(item.getPermalink());
            getTabActivity().pushFragment(BaseTabActivity.TAB_VIDEO, new VideoDetailsFragment());
        }
        
        InputMethodManager imm = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)mRoot.findViewById(R.id.search_bar_plan)).getWindowToken(), 0);
    }
    
    private class CategoriesAdapter extends ArrayAdapter<String> {  
		private LayoutInflater mInflater;

		CategoriesAdapter(Activity context, ArrayList<String> arrayList) {
			super(context, R.layout.row_video_category, arrayList);
			mInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			String categorie = getItem(position);
		
			Row rowView = new Row();
			convertView = mInflater.inflate(R.layout.row_video_category, null);
		
			rowView.titleTextView = (TextView) convertView.findViewById(R.id.title);
			rowView.countTextView = (TextView) convertView.findViewById(R.id.count);
			
			convertView.setTag(categorie);
			rowView.titleTextView.setText(categorie);
			int count = 0;
			if(categorie.equals(allVideos))
				count = xml.items.size();
			for(int i = 0; i< xml.items.size(); i++){
				if(xml.items.get(i).categories.contains(categorie))
					count++;
			}
			rowView.countTextView.setText(String.valueOf(count));
			return(convertView);
		}

		// Object container for view fields
		class Row {
			
			TextView titleTextView;
			TextView countTextView;
			
		}  
	}

    
    
    
    private class VideosAdapter extends ArrayAdapter<VideoItem> {  
		private LayoutInflater mInflater;

		VideosAdapter(Activity context, ArrayList<VideoItem> arrayList) {
			super(context, R.layout.videos_row, arrayList);
			mInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			VideoItem item = getItem(position);
			
			Row rowView = new Row();
			convertView = mInflater.inflate(R.layout.videos_row, null);	
		
			rowView.title = (TextView) convertView.findViewById(R.id.title);
			rowView.thumbnail = (ImageView) convertView.findViewById(R.id.THUMB);
			
			if(SingleApp.getImage(item.getThumbnail()) == null)
				GetImage = new DownloadImageTask(rowView.thumbnail).execute(item.getThumbnail());
			else
				rowView.thumbnail.setImageBitmap(SingleApp.getImage(item.getThumbnail()));
			convertView.setTag(item.getId());
			rowView.title.setText(item.getTitle());
			return(convertView);
		}

		// Object container for view fields
		class Row {
			
			TextView title;
			ImageView thumbnail;
			
		}  
    }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;
	    WebService imageDownloader;
	
	    public DownloadImageTask(ImageView bmImage) {
	    	this.bmImage = bmImage;
	        imageDownloader = new WebService();
	    }
	
	    protected Bitmap doInBackground(String... urls) {
	        return imageDownloader.downloadBitmapAndResize(urls[0], 157, 105, false);
	    }
	
	    protected void onPostExecute(Bitmap result) {
	    	if (result != null)
	    		bmImage.setImageBitmap(result);
	    		
	    }
	}
    
    
    
    
    private class AsyncTaskGetVideos extends AsyncTask<String, Integer, Long> {
		WebService rssFeeder;
		
		protected void onPreExecute() {
	
		}
	
		protected Long doInBackground(String... tasks) {
			long result = 0;
			rssFeeder = new WebService();
			rssFeeder.getVideos();
			result = rssFeeder.responseCode;
			SingleApp.setVideosXml(rssFeeder.body);
			return result;
		}
	
		protected void onProgressUpdate(Integer... progress) {
		}
	
		protected void onPostExecute(Long result) {
	        	
			if (result >= 200 && result < 300)
			{
				xml = SingleApp.getVideosXml();
//				Log.d("video_content", "videos xml content : " + xml.businessFiles);
				allCategories = new ArrayList<String>();
				allCategories.add(allVideos);
		    	allCategories.addAll(xml.categories);
				CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getActivity(), allCategories);
				setListAdapter(categoriesAdapter);
				((BaseAdapter) getListAdapter()).notifyDataSetChanged();
			
				
				mRoot.findViewById(R.id.LOADING).setVisibility(View.GONE);
			}
			else {
	        	WebService.showError(result);
	        }
			
			GetVideos = null;
		}
	}
    
}