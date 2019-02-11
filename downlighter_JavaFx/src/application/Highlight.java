package application;

import java.io.File;

import org.jsoup.nodes.Document;

/**
 * @author Kain
 *
 *This class creates a highlight object which has:
 *a search text that could be search in the book using regex
 *the same text surrounded with highlight tags to replace the original text
 */
public class Highlight {
private String highligghtText;
private String cleanHilightText;
private String highlightDomSelector1="p:matches(.*";
private String highlightDomSelector2=".*)";
private String beginTagHighlight1="<span id=\"";
private String beginTagHighlight2= "\" style=\"background-color: #FFFF00\">";
private String endTagHighlight="</span>";
private String beginLinkTag="<a href=\"";
private String beginLinkTag2="\">";
private String endLinkTag="</a>";
private String searchable;
private String highlightedText;
private String highlightLink;
private String highlightUrl;

int hashCode;
File containerFile;

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
private String cleanEspecialCharacters(String highligghtText) {
	return highligghtText.replace(".", ".");
}
/**
 * Adds the select code from Jsoup and the regex to the highlight text
 * this makes possible to realize the search with jsoup.
 */
private String createSearchable(String highligghtText) {
	String searchable=highlightDomSelector1+highligghtText+highlightDomSelector2;
	return searchable;
}
/**
 * wraps the text with HTML tags to makes the replacement
 */
private String constructHIghlightedText(String cleanHilightText) {
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
}
