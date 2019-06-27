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

public class BusinessFileXmlParsing extends DefaultHandler {
	private BusinessFilesList mBusinessFilesList;
	private StringBuilder sb;

	public BusinessFileXmlParsing() {
		sb = new StringBuilder();
	}

	public BusinessFilesList readFromXmlContent(String xmlContent) {
		try {
			InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes());
			BusinessFilesList businessFilesList = processSaxStream(inputStream);
			inputStream.close();
			return businessFilesList;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// To keep track of which XML element we're in:
	private enum XmlElementType {
		NONE,
		BUSINESS_FILE,
		OBJECT, // = Form
		FIELD
	}
	private XmlElementType mCurrentElementType = XmlElementType.NONE;
	private BusinessFile mCurrentBusinessFile;
	private Form mCurrentForm;
	private FormField mCurrentField;

	public void startElement(String uri, String name, String qName, Attributes atts) {
		String elementName = name.trim();
		sb.setLength(0); // efficient way to clear the StringBuilder
		
		if (elementName.equals("business_file")) {
			mCurrentElementType = XmlElementType.BUSINESS_FILE;
			mCurrentBusinessFile = new BusinessFile();
		} 
		else if (mCurrentElementType == XmlElementType.BUSINESS_FILE && elementName.equals("object")) {
			mCurrentElementType = XmlElementType.OBJECT;
			mCurrentForm = new Form();
		}
		else if (mCurrentElementType == XmlElementType.OBJECT && elementName.equals("les_champs")) {
			mCurrentElementType = XmlElementType.FIELD;
			mCurrentField = new FormField();
		}

		if (elementName.equals("la_valeur")) {
			mCurrentField.values.add(atts.getValue("key"));
		}
	}

	public void endElement(String uri, String name, String qName) throws SAXException {
		String elementName = name.trim();
		String value = sb.toString().trim();
		try {
			if (mCurrentElementType == XmlElementType.NONE) {
				// everything is done
			}
			else if (mCurrentElementType == XmlElementType.BUSINESS_FILE) {
				if (elementName.equals("id"))
					mCurrentBusinessFile.id = value;
				else if (elementName.equals("name"))
					mCurrentBusinessFile.name = value;
				else if (elementName.equals("kind"))
					mCurrentBusinessFile.kind = elementName;
			}
			else if (mCurrentElementType == XmlElementType.OBJECT) {
				if (elementName.equals("lobjet"))
					mCurrentForm.className = value;
				else if (elementName.equals("la_classe"))
					mCurrentForm.type = value;
			}
			else if (mCurrentElementType == XmlElementType.FIELD) {
				if (elementName.equals("le_nom"))
					mCurrentField.name = value;
				else if (elementName.equals("le_champ"))
					mCurrentField.key = value;
				else if (elementName.equals("le_type")) {
					if (value.equals("my_signature"))
						mCurrentField.type = "signature";
					else
						mCurrentField.type = value;
				}
				else if (elementName.equals("la_classe"))
					mCurrentField.classValue = value;
				else if (elementName.equals("la_valeur")) {
					mCurrentField.valueTitles.add(value);
				
				}
				else if (elementName.equals("description"))
					mCurrentField.description = value;
			}
		} catch (Exception e) {
		}
		
		if (name.trim().equals("les_champs")) {
			mCurrentElementType = XmlElementType.OBJECT;
			if ((!mCurrentField.name.equals("")))
				mCurrentForm.fields.add(mCurrentField);
		}
		else if (name.trim().equals("object")) {
			mCurrentElementType = XmlElementType.BUSINESS_FILE;
			if ((!mCurrentForm.className.equals("")) && (!mCurrentForm.type.equals("")))
			{
				mCurrentBusinessFile.mForms.add(mCurrentForm);
				mCurrentBusinessFile.objectsName.add(mCurrentForm.className);
			}
		}
		else if (name.trim().equals("business_file")) {
			mCurrentElementType = XmlElementType.NONE;
			if ((!mCurrentBusinessFile.id.equals("")) && (!mCurrentBusinessFile.name.equals(""))) {
				mBusinessFilesList.businessFiles.add(mCurrentBusinessFile);
			}
		}
	}

	public void characters(char ch[], int start, int length) {
        for (int i=start; i<start+length; i++) {
            sb.append(ch[i]);
        }
	}

	private BusinessFilesList processSaxStream(InputStream stream) {
		try {
				mBusinessFilesList = new BusinessFilesList();
	
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				saxParserFactory.setValidating(false); 
				SAXParser saxParser = saxParserFactory.newSAXParser();
				XMLReader xmlReader = saxParser.getXMLReader();
				xmlReader.setContentHandler(this);		
				xmlReader.parse(new InputSource(stream));

				return mBusinessFilesList;
			}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
