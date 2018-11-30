package com.incwo.facilescan.helpers.rss;

import android.text.format.DateUtils;

import com.incwo.facilescan.app.FacilescanApp;

import java.util.ArrayList;
import java.util.Date;



public class RssItem {
	public String guid;

	public String title;
	public String description;
	public String htmlFullText;
	public String sPubDate;
	public Date pubDate;
	public String link;
	public String legend;
	public String name;
	public String firstName;


	public ArrayList<Enclosure> images;
	public ArrayList<Enclosure> videos;
	public ArrayList<Enclosure> audios;

	public String author;
	public String source;
	public String category;
	private String	  imageHeightRatio = "";


	public RssItem() {
		guid = "";

		title = "";
		description = "";
		sPubDate = "";
		pubDate = null;
		link = "";
		name = "";
		firstName = "";

		images = new ArrayList<Enclosure>();
		videos = new ArrayList<Enclosure>();
		audios = new ArrayList<Enclosure>();

		author = "";
		source = "";
		category = "";
		legend = "";
		htmlFullText = "";
		
	}

	//
	// GUI Helpers
	public String getId() {
		return guid;
	}

	public String getTitle() {
		return title;
	}

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return link;
	}

	public String getNewsImageUrl() {
		if (images.size() == 0)
			return "";
		else
			return images.get(images.size()-1).url;
	}
	
	public String getVideoUrl() {
		if (videos.size() == 0)
			return "";
		else
			return videos.get(0).url;
	}
	
	
	
	public int getNativeImageWidth() {
		if (images.size() == 0)
			return 0;
		else
			return images.get(0).width;
	}
	
	public int getNativeImageHeight() {
		if (images.size() == 0)
			return 0;
		else
			return images.get(0).height;
	}

	public String getImageHeightRatio() {
		return imageHeightRatio;
	}

	public void setImageHeightRatio(String imageHeightRatio) {
		this.imageHeightRatio = imageHeightRatio;
	}
	
	public String getNewsImageLegend() {
		if (images.size() == 0)
			return "";
		else if(images.get(0).legend.equals(""))
			return legend;
		else
			return images.get(0).legend;
	}

	public String getAuthorName() {
		return firstName+ " " + name;
	}

	public String getSourceName() {
		return source;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public String getFormattedDate() {
		return DateUtils.formatDateTime(FacilescanApp.getInstance().getApplicationContext(), pubDate.getTime(), DateUtils.FORMAT_SHOW_YEAR).toString();
	}
	
	public String getHour() {
		return DateUtils.formatDateTime(FacilescanApp.getInstance().getApplicationContext(), pubDate.getTime(), DateUtils.FORMAT_SHOW_TIME).toString();
	}
	
	public String getHtmlFullText(){
		return htmlFullText;
	}
	
	
}
