package application;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.siegmann.epublib.domain.Author;


/**
 * @author Kain
 *<h1>Class Book</h1>
 *Creates a book with highlights representation
 */

@Entity
@Table(name="eBook")  
public class EBook {
	@Id  
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int ebookId;
	//metadata
	private List<Author> author;
	//results of process
	private String numberHighlightsFound;
	

	@OneToMany(mappedBy = "eBook")
	private final List<Highlight>highlightsFound= new ArrayList<Highlight>();

	private ArrayList<File> htmlTextfiles=new ArrayList<File>();
	private String containerFolder;
	//paths

	
	public EBook() {
		 super();
	}

	
	public List<Author> getAuthor() {
		return author;
	}

	public String getNumberHighlightsFound() {
		return numberHighlightsFound;
	}


	public void setNumberHighlightsFound(String numberHighlightsFound) {
		this.numberHighlightsFound = numberHighlightsFound;
	}

	public ArrayList<File> getHtmlTextfiles() {
		return htmlTextfiles;
	}


	public void setHtmlTextFiles(File htmlTextfile) {
		htmlTextfiles.add(htmlTextfile);
	}


	public String getContainerFolder() {
		return containerFolder;
	}


	public void setContainerFolder(String containerFolder) {
		this.containerFolder = containerFolder;
	}


	public int getEbookId() {
		return ebookId;
	}


	public void setEbookId(int ebookId) {
		this.ebookId = ebookId;
	}


	public void setAuthor(List<Author> author) {
		this.author = author;
	}

	@Lob
	public void setHighlightsFound(Highlight highlight) {
		highlightsFound.add(highlight);
	}

	@Lob
	public  List<Highlight> getHighlightsFound() {
		return highlightsFound;
	}




}
