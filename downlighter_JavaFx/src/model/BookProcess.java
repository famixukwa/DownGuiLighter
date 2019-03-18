package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.googlecode.jatl.Html;
import javafx.concurrent.Task;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;

/**
 * Creates a processing unit where the book is processed, find the highlights, change the book etc..
 *
 */

public class BookProcess extends Task<Void>{
	//Collections



	ArrayList<InformedFile> htmlfiles;

	//links
	private Highlight highlight;

	//instance creation
	private EpubReader epubReader = new EpubReader();
	private EBook eBook= new EBook();
	private PathHandler pathHandler= new PathHandler();

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
		ModelConnector.setProgress(0.10F);
		fileRendering();
		ModelConnector.setProgress(0.20F);
		extractMetaData();
		extractCover();
		SearchReplaceEngine searchReplaceEngine=new SearchReplaceEngine(eBook, pathHandler);
		eBook.setNumberHighlightsFound(ModelConnector.getNumberHighlightsFound());
		addMessagesToDisplay("Number of highlights found: "+ModelConnector.getNumberHighlightsFound()+"\n");
		htmlListCreator();
		SavePersistanceService task = new SavePersistanceService(eBook);
		bookCompress();
		addHighlightSection();
		ModelConnector.setProgress(0.95F);
		saveBookInStatusObservable(eBook);
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		ModelConnector.setProgress(1F);
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
			ModelConnector.popupWindowView(eBook);
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
		eBook.setAuthor(umwrapList(authorList));
		ModelConnector.setAuthor(umwrapList(authorList));

		eBook.setBookTitleP(bookTitle);
		ModelConnector.setBookTitleP(bookTitle);

		eBook.setPublisher(umwrapList(publisherList));
		ModelConnector.setPublisher(umwrapList(publisherList));

		eBook.setDescription(umwrapList(descriptionList));
		ModelConnector.setDescription(umwrapList(descriptionList));
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
		Path pathToCover=Paths.get(pathHandler.getExtractedBook().toString(),relativePathToCover.toString());
		eBook.setCoverPath(pathToCover.toString());
		ModelConnector.setCoverPath(pathToCover.toString());
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
		ArrayList<Path>folderArray=pathHandler.getFolderArray();
		for (Path path : folderArray) {
			System.out.println(path.toString());
			File directory = new File(path.toString());
			//creates directory
			// if the directory does not exist, create it
			if (!directory.exists()) {
				System.out.println("creating directory: " + path.toString());
				boolean result = false;
				try{
					directory.mkdir();
					result = true;
				} 
				catch(SecurityException se){
					System.out.println("there was a problem creating the file");
				}        
				if(result) {    
					System.out.println("DIR created"); 
					createdFolder=true;
					//saves the files container folder in the book
					eBook.setContainerFolder(pathHandler.getBookPath().toString());
				}
			}
			else {
				createdFolder=true;
				eBook.setContainerFolder(pathHandler.getBookPath().toString());
			}
		}
	}
	/**
	 * extracts the epub file and sets the path of content.opf that informs pathHandler where is the root directory
	 *
	 */
	private void extractEpub() {
		if (createdFolder) {
			try {
				ZipFile zipFile = new ZipFile(pathHandler.getPathToEpub().toFile());
				zipFile.extractAll(pathHandler.getExtractedBook().toString());
				pathHandler.setOpfPath();
			} catch (ZipException e) {
				e.printStackTrace();
			}
		}
		pathHandler.setOpfPath();
		pathHandler.setPathOfhtmlfiles();
		pathHandler.setHtmlWithHighlights();
	}

	/**
	 * method that creates an html link list to be used in the book as a way to see where the highlight was 
	 */
	private void htmlListCreator() {
		StringWriter sw = new StringWriter();
		Html html = new Html(sw);
		html
		.html()
		.head()
		.meta().httpEquiv("content-type").content("application/xhtml+xml; charset=UTF-8")
		.title().text("Highlight index").end().end(1)
		.body()
		.h1().text("Highllight Index").end()
		.ul().style("text-decoration:none;list-style:square;font-weight:normal");
		for (int i = 0; i < ModelConnector.getHighlightsFound().size(); i++) {
			html.li().style("text-decoration:none").raw(ModelConnector.getHighlightsFound().get(i).getBookHighlightLink()).end();
		}
		html
		.endAll();
		String htmlList = sw.getBuffer().toString();
		OutputHandler saveIndex= new OutputHandler(htmlList, pathHandler);
		saveIndex.saveIndex();
	}
	/**
	 * compresses the analyzed files on to a new book that could by used by any ebook reader accepting epub files.
	 */
	private void bookCompress( ) {
		Path extractedBook=pathHandler.getExtractedBook();
		Path compressedBook=Paths.get(pathHandler.getEpubFiles().toString(), pathHandler.getFilenameWithNoExtension()+".epub");
		// Initiate ZipFile object with the path/name of the zip file.
		ZipFile zipFile=null;
		try {
			zipFile = new ZipFile(compressedBook.toString());

			// Folder to add

			String folderToAdd = extractedBook.toString();
			System.out.println("folder to compress: "+folderToAdd);
			// Initiate Zip Parameters which define various properties such
			// as compression method, etc.
			ZipParameters parameters = new ZipParameters();
			parameters.setIncludeRootFolder(false);
			// set compression method to store compression
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			// Set the compression level
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			// Add folder to the zip file
			zipFile.addFolder(folderToAdd, parameters);

		} catch (Exception e) {
			e.printStackTrace();
		}
		pathHandler.setIsepubfileWHighlightsCreated(true);
	}
	/**
	 * adds the section depicting all the highlihgts found for later use in any other book reader to the new epub file created.
	 */
	private void addHighlightSection() {
		EpubReader epubReader = new EpubReader();
		EpubWriter epubWriter = new EpubWriter();
		Path copyOfBookPath=Paths.get(pathHandler.getEpubFiles().toString(), pathHandler.getFilenameWithNoExtension()+".epub");
		Path copyOfBookPathWHigh=Paths.get(pathHandler.getEpubFiles().toString(), pathHandler.getFilenameWithNoExtension()+"highlighted"+".epub");
		Path localPathtoList=Paths.get(pathHandler.getPathOfhtmlfiles().toString(), "highlight_index.html");
		Path pathToHtmlList=Paths.get(pathHandler.getHtmlWithHighlights().toString());
		try {
			Book bookCopyForLaterUse = epubReader.readEpub(new FileInputStream(copyOfBookPath.toString()));
			bookCopyForLaterUse.addSection("Highliht index", new Resource(new FileInputStream(pathToHtmlList.toString()),localPathtoList.toString()));
			epubWriter.write(bookCopyForLaterUse, new FileOutputStream(copyOfBookPathWHigh.toString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pathHandler.setEpubfileWHighlights(copyOfBookPathWHigh);
	}
	/**
	 * add book in Observable list of model connector
	 */
	public void saveBookInStatusObservable (EBook eBook) {
		ModelConnector.addBookToObservable(eBook);
	}
	/**
	 * add messages to display in the gui
	 */
	void addMessagesToDisplay(String s) {
		ModelConnector.setMessages(s);
	}

	public PathHandler getPathHandler() {
		return pathHandler;
	}



}
