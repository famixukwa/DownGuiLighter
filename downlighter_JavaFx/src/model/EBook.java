package model;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * @author Kain
 *<h1>Class Book</h1>
 *Creates a book with highlights representation
 */

@Entity
@Access(AccessType.FIELD)
@Table(name="eBook")  
public class EBook {
	@Id  
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int ebookId;
	//metadata
	@Transient
	private final StringProperty coverPath= new SimpleStringProperty();
	@Transient
	private final StringProperty bookTitleP= new SimpleStringProperty();
	@Transient
	private final StringProperty author= new SimpleStringProperty();
	@Transient
	private  final StringProperty description= new SimpleStringProperty();
	@Transient
	private  final StringProperty publisher= new SimpleStringProperty();
	
	//results of process
	private int numberHighlightsFound;

	@OneToMany(mappedBy = "eBook")
	private final List<Highlight>highlightsFound= new ArrayList<Highlight>();
	private String containerFolder;
	//paths

	
	public EBook() {
		 super();
	}

	
	

	public int getNumberHighlightsFound() {
		return numberHighlightsFound;
	}


	public void setNumberHighlightsFound(int numberHighlightsFound) {
		this.numberHighlightsFound = numberHighlightsFound;
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


	 
	public void setHighlightsFound(Highlight highlight) {
		highlightsFound.add(highlight);
	}

	 
	public  List<Highlight> getHighlightsFound() {
		return highlightsFound;
	}


	public StringProperty coverPathProperty() {
		return this.coverPath;
	}
	

	@Access(AccessType.PROPERTY)
	public String getCoverPath() {
		return this.coverPathProperty().get();
	}
	


	public void setCoverPath(final String coverPath) {
		this.coverPathProperty().set(coverPath);
	}
	


	public StringProperty bookTitlePProperty() {
		return this.bookTitleP;
	}
	

	@Access(AccessType.PROPERTY)
	public String getBookTitleP() {
		return this.bookTitlePProperty().get();
	}
	


	public void setBookTitleP(final String bookTitleP) {
		this.bookTitlePProperty().set(bookTitleP);
	}
	


	public StringProperty descriptionProperty() {
		return this.description;
	}
	
	@Lob 
	@Column(length=2000)
	@Access(AccessType.PROPERTY)
	public String getDescription() {
		return this.descriptionProperty().get();
	}
	


	public void setDescription(final String description) {
		this.descriptionProperty().set(description);
	}
	


	public StringProperty publisherProperty() {
		return this.publisher;
	}
	

	@Access(AccessType.PROPERTY)
	public String getPublisher() {
		return this.publisherProperty().get();
	}
	


	public void setPublisher(final String publisher) {
		this.publisherProperty().set(publisher);
	}




	public StringProperty authorProperty() {
		return this.author;
	}
	



	@Access(AccessType.PROPERTY)
	public String getAuthor() {
		return this.authorProperty().get();
	}
	




	public void setAuthor(final String author) {
		this.authorProperty().set(author);
	}
	


}
