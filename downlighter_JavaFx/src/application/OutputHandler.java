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
	public OutputHandler() {
		this.pathTorenderFolder = Paths.get(eBook.getContainerFolder());
	}
	public void saveHtml(Document bookHtml) {
		Document doc = null;
		PrintWriter out=null;
		try {
			File directory=new File("epubs");
			directory.mkdir();
			out = new PrintWriter("epubs/test.html");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		out.print(bookHtml);
	}
}
