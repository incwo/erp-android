package com.incwo.facilescan.scan;

import java.util.ArrayList;

public class ScanCategory {
	public String className;
	public String type;
	public ArrayList<ScanField> fields;
	
	public ScanCategory() {
	    className = "";
	    type = "";
		fields = new ArrayList<ScanField>();
	}

	public ScanField getFieldByName(String name)
	{
		for (ScanField tmp : fields)
		{
			if (name.equals(tmp.name))
				return tmp;
		}
		return null;
	}
	
	public ArrayList<String> getAllFieldsValue()
	{
		ArrayList<String> values = new ArrayList<String>();
		for (ScanField field : fields)
			values.add(field.valueHolder.getText().toString());
		return values;
	}
	
	public String getClassName() {
		return className;
	}

	public String getType() {
		return type;
	}
	
	public ArrayList<ScanField> getFields() {
		return fields;
	}

}
