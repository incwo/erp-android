package com.incwo.facilescan.helpers.rss;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RssDateParser {
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
	private static SimpleDateFormat simpleDateFormatISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
	
	public RssDateParser() {
	}
	
	public static Date parse(String d) {
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			return simpleDateFormat.parse(d);
		}
		catch (Exception e) {
		}
		
		simpleDateFormatISO.setTimeZone(TimeZone.getTimeZone("Europe/Brussels"));
		try {
			return simpleDateFormatISO.parse(d);
		}
		catch (Exception e) {
		}

		return new Date(2012, 1, 1);
	}
}
