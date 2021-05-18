package com.incwo.facilescan.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.incwo.facilescan.BuildConfig;
import com.incwo.facilescan.R;
import com.incwo.facilescan.helpers.Account;
import com.incwo.facilescan.helpers.Base64;
import com.incwo.facilescan.scan.FormField;
import com.incwo.facilescan.scan.Form;
import com.incwo.facilescan.toasts.ToastNoQueue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WebService {

    final String inputStreamFormat = "UTF-8";

    public int responseCode;
    public Map<String, List<String>> headersFields;
    public String body;
    public String error;
    public List<String> cookies = null;


    // Error codes
    static public int NO_CONNECTION = -1;
    static public int BAD_IDENTIFIERS = -2;



    public void get(String url) {
        HttpGet(url, "UTF-8", SingleApp.getAccount());
    }

    public void getNews() {
        HttpGet(URLProvider.NEWS_RSS_URL, "application/rss+xml", null);
    }

    public void getVideos() {
        HttpGet(URLProvider.VIDEOS_RSS_URL, "application/rss+xml", null);
    }

    // Pass account = null if not authentified.
    public void HttpGet(String remoteUrl, String contentType, @Nullable Account account) {
        try {
            URL url = new URL(remoteUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            setupConnection(connection, "GET", contentType, account);
            connection.connect();

            if (cookies == null)
                cookies = connection.getHeaderFields().get("Set-Cookie");

            responseCode = connection.getResponseCode();
            headersFields = connection.getHeaderFields();

            if ((responseCode >= 200 && responseCode < 300)) {
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), inputStreamFormat);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                body = stringBuffer.toString();
            } else if ((responseCode >= 400 && responseCode < 500)) {
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                }
                stringBuffer.append(line);
                body = stringBuffer.toString();
            } else
                body = connection.getResponseMessage();
            connection.disconnect();
        } catch (Exception e) {
            manageError(e);
        }

    }

    // This function is very slow
    private HttpURLConnection tryConnectToFacileAndManageCookies(HttpURLConnection httpURLConnection, URL url, Account account) {
        try {
            // If we don't have any cookies,
            // send any request in order to get cookies
            // didn't found anything smarter than that...
            if (cookies == null) {
                URL tmpURL = new URL(URLProvider.getLoginUrl(account));
                HttpURLConnection tmpConnection = (HttpURLConnection) tmpURL.openConnection();
                final String contentType = "application/x-www-form-urlencoded;charset=" + inputStreamFormat;
                setupConnection(tmpConnection, "POST", contentType, null);
                tmpConnection.connect();

                cookies = tmpConnection.getHeaderFields().get("Set-Cookie");
                tmpConnection.disconnect();
            }

            //
            // Test connections identifiers
            // We try to connect, and if the identifiers are bad, an exception will be raised
            // (very ugly)
            //
            URL tmpURL = new URL(URLProvider.getScanUrl(account));
            HttpURLConnection tmpConnection = (HttpURLConnection) tmpURL.openConnection();
            setupConnection(tmpConnection, "GET", "application/x-www-form-urlencoded;charset=" + inputStreamFormat, account);

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
            SingleApp.saveAccount(account);

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
                        cookieManager.setCookie(URLProvider.getUnauthBaseUrl(), tmp);
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

    public void logToDesktop(Account account) {
        try {
            URL url = new URL(URLProvider.getLoginUrl(account));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            setupConnection(httpURLConnection, "POST", "application/x-www-form-urlencoded;charset=" + inputStreamFormat, account);
            httpURLConnection = tryConnectToFacileAndManageCookies(httpURLConnection, url, account);

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

    public void logToScan(Account account) {
        try {
            URL url = new URL(URLProvider.getScanUrl(account));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            setupConnection(httpURLConnection, "GET", "application/x-www-form-urlencoded;charset=" + inputStreamFormat, account);
            httpURLConnection.connect();

            responseCode = httpURLConnection.getResponseCode();
            headersFields = httpURLConnection.getHeaderFields();

            if (responseCode >= 200 && responseCode < 300) {
                if (!account.equals(SingleApp.getAccount())) {
                    SingleApp.saveAccount(account);
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

    public static void setSessionCookie(String session) {
        CookieManager.getInstance().removeSessionCookie();
        CookieManager.getInstance().setCookie(URLProvider.getUnauthBaseUrl(), session);
    }

    public static void removeCookies() {
        if (CookieManager.getInstance().getCookie(URLProvider.getUnauthBaseUrl()) != null) {
            CookieManager.getInstance().setCookie(URLProvider.getUnauthBaseUrl(), "");
        }
        CookieManager.getInstance().removeSessionCookie();
    }

    // submit scan informations
    public void uploadForm(String businessFileId, Form form, Bitmap image, Account account) {

        try {
            String postData = buildPOSTData(form, image);
            URL url = new URL(URLProvider.getUploadScanUrl(businessFileId, account));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            setPOSTFormConnectionHeaders(httpURLConnection, postData.length());
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

    private String buildPOSTData(Form form, Bitmap image) {
        StringBuilder sb = new StringBuilder();
        sb.append("<upload_file>");
        sb.append("<object_zname>" + form.type + "</object_zname>");

        if (image != null) {
            sb.append(buildImageTags(image));
        }

        sb.append("<les_champs>");
        for (FormField field : form.fields) {
            if (field.type.equals("signature")) {
                Bitmap signature = SingleApp.getSignature();
                if (signature != null) {
                    sb.append(buildSignatureTags(signature, field.key));
                    SingleApp.clearSignature();
                }
            } else {
                String tags = "<" + field.key + ">" + field.savedValue + "</" + field.key + ">";
                sb.append(tags);
            }
        }
        sb.append("</les_champs>");
        sb.append("</upload_file>");
        return sb.toString();
    }

    private String buildImageTags(Bitmap image) {
        StringBuilder stringBuilder = new StringBuilder();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageData = stream.toByteArray();

        stringBuilder.append("<upload_file_name>android-picture.jpg</upload_file_name>"); // seems no error here...
        stringBuilder.append("<upload_file_size>" + stream.size() + "</upload_file_size>");
        stringBuilder.append("<file_data_base64>");
        stringBuilder.append(Base64.encodeBytes(imageData));
        stringBuilder.append("</file_data_base64>");
        return stringBuilder.toString();
    }

    private String buildSignatureTags(Bitmap signature, String fieldKey) {
        StringBuilder stringBuilder = new StringBuilder();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signature.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] signatureData = stream.toByteArray();

        stringBuilder.append("<" + fieldKey + ">");
        stringBuilder.append("<file_name>" + fieldKey + ".png</file_name>\n");
        stringBuilder.append("<file_size>" + stream.size() + "</file_size>\n");
        stringBuilder.append("<file_data_base64>" + Base64.encodeBytes(signatureData) + "</file_data_base64>\n");
        stringBuilder.append("</" + fieldKey + ">");
        return stringBuilder.toString();
    }

    private void setupConnection(HttpURLConnection connection, String method, String contentType, @Nullable Account account) throws ProtocolException {
        final int CONNECT_TIME_OUT = 15 * 1000;
        final int READ_TIME_OUT = 60 * 1000;

        connection.setRequestMethod(method);
        connection.setDoOutput(method.equals("POST")); // Needed if setRequestedMethod() is called? The doc is very unclear
        connection.setConnectTimeout(CONNECT_TIME_OUT);
        connection.setReadTimeout(READ_TIME_OUT);
        connection.setInstanceFollowRedirects(true);

        connection.setRequestProperty("Accept-Language", "fr");
        connection.setRequestProperty("Accept-Charset", inputStreamFormat);
        connection.setRequestProperty("Content-Type", contentType);
        if(account != null) {
            connection.setRequestProperty("Authorization", account.getAuthorizationToken());
        }
        connection.setRequestProperty("X_FACILE_VERSION", BuildConfig.VERSION_NAME);
    }

    private void setPOSTFormConnectionHeaders(HttpURLConnection connection, int contentLength) throws ProtocolException {
        setupConnection(connection, "POST", "application/xml", SingleApp.getAccount());
        connection.addRequestProperty("Cookie", SingleApp.getSessionId());
        connection.setRequestProperty("Content-Length", String.valueOf(contentLength));
    }

    ////
    // Image download functions
    ////

    // Download an image and return a Bitmap with it original size
    public Bitmap downloadBitmap(String urldisplay) {
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            Bitmap mIcon11 = BitmapFactory.decodeStream(in);
            return mIcon11;
        } catch (Exception e) {
            e.printStackTrace();
            manageError(e);
            return null;
        }
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

