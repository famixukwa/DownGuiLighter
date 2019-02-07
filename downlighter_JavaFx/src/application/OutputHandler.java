package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

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
				out = new PrintWriter(eBook.getContainerFolder()+file.getName());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.print(htmlDokument);
		}
		else {
			try {	
				out = new PrintWriter(eBook.getContainerFolder()+"/"+file.getName());
				out.print(htmlDokument);
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
