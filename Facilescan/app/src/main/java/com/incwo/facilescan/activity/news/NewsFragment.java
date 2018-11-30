package com.incwo.facilescan.activity.news;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.incwo.facilescan.R;
import com.incwo.facilescan.helpers.fragments.BaseListFragment;
import com.incwo.facilescan.activity.application.BaseTabActivity;
import com.incwo.facilescan.helpers.rss.Rss;
import com.incwo.facilescan.helpers.rss.RssItem;
import com.incwo.facilescan.managers.SingleApp;
import com.incwo.facilescan.managers.WebService;

import java.util.ArrayList;

public class NewsFragment extends BaseListFragment {

	private AsyncTask<?, ?, ?> GetNews = null;
	private Rss rss = null;
	private View mRoot;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mRoot = inflater.inflate(R.layout.news_fragment, null);
    	return mRoot;
	}
    
    public void onHiddenChanged(boolean hidden) {
    	rss = SingleApp.getNewsRss();
		if(rss.items != null){
			NewsAdapter newsAdapter = new NewsAdapter(getActivity(), rss.items);
			setListAdapter(newsAdapter);
			((BaseAdapter) getListAdapter()).notifyDataSetChanged();
		}
    }
    
    @Override
    public void onResume () {
    	super.onResume();
		if (GetNews != null && GetNews.getStatus() == AsyncTask.Status.RUNNING) {
			GetNews.cancel(true);
		} 	
		GetNews = new AsyncTaskGetNews().execute();
		rss = SingleApp.getNewsRss();
		if(rss.items != null){
			if(rss.items.size() > 0)
				mRoot.findViewById(R.id.LOADING).setVisibility(View.GONE);
			NewsAdapter newsAdapter = new NewsAdapter(this.getActivity(), rss.items);
			setListAdapter(newsAdapter);
			
		}
    }
    
    private class NewsAdapter extends ArrayAdapter<RssItem> {  
		private LayoutInflater mInflater;

		NewsAdapter(Activity context, ArrayList<RssItem> arrayList) {
			super(context, R.layout.news_row, arrayList);
			mInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			RssItem item = getItem(position);
			
			Row rowView = null;
			if (convertView == null) {
				rowView = new Row();
				convertView = mInflater.inflate(R.layout.news_row, null);	
				rowView.title = (TextView) convertView.findViewById(R.id.title);
				rowView.date = (TextView) convertView.findViewById(R.id.date);
			
				// Set the tag of the current view
				convertView.setTag(rowView);
			}
			else
				rowView = (Row)convertView.getTag();
			rowView.title.setText(item.getTitle());
			ArrayList<String> readNews = SingleApp.getReadNews();
			if(!readNews.contains(item.guid))
				rowView.title.setTypeface(null, Typeface.BOLD);
			else
				rowView.title.setTypeface(null, Typeface.NORMAL);
			rowView.date.setText(item.getFormattedDate() + " " + item.getHour());
			
			return(convertView);
		}

		// Object container for view fields
		class Row {
			
			TextView title;
			TextView date;
		}  
	}
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        SingleApp.setSelectedNews(position);
        getTabActivity().pushFragment(BaseTabActivity.TAB_NEWS, new NewsDetailFragment());
    }
    
    
    private class AsyncTaskGetNews extends AsyncTask<String, Integer, Long> {
		WebService rssFeeder;
		
		protected void onPreExecute() {
	
		}
	
		protected Long doInBackground(String... tasks) {
			long result = 0;
			rssFeeder = new WebService();
			rssFeeder.getNews();
			result = rssFeeder.responseCode;
			SingleApp.setRssNewsXml(rssFeeder.body);
			rss = SingleApp.getNewsRss();
			if(rss.items != null){
				if(SingleApp.getReadNews().isEmpty()){
					for(RssItem item : rss.items){
						SingleApp.addReadNews(item.guid);
					}
				}
			}
			
			
			return result;
		}
	
		protected void onProgressUpdate(Integer... progress) {
		}
	
		protected void onPostExecute(Long result) {
	        	
			if (result >= 200 && result < 300)
			{
				rss = SingleApp.getNewsRss();
				if(rss.items != null){
					if(SingleApp.getReadNews().isEmpty()){
						for(RssItem item : rss.items){
							SingleApp.addReadNews(item.guid);
						}
					}
					
					NewsAdapter newsAdapter = new NewsAdapter(getActivity(), rss.items);
					setListAdapter(newsAdapter);
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
			    	getTabActivity().setNewsBadgeCount(SingleApp.getCountOfUnreadNews());
				}
				
				mRoot.findViewById(R.id.LOADING).setVisibility(View.GONE);
			}
			else {
	        	WebService.showError(result);
	        }
			
			GetNews = null;
		}
	}
    
	public void onDestroy() {
		super.onDestroy();		
		
		 if (GetNews != null && GetNews.getStatus() == AsyncTask.Status.RUNNING) {
			 GetNews.cancel(true);
			} 	
		 GetNews = null;
	}
    
}