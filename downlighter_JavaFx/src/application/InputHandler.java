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
	private static File highlights;
	private static File Ebook;
	public static void setEbook(File Ebook) {
		Ebook = Ebook;
	}

	public static void setHighlights(File highlights) {
		highlights = highlights;
	}

	public static Document getHtmlFile() {
		File htmlFile = Ebook;
		Document doc = null;
		try {
			doc = Jsoup.parse(htmlFile, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
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
