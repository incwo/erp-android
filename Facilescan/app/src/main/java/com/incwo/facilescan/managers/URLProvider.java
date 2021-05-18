package com.incwo.facilescan.managers;

import com.incwo.facilescan.helpers.Account;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

public class URLProvider {
    public final static String  NEWS_RSS_URL = "https://blog.incwo.com/xml/rss20/feed.xml?show_extended=1";
    public final static String  VIDEOS_RSS_URL = "http://www.incwo.com/videos/trainings.xml";
    private final static String  FACILE_BASEURL = "https://www.incwo.com";
    private final static String  FACILE_BASEURL_DEV = "http://dev.incwo.com";
    private final static String	LOGIN_URL = "/account/login";
    public final static String	LOGOUT_URL = "/account/logout";
    private final static String	SCAN_URL = "/account/get_files_and_image_enabled_objects/0.xml?r=";
    public final static String	ACCOUNT_CREATION_URL = "/iframe/pos_new_account?bundle_id=com.facilescan";
    private final static String  UPLOAD_SCAN_URL = "/upload_files.xml";

    private final static boolean mIsDevServer = false;


    public static String getBaseURL()
    {
        if (mIsDevServer)
            return FACILE_BASEURL_DEV;
        else
            return FACILE_BASEURL;
    }

    public static String getAccountCreationUrl() {
        return getBaseURL() + ACCOUNT_CREATION_URL;
    }

    public static String getLoginUrl(Account account) {
        Random rand = new Random();
        return getBaseURL() + LOGIN_URL + "?mobile=2&remember_me=1"+ getAccountURLParameters(account)+ "&r=" + rand.nextInt();
    }

    public static String getLogoutUrl() {
        return getBaseURL() + LOGOUT_URL;
    }

    public static String getScanUrl() {
        // random avoid caching issue
        Random rand = new Random();
        return getBaseURL() + SCAN_URL + rand.nextInt() + "&hierarchical=1";
    }

    public static String getUploadScanUrl(String businessFileId) {
        return getBaseURL() + "/" + businessFileId + UPLOAD_SCAN_URL;
    }

    private static String getAccountURLParameters(Account account) {
        try {
            String encUsername = URLEncoder.encode(account.getUsername(), "UTF-8");
            String encPassword = URLEncoder.encode(account.getPassword(), "UTF-8");
            return "&email=" + encUsername + "&password=" + encPassword;
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return "";
        }
    }

    public static String getVideoUrl(String videoId) {
        return getBaseURL() + "/iframe/training/" + videoId;
    }
}
