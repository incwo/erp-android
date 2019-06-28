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

	public Form getFormByName(String name)
	{
		for (Form form: mForms)
		{
			if (form.className.equals(name))
				return form;
		}
		return null;
	}

	public ArrayList<String> getFormNames() {
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
