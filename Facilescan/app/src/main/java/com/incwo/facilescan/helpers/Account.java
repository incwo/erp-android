package com.incwo.facilescan.helpers;

import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class Account {
	public Account(String username, String password, @Nullable String shard) {
		mUsername = username;
		mPassword = password;
		mShard = shard.length() > 0 ? shard : null;
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

	private @Nullable String mShard;
	public @Nullable String getShard() {
		return mShard;
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
}

