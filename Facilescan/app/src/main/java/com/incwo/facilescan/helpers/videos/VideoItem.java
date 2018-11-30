package com.incwo.facilescan.helpers.videos;

import java.util.ArrayList;



public class VideoItem {
	public String id;
	public String youtube_id;
	public String permalink;
	public String title;
	public String created_at;
	public String categorie;
	public ArrayList<String> categories;
	


	public String getThumbnail() {
		// When we download this image, "_0.jpg" have to be "/0.jpg"
		// This fix is maybe temp
		return "http://img.youtube.com/vi/" + youtube_id + "_0.jpg";
	}




	public VideoItem() {
		id = "";
		youtube_id = "";
		permalink = "";
		title = "";
		created_at = "";
		categorie = "";
		categories = new ArrayList<String>();
	}




	public String getId() {
		return id;
	}




	public String getYoutube_id() {
		return youtube_id;
	}




	public String getPermalink() {
		return permalink;
	}




	public String getTitle() {
		return title;
	}




	public String getCreated_at() {
		return created_at;
	}




	public ArrayList<String> getCategories() {
		return categories;
	}

	
	
}
