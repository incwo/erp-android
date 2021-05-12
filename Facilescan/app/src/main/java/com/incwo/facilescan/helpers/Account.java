package com.incwo.facilescan.helpers;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

public class Customer {
	private final Object lock = new Object();

	public String username;
	public String password;

	public Customer() {
		username = "";
		password = "";
	}

	public void load(String filename) {
		synchronized (lock) {
			try {
				FileInputStream fileInputStream = new FileInputStream(new File(filename));
				byte [] buffer = new byte[fileInputStream.available()];
				while (fileInputStream.read(buffer) != -1);
				fileInputStream.close();
				String jsonContent = new String(buffer);
				processJsonContent(jsonContent);
			} 
			catch (Exception e) {
				e.toString();
			}
		}
	}

	public void processJsonContent(String jsonContent) {
//			try {
//				JSONObject object = new JSONObject(jsonContent);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
	}

	public String getJsonRegister() {
		try {
			JSONObject object = new JSONObject();
			object.put("username", username);
			object.put("password", password);
			return object.toString();
		}
		catch (Exception e) {
			e.toString();
		}
		return "";
	}

	public boolean isAuthorizationTokenValid() {
		return (username.length() > 0 && password.length() > 0);
	}

	public String getAuthorizationToken() {
		try {
			String token = username + ":" + password;
			String token64 = Base64.encodeBytes(token.getBytes());
			return "Basic <" + token64 + ">";
		} catch (Exception e) {
			e.toString();
		}
		return "";
	}

}

