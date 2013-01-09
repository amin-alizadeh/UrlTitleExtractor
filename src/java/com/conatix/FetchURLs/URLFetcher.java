package com.conatix.FetchURLs;

import java.io.BufferedReader;
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
//		System.out.println(siteContent);
		List<String[]> titlesNUrls = getTitlesNUrls(siteContent);
		for (int i = 0; i < titlesNUrls.size(); i++){
//			System.out.println(i + " URL = " + titlesNUrls.get(i)[1]);
		}
		
	}

	private static List<String[]> getTitlesNUrls(String siteContent) {
		List <String[]> allTitlesUrls = new ArrayList<String[]>();
		
		Pattern p = Pattern.compile("<a\\shref=\".*\"\\stitle=\".*\">");
	    Matcher m = p.matcher(siteContent);
	    int count = 0;
	    String tag;
//	    UrlTitle titleUrl = new UrlTitle();
	    String[] TU = new String[2];
	    while (m.find()){
	    	tag = m.group();
	    	
	    	TU[0] = getTitleFromTag(tag);
	    	TU[1] = getUrlFromTag(tag);
	    	
	    	allTitlesUrls.add(TU);

//	    	System.out.println(tag);
//	    	System.out.println(TU[0]);
	    	System.out.println(allTitlesUrls.get(count)[0]);
//	    	titleUrl.title = getTitleFromTag(tag);
//	    	titleUrl.url = getUrlFromTag(tag);
//	    	System.out.println(titleUrl.title);
//	    	System.out.println(titleUrl.url);
//
//	    	allTitlesUrls.add(titleUrl);
//	    	titleUrl = null;
	    	
	    	count +=1;
	    }
	    
	    
	    for (int i = 0; i < allTitlesUrls.size(); i++){
			System.out.println(i + " URL = " + allTitlesUrls.get(i)[0]);
		}
	    
	    
//	    System.out.println(allTitlesUrls.size());
//	    System.out.println(count);
	    
//		String[] aSplits = siteContent.split("<a href=\".\" title");
//		for (int i = 0; i < aSplits.length; i++){
//			System.out.println(i + " -> " + aSplits[i]);
//		}
		return allTitlesUrls;
	}

	private static String getUrlFromTag(String tag) {
		String url = "";
		Pattern p = Pattern.compile("\\shref=\".*\"\\stitle");
	    Matcher m = p.matcher(tag);
	    while (m.find()){
	    	url = m.group().trim();
	    	url = url.substring(6, url.length()-7);
	    }
	    url = "http://fa.wikipedia.org" + url;
		return url;
	}

	private static String getTitleFromTag(String tag) {
		String title = "";
		Pattern p = Pattern.compile("\\stitle=\".*\"");
	    Matcher m = p.matcher(tag);
	    while (m.find()){
	    	title = m.group().trim();
	    	title = title.substring(7, title.length()-1);
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
			while ((line = rd.readLine())!= null) {
				result += line.toLowerCase() + "\n";
			}
			rd.close();
			
//
//			String[] allSplits = result.split("<title>");
//			String[] titleTextSplit = allSplits[1].split("</title>");
//			String titleText = titleTextSplit[0].trim();
//			result = titleText;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
