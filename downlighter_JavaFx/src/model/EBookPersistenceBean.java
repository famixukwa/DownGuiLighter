package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import nl.siegmann.epublib.domain.Author;

public class EBookPersistenceBean extends EBook{
	private int ebookId;
	private List<Author> author;
	private StringProperty bookTitle= new SimpleStringProperty();
	private IntegerProperty numberHighlightsFound= new SimpleIntegerProperty();
	private String containerFolder;
	
	public EBookPersistenceBean(int ebookId, String containerFolder) {
		super();
		this.ebookId = ebookId;
		this.containerFolder = containerFolder;
	}

	public int getEbookId() {
		return ebookId;
	}

	public StringProperty bookTitleProperty() {
		return this.bookTitle;
	}
	

	public String getBookTitle() {
		return this.bookTitleProperty().get();
	}
	

	public void setBookTitle(final String bookTitle) {
		this.bookTitleProperty().set(bookTitle);
	}
	

	public IntegerProperty numberHighlightsFoundProperty() {
		return this.numberHighlightsFound;
	}
	

	public int getNumberHighlightsFound() {
		return this.numberHighlightsFoundProperty().get();
	}
	

	public void setNumberHighlightsFound(final int numberHighlightsFound) {
		this.numberHighlightsFoundProperty().set(numberHighlightsFound);
	}
	
	
	
}
