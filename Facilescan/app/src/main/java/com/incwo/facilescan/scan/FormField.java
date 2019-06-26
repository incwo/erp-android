package com.incwo.facilescan.scan;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class FormField {
	public String name; // Localized, human-readable title of the field
	public String key; // Key to send the field to the server
	public String type; // Type of the field = string|signature|enum
	public String classValue;
	public ArrayList<String> valueTitles; // Enum input. Presented in the EnumFragment.
	public ArrayList<String> values; // Enum input
	public String description;

	// This is terrible. The TextView which holds the value (typed by the user or chosen from an enum)
	// is stored here *in the Model* and the WebService calls valueHolder.getText().
	// One could think it could simply be replaced by the savedValue, but it is not so simple: there
	// has to be a moment when the content of the textView is saved to the savedValue.
	public TextView valueHolder;
	public String savedValue; // To save the value while the fragment's views are destroyed.

	
	public FormField() {
		  name = "";
		  key = "";
		  type = "";
		  valueTitles = new ArrayList<String>();
		  values= new ArrayList<String>();
		  description = "";
		  savedValue = null;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	public String getClassValue() {
		return classValue;
	}
	
	public ArrayList<String> getValueTitles() {
		return valueTitles;
	}

	
	public ArrayList<String> getValues() {
		return values;
	}
	
	
	public String getDescription() {
		return description;
	}

}
