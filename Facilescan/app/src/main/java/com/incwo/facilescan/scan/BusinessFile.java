package com.incwo.facilescan.scan;

import java.io.Serializable;
import java.util.ArrayList;

public class BusinessFile implements Serializable {
	public String id;
	public String name;
	public String kind;
	protected ArrayList<Object> mChildren;

	public BusinessFile() {
		id = "";
		name = "";
		kind = "";
		mChildren = new ArrayList<Object>();
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<Object> getChildren() {
		return mChildren;
	}
	
}
