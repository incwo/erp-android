package com.incwo.facilescan.scan;

import java.util.ArrayList;

public class BusinessFile {
	public String id;
	public String name;
	public String kind;
	protected ArrayList<Form> mForms;

	public BusinessFile() {
		id = "";
		name = "";
		kind = "";
		mForms = new ArrayList<Form>();
	}

	public Form getFormByClassName(String className)
	{
		for (Form form: mForms)
		{
			if (form.className.equals(className))
				return form;
		}
		return null;
	}

	public ArrayList<String> getFormClassNames() {
		ArrayList<String> formNames = new ArrayList<>();
		for (Form form: mForms) {
			formNames.add(form.className);
		}
		return formNames;
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
