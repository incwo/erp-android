package com.incwo.facilescan.helpers.videos;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XmlParser extends DefaultHandler {
	
	StringBuilder sb;
	
	public XmlParser() {
		sb = new StringBuilder();
	}

	public VideoXml readFromXmlContent(String xmlContent) {
		try {
			InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes());
			processSaxStream(inputStream);
			inputStream.close();
			return xml;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public VideoXml readFromFile(String rssPath) {
		try {
			FileInputStream fileInputStream = new FileInputStream(rssPath);
			processSaxStream(fileInputStream);
			fileInputStream.close();
			return xml;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public VideoXml readFromUrl(String rssUrlPath) {
		try {
			URL url = new URL(rssUrlPath);
			InputStream inputStream = url.openStream();
			processSaxStream(inputStream);
			inputStream.close();
			return xml;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Used to define what elements we are currently in
	private String inName = "";
	private boolean inItem = false;

	public VideoXml xml;
	private VideoItem videoItem;

	public void startElement(String uri, String name, String qName, Attributes atts) {
		inName = name.trim();

		// efficient way to clear the StringBuilder
		sb.setLength(0);
		
		if (inName.equals("media")) {
			inItem = true;
			videoItem = new VideoItem();
		} 

		if (inItem) {
			
		}
	}

	public void endElement(String uri, String name, String qName) throws SAXException {
		
		try {
			// If not in item, then title/link refers to channel
			if (!inItem) {
				
			} else {
				if (inName.equals("id"))
					videoItem.id = sb.toString();
				else if (inName.equals("title"))
					videoItem.title = sb.toString();
				else if (inName.equals("video_reference"))
					videoItem.youtube_id = sb.toString();
				else if (inName.equals("permalink"))
					videoItem.permalink = sb.toString();
				else if (inName.equals("created_at"))
					videoItem.created_at = sb.toString();
				else if (inName.contains("feature"))
				{
					if(!sb.toString().equals(""))
					videoItem.categorie = sb.toString();
				}
			}
		} catch (Exception e) {
		}
		
		if (name.trim().equals("media")) {
			inItem = false;

			
			videoItem.id = videoItem.id.trim();
			videoItem.categorie = videoItem.categorie.trim();
			videoItem.created_at = videoItem.created_at.trim();
			videoItem.permalink = videoItem.permalink.trim();
			videoItem.title = videoItem.title.trim();
			videoItem.youtube_id = videoItem.youtube_id.trim();
			
			// add item !
			if ((!videoItem.id.equals("")) && (!videoItem.title.equals("")))
				xml.items.add(videoItem);
		}
		
		if(name.trim().contains("feature")){
			if(!videoItem.categorie.trim().equals("")){
			if(!xml.categories.contains(videoItem.categorie.trim()))
				xml.categories.add(videoItem.categorie.trim());
			videoItem.categories.add(videoItem.categorie.trim());
			videoItem.categorie = "";
			}
		}
	}

	public void characters(char ch[], int start, int length) {
//		String chars = (new String(ch).substring(start, start + length));
//
//		if (chars.equals("\n"))
//			return;

        for (int i=start; i<start+length; i++) {
            sb.append(ch[i]);
        }
		
	}

	public void processSaxStream(InputStream stream) {
		try {
			xml = new VideoXml();

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false); 
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(this);		
			
			xmlReader.parse(new InputSource(stream));
			}
		catch (Exception e) {
			e.printStackTrace();
			xml = null;
		}
	}
}
