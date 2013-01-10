package com.conatix.FetchURLs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLFetcher {

	public static void main(String[] args) {
		String url = "http://en.wikipedia.org/wiki/Category:Engineering";
		String format = "<http://www.w3.org/2000/01/rdf-schema#label>";
		String language = "en";
		String fileName = "EnglishWikiEnginnering.rdf";

		for (int i = 0; i < args.length; i++) {
			if ("-url".equals(args[i])) {
				url = args[i + 1];
				i++;
			} else if ("-format".equals(args[i])) {
				format = args[i + 1];
				i++;
			} else if ("-language".equals(args[i])) {
				language = args[i + 1];
				i++;
			} else if ("-filename".equals(args[i])) {
				fileName = args[i + 1];
				i++;
			}
		}

		String siteContent = getUrlContent(url);

		String subCategoriesContent = getMWsubCategories(siteContent);

		ListOfTitlesUrls categoriesNUrls = getSubCategories(subCategoriesContent);

		// ListOfTitlesUrls titlesNUrls = getTitlesNURLs(siteContent);

		// for (int j = 0; j < titlesNUrls.allUrls.size(); j++) {
		// System.out.println(j + " -> TITLE = " + titlesNUrls.allUrls.get(j));
		// }

		// RDFFile stanbolRDF = new RDFFile(fileName, format, language,
		// titlesNUrls.allTitles, titlesNUrls.allUrls);
		// stanbolRDF.saveRDFFile();
	}

	private static ListOfTitlesUrls getSubCategories(String subCategoriesContent) {

		List<String> allTitles = new ArrayList<String>();
		List<String> allUrls = new ArrayList<String>();
		ListOfTitlesUrls subCategoriesNUrls = new ListOfTitlesUrls();

		Pattern pCategories = Pattern.compile("<a\\s.*\\shref=\".*\".*</a>");
		Matcher mCategories = pCategories.matcher(subCategoriesContent);
		String tag = "";
		String hrefURL, title;
		
		while (mCategories.find()) {
			tag = mCategories.group();
			hrefURL = getHrefURL(tag);
			title = getTitle(tag);
//			System.out.println(tag);
		}
		// The results are:
		// <a class="categorytreelabel  categorytreelabelns14 categorytreelabelcategory"
		// href="/wiki/category:technology">technology</a>
		
		return null;
	}

	private static String getTitle(String tag) {
		String title = "";
		tag = tag.replaceAll("<a\\s.*href=\".*\"\\w*>", "");
		title = tag.replaceAll("</a>", "");
		return title;
	}

	private static String getHrefURL(String tag) {
		String hrefURL = "";
		String[] tagSplits1 = tag.split("\\shref=\"");
		hrefURL = tagSplits1[1].split("\".*>")[0];
		System.out.println(hrefURL);
		return hrefURL;
	}

	private static String getMWsubCategories(String siteContent) {

		String[] contentSplits = siteContent
				.split("<div id=\"mw-subcategories\">");
		String mwPageAndAll = contentSplits[1];
		String[] divSplits = mwPageAndAll.split("</div><div id=\"mw-pages\">");

		return divSplits[0];
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
			strUrl = getUrlFromTag(tag);

			if (isValidURL(strUrl)) {
				allTitles.add(ttl);
				allUrls.add(strUrl);
				System.out.println("Tag: " + tag + "\nurl: " + strUrl
						+ "\ntitle: " + ttl);
			}
		}

		ListOfTitlesUrls ttlURL = new ListOfTitlesUrls();
		ttlURL.allTitles = allTitles;
		ttlURL.allUrls = allUrls;

		return ttlURL;
	}

	private static boolean isValidURL(String strUrl) {
		URL validURL;
		boolean isValid = false;
		try {
			validURL = new URL(strUrl);
			isValid = true;
		} catch (MalformedURLException e) {
			isValid = false;
		}

		return isValid;
	}

	private static String getUrlFromTag(String tag) {
		String url = "";
		Pattern p = Pattern.compile("<a\\shref=\".*\"\\stitle");
		Matcher m = p.matcher(tag);
		while (m.find()) {
			url = m.group().trim();
			url = url.substring(6, url.length() - 7);
		}
		if (!url.contains("wikipedia.org")) {
			url = "http://en.wikipedia.org" + url;
		}
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