package model;

import java.io.File;
import java.util.regex.Pattern;
/**
 * Sentence is an extension of highlight that is used by the sentence search and replace mechanism. It is produced by highlight and it allows to search for highlights that are distributed into several paragraphs or list tags
 *
 */
public class Sentence extends Highlight {
	private int hashCode;
	public Sentence(String highligghtText) {
		super(highligghtText);
	}
	public Sentence(String highligghtText,int hashCode,int fileIndex) {
		super();
		this.highligghtText = cleanAmazonGlitches(highligghtText);
		this.hashCode=hashCode;
		SPECIAL_REGEX_CHARS = Pattern.compile("[\\{\\}\\(\\)\\[\\]\\?\\+\\*\\^$\\|\\\\\\-]");
		searchable= createSearchable(this.highligghtText);
		highlightedText=constructHIghlightedText(this.highligghtText);
		System.out.println(highlightedText);
	}
	public Sentence(String highligghtText,int fileIndex) {
		super();
		this.highligghtText = cleanAmazonGlitches(highligghtText);
		hashCode= Math.abs(highligghtText.hashCode());
		SPECIAL_REGEX_CHARS = Pattern.compile("[\\{\\}\\(\\)\\[\\]\\?\\+\\*\\^$\\|\\\\\\-]");
		searchable= createSearchable(this.highligghtText);
		highlightedText=constructHIghlightedText(this.highligghtText);
		System.out.println(highlightedText);
	}
	/**
	 * wraps the text with HTML tags to makes the replacement
	 *  @param highligghtText text obtained from the highlights file.
	 */
@Override
	protected String constructHIghlightedText(String highligghtText) {
		return beginTagHighlight1+hashCode+beginTagHighlight2+highligghtText+endTagHighlight;
		
	}

}
