package com.incwo.facilescan.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.incwo.facilescan.app.FacilescanApp;
import com.incwo.facilescan.helpers.Account;
import com.incwo.facilescan.helpers.EncryptHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class FacileScanPreference {
	private final Object lock = new Object();

	public Account mAccount = null;
	
	public String news_xml;
	public String scans_xml;
	public String videos_xml;
	public String session;
	
	public Boolean mIsLoggedIn;

	private static final String FacileScanPreferencesFile = "FacileScanPreferencesFile";
	
	FacileScanPreference(){
		news_xml = "";
		videos_xml = "";
		session = "";
		mIsLoggedIn = false;
	}
	
	private SharedPreferences getSharedPreferences() {
		return FacilescanApp.getInstance().getApplicationContext().getSharedPreferences(FacileScanPreferencesFile, Context.MODE_PRIVATE);
	}

	public Account getAccount() {
		return mAccount;
	}

	public void loadCustomer(){
		synchronized (lock) {
			SharedPreferences sharedPreferences = getSharedPreferences();
			EncryptHelper encryptHelper = new EncryptHelper();
			try {
				String username = sharedPreferences.getString("username", "");
				username = (new String(encryptHelper.decrypt(username))).trim();
				String password = sharedPreferences.getString("password", "");
				password = (new String(encryptHelper.decrypt(password))).trim();
				mAccount = new Account(username, password);
			} catch (Exception e) {
				e.toString();
				mAccount = null;
			}

		}
	}
	
	public void saveCustomer() {
		synchronized (lock) {
			SharedPreferences sharedPreferences = getSharedPreferences();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			EncryptHelper encryptHelper = new EncryptHelper();
			try {
				editor.putString("username", EncryptHelper.bytesToHex(encryptHelper.encrypt(mAccount.getUsername())));
				editor.putString("password", EncryptHelper.bytesToHex(encryptHelper.encrypt(mAccount.getPassword())));
			} catch (Exception e) {
				e.toString();
			}
			editor.commit();
		}
	}
	
	public void loadNews(){
		synchronized (lock) {
			SharedPreferences sharedPreferences = getSharedPreferences();
			news_xml = sharedPreferences.getString("news_xml", "");
		}
	}
	
	public void saveReadNews(ArrayList<String> toBeSaved){
		
		SharedPreferences sharedPreferences = getSharedPreferences();
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putInt("READ_NEWS_LENGTH", toBeSaved.size());
		 for(int i=0;i<toBeSaved.size();i++)  
		    {

			 edit.remove("READ_NEWS_" + i);
			 edit.putString("READ_NEWS_" + i, toBeSaved.get(i));  
		    }
		 edit.commit();
	}
	
	public ArrayList<String>  loadReadNews(){
		 SharedPreferences sharedPreferences = getSharedPreferences();
		 int size = sharedPreferences.getInt("READ_NEWS_LENGTH", 0);
		 
		 ArrayList<String> toLoad = new ArrayList<String>();
		  for(int i=0;i<size;i++) 
		    {
			  toLoad.add(sharedPreferences.getString("READ_NEWS_" + i, null));  

		    }
		  return toLoad;
		 
	}
	
	public void saveNews() {
		synchronized (lock) {
			SharedPreferences sharedPreferences = getSharedPreferences();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("news_xml", news_xml);
			editor.commit();
		}
	}
	
	public void loadVideos(){
		synchronized (lock) {
			SharedPreferences sharedPreferences = getSharedPreferences();
			videos_xml = sharedPreferences.getString("videos_xml", "");
		}
	}
	
	public void saveVideos() {
		synchronized (lock) {
			SharedPreferences sharedPreferences = getSharedPreferences();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("videos_xml", videos_xml);
			editor.commit();
		}
	}
	
	public void loadSession(){
		synchronized (lock) {
			SharedPreferences sharedPreferences = getSharedPreferences();
			session = sharedPreferences.getString("session", "");
		}
	}
	
	public void saveSession() {
		synchronized (lock) {
			SharedPreferences sharedPreferences = getSharedPreferences();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("session", session);
			editor.commit();
		}
	}
	
	public void loadIsLoggedIn(){
		SharedPreferences sharedPreferences = getSharedPreferences();
		mIsLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
	}
	
	public void saveIsLoggedIn(){
		SharedPreferences sharedPreferences = getSharedPreferences();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("isLoggedIn", mIsLoggedIn);
		editor.commit();
	}
	
	
	public void saveImage(String url, Bitmap bmp){
		String appPath = FacilescanApp.getInstance().getApplicationContext().getFilesDir().getAbsolutePath();
		String filename = url.substring(26);
		OutputStream fOut = null;
        File file = new File(appPath,filename);
            try {
				fOut = new FileOutputStream(file);
				bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
	            try {
					fOut.flush();
					fOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            //MediaStore.Images.Media.insertImage(FigaroApp.getInstance().getApplicationContext().getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	public Bitmap loadImage(String url){
		String filename = url.substring(26);
		String appPath = FacilescanApp.getInstance().getApplicationContext().getFilesDir().getAbsolutePath();
	    File f = new File(appPath, filename);
        if (!f.exists())
        	return null;
	    FileInputStream fis = null;
	    try {
	        fis = new FileInputStream(f);
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		return BitmapFactory.decodeStream(fis);
	}
	
}
