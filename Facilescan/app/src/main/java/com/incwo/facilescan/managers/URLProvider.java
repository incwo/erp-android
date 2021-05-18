package com.incwo.facilescan.managers;

import com.incwo.facilescan.helpers.Account;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

/** Central point to provide and build URLs. */
public class URLProvider {
    public final static String  NEWS_RSS_URL = "https://blog.incwo.com/xml/rss20/feed.xml?show_extended=1";
    public final static String  VIDEOS_RSS_URL = "http://www.incwo.com/videos/trainings.xml";
    private final static String	LOGIN_URL = "/account/login";
    private final static String	LOGOUT_URL = "/account/logout";
    private final static String	SCAN_URL = "/account/get_files_and_image_enabled_objects/0.xml?r=";
    private final static String	ACCOUNT_CREATION_URL = "/iframe/pos_new_account?bundle_id=com.facilescan";
    private final static String UPLOAD_SCAN_URL = "/upload_files.xml";

    /** Base URL when sending requests without authentification. */
    public static String getUnauthBaseUrl() {
        return "https://www.incwo.com";
    }

    /** Base URL for sending authenticated requests. */
    public static String getBaseUrl(Account account) {
        String shard = account.getShard();
        if(shard == null) {
            return "https://www.incwo.com";
        } else {
            if(shard.equals("dev")) {
                return "http://dev.incwo.com"; // HTTPS is not supported on the development server.
            } else {
                return "https://" + shard + ".incwo.com";
            }
        }
    }

    public static String getAccountCreationUrl() {
        return getUnauthBaseUrl() + ACCOUNT_CREATION_URL;
    }

    public static String getLoginUrl(Account account) {
        // Pass a random number in the URL to prevent caching.
        Random rand = new Random();
        return getUnauthBaseUrl() + LOGIN_URL + "?mobile=2&remember_me=1"+ getAccountURLParameters(account)+ "&r=" + rand.nextInt();
    }

    public static String getLogoutUrl() {
        return getUnauthBaseUrl() + LOGOUT_URL;
    }

    public static String getScanUrl(Account account) {
        // Pass a random number in the URL to prevent caching.
        Random rand = new Random();
        return getBaseUrl(account) + SCAN_URL + rand.nextInt() + "&hierarchical=1";
    }

    public static String getUploadScanUrl(String businessFileId, Account account) {
        return getBaseUrl(account) + "/" + businessFileId + UPLOAD_SCAN_URL;
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
        return getUnauthBaseUrl() + "/iframe/training/" + videoId;
    }
}
