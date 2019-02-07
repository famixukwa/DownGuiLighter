package application;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;


/**
 * @author Kain
 *<h1>Class Book</h1>
 *Creates a book with highlights representation
 */


public class EBook {
	//metadata
	private List<Author> author;
	//results of process
	private String numberHighlightsFound;
	private ArrayList<Highlight> highlightsFound;
	private ArrayList<File> htmlTextfiles;
	private String containerFolder;
	//paths
	private Path ebookFilePath;
	//links

	private EpubReader epubReader = new EpubReader();

	public EBook() {
		ebookFilePath= Paths.get(InputHandler.getEbookFile().getPath());
		setAuthor();
	}


	public List<Author> getAuthor() {
		return author;
	}

	private Book openBook() {
		Book book = null;
		try {
			book = epubReader.readEpub(new FileInputStream(ebookFilePath.toString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return book;
	}
	private void setAuthor() {
		Book book=openBook();
		List<Author> author = book.getMetadata().getAuthors();
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
