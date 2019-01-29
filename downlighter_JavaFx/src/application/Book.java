package application;


import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Kain
 *<h1>Class Book</h1>
 *Creates a book with highlights representation
 */


public class Book {
	private Document bookHtml=InputHandler.getHtmlFile();
	private ArrayList<Highlight> highlightList=new ArrayList<>();
	private List<Element> highlightSnippets=InputHandler.getHighlightFileSnippets();
	private ArrayList<Element> foundParagraphs=new ArrayList<>();
	private Highlight highlight;
	
	public Book() {
		createHighlights(highlightSnippets);
		this.bookHtml = bookHtml;
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
			
			if (this.bookHtml.select(searcheable).size()!=0) {
				Element found = this.bookHtml.select(searcheable).get(0);
				System.out.println(found.text());
				
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
	
	public void textReplacer(Element found,Integer i) {
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
		OutputHandler.saveBook(this.bookHtml);
	}
	
	
	
	
}
