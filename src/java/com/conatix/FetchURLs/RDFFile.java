package com.conatix.FetchURLs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RDFFile {
	private static String format;
	private static String language;
	private static List<String> titles;
	private static List<String> urls;
	private static int size;
	
	
	
	@SuppressWarnings("static-access")
	public RDFFile(String format, String language,
			List<String> titles, List<String> urls) {
		super();
		this.format = format;
		this.language = language;
		this.titles = titles;
		this.urls = urls;
		this.size = titles.size();
	}
	
	public void saveRDFFile(String fileName){
		saveRDFFile(fileName, false);
	}
	
	public void saveRDFFile(String fileName, boolean append) {
		
		FileWriter fStream;
		try {
			fStream = new FileWriter(fileName, append);
			BufferedWriter rdfWriter = new BufferedWriter(fStream);
			String rdfFormat = this.toString();
			rdfWriter.write(rdfFormat);
			rdfWriter.flush();
			rdfWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}


	@Override
	public String toString() {
		String rdfFormat = "";
		for (int i = 0; i < size; i++){
			rdfFormat += "<" + urls.get(i) + "> " + format + " \"" + titles.get(i) + "\"@" + language + " .\n";
		}
		return rdfFormat;
	}

	
	
}
