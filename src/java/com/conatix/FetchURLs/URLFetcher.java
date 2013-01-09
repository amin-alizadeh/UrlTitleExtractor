package com.conatix.FetchURLs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLFetcher {

	public static void main(String[] args) {
		String url = "http://fa.wikipedia.org/wiki/%D8%B1%D8%AF%D9%87:%D9%85%D9%82%D8%A7%D9%84%D9%87%E2%80%8C%D9%87%D8%A7%DB%8C_%D8%AE%D8%B1%D8%AF_%D8%B1%D8%A7%DB%8C%D8%A7%D9%86%D9%87";
		String siteContent = getUrlContent(url);

		ListOfTitlesUrls titlesNUrls = getTitlesNURLs(siteContent);

		for (int j = 0; j < titlesNUrls.allUrls.size(); j++) {
			System.out.println("TITLE = " + titlesNUrls.allUrls.get(j));
		}
		
		String format = "<http://www.w3.org/2000/01/rdf-schema#label>";
		String language = "fa";
		String fileName = "PersianWikiComputer.rdf";
		
		RDFFile stanbolRDF = new RDFFile(fileName, format, language, titlesNUrls.allTitles, titlesNUrls.allUrls);
		stanbolRDF.saveRDFFile();
	}


	private static ListOfTitlesUrls getTitlesNURLs(String siteContent) {
		List<String> allTitles = new ArrayList<String>();
		List<String> allUrls = new ArrayList<String>();
		Pattern p = Pattern.compile("<a\\shref=\".*\"\\stitle=\".*\">");
		Matcher m = p.matcher(siteContent);
		String tag;
		String ttl = "";
		String strUrl = "";

		while (m.find()) {
			tag = m.group();
			ttl = getTitleFromTag(tag);
			allTitles.add(ttl);

			strUrl = getUrlFromTag(tag);
			allUrls.add(strUrl);
		}

		ListOfTitlesUrls ttlURL = new ListOfTitlesUrls();
		ttlURL.allTitles = allTitles;
		ttlURL.allUrls = allUrls;

		return ttlURL;
	}

	private static String getUrlFromTag(String tag) {
		String url = "";
		Pattern p = Pattern.compile("\\shref=\".*\"\\stitle");
		Matcher m = p.matcher(tag);
		while (m.find()) {
			url = m.group().trim();
			url = url.substring(6, url.length() - 7);
		}
		url = "http://fa.wikipedia.org" + url;
		return url;
	}

	private static String getTitleFromTag(String tag) {
		String title = "";
		Pattern p = Pattern.compile("\\stitle=\".*\"");
		Matcher m = p.matcher(tag);
		while (m.find()) {
			title = m.group().trim();
			title = title.substring(7, title.length() - 1);
		}
		return title;
	}

	private static String getUrlContent(String recUrl) {

		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(recUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line.toLowerCase() + "\n";
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}

class ListOfTitlesUrls {
	List<String> allTitles = new ArrayList<String>();
	List<String> allUrls = new ArrayList<String>();
}