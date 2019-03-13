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
	private final String highlightDomSelector1="p:matches(.*";
	private final String highlightDomSelector2=".*)";
	private final String highlightDomSelector3="ul:matches(.*";
	private final String highlightDomSelector4=".*)";
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
	protected int highlightFileIndex;
	//sentences of the highlights
	@Transient
	public ArrayList<Sentence> sentences=new ArrayList<>();
	int hashCode;
	@Lob
	File containerFile;
	Pattern SPECIAL_REGEX_CHARS;

	public Highlight() {
		super();
	}

	public Highlight(String highligghtText) {
		this.highligghtText = cleanAmazonGlitches(highligghtText);
		hashCode= Math.abs(highligghtText.hashCode());
		SPECIAL_REGEX_CHARS = Pattern.compile("[\\{\\}\\(\\)\\[\\]\\?\\+\\*\\^$\\|\\\\\\-]");
		searchable= createSearchable(this.highligghtText);
		highlightedText=constructHIghlightedText(this.highligghtText);
		sentenceSplitter(this.highligghtText);
	}
	/**
	 * Method that cleans text from an amazon glitch with the hyphen character
	 */
	protected String cleanAmazonGlitches(String highligghtText) {
		String s=highligghtText.replaceAll("(–)([a-zA-Z]{1,1})", "$1 $2");
		s=s.replaceAll("([a-zA-Z]{1,1})(–)", "$1 $2");
		
		return s;
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
		String s=SPECIAL_REGEX_CHARS.matcher(highligghtText).replaceAll("\\\\$0");
		String searchable=highlightDomSelector1+s+highlightDomSelector2+","+highlightDomSelector3+s+highlightDomSelector4;
		return searchable;
	}
	/**
	 * wraps the text with HTML tags to makes the replacement
	 */

	protected String constructHIghlightedText(String highligghtText) {
		return beginTagHighlight1+hashCode+beginTagHighlight2+highligghtText+endTagHighlight;
	}
	/**
	 * constructs a link using the text of the highlight and a hash number to identify it
	 * @return String that is the link
	 */

	public void constructHighlightLink() {
		String HighlightLinkBeginning=beginLinkTag+containerFile.getAbsolutePath()+"#"+hashCode+beginLinkTag2;
		String HighlightLink=HighlightLinkBeginning+highligghtText+endLinkTag;
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
