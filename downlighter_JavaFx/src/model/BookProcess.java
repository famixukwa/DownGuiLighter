package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.googlecode.jatl.Html;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private ArrayList<Highlight> highlightList=new ArrayList<>();
	private List<Element> highlightSnippets=InputHandler.getHighlightFileSnippets();
	private ObservableList<Highlight> highlightsFound=FXCollections.observableArrayList();

	//links
	private Highlight highlight;
	public StringProperty messages;

	//instance creation
	private EpubReader epubReader = new EpubReader();
	private EBook eBook= new EBook();
	
	int numberHighlightsFound=0;

	//Path variables:
	Path source=Paths.get(InputHandler.getEbookFile().toString()+"/");
	String baseDirectory="test/files archive/";
	String fileName=source.getFileName().toString();
	String filenameWithNoExtension=fileName.replaceAll("\\.epub", "")+"/";
	String bookSubfolder="text";

	//flags
	Boolean createdFolder;

	public BookProcess() {
		messages=new SimpleStringProperty(this,"Begin");	
	}

	/**
	 * method that starts the book process signaling the process order
	 */

	@Override
	protected Void call() throws Exception {
		createHighlights(highlightSnippets);
		extractBookTitle();
		fileRendering();
		extractAuthor();
		searchReplaceHighlights(eBook);
		eBook.setNumberHighlightsFound(numberHighlightsFound);
		addMessagesToDisplay("Number of highlights found: "+numberHighlightsFound+"\n");
		addMessagesToDisplay("Number of highlights for popup: "+highlightsFound.size()+"\n");
		System.out.println(htmlListCreator());
		SavePersistanceService task = new SavePersistanceService(eBook);
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		return null;

	}

	/**
	 * opens the book to take metadata out
	 */
	private Book openBook() {
		Book book = null;
		try {
			book = epubReader.readEpub(new FileInputStream(InputHandler.getEbookFile().toString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return book;
	}

	/**
	 * ectracts the author from the book and saves in the book
	 */
	private void extractAuthor() {
		Book book=openBook();
		List<Author> author = book.getMetadata().getAuthors();
		eBook.setAuthor(author);
	}
	/**
	 * ectracts the author from the book and saves in the book
	 */
	private void extractBookTitle() {
		Book book=openBook();
		String bookTitle = book.getMetadata().getFirstTitle();
		eBook.setBookTitle(bookTitle);
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
	 * extracts the epub file
	 *
	 */
	private void extractEpub() {
		if (createdFolder) {
			try {
				ZipFile zipFile = new ZipFile(source.toFile());
				zipFile.extractAll(baseDirectory+filenameWithNoExtension);
			} catch (ZipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	/**
	 * makes a list of the extracted files
	 */
	private void listExtractedFiles() {
		File folder = new File(eBook.getContainerFolder());

		//Implementing FilenameFilter to retrieve only html files

		FilenameFilter txtFileFilter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				if(name.endsWith(".html"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		};

		//Passing txtFileFilter to listFiles() method to retrieve only html files

		File[] files = folder.listFiles(txtFileFilter);

		for (File file : files)
		{
			eBook.setHtmlTextFiles(file);
		}
	}

	/**
	 * creates a list of highlight objects from the texts extracted from the highlights file
	 *
	 */

	public void createHighlights(List<Element> highlightSnippets) {
		addMessagesToDisplay("Begining process\n");
		for (int i = 0; i < highlightSnippets.size(); i++) {
			String highligghtText=highlightSnippets.get(i).text();
			highlight=new Highlight(highligghtText);
			highlightList.add(highlight);
		}
		addMessagesToDisplay("Highlights extracted! \n");
	}
	/**
	 * extracts the html document of the file
	 */
	private Document extractDocumentFromFile(File file) {
		File htmlFile = file;
		Document fileHtmlDocument = null;
		try {
			fileHtmlDocument = Jsoup.parse(htmlFile, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileHtmlDocument;

	}
	/**
	 * uses searchReplaceInHtml in all the book files
	 */
	public void searchReplaceHighlights(EBook eBook) {
		//sends a list of the extracted files to ebook
		listExtractedFiles();
		System.out.println(highlightList.size());
		ArrayList<File> htmlfiles=eBook.getHtmlTextfiles();
		ArrayList<Document> documentsCache=documentCache(htmlfiles);
		
		for (int i = 0; i < highlightList.size(); i++) {
			//Document htmlDoc=extractDocumentFromFile(htmlFile);
			Boolean highlightFoundBoolean=searchReplaceInHtml(eBook,highlightList.get(i),documentsCache,htmlfiles);
			if (highlightFoundBoolean=true) {
				addMessagesToDisplay("highlight "+i+" found"+"\n");
			}
			
		}
		
	}
	/**
	 * Method that makes a cache of parsed html files
	 *
	 */
	public ArrayList<Document> documentCache(ArrayList<File> htmlfiles){
		ArrayList<Document> documentFiles = new ArrayList<Document>();
		for (int i = 0; i < htmlfiles.size(); i++) {
			File file=htmlfiles.get(i);
			Document htmlDoc=extractDocumentFromFile(file);
			documentFiles.add(htmlDoc);
			System.out.println(file.getName()+"   cache");
		}
		
		return documentFiles;
		
	}
	/**
	 * Method that Search the searcheable text in the html text and replaces it with the same text surrounded with 
	 * an underlined tag.
	 *
	 */
	private boolean searchReplaceInHtml(EBook eBook,Highlight highlight, ArrayList<Document> documentsCache, ArrayList<File> htmlfiles) {
		//gets the list of files
		boolean highlightFoundBoolean=false;
		for (int i = 0; i < documentsCache.size(); i++) {
			highlightFoundBoolean=false;
			System.out.println("test 0");
			File file=htmlfiles.get(i);
			System.out.println(file.getName());
			Document htmlDoc=documentsCache.get(i);
			String searcheable=highlight.getSearchable();
			System.out.println("test 0.1");
			//search the highlights text in the book
			Elements founds = htmlDoc.select(searcheable);
			if (founds.size()!=0) {
				System.out.println("test 1");
				highlightFoundBoolean=true;
				Element found= founds.first();
				System.out.println("test 2");
				
				//informs what is found 
				informer (found, i,file, highlight);
				
				
				System.out.println("test 3");
				//counts the highlights
				numberHighlightsFound++;
				//replaces the text in the book with the bookmarked and highlighted text
				textReplacer(found, i, highlight);
				//saves the modified file
				System.out.println("test 4");
				saveTheHtmlOfBook(htmlDoc,file,eBook);
			} else {
				//add message to display
				//addMessagesToDisplay("highlight "+i+" not found"+"\n");
			}

		}
		
		//add message to display and passes the number of highlights to eBook
		return highlightFoundBoolean;

	}
	/**
	 * saves the highlights in the book
	 *
	 */
	private void saveHighlightInEBook (Highlight highlight) {
		eBook.setHighlightsFound(highlight);
		highlight.eBook=eBook;
	}
	/**
	 * add messages to display in the gui
	 */
	void addMessagesToDisplay(String s) {
		messages.set(s);
	}
	/**
	 * Helper method for searchReplaceInBook that takes the found element and the i integer for the for loop
	 *replaces the text with the text modified with highlight tags.
	 *@param found are the paragraphs that in the book HTML file includes
	 *the searched text 
	 *@param i is the counter for the searchReplaceInBook for loop.
	 */

	private void textReplacer(Element found,int i,Highlight highlight) {
		String textHighlighted=highlight.getHighlightedText();
		String textToModify=found.html();
		String modifiedText=textToModify.replace(highlight.getHighligghtText(), textHighlighted);
		found.html(modifiedText);
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
		for (int i = 0; i < getHighlightsFound().size(); i++) {
			html.li().raw(getHighlightsFound().get(i).getHighlightLink()).end();
		}
		html
		.endAll();
		String htmlList = sw.getBuffer().toString();
		return htmlList;
	}
	/**
	 * Saves the book calling the OutputHandler
	 *
	 */
	public void saveTheHtmlOfBook(Document htmlDokument, File file,EBook eBook) {
		OutputHandler outputHandler= new OutputHandler(eBook,file);
		outputHandler.saveHtml(htmlDokument);
	}
	
	/**
	 * 
	 *informs ebook and highlight of the information found on the search
	 */
	public void informer (Element found,int i,File file, Highlight highlight) {
		
		//saves the highlights in book
		saveHighlightInEBook(highlight);
		//saves the file where the highlight was found
		highlight.setContainerFile(file);
		//produces the links
		highlight.constructHighlightLink();
		//adds highlight to observable for the gui:
		highlightsFound.add(highlight);
		//saves the paragraph where the highlight was found
		highlight.setHighlightLocationInHtml(found.elementSiblingIndex());
		
	}

	//getters and setters
	public EBook getEBook() {
		return eBook;
	}

	public final StringProperty messagesProperty() {
		return this.messages;
	}

	public final String getMessages() {
		return this.messagesProperty().get();
	}

	public final void setMessages(final String messages) {
		this.messagesProperty().set(messages);
	}

	public ObservableList<Highlight> getHighlightsFound() {
		return highlightsFound;
	}



}
