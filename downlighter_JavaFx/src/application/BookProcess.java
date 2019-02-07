package application;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Creates a processing unit where the book is processed, find the highlights, change the book etc..
 *
 */

public class BookProcess {
	//Collections
	private ArrayList<Highlight> highlightList=new ArrayList<>();
	private List<Element> highlightSnippets=InputHandler.getHighlightFileSnippets();
	private ArrayList<Element> foundParagraphs=new ArrayList<>();

	//links
	private Highlight highlight;
	private EBook eBook;
	
	public StringProperty messages;

	//Path variables:
	Path source=Paths.get(eBook.getContainerFolder().toString()+"/");
	String baseDirectory="test/files archive/";
	String fileName=source.getFileName().toString();
	String filenameWithNoExtension=fileName.replaceAll("\\.epub", "")+"/";
	String bookSubfolder="text";
	
	//flags
	Boolean createdFolder;

	public BookProcess() {
		messages=new SimpleStringProperty(this,"Begin");
		eBook= new EBook();
		createHighlights(highlightSnippets);
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
	public void folderCreation () {
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
		
	}
	/**
	 * extracts the epub file
	 *
	 */
	public void extractEpub() {
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
	public void listExtractedFiles() {
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
			System.out.println(file.getName());
			eBook.setHtmlTextFiles(file);
		}
	}
	
	/**
	 * creates a list of highlight objects from the texts extracted from the highlights file
	 *
	 */

	public void createHighlights(List<Element> highlightSnippets) {
		for (int i = 0; i < highlightSnippets.size(); i++) {
			String highligghtText=highlightSnippets.get(i).text();
			Highlight highlight=new Highlight(highligghtText);
			highlightList.add(highlight);
		}
	}
	/**
	 * extracts the html document of the file
	 */
	public Document extractDocumentFromFile(File file) {
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
	public void searchReplaceInBook() {
		listExtractedFiles();
		ArrayList<File> htmlfiles=eBook.getHtmlTextfiles();
		for (File htmlFile : htmlfiles) {
			Document htmlDoc=extractDocumentFromFile(htmlFile);
			searchReplaceInHtml(htmlDoc);
			
//			Document htmlDoc = null;
//			try {
//				htmlDoc = Jsoup.parse(htmlFile, "UTF-8");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}
		
	}
	/**
	 * Method that Search the searcheable text in the html text and replaces it with the same text surrounded with 
	 * an underlined tag.
	 *
	 */
	public void searchReplaceInHtml(Document htmlDoc) {
		for (int i = 0; i < highlightList.size(); i++) {
			String searcheable=highlightList.get(i).getSearchable();
			//search the highlights text in the book
			if (htmlDoc.select(searcheable).size()!=0) {
				Element found = htmlDoc.select(searcheable).get(0);
				System.out.println(found.text()+"         "+found.elementSiblingIndex());
			//saves the highlights in book
				saveHighlightInEBook(highlightList.get(i));
			//add message to display
				addMessagesToDisplay(highlightList.get(i).getHighligghtText()+"\n");
			//add found paragraph
				foundParagraphs.add(found);
			//replaces the text in the book with the bookmarked and highlighted text
				textReplacer(found, i);
			//saves the modified file
				saveTheHtmlOfBook(htmlDoc);
			} else {
				System.out.println("Hilighttext "+highlightList.get(i).getHighligghtText()+" not found");
			}

		}
	}
	/**
	 * saves the highlights in the book
	 *
	*/
	public void saveHighlightInEBook (Highlight highlight) {
		eBook.setHighlightsFound(highlight);
	}
	/**
	 * add messages to display in the gui
	*/
	public void addMessagesToDisplay(String s) {
		messages.set(s);
	}
	/**
	 * Helper method for searchReplaceInBook that takes the found element and the i integer for the for loop
	 *replaces the text with the text modified with highlight tags.
	 *@param found are the paragraphs that in the book HTML file includes
	 *the searched text 
	 *@param i is the counter for the searchReplaceInBook for loop.
	 */

	public void textReplacer(Element found,int i) {
		String textHighlighted=highlightList.get(i).getHighlightedText();
		String textToModify=found.html();
		String modifiedText=textToModify.replace(highlightList.get(i).getHighligghtText(), textHighlighted);
		found.html(modifiedText);
	}
	/**
	 * Saves the book calling the OutputHandler
	 *
	 */
	public void saveTheHtmlOfBook(Document htmlDokument) {
		OutputHandler outputHandler= new OutputHandler();
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

}
