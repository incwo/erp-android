package com.incwo.facilescan.managers;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.webkit.CookieManager;

import com.incwo.facilescan.app.FacilescanApp;
import com.incwo.facilescan.helpers.Base64;
import com.incwo.facilescan.helpers.rss.Rss;
import com.incwo.facilescan.helpers.rss.RssItem;
import com.incwo.facilescan.helpers.videos.VideoXml;
import com.incwo.facilescan.scan.BusinessFile;
import com.incwo.facilescan.scan.BusinessFilesList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class SingleApp
{
	public final static long 	SPLASH_SCREEN_DURATION = 1000;
	public final static String  NEWS_RSS_URL = "https://blog.incwo.com/xml/rss20/feed.xml?show_extended=1";
	public final static String  VIDEOS_RSS_URL = "http://www.incwo.com/videos/trainings.xml";
	public final static String  FACILE_BASEURL = "https://www.incwo.com";
	public final static String  FACILE_BASEURL_DEV = "http://dev.incwo.com";
	public final static String	LOGIN_URL = "/account/login";
	public final static String	LOGOUT_URL = "/account/logout";
	public final static String	SCAN_URL = "/account/get_files_and_image_enabled_objects/0.xml?r=";
	public final static String	ACCOUNT_CREATION_URL = "/iframe/pos_new_account?bundle_id=com.facilescan";
	public final static String  UPLOAD_SCAN_URL = "/upload_files.xml";
	
	private final static boolean mIsDevServer = false;
	
	private static boolean mIsLoggedIn = false;

	// datas
	private static FacileScanPreference pref = null;
	private static Rss news_rss = new Rss();
	private static VideoXml videos_xml = new VideoXml();
	private static BusinessFilesList sBusinessFilesList = new BusinessFilesList();
	private static HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();
	private static ArrayList<String> readNews = new ArrayList<String>();

	private static Bitmap Signature;
	
	// selected businessFiles
	private static int selectedNews = 0;
	private static String selectedVideo = "";
	private static String selectedCategorie = "";
	
	// messages
	public static final int BADGE_UPDATE_MESSAGE = 1000;
	
	
	// store data, that can be used to send data between fragments
	private static String dataHolder = new String();
	
	public static String getDataSendByPreviousFragment() {
		return dataHolder;
	}
	
	public static void setDataForNextFragment(String name) {
		SingleApp.dataHolder = name;
	
	}
	
	//
	// functions like "getSelected*" and "setSelected*"
	//

	public static String getSelectedCategorie() {
		return selectedCategorie;
	}

	public static void setSelectedCategorie(String selectedCategorie) {
		SingleApp.selectedCategorie = selectedCategorie;
	}

	public static String getSelectedVideo() {
		return selectedVideo;
	}

	public static void setSelectedVideo(String selectedVideo) {
		SingleApp.selectedVideo = selectedVideo;
	}

	public static int getSelectedNews() {
		return selectedNews;
	}
	
	public static ArrayList<String> getReadNews() {
		readNews = getFacileScanPreference().loadReadNews();
		return readNews;
	}

	public static void addReadNews(String guid) {
		if(!SingleApp.readNews.contains(guid)){
			SingleApp.readNews.add(guid);
			getFacileScanPreference().saveReadNews(SingleApp.readNews);
		}
	}

	public static void setSelectedNews(int selectedNews) {
		SingleApp.selectedNews = selectedNews;
	}
	//
	// end of "setSelected*" and "getSelected*" type of functions
	//
	
	public static Bitmap getSignature() {
		return Signature;
	}

	public static void setSignature(Bitmap signature) {
		Signature = signature;
	}
	
	public static void clearSignature(){

		if (Signature != null)
		{
			Signature.recycle();
			Signature = null;
		}

	}
	
	private SingleApp() {
	}

	private static class SingletonHolder {
		private static SingleApp instance = new SingleApp();
	}

	public static SingleApp getInstance() {
		return SingletonHolder.instance;
	}
	
	public static boolean isLoggedIn()
	{
		getFacileScanPreference().loadIsLoggedIn();
		mIsLoggedIn =  getFacileScanPreference().mIsLoggedIn;
		return mIsLoggedIn;
	}
	
	public static void setLoggedIn(boolean loggedIn)
	{
		mIsLoggedIn = loggedIn;
		getFacileScanPreference().mIsLoggedIn = mIsLoggedIn;
		getFacileScanPreference().saveIsLoggedIn();
	}
	
	
	public static void logOut()
	{
		if (CookieManager.getInstance().getCookie(SingleApp.getBaseURLForCookie()) != null) {
			CookieManager.getInstance().setCookie(SingleApp.getBaseURLForCookie(), "");
		}
		CookieManager.getInstance().removeSessionCookie();
		SingleApp.saveUsernameAndPassword("", "");
		SingleApp.setSessionId("");
		SingleApp.setLoggedIn(false);
	}
	
	public static String getUsername()
	{
		return (pref.username);
	}
	
	public static String getPassword()
	{
		return (pref.password);
	}

	public static void saveUsernameAndPassword(String username, String password)
	{
		pref.username = username;
		pref.password = password;
		pref.saveCustomer();
	}
	
	public static void loadUsernameAndPassword()
	{
		pref.loadCustomer();
	}
	
	public static String getAutorizationToken()
	{
		return getAutorizationToken(pref.username, pref.password);
	}
	
	public static String getBaseURL()
	{
		if (mIsDevServer)
			return FACILE_BASEURL_DEV;
		else
			return FACILE_BASEURL;
	}

	public static String getBaseURLForCookie()
	{
//		if (mIsDevServer)
//			return "dev.incwo.com";
//		else
//			return "www.incwo.com";
		return getBaseURL();
	}

	public static String getAutorizationToken(String username, String password)
	{
		String token = username + ":" + password;
		return "Basic " + Base64.encodeBytes(token.getBytes());
	}
	
	public static boolean isNetworkAvailable() {
		ConnectivityManager connManager = (ConnectivityManager) FacilescanApp.getInstance().getSystemService(Application.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
		if (activeNetworkInfo == null) 
			return false;
		if (activeNetworkInfo.isAvailable() == false) 
			return false;

		return true;
	}
	
	public static FacileScanPreference getFacileScanPreference() {
		if (pref == null)
			pref = new FacileScanPreference();
		return pref;
	}
	
	public static Rss setRssNewsXml(String xml) {
		if (news_rss == null)
			news_rss = new Rss();

		if (xml == null || xml.equals("")) {
			return null;
		}
		getFacileScanPreference().news_xml = xml;
		getFacileScanPreference().saveNews();
		news_rss.processXmLContent(xml);
		updateReadNews(news_rss);
		return news_rss;
	}

	// Clean from the read news the ones which are not found in the new RSS.
	private static void updateReadNews(Rss newsRss) {
		ArrayList<String> readNewsGuids = getReadNews();
		ArrayList<String> newsToRemove = new ArrayList<>();

		for(String guid: readNewsGuids) {
			if(!newsItemsContainGuid(newsRss.items, guid)) {
				newsToRemove.add(guid);
			}
		}

		readNewsGuids.removeAll(newsToRemove);
		getFacileScanPreference().saveReadNews(readNewsGuids);

	}

	private static boolean newsItemsContainGuid(ArrayList<RssItem> items, String guid) {
		for(RssItem newsItem: items) {
			if(newsItem.guid.equals(guid)) {
				return true;
			}
		}
		return false; // Not found
	}
	
	
	public static void loadAll(){
		loadSessionId();
		loadNewsRss();
		loadVideosXml();
		loadUsernameAndPassword();
	}
	
	public static void setSessionId(String id){
		if (id == null || id.equals("")) {
			return ;
		}
		getFacileScanPreference().session = id;
		getFacileScanPreference().saveSession();
	}
	
	public static void loadSessionId(){
		getFacileScanPreference().loadSession();
		CookieManager.getInstance().removeSessionCookie();
		CookieManager.getInstance().setCookie(getBaseURLForCookie(), getFacileScanPreference().session);
	}
	
	public static String getSessionId(){
		return getFacileScanPreference().session;
	}
	
	public static void loadNewsRss(){
		getFacileScanPreference().loadNews();
		if (getFacileScanPreference().news_xml.equals("") == false)
			news_rss.processXmLContent(getFacileScanPreference().news_xml);
	}
	
	public static Rss getNewsRss() {
		return news_rss;
	}

	public static int getCountOfUnreadNews() {
		int all = getNewsRss().items.size();
		int read = getReadNews().size();
		return all - read;
	}

	public static BusinessFilesList getBusinessFilesList() {
		return sBusinessFilesList;
	}

	
	public static void loadVideosXml(){
		getFacileScanPreference().loadVideos();
		if (getFacileScanPreference().videos_xml.equals("") == false)
			videos_xml.processXmLContent(getFacileScanPreference().videos_xml);
	}
	
	public static VideoXml getVideosXml() {
		return videos_xml;
	}
	
	public static VideoXml setVideosXml(String xml) {
		if (videos_xml == null)
			videos_xml = new VideoXml();

		if (xml == null || xml.equals("")) {
			return null;
		}
		getFacileScanPreference().videos_xml = xml;
		getFacileScanPreference().saveVideos();
		videos_xml.processXmLContent(xml);
		return videos_xml;
	}

	public static void saveImage(String url, Bitmap image){
		final String murl = url;
		final Bitmap mimage = image;
		if(!images.containsKey(url))
			images.put(url, image);
		new Thread(new Runnable() {
        	@Override
        	public void run() {
        		getFacileScanPreference().saveImage(murl, mimage);
        	}
    	}).start();
		
	}
	
	public static Bitmap getImage(String url){
		Bitmap bmp = null;
		bmp = images.get(url);
		if(bmp == null)
			bmp = getFacileScanPreference().loadImage(url);
		return bmp;
	}


	//
	// Intents
	//

	public static boolean hasTelephony(Context context) {
		return hasSystemFeature(context, "android.hardware.telephony");
	}

	private static boolean hasSystemFeature(Context context, String feature) {
		boolean hasFeature;
		PackageManager packageManager = context.getPackageManager();
		Method method = null;

		if (packageManager == null)
			hasFeature = false;
		else {
			try {
				Class[] parameters = new Class[1];
				parameters[0] = String.class;
				method = packageManager.getClass().getMethod("hasSystemFeature", parameters);
				Object[] parm = new Object[1];
				parm[0] = new String(feature);
				Object retValue = method.invoke(packageManager, parm);
				if(retValue instanceof Boolean)
					hasFeature = new Boolean(((Boolean) retValue).booleanValue());
				else
					hasFeature = false;
			}
			catch(Exception e) {
				hasFeature = false;
			}
		}

		return hasFeature;
	}

	public void startIntent(Context context, String url){

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(intent);

//		if (SingleApp.hasTelephony(context)) {
//			String tel = "tel:" + "0123456789";
//			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
//			context.startActivity(intent);
//		}
	}
	
}