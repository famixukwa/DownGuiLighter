package model;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.jsoup.nodes.TextNode;


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
	protected  String highligghtText;
	@Lob 
	@Column(length=2000)
	public String cleanHilightText;
	@Transient
	private String highlightDomSelector1="p:matchesOwn(.*";
	@Transient
	private String highlightDomSelector2=".*)";
	@Transient
	public String beginTagHighlight1="<span id=\"";
	@Transient
	public String beginTagHighlight2= "\" style=\"background-color: #FFFF00\">";
	@Transient
	public String endTagHighlight="</span>";
	@Transient
	protected String beginLinkTag="<a href=\"";
	@Transient
	protected String beginLinkTag2="\">";
	@Transient
	protected String endLinkTag="</a>";
	@Lob 
	@Column(length=2000)
	protected String searchable;
	@Lob 
	@Column(length=2000)
	protected String highlightedText;
	@Lob 
	@Column( length=2000)
	protected String highlightLink;
	@Lob 
	@Column(length=2000)
	private String highlightUrl;
	private int highlightLocationInHtml;
	private int highlightFileIndex;
	//sentences of the highlights
	public ArrayList<Sentence> sentences=new ArrayList<>();
	int hashCode;
	@Lob
	File containerFile;
	Pattern REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
	
	public Highlight() {
		 super();
	}
	
	public Highlight(String highligghtText) {
		this.highligghtText = highligghtText;
		hashCode= Math.abs(highligghtText.hashCode());
		cleanHilightText=cleanEspecialCharacters(highligghtText);
		searchable= createSearchable(cleanHilightText);
		highlightedText=constructHIghlightedText(highligghtText);
		sentenceSplitter(highligghtText);
	}
	/**
	 * Method that cleans text from special characters
	 * @param highligghtText highlight text to be cleaned
	 */
	
	protected String cleanEspecialCharacters(String highligghtText) {
		return REGEX_CHARS.matcher(highligghtText).replaceAll("\\\\$0");
		
		
	}
	/**
	 * splits the highlights in sentences
	 */
	protected void sentenceSplitter(String s)
	{
		String[] parts = s.split("(?<=\\.)");
		if (parts.length>1) {
			for (String string : parts) {
				Sentence sentence=new Sentence(string);
				sentences.add(sentence);
			}
		}
	}
	/**
	 * Adds the select code from Jsoup and the regex to the highlight text
	 * this makes possible to realize the search with jsoup.
	 */
	protected String createSearchable(String highligghtText) {
		String searchable=highlightDomSelector1+cleanEspecialCharacters(highligghtText)+highlightDomSelector2;
		return searchable;
	}
	/**
	 * wraps the text with HTML tags to makes the replacement
	 */
	
	protected String constructHIghlightedText(String cleanHilightText) {
		return beginTagHighlight1+hashCode+beginTagHighlight2+cleanHilightText+endTagHighlight;
	}
	/**
	 * constructs a link using the text of the highlight and a hash number to identify it
	 * @return String that is the link
	 */
	
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
	
	protected void constructUrl() {
		String highlightUrl="file://"+containerFile.getAbsolutePath()+"#"+hashCode;
		highlightUrl=highlightUrl.replace(" ", "%20");
		this.highlightUrl= highlightUrl;
	}
	
	String escapeSpecialRegexChars(String str) {
	    return REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
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
	public int getHighlightLocationInHtml() {
		return highlightLocationInHtml;
	}

	public void setHighlightLocationInHtml(int highlightLocationInHtml) {
		this.highlightLocationInHtml = highlightLocationInHtml;
	}

	public int getHighlightFileIndex() {
		return highlightFileIndex;
	}

	public void setHighlightFileIndex(int highlightFileIndex) {
		this.highlightFileIndex = highlightFileIndex;
	}

	public ArrayList<Sentence> getSentences() {
		return sentences;
	}

	
}
