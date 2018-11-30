package com.incwo.facilescan.scan;

import java.util.ArrayList;

public class ScanXml  {
	public ArrayList<BusinessFile> businessFiles;

    public ScanXml() {
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

