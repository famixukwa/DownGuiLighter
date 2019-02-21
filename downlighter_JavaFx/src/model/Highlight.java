package model;

import java.io.File;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;


/**
 * @author Kain
 *
 *This class creates a highlight object which has:
 *a search text that could be search in the book using regex
 *the same text surrounded with highlight tags to replace the original text
 */
@Entity
public class Highlight {
	@Id  
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int highlightId;
	@ManyToOne
	public EBook eBook;
	@Lob 
	@Column(name="CONTENT", length=2000)
	private  String highligghtText;
	@Lob 
	@Column(length=2000)
	private String cleanHilightText;
	@Transient
	private String highlightDomSelector1="p:matches(.*";
	@Transient
	private String highlightDomSelector2=".*)";
	@Transient
	private String beginTagHighlight1="<span id=\"";
	@Transient
	private String beginTagHighlight2= "\" style=\"background-color: #FFFF00\">";
	@Transient
	private String endTagHighlight="</span>";
	@Transient
	private String beginLinkTag="<a href=\"";
	@Transient
	private String beginLinkTag2="\">";
	@Transient
	private String endLinkTag="</a>";
	@Lob 
	@Column(length=2000)
	private String searchable;
	@Lob 
	@Column(length=2000)
	private String highlightedText;
	@Lob 
	@Column( length=2000)
	private String highlightLink;
	@Lob 
	@Column(length=2000)
	private String highlightUrl;

	int hashCode;
	@Lob
	File containerFile;
	
	
	public Highlight() {
		 super();
	}
	
	public Highlight(String highligghtText) {
		this.highligghtText = highligghtText;
		hashCode= Math.abs(highligghtText.hashCode());
		cleanHilightText=cleanEspecialCharacters(highligghtText);
		searchable= createSearchable(cleanHilightText);
		highlightedText=constructHIghlightedText(highligghtText);
	}
	/**
	 * Method that cleans text from special characters
	 * @param highligghtText highlight text to be cleaned
	 */
	@Transient
	private String cleanEspecialCharacters(String highligghtText) {
		return highligghtText.replace(".", ".");
	}
	/**
	 * Adds the select code from Jsoup and the regex to the highlight text
	 * this makes possible to realize the search with jsoup.
	 */
	@Transient
	private String createSearchable(String highligghtText) {
		String searchable=highlightDomSelector1+highligghtText+highlightDomSelector2;
		return searchable;
	}
	/**
	 * wraps the text with HTML tags to makes the replacement
	 */
	@Transient
	private String constructHIghlightedText(String cleanHilightText) {
		return beginTagHighlight1+hashCode+beginTagHighlight2+cleanHilightText+endTagHighlight;
	}
	/**
	 * constructs a link using the text of the highlight and a hash number to identify it
	 * @return String that is the link
	 */
	@Transient
	public void constructHighlightLink() {
		String HighlightLinkBeginning=beginLinkTag+containerFile.getAbsolutePath()+"#"+hashCode+beginLinkTag2;
		String HighlightLink=HighlightLinkBeginning+cleanHilightText+endLinkTag;
		constructUrl();
		this.highlightLink= HighlightLink;
	}
	/**
	 * constructs the url that will be used in the gui to find the file that contains the highlight
	 * 
	 */
	@Transient
	private void constructUrl() {
		String highlightUrl="file://"+containerFile.getAbsolutePath()+"#"+hashCode;
		highlightUrl=highlightUrl.replace(" ", "%20");
		this.highlightUrl= highlightUrl;
	}
	//getters and setters
	public String getHighlightedText() {
		return highlightedText;
	}
	public String getHighlightLink() {
		return highlightLink;
	}
	public String getCleanHilightText() {
		return cleanHilightText;
	}
	public String getSearchable() {
		return searchable;
	}
	public String getHighligghtText() {
		return highligghtText;
	}
	public File getContainerFile() {
		return containerFile;
	}
	public void setContainerFile(File containerFile) {
		this.containerFile = containerFile;
	}
	public String getHighlightUrl() {
		return highlightUrl;
	}
	
	public EBook geteBook() {
		return eBook;
	}
	public void seteBook(EBook eBook) {
		this.eBook = eBook;
	}
}
