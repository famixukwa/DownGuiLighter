package application;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * @author Kain
 *<h1>Class Book</h1>
 *Creates a book with highlights representation
 */


public class Book {
	private String author;
	private String numberHighlightsFound;
	private ArrayList<Highlight> highlightsFound;
	private ArrayList<File> htmlTextfiles;
	private String containerFolder;
	private EpubReader epubReader = new EpubReader();

	public Book() {
			
	}
	
	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getNumberHighlightsFound() {
		return numberHighlightsFound;
	}


	public void setNumberHighlightsFound(String numberHighlightsFound) {
		this.numberHighlightsFound = numberHighlightsFound;
	}


	public ArrayList<Highlight> getHighlightsFound() {
		return highlightsFound;
	}


	public void setHighlightsFound(Highlight highlight) {
		highlightsFound.add(highlight);
	}


	public ArrayList<File> getHtmlTextfiles() {
		return htmlTextfiles;
	}


	public void setHtmlTextFiles(File htmlTextfile) {
		htmlTextfiles.add(htmlTextfile);
	}


	public String getContainerFolder() {
		return containerFolder;
	}


	public void setContainerFolder(String containerFolder) {
		this.containerFolder = containerFolder;
	}








}
