package com.incwo.facilescan.helpers;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

public class Account {
	private final Object lock = new Object();


	public Account(String username, String password) {
		mUsername = username;
		mPassword = password;
	}
	private Account() { }

	private String mUsername;
	public String getUsername() {
		return mUsername;
	}

	private String mPassword;
	public String getPassword() {
		return mPassword;
	}

	public boolean isAuthorizationTokenValid() {
		return (mUsername.length() > 0 && mPassword.length() > 0);
	}

	public String getAuthorizationToken() {
		try {
			String token = mUsername + ":" + mPassword;
			String token64 = Base64.encodeBytes(token.getBytes());
			return "Basic <" + token64 + ">";
		} catch (Exception e) {
			e.toString();
		}
		return "";
	}

}

