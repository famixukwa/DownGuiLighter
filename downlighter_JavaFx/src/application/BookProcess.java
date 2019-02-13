package application;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.googlecode.jatl.Html;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Creates a processing unit where the book is processed, find the highlights, change the book etc..
 *
 */

public class BookProcess extends Observable{
	private String messages2="begin";
	
	//Collections
	private ArrayList<Highlight> highlightList=new ArrayList<>();
	private List<Element> highlightSnippets;
	private ArrayList<Element> foundParagraphs=new ArrayList<>();
	 
	//links
	private Highlight highlight;
	private EBook eBook;
	public StringProperty messages=new SimpleStringProperty(this,"Begin");;

	//Path variables:
	Path source;
	String baseDirectory;
	String fileName;
	String filenameWithNoExtension;
	String bookSubfolder="text";

	//flags
	Boolean createdFolder;

	public BookProcess() {
		this.messages2=messages2;
	}
	
	public void start() {
		this.source=Paths.get(InputHandler.getEbookFile().toString()+"/");
		this.baseDirectory="test/files archive/";
		this.fileName=source.getFileName().toString();
		this.filenameWithNoExtension=fileName.replaceAll("\\.epub", "")+"/";
		eBook= new EBook();
		createHighlights();
		fileRendering();
		searchReplaceInBook(eBook);
		System.out.println(htmlListCreator());
		PopupWindowView popup= new   PopupWindowView(eBook);
	}
	
	/**
	 * renders the file system extracts the book 
	 */
	public void fileRendering() {
		folderCreation ();
		extractEpub();
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

	public void createHighlights() {
		List<Element> highlightSnippets=InputHandler.getHighlightFileSnippets();
		for (int i = 0; i < highlightSnippets.size(); i++) {
			String highligghtText=highlightSnippets.get(i).text();
			highlight=new Highlight(highligghtText);
			highlightList.add(highlight);
		}
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
	public void searchReplaceInBook(EBook eBook) {
		listExtractedFiles();
		ArrayList<File> htmlfiles=eBook.getHtmlTextfiles();
		for (File htmlFile : htmlfiles) {
			Document htmlDoc=extractDocumentFromFile(htmlFile);
			searchReplaceInHtml(htmlDoc,htmlFile,eBook);

		}

	}
	/**
	 * Method that Search the searcheable text in the html text and replaces it with the same text surrounded with 
	 * an underlined tag.
	 *
	 */
	private void searchReplaceInHtml(Document htmlDoc,File file,EBook eBook) {
		for (int i = 0; i < highlightList.size(); i++) {
			String searcheable=highlightList.get(i).getSearchable();
			//search the highlights text in the book
			if (htmlDoc.select(searcheable).size()!=0) {
				Element found = htmlDoc.select(searcheable).get(0);
				System.out.println(file.getName()+"\n");
				System.out.println(highlightList.get(i).getHighligghtText()+" found!!!"+"\n");
				//saves the file where the highlight was found
				highlightList.get(i).setContainerFile(file);
				//saves the highlights in book
				saveHighlightInEBook(highlightList.get(i));
				//produces the links
				highlightList.get(i).constructHighlightLink();
				//add message to display
				addMessagesToDisplay(highlightList.get(i).getHighligghtText()+"\n");
				
				//add found paragraph
				foundParagraphs.add(found);
				//replaces the text in the book with the bookmarked and highlighted text
				textReplacer(found, i);
				//saves the modified file
				saveTheHtmlOfBook(htmlDoc,file,eBook);
			} else {
				//	System.out.println(highlightList.get(i).getHighligghtText()+"not found!!!"+"\n");
			}

		}
	}
	/**
	 * saves the highlights in the book
	 *
	 */
	private void saveHighlightInEBook (Highlight highlight) {
		eBook.setHighlightsFound(highlight);
	}
	/**
	 * add messages to display in the gui
	 */
	private void addMessagesToDisplay(String s) {
		messages.setValue(s);
	}
	/**
	 * Helper method for searchReplaceInBook that takes the found element and the i integer for the for loop
	 *replaces the text with the text modified with highlight tags.
	 *@param found are the paragraphs that in the book HTML file includes
	 *the searched text 
	 *@param i is the counter for the searchReplaceInBook for loop.
	 */

	private void textReplacer(Element found,int i) {
		String textHighlighted=highlightList.get(i).getHighlightedText();
		String textToModify=found.html();
		String modifiedText=textToModify.replace(highlightList.get(i).getHighligghtText(), textHighlighted);
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
		for (int i = 0; i < eBook.getHighlightsFound().size(); i++) {
			html.li().raw(eBook.getHighlightsFound().get(i).getHighlightLink()).end();
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
	public String getMessages2() {
		return messages2;
	}
	public void setMessages2(String messages2) {
		this.messages2 = messages2;
		setChanged();
		notifyObservers();
	}

}
