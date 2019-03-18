package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jsoup.nodes.Document;

/**
 * @author Kain
 *<h1>OutputHandler</h1>
 *this class creates the files that composes the book
 *it retrieves the content of the files from Book 
 *Later on it compresses the folder and saves acopy of the modified book
 */
public class OutputHandler {
	private Path pathTorenderFolder;
	private EBook eBook;
	private File file;
	private PathHandler pathHandler;
	private String highlightIndexText;
	
	public OutputHandler(String highlightIndexText, PathHandler pathHandler) {
		this.pathTorenderFolder = pathHandler.getArchivePath();
		this.highlightIndexText=highlightIndexText;
		this.pathHandler=pathHandler;
	}
	
	public OutputHandler(EBook eBook, File file) {
		this.pathTorenderFolder = Paths.get(eBook.getContainerFolder());
		this.file=file;
		this.eBook=eBook;
	}
	/**
	 * it saves the document processed by the jsoup html parser in to the corresponding html file of the book
	 * @param htmlDokument jsoup document resulting of the search and replace functions
	 */
	public void saveHtml(Document htmlDokument) {
		Document doc = null;
		PrintWriter out=null;
			try {	
				out = new PrintWriter(file.getAbsolutePath());
				out.print(htmlDokument);
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	/**
	 * it saves an html list index with all the highlights found. This file will be later on included in the TOC of the copy of the book to be able to access the highlights from every epub compatible epub reader
	 */
	public void saveIndex() {
		PrintWriter out=null;
		File highlightIndex=new File(pathHandler.getHtmlWithHighlights().toString());
		System.out.println(highlightIndexText);
		try {
			out = new PrintWriter(highlightIndex,"UTF-8");
			out.print(highlightIndexText);
			out.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
