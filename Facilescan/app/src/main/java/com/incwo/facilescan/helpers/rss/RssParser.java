package com.incwo.facilescan.helpers.rss;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RssParser extends DefaultHandler {
	
	StringBuilder sb;
	 
	
	public RssParser() {
		sb=new StringBuilder();
	}

	public Rss readFromXmlContent(String xml) {
		try {
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
			processSaxStream(inputStream);
			inputStream.close();
			return rss;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Rss readFromFile(String rssPath) {
		try {
			FileInputStream fileInputStream = new FileInputStream(rssPath);
			processSaxStream(fileInputStream);
			fileInputStream.close();
			return rss;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Rss readFromUrl(String rssUrlPath) {
		try {
			URL url = new URL(rssUrlPath);
			InputStream inputStream = url.openStream();
			processSaxStream(inputStream);
			inputStream.close();
			return rss;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// helpers
	private int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return 0;
		}
	}

	private Date parseDate(String value) {
		try {
			return RssDateParser.parse(value);
		}
		catch (Exception e) {
			return null;
		}
	}

	// Used to define what elements we are currently in
	private String inName = "";
	private boolean inItem = false;

	public Rss rss;
	private RssItem rssItem;

	public void startElement(String uri, String name, String qName, Attributes atts) {
		inName = name.trim();
		
		// efficient way to clear the StringBuilder
		sb.setLength(0);
		
		if (inName.equals("item")) {
			inItem = true;
			rssItem = new RssItem();
		} 

		if (inItem) {
			if (inName.equals("enclosure")) {
				Enclosure enclosure = new Enclosure();

				for (int index = 0; index < atts.getLength(); index++) {
					if (atts.getLocalName(index).equals("url"))
						enclosure.url = atts.getValue(index);
					else if (atts.getLocalName(index).equals("type"))
						enclosure.type = atts.getValue(index);
					else if (atts.getLocalName(index).equals("length"))
						enclosure.length = parseInt(atts.getValue(index));
					else if (atts.getLocalName(index).equals("width"))
						enclosure.width = parseInt(atts.getValue(index));
					else if (atts.getLocalName(index).equals("height"))
						enclosure.height = parseInt(atts.getValue(index));
					else if (atts.getLocalName(index).equals("legend"))
						enclosure.legend = atts.getValue(index);
				}

				if (enclosure.type.indexOf("image") == 0)
					rssItem.images.add(enclosure);
				else if (enclosure.type.indexOf("video") == 0)
					rssItem.videos.add(enclosure);
				else if (enclosure.type.indexOf("audio") == 0)
					rssItem.audios.add(enclosure);
			}
			else if (inName.equals("content")) {
				Enclosure enclosure = new Enclosure();

				for (int index = 0; index < atts.getLength(); index++) {
					if (atts.getLocalName(index).equals("url"))
						enclosure.url = atts.getValue(index);
					
					else if (atts.getLocalName(index).equals("width"))
						enclosure.width = parseInt(atts.getValue(index));
					else if (atts.getLocalName(index).equals("height"))
						enclosure.height = parseInt(atts.getValue(index));
					
				}
					rssItem.videos.add(enclosure);
				
			}
			else if (inName.equals("thumbnail")) {
				Enclosure enclosure = new Enclosure();

				for (int index = 0; index < atts.getLength(); index++) {
					if (atts.getLocalName(index).equals("url"))
						enclosure.url = atts.getValue(index);
					
					else if (atts.getLocalName(index).equals("width"))
						enclosure.width = parseInt(atts.getValue(index));
					else if (atts.getLocalName(index).equals("height"))
						enclosure.height = parseInt(atts.getValue(index));
					
				}
				rssItem.images.add(enclosure);
			}
		}
	}

	public void endElement(String uri, String name, String qName) throws SAXException {
		try {
			// If not in item, then title/link refers to channel
			if (!inItem) {
				if (inName.equals("title"))
					rss.title = sb.toString();
				else if (inName.equals("description"))
					rss.description = sb.toString();
				else if (inName.equals("link"))
					rss.link = sb.toString();
			} else {
				if (inName.equals("guid"))
					rssItem.guid = sb.toString();
				else if (inName.equals("title"))
					rssItem.title = sb.toString();
				else if (inName.equals("description"))
					rssItem.description = sb.toString();
				else if (inName.equals("pubDate"))
					rssItem.sPubDate = sb.toString();
				else if (inName.equals("link"))
					rssItem.link = sb.toString();
				else if (inName.equals("sourceName"))
					rssItem.source = sb.toString();
				else if (inName.equals("category"))
					rssItem.category = sb.toString();
			}
		} catch (Exception e) {
		}               

		
		if (name.trim().equals("item")) {
			inItem = false;

			rssItem.guid = rssItem.guid.trim();
			rssItem.title = rssItem.title.trim();
			rssItem.description = rssItem.description.trim();
			rssItem.sPubDate = rssItem.sPubDate.trim();
			rssItem.link = rssItem.link.trim();
			rssItem.author = rssItem.author.trim();
			rssItem.source = rssItem.source.trim();
			rssItem.category = rssItem.category.trim();
			rssItem.htmlFullText = rssItem.htmlFullText.trim();
			rssItem.legend = rssItem.legend.trim();
			rssItem.name = rssItem.name.trim();
			rssItem.firstName = rssItem.firstName.trim();

			// patch guid if null and if it is possible
			if (rssItem.guid.equals(""))
				rssItem.guid = rssItem.link;

			// set default date if necessary
			if (rssItem.pubDate == null)
				rssItem.pubDate = parseDate(rssItem.sPubDate);

			// add item !
			if ((!rssItem.guid.equals("")) && (!rssItem.title.equals("")))
				rss.items.add(rssItem);
		}
	}

	public void characters(char ch[], int start, int length) {

//		String chars = (new String(ch).substring(start, start + length));
//		if (chars.equals("\n"))
//			return;
		
        for (int i=start; i<start+length; i++) {
            sb.append(ch[i]);
        }
		
	}

	public void processSaxStream(InputStream stream) {
		try {
			rss = new Rss();

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false); 
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(this);		
			xmlReader.parse(new InputSource(stream));
		}
		catch (Exception e) {
			e.printStackTrace();
			rss = null;
		}
	}
}
