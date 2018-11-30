
package com.incwo.facilescan.activity.videos;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.incwo.facilescan.R;
import com.incwo.facilescan.app.FacilescanApp;
import com.incwo.facilescan.helpers.fragments.BaseListFragment;
import com.incwo.facilescan.activity.application.BaseTabActivity;
import com.incwo.facilescan.helpers.videos.VideoItem;
import com.incwo.facilescan.helpers.videos.VideoXml;
import com.incwo.facilescan.managers.SingleApp;
import com.incwo.facilescan.managers.WebService;

import java.util.ArrayList;




public class VideosFragment extends BaseListFragment {

	private AsyncTask<?, ?, ?> GetImage = null;
	private VideoXml xml = null;
	private View mRoot;
	private ArrayList<VideoItem> selectedVideos = new ArrayList<VideoItem>();
	private String allVideos;


    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Resources resources = FacilescanApp.getInstance().getResources();
    	allVideos = resources.getString(R.string.All_videos);
    	xml = SingleApp.getVideosXml();
    	
    	if(SingleApp.getSelectedCategorie().equals(allVideos))
    		selectedVideos.addAll(xml.items);
    	else{
    		for(int i = 0; i < xml.items.size(); i++){
    			if(xml.items.get(i).categories.contains(SingleApp.getSelectedCategorie()))
    				selectedVideos.add(xml.items.get(i));
    		}
    	}
    	
         VideosAdapter videosAdapter = new VideosAdapter(this.getActivity(), selectedVideos);
         setListAdapter(videosAdapter);
		mRoot = inflater.inflate(R.layout.videos_fragment, null);
		return mRoot;
	}
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        VideoItem item = selectedVideos.get(position);
        SingleApp.setSelectedVideo(item.getPermalink());
        getTabActivity().pushFragment(BaseTabActivity.TAB_VIDEO, new VideoDetailsFragment());
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
    
    public void onDestroy() {
		super.onDestroy();		
		
		 if (GetImage != null && GetImage.getStatus() == AsyncTask.Status.RUNNING) {
			 GetImage.cancel(true);
			} 	
		 GetImage = null;
	}
    
}