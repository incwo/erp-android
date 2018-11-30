package com.incwo.facilescan.scan;

import java.util.ArrayList;

public class BusinessFilesList {
	public ArrayList<BusinessFile> businessFiles;

    public BusinessFilesList() {
		businessFiles = new ArrayList<BusinessFile>();
	}

	@SuppressWarnings("unchecked")
	public void processXmLContent(String xml) {
		XmlScanParser xmlScanParser = new XmlScanParser();
		xmlScanParser.readFromXmlContent(xml);
		if (xmlScanParser.xml != null) {
			businessFiles = (ArrayList<BusinessFile>) xmlScanParser.xml.businessFiles.clone();
		}
	}
}

