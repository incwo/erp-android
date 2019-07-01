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

	public FormField getFieldByName(String name)
	{
		for (FormField tmp : fields)
		{
			if (name.equals(tmp.name))
				return tmp;
		}
		return null;
	}
	
	public ArrayList<String> getAllFieldsValue()
	{
		ArrayList<String> values = new ArrayList<String>();
		for (FormField field : fields)
			values.add(field.valueHolder.getText().toString());
		return values;
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
