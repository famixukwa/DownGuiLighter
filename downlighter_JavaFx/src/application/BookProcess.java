package application;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
	private Book book;
	private  Document bookHtml;
	public StringProperty messages;
	
	//Path variables:
	Path source=Paths.get(bookHtml.toString()+"/");
	String baseDirectory="test/files archive/";
	String fileName=source.getFileName().toString();
	String filenameWithNoExtension=fileName.replaceAll("\\.epub", "");
	String bookSubfolder="text";
	
	public BookProcess() {
		messages=new SimpleStringProperty(this,"Begin");
		book= new Book();
		bookHtml=book.getBookHtml();
		createHighlights(highlightSnippets);
	}
	/**
	 * creates archive folder if it doesn't exist
	 *
	 */
	public void folderCreation () {
		File theDir = new File(baseDirectory);
		//creates directory
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + theDir.getName());
			boolean result = false;

			try{
				theDir.mkdir();
				result = true;
			} 
			catch(SecurityException se){
				//handle it
			}        
			if(result) {    
				System.out.println("DIR created");  
			}
		}
	}
	/**
	 * extracts the epub file
	 *
	 */
	public void extractEpub() {
		try {
			ZipFile zipFile = new ZipFile(source.toFile());
			zipFile.extractAll(baseDirectory+filenameWithNoExtension);
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * Method that Search the searcheable text in the book text and replaces it with the same text surrounded with 
	 * an underlined tag.
	 *
	 */
	public void searchReplaceInBook() {
		for (int i = 0; i < highlightList.size(); i++) {
			String searcheable=highlightList.get(i).getSearchable();
			System.out.println(searcheable);
			
			if (bookHtml.select(searcheable).size()!=0) {
				Element found = bookHtml.select(searcheable).get(0);
				System.out.println(found.text()+"         "+found.elementSiblingIndex());
				messages.set(found.text()+"\n");
				foundParagraphs.add(found);
				textReplacer(found, i);
			} else {
				System.out.println("Hilighttext "+highlightList.get(i).getHighligghtText()+" not found");
			}

		}
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
	public void saveTheHtmlOfBook() {
		OutputHandler.saveBook(bookHtml);
	}
	public Book getBook() {
		return book;
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
