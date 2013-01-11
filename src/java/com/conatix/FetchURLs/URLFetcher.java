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
		String validateUrl = "http://en.wikipedia.org";
		String url = "http://en.wikipedia.org/wiki/Category:Engineering";
		String format = "<http://www.w3.org/2000/01/rdf-schema#label>";
		String language = "en";
		String fileName = "EnglishWikiEnginnering.rdf";
		boolean append = false;

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
			} else if ("-validateUrl".equals(args[i])) {
				validateUrl = args[i + 1];
				i++;
			} else if ("-append".equals(args[i])) {
				append = Boolean.parseBoolean(args[i + 1]);
				i++;
			}
		}

		String siteContent = getUrlContent(url);

		String subCategoriesContent = getMWsubCategories(siteContent);
		ListOfTitlesUrls categoriesNUrls = new ListOfTitlesUrls();
		categoriesNUrls = getTitlesAndUrls(subCategoriesContent);
		
		String subPagesContent = getMWSubPages(siteContent);
		ListOfTitlesUrls pagesNUrls = new ListOfTitlesUrls();
		pagesNUrls = getTitlesAndUrls(subPagesContent);
		
		List<String> validCategoriesURLs = makeValidURLs(categoriesNUrls.allUrls, validateUrl);
		List<String> validPagesURLs = makeValidURLs(pagesNUrls.allUrls, validateUrl);
		
		ListOfTitlesUrls validatedCategoriesAndUrls = new ListOfTitlesUrls();
		validatedCategoriesAndUrls.allTitles.addAll(categoriesNUrls.allTitles);
		validatedCategoriesAndUrls.allUrls.addAll(validCategoriesURLs);
		
		ListOfTitlesUrls validatedPagesAndUrls = new ListOfTitlesUrls();
		validatedPagesAndUrls.allTitles.addAll(pagesNUrls.allTitles);
		validatedPagesAndUrls.allUrls.addAll(validPagesURLs);
		
//		System.out.println("Titles size: " + validatedCategoriesAndUrls.allTitles.size() + " URL size: " + validatedCategoriesAndUrls.allUrls.size() + " get size: " + validatedCategoriesAndUrls.size());
		
//		System.out.println(validatedCategoriesAndUrls.toString());

		RDFFile stanbolRDF = new RDFFile(format, language, validatedPagesAndUrls.allTitles, validatedPagesAndUrls.allUrls);
		System.out.println(stanbolRDF.toString());
		stanbolRDF.saveRDFFile(fileName);
		// RDFFile stanbolRDF = new RDFFile(format, language,
		// titlesNUrls.allTitles, titlesNUrls.allUrls);
		// stanbolRDF.saveRDFFile();
	}

	private static List<String> makeValidURLs(List<String> allUrls, String webUrl) {
		List<String> validURLs = new ArrayList<String>();
		for(int i = 0; i < allUrls.size(); i++){
			String url = allUrls.get(i);
			String validUrl = "";
			if(isValidURL(url)){
				validUrl = url;
			} else {
				validUrl = webUrl + url;
			}
			validURLs.add(validUrl);
		}
		return validURLs;
	}

	private static String getMWSubPages(String siteContent) {
		String[] contentSplits = siteContent.split("<div id=\"mw-pages\">");
		String mwPageAndAll = contentSplits[1];
		String[] divSplits = mwPageAndAll
				.split("</div>.*<!-- /bodycontent -->");

		return divSplits[0];
	}

	private static ListOfTitlesUrls getTitlesAndUrls(String subTitlesContent) {

		List<String> allTitles = new ArrayList<String>();
		List<String> allUrls = new ArrayList<String>();
		ListOfTitlesUrls subTitlesNUrls = new ListOfTitlesUrls();
		
		Pattern pCategories = Pattern.compile("<a\\s.*href=\".*\".*</a>");
		Matcher mCategories = pCategories.matcher(subTitlesContent);
		String tag = "";
		String hrefURL, title;
		
		while (mCategories.find()) {
			tag = mCategories.group();
			hrefURL = getHrefURL(tag);
			title = getTitle(tag);
			allUrls.add(hrefURL);
			allTitles.add(title);
		}
		subTitlesNUrls.allTitles.addAll(allTitles);
		subTitlesNUrls.allUrls.addAll(allUrls);

		return subTitlesNUrls;
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
		return hrefURL;
	}

	private static String getMWsubCategories(String siteContent) {

		String[] contentSplits = siteContent
				.split("<div id=\"mw-subcategories\">");
		String mwPageAndAll = contentSplits[1];
		String[] divSplits = mwPageAndAll.split("</div><div id=\"mw-pages\">");

		return divSplits[0];
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