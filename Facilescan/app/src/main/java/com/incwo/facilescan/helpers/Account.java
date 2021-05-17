package com.incwo.facilescan.helpers;

import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class Account {
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

	@Nullable
	public String getAuthorizationToken() {
		try {
			String token = mUsername + ":" + mPassword;
			String token64 = Base64.encodeBytes(token.getBytes());
			return "Basic " + token64;
		} catch (Exception e) {
			e.toString();
			return null;
		}
	}

	public String getURLParameters() {
		String encUsername = null;
		String encPassword = null;
		try {
			encUsername = URLEncoder.encode(mUsername, "UTF-8");
			encPassword = URLEncoder.encode(mPassword, "UTF-8");
			return "&email=" + encUsername + "&password=" + encPassword;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return "";
		}
	}
}

