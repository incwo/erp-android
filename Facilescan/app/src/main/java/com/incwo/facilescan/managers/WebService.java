package com.incwo.facilescan.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.incwo.facilescan.BuildConfig;
import com.incwo.facilescan.R;
import com.incwo.facilescan.helpers.Base64;
import com.incwo.facilescan.scan.FormField;
import com.incwo.facilescan.scan.Form;
import com.incwo.facilescan.toasts.ToastNoQueue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WebService {

    private final int CONNECT_TIME_OUT = 15 * 1000;
    private final int READ_TIME_OUT = 60 * 1000;
    public int responseCode;
    public Map<String, List<String>> headersFields;
    public String body;
    public String error;
    public String inputStreamFormat = "UTF-8";
    public List<String> cookies = null;

    // Error codes
    static public int NO_CONNECTION = -1;
    static public int BAD_IDENTIFIERS = -2;

    public void get(String url) {
        HttpGetFR(url, "UTF-8");
    }

    public void getNews() {
        HttpGetFR(SingleApp.NEWS_RSS_URL, "application/rss+xml");
    }

    public void getVideos() {
        HttpGetFR(SingleApp.VIDEOS_RSS_URL, "application/rss+xml");
    }

    public void HttpGetFR(String remoteUrl, String contentType) {
        HttpGetFR(remoteUrl, contentType, "GET");
    }

    public void HttpGetFR(String remoteUrl, String contentType, String requestMethod) {
        HttpGetFR(remoteUrl, contentType, requestMethod, null);
    }

    public void HttpGetFR(String remoteUrl, String contentType, String requestMethod, String token) {
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(remoteUrl);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
            httpURLConnection.setReadTimeout(READ_TIME_OUT);

            httpURLConnection.setRequestProperty("Accept-Language", "fr");
            httpURLConnection.setRequestProperty("Accept-Charset", inputStreamFormat);
            httpURLConnection.setRequestProperty("Content-Type", contentType);
            if (token != null)
                httpURLConnection.setRequestProperty("Authorization", token);
            else if (SingleApp.getAutorizationToken() != null)
                httpURLConnection.setRequestProperty("Authorization", SingleApp.getAutorizationToken());
            httpURLConnection.setRequestProperty("X_FACILE_VERSION", BuildConfig.VERSION_NAME);


            httpURLConnection.connect();

            if (cookies == null)
                cookies = httpURLConnection.getHeaderFields().get("Set-Cookie");

            responseCode = httpURLConnection.getResponseCode();
            headersFields = httpURLConnection.getHeaderFields();

            if ((responseCode >= 200 && responseCode < 300)) {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(), inputStreamFormat);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                body = stringBuffer.toString();
            } else if ((responseCode >= 400 && responseCode < 500)) {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                }
                stringBuffer.append(line);
                body = stringBuffer.toString();
            } else
                body = httpURLConnection.getResponseMessage();
            httpURLConnection.disconnect();
        } catch (Exception e) {
            manageError(e);
        }

    }

    // This function is very slow
    private HttpURLConnection tryConnectToFacileAndManageCookies(HttpURLConnection httpURLConnection, URL url, String username, String password) {
        try {
            // If we don't have any cookies,
            // send any request in order to get cookies
            // didn't found anything smarter than that...
            if (cookies == null) {
                Random rand = new Random();
                String remoteUrl = SingleApp.getBaseURL() + SingleApp.LOGIN_URL + "?mobile=2&remember_me=1&email=" + URLEncoder.encode(username, "utf-8") + "&password=" + URLEncoder.encode(password, "utf-8") + "&r=" + rand.nextInt();
                HttpURLConnection tmpConnection = null;
                URL tmpURL = new URL(remoteUrl);
                tmpConnection = (HttpURLConnection) tmpURL.openConnection();
                tmpConnection.setRequestMethod("POST");
                tmpConnection.setDoOutput(true);
                tmpConnection.setConnectTimeout(CONNECT_TIME_OUT);
                tmpConnection.setReadTimeout(READ_TIME_OUT);

                tmpConnection.setRequestProperty("Accept-Language", "fr");
                tmpConnection.setRequestProperty("Accept-Charset", inputStreamFormat);
                tmpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + inputStreamFormat);
                tmpConnection.setRequestProperty("Authorization", SingleApp.getAutorizationToken(username, password));
                tmpConnection.setRequestProperty("X_FACILE_VERSION", BuildConfig.VERSION_NAME);
                tmpConnection.setInstanceFollowRedirects(true);
                tmpConnection.connect();

                cookies = tmpConnection.getHeaderFields().get("Set-Cookie");
                tmpConnection.disconnect();
            }

            //
            // Test connections identifiers
            // We try to connect, and if the identifiers are bad, an exeption will be raised
            // (very ugly)
            //
            // random avoid caching issue
            Random rand = new Random();
            String remoteUrl = SingleApp.getBaseURL() + SingleApp.SCAN_URL + rand.nextInt();

            URL tmpURL = new URL(remoteUrl);
            HttpURLConnection tmpConnection = null;
            tmpConnection = (HttpURLConnection) tmpURL.openConnection();
            tmpConnection.setRequestMethod("GET");
            tmpConnection.setDoOutput(false);
            tmpConnection.setConnectTimeout(CONNECT_TIME_OUT);
            tmpConnection.setReadTimeout(READ_TIME_OUT);

            tmpConnection.setRequestProperty("Accept-Language", "fr");
            tmpConnection.setRequestProperty("Accept-Charset", inputStreamFormat);
            tmpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + inputStreamFormat);
            tmpConnection.setRequestProperty("Authorization", SingleApp.getAutorizationToken(username, password));
            tmpConnection.setRequestProperty("X_FACILE_VERSION", BuildConfig.VERSION_NAME);

            // here is the trick with the exception
            // if there are no content, an exception is raised and the function return null
            InputStreamReader inputStreamReader = new InputStreamReader(tmpConnection.getInputStream(), inputStreamFormat);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            // END


            // if we reach this line, password and username are good --> we can save.
            SingleApp.saveUsernameAndPassword(username, password);

            // open new connection (for the real request this time)
