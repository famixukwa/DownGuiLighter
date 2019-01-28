package application;

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
private String beginTagHighlight="<span style=\"background-color: #FFFF00\">";
private String endTagHighlight="</span>";
private String searchable;
private String highlightedText;

public Highlight(String highligghtText) {
	this.highligghtText = highligghtText;
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
	return beginTagHighlight+cleanHilightText+endTagHighlight;
}
public String getHighlightedText() {
	return highlightedText;
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
}
