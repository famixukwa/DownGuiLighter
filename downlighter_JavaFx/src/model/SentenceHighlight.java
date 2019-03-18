package model;

import java.util.regex.Pattern;
/**
 * sentence highlight is an special type of highlight for being used when sentence search replacement. It treats the first sentence differently by giving her the hash of the highlight. This allows that the highlight shown in the list of the popupwindow to be link only to the first sentence of the distributed highlight. It also groups all of the founded sentences for showing in the popup window onto a single piece of text although  different sentences has been found
 * @author fieltro
 *
 */
public class SentenceHighlight extends Highlight {
	private Sentence firstSentence;
	/**
	 * 
	 * @param highligghtText text obtained from the highlights file.
	 * @param highlightFileIndex index representing the order where the file containing the highlight is in the book (book spline)
	 */
	public SentenceHighlight(String highligghtText,int highlightFileIndex) {
		super();
		this.highligghtText = highligghtText;
		this.highlightFileIndex=highlightFileIndex;
		hashCode= Math.abs(highligghtText.hashCode());
		sentenceSplitter(highligghtText);
		setFirstSentence();
		SPECIAL_REGEX_CHARS = Pattern.compile("[\\{\\}\\(\\)\\[\\]\\?\\+\\*\\^$\\|\\\\\\-]");
		searchable= createSearchable(highligghtText);
		highlightedText=constructHIghlightedText(highligghtText);
	}

	@Override
	public void constructHighlightLink() {
		String HighlightLinkBeginning=beginLinkTag+containerFile.getAbsolutePath()+"#"+hashCode+beginLinkTag2;
		String HighlightLink=HighlightLinkBeginning+firstSentence.getHighligghtText()+endLinkTag;
		constructUrl();
		this.highlightLink= HighlightLink;
	}
	
	protected void sentenceSplitter(String s)
	{
		String[] parts = s.split("(?<=\\.)");
		if (parts.length>1) {
			for (int i = 0; i < parts.length; i++) {
				if (i==0) {
					System.out.println("hashcode: "+hashCode);
					Sentence sentence=new Sentence(parts[i],hashCode,highlightFileIndex);
					sentences.add(sentence);
					System.out.println("first sentence:  "+sentence.getSearchable());
				}
				else {
					s=parts[i].replaceAll("^ ", "");
					Sentence sentence=new Sentence(s,highlightFileIndex);
					sentences.add(sentence);
					System.out.println("other sentences:  "+sentence.getSearchable());
				}
			}
		}
	}

	protected void setFirstSentence() {
		this.firstSentence=sentences.get(1);

	}

	public Sentence getFirstSentence() {
		return firstSentence;
	}
}
