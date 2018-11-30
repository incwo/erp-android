package com.incwo.facilescan.scan;

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

public class XmlScanParser extends DefaultHandler {
	
	StringBuilder sb;
	
	public XmlScanParser() {
		sb = new StringBuilder();
	}

	public ScanXml readFromXmlContent(String xmlContent) {
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

	public ScanXml readFromFile(String rssPath) {
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

	public ScanXml readFromUrl(String rssUrlPath) {
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
	static private final int notInItem = 0;
	static private final int inBusinessFileItem = 1;
	static private final int inObjectItem = 2;
	static private final int inFieldItem = 3;
	private int inItem = XmlScanParser.notInItem;

	public ScanXml xml;
	private BusinessFile businessItem;
	private ScanCategory objectItem;
	private ScanField fieldItem;

	public void startElement(String uri, String name, String qName, Attributes atts) {
		inName = name.trim();

		// efficient way to clear the StringBuilder
		sb.setLength(0);
		
		if (inName.equals("business_file")) {
			inItem = XmlScanParser.inBusinessFileItem;
			businessItem = new BusinessFile();
		} 
		else if (inItem == inBusinessFileItem && inName.equals("object")) {
			inItem = XmlScanParser.inObjectItem;
			objectItem = new ScanCategory();
		}
		else if (inItem == inObjectItem && inName.equals("les_champs")) {
			inItem = XmlScanParser.inFieldItem;
			fieldItem = new ScanField();
			
		}
		if (inName.equals("la_valeur"))
		{
		
			fieldItem.values.add(atts.getValue("key"));
		}

	}

	public void endElement(String uri, String name, String qName) throws SAXException {
		inName = name.trim();
		String value = sb.toString().trim();
		try {
			if (inItem == XmlScanParser.notInItem) {
				// everything is done
			}
			else if (inItem == XmlScanParser.inBusinessFileItem) {
				if (inName.equals("id"))
					businessItem.id = value;
				else if (inName.equals("name"))
					businessItem.name = value;
				else if (inName.equals("kind"))
					businessItem.kind = inName;
			}
			else if (inItem == XmlScanParser.inObjectItem) {
				if (inName.equals("lobjet"))
					objectItem.className = value;
				else if (inName.equals("la_classe"))
					objectItem.type = value;
			}
			else if (inItem == XmlScanParser.inFieldItem)
			{
				if (inName.equals("le_nom"))
					fieldItem.name = value;
				else if (inName.equals("le_champ"))
					fieldItem.key = value;
				else if (inName.equals("le_type"))
				{
					if (value.equals("my_signature"))
						fieldItem.type = "signature";
					else
						fieldItem.type = value;
				}
				else if (inName.equals("la_classe"))
					fieldItem.classValue = value;
				else if (inName.equals("la_valeur"))
				{
					fieldItem.valueTitles.add(value);
				
				}
				else if (inName.equals("description"))
					fieldItem.description = value;
			}
		} catch (Exception e) {
		}
		
		if (name.trim().equals("les_champs"))
		{
			inItem = XmlScanParser.inObjectItem;
			if ((!fieldItem.name.equals("")))
				objectItem.fields.add(fieldItem);
		}
		else if (name.trim().equals("object")) {
			inItem = XmlScanParser.inBusinessFileItem;
			if ((!objectItem.className.equals("")) && (!objectItem.type.equals("")))
			{
				businessItem.objects.add(objectItem);
				businessItem.objectsName.add(objectItem.className);
			}
		}
		else if (name.trim().equals("business_file")){
			inItem = XmlScanParser.notInItem;
			if ((!businessItem.id.equals("")) && (!businessItem.name.equals(""))) {
				xml.businessFiles.add(businessItem);
			}
		}
	}

	public void characters(char ch[], int start, int length) {
        for (int i=start; i<start+length; i++) {
            sb.append(ch[i]);
        }
	}

	public void processSaxStream(InputStream stream) {
		try {
				xml = new ScanXml();
	
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
