package com.conatix.FetchURLs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLFetcher {
	static String validateUrl = "http://fa.wikipedia.org";
	static String url = "http://fa.wikipedia.org/wiki/%D8%B1%D8%AF%D9%87:%D9%85%D9%87%D9%86%D8%AF%D8%B3%DB%8C";
	static String format = "<http://www.w3.org/2000/01/rdf-schema#label>";
	static String language = "fa";
	static String fileName = "wikis/PersianMeasurement.rdf";
	static boolean append = false;

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {

		url = "http://fa.wikipedia.org/wiki/%D8%B1%D8%AF%D9%87:%D8%A7%D9%86%D8%AF%D8%A7%D8%B2%D9%87%E2%80%8C%DA%AF%DB%8C%D8%B1%DB%8C";

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

		// startFetchWiki();
		String stanbolUrl = "http://5.9.86.210:8082/entityhub/entity";
		String type = "PUT";
		String contentType = "text/rdf+n3";
		String reqbody = "<http://fa.wikipedia.org/wiki/آنگولا> <http://www.w3.org/2000/01/rdf-schema#label> \"آنگولا\"@fa .";

		String result = null;

		try {
			HttpURLConnection stanbolCon = getHttpConnection(stanbolUrl, type,
					contentType);
			// you can add any request body here if you want to post
			if (reqbody != null) {
				stanbolCon.setDoInput(true);
				stanbolCon.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(
						stanbolCon.getOutputStream());
				out.writeBytes(reqbody);
				out.flush();
				out.close();
				System.out.println(reqbody);
			}
			
			stanbolCon.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(stanbolCon.getInputStream()));
			String temp = null;
			StringBuilder sb = new StringBuilder();
			while ((temp = in.readLine()) != null) {
				sb.append(temp).append(" ");
			}
			result = sb.toString();
			in.close();
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("RESULT = " + result);
	}

	private static HttpURLConnection getHttpConnection(String url, String type,
			String contentType) {
		URL uri = null;
		HttpURLConnection con = null;
		try {
			uri = new URL(url);
			con = (HttpURLConnection) uri.openConnection();
			con.setRequestMethod(type); // type: POST, PUT, DELETE, GET
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setConnectTimeout(60000); // 60 secs
			con.setReadTimeout(60000); // 60 secs
			con.setRequestProperty("Content-Type", contentType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return con;
	}

	private static void httpPUTRequest() throws IOException {
		URL reqUrl = new URL("http://5.9.86.210:8082/entityhub/entity");
		HttpURLConnection httpCon = (HttpURLConnection) reqUrl.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("PUT");
		OutputStreamWriter out = new OutputStreamWriter(
				httpCon.getOutputStream());
		out.write("Resource content");
		out.close();
	}

	private static void startFetchWiki() {

		ListOfTitlesUrls listTitlesNUrls = new ListOfTitlesUrls();

		// listTitlesNUrls = getLisUrls(siteContent);
		//
		// List<String> validListUrls = makeValidURLs(listTitlesNUrls.allUrls,
		// validateUrl);
		// ListOfTitlesUrls validatedTitlesUrls = new ListOfTitlesUrls();
		// validatedTitlesUrls.allTitles.addAll(listTitlesNUrls.allTitles);
		// validatedTitlesUrls.allUrls.addAll(validListUrls);
		//
		// RDFFile rdfStanbol = new RDFFile(format, language,
		// validatedTitlesUrls.allTitles, validatedTitlesUrls.allUrls);
		// rdfStanbol.saveRDFFile(fileName);
		// System.out.println(rdfStanbol.toString());

		// String subCategoriesContent = getMWsubCategories(siteContent);
		// ListOfTitlesUrls categoriesNUrls = new ListOfTitlesUrls();
		// categoriesNUrls = getTitlesAndUrls(subCategoriesContent);
		String siteContent = getUrlContent(url);
		String subPagesContent = getMWSubPages(siteContent);
		ListOfTitlesUrls pagesNUrls = new ListOfTitlesUrls();
		pagesNUrls = getTitlesAndUrls(subPagesContent);

		List<String> validPagesURLs = makeValidURLs(pagesNUrls.allUrls,
				validateUrl);
		ListOfTitlesUrls validatedPagesAndUrls = new ListOfTitlesUrls();
		validatedPagesAndUrls.allTitles.addAll(pagesNUrls.allTitles);
		validatedPagesAndUrls.allUrls.addAll(validPagesURLs);
		RDFFile stanbolRDF = new RDFFile(format, language,
				validatedPagesAndUrls.allTitles, validatedPagesAndUrls.allUrls);
		System.out.println(stanbolRDF.toString());
		stanbolRDF.saveRDFFile(fileName);

	}

	private static ListOfTitlesUrls getLisUrls(String siteContent) {
		// <li><span class="flagicon"><img alt="flag of azerbaijan.svg"
		// src="//upload.wikimedia.org/wikipedia/commons/thumb/d/dd/flag_of_azerbaijan.svg/22px-flag_of_azerbaijan.svg.png"
		// width="22" height="11" class="thumbborder"
		// srcset="//upload.wikimedia.org/wikipedia/commons/thumb/d/dd/flag_of_azerbaijan.svg/33px-flag_of_azerbaijan.svg.png 1.5x, //upload.wikimedia.org/wikipedia/commons/thumb/d/dd/flag_of_azerbaijan.svg/44px-flag_of_azerbaijan.svg.png 2x"
		// />&#160;</span><a
		// href="/wiki/%d8%ac%d9%85%d9%87%d9%88%d8%b1%db%8c_%d8%a2%d8%b0%d8%b1%d8%a8%d8%a7%db%8c%d8%ac%d8%a7%d9%86"
		// title="جمهوری آذربایجان">جمهوری آذربایجان</a></li>

		List<String> allTitles = new ArrayList<String>();
		List<String> allUrls = new ArrayList<String>();
		ListOfTitlesUrls subTitlesNUrls = new ListOfTitlesUrls();

		Pattern pLi = Pattern
				.compile("<li>.*<a\\s.*href=\".*\".*>.*</a>.*</li>");
		Matcher mLi = pLi.matcher(siteContent);
		String listContent = "";
		String hrefURL, title;
		Pattern pTU = Pattern.compile("<a\\s.*href=\".*\".*</a>");
		while (mLi.find()) {
			listContent = mLi.group();
			Matcher matchTU = pTU.matcher(listContent);
			String tag = "";
			while (matchTU.find())
				tag = matchTU.group();

			hrefURL = getHrefURL(tag);
			title = getTitle(tag);
			allUrls.add(hrefURL);
			allTitles.add(title);
		}
		subTitlesNUrls.allTitles.addAll(allTitles);
		subTitlesNUrls.allUrls.addAll(allUrls);

		return subTitlesNUrls;
	}

	private static String getUrlFromList(String listContent) {
		String retUrl = "";

		return retUrl;
	}

	@SuppressWarnings("deprecation")
	private static List<String> makeValidURLs(List<String> allUrls,
			String webUrl) {
		List<String> validURLs = new ArrayList<String>();
		for (int i = 0; i < allUrls.size(); i++) {
			String url = allUrls.get(i);
			String validUrl = "";
			if (isValidURL(url)) {
				validUrl = url;
			} else {
				validUrl = webUrl + url;
			}
			validUrl = URLDecoder.decode(validUrl);
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

	@SuppressWarnings("unused")
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