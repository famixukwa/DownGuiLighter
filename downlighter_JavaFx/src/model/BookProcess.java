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

import org.apache.derby.tools.sysinfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.googlecode.jatl.Html;

import controllers.PopupWindowController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.RetrievePersistanceService.Mode;
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
	ArrayList<InformedFile> htmlfiles;
	/**
	 * metadata properties
	 */
	private static StringProperty coverPath= new SimpleStringProperty();


	//links
	private Highlight highlight;
	private StringProperty messages;
	private Sentence sentence;

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

	int numberHighlightsFound=0;

	//flags
	Boolean createdFolder;

	//search replace modus
	public enum Mode {HIGHLIGHT,SENTENCE};
	public Mode selectedMode;
	public BookProcess() {
		messages=new SimpleStringProperty(this,"Begin");	
	}

	/**
	 * method that starts the book process signaling the process order
	 */

	@Override
	protected Void call() throws Exception {
		createHighlights(highlightSnippets);
		ModelInterface.setProgress(0.10F);
		extractBookTitle();
		fileRendering();
		ModelInterface.setProgress(0.20F);
		extractMetaData();
		extractCover();
		searchReplaceHighlights(eBook);
		eBook.setNumberHighlightsFound(numberHighlightsFound);
		addMessagesToDisplay("Number of highlights found: "+numberHighlightsFound+"\n");
		System.out.println(htmlListCreator());
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
			ModelInterface.popupWindowView(this.getHighlightsFound(),eBook);
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
			if (i<list.size() || i!=0) {
				s.append(",");
			}
		}
		return s.toString();	
	}

	/**
	 * ectracts the title from the book and saves in the book
	 */
	private void extractBookTitle() {
		Book book=openBook();
		String bookTitle = book.getMetadata().getFirstTitle();
		eBook.setBookTitleP(bookTitle);
	}
	/**
	 * ectracts the cover from the book and saves in the book
	 */
	private void extractCover() {
		XmlExtractor xmlExtractor=new XmlExtractor(pathHandler.getOpfPath().toString());
		Path relativePathToCover=xmlExtractor.getAttributePath("manifest", "id","cover");
		Path pathToCover=Paths.get(pathHandler.getBookPath().toString(),relativePathToCover.toString());
		eBook.setCoverPath(pathToCover.toString());
		coverPath.set(pathToCover.toString());
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
	 * makes a list of the extracted files
	 */
	private void listExtractedFiles() {
		ArrayList<InformedFile> htmlTextfiles= new ArrayList<InformedFile>();
		XmlExtractor xmlExtractor= new XmlExtractor(pathHandler.getOpfPath().toString());
		ArrayList<Path> filesPath=xmlExtractor.getHmlFilesPath();
		for (int i = 0; i < filesPath.size(); i++) {
			Path fullPath=Paths.get(pathHandler.getBookPath().toString(), filesPath.get(i).toString());
			InformedFile file= new InformedFile(fullPath.toString());
			file.setOrderIndex(i);
			htmlTextfiles.add(file);
		}
		eBook.setHtmlTextfiles(htmlTextfiles);
	}

	/**
	 * creates a list of highlight objects from the texts extracted from the highlights file
	 *
	 */

	public void createHighlights(List<Element> highlightSnippets) {
		addMessagesToDisplay("Begining process\n");
		for (int i = 0; i < highlightSnippets.size(); i++) {
			String highligghtText=highlightSnippets.get(i).text();
			System.out.println("number of highlights: "+highlightSnippets.size());
			highlight=new Highlight(highligghtText);
			highlightList.add(highlight);
		}
		addMessagesToDisplay("Highlights extracted! \n");
	}
	/**
	 * extracts the html document of the file
	 */
	private Document extractDocumentFromFile(InformedFile file) {
		InformedFile htmlFile = file;
		Document fileHtmlDocument = null;
		try {
			fileHtmlDocument = Jsoup.parse(htmlFile, "UTF-8");
		} catch (IOException e) {
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
		htmlfiles=eBook.getHtmlTextfiles();

		for (int i = 0; i < highlightList.size(); i++) {
			//set progress
			Double d =0.7/highlightList.size();
			ModelInterface.setProgress(i*d+0.2);
			//Document htmlDoc=extractDocumentFromFile(htmlFile);
			Boolean highlightFoundBoolean=searchReplaceInHtml(eBook,highlightList.get(i));
			if (highlightFoundBoolean) {
				addMessagesToDisplay("highlight "+i+" found"+"\n");
			}
			else {
				addMessagesToDisplay("highlight "+i+" not found!! :("+"\n");

			}
		}

	}

	/**
	 * Method that Search the searcheable text in the html text and replaces it with the same text surrounded with 
	 * an underlined tag.
	 *
	 */
	private boolean searchReplaceInHtml(EBook eBook,Highlight highlight) {
		//gets the list of files
		boolean highlightFoundBoolean=false;

		for (int i = 0; i < htmlfiles.size(); i++) {
			InformedFile file=htmlfiles.get(i);
			Document htmlDoc=extractDocumentFromFile(file);
			String searcheable=highlight.getSearchable();

			//search the highlights text in the book
			Elements founds = htmlDoc.select(searcheable);
			if (founds.size()!=0) {

				highlightFoundBoolean=true;
				Element found= founds.first();
				//informs what is found 
				informer (found, i,file, highlight,htmlfiles);
				//counts the highlights
				numberHighlightsFound++;
				//saved the highlight as past highlight for optimization purposes
				ModelInterface.setPastHighlight(highlight);
				//replaces the text in the book with the bookmarked and highlighted text
				textReplacer(found, i, highlight);
				//saves the modified file
				saveTheHtmlOfBook(htmlDoc,file,eBook);
				//	optimizer(highlight,htmlfiles);
			}
		}
		if (!highlightFoundBoolean&highlight.getSentences().size()>0) {
			System.out.println("test sentence begin");
			SentenceHighlight sentenceHighlight= new SentenceHighlight(highlight.getHighligghtText());
			sentenceSearchReplacer(eBook,sentenceHighlight,highlightFoundBoolean);
		}
		if (!highlightFoundBoolean) {
			System.out.println("Highlight: "+highlight.getCleanHilightText()+"not found"+"\n");
		}
		return highlightFoundBoolean;
	}
	/**
	 * Helper method for searchReplaceInHtml that searches in sentence mode for the case the highlight contains sentences from two different paragraphs
	 * 
	 */
	private void sentenceSearchReplacer(EBook eBook,SentenceHighlight sentenceHighlight, Boolean highlightFoundBoolean ) {

		htmlfiles=eBook.getHtmlTextfiles();
		System.out.println("entering sentence mode");
		if (sentenceHighlight.getSentences().size()>1) {
			for (int i = 0; i < sentenceHighlight.getSentences().size(); i++) {
				System.out.println("sentenceLoop "+i+"   ---"+sentenceHighlight.getSentences().get(i).getCleanHilightText());
				highlightFoundBoolean=sentenceSearchReplaceInHtml(eBook,sentenceHighlight,sentenceHighlight.getSentences().get(i), i);
			}
		}
		else {
			highlightFoundBoolean=null;
		}

	}
	/**
	 * Helper method for sentenSearchReplacer that searches in sentence mode on the files for the case the highlight contains sentences from two different paragraphs
	 * 
	 */
	private boolean sentenceSearchReplaceInHtml(EBook eBook,SentenceHighlight sentenceHighlight, Sentence sentence,int i) {
		//gets the list of files
		boolean highlightFoundBoolean=false;
		ArrayList<InformedFile> copyOfHtmlFiles=new ArrayList<InformedFile>(htmlfiles);
		for (int j = 0; j < copyOfHtmlFiles.size(); j++) {
			InformedFile file=copyOfHtmlFiles.get(j);
			Document htmlDoc=extractDocumentFromFile(file);
			String searcheable=sentence.getSearchable();
			//search the highlights text in the book
			Elements founds = htmlDoc.select(searcheable);
			if (founds.size()!=0) {
				System.out.println("sentence: "+sentence.getCleanHilightText()+"   Found!"+"\n");
				highlightFoundBoolean=true;
				Element found= founds.first();
				if (i<1) {

					//informs what is found 
					informer (found, j,file, sentenceHighlight,copyOfHtmlFiles);
					//counts the highlights
					numberHighlightsFound++;
					highlightFoundBoolean=true;
					sentenceOptimizer(sentenceHighlight, copyOfHtmlFiles);
				}
				//replaces the text in the book with the bookmarked and highlighted text
				textReplacer(found, j, sentence);
				//saves the modified file
				saveTheHtmlOfBook(htmlDoc,file,eBook);
			}
		}
		if (!highlightFoundBoolean) {
			System.out.println("sentence: "+highlight.getCleanHilightText()+"not found"+"\n");
		}
		//add message to display and passes the number of highlights to eBook

		return highlightFoundBoolean;
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
	 * 
	 *Optimizes the search algorithm deleting the files on the list that have been already explored
	 */
	private void optimizer(Highlight highlight,ArrayList<InformedFile> htmlfiles) {
		if (ModelInterface.getPastHighlight()!=null) {
			int value=highlight.getHighlightFileIndex()-ModelInterface.getPastHighlight().getHighlightFileIndex();
			if (value>1) {
				int readFiles=highlight.getHighlightFileIndex()-1;
				for (int j = 0; j < readFiles; j++) {
					htmlfiles.remove(j);
				}
			}
		}
	}
	/**
	 * 
	 *Optimizes the search algorithm deleting the files on the list that have been already explored in the case of sentence search
	 */
	private void sentenceOptimizer(Highlight highlight,ArrayList<InformedFile> htmlfiles) {
		int value=highlight.getHighlightFileIndex();
		if (value>1) {
			int readFiles=highlight.getHighlightFileIndex()-1;
			for (int j = 0; j < readFiles; j++) {
				htmlfiles.remove(j);
			}
		}
	}
	/**
	 * 
	 *informs ebook and highlight of the information found on the search
	 */
	public void informer (Element found,int i,InformedFile file, Highlight highlight,  ArrayList<InformedFile> htmlfiles) {
		//saves the file where the highlight was found
		highlight.setContainerFile(file);
		//produces the links
		highlight.constructHighlightLink();
		//adds highlight to observable for the gui:
		highlightsFound.add(highlight);
		//saves the paragraph where the highlight was found
		highlight.setHighlightLocationInHtml(found.elementSiblingIndex());
		//saves the index file where it was found
		highlight.setHighlightFileIndex(htmlfiles.get(i).getOrderIndex());
		//saves the highlights in book
		saveHighlightInEBook(highlight);

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



	public void saveBookInStatusObservable (EBook eBook) {
		ModelInterface.addBookToObservable(eBook);
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

	public StringProperty coverPathProperty() {
		return this.coverPath;
	}


	public String getCoverPath() {
		return this.coverPathProperty().get();
	}


	public void setCoverPath(final String coverPath) {
		this.coverPathProperty().set(coverPath);
	}





}
