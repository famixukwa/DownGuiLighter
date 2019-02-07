package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


/**
 *@author  Kain
 *<h1>InputHandler</h1>
 *This class imports the book file and the highlights HTML files.
 *
 */
public class InputHandler {
	public static File highlights;
	public static File ebookFile;
	public static void setEbookFile(File ebookFile) {
		ebookFile = ebookFile;
	}

	public static File getEbookFile() {
		return ebookFile;
	}

	public static void setHighlights(File highlightsFile) {
		highlights = highlightsFile;
	}

	

	/**
	 *
	 *This method gets the HTML with the highlights and extracts them
	 *to a list of HTML Elements (Jsoup)
	 */
	public static List<Element> getHighlightFileSnippets() {
		Document  highlightsFileContent=null;
		File highlightsFile = highlights;
		try {
			highlightsFileContent = Jsoup.parse(highlightsFile, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Element> highlightsList = highlightsFileContent.select("div.noteText");
		return highlightsList;
	}
}
