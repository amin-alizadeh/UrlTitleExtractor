package com.conatix.FetchURLs;

import java.util.ArrayList;
import java.util.List;

public class ListOfTitlesUrls {
	public List<String> allTitles = new ArrayList<String>();
	public List<String> allUrls = new ArrayList<String>();

	public int size() {
		return allTitles.size();
	}

	public String toString() {
		String titlesNUrls = "";
		for (int i = 0; i < size(); i++) {
			titlesNUrls += "Title: " + allTitles.get(i) + "\nURL: "
					+ allUrls.get(i) + "\n";
		}
		return titlesNUrls;
	}
}
