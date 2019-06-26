package com.incwo.facilescan.scan;

import java.util.ArrayList;

public class BusinessFile {
	public String id;
	public String name;
	public String kind;
	protected ArrayList<Form> mForms;
	public ArrayList<String> objectsName;
	
	public BusinessFile() {
		id = "";
		name = "";
		kind = "";
		mForms = new ArrayList<Form>();
		objectsName = new ArrayList<String>();
	}

	public Form getFormByName(String name)
	{
		for (Form form: mForms)
		{
			if (form.className.equals(name))
				return form;
		}
		return null;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<Form> getForms() {
		return mForms;
	}
	
}
