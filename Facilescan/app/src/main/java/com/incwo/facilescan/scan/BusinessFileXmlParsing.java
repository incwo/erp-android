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
import java.util.Stack;

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

	private Stack<String> tagStack = new Stack<String>();
	private BusinessFile mCurrentBusinessFile;
	private FormFolder mCurrentFolder;
	private Form mCurrentForm;
	private FormField mCurrentField;
	private String mCurrentValueKey;

	private class XmlTag {
		private String mIdentifier;
		XmlTag(String identifier, Attributes atts) {
			mIdentifier = identifier;
		}

		private StringBuilder mStringBuilder;
		void appendCharacters(char ch[], int start, int length) {
			for (int i=start; i<start+length; i++) {
				mStringBuilder.append(ch[i]);
			}
		}
	}

	public void startElement(String uri, String name, String qName, Attributes atts) {
		sb.setLength(0); // efficient way to clear the StringBuilder

		tagStack.push(name); // Track the tags hierarchy
		switch (name) {
			case "business_file":
				mCurrentBusinessFile = new BusinessFile();
				break;

			case "folder":
				mCurrentFolder = new FormFolder(atts.getValue("title"));
				break;

			case "les_champs":
				mCurrentField = new FormField();
				break;

			case "object":
				mCurrentForm = new Form();
				break;

			case "la_valeur":
				mCurrentValueKey = atts.getValue("key");
				break;

				// Other tags don't correspond to objects.
		}
	}

	public void endElement(String uri, String name, String qName) throws SAXException {
		String value = sb.toString();
		String topTag = tagStack.pop(); // Point to the parent tag since this tag is now closed.
		if (!(name.equals(topTag))) {
			throw new SAXException(); // Closing a tag which is not the current one.
		}

		if(tagStack.empty()) { // No parent tag
			return;
		}

		String parentTag = tagStack.peek();
		switch (name) {
			case "business_file":
				mBusinessFilesList.businessFiles.add(mCurrentBusinessFile);
				mCurrentBusinessFile = null;
				break;

			case "description":
				if (parentTag.equals("les_champs")) {
					mCurrentField.description = value;
				}
				break;

			case "folder":
				if (parentTag.equals("business_file")) {
					//mCurrentBusinessFile.addFolder(mCurrentFolder);
				}
				mCurrentFolder = null;
				break;

			case "id":
				if (parentTag.equals("business_file")) {
					mCurrentBusinessFile.id = value;
				}
				break;

			case "kind":
				if (parentTag.equals("business_file")) {
					mCurrentBusinessFile.kind = value;
				}
				break;

			case "la_classe":
				if (parentTag.equals("object")) {
					mCurrentForm.type = value;
				}
				break;

			case "la_valeur":
				if (parentTag.equals("les_champs")) {
					mCurrentField.valueTitles.add(value);
					mCurrentField.values.add(mCurrentValueKey);
				}
				mCurrentValueKey = null;
				break;

			case "le_champ":
				if (parentTag.equals("les_champs")) {
					mCurrentField.key = value;
				}
				break;

			case "le_nom":
				if (parentTag.equals("les_champs")) {
					mCurrentField.name = value;
				}
				break;

			case "le_type":
				if (parentTag.equals("les_champs")) {
					if (value.equals("my_signature"))
						mCurrentField.type = "signature";
					else
						mCurrentField.type = value;
				}
				break;

			case "les_champs":
				if (parentTag.equals("object")) {
					mCurrentForm.fields.add(mCurrentField);
				}
				mCurrentField = null;
				break;

			case "lobjet":
				if (parentTag.equals("object")) {
					mCurrentForm.className = value;
				}
				break;

			case "name":
				if (parentTag.equals("business_file")) {
					mCurrentBusinessFile.name = value;
				}
				break;

			case "object":
				if (parentTag.equals("business_file")) {
					mCurrentBusinessFile.mForms.add(mCurrentForm);
				} else if (parentTag.equals("folder")) {
					// mCurrentForm.add(mCurrentForm)
				}
				mCurrentForm = null;
				break;
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
				saxParserFactory.setNamespaceAware(true); // Needed for unit tests or the localName is always empty.
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
