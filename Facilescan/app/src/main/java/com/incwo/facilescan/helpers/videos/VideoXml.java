package com.incwo.facilescan.helpers.videos;

import java.util.ArrayList;
import java.util.Collections;

public class VideoXml  {
	public ArrayList<VideoItem> items;
	public ArrayList<String> categories;
	
    public VideoXml() {
		categories = new ArrayList<String> ();
		items = new ArrayList<VideoItem>();
	}

	@SuppressWarnings("unchecked")
	public void processXmLContent(String xml) {
		XmlParser xmlParser = new XmlParser();
		xmlParser.readFromXmlContent(xml);
		if (xmlParser.xml != null)
		{
			items = (ArrayList<VideoItem>) xmlParser.xml.items.clone();	
			categories = (ArrayList<String>) xmlParser.xml.categories.clone();	
			Collections.sort(categories);
		}
	}
}
