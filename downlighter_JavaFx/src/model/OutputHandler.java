package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
	public void saveHtml(Document htmlDokument) {
		Document doc = null;
		PrintWriter out=null;
		File directory=new File("epubs");
		if (!directory.exists()) {
			try {	
				directory.mkdir();
				out = new PrintWriter(file.getAbsolutePath());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.print(htmlDokument);
		}
		else {
			try {	
				out = new PrintWriter(file.getAbsolutePath());
				out.print(htmlDokument);
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	public void saveIndex() {
		PrintWriter out=null;
		File highlightIndex=new File(pathHandler.getHtmlWithHighlights().toString());
		System.out.println(highlightIndexText);
		try {
			out = new PrintWriter(highlightIndex);
			out.print(highlightIndexText);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
