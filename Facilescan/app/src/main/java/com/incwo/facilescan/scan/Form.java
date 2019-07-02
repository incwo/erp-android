package com.incwo.facilescan.scan;

import java.io.Serializable;
import java.util.ArrayList;

public class Form implements Serializable {
	public String className;
	public String type;
	public ArrayList<FormField> fields;
	
	public Form() {
	    className = "";
	    type = "";
		fields = new ArrayList<FormField>();
	}
	
	public String getClassName() {
		return className;
	}

	public String getType() {
		return type;
	}
	
	public ArrayList<FormField> getFields() {
		return fields;
	}

}
