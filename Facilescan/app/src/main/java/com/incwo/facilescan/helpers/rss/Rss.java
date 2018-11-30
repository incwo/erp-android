package com.incwo.facilescan.helpers.rss;

import java.util.ArrayList;

public class Rss  {
	public String title;
	public String description;
	public String link;

	public ArrayList<RssItem> items;
    
	public Rss() {
		title = "";
		description = "";
		link = "";

		items = new ArrayList<RssItem>();
	}

	@SuppressWarnings("unchecked")
	public void processXmLContent(String xml) {
		RssParser rssParser = new RssParser();
		rssParser.readFromXmlContent(xml);
		if (rssParser.rss != null)
		{
			title = rssParser.rss.title.trim();
			description = rssParser.rss.description.trim();
			link = rssParser.rss.link.trim();
			items = (ArrayList<RssItem>) rssParser.rss.items.clone();	
		}
	}
}
