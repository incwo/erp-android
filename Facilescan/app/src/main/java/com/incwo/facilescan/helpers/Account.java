package com.incwo.facilescan.helpers;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

public class Account {
	private final Object lock = new Object();

	public String username;
	public String password;

	public Account() {
		username = "";
		password = "";
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

