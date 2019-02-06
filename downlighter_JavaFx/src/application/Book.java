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
	public String author;
	public String highlightsFound;
	public Book() {
		
		this.bookHtml = bookHtml;	
	}

	
	public Document getBookHtml() {
		return bookHtml;
	}
	
	
}
