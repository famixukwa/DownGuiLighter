package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.googlecode.jatl.Html;
import javafx.concurrent.Task;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Creates a processing unit where the book is processed, find the highlights, change the book etc..
 *
 */

public class BookProcess extends Task<Void>{
	//Collections
	
	
	
	ArrayList<InformedFile> htmlfiles;
	
	//links
	private Highlight highlight;

	//Path variables:
	Path source=Paths.get(InputHandler.getEbookFile().toString()+"/");
	String baseDirectory="test/files archive/";
	String fileName=source.getFileName().toString();
	String filenameWithNoExtension=fileName.replaceAll("\\.epub", "")+"/";
	String bookSubfolder="text";

	//instance creation
	private EpubReader epubReader = new EpubReader();
	private EBook eBook= new EBook();
	private PathHandler pathHandler= new PathHandler(filenameWithNoExtension);

	//flags
	Boolean createdFolder;

	//search replace modus
	public BookProcess() {
			
	}

	/**
	 * method that starts the book process signaling the process order
	 */

	@Override
	protected Void call() throws Exception {
		extractBookTitle();
		ModelInterface.setProgress(0.10F);
		fileRendering();
		ModelInterface.setProgress(0.20F);
		extractMetaData();
		extractCover();
		SearchReplaceEngine searchReplaceEngine=new SearchReplaceEngine(eBook, pathHandler);
		eBook.setNumberHighlightsFound(ModelInterface.getNumberHighlightsFound());
		addMessagesToDisplay("Number of highlights found: "+ModelInterface.getNumberHighlightsFound()+"\n");
//		System.out.println(htmlListCreator());
		SavePersistanceService task = new SavePersistanceService(eBook);
		ModelInterface.setProgress(0.95F);
		saveBookInStatusObservable(eBook);
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		ModelInterface.setProgress(1F);
		return null;

	}
	/**
	 * starts the processBook in a new thread
	 */
	public void start() {
		Thread th = new Thread(this);
		th.setDaemon(false);
		th.start();
		this.setOnSucceeded( e -> {
			ModelInterface.popupWindowView(eBook);
		});
	}

	/**
	 * opens the book to take metadata out
	 */
	private Book openBook() {
		Book book = null;
		try {
			book = epubReader.readEpub(new FileInputStream(InputHandler.getEbookFile().toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return book;
	}

	/**
	 * ectracts the author from the book and saves in the book
	 */
	private void extractMetaData() {
		Book book=openBook();
		List<Author> authorList = book.getMetadata().getAuthors();
		String bookTitle = book.getMetadata().getFirstTitle();
		List<String> publisherList = book.getMetadata().getPublishers();
		List<String> descriptionList = book.getMetadata().getDescriptions();
		for (String string : descriptionList) {
		}
		eBook.setAuthor(umwrapList(authorList));
		ModelInterface.setAuthor(umwrapList(authorList));

		eBook.setBookTitleP(bookTitle);
		ModelInterface.setBookTitleP(bookTitle);

		eBook.setPublisher(umwrapList(publisherList));
		ModelInterface.setPublisher(umwrapList(publisherList));

		eBook.setDescription(umwrapList(descriptionList));
		ModelInterface.setDescription(umwrapList(descriptionList));
	}
	private String umwrapList(List list) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
		}
		for (int i = 0; i < list.size(); i++) {
			s.append(list.get(i).toString());
			if (i<list.size()-1 || i!=0) {
				s.append(",");
			}
		}
		return s.toString();	
	}

	/**
	 * Extracts the title from the book and saves in the book
	 */
	private void extractBookTitle() {
		Book book=openBook();
		String bookTitle = book.getMetadata().getFirstTitle();
		eBook.setBookTitleP(bookTitle);
	}
	/**
	 * Extracts the cover from the book and saves in the book
	 */
	private void extractCover() {
		XmlExtractor xmlExtractor=new XmlExtractor(pathHandler.getOpfPath().toString());
		Path relativePathToCover=xmlExtractor.getAttributePath("manifest", "id","cover");
		Path pathToCover=Paths.get(pathHandler.getBookPath().toString(),relativePathToCover.toString());
		eBook.setCoverPath(pathToCover.toString());
		ModelInterface.setCoverPath(pathToCover.toString());
	}
	/**
	 * renders the file system extracts the book 
	 */
	public void fileRendering() {
		folderCreation ();
		extractEpub();
		//add message to display
		addMessagesToDisplay("Book extracted"+"\n");
	}
	/**
	 * creates archive folder if it doesn't exist and saves the folder where the files are in the book
	 *
	 */
	private void folderCreation () {
		File theDir = new File(baseDirectory);

		//creates directory
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			boolean result = false;
			try{
				theDir.mkdir();
				result = true;
			} 
			catch(SecurityException se){
				System.out.println("there was a problem creating the folder");

			}        
			if(result) {    
				System.out.println("DIR created"); 
				createdFolder=true;
				//saves the files container folder in the book
				eBook.setContainerFolder(baseDirectory+filenameWithNoExtension+bookSubfolder);
			}
		}
		else {
			createdFolder=true;
			eBook.setContainerFolder(baseDirectory+filenameWithNoExtension+bookSubfolder);
		}

	}
	/**
	 * extracts the epub file and sets the path of content.opf that informs pathHandler where is the root directory
	 *
	 */
	private void extractEpub() {
		if (createdFolder) {
			try {
				ZipFile zipFile = new ZipFile(source.toFile());
				zipFile.extractAll(baseDirectory+filenameWithNoExtension);
				pathHandler.setOpfPath();
			} catch (ZipException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * method that creates an html link list to be used in the book as a way to see where the highlight was 
	 */
	private String htmlListCreator() {
		StringWriter sw = new StringWriter();
		Html html = new Html(sw);
		html
		.html()
		.head()
		.meta().httpEquiv("content-type").content("application/xhtml+xml; charset=UTF-8")
		.title().text("asd").end().end(1)
		.body()
		.ul().style("text-decoration:none;list-style:square;font-weight:bold");
		for (int i = 0; i < ModelInterface.getHighlightsFound().size(); i++) {
			html.li().raw(ModelInterface.getHighlightsFound().get(i).getHighlightLink()).end();
		}
		html
		.endAll();
		String htmlList = sw.getBuffer().toString();
		return htmlList;
	}
	/**
	 * add book in Observable list of model connector
	 */
	public void saveBookInStatusObservable (EBook eBook) {
		ModelInterface.addBookToObservable(eBook);
	}
	/**
	 * add messages to display in the gui
	 */
	void addMessagesToDisplay(String s) {
		ModelInterface.setMessages(s);
	}



}