//            httpURLConnection = (HttpURLConnection) url.openConnection();

            // if we got cookies, notify "httpURLConnection", "cookieManager", "CookieSyncManager" and "SingleApp"
            if (cookies != null) {
                CookieManager cookieManager = CookieManager.getInstance();

                for (String cookie : cookies) {
                    String tmp = cookie.split(";", 2)[0] + ";";
                    httpURLConnection.addRequestProperty("Cookie", tmp);
                    if (tmp.contains("_session_id=")) {
                        cookieManager.removeSessionCookie();
                        cookieManager.setCookie(SingleApp.getBaseURLForCookie(), tmp);
                        SingleApp.setSessionId(tmp);
                    }
                }
                CookieSyncManager.getInstance().sync();
                SystemClock.sleep(500);
            }

        } catch (Exception e) {
            e.printStackTrace();
            httpURLConnection = null;
        }
        return httpURLConnection;
    }

    public void logToDesktop(String username, String password) {

        // encode
        String login = null;
        String pass = null;
        try {
            login = URLEncoder.encode(username, "UTF-8");
            pass = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }


        String remoteUrl = SingleApp.getBaseURL() + SingleApp.LOGIN_URL + "?mobile=2&remember_me=1&email=" + login + "&password=" + pass;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(remoteUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
            httpURLConnection.setReadTimeout(READ_TIME_OUT);

            httpURLConnection.setRequestProperty("Accept-Language", "fr");
            httpURLConnection.setRequestProperty("Accept-Charset", inputStreamFormat);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + inputStreamFormat);
            httpURLConnection.setRequestProperty("Authorization", SingleApp.getAutorizationToken(username, password));
            httpURLConnection.setRequestProperty("X_FACILE_VERSION", BuildConfig.VERSION_NAME);

            httpURLConnection = tryConnectToFacileAndManageCookies(httpURLConnection, url, username, password);

            if (httpURLConnection == null) {
                responseCode = WebService.BAD_IDENTIFIERS;
                return;
            }

            httpURLConnection.connect();

            responseCode = httpURLConnection.getResponseCode();
            headersFields = httpURLConnection.getHeaderFields();

            if (responseCode >= 200 && responseCode < 300) {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(), inputStreamFormat);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                body = stringBuffer.toString();
            } else if (responseCode >= 400 && responseCode < 500) {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                }
                stringBuffer.append(line);
                body = stringBuffer.toString();
            } else
                body = httpURLConnection.getResponseMessage();
            httpURLConnection.disconnect();
        } catch (Exception e) {
            manageError(e);
        }
    }

    public void logToScan(String username, String password) {
        // random avoid caching issue
        Random rand = new Random();
        String remoteUrl = SingleApp.getBaseURL() + SingleApp.SCAN_URL + rand.nextInt();

        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(remoteUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
            httpURLConnection.setReadTimeout(READ_TIME_OUT);

            httpURLConnection.setRequestProperty("Accept-Language", "fr");
            httpURLConnection.setRequestProperty("Accept-Charset", inputStreamFormat);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + inputStreamFormat);
            httpURLConnection.setRequestProperty("Authorization", SingleApp.getAutorizationToken(username, password));
            httpURLConnection.setRequestProperty("X_FACILE_VERSION", BuildConfig.VERSION_NAME);

            httpURLConnection.connect();

            responseCode = httpURLConnection.getResponseCode();
            headersFields = httpURLConnection.getHeaderFields();

            if (responseCode >= 200 && responseCode < 300) {
                if (SingleApp.getUsername().equals(username) == false || SingleApp.getPassword().equals(password) == false) {
                    SingleApp.saveUsernameAndPassword(username, password);
                }
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(), inputStreamFormat);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                body = stringBuffer.toString();
            } else if ((responseCode >= 400 && responseCode < 500)) {
                if (responseCode == 401) {
                    responseCode = WebService.BAD_IDENTIFIERS;
                }
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                }
                stringBuffer.append(line);
                body = stringBuffer.toString();
            } else {
                body = httpURLConnection.getResponseMessage();
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            manageError(e);
        }

    }


    // submit scan informations
    public void uploadForm(String businessFileId, Form form, Bitmap image) {
        String remoteUrl = SingleApp.getBaseURL() + "/" + businessFileId + SingleApp.UPLOAD_SCAN_URL;
        StringBuilder sb = new StringBuilder();

        HttpURLConnection httpURLConnection = null;


        try {

            // convert bitmap to byte
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                int imageLength = stream.size();
                byte[] imageData = stream.toByteArray();

                sb.append("<upload_file>");
                sb.append("<object_zname>" + form.type + "</object_zname>");

                // if imageData is not empty
                sb.append("<upload_file_name>android-picture.jpg</upload_file_name>"); // seems no error here...
                sb.append("<upload_file_size>" + imageLength + "</upload_file_size>");
                sb.append("<file_data_base64>");
                sb.append(Base64.encodeBytes(imageData));
                sb.append("</file_data_base64>");
                // end
            } else {
                sb.append("<upload_file>");
                sb.append("<object_zname>" + form.type + "</object_zname>");

                // if imageData is not empty
                sb.append("<upload_file_name>android-picture.jpg</upload_file_name>"); // seems no error here...
                sb.append("<upload_file_size>" + "</upload_file_size>");
                sb.append("<file_data_base64>");
                sb.append("</file_data_base64>");

            }
            sb.append("<les_champs>");
            for (FormField field : form.fields) {
                if (field.type.equals("signature")) {
                    if (SingleApp.getSignature() != null) {
                        ByteArrayOutputStream sstream = new ByteArrayOutputStream();
                        Bitmap signature = SingleApp.getSignature();
                        signature.compress(Bitmap.CompressFormat.PNG, 100, sstream);
                        byte[] signatureData = sstream.toByteArray();

                        sb.append("<" + field.key + ">");
                        sb.append("<file_name>" + field.key + ".png</file_name>\n");
                        sb.append("<file_size>" + sstream.size() + "</file_size>\n");
                        sb.append("<file_data_base64>" + Base64.encodeBytes(signatureData) + "</file_data_base64>\n");
                        sb.append("</" + field.key + ">");

                        SingleApp.clearSignature();
                    }
                } else {
                    sb.append("<" + field.key + ">" + field.valueHolder.getText() + "</" + field.key + ">");
                }
            }
            sb.append("</les_champs>");
            sb.append("</upload_file>");
            String postData = sb.toString();

            URL url = new URL(remoteUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.addRequestProperty("Cookie", SingleApp.getSessionId());

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
            httpURLConnection.setReadTimeout(READ_TIME_OUT);

            httpURLConnection.setRequestProperty("Accept-Language", "fr");
            httpURLConnection.setRequestProperty("Accept-Charset", inputStreamFormat);
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(postData.length()));
            httpURLConnection.setRequestProperty("Content-Type", "application/xml");
            httpURLConnection.setRequestProperty("Authorization", SingleApp.getAutorizationToken());
            httpURLConnection.setRequestProperty("X_FACILE_VERSION", BuildConfig.VERSION_NAME);

            httpURLConnection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            writer.write(postData);
            writer.flush();

            responseCode = httpURLConnection.getResponseCode();
            headersFields = httpURLConnection.getHeaderFields();
            if ((responseCode >= 200 && responseCode < 300)) {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(), inputStreamFormat);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                body = stringBuffer.toString();
            } else if ((responseCode >= 400 && responseCode < 500)) {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                }
                stringBuffer.append(line);
                body = stringBuffer.toString();
            } else
                body = httpURLConnection.getResponseMessage();
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            //			manageError(e);
        }

    }


    ////
    // Image download functions
    ////

    // Download an image and return a Bitmap with it original size
    public Bitmap downloadBitmap(String urldisplay) {
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
            manageError(e);
            return null;
        }
        return mIcon11;
    }

    // Download an image and return a resized Bitmap
    // Setting "isScalingUp" to "true" allow us to render a prettier Bitmap when scaling up
    public Bitmap downloadBitmapAndResize(String urldisplay, int width, int height, boolean isScalingUp) {
        Bitmap mIcon11 = null;
        Bitmap tmp = null;
        try {
            InputStream in = new java.net.URL(urldisplay.replace("_0.jpg", "/0.jpg")).openStream(); // This "replace()" is maybe temp
            BitmapFactory.Options opt = new BitmapFactory.Options();

            // give anything "> 1" to activate "inSampleSize"
            opt.inSampleSize = 2;
            tmp = BitmapFactory.decodeStream(in, null, opt);
        } catch (Exception e) {
            e.printStackTrace();
            manageError(e);
            return null;
        }

        // the fourth parameter of "createScaledBitmap" doesn't matter when we scale down the Bitmap
        // otherwise: 
        // Passing filter = false will result in a blocky, pixellated image.
        // Passing filter = true will give you smoother edges.
        mIcon11 = Bitmap.createScaledBitmap(tmp, width, height, isScalingUp);

        tmp.recycle();

        SingleApp.saveImage(urldisplay, mIcon11);
        return mIcon11;
    }

    ////
    // Error handling functions
    ////

    // TODO: (Maybe)
    // manageError take an "Exception" as parameter.
    // Maybe we should create a "manageError" called with our http request results --> "manageError(long result)".
    // That would allow us to use "showError" with http request error
    private void manageError(Exception e) {
        error = e.getClass().getSimpleName();
        if (error.equals("UnknownHostException"))
            responseCode = WebService.NO_CONNECTION;
    }


    static public void showError(long result) {
        if (result == WebService.NO_CONNECTION) {
            ToastNoQueue.makeText(R.string.connexion_error, Toast.LENGTH_SHORT).show();
        } else if (result == WebService.BAD_IDENTIFIERS) {
            ToastNoQueue.makeText(R.string.bad_identifiers, Toast.LENGTH_SHORT).show();
        }
    }


}

