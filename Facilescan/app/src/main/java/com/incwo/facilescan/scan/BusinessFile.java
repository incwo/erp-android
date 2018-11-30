package com.incwo.facilescan.scan;

import java.util.ArrayList;

public class BusinessFile {
	public String id;
	public String name;
	public String kind;
	public ArrayList<ScanCategory> objects;
	public ArrayList<String> objectsName;
	
	public BusinessFile() {
		id = "";
		name = "";
		kind = "";
		objects = new ArrayList<ScanCategory>();
		objectsName = new ArrayList<String>();
	}

	public ScanCategory getObjectByName(String name)
	{
		for (ScanCategory obj : objects)
		{
			if (obj.className.equals(name))
				return obj;
		}
		return null;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<ScanCategory> getCategories() {
		return objects;
	}
	
}
