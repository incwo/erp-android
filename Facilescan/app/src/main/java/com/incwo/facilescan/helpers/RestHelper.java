package com.incwo.facilescan.helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RestHelper {
	private final int connectTimeOut = 15 * 1000;
	private final int readTimeOut = 60 * 1000;

	private String xAutolibToken = "zookeiN4wee8";
	private String xAuthorization = "";

	public int responseCode;
	public Map<String, List<String>> headersFields;
	public String body;
	public String error;

	public RestHelper() {
//		xAutolibToken = SingleApp.getXAutolibToken(apiVersion);
		xAuthorization = null;

		responseCode = 500;
		headersFields = null;
		body = "";
		error = "";
	}

//	public RestHelper(String authorization, double apiVersion) {
//		xAutolibToken = SingleApp.getXAutolibToken(apiVersion);
//		xAuthorization = authorization;
//
//		responseCode = 500;
//		headersFields = null;
//		body = "";
//		error = "";
//	}

	public String getLanguage() {
		String language = Locale.getDefault().getLanguage();
		if (language.equals("fr"))
			return "fr";
		else
			return "en";
	}

	public void getFR(String remoteUrl) {
		HttpURLConnection httpURLConnection = null;

		try {
			URL url = new URL(remoteUrl);

			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setDoOutput(false);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setConnectTimeout(connectTimeOut);
			httpURLConnection.setReadTimeout(readTimeOut);

			httpURLConnection.setRequestProperty("Accept-Language", "fr");
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("X-Autolib-Token", xAutolibToken);
			if (xAuthorization!= null)
				httpURLConnection.setRequestProperty("X-Authorization", xAuthorization);

			httpURLConnection.connect();

			responseCode = httpURLConnection.getResponseCode();
			headersFields = httpURLConnection.getHeaderFields();

			if ((responseCode >= 200 && responseCode < 300)) {
				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line;
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				body = stringBuffer.toString();
			}
			else if ((responseCode >= 400 && responseCode < 500)) {
				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line;
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				body = stringBuffer.toString();
			}
			else
				body = httpURLConnection.getResponseMessage();

			httpURLConnection.disconnect();
		}
		catch (Exception e) {
			if(e != null)
			{
				if(e.getMessage() != null)
				{
					if(e.getMessage().contains("Received authentication challenge is null"))
						responseCode = 401;
				}
			}
			e.toString();
		}
	}

	public void get(String remoteUrl) {
		HttpURLConnection httpURLConnection = null;

		try {
			URL url = new URL(remoteUrl);

			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setDoOutput(false);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setConnectTimeout(connectTimeOut);
			httpURLConnection.setReadTimeout(readTimeOut);

			httpURLConnection.setRequestProperty("Accept-Language", getLanguage());
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("X-Autolib-Token", xAutolibToken);
			if (xAuthorization!= null)
				httpURLConnection.setRequestProperty("X-Authorization", xAuthorization);

			httpURLConnection.connect();

			responseCode = httpURLConnection.getResponseCode();
			headersFields = httpURLConnection.getHeaderFields();

			if ((responseCode >= 200 && responseCode < 300)) {
				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line;
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				body = stringBuffer.toString();
			}
			else if ((responseCode >= 400 && responseCode < 500)) {
				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line;
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				body = stringBuffer.toString();
			}
			else
				body = httpURLConnection.getResponseMessage();

			httpURLConnection.disconnect();
		}
		catch (Exception e) {
			if(e != null)
			{
				if(e.getMessage() != null)
				{
					if(e.getMessage().contains("Received authentication challenge is null"))
						responseCode = 401;
				}
			}
			e.toString();
		}
	}

	public void post(String remoteUrl, String postBody) {
		HttpURLConnection httpURLConnection = null;

		try {
			URL url = new URL(remoteUrl);

			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
//			if (!SingleApp.developmentServer)
//				httpURLConnection.setChunkedStreamingMode(0);
			httpURLConnection.setConnectTimeout(connectTimeOut);
			httpURLConnection.setReadTimeout(readTimeOut);

			httpURLConnection.setRequestProperty("Accept-Language", getLanguage());
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
//			httpURLConnection.setRequestProperty("Content-Type", "application/json");
//			httpURLConnection.setRequestProperty("X-Autolib-Token", xAutolibToken);
			if (xAuthorization!= null)
				httpURLConnection.setRequestProperty("X-Authorization", xAuthorization);

			httpURLConnection.connect();

			OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
			writer.write(postBody);
			writer.flush();

			responseCode = httpURLConnection.getResponseCode();
			headersFields = httpURLConnection.getHeaderFields();

			if ((responseCode >= 200 && responseCode < 300)) {
				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line;
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				body = stringBuffer.toString();
			}
			else if ((responseCode >= 400 && responseCode < 500)) {
				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line;
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				body = stringBuffer.toString();
			}
			else
				body = httpURLConnection.getResponseMessage();

			httpURLConnection.disconnect();
		}
		catch (Exception e) {
			if(e != null)
			{
				if(e.getMessage() != null)
				{
					if(e.getMessage().contains("Received authentication challenge is null"))
						responseCode = 401;
				}
			}
			e.toString();
		}
	}

	public void put(String remoteUrl, String postBody) {
		HttpURLConnection httpURLConnection = null;

		try {
			URL url = new URL(remoteUrl);

			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("PUT");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
//			if (!SingleApp.developmentServer)
//				httpURLConnection.setChunkedStreamingMode(0);
			httpURLConnection.setConnectTimeout(connectTimeOut);
			httpURLConnection.setReadTimeout(readTimeOut);

			httpURLConnection.setRequestProperty("Accept-Language", getLanguage());
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("X-Autolib-Token", xAutolibToken);
			if (xAuthorization!= null)
				httpURLConnection.setRequestProperty("X-Authorization", xAuthorization);

			httpURLConnection.connect();

			OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
			writer.write(postBody);
			writer.flush();

			responseCode = httpURLConnection.getResponseCode();
			headersFields = httpURLConnection.getHeaderFields();

			if ((responseCode >= 200 && responseCode < 300)) {
				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line;
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				body = stringBuffer.toString();
			}
			else if ((responseCode >= 400 && responseCode < 500)) {
				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line;
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				body = stringBuffer.toString();
			}
			else
				body = httpURLConnection.getResponseMessage();

			httpURLConnection.disconnect();
		}
		catch (Exception e) {
			if(e != null)
			{
				if(e.getMessage() != null)
				{
					if(e.getMessage().contains("Received authentication challenge is null"))
						responseCode = 401;
				}
			}
			e.toString();
		}
	}
	
	public void delete(String remoteUrl) {
		HttpURLConnection httpURLConnection = null;

		try {
			URL url = new URL(remoteUrl);

			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("DELETE");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(false);
			httpURLConnection.setUseCaches(false);
//			if (!SingleApp.developmentServer)
//				httpURLConnection.setChunkedStreamingMode(0);
			httpURLConnection.setConnectTimeout(connectTimeOut);
			httpURLConnection.setReadTimeout(readTimeOut);

			httpURLConnection.setRequestProperty("Accept-Language", getLanguage());
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("X-Autolib-Token", xAutolibToken);
			if (xAuthorization!= null)
				httpURLConnection.setRequestProperty("X-Authorization", xAuthorization);

			httpURLConnection.connect();

			responseCode = httpURLConnection.getResponseCode();
			headersFields = httpURLConnection.getHeaderFields();

			if ((responseCode >= 200 && responseCode < 300)) {
				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line;
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				body = stringBuffer.toString();
			}
			else if ((responseCode >= 400 && responseCode < 500)) {
				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line;
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				body = stringBuffer.toString();
			}
			else
				body = httpURLConnection.getResponseMessage();

			httpURLConnection.disconnect();
		}
		catch (Exception e) {
			if(e != null)
			{
				if(e.getMessage() != null)
				{
					if(e.getMessage().contains("Received authentication challenge is null"))
						responseCode = 401;
				}
			}
			e.toString();
		}
		
	}
	
}
