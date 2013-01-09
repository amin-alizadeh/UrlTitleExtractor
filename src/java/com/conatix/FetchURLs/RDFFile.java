package com.conatix.FetchURLs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public class RDFFile {
	private static String fileName;
	private static String format;
	private static String language;
	private static List<String> titles;
	private static List<String> urls;
	private static int size;
	
	
	
	@SuppressWarnings("static-access")
	public RDFFile(String fileName, String format, String language,
			List<String> titles, List<String> urls) {
		super();
		this.fileName = fileName;
		this.format = format;
		this.language = language;
		this.titles = titles;
		this.urls = urls;
		this.size = titles.size();
	}
	
	
	public void saveRDFFile() {
		
		FileWriter fStream;
//		System.out.println(logFileName);
		try {
			fStream = new FileWriter(fileName);
			BufferedWriter rdfWriter = new BufferedWriter(fStream);
			
			String line = "";
			for (int i = 0; i < size; i++){
				line = "<" + urls.get(i) + "> " + format + " \"" + titles.get(i) + "\"@" + language + " .";
				rdfWriter.write(line);
				rdfWriter.newLine();
			}

			rdfWriter.flush();
			rdfWriter.close();
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}

		
	}

	
	
}
