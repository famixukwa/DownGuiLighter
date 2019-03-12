package model;

import java.io.File;
import java.util.regex.Pattern;

public class Sentence extends Highlight{
	private int hashCode;
	public Sentence(String highligghtText) {
		super(highligghtText);
	}
	public Sentence(String highligghtText,int hashCode) {
		super();
		this.highligghtText = cleanAmazonGlitches(highligghtText);
		this.hashCode=hashCode;
		SPECIAL_REGEX_CHARS = Pattern.compile("[\\{\\}\\(\\)\\[\\]\\?\\+\\*\\^$\\|\\\\\\-]");
		searchable= createSearchable(this.highligghtText);
		highlightedText=constructHIghlightedText(this.highligghtText);
		System.out.println(highlightedText);
	}
	/**
	 * wraps the text with HTML tags to makes the replacement
	 */
@Override
	protected String constructHIghlightedText(String highligghtText) {
		return beginTagHighlight1+hashCode+beginTagHighlight2+highligghtText+endTagHighlight;
		
	}

}
